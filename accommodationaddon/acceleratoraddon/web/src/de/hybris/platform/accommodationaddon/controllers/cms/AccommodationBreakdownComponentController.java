/*
 *
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

package de.hybris.platform.accommodationaddon.controllers.cms;

import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.model.components.AccommodationBreakdownComponentModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.travelfacades.facades.BookingFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Accommodation Breakdown Component
 */
@Controller("AccommodationBreakdownComponentController")
@RequestMapping(value = AccommodationaddonControllerConstants.Actions.Cms.AccommodationBreakdownComponent)
public class AccommodationBreakdownComponentController
		extends SubstitutingCMSAddOnComponentController<AccommodationBreakdownComponentModel>
{
	private static final String RESERVATION = "reservation";

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AccommodationBreakdownComponentModel component)
	{
		final String bookingReference = sessionService.getAttribute("bookingConfirmationReference");
		model.addAttribute(RESERVATION, bookingFacade.getFullAccommodationBookingForAmendOrder(bookingReference));
	}
}
