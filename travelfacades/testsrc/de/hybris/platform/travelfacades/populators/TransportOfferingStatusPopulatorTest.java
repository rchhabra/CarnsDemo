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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

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
public class TransportOfferingStatusPopulatorTest
{
	@InjectMocks
	TransportOfferingStatusPopulator transportOfferingStatusPopulator;

	@Mock
	private EnumerationService enumerationService;

	@Test
	public void testPopulateTransportOfferingStatus()
	{
		final TransportOfferingModel toModel = Mockito.mock(TransportOfferingModel.class);
		Mockito.when(toModel.getUpdatedDepartureTime()).thenReturn(new Date());
		Mockito.when(toModel.getStatus()).thenReturn(TransportOfferingStatus.DELAYED);
		final TransportOfferingData toData = new TransportOfferingData();
		transportOfferingStatusPopulator.populate(toModel, toData);
		Assert.assertEquals(TransportOfferingStatus.DELAYED.toString(), toData.getStatus());
	}
}
