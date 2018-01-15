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

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.strategies.stock.StockResolvingStrategyByEntryType;
import de.hybris.platform.travelservices.strategies.stock.accommodation.impl.AccommodationStockResolvingStrategy;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for AccommodationCartEntryValidationStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationCartEntryValidationStrategyTest
{
	@InjectMocks
	private AccommodationCartEntryValidationStrategy strategy;

	@Mock
	private CartEntryModel cartEntryModel;
	@Mock
	private CartModel cartModel;
	@Mock
	private RoomRateProductModel product;
	@Mock
	private ProductService productService;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;

	@Mock
	private AccommodationCommerceCartService accommodationCommerceCartService;

	@Mock
	private Map<OrderEntryType, StockResolvingStrategyByEntryType> entryTypeStockResolvingStrategyMap;
	@Mock
	private AccommodationStockResolvingStrategy accommodationStockResolvingStrategy;
	@Mock
	private OrderModel order;

	@Test
	public void testValidateWithEmptyStockLevel()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(null);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithUnknownIdentifierException()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenThrow(new UnknownIdentifierException("Product Not Found"));
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithAmendStatusSame()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithAmendStatusNew()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.NEW);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito
				.when(accommodationCommerceCartService.getEntriesForProductAndAccommodation(Matchers.any(CartModel.class),
						Matchers.any(ProductModel.class), Matchers.any(CartEntryModel.class)))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithAmendStatusNewForNonRoomRateModelProduct()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		final ProductModel product = new ProductModel();
		product.setCode("product1");
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.NEW);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito
				.when(accommodationCommerceCartService.getEntriesForProductAndAccommodation(Matchers.any(CartModel.class),
						Matchers.any(ProductModel.class), Matchers.any(CartEntryModel.class)))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithAmendStatusChanged()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartModel.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito
				.when(accommodationCommerceCartService.getEntriesForProductAndAccommodation(Matchers.any(CartModel.class),
						Matchers.any(ProductModel.class), Matchers.any(CartEntryModel.class)))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithAmendStatusChangedForNonRoomRateProduct()
	{
		final ProductModel product = new ProductModel();
		product.setCode("product1");

		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);

		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartModel.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito
				.when(accommodationCommerceCartService.getEntriesForProductAndAccommodation(Matchers.any(CartModel.class),
						Matchers.any(ProductModel.class), Matchers.any(CartEntryModel.class)))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithDifferentProduct()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		final FareProductModel differentProduct = new FareProductModel();
		Mockito.when(cartEntryModel.getProduct()).thenReturn(differentProduct);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(differentProduct);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationStockResolvingStrategy);
		Mockito.when(accommodationStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		Mockito.when(cartService.getEntriesForProduct(cartModel, differentProduct))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartModel.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		assertNotNull(strategy.validate(cartEntryModel));
	}

}
