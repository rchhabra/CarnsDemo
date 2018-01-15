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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Strategy that extends the {@link AmendOrderValidationStrategy}.
 * The strategy is used to validate the addToOrder of a product based on its availability.
 */
public class ProductAvailabilityOrderValidationStrategy implements AmendOrderValidationStrategy
{
	private ProductService productService;
	private TransportOfferingService transportOfferingService;
	private TravelCommerceStockService travelCommerceStockService;
	private BookingService bookingService;

	@Override
	public boolean validateAmendOrder(final OrderModel order, final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		return qty <= 0 || isProductAvailable(productCode, transportOfferingCodes, qty, order);
	}

	protected boolean isProductAvailable(final String productCode, final List<String> transportOfferingCodes, final Long quantity,
			final OrderModel order)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final List<TransportOfferingModel> transportOfferingModels = transportOfferingCodes.stream()
				.map(transportOfferingCode -> getTransportOfferingService().getTransportOffering(transportOfferingCode))
				.collect(Collectors.toList());

		final Long quantityToOffer = getQuantityToOffer(productModel, transportOfferingModels, quantity, order);

		return transportOfferingModels.stream().allMatch(transportOfferingModel ->
		{
			final Long availableStock = getAvailableStock(productModel, transportOfferingModel, order);
			return availableStock == null || availableStock >= quantityToOffer;
		});

	}

	protected Long getAvailableStock(final ProductModel productModel, final TransportOfferingModel transportOfferingModel,
			final OrderModel order)
	{
		final Long quantityInStock = getTravelCommerceStockService().getStockLevel(productModel,
				Stream.of(transportOfferingModel).collect(Collectors.toList()));
		if (Objects.isNull(quantityInStock))
		{
			return null;
		}
		if (Objects.isNull(order.getOriginalOrder()))
		{
			return quantityInStock;
		}
		final Long quantityInOrder = getBookingService().getProductQuantityInOrderForTransportOffering(
				order.getOriginalOrder().getCode(), productModel, transportOfferingModel);
		return quantityInStock + quantityInOrder;
	}

	/**
	 * This method calculates the quantity of a product in offer to compare against the available stock.
	 *
	 * @param productModel            the product model
	 * @param transportOfferingModels the transport offering models
	 * @param quantity                the quantity
	 * @param order
	 * @return a Long
	 */
	protected Long getQuantityToOffer(final ProductModel productModel, final List<TransportOfferingModel> transportOfferingModels,
			final Long quantity, final OrderModel order)
	{
		final List<AbstractOrderEntryModel> orderEntries = order.getEntries().stream().filter(abstractOrderEntryModel ->
				productModel.getPk().equals(abstractOrderEntryModel.getProduct().getPk())).collect(Collectors.toList());
		return quantity + orderEntries.stream().filter(orderEntryModel -> transportOfferingModels
				.containsAll(orderEntryModel.getTravelOrderEntryInfo().getTransportOfferings()))
				.mapToLong(AbstractOrderEntryModel::getQuantity).sum();
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	protected TravelCommerceStockService getTravelCommerceStockService()
	{
		return travelCommerceStockService;
	}

	@Required
	public void setTravelCommerceStockService(final TravelCommerceStockService travelCommerceStockService)
	{
		this.travelCommerceStockService = travelCommerceStockService;
	}

	protected BookingService getBookingService()
	{
		return bookingService;
	}

	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
