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
package de.hybris.platform.travelcommons.controllers;

import de.hybris.platform.travelcommons.model.components.DealComponentModel;
import de.hybris.platform.travelcommons.model.components.PackageFinderComponentModel;
import de.hybris.platform.travelcommons.model.components.TabbedFinderComponentModel;
import de.hybris.platform.travelcommons.model.components.TravelBookingDetailsComponentModel;
import de.hybris.platform.travelcommons.model.components.TravelBookingListComponentModel;
import de.hybris.platform.travelcommons.model.components.TravelBookingPaymentDetailsComponentModel;
import de.hybris.platform.travelcommons.model.components.TravelFinderComponentModel;


/**
 */
public interface TravelcommonsControllerConstants
{
	String ADDON_PREFIX = "addon:/travelcommons/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Pages
		{
			interface PackageSearch
			{
				final String packageResultsViewJsonResponse = ADDON_PREFIX + "pages/package/packageResultsViewJsonResponse";
				final String packageListingJsonResponse = ADDON_PREFIX + "pages/package/packageListingJsonResponse";
			}

			interface Deal
			{
				String DealValidDatesJsonResponse = ADDON_PREFIX + "pages/deal/dealValidDatesJsonResponse";
				String DealDepartureDateValidationJsonResponse = ADDON_PREFIX + "pages/deal/dealDepartureDateValidationJsonResponse";
				String DealDetailsReturnDateJsonResponse = ADDON_PREFIX + "pages/deal/dealDetailsReturnDateJsonResponse";

			}

			interface Booking
			{
				String AddAccommodationRoomJsonResponse = ADDON_PREFIX + "pages/booking/addAccommodationRoomJsonResponse";
				String AddAccommodationRoomPackageFormValidationJsonResponse = ADDON_PREFIX
						+ "pages/booking/validateAddRoomForPackageJsonResponse";
			}

			interface FormErrors
			{
				String FormErrorsResponse = ADDON_PREFIX + "pages/form/formErrorsResponse";
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
			String TravelFinderComponent = _Prefix + TravelFinderComponentModel._TYPECODE + _Suffix;
			String PackageFinderComponent = _Prefix + PackageFinderComponentModel._TYPECODE + _Suffix;
			String TravelBookingDetailsComponent = _Prefix + TravelBookingDetailsComponentModel._TYPECODE + _Suffix;
			String TravelBookingListComponent = _Prefix + TravelBookingListComponentModel._TYPECODE + _Suffix;
			String TabbedFinderComponent = _Prefix + TabbedFinderComponentModel._TYPECODE + _Suffix;
			String DealComponent = _Prefix + DealComponentModel._TYPECODE + _Suffix;
			String TravelBookingPaymentDetailsComponent = _Prefix + TravelBookingPaymentDetailsComponentModel._TYPECODE + _Suffix;
		}
	}
}
