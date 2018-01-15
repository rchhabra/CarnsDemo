/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 *
 *
 */

package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Passengers.Passenger;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;


/**
 * Strategy that implements the {@link AmendOrderStrategy}. The strategy is used to create a new order with the new
 * accommodation request in the {@link OrderChangeRQ}.
 */
public class AddAccommodationToOrderStrategy extends AbstractAmendOrderStrategy implements AmendOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(AddAccommodationToOrderStrategy.class);
	@Override
	public OrderModel amendOrder(final OrderModel originalOrder, final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		final OrderModel amendedOrder = cloneOrder(originalOrder);
		final List<AbstractOrderEntryModel> orderEntries = new LinkedList<>();

		try
		{
			validateOrderItems(orderChangeRQ, originalOrder);

			for (final OrderItem orderItem : orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem())
			{
				final SeatItem seatItem = orderItem.getSeatItem();
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(orderItem.getOrderItemID().getValue());

				final List<Passenger> passengers = getPassengersFromOrderItem(orderItem);

				final List<TravellerModel> travellers = getTravellersFromPassengers(passengers, amendedOrder);

				validateTravellers(amendedOrder, travellers);

				final ListOfSeatType seat = (ListOfSeatType) seatItem.getSeatReference().get(0).getValue();
				final String seatNum = NdcFacadesUtils.getSeatNum(seat);

				final String transportOfferingCode = getTransportOfferingCode(
						(ListOfSeatType) seatItem.getSeatReference().get(0).getValue());

				TransportOfferingModel transportOffering = null;
				try
				{
					transportOffering = getNdcTransportOfferingService().getTransportOffering(transportOfferingCode);

				}
				catch (final ModelNotFoundException exception)
				{
					LOG.warn("Error while fetching TransportOfferingModel for code :" + transportOfferingCode
							+ " , removing order and throwing the exception");
					throw new NDCOrderException(getConfigurationService().getConfiguration()
							.getString(NdcfacadesConstants.ORDER_AMEND_INVALID_TRANSPORT_OFFERING));
				}

				final ConfiguredAccommodationModel accommodation = getConfiguredAccommodation(ndcOfferItemId, transportOffering,
						seatNum);

				checkValidAccommodation(accommodation.getProduct(), seatNum, ndcOfferItemId, transportOffering);

				if (Objects.nonNull(getNdcAccommodationService()
						.getSelectedAccommodationForTraveller(transportOffering, amendedOrder, travellers.get(0))))
				{
					removeAccommodation(accommodation, travellers, amendedOrder, transportOffering, seatNum, orderEntries);
				}

				addAccommodationOrderEntry(amendedOrder, accommodation.getProduct(), ndcOfferItemId,
						Collections.singletonList(transportOfferingCode), travellers, orderEntries);

				getNdcAccommodationService().createOrUpdateSelectedAccommodation(transportOffering, travellers, amendedOrder,
						accommodation);
			}

			createPaymentTransaction(amendedOrder, orderEntries);
		}
		catch (final Exception e)
		{
			LOG.warn("Error occurred, removing order and throwing again the exception");
			removeOrder(amendedOrder);
			throw e;
		}

		return amendedOrder;
	}

	/**
	 * Creates an {@link AbstractOrderEntryModel}. Since we are working with ACCOMMODATION the quantity specified in the {@link AbstractOrderEntryModel} will always be 1
	 *
	 * @param amendmentOrder
	 * @param product
	 * @param ndcOfferItemId
	 * @param transportOfferingCodes
	 * @param travellers
	 * @param orderEntries
	 * @throws NDCOrderException
	 */
	protected void addAccommodationOrderEntry(final OrderModel amendmentOrder, final ProductModel product,
			final NDCOfferItemId ndcOfferItemId, final List<String> transportOfferingCodes, final List<TravellerModel> travellers,
			final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		final List<TransportOfferingModel> transportOfferings = getNdcTransportOfferingService()
				.getTransportOfferings(transportOfferingCodes);

		orderEntries.add(getNdcOrderService()
				.populateOrderEntry(amendmentOrder, product, null, NdcfacadesConstants.ASSOCIATED_SERVICE_BUNDLE_NUMBER,
						getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId), transportOfferings, travellers,
						ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber(), 1));
	}

	/**
	 * Checks if the specified seat can added taking into account the seat availability and the class of service associated to the bundle
	 *
	 * @param product
	 * @param seatNum
	 * @param ndcOfferItemId
	 * @param transportOffering
	 * @throws NDCOrderException
	 */
	protected void checkValidAccommodation(final ProductModel product, final String seatNum, final NDCOfferItemId ndcOfferItemId,
			final TransportOfferingModel transportOffering) throws NDCOrderException
	{
		if (!getNdcAccommodationService()
				.checkIfAccommodationCanBeAdded(product, seatNum, ndcOfferItemId, transportOffering))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.ORDER_CREATE_SEAT_UNAVAILABLE));
		}
		if (!getNdcAccommodationService().checkIfSeatValidForFareProd(product, ndcOfferItemId))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.ORDER_CREATE_SEAT_INVALID_BUNDLE));
		}
	}
}
