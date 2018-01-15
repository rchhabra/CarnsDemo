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
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.ExtraServiceRestrictionHandler;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;



/**
 * Unit Test for the implementation of {@link DefaultAccommodationServicePipelineManager}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationServicePipelineManagerTest
{
	@InjectMocks
	DefaultAccommodationServicePipelineManager defaultAccommodationServicePipelineManager;
	@Mock
	ExtraServiceRestrictionHandler extraServiceRestrictionHandler;


	@Before
	public void setUp()
	{
		defaultAccommodationServicePipelineManager.setHandlers(Arrays.asList(extraServiceRestrictionHandler));
	}

	@Test
	public void testExecutePipeline()
	{
		Mockito.doNothing().when(extraServiceRestrictionHandler).handle(Matchers.any(ProductModel.class),
				Matchers.any(ReservedRoomStayData.class), Matchers.any(ServiceData.class),
				Matchers.any(AccommodationReservationData.class));
		Assert.assertNotNull(defaultAccommodationServicePipelineManager.executePipeline(null, null, null));
	}

	@Test
	public void testExecutePipelineWithAccommodationPipelineException()
	{
		Mockito.doThrow(new AccommodationPipelineException("TEST_EXCEPTION")).when(extraServiceRestrictionHandler).handle(
				Matchers.any(ProductModel.class),
				Matchers.any(ReservedRoomStayData.class), Matchers.any(ServiceData.class),
				Matchers.any(AccommodationReservationData.class));
		Assert.assertNull(defaultAccommodationServicePipelineManager.executePipeline(null, null, null));
	}
}
