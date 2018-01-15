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
import de.hybris.platform.travelservices.dao.SpecialServiceRequestDao;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;

import org.apache.commons.lang.StringUtils;
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
 * Unit Test for the DefaultSpecialServiceRequestService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSpecialServiceRequestServiceTest
{

	@InjectMocks
	private DefaultSpecialServiceRequestService specialServiceRequestService;
	@Mock
	private SpecialServiceRequestDao specialServiceRequestDao;

	@Test
	public void getSpecialServiceRequestTest()
	{
		final SpecialServiceRequestModel specialServicerequest = new SpecialServiceRequestModel();
		Mockito.when(specialServiceRequestDao.findSpecialServiceRequest(Matchers.any())).thenReturn(specialServicerequest);

		final SpecialServiceRequestModel result = specialServiceRequestService.getSpecialServiceRequest("code1");
		Assert.assertNotNull(result);
	}

	@Test
	public void getSpecialServiceRequestWithEmptyCodeTest()
	{
		final SpecialServiceRequestModel result = specialServiceRequestService.getSpecialServiceRequest(StringUtils.EMPTY);
		Assert.assertNull(result);
	}
}
