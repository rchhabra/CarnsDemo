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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelcommons.model.components.TravelBookingListComponentModel;
import de.hybris.platform.travelfacades.facades.BookingListFacade;
import de.hybris.platform.travelfacades.facades.TravelImageFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Travel Booking List Controller for handling requests for My Booking Section in My Account Page.
 */
@Controller("TravelBookingListComponentController")
@RequestMapping(value = TravelcommonsControllerConstants.Actions.Cms.TravelBookingListComponent)
public class TravelBookingListComponentController extends SubstitutingCMSAddOnComponentController<TravelBookingListComponentModel>
{
	private static final int MAX_PAGE_LIMIT = 100;

	@Resource(name = "bookingListFacade")
	private BookingListFacade bookingListFacade;

	@Resource(name = "travelImageFacade")
	private TravelImageFacade travelImageFacade;

	@Resource(name = "timeService")
	private TimeService timeService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final TravelBookingListComponentModel component)
	{
		int pageSize = getConfigurationService().getConfiguration().getInt(TravelcommonsWebConstants.MY_BOOKINGS_PAGE_SIZE);
		pageSize = pageSize > 0 ? pageSize : MAX_PAGE_LIMIT;
		final List<GlobalTravelReservationData> myBookings = bookingListFacade.getVisibleCurrentCustomerTravelBookings();
		final Map<String, ImageData> bookingImages = getMediaForAccommodation(myBookings);
		final Map<String, Boolean> removeLinks = getRemoveLinks(myBookings);
		model.addAttribute(TravelcommonsWebConstants.MY_ACCOUNT_BOOKING, myBookings);
		model.addAttribute(TravelcommonsWebConstants.MY_ACCOUNT_BOOKING_IMAGES, bookingImages);
		model.addAttribute(TravelcommonsWebConstants.MY_ACCOUNT_REMOVE_LINKS, removeLinks);
		model.addAttribute(TravelcommonsWebConstants.MY_ACCOUNT_BOOKING_ACCOMMODATION_ROOM_MAPPING,
				getAccommodationRoomNameMapping(myBookings));
		model.addAttribute(TravelcommonsWebConstants.DATE_PATTERN, TravelservicesConstants.DATE_PATTERN);
		model.addAttribute(TravelcommonsWebConstants.PAGE_SIZE, pageSize);
	}

	protected Map<String, Boolean> getRemoveLinks(final List<GlobalTravelReservationData> myBookings)
	{
		final Map<String, Boolean> removeLinks = new HashMap<String, Boolean>();
		myBookings.forEach(myBooking -> {
			final String reservationNumber = getReservationNumber(myBooking);
			removeLinks.put(reservationNumber, Boolean.FALSE);
			if (canUnlinkBooking(myBooking))
			{
				removeLinks.put(reservationNumber, Boolean.TRUE);
			}
		});
		return removeLinks;
	}

	protected Boolean canUnlinkBooking(final GlobalTravelReservationData reservation)
	{
		if (reservation.getBookingStatusCode().equals(OrderStatus.PAST.getCode())
				|| reservation.getBookingStatusCode().equals(OrderStatus.CANCELLED.getCode()))
		{
			return true;
		}

		final List<Date> datesToCheck = new ArrayList<Date>();

		if (reservation.getAccommodationReservationData() != null)
		{
			datesToCheck.add(reservation.getAccommodationReservationData().getRoomStays().get(0).getCheckOutDate());
		}

		if (reservation.getReservationData() != null)
		{
			final List<ReservationItemData> reservationItems = reservation.getReservationData().getReservationItems();
			if (!CollectionUtils.isEmpty(reservationItems))
			{
				final ItineraryData reservationItinerary = reservationItems.get(reservationItems.size() - 1)
						.getReservationItinerary();
				if (reservationItinerary != null && CollectionUtils.isNotEmpty(reservationItinerary.getOriginDestinationOptions()))
				{
					//Get last transport offering in the journey
					final OriginDestinationOptionData lastOriginDestinationOption = reservationItinerary.getOriginDestinationOptions()
							.get(0);
					final TransportOfferingData lastTransportOffering = lastOriginDestinationOption.getTransportOfferings()
							.get(lastOriginDestinationOption.getTransportOfferings().size() - 1);

					datesToCheck.add(lastTransportOffering.getArrivalTime());
				}
			}
		}

		final Date now = timeService.getCurrentTime();

		for (final Date dateToCheck : datesToCheck)
		{
			if (dateToCheck.compareTo(now) >= 0)
			{
				return false;
			}
		}

		return true;
	}

	protected String getReservationNumber(final GlobalTravelReservationData myBooking)
	{
		if (myBooking.getReservationData() != null && StringUtils.isNotEmpty(myBooking.getReservationData().getCode()))
		{
			return myBooking.getReservationData().getCode();
		}

		if (myBooking.getAccommodationReservationData() != null
				&& StringUtils.isNotEmpty(myBooking.getAccommodationReservationData().getCode()))
		{
			return myBooking.getAccommodationReservationData().getCode();
		}

		return StringUtils.EMPTY;
	}

	protected Map<String, ImageData> getMediaForAccommodation(final List<GlobalTravelReservationData> globalTravelReservationDatas)
	{
		if (CollectionUtils.isEmpty(globalTravelReservationDatas))
		{
			return Collections.emptyMap();
		}
		final Map<String, ImageData> travelBookingsImagesMap = new HashMap<>();
		globalTravelReservationDatas.forEach(globalTravelReservationData -> {
			if (globalTravelReservationData.getReservationData() != null)
			{
				final ReservationData reservationData = globalTravelReservationData.getReservationData();
				reservationData.getReservationItems().stream()
						.filter(reservationItem -> reservationItem.getOriginDestinationRefNumber() == 0).forEach(reservationItem -> {
							travelBookingsImagesMap.put(reservationData.getCode(), travelImageFacade.getImageForArrivalTransportFacility(
									reservationItem.getReservationItinerary().getRoute().getDestination().getCode()));
						});
			}
			else
			{
				final AccommodationReservationData accommodationReservationData = globalTravelReservationData
						.getAccommodationReservationData();
				final String accommodationOfferingCode = accommodationReservationData.getAccommodationReference()
						.getAccommodationOfferingCode();

				travelBookingsImagesMap.put(accommodationReservationData.getCode(),
						travelImageFacade.getImageForAccommodationOfferingLocation(accommodationOfferingCode));
			}
		});
		return travelBookingsImagesMap;
	}

	protected Map<String, Map<String, Integer>> getAccommodationRoomNameMapping(
			final List<GlobalTravelReservationData> globalTravelReservationDatas)
	{
		if (CollectionUtils.isEmpty(globalTravelReservationDatas))
		{
			return Collections.emptyMap();
		}
		final Map<String, Map<String, Integer>> accommodationRoomStayMapping = new HashMap<>();
		globalTravelReservationDatas.stream()
				.filter(globalTravelReservationData -> globalTravelReservationData.getAccommodationReservationData() != null)
				.forEach(globalTravelReservationData -> {
					final AccommodationReservationData accommodationReservationData = globalTravelReservationData
							.getAccommodationReservationData();
					final Map<String, Integer> roomStayMapping = new HashMap<>();
					accommodationReservationData.getRoomStays().forEach(roomStay -> {
						final String roomTypeName = roomStay.getRoomTypes().get(0).getName();
						roomStayMapping.put(roomTypeName,
								roomStayMapping.get(roomTypeName) != null ? roomStayMapping.get(roomTypeName) + 1 : 1);
					});
					accommodationRoomStayMapping.put(accommodationReservationData.getCode(), roomStayMapping);
				});
		return accommodationRoomStayMapping;
	}
}
