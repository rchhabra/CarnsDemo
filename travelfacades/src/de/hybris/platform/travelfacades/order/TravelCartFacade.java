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

package de.hybris.platform.travelfacades.order;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.order.PaymentOptionData;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.subscriptionfacades.order.SubscriptionCartFacade;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.List;
import java.util.Map;


/**
 * Travel Cart facade interface. Service is responsible for updating all necessary information specific for a travel
 * bundle cart.
 */
public interface TravelCartFacade extends SubscriptionCartFacade
{

	/**
	 * Gets order entry.
	 *
	 * @param productCode
	 *           the product code
	 * @param travelRouteCode
	 *           the travel route code
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travellerCode
	 *           the traveller code
	 * @param bundleNoCheckRequired
	 *           the bundle no check required
	 * @return OrderEntryData order entry
	 */
	OrderEntryData getOrderEntry(String productCode, String travelRouteCode, List<String> transportOfferingCodes,
			String travellerCode, boolean bundleNoCheckRequired);

	/**
	 * Add properties to cart entry.
	 *
	 * @param productCode
	 *           the product code
	 * @param entryNo
	 *           the entry no
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travelRouteCode
	 *           the travel route code
	 * @param originDestinationRefNumber
	 *           the origin destination ref number
	 * @param travellerCode
	 *           the traveller code
	 * @param active
	 *           the active
	 * @param amendStatus
	 *           the amend status
	 * @deprecated Deprecated since version 2.0. Replaced by
	 *             {@link #addPropertiesToCartEntry(String, int, List, String, int, String, Boolean, AmendStatus, String)}
	 */
	@Deprecated
	void addPropertiesToCartEntry(String productCode, int entryNo, List<String> transportOfferingCodes, String travelRouteCode,
			int originDestinationRefNumber, String travellerCode, Boolean active, AmendStatus amendStatus);

	/**
	 * Add properties to cart entry.
	 *
	 * @param productCode
	 *           the product code
	 * @param entryNo
	 *           the entry no
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travelRouteCode
	 *           the travel route code
	 * @param originDestinationRefNumber
	 *           the origin destination ref number
	 * @param travellerCode
	 *           the traveller code
	 * @param active
	 *           the active
	 * @param amendStatus
	 *           the amend status
	 * @param addToCartCriteriaType
	 *           the add to cart criteria type
	 */
	void addPropertiesToCartEntry(String productCode, int entryNo, List<String> transportOfferingCodes, String travelRouteCode,
			int originDestinationRefNumber, String travellerCode, Boolean active, AmendStatus amendStatus,
			String addToCartCriteriaType);

	/**
	 * Add properties to cart entry.
	 *
	 * @param productCode
	 *           the product code
	 * @param entryNo
	 *           the entry no
	 * @param params
	 *           the params
	 */
	void addPropertiesToCartEntry(String productCode, int entryNo, Map<String, Object> params);

	/**
	 * This method adds a configured accommodation as a selected accommodation to the cart
	 *
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param travellerCode
	 *           the traveller code
	 * @param configuredAccommodationIdentifier
	 *           the configured accommodation identifier
	 */
	void addSelectedAccommodationToCart(String transportOfferingCode, String travellerCode,
			String configuredAccommodationIdentifier);

	/**
	 * This method removes a selected accommodation from the cart
	 *
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param travellerCode
	 *           the traveller code
	 * @param configuredAccommodationUid
	 *           the configured accommodation identifier
	 */
	void removeSelectedAccommodationFromCart(String transportOfferingCode, String travellerCode,
			String configuredAccommodationUid);

	/**
	 * Checks if the current cart in session is used for amendment
	 *
	 * @return true if session cart is used for amendment
	 */
	Boolean isAmendmentCart();

	/**
	 * Checks if the current cart in session has the additional security active
	 *
	 * @return true if session cart has the additional security active
	 */
	Boolean isAdditionalSecurityActive();

	/**
	 * Sets the additional security active on the session cart
	 *
	 * @param additionalSecurity
	 *           the additionalSecurity value
	 * @return true if it sets the session cart additional security
	 */
	void setAdditionalSecurity(Boolean additionalSecurity);

	/**
	 * Removes delivery address from a cart which will be then copied over from billing information
	 */
	void removeDeliveryAddress();

	/**
	 * Gets the original order code from the amendment cart
	 *
	 * @return original order code
	 */
	String getOriginalOrderCode();

	/**
	 * Method to add voucher to cart.
	 *
	 * @param voucherCode
	 *           the voucher code
	 * @throws VoucherOperationException
	 *            the voucher operation exception
	 */
	void applyVoucher(String voucherCode) throws VoucherOperationException;

	/**
	 * Method to remove voucher from cart.
	 *
	 * @param voucherCode
	 *           the voucher code
	 * @throws VoucherOperationException
	 *            the voucher operation exception
	 */
	void removeVoucher(String voucherCode) throws VoucherOperationException;

	/**
	 * Method re-calculates cart after adding travel specific details to cart entry i.e. price level, route code,
	 * traveller details etc.
	 */
	void recalculateCart();

	/**
	 * Method to add the fare product including bundled products to cart
	 *
	 * @param addBundleToCartRequestData
	 *           the add bundle to cart request data
	 * @return list of cart modification data
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 * @deprecated since 4.0 use {@link #addBundleToCart(AddBundleToCartRequestData)} instead
	 */
	@Deprecated
	List<CartModificationData> addToCartBundle(AddBundleToCartRequestData addBundleToCartRequestData)
			throws CommerceCartModificationException;

	/**
	 * Method to add the fare product including bundled products to cart
	 * using bundle implementation introduced with Hybris 6.4
	 *
	 * @param addBundleToCartRequestData
	 * 		the add bundle to cart request data
	 * @return list of cart modification data
	 * @throws CommerceCartModificationException
	 * 		the commerce cart modification exception
	 */
	List<CartModificationData> addBundleToCart(AddBundleToCartRequestData addBundleToCartRequestData)
			throws CommerceCartModificationException;

	/**
	 * Checks if user made any changes during amendment
	 *
	 * @return true if some changes were made
	 */
	boolean hasCartBeenAmended();

	/**
	 * Gets the current cart code
	 *
	 * @return current cart code
	 */
	String getCurrentCartCode();

	/**
	 * Returns true if the product is available for the transport offering.
	 *
	 * @param productCode
	 *           the product code
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param quantityToOffer
	 *           the quantity to offer
	 * @return true if product is available, else false.
	 */
	boolean isProductAvailable(String productCode, List<String> transportOfferingCodes, Long quantityToOffer);

	/**
	 * Method to evaluate cart using rules engine for fees and discounts
	 */
	void evaluateCart();

	/**
	 * This method will return true if the current cart can be used in booking/amendment flow, false otherwise
	 *
	 * @return the boolean
	 */
	boolean isCurrentCartValid();

	/**
	 * Calculates the value of total to pay or refund for current reservation
	 *
	 * @return total to pay
	 */
	PriceData getTotalToPayPrice();

	/**
	 * Assign a specific {@link OrderEntryType} to a cart entry
	 *
	 * @param type
	 *           the type to set
	 * @param entryNumber
	 *           the number of the cart entry to update
	 */
	void setOrderEntryType(OrderEntryType type, int entryNumber);

	/**
	 * Returns the list of available payment options for the current order
	 *
	 * @return payment options
	 */
	List<PaymentOptionData> getPaymentOptions();

	/**
	 * Deletes current cart to avoid processing amendment carts in a normal booking journey
	 */
	void deleteCurrentCart();

	/**
	 * Checks if the selected payment options passed as a list of associated transaction is a valid option comparing it
	 * with all the available options
	 *
	 * @param transactions
	 *           the transactions
	 * @return boolean
	 */
	boolean isValidPaymentOption(List<PaymentTransactionData> transactions);

	/**
	 * Returns the list of available payment options for the current order to pay the remaining amount for a given
	 * OrderEntryType
	 *
	 * @param orderEntryType
	 *           order entry type
	 *
	 * @return the list of PaymentOptionData
	 */
	List<PaymentOptionData> getPaymentOptions(OrderEntryType orderEntryType);

	/**
	 * Calculates partial payment
	 *
	 * @return partial payment to pay
	 */
	PriceData getPartialPaymentAmount();

	/**
	 * Calculates actual Total to pay amount after change dates
	 *
	 * @return total to pay
	 */
	PriceData getTotalToPayPriceAfterChangeDates();

	/**
	 * Returns the total booking amount
	 *
	 * @param originalOrderCode
	 *           original order code
	 * @return booking total
	 */
	PriceData getBookingTotal(String originalOrderCode);

	/**
	 * Returns amount due for the booking
	 *
	 * @param totalAmount
	 *           total amount
	 * @param amountPaid
	 *           amount paid
	 * @return due amount
	 */
	PriceData getBookingDueAmount(PriceData totalAmount, PriceData amountPaid);

	/**
	 * Returns current cart total
	 *
	 * @return cart total
	 */
	PriceData getCartTotal();

	/**
	 * Calculates the next bundle number to be used while adding products belonging to bundle to the cart using directly
	 * methods requiring it.
	 *
	 * @return the next number to use when manually add bundle products to cart
	 */
	Integer getNextBundleNumberToUse();

	/**
	 * Updates order entries with the correct incremental bundle number after add to cart
	 *
	 * @param entryNumbers
	 *           entry numbers
	 * @param forcedBundleNumber
	 *           forcedBundleNumber
	 */
	void updateBundleEntriesWithBundleNumber(List<Integer> entryNumbers, Integer forcedBundleNumber);

	/**
	 * Validates cart to check if it matches currently selected options. If not, the cart is cleared.
	 *
	 * @param departureLocation
	 *           departure location
	 * @param arrivalLocation
	 *           arrival location
	 * @param departureDate
	 *           departure date
	 * @param returnDate
	 *           return date
	 */
	void validateCart(String departureLocation, String arrivalLocation, String departureDate, String returnDate);

	/**
	 * Cleans up the cart from bundles. It removes all the entries of type TRANSPORT with originDestinationRefNumber
	 * greater than the given odRefNum.
	 *
	 * @param odRefNum
	 *           as the minimum originDestinationRefNumber
	 */
	void cleanUpCartForMinOriginDestinationRefNumber(Integer odRefNum);

	/**
	 * Removes all cart entries for specific origin destination ref number.
	 *
	 * @param odRefNum
	 *           origin destination ref number
	 */
	void removeEntriesForOriginDestinationRefNumber(Integer odRefNum);

	/**
	 * Validates the abstract order entries in the cart, to check that if n is the max originDestinationRefNumber, there
	 * is at least one abstract order entry for each i from 0 to n.
	 *
	 * @return true if the cart contains at least one abstract order entry type for each originDestinationRefNumeber,
	 *         false otherwise.
	 */
	boolean validateOriginDestinationRefNumbersInCart();

}


