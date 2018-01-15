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

import de.hybris.platform.accommodationaddon.forms.cms.AccommodationAvailabilityForm;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingCustomerReviewFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Abstract accommodation page controller to handle common operations
 */
public class AbstractAccommodationPageController extends TravelAbstractPageController
{
	private static final Logger LOG = Logger.getLogger(AbstractAccommodationPageController.class);
	private static final String NUMBER_OF_ROOMS = "numberOfRooms";
	protected static final int MAX_PAGE_LIMIT = 100;
	protected static final String TOTAL_NUMBER_OF_REVIEWS = "totalNumberOfReviews";
	protected static final String ERROR_AMEND_BOOKING_EMPTY = "empty";
	protected static final String ERROR_AMEND_BOOKING_DATES = "dates";
	protected static final String ERROR_AMEND_BOOKING_DATES_PARSE = "dates.parse";
	protected static final String ERROR_AMEND_BOOKING_ORDER_ID = "booking.reference";
	protected static final String ERROR_AMEND_BOOKING_ORDER_PRICE_CALCULATION = "order.price.calculation";
	protected static final String ERROR_AMEND_BOOKING_CART = "cart";
	protected static final String CHECK_IN_DATE_TIME = "checkInDateTime";
	protected static final String CHECK_OUT_DATE_TIME = "checkOutDateTime";
	protected static final String PART_HOTEL_STAY = "partHotelStay";
	protected static final String EQUAL_SIGN = "=";
	protected static final String AND_SIGN = "&";

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	@Resource(name = "accommodationOfferingFacade")
	private AccommodationOfferingFacade accommodationOfferingFacade;

	@Resource(name = "accommodationOfferingCustomerReviewFacade")
	private AccommodationOfferingCustomerReviewFacade accommodationOfferingCustomerReviewFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	/**
	 * @param request the HTTP request
	 * @return a list of {@link RoomStayCandidateData} built using request parameters
	 */
	protected List<RoomStayCandidateData> createRoomStayCandidates(final HttpServletRequest request)
	{
		return createRoomStayCandidates(request, 0);
	}

	/**
	 * @param request
	 *           the HTTP request
	 * @param startingRoomStayRefNum
	 *           the HTTP request
	 * @return a list of {@link RoomStayCandidateData} built using request parameters
	 */
	protected List<RoomStayCandidateData> createRoomStayCandidates(final HttpServletRequest request,
			final int startingRoomStayRefNum)
	{
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();

		final List<PassengerTypeData> passengerTypes = passengerTypeFacade.getPassengerTypes();
		try
		{
			final int numberOfRooms = Integer.parseInt(request.getParameter(NUMBER_OF_ROOMS));
			for (int i = 0; i < numberOfRooms; i++)
			{
				final String guestString = request.getParameter("r" + i);
				if (StringUtils.isEmpty(guestString)
						|| !Pattern.matches(TravelacceleratorstorefrontValidationConstants.REGEX_NUMBER_DASH_LETTERS_LIST, guestString))
				{
					return Collections.emptyList();
				}
				final List<String> guestsList = Arrays.asList(guestString.split(","));
				final List<PassengerTypeQuantityData> guestCounts = new ArrayList<>();
				for (final String guest : guestsList)
				{
					if (!(Pattern.compile(TravelacceleratorstorefrontValidationConstants.REGEX_PASSENGER_TYPE_QUANTITY).matcher(guest)
							.matches()))
					{
						return Collections.emptyList();
					}
					final List<String> guestAndQuantity = Arrays.asList(guest.split("-"));
					final int guestQuantity = Integer.parseInt(guestAndQuantity.get(0));
					final String guestType = guestAndQuantity.get(guestAndQuantity.size() - 1);
					final PassengerTypeData passengerTypeData = passengerTypes.stream()
							.filter(pData -> pData.getCode().equals(guestType)).findFirst().get();
					final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
					passengerTypeQuantityData.setQuantity(guestQuantity);
					passengerTypeQuantityData.setPassengerType(passengerTypeData);
					guestCounts.add(passengerTypeQuantityData);
				}
				final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
				roomStayCandidateData.setRoomStayCandidateRefNumber(startingRoomStayRefNum + i);
				roomStayCandidateData.setPassengerTypeQuantityList(guestCounts);
				roomStayCandidates.add(roomStayCandidateData);
			}

		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer");
			LOG.error(e.getClass().getName() + " : " + e.getMessage());
			LOG.debug(e);
			return Collections.emptyList();
		}
		catch (final NoSuchElementException e)
		{
			LOG.error("No Such Passenger Type Exists", e);
			return Collections.emptyList();
		}

		return roomStayCandidates;
	}

	/**
	 * @param request the HTTP request
	 * @return a list of {@link RoomStayCandidateData} built using request parameters
	 */
	protected List<RoomStayCandidateData> createRoomStayCandidatesForSearchPage(final HttpServletRequest request)
	{
		final int numberOfRooms;
		try
		{
			numberOfRooms = Integer.parseInt(request.getParameter(NUMBER_OF_ROOMS));
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer");
			LOG.debug(e);
			return Collections.emptyList();
		}

		final List<RoomStayCandidateData> roomStayCandidates = createRoomStayCandidates(request);
		if (CollectionUtils.isEmpty(roomStayCandidates) && numberOfRooms > 0)
		{
			return Collections.emptyList();
		}
		final int maxAccommodationQuantity = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		if (numberOfRooms < maxAccommodationQuantity)
		{
			for (int i = numberOfRooms; i < maxAccommodationQuantity; i++)
			{
				final RoomStayCandidateData roomStayCandidateData = createRoomStayCandidate();
				roomStayCandidateData.setRoomStayCandidateRefNumber(i);
				roomStayCandidates.add(roomStayCandidateData);
			}
		}
		return roomStayCandidates;
	}

	protected RoomStayCandidateData createRoomStayCandidate()
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<PassengerTypeData> sortedPassengerTypes = travellerSortStrategy
				.sortPassengerTypes(passengerTypeFacade.getPassengerTypes());
		for (final PassengerTypeData passengerTypeData : sortedPassengerTypes)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		roomStayCandidateData.setPassengerTypeQuantityList(passengerTypeQuantityList);
		return roomStayCandidateData;
	}

	/**
	 * Creates the pageable data.
	 *
	 * @param pageNumber the page number
	 * @param pageSize   the page size
	 * @param sortCode   the sort code
	 * @return the pageable data
	 */
	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);
		pageableData.setPageSize(pageSize > 0 ? pageSize : MAX_PAGE_LIMIT);
		return pageableData;
	}

	protected void adjustSessionBookingJourney()
	{
		if (bookingFacade.isCurrentCartOfType(OrderEntryType.TRANSPORT.getCode()))
		{
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION);
		}
	}

	/**
	 * Check if in the travel journey hotel is booked for part stay, if not, checks if the dates stored in session as parameters
	 * are the same as in the current request. This is to avoid discrepancies between transport and accommodation parts of travel
	 * booking.
	 *
	 * @param checkInDate
	 * @param checkOutDate
	 * @param currentQueryString
	 * @return
	 */
	protected String checkDatesAndGetNewQueryString(final String checkInDate, final String checkOutDate,
			final String currentQueryString)
	{
		final String accommodationSearchParams = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_QUERY_STRING);

		String originalCheckInDate = null;
		String originalCheckOutDate = null;
		boolean isPartStay = false;
		if (StringUtils.isNotBlank(accommodationSearchParams))
		{
			final String[] searchParamsList = accommodationSearchParams.split(AND_SIGN);
			for (final String param : searchParamsList)
			{
				if (StringUtils.containsIgnoreCase(param, CHECK_IN_DATE_TIME))
				{
					final String[] paramValueList = param.split(EQUAL_SIGN);
					if (paramValueList.length == 2)
					{
						originalCheckInDate = paramValueList[1];
					}
				}
				else if (StringUtils.containsIgnoreCase(param, CHECK_OUT_DATE_TIME))
				{
					final String[] paramValueList = param.split(EQUAL_SIGN);
					if (paramValueList.length == 2)
					{
						originalCheckOutDate = paramValueList[1];
					}
				}
				else if (StringUtils.containsIgnoreCase(param, PART_HOTEL_STAY))
				{
					final String[] paramValueList = param.split(EQUAL_SIGN);
					if (paramValueList.length == 2)
					{
						isPartStay = Boolean.parseBoolean(paramValueList[1]);
					}
				}
			}
		}
		if (isPartStay || originalCheckInDate == null || originalCheckOutDate == null)
		{
			// part stay or wrong session attribute or no accommodation search params in session - don't update dates
			return StringUtils.EMPTY;
		}

		String newQueryString = StringUtils.EMPTY;
		if (!StringUtils.equalsIgnoreCase(originalCheckInDate, checkInDate) || !StringUtils
				.equalsIgnoreCase(originalCheckOutDate, checkOutDate))
		{
			final String[] searchParamsList = currentQueryString.split(AND_SIGN);
			for (int i = 0; i < searchParamsList.length; i++)
			{
				if (StringUtils.containsIgnoreCase(searchParamsList[i], CHECK_IN_DATE_TIME))
				{
					final String[] paramValueList = searchParamsList[i].split(EQUAL_SIGN);
					if (paramValueList.length == 2)
					{
						searchParamsList[i] = paramValueList[0] + EQUAL_SIGN + originalCheckInDate;
					}

				}
				else if (StringUtils.containsIgnoreCase(searchParamsList[i], CHECK_OUT_DATE_TIME))
				{
					final String[] paramValueList = searchParamsList[i].split(EQUAL_SIGN);
					if (paramValueList.length == 2)
					{
						searchParamsList[i] = paramValueList[0] + EQUAL_SIGN + originalCheckOutDate;
					}
				}
				newQueryString += searchParamsList[i];
				if (i < searchParamsList.length - 1)
				{
					newQueryString += AND_SIGN;
				}
			}
		}
		return newQueryString;
	}

	/**
	 * Prepare accommodation search request data
	 *
	 * @param accommodationAvailabilityForm the accommodation availability form
	 * @param accommodationOfferingCode     the accommodationOffering code
	 * @return the accommodation search request data
	 */
	protected AccommodationSearchRequestData prepareAccommodationSearchRequestData(final String accommodationOfferingCode,
			final AccommodationAvailabilityForm accommodationAvailabilityForm)
	{
		final AccommodationSearchRequestData accommodationSearchRequestData = new AccommodationSearchRequestData();

		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode(accommodationOfferingCode);
		final StayDateRangeData stayRangeData = new StayDateRangeData();
		stayRangeData.setStartTime(TravelDateUtils
				.convertStringDateToDate(accommodationAvailabilityForm.getCheckInDateTime(), TravelservicesConstants.DATE_PATTERN));
		stayRangeData.setEndTime(TravelDateUtils
				.convertStringDateToDate(accommodationAvailabilityForm.getCheckOutDateTime(), TravelservicesConstants.DATE_PATTERN));

		final CriterionData criterionData = new CriterionData();
		criterionData.setAccommodationReference(accommodationReference);
		criterionData.setRoomStayCandidates(accommodationAvailabilityForm.getRoomStayCandidates());
		criterionData.setStayDateRange(stayRangeData);

		accommodationSearchRequestData.setCriterion(criterionData);

		return accommodationSearchRequestData;
	}

	/**
	 * Gets the paged accommodation offering customer reviews.
	 *
	 * @param accommodationOfferingCode
	 *           the accommodation offering code
	 * @param pageNumber
	 *           the page number
	 * @return SearchPageData having paged list of ReviewData
	 */
	protected SearchPageData<ReviewData> getPagedAccommodationOfferingCustomerReviews(final String accommodationOfferingCode,
			final int pageNumber)
	{
		final int pageSize = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.ACCOMMODATION_DETAILS_REVIEWS_PAGE_SIZE);

		final PageableData pageableData = createPageableData(pageNumber, pageSize, "byDate");

		return getAccommodationOfferingCustomerReviewFacade()
				.getAccommodationOfferingCustomerReviewDetails(accommodationOfferingCode, pageableData);
	}

	/**
	 * @return the accommodationOfferingFacade
	 */
	protected AccommodationOfferingFacade getAccommodationOfferingFacade()
	{
		return accommodationOfferingFacade;
	}

	/**
	 * @param accommodationOfferingFacade the accommodationOfferingFacade to set
	 */
	public void setAccommodationOfferingFacade(final AccommodationOfferingFacade accommodationOfferingFacade)
	{
		this.accommodationOfferingFacade = accommodationOfferingFacade;
	}

	/**
	 * @return the accommodationOfferingCustomerReviewFacade
	 */
	protected AccommodationOfferingCustomerReviewFacade getAccommodationOfferingCustomerReviewFacade()
	{
		return accommodationOfferingCustomerReviewFacade;
	}

	/**
	 * @param accommodationOfferingCustomerReviewFacade the accommodationOfferingCustomerReviewFacade to set
	 */
	public void setAccommodationOfferingCustomerReviewFacade(
			final AccommodationOfferingCustomerReviewFacade accommodationOfferingCustomerReviewFacade)
	{
		this.accommodationOfferingCustomerReviewFacade = accommodationOfferingCustomerReviewFacade;
	}

	/**
	 * @return the bookingFacade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

}
