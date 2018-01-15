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
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler class to populate TravellerBreakdownData.
 */
public class TravellerOfferGroupBreakdownHandler extends AbstractBreakdownHandler implements AncillarySearchHandler
{

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			if (CollectionUtils.isNotEmpty(offerGroupData.getOfferPricingInfos()))
			{
				final List<OfferPricingInfoData> offerPricingInfos = getFilteredOfferPricingInfo(
						offerGroupData.getOfferPricingInfos());

				for (final OfferPricingInfoData offerPricingInfoData : offerPricingInfos)
				{
					final List<TravellerBreakdownData> travellerBreakdownDataList = getTravellerBreakdownData(
							offerPricingInfoData.getProduct().getCode(), offerRequestData, offerGroupData.getCode());
					offerPricingInfoData.setTravellerBreakdowns(travellerBreakdownDataList);
				}
			}
		}

	}

	/**
	 * Filters the offerGroups by the PER_PAX {@link AddToCartCriteriaType}
	 *
	 * @param offerPricingInfos
	 * @return
	 */
	protected List<OfferPricingInfoData> getFilteredOfferPricingInfo(final List<OfferPricingInfoData> offerPricingInfos)
	{
		return offerPricingInfos.stream()
				.filter(opi -> opi.getTravelRestriction() != null && opi.getTravelRestriction().getAddToCartCriteria() != null
						&& StringUtils.equalsIgnoreCase(opi.getTravelRestriction().getAddToCartCriteria(),
								AddToCartCriteriaType.PER_PAX.getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * Creates and returns the list of travellerBreakdownData for the given productCode
	 *
	 * @param productCode
	 * @param offerRequestData
	 * @param offerGroupCode
	 * @return
	 */
	protected List<TravellerBreakdownData> getTravellerBreakdownData(final String productCode,
			final OfferRequestData offerRequestData, final String offerGroupCode)
	{
		final List<TravellerBreakdownData> travellerBreakdownDataList = new ArrayList<>();

		PriceData priceData = null;
		//default per currency should be picked up
		final PriceInformation priceInfo = getPriceInformation(productCode, null, null);
		if (Optional.ofNullable(priceInfo).isPresent())
		{
			priceData = createPriceData(priceInfo);
		}

		for (final TravellerData travellerData : getTravellerData(offerRequestData))
		{
			final TravellerBreakdownData travellerBreakdownData = new TravellerBreakdownData();
			travellerBreakdownData.setTraveller(getMergedTravellerData(travellerData, offerRequestData));

			final int quantity = getTravellerQuantity(offerRequestData, travellerData, offerGroupCode, productCode);
			travellerBreakdownData.setQuantity(quantity);

			if (Optional.ofNullable(priceData).isPresent())
			{
				travellerBreakdownData.setPassengerFare(getPassengerFareData(priceData, quantity));
			}

			travellerBreakdownDataList.add(travellerBreakdownData);
		}

		return travellerBreakdownDataList;
	}

	/**
	 * This method will return a TravellerData where the TravellerStatusInfo is the result of the merge of the
	 * setTravellerStatusInfo for each leg.
	 *
	 * @param travellerData
	 * @param offerRequestData
	 *
	 * @return the TravellerData where the TravellerStatusInfo is the result of the merge of the setTravellerStatusInfo
	 *         for each leg
	 */
	protected TravellerData getMergedTravellerData(final TravellerData travellerData, final OfferRequestData offerRequestData)
	{
		if (MapUtils.isEmpty(travellerData.getTravellerStatusInfo()))
		{
			return travellerData;
		}

		final TravellerData newTravellerData = new TravellerData();
		newTravellerData.setLabel(travellerData.getLabel());
		newTravellerData.setSavedTravellerUid(travellerData.getSavedTravellerUid());
		newTravellerData.setTravellerInfo(travellerData.getTravellerInfo());
		newTravellerData.setTravellerType(travellerData.getTravellerType());
		newTravellerData.setUid(travellerData.getUid());

		newTravellerData.setTravellerStatusInfo(new HashMap<>());
		final List<TravellerData> travellerList = offerRequestData.getItineraries().stream()
				.flatMap(itinerary -> itinerary.getTravellers().stream())
				.filter(traveller -> traveller.getUid().equals(travellerData.getUid())).collect(Collectors.toList());
		travellerList.forEach(traveller -> newTravellerData.getTravellerStatusInfo().putAll(traveller.getTravellerStatusInfo()));

		return newTravellerData;
	}

	/**
	 * Returns the list of travellerData
	 *
	 * @param offerRequestData
	 * @return
	 */
	protected List<TravellerData> getTravellerData(final OfferRequestData offerRequestData)
	{
		return Objects.nonNull(offerRequestData) && CollectionUtils.isNotEmpty(offerRequestData.getItineraries())
				? offerRequestData.getItineraries().get(0).getTravellers() : Collections.emptyList();
	}

	/**
	 * Returns the quantity for the given travellerData of the given productCode
	 *
	 * @param offerRequestData
	 * @param travellerData
	 * @param offerGroupCode
	 * @param productCode
	 * @return
	 */
	protected int getTravellerQuantity(final OfferRequestData offerRequestData, final TravellerData travellerData,
			final String offerGroupCode, final String productCode)
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
			final Optional<TravellerBreakdownData> selectedTravellerBreakdownData = selectedOfferPricingInfoData.get()
					.getTravellerBreakdowns().stream()
					.filter(travellerBreakdown -> travellerBreakdown.getTraveller().getLabel().equals(travellerData.getLabel()))
					.findFirst();

			if (selectedTravellerBreakdownData.isPresent())
			{
				quantity = quantity + selectedTravellerBreakdownData.get().getQuantity();
			}
		}
		return quantity;
	}

}
