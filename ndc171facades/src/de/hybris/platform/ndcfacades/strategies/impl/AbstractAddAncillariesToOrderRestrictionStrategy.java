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
import de.hybris.platform.ndcfacades.order.NDCProductFacade;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.strategies.AddAncillariesToOrderRestrictionStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to collect methods used across different AddAncillariesToOrderRestriction Strategy
 */
public abstract class AbstractAddAncillariesToOrderRestrictionStrategy implements AddAncillariesToOrderRestrictionStrategy
{
	private BookingService bookingService;
	private ModelService modelService;

	private NDCProductFacade ndcProductFacade;
	private NDCOrderService ndcOrderService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;

	private Map<String, String> offerGroupToOriginDestinationMapping;


	/**
	 * Checks if an order entry for the same {@link ProductModel}, {@link TravellerModel} and list of {@link TransportOfferingModel}
	 * exists. In case it exist it updates the quantity, otherwise it creates a new {@link AbstractOrderEntryModel}
	 *
	 * @param order
	 * @param product
	 * @param traveller
	 * @param transportOfferings
	 * @param offerItemID
	 * @param routeCode
	 * @param originDestinationRefNumber
	 * @param orderEntries
	 */
	protected void add(final OrderModel order, final ProductModel product, final TravellerModel traveller,
			final List<TransportOfferingModel> transportOfferings, final String offerItemID, final String routeCode,
			final int originDestinationRefNumber, final List<AbstractOrderEntryModel> orderEntries)
	{
		final AbstractOrderEntryModel orderEntry = getBookingService()
				.getOrderEntry(order, product.getCode(), routeCode,
						transportOfferings.stream().map(WarehouseModel::getCode).collect(Collectors.toList()),
						Objects.nonNull(traveller) ? Collections.singletonList(traveller.getLabel()) : Collections.emptyList(), false);

		if (Objects.nonNull(orderEntry))
		{
			orderEntry.setQuantity(orderEntry.getQuantity() + NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
			getModelService().save(orderEntry);
			orderEntries.add(orderEntry);
		}
		else
		{
			orderEntries.add(getNdcOrderService()
					.populateOrderEntry(order, product, null, NdcfacadesConstants.ASSOCIATED_SERVICE_BUNDLE_NUMBER,
							offerItemID, transportOfferings, Collections.singletonList(traveller), routeCode,
							originDestinationRefNumber, NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY));
		}
	}

	/**
	 * Raises an exception if the transport offering codes contained in the offerItemID do not match with the provided ones
	 * Since this is a TravelRoute ancillary, the product should be associated to all the transport offering of the specified leg
	 *
	 * @param transportOfferingCodes
	 * @param offerItemID
	 * @param ancillaryProduct
	 * @throws NDCOrderException
	 */
	protected void checkRouteTransportOfferings(final Set<String> transportOfferingCodes, final String offerItemID,
			final ProductModel ancillaryProduct) throws NDCOrderException
	{
		final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(offerItemID);

		final Set<String> refTransportOfferings = ndcOfferItemId.getBundleList().stream()
				.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toSet());

		if (!transportOfferingCodes.equals(refTransportOfferings))
		{
			throw new NDCOrderException("Invalid flight segment reference provided for " + ancillaryProduct.getCode());
		}
	}

	/**
	 * Raises an exception if the transport offering codes contained in the offerItemID do not include the provided ones
	 * Since this is a TransportOffeing ancillary, the product should be associated to a subset the transport offering of
	 * the specified leg
	 *
	 * @param transportOfferingCodes
	 * @param offerItemID
	 * @param ancillaryProduct
	 * @throws NDCOrderException
	 */
	protected void checkSingleTransportOfferings(final Set<String> transportOfferingCodes, final String offerItemID,
			final ProductModel ancillaryProduct) throws NDCOrderException
	{
		final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(offerItemID);

		final Set<String> refTransportOfferings = ndcOfferItemId.getBundleList().stream()
				.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toSet());

		if (CollectionUtils.isEmpty(transportOfferingCodes) || !refTransportOfferings.containsAll(transportOfferingCodes))
		{
			throw new NDCOrderException("Invalid flight segment reference provided for " + ancillaryProduct.getCode());
		}
	}

	/**
	 * Returns the mapping extracted from the category that the provided {@link ProductModel} belongs to.
	 *
	 * @param ancillaryProduct
	 * @return
	 */
	protected String getCategoryMapping(final ProductModel ancillaryProduct)
	{
		String categoryCode = StringUtils.EMPTY;
		if (Objects.nonNull(ancillaryProduct) && CollectionUtils.isNotEmpty(ancillaryProduct.getSupercategories()))
		{
			final Optional<CategoryModel> category = ancillaryProduct.getSupercategories().stream().findFirst();
			categoryCode = category.map(CategoryModel::getCode).orElse(StringUtils.EMPTY);
		}

		return getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode, getOfferGroupToOriginDestinationMapping()
				.getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING, TravelservicesConstants.TRAVEL_ROUTE));
	}

	/**
	 * Checks if the list of {@link TravellerModel} is not empty. PER_LEG_PER_PAX ancillaries needs to be associated to
	 * travellers
	 *
	 * @param travellers
	 * @param ancillaryProduct
	 * @throws NDCOrderException
	 */
	protected void checkTravellers(final List<TravellerModel> travellers, final ProductModel ancillaryProduct)
			throws NDCOrderException
	{
		if (Objects.isNull(travellers) || CollectionUtils.isEmpty(travellers))
		{
			throw new NDCOrderException("Missing passenger reference for " + ancillaryProduct.getName());
		}
	}

	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

	protected NDCOrderService getNdcOrderService()
	{
		return ndcOrderService;
	}

	@Required
	public void setNdcOrderService(final NDCOrderService ndcOrderService)
	{
		this.ndcOrderService = ndcOrderService;
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

	protected BookingService getBookingService()
	{
		return bookingService;
	}

	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
