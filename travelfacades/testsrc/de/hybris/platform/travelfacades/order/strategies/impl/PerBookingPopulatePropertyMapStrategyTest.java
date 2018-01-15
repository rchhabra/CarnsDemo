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

package de.hybris.platform.travelfacades.order.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.travelservices.enums.AmendStatus;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link PerBookingPopulatePropertyMapStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PerBookingPopulatePropertyMapStrategyTest
{
	@InjectMocks
	PerBookingPopulatePropertyMapStrategy perBookingPopulatePropertyMapStrategy;

	@Test
	public void testPopulatePropertiesMap()
	{
		final Map<String, Object> results = perBookingPopulatePropertyMapStrategy.populatePropertiesMap(null, 0, null, null,
				Boolean.TRUE, AmendStatus.NEW);

		Assert.assertEquals(AmendStatus.NEW, results.get(AbstractOrderEntryModel.AMENDSTATUS));
	}
}
