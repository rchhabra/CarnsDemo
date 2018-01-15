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
 */

package de.hybris.platform.ndcfacades.airshopping.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAirShoppingFacadeTest
{
	@InjectMocks
	DefaultAirShoppingFacade defaultAirShoppingFacade;

	@Mock
	FareSearchFacade fareSearchFacade;

	@Mock
	Converter<AirShoppingRQ, FareSearchRequestData> ndcFareSearchRequestConverter;

	@Mock
	Converter<FareSelectionData, AirShoppingRS> ndcFareSelectionDataConverter;

	@Test
	public void testDoSearch()
	{
		final AirShoppingRQ airShoppingRQ = new AirShoppingRQ();
		final AirShoppingRS airShoppingRS = defaultAirShoppingFacade.doSearch(airShoppingRQ);
		Assert.assertNotNull(airShoppingRS);
	}
}
