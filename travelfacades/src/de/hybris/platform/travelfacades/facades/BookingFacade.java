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

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.travelfacades.facades.impl.DefaultReservationFacade;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Interface to retrieve the reservation data using a booking reference.
 */
public interface BookingFacade
{
	/**
	 * Returns ReservationData object for the bookingReference. Extracts the OrderModel using bookingReference and
	 * converts to ReservationData.
	 *
	 * @param bookingReference
	 * 		a String representing bookingReference Number
	 * @return ReservationData object.
	 */
	ReservationData getBookingByBookingReference(String bookingReference);

	/**
	 * Performs a validation on the booking, to check if the user is one of the travellers in the booking(only checks
	 * last name) and returns email id of the booker.
	 *
	 * @param bookingReference
	 * 		a String representing bookingReference Number
	 * @param lastName
	 * 		a string representing lastName of the user.
	 * @return String EmailId.
	 * @deprecated Deprecated since version 4.0. use {@link DefaultReservationFacade#retrieveGlobalReservationData(String)} and
	 * {@link #getBookerEmailID(GlobalTravelReservationData, String, String)}
	 */
	@Deprecated
	String validateAndReturnBookerEmailId(String bookingReference, String lastName);

	/**
	 * Performs a validation on the booking, to check if the user is one of the travellers in the booking(only checks
	 * last name and passengerReference if additional security is enabled) and returns email id of the booker.
	 *
	 * @param globalReservationData
	 * 		a GlobalTravelReservationData
	 * @param lastName
	 * 		a string representing lastName of the user.
	 * @param passengerReference
	 * 		a string representing passengerReference.
	 * @return booker email id
	 */
	String getBookerEmailID(GlobalTravelReservationData globalReservationData, String lastName, String passengerReference);

	/**
	 * Creates a cart from order and attaches it to the session to start amendment process
	 *
	 * @param orderCode
	 * 		- code of the order which will be amended
	 * @param guid
	 * 		the guid
	 * @return true if there were no errors when starting amendment process
	 */
	Boolean amendOrder(String orderCode, String guid);

	/**
	 * Creates a refund payment transaction based on previous details in cart and current total to pay (which is negative
	 * in this case)
	 *
	 * @param totalToPay
	 * 		the total amount to be refunded
	 * @return TRUE is refund was successful
	 */
	Boolean createRefundPaymentTransaction(PriceData totalToPay);

	/**
	 * Check if the cancel order is possible for the order with the given orderCode
	 *
	 * @param orderCode
	 * 		as the code of the order to be cancelled
	 * @return true if the cancellation is possible, false otherwise
	 */
	boolean isCancelPossible(String orderCode);

	/**
	 * Returns the total amount to be refunded
	 *
	 * @param orderCode
	 * 		as the code of the order to be cancelled
	 * @return the PriceData corresponding to the amount to be refunded
	 */
	PriceData getRefundTotal(String orderCode);

	/**
	 * Returns the total amount to refund based on the given OrderEntryType
	 *
	 * @param orderCode
	 * 		as the code of the order to cancel
	 * @param orderEntryType
	 * 		as the orderEntryType
	 * @return the PriceData corresponding to the amount to refund
	 */
	PriceData getRefundTotal(String orderCode, OrderEntryType orderEntryType);

	/**
	 * Performs the cancellation of the order with the given orderCode
	 *
	 * @param orderCode
	 * 		as the code of the order to be cancelled
	 * @return true if the cancellation of the order was successful, false otherwise
	 */
	boolean cancelOrder(String orderCode);

	/**
	 * Returns a list of reservation data which corresponds to all the bookings of the current customer in session.
	 * In future release this method has been moved to {@link= BookingListFacade}
	 *
	 * @return list of current customer's bookings
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	List<ReservationData> getCurrentCustomerBookings();

	/**
	 * Returns a list of accommodation reservation data which corresponds to all accommodation bookings of the current
	 * customer in session
	 * In future release this method has been moved to {@link= BookingListFacade}
	 *
	 * @return list of current customer's bookings
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	List<AccommodationReservationData> getCurrentCustomerAccommodationBookings();

	/**
	 * Returns a list of global travel reservation data which corresponds to all travel bookings of the current customer
	 * in session
	 * In future release this method has been moved to {@link= BookingListFacade}
	 *
	 * @return list of current customer's bookings
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	List<GlobalTravelReservationData> getCurrentCustomerTravelBookings();

	/**
	 * Returns the next scheduled transportOffering data from current customer's active bookings
	 *
	 * @return current customer's scheduled transportOffering data
	 */
	TransportOfferingData getNextScheduledTransportOfferingData();

	/**
	 * Creates a cart from order without entries related to traveller
	 *
	 * @param orderCode
	 * 		the order code
	 * @param cancelledTravellerCode
	 * 		the cancelled traveller code
	 * @param cancelledTravellerUid
	 * 		the cancelled traveller uid
	 * @param guid
	 * 		the guid
	 * @return TRUE if cart creation was correct
	 */
	boolean beginTravellerCancellation(String orderCode, String cancelledTravellerCode, String cancelledTravellerUid, String guid);

	/**
	 * Converts current cart to retrieve totalToPay value
	 *
	 * @return total to pay
	 */
	PriceData getTotalToPay();

	/**
	 * Proceeds with placing an order for cart without removed traveller
	 *
	 * @param totalToPay
	 * 		the total to pay
	 * @return true if cancellation is successful and a new order is placed
	 * @deprecated Deprecated since version 2.0. Use {@link #cancelTraveller(PriceData, TravellerData)} instead.
	 */
	@Deprecated
	boolean cancelTraveller(PriceData totalToPay);

	/**
	 * Proceeds with placing an order for cart without removed traveller
	 *
	 * @param totalToPay
	 *           the total to pay
	 * @param travellerData
	 *           the travellerData to cancel
	 * @return true if cancellation is successful and a new order is placed
	 * @deprecated Deprecated since version 4.0. Use {@link #cancelTraveller(PriceData, PriceData, TravellerData)}
	 *             instead.
	 */
	@Deprecated
	boolean cancelTraveller(PriceData totalToPay, TravellerData travellerData);

	/**
	 * Proceeds with placing an order for cart without removed traveller
	 *
	 * @param totalToPay
	 *           the total to pay
	 * @param travellerData
	 *           the travellerData to cancel
	 * @return true if cancellation is successful and a new order is placed
	 */
	boolean cancelTraveller(PriceData totalToPay, PriceData totalToRefund, TravellerData travellerData);

	/**
	 * Gets current user uid.
	 *
	 * @return String current user uid
	 */
	String getCurrentUserUid();

	/**
	 * Checks if after cancellation of selected traveller, there will still be at least one adult left in the booking
	 *
	 * @param orderCode
	 * 		the order code
	 * @param cancelledTravellerCode
	 * 		the cancelled traveller code
	 * @return true if one adult will be on the booking after cancellation of the traveller
	 */
	boolean atleastOneAdultTravellerRemaining(String orderCode, String cancelledTravellerCode);

	/**
	 * Checks whether the order is a result of an amendment
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return true if the order is a result of amendment
	 */
	Boolean isAmendment(String bookingReference);

	/**
	 * Gets booking based on booking reference, then checks status of booking, IF status is amendment in progress, (that
	 * means this is amend order flow) , to get latest order details, it gets the booking which has original order code
	 * equal to booking reference, version id is null and status is NOT amendment in progress, i.e. the clone of original
	 * order(new order). ELSE returns booking retrieved based on booking reference
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return booking by booking reference and amending order
	 */
	ReservationData getBookingByBookingReferenceAndAmendingOrder(String bookingReference);

	/**
	 * For a logged in user, this method validates if the supplied booking reference belongs to one of the reservations
	 * made by the customer. For guest users it validates if the given booking reference matches with the booking
	 * reference used to login in the Manage My Booking component.
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return boolean boolean
	 */
	boolean validateUserForBooking(String bookingReference);

	/**
	 * This method validates if the supplied booking reference belongs to one of the reservations made by the customer
	 * for logged-in users or if booking reference is valid for current guest user during checkout.
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return boolean boolean
	 */
	boolean validateUserForCheckout(String bookingReference);

	/**
	 * Gets disrupted reservation which only has the items that have been disrupted
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return disrupted reservation
	 */
	ReservationData getDisruptedReservation(String bookingReference);

	/**
	 * Performs the acceptance of the order in case the user accepts the new booking suggested by the system
	 *
	 * @param orderCode
	 * 		the order code
	 * @return boolean boolean
	 */
	boolean acceptOrder(String orderCode);

	/**
	 * Maps an order to a user if the user is not an owner and the order hasn't been linked already
	 *
	 * @param bookingReferenceNumber
	 * 		the booking reference number
	 */
	void mapOrderToUserAccount(String bookingReferenceNumber);


	/**
	 * Check whether the current user is the owner of the booking reference
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return boolean boolean
	 */
	Boolean isUserOrderOwner(String bookingReference);

	/**
	 * Returns an accommodation reservation data fully populated with room rates per day
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return full accommodation booking
	 */
	AccommodationReservationData getFullAccommodationBooking(String bookingReference);

	/**
	 * Returns an accommodation reservation data without room rates per day
	 *
	 * @return basic accommodation booking from cart
	 */
	AccommodationReservationData getBasicAccommodationBookingFromCart();

	/**
	 * Returns an accommodation reservation data specific for Guest Details Page Controller.
	 *
	 * @return accommodation booking from cart.
	 */
	AccommodationReservationData getAccommodationReservationDataForGuestDetailsFromCart();

	/**
	 * Returns an accommodation reservation data fully populated with room rates per day. IF status is amendment in
	 * progress, (that means this is amend order flow) , to get latest order details, it gets the booking which has
	 * original order code equal to booking reference, version id is null and status is NOT amendment in progress, i.e.
	 * the clone of original order(new order). ELSE returns booking retrieved based on booking reference
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return booking by booking reference and amending order
	 */
	AccommodationReservationData getFullAccommodationBookingForAmendOrder(String bookingReference);

	/**
	 * Updates AccommodationOrderEntryGroup with guest details
	 *
	 * @param roomStayRefNum
	 * 		the room stay ref num
	 * @param guestData
	 * 		the guest data
	 * @param passengerTypeQuantityData
	 * 		the passenger type quantity data
	 * @param roomPreferenceData
	 * 		the room preference data
	 * @param checkInTime
	 * 		the check in time
	 * @return boolean boolean
	 */
	Boolean updateAccommodationOrderEntryGroup(int roomStayRefNum, GuestData guestData,
			List<PassengerTypeQuantityData> passengerTypeQuantityData, List<String> roomPreferenceData, String checkInTime);


	/**
	 * Returns guest occupancies from an accommodation or the rate plan if available or returns the minimum of two if
	 * both are available using the room stay reference number
	 *
	 * @param roomStayRefNum
	 * 		the room stay ref num
	 * @return guest occupancies from cart
	 */
	List<GuestOccupancyData> getGuestOccupanciesFromCart(int roomStayRefNum);

	/**
	 * Returns an global reservation data fully populated
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return global travel reservation data
	 */
	GlobalTravelReservationData getGlobalTravelReservationData(String bookingReference);

	/**
	 * Add an user request against a room stay booking
	 *
	 * @param request
	 * 		the request
	 * @param roomStayRefNumber
	 * 		the room stay ref number
	 * @param bookingReference
	 * 		the booking reference
	 * @return true if the operation of adding the request is successfully performed, false otherwise
	 */
	boolean addRequestToRoomStayBooking(String request, int roomStayRefNumber, String bookingReference);

	/**
	 * Remove an user request from a room stay booking
	 *
	 * @param requestCode
	 * 		the request code
	 * @param roomStayRefNumber
	 * 		the room stay ref number
	 * @param bookingReference
	 * 		the booking reference
	 * @return true if the operation of removing the request is successfully performed, false otherwise
	 */
	boolean removeRequestFromRoomStayBooking(String requestCode, int roomStayRefNumber, String bookingReference);

	/**
	 * Creates a cart from order and attaches it to the session to start add room amendment process
	 *
	 * @param orderCode
	 * 		the order code
	 * @return boolean boolean
	 * @deprecated Deprecated since version 3.0. Please use startAmendment() in {@link AccommodationAmendmentFacade} which does the
	 * same thing.
	 */
	@Deprecated
	boolean amendAddRoom(String orderCode);

	/**
	 * Defines a queryString needed to access accommodation details page based on selections that are currently in the
	 * cart.
	 *
	 * @return string string
	 */
	String buildAccommodationDetailsQueryFromCart();

	/**
	 * Returns only the room stays that have all associated entries with AmendStatus set to NEW
	 *
	 * @return new reserved room stays
	 */
	List<ReservedRoomStayData> getNewReservedRoomStays();

	/**
	 * Returns only the room stays that have all associated ACTIVE entries with AmendStatus set to SAME.
	 *
	 * @return old reserved room stays
	 */
	List<ReservedRoomStayData> getOldReservedRoomStays();

	/**
	 * Checks if is current cart of type.
	 *
	 * @param bookingType
	 * 		the booking type
	 * @return true, if current cart is of type
	 */
	boolean isCurrentCartOfType(String bookingType);

	/**
	 * Checks if is current cart of type.
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param bookingType
	 * 		the booking type
	 * @return true, if current cart is of type
	 */
	boolean isOrderOfType(String bookingReference, String bookingType);

	/**
	 * Creates a cart from the order without entries of type orderEntryType
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param orderEntryType
	 * 		the order entry type
	 * @param guid
	 * 		the guid
	 * @return true if cart creation was correct
	 */
	boolean beginPartialOrderCancellation(String bookingReference, OrderEntryType orderEntryType, String guid);

	/**
	 * Proceeds with placing an order for cart after the partialCancelOrder has begun
	 *
	 * @param totalToRefund
	 * 		the total to refund
	 * @param accommodation
	 * 		the accommodation
	 * @return true if cancellation is successful and a new order is placed
	 */
	boolean cancelPartialOrder(PriceData totalToRefund, OrderEntryType accommodation);

	/**
	 * Methods Changes for dates for accommodation Booking in the cart, create and add RoomRateCartData for each
	 * AccommodationAddToCartForm details, and link to the original AccommodationOrderEntryGroup
	 *
	 * @param checkInDate
	 * 		the check in date
	 * @param checkOutDate
	 * 		the check out date
	 * @param accommodationAvailabilityResponse
	 * 		object of accommodationAvailabilityResponse
	 * @param accommodationReservationData
	 * 		object of accommodationReservationData
	 * @return Success /Failure true/false
	 */
	boolean changeDatesForAccommodationBooking(String checkInDate, String checkOutDate,
			AccommodationAvailabilityResponseData accommodationAvailabilityResponse,
			AccommodationReservationData accommodationReservationData);

	/**
	 * This method updates all the RoomRate cart entries that are currently active to have quantity zero, status CHANGED
	 * and active flag to false
	 *
	 * @return boolean to indicate whether update operation was successful
	 */
	boolean deleteRoomRateEntries();

	/**
	 * Changes dates for AccommodationOrderEntryGroups in the cart and returns list of AccommodationOrderEntryGroups in
	 * the Cart.
	 *
	 * @param checkInDate
	 * 		the check in date
	 * @param checkOutDate
	 * 		the check out date
	 * @return accommodationOrderEntryGroupModels list of AccommodationOrderEntryGroupModel
	 */
	List<AccommodationOrderEntryGroupModel> changeDatesForAccommodationOrderEntryGroup(String checkInDate, String checkOutDate);

	/**
	 * Flags the booking as not visible to customer account
	 *
	 * @param orderCode
	 * 		the order code
	 * @return boolean
	 */
	boolean unlinkBooking(String orderCode);

	/**
	 * Returns only the bookings visible to the current user
	 * In future release this method has been moved to {@link= BookingListFacade}
	 *
	 * @return visible current customer travel bookings
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	List<GlobalTravelReservationData> getVisibleCurrentCustomerTravelBookings();

	/**
	 * Returns only the accommodation bookings visible to the current user
	 * In future release this method has been moved to {@link= BookingListFacade}
	 *
	 * @return visible current customer accommodation bookings
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	List<AccommodationReservationData> getVisibleCurrentCustomerAccommodationBookings();

	/**
	 * Retrieves the total price of the booking
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return booking total
	 */
	PriceData getBookingTotal(String bookingReference);

	/**
	 * Place order
	 *
	 * @return orderData representing the order
	 */
	boolean placeOrder();

	/**
	 * Returns the total amount already paid for the given orderEntryType and the given bookingReference
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param orderEntryType
	 * 		the order entry type
	 * @return order total paid for order entry type
	 */
	BigDecimal getOrderTotalPaidForOrderEntryType(String bookingReference, OrderEntryType orderEntryType);

	/**
	 * Returns the total amount to be paid for the given orderEntryType and the given bookingReference
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param orderEntryType
	 * 		the order entry type
	 * @return order total to pay for order entry type
	 */
	BigDecimal getOrderTotalToPayForOrderEntryType(String bookingReference, OrderEntryType orderEntryType);

	/**
	 * Calculates the total to pay for change dates for the order in cart
	 *
	 * @return totalAmountToPay order total to pay for change dates
	 */
	BigDecimal getOrderTotalToPayForChangeDates();

	/**
	 * Validate b 2 b user boolean.
	 *
	 * @param currentCustomer
	 * 		the current customer
	 * @param orderModel
	 * 		the order model
	 * @return boolean  validate b2b user against order to grant access
	 */
	boolean validateB2BUser(CustomerModel currentCustomer, OrderModel orderModel);

	/**
	 * Provides the payment information for The change date functionality. It provides amount paid already paid for
	 * order, new amount to be paid/refund(if any) , the payment action required(REFUND,PAYABLE or SAME).
	 *
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 * @param accommodationAvailabilityResponse
	 * 		the accommodation availability response
	 * @return change date payment results map
	 */
	Map<String, String> getChangeDatePaymentResultsMap(AccommodationReservationData accommodationReservationData,
			AccommodationAvailabilityResponseData accommodationAvailabilityResponse);

	/**
	 * Returns List of room stay reference numbers of the added rooms
	 *
	 * @return accommodation order entry group refs
	 */
	List<Integer> getAccommodationOrderEntryGroupRefs();

	/**
	 * Returns List of room stay reference numbers of the newly added rooms
	 *
	 * @return new accommodation order entry group refs
	 */
	List<Integer> getNewAccommodationOrderEntryGroupRefs();

	/**
	 * Returns List of room stay reference numbers of the old added active rooms
	 *
	 * @return old accommodation order entry group refs
	 */
	List<Integer> getOldAccommodationOrderEntryGroupRefs();

	/**
	 * Returns Total Paid for the booking
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return order total paid
	 */
	PriceData getOrderTotalPaid(String bookingReference);

	/**
	 * Calculates the booking total amount for the orderEntryType in the abstract order
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param orderEntryType
	 * 		the orderEntryType
	 * @return totalAmount booking total by order entry type
	 */
	PriceData getBookingTotalByOrderEntryType(String bookingReference, OrderEntryType orderEntryType);

	/**
	 * Checks if the order corresponding to the given orderCode has the same BookingJourneyType attribute than the given one.
	 *
	 * @param orderCode
	 * 		as the order code
	 * @param bookingJourneyType
	 * 		as the bookingJourneyType
	 * @return true if the order corresponding to the orderCode has the same BookingJourneyType attribute than the given
	 * bookingJourneyType, false otherwise.
	 */
	boolean checkBookingJourneyType(String orderCode, BookingJourneyType bookingJourneyType);

	/**
	 * Retrieves a map of parameters needed to access accommodation details page (accommodation offering, check in date,
	 * check out date)
	 *
	 * @return accommodation details parameters
	 */
	Map<String, String> getAccommodationDetailsParametersFromCart();

	/**
	 * Returns a {@link PriceData) representing the total amount that has been paid but cannot be refunded
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return the not refundable amount
	 * @return*
	 */
	PriceData getNotRefundableAmount(String bookingReference);

	/**
	 * Checks whether the order has the additional security flag set to true
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @return true if the order has the additional security flag set to true
	 */
	boolean isAdditionalSecurityActive(String bookingReference);

	/**
	 * Calculates the total to refund for cancelled traveller
	 *
	 * @param orderCode
	 * @return
	 */
	PriceData getRefundForCancelledTraveller();
}
