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

package de.hybris.platform.travelacceleratorstorefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.forms.AdditionalSecurityForm;
import de.hybris.platform.travelfacades.facades.ActionFacade;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller class to handle Booking details page. This page can be accessed from MyProfilePage or BookingConfirmation
 * page in which case the access url is "/manage-booking/**" and also from ManageMyBooking component, in which case the
 * url is "/checkout/manage-booking/**"
 */
@Controller
@RequestMapping("/manage-booking")
public class BookingDetailsPageController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(BookingDetailsPageController.class);

	private static final String BOOKING_DETAILS_CMS_PAGE = "bookingDetailsPage";
	private static final String ADDITIONAL_SECURITY_FORM = "AdditionalSecurityForm";
	private static final String LOGIN_STATUS = "loginStatus";
	private static final String LOGIN_OK = "OK";
	private static final String LOGIN_ERROR = "ERROR";
	private static final String LOGIN_ADDITIONAL_SECURITY = "ADDITIONAL_SECURITY";
	private static final String LAST_NAME = "lastName";
	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String BOOKING_REFERENCE = "bookingReference";
	private static final String ADDITIONAL_SECURITY = "additionalSecurity";
	private static final String PASSENGER_REFERENCE = "passengerReference";

	/**
	 * The constant BOOKING_DETAILS_USER_VALIDATION_ERROR.
	 */
	public static final String BOOKING_DETAILS_USER_VALIDATION_ERROR = "booking.details.user.validation.error";

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "actionFacade")
	private ActionFacade actionFacade;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@Resource(name = "i18nService")
	private I18NService i18nService;

	@Resource(name = "travelCustomerFacade")
	private TravelCustomerFacade travelCustomerFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	/**
	 * This method enables to retrieve a booking using the booking reference number.
	 *
	 * @param bookingReference
	 * 		a string representing bookingReferenceNumber
	 * @param model
	 * 		a Model Object
	 * @param redirectModel
	 * 		a RedirectAttributes object
	 * @return a string for a redirected page.
	 * @throws CMSItemNotFoundException
	 * 		the cms item not found exception
	 */
	@RequestMapping(value = "/booking-details/{bookingReference}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getManageBookingsPage(@PathVariable final String bookingReference, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		try
		{
			final boolean isValidUser = bookingFacade.validateUserForBooking(bookingReference);
			if (!isValidUser)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						BOOKING_DETAILS_USER_VALIDATION_ERROR);
				return REDIRECT_PREFIX + "/";
			}
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					TravelacceleratorstorefrontWebConstants.PAGE_NOT_AVAILABLE);
			return REDIRECT_PREFIX + "/";
		}

		model.addAttribute(ADDITIONAL_SECURITY, bookingFacade.isAdditionalSecurityActive(bookingReference));
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_REFERENCE, bookingReference);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_REFERENCE, bookingReference);
		storeCmsPageInModel(model, getContentPageForLabelOrId(BOOKING_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(BOOKING_DETAILS_CMS_PAGE));
		// model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return getViewForPage(model);
	}


	/**
	 * This method allows a traveller to login to bookingDetails page. Validates BookingReference and Last name, if
	 * success full retrieves booking and redirects to bookingDetails page, else redirects to home page with error
	 * message.
	 *
	 * @param mmbForm
	 * 		ManageMyBookingForm object.
	 * @param request
	 * 		HttpServletRequest
	 * @param response
	 * 		HttpServletResponse
	 * @param model
	 * 		the model
	 * @return a string for a redirected page.
	 * @throws CMSItemNotFoundException
	 * 		the cms item not found exception
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public String loginManageMyBooking(final AdditionalSecurityForm mmbForm, final HttpServletRequest request,
			final HttpServletResponse response, final Model model) throws CMSItemNotFoundException
	{
		getSessionService().removeAttribute(TravelservicesConstants.PASSENGER_REFERENCE);
		final GlobalTravelReservationData globalTravelReservationData = reservationFacade
				.retrieveGlobalReservationData(mmbForm.getBookingReference());

		model.addAttribute(ADDITIONAL_SECURITY_FORM, mmbForm);

		if (Objects.isNull(globalTravelReservationData))
		{
			model.addAttribute(LOGIN_STATUS, LOGIN_ERROR);
			model.addAttribute(ERROR_MESSAGE, "text.error.manage.booking.invalid.booking.reference.or.last.name");
			return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.additionalSecurityResponse;
		}

		final String emailId = bookingFacade
				.getBookerEmailID(globalTravelReservationData, mmbForm.getLastName(), mmbForm.getPassengerReference());

		if (Objects.isNull(emailId))
		{
			if (Objects.nonNull(globalTravelReservationData.getReservationData()) && globalTravelReservationData.getReservationData()
					.getAdditionalSecurity())
			{
				if (Objects.isNull(mmbForm.getPassengerReference()))
				{
					model.addAttribute(LOGIN_STATUS, LOGIN_ADDITIONAL_SECURITY);
					model.addAttribute(BOOKING_REFERENCE, mmbForm.getBookingReference());
					model.addAttribute(LAST_NAME, mmbForm.getLastName());
					return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.additionalSecurityResponse;
				}
				else
				{
					model.addAttribute(LOGIN_STATUS, LOGIN_ADDITIONAL_SECURITY);
					model.addAttribute(BOOKING_REFERENCE, mmbForm.getBookingReference());
					model.addAttribute(LAST_NAME, mmbForm.getLastName());
					model.addAttribute(PASSENGER_REFERENCE, mmbForm.getPassengerReference());
					model.addAttribute(ERROR_MESSAGE, "text.error.manage.booking.invalid.booking.reference.or.last.name.or.passenger.reference");
					return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.additionalSecurityResponse;
				}
			}
			else
			{
				model.addAttribute(LOGIN_STATUS, LOGIN_ERROR);
				model.addAttribute(ERROR_MESSAGE, "text.error.manage.booking.invalid.booking.reference.or.last.name");
				return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.additionalSecurityResponse;
			}
		}

		bookingFacade.mapOrderToUserAccount(mmbForm.getBookingReference());

		try
		{
			final String guid = travelCustomerFacade.createGuestCustomer(emailId, mmbForm.getLastName());
			getGuidCookieStrategy().setCookie(request, response);
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_GUEST_UID, guid);
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_AUTHENTICATION, Boolean.TRUE);
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.MANAGE_MY_BOOKING_BOOKING_REFERENCE,
					mmbForm.getBookingReference());
		}
		catch (NoSuchMessageException | DuplicateUidException e)
		{
			LOG.error("Error creating Anonymous customer for Booking Reference number: " + mmbForm.getBookingReference());
			model.addAttribute(LOGIN_STATUS, LOGIN_ERROR);
			model.addAttribute(ERROR_MESSAGE, "Sorry,Internal error, Please try again.");
			return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.additionalSecurityResponse;
		}

		if (StringUtils.isNotEmpty(mmbForm.getPassengerReference()))
		{
			getSessionService().setAttribute(TravelservicesConstants.PASSENGER_REFERENCE, mmbForm.getPassengerReference());
		}

		model.addAttribute(LOGIN_STATUS, LOGIN_OK);
		model.addAttribute(BOOKING_REFERENCE, mmbForm.getBookingReference());
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.BookingDetails.additionalSecurityResponse;
	}

	/**
	 * Gets disable currency selector.
	 *
	 * @return the disable currency selector
	 */
	@ModelAttribute("disableCurrencySelector")
	public Boolean getDisableCurrencySelector()
	{
		return Boolean.TRUE;
	}

	@Override
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 * 		the session service
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets guid cookie strategy.
	 *
	 * @return the guid cookie strategy
	 */
	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	/**
	 * Sets guid cookie strategy.
	 *
	 * @param guidCookieStrategy
	 * 		the guid cookie strategy
	 */
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	@Override
	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	/**
	 * Sets customer facade.
	 *
	 * @param customerFacade
	 * 		the customer facade
	 */
	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	@Override
	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	/**
	 * Sets message source.
	 *
	 * @param messageSource
	 * 		the message source
	 */
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	@Override
	protected I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * Sets i 18 n service.
	 *
	 * @param i18nService
	 * 		the 18 n service
	 */
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

}
