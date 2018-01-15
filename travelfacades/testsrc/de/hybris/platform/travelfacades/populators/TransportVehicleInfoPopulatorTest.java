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
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TransportVehicleInfoPopulator}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportVehicleInfoPopulatorTest
{
	@InjectMocks
	private TransportVehicleInfoPopulator transportVehicleInfoPopulator;

	private final String TEST_TRANSPORT_VEHICLE_CODE = "TEST_TRANSPORT_VEHICLE_CODE";

	@Test
	public void populateTest()
	{
		final TransportVehicleInfoModel source = Mockito.mock(TransportVehicleInfoModel.class);
		final TransportVehicleInfoData target = new TransportVehicleInfoData();
		given(source.getCode()).willReturn(TEST_TRANSPORT_VEHICLE_CODE);
		transportVehicleInfoPopulator.populate(source, target);
		Assert.assertEquals(TEST_TRANSPORT_VEHICLE_CODE, target.getCode());
	}
}
