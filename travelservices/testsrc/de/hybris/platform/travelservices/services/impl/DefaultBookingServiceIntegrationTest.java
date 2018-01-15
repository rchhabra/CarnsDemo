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
package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.ordersplitting.ConsignmentService;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


@IntegrationTest
public class DefaultBookingServiceIntegrationTest extends ServicelayerTest
{

	@Mock
	private ItemModelContext context;

	@Resource
	private BookingService bookingService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CommerceCheckoutService commerceCheckoutService;

	@Resource
	private PaymentService paymentService;

	@Resource
	private OrderCancelService orderCancelService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private ConsignmentService consignmentService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private BaseStoreService baseStoreService;

	private OrderModel orderModel;

	private BaseStoreModel baseStore;
	private BaseSiteModel baseSite;

	protected static final String ORDER_CODE = "AbstractOrderConverterIntegrationTestOrder";
	protected static final String PRODUCT_CODE = "AbstractOrderConverterIntegrationTestProduct";
	protected static final String USER_CODE = "AbstractOrderConverterIntegrationTestUser";
	protected static final String USER_NAME = "AbstractOrderConverterIntegrationTestUserName";


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);

		baseStore = modelService.create(BaseStoreModel.class);
		baseStore.setUid("airline");
		modelService.save(baseStore);
		baseStore = baseStoreService.getBaseStoreForUid("airline");

		baseSite = modelService.create(BaseSiteModel.class);
		baseSite.setUid("airline");
		baseSite.setStores(Arrays.asList(baseStore));
		modelService.save(baseSite);
		baseSite = baseSiteService.getBaseSiteForUID("airline");

		baseSiteService.setCurrentBaseSite(baseSite, false);

		final CurrencyModel currencyModel = (CurrencyModel) flexibleSearchService
				.search("SELECT {PK} FROM {Currency} WHERE {isocode}='EUR'").getResult().get(0);

		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(USER_CODE);
		customerModel.setName(USER_NAME);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(PRODUCT_CODE);
		productModel.setCatalogVersion(flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog))
				.getResult().get(0));

		final UnitModel unitModel = modelService.create(UnitModel.class);
		unitModel.setUnitType("awsome");
		unitModel.setCode("goblins");

		orderModel = modelService.create(OrderModel.class);
		orderModel.setCode(ORDER_CODE);
		orderModel.setCurrency(currencyModel);
		orderModel.setTotalPrice(Double.valueOf(1.0));
		orderModel.setSubtotal(Double.valueOf(2.0));
		orderModel.setDeliveryCost(Double.valueOf(3.0));
		orderModel.setDate(new Date());
		orderModel.setNet(Boolean.TRUE);
		orderModel.setUser(customerModel);
		orderModel.setStore(baseStore);

		final OrderEntryModel orderEntry = modelService.create(OrderEntryModel.class);
		orderEntry.setProduct(productModel);
		orderEntry.setOrder(orderModel);
		orderEntry.setBasePrice(Double.valueOf(2.2));
		orderEntry.setTotalPrice(Double.valueOf(3.9));
		orderEntry.setQuantity(Long.valueOf(1));
		orderEntry.setUnit(unitModel);

		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();
		orderEntryList.add(orderEntry);
		orderModel.setEntries(orderEntryList);
		modelService.save(orderModel);
	}

	@Test
	public void testIsCancelPossible()
	{
		final OrderCancelConfigModel configuration = modelService.create(OrderCancelConfigModel.class);
		configuration.setOrderCancelAllowed(true);
		modelService.save(configuration);

		orderModel.setStatus(OrderStatus.ORDER_SPLIT);
		boolean result = bookingService.isCancelPossible(orderModel);
		Assert.assertTrue(result);

		orderModel.setStatus(OrderStatus.CANCELLED);
		result = bookingService.isCancelPossible(orderModel);
		Assert.assertFalse(result);

	}

	@Test
	public void testGetOrder()
	{

		final OrderModel testOrder = bookingService.getOrder(ORDER_CODE);
		Assert.assertNotNull(testOrder);

	}

}
