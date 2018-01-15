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

package de.hybris.platform.travelrulesengine.action.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelrulesengine.constants.TravelrulesengineConstants;
import de.hybris.platform.travelrulesengine.rao.FeeRAO;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Action strategy class for admin fee
 */
public class DefaultAdminFeeActionStrategy extends AbstractTravelRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultAdminFeeActionStrategy.class);
	private ProductService productService;
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private CommercePriceService commercePriceService;
	private ModelService modelService;

	@Override
	public List<? extends ItemModel> apply(final AbstractRuleActionRAO actionRao)
	{
		if (!(actionRao instanceof FeeRAO))
		{
			LOG.debug(String.format("cannot apply %s, action is not of type FeeRAO", this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final FeeRAO feeRao = (FeeRAO) actionRao;
		final OrderEntryRAO addedOrderEntryRao = feeRao.getAddedOrderEntry();
		if (addedOrderEntryRao != null)
		{
			final CommerceCartModification modification = addAdminFeeToCart(addedOrderEntryRao);
			return modification != null ?
					Collections.singletonList((CartEntryModel) modification.getEntry()) :
					Collections.emptyList();
		}
		else
		{
			final ProductModel product = addAdminFee(feeRao);
			return Arrays.asList(product);
		}
	}

	protected ProductModel addAdminFee(final FeeRAO feeRao)
	{
		final ProductModel product = getAdminFeeProduct();
		final PriceInformation priceInfo = getCommercePriceService().getWebPriceForProduct(product);
		final double priceValue = (priceInfo != null) ? priceInfo.getPriceValue().getValue() : 0;
		feeRao.setPrice(BigDecimal.valueOf(priceValue));
		return product;
	}

	protected CommerceCartModification addAdminFeeToCart(final OrderEntryRAO addedOrderEntryRao)
	{
		final ProductModel product = getAdminFeeProduct();
		final CartModel cartModel = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setQuantity(1);
		parameter.setProduct(product);
		parameter.setUnit(product.getUnit());
		parameter.setCreateNewEntry(true);

		CommerceCartModification modification = null;
		try
		{
			modification = getCommerceCartService().addToCart(parameter);
			final CartEntryModel entry = (CartEntryModel) modification.getEntry();
			entry.setType(OrderEntryType.TRANSPORT);
			getModelService().save(entry);
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("Error while adding admin product to cart:" + e.getMessage(), e);
		}
		return modification;
	}

	protected ProductModel getAdminFeeProduct()
	{
		ProductModel product = null;
		try
		{
			product = getProductService().getProductForCode(TravelrulesengineConstants.ADMIN_FEE_PRODUCT_CODE);
		}
		catch (IllegalArgumentException | UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.debug("Admin product not found", e);
		}
		return product;
	}

	@Override
	public void undo(final ItemModel var1)
	{
		// DO NOTHING
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the commerceCartService
	 */
	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService the commerceCartService to set
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	/**
	 * @return the commercePriceService
	 */
	protected CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	/**
	 * @param commercePriceService the commercePriceService to set
	 */
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
