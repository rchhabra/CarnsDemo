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

package de.hybris.platform.travelservices.strategies.cart.validation.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.strategies.cart.validation.CartEntryValidationStrategyByEntryType;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;



/**
 * The strategy provides customized method with context of cart validation for travel.
 */
public class DefaultTravelCartValidationStrategy extends DefaultCartValidationStrategy
{
	private Map<OrderEntryType, CartEntryValidationStrategyByEntryType> cartEntryValidationStrategyByEntryTypeMap;

	@Override
	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final CartEntryModel cartEntryModel)
	{
		final CartEntryValidationStrategyByEntryType strategy = getCartEntryValidationStrategyByEntryTypeMap()
				.get(cartEntryModel.getType());
		if (Objects.nonNull(strategy))
		{
			return strategy.validate(cartEntryModel);
		}
		return super.validateCartEntry(cartModel, cartEntryModel);
	}

	/**
	 * Gets cart entry validation strategy by entry type map.
	 *
	 * @return the cart entry validation strategy by entry type map
	 */
	protected Map<OrderEntryType, CartEntryValidationStrategyByEntryType> getCartEntryValidationStrategyByEntryTypeMap()
	{
		return cartEntryValidationStrategyByEntryTypeMap;
	}

	/**
	 * Sets cart entry validation strategy by entry type map.
	 *
	 * @param cartEntryValidationStrategyByEntryTypeMap
	 *           the cart entry validation strategy by entry type map
	 */
	@Required
	public void setCartEntryValidationStrategyByEntryTypeMap(
			final Map<OrderEntryType, CartEntryValidationStrategyByEntryType> cartEntryValidationStrategyByEntryTypeMap)
	{
		this.cartEntryValidationStrategyByEntryTypeMap = cartEntryValidationStrategyByEntryTypeMap;
	}

	@Override
	protected void validateDelivery(final CartModel cartModel)
	{
		final CustomerModel currentUser = (CustomerModel) cartModel.getUser();
		if (currentUser instanceof B2BCustomerModel && CheckoutPaymentType.ACCOUNT.equals(cartModel.getPaymentType()))
		{
			return;
		}
		if ((cartModel.getDeliveryAddress() != null) && !isGuestUserCart(cartModel)
				&& !getUserService().getCurrentUser().equals(cartModel.getDeliveryAddress().getOwner()))
		{
			cartModel.setDeliveryAddress(null);
			getModelService().save(cartModel);
		}
	}

}
