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
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.travelfacades.facades.packages.handlers.impl.PackageConfiguredReservedRoomStaysHandler;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultPackageReservedRoomStaysPipelineManager}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPackageReservedRoomStaysPipelineManagerTest
{
	@InjectMocks
	DefaultPackageReservedRoomStaysPipelineManager defaultPackageReservedRoomStaysPipelineManager;

	@Mock
	PackageConfiguredReservedRoomStaysHandler packageConfiguredReservedRoomStaysHandler;

	@Test
	public void testExecutePipeline()
	{
		defaultPackageReservedRoomStaysPipelineManager
				.setHandlers(Collections.singletonList(packageConfiguredReservedRoomStaysHandler));

		Mockito.doNothing().when(packageConfiguredReservedRoomStaysHandler).handle(Matchers.any(PackageRequestData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class));

		defaultPackageReservedRoomStaysPipelineManager.executePipeline(new PackageRequestData(),
				new AccommodationAvailabilityResponseData());
		Mockito.verify(packageConfiguredReservedRoomStaysHandler, Mockito.times(1)).handle(Matchers.any(PackageRequestData.class),
				Matchers.any(AccommodationAvailabilityResponseData.class));
	}

}
