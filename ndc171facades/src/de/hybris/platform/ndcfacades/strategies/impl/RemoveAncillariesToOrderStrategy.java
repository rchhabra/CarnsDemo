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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.order.NDCProductFacade;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy that implements the {@link AmendOrderStrategy}. The strategy is used to create a new order without the
 * ancillaries request in the {@link OrderChangeRQ}.
 */
public class RemoveAncillariesToOrderStrategy extends AbstractAmendOrderStrategy implements AmendOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(RemoveAncillariesToOrderStrategy.class);

	private NDCProductFacade ndcProductFacade;

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
				final List<PassengerType> passengers = getPassengersFromOrderItem(orderItem);
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(orderItem.getOrderItemID().getValue());
				final List<String> transportOfferingCodes = ndcOfferItemId.getBundleList().stream()
						.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toList());
				final List<TravellerModel> travellers = getTravellersFromPassengers(passengers, amendedOrder);

				validateTravellers(amendedOrder, travellers);

				for (final ServiceIDType serviceId : orderItem.getAssociations().getServices().getServiceID())
				{
					removeAncillary(amendedOrder, serviceId, transportOfferingCodes, travellers, ndcOfferItemId, orderEntries);
				}
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
	 * Checks if the specified ancillary can be removed and, in case, it removes it from the {@link OrderModel} provided.
	 * It fills a list of {@link AbstractOrderEntryModel} with order entries that has been modified.
	 *
	 * @param amendmentOrder
	 * @param serviceId
	 * @param transportOfferingCodes
	 * @param travellers
	 * @param ndcOfferItemId
	 * @param orderEntries
	 * @throws NDCOrderException
	 */
	protected void removeAncillary(final OrderModel amendmentOrder, final ServiceIDType serviceId,
			final List<String> transportOfferingCodes, final List<TravellerModel> travellers, final NDCOfferItemId ndcOfferItemId,
			final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		try
		{
			final ProductModel product = getProductService().getProductForCode(serviceId.getValue());

			final List<String> associatedTransportOfferings = extractAssociatedTransportOffering(serviceId, transportOfferingCodes,
					ndcOfferItemId);

			getNdcProductFacade().checkIfValidProductForTravellers(amendmentOrder, travellers, product,
					-NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY, associatedTransportOfferings, ndcOfferItemId.getRouteCode());

			final List<AbstractOrderEntryModel> ancillaryEntries = amendmentOrder.getEntries().stream()
					.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo()) && entry.getActive()
							&& sameTravellers(entry.getTravelOrderEntryInfo().getTravellers(), travellers)
							&& sameTransportOffering(entry.getTravelOrderEntryInfo().getTransportOfferings(),
									associatedTransportOfferings)
							&& StringUtils.equals(product.getCode(), entry.getProduct().getCode()))
					.collect(Collectors.toList());

			if (CollectionUtils.isEmpty(ancillaryEntries))
			{
				throw new NDCOrderException("Unable to remove " + product.getName() + ". Ancillary is not present in the order.");
			}

			for (final AbstractOrderEntryModel entry : ancillaryEntries)
			{
				if (NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY > entry.getQuantity())
				{
					throw new NDCOrderException("Unable to remove " + NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY + " "
							+ entry.getProduct().getName() + ". Quantity specified is greater than the quantity present in the order.");
				}

				entry.setQuantity(entry.getQuantity() - NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);

				if (entry.getQuantity() == 0)
				{
					entry.setActive(Boolean.FALSE);
				}
				entry.setAmendStatus(AmendStatus.CHANGED);
				getModelService().save(entry);

				orderEntries.add(entry);
			}

			getModelService().refresh(amendmentOrder);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug(e);
			throw new NDCOrderException("Invalid ServiceID provided");
		}
	}

	/**
	 * Checks that the list of {@link TransportOfferingModel} associated to a particular {@link AbstractOrderEntryModel}
	 * are the match with the transport offering codes provided in the {@link OrderChangeRQ} for that particular
	 * {@link ServiceIDType}
	 *
	 * @param transportOfferings
	 * @param associatedTransportOfferings
	 * @return
	 */
	protected boolean sameTransportOffering(final Collection<TransportOfferingModel> transportOfferings,
			final List<String> associatedTransportOfferings)
	{
		final Set<String> orderEntryTransportOffering = transportOfferings.stream().map(WarehouseModel::getCode)
				.collect(Collectors.toSet());
		final Set<String> associatedTransportOfferingsSet = new HashSet<>(associatedTransportOfferings);

		return orderEntryTransportOffering.equals(associatedTransportOfferingsSet);
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
			LOG.error("Unable to cancel a passenger where there is an additional payment required.");
			throw new NDCOrderException("Error during removing the ancillary(ies)");
		}

		if (!createRefundPaymentTransaction(amendmentOrder, orderEntries))
		{
			throw new NDCOrderException("Error during removing the ancillary(ies)");
		}
	}

	protected NDCProductFacade getNdcProductFacade()
	{
		return ndcProductFacade;
	}

	@Required
	public void setNdcProductFacade(final NDCProductFacade ndcProductFacade)
	{
		this.ndcProductFacade = ndcProductFacade;
	}
}
