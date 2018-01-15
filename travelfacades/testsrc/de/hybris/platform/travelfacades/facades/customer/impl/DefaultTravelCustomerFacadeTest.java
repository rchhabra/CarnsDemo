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

package de.hybris.platform.travelfacades.facades.customer.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit test for the implementation {@link TravelCustomerFacade}
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCustomerFacadeTest
{
	private static final String TEST_EMAIL = "test@test.com";
	private static final String TEST_LAST_NAME = "lastName";
	private static final String TEST_SEARCH_ID = "testSearchID";
	private static final String TEST_ENCODED_SEARCH_SAVED = "testSavedEncodedSearch";
	private static final String TEST_ENCODED_SEARCH_CURRENT = "testCurrentEncodedSearch";

	@InjectMocks
	private DefaultTravelCustomerFacade travelCustomerFacade;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private ModelService mockModelService;

	@Mock
	private UserService mockUserService;

	@Mock
	private TimeService timeService;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private TravelCustomerAccountService mockTravelCustomerAccountService;

	@Mock
	private ConfigurationService mockConfigurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private Converter<SavedSearchData, SavedSearchModel> mockSavedSearchReverseConverter;

	@Mock
	private Converter<SavedSearchModel, SavedSearchData> mockSavedSearchConverter;

	private CustomerModel customerModel;
	private Collection<SavedSearchModel> savedSearches;
	private SavedSearchData testSavedSearchData;
	private SavedSearchModel testCurrentSearch;
	private SavedSearchModel testSavedSearch;

	/**
	 * Setup of data for the test case
	 */
	@Before
	public void setUp()
	{
		//Sample SavedSearchData
		testSavedSearchData = new SavedSearchData();

		// Sample SaveSearch Model already saved for Customer.
		testSavedSearch = new SavedSearchModel();
		testSavedSearch.setEncodedSearch(TEST_ENCODED_SEARCH_SAVED);
		testSavedSearch
				.setCreationtime(TravelDateUtils.convertStringDateToDate("01/01/2016", TravelservicesConstants.DATE_PATTERN));

		// Sample SaveSearch Model to be saved for Customer.
		testCurrentSearch = new SavedSearchModel();
		testCurrentSearch.setEncodedSearch(TEST_ENCODED_SEARCH_CURRENT);
		testCurrentSearch
				.setCreationtime(TravelDateUtils.convertStringDateToDate("15/01/2016", TravelservicesConstants.DATE_PATTERN));

		savedSearches = new ArrayList<SavedSearchModel>();
		savedSearches.add(testSavedSearch);
		customerModel = new CustomerModel();
		customerModel.setSavedSearch(savedSearches);
		travelCustomerFacade.setSavedSearchReverseConverter(mockSavedSearchReverseConverter);

		travelCustomerFacade.setTimeService(timeService);
		travelCustomerFacade.setCustomerAccountService(customerAccountService);

		given(mockUserService.getCurrentUser()).willReturn(customerModel);
		given(mockConfigurationService.getConfiguration()).willReturn(configuration);
		given(mockSavedSearchReverseConverter.convert(Matchers.any(SavedSearchData.class))).willReturn(testCurrentSearch);
		given(configuration.getInt(Matchers.anyString())).willReturn(1);

	}

	@Test
	public void testCreateGuestCustomer() throws DuplicateUidException
	{
		final CustomerModel guestCustomerModel = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(guestCustomerModel);
		travelCustomerFacade.createGuestCustomer(TEST_EMAIL, TEST_LAST_NAME);
		Assert.assertEquals(StringUtils.substringAfter(guestCustomerModel.getUid(), "|"), TEST_EMAIL);

	}

	@Test
	public void testGuestUser() throws DuplicateUidException
	{
		final CustomerModel guestCustomerModel = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(guestCustomerModel);

		Assert.assertTrue(StringUtils.isNotEmpty(travelCustomerFacade.createGuestCustomer(TEST_EMAIL, TEST_LAST_NAME)));
	}

	@Test
	public void testSaveCustomerSearchNoPreviousSavedSearch()
	{
		customerModel.setSavedSearch(null);

		given(mockUserService.getCurrentUser()).willReturn(customerModel);

		Assert.assertTrue(travelCustomerFacade.saveCustomerSearch(testSavedSearchData));
	}

	@Test
	public void testSaveCustomerSearchWithPreviousDifferentSavedSearch()
	{
		Assert.assertTrue(travelCustomerFacade.saveCustomerSearch(testSavedSearchData));
	}

	@Test
	public void testSaveCustomerSearchWithPreviousSameSavedSearch()
	{
		given(mockSavedSearchReverseConverter.convert(Matchers.any(SavedSearchData.class))).willReturn(testSavedSearch);

		Assert.assertFalse(travelCustomerFacade.saveCustomerSearch(testSavedSearchData));
	}

	@Test
	public void testSaveCustomerSearchWithMaxLimit()
	{
		given(configuration.getInt(Matchers.anyString())).willReturn(1);

		Assert.assertTrue(travelCustomerFacade.saveCustomerSearch(testSavedSearchData));
		Assert.assertNotNull(customerModel.getSavedSearch().stream()
				.filter(savedSearch -> savedSearch.getEncodedSearch().equalsIgnoreCase(TEST_ENCODED_SEARCH_CURRENT)).findAny().get());
	}

	@Test
	public void testGetCustomerSearches() throws ParseException
	{
		savedSearches.add(testCurrentSearch);
		customerModel.setSavedSearch(savedSearches);
		given(mockUserService.getCurrentUser()).willReturn(customerModel);
		travelCustomerFacade.setSavedSearchConverter(mockSavedSearchConverter);

		final SimpleDateFormat sf = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		customerModel.getSavedSearch().forEach(savedSearch -> {
			final SavedSearchData savedSearchData = new SavedSearchData();
			savedSearchData.setDepartingDateTime(sf.format(savedSearch.getCreationtime()));
			given(mockSavedSearchConverter.convert(savedSearch)).willReturn(savedSearchData);
		});
		given(timeService.getCurrentTime()).willReturn(sf.parse(sf.format(new Date())));
		Assert.assertTrue(CollectionUtils.isNotEmpty(travelCustomerFacade.getCustomerSearches()));
	}

	@Test
	public void testGetCustomerSearchesWithAllFutureSavedSearches() throws ParseException
	{
		savedSearches.add(testCurrentSearch);
		customerModel.setSavedSearch(savedSearches);
		given(mockUserService.getCurrentUser()).willReturn(customerModel);
		travelCustomerFacade.setSavedSearchConverter(mockSavedSearchConverter);

		final SimpleDateFormat sf = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		customerModel.getSavedSearch().forEach(savedSearch -> {
			final SavedSearchData savedSearchData = new SavedSearchData();
			savedSearchData.setDepartingDateTime(TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(new Date(), 2),
					TravelservicesConstants.DATE_PATTERN));
			given(mockSavedSearchConverter.convert(savedSearch)).willReturn(savedSearchData);
		});
		given(timeService.getCurrentTime()).willReturn(sf.parse(sf.format(new Date())));
		Assert.assertTrue(CollectionUtils.isNotEmpty(travelCustomerFacade.getCustomerSearches()));
	}

	@Test
	public void testGetCustomerSearchesWithoutPreviousSavedSearches() throws ParseException
	{
		travelCustomerFacade.setSavedSearchConverter(mockSavedSearchConverter);
		final CustomerModel customerModel = new CustomerModel();
		customerModel.setSavedSearch(Collections.emptyList());
		given(mockUserService.getCurrentUser()).willReturn(customerModel);
		final SavedSearchData savedSearchData = new SavedSearchData();
		final SimpleDateFormat sf = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		savedSearchData.setDepartingDateTime(sf.format(new Date()));
		given(timeService.getCurrentTime()).willReturn(sf.parse(sf.format(new Date())));
		Assert.assertTrue(CollectionUtils.isEmpty(travelCustomerFacade.getCustomerSearches()));
	}

	@Test
	public void testRemoveSavedSearch_SearchExist()
	{
		given(mockTravelCustomerAccountService.findSavedSearch(Matchers.anyString())).willReturn(null);

		Assert.assertTrue(travelCustomerFacade.removeSavedSearch(TEST_SEARCH_ID));
	}

	@Test
	public void testRemoveSavedSearch_SearchDoNotExist()
	{
		given(mockTravelCustomerAccountService.findSavedSearch(Matchers.anyString()))
				.willThrow(new ModelNotFoundException(Matchers.anyString()));
		Assert.assertFalse(travelCustomerFacade.removeSavedSearch(TEST_SEARCH_ID));
	}

	@Test
	public void testRemoveOldSavedSearches()
	{
		savedSearches.add(testCurrentSearch);
		savedSearches.add(testSavedSearch);
		travelCustomerFacade.removeOldSavedSearches(savedSearches, customerModel, 1);
		verify(mockModelService, times(1)).refresh(Matchers.any(CustomerModel.class));
	}

	@Test
	public void testisCurrentUserB2bCustomer()
	{
		Assert.assertFalse(travelCustomerFacade.isCurrentUserB2bCustomer());
		given(mockUserService.getCurrentUser()).willReturn(new B2BCustomerModel());
		Assert.assertTrue(travelCustomerFacade.isCurrentUserB2bCustomer());
	}
}
