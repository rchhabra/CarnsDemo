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
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.cart.validation.CartEntryValidationStrategyByEntryType;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCartValidationStrategyTest
{
	@InjectMocks
	DefaultTravelCartValidationStrategy strategy;

	@Mock
	private CartEntryModel cartEntryModel;
	@Mock
	private CartModel cartModel;
	@Mock
	private TransportCartEntryValidationStrategy transportCartEntryValidationStrategy;
	@Mock
	private AccommodationCartEntryValidationStrategy accommodationCartEntryValidationStrategy;
	@Mock
	private Map<OrderEntryType, CartEntryValidationStrategyByEntryType> cartEntryValidationStrategyByEntryTypeMap;
	@Mock
	private CommerceCartModification modification;
	@Mock
	private RoomRateProductModel product;
	@Mock
	private ProductService productService;
	@Mock
	private PointOfServiceModel pointOfServiceModel;
	@Mock
	private TravelCommerceStockService commerceStockService;
	@Mock
	private CartService cartService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private ModelService modelService;
	@Mock
	private AbstractOrderEntryProductInfoModel productInfos;
	@Mock
	private CustomerModel currentUser;
	@Mock
	private AddressModel address;
	@Mock
	private UserService userService;

	@Test
	public void testValidate()
	{
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(cartEntryValidationStrategyByEntryTypeMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(accommodationCartEntryValidationStrategy);
		Mockito.when(accommodationCartEntryValidationStrategy.validate(cartEntryModel)).thenReturn(modification);
		assertNotNull(strategy.validateCartEntry(cartModel, cartEntryModel));
	}

	@Test
	public void testValidateWithNullCartValidationStrategy()
	{
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(cartEntryValidationStrategyByEntryTypeMap.get(OrderEntryType.ACCOMMODATION))
				.thenReturn(null);
		Mockito.when(accommodationCartEntryValidationStrategy.validate(cartEntryModel)).thenReturn(modification);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(product, pointOfServiceModel)).thenReturn(10L);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(1L);
		assertNotNull(strategy.validateCartEntry(cartModel, cartEntryModel));
	}

	@Test
	public void testValidateWithProductNotAvailableInPOS()
	{
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(cartEntryValidationStrategyByEntryTypeMap.get(OrderEntryType.ACCOMMODATION)).thenReturn(null);
		Mockito.when(accommodationCartEntryValidationStrategy.validate(cartEntryModel)).thenReturn(modification);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(product, pointOfServiceModel)).thenReturn(-10L);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(null);
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validateCartEntry(cartModel, cartEntryModel));
	}

	@Test
	public void testValidateWithProductNotAvailableInPOSAndNonEmptyStockInBaseStore()
	{
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(cartEntryValidationStrategyByEntryTypeMap.get(OrderEntryType.ACCOMMODATION)).thenReturn(null);
		Mockito.when(accommodationCartEntryValidationStrategy.validate(cartEntryModel)).thenReturn(modification);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(product, pointOfServiceModel)).thenReturn(10L);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(5L);
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).save(cartEntryModel);
		assertNotNull(strategy.validateCartEntry(cartModel, cartEntryModel));
	}

	@Test
	public void testValidateWithProductNotAvailableInPOSAndConfigurationErrors()
	{
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(cartEntryValidationStrategyByEntryTypeMap.get(OrderEntryType.ACCOMMODATION)).thenReturn(null);
		Mockito.when(accommodationCartEntryValidationStrategy.validate(cartEntryModel)).thenReturn(modification);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(product, pointOfServiceModel)).thenReturn(10L);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(1L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(null);
		Mockito.doNothing().when(modelService).remove(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		Mockito.when(cartEntryModel.getProductInfos()).thenReturn(Stream.of(productInfos).collect(Collectors.toList()));
		Mockito.when(productInfos.getProductInfoStatus()).thenReturn(ProductInfoStatus.ERROR);
		assertNotNull(strategy.validateCartEntry(cartModel, cartEntryModel));
	}

	@Test
	public void testValidateWithDifferentStockAndQuantity()
	{
		Mockito.when(cartEntryModel.getType()).thenReturn(OrderEntryType.ACCOMMODATION);
		Mockito.when(cartEntryValidationStrategyByEntryTypeMap.get(OrderEntryType.ACCOMMODATION)).thenReturn(null);
		Mockito.when(accommodationCartEntryValidationStrategy.validate(cartEntryModel)).thenReturn(modification);
		Mockito.when(cartEntryModel.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn("product1");
		Mockito.when(productService.getProductForCode("product1")).thenReturn(product);
		Mockito.when(cartEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(product, pointOfServiceModel)).thenReturn(10L);
		Mockito.when(cartEntryModel.getQuantity()).thenReturn(15L);
		Mockito.when(cartService.getEntriesForProduct(cartModel, product))
				.thenReturn(Stream.of(cartEntryModel).collect(Collectors.toList()));
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore)).thenReturn(null);
		Mockito.doNothing().when(modelService).save(cartEntryModel);
		Mockito.doNothing().when(modelService).refresh(cartModel);
		assertNotNull(strategy.validateCartEntry(cartModel, cartEntryModel));
	}

	@Test
	public void testValidateDeliveryWithDifferentOwnerAndCurrentUser()
	{
		Mockito.when(cartModel.getUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getType()).thenReturn(CustomerType.REGISTERED);
		Mockito.when(cartModel.getDeliveryAddress()).thenReturn(address);
		final CustomerModel customer = new CustomerModel();
		Mockito.when(address.getOwner()).thenReturn(customer);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.doNothing().when(modelService).save(cartModel);
		strategy.validateDelivery(cartModel);
	}

	@Test
	public void testValidateDeliveryWithGuestCustomer()
	{
		Mockito.when(cartModel.getUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getType()).thenReturn(CustomerType.GUEST);
		Mockito.when(cartModel.getDeliveryAddress()).thenReturn(address);
		strategy.validateDelivery(cartModel);
	}

	@Test
	public void testValidateDeliveryWithNullDeliveryAddress()
	{
		Mockito.when(cartModel.getUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getType()).thenReturn(CustomerType.GUEST);
		Mockito.when(cartModel.getDeliveryAddress()).thenReturn(null);
		strategy.validateDelivery(cartModel);
	}

	@Test
	public void testValidateDeliveryWithSameOwnerAndCurrentUser()
	{
		Mockito.when(cartModel.getUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getType()).thenReturn(CustomerType.REGISTERED);
		Mockito.when(cartModel.getDeliveryAddress()).thenReturn(address);
		Mockito.when(address.getOwner()).thenReturn(currentUser);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		strategy.validateDelivery(cartModel);
	}

	@Test
	public void testValidateDeliveryWithB2BCustomer()
	{
		final B2BCustomerModel user = new B2BCustomerModel();
		Mockito.when(cartModel.getUser()).thenReturn(user);
		Mockito.when(cartModel.getPaymentType()).thenReturn(CheckoutPaymentType.ACCOUNT);
		strategy.validateDelivery(cartModel);
	}
}
