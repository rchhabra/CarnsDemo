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
 */

package de.hybris.platform.accommodationaddon.controllers.cms;

import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.model.components.AccommodationReservationComponentModel;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.travelfacades.facades.ReservationFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for Accommodation Reservation Component
 */
@Controller("AccommodationReservationComponentController")
@RequestMapping(value = AccommodationaddonControllerConstants.Actions.Cms.AccommodationReservationComponent)
public class AccommodationReservationComponentController
		extends SubstitutingCMSAddOnComponentController<AccommodationReservationComponentModel>
{
	private static final Logger LOGGER = Logger.getLogger(AccommodationReservationComponentController.class);
	private static final String ACCOMMODATION_RESERVATION_DATA = "accommodationReservationData";

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AccommodationReservationComponentModel component)
	{
		// Model is only populated when this component is shown
	}

	/**
	 * This method is responsible for populating accommodation reservation component after see full reservation button is clicked
	 *
	 * @param componentUid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/load", method = RequestMethod.GET)
	protected String getComponent(@RequestParam final String componentUid, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		request.setAttribute(COMPONENT_UID, componentUid);
		try
		{
			final String view = handleGet(request, response, model);
			populateModel(model);
			return view;
		}
		catch (final Exception e)
		{
			LOGGER.error("Exception loading the component", e);
		}
		return StringUtils.EMPTY;
	}

	protected void populateModel(final Model model)
	{
		model.addAttribute(ACCOMMODATION_RESERVATION_DATA, reservationFacade.getCurrentAccommodationReservation());
	}
}
