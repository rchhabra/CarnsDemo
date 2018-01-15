/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.accommodationaddon.controllers;

import de.hybris.platform.accommodationaddon.model.components.AccommodationBookingDetailsComponentModel;
import de.hybris.platform.accommodationaddon.model.components.AccommodationBookingListComponentModel;
import de.hybris.platform.accommodationaddon.model.components.AccommodationBreakdownComponentModel;
import de.hybris.platform.accommodationaddon.model.components.AccommodationFinderComponentModel;
import de.hybris.platform.accommodationaddon.model.components.AccommodationReservationComponentModel;
import de.hybris.platform.accommodationaddon.model.components.AccommodationSummaryComponentModel;


/**
 */
public interface AccommodationaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/accommodationaddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Pages
		{
			interface Hotel
			{
				String CustomerReviewJsonResponse = ADDON_PREFIX + "pages/hotel/accommodationOfferingCustomerReviewsJsonResponse";
				String AccommodationListingJsonResponse = ADDON_PREFIX + "pages/hotel/accommodationListingJsonResponse";
				String AccommodationResultsViewJsonResponse = ADDON_PREFIX + "pages/hotel/accommodationResultsViewJsonResponse";

				String CustomerReviewPagedJsonResponse =
						ADDON_PREFIX + "pages/hotel/accommodationOfferingDetailsCustomerReviewsJsonResponse";
				String removeRoomJsonResponse = ADDON_PREFIX + "pages/hotel/removeRoomJsonResponse";
				String AddAccommodationToCartResponse = ADDON_PREFIX + "pages/hotel/addAccommodationToCartResponse";
				String AddExtraToCartResponse = ADDON_PREFIX + "pages/hotel/addExtraToCartResponse";
				String ValidateAccommodationCartResponse = ADDON_PREFIX + "pages/hotel/validateAccommodationCartResponse";
				String UpdateBookingDatesPageJsonResponse = ADDON_PREFIX
						+ "pages/hotel/updateAccommodationBookingDatesJsonResponse";
			}

			interface FormErrors
			{
				String FormErrorsResponse = ADDON_PREFIX + "pages/form/formErrorsResponse";
			}

			interface Suggestions
			{
				String SuggestionsSearchJsonResponse = ADDON_PREFIX + "pages/suggestions/suggestionsSearchJsonResponse";
			}

		}
	}

	interface Actions
	{
		interface Cms
		{
			String _Prefix = "/view/";

			String _Suffix = "Controller";

			/**
			 * CMS components that have specific handlers
			 */
			String AccommodationSummaryComponent = _Prefix + AccommodationSummaryComponentModel._TYPECODE + _Suffix;
			String AccommodationFinderComponent = _Prefix + AccommodationFinderComponentModel._TYPECODE + _Suffix;
			String AccommodationBreakdownComponent = _Prefix + AccommodationBreakdownComponentModel._TYPECODE + _Suffix;
			String AccommodationBookingDetailsComponent = _Prefix + AccommodationBookingDetailsComponentModel._TYPECODE + _Suffix;
			String AccommodationBookingListComponent = _Prefix + AccommodationBookingListComponentModel._TYPECODE + _Suffix;

			String AccommodationReservationComponent = _Prefix + AccommodationReservationComponentModel._TYPECODE + _Suffix;
		}
	}
}
