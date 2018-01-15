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
package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SimpleUIDAttributeHandlerTest
{
	@InjectMocks
	private SimpleUIDAttributeHandler simpleUIDAttributeHandler;

	@Test
	public void testGetNullUID()
	{
		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid(null);
		Assert.assertEquals("", simpleUIDAttributeHandler.get(travellerModel));
	}

	@Test
	public void testGetSimpleUIDWithNoPrefix()
	{
		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid("1_QBU3HVKWI26JAB3TD3ZI");
		PassengerInformationModel info = new PassengerInformationModel();
		info.setFirstName("John");
		travellerModel.setInfo(info);
		Assert.assertEquals("john_1QBU3H", simpleUIDAttributeHandler.get(travellerModel));
	}

	@Test
	public void testGetSimpleUIDWithPrefix()
	{
		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid("00000001_1_QBU3HVKWI26JAB3TD3ZI");
		PassengerInformationModel info = new PassengerInformationModel();
		info.setFirstName("John");
		travellerModel.setInfo(info);
		Assert.assertEquals("john_1QBU3H", simpleUIDAttributeHandler.get(travellerModel));
	}

}
