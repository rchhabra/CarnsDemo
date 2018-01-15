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
*/

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler to populate OfferPricingInfo in SelectedOffers.
 */
public class PricingInfoOffersRequestHandler implements AncillarySearchRequestHandler
{

	private TravelRestrictionFacade travelRestrictionFacade;

	/**
	 * Method to populate offerPricingInfo to Selected Offers.
	 */
	@Override
	public void handle(final ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		final Map<String, Map<List<TransportOfferingData>, List<OfferPricingInfoData>>> offersByGroup = getOfferPricingInfoFromReservation(
				reservationData);
		final Map<String, List<OfferPricingInfoData>> offerPricingInfosForOfferGroup = getOfferPricingInfoForOfferGroup(
				reservationData);
		for (final OfferGroupData offerGroup : offerRequestData.getSelectedOffers().getOfferGroups())
		{
			if (CollectionUtils.isNotEmpty(offerGroup.getOriginDestinationOfferInfos()))
			{
				final Map<List<TransportOfferingData>, List<OfferPricingInfoData>> offersByODInfo = offersByGroup
						.get(offerGroup.getCode());
				offerGroup.getOriginDestinationOfferInfos().forEach(odOfferInfo -> {
					final List<OfferPricingInfoData> offerPricingInfos = getOfferPricingInfosForTransportOfferings(offersByODInfo,
							odOfferInfo.getTransportOfferings());
					if (CollectionUtils.isNotEmpty(offerPricingInfos))
					{
						odOfferInfo.setOfferPricingInfos(offerPricingInfos);
					}
					else
					{
						odOfferInfo.setOfferPricingInfos(Collections.<OfferPricingInfoData> emptyList());
					}
				});
			}
			if (MapUtils.isNotEmpty(offerPricingInfosForOfferGroup))
			{
				final List<OfferPricingInfoData> opInfos = offerPricingInfosForOfferGroup.get(offerGroup.getCode());
				if (CollectionUtils.isNotEmpty(opInfos))
				{
					offerGroup.setOfferPricingInfos(opInfos);
				}
				else
				{
					offerGroup.setOfferPricingInfos(Collections.<OfferPricingInfoData> emptyList());
				}
			}
		}

	}

	/**
	 * Returns a map of the offerPricingInfo for different offerGroups
	 *
	 * @param reservationData
	 * @return a map with the offerGroup code as key and the list of its offerPricingInfos as value
	 */
	protected Map<String, List<OfferPricingInfoData>> getOfferPricingInfoForOfferGroup(final ReservationData reservationData)
	{
		if (CollectionUtils.isEmpty(reservationData.getOfferPricingInfos()))
		{
			return null;
		}
		final Map<String, List<OfferPricingInfoData>> offerPricingInfoByOffersGroupMap = new HashMap<>();

		reservationData.getOfferPricingInfos().forEach(offerPriceInfo -> {
			final Optional<CategoryData> categoryData = offerPriceInfo.getProduct().getCategories().stream().findFirst();
			final String addToCartCriteria = getTravelRestrictionFacade()
					.getAddToCartCriteria(offerPriceInfo.getProduct().getCode());
			if (categoryData.isPresent() && (StringUtils.equalsIgnoreCase(addToCartCriteria, AddToCartCriteriaType.PER_LEG.getCode())
					|| StringUtils.equalsIgnoreCase(addToCartCriteria, AddToCartCriteriaType.PER_LEG_PER_PAX.getCode())))
			{
				final String categoryCode = categoryData.get().getCode();
				if (offerPricingInfoByOffersGroupMap.containsKey(categoryCode))
				{
					offerPricingInfoByOffersGroupMap.get(categoryCode).add(offerPriceInfo);
				}
				else
				{
					offerPricingInfoByOffersGroupMap.put(categoryCode, Arrays.asList(offerPriceInfo));
				}
			}
		});

		return offerPricingInfoByOffersGroupMap;
	}

	/**
	 * Returns a list of OfferPricingInfoData matching given list of TransportOfferings
	 *
	 * @param offersByODInfo
	 *           - map containing OfferPricingInfos for TransportOffering combinations
	 * @param transportOfferings
	 *           - given set of transport offerings
	 * @return matching list of OfferPricingInfoData
	 */
	protected List<OfferPricingInfoData> getOfferPricingInfosForTransportOfferings(
			final Map<List<TransportOfferingData>, List<OfferPricingInfoData>> offersByODInfo,
			final List<TransportOfferingData> transportOfferings)
	{

		for (final TransportOfferingData odTransportOffering : transportOfferings)
		{
			for (final Map.Entry<List<TransportOfferingData>, List<OfferPricingInfoData>> entry : offersByODInfo.entrySet())
			{
				final List<TransportOfferingData> offerTransportOfferings = entry.getKey();
				for (final TransportOfferingData offerTransportOffering : offerTransportOfferings)
				{
					if (odTransportOffering.getCode().equals(offerTransportOffering.getCode()))
					{
						return offersByODInfo.get(entry.getKey());
					}
				}
			}
		}
		return Collections.emptyList();
	}

	/**
	 * This method categorizes OfferPricingInfo objects by combination of TransportOfferings and CategoryCode.
	 *
	 * Map<ProductCategoryCode, Map<List<TransportOffering>, List<offferPricingInfo>>
	 *
	 * @param reservationData
	 * @return a Map of OfferPricingInfo categorized by combination of TransportOfferings and CategoryCode.
	 */
	protected Map<String, Map<List<TransportOfferingData>, List<OfferPricingInfoData>>> getOfferPricingInfoFromReservation(
			final ReservationData reservationData)
	{
		final Map<String, Map<List<TransportOfferingData>, List<OfferPricingInfoData>>> offerPricingInfoByOffersGroupMap = new HashMap<>();
		reservationData.getReservationItems()
				.forEach(reservationItem -> reservationItem.getReservationPricingInfo().getOriginDestinationOfferInfos()
						.forEach(odOfferInfo -> odOfferInfo.getOfferPricingInfos().forEach(offerPriceInfo -> {
							final Optional<CategoryData> categoryData = offerPriceInfo.getProduct().getCategories().stream().findFirst();
							final TravelRestrictionData travelRestrictionData = offerPriceInfo.getTravelRestriction();
							if (categoryData.isPresent()
									&& (travelRestrictionData == null || StringUtils.isBlank(travelRestrictionData.getAddToCartCriteria())
											|| StringUtils.equalsIgnoreCase(travelRestrictionData.getAddToCartCriteria(),
													AddToCartCriteriaType.PER_LEG.getCode())
											|| StringUtils.equalsIgnoreCase(travelRestrictionData.getAddToCartCriteria(),
													AddToCartCriteriaType.PER_LEG_PER_PAX.getCode())))
							{
								setOfferPricingInfoByRoute(offerPricingInfoByOffersGroupMap, odOfferInfo, offerPriceInfo,
										categoryData.get().getCode());
							}

						})));
		return offerPricingInfoByOffersGroupMap;
	}

	protected void setOfferPricingInfoByRoute(
			final Map<String, Map<List<TransportOfferingData>, List<OfferPricingInfoData>>> offerPricingInfoByOffersGroupMap,
			final OriginDestinationOfferInfoData odOfferInfo, final OfferPricingInfoData offerPriceInfo, final String categoryCode)
	{
		Map<List<TransportOfferingData>, List<OfferPricingInfoData>> offerPricingInfoByTransportOfferingsMap;
		if (offerPricingInfoByOffersGroupMap.containsKey(categoryCode))
		{
			offerPricingInfoByTransportOfferingsMap = offerPricingInfoByOffersGroupMap.get(categoryCode);
			List<OfferPricingInfoData> selecterOfferPricingInfo;
			if (offerPricingInfoByTransportOfferingsMap.containsKey(odOfferInfo.getTransportOfferings()))
			{
				selecterOfferPricingInfo = offerPricingInfoByTransportOfferingsMap.get(odOfferInfo.getTransportOfferings());
			}
			else
			{
				selecterOfferPricingInfo = new ArrayList<>();
				offerPricingInfoByTransportOfferingsMap.put(odOfferInfo.getTransportOfferings(), selecterOfferPricingInfo);
			}
			selecterOfferPricingInfo.add(offerPriceInfo);
		}
		else
		{
			offerPricingInfoByTransportOfferingsMap = new HashMap<>();
			final List<OfferPricingInfoData> selectedOfferPricingInfo = new ArrayList<>();
			selectedOfferPricingInfo.add(offerPriceInfo);
			offerPricingInfoByTransportOfferingsMap.put(odOfferInfo.getTransportOfferings(), selectedOfferPricingInfo);
			offerPricingInfoByOffersGroupMap.put(categoryCode, offerPricingInfoByTransportOfferingsMap);
		}
	}

	/**
	 * @return the travelRestrictionFacade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * @param travelRestrictionFacade
	 *           the travelRestrictionFacade to set
	 */
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}
}
