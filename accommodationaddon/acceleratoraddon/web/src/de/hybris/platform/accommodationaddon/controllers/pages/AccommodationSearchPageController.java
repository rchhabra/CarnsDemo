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

package de.hybris.platform.accommodationaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationFinderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RadiusData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.accommodation.user.data.SearchAddressData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.search.AccommodationSearchFacade;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.EncodeSearchUrlToMapStrategy;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
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
@RequestMapping("/accommodation-search")
public class AccommodationSearchPageController extends AbstractAccommodationPageController
{
	private static final Logger LOG = Logger.getLogger(AccommodationSearchPageController.class);

	private static final String ACCOMMODATION_SEARCH_CMS_PAGE = "accommodationSearchPage";
	private static final String ROOM_QUERY_STRING_INDICATOR = "r";
	private static final String TOTAL_NUMBER_OF_RESULTS = "totalNumberOfResults";
	private static final String TOTAL_SHOWN_RESULTS = "totalshownResults";
	private static final String PAGE_NUM = "pageNum";
	private static final String HAS_MORE_RESULTS = "hasMoreResults";
	private static final String ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES = "accommodationSearchResponseProperties";
	private static final String ACCOMMODATION_DETAILS_URL_PARAMS = "accommodationDetailsUrlParams";
	private static final String ACCOMMODATION_SEARCH_ROOT = "/accommodation-search";
	private static final String PROPERTY_NAME = "propertyName";
	private static final String QUERY = "q";
	public static final String ORIGINAL_REFERER = "originalReferer";
	public static final String RESULTS_VIEW_TYPE = "resultsViewType";

	@Resource(name = "accommodationFinderValidator")
	private Validator accommodationFinderValidator;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "accommodationSearchFacade")
	private AccommodationSearchFacade accommodationSearchFacade;

	@Resource(name = "encodeSearchUrlToMapStrategy")
	private EncodeSearchUrlToMapStrategy encodeSearchUrlToMapStrategy;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	/**
	 * Gets accommodation search page.
	 *
	 * @param accommodationFinderForm
	 *           the accommodation finder form
	 * @param propertyName
	 *           the property name
	 * @param query
	 *           the query
	 * @param sortCode
	 *           the sort code
	 * @param pageNumber
	 *           the page number
	 * @param bindingResult
	 *           the binding result
	 * @param model
	 *           the model
	 * @param request
	 *           the request
	 * @return the accommodation search page
	 * @throws CMSItemNotFoundException
	 *            the cms item not found exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getAccommodationSearchPage(
			@Valid @ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM) final AccommodationFinderForm accommodationFinderForm,
			@RequestParam(value = "propertyName", required = false) final String propertyName,
			@RequestParam(value = "q", required = false) final String query,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNumber,
			@RequestParam(value = "resultsViewType", required = false, defaultValue = "listView") final String resultsViewType,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOMMODATION_SEARCH_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOMMODATION_SEARCH_CMS_PAGE));

		adjustSessionBookingJourney();

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
			if (StringUtils.equalsIgnoreCase(sessionBookingJourney,
					TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION))
			{
				final String checkInDate = accommodationFinderForm.getCheckInDateTime();
				final String checkOutDate = accommodationFinderForm.getCheckOutDateTime();

				final String newQueryString = checkDatesAndGetNewQueryString(checkInDate, checkOutDate, request.getQueryString());
				if (StringUtils.isNotBlank(newQueryString))
				{
					return REDIRECT_PREFIX + ACCOMMODATION_SEARCH_ROOT + "?" + newQueryString;
				}
			}
		}
		else
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		}

		/*
		 * Below block is to re-set the facets whenever there is a change in the currency
		 */
		if (!StringUtils.equals(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PREVIOUS_CURRENCY),
				storeSessionFacade.getCurrentCurrency().getIsocode()))
		{
			return getModifiedQueryString(request);
		}

		/*
		 * below code is required because if a person hits accommodation-search url directly, roomStayCandidates will be
		 * empty as it is not set in the url as per its respective form field name
		 */
		if (accommodationFinderForm.getRoomStayCandidates() == null)
		{
			accommodationFinderForm.setRoomStayCandidates(createRoomStayCandidatesForSearchPage(request));
		}

		accommodationFinderValidator.validate(accommodationFinderForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			request.setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM_BINDING_RESULT, bindingResult);
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

			String validateQuery = query;
			if (StringUtils.isNotBlank(query)
					&& !validateFieldPattern(query, TravelacceleratorstorefrontValidationConstants.REGEX_QUERY_PARAMETER))
			{
				model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PARAMS_ERROR,
						AccommodationaddonWebConstants.FILTER_QUERY_ERROR_MESSAGE);
				validateQuery = null;
			}

			final AccommodationSearchRequestData accommodationSearchRequestData = prepareAccommodationSearchRequestData(
					accommodationFinderForm, request, encodedPropertyName, validateQuery, validateSortCode);

			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PARAMS,
					encodeSearchUrlToMapStrategy.encode(accommodationSearchRequestData));

			final AccommodationSearchResponseData accommodationSearchResponseData = accommodationSearchFacade
					.doSearch(accommodationSearchRequestData);

			final List<PropertyData> searchResponseProperties = accommodationSearchResponseData.getProperties();
			sessionService.setAttribute(ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES, searchResponseProperties);

			final int accommodationSearchPageSize = getAccommodationSearchResultsPageSize();

			accommodationSearchResponseData.setProperties(searchResponseProperties.subList(0,
					Math.min(pageNumber * accommodationSearchPageSize, searchResponseProperties.size())));

			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE, accommodationSearchResponseData);
			sessionService.setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE,
					accommodationSearchResponseData);
			populateModelWithShowMoreInfo(pageNumber, model, searchResponseProperties, accommodationSearchPageSize);


			final String accommodationDetailsUrlParams = buildAccommodationOfferingDetailsPageUrlParameters(
					accommodationSearchRequestData.getCriterion());

			sessionService.setAttribute(ACCOMMODATION_DETAILS_URL_PARAMS, accommodationDetailsUrlParams);

			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_OFFERING_DETAILS_URL_PARAMETERS,
					accommodationDetailsUrlParams);

			model.addAttribute(TravelacceleratorstorefrontWebConstants.GOOGLE_API_KEY,
					getConfigurationService().getConfiguration().getString(TravelfacadesConstants.GOOGLE_API_KEY));
		}

		request.setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM, accommodationFinderForm);
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_ACCOMMODATION_FINDER_FORM,
				accommodationFinderForm);

		model.addAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE,
				getResultViewType(resultsViewType));
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_SALES_APPLICATION,
				SalesApplication.WEB);
		return getViewForPage(model);

	}

	protected String getResultViewType(final String resultViewType)
	{
		if (StringUtils.equalsIgnoreCase(
				TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_DEFAULT, resultViewType)
				|| StringUtils.equalsIgnoreCase(
						TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_GRID, resultViewType)
				|| StringUtils.equalsIgnoreCase(
						TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_MAP, resultViewType))
		{
			return resultViewType;
		}
		return TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE_DEFAULT;
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
			if (StringUtils.equals(QUERY, key) || StringUtils.equals(PROPERTY_NAME, key))
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
		return REDIRECT_PREFIX + AccommodationaddonWebConstants.ACCOMMODATION_SELECTION_ROOT_URL + "?" + urlParametersString;
	}


	/**
	 * Prepare accommodation search request data accommodation search request data.
	 *
	 * @param accommodationFinderForm
	 *           the accommodation finder form
	 * @param request
	 *           the request
	 * @param propertyName
	 *           the property name
	 * @param query
	 *           the query
	 * @param sortCode
	 *           the sort code
	 * @return the accommodation search request data
	 */
	protected AccommodationSearchRequestData prepareAccommodationSearchRequestData(
			final AccommodationFinderForm accommodationFinderForm, final HttpServletRequest request, final String propertyName,
			final String query, final String sortCode)
	{
		final AccommodationSearchRequestData accommodationSearchRequestData = new AccommodationSearchRequestData();

		//create Criterion Data
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
		if (StringUtils.isNotEmpty(accommodationFinderForm.getLatitude())
				&& StringUtils.isNotEmpty(accommodationFinderForm.getLongitude()))
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
		stayRangeData.setStartTime(TravelDateUtils.convertStringDateToDate(accommodationFinderForm.getCheckInDateTime(),
				TravelservicesConstants.DATE_PATTERN));
		stayRangeData.setEndTime(TravelDateUtils.convertStringDateToDate(accommodationFinderForm.getCheckOutDateTime(),
				TravelservicesConstants.DATE_PATTERN));
		stayRangeData.setLengthOfStay((int) TravelDateUtils.getDaysBetweenDates(stayRangeData.getStartTime(), stayRangeData.getEndTime()));
		criterionData.setStayDateRange(stayRangeData);

		// set roomStayCandidates
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		try
		{
			if (!CollectionUtils.isEmpty(accommodationFinderForm.getRoomStayCandidates()))
			{
				IntStream.range(0, Integer.parseInt(accommodationFinderForm.getNumberOfRooms())).forEach(index -> {
					final RoomStayCandidateData roomStayCandidateData = accommodationFinderForm.getRoomStayCandidates().get(index);
					roomStayCandidates.add(roomStayCandidateData);
				});
			}
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer");
			LOG.error(e.getClass().getName() + " : " + e.getMessage());
			LOG.debug(e);
		}
		criterionData.setRoomStayCandidates(roomStayCandidates);

		criterionData.setPropertyFilterText(propertyName);

		criterionData.setQuery(query);
		criterionData.setSort(sortCode);
		criterionData.setSuggestionType(accommodationFinderForm.getSuggestionType());

		accommodationSearchRequestData.setCriterion(criterionData);

		return accommodationSearchRequestData;
	}

	/**
	 * Build accommodation offering details page url parameters string.
	 *
	 * @param criterion
	 *           the criterion
	 * @return the string
	 */
	protected String buildAccommodationOfferingDetailsPageUrlParameters(final CriterionData criterion)
	{
		final StringBuilder urlParameters = new StringBuilder();

		urlParameters.append(TravelacceleratorstorefrontValidationConstants.CHECKIN_DATE);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(TravelDateUtils.convertDateToStringDate(criterion.getStayDateRange().getStartTime(),
				TravelservicesConstants.DATE_PATTERN));
		urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

		urlParameters.append(TravelacceleratorstorefrontValidationConstants.CHECKOUT_DATE);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(TravelDateUtils.convertDateToStringDate(criterion.getStayDateRange().getEndTime(),
				TravelservicesConstants.DATE_PATTERN));
		urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

		urlParameters.append(TravelacceleratorstorefrontValidationConstants.NUMBER_OF_ROOMS);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(criterion.getRoomStayCandidates().size());

		criterion.getRoomStayCandidates().forEach(candidate -> {
			urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
			urlParameters.append(ROOM_QUERY_STRING_INDICATOR);
			urlParameters.append(candidate.getRoomStayCandidateRefNumber().toString());
			urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			final StringJoiner joiner = new StringJoiner(TravelacceleratorstorefrontWebConstants.COMMA);
			candidate.getPassengerTypeQuantityList().forEach(passenger -> {
				joiner.add(passenger.getQuantity() + TravelacceleratorstorefrontWebConstants.HYPHEN + passenger.getPassengerType().getCode());
			});
			urlParameters.append(joiner.toString());
		});
		return urlParameters.toString();
	}

	/**
	 * Prepare customer review data json response.
	 *
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param model
	 *           the model
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
			final HttpServletRequest request)
	{
		request.getSession().setAttribute(ORIGINAL_REFERER,
				updateOriginalReferer(String.valueOf(request.getSession().getAttribute(ORIGINAL_REFERER)), resultsViewType));
		final List<PropertyData> searchResponseProperties = sessionService.getAttribute(ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES);
		sessionService.setAttribute(ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES, searchResponseProperties);

		final int accommodationSearchPageSize = getAccommodationSearchResultsPageSize();
		final AccommodationSearchResponseData accommodationSearchResponseData = sessionService
				.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE);
		accommodationSearchResponseData.setProperties(searchResponseProperties.subList(0,
				Math.min(pageNumber * accommodationSearchPageSize, searchResponseProperties.size())));
		sessionService.setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE, accommodationSearchResponseData);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PROPERTIES, searchResponseProperties);
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_OFFERING_DETAILS_URL_PARAMETERS,
				sessionService.getAttribute(ACCOMMODATION_DETAILS_URL_PARAMS));
		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE, accommodationSearchResponseData);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE,
				getResultViewType(resultsViewType));
		populateModelWithShowMoreInfo(pageNumber, model, searchResponseProperties,
				accommodationSearchPageSize);
		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AccommodationResultsViewJsonResponse;
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
	 * Show more method to return lazy loaded properties.
	 *
	 * @param pageNumber
	 *           the page number
	 * @param model
	 *           the model
	 * @return the string
	 */
	@RequestMapping("/show-more")
	public String showMore(@RequestParam(value = "pageNumber", required = false) final int pageNumber,
			@RequestParam(value = "resultsViewType", required = false, defaultValue = "listView") final String resultsViewType,
			final Model model)
	{
		final List<PropertyData> accommodationSearchSessionProperties = sessionService
				.getAttribute(ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES);

		final int accommodationSearchPageSize = getAccommodationSearchResultsPageSize();

		if (CollectionUtils.isNotEmpty(accommodationSearchSessionProperties))
		{
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_PROPERTIES,
					accommodationSearchSessionProperties.subList((pageNumber - 1) * accommodationSearchPageSize,
							Math.min(pageNumber * accommodationSearchPageSize, accommodationSearchSessionProperties.size())));
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE,
					sessionService.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_SEARCH_RESPONSE));
		}
		populateModelWithShowMoreInfo(pageNumber, model, accommodationSearchSessionProperties, accommodationSearchPageSize);

		final String accommodationDetailsUrlParams = sessionService.getAttribute(ACCOMMODATION_DETAILS_URL_PARAMS);

		if (StringUtils.isNotEmpty(accommodationDetailsUrlParams))
		{
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_OFFERING_DETAILS_URL_PARAMETERS,
					accommodationDetailsUrlParams);
		}
		model.addAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_LISTING_PAGE_RESULT_VIEW_TYPE,
				getResultViewType(resultsViewType));
		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AccommodationListingJsonResponse;
	}

	/**
	 * Gets the accommodation search results page size.
	 *
	 * @return the accommodation search results page size
	 */
	protected int getAccommodationSearchResultsPageSize()
	{
		int accommodationSearchPageSize = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.ACCOMMODATION_SEARCH_RESULT_PAGE_SIZE);
		accommodationSearchPageSize = accommodationSearchPageSize > 0 ? accommodationSearchPageSize : MAX_PAGE_LIMIT;
		return accommodationSearchPageSize;
	}


	/**
	 * Populate model with show more info.
	 *
	 * @param pageNumber
	 *           the page number
	 * @param model
	 *           the model
	 * @param properties
	 *           the properties
	 * @param accommodationSearchPageSize
	 *           the accommodation search page size
	 */
	private void populateModelWithShowMoreInfo(final int pageNumber, final Model model, final List<PropertyData> properties,
			final int accommodationSearchPageSize)
	{
		model.addAttribute(PAGE_NUM, pageNumber);
		model.addAttribute(TOTAL_NUMBER_OF_RESULTS, properties.size());
		final Boolean hasMoreResults = pageNumber * accommodationSearchPageSize < properties.size();
		model.addAttribute(HAS_MORE_RESULTS, hasMoreResults);

		if (hasMoreResults)
		{
			model.addAttribute(TOTAL_SHOWN_RESULTS, pageNumber * accommodationSearchPageSize);
		}
		else
		{
			model.addAttribute(TOTAL_SHOWN_RESULTS, properties.size());
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
	 * Gets accommodation search facade.
	 *
	 * @return the accommodation search facade
	 */
	public AccommodationSearchFacade getAccommodationSearchFacade()
	{
		return accommodationSearchFacade;
	}

	/**
	 * Sets accommodation search facade.
	 *
	 * @param accommodationSearchFacade
	 *           the accommodation search facade
	 */
	public void setAccommodationSearchFacade(final AccommodationSearchFacade accommodationSearchFacade)
	{
		this.accommodationSearchFacade = accommodationSearchFacade;
	}

}
