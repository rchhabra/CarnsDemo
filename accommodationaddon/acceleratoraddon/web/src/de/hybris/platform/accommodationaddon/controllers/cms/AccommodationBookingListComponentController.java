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

package de.hybris.platform.accommodationaddon.controllers.cms;

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.model.components.AccommodationBookingListComponentModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.travelfacades.facades.BookingListFacade;
import de.hybris.platform.travelfacades.facades.TravelImageFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * Accommodation Booking List Controller for handling requests for My Booking Section in My Account Page.
 */
@Controller("AccommodationBookingListComponentController")
@RequestMapping(value = AccommodationaddonControllerConstants.Actions.Cms.AccommodationBookingListComponent)
public class AccommodationBookingListComponentController
		extends SubstitutingCMSAddOnComponentController<AccommodationBookingListComponentModel>
{
	private static final int MAX_PAGE_LIMIT = 100;

	@Resource(name = "bookingListFacade")
	private BookingListFacade bookingListFacade;

	@Resource(name = "travelImageFacade")
	private TravelImageFacade travelImageFacade;

	@Resource(name = "timeService")
	private TimeService timeService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AccommodationBookingListComponentModel component)
	{
		int pageSize = getConfigurationService().getConfiguration().getInt(AccommodationaddonWebConstants.MY_BOOKINGS_PAGE_SIZE);
		pageSize = pageSize > 0 ? pageSize : MAX_PAGE_LIMIT;
		final List<AccommodationReservationData> myBookings = bookingListFacade.getVisibleCurrentCustomerAccommodationBookings();
		final Map<String, ImageData> accommodationImages = getMediaForAccommodation(myBookings);
		final Map<String, Boolean> removeLinks = getRemoveLinks(myBookings);
		model.addAttribute(AccommodationaddonWebConstants.MY_ACCOUNT_BOOKING, myBookings);
		model.addAttribute(AccommodationaddonWebConstants.MY_ACCOUNT_BOOKING_IMAGES, accommodationImages);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.MY_ACCOUNT_BOOKING_ACCOMMODATION_ROOM_MAPPING,
				getAccommodationRoomNameMapping(myBookings));
		model.addAttribute(AccommodationaddonWebConstants.MY_ACCOUNT_REMOVE_LINKS, removeLinks);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.DATE_PATTERN, TravelservicesConstants.DATE_PATTERN);
		model.addAttribute(AccommodationaddonWebConstants.PAGE_SIZE, pageSize);

	}

	protected Map<String, Boolean> getRemoveLinks(final List<AccommodationReservationData> myBookings)
	{
		final Map<String, Boolean> removeLinks = new HashMap<String, Boolean>();
		myBookings.forEach(myBooking -> {
			final String reservationNumber = myBooking.getCode();
			removeLinks.put(reservationNumber, Boolean.FALSE);

			if (canUnlinkBooking(myBooking))
			{
				removeLinks.put(reservationNumber, Boolean.TRUE);
			}
		});
		return removeLinks;
	}

	protected Boolean canUnlinkBooking(final AccommodationReservationData reservation)
	{
		if (reservation.getBookingStatusCode().equals(OrderStatus.PAST.getCode())
				|| reservation.getBookingStatusCode().equals(OrderStatus.CANCELLED.getCode()))
		{
			return true;
		}

		final Date now = timeService.getCurrentTime();

		if (reservation.getRoomStays().get(0).getCheckOutDate().compareTo(now) >= 0)
		{
			return false;
		}

		return true;
	}

	protected Map<String, ImageData> getMediaForAccommodation(
			final List<AccommodationReservationData> accommodationReservationDatas)
	{
		if (CollectionUtils.isEmpty(accommodationReservationDatas))
		{
			return Collections.emptyMap();
		}

		final Map<String, ImageData> accommodationBookingsImagesMap = new HashMap<>();
		accommodationReservationDatas.forEach(accommodationReservationData -> {
			final String accommodationOfferingCode = accommodationReservationData.getAccommodationReference()
					.getAccommodationOfferingCode();

			accommodationBookingsImagesMap.put(accommodationReservationData.getCode(),
					travelImageFacade.getImageForAccommodationOfferingLocation(accommodationOfferingCode));
		});

		return accommodationBookingsImagesMap;
	}

	protected Map<String, Map<String, Integer>> getAccommodationRoomNameMapping(
			final List<AccommodationReservationData> accommodationReservationDatas)
	{
		if (CollectionUtils.isEmpty(accommodationReservationDatas))
		{
			return Collections.emptyMap();
		}
		final Map<String, Map<String, Integer>> accommodationRoomStayMapping = new HashMap<>();
		accommodationReservationDatas.forEach(accommodationReservationData -> {
			final Map<String, Integer> roomStayMapping = new HashMap<>();
			accommodationReservationData.getRoomStays().forEach(roomStay -> {
				final String roomTypeName = roomStay.getRoomTypes().get(0).getName();
				roomStayMapping.put(roomTypeName, roomStayMapping.get(roomTypeName) != null
						? roomStayMapping.get(roomTypeName) + 1 : 1);

			});
			accommodationRoomStayMapping.put(accommodationReservationData.getCode(), roomStayMapping);
		});
		return accommodationRoomStayMapping;
	}
}