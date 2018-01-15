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

import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRQ.Query.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * The Concrete class that validates origin destination for {@link BaggageAllowanceRQ}
 */
public class NDCBaggageAllowanceOriginDestinationValidator extends NDCOriginDestinationValidator<BaggageAllowanceRQ>
{
	@Override
	public void validate(final BaggageAllowanceRQ baggageAllowanceRQ, final ErrorsType errorsType)
	{
		final List<OriginDestination> originDestinations = baggageAllowanceRQ.getQuery().getOriginDestination();
		if (!validateFlightNumber(originDestinations.size(), errorsType))
		{
			return;
		}
		validateODKey(originDestinations, errorsType);
	}

	/**
	 * This method validates OriginDestinationKey for every {@link OriginDestination}, add error if it is not present.
	 *
	 * @param originDestinations
	 * 		the origin destinations
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateODKey(final List<OriginDestination> originDestinations, final ErrorsType errorsType)
	{
		if (originDestinations.stream().anyMatch(originDestination -> StringUtils.isEmpty(originDestination.getOriginDestinationKey())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_ORIGIN_DESTINATION_KEY));
			return false;
		}
		return true;
	}
}
