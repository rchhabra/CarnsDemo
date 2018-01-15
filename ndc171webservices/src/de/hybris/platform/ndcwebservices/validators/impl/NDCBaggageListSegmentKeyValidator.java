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

package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageListRQ.Query.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;

import java.util.List;


/**
 * Concrete class to validate Segment key for {@link BaggageListRQ}
 */
public class NDCBaggageListSegmentKeyValidator extends NDCOffersSegmentKeyValidator<BaggageListRQ>
{
	@Override
	public void validate(final BaggageListRQ baggageListRQ, final ErrorsType errorsType)
	{
		final List<OriginDestination> originDestinations = baggageListRQ.getQuery().getOriginDestination();
		for (final OriginDestination originDestination : originDestinations)
		{
			final boolean validSegmentkey = originDestination.getFlight().stream()
					.allMatch(flight -> validateSegmentKey(flight.getSegmentKey(), errorsType));
			if (!validSegmentkey)
			{
				return;
			}

			final String originDestinationKey = originDestination.getOriginDestinationKey();
			final boolean validOriginDestinationKey = validateOriginDestinationKey(originDestinationKey,
					originDestination.getFlight(), errorsType);
			if (!validOriginDestinationKey)
			{
				return;
			}
		}
	}
}
