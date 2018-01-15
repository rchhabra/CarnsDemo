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

package de.hybris.platform.travelb2bfacades.budget.impl;

import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BBudgetFacade;
import de.hybris.platform.travelb2bfacades.budget.TravelB2BBudgetFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;

import org.apache.commons.lang.StringUtils;


/**
 * The type Default Travel B2B Budget facade.
 */
public class DefaultTravelB2BBudgetFacade extends DefaultB2BBudgetFacade implements TravelB2BBudgetFacade
{

	@Override
	public Date getDateFromStringDate(final String date)
	{
		if (StringUtils.isEmpty(date))
		{
			return null;
		}

		return TravelDateUtils.convertStringDateToDate(date, TravelservicesConstants.DATE_PATTERN);
	}

	@Override
	public String getStringDateFromDate(final Date date)
	{
		if (date == null)
		{
			return StringUtils.EMPTY;
		}

		return TravelDateUtils.convertDateToStringDate(date, TravelservicesConstants.DATE_PATTERN);
	}

}
