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
import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TerminalModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportOfferingTerminalPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingTerminalPopulatorTest
{
	@InjectMocks
	TransportOfferingTerminalPopulator transportOfferingTerminalPopulator;
	@Mock
	private Converter<TerminalModel, TerminalData> terminalConverter;

	@Test
	public void testPopulateTransportOfferingTerminal()
	{
		final TransportOfferingModel toModel = Mockito.mock(TransportOfferingModel.class);
		final TerminalModel originTerminalModel = new TerminalModel();
		final TerminalModel destinationTerminalModel = new TerminalModel();
		Mockito.when(toModel.getOriginTerminal()).thenReturn(originTerminalModel);
		Mockito.when(toModel.getDestinationTerminal()).thenReturn(destinationTerminalModel);
		final TerminalData terminalData = new TerminalData();
		given(terminalConverter.convert(Matchers.any(TerminalModel.class))).willReturn(terminalData);
		final TransportOfferingData toData = new TransportOfferingData();
		transportOfferingTerminalPopulator.populate(toModel, toData);
		Assert.assertNotNull(toData.getOriginTerminal());
		Assert.assertNotNull(toData.getDestinationTerminal());
	}
}
