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

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.TransportOfferingStatusSearchForm;
import de.hybris.platform.traveladdon.forms.validation.TransportOfferingStatusSearchValidator;
import de.hybris.platform.travelfacades.facades.BookingFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for the TransportOfferingStatusSearchComponent
 */

@Controller("TransportOfferingStatusSearchComponentController")
@RequestMapping(value = TraveladdonControllerConstants.Actions.Cms.TransportOfferingStatusSearchComponent)
public class TransportOfferingStatusSearchComponentController
		extends SubstitutingCMSAddOnComponentController<AbstractCMSComponentModel>
{
	private static final Logger LOGGER = Logger.getLogger(TransportOfferingStatusSearchComponentController.class);

	private static final String DEPARTURE_DATE = "departureDate";
	private static final String NUMBER = "transportOfferingNumber";
	private static final String REDIRECT_TO_STATUS_PAGE = "redirect:/transport-offering-status";

	@Resource(name = "transportOfferingStatusSearchValidator")
	private TransportOfferingStatusSearchValidator transportOfferingStatusSearchValidator;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component)
	{
		if (!model.containsAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM))
		{
			final TransportOfferingStatusSearchForm form = new TransportOfferingStatusSearchForm();
			if (request.getParameter(NUMBER) != null)
			{
				form.setTransportOfferingNumber(request.getParameter(NUMBER));
			}
			if (request.getParameter(DEPARTURE_DATE) != null)
			{
				form.setDepartureDate(request.getParameter(DEPARTURE_DATE));
			}
			model.addAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM, form);
		}
		if (StringUtils.isEmpty((String) request.getAttribute(AbstractCMSAddOnComponentController.COMPONENT_UID)))
		{
			final TransportOfferingData nextScheduledTransportOfferingData = bookingFacade.getNextScheduledTransportOfferingData();
			model.addAttribute("nextScheduledTransportOfferingData", nextScheduledTransportOfferingData);
		}
	}

	/**
	 * Method called form the transportOfferingStatusComponent. It performs returns the html form for
	 * transportOfferingStatusSearch
	 *
	 * @return the html content for transportOfferingStatusSearch form
	 */
	@RequestMapping(value = "/get-transport-offering-status-search-form", method = RequestMethod.GET)
	public String getTransportOfferingStatusSearchForm(@RequestParam final String componentUid, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		request.setAttribute(AbstractCMSAddOnComponentController.COMPONENT_UID, componentUid);
		try
		{
			return handleGet(request, response, model);
		}
		catch (final Exception e)
		{
			LOGGER.error("Exception retrieving TransportOffering status search form", e);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Method called form the transportOfferingStatusComponent. It performs a form validation and returns a json object
	 * with a boolean value and a list of field errors.
	 *
	 * @param transportOfferingStatusForm as the input TransportOfferingStatusForm
	 * @param bindingResult               as the bindingResult of the form validation
	 * @param model
	 * @return a json object with the results of the validation
	 */
	@RequestMapping(value = "/validate-transport-offering-status-form", method = RequestMethod.POST)
	public String validateTransportOfferingStatusSearchForm(
			@Valid @ModelAttribute("transportOfferingStatusForm") final TransportOfferingStatusSearchForm transportOfferingStatusForm,
			final BindingResult bindingResult, final Model model)
	{
		getTransportOfferingStatusSearchValidator().validate(transportOfferingStatusForm, bindingResult);
		model.addAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM, transportOfferingStatusForm);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(TraveladdonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(TraveladdonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}

		return TraveladdonControllerConstants.Views.Pages.FormErrors.formErrorsResponse;
	}

	/**
	 * Method called from the transportOfferingStatusComponent. It performs a form validation and in case there are no
	 * errors, it does a redirect to the TransportOfferingStatusResult page
	 *
	 * @param transportOfferingStatusForm as the input TransportOfferingStatusForm
	 * @param bindingResult               as the bindingResult of the form validation
	 * @param redirectModel
	 * @return a string representing the redirect to the TransportOfferingStatusResult page if there are no errors, the
	 * landing page otherwise
	 */
	@RequestMapping(value = "/get-transport-offering-status-page", method = RequestMethod.POST)
	public String getTransportOfferingStatus(
			@Valid @ModelAttribute("transportOfferingStatusForm") final TransportOfferingStatusSearchForm transportOfferingStatusForm,
			final BindingResult bindingResult, final RedirectAttributes redirectModel)
	{

		getTransportOfferingStatusSearchValidator().validate(transportOfferingStatusForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			redirectModel.addFlashAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM_BINDING_RESULT,
					bindingResult);
			redirectModel.addFlashAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM,
					transportOfferingStatusForm);
			return AbstractCMSAddOnComponentController.REDIRECT_PREFIX + "/";
		}

		redirectModel.addFlashAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM,
				transportOfferingStatusForm);
		return REDIRECT_TO_STATUS_PAGE + "?" + NUMBER + "=" + transportOfferingStatusForm.getTransportOfferingNumber() + "&"
				+ DEPARTURE_DATE + "=" + transportOfferingStatusForm.getDepartureDate();
	}

	/**
	 * @return the transportOfferingStatusSearchValidator
	 */
	protected TransportOfferingStatusSearchValidator getTransportOfferingStatusSearchValidator()
	{
		return transportOfferingStatusSearchValidator;
	}

	/**
	 * @param transportOfferingStatusSearchValidator the transportOfferingStatusSearchValidator to set
	 */
	public void setTransportOfferingStatusSearchValidator(
			final TransportOfferingStatusSearchValidator transportOfferingStatusSearchValidator)
	{
		this.transportOfferingStatusSearchValidator = transportOfferingStatusSearchValidator;
	}

}
