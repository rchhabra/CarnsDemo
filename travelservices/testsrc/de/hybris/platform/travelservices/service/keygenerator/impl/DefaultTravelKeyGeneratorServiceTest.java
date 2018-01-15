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

package de.hybris.platform.travelservices.service.keygenerator.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultTravelKeyGeneratorService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelKeyGeneratorServiceTest
{
	@InjectMocks
	DefaultTravelKeyGeneratorService defaultTravelKeyGeneratorService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);

	}

	@Test
	public void testGenerateTravellerUidForEmptyScenarios()
	{
		when(configuration.getInt(Matchers.anyString())).thenReturn(0);

		Assert.assertNotNull(defaultTravelKeyGeneratorService.generateTravellerUid(StringUtils.EMPTY, StringUtils.EMPTY));
	}

	@Test
	public void testGenerateTravellerUid()
	{
		when(configuration.getInt(Matchers.anyString())).thenReturn(10);
		Assert.assertTrue(StringUtils
				.contains(defaultTravelKeyGeneratorService.generateTravellerUid("TEST_PREFIX", "TEST_PASSENGER_NUM"), "TEST_PREFIX"));
	}

	@Test
	public void testGenerateAccommodationRequestCode()
	{
		when(configuration.getInt(Matchers.anyString())).thenReturn(10);
		Assert.assertTrue(
				StringUtils.contains(defaultTravelKeyGeneratorService.generateAccommodationRequestCode(2, "TEST_BOOKING_REFERENCE"),
						"TEST_BOOKING_REFERENCE"));
	}

}
