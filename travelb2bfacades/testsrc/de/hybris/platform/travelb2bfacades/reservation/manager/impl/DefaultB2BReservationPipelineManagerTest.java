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

package de.hybris.platform.travelb2bfacades.reservation.manager.impl;

import static org.mockito.Mockito.doNothing;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelb2bfacades.reservation.handlers.impl.DefaultB2BReservationBasicHandler;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultB2BReservationPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BReservationPipelineManagerTest
{
	@InjectMocks
	DefaultB2BReservationPipelineManager defaultB2BReservationPipelineManager;

	@Mock
	DefaultB2BReservationBasicHandler b2bReservationBasicHandler;

	@Mock
	private GlobalTravelReservationPipelineManager globalTravelReservationPipelineManager;

	@Test
	public void test()
	{
		defaultB2BReservationPipelineManager.setHandlers(Arrays.asList(b2bReservationBasicHandler));

		doNothing().when(globalTravelReservationPipelineManager).executePipeline(Matchers.any(AbstractOrderModel.class),
				Matchers.any());
		doNothing().when(b2bReservationBasicHandler).handle(Matchers.any(AbstractOrderModel.class), Matchers.any());

		Assert.assertNotNull(defaultB2BReservationPipelineManager.executePipeline(new AbstractOrderModel()));
	}

}
