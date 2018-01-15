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

package de.hybris.platform.traveladdon.controllers.cms;

import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.model.components.TransportSummaryComponentModel;
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


@Controller("TransportSummaryComponentController")
@RequestMapping(value = TraveladdonControllerConstants.Actions.Cms.TransportSummaryComponent)
public class TransportSummaryComponentController extends SubstitutingCMSAddOnComponentController<TransportSummaryComponentModel>
{
	private static final Logger LOG = Logger.getLogger(TransportSummaryComponentController.class);

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final TransportSummaryComponentModel component)
	{
		final ReservationData reservationData = reservationFacade.getCurrentReservationSummary();
		model.addAttribute(TraveladdonWebConstants.RESERVATION, reservationData);
		model.addAttribute(TraveladdonWebConstants.DATE_FORMAT_LABEL, TraveladdonWebConstants.DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.TIME_FORMAT_LABEL, TraveladdonWebConstants.TIME_FORMAT);
	}

	/**
	 * This method is responsible for refreshing the component after an operation is performed on the cart
	 *
	 * @param componentUid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	protected String getComponent(@RequestParam final String componentUid, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		request.setAttribute(COMPONENT_UID, componentUid);
		try
		{
			return handleGet(request, response, model);
		}
		catch (final Exception e)
		{
			LOG.error("Exception loading the component", e);
		}
		return StringUtils.EMPTY;
	}
}
