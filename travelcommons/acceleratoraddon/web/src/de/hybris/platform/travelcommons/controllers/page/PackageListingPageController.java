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
 *
 *
 */

package de.hybris.platform.travelcommons.controllers.page;

import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationFinderForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationFinderValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RadiusData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.accommodation.user.data.SearchAddressData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.traveladdon.validators.FareFinderValidator;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.wrapper.AccommodationAutoSuggestWrapper;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.TravelLocationFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationSuggestionFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageSearchFacade;
import de.hybris.platform.travelfacades.facades.packages.strategies.EncodeSearchUrlToMapPackageStrategy;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * Controller for Accommodation Search page
 */
@Controller
@RequestMapping("/package-listing")
public class PackageListingPageController extends AbstractPackagePageController
{
	private static final Logger LOG = Logger.getLogger(PackageListingPageController.class);
	public static final String REDIRECT_PREFIX = "redirect:";

	private static final String PACKAGE_LISTING_CMS_PAGE = "packageListingPage";
	private static final String ROOM_QUERY_STRING_INDICATOR = "r";
	private static final String TOTAL_NUMBER_OF_RESULTS = "totalNumberOfResults";
	private static final String TOTAL_SHOWN_RESULTS = "totalshownResults";
	private static final String PAGE_NUM = "pageNum";
	private static final String HAS_MORE_RESULTS = "hasMoreResults";
	private static final String PACKAGE_DETAILS_URL_PARAMS = "packageDetailsUrlParams";
	private static final String PROPERTY_NAME = "propertyName";
	private static final String QUERY = "q";
	private static final String HYPHEN_SEPERATOR = " - ";
	private static final String MIN_PACKAGE_PRICE = "minPackagePrice";
	private static final String MAX_PACKAGE_PRICE = "maxPackagePrice";
	private static final String PRICE_RANGE = "priceRange";
	private static final String ERROR = "error";
	private static final String ORIGINAL_REFERER = "originalReferer";
	private static final String RESULTS_VIEW_TYPE = "resultsViewType";

	@Resource(name = "encodeSearchUrlToMapPackageStrategy")
	private EncodeSearchUrlToMapPackageStrategy encodeSearchUrlToMapPackageStrategy;

	@Resource(name = "accommodationSuggestionFacade")
	private AccommodationSuggestionFacade accommodationSuggestionFacade;

	@Resource(name = "accommodationAutoSuggestWrapper")
	private AccommodationAutoSuggestWrapper accommodationAutoSuggestWrapper;

	@Resource(name = "fareFinderValidator")
	protected FareFinderValidator fareFinderValidator;

	@Resource(name = "accommodationFinderValidator")
	private AccommodationFinderValidator accommodationFinderValidator;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "transportOfferingFacade")
	private TransportOfferingFacade transportOfferingFacade;

	@Resource(name = "packageSearchFacade")
	private PackageSearchFacade packageSearchFacade;

	@Resource(name = "transportFacilityFacade")
	private TransportFacilityFacade transportFacilityFacade;

	@Resource(name = "travelLocationFacade")
	private TravelLocationFacade travelLocationFacade;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	/**
	 * Gets accommodation search page.
	 *
	 * @param accommodationFinderForm the accommodation finder form
	 * @param propertyName            the property name
	 * @param query                   the query
	 * @param sortCode                the sort code
	 * @param pageNumber              the page number
	 * @param bindingResult           the binding result
	 * @param model                   the model
	 * @param request                 the request
	 * @return the accommodation search page
	 * @throws CMSItemNotFoundException the cms item not found exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getPackageListingPage(
			@ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM) final AccommodationFinderForm accommodationFinderForm,
			final BindingResult accommodationBindingResult,
			@ModelAttribute(TraveladdonWebConstants.FARE_FINDER_FORM) final FareFinderForm fareFinderForm,
			final BindingResult fareBindingResult,
			@RequestParam(value = "propertyName", required = false) final String propertyName,
			@RequestParam(value = QUERY, required = false) final String query,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNumber,
			@RequestParam(value = "resultsViewType", required = false, defaultValue = "listView") final String resultsViewType,
			@RequestParam(value = PRICE_RANGE, required = false) final String priceRange,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(PACKAGE_LISTING_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PACKAGE_LISTING_CMS_PAGE));

		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
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

		try
		{
			initializeForms(fareFinderForm, accommodationFinderForm, request);
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer" + e.getClass().getName() + " : " + e.getMessage());
			return REDIRECT_PREFIX + "/";
		}

		validateFareFinderForm(fareFinderValidator, fareFinderForm, fareBindingResult, TraveladdonWebConstants.FARE_FINDER_FORM);
		if (fareBindingResult.hasErrors())
		{
			request.setAttribute(TravelcommonsWebConstants.TRAVEL_FINDER_FORM_BINDING_RESULT, fareBindingResult);
		}
		else
		{
			validateAccommodationFinderForm(accommodationFinderValidator, accommodationFinderForm, accommodationBindingResult,
					AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);

			if (accommodationBindingResult.hasErrors())
			{
				request.setAttribute(TravelcommonsWebConstants.TRAVEL_FINDER_FORM_BINDING_RESULT, accommodationBindingResult);
			}
			else
			{
				final String encodedPropertyName;
				if (StringUtils.isNotBlank(propertyName) && !validateFieldPattern(propertyName,
						TravelacceleratorstorefrontValidationConstants.REGEX_SPECIAL_LETTERS_NUMBER_SPACES))
				{
					model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PARAMS_ERROR,
							AccommodationaddonWebConstants.FILTER_PROPERTY_ERROR_MESSAGE);
					encodedPropertyName = StringUtils.EMPTY;
				}
				else
				{
					encodedPropertyName = StringUtils.trim(XSSFilterUtil.filter(propertyName));
				}
				model.addAttribute(AccommodationaddonWebConstants.FILTER_PROPERTY_NAME, encodedPropertyName);

				String validateSortCode = sortCode;
				if (StringUtils.isNotBlank(sortCode)
						&& !validateFieldPattern(sortCode, TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES))
				{
					model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PARAMS_ERROR,
							AccommodationaddonWebConstants.SORT_CODE_ERROR_MESSAGE);
					validateSortCode = null;
				}

				/*
				 * Below block is to re-set the facets whenever there is a change in the currency
				 */
				if (!StringUtils.equals(
						getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PREVIOUS_CURRENCY),
						storeSessionFacade.getCurrentCurrency().getIsocode()))
				{
					return getModifiedQueryString(request);
				}

				String validateQuery = query;
				if (StringUtils.isNotBlank(query)
						&& !validateFieldPattern(query, TravelacceleratorstorefrontValidationConstants.REGEX_QUERY_PARAMETER))
				{
					model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PARAMS_ERROR,
							AccommodationaddonWebConstants.FILTER_QUERY_ERROR_MESSAGE);
					validateQuery = null;
				}

				final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
				packageSearchRequestData.setCriterion(
						getCriterionData(accommodationFinderForm, request, encodedPropertyName, validateQuery, validateSortCode));
				final FareSearchRequestData fareSearchRequestData = prepareFareSearchRequestData(fareFinderForm, request);
				packageSearchRequestData.setFareSearchRequestData(fareSearchRequestData);

				if (StringUtils.isNotBlank(priceRange))
				{
					populatePriceRangeFilters(packageSearchRequestData, priceRange);
				}

				final PackageSearchResponseData packageSearchResponseData = packageSearchFacade.doSearch(packageSearchRequestData);
				populateModelWithPackagePriceRange(packageSearchResponseData, model, priceRange);

				final String packageDetailsUrlParams = buildPackageDetailsPageUrlParameters(accommodationFinderForm, fareFinderForm);

				getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_ACCOMMODATION_FINDER_FORM,
						accommodationFinderForm);
				getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_FINDER_FORM, fareFinderForm);
				getSessionService().setAttribute(TravelcommonsWebConstants.PACKAGE_SEARCH_RESPONSE, packageSearchResponseData);
				getSessionService().setAttribute(PACKAGE_DETAILS_URL_PARAMS, packageDetailsUrlParams);
				getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES,
						packageSearchResponseData.getProperties());

				populateCommonModelAttributes(0, pageNumber, resultsViewType, packageSearchResponseData, model);

				model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PARAMS,
						encodeSearchUrlToMapPackageStrategy.encode(packageSearchRequestData));
				model.addAttribute(TravelacceleratorstorefrontWebConstants.GOOGLE_API_KEY,
						getConfigurationService().getConfiguration().getString(TravelfacadesConstants.GOOGLE_API_KEY));
			}
		}

		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_SALES_APPLICATION,
				SalesApplication.WEB);
		return getViewForPage(model);

	}

	/**
	 * This method updates the original url with the selected view type
	 *
	 * @param resultsViewType
	 * @return
	 */
	private String updateOriginalReferer(final String originalUrl, final String resultsViewType)
	{
		final UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(originalUrl);
		urlBuilder.replaceQueryParam(RESULTS_VIEW_TYPE, resultsViewType);
		return urlBuilder.build().toUriString();
	}

	/**
	 * Initialize forms.
	 *
	 * @param fareFinderForm          the fare finder form
	 * @param accommodationFinderForm the accommodation finder form
	 * @param request                 the request
	 */
	protected void initializeForms(final FareFinderForm fareFinderForm, final AccommodationFinderForm accommodationFinderForm,
			final HttpServletRequest request) throws NumberFormatException
	{
		if (StringUtils.isEmpty(fareFinderForm.getArrivalLocationName()) || StringUtils
				.isEmpty(fareFinderForm.getDepartureLocationName()))
		{
			resolveLocationFields(fareFinderForm);
		}
		if (StringUtils.isEmpty(accommodationFinderForm.getDestinationLocationName()))
		{
			resolveDestinationLocation(fareFinderForm.getArrivalLocation(), fareFinderForm.getArrivalLocationSuggestionType(),
					accommodationFinderForm);
		}
		if (Objects.isNull(accommodationFinderForm.getRoomStayCandidates()))
		{
			accommodationFinderForm.setRoomStayCandidates(createRoomStayCandidatesForSearchPage(request));
			fareFinderForm.setPassengerTypeQuantityList(createPassengerTypeQuantityData(accommodationFinderForm.getNumberOfRooms(),
					accommodationFinderForm.getRoomStayCandidates()));
		}
	}

	/**
	 * Resolve location name fields for fareFinderForm.
	 *
	 * @param fareFinderForm the fare finder form
	 */
	protected void resolveLocationFields(final FareFinderForm fareFinderForm)
	{
		fareFinderForm.setDepartureLocationName(
				getLocationName(fareFinderForm.getDepartureLocation(), fareFinderForm.getDepartureLocationSuggestionType()));
		fareFinderForm.setArrivalLocationName(
				getLocationName(fareFinderForm.getArrivalLocation(), fareFinderForm.getArrivalLocationSuggestionType()));
	}

	/**
	 * This method resolves the destination location fields for accommodationFinderForm. It tries to resolve the location
	 * from the transport facility, if no result is found we fall back to Google search and use geographic coordinates
	 *
	 * @param arrivalLocation
	 * @param arrivalLocationSuggestionType
	 * @param accommodationFinderForm
	 */
	protected void resolveDestinationLocation(final String arrivalLocation, final String arrivalLocationSuggestionType,
			final AccommodationFinderForm accommodationFinderForm)
	{
		LocationData location = null;
		if (StringUtils.isNotBlank(arrivalLocationSuggestionType))
		{
			if (StringUtils.equalsIgnoreCase(SuggestionType.AIRPORTGROUP.toString(), arrivalLocationSuggestionType))
			{
				location = transportFacilityFacade.getLocation(arrivalLocation);
			}
			else if (StringUtils.equalsIgnoreCase(SuggestionType.CITY.toString(), arrivalLocationSuggestionType))
			{
				location = travelLocationFacade.getLocation(arrivalLocation);
			}
		}

 		List<GlobalSuggestionData> suggestionResults = Objects.nonNull(location) ?
				accommodationSuggestionFacade.getLocationSuggestions(location.getName()) :
				Collections.emptyList();

		if (CollectionUtils.isNotEmpty(suggestionResults))
		{
			final GlobalSuggestionData firstValidResult = suggestionResults.stream().findFirst().get();
			accommodationFinderForm.setDestinationLocation(firstValidResult.getCode());
			accommodationFinderForm.setDestinationLocationName(firstValidResult.getName());
			accommodationFinderForm.setSuggestionType(SuggestionType.LOCATION.toString());
		}
		else
		{
			suggestionResults = accommodationAutoSuggestWrapper.getAutoCompleteResults(arrivalLocation);
			if (CollectionUtils.isNotEmpty(suggestionResults))
			{
				final GlobalSuggestionData firstValidResult = suggestionResults.stream().findFirst().get();
				accommodationFinderForm.setDestinationLocationName(firstValidResult.getName());
				accommodationFinderForm.setLongitude(String.valueOf(firstValidResult.getLongitude()));
				accommodationFinderForm.setLatitude(String.valueOf(firstValidResult.getLatitude()));
				accommodationFinderForm.setRadius(String.valueOf(firstValidResult.getRadius()));
				accommodationFinderForm.setSuggestionType(StringUtils.EMPTY);
			}

		}
	}

	/**
	 * Gets the result view type.
	 *
	 * @param resultViewType the result view type
	 * @return the result view type
	 */
	protected String getResultViewType(final String resultViewType)
	{
		if (StringUtils
				.equalsIgnoreCase(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_DEFAULT,
						resultViewType) || StringUtils
				.equalsIgnoreCase(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_GRID,
						resultViewType) || StringUtils
				.equalsIgnoreCase(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_MAP,
						resultViewType))
		{
			return resultViewType;
		}
		return TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_DEFAULT;
	}

	/**
	 * Prepare accommodation search request data accommodation search request data.
	 *
	 * @param accommodationFinderForm the accommodation finder form
	 * @param request                 the request
	 * @param propertyName            the property name
	 * @param query                   the query
	 * @param sortCode                the sort code
	 * @return the accommodation search request data
	 */
	protected CriterionData getCriterionData(final AccommodationFinderForm accommodationFinderForm,
			final HttpServletRequest request, final String propertyName, final String query, final String sortCode)
	{
		final CriterionData criterionData = new CriterionData();

		//set Address Data
		final SearchAddressData addressData = new SearchAddressData();
		addressData.setFormattedIndicator(false);
		addressData.setLine1(accommodationFinderForm.getDestinationLocation());
		addressData.setLine2(accommodationFinderForm.getDestinationLocationName());
		criterionData.setAddress(addressData);

		// set Facility Data
		final List<FacilityData> amenities = new ArrayList<>();
		criterionData.setAmenities(amenities);

		// set Position Data
		if (StringUtils.isNotEmpty(accommodationFinderForm.getLatitude()) && StringUtils
				.isNotEmpty(accommodationFinderForm.getLongitude()))
		{
			final PositionData positionData = new PositionData();
			positionData.setLongitude(Double.parseDouble(accommodationFinderForm.getLongitude()));
			positionData.setLatitude(Double.parseDouble(accommodationFinderForm.getLatitude()));
			criterionData.setPosition(positionData);
		}
		// set Radius Data
		if (StringUtils.isNotEmpty(accommodationFinderForm.getRadius()))
		{
			final RadiusData radiusData = new RadiusData();
			radiusData.setValue(Double.parseDouble(accommodationFinderForm.getRadius()));
			criterionData.setRadius(radiusData);
		}

		// set Stay Range Data
		final StayDateRangeData stayRangeData = new StayDateRangeData();
		stayRangeData.setStartTime(TravelDateUtils
				.convertStringDateToDate(accommodationFinderForm.getCheckInDateTime(), TravelservicesConstants.DATE_PATTERN));
		stayRangeData.setEndTime(TravelDateUtils
				.convertStringDateToDate(accommodationFinderForm.getCheckOutDateTime(), TravelservicesConstants.DATE_PATTERN));
		criterionData.setStayDateRange(stayRangeData);

		// set roomStayCandidates
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		try
		{
			if (!CollectionUtils.isEmpty(accommodationFinderForm.getRoomStayCandidates()))
			{
				IntStream.range(0, Integer.parseInt(accommodationFinderForm.getNumberOfRooms())).forEach(index ->
				{
					final RoomStayCandidateData roomStayCandidateData = accommodationFinderForm.getRoomStayCandidates().get(index);
					roomStayCandidates.add(roomStayCandidateData);
				});
			}
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer" + e.getClass().getName() + " : " + e.getMessage());
		}
		criterionData.setRoomStayCandidates(roomStayCandidates);

		criterionData.setPropertyFilterText(propertyName);
		criterionData.setQuery(query);
		criterionData.setSort(sortCode);
		criterionData.setSuggestionType(accommodationFinderForm.getSuggestionType());

		return criterionData;
	}

	/**
	 * Build package details page url parameters string.
	 *
	 * @param accommodationFinderForm accommodationFinderForm
	 * @param fareFinderForm          fareFinderForm
	 * @return the string
	 */
	protected String buildPackageDetailsPageUrlParameters(final AccommodationFinderForm accommodationFinderForm,
			final FareFinderForm fareFinderForm)
	{
		final StringBuilder urlParameters = new StringBuilder();

		urlParameters.append(TravelacceleratorstorefrontWebConstants.PART_HOTEL_STAY);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(accommodationFinderForm.getPartHotelStay());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION,
				fareFinderForm.getDepartureLocation());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION_SUGGESTION_TYPE,
				fareFinderForm.getDepartureLocationSuggestionType());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION,
				fareFinderForm.getArrivalLocation());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_SUGGESTION_TYPE,
				fareFinderForm.getArrivalLocationSuggestionType());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.DEPARTING_DATE_TIME,
				fareFinderForm.getDepartingDateTime());

		if (StringUtils.equalsIgnoreCase(TripType.RETURN.toString(), fareFinderForm.getTripType()))
		{
			appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.RETURN_DATE_TIME,
					fareFinderForm.getReturnDateTime());
		}

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.TRIP_TYPE, fareFinderForm.getTripType());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.CABIN_CLASS, fareFinderForm.getCabinClass());

		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.CHECKIN_DATE,
				accommodationFinderForm.getCheckInDateTime());
		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.CHECKOUT_DATE,
				accommodationFinderForm.getCheckOutDateTime());
		appendParameter(urlParameters, TravelacceleratorstorefrontWebConstants.NUMBER_OF_ROOMS,
				accommodationFinderForm.getNumberOfRooms());

		try
		{
			final int numberOfRooms = Integer.parseInt(accommodationFinderForm.getNumberOfRooms());
			for (int i = 0; i < numberOfRooms; i++)
			{
				final StringBuilder guestsStringPerRoom = new StringBuilder();
				final List<PassengerTypeQuantityData> guestCounts = accommodationFinderForm.getRoomStayCandidates().get(i)
						.getPassengerTypeQuantityList();
				for (final PassengerTypeQuantityData guestCount : guestCounts)
				{
					final String passengerType = guestCount.getPassengerType().getCode();
					final int passengerQuantity = guestCount.getQuantity();
					final String guestParam = String.valueOf(passengerQuantity) + "-" + passengerType;
					guestsStringPerRoom.append(guestParam);
					guestsStringPerRoom.append(",");
				}
				String result = guestsStringPerRoom.toString();
				result = result.substring(0, result.length() - 1);

				urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
				urlParameters.append(ROOM_QUERY_STRING_INDICATOR).append(i);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				urlParameters.append(result);
			}
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer" + e.getClass().getName() + " : " + e.getMessage());
			LOG.debug(e);
		}

		return urlParameters.toString();
	}

	protected void appendParameter(final StringBuilder urlParameters, final String parameter, final String value)
	{
		urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
		urlParameters.append(parameter);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(value);
	}

	/**
	 * Update criterion data for price range filter.
	 *
	 * @param packageSearchResponseData
	 *           the package search response data
	 * @param model
	 *           the model
	 * @param lowerPriceRange
	 *           the lower price range
	 * @param upperPriceRange
	 *           the upper price range
	 */
	protected void populateModelWithPackagePriceRange(final PackageSearchResponseData packageSearchResponseData, final Model model,
			final String priceRange)
	{
		if (StringUtils.isNotBlank(priceRange))
		{
			model.addAttribute(PRICE_RANGE, priceRange);
		}

		final List<PropertyData> packageSearchResponseProperties = Objects.nonNull(packageSearchResponseData)
				? packageSearchResponseData.getProperties() : Collections.emptyList();

		/* to min total package price */
		long minPackagePrice = 0;
		final PackageData minPricedPackage = packageSearchFacade.getMinPricedPackage(packageSearchResponseProperties);
		if (Objects.nonNull(minPricedPackage))
		{
			minPackagePrice = Objects.nonNull(minPricedPackage.getTotalPackagePrice())
					? minPricedPackage.getTotalPackagePrice().getValue().longValue() : 0;
		}

		/* to max total package price */
		long maxPackagePrice = 0;
		final PackageData maxPricedPackage = packageSearchFacade.getMaxPricedPackage(packageSearchResponseProperties);
		if (Objects.nonNull(maxPricedPackage))
		{
			final double totalPackagePrice = Objects.nonNull(maxPricedPackage.getTotalPackagePrice())
					? maxPricedPackage.getTotalPackagePrice().getValue().doubleValue() : 0;
			maxPackagePrice = (long) Math.ceil(totalPackagePrice);
		}

		model.addAttribute(MIN_PACKAGE_PRICE, minPackagePrice);
		model.addAttribute(MAX_PACKAGE_PRICE, maxPackagePrice);
	}

	/**
	 * Prepare customer review data json response.
	 *
	 * @param accommodationOfferingCode the accommodation offering code
	 * @param model                     the model
	 * @return the string
	 */
	@RequestMapping("/customer-review/{accommodationOfferingCode}")
	public String prepareCustomerReviewData(@PathVariable final String accommodationOfferingCode, final Model model)
	{

		final int noOfReviews = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.ACCOMMODATION_OFFERING_NUMBER_OF_REVIEWS);

		final PageableData pageableData = createPageableData(0, noOfReviews, "byDate");

		final SearchPageData<ReviewData> customerReviewsSearchPageData = getAccommodationOfferingCustomerReviewFacade()
				.getAccommodationOfferingCustomerReviewDetails(accommodationOfferingCode, pageableData);

		final PropertyData propertyData = getAccommodationOfferingFacade().getPropertyData(accommodationOfferingCode);
		model.addAttribute(AccommodationaddonWebConstants.PROPERTY, propertyData);

		model.addAttribute(AccommodationaddonWebConstants.CUSTOMER_REVIEW_SEARCH_PAGE_DATA, customerReviewsSearchPageData);

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.CustomerReviewJsonResponse;
	}

	/**
	 * Display method to return the properties to mark on Map.
	 *
	 * @return the string
	 */
	@RequestMapping("/display-view")
	public String displayView(final Model model,
			@RequestParam(value = "resultsViewType", required = false, defaultValue = "listView") final String resultsViewType,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNumber,
			@RequestParam(value = PRICE_RANGE, required = false) final String priceRange,
			final HttpServletRequest request)
	{
		request.getSession().setAttribute(ORIGINAL_REFERER,
				updateOriginalReferer(String.valueOf(request.getSession().getAttribute(ORIGINAL_REFERER)), resultsViewType));
		final PackageSearchResponseData packageSearchResponseData = getPackageSearchWithAppliedPriceRange(priceRange);
		populateCommonModelAttributes(0, pageNumber, resultsViewType, packageSearchResponseData, model);
		return TravelcommonsControllerConstants.Views.Pages.PackageSearch.packageResultsViewJsonResponse;
	}

	/**
	 * Show more method to return lazy loaded properties.
	 *
	 * @param pageNumber the page number
	 * @param model      the model
	 * @return the string
	 */
	@RequestMapping("/show-more")
	public String showMore(@RequestParam(value = "pageNumber", required = false) final int pageNumber,
			@RequestParam(value = "resultsViewType", required = false, defaultValue = "listView") final String resultsViewType,
			@RequestParam(value = PRICE_RANGE, required = false) final String priceRange,
			final Model model)
	{
		final PackageSearchResponseData packageSearchResponseData = getPackageSearchWithAppliedPriceRange(priceRange);
		populateCommonModelAttributes(pageNumber - 1, pageNumber, resultsViewType, packageSearchResponseData, model);
		return TravelcommonsControllerConstants.Views.Pages.PackageSearch.packageListingJsonResponse;
	}

	/**
	 * Gets the package search results page size.
	 *
	 * @param resultsViewType
	 *           the results view type
	 * @return the package search results page size
	 */
	protected int getPackageSearchResultsPageSize(final String resultsViewType)
	{
		if (StringUtils.equalsIgnoreCase(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_MAP,
				resultsViewType))
		{
			return MAX_PAGE_LIMIT;
		}

		int packageSearchPageSize = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.ACCOMMODATION_SEARCH_RESULT_PAGE_SIZE);
		packageSearchPageSize = packageSearchPageSize > 0 ? packageSearchPageSize : MAX_PAGE_LIMIT;
		return packageSearchPageSize;
	}


	/**
	 * Populate common model attributes.
	 *
	 * @param pageNumber
	 *           the page number
	 * @param resultsViewType
	 *           the results view type
	 * @param packageSearchResponseData
	 *           the package search response data
	 * @param model
	 *           the model
	 */
	protected void populateCommonModelAttributes(final int startIndex, final int pageNumber, final String resultsViewType,
			final PackageSearchResponseData packageSearchResponseData,
			final Model model)
	{
		final List<PropertyData> packageSearchResponseProperties = packageSearchResponseData.getProperties().stream()
				.filter(propertyData -> (propertyData instanceof PackageData && ((PackageData) propertyData).getFiltered()))
				.collect(Collectors.toList());
		final int packageSearchPageSize = getPackageSearchResultsPageSize(resultsViewType);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES,
				packageSearchResponseProperties.subList(startIndex * packageSearchPageSize,
						Math.min(pageNumber * packageSearchPageSize, packageSearchResponseProperties.size())));
		model.addAttribute(TravelcommonsWebConstants.PACKAGE_SEARCH_RESPONSE, packageSearchResponseData);
		model.addAttribute(TravelcommonsWebConstants.PACKAGE_DETAILS_URL_PARAMETERS,
				getSessionService().getAttribute(PACKAGE_DETAILS_URL_PARAMS));
		model.addAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE,
				getResultViewType(resultsViewType));

		model.addAttribute(PAGE_NUM, pageNumber);
		model.addAttribute(TOTAL_NUMBER_OF_RESULTS, packageSearchResponseProperties.size());
		final Boolean hasMoreResults = pageNumber * packageSearchPageSize < packageSearchResponseProperties.size();
		model.addAttribute(HAS_MORE_RESULTS, hasMoreResults);
		model.addAttribute(TOTAL_SHOWN_RESULTS,
				hasMoreResults ? pageNumber * packageSearchPageSize : packageSearchResponseProperties.size());
	}

	/**
	 * Below method clears the q and propertyName request parameters from the query string as they are supposed to be
	 * cleared on currency change
	 *
	 * @param request
	 * @return
	 */
	protected String getModifiedQueryString(final HttpServletRequest request)
	{
		final StringBuilder urlParameters = new StringBuilder();
		final Map<String, String[]> map = request.getParameterMap();
		final Set<String> keys = map.keySet();
		for (final String key : keys)
		{
			if (StringUtils.equals(QUERY, key) || StringUtils.equals(PROPERTY_NAME, key) || StringUtils.equals(PRICE_RANGE, key))
			{
				continue;
			}
			urlParameters.append(key);
			urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			String value = map.get(key)[0];
			if (value.contains("|"))
			{
				value = value.replaceAll("\\|", "%7C");
			}
			urlParameters.append(value);
			urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
		}
		String urlParametersString = urlParameters.toString();
		urlParametersString = urlParametersString.substring(0, urlParametersString.length() - 1);
		return REDIRECT_PREFIX + TravelcommonsWebConstants.PACKAGE_LISTING_PATH + "?" + urlParametersString;
	}

	/**
	 * Gets the price range filtered packages.
	 *
	 * @param lowerPriceRange
	 *           the lower price range
	 * @param upperPriceRange
	 *           the upper price range
	 * @param resultsViewType
	 *           the results view type
	 * @param pageNumber
	 *           the page number
	 * @param model
	 *           the model
	 * @return the price range filtered packages
	 */
	@RequestMapping("/filter-price-range")
	public String getPriceRangeFilteredPackages(@RequestParam(value = PRICE_RANGE, required = false) final String priceRange,
			@RequestParam(value = "resultsViewType", required = false, defaultValue = "listView") final String resultsViewType,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNumber, final Model model)
	{
		final PackageSearchResponseData packageSearchResponseData = getPackageSearchWithAppliedPriceRange(priceRange);
		populateCommonModelAttributes(0, pageNumber, resultsViewType, packageSearchResponseData, model);

		if (StringUtils.equalsIgnoreCase(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_MAP,
				resultsViewType))
		{
			return TravelcommonsControllerConstants.Views.Pages.PackageSearch.packageResultsViewJsonResponse;
		}

		return TravelcommonsControllerConstants.Views.Pages.PackageSearch.packageListingJsonResponse;
	}

	/**
	 * Gets the package search with applied price range.
	 *
	 * @param priceRange
	 *           the price range
	 * @return the package search with applied price range
	 */
	protected PackageSearchResponseData getPackageSearchWithAppliedPriceRange(final String priceRange)
	{
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		if (StringUtils.isNotBlank(priceRange))
		{
			populatePriceRangeFilters(packageSearchRequestData, priceRange);
		}
		final PackageSearchResponseData packageSearchResponseData = packageSearchFacade
				.getFilteredPackageResponseFilteredByPriceRange(packageSearchRequestData);
		return packageSearchResponseData;
	}

	/**
	 * Populate price range filters.
	 *
	 * @param packageSearchRequestData
	 *           the package search request data
	 * @param priceRange
	 *           the price range
	 */
	private void populatePriceRangeFilters(final PackageSearchRequestData packageSearchRequestData, final String priceRange)
	{
		final String[] rangeArr = priceRange.split(HYPHEN_SEPERATOR);
		long lowerPriceRange = 0;
		long upperPriceRange = 0;
		if (CollectionUtils.size(rangeArr) > 1)
		{
			try
			{
				lowerPriceRange = Long.valueOf(StringUtils.substring(rangeArr[0], 1));
				upperPriceRange = Long.valueOf(StringUtils.substring(rangeArr[1], 1));
				packageSearchRequestData.setMinPrice(lowerPriceRange);
				packageSearchRequestData.setMaxPrice(upperPriceRange);
			}
			catch (final NumberFormatException e)
			{
				LOG.error("Cannot parse price range limit string to integer" + e.getClass().getName() + " : " + e.getMessage());
			}
		}
	}

	/**
	 * Validate field pattern boolean.
	 *
	 * @param attribute
	 *           the attribute
	 * @param pattern
	 *           the pattern
	 * @return the boolean
	 */
	protected Boolean validateFieldPattern(final String attribute, final String pattern)
	{
		if (!attribute.matches(pattern))
		{
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Validate accommodation finder form.
	 *
	 * @param accommodationFinderValidator
	 *           the accommodation finder validator
	 * @param accommodationFinderForm
	 *           the accommodation finder form
	 * @param bindingResult
	 *           the binding result
	 * @param formName
	 *           the form name
	 */
	protected void validateAccommodationFinderForm(final AbstractTravelValidator accommodationFinderValidator,
			final AccommodationFinderForm accommodationFinderForm, final BindingResult bindingResult, final String formName)
	{
		accommodationFinderValidator.setTargetForm(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		accommodationFinderValidator.setAttributePrefix("");
		accommodationFinderValidator.validate(accommodationFinderForm, bindingResult);
	}

	/**
	 * Validate fare finder form.
	 *
	 * @param fareFinderValidator
	 *           the fare finder validator
	 * @param fareFinderForm
	 *           the fare finder form
	 * @param bindingResult
	 *           the binding result
	 * @param formName
	 *           the form name
	 */
	protected void validateFareFinderForm(final AbstractTravelValidator fareFinderValidator, final FareFinderForm fareFinderForm,
			final BindingResult bindingResult, final String formName)
	{
		fareFinderValidator.setTargetForm(TraveladdonWebConstants.FARE_FINDER_FORM);
		fareFinderValidator.setAttributePrefix("");
		fareFinderValidator.validate(fareFinderForm, bindingResult);
	}
}
