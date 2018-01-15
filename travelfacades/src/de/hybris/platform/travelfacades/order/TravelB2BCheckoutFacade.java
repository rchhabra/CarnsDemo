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
package de.hybris.platform.travelfacades.order;

import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;

import java.util.List;


/**
 * Exposing B2B checkout API for Travel.
 */
public interface TravelB2BCheckoutFacade extends CheckoutFacade
{
	/**
	 * Authorize payment boolean.
	 *
	 * @param securityCode
	 *           the security code
	 * @return the boolean
	 */
	boolean authorizePayment(String securityCode);


	/**
	 * Get the list of supported delivery addresses.
	 *
	 * @param visibleAddressesOnly
	 *           include only the visible addresses
	 * @return the supported delivery addresses
	 */
	List<? extends AddressData> getSupportedDeliveryAddresses(boolean visibleAddressesOnly);

	/**
	 * Set the delivery address on the cart.
	 *
	 * @param address
	 *           the address, If null the delivery address is removed from the session cart.
	 * @return true if operation succeeded
	 */
	boolean setDeliveryAddress(AddressData address);
}