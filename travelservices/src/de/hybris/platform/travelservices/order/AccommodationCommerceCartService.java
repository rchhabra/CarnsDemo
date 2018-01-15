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

package de.hybris.platform.travelservices.order;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import java.util.Date;
import java.util.List;


/**
 * This service provides functionality of adding, removing and modifying order entries specific to accommodations.
 */
public interface AccommodationCommerceCartService
{
	/**
	 * Populates entry type, dates and amend status on a accommodationOrderEntryInfo attribute of a given entry
	 *
	 * @param entryNumber
	 *           the entry number
	 * @param dates
	 *           the dates
	 */
	void populateAccommodationDetailsOnRoomRateEntry(int entryNumber, List<Date> dates);

	/**
	 * Creates order entry group which will hold a set of data related to one of the roomStays. It will also be linked to
	 * all order entries related to this roomStay.
	 *
	 * @param entryNumbers
	 *           the entry numbers
	 * @param checkInDate
	 *           the check in date
	 * @param checkOutDate
	 *           the check out date
	 * @param accOffCode
	 *           the acc off code
	 * @param accCode
	 *           the acc code
	 * @param ratePlanCode
	 *           the rate plan code
	 * @param refNumber
	 *           the ref number
	 */
	void createOrderEntryGroup(List<Integer> entryNumbers, Date checkInDate, Date checkOutDate, String accOffCode, String accCode,
			String ratePlanCode, int refNumber);

	/**
	 * Link accommodationOrderEntryGroup to new Entries of the amended Order based on the paymentType and the
	 * paymentMethod used during original method
	 *
	 * @param accommodationOrderEntryGroup
	 *           the accommodation order entry group
	 * @param entryNumbers
	 *           the entry numbers
	 * @param paymentType
	 *           then type of transaction to create
	 * @return true/false
	 *
	 */
	boolean amendOrderEntryGroup(AccommodationOrderEntryGroupModel accommodationOrderEntryGroup, List<Integer> entryNumbers,
			String paymentType);

	/**
	 * Retrieves a list of AccommodationOrderEntryGroup from current session cart which match given
	 * accommodationOfferingCode, accommodationCode and ratePlan set.
	 *
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param accommodationCode
	 *           the accommodation code
	 * @param ratePlanCode
	 *           the rate plan code
	 * @return list of AccommodationOrderEntryGroup
	 */
	List<AccommodationOrderEntryGroupModel> getNewAccommodationOrderEntryGroups(String accommodationOfferingCode,
			String accommodationCode, String ratePlanCode);

	/**
	 * Identifies the maximum roomStayRefNumber based on the AccommodationOrderEntryGroups that are currently in the
	 * cart.
	 *
	 * @return maxRoomStayRefNumber max room stay ref number
	 */
	int getMaxRoomStayRefNumber();

	/**
	 * Removes a number of given AccommodationOrderEntryGroups from cart and all entries associated to them. It then
	 * normalises the roomStayRefNumbers not to lose track of them.
	 *
	 * @param accommodationOrderEntryGroups
	 *           - accommodationOrderEntryGroups currently in cart
	 * @param numberToRemove
	 *           - specifies how many AccommodationOrderEntryGroups should be removed.
	 */
	void removeAccommodationOrderEntryGroups(List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups,
			int numberToRemove);

	/**
	 * Removes the accommodationOfferingEntries that were associated with the option which was affected during add to
	 * cart.
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
	void cleanupCartBeforeAddition(String accommodationOfferingCode, String checkInDateTime, String checkOutDateTime);

	/**
	 * @param accommodationOfferingCode accommodation offering code
	 * @param checkInDateTime           check in date
	 * @param checkOutDateTime          check out date
	 * @param entryGroups               accommodation order entry groups
	 * @return true if cart is valid
	 */
	boolean validateCart(String accommodationOfferingCode, String checkInDateTime, String checkOutDateTime,
			List<AccommodationOrderEntryGroupModel> entryGroups);

	/**
	 * Removes all the entries in the cart.
	 */
	void emptyCart();

	/**
	 * Returns a number of AccommodationOrderEntryGroups that are currently in the cart.
	 *
	 * @return number of entry groups in cart
	 */
	int getNumberOfEntryGroupsInCart();

	/**
	 * Removes accommodation order entry group and entries associated to it from the cart
	 *
	 * @param roomStayReference
	 *           the room stay reference
	 * @return boolean
	 */
	Boolean removeAccommodationOrderEntryGroup(int roomStayReference);

	/**
	 * Retrieves AccommodationOffering that is currently in the cart
	 *
	 * @return current accommodation offering
	 */
	String getCurrentAccommodationOffering();

	/**
	 * Removes accommodation order entry and related entries from the cart without room stay ref normalisation
	 * @param roomStayRefNumber
	 */
	void removeRoomStay(Integer roomStayRefNumber);

	/**
	 * Check whether new room is added into the cart.
	 * 
	 * @return
	 */
	boolean isNewRoomInCart();

	/**
	 * Retrieves a list of {@link CartEntryModel} that matches the provided {@link ProductModel} and
	 * {@link AccommodationModel} in the {@link CartModel}
	 * 
	 * @param cartModel
	 * @param product
	 * @param cartEntryModel
	 *
	 * @return current accommodation offering
	 */
	List<CartEntryModel> getEntriesForProductAndAccommodation(CartModel cartModel, ProductModel product,
			CartEntryModel cartEntryModel);
}
