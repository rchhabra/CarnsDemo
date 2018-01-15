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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler class to populate TravellerBreakdownData.
 */
public class TravellerBreakdownHandler extends AbstractBreakdownHandler implements AncillarySearchHandler
{
	private Map<String, String> offerGroupToOriginDestinationMapping;

	/**
	 * Method to populate travellerBreakdown data
	 */
	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			if (CollectionUtils.isEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				continue;
			}

			for (final OriginDestinationOfferInfoData odOfferInfo : offerGroupData.getOriginDestinationOfferInfos())
			{
				final List<OfferPricingInfoData> filteredOfferPricingInfos = getFilteredPricingInfos(odOfferInfo);

				for (final OfferPricingInfoData offerPricingInfoData : filteredOfferPricingInfos)
				{
					final List<TravellerBreakdownData> travellerBreakdownDataList = getTravellerBreakdownData(
							offerPricingInfoData.getProduct().getCode(), offerRequestData, offerGroupData.getCode(), odOfferInfo);
					offerPricingInfoData.setTravellerBreakdowns(travellerBreakdownDataList);
				}
			}
		}
	}

	/**
	 * Filters the offerGroups by the PER_LEG_PER_PAX {@link AddToCartCriteriaType}
	 *
	 * @param odOfferInfo
	 * @return
	 */
	protected List<OfferPricingInfoData> getFilteredPricingInfos(final OriginDestinationOfferInfoData odOfferInfo)
	{
		return odOfferInfo.getOfferPricingInfos().stream()
				.filter(opi -> opi.getTravelRestriction() == null || opi.getTravelRestriction().getAddToCartCriteria() == null
						|| StringUtils.equalsIgnoreCase(opi.getTravelRestriction().getAddToCartCriteria(),
								AddToCartCriteriaType.PER_LEG_PER_PAX.getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * For each TravellerData in ItineraryData(represents travellers of the Itinerary), create TravellerBreakdownData as
	 *
	 * Get price for the Product in offer
	 *
	 * Set TravellerData in TravellerBreakdownData
	 *
	 * Determine the quantity of offer from the selectedOffers.
	 *
	 * Calculate and set PassengerFareData for the product in offer.
	 *
	 * @param productCode
	 *           product in offer
	 * @param offerRequestData
	 *           OfferRequestData
	 * @param offerGroupCode
	 *           category of product in offer
	 * @param odOfferInfo
	 *           OriginDestinationOfferInfoData
	 * @return a list of TravellerBreakdownData.
	 */
	protected List<TravellerBreakdownData> getTravellerBreakdownData(final String productCode,
			final OfferRequestData offerRequestData, final String offerGroupCode, final OriginDestinationOfferInfoData odOfferInfo)
	{
		final PriceInformation priceInfo = getOfferPricingInformation(offerGroupCode, odOfferInfo, productCode);
		PriceData priceData = null;
		if (Optional.ofNullable(priceInfo).isPresent())
		{
			priceData = createPriceData(priceInfo);
		}
		final List<TravellerBreakdownData> travellerBreakdownDataList = new ArrayList<>();
		for (final TravellerData travellerData : getTravellerData(offerRequestData, odOfferInfo))
		{
			final TravellerBreakdownData travellerBreakdownData = new TravellerBreakdownData();
			travellerBreakdownData.setTraveller(travellerData);

			final int quantity = getTravellerQuantity(offerRequestData, travellerData, offerGroupCode, odOfferInfo, productCode);
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
	 * Method to get travellers data from ItineraryData of OfferRequestData which match to OriginDestinationOfferInfoData
	 *
	 * @param offerRequestData
	 * @param odOfferInfo
	 * @return
	 */
	protected List<TravellerData> getTravellerData(final OfferRequestData offerRequestData,
			final OriginDestinationOfferInfoData odOfferInfo)
	{
		final List<ItineraryData> itineraryDataForODInfoData = offerRequestData.getItineraries().stream()
				.filter(itineraryData -> itineraryData.getRoute().getCode().equals(odOfferInfo.getTravelRouteCode()))
				.collect(Collectors.<ItineraryData> toList());
		return CollectionUtils.isNotEmpty(itineraryDataForODInfoData) ? itineraryDataForODInfoData.get(0).getTravellers()
				: Collections.emptyList();
	}

	/**
	 * This method determines the quantity of product in offer as
	 *
	 * if the product in offer is already selected by customer (either bundled or selected) then determine the quantity
	 * from SelectedOffers in offerRequestData.
	 *
	 * if no match found, then quantity is zero.
	 *
	 * @param offerRequestData
	 *           OfferRequestData
	 * @param travellerData
	 *           TravellerData
	 * @param offerGroupCode
	 *           category of product in offer
	 * @param odOfferInfo
	 *           OriginDestinationOfferInfoData
	 * @param productCode
	 *           product in offer
	 * @return
	 */
	protected Integer getTravellerQuantity(final OfferRequestData offerRequestData, final TravellerData travellerData,
			final String offerGroupCode, final OriginDestinationOfferInfoData odOfferInfo, final String productCode)
	{
		Integer quantity = 0;

		final Optional<OfferGroupData> offerGroupData = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> offerGroup.getCode().equals(offerGroupCode)).findFirst();

		if (!offerGroupData.isPresent())
		{
			return quantity;
		}

		for (final OriginDestinationOfferInfoData selectedOdOfferInfoData : offerGroupData.get().getOriginDestinationOfferInfos())
		{
			if (!(selectedOdOfferInfoData.getTravelRouteCode().equals(odOfferInfo.getTravelRouteCode()) && TransportOfferingUtils
					.compareTransportOfferings(selectedOdOfferInfoData.getTransportOfferings(), odOfferInfo.getTransportOfferings())))
			{
				continue;
			}
			for (final OfferPricingInfoData offerPricingInfoData : selectedOdOfferInfoData.getOfferPricingInfos())
			{
				if (!offerPricingInfoData.getProduct().getCode().equals(productCode))
				{
					continue;
				}
				final Optional<TravellerBreakdownData> selectedTravellerBreakdownData = offerPricingInfoData.getTravellerBreakdowns()
						.stream()
						.filter(travellerBreakdown -> travellerBreakdown.getTraveller().getLabel().equals(travellerData.getLabel()))
						.findFirst();
				if (selectedTravellerBreakdownData.isPresent())
				{
					quantity = quantity + selectedTravellerBreakdownData.get().getQuantity();
				}
			}
		}

		return quantity;
	}

	/**
	 * This method determines the PriceInformation of the product in offer as
	 *
	 * If the Product category (offerGroupCode) is configured at RouteLevel, check if there is a priceRow at TravelRoute
	 * and offer, if not, check if there is a priceRow at TransportOffering and offer. if not, check if there is a
	 * priceRow for travelSector and offer
	 *
	 * If the Product category (offerGroupCode) is configured at TransportOfferingLevel, check if there is a priceRow at
	 * TransportOffering and offer. if not, check if there is a priceRow for travelSector and offer.
	 *
	 * In either cases, if we do not find a price, offer default price.
	 *
	 * @param offerGroupCode
	 * @param odOfferInfo
	 * @param productCode
	 * @return
	 */
	protected PriceInformation getOfferPricingInformation(final String offerGroupCode,
			final OriginDestinationOfferInfoData odOfferInfo, final String productCode)
	{
		PriceInformation priceInfo = null;
		final String offerGroupType = getOfferGroupToOriginDestinationMapping().getOrDefault(offerGroupCode,
				getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
						TravelservicesConstants.TRAVEL_ROUTE));
		if (TravelfacadesConstants.TRAVEL_ROUTE.equalsIgnoreCase(offerGroupType))
		{
			priceInfo = getPriceInformation(productCode, PriceRowModel.TRAVELROUTECODE, odOfferInfo.getTravelRouteCode());
			if (!Optional.ofNullable(priceInfo).isPresent())
			{
				priceInfo = getPriceInformationFromTransportOfferingOrSector(odOfferInfo, productCode);
			}
		}
		else if (TravelfacadesConstants.TRANSPORT_OFFERING.equalsIgnoreCase(offerGroupType))
		{
			priceInfo = getPriceInformationFromTransportOfferingOrSector(odOfferInfo, productCode);
		}

		//If still no price found, get the default price.
		if (!Optional.ofNullable(priceInfo).isPresent())
		{
			priceInfo = getPriceInformation(productCode, null, null);
		}

		return priceInfo;
	}

	/**
	 * @return the offerGroupToOriginDestinationMapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * @param offerGroupToOriginDestinationMapping
	 *           the offerGroupToOriginDestinationMapping to set
	 */
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

}
