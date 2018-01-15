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
package de.hybris.platform.traveladdon.controllers;

import de.hybris.platform.traveladdon.model.components.FareFinderComponentModel;
import de.hybris.platform.traveladdon.model.components.ReservationBreakdownComponentModel;
import de.hybris.platform.traveladdon.model.components.TransportBookingDetailsComponentModel;
import de.hybris.platform.traveladdon.model.components.TransportBookingListComponentModel;
import de.hybris.platform.traveladdon.model.components.TransportOfferingStatusSearchComponentModel;
import de.hybris.platform.traveladdon.model.components.TransportReservationComponentModel;
import de.hybris.platform.traveladdon.model.components.TransportSummaryComponentModel;


/**
 */
public interface TraveladdonControllerConstants
{
	String ADDON_PREFIX = "addon:/traveladdon/";

	interface Views
	{

		interface Pages
		{

			interface Account
			{
				String SavedPassengersPage = ADDON_PREFIX + "pages/account/saved-passengers";

				String MyBookingsPage = ADDON_PREFIX + "pages/account/my-bookings";
			}

			interface CheckIn
			{
				String CheckInPage = ADDON_PREFIX + "pages/checkin/checkInPage";

				String CheckInDetailsPage = ADDON_PREFIX + "pages/checkin/checkInDetails";

				String CheckInStatusPage = ADDON_PREFIX + "pages/checkin/checkInStatus";

				String CheckInSuccessPage = ADDON_PREFIX + "pages/checkin/checkInSuccess";
			}

			interface Order
			{
				String OrderConfirmationPage = ADDON_PREFIX + "pages/order/orderConfirmationPage";
			}

			interface Ancillary
			{
				String AncillaryPage = ADDON_PREFIX + "pages/ancillary/ancillaryPage";

				String AddProductToCartResponse = ADDON_PREFIX + "pages/ancillary/addProductToCartResponse";

				String UpdateBundleJSONData = ADDON_PREFIX + "pages/ancillary/updateBundleJSONData";

				String TravelRestrictionResponse = ADDON_PREFIX + "pages/booking/travelRestrictionResponse";
			}

			interface FareSelection
			{
				String FareSelectionPage = ADDON_PREFIX + "pages/booking/fareSelectionPage";

				String fareSelectionSortingResult = ADDON_PREFIX + "pages/booking/fareSelectionSortingResult";

				String savedSearchResult = ADDON_PREFIX + "pages/booking/savedSearch";

				String AddBundleToCartResponse = ADDON_PREFIX + "pages/booking/addBundleToCartResponse";
			}

			interface Booking
			{
				String BookingConfirmationPage = ADDON_PREFIX + "pages/order/bookingConfirmationPage";
			}

			interface FlightStatus
			{
				String flightStatusSearchResponse = ADDON_PREFIX + "pages/transportofferingstatus/flightStatusSearchResponse";
			}

			interface TripFinder
			{
				String DestinationLocationJSONResponse = ADDON_PREFIX + "pages/tripfinder/destinationLocationsJSONResponse";
			}

			interface FormErrors
			{
				String formErrorsResponse = ADDON_PREFIX + "pages/tripfinder/formErrorsResponse";
			}

			interface Suggestions
			{
				String JSONSearchResponse = ADDON_PREFIX + "pages/suggestions/suggestionsSearchJsonResponse";
				String JSONNamesSearchResponse = ADDON_PREFIX + "pages/suggestions/suggestionsNamesSearchJsonResponse";

			}

			interface Cancel
			{
				String CancelTravellerResponse = ADDON_PREFIX + "pages/cancel/cancelTravellerResponse";
			}

			interface Cart
			{
				String VoucherJSONResponse = ADDON_PREFIX + "pages/cart/voucherJSONResponse";
			}

			interface TravellerDetails
			{
				String TravellerDetailsPage = ADDON_PREFIX + "pages/travellerdetails/travellerDetailsPage";

				String JSONTravellerDetailsAuthentication = ADDON_PREFIX
						+ "pages/travellerdetails/JSONTravellerDetailsAuthentication";
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
			String TransportReservationComponent = _Prefix + TransportReservationComponentModel._TYPECODE + _Suffix;

			String FareFinderComponent = _Prefix + FareFinderComponentModel._TYPECODE + _Suffix;

			String TransportOfferingStatusSearchComponent = _Prefix + TransportOfferingStatusSearchComponentModel._TYPECODE
					+ _Suffix;

			String ReservationBreakdownComponent = _Prefix + ReservationBreakdownComponentModel._TYPECODE + _Suffix;

			String TransportBookingDetailsComponent = _Prefix + TransportBookingDetailsComponentModel._TYPECODE + _Suffix;

			String TransportBookingListComponent = _Prefix + TransportBookingListComponentModel._TYPECODE + _Suffix;

			String TransportSummaryComponent = _Prefix + TransportSummaryComponentModel._TYPECODE + _Suffix;
		}
	}
}
