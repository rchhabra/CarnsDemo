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
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerInfoData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.TravellerInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravellerDataPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravellerDataPopulatorTest
{
	@InjectMocks
	TravellerDataPopulator travellerDataPopulator;
	@Mock
	private Converter<PassengerInformationModel, PassengerInformationData> passengerInformationDataConverter;
	@Mock
	private Converter<SpecialRequestDetailModel, SpecialRequestDetailData> specialRequestDetailsConverter;

	@Test
	public void testPopulateTravellerData()
	{
		final TravellerModel travellerModel = Mockito.mock(TravellerModel.class);

		Mockito.when(travellerModel.getLabel()).thenReturn("testCode");
		Mockito.when(travellerModel.getUid()).thenReturn("testUid");
		Mockito.when(travellerModel.getSimpleUID()).thenReturn("734IEH");
		Mockito.when(travellerModel.getVersionID()).thenReturn("000000001");
		Mockito.when(travellerModel.getBooker()).thenReturn(Boolean.TRUE);
		Mockito.when(travellerModel.getType()).thenReturn(TravellerType.PASSENGER);

		final TravellerInfoModel passengerInfoModel = new PassengerInformationModel();
		Mockito.when(travellerModel.getInfo()).thenReturn(passengerInfoModel);

		final TravellerInfoData travellerInfoData = new PassengerInformationData();
		given(passengerInformationDataConverter.convert(Matchers.any(PassengerInformationModel.class)))
				.willReturn((PassengerInformationData) travellerInfoData);

		final SpecialRequestDetailModel srDetailModel = new SpecialRequestDetailModel();
		Mockito.when(travellerModel.getSpecialRequestDetail()).thenReturn(srDetailModel);

		final SpecialRequestDetailData srDetailData = new SpecialRequestDetailData();
		given(specialRequestDetailsConverter.convert(Matchers.any(SpecialRequestDetailModel.class))).willReturn(srDetailData);

		Mockito.when(travellerModel.getSavedTravellerUid()).thenReturn("testSavedTravellerUid");

		final TravellerData trData = new TravellerData();
		travellerDataPopulator.populate(travellerModel, trData);

		Assert.assertEquals("testCode", trData.getLabel());

	}
}
