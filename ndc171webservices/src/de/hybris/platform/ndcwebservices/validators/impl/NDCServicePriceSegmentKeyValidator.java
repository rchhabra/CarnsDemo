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

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;

import org.apache.commons.collections.CollectionUtils;


/**
 * Concrete class to validate Segment key for {@link ServicePriceRQ}
 */
public class NDCServicePriceSegmentKeyValidator extends NDCOffersSegmentKeyValidator<ServicePriceRQ>
{
	@Override
	public void validate(final ServicePriceRQ servicePriceRQ, final ErrorsType errorsType)
	{
		if (CollectionUtils.isNotEmpty(servicePriceRQ.getQuery().getOriginDestination()))
		{
			servicePriceRQ.getQuery().getOriginDestination()
					.forEach(originDestination -> originDestination.getFlight().forEach(flight -> {
						final boolean validSegmentkey = validateSegmentKey(flight.getSegmentKey(), errorsType);
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
					}));
		}
	}
}
