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

package de.hybris.platform.travelcommons.controllers.cms;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackagesResponseData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelcommons.forms.cms.AddDealToCartForm;
import de.hybris.platform.travelcommons.model.components.DealComponentModel;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.packages.DealBundleTemplateFacade;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Deal Component Controller
 */
@Controller("DealComponentController")
@RequestMapping(value = TravelcommonsControllerConstants.Actions.Cms.DealComponent)
public class DealComponentController extends SubstitutingCMSAddOnComponentController<DealComponentModel>
{

	private static final Logger LOGGER = Logger.getLogger(DealComponentController.class);

	@Resource(name = "dealSearchBundleTemplateFacade")
	private DealBundleTemplateFacade dealBundleTemplateFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "fareSearchHashResolver")
	private FareSearchHashResolver fareSearchHashResolver;

	@Resource(name = "travelBundleTemplateFacade")
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final DealComponentModel component)
	{
		sessionService.setAttribute(TravelservicesConstants.SEARCH_SEED + "-" + component.getDealBundleTemplateId(),
				fareSearchHashResolver.generateSeed());
		final String selectedDepartureDate = Objects
				.isNull(request.getAttribute(TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE)) ? StringUtils.EMPTY
				: (String) request.getAttribute(TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE);
		final PackageRequestData packageRequestData = preparePackageRequestData(component.getDealBundleTemplateId(),
				selectedDepartureDate);
		final PackagesResponseData packagesResponseData = dealBundleTemplateFacade.getPackageResponseDetails(packageRequestData);

		if (packagesResponseData != null && CollectionUtils.isNotEmpty(packagesResponseData.getPackageResponses()))
		{
			model.addAttribute(TravelcommonsWebConstants.PACKAGE_RESPONSE_DATA, packagesResponseData.getPackageResponses().get(0));
		}
		model.addAttribute(TravelcommonsWebConstants.ADD_DEAL_TO_CART_FORM, new AddDealToCartForm());

		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_SELECTION_DATA);
		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES);
		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES);

		model.addAttribute(TravelcommonsWebConstants.DEAL_COMPONENT_ID, component.getUid());
		model.addAttribute(TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, component.getDealBundleTemplateId());
	}

	/**
	 * This method populates and returns the PackageRequestData populated for the required bundleTemplate based on the
	 * given dealBundleTemplateId
	 *
	 * @param dealBundleTemplateId the dealBundleTemplateId
	 * @param departureDate        the departureDate
	 * @return the PackageRequestData
	 */
	protected PackageRequestData preparePackageRequestData(final String dealBundleTemplateId, final String departureDate)
	{
		return dealBundleTemplateFacade.getPackageRequestData(dealBundleTemplateId, departureDate);
	}

	/**
	 * This method is responsible for refreshing the component.
	 *
	 * @param dealComponentId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	protected String getComponent(
			@RequestParam(value = TravelcommonsWebConstants.DEAL_COMPONENT_ID, required = true) final String dealComponentId,
			@RequestParam(value = TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE, required = true) final String dealSelectedDepartureDate,
			final HttpServletRequest request, final HttpServletResponse response, final Model model)
	{
		request.setAttribute(COMPONENT_UID, dealComponentId);
		request.setAttribute(TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE, dealSelectedDepartureDate);
		try
		{
			return handleGet(request, response, model);
		}
		catch (final Exception e)
		{
			LOGGER.error("Exception loading the component", e);
		}
		return StringUtils.EMPTY;
	}

	@RequestMapping(value = "/get-valid-dates", method = RequestMethod.GET)
	public String getDealsValidDates(
			@RequestParam(value = "dealStartingDatePattern", required = true) final String dealStartingDatePattern,
			@RequestParam(value = "dealDepartureDate", required = true) final String dealDepartureDate,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel,
			final Model model) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		final List<String> datePickerFormatedValidDates = dealBundleTemplateFacade
				.getFormattedDealValidDates(dealStartingDatePattern, dealDepartureDate);
		model.addAttribute(TravelcommonsWebConstants.DEAL_VALID_DATES, datePickerFormatedValidDates);

		return TravelcommonsControllerConstants.Views.Pages.Deal.DealValidDatesJsonResponse;
	}

	@RequestMapping(value = "/validate-departure-date", method = RequestMethod.GET)
	public String validateDealDepartureDate(
			@RequestParam(value = TravelcommonsWebConstants.DEAL_COMPONENT_ID, required = true) final String dealComponentId,
			@RequestParam(value = TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, required = true) final String dealBundleTemplateId,
			@RequestParam(value = TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE, required = true) final String dealSelectedDepartureDate,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel,
			final Model model) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		if (StringUtils.isEmpty(dealComponentId) || StringUtils.isEmpty(dealBundleTemplateId))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_COMPONENT_ID_EMPTY, model);
		}

		if (StringUtils.isEmpty(dealSelectedDepartureDate))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_DEPARTURE_DATE_EMPTY, model);
		}

		final String startingDatePattern = dealBundleTemplateFacade.getDealValidCronJobExpressionById(dealBundleTemplateId);
		if (Objects.isNull(startingDatePattern))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_BUNDLE_ID_INVALID, model);
		}

		final List<Date> validDates = dealBundleTemplateFacade.getDealValidDates(startingDatePattern, dealSelectedDepartureDate);

		final Date dealDepartureDate = TravelDateUtils.convertStringDateToDate(dealSelectedDepartureDate,
				TravelservicesConstants.DATE_PATTERN);

		if (Objects.isNull(dealDepartureDate))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_DEPARTURE_DATE_INVALID_FORMAT, model);
		}
		else if (CollectionUtils.isEmpty(validDates) || !validDates.contains(dealDepartureDate))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_DEPARTURE_DATE_NOT_AVAILABLE, model);
		}

		return REDIRECT_PREFIX + TravelcommonsControllerConstants.Actions.Cms.DealComponent + "/refresh"
				+ TravelacceleratorstorefrontWebConstants.QUESTION_MARK + TravelcommonsWebConstants.DEAL_COMPONENT_ID
				+ TravelacceleratorstorefrontWebConstants.EQUALS + dealComponentId + TravelacceleratorstorefrontWebConstants.AMPERSAND
				+ TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE + TravelacceleratorstorefrontWebConstants.EQUALS
				+ dealSelectedDepartureDate;
	}

	protected String getChangeDealDateError(final String error, final Model model)
	{
		model.addAttribute(TravelcommonsWebConstants.DEAL_CHANGE_DATE_VALIDATION_ERROR, error);

		return TravelcommonsControllerConstants.Views.Pages.Deal.DealDepartureDateValidationJsonResponse;
	}

	protected void disableCachingForResponse(final HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	}

}
