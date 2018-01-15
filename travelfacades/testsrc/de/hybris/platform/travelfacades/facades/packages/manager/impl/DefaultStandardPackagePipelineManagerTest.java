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
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.travelfacades.facades.packages.handlers.impl.PackageProductStockHandler;

import java.util.Arrays;

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
 * unit test for {@link DefaultStandardPackagePipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStandardPackagePipelineManagerTest
{
	@InjectMocks
	DefaultStandardPackagePipelineManager defaultStandardPackagePipelineManager;

	@Mock
	PackageProductStockHandler packageProductStockHandler;

	@Before
	public void setUp()
	{
		defaultStandardPackagePipelineManager.setHandlers(Arrays.asList(packageProductStockHandler));
		Mockito.doNothing().when(packageProductStockHandler).handle(Matchers.any(BundleTemplateData.class), Matchers.anyList());
	}

	@Test
	public void testExecutePipeline()
	{
		Assert.assertNotNull(defaultStandardPackagePipelineManager.executePipeline(new BundleTemplateData()));
	}
}
