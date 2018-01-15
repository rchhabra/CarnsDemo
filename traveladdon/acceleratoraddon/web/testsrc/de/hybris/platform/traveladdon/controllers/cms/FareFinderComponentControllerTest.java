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
 */
package de.hybris.platform.traveladdon.controllers.cms;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.traveladdon.validators.FareFinderValidator;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;


/**
 * Unit tests for the FareFinderComponentController using MockMvc
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FareFinderComponentControllerTest
{

	@InjectMocks
	private FareFinderComponentController controller = new FareFinderComponentController();

	@Mock
	private CabinClassFacade cabinClassFacade;

	@Mock
	private PassengerTypeFacade passengerTypeFacade;

	@Mock
	private FareFinderValidator fareFinderValidator;

	private MockMvc mockMvc = null;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void fillModelTest() throws Exception
	{

		final Model model = new ExtendedModelMap();

		Mockito.when(cabinClassFacade.getCabinClasses()).thenReturn(getCabinClassDataSampleData());
		Mockito.when(passengerTypeFacade.getPassengerTypes()).thenReturn(getPassengerTypeSampleData());

		controller.fillModel(null, model, null);

		Assert.assertTrue(model.asMap().containsKey("adultPassengerType"));
		Assert.assertTrue(model.asMap().containsKey("passengerTypes"));
		Assert.assertEquals(2, ((ArrayList<PassengerTypeData>) model.asMap().get("passengerTypes")).size());
	}

	@Test
	public void postSearchWithErrorsTest() throws Exception
	{

		mockMvc.perform(MockMvcRequestBuilders
				.post("/view/FareFinderComponentController/search"))
				.andExpect(MockMvcResultMatchers.view().name("redirect:/"));
	}

	@Test
	public void postSearchWithoutErrorsTest() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.post("/view/FareFinderComponentController/search")
				.param("travelingTo", "London")
				.param("travelingFrom", "New York")
				.param("departingDateTime", "01/01/2016")
				.param("returningDateTime", "10/01/2016")
				.param("tripType", "economy")
				.param("numberOfAdults", "2")
				.param("numberOfChildren", "1")
				.param("numberOfInfants", "0")
				.param("cabinClass", "roundtrip")
				.param("travelingWithChildren", "true"))
				.andExpect(MockMvcResultMatchers.view().name("/fare-search"));

	}

	/**
	 * Method that returns a list of CabinClassData sample data
	 *
	 * @return List<CabinClassData>
	 */
	private List<CabinClassData> getCabinClassDataSampleData()
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

		final List<CabinClassData> ccData = new ArrayList<>();
		ccData.add(economy);
		ccData.add(firstClass);
		ccData.add(businessClass);

		return ccData;
	}

	/**
	 * Method that returns a list of PassengerType sample data
	 *
	 * @return List<PassengerType>
	 */
	private List<PassengerTypeData> getPassengerTypeSampleData()
	{

		final PassengerTypeData adult = new PassengerTypeData();
		adult.setIdentifier("adult");
		adult.setPassengerType("Adult");

		final PassengerTypeData children = new PassengerTypeData();
		children.setIdentifier("children");
		children.setPassengerType("Children");

		final PassengerTypeData infant = new PassengerTypeData();
		infant.setIdentifier("infant");
		infant.setPassengerType("Infant");

		final List<PassengerTypeData> passengerTypes = new ArrayList<>();
		passengerTypes.add(adult);
		passengerTypes.add(children);
		passengerTypes.add(infant);

		return passengerTypes;
	}
}
