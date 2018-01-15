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

package de.hybris.platform.travelfacades.ancillary.search.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers.SelectedAccommodationHandler;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultAncillarySearchPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAncillarySearchPipelineManagerTest
{
	@InjectMocks
	DefaultAncillarySearchPipelineManager defaultAncillarySearchPipelineManager;

	@Mock
	SelectedAccommodationHandler selectedAccommodationHandler;

	@Test
	public void testExecutePipeline()
	{
		Mockito.doNothing().when(selectedAccommodationHandler).handle(Matchers.any(OfferRequestData.class),
				Matchers.any(OfferResponseData.class));

		defaultAncillarySearchPipelineManager.setHandlers(Arrays.asList(selectedAccommodationHandler));

		Assert.assertNotNull(defaultAncillarySearchPipelineManager.executePipeline(new OfferRequestData()));
	}
}
