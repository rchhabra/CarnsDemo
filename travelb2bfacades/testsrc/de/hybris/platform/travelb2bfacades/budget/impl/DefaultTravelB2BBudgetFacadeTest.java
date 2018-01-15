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

package de.hybris.platform.travelb2bfacades.budget.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelB2BBudgetFacadeTest
{
	@InjectMocks
	DefaultTravelB2BBudgetFacade defaultTravelB2BBudgetFacade;

	@Test
	public void testGetDateFromStringDate()
	{
		final Date dateRtrNull = defaultTravelB2BBudgetFacade.getDateFromStringDate("");
		Assert.assertNull(dateRtrNull);

		final Date dateRtr = defaultTravelB2BBudgetFacade.getDateFromStringDate("10/11/2016");
		Assert.assertEquals(dateRtr, getDate("10/11/2016"));
	}

	@Test
	public void testGetStringDateFromDate()
	{
		final String dateRtrNull = defaultTravelB2BBudgetFacade.getStringDateFromDate(null);
		Assert.assertEquals(dateRtrNull, "");

		final String dateRtr = defaultTravelB2BBudgetFacade.getStringDateFromDate(getDate("10/11/2016"));
		Assert.assertEquals(dateRtr, "10/11/2016");
	}

	protected Date getDate(final String date)
	{
		final SimpleDateFormat format = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		Date obj = null;
		try
		{
			obj = format.parse(date);
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}
		return obj;
	}

}
