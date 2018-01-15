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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultDecodeSavedSearchStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDecodeSavedSearchStrategyTest
{
	@InjectMocks
	DefaultDecodeSavedSearchStrategy defaultDecodeSavedSearchStrategy;

	@Test
	public void testGetEncodedDataMap()
	{
		final String testEncodedSearch = "key1=value1|key2=value2 ";

		Assert.assertEquals("value1", defaultDecodeSavedSearchStrategy.getEncodedDataMap(testEncodedSearch).get("key1"));
	}
}
