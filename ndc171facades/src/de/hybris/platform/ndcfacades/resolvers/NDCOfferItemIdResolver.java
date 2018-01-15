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
package de.hybris.platform.ndcfacades.resolvers;

import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Interface for NDC OfferItemId Resolver
 */
public interface NDCOfferItemIdResolver
{

	/**
	 * Generate the OfferItemId with the information extracted from the ptcFareBreakdownData, pricedItinerary and
	 * itineraryPricingInfo
	 *
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 *
	 * @return string
	 */
	String generateAirShoppingNDCOfferItemId(PTCFareBreakdownData ptcFareBreakdownData, PricedItineraryData pricedItinerary,
			ItineraryPricingInfoData itineraryPricingInfo);

	/**
	 * Generate the OfferItemId with the information extracted from the pricedItinerary and itineraryPricingInfo
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 *
	 * @return string
	 */
	String generateAirShoppingNDCOfferItemId(PricedItineraryData pricedItinerary, ItineraryPricingInfoData itineraryPricingInfo);

	/**
	 * Decode the NDCOfferItemId string in an NDCOfferItemId object
	 *
	 * @param NDCOfferItemIdString
	 * 		the OfferItemID that needs to be parsed
	 *
	 * @return NDCOfferItemId object
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	NDCOfferItemId getNDCOfferItemIdFromString(String NDCOfferItemIdString) throws NDCOrderException;

	/**
	 * Check if two OfferItemIds belongs to the same offer (same originDestinationRefNumber, fare product, route code,
	 * bundle and transport offering). They might be related to different PTC
	 *
	 * @param firstOfferItemId
	 * 		OfferItemID that needs to be compared to secondOfferItemId
	 * @param secondOfferItemId
	 * 		second OfferItemID
	 *
	 * @return true it the OfferItemIDs refers to the same flight
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	boolean isSameOffer(String firstOfferItemId, String secondOfferItemId) throws NDCOrderException;


	/**
	 * Return the string corresponding to the ndcOfferItemId element
	 *
	 * @param ndcOfferItemId
	 * 		the ndcOfferItemId that needs to be parsed
	 *
	 * @return String string
	 */
	String ndcOfferItemIdToString(NDCOfferItemId ndcOfferItemId);

	/**
	 * Generate the OfferItemId with the information extracted from the AbstractOrderEntryModel
	 *
	 * @param orderEntry
	 * 		the order entry
	 *
	 * @return string
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	String generateOrderNDCOfferItemId(List<AbstractOrderEntryModel> orderEntry) throws NDCOrderException;

	/**
	 * Returns the list of {@link TransportOfferingModel} included in the specified orderItemId
	 *
	 * @param orderItemId
	 * 		the order item id
	 *
	 * @return transport offering from ndc offer item id
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	List<TransportOfferingModel> getTransportOfferingFromNDCOfferItemId(String orderItemId) throws NDCOrderException;
}
