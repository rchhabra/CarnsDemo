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

package de.hybris.platform.travelfulfilmentprocess.jobs;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

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
 * unit test for {@link CleanUpFraudOrderJob}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CleanUpFraudOrderJobTest
{
	@InjectMocks
	private CleanUpFraudOrderJob cleanUpFraudOrderJob;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	SearchResult<Object> searchResult;

	@Before
	public void setUp()
	{
		final BusinessProcessModel bpm = new BusinessProcessModel();
		bpm.setCode("TEST_CODE");
		final List<Object> processes = new ArrayList<>(3);
		processes.add(bpm);
		Mockito.doNothing().when(businessProcessService).triggerEvent(Matchers.anyString());
		given(searchResult.getResult()).willReturn(processes);
		given(flexibleSearchService.search(Matchers.any(FlexibleSearchQuery.class))).willReturn(searchResult);
	}

	@Test
	public void performTest()
	{
		final CronJobModel cronJob = new CronJobModel();
		final PerformResult result = cleanUpFraudOrderJob.perform(cronJob);
		Assert.assertEquals(result.getResult(), CronJobResult.SUCCESS);
	}
}
