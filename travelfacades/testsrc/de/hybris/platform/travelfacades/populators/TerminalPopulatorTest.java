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
import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.travelservices.model.travel.TerminalModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TerminalPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TerminalPopulatorTest
{
	@InjectMocks
	TerminalPopulator terminalPopulator;

	@Test
	public void testPopulateTerminalData()
	{
		final TerminalModel terminalModel = new TerminalModel()
		{
			@Override
			public String getName()
			{
				return "testName";
			}

			@Override
			public String getCode()
			{
				return "testCode";
			}
		};

		final TerminalData terminalData = new TerminalData();
		terminalPopulator.populate(terminalModel, terminalData);
		Assert.assertEquals("testName", terminalData.getName());
	}
}
