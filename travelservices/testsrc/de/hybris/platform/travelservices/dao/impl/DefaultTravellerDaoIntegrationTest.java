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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.dao.TravellerDao;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration Test for the TravellerDao implementation using ServicelayerTransactionalTest
 */
@IntegrationTest
public class DefaultTravellerDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private TravellerDao travellerDao;

	@Resource
	private ModelService modelService;

	private TravellerModel traveller;

	private CustomerModel customer;

	/**
	 * Setup method for the test class
	 */
	@Before
	public void setUp()
	{
		customer = modelService.create(CustomerModel.class);
		customer.setUid("bad.guy@gmail.com");
		customer.setName("Adult");

		traveller = new TravellerModel();
		traveller.setLabel("adult1");
		traveller.setType(TravellerType.PASSENGER);
		traveller.setUid("001122334455");

		final PassengerTypeModel passengerType = new PassengerTypeModel();
		passengerType.setCode("adult");
		passengerType.setName("Adult", Locale.ENGLISH);

		final PassengerInformationModel passengerInfo = modelService.create(PassengerInformationModel._TYPECODE);
		passengerInfo.setPassengerType(passengerType);
		passengerInfo.setFirstName("asd");
		passengerInfo.setSurname("asdf");
		traveller.setInfo(passengerInfo);

		traveller.setCustomer(customer);

		modelService.saveAll(traveller);
	}

	/**
	 * Method to test get passenger type by passengerTypeCode
	 */
	@Test
	public void testFindTravellerByUid()
	{
		final TravellerModel existingTraveller = travellerDao.findTraveller(traveller.getUid());
		Assert.assertEquals(traveller.getLabel(), existingTraveller.getLabel());
	}

	/**
	 * Method to test get travelers by passengerTypeCode, first name text and current customer
	 */
	@Test
	public void testFindTravellerByFirstName()
	{
		final List<TravellerModel> travellers = travellerDao.findSavedTravellersUsingFirstNameText("asd", "adult", customer);
		Assert.assertTrue(CollectionUtils.isNotEmpty(travellers));
	}

	/**
	 * Method to test get travelers by passengerTypeCode, last name text and current customer
	 */
	@Test
	public void testFindTravellerBySurName()
	{
		final List<TravellerModel> travellers = travellerDao.findSavedTravellersUsingSurnameText("asd", "adult", customer);
		Assert.assertTrue(CollectionUtils.isNotEmpty(travellers));
	}

}
