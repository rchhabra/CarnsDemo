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
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.enums.TransportOfferingType;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportOfferingPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingPopulatorTest
{
	@InjectMocks
	TransportOfferingPopulator transportOfferingPopulator;
	@Mock
	private Converter<TravelSectorModel, TravelSectorData> travelSectorConverter;
	@Mock
	private Converter<TransportVehicleModel, TransportVehicleData> transportVehicleConverter;
	@Mock
	TransportOfferingUtils transportOfferingUtils;

	@Test
	public void test()
	{
		final TransportOfferingModel toModel = Mockito.mock(TransportOfferingModel.class);
		transportOfferingPopulator.setTravelSectorConverter(travelSectorConverter);

		Mockito.when(toModel.getCode()).thenReturn("testCode");
		Mockito.when(toModel.getNumber()).thenReturn("123");
		Mockito.when(toModel.getDepartureTime()).thenReturn(new Date());
		Mockito.when(toModel.getArrivalTime()).thenReturn(new Date());
		Mockito.when(toModel.getType()).thenReturn(TransportOfferingType.FLIGHT);
		Mockito.when(toModel.getDuration()).thenReturn(5L);

		final PointOfServiceModel pos = new PointOfServiceModel();
		pos.setTimeZoneId(ZoneId.systemDefault().toString());
		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		transportFacilityModel.setPointOfService(Arrays.asList(pos));
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		travelSectorModel.setOrigin(transportFacilityModel);
		travelSectorModel.setDestination(transportFacilityModel);
		toModel.setTravelSector(travelSectorModel);
		Mockito.when(toModel.getTravelSector()).thenReturn(travelSectorModel);
		given(travelSectorConverter.convert(toModel.getTravelSector())).willReturn(new TravelSectorData());

		final TransportVehicleModel transportVehicleModel = new TransportVehicleModel();
		toModel.setTransportVehicle(transportVehicleModel);
		Mockito.when(toModel.getTransportVehicle()).thenReturn(transportVehicleModel);
		given(transportVehicleConverter.convert(toModel.getTransportVehicle())).willReturn(new TransportVehicleData());
		final TransportOfferingData toData = new TransportOfferingData();
		transportOfferingPopulator.populate(toModel, toData);
		Assert.assertEquals("123", toData.getNumber());


	}
}
