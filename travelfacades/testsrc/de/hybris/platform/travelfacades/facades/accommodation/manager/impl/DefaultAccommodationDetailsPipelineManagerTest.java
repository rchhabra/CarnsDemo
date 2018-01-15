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

package de.hybris.platform.travelfacades.facades.accommodation.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.RoomRatesHandler;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationDetailsPipelineManager}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationDetailsPipelineManagerTest
{
	@InjectMocks
	DefaultAccommodationDetailsPipelineManager defaultAccommodationDetailsPipelineManager;

	@Mock
	RoomRatesHandler roomRatesHandler;

	@Before
	public void setUp()
	{
		defaultAccommodationDetailsPipelineManager.setHandlers(Arrays.asList(roomRatesHandler));
	}

	@Test
	public void testExecutePipeline()
	{
		Mockito.doNothing().when(roomRatesHandler).handle(Matchers.any(AccommodationAvailabilityRequestData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class));
		Assert.assertNotNull(defaultAccommodationDetailsPipelineManager.executePipeline(null));
	}
}
