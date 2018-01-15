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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.exceptions.RequestKeyGeneratorException;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * BookingService interface that provides functionality to manage Bookings.
 */
public interface BookingService
{

	/**
	 * Creates a payment transaction for refund
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @param amountToRefund
	 *           the amount to refund
	 * @return true if successfully created transaction
	 *
	 * @deprecated Deprecated since version 2.0. Use {@link #createRefundPaymentTransaction(AbstractOrderModel, BigDecimal, List)}
	 * instead.
	 */
	@Deprecated
	Boolean createRefundPaymentTransaction(AbstractOrderModel abstractOrder, BigDecimal amountToRefund);

	/**
	 * Creates a payment transaction for refund
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @param amountToRefund
	 *           the amount to refund
	 * @param entries
	 *           the entries the transaction is linked to
	 * @return true if successfully created transaction
	 */
	Boolean createRefundPaymentTransaction(AbstractOrderModel abstractOrder, BigDecimal amountToRefund,
			List<AbstractOrderEntryModel> entries);

	/**
	 * Return a list of all the orders for the given orderCode and baseStore
	 *
	 * @param orderCode
	 *           the order code
	 * @param baseStore
	 *           the base store
	 *
	 * @return the list of OrderModel
	 */
	List<OrderModel> getAllOrders(String orderCode, BaseStoreModel baseStore);

	/**
	 * Check if the cancel order is possible for the order with the given orderCode
	 *
	 * @param order
	 *           as the order to be cancelled
	 * @return true if the cancellation is possible, false otherwise
	 */
	boolean isCancelPossible(OrderModel order);

	/**
	 * Performs the cancellation of the order with the given orderCode
	 *
	 * @param order
	 *           as the order to be cancelled
	 * @return true if the cancellation of the order was successful, false otherwise
	 */
	boolean cancelOrder(OrderModel order);

	/**
	 * Calculate the total amount to be refunded
	 *
	 * @param order
	 *           as the order
	 * @return the BigDecimal value to be refunded
	 */
	BigDecimal getTotalToRefund(OrderModel order);

	/**
	 * Calculate the total amount to refund based on the given orderEntryType
	 *
	 * @param order
	 *           as the order
	 * @param orderEntryType
	 *           as the orderEntryType
	 * @return the BigDecimal value to refund
	 */
	BigDecimal getTotalToRefund(OrderModel order, OrderEntryType orderEntryType);

	/**
	 * Proceeds with cancellation of traveller, creates a refund transaction if there is any refund available
	 *
	 * @param totalToRefund
	 *           the total to refund
	 * @return true if successfully created refund payment transaction
	 *
	 * @deprecated Deprecated since version 2.0. Use {@link #cancelTraveller(BigDecimal, TravellerData)} instead.
	 */
	@Deprecated
	boolean cancelTraveller(BigDecimal totalToRefund);

	/**
	 * Proceeds with cancellation of traveller, creates a refund transaction if there is any refund available
	 *
	 * @param totalToRefund
	 *           the total to refund
	 * @param travellerData
	 *           the travellerData for which to create the refund payment
	 * @return true if successfully created refund payment transaction
	 */
	boolean cancelTraveller(BigDecimal totalToRefund, TravellerData travellerData);

	/**
	 * Gets order model from store.
	 *
	 * @param bookingReference
	 *           the booking reference
	 * @return OrderModel order model from store
	 */
	OrderModel getOrderModelFromStore(String bookingReference);

	/**
	 * Retrieves abstractOrderEntry from given abstractOrderModel
	 *
	 * @param abstractOrderModel
	 *           - the abstract order model
	 * @param productCode
	 *           the productCode
	 * @param travelRouteCode
	 *           the travel Route Code
	 * @param transportOfferingCodes
	 *           list of transport Offering Code
	 * @param travellerCodes
	 *           the Traveller unique id
	 * @param bundleNoCheckRequired
	 *           flag to check if bundle no. check is required.
	 * @return AbstractOrderEntryModel order entry
	 */
	AbstractOrderEntryModel getOrderEntry(AbstractOrderModel abstractOrderModel, String productCode, String travelRouteCode,
			List<String> transportOfferingCodes, List<String> travellerCodes, boolean bundleNoCheckRequired);

	/**
	 * Retrieves abstractOrderEntry from given abstractOrderModel's code
	 *
	 * @param originalOrderCode
	 *           the original order code
	 * @param productCode
	 *           the product code
	 * @param travelRouteCode
	 *           the travel route code
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travellerCodes
	 *           the traveller codes
	 * @param bundleNoCheckRequired
	 *           the bundle no check required
	 * @return order entry for given code of abstract order model
	 */
	AbstractOrderEntryModel getOriginalOrderEntry(String originalOrderCode, String productCode, String travelRouteCode,
			List<String> transportOfferingCodes, List<String> travellerCodes, Boolean bundleNoCheckRequired);

	/**
	 * Checks if user made any changes during amendment
	 *
	 * @return true if something has been changed
	 */
	boolean hasCartBeenAmended();

	/**
	 * Retrieves the orderModel for the specified bookingReference.
	 *
	 * @param bookingReference
	 *           the booking reference
	 * @return OrderModel object.
	 */
	OrderModel getOrder(String bookingReference);

	/**
	 * Returns the quantity of products in the Order for a transportOffering.
	 *
	 * @param bookingReference
	 *           the booking reference
	 * @param productModel
	 *           the product model
	 * @param transportOfferingModel
	 *           the transport offering model
	 * @return a Long
	 */
	Long getProductQuantityInOrderForTransportOffering(String bookingReference, ProductModel productModel,
			TransportOfferingModel transportOfferingModel);

	/**
	 * Checks if a product is included in a bundle
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @param productCode
	 *           the product code
	 * @param qty
	 *           the qty
	 * @param travelRouteCode
	 *           the travel route code
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travellerCodes
	 *           the traveller codes
	 * @return true if the product is included in a bundle, false otherwise
	 */
	boolean checkBundleToAmendProduct(AbstractOrderModel abstractOrderModel, String productCode, long qty, String travelRouteCode,
			List<String> transportOfferingCodes, List<String> travellerCodes);

	/**
	 * Validates if atleast one adult traveller belongs to booking(apart from the one removed currently), while removing
	 * traveller from the booking.
	 *
	 * @param orderCode
	 *           the order code
	 * @param cancelledTravellerCode
	 *           the cancelled traveller code
	 * @return boolean
	 */
	boolean atleastOneAdultTravellerRemaining(String orderCode, String cancelledTravellerCode);

	/**
	 * Gets order with original order code equal to booking reference
	 *
	 * @param bookingReference
	 *           the booking reference
	 * @return order model by original order code
	 */
	OrderModel getOrderModelByOriginalOrderCode(String bookingReference);

	/**
	 * Updates the order with the status provided as a param
	 *
	 * @param orderModel
	 *           the order model
	 * @param status
	 *           the status
	 */
	void updateOrderStatus(OrderModel orderModel, OrderStatus status);

	/**
	 * Returns list of AccommodationOrderEntryGroups that are in the abstract order.
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @return accommodation order entry groups
	 */
	List<AccommodationOrderEntryGroupModel> getAccommodationOrderEntryGroups(AbstractOrderModel abstractOrder);


	/**
	 * Returns AccommodationOrderEntryGroupModel based on roomStayRefNumber and cart number
	 *
	 * @param roomStayRefNum
	 *           the room stay ref num
	 * @param abstractOrder
	 *           the abstract order
	 * @return accommodation order entry group
	 */
	AccommodationOrderEntryGroupModel getAccommodationOrderEntryGroup(int roomStayRefNum, AbstractOrderModel abstractOrder);

	/**
	 * Check if the reservation for the specified orderEntryType has been cancelled.
	 *
	 * @param abstractOrderModel
	 *           as the abstractOrderModel to be checked
	 * @param orderEntryType
	 *           as the {@link OrderEntryType} corresponding to the reservation to be checked
	 * @return true if all the orderEntries for the specified orderEntryType are inactive or if quantity is equals to 0
	 */
	boolean isReservationCancelled(AbstractOrderModel abstractOrderModel, OrderEntryType orderEntryType);

	/**
	 * Check if the abstractOrderModel has at least one orderEntry with type equals to the given orderEntryType
	 *
	 * @param abstractOrderModel
	 *           as the abstractOrderModel to be checked
	 * @param orderEntryType
	 *           as the {@link OrderEntryType} to be checked
	 * @return true if there is at least one orderEntry with type equals to the given orderEntryType, false otherwise
	 */
	boolean checkIfAnyOrderEntryByType(AbstractOrderModel abstractOrderModel, OrderEntryType orderEntryType);

	/**
	 * Add an user request against a room stay booking
	 *
	 * @param request
	 *           the request
	 * @param roomStayRefNumber
	 *           the room stay ref number
	 * @param bookingReference
	 *           the booking reference
	 * @return true if the operation of adding the request is successfully performed, false otherwise
	 * @throws ModelSavingException
	 *            the model saving exception
	 * @throws RequestKeyGeneratorException
	 *            the request key generator exception
	 */
	void addRequestToRoomStayBooking(String request, int roomStayRefNumber, String bookingReference)
			throws ModelSavingException, RequestKeyGeneratorException;

	/**
	 * Remove an user request from a room stay booking
	 *
	 * @param requestCode
	 *           the request code
	 * @param roomStayRefNumber
	 *           the room stay ref number
	 * @param bookingReference
	 *           the booking reference
	 * @return true if the operation of removing the request is successfully performed, false otherwise
	 * @throws ModelNotFoundException
	 *            the model not found exception
	 * @throws ModelRemovalException
	 *            the model removal exception
	 */
	void removeRequestFromRoomStayBooking(String requestCode, int roomStayRefNumber, String bookingReference)
			throws ModelNotFoundException, ModelRemovalException;

	/**
	 * Retrieves a map of parameters needed to access accommodation details page (accommodation offering, check in date,
	 * check out date)
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return accommodation details parameters
	 */
	Map<String, String> getAccommodationDetailsParameters(AbstractOrderModel abstractOrderModel);

	/**
	 * Returns a list of AccommodationOrderEntryGroup reference numbers for the groups that have all entries in
	 * AmendStatus.NEW
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return new accommodation order entry group refs
	 */
	List<Integer> getAccommodationOrderEntryGroupRefs(AbstractOrderModel abstractOrderModel);

	/**
	 * Returns a list of AccommodationOrderEntryGroup reference numbers for the groups that have all entries in
	 * AmendStatus.NEW
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return new accommodation order entry group refs
	 */
	List<Integer> getNewAccommodationOrderEntryGroupRefs(AbstractOrderModel abstractOrderModel);

	/**
	 * Returns a list of AccommodationOrderEntryGroup reference numbers for the groups that have all entries in
	 * AmendStatus.SAME
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return new accommodation order entry group refs
	 */
	List<Integer> getOldAccommodationOrderEntryGroupRefs(AbstractOrderModel abstractOrderModel);

	/**
	 * Returns the orderTotal related to the given orderEntryType part of the order.
	 *
	 * @param abstractOrder
	 * @param orderEntryType
	 * @return the Double value of the totalPrice for the given orderEntryType
	 */
	Double getOrderTotalPriceByType(AbstractOrderModel abstractOrder, OrderEntryType orderEntryType);

	/**
	 * Proceeds with cancellation of a part of the order, creates a refund transaction if there is any refund available
	 *
	 * @param totalToRefund
	 * @param orderEntryType
	 * @return true if successfully created refund payment transaction
	 */
	boolean cancelPartialOrder(BigDecimal totalToRefund, OrderEntryType orderEntryType);

	/**
	 * Unlinks the booking from user account
	 *
	 * @param user
	 * @param order
	 * @return
	 */
	boolean unlinkBooking(UserModel user, OrderModel order);

	/**
	 * Returns the last active order for the specified OrderEntryType
	 *
	 * @param abstractOrderModel
	 *           as the abstractOrderModel
	 * @param orderEntryType
	 *           as the orderEntryType
	 *
	 * @return the last active OrderModel
	 */
	AbstractOrderModel getLastActiveOrderForType(AbstractOrderModel abstractOrderModel, OrderEntryType orderEntryType);

	/**
	 * Calculates the total already paid for a given order
	 *
	 * @param abstractOrder
	 *
	 * @return
	 */
	BigDecimal getOrderTotalPaid(AbstractOrderModel abstractOrder);

	/**
	 * Calculates the total already paid for products belonging to a given entry group within the given order
	 *
	 * @param abstractOrder
	 * @param entryGroup
	 * @return
	 */
	BigDecimal getOrderTotalPaidByEntryGroup(AbstractOrderModel abstractOrder, AbstractOrderEntryGroupModel entryGroup);


	/**
	 * Calculates the total already paid for products of the given orderEntryType and for the given order
	 *
	 * @param abstractOrder
	 * @param orderEntryType
	 * @return
	 */
	BigDecimal getOrderTotalPaidForOrderEntryType(AbstractOrderModel abstractOrder, OrderEntryType orderEntryType);

	/**
	 * Calculates the total to pay for change dates for the order in cart
	 *
	 * @return totalAmountToPay
	 */
	BigDecimal getOrderTotalToPayForChangeDates();

	/**
	 * This method will replace all the old RoomRateModel entries belonging to specific group transaction entries , with
	 * new Entries.
	 * 
	 * @param orderEntryGroup
	 * @param entries
	 * @return
	 */
	boolean linkEntriesToOldPaymentTransactions(AccommodationOrderEntryGroupModel orderEntryGroup,
			List<AbstractOrderEntryModel> entries);

	/**
	 * Retrieves abstractOrderEntry from given abstractOrderModel
	 *
	 * @param abstractOrderModel
	 *           - the abstract order model
	 * @param productCode
	 *           the productCode
	 * @param roomStayRefNumber
	 *           the room stay reference number
	 * @return AbstractOrderEntryModel order entry
	 */
	AbstractOrderEntryModel getOriginalOrderEntry(AbstractOrderModel abstractOrderModel, String productCode,
			int roomStayRefNumber);

	/**
	 * Checks if is abstract Order is of type.
	 * 
	 * @param abstractOrder
	 *           the order
	 * @param bookingType
	 *           the booking type
	 * @return true, if current cart is of type
	 */
	boolean isAbstractOrderOfType(AbstractOrderModel abstractOrder, String bookingType);

	/**
	 * Calculates the booking total amount for the orderEntryType in the abstract order
	 * 
	 * @param abstractOrder
	 *           the order
	 * @param orderEntryType
	 *           the orderEntryType
	 * @return totalAmount
	 */
	double getBookingTotalByOrderEntryType(AbstractOrderModel abstractOrder, OrderEntryType orderEntryType);

	/**
	 * Checks if the passenger reference is valid for the provided abstract order
	 *
	 * @param abstractOrder
	 *           the abstractOrder
	 * @param passengerReference
	 *           the passengerReference
	 * @return
	 */
	boolean isValidPassengerReference(AbstractOrderModel abstractOrder, String passengerReference);

	/**
	 * Calculate refund for cancelled traveller
	 *
	 * @param abstractOrder
	 * @return
	 */
	BigDecimal calculateTotalRefundForCancelledTraveller(AbstractOrderModel abstractOrder);

	/**
	 * returns an original order
	 *
	 * @param abstractOrder
	 * @return
	 */
	OrderModel getOriginalOrder(AbstractOrderModel abstractOrder);

}
