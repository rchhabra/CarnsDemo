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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.services.CabinClassService;


/**
 * 
 * Integration Test for the CabinClassService implementation using ServicelayerTransactionalTest
 *
 */

@IntegrationTest
public class DefaultCabinClassServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private CabinClassService cabinClassService;

	@Resource
	private ModelService modelService;

	private List<CabinClassModel> ccModels;

	@Before
	public void setUp()
	{

		ccModels = new ArrayList<>();

		final CabinClassModel economy = new CabinClassModel();
		economy.setCode("economyClassCabin");
		economy.setName("Economy Class Cabin", Locale.ENGLISH);

		final CabinClassModel firstclass = new CabinClassModel();
		firstclass.setCode("firstclasscabin");
		firstclass.setName("First Class Cabin", Locale.ENGLISH);

		final CabinClassModel businessclass = new CabinClassModel();
		businessclass.setCode("businessClassCabin");
		businessclass.setName("Business Cabin Class", Locale.ENGLISH);

		final CabinClassModel vipclass = new CabinClassModel();
		vipclass.setCode("vipCabinClass");
		vipclass.setName("VIP Cabin Class", Locale.ENGLISH);

		ccModels.add(economy);
		ccModels.add(firstclass);
		ccModels.add(businessclass);
		ccModels.add(vipclass);
	}

	@Test
	public void getCabinClassesTest()
	{

		// get list of current cabin classes from the database

		final List<CabinClassModel> cabinClasses = cabinClassService.getCabinClasses();

		Assert.assertNotNull(cabinClasses);

		// save out new sample data

		modelService.saveAll(ccModels);

		// get an update list of cabin classes which should contain the sample data

		final List<CabinClassModel> newCabinClasses = cabinClassService.getCabinClasses();

		// evaluate results

		Assert.assertNotNull(newCabinClasses);
		Assert.assertTrue(!newCabinClasses.isEmpty());

		Assert.assertEquals(ccModels.size(), (newCabinClasses.size() - cabinClasses.size()));
		Assert.assertTrue(newCabinClasses.containsAll(ccModels));

	}

}
