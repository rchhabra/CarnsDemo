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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpgradeBasicResponseHandlerTest
{
	@InjectMocks
	private UpgradeBasicResponseHandler handler;

	@Test
	public void testHanle()
	{
		final FareSearchRequestData request = new FareSearchRequestData();
		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		request.setOriginDestinationInfo(Stream.of(originDestinationInfo).collect(Collectors.toList()));
		final FareSelectionData fareSelectionData = new FareSelectionData();
		handler.handle(Collections.emptyList(), request, fareSelectionData);
		assertTrue(Objects.nonNull(fareSelectionData.getPricedItineraries()));
	}
}
