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

package de.hybris.platform.travelservices.stock.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stock.TravelStockService;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockByEntryTypeStrategy;
import de.hybris.platform.travelservices.strategies.stock.accommodation.impl.AccommodationStockReservationReleaseStrategy;
import de.hybris.platform.travelservices.strategies.stock.impl.DefaultStockReservationReleaseStrategy;
import de.hybris.platform.travelservices.strategies.stock.transport.impl.TransportEntryManageStockStrategy;
import de.hybris.platform.travelservices.strategies.stock.transport.impl.TransportStockReservationReleaseStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultTravelCommerceStockService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCommerceStockServiceTest
{

	@InjectMocks
	private DefaultTravelCommerceStockService travelCommerceStockService;

	@Mock
	private CommerceAvailabilityCalculationStrategy commerceStockLevelCalculationStrategy;

	@Mock
	TransportStockReservationReleaseStrategy transportStockReservationReleaseStrategy;

	@Mock
	AccommodationStockReservationReleaseStrategy accommodationStockReservationReleaseStrategy;

	@Mock
	DefaultStockReservationReleaseStrategy defaultStockReservationReleaseStrategy;

	@Mock
	private StockService stockService;

	@Mock
	private ProductModel productModel;

	@Mock
	private TransportOfferingModel transportOffering1;

	@Mock
	private TransportOfferingModel transportOffering2;

	@Mock
	private Collection<StockLevelModel> stockLevel;

	@Mock
	private WarehouseService warehouseService;

	@Mock
	private Configuration config;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Map<OrderEntryType, TravelManageStockByEntryTypeStrategy> manageStockByEntryTypeStrategyMap;

	@Mock
	private TransportEntryManageStockStrategy transportEntryManageStockStrategy;

	@Mock
	private TravelStockService travelStockService;

	@Mock
	private ModelService modelService;


	/**
	 * Test Method to stock level for product and transport offerings
	 */
	@Test
	public void testStockLevelForProductAndTransportOfferings()
	{
		final Collection<TransportOfferingModel> transportOfferings = new ArrayList<>();
		transportOfferings.add(transportOffering1);
		transportOfferings.add(transportOffering2);

		when(stockService.getStockLevels(Matchers.any(ProductModel.class), Matchers.anyCollectionOf(WarehouseModel.class)))
				.thenReturn(stockLevel);

		when(commerceStockLevelCalculationStrategy.calculateAvailability(Matchers.anyCollectionOf(StockLevelModel.class)))
				.thenReturn(10L);
		travelCommerceStockService.getStockLevel(productModel, transportOfferings);

		verify(commerceStockLevelCalculationStrategy, times(1))
				.calculateAvailability(Matchers.anyCollectionOf(StockLevelModel.class));

	}

	/**
	 * Test Method to stock level for product and transport offerings
	 */
	@Test
	public void testStockLevelForEmptyTransportOfferings()
	{
		when(stockService.getStockLevels(Matchers.any(ProductModel.class), Matchers.anyCollectionOf(WarehouseModel.class)))
				.thenReturn(stockLevel);

		when(commerceStockLevelCalculationStrategy.calculateAvailability(Matchers.anyCollectionOf(StockLevelModel.class)))
				.thenReturn(10L);

		when(warehouseService.getWarehouseForCode("default")).thenReturn(new WarehouseModel());

		travelCommerceStockService.getStockLevel(productModel, Collections.EMPTY_LIST);

		verify(commerceStockLevelCalculationStrategy, times(1))
				.calculateAvailability(Matchers.anyCollectionOf(StockLevelModel.class));

	}

	@Test
	public void testReserveStockReservationNotEnabled() throws InsufficientStockLevelException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = testDataSetUp.createAbstractOrderModel(null, null);
		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.FALSE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.reserve(abstractOrderModel);
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testReleaseStockReservationNotEnabled() throws InsufficientStockLevelException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final AbstractOrderModel abstractOrderModel = testDataSetUp.createAbstractOrderModel(null, null);
		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.FALSE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.release(abstractOrderModel);
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testReleaseNotOrderModel() throws InsufficientStockLevelException
	{
		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.TRUE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.release(new CartModel());
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testReleaseNoHistory() throws InsufficientStockLevelException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ProductModel fareProduct = testDataSetUp.createProductModel(ProductType.FARE_PRODUCT, "OOWC521");
		final ProductModel ancillaryProduct = testDataSetUp.createProductModel(ProductType.ANCILLARY, "EXBAG32KG");

		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("HB1234050720160735");
		final TransportOfferingModel transportOffering2 = testDataSetUp.createTransportOfferingModel("HB5678050720160735");

		final AbstractOrderEntryModel entry1 = testDataSetUp.createOrderEntryModel(2l,
				Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()), fareProduct);
		final AbstractOrderEntryModel entry2 = testDataSetUp.createOrderEntryModel(3l,
				Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()), ancillaryProduct);

		final OrderModel orderModel = testDataSetUp.createAbstractOrderModel(Stream.of(entry1, entry2).collect(Collectors.toList()),
				Collections.EMPTY_LIST);

		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.TRUE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.release(orderModel);
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testReleaseOrderWithHistoryNull() throws InsufficientStockLevelException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OrderHistoryEntryModel historyEntry1 = testDataSetUp.createOrderHistoryEntryModel(null);
		final OrderHistoryEntryModel historyEntry2 = testDataSetUp.createOrderHistoryEntryModel(null);

		final OrderModel orderToTest = testDataSetUp.createAbstractOrderModel(null,
				Stream.of(historyEntry1, historyEntry2).collect(Collectors.toList()));

		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.TRUE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.release(orderToTest);
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testReleaseWithHistory() throws InsufficientStockLevelException
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ProductModel fareProduct = testDataSetUp.createProductModel(ProductType.FARE_PRODUCT, "OOWC521");
		final ProductModel ancillaryProduct = testDataSetUp.createProductModel(ProductType.ANCILLARY, "EXBAG32KG");

		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("HB1234050720160735");
		final TransportOfferingModel transportOffering2 = testDataSetUp.createTransportOfferingModel("HB5678050720160735");

		final AbstractOrderEntryModel entry1 = testDataSetUp.createOrderEntryModel(2l,
				Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()), fareProduct);
		final AbstractOrderEntryModel entry2 = testDataSetUp.createOrderEntryModel(3l,
				Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()), ancillaryProduct);

		final OrderModel orderModel = testDataSetUp.createAbstractOrderModel(Stream.of(entry1, entry2).collect(Collectors.toList()),
				Collections.EMPTY_LIST);

		final OrderHistoryEntryModel historyEntry1 = testDataSetUp.createOrderHistoryEntryModel(orderModel);
		final OrderHistoryEntryModel historyEntry2 = testDataSetUp.createOrderHistoryEntryModel(orderModel);

		final OrderModel orderToTest = testDataSetUp.createAbstractOrderModel(null,
				Stream.of(historyEntry1, historyEntry2).collect(Collectors.toList()));

		travelCommerceStockService.setManageStockByEntryTypeStrategyMap(manageStockByEntryTypeStrategyMap);
		when(manageStockByEntryTypeStrategyMap.get(OrderEntryType.TRANSPORT)).thenReturn(transportEntryManageStockStrategy);

		doNothing().when(stockService).release(fareProduct, transportOffering1, 2, null);
		doNothing().when(stockService).release(fareProduct, transportOffering2, 2, null);
		doNothing().when(stockService).release(ancillaryProduct, transportOffering1, 3, null);
		doNothing().when(stockService).release(ancillaryProduct, transportOffering2, 3, null);

		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.TRUE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.release(orderToTest);
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testAdjustStockReservationForAmmendmentNotEnabled() throws InsufficientStockLevelException
	{
		when(config.getBoolean("enable.stock.reservation")).thenReturn(Boolean.FALSE);
		when(configurationService.getConfiguration()).thenReturn(config);
		travelCommerceStockService.adjustStockReservationForAmmendment(null, null);
		verify(config, times(1)).getBoolean("enable.stock.reservation");
		verify(configurationService, times(1)).getConfiguration();
	}

	@Test
	public void testGetStockForDate()
	{
		final StockLevelModel stockLevel = new StockLevelModel();
		stockLevel.setAvailable(10);
		stockLevel.setReserved(8);
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(stockLevel);
		Assert.assertEquals(2,
				travelCommerceStockService.getStockForDate(new ProductModel(), new Date(), Collections.emptyList()).intValue());
	}

	@Test
	public void testGetStockForDateForNullStock()
	{
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(null);
		Assert.assertEquals(0,
				travelCommerceStockService.getStockForDate(new ProductModel(), new Date(), Collections.emptyList()).intValue());
	}

	@Test
	public void testGetStockLevelQuantityForNullWarehouses()
	{
		when(commerceStockLevelCalculationStrategy.calculateAvailability(Matchers.anyCollection())).thenReturn(10l);
		when(warehouseService.getWarehouseForCode("default")).thenReturn(new WarehouseModel());
		when(stockService.getStockLevels(Matchers.any(ProductModel.class), Matchers.anyCollection()))
				.thenReturn(Stream.of(new StockLevelModel()).collect(Collectors.toList()));
		Assert.assertEquals(10,
				travelCommerceStockService.getStockLevelQuantity(new ProductModel(), Collections.emptyList()).longValue());
	}

	@Test
	public void testGetStockLevelQuantityForNullStocks()
	{
		when(commerceStockLevelCalculationStrategy.calculateAvailability(Matchers.anyCollection())).thenReturn(10l);
		when(stockService.getStockLevels(Matchers.any(ProductModel.class), Matchers.anyCollection()))
				.thenReturn(Collections.emptyList());
		Assert.assertEquals(10, travelCommerceStockService
				.getStockLevelQuantity(new ProductModel(), Stream.of(new WarehouseModel()).collect(Collectors.toList())).longValue());
	}

	@Test
	public void testGetStockLevelQuantity()
	{
		when(commerceStockLevelCalculationStrategy.calculateAvailability(Matchers.anyCollection())).thenReturn(10l);
		when(stockService.getStockLevels(Matchers.any(ProductModel.class), Matchers.anyCollection()))
				.thenReturn(Stream.of(new StockLevelModel()).collect(Collectors.toList()));
		Assert.assertEquals(10, travelCommerceStockService
				.getStockLevelQuantity(new ProductModel(), Stream.of(new WarehouseModel()).collect(Collectors.toList())).longValue());
	}

	@Test
	public void testIsStockSystemEnabledForNullBaseStore()
	{
		Assert.assertFalse(travelCommerceStockService.isStockSystemEnabled(null));
	}

	@Test
	public void testIsStockSystemEnabledForWarehouse()
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		Assert.assertFalse(travelCommerceStockService.isStockSystemEnabled(baseStore));

		final BaseStoreModel baseStore1 = new BaseStoreModel();
		baseStore1.setWarehouses(Collections.emptyList());
		Assert.assertFalse(travelCommerceStockService.isStockSystemEnabled(baseStore1));

		final WarehouseModel warehouseModel = new WarehouseModel();
		warehouseModel.setCode("default");
		baseStore1.setWarehouses(Arrays.asList(warehouseModel));
		Assert.assertFalse(travelCommerceStockService.isStockSystemEnabled(baseStore1));
	}

	@Test
	public void testIsStockSystemEnabled()
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		final WarehouseModel warehouseModel = new WarehouseModel();
		warehouseModel.setCode("");
		baseStore.setWarehouses(Arrays.asList(warehouseModel));
		Assert.assertTrue(travelCommerceStockService.isStockSystemEnabled(baseStore));
	}

	@Test(expected = InsufficientStockLevelException.class)
	public void testReservePerDateProductForNullStock() throws InsufficientStockLevelException
	{
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(null);

		final ProductModel product = new ProductModel();
		product.setCode("TEST_PRODUCT_CODE");
		travelCommerceStockService.reservePerDateProduct(product, new Date(), 2, Collections.emptyList());
	}

	@Test(expected = InsufficientStockLevelException.class)
	public void testReservePerDateProductForQuantityGreaterThanStock() throws InsufficientStockLevelException
	{
		final StockLevelModel stockLevel = new StockLevelModel();
		stockLevel.setAvailable(10);
		stockLevel.setReserved(8);
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(stockLevel);

		final ProductModel product = new ProductModel();
		product.setCode("TEST_PRODUCT_CODE");
		travelCommerceStockService.reservePerDateProduct(product, new Date(), 4, Collections.emptyList());
	}

	@Test
	public void testReservePerDateProductForQuantityLessThanStock() throws InsufficientStockLevelException
	{
		final StockLevelModel stockLevel = new StockLevelModel();
		stockLevel.setAvailable(10);
		stockLevel.setReserved(8);
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(stockLevel);

		final ProductModel product = new ProductModel();
		product.setCode("TEST_PRODUCT_CODE");
		travelCommerceStockService.reservePerDateProduct(product, new Date(), 1, Collections.emptyList());
		verify(modelService, times(1)).save(stockLevel);
	}

	@Test
	public void testReleasePerDateProductForNullStock() throws InsufficientStockLevelException
	{
		final StockLevelModel stockLevel = new StockLevelModel();
		stockLevel.setAvailable(10);
		stockLevel.setReserved(8);
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(null);

		final ProductModel product = new ProductModel();
		product.setCode("TEST_PRODUCT_CODE");
		travelCommerceStockService.releasePerDateProduct(product, new Date(), 1, Collections.emptyList());
		verify(modelService, times(0)).save(stockLevel);
	}

	@Test
	public void testReleasePerDateProduct() throws InsufficientStockLevelException
	{
		final StockLevelModel stockLevel = new StockLevelModel();
		stockLevel.setAvailable(10);
		stockLevel.setReserved(8);
		when(travelStockService.getStockLevelForDate(Matchers.any(ProductModel.class), Matchers.anyCollection(),
				Matchers.any(Date.class))).thenReturn(stockLevel);

		final ProductModel product = new ProductModel();
		product.setCode("TEST_PRODUCT_CODE");
		travelCommerceStockService.releasePerDateProduct(product, new Date(), 1, Collections.emptyList());
		verify(modelService, times(1)).save(stockLevel);
	}


	private class TestDataSetUp
	{
		private OrderModel createAbstractOrderModel(final List<AbstractOrderEntryModel> entries,
				final List<OrderHistoryEntryModel> histories)
		{
			final OrderModel order = new OrderModel();
			order.setEntries(entries);
			order.setCode("00001");
			order.setHistoryEntries(histories);
			return order;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel to = new TransportOfferingModel();
			to.setCode(code);
			return to;
		}

		private AbstractOrderEntryModel createOrderEntryModel(final long quantity,
				final List<TransportOfferingModel> transportOfferings, final ProductModel product)
		{
			final AbstractOrderEntryModel entry = new OrderEntryModel();
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			entry.setQuantity(quantity);
			entry.setType(OrderEntryType.TRANSPORT);
			orderEntryInfo.setTransportOfferings(transportOfferings);
			entry.setProduct(product);
			entry.setTravelOrderEntryInfo(orderEntryInfo);
			return entry;
		}

		private ProductModel createProductModel(final ProductType type, final String code)
		{
			final ProductModel product = new ProductModel();
			product.setProductType(type);
			product.setCode(code);
			return product;
		}

		private OrderHistoryEntryModel createOrderHistoryEntryModel(final OrderModel order)
		{
			final OrderHistoryEntryModel history = new OrderHistoryEntryModel();
			history.setPreviousOrderVersion(order);
			return history;
		}
	}
}