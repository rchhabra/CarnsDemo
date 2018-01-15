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

package de.hybris.platform.travelservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.dao.ConfiguredAccommodationDao;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.order.daos.TravelCartDao;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest
public class DefaultConfiguredAccommodationDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	ConfiguredAccommodationDao configuredAccommodationDao;

	@Resource
	AccommodationMapService accommodationMapService;

	@Resource
	TransportOfferingService transportOfferingService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	public BaseStoreService baseStoreService;

	@Resource
	public KeyGenerator orderCodeGenerator;

	@Resource
	public UserService userService;

	@Resource
	public TravelCartDao travelCartDao;

	@Resource
	public CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Resource
	public CommonI18NService commonI18NService;

	@Resource
	public BookingService bookingService;

	@Resource
	public TravelCommerceStockService commerceStockService;

	@Resource
	public StoreSessionService storeSessionService;

	@Resource
	public SessionService sessionService;

	@Resource
	public TimeService timeService;

	@Resource
	public ModelService modelService;

	@Resource
	public BaseSiteService baseSiteService;

	@Resource
	public EnumerationService enumerationService;

	@Resource
	private GuidKeyGenerator guidKeyGenerator;

	@Resource
	private CatalogVersionService catalogVersionService;

	private UserModel user;
	private CurrencyModel currencyModel;

	AccommodationMapModel accommodationMap;
	CatalogVersionModel catalogVersion;

	public static final String CURRENCY_SESSION_ATTR_KEY = "currency".intern();
	private static final String TEST_BASESITE_UID = "testSite";

	@Before
	public void setUp() throws ImpExException
	{
		if (Objects.isNull(baseSiteService.getCurrentBaseSite()))
		{
			importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
			baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
			final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
			Assert.assertNotNull(catalogVersionModel);
			catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));

			user = createUser("user");

			userService.setCurrentUser(user);
		}
		if (Objects.isNull(currencyModel) || Objects.isNull(commonI18NService.getCurrentCurrency()))
		{
			currencyModel = Objects.isNull(commonI18NService.getCurrentCurrency()) ? modelService.create(CurrencyModel.class)
					: commonI18NService.getCurrentCurrency();
			final CurrencyModel optCurrency = flexibleSearchService
					.<CurrencyModel> search("SELECT {PK} FROM {Currency} WHERE {isocode}='EUR'").getResult().get(0);
			if (optCurrency != null)
			{
				currencyModel = optCurrency;
			}
			if (!currencyModel.getIsocode().equals("EUR".intern()))
			{
				currencyModel.setIsocode("EUR".intern());
				modelService.save(currencyModel);
				commonI18NService.setCurrentCurrency(currencyModel);
				sessionService.setAttribute(CURRENCY_SESSION_ATTR_KEY, "EUR".intern());
			}

		}
		importCsv("/travelservices/test/testTravelBundleCommerceCartService.csv", "utf-8");

		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);

		catalogVersion = flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog))
				.getResult().stream().findFirst().get();

		sessionService.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, Collections.singletonList(catalogVersion));

		accommodationMap = modelService.create(AccommodationMapModel.class);
		accommodationMap.setCode("config1");
		modelService.save(accommodationMap);

	}

	private UserModel createUser(final String uid)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(uid);
		user.setName(uid);
		modelService.save(user);
		return user;
	}

	@Test
	@Ignore
	public void testFindAccommodationMapConfiguration()
	{
		final List<ConfiguredAccommodationModel> configuredAccommodations = configuredAccommodationDao
				.findAccommodationMapConfiguration(accommodationMap, catalogVersion);
		Assert.assertTrue(CollectionUtils.isNotEmpty(configuredAccommodations));
	}

	@Test
	public void testFindAccommodation() throws AccommodationMapDataSetUpException
	{
		final String uid = "config1-EconRow40";
		final ConfiguredAccommodationModel configuredAccommodation = configuredAccommodationDao.findAccommodation(uid,
				catalogVersion);
		Assert.assertTrue(Objects.nonNull(configuredAccommodation));
		Assert.assertEquals(uid, configuredAccommodation.getUid());
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void testFindAccommodationForAccommodationMapDataSetUpException()
	{
		final String uid = "config1-EconRow41";
		final ConfiguredAccommodationModel configuredAccommodation = configuredAccommodationDao.findAccommodation(uid,
				catalogVersion);
		Assert.assertTrue(Objects.nonNull(configuredAccommodation));
		Assert.assertEquals(uid, configuredAccommodation.getUid());
	}
}
