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

package de.hybris.platform.travelfacades.order;

import de.hybris.platform.commercefacades.accommodation.RoomRateCartData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;

import java.util.Date;
import java.util.List;


/**
 * This facade provides functionality of adding or removing accommodation specific products to / from cart.
 */
public interface AccommodationCartFacade
{
	/**
	 * Perform accommodation into cart, numberOfRooms times.
	 *
	 * @param checkInDate
	 *           the check in date
	 * @param checkOutDate
	 *           the check out date
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param accommodationCode
	 *           the accommodation code
	 * @param rates
	 *           the rates
	 * @param numberOfRooms
	 *           the number of rooms
	 * @param ratePlanCode
	 *           the rate plan code
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	void addAccommodationToCart(Date checkInDate, Date checkOutDate, String accommodationOfferingCode, String accommodationCode,
			List<RoomRateCartData> rates, int numberOfRooms, String ratePlanCode) throws CommerceCartModificationException;

	/**
	 * 1. Verify how many AccommodationOrderEntryGroups are in the cart for given accommodation, accommodationOffering
	 * and ratePlan set. 2. Compare this number to numberOfRooms to get roomQuantityToAdd (or remove) 3a. if
	 * roomQuantityToAdd == 0 -> do nothing 3b. if roomQuantityToAdd < 0 -> get the list of results from point 1 and
	 * remove the last few (or all of them if size == Math.abs(roomQuantityToAdd) ) - remove all entries related to these
	 * AccommodationOfferGroupEntries and then the groupEntries themselves and then normalize roomStayRefNumbers 3c.if
	 * roomQuantityToAdd > 0 -> perform addAccommodationBookingToCart roomQuantityToAdd times
	 *
	 * @param checkInDate
	 *           the check in date
	 * @param checkOutDate
	 *           the check out date
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param accommodationCode
	 *           the accommodation code
	 * @param rates
	 *           the rates
	 * @param numberOfRooms
	 *           the number of rooms
	 * @param ratePlanCode
	 *           the rate plan code
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	void addAccommodationsToCart(Date checkInDate, Date checkOutDate, String accommodationOfferingCode, String accommodationCode,
			List<RoomRateCartData> rates, int numberOfRooms, String ratePlanCode) throws CommerceCartModificationException;


	/**
	 * Perform addAccommodationBookingToCart, numberOfRooms times. and then link the entries to given
	 * accommodationOrderEntryGroup
	 *
	 * @param accommodationOrderEntryGroup
	 *           the accommodation order entry group
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param accommodationCode
	 *           the accommodation code
	 * @param rates
	 *           the rates
	 * @param numberOfRooms
	 *           the number of rooms
	 * @param ratePlanCode
	 *           the rate plan code
	 * @param paymentType
	 *           the payment type
	 * @return the boolean
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	Boolean addAccommodationsToCart(AccommodationOrderEntryGroupModel accommodationOrderEntryGroup,
			String accommodationOfferingCode, String accommodationCode, List<RoomRateCartData> rates, int numberOfRooms,
			String ratePlanCode, String paymentType) throws CommerceCartModificationException;


	/**
	 * Removes entries associated to selected option that was affected during add to cart.
	 *
	 * @param accommodationCode
	 *           the accommodation code
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param ratePlanCode
	 *           the rate plan code
	 */
	void rollbackAccommodationEntries(String accommodationCode, String accommodationOfferingCode, String ratePlanCode);

	/**
	 * Checks if the accommodationOfferingCode matches the one that is currently in the cart. If not, all
	 * AccommodationOrderEntryGroups should be removed from cart because multi accommodation offering booking is not
	 * supported
	 *
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param checkInDateTime
	 *           the check in date time
	 * @param checkOutDateTime
	 *           the check out date time
	 */
	void cleanUpCartBeforeAddition(String accommodationOfferingCode, String checkInDateTime, String checkOutDateTime);

	/**
	 * This method checks if entries that are currently in the cart match the request. If they don't the cart is removed.
	 *
	 * @param accommodationOfferingCode
	 * @param checkInDate
	 * @param checkOutDate
	 * @param roomStayCandidates
	 */
	void cleanUpCartBeforeAddition(String accommodationOfferingCode, String checkInDate, String checkOutDate,
			List<RoomStayCandidateData> roomStayCandidates);

	/**
	 * Removes all the entries in the cart.
	 */
	void emptyCart();

	/**
	 * Checks if the number of rooms in the cart after addition would be within the configured limit
	 *
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param accommodationCode
	 *           the accommodation code
	 * @param ratePlanCode
	 *           the rate plan code
	 * @param numberOfRooms
	 *           the number of rooms
	 * @param allowedNumberOfRooms
	 *           the allowed number of rooms
	 * @return boolean boolean
	 */
	boolean validateNumberOfRoomsToAdd(String accommodationOfferingCode, String accommodationCode, String ratePlanCode,
			int numberOfRooms, int allowedNumberOfRooms);

	/**
	 * Methods that performs the addToCart of a product for the given productCode. If the product is already in the cart
	 * and the quantity is greater than 0, the abstractOrderEntry will be updated. If the product is already in the cart
	 * and the quantity 0, the abstractOrderEntry will be remove. If the product is not yet in the cart and the quantity
	 * is greater than 0, a new abstractOrderEntry will be created.
	 *
	 * @param productCode
	 *           as the code of the product to be added to the cart
	 * @param roomStayReferenceNumber
	 *           as the reference Number of the room that the product is added to
	 * @param quantity
	 *           as the quantity of the product to add to the cart
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	void addProductToCart(String productCode, int roomStayReferenceNumber, long quantity) throws CommerceCartModificationException;

	/**
	 * Removes the Accommodation Order Entry Group from cart alongside with all the associated entries
	 *
	 * @param roomStayReference
	 *           the room stay reference
	 * @return boolean boolean
	 */
	Boolean removeAccommodationOrderEntryGroup(int roomStayReference);

	/**
	 * Validates if there are any AccommodationOrderEntryGroups in the cart.
	 *
	 * @return boolean boolean
	 */
	boolean validateAccommodationCart();

	/**
	 * Returns current accommodation offering that is in the cart
	 *
	 * @return current accommodation offering
	 */
	String getCurrentAccommodationOffering();

	/**
	 * Collect room rates list.
	 *
	 * @param form
	 *           the form
	 * @return the list
	 */
	List<RoomRateCartData> collectRoomRates(AccommodationAddToCartForm form);

	/**
	 * Determines if an amendment is happening on services
	 *
	 * @return true if an amendment is happening on services
	 */
	Boolean isAmendmentForServices();

	/**
	 * Check whether new room is added into the cart.
	 *
	 * @return true if new room is added into the cart
	 */
	boolean isNewRoomInCart();

	/**
	 * This method adds to the cart all the valid room rates belonging to a bundle according with a date range
	 *
	 * @param accommodationBundleTemplateModel
	 * @param addDealToCartData
	 * @return list
	 * @throws CommerceCartModificationException
	 */
	List<CartModificationData> addAccommodationBundleToCart(AccommodationBundleTemplateModel accommodationBundleTemplateModel,
			AddDealToCartData addDealToCartData) throws CommerceCartModificationException;

	/**
	 * Replaces the current room stay with a newly selected option. It doesn't do normalisation of room stay ref number
	 * on removal because the ref number should be the same for the newly added option
	 *
	 * @param checkInDate
	 *           check in date
	 * @param checkOutDate
	 *           check out date
	 * @param accommodationOfferingCode
	 *           accommodation offering code
	 * @param accommodationCode
	 *           accommodation code
	 * @param rates
	 *           room rates
	 * @param numberOfRooms
	 *           number of rooms
	 * @param ratePlanCode
	 *           rate plan code
	 * @param roomStayRefNumber
	 *           room stay ref number
	 * @throws CommerceCartModificationException
	 *            commerce cart modification exception
	 */
	void replaceAccommodationInCart(Date checkInDate, Date checkOutDate, String accommodationOfferingCode,
			String accommodationCode, List<RoomRateCartData> rates, int numberOfRooms, String ratePlanCode,
			Integer roomStayRefNumber) throws CommerceCartModificationException;

	/**
	 * Method responsible to add the accommodation to the cart for the traveller based on the following scenarios: 1. If
	 * the accommodation(ConfiguredAccommodationModel) is configured in the system. 2. If the accommodation is bookable.
	 * 3. if the products of accommodation are referenced by the fare product entry in the cart. 4. if the accommodation
	 * is not already booked by other travellers in the transportOffering. 5. If the accommodation is not already added
	 * to the cart by other passenger of same cart.
	 *
	 * @param accommodationUid
	 *           the accommodation uid
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param travellerCode
	 *           the traveller code
	 * @param originDestinationRefNo
	 *           the origin destination ref no
	 * @param travelRoute
	 *           the travel route
	 * @return true, if successful
	 */
	boolean addSelectedAccommodationToCart(String accommodationUid, String transportOfferingCode, String travellerCode,
			String originDestinationRefNo, String travelRoute);


	/**
	 * Removes the selected accommodation from cart.
	 *
	 * @param accommodationUid
	 *           the accommodation uid
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param travellerCode
	 *           the traveller code
	 * @param travelRoute
	 *           the travel route
	 * @return true, if successful
	 */
	boolean removeSelectedAccommodationFromCart(String accommodationUid, String transportOfferingCode, String travellerCode,
			String travelRoute);

}
