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

import de.hybris.platform.category.model.CategoryModel;
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
import de.hybris.platform.ndcfacades.strategies.AddAncillariesToOrderRestrictionStrategy;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy that implements the {@link AmendOrderStrategy}. The strategy is used to create a new order with the new
 * ancillaries request in the {@link OrderChangeRQ}.
 */
public class AddAncillariesToOrderStrategy extends AbstractAmendOrderStrategy implements AmendOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(AddAncillariesToOrderStrategy.class);

	private List<String> categoriesNotAllowed;
	private NDCProductFacade ndcProductFacade;

	private Map<String, AddAncillariesToOrderRestrictionStrategy> addAncillariesToOrderRestrictionStrategyMap;

	@Override
	public OrderModel amendOrder(final OrderModel originalOrder, final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		final OrderModel amendedOrder = cloneOrder(originalOrder);
		final List<AbstractOrderEntryModel> orderEntries = new LinkedList<>();

		validateOrderItems(orderChangeRQ, originalOrder);
		try
		{
			for (final OrderItem orderItem : orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem())
			{
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(orderItem.getOrderItemID().getValue());
				final List<String> transportOfferingCodes = ndcOfferItemId.getBundleList().stream()
						.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toList());
				final List<PassengerType> passengers = getPassengersFromOrderItem(orderItem);
				final List<TravellerModel> travellers = getTravellersFromPassengers(passengers, amendedOrder);

				validateTravellers(amendedOrder, travellers);

				for (final TravellerModel traveller : travellers)
				{
					for (final ServiceIDType serviceId : orderItem.getAssociations().getServices().getServiceID())
					{
						addAncillary(amendedOrder, serviceId, transportOfferingCodes, traveller, ndcOfferItemId,
								orderItem.getOrderItemID().getValue(), orderEntries);
					}
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
	 * Checks if the specified ancillary can be added and, in case, it adds it from the {@link OrderModel} provided. It
	 * fills a list of {@link AbstractOrderEntryModel} with order entries that has been modified.
	 *
	 * @param amendmentOrder
	 * @param serviceId
	 * @param transportOfferingCodes
	 * @param traveller
	 * @param ndcOfferItemId
	 * @param ndcOfferItemIdString
	 * @param orderEntries
	 * @throws NDCOrderException
	 */
	protected void addAncillary(final OrderModel amendmentOrder, final ServiceIDType serviceId,
			final List<String> transportOfferingCodes, final TravellerModel traveller, final NDCOfferItemId ndcOfferItemId,
			final String ndcOfferItemIdString, final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		try
		{
			final ProductModel product = getProductService().getProductForCode(serviceId.getValue());

			checkProductCategory(product);

			final String productRestriction = getTravelRestrictionFacade().getAddToCartCriteria(product.getCode());
			final List<String> associatedTransportOfferings = extractAssociatedTransportOffering(serviceId, transportOfferingCodes,
					ndcOfferItemId);

			final List<TransportOfferingModel> transportOfferings = getNdcTransportOfferingService()
					.getTransportOfferings(associatedTransportOfferings);

			final AbstractOrderEntryModel orderEntry = getBookingService().getOrderEntry(amendmentOrder, product.getCode(),
					ndcOfferItemId.getRouteCode(), associatedTransportOfferings, Collections.singletonList(traveller.getLabel()),
					false);

			if (Objects.nonNull(orderEntry))
			{
				getNdcProductFacade()
						.checkIfProductCanBeAddedToTraveller(amendmentOrder, traveller, product,
								NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY, associatedTransportOfferings,
								orderEntry.getTravelOrderEntryInfo().getTravelRoute().getCode());

				if (orderEntry.getActive())
				{
					orderEntry.setQuantity(orderEntry.getQuantity() + NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
				}
				else
				{
					orderEntry.setQuantity((long) NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
					orderEntry.setActive(true);
				}
				orderEntry.setAmendStatus(AmendStatus.CHANGED);
				getModelService().save(orderEntry);

				orderEntries.add(orderEntry);
			}
			else
			{
				getAddAncillariesToOrderRestrictionStrategyMap().get(productRestriction)
						.addAncillary(amendmentOrder, Collections.singletonList(traveller), product, transportOfferings,
								ndcOfferItemIdString, ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber(),
								orderEntries);
			}
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug(e);
			throw new NDCOrderException("Invalid ServiceID provided");
		}
	}

	/**
	 * Checks that the product category is not among the restricted ones. Throws an exception otherwise. (i.e. Seat are
	 * accommodation products and needs to be included in a different element in the OrderCreateRQ
	 *
	 * @param ancillaryProduct
	 */
	protected void checkProductCategory(final ProductModel ancillaryProduct) throws NDCOrderException
	{
		String categoryCode = StringUtils.EMPTY;
		if (Objects.nonNull(ancillaryProduct) && CollectionUtils.isNotEmpty(ancillaryProduct.getSupercategories()))
		{
			final Optional<CategoryModel> category = ancillaryProduct.getSupercategories().stream().findFirst();
			categoryCode = category.map(CategoryModel::getCode).orElse(StringUtils.EMPTY);
		}

		if (Objects.nonNull(ancillaryProduct) && getCategoriesNotAllowed().contains(categoryCode))
		{
			throw new NDCOrderException(ancillaryProduct.getName() + " cannot be added to the order");
		}
	}

	protected Map<String, AddAncillariesToOrderRestrictionStrategy> getAddAncillariesToOrderRestrictionStrategyMap()
	{
		return addAncillariesToOrderRestrictionStrategyMap;
	}

	@Required
	public void setAddAncillariesToOrderRestrictionStrategyMap(
			final Map<String, AddAncillariesToOrderRestrictionStrategy> addAncillariesToOrderRestrictionStrategyMap)
	{
		this.addAncillariesToOrderRestrictionStrategyMap = addAncillariesToOrderRestrictionStrategyMap;
	}

	protected List<String> getCategoriesNotAllowed()
	{
		return categoriesNotAllowed;
	}

	@Required
	public void setCategoriesNotAllowed(final List<String> categoriesNotAllowed)
	{
		this.categoriesNotAllowed = categoriesNotAllowed;
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
