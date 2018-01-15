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

package de.hybris.platform.travelservices.order;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;

import java.math.BigDecimal;
import java.util.List;


/**
 * Extension of CartService which contains travel specific functionality
 */
public interface TravelCartService extends CartService
{

	/**
	 * Creates a cart from order which will contain a reference to cloned order by originalOrderCode attribute
	 *
	 * @param orderCode
	 *           original order
	 * @param guid
	 *           the guid
	 * @return newly created Cart
	 */
	CartModel createCartFromOrder(String orderCode, String guid);

	/**
	 * Creates cart from order and removes all entries related to traveller
	 *
	 * @param orderCode
	 *           the order code
	 * @param cancelledTravellerCode
	 *           the cancelled traveller code
	 * @param cancelledTravellerUid
	 *           the cancelled traveller uid
	 * @param guid
	 *           the guid
	 * @return Cart Model without entries related to given traveller
	 */
	CartModel cancelTraveller(String orderCode, String cancelledTravellerCode, String cancelledTravellerUid, String guid);

	/**
	 * Removes delivery address from the cart
	 */
	void removeDeliveryAddress();

	/**
	 * Returns the available stock for Product in TransportOffering.
	 *
	 * @param productModel
	 *           the product model
	 * @param transportOfferingModel
	 *           the transport offering model
	 * @return Long, quantity available for the product.
	 */
	Long getAvailableStock(ProductModel productModel, TransportOfferingModel transportOfferingModel);

	/**
	 * Returns the list of AbstractOrderEntryModel for fare product from the entries of abstractOrderModel
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return list AbstractOrderEntryModel
	 */
	List<AbstractOrderEntryModel> getFareProductEntries(final AbstractOrderModel abstractOrderModel);

	/**
	 * Retrieves current destination transport facility from items that are in the cart
	 *
	 * @return current destination
	 */
	String getCurrentDestination();

	/**
	 * Returns a list of payment options, in which every option is represented by a list of objects storing a subset of
	 * order entries, with the relative price to pay and the associated payment conditions
	 *
	 * @return payment options
	 */
	List<PaymentOptionInfo> getPaymentOptions();

	/**
	 * Returns a list of payment options, in which every option is represented by a list of objects storing a subset of
	 * order entries, with the relative price to pay and the associated payment conditions
	 *
	 * @param orderEntryType
	 *
	 * @return payment options
	 */
	List<PaymentOptionInfo> getPaymentOptions(OrderEntryType orderEntryType);

	/**
	 * Deletes current cart model
	 */
	void deleteCurrentCart();

	/**
	 * Creates cart from order and removes all entries of type orderEntryType
	 *
	 * @param bookingReference
	 * @param orderEntryType
	 * @param guid
	 * @return Cart Model without entries of type orderEntryType
	 */
	CartModel cancelPartialOrder(String bookingReference, OrderEntryType orderEntryType, String guid);

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
	 * @param forcedBundleNumber
	 */
	void updateBundleEntriesWithBundleNumber(List<Integer> entryNumbers, Integer forcedBundleNumber);

	/**
	 * Validates cart to check if it transport details matches currently selected options. If not, the cart is cleared.
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
	 * Sets the additional security active on the session cart
	 *
	 * @param additionalSecurity
	 *           the additionalSecurity value
	 */
	void setAdditionalSecurity(Boolean additionalSecurity);

	/**
	 * calculates total by entries
	 *
	 * @param order
	 * @param entries
	 * @return
	 */
	BigDecimal getTransportTotalByEntries(AbstractOrderModel order, List<AbstractOrderEntryModel> entries);
}
