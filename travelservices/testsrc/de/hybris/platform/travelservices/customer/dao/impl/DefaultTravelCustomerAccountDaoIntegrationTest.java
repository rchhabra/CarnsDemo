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

package de.hybris.platform.travelservices.customer.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.customer.dao.TravelCustomerAccountDao;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * integration test for {@link DefaultTravelCustomerAccountDao}
 */
@IntegrationTest
public class DefaultTravelCustomerAccountDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private TravelCustomerAccountDao customerAccountDao;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	public CommonI18NService commonI18NService;

	@Resource
	public SessionService sessionService;

	@Resource
	private CatalogVersionService catalogVersionService;

	public static final String CURRENCY_SESSION_ATTR_KEY = "currency".intern();
	private static final String TEST_BASESITE_UID = "testSite";

	private BaseStoreModel baseStore;
	private CustomerModel customerModel;
	private CurrencyModel currencyModel;
	private UserModel user;

	/**
	 * Setup of data for the test case
	 *
	 * @throws ImpExException
	 */
	@Before
	public void setup() throws ImpExException
	{
		baseStore = modelService.create(BaseStoreModel.class);
		baseStore.setUid("airline");
		modelService.save(baseStore);
		baseStore = baseStoreService.getBaseStoreForUid("airline");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("airline"), false);

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

		//final CustomerModel customerModel = modelService.create(CustomerModel.class);
		//customerModel.setUid("testUser");
		//customerModel.setName("Test User");
		//modelService.save(customerModel);
		//this.customerModel = customerModel;
		final Calendar cal = Calendar.getInstance();
		cal.set(2016, Calendar.APRIL, 1, 10, 11, 12); //Year, month, day of month, hours, minutes and seconds
		final Date date = cal.getTime();

		final OrderModel originalOrder = new OrderModel();
		originalOrder.setCode("00000010000");
		originalOrder.setCurrency(currencyModel);
		originalOrder.setDate(date);
		originalOrder.setUser(user);
		originalOrder.setStatus(OrderStatus.ACTIVE);
		originalOrder.setStore(baseStore);
		modelService.save(originalOrder);

		final OrderModel order1 = new OrderModel();
		order1.setCode("00000010001");
		order1.setOriginalOrder(originalOrder);
		order1.setCurrency(currencyModel);
		order1.setDate(date);
		order1.setUser(user);
		order1.setStatus(OrderStatus.ACTIVE);
		order1.setStore(baseStore);
		modelService.save(order1);

		final Calendar cal1 = Calendar.getInstance();
		cal1.set(2016, Calendar.APRIL, 1, 10, 15, 12); //Year, month, day of month, hours, minutes and seconds
		final Date date1 = cal.getTime();

		final OrderModel order2 = new OrderModel();
		order2.setCode("00000010001");
		order2.setOriginalOrder(originalOrder);
		order2.setCurrency(currencyModel);
		order2.setDate(date1);
		order2.setVersionID("0007");
		order2.setUser(user);
		order2.setStatus(OrderStatus.AMENDMENTINPROGRESS);
		order2.setStore(baseStore);
		modelService.save(order2);

		final OrderUserAccountMappingModel orderUserAccountMappingModel = new OrderUserAccountMappingModel();
		orderUserAccountMappingModel.setUser(user);
		orderUserAccountMappingModel.setOrderCode(order1.getCode());
		modelService.save(orderUserAccountMappingModel);
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
	public void testFindOrderModelByOriginalOrderCode()
	{
		final OrderModel order = customerAccountDao.findOrderModelByOriginalOrderCode("00000010000", baseStore);
		Assert.assertNotNull(order);
		Assert.assertEquals("00000010001", order.getCode());
	}

	@Test
	public void testFindOrdersByOrderUserMapping_WithCustomer()
	{
		Assert.assertTrue(customerAccountDao.findOrdersByOrderUserMapping((CustomerModel) user).size() > 0);
	}

	@Test
	public void testFindOrdersByOrderUserMapping_WithoutCustomer()
	{
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid("testUser2");
		customerModel.setName("Test User2");
		modelService.save(customerModel);
		Assert.assertTrue(customerAccountDao.findOrdersByOrderUserMapping(customerModel).size() == 0);
	}

	@Test
	public void testFindOrderUserMapping()
	{
		Assert.assertNotNull(customerAccountDao.findOrderUserMapping("00000010001", (CustomerModel) user));
	}

	@Test
	public void testFindSavedSearch()
	{
		final String testEncodedSearch = "testEncodedSearch";
		final SavedSearchModel savesearch = modelService.create(SavedSearchModel.class);
		savesearch.setEncodedSearch(testEncodedSearch);
		modelService.save(savesearch);

		Assert.assertEquals(testEncodedSearch,
				customerAccountDao.findSavedSearch(savesearch.getPk().toString()).getEncodedSearch());
	}
}
