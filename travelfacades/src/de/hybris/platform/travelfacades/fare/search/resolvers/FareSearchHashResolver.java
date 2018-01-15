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
package de.hybris.platform.travelfacades.fare.search.resolvers;

import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;

import java.security.SecureRandom;
import java.util.List;


/**
 * Interface for the FareSearchHashResolver
 */
public interface FareSearchHashResolver
{
	/**
	 * Returns a random string generated using {@link SecureRandom}
	 *
	 * @return
	 */
	String generateSeed();

	/**
	 * Generates the identifier based on the seed stored in session and the {@link ItineraryPricingInfoData} provided
	 *
	 * @param itineraryPricingInfo
	 */
	String generateIdentifier(ItineraryPricingInfoData itineraryPricingInfo);

	/**
	 * Generates the identifier based on the seed and the {@link ItineraryPricingInfoData} provided
	 *
	 * @param itineraryPricingInfo
	 */
	String generateIdentifier(ItineraryPricingInfoData itineraryPricingInfo, String searchSeed);

	/**
	 * Returns true if the identifier generated from the {@link AddBundleToCartRequestData} matches with the itineraryIdentifier and bundleType provided
	 *
	 * @param addBundleToCartRequestData
	 * @param itineraryIdentifier
	 * @param bundleType
	 * @return
	 */
	boolean validItineraryIdentifier(AddBundleToCartRequestData addBundleToCartRequestData, String itineraryIdentifier,
			String bundleType);

	/**
	 * Returns true if the identifier included in the {@link ItineraryPricingInfoData} matches the products included in the offer.
	 * Removes all the seeds from the session. This function in meant to be called from a deal page that reloads its content and regenerates the search seeds.
	 *
	 * @param itineraryPricingInfos
	 * @param bundleTemplateID
	 * @return
	 */
	boolean validItineraryIdentifier(List<ItineraryPricingInfoData> itineraryPricingInfos, String bundleTemplateID);

	/**
	 * Returns true if the identifier included in the {@link ItineraryPricingInfoData} matches the products included in the offer
	 *
	 * @param itineraryPricingInfo
	 * @return
	 */
	boolean validPackageItineraryIdentifier(ItineraryPricingInfoData itineraryPricingInfo);
}
