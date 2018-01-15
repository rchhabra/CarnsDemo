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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query.Passengers.Passenger;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Strategy that implements the {@link AmendOrderStrategy}. The strategy is used to create a new order to remove
 * selected accommodation for passenger(s) from {@link OrderChangeRQ} request.
 */
public class RemoveAccommodationToOrderStrategy extends AbstractAmendOrderStrategy implements AmendOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(RemoveAccommodationToOrderStrategy.class);

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
				final List<Passenger> passengers = getPassengersFromOrderItem(orderItem);

				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(orderItem.getOrderItemID().getValue());
				final List<TravellerModel> travellers = getTravellersFromPassengers(passengers, amendedOrder);

				validateTravellers(amendedOrder, travellers);

				final String seatNum = NdcFacadesUtils.getSeatNum((ListOfSeatType) seatItem.getSeatReference().get(0).getValue());

				final String transportOfferingCode = getTransportOfferingCode(
						(ListOfSeatType) seatItem.getSeatReference().get(0).getValue());

				final TransportOfferingModel transportOffering = getNdcTransportOfferingService()
						.getTransportOffering(transportOfferingCode);

				final ConfiguredAccommodationModel accommodation = getConfiguredAccommodation(ndcOfferItemId, transportOffering,
						seatNum);

				removeAccommodation(accommodation, travellers, amendedOrder, transportOffering, seatNum, orderEntries);
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
	 * If needed, creates the refund payment transaction and starts the order process
	 *
	 * @param amendmentOrder
	 * @param orderEntries
	 * @return
	 * @throws NDCOrderException
	 */
	@Override
	protected void createPaymentTransaction(final OrderModel amendmentOrder,
			final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		getModelService().refresh(amendmentOrder);

		calculateOrderTotal(amendmentOrder);

		final PriceData totalToPay = getTotalToPay(amendmentOrder);

		if (totalToPay.getValue().doubleValue() > 0d)
		{
			LOG.error("Unable to cancel a seat where there is an additional payment required.");
			throw new NDCOrderException("Error during removing the ancillary(ies)");
		}

		if (!createRefundPaymentTransaction(amendmentOrder, orderEntries))
		{
			throw new NDCOrderException("Error during removing the seat");
		}
	}
}
