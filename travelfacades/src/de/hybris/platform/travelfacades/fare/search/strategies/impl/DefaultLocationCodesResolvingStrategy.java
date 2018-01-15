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

package de.hybris.platform.travelfacades.fare.search.strategies.impl;

import de.hybris.platform.travelfacades.fare.search.strategies.LocationCodesResolvingStrategy;

import java.util.Collections;
import java.util.List;


/**
 * This implementation just returns a singleton list not performing any kind of logic
 */
public class DefaultLocationCodesResolvingStrategy implements LocationCodesResolvingStrategy
{

	@Override
	public List<String> getLocationCodes(final String locationParameter)
	{
		return Collections.singletonList(locationParameter);
	}

}
