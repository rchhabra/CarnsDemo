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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.core.PK;
import de.hybris.platform.travelservices.enums.RoomPreferenceType;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for RoomPreferencePopulator implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomPreferencePopulatorTest
{
	@InjectMocks
	RoomPreferencePopulator<RoomPreferenceModel, RoomPreferenceData> roomPreferencePopulator;

	@Test
	public void populateTest()
	{
		final RoomPreferenceModel source = Mockito.mock(RoomPreferenceModel.class);
		given(source.getPk()).willReturn(PK.fromLong(00001l));
		given(source.getPreferenceType()).willReturn(RoomPreferenceType.BED_PREFERENCE);
		final RoomPreferenceData target = new RoomPreferenceData();
		roomPreferencePopulator.populate(source, target);

		Assert.assertEquals(PK.fromLong(00001l).toString(), target.getCode());
	}
}
