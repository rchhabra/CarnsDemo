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

package de.hybris.platform.travelfacades.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravellerUtilsTest
{
	@Mock
	private PassengerTypeFacade passengerTypeFacade;

	@Test
	public void testPopulateTravellersNamesMap()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PassengerTypeData adultPax = testDataSetUp.createPassengerTypeData("Adult");
		final PassengerTypeData childPax = testDataSetUp.createPassengerTypeData("Child");
		final PassengerTypeData infantPax = testDataSetUp.createPassengerTypeData("Infant");

		final List<PassengerTypeData> paxTypes = Stream.of(adultPax, childPax, infantPax).collect(Collectors.toList());

		final PassengerInformationData paxInfoDataAdult = testDataSetUp.createPassengerInformation("Adult");
		final TravellerData traveller1 = testDataSetUp.createTravellerData("1234", paxInfoDataAdult);

		final PassengerInformationData paxInfoDataChild = testDataSetUp.createPassengerInformation("Adult");
		final TravellerData traveller2 = testDataSetUp.createTravellerData("5678", paxInfoDataChild);

		given(passengerTypeFacade.getPassengerTypes()).willReturn(paxTypes);

		final Map<String, Map<String, String>> paxTypeMap = TravellerUtils
				.populateTravellersNamesMap(Stream.of(traveller1, traveller2).collect(Collectors.toList()),
				passengerTypeFacade);

		assertFalse(paxTypeMap.isEmpty());
		assertNotNull(paxTypeMap.get("Adult").get("1234"));
		assertEquals("Adult 1", paxTypeMap.get("Adult").get("1234"));
	}

	@Test
	public void testPopulateTravellersNamesMapWithNonEmptyNames()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PassengerTypeData adultPax = testDataSetUp.createPassengerTypeData("Adult");
		final PassengerTypeData childPax = testDataSetUp.createPassengerTypeData("Child");
		final PassengerTypeData infantPax = testDataSetUp.createPassengerTypeData("Infant");

		final List<PassengerTypeData> paxTypes = Stream.of(adultPax, childPax, infantPax).collect(Collectors.toList());

		final PassengerInformationData paxInfoDataAdult = testDataSetUp.createPassengerInformationWithNames("Adult", "firstName1",
				"lastName1");
		final TravellerData traveller1 = testDataSetUp.createTravellerData("1234", paxInfoDataAdult);

		final PassengerInformationData paxInfoDataChild = testDataSetUp.createPassengerInformationWithNames("Adult", "firstName2",
				"lastName2");
		final TravellerData traveller2 = testDataSetUp.createTravellerData("5678", paxInfoDataChild);

		given(passengerTypeFacade.getPassengerTypes()).willReturn(paxTypes);

		final Map<String, Map<String, String>> paxTypeMap = TravellerUtils
				.populateTravellersNamesMap(Stream.of(traveller1, traveller2).collect(Collectors.toList()), passengerTypeFacade);

		assertFalse(paxTypeMap.isEmpty());
		assertNotNull(paxTypeMap.get("Adult").get("1234"));
		assertEquals("firstName1 lastName1", paxTypeMap.get("Adult").get("1234"));
	}

	private class TestDataSetUp
	{
		private PassengerInformationData createPassengerInformationWithNames(final String paxType, final String firstName,
				final String surName)
		{
			final PassengerInformationData paxInfo = new PassengerInformationData();
			paxInfo.setFirstName(firstName);
			paxInfo.setSurname(surName);
			paxInfo.setPassengerType(createPassengerTypeData(paxType));
			return paxInfo;
		}

		private PassengerInformationData createPassengerInformation(final String paxType)
		{
			final PassengerInformationData paxInfo = new PassengerInformationData();
			paxInfo.setPassengerType(createPassengerTypeData(paxType));
			return paxInfo;
		}

		private PassengerTypeData createPassengerTypeData(final String code)
		{
			final PassengerTypeData paxType = new PassengerTypeData();
			paxType.setCode(code);
			paxType.setName(code);
			return paxType;
		}

		private TravellerData createTravellerData(final String code, final PassengerInformationData paxInfo)
		{
			final TravellerData traveller = new TravellerData();
			traveller.setLabel(code);
			traveller.setTravellerInfo(paxInfo);
			return traveller;
		}
	}

}
