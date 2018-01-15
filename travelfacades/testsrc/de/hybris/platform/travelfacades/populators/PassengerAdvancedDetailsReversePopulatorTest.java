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
 *
 */

package de.hybris.platform.travelfacades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit Test for the implementation of {@link PassengerAdvancedDetailsReversePopulator}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PassengerAdvancedDetailsReversePopulatorTest
{
	@InjectMocks
	PassengerAdvancedDetailsReversePopulator passengerAdvancedDetailsReversePopulator;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private PassengerTypeService passengerTypeService;

	private final String TEST_ISO_CODE = "TEST_ISO_CODE";

	@Test
	public void testPopulatePassengerInformationDataWithNullValues()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("airline");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setPassengerType(passengerTypeData);
		passengerInformationData.setDocumentNumber("testDocument");

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		passengerAdvancedDetailsReversePopulator.populate(passengerInformationData, passengerInformationModel);
		Assert.assertEquals("testDocument", passengerInformationModel.getDocumentNumber());
	}

	@Test
	public void testPopulatePassengerInformationData()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("airline");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setPassengerType(passengerTypeData);
		passengerInformationData.setDocumentNumber("testDocument");
		final CountryData countryData = new CountryData();
		countryData.setIsocode(TEST_ISO_CODE);
		passengerInformationData.setCountryOfIssue(countryData);
		passengerInformationData.setNationality(countryData);
		given(commonI18NService.getCountry(TEST_ISO_CODE)).willReturn(null);
		
		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setCode("Test");
		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		passengerInformationModel.setPassengerType(passengerTypeModel);
		passengerAdvancedDetailsReversePopulator.populate(passengerInformationData, passengerInformationModel);
		Assert.assertEquals("testDocument", passengerInformationModel.getDocumentNumber());
	}
}
