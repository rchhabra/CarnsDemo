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

package de.hybris.platform.traveladdon.controllers.cms;

import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.model.components.TransportBookingListComponentModel;
import de.hybris.platform.travelfacades.facades.BookingListFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Transport Booking List Controller for handling requests for My Booking Section in My Account Page.
 */
@Controller("TransportBookingListComponentController")
@RequestMapping(value = TraveladdonControllerConstants.Actions.Cms.TransportBookingListComponent)
public class TransportBookingListComponentController
		extends SubstitutingCMSAddOnComponentController<TransportBookingListComponentModel>
{
	private static final int MAX_PAGE_LIMIT = 100;

	@Resource(name = "bookingListFacade")
	private BookingListFacade bookingListFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final TransportBookingListComponentModel component)
	{
		int pageSize = getConfigurationService().getConfiguration().getInt(TraveladdonWebConstants.MY_BOOKINGS_PAGE_SIZE);
		pageSize = pageSize > 0 ? pageSize : MAX_PAGE_LIMIT;
		final List<ReservationData> myBookings = bookingListFacade.getCurrentCustomerBookings();
		model.addAttribute(TraveladdonWebConstants.MY_ACCOUNT_BOOKING, myBookings);
		model.addAttribute(TraveladdonWebConstants.DATE_PATTERN, TravelservicesConstants.DATE_PATTERN);
		model.addAttribute(TraveladdonWebConstants.PAGE_SIZE, pageSize);
	}
}
