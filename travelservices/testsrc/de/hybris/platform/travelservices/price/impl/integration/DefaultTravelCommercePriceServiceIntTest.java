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
package de.hybris.platform.travelservices.price.impl.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.price.impl.DefaultTravelCommercePriceService;
import de.hybris.platform.util.PriceValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultTravelCommercePriceService}
 * 
 * Tests to validate the prices retrieved for a specific product and travel specific search criteria.
 */
@IntegrationTest
public class DefaultTravelCommercePriceServiceIntTest extends ServicelayerTransactionalTest
{
	private static final String TEST_CATALOG = "testCatalog";
	private static final String TEST_CATALOG_VERSION = "Online";
	private static final String TEST_CURRENCY = "GBP";
	private static final String TEST_PRODUCT = "FPLGWCDGOCAT1Test";
	private static final String TEST_PRICE_TRANSPORT_OFFERING_CODE = "transportOfferingCode1";
	private static final String TEST_PRICE_SECTOR_CODE = "sectorCode1";
	private static final String TEST_PRICE_ROUTE_CODE = "routeCode1";
	private static final String TEST_NOPRICE_ROUTE_CODE = "routeCode2";
	private static final double DELTA = 0.001;
	private static final double TRANSPORT_OFFERING_PRICE = 20.00;
	private static final double SECTOR_PRICE = 30.00;
	private static final double ROUTE_PRICE = 40.00;

	@Resource
	private TravelCommercePriceService travelCommercePriceService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private UserService userService;
	@Resource
	private SearchRestrictionService searchRestrictionService;
	@Resource
	private SessionService sessionService;

	private ProductModel product;

	/**
	 * Initializes data for testing Prices
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception
	{
		userService.setCurrentUser(userService.getAdminUser());
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/travelservices/test/testTravelPriceData.csv", "utf-8");

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(TEST_CATALOG, TEST_CATALOG_VERSION);
		final CurrencyModel currency = commonI18NService.getCurrency(TEST_CURRENCY);
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));
		commonI18NService.setCurrentCurrency(currency);

		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				searchRestrictionService.disableSearchRestrictions();

				product = productService.getProductForCode(TEST_PRODUCT);
			}
		});
	}

	/**
	 * Test method to validate the prices retrieved for the product and transport offering code.
	 */
	@Test
	public void testTransportOfferingPrices()
	{
		final Map<String, String> searchCriteria = new HashMap<String, String>();
		searchCriteria.put(PriceRowModel.TRANSPORTOFFERINGCODE, TEST_PRICE_TRANSPORT_OFFERING_CODE);

		final PriceInformation priceInfo = travelCommercePriceService.getProductWebPrice(product, searchCriteria);
		Assert.assertNotNull(priceInfo);
		verifyPrice(priceInfo, TRANSPORT_OFFERING_PRICE);
	}

	/**
	 * Test method to validate the prices retrieved for the product and sector code.
	 */
	@Test
	public void testSectorPrices()
	{
		final Map<String, String> searchCriteria = new HashMap<String, String>();
		searchCriteria.put(PriceRowModel.TRAVELSECTORCODE, TEST_PRICE_SECTOR_CODE);
		final PriceInformation priceInfo = travelCommercePriceService.getProductWebPrice(product, searchCriteria);
		Assert.assertNotNull(priceInfo);
		verifyPrice(priceInfo, SECTOR_PRICE);
	}

	/**
	 * Test method to validate the prices retrieved for the product and route code.
	 */
	@Test
	public void testRoutePrices()
	{
		final Map<String, String> searchCriteria = new HashMap<String, String>();
		searchCriteria.put(PriceRowModel.TRAVELROUTECODE, TEST_PRICE_ROUTE_CODE);
		final PriceInformation priceInfo = travelCommercePriceService.getProductWebPrice(product, searchCriteria);
		Assert.assertNotNull(priceInfo);
		verifyPrice(priceInfo, ROUTE_PRICE);
	}

	/**
	 * Test method to validate the prices when there are no prices available for the product and route code.
	 */
	@Test
	public void testUnavailableRoutePrices()
	{
		final Map<String, String> searchCriteria = new HashMap<String, String>();
		searchCriteria.put(PriceRowModel.TRAVELROUTECODE, TEST_NOPRICE_ROUTE_CODE);
		final PriceInformation priceInfo = travelCommercePriceService.getProductWebPrice(product, searchCriteria);
		Assert.assertNull(priceInfo);
	}

	/**
	 * Test method to validate the default prices retrieved for the product i.e. with no travel specific search criteria.
	 */
	@Test
	public void testDefaultPrices()
	{
		final Map<String, String> searchCriteria = null;
		final PriceInformation priceInfo = travelCommercePriceService.getProductWebPrice(product, searchCriteria);
		Assert.assertNotNull(priceInfo);
	}

	protected void verifyPrice(final PriceInformation price, final double expectedPrice)
	{
		final PriceValue value = price.getPriceValue();
		Assert.assertEquals(expectedPrice, value.getValue(), DELTA);
		Assert.assertEquals(TEST_CURRENCY, value.getCurrencyIso());
	}
}
