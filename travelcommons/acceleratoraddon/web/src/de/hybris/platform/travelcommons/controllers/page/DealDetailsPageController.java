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

package de.hybris.platform.travelcommons.controllers.page;


import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackagesResponseData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelcommons.forms.cms.AddDealToCartForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(
{ "/deal-details" })
public class DealDetailsPageController extends AbstractPackagePageController
{
	private static final String DEAL_DETAILS_CMS_PAGE = "dealDetailsPage";
	private static final String HOME_PAGE_PATH = "/";

	@Resource(name = "dealBundleTemplateFacade")
	private DealBundleTemplateFacade dealBundleTemplateFacade;

	@Resource(name = "dealSearchBundleTemplateFacade")
	private DealBundleTemplateFacade dealSearchBundleTemplateFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "fareSearchHashResolver")
	private FareSearchHashResolver fareSearchHashResolver;

	@RequestMapping(method = RequestMethod.GET)
	public String getDealDetailsPage(
			@RequestParam(value = TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, required = false) final String dealBundleTemplateId,
			@RequestParam(value = TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE, required = false) final String dealSelectedDepartureDate,
			final Model model, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		if (!getTravelCartFacade().isCurrentCartValid())
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}

		if (StringUtils.isEmpty(dealBundleTemplateId) || StringUtils.isEmpty(dealSelectedDepartureDate))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}

		if (!dealBundleTemplateFacade.isDealBundleTemplateMatchesCart(dealBundleTemplateId))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}

		final Date dealDepartureDate = TravelDateUtils.convertStringDateToDate(dealSelectedDepartureDate,
				TravelservicesConstants.DATE_PATTERN);

		if (!dealBundleTemplateFacade.isDepartureDateInCartEquals(dealDepartureDate))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}

		final String startingDatePattern = dealBundleTemplateFacade.getDealValidCronJobExpressionById(dealBundleTemplateId);
		if (Objects.isNull(startingDatePattern))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}

		sessionService.setAttribute(TravelservicesConstants.SEARCH_SEED, fareSearchHashResolver.generateSeed());
		sessionService.setAttribute(TravelservicesConstants.SEARCH_SEED + "-" + dealBundleTemplateId, fareSearchHashResolver.generateSeed());

		final List<Date> validDates = dealBundleTemplateFacade.getDealValidDates(startingDatePattern, dealSelectedDepartureDate);

		if (Objects.isNull(dealDepartureDate))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}
		else if (CollectionUtils.isEmpty(validDates) || !validDates.contains(dealDepartureDate))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);
		}

		final PackageRequestData packageRequestData = preparePackageRequestData(dealBundleTemplateId, dealSelectedDepartureDate);
		final PackagesResponseData packagesResponseData = dealBundleTemplateFacade.getPackageResponseDetails(packageRequestData);

		if (Objects.isNull(packagesResponseData) || CollectionUtils.isEmpty(packagesResponseData.getPackageResponses()))
		{
			return getChangeDealDateURLError(TravelcommonsWebConstants.ERROR_DEAL_DETAILS_URL_INVALID, redirectModel);

		}
		else
		{
			// sort fareSelectionData by displayOrder
			final FareSearchRequestData fareSearchRequestData = packageRequestData.getTransportPackageRequest()
					.getFareSearchRequest();
			final String displayOrder = fareSearchRequestData.getSearchProcessingInfo().getDisplayOrder();
			final FareSelectionData fareSelectionData = packagesResponseData.getPackageResponses().get(0)
					.getTransportPackageResponse().getFareSearchResponse();
			sortFareSelectionData(fareSelectionData, displayOrder);

			populateFareSearchResponseInModel(fareSelectionData, model);
		}
		model.addAttribute(TravelcommonsWebConstants.PACKAGE_RESPONSE_DATA, packagesResponseData.getPackageResponses().get(0));
		model.addAttribute(TravelcommonsWebConstants.ADD_DEAL_TO_CART_FORM, new AddDealToCartForm());

		getSessionService().removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_SELECTION_DATA);
		getSessionService().removeAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES);
		getSessionService().removeAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES);

		model.addAttribute(TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, dealBundleTemplateId);

		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = packagesResponseData.getPackageResponses()
				.get(0).getAccommodationPackageResponse().getAccommodationAvailabilityResponse();

		model.addAttribute(TraveladdonWebConstants.DATE_FORMAT_LABEL, TraveladdonWebConstants.DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.TIME_FORMAT_LABEL, TraveladdonWebConstants.TIME_FORMAT);

		final SearchPageData<ReviewData> customerReviewsSearchPageData = getPagedAccommodationOfferingCustomerReviews(
				accommodationAvailabilityResponse.getAccommodationReference().getAccommodationOfferingCode(), 0);
		model.addAttribute(AccommodationaddonWebConstants.CUSTOMER_REVIEW_SEARCH_PAGE_DATA, customerReviewsSearchPageData);

		model.addAttribute(TravelacceleratorstorefrontWebConstants.GOOGLE_API_KEY,
				getConfigurationService().getConfiguration().getString(TravelfacadesConstants.GOOGLE_API_KEY));

		storeCmsPageInModel(model, getContentPageForLabelOrId(DEAL_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(DEAL_DETAILS_CMS_PAGE));
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_SALES_APPLICATION,
				SalesApplication.WEB);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/validate-departure-date", method = RequestMethod.GET)
	public String validateDealDepartureDate(
			@RequestParam(value = TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, required = true) final String dealBundleTemplateId,
			@RequestParam(value = TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE, required = true) final String dealSelectedDepartureDate,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel,
			final Model model) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		if (StringUtils.isEmpty(dealSelectedDepartureDate))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_DEPARTURE_DATE_EMPTY, model);
		}

		final String startingDatePattern = dealSearchBundleTemplateFacade.getDealValidCronJobExpressionById(dealBundleTemplateId);
		if (Objects.isNull(startingDatePattern))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_BUNDLE_ID_INVALID, model);
		}

		final List<Date> validDates = dealSearchBundleTemplateFacade.getDealValidDates(startingDatePattern,
				dealSelectedDepartureDate);
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

		final PackageRequestData packageRequestData = preparePackageRequestData(dealBundleTemplateId, dealSelectedDepartureDate);

		final PackagesResponseData packagesResponseData = dealSearchBundleTemplateFacade
				.getPackageResponseDetails(packageRequestData);
		if (Objects.isNull(packagesResponseData) || CollectionUtils.isEmpty(packagesResponseData.getPackageResponses()))
		{
			return getChangeDealDateError(TravelcommonsWebConstants.ERROR_DEAL_DEPARTURE_DATE_NOT_AVAILABLE, model);
		}

		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (StringUtils.isNotEmpty(sessionBookingJourney))
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_PACKAGE);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_PACKAGE);
		}

		model.addAttribute(TravelcommonsWebConstants.PACKAGE_RESPONSE_DATA, packagesResponseData.getPackageResponses().get(0));
		model.addAttribute(TravelcommonsWebConstants.ADD_DEAL_TO_CART_FORM, new AddDealToCartForm());
		model.addAttribute(TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, dealBundleTemplateId);
		return TravelcommonsControllerConstants.Views.Pages.Deal.DealDetailsReturnDateJsonResponse;
	}

	protected String getChangeDealDateError(final String error, final Model model)
	{
		model.addAttribute(TravelcommonsWebConstants.DEAL_CHANGE_DATE_VALIDATION_ERROR, error);

		return TravelcommonsControllerConstants.Views.Pages.Deal.DealDetailsReturnDateJsonResponse;
	}

	protected String getChangeDealDateURLError(final String error, final RedirectAttributes redirectModel)
	{
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, error);
		return REDIRECT_PREFIX + HOME_PAGE_PATH;
	}

	/**
	 * This method populates and returns the PackageRequestData populated for the required bundleTemplate based on the
	 * given dealBundleTemplateId
	 *
	 * @param dealBundleTemplateId
	 *           the dealBundleTemplateId
	 * @param departureDate
	 *           the departureDate
	 * @return the PackageRequestData
	 */
	protected PackageRequestData preparePackageRequestData(final String dealBundleTemplateId, final String departureDate)
	{
		return dealBundleTemplateFacade.getPackageRequestData(dealBundleTemplateId, departureDate);
	}

	protected void disableCachingForResponse(final HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	}

	@ModelAttribute(TraveladdonWebConstants.PI_DATE_FORMAT)
	public String getPriceItineraryDateFormat()
	{
		return TraveladdonWebConstants.PRICED_ITINERARY_DATE_FORMAT;
	}
}
