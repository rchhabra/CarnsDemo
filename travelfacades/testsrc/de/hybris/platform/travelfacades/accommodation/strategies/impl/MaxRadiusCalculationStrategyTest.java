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

package de.hybris.platform.travelfacades.accommodation.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * Unit tests for {@link MaxRadiusCalculationStrategy} implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MaxRadiusCalculationStrategyTest
{
	@InjectMocks
	private MaxRadiusCalculationStrategy maxRadiusCalculationStrategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private final String TEST_PLACE_TYPE_CODE_A = "TEST_PLACE_TYPE_CODE_A";
	private final String TEST_PLACE_TYPE_CODE_B = "TEST_PLACE_TYPE_CODE_B";
	private List<String> testPlaceTypes;

	@Before
	public void setUp()
	{
		testPlaceTypes = Arrays.asList(TEST_PLACE_TYPE_CODE_A, TEST_PLACE_TYPE_CODE_B);
		given(configurationService.getConfiguration()).willReturn(configuration);
		maxRadiusCalculationStrategy.setDefaultRadius(1000d);
	}

	@Test
	public void calculateTestUsingKnownPlaceTypes()
	{
		given(configuration.getDouble(Matchers.contains(TEST_PLACE_TYPE_CODE_A))).willReturn(200d);
		given(configuration.getDouble(Matchers.contains(TEST_PLACE_TYPE_CODE_B))).willReturn(400d);
		Assert.assertEquals(400d, maxRadiusCalculationStrategy.calculateRadius(testPlaceTypes).doubleValue(), 0.001);
	}

	@Test
	public void calculateTestUsingUnKnownPlaceTypes()
	{
		given(configuration.getDouble(Matchers.any())).willThrow(new NoSuchElementException());
		Assert.assertEquals(1000d, maxRadiusCalculationStrategy.calculateRadius(testPlaceTypes).doubleValue(), 0.001);
	}

	@Test
	public void calculateRadiusTestForNoPlaceTypes()
	{
		given(configuration.getDouble(Matchers.any())).willThrow(new NoSuchElementException());
		Assert.assertEquals(0d, maxRadiusCalculationStrategy.calculateRadius(Collections.emptyList()).doubleValue(), 0.001);
	}
}
