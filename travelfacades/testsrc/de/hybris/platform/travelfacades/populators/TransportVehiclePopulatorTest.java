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
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportVehiclePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportVehiclePopulatorTest
{
	@InjectMocks
	TransportVehiclePopulator transportVehiclePopulator;
	@Mock
	private Converter<TransportVehicleInfoModel, TransportVehicleInfoData> transportVehicleInfoConverter;

	@Test
	public void testPopulateTransportVehicleInfoData()
	{
		final TransportVehicleModel tvModel = Mockito.mock(TransportVehicleModel.class);
		final TransportVehicleInfoModel tvInfoModel = new TransportVehicleInfoModel();
		Mockito.when(tvModel.getTransportVehicleInfo()).thenReturn(tvInfoModel);
		final TransportVehicleInfoData tvInfoData = new TransportVehicleInfoData();
		given(transportVehicleInfoConverter.convert(Matchers.any(TransportVehicleInfoModel.class))).willReturn(tvInfoData);
		final TransportVehicleData tvData = new TransportVehicleData();
		transportVehiclePopulator.populate(tvModel, tvData);
		Assert.assertNotNull(tvData.getVehicleInfo());
	}

	@Test
	public void testPopulateTransportVehicleInfoDataForNullVehicle()
	{
		final TransportVehicleModel tvModel = Mockito.mock(TransportVehicleModel.class);
		Mockito.when(tvModel.getTransportVehicleInfo()).thenReturn(null);
		final TransportVehicleData tvData = new TransportVehicleData();
		transportVehiclePopulator.populate(tvModel, tvData);
		Assert.assertNull(tvData.getVehicleInfo());
	}
}
