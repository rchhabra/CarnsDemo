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

package de.hybris.platform.travelfacades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.DocumentType;
import de.hybris.platform.travelservices.enums.ReasonForTravel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link PassengerInformationDataPopulator}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PassengerInformationDataPopulatorTest
{

	@InjectMocks
	PassengerInformationDataPopulator passengerInformationDataPopulator;
	@Mock
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private Converter<TitleModel, TitleData> titleConverter;
	@Mock
	private Converter<CountryModel, CountryData> countryConverter;

	@Test
	public void testPopulatePassengerInfoData()
	{
		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();

		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setCode("airline");

		passengerInformationModel.setPassengerType(passengerTypeModel);
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode("testTitle");
		passengerInformationModel.setTitle(titleModel);
		passengerInformationModel.setFirstName("testFristName");
		passengerInformationModel.setSurname("testSurname");
		passengerInformationModel.setDateOfBirth(new Date());
		passengerInformationModel.setMembershipNumber("123");
		passengerInformationModel.setGender("M");
		passengerInformationModel.setReasonForTravel(ReasonForTravel.BUSINESS);
		passengerInformationModel.setDocumentNumber("123456");
		passengerInformationModel.setDocumentType(DocumentType.PASSPORT);
		passengerInformationModel.setAPISType("APISTYPE");
		passengerInformationModel.setCountryOfIssue(new CountryModel());
		passengerInformationModel.setNationality(new CountryModel());
		passengerInformationModel.setEmail("TEST_EMAIL");

		given(enumerationService.getEnumerationValue(Matchers.eq(ReasonForTravel.class), Matchers.eq("Business")))
				.willReturn(ReasonForTravel.BUSINESS);

		given(countryConverter.convert(Matchers.any(CountryModel.class))).willReturn(null);

		final PassengerInformationData passengerInfoData = new PassengerInformationData();

		passengerInformationDataPopulator.populate(passengerInformationModel, passengerInfoData);

		Assert.assertEquals("testFristName", passengerInfoData.getFirstName());
	}

	@Test
	public void populateTest()
	{
		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();

		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setCode("airline");

		passengerInformationModel.setPassengerType(passengerTypeModel);
		passengerInformationModel.setFirstName("testFristName");
		passengerInformationModel.setSurname("testSurname");
		passengerInformationModel.setDateOfBirth(new Date());
		passengerInformationModel.setMembershipNumber("123");
		passengerInformationModel.setGender("M");

		final PassengerInformationData passengerInfoData = new PassengerInformationData();

		passengerInformationDataPopulator.populate(passengerInformationModel, passengerInfoData);

		Assert.assertEquals("testFristName", passengerInfoData.getFirstName());
	}
}
