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
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.dao.CabinClassDao;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 *
 * Integration Test for the CabinClassDao implementation using ServicelayerTransactionalTest
 *
 */

@IntegrationTest
public class DefaultCabinClassDaoIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private CabinClassDao cabinClassDao;

	@Resource
	private ModelService modelService;

	@Test
	public void getAllCabinClassesTest()
	{
		// get current number cabin classes items in the database
		final int previousNumOfCabins = cabinClassDao.findCabinClasses().size();

		final CabinClassModel vipCabinClass = new CabinClassModel();
		vipCabinClass.setCode("vipCabinClass");
		vipCabinClass.setName("VIP Cabin Class", Locale.ENGLISH);

		modelService.save(vipCabinClass);

		// get an update list of cabin classes which should contain the sample data
		final List<CabinClassModel> cabinClasses = cabinClassDao.findCabinClasses();

		// evaluate results
		Assert.assertNotNull(cabinClasses);
		Assert.assertTrue(!cabinClasses.isEmpty());
		Assert.assertEquals(1, (cabinClasses.size() - previousNumOfCabins));
	}

	@Test
	public void getCabinClassForCodeTest()
	{
		final CabinClassModel businessCabinClass = new CabinClassModel();
		businessCabinClass.setCode("businessCabinClass");
		businessCabinClass.setName("Business Cabin Class", Locale.ENGLISH);

		// save new sample data
		modelService.save(businessCabinClass);

		// get an update list of cabin classes which should contain the sample data
		final CabinClassModel cabinClasses = cabinClassDao.findCabinClass(businessCabinClass.getCode());

		// evaluate results
		Assert.assertNotNull(cabinClasses);
		Assert.assertEquals(businessCabinClass.getCode(), cabinClasses.getCode());
	}

	@Test
	public void noCabinClassForCodeTest()
	{
		// get an update list of cabin classes which should contain the sample data
		final CabinClassModel cabinClasses = cabinClassDao.findCabinClass("testCabinClass");

		// evaluate results
		Assert.assertNull(cabinClasses);
	}

	@Test
	public void ambiguousCabinClassFoundForCodeTest()
	{
		final CabinClassModel classA = new CabinClassModel();
		classA.setCode("classA");

		final CabinClassModel classB = new CabinClassModel();
		classB.setCode("classA");

		// save new sample data
		modelService.save(classA);
		modelService.save(classB);

		// get an update list of cabin classes which should contain the sample data
		final CabinClassModel cabinClasses = cabinClassDao.findCabinClass("classA");

		// evaluate results
		Assert.assertNull(cabinClasses);
	}
}
