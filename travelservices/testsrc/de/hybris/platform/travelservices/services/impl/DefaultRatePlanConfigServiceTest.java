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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.dao.RatePlanConfigDao;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultRatePlanConfigService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRatePlanConfigServiceTest
{
	@Mock
	private RatePlanConfigDao ratePlanConfigDao;

	@InjectMocks
	private DefaultRatePlanConfigService ratePlanConfigService;

	@Test
	public void getRatePlanConfigForCodeTest()
	{
		final RatePlanConfigModel ratePlan = new RatePlanConfigModel();
		Mockito.when(ratePlanConfigDao.findRatePlanConfig(Matchers.anyString())).thenReturn(ratePlan);

		final RatePlanConfigModel result = ratePlanConfigService.getRatePlanConfigForCode("ratePlan");
		Assert.assertNotNull(result);
	}
}
