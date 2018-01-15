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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler class to populate the BookingBreakdown for the OfferResponseData.
 */
public class BookingBreakdownHandler extends AbstractBreakdownHandler implements AncillarySearchHandler
{

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			if (CollectionUtils.isNotEmpty(offerGroupData.getOfferPricingInfos()))
			{
				final List<OfferPricingInfoData> offerPricingInfos = getFilteredOfferPricingInfos(
						offerGroupData.getOfferPricingInfos());

				for (final OfferPricingInfoData offerPricingInfoData : offerPricingInfos)
				{
					final BookingBreakdownData bookingBreakdown = getBookingBreakdown(offerRequestData, offerPricingInfoData,
							offerGroupData);
					offerPricingInfoData.setBookingBreakdown(bookingBreakdown);
				}
			}
		}
	}

	/**
	 * Returns the list of offerPricingInfos of PER_BOOKING @{link AddToCartCriteriaType}
	 *
	 * @param offerPricingInfos
	 *           as the input offerPricingInfos to be filtered
	 * @return the list of PER_BOOKING offerPricingInfos
	 */
	protected List<OfferPricingInfoData> getFilteredOfferPricingInfos(final List<OfferPricingInfoData> offerPricingInfos)
	{
		return offerPricingInfos.stream()
				.filter(opi -> opi.getTravelRestriction() != null && opi.getTravelRestriction().getAddToCartCriteria() != null
						&& StringUtils.equalsIgnoreCase(opi.getTravelRestriction().getAddToCartCriteria(),
								AddToCartCriteriaType.PER_BOOKING.getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * Creates and returns the bookingBreakdownData for the given offerPricingInfoData
	 *
	 * @param offerRequestData
	 * @param offerPricingInfoData
	 * @param offerGroupData
	 *
	 * @return the bookingBreakdownData
	 */
	protected BookingBreakdownData getBookingBreakdown(final OfferRequestData offerRequestData,
			final OfferPricingInfoData offerPricingInfoData, final OfferGroupData offerGroupData)
	{
		final BookingBreakdownData bookingBreakdownData = new BookingBreakdownData();

		final int quantity = getQuantity(offerRequestData, offerGroupData.getCode(), offerPricingInfoData.getProduct().getCode());
		bookingBreakdownData.setQuantity(quantity);

		//default per currency should be picked up
		final PriceInformation priceInfo = getPriceInformation(offerPricingInfoData.getProduct().getCode(), null, null);

		PriceData priceData = null;
		if (Optional.ofNullable(priceInfo).isPresent())
		{
			priceData = createPriceData(priceInfo);
		}
		if (Optional.ofNullable(priceData).isPresent())
		{
			bookingBreakdownData.setPassengerFare(getPassengerFareData(priceData, bookingBreakdownData.getQuantity()));
		}

		return bookingBreakdownData;
	}

	/**
	 * Returns the quantity of the selected products for the given productCode
	 *
	 * @param offerRequestData
	 * @param offerGroupCode
	 * @param productCode
	 *
	 * @return the quantity of the selected products for the given productCode
	 */
	protected Integer getQuantity(final OfferRequestData offerRequestData, final String offerGroupCode, final String productCode)
	{
		Integer quantity = 0;

		final Optional<OfferGroupData> selectedOfferGroupData = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> offerGroup.getCode().equals(offerGroupCode)).findFirst();
		if (!selectedOfferGroupData.isPresent())
		{
			return quantity;
		}

		final Optional<OfferPricingInfoData> selectedOfferPricingInfoData = selectedOfferGroupData.get().getOfferPricingInfos()
				.stream().filter(opi -> opi.getProduct().getCode().equals(productCode)).findFirst();
		if (selectedOfferPricingInfoData.isPresent())
		{
			quantity += selectedOfferPricingInfoData.get().getBookingBreakdown().getQuantity();
		}

		return quantity;
	}

}
