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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.reservation.data.OfferBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is responsible for creating an overview of ancillaries of type PER_BOOKING and PER_PAX from Abstract
 * Order with an interpretation of whether it is included in bundle or added manually by customer.
 */
public class ReservationOfferBreakdownHandler extends AbstractOfferBreakdownHandler implements ReservationHandler
{
	private List<ProductType> notAncillaryProductTypes;
	private Map<String, ProductType> productTypeInstanceMap;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		if (CollectionUtils.isEmpty(reservationData.getOfferPricingInfos()))
		{
			return;
		}

		reservationData.getOfferPricingInfos()
				.forEach(offerPricingInfo -> reservationData.setOfferBreakdowns(createOfferBreakdowns(abstractOrderModel)));
	}

	/**
	 * Creates a list of Offer Breakdowns for each ancillary product
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return list of offer breakdowns
	 */
	protected List<OfferBreakdownData> createOfferBreakdowns(final AbstractOrderModel abstractOrderModel)
	{
		final List<AbstractOrderEntryModel> ancillaryEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()))
				.filter(entry -> isAncillaryEntry(entry) && (isPerPaxEntry(entry) || isPerBookingEntry(entry)))
				.collect(Collectors.toList());

		final List<OfferBreakdownData> offerBreakdowns = new ArrayList<>();
		final List<AbstractOrderEntryModel> includedEntries = ancillaryEntries.stream().filter(entry -> entry.getBundleNo() > 0)
				.collect(Collectors.toList());
		retreiveOfferBreakdownsFromEntries(includedEntries, offerBreakdowns, Boolean.TRUE);

		final List<AbstractOrderEntryModel> extraEntries = ancillaryEntries.stream().filter(entry -> entry.getBundleNo() == 0)
				.collect(Collectors.toList());

		retreiveOfferBreakdownsFromEntries(extraEntries, offerBreakdowns, Boolean.FALSE);


		return offerBreakdowns;
	}

	/**
	 * Checks if the given abstractOrderEntry is an ancillary entry.
	 *
	 * @param entry
	 * 		the entry
	 * @return true if the abstractOrderEntry is an ancillary entry, false otherwise
	 */
	protected boolean isAncillaryEntry(final AbstractOrderEntryModel entry)
	{
		if (entry.getActive())
		{
			final String className = entry.getProduct().getClass().getSimpleName();
			ProductType productType = getProductTypeInstanceMap().get(className);
			if (Objects.isNull(productType))
			{
				productType = entry.getProduct().getProductType();
			}

			return !getNotAncillaryProductTypes().contains(productType);
		}

		return false;
	}

	/**
	 * Checks if the given abstractOrderEntry is an entry for a product of type PER_BOOKING
	 *
	 * @param entry
	 * 		the entry
	 * @return true if the abstractOrderEntry is PER_BOOKING, false otherwise
	 */
	protected boolean isPerBookingEntry(final AbstractOrderEntryModel entry)
	{
		return entry.getTravelOrderEntryInfo().getTransportOfferings().isEmpty()
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == null && entry.getTravelOrderEntryInfo()
				.getTravellers().isEmpty();
	}

	/**
	 * Checks if the given abstractOrderEntry is an entry for a product of type PER_PAX
	 *
	 * @param entry
	 * 		the entry
	 * @return true if the abstractOrderEntry is PER_PAX, false otherwise
	 */
	protected boolean isPerPaxEntry(final AbstractOrderEntryModel entry)
	{
		return entry.getActive() && entry.getTravelOrderEntryInfo().getTransportOfferings().isEmpty()
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == null && !entry.getTravelOrderEntryInfo()
				.getTravellers().isEmpty();
	}

	/**
	 * Gets not ancillary product types.
	 *
	 * @return the not ancillary product types
	 */
	protected List<ProductType> getNotAncillaryProductTypes()
	{
		return notAncillaryProductTypes;
	}

	/**
	 * Sets not ancillary product types.
	 *
	 * @param notAncillaryProductTypes
	 * 		the not ancillary product types
	 */
	public void setNotAncillaryProductTypes(final List<ProductType> notAncillaryProductTypes)
	{
		this.notAncillaryProductTypes = notAncillaryProductTypes;
	}

	/**
	 * Gets the product type instance map.
	 *
	 * @return the productTypeInstanceMap
	 */
	protected Map<String, ProductType> getProductTypeInstanceMap()
	{
		return productTypeInstanceMap;
	}

	/**
	 * Sets the product type instance map.
	 *
	 * @param productTypeInstanceMap
	 *           the productTypeInstanceMap to set
	 */
	@Required
	public void setProductTypeInstanceMap(final Map<String, ProductType> productTypeInstanceMap)
	{
		this.productTypeInstanceMap = productTypeInstanceMap;
	}

}
