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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.travelfacades.facades.packages.handlers.impl.PackagePriceHandler;

import java.util.Collections;

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
 * unit test for {@link DefaultPackageSearchResponsePipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPackageSearchResponsePipelineManagerTest
{
	@InjectMocks
	DefaultPackageSearchResponsePipelineManager defaultPackageSearchResponsePipelineManager;

	@Mock
	PackagePriceHandler packagePriceHandler;

	@Before
	public void setUp()
	{
		defaultPackageSearchResponsePipelineManager.setHandlers(Collections.singletonList(packagePriceHandler));
		Mockito.doNothing().when(packagePriceHandler).handle(Matchers.anyList(), Matchers.any(PackageSearchRequestData.class),
				Matchers.any(PackageSearchResponseData.class));
	}

	@Test
	public void testExecutePipeline()
	{
		Assert.assertNotNull(defaultPackageSearchResponsePipelineManager
				.executePipeline(Collections.singletonList(new AccommodationOfferingDayRateData()), new PackageSearchRequestData()));
	}
}
