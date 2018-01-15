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
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelservices.enums.ReasonForTravel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link PassengerBasicDetailsReversePopulator}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PassengerBasicDetailsReversePopulatorTest
{
	@InjectMocks
	PassengerBasicDetailsReversePopulator passengerBasicDetailsReversePopulator;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private UserService userService;

	@Test
	public void testPopulatePassengerInformationDataForNull()
	{
		final PassengerInformationData passengerInformationData = createPassengerInformationData();
		passengerInformationData.setTitle(null);
		passengerInformationData.setReasonForTravel(null);

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();

		passengerBasicDetailsReversePopulator.populate(passengerInformationData, passengerInformationModel);
		Assert.assertEquals("testFirstName", passengerInformationModel.getFirstName());
	}

	@Test
	public void testPopulatePassengerInformationDataForNullTitleCode()
	{
		final PassengerInformationData passengerInformationData = createPassengerInformationData();
		passengerInformationData.setTitle(new TitleData());
		passengerInformationData.setReasonForTravel(null);

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();

		passengerBasicDetailsReversePopulator.populate(passengerInformationData, passengerInformationModel);
		Assert.assertEquals("testFirstName", passengerInformationModel.getFirstName());
	}

	@Test
	public void testPopulatePassengerInformationData()
	{
		given(enumerationService.getEnumerationValue(Matchers.eq(ReasonForTravel.class), Matchers.eq("Business")))
				.willReturn(ReasonForTravel.BUSINESS);
		given(userService.getTitleForCode("TITLE_CODE")).willReturn(new TitleModel());

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		final PassengerInformationData passengerInformationData = createPassengerInformationData();
		passengerBasicDetailsReversePopulator.populate(passengerInformationData, passengerInformationModel);
		Assert.assertEquals("testFirstName", passengerInformationModel.getFirstName());
	}

	private PassengerInformationData createPassengerInformationData()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("airline");

		final TitleData title = new TitleData();
		title.setCode("TITLE_CODE");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setFirstName("testFirstName");
		passengerInformationData.setSurname("testSurname");
		passengerInformationData.setReasonForTravel("Business");
		passengerInformationData.setPassengerType(passengerTypeData);
		passengerInformationData.setTitle(title);

		return passengerInformationData;
	}
}
