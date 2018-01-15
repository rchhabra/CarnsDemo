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
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.stock.StockResolvingStrategyByEntryType;
import de.hybris.platform.travelservices.strategies.stock.transport.impl.TransportStockResolvingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for TransportCartEntryValidationStrategy
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportCartEntryValidationStrategyTest
{
	@InjectMocks
	TransportCartEntryValidationStrategy strategy;

	@Mock
	private CartEntryModel cartEntryModel;
	@Mock
	private CartModel cartModel;
	@Mock
	private ProductModel product;
	@Mock
	private ProductService productService;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;
	@Mock
	private Map<OrderEntryType, StockResolvingStrategyByEntryType> entryTypeStockResolvingStrategyMap;
	@Mock
	private TransportStockResolvingStrategy transportStockResolvingStrategy;
	@Mock
	private PointOfServiceModel pointOfServiceModel;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private TravelCommerceStockService commerceStockService;
	@Mock
	private OrderModel order;
	@Mock
	private TravelOrderEntryInfoModel travelOrderEntryInfo;

	@Test
	public void testValidateWithEmptyStockLevel()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(null);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithUnknownIdentifierException()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenThrow(new UnknownIdentifierException("Product Not Found"));
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithStockLevelAndNewStatus()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(5L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.NEW);
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = new HashMap<>();
		matchingEntries.put(AmendStatus.NEW, Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.doNothing().when(modelService).save(cartEntryModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithProductNotAvailableInPOS()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(-5L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.NEW);
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = new HashMap<>();
		matchingEntries.put(AmendStatus.NEW, Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(2L);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).save(cartEntryModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithProductAvailableInPOS()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(-5L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.NEW);
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = new HashMap<>();
		matchingEntries.put(AmendStatus.NEW, Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(2L);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(null);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithAmendStatusChanged()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(5L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = new HashMap<>();
		matchingEntries.put(AmendStatus.CHANGED, Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartModel.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(2L);
		Mockito.when(cartEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfo);
		final TransportOfferingModel transportOffering = Mockito.mock(TransportOfferingModel.class);
		Mockito.when(travelOrderEntryInfo.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(null);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithDiffernetStockAndCartQuantity()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.CHANGED);
		final Map<AmendStatus, List<CartEntryModel>> matchingEntries = new HashMap<>();
		matchingEntries.put(AmendStatus.CHANGED, Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartModel.getOriginalOrder()).thenReturn(order);
		Mockito.when(order.getEntries()).thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(2L);
		Mockito.when(cartEntryModel.getTravelOrderEntryInfo()).thenReturn(travelOrderEntryInfo);
		final TransportOfferingModel transportOffering = Mockito.mock(TransportOfferingModel.class);
		Mockito.when(travelOrderEntryInfo.getTransportOfferings())
				.thenReturn(Stream.of(transportOffering).collect(Collectors.toList()));
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(null);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.doNothing().when(modelService).refresh(cartModel);
		Mockito.doNothing().when(modelService).save(cartEntryModel);
		assertNotNull(strategy.validate(cartEntryModel));
	}

	@Test
	public void testValidateWithDiffernetAmendStatusSame()
	{
		Mockito.when(cartEntryModel.getOrder()).thenReturn(cartModel);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.TRANSPORT);
		Mockito.when(entryTypeStockResolvingStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportStockResolvingStrategy);
		Mockito.when(transportStockResolvingStrategy.getStock(cartEntryModel)).thenReturn(1L);
		Mockito.when(cartEntryModel.getAmendStatus()).thenReturn(AmendStatus.SAME);
		assertNotNull(strategy.validate(cartEntryModel));
	}

}
