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

package de.hybris.platform.travelacceleratorstorefront.security.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test class for DefaultManageMyBookingSessionCleanUpStrategy.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultManageMyBookingSessionCleanUpStrategyTest
{
	@InjectMocks
	private DefaultManageMyBookingSessionCleanUpStrategy classToTest;

	@Mock
	private SessionService sessionService;

	@Mock
	private HttpServletRequest request;

	@BeforeClass
	public void setUp()
	{
		classToTest = new DefaultManageMyBookingSessionCleanUpStrategy();
		classToTest.setManageMyBookingUrlPattern(Pattern.compile("(^https://.*/manage-booking/.*)"));
	}

	@Test
	public void testManageMyBookingCleanUp_mmbFlow()
	{
		given(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_AUTHENTICATION))
				.willReturn(Boolean.TRUE);
		given(request.getRequestURL().toString()).willReturn("https://yacceleratorstorefront/manage-booking/booking-details");

		classToTest.manageMyBookingCleanUp(request);

		Mockito.verify(sessionService);
		Mockito.verify(request);
	}

}
