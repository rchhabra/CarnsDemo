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

package de.hybris.platform.travelbackofficeservices.cronjob;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * The type Update locations to transport offering job test.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateLocationsToTransportOfferingJobTest
{
	/**
	 * The Cron job.
	 */
	@InjectMocks
	UpdateLocationsToTransportOfferingJob cronJob;

	@Mock
	private BackofficeTransportOfferingService transportOfferingService;

	@Mock
	private ModelService modelService;

	/**
	 * Sets up.
	 */
	@Before
	public void setUp()
	{
		cronJob.setBackofficeTransportOfferingService(transportOfferingService);
	}

	/**
	 * Test update locations with null sector.
	 */
	@Test
	public void testUpdateLocationsWithNullSector()
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		given(transportOfferingService.getTransportOfferings()).willReturn(Arrays.asList(transportOffering));
		Mockito.doNothing().when(transportOfferingService).updateTransportOfferingsWithLocations(Arrays.asList(transportOffering));
		cronJob.perform(Mockito.mock(CronJobModel.class));
		Assert.assertSame(CronJobStatus.FINISHED, cronJob.perform(Mockito.mock(CronJobModel.class)).getStatus());
		Assert.assertSame(CronJobResult.SUCCESS, cronJob.perform(Mockito.mock(CronJobModel.class)).getResult());
	}

}
