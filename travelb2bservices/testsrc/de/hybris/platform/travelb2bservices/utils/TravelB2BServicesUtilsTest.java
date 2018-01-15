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

package de.hybris.platform.travelb2bservices.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.travelb2bservices.constants.Travelb2bservicesConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * unit test for {@link TravelB2BServicesUtils}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelB2BServicesUtilsTest
{
	private final String b2bUnitCode = "TEST_BTB_UNIT_CODE";
	private final String email = "TEST_EMAIL";
	private final Date date = new Date();
	private final String costCenterUid = "TEST_COST_CENTER_UID";
	private final CurrencyModel currency = new CurrencyModel();

	@Test
	public void test()
	{
		final Map<String, Object> queryParams=new HashMap<>();
		final String query = StringUtils.EMPTY;
		final String result = TravelB2BServicesUtils.populateQueryParameters(email, date, date, query, costCenterUid, currency,
				queryParams);

		Assert.assertTrue(StringUtils.contains(result, Travelb2bservicesConstants.USER_RESTRICTIONS));
		Assert.assertTrue(StringUtils.contains(result, Travelb2bservicesConstants.FROM_DATE_RESTRICTION));
		Assert.assertTrue(StringUtils.contains(result, Travelb2bservicesConstants.TO_DATE_RESTRICTION));
		Assert.assertTrue(StringUtils.contains(result, Travelb2bservicesConstants.COST_CENTER_RESTRICTION));
		Assert.assertTrue(StringUtils.contains(result, Travelb2bservicesConstants.CURRENCY_RESTRICTION));

		Assert.assertEquals(email, queryParams.get("userId"));
		Assert.assertEquals(date, queryParams.get("fromDate"));
		Assert.assertEquals(date, queryParams.get("toDate"));
		Assert.assertEquals(costCenterUid, queryParams.get("costCenterId"));
		Assert.assertEquals(currency, queryParams.get("currency"));

	}

	@Test
	public void testForEmptyArguments()
	{
		final Map<String, Object> queryParams = new HashMap<>();
		final String query = StringUtils.EMPTY;
		final String result = TravelB2BServicesUtils.populateQueryParameters(StringUtils.EMPTY, null, null, query,
				StringUtils.EMPTY, null,
				queryParams);

		Assert.assertFalse(StringUtils.contains(result, Travelb2bservicesConstants.USER_RESTRICTIONS));
		Assert.assertFalse(StringUtils.contains(result, Travelb2bservicesConstants.FROM_DATE_RESTRICTION));
		Assert.assertFalse(StringUtils.contains(result, Travelb2bservicesConstants.TO_DATE_RESTRICTION));
		Assert.assertFalse(StringUtils.contains(result, Travelb2bservicesConstants.COST_CENTER_RESTRICTION));
		Assert.assertFalse(StringUtils.contains(result, Travelb2bservicesConstants.CURRENCY_RESTRICTION));

		Assert.assertNotEquals(email, queryParams.get("userId"));
		Assert.assertNotEquals(date, queryParams.get("fromDate"));
		Assert.assertNotEquals(date, queryParams.get("toDate"));
		Assert.assertNotEquals(costCenterUid, queryParams.get("costCenterId"));
		Assert.assertNotEquals(currency, queryParams.get("currency"));

	}
}
