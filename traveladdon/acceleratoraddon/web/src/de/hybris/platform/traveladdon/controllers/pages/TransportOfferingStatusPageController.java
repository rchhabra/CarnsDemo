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
package de.hybris.platform.traveladdon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingOption;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.TransportOfferingStatusSearchForm;
import de.hybris.platform.traveladdon.forms.validation.TransportOfferingStatusSearchValidator;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller for Transport Offering Status page
 *
 */
@Controller
@RequestMapping("/transport-offering-status")
public class TransportOfferingStatusPageController extends AbstractPageController
{
	private static final String DEPARTURE_DATE = "departureDate";
	private static final String TRANSPORT_OFFERING_STATUS_CMS_PAGE = "transportOfferingStatusPage";

	@Resource(name = "transportOfferingFacade")
	private TransportOfferingFacade transportOfferingFacade;

	@Resource(name = "transportOfferingStatusSearchValidator")
	private TransportOfferingStatusSearchValidator transportOfferingStatusSearchValidator;

	/**
	 * Method responsible for handling GET request on Transport Offering Status page
	 *
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String getTransportOfferingStatusPage(
			@Valid @ModelAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM) final TransportOfferingStatusSearchForm form,
			final BindingResult bindingResult, final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(TRANSPORT_OFFERING_STATUS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRANSPORT_OFFERING_STATUS_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		if (bindingResult.getFieldErrorCount(DEPARTURE_DATE) == 0)
		{
			transportOfferingStatusSearchValidator.validate(form, bindingResult);
		}

		final boolean hasErrorFlag = bindingResult.hasErrors();
		if (hasErrorFlag)
		{
			model.addAttribute(TraveladdonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}
		else
		{
			model.addAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM, form);
			final Date departureDate = TravelDateUtils.convertStringDateToDate(form.getDepartureDate(),
					TravelservicesConstants.DATE_PATTERN);
			final List<TransportOfferingData> transportOfferingDataList = transportOfferingFacade.getTransportOfferings(
					form.getTransportOfferingNumber(), departureDate,
					Arrays.asList(TransportOfferingOption.STATUS, TransportOfferingOption.TERMINAL));
			model.addAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_DATA_LIST, transportOfferingDataList);
		}

		return getViewForPage(model);
	}

	/**
	 * Method to refresh the transportOfferingStatus page after performing a form validation
	 *
	 * @param transportOfferingStatusForm
	 *           as the input TransportOfferingStatusForm
	 * @param bindingResult
	 *           as the spring bindingResults
	 * @param model
	 *
	 * @return the location of the flightStatusSearchResponse tag use to render the status result table if there are no
	 *         errors, the landing page otherwise
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/refresh-transport-offering-status-results", method = RequestMethod.POST)
	public String getTransportOfferingStatusResults(
			@Valid @ModelAttribute final TransportOfferingStatusSearchForm transportOfferingStatusForm,
			final BindingResult bindingResult, final Model model) throws CMSItemNotFoundException
	{

		storeCmsPageInModel(model, getContentPageForLabelOrId(TRANSPORT_OFFERING_STATUS_CMS_PAGE));

		if (bindingResult.getFieldErrorCount(DEPARTURE_DATE) == 0)
		{
			transportOfferingStatusSearchValidator.validate(transportOfferingStatusForm, bindingResult);
		}

		model.addAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_STATUS_FORM, transportOfferingStatusForm);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(TraveladdonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(TraveladdonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
			return TraveladdonControllerConstants.Views.Pages.FormErrors.formErrorsResponse;
		}
		else
		{
			final Date departureDate = TravelDateUtils.convertStringDateToDate(transportOfferingStatusForm.getDepartureDate(),
					TravelservicesConstants.DATE_PATTERN);
			final List<TransportOfferingData> transportOfferingDataList = transportOfferingFacade.getTransportOfferings(
					transportOfferingStatusForm.getTransportOfferingNumber(), departureDate,
					Arrays.asList(TransportOfferingOption.STATUS, TransportOfferingOption.TERMINAL));
			model.addAttribute(TraveladdonWebConstants.TRANSPORT_OFFERING_DATA_LIST, transportOfferingDataList);
			return TraveladdonControllerConstants.Views.Pages.FlightStatus.flightStatusSearchResponse;
		}

	}
}
