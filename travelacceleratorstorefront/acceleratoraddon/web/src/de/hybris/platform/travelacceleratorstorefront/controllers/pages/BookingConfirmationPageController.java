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
package de.hybris.platform.travelacceleratorstorefront.controllers.pages;

import de.hybris.platform.acceleratorfacades.flow.impl.SessionOverrideCheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * BookingConfirmationPageController
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/checkout")
public class BookingConfirmationPageController extends AbstractCheckoutController
{
	private static final String BOOKING_CONFIRMATION_REFERENCE = "bookingConfirmationReference";
	private static final String BOOKING_REFERENCE = "bookingReference";
	protected static final Logger LOG = Logger.getLogger(BookingConfirmationPageController.class);
	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String BOOKING_REFERENCE_PATH_VARIABLE_PATTERN = "{bookingReference:.*}";

	private static final String CHECKOUT_BOOKING_CONFIRMATION_CMS_PAGE_LABEL = "bookingConfirmation";

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	/**
	 * @param exception
	 * @param request
	 * @return String
	 */
	@ExceptionHandler(ModelNotFoundException.class)
	public String handleModelNotFoundException(final ModelNotFoundException exception, final HttpServletRequest request)
	{
		request.setAttribute("message", exception.getMessage());
		return FORWARD_PREFIX + "/404";
	}

	/**
	 * @param bookingReference
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/bookingConfirmation/" + BOOKING_REFERENCE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String orderConfirmation(@PathVariable(BOOKING_REFERENCE) final String bookingReference, final Model model)
			throws CMSItemNotFoundException
	{
		if (!bookingFacade.validateUserForCheckout(bookingReference))
		{
			return REDIRECT_PREFIX + "/";
		}
		SessionOverrideCheckoutFlowFacade.resetSessionOverrides();

		sessionService.setAttribute(BOOKING_CONFIRMATION_REFERENCE, bookingReference);

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}

		model.addAttribute(TravelacceleratorstorefrontWebConstants.AMEND, bookingFacade.isAmendment(bookingReference));

		final AbstractPageModel cmsPage = getContentPageForLabelOrId(CHECKOUT_BOOKING_CONFIRMATION_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, cmsPage);
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_BOOKING_CONFIRMATION_CMS_PAGE_LABEL));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.Order.OrderConfirmationPage;
	}

	@ModelAttribute("disableCurrencySelector")
	public Boolean getDisableCurrencySelector()
	{
		return Boolean.TRUE;
	}
}
