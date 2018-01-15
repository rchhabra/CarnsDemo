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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.travel.CheckInRequestData;
import de.hybris.platform.commercefacades.travel.CheckInResponseData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;

import java.util.List;


/**
 * Facade exposing methods relevant to the Check In process
 */
public interface CheckInFacade
{

	/**
	 * This method will perform the Check-In based on the incoming RequestData
	 *
	 * @param checkInRequest
	 * 		the check in request
	 * @return CheckInResponseData evaluated from checkInRequest
	 */
	CheckInResponseData doCheckin(CheckInRequestData checkInRequest);

	/**
	 * This method checks if the Check-In process is possible for the leg identified by the originDestinationRefNumber
	 * for the given ReservationData
	 * Returns true if Check In is possible, false otherwise
	 *
	 * @param reservationData
	 * 		Reservation details to be Checked In
	 * @param originDestinationRefNumber
	 * 		Origin Destination reference number to identify the leg
	 * @return boolean boolean
	 */
	boolean isCheckInPossible(ReservationData reservationData, int originDestinationRefNumber);

	/**
	 * Gets checkin flow group for checkout.
	 *
	 * @return String checkin flow group for checkout
	 */
	String getCheckinFlowGroupForCheckout();

	/**
	 * Method to check if a traveller is eligible to amend the ancillaries of his order.
	 *
	 * @param travellerCode
	 * 		as the code of the traveller to be checked
	 * @param transportOfferingCodes
	 * 		as the codes of the transportOffering to check
	 * @param abstractOrderCode
	 * 		the abstract order code, i.e., the booking reference number
	 * @return true if it is possible to amend the ancillaries for the specific traveller and transportOfferings, false
	 * otherwise
	 */
	boolean checkTravellerEligibility(String travellerCode, List<String> transportOfferingCodes, String abstractOrderCode);

	/**
	 * Method to start the check-in process
	 *
	 * @param bookingReference
	 * 		as the reference of the booking
	 * @param originDestinationRefNumber
	 * 		as the originDestinationRefNumber of the leg the traveller has checked in
	 * @param travellersToCheckIn
	 * 		as the list of travellers to check in
	 */
	void startCheckInProcess(String bookingReference, int originDestinationRefNumber, List<String> travellersToCheckIn);

}
