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

package de.hybris.platform.travelfacades.facades.accommodation.search.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.AccommodationSearchHandler;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.AccommodationBasicResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test cases for implementation of {@link DefaultAccommodationOfferingSearchPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationOfferingSearchPipelineManagerTest
{
	@InjectMocks
	DefaultAccommodationOfferingSearchPipelineManager defaultAccommodationOfferingSearchPipelineManager;

	private List<AccommodationSearchHandler> handlers;

	@Before
	public void setUp()
	{
		handlers = new ArrayList<>();
		defaultAccommodationOfferingSearchPipelineManager.setHandlers(Collections.emptyList());
	}

	@Test
	public void test()
	{
		final AccommodationSearchResponseData response = defaultAccommodationOfferingSearchPipelineManager
				.executePipeline(Collections.emptyList(), null);

		Assert.assertNotNull(response);
	}

	@Test
	public void testWithHandlers()
	{
		handlers.add(new AccommodationBasicResponseHandler());
		defaultAccommodationOfferingSearchPipelineManager.setHandlers(handlers);
		final AccommodationSearchRequestData accommodationSearchRequest = new AccommodationSearchRequestData();
		accommodationSearchRequest.setCriterion(new CriterionData());
		final AccommodationSearchResponseData response = defaultAccommodationOfferingSearchPipelineManager
				.executePipeline(Collections.emptyList(), accommodationSearchRequest);

		Assert.assertNotNull(response);
	}
}
