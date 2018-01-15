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
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.travelservices.dao.AccommodationDao;
import de.hybris.platform.travelservices.model.product.AccommodationModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAccommodationDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	AccommodationDao accommodationDao;

	@Resource
	private FlexibleSearchService flexibleSearchService;

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

	private static final String CURRENCY_SESSION_ATTR_KEY = "currency".intern();
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String userId = "jane.smith1@mailinator.com";
	private final String accommodationOfferingCode = "DORSET_SQUARE_HOTEL";
	private final String accommodationCode = "DORSET_SQUARE_HOTELLUXURY_ROOM";

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

		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);

		final CatalogVersionModel catalogVersion = flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog))
				.getResult().stream().findFirst().get();

		sessionService.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, Collections.singletonList(catalogVersion));
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
	public void testFindAccommodationForAccommodationOffering()
	{
		final List<AccommodationModel> accommodations = accommodationDao
				.findAccommodationForAccommodationOffering(accommodationOfferingCode);
		Assert.assertTrue(CollectionUtils.isNotEmpty(accommodations));
	}

	@Test
	public void testFindAccommodationForAccommodationOfferingWithCode()
	{
		final AccommodationModel accommodation = accommodationDao
				.findAccommodationForAccommodationOffering(accommodationOfferingCode, accommodationCode);
		Assert.assertNotNull(accommodation);
	}
}
