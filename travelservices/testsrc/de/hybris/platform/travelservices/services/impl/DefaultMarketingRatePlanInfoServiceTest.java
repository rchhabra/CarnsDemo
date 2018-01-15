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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.dao.MarketingRatePlanInfoDao;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultMarketingRatePlanInfoService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketingRatePlanInfoServiceTest
{
	@InjectMocks
	private DefaultMarketingRatePlanInfoService marketRatePlanInfoService;
	@Mock
	private MarketingRatePlanInfoDao marketingRatePlanInfoDao;

	@Test
	public void getMarketingRatePlanInfoForCodeTest()
	{
		final MarketingRatePlanInfoModel marketRatePlanInfo = new MarketingRatePlanInfoModel();
		Mockito.when(marketingRatePlanInfoDao.findMarketingRatePlanInfo(Matchers.anyString())).thenReturn(marketRatePlanInfo);

		final MarketingRatePlanInfoModel result = marketRatePlanInfoService.getMarketingRatePlanInfoForCode("marketRatePlan");
		Assert.assertNotNull(result);
	}
}
