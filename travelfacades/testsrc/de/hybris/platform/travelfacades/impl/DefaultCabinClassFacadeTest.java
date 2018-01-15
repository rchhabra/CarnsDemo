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

package de.hybris.platform.travelfacades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultCabinClassFacade;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.services.impl.DefaultCabinClassService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the implementation of {@link CabinClassFacade}
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCabinClassFacadeTest
{

	@InjectMocks
	private final DefaultCabinClassFacade cabinClassFacade = new DefaultCabinClassFacade();

	@Mock
	private DefaultCabinClassService cabinClassService;

	@Mock
	private Converter<CabinClassModel, CabinClassData> cabinClassConverter;

	private final List<CabinClassModel> ccModels = new ArrayList<CabinClassModel>();

	private final List<CabinClassData> ccData = new ArrayList<CabinClassData>();

	@Before
	public void setup()
	{
		setupCabinClassModelSampleData(ccModels);
		setupCabinClassDataSampleData(ccData);
	}

	@Test
	public void getAllCabinClassesTest()
	{

		Mockito.when(cabinClassService.getCabinClasses()).thenReturn(ccModels);
		Mockito.when(Converters.convertAll(ccModels, cabinClassConverter)).thenReturn(ccData);

		final List<CabinClassData> result = cabinClassFacade.getCabinClasses();

		Assert.assertNotNull(result);
		Assert.assertEquals(ccModels.size(), result.size());
	}

	/**
	 * Sample data containing a list of CabinClassModel
	 * 
	 * @param ccModels
	 */
	private void setupCabinClassModelSampleData(final List<CabinClassModel> ccModels)
	{

		final CabinClassModel economy = new CabinClassModel();
		economy.setCode("economy");
		economy.setName("Economy", Locale.ENGLISH);

		final CabinClassModel firstClass = new CabinClassModel();
		firstClass.setCode("firstClass");
		firstClass.setName("First Class", Locale.ENGLISH);

		final CabinClassModel businessClass = new CabinClassModel();
		businessClass.setCode("businessClass");
		businessClass.setName("Business Class", Locale.ENGLISH);

		ccModels.add(economy);
		ccModels.add(firstClass);
		ccModels.add(businessClass);
	}

	/**
	 * Sample data containing a list of CabinClassData
	 * 
	 * @param ccData
	 */
	private void setupCabinClassDataSampleData(final List<CabinClassData> ccData)
	{

		final CabinClassData economy = new CabinClassData();
		economy.setCode("economy");
		economy.setName("Economy");

		final CabinClassData firstClass = new CabinClassData();
		firstClass.setCode("firstClass");
		firstClass.setName("First Class");

		final CabinClassData businessClass = new CabinClassData();
		businessClass.setCode("businessClass");
		businessClass.setName("Business Class");

		ccData.add(economy);
		ccData.add(firstClass);
		ccData.add(businessClass);
	}

}
