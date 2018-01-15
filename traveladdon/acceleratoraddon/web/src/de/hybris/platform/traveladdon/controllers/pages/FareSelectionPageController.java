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

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.SearchProcessingInfoData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.enums.FareSelectionDisplayOrder;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.AddBundleToCartForm;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;
import de.hybris.platform.travelfacades.fare.sorting.strategies.AbstractResultSortingStrategy;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for Fare Selection page
 */
@Controller
@RequestMapping("/fare-selection")
public class FareSelectionPageController extends TravelAbstractPageController
{
	private static final Logger LOG = Logger.getLogger(FareSelectionPageController.class);

	private static final String FARE_SELECTION_CMS_PAGE = "fareSelectionPage";
	private static final String MODEL_SAVED_SEARCH_NAME = "savedSearchMessage";
	private static final String ERROR_SAVE_MESSAGE = "text.page.savesearch.save.error";
	private static final String FAIL_SAVE_MESSAGE = "text.page.savesearch.save.fail";
	private static final String SUCCESS_SAVE_MESSAGE = "text.page.savesearch.save.success";

	@Resource(name = "fareSearchFacade")
	private FareSearchFacade fareSearchFacade;

	@Resource(name = "travelCustomerFacade")
	private TravelCustomerFacade travelCustomerFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "fareFinderValidator")
	private AbstractTravelValidator fareFinderValidator;

	@Resource(name = "fareSelectionSortingStrategyMap")
	private Map<FareSelectionDisplayOrder, AbstractResultSortingStrategy> fareSelectionSortingStrategyMap;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "fareSearchHashResolver")
	private FareSearchHashResolver fareSearchHashResolver;

	@Resource(name = "maxGuestMap")
	private Map<String, String> maxGuestMap;

	/**
	 * Method responsible for handling GET request on Fare Selection page
	 *
	 * @param fareFinderForm
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return fare selection page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getFareSelectionPage(
			@Valid @ModelAttribute(TraveladdonWebConstants.FARE_FINDER_FORM) final FareFinderForm fareFinderForm,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);
		}

		// need to repopulate PassengerTypeQuantityList as the request is coming from the URL
		if (fareFinderForm.getPassengerTypeQuantityList() == null)
		{
			fareFinderForm.setPassengerTypeQuantityList(createPassengerTypes(request));
		}

		validateForm(fareFinderValidator, fareFinderForm, bindingResult, TraveladdonWebConstants.FARE_FINDER_FORM);

		if (bindingResult.hasErrors())
		{
			request.setAttribute(TraveladdonWebConstants.FARE_FINDER_FORM_BINDING_RESULT, bindingResult);
		}
		else
		{
			sessionService.setAttribute(TravelservicesConstants.SEARCH_SEED, fareSearchHashResolver.generateSeed());

			if (StringUtils.isNotEmpty(request.getHeader("X-FORWARDED-FOR"))) {
				sessionService.setAttribute(TravelservicesConstants.IP_ADDRESS, request.getHeader("X-FORWARDED-FOR"));
			}
			else
			{
				sessionService.setAttribute(TravelservicesConstants.IP_ADDRESS, request.getRemoteAddr());
			}

			final FareSearchRequestData fareSearchRequestData = prepareFareSearchRequestData(fareFinderForm, request);
			final FareSelectionData fareSelectionData = fareSearchFacade.doSearch(fareSearchRequestData);

			// sort fareSelectionData by displayOrder
			final String displayOrder = fareSearchRequestData.getSearchProcessingInfo().getDisplayOrder();
			sortFareSelectionData(fareSelectionData, displayOrder, model);

			sessionService.setAttribute(TraveladdonWebConstants.SESSION_FARE_SELECTION_DATA, fareSelectionData);
			populateModel(model, fareFinderForm, fareSelectionData);
			model.addAttribute(TraveladdonWebConstants.FARE_SELECTION_REDIRECT_URL,
					TraveladdonWebConstants.FARE_SELECTION_ROOT_URL + buildRequestParameters(fareFinderForm, null, -1));
		}

		if (StringUtils.isNotBlank(fareFinderForm.getTripType()))
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_TRIP_TYPE, fareFinderForm.getTripType());
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(FARE_SELECTION_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FARE_SELECTION_CMS_PAGE));

		// model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		request.setAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, fareFinderForm);
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_FINDER_FORM, fareFinderForm);
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_SALES_APPLICATION,
				SalesApplication.WEB);

		return getViewForPage(model);
	}


	private void validateForm(final AbstractTravelValidator fareFinderValidator, final FareFinderForm fareFinderForm,
			final BindingResult bindingResult, final String formName)
	{
		fareFinderValidator.setTargetForm(TraveladdonWebConstants.FARE_FINDER_FORM);
		fareFinderValidator.setAttributePrefix("");
		fareFinderValidator.validate(fareFinderForm, bindingResult);
	}

	/**
	 * Method responsible for handling POST request to save Customer Search.
	 *
	 * @param fareFinderForm
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return the JSON object representing the Success/Failure message.
	 */
	@RequestMapping(value = "/save-search", method = RequestMethod.POST)
	@RequireHardLogIn
	public String saveCustomerSearch(@Valid final FareFinderForm fareFinderForm, final BindingResult bindingResult,
			final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{

		if (fareFinderForm.getPassengerTypeQuantityList() == null)
		{
			fareFinderForm.setPassengerTypeQuantityList(createPassengerTypes(request));
		}

		fareFinderValidator.validate(fareFinderForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			model.addAttribute(MODEL_SAVED_SEARCH_NAME, ERROR_SAVE_MESSAGE);
		}
		else
		{
			final boolean isSearchSaved = travelCustomerFacade.saveCustomerSearch(populateSavedSearchData(fareFinderForm));
			model.addAttribute(MODEL_SAVED_SEARCH_NAME, isSearchSaved ? SUCCESS_SAVE_MESSAGE : FAIL_SAVE_MESSAGE);
		}
		return TraveladdonControllerConstants.Views.Pages.FareSelection.savedSearchResult;
	}

	/**
	 * Method responsible for populating SavedSearchData from FareFinderForm.
	 *
	 * @param fareFinderForm
	 * @return savedSearchData
	 */
	protected SavedSearchData populateSavedSearchData(final FareFinderForm fareFinderForm)
	{
		final SavedSearchData savedSearchData = new SavedSearchData();


		savedSearchData.setCabinClass(fareFinderForm.getCabinClass());
		savedSearchData.setPassengerTypeQuantities(fareFinderForm.getPassengerTypeQuantityList());
		savedSearchData.setTripType(fareFinderForm.getTripType());
		savedSearchData.setDepartureLocation(fareFinderForm.getDepartureLocation());
		savedSearchData.setArrivalLocation(fareFinderForm.getArrivalLocation());
		savedSearchData.setDepartureLocationName(fareFinderForm.getDepartureLocationName());
		savedSearchData.setArrivalLocationName(fareFinderForm.getArrivalLocationName());
		savedSearchData.setArrivalLocationSuggestionType(fareFinderForm.getArrivalLocationSuggestionType());
		savedSearchData.setDepartureLocationSuggestionType(fareFinderForm.getDepartureLocationSuggestionType());

		savedSearchData.setReturnDateTime(StringUtils.equalsIgnoreCase(TripType.SINGLE.toString(), fareFinderForm.getTripType())
				? StringUtils.EMPTY : fareFinderForm.getReturnDateTime());
		savedSearchData.setDepartingDateTime(fareFinderForm.getDepartingDateTime());

		return savedSearchData;
	}

	/**
	 * Method handles the preparation of the Model object
	 *
	 * @param fareFinderForm
	 * @param model
	 * @param fareSelectionData
	 */
	protected void populateModel(final Model model, final FareFinderForm fareFinderForm, final FareSelectionData fareSelectionData)
	{
		final String priceDisplayPassengerType = configurationService.getConfiguration()
				.getString(TraveladdonWebConstants.PRICE_DISPLAY_PASSENGER_TYPE);
		model.addAttribute(TraveladdonWebConstants.MODEL_PRICE_DISPLAY_PASSENGER_TYPE, priceDisplayPassengerType);

		model.addAttribute(TraveladdonWebConstants.FARE_SELECTION, fareSelectionData);
		model.addAttribute(TraveladdonWebConstants.TRIP_TYPE, fareFinderForm.getTripType());
		final AddBundleToCartForm addBundleToCartForm = new AddBundleToCartForm();
		model.addAttribute(TraveladdonWebConstants.ADD_BUNDLE_TO_CART_FORM, addBundleToCartForm);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.ADD_BUNDLE_TO_CART_URL, TraveladdonWebConstants.ADD_BUNDLE_URL);
		final List<Date> outboundDates = getDatesForTabs(fareFinderForm.getDepartingDateTime());
		model.addAttribute(TraveladdonWebConstants.OUTBOUND_DATES, outboundDates);
		final List<String> outboundTabLinks = getLinksForTabs(outboundDates, fareFinderForm,
				TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER);
		model.addAttribute(TraveladdonWebConstants.OUTBOUND_TAB_LINKS, outboundTabLinks);
		model.addAttribute(TraveladdonWebConstants.OUTBOUND_LOW_PRICE,
				getLowestPriceForDay(fareSelectionData, TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER, priceDisplayPassengerType));
		if (StringUtils.isNotEmpty(fareFinderForm.getReturnDateTime()))
		{
			final List<Date> inboundDates = getDatesForTabs(fareFinderForm.getReturnDateTime());
			model.addAttribute(TraveladdonWebConstants.INBOUND_DATES, inboundDates);
			final List<String> inboundTabLinks = getLinksForTabs(inboundDates, fareFinderForm,
					TravelfacadesConstants.INBOUND_REFERENCE_NUMBER);
			model.addAttribute(TraveladdonWebConstants.INBOUND_TAB_LINKS, inboundTabLinks);
			model.addAttribute(TraveladdonWebConstants.INBOUND_LOW_PRICE, getLowestPriceForDay(fareSelectionData,
					TravelfacadesConstants.INBOUND_REFERENCE_NUMBER, priceDisplayPassengerType));
		}

		model.addAttribute(TraveladdonWebConstants.TAB_DATE_FORMAT, TraveladdonWebConstants.FARE_SELECTION_TAB_DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.PI_DATE_FORMAT, TraveladdonWebConstants.PRICED_ITINERARY_DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.DATE_FORMAT_LABEL, TraveladdonWebConstants.DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.TIME_FORMAT_LABEL, TraveladdonWebConstants.TIME_FORMAT);
		model.addAttribute(TraveladdonWebConstants.OUTBOUND_REF_NUMBER, TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER);
		model.addAttribute(TraveladdonWebConstants.INBOUND_REF_NUMBER, TravelfacadesConstants.INBOUND_REFERENCE_NUMBER);
		model.addAttribute(TraveladdonWebConstants.NO_OF_OUTBOUND_OPTIONS,
				countJourneyOptions(fareSelectionData, TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER));
		model.addAttribute(TraveladdonWebConstants.NO_OF_INBOUND_OPTIONS,
				countJourneyOptions(fareSelectionData, TravelfacadesConstants.INBOUND_REFERENCE_NUMBER));
		model.addAttribute(TraveladdonWebConstants.ECO_BUNDLE_TYPE, BundleType.ECONOMY);
		model.addAttribute(TraveladdonWebConstants.ECO_PLUS_BUNDLE_TYPE, BundleType.ECONOMY_PLUS);
		model.addAttribute(TraveladdonWebConstants.BUSINESS_BUNDLE_TYPE, BundleType.BUSINESS);

		final Map<String, TransportFacilityData> originDestination = getOriginDestination(fareSelectionData);

		if (MapUtils.isNotEmpty(originDestination))
		{
			model.addAttribute(TraveladdonWebConstants.ORIGIN, fareFinderForm.getDepartureLocationName());
			model.addAttribute(TraveladdonWebConstants.DESTINATION, fareFinderForm.getArrivalLocationName());
		}

		final List<String> sortingParameters = Arrays.asList(FareSelectionDisplayOrder.values()).stream().map(val -> val.toString())
				.collect(Collectors.toList());
		model.addAttribute(TraveladdonWebConstants.SORTING_PARAMETERS, sortingParameters);

		if (assistedServiceFacade.isAssistedServiceAgentLoggedIn())
		{
			final Map<Integer, Map<String, Long>> remainingSeatsMap = fareSearchFacade.getRemainingSeats(fareSelectionData);
			model.addAttribute(TraveladdonWebConstants.REMAINING_SEATS, remainingSeatsMap);
		}

	}

	/**
	 * Method handles the preparation of a FareSearchRequestData object using the FareFinderForm.
	 *
	 * @param fareFinderForm
	 * @return
	 */
	protected FareSearchRequestData prepareFareSearchRequestData(final FareFinderForm fareFinderForm,
			final HttpServletRequest request)
	{

		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();

		// create Transport Offering PreferencesData

		final TransportOfferingPreferencesData transportOfferingPreferencesData = new TransportOfferingPreferencesData();
		transportOfferingPreferencesData.setTransportOfferingType(TransportOfferingType.DIRECT);

		// create Travel Preferences

		final TravelPreferencesData travelPreferences = new TravelPreferencesData();
		travelPreferences.setCabinPreference(fareFinderForm.getCabinClass());
		travelPreferences.setTransportOfferingPreferences(transportOfferingPreferencesData);

		// populate passenger types

		fareSearchRequestData.setPassengerTypes(fareFinderForm.getPassengerTypeQuantityList());

		// populate sales application

		fareSearchRequestData.setSalesApplication(SalesApplication.WEB);

		// create OriginDestinationInfoData

		final List<OriginDestinationInfoData> originDestinationInfoData = new ArrayList<>();

		final OriginDestinationInfoData departureInfo = new OriginDestinationInfoData();

		departureInfo.setDepartureLocation(fareFinderForm.getDepartureLocation());
		departureInfo.setDepartureLocationType(
				enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getDepartureLocationSuggestionType()));
		departureInfo.setArrivalLocation(fareFinderForm.getArrivalLocation());
		departureInfo.setArrivalLocationType(
				enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getArrivalLocationSuggestionType()));
		departureInfo.setDepartureTime(
				TravelDateUtils.convertStringDateToDate(fareFinderForm.getDepartingDateTime(), TravelservicesConstants.DATE_PATTERN));
		departureInfo.setReferenceNumber(0);

		originDestinationInfoData.add(departureInfo);

		if (fareFinderForm.getTripType().equals(TripType.RETURN.name()))
		{
			final OriginDestinationInfoData returnInfo = new OriginDestinationInfoData();

			returnInfo.setDepartureLocation(fareFinderForm.getArrivalLocation());
			returnInfo.setDepartureLocationType(
					enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getArrivalLocationSuggestionType()));
			returnInfo.setArrivalLocation(fareFinderForm.getDepartureLocation());
			returnInfo.setArrivalLocationType(
					enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getDepartureLocationSuggestionType()));
			returnInfo.setDepartureTime(
					TravelDateUtils.convertStringDateToDate(fareFinderForm.getReturnDateTime(), TravelservicesConstants.DATE_PATTERN));
			returnInfo.setReferenceNumber(1);
			originDestinationInfoData.add(returnInfo);
		}

		// set fareSearchRequestData

		fareSearchRequestData.setOriginDestinationInfo(originDestinationInfoData);
		fareSearchRequestData.setTripType(TripType.valueOf(fareFinderForm.getTripType()));
		fareSearchRequestData.setTravelPreferences(travelPreferences);

		// set searchProcessingInfoData and displayOption
		final SearchProcessingInfoData searchProcessingInfoData = new SearchProcessingInfoData();
		final String displayOrder = request.getParameter(TraveladdonWebConstants.DISPLAY_ORDER);
		if (StringUtils.isNotBlank(displayOrder))
		{
			searchProcessingInfoData.setDisplayOrder(displayOrder);
		}
		else
		{
			searchProcessingInfoData.setDisplayOrder(FareSelectionDisplayOrder.DEPARTURE_TIME.toString());
		}
		fareSearchRequestData.setSearchProcessingInfo(searchProcessingInfoData);

		return fareSearchRequestData;

	}

	/**
	 * Method to sort the FareSelectionData based on the displayOrder. If displayOrder is null, empty or not a valid
	 * FareSelectionDisplayOrder enum, the default sorting by departureDate is applied.
	 *
	 * @param fareSelectionData
	 * 		as the FareSelectionData to be sorted
	 * @param displayOrder
	 * 		as the String corresponding to a sortingStrategy
	 * @param model
	 */
	public void sortFareSelectionData(final FareSelectionData fareSelectionData, final String displayOrder, final Model model)
	{

		final FareSelectionDisplayOrder displayOrderOption = Arrays.asList(FareSelectionDisplayOrder.values()).stream()
				.filter(val -> val.toString().equals(displayOrder)).findAny().orElse(null);

		if (displayOrderOption != null)
		{
			final AbstractResultSortingStrategy sortingStrategy = fareSelectionSortingStrategyMap.get(displayOrderOption);
			sortingStrategy.sortFareSelectionData(fareSelectionData);
			model.addAttribute(TraveladdonWebConstants.SELECTED_SORTING, displayOrderOption.toString());
		}

	}

	/**
	 * Method called to sort the FareSelectionData based on a selected sorting parameter.
	 *
	 * @param fareFinderForm
	 * 		as the fareFinderForm
	 * @param bindingResult
	 * 		as the bindingResult of the fareFinderForm
	 * @param refNumber
	 * 		as the refNumber of the leg to order
	 * @param displayOrder
	 * 		as the selected parameter used to order the fareSelectionData
	 * @param model
	 * @param request
	 * @param response
	 * @return the JSON object to refresh the result section with the sorted results
	 */
	@RequestMapping(value = "/sorting-fare-selection-results", method = RequestMethod.POST)
	public String sortingFareSelectionResults(@Valid final FareFinderForm fareFinderForm, final BindingResult bindingResult,
			@RequestParam final String refNumber, @RequestParam final String displayOrder, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		if (fareFinderForm.getPassengerTypeQuantityList() == null)
		{
			fareFinderForm.setPassengerTypeQuantityList(createPassengerTypes(request));
		}

		fareFinderValidator.validate(fareFinderForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			request.setAttribute(TraveladdonWebConstants.FARE_FINDER_FORM_BINDING_RESULT, bindingResult);
		}
		else
		{
			try
			{
				Integer.parseInt(refNumber);
				final FareSelectionData fareSelectionData = sessionService
						.getAttribute(TraveladdonWebConstants.SESSION_FARE_SELECTION_DATA);

				if (displayOrder.matches(TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_AND_UNDERSCORES))
				{
					sortFareSelectionData(fareSelectionData, displayOrder, model);
				}
				populateModel(model, fareFinderForm, fareSelectionData);
				model.addAttribute(TraveladdonWebConstants.REF_NUMBER, refNumber);
			}
			catch (final NumberFormatException ex)
			{
				LOG.error("Cannot parse ref number to integer", ex);
			}
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(FARE_SELECTION_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FARE_SELECTION_CMS_PAGE));

		return TraveladdonControllerConstants.Views.Pages.FareSelection.fareSelectionSortingResult;
	}

	protected void disableCachingForResponse(final HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	}

	/**
	 * Calculates the lowest price for the date selected for particular leg of the journey
	 *
	 * @param fareSelectionData
	 * 		- full response object
	 * @param refNumber
	 * 		- identifier of the leg
	 * @return lowest price for day
	 */
	protected PriceData getLowestPriceForDay(final FareSelectionData fareSelectionData, final int refNumber,
			final String priceDisplayPassengerType)
	{
		PriceData lowestPrice = null;
		PTCFareBreakdownData perPaxPtcBreakdownData = null;
		PriceData price;

		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			if (pricedItinerary.getOriginDestinationRefNumber() == refNumber && pricedItinerary.isAvailable())
			{
				for (final ItineraryPricingInfoData itineraryPricingInfo : pricedItinerary.getItineraryPricingInfos())
				{
					if (itineraryPricingInfo.isAvailable())
					{

						if (StringUtils.isNotEmpty(priceDisplayPassengerType))
						{
							perPaxPtcBreakdownData = itineraryPricingInfo.getPtcFareBreakdownDatas().stream()
									.filter(ptcFareBreakdown -> StringUtils.equalsIgnoreCase(priceDisplayPassengerType,
											ptcFareBreakdown.getPassengerTypeQuantity().getPassengerType().getCode()))
									.sorted((o1, o2) -> o1.getPassengerFare().getPerPax().getValue()
											.compareTo(o2.getPassengerFare().getPerPax().getValue()))
									.findFirst().get();
						}

						price = perPaxPtcBreakdownData != null ? perPaxPtcBreakdownData.getPassengerFare().getPerPax()
								: itineraryPricingInfo.getTotalFare().getTotalPrice();

						if (lowestPrice == null || price.getValue().compareTo(lowestPrice.getValue()) < 0)
						{
							lowestPrice = price;
						}
					}
				}
			}
		}
		return lowestPrice;
	}

	/**
	 * Creates a list of dates which will be displayed in Fare Selection table tabs
	 *
	 * @param selectedDateString
	 * 		- date selected for a leg
	 * @return a list of dates for tabs
	 */
	protected List<Date> getDatesForTabs(final String selectedDateString)
	{
		final List<Date> dates = new ArrayList<Date>();
		final SimpleDateFormat formatter = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);

		Date selectedDate = null;
		try
		{
			selectedDate = formatter.parse(selectedDateString);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(selectedDate);
			cal.add(Calendar.DAY_OF_MONTH, -3);
			for (int i = 0; i < 5; i++)
			{
				cal.add(Calendar.DAY_OF_MONTH, 1);
				dates.add(cal.getTime());
			}
		}
		catch (final ParseException e)
		{
			LOG.error("Error while parsing dates for tabs: " + selectedDateString);
		}

		return dates;
	}

	/**
	 * Checks how many priced itineraries are available for the relevant journey
	 *
	 * @param fareSelectionData
	 * 		- full response object
	 * @param referenceNumber
	 * 		- specifies which leg of the journey it is
	 * @return number of options for the journey
	 */
	protected int countJourneyOptions(final FareSelectionData fareSelectionData, final int referenceNumber)
	{
		int count = 0;
		if (!CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
			{
				if (pricedItinerary.getOriginDestinationRefNumber() == referenceNumber && pricedItinerary.isAvailable())
				{
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Method takes the request URL and extracts the passenger type and passenger quantity to create a list of
	 * PassengerTypeQuantityData
	 *
	 * @param request
	 * @return List<PassengerTypeQuantityData>
	 */
	protected List<PassengerTypeQuantityData> createPassengerTypes(final HttpServletRequest request)
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();

		final List<PassengerTypeData> passengerTypesDatas = travellerSortStrategy
				.sortPassengerTypes(passengerTypeFacade.getPassengerTypes());
		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		final int maxGuestQuantity = configurationService.getConfiguration().getInt(maxGuestMap.get(sessionBookingJourney));
		
		for (final PassengerTypeData passengerTypeData : passengerTypesDatas)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			passengerTypeQuantityData.setQuantity(0);

			final String passengerQuantity = request.getParameter(passengerTypeData.getCode());

			if (StringUtils.isNotBlank(passengerQuantity))
			{
				try
				{
					final int quantity = Integer.parseInt(passengerQuantity);
					if (quantity < 0 || quantity > maxGuestQuantity)
					{
						passengerTypeQuantityData
								.setQuantity(passengerTypeData.getCode().equalsIgnoreCase(TraveladdonWebConstants.PASSENGER_TYPE_ADULT)
										? TravelservicesConstants.DEFAULT_ADULTS : 0);
					}
					else
					{
						passengerTypeQuantityData.setQuantity(quantity);
					}
				}
				catch (final NumberFormatException nfe)
				{
					passengerTypeQuantityData
							.setQuantity(passengerTypeData.getCode().equalsIgnoreCase(TraveladdonWebConstants.PASSENGER_TYPE_ADULT)
									? TravelservicesConstants.DEFAULT_ADULTS : 0);
				}
			}
			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}
		return passengerTypeQuantityList;
	}

	/**
	 * Returns links for tabs so that if users wants to change a date, new set of results is generated.
	 *
	 * @param dates
	 * 		- dates that will be displayed for tabs
	 * @param fareFinderForm
	 * @param refNumber
	 * @return links to fare selection page with updated date
	 */
	protected List<String> getLinksForTabs(final List<Date> dates, final FareFinderForm fareFinderForm, final int refNumber)
	{
		final List<String> linksForTabs = new ArrayList<String>();
		for (final Date date : dates)
		{
			final String dateString = TravelDateUtils.convertDateToStringDate(date, TravelservicesConstants.DATE_PATTERN);
			linksForTabs
					.add(TraveladdonWebConstants.FARE_SELECTION_PATH + buildRequestParameters(fareFinderForm, dateString, refNumber));
		}
		return linksForTabs;
	}

	/**
	 * Builds fare selection current request parameters
	 *
	 * @param fareFinderForm
	 * @param newDate
	 * @param refNumber
	 * @return
	 */
	protected String buildRequestParameters(final FareFinderForm fareFinderForm, final String newDate, final int refNumber)
	{
		final StringBuilder requestParams = new StringBuilder();
		requestParams.append("?departureLocation=");
		requestParams.append(fareFinderForm.getDepartureLocation());
		requestParams.append("&arrivalLocation=");
		requestParams.append(fareFinderForm.getArrivalLocation());
		requestParams.append("&tripType=");
		requestParams.append(fareFinderForm.getTripType());
		requestParams.append("&cabinClass=");
		requestParams.append(fareFinderForm.getCabinClass());
		requestParams.append("&departureLocationName=");
		requestParams.append(fareFinderForm.getDepartureLocationName());
		requestParams.append("&departureLocationSuggestionType=");
		requestParams.append(fareFinderForm.getDepartureLocationSuggestionType());
		requestParams.append("&arrivalLocationName=");
		requestParams.append(fareFinderForm.getArrivalLocationName());
		requestParams.append("&arrivalLocationSuggestionType=");
		requestParams.append(fareFinderForm.getArrivalLocationSuggestionType());

		for (final PassengerTypeQuantityData ptq : fareFinderForm.getPassengerTypeQuantityList())
		{
			requestParams.append("&");
			requestParams.append(ptq.getPassengerType().getCode());
			requestParams.append("=");
			requestParams.append(ptq.getQuantity());
		}

		if (refNumber == TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER)
		{
			requestParams.append("&departingDateTime=");
			requestParams.append(newDate);
			if (StringUtils.isNotBlank(fareFinderForm.getReturnDateTime()))
			{
				requestParams.append("&returnDateTime=");
				requestParams.append(fareFinderForm.getReturnDateTime());
			}
			requestParams.append(TraveladdonWebConstants.OUTBOUND_SECTION_START);
		}
		else if (refNumber == TravelfacadesConstants.INBOUND_REFERENCE_NUMBER)
		{
			requestParams.append("&departingDateTime=");
			requestParams.append(fareFinderForm.getDepartingDateTime());
			requestParams.append("&returnDateTime=");
			requestParams.append(newDate);
			requestParams.append(TraveladdonWebConstants.INBOUND_SECTION_START);
		}
		else
		{
			requestParams.append("&departingDateTime=");
			requestParams.append(fareFinderForm.getDepartingDateTime());
			if (StringUtils.isNotBlank(fareFinderForm.getReturnDateTime()))
			{
				requestParams.append("&returnDateTime=");
				requestParams.append(fareFinderForm.getReturnDateTime());
			}
		}

		return requestParams.toString();
	}

	/**
	 * Provides travel route for the first leg of the journey based on response object
	 *
	 * @param fareSelectionData
	 * @return travel route
	 */
	protected Map<String, TransportFacilityData> getOriginDestination(final FareSelectionData fareSelectionData)
	{
		final Map<String, TransportFacilityData> originDestination = new HashMap<String, TransportFacilityData>(2);
		if (CollectionUtils.isNotEmpty(fareSelectionData.getPricedItineraries()))
		{
			final PricedItineraryData pricedItinerary = fareSelectionData.getPricedItineraries().get(0);
			final TravelRouteData travelRoute = pricedItinerary.getItinerary().getRoute();
			if (TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER == pricedItinerary.getOriginDestinationRefNumber())
			{
				originDestination.put(TravelfacadesConstants.ORIGIN, travelRoute.getOrigin());
				originDestination.put(TravelfacadesConstants.DESTINATION, travelRoute.getDestination());
			}
			else
			{
				originDestination.put(TravelfacadesConstants.ORIGIN, travelRoute.getDestination());
				originDestination.put(TravelfacadesConstants.DESTINATION, travelRoute.getOrigin());
			}
		}
		return originDestination;
	}
}
