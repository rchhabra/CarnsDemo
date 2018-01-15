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

package de.hybris.platform.travelfacades.facades.packages.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.facades.packages.DealBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.packages.DealCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.packages.strategies.AddBundleToCartByTypeStrategy;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Class responsible for handling all the operations related to add a deal to the cart
 */
public class DefaultDealCartFacade implements DealCartFacade
{
	private static final String DEFAULT = "DEFAULT";

	private static final Logger LOG = Logger.getLogger(DefaultDealCartFacade.class);

	private Map<String, AddBundleToCartByTypeStrategy> addBundleByTypeStrategyMap;
	private CommerceCartService commerceCartService;
	private CartService cartService;
	private DealBundleTemplateFacade dealBundleTemplateFacade;
	private TravelCartFacade cartFacade;


	@Override
	public List<CartModificationData> addDealToCart(final AddDealToCartData addDealToCartData)
	{
		getCartFacade().deleteCurrentCart();
		final List<CartModificationData> cartModifications = new ArrayList<>();
		final DealBundleTemplateModel masterBundleTemplate = getDealBundleTemplateFacade()
				.getDealBundleTemplateById(addDealToCartData.getDealBundleId());

		if(!checkAndUpdateDealDates(addDealToCartData, masterBundleTemplate))
		{
			cleanSessionCart();
			return Collections.singletonList(createCartModificationData(CommerceCartModificationStatus.UNAVAILABLE, 0));
		}

		try
		{
			for (final BundleTemplateModel childTemplate : masterBundleTemplate.getChildTemplates())
			{
				final String key = childTemplate.getClass().getSimpleName();
				final AddBundleToCartByTypeStrategy strategy = Objects.nonNull(getAddBundleByTypeStrategyMap().get(key))
						? getAddBundleByTypeStrategyMap().get(key) : getAddBundleByTypeStrategyMap().get(DEFAULT);
				cartModifications.addAll(strategy.addBundleToCart(childTemplate, addDealToCartData));
			}
			// Add fees and discounts
			getCartFacade().evaluateCart();
		}
		catch (final CommerceCartModificationException modEx)
		{
			LOG.error("Problem occured while adding deal bundle to the cart", modEx);
			cleanSessionCart();
			return Collections.singletonList(createCartModificationData(CommerceCartModificationStatus.UNAVAILABLE, 0));
		}

		return cartModifications;
	}

	@Override
	public boolean isDealInCart()
	{
		return getCartService().hasSessionCart() ?
				getDealBundleTemplateFacade().isDealAbstractOrder(getCartService().getSessionCart()) :
				Boolean.FALSE;
	}

	protected boolean checkAndUpdateDealDates(final AddDealToCartData addDealToCartData,
			final DealBundleTemplateModel masterBundleTemplate)
	{
		if(Objects.isNull(masterBundleTemplate))
		{
			return false;
		}

		final Date startingDate = addDealToCartData.getStartingDate();
		final Date endingDate = TravelDateUtils.addDays(startingDate, masterBundleTemplate.getLength());
		addDealToCartData.setEndingDate(endingDate);

		final List<Date> validDates = TravelDateUtils
				.getValidDates(masterBundleTemplate.getStartingDatePattern(), TravelDateUtils.addDays(startingDate, -1), endingDate);
		if (CollectionUtils.isEmpty(validDates) || !validDates.contains(addDealToCartData.getStartingDate()))
		{
			LOG.error(
					"Problem occurred while adding deal bundle to the cart: the selected startingDate doesn't match the deal "
							+ "startingDatePattern");
			return false;
		}
		return true;
	}

	protected CartModificationData createCartModificationData(final String status, final long quantity)
	{
		final CartModificationData cartModificationData = new CartModificationData();
		cartModificationData.setQuantityAdded(quantity);
		cartModificationData.setStatusCode(status);
		return cartModificationData;
	}


	protected void cleanSessionCart()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(getCartService().getSessionCart());
		getCommerceCartService().removeAllEntries(parameter);
	}

	/**
	 * @return the dealBundleTemplateFacade
	 */
	protected DealBundleTemplateFacade getDealBundleTemplateFacade()
	{
		return dealBundleTemplateFacade;
	}

	/**
	 * @param dealBundleTemplateFacade
	 * 		the dealBundleTemplateFacade to set
	 */
	@Required
	public void setDealBundleTemplateFacade(
			final DealBundleTemplateFacade dealBundleTemplateFacade)
	{
		this.dealBundleTemplateFacade = dealBundleTemplateFacade;
	}

	/**
	 *
	 * @return addBundleByTypeStrategyMap
	 */
	protected Map<String, AddBundleToCartByTypeStrategy> getAddBundleByTypeStrategyMap()
	{
		return addBundleByTypeStrategyMap;
	}

	/**
	 *
	 * @param addBundleByTypeStrategyMap
	 *           the addBundleByTypeStrategyMap to set
	 */
	@Required
	public void setAddBundleByTypeStrategyMap(final Map<String, AddBundleToCartByTypeStrategy> addBundleByTypeStrategyMap)
	{
		this.addBundleByTypeStrategyMap = addBundleByTypeStrategyMap;
	}

	/**
	 *
	 * @return commerceCartService
	 */
	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 *
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	/**
	 *
	 * @return cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 *
	 * @param cartService
	 *           the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the cartFacade
	 */
	protected TravelCartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * @param cartFacade
	 *           the cartFacade to set
	 */
	@Required
	public void setCartFacade(final TravelCartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}
}
