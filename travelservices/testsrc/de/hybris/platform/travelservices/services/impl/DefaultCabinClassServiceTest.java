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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.dao.CabinClassDao;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultCabinClassService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCabinClassServiceTest
{

	@Mock
	private CabinClassDao cabinClassDao;

	@InjectMocks
	private final DefaultCabinClassService cabinClassServiceService = new DefaultCabinClassService();

	@Test
	public void getCabinClassesTest()
	{

		// setup sample data

		final CabinClassModel economy = new CabinClassModel();
		economy.setCode("economy");
		economy.setName("Economy", Locale.ENGLISH);

		final CabinClassModel firstClass = new CabinClassModel();
		firstClass.setCode("firstClass");
		firstClass.setName("First Class", Locale.ENGLISH);

		final CabinClassModel businessClass = new CabinClassModel();
		businessClass.setCode("businessClass");
		businessClass.setName("Business Class", Locale.ENGLISH);

		final List<CabinClassModel> cabinClasses = new ArrayList<>();
		cabinClasses.add(economy);
		cabinClasses.add(firstClass);
		cabinClasses.add(businessClass);

		// setup mock scenario

		Mockito.when(cabinClassDao.findCabinClasses()).thenReturn(cabinClasses);

		// perform test

		final List<CabinClassModel> result = cabinClassServiceService.getCabinClasses();

		// evaluate results

		Assert.assertNotNull(result);
		Assert.assertEquals(cabinClasses.size(), result.size());
	}

	@Test
	public void getCabinClassTest()
	{
		final CabinClassModel economyCabinClass = new CabinClassModel();
		economyCabinClass.setCode("economy");
		economyCabinClass.setName("Economy", Locale.ENGLISH);
		Mockito.when(cabinClassDao.findCabinClass("economy")).thenReturn(economyCabinClass);

		final CabinClassModel result = cabinClassServiceService.getCabinClass("economy");

		// evaluate results

		Assert.assertNotNull(result);
		Assert.assertEquals("economy", result.getCode());
	}

	@Test
	public void getCabinClassUsingCabinIndexTest()
	{
		final CabinClassModel economyCabinClass = new CabinClassModel();
		economyCabinClass.setCode("economy");
		economyCabinClass.setName("Economy", Locale.ENGLISH);
		Mockito.when(cabinClassDao.findCabinClass(0)).thenReturn(economyCabinClass);

		final CabinClassModel result = cabinClassServiceService.getCabinClass(0);

		// evaluate results

		Assert.assertNotNull(result);
		Assert.assertEquals("economy", result.getCode());
	}

}
