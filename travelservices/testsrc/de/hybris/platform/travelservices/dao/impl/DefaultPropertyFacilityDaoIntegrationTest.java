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
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.travelservices.dao.PropertyFacilityDao;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import java.util.Collections;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultPropertyFacilityDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	PropertyFacilityDao propertyFacilityDao;

	@Resource
	public BaseSiteService baseSiteService;

	@Resource
	public CommonI18NService commonI18NService;

	@Resource
	public SessionService sessionService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private static final String CURRENCY_SESSION_ATTR_KEY = "currency".intern();
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String userId = "jane.smith1@mailinator.com";

	private CurrencyModel currencyModel;
	private UserModel user;

	@Before
	public void setUp() throws Exception
	{
		if (Objects.isNull(baseSiteService.getCurrentBaseSite()))
		{
			importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
			baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
			final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
			Assert.assertNotNull(catalogVersionModel);
			catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));

			user = createUser(userId);

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
		importCsv("/travelservices/test/testAccommodationData.csv", "utf-8");

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
	public void testFindPropertyFacilities()
	{
		Assert.assertTrue(CollectionUtils.isNotEmpty(propertyFacilityDao.findPropertyFacilities()));
	}

	@Test
	public void testFindPropertyFacilitiesWithBatch()
	{
		final SearchResult<PropertyFacilityModel> facilities = propertyFacilityDao.findPropertyFacilities(10, 0);
		Assert.assertTrue(CollectionUtils.isNotEmpty(facilities.getResult()));
	}

	@Test
	public void testFindPropertyFacilityWithCode()
	{
		final String code = "facil1";
		final PropertyFacilityModel facility = propertyFacilityDao.findPropertyFacility(code);
		Assert.assertEquals(code, facility.getCode());
	}

}
