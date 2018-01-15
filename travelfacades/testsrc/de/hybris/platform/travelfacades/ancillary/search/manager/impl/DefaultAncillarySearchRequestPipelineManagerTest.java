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
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.OriginDestinationOffersRequestHandler;

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
 * unit test for {@link DefaultAncillarySearchRequestPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAncillarySearchRequestPipelineManagerTest
{

	@InjectMocks
	DefaultAncillarySearchRequestPipelineManager defaultAncillarySearchRequestPipelineManager;

	@Mock
	OriginDestinationOffersRequestHandler originDestinationOffersRequestHandler;

	@Test
	public void testExecutePipeline()
	{
		Mockito.doNothing().when(originDestinationOffersRequestHandler).handle(Matchers.any(ReservationData.class),
				Matchers.any(OfferRequestData.class));

		defaultAncillarySearchRequestPipelineManager.setHandlers(Arrays.asList(originDestinationOffersRequestHandler));

		Assert.assertNotNull(defaultAncillarySearchRequestPipelineManager.executePipeline(new ReservationData()));
	}
}
