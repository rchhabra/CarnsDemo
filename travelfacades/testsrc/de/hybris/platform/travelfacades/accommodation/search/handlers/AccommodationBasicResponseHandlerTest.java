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

package de.hybris.platform.travelfacades.accommodation.search.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.AccommodationBasicResponseHandler;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationBasicResponseHandlerTest
{
	AccommodationBasicResponseHandler handler = new AccommodationBasicResponseHandler();

	@Test
	public void testHandler()
	{
		final AccommodationSearchRequestData request = new AccommodationSearchRequestData();
		final AccommodationSearchResponseData response = new AccommodationSearchResponseData();
		final CriterionData criterion = new CriterionData();
		request.setCriterion(criterion);

		handler.handle(Collections.emptyList(), request, response);

		Assert.assertNotNull(response.getCriterion());
	}
}
