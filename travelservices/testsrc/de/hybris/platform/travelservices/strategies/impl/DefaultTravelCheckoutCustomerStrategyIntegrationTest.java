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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.strategies.TravelCheckoutCustomerStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultTravelCheckoutCustomerStrategyIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	TravelCheckoutCustomerStrategy checkoutCustomerStrategy;

	@Resource
	private ModelService modelService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private UserService userService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private SessionService sessionService;

	private OrderModel orderModel;

	private static final String BASESITE_UID = "testSite";
	private static final String BASESTORE_UID = "testStore";
	private static final String PRODUCT_CODE = "TestProductCode";
	private static final String USER_ID = "TEST_USER_UID|TEST_USER";
	private static final String USER_NAME = "TestUserName";
	private static final String ORDER_CODE = "TestOrderCode";
	private static final String ANONYMOUS_CHECKOUT_GUID = "anonymous_checkout_guid";

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSite, false);

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(BASESTORE_UID);

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersion));

		final UserModel user = modelService.create(CustomerModel.class);
		user.setUid(USER_ID);
		user.setName(USER_NAME);
		modelService.save(user);

		final CurrencyModel currencyModel = commonI18NService.getCurrency("EUR");

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(PRODUCT_CODE);
		productModel.setCatalogVersion(catalogVersion);

		final UnitModel unitModel = modelService.create(UnitModel.class);
		unitModel.setUnitType("awsome");
		unitModel.setCode("goblins");

		orderModel = modelService.create(OrderModel.class);
		orderModel.setCode(ORDER_CODE);
		orderModel.setCurrency(currencyModel);
		orderModel.setTotalPrice(Double.valueOf(100.0));
		orderModel.setSubtotal(Double.valueOf(20));
		orderModel.setDeliveryCost(Double.valueOf(30));
		orderModel.setDate(new Date());
		orderModel.setNet(Boolean.TRUE);
		orderModel.setUser(user);
		orderModel.setStore(baseStore);

		final OrderEntryModel orderEntry = modelService.create(OrderEntryModel.class);
		orderEntry.setProduct(productModel);
		orderEntry.setOrder(orderModel);
		orderEntry.setBasePrice(Double.valueOf(20));
		orderEntry.setTotalPrice(Double.valueOf(40));
		orderEntry.setQuantity(Long.valueOf(1));
		orderEntry.setUnit(unitModel);
		orderEntry.setOrder(orderModel);

		userService.setCurrentUser(user);

		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();
		orderEntryList.add(orderEntry);

		orderModel.setEntries(orderEntryList);
		modelService.save(orderModel);

		sessionService.setAttribute(ANONYMOUS_CHECKOUT_GUID, "TEST_USER");
	}

	@Test
	public void testIsValidBookingForCurrentGuestUser()
	{
		Assert.assertTrue(checkoutCustomerStrategy.isValidBookingForCurrentGuestUser(ORDER_CODE));
	}

}
