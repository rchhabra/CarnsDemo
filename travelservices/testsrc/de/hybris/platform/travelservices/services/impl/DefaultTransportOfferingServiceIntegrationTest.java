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
package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Integration Test for the CabinClassDao implementation using ServicelayerTransactionalTest
 */
@IntegrationTest
public class DefaultTransportOfferingServiceIntegrationTest extends ServicelayerTransactionalTest
{

	/** The class being tested gets injected here */
	@Resource
	private TransportOfferingService transportOfferingService;

	/** Platform's ModelService used for creation of test data. */
	@Resource
	private ModelService modelService;

	private final String number = "8323";

	@Test
	@Ignore
	public void getTranportOfferingsByNumberAndDepartureDateTest() throws ParseException
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		final Date departureDate = TravelDateUtils.convertStringDateToDate(formatter.format(DateUtils.addDays(new Date(), 1)),
				TravelservicesConstants.DATE_PATTERN);

		// has correct number and date
		final List<TransportOfferingModel> results = transportOfferingService.getTransportOfferings(number, departureDate);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getTranportOfferingsNullNumber() throws ParseException
	{
		final Date departureDate = DateUtils.parseDate("2015/12/07", new String[]
		{ "yyyy/MM/dd" });

		// number is null
		transportOfferingService.getTransportOfferings(null, departureDate);
	}

	@Test
	public void getTranportOfferingsEmptyNumber() throws ParseException
	{
		final Date departureDate = DateUtils.parseDate("2015/12/07", new String[]
		{ "yyyy/MM/dd" });

		// number is empty
		final List<TransportOfferingModel> results = transportOfferingService.getTransportOfferings("", departureDate);
		Assert.assertTrue(results.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getTranportOfferingsNullDate()
	{
		// date is null
		transportOfferingService.getTransportOfferings(number, null);
	}

}
