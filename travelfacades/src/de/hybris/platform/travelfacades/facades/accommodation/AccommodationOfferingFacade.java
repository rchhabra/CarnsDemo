/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */

package de.hybris.platform.travelfacades.facades.accommodation;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;


/**
 * The interface Accommodation offering facade.
 */
public interface AccommodationOfferingFacade
{
	/**
	 * Search accommodation offering day rates accommodation offering search page data.
	 *
	 * @param accommodationRequestData
	 * 		the accommodation request data
	 * @param roomStayCandidateData
	 * 		the room stay candidate data
	 * @return the accommodation offering search page data
	 */
	AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> searchAccommodationOfferingDayRates(
			AccommodationSearchRequestData accommodationRequestData, RoomStayCandidateData roomStayCandidateData);

	/**
	 * This method will return a full AccommodationOffering details wrapped into a PropertyData
	 *
	 * @param accommodationAvailabilityRequestData
	 * 		the accommodation availability request data
	 * @return a PropertyData containing all the details
	 */
	AccommodationAvailabilityResponseData getAccommodationOfferingDetails(
			AccommodationAvailabilityRequestData accommodationAvailabilityRequestData);

	/**
	 * This method will return a full AccommodationOffering details in AccommodationAvailabilityRequestData for wrapped
	 * into a PropertyData
	 *
	 * @param accommodationAvailabilityRequestData
	 * 		the accommodation availability request data
	 * @return a PropertyData containing all the details
	 */
	AccommodationAvailabilityResponseData getSelectedAccommodationOfferingDetails(
			AccommodationAvailabilityRequestData accommodationAvailabilityRequestData);

	/**
	 * Gets the property data.
	 *
	 * @param accommodationOfferingCode
	 * 		the accommodation offering code
	 * @return the property data
	 */
	PropertyData getPropertyData(String accommodationOfferingCode);

	/**
	 * Checks if there is at least 1 rate plan available in the AccommodationAvailabilityResponseData
	 *
	 * @param accommodationAvailabilityResponse
	 * 		the accommodation availability response
	 * @return boolean
	 */
	boolean checkAvailability(AccommodationAvailabilityResponseData accommodationAvailabilityResponse);

	/**
	 * Checks if all rate plans are available in the AccommodationAvailabilityResponseData
	 *
	 * @param accommodationAvailabilityResponse
	 * 		the accommodation availability response
	 * @return boolean
	 */
	boolean isAccommodationAvailableForQuickSelection(AccommodationAvailabilityResponseData accommodationAvailabilityResponse);
}
