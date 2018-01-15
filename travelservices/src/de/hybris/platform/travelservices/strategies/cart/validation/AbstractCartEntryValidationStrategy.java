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

package de.hybris.platform.travelservices.strategies.cart.validation;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.strategies.stock.StockResolvingStrategyByEntryType;

import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract cart entry validation strategy to validate cart entries
 */
public abstract class AbstractCartEntryValidationStrategy implements CartEntryValidationStrategyByEntryType
{
	private static final Logger LOG = Logger.getLogger(AbstractCartEntryValidationStrategy.class);

	private CommerceStockService commerceStockService;
	private BaseStoreService baseStoreService;
	private ModelService modelService;
	private CartService cartService;
	private ProductService productService;
	private Map<OrderEntryType, StockResolvingStrategyByEntryType> entryTypeStockResolvingStrategyMap;

	protected Long getStockLevel(final CartEntryModel cartEntryModel)
	{
		final StockResolvingStrategyByEntryType strategy = getEntryTypeStockResolvingStrategyMap().get(cartEntryModel.getType());
		return (Objects.nonNull(strategy)) ? strategy.getStock(cartEntryModel)
				: getEntryTypeStockResolvingStrategyMap().get(OrderEntryType.DEFAULT).getStock(cartEntryModel);
	}

	protected CommerceCartModification createModification(final String status, final long quantityAdded, final long quantity,
			final AbstractOrderEntryModel entry)
	{
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setStatusCode(status);
		modification.setQuantityAdded(quantityAdded);
		modification.setQuantity(quantity);
		modification.setEntry(entry);
		return modification;
	}

	protected abstract long getCartLevel(final CartEntryModel cartEntryModel, final CartModel cartModel);

	protected CartEntryModel getExistingShipCartEntryForProduct(final CartModel cartModel, final ProductModel product)
	{
		for (final CartEntryModel entryModel : getCartService().getEntriesForProduct(cartModel, product))
		{
			if (entryModel.getDeliveryPointOfService() == null)
			{
				return entryModel;
			}
		}
		return null;
	}

	protected boolean isProductNotAvailableInPOS(final CartEntryModel cartEntryModel, final Long stockLevel)
	{
		return stockLevel.longValue() <= 0 && hasPointOfService(cartEntryModel);
	}


	protected boolean hasPointOfService(final CartEntryModel cartEntryModel)
	{
		return cartEntryModel.getDeliveryPointOfService() != null;
	}

	@Override
	public CommerceCartModification validate(final CartEntryModel cartEntryModel)
	{
		final CartModel cartModel = cartEntryModel.getOrder();
		try
		{
			getProductService().getProductForCode(cartEntryModel.getProduct().getCode());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.debug("Product not found", e);
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(cartEntryModel.getProduct());
			final CommerceCartModification modification = createModification(CommerceCartModificationStatus.UNAVAILABLE, 0, 0,
					entry);
			getModelService().remove(cartEntryModel);
			getModelService().refresh(cartModel);
			return modification;
		}

		final Long stockLevel = getStockLevel(cartEntryModel);

		final long cartLevel = getCartLevel(cartEntryModel, cartModel);

		if (AmendStatus.SAME.equals(cartEntryModel.getAmendStatus()))
		{
			return createModification(CommerceCartModificationStatus.SUCCESS, cartEntryModel.getQuantity(),
					cartEntryModel.getQuantity(), cartEntryModel);
		}

		if (stockLevel == null || stockLevel.longValue() <= 0 || cartLevel > stockLevel.longValue())
		{
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(cartEntryModel.getProduct());
			final CommerceCartModification modification = createModification(CommerceCartModificationStatus.NO_STOCK, 0,
					cartEntryModel.getQuantity(), entry);
			getModelService().remove(cartEntryModel);
			getModelService().refresh(cartModel);
			return modification;
		}
		else
		{
			return createModification(CommerceCartModificationStatus.SUCCESS, cartEntryModel.getQuantity(),
					cartEntryModel.getQuantity(), cartEntryModel);
		}

	}


	/**
	 *
	 * @return commerceStockService
	 */
	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 *
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	@Required
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 *
	 * @return baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 *
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 *
	 * @return modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 *
	 * @return entryTypeStockResolvingStrategyMap
	 */
	protected Map<OrderEntryType, StockResolvingStrategyByEntryType> getEntryTypeStockResolvingStrategyMap()
	{
		return entryTypeStockResolvingStrategyMap;
	}

	/**
	 *
	 * @param entryTypeStockResolvingStrategyMap
	 *           the entryTypeStockResolvingStrategyMap to set
	 */
	@Required
	public void setEntryTypeStockResolvingStrategyMap(
			final Map<OrderEntryType, StockResolvingStrategyByEntryType> entryTypeStockResolvingStrategyMap)
	{
		this.entryTypeStockResolvingStrategyMap = entryTypeStockResolvingStrategyMap;
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
	 *
	 * @return productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 *
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}



}
