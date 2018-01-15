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

package de.hybris.platform.travelservices.strategies.cart.validation.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.strategies.cart.validation.AbstractCartEntryValidationStrategy;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * The type Transport cart entry validation strategy.
 */
public class TransportCartEntryValidationStrategy extends AbstractCartEntryValidationStrategy
{
	private static final Logger LOG = Logger.getLogger(TransportCartEntryValidationStrategy.class);

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
		final long cartEntryLevel = cartEntryModel.getQuantity();

		if (stockLevel == null)
		{
			return createModification(CommerceCartModificationStatus.SUCCESS, cartEntryLevel, cartEntryLevel, cartEntryModel);
		}

		final long cartLevel = getCartLevel(cartEntryModel, cartModel);


		if (AmendStatus.SAME.equals(cartEntryModel.getAmendStatus()))
		{
			return createModification(CommerceCartModificationStatus.SUCCESS, cartEntryLevel, cartEntryLevel, cartEntryModel);
		}

		Long newOrderEntryLevel;
		Long stockLevelForProductInBaseStore = null;

		if (isProductNotAvailableInPOS(cartEntryModel, stockLevel))
		{
			stockLevelForProductInBaseStore = getCommerceStockService()
					.getStockLevelForProductAndBaseStore(cartEntryModel.getProduct(), getBaseStoreService().getCurrentBaseStore());

			if (stockLevelForProductInBaseStore != null)
			{
				newOrderEntryLevel = Math.min(cartEntryLevel, stockLevelForProductInBaseStore.longValue());
			}
			else
			{
				newOrderEntryLevel = Math.min(cartEntryLevel, cartLevel);
			}
		}
		else
		{
			newOrderEntryLevel = Math.min(cartEntryLevel, stockLevel.longValue());
		}

		if (stockLevelForProductInBaseStore != null && stockLevelForProductInBaseStore.longValue() != 0)
		{
			final CartEntryModel existingEntryForProduct = getExistingShipCartEntryForProduct(cartModel,
					cartEntryModel.getProduct());
			CommerceCartModification modification;
			if (existingEntryForProduct != null)
			{
				getModelService().remove(cartEntryModel);
				final long quantityAdded = stockLevelForProductInBaseStore.longValue() >= cartLevel ? newOrderEntryLevel
						: cartLevel - stockLevelForProductInBaseStore.longValue();
				final long updatedQuantity = (stockLevelForProductInBaseStore.longValue() <= cartLevel
						? stockLevelForProductInBaseStore.longValue() : cartLevel);
				existingEntryForProduct.setQuantity(Long.valueOf(updatedQuantity));
				getModelService().save(existingEntryForProduct);
				modification = createModification(CommerceCartModificationStatus.MOVED_FROM_POS_TO_STORE, quantityAdded,
						updatedQuantity, existingEntryForProduct);
			}
			else
			{
				cartEntryModel.setDeliveryPointOfService(null);
				modification = createModification(CommerceCartModificationStatus.MOVED_FROM_POS_TO_STORE, newOrderEntryLevel,
						cartEntryLevel, cartEntryModel);
				getModelService().save(cartEntryModel);
			}

			getModelService().refresh(cartModel);
			return modification;
		}
		else if (stockLevel.longValue() <= 0 || newOrderEntryLevel < 0)
		{
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(cartEntryModel.getProduct());
			final CommerceCartModification modification = createModification(CommerceCartModificationStatus.NO_STOCK, 0,
					cartEntryLevel, entry);
			getModelService().remove(cartEntryModel);
			getModelService().refresh(cartModel);
			return modification;
		}
		else if (cartEntryLevel != newOrderEntryLevel)
		{
			final CommerceCartModification modification = createModification(CommerceCartModificationStatus.LOW_STOCK,
					newOrderEntryLevel, cartEntryLevel, cartEntryModel);
			cartEntryModel.setQuantity(Long.valueOf(newOrderEntryLevel));
			getModelService().save(cartEntryModel);
			getModelService().refresh(cartModel);
			return modification;
		}
		else
		{
			return createModification(CommerceCartModificationStatus.SUCCESS, cartEntryLevel, cartEntryLevel, cartEntryModel);
		}
	}

	@Override
	protected long getCartLevel(final CartEntryModel cartEntryModel, final CartModel cartModel)
	{
		Long cartLevel = 0L;
		if (AmendStatus.SAME.equals(cartEntryModel.getAmendStatus()))
		{
			return cartLevel;
		}
		final ProductModel product = cartEntryModel.getProduct();
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = getCartService().getEntriesForProduct(cartModel, product)
				.stream().collect(Collectors.groupingBy(CartEntryModel::getAmendStatus));
		final List<CartEntryModel> newEntries = matchingEntries.get(AmendStatus.NEW);
		if (CollectionUtils.isNotEmpty(newEntries))
		{
			cartLevel = Long.sum(cartLevel, newEntries.stream().mapToLong(CartEntryModel::getQuantity).sum());
		}
		final List<CartEntryModel> changedEntries = matchingEntries.get(AmendStatus.CHANGED);
		if (CollectionUtils.isNotEmpty(changedEntries))
		{
			cartLevel = cartLevel - changedEntries.stream().map(entry -> getOriginalOrderEntry(entry, cartModel.getOriginalOrder()))
					.filter(Objects::nonNull).mapToLong(AbstractOrderEntryModel::getQuantity).sum();
		}
		return cartLevel;
	}

	/**
	 * Gets original order entry.
	 *
	 * @param entry
	 * 		the entry
	 * @param originalOrder
	 * 		the original order
	 *
	 * @return the original order entry
	 */
	protected AbstractOrderEntryModel getOriginalOrderEntry(final CartEntryModel entry, final OrderModel originalOrder)
	{
		final Optional<AbstractOrderEntryModel> abstractOrderEntryModel = originalOrder.getEntries().stream()
				.filter(originalEntry -> Objects.equals(OrderEntryType.TRANSPORT, originalEntry.getType())
						&& !(Objects.equals(ProductType.FEE, originalEntry.getProduct().getProductType())
								|| originalEntry.getProduct() instanceof FeeProductModel)
						&& originalEntry.getTravelOrderEntryInfo().getTransportOfferings()
								.containsAll(entry.getTravelOrderEntryInfo().getTransportOfferings())
						&& originalEntry.getProduct().equals(entry.getProduct()))
				.findFirst();
		return abstractOrderEntryModel.orElse(null);
	}


}
