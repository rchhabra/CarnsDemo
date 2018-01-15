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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.facades.BookingListFacade;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for bookings in My Account -> My Booking
 */
public class DefaultBookingListFacade implements BookingListFacade
{
	private Map<String, Integer> orderStatusValueMap;

	private ReservationPipelineManager transportBookingListPipelineManager;
	private AccommodationReservationPipelineManager accommodationBookingListPipelineManager;
	private GlobalTravelReservationPipelineManager travelBookingListPipelineManager;

	private UserService userService;
	private BaseStoreService baseStoreService;
	private TravelCustomerAccountService customerAccountService;

	@Override
	public List<ReservationData> getCurrentCustomerBookings()
	{
		final List<OrderModel> allOrders = getCustomerOrders();
		if (CollectionUtils.isEmpty(allOrders))
		{
			return Collections.emptyList();
		}
		final List<ReservationData> myBookings = allOrders.stream()
				.map(orderModel -> getTransportBookingListPipelineManager().executePipeline(orderModel)).collect(Collectors.toList());

		return sortBookings(myBookings);
	}


	@Override
	public List<AccommodationReservationData> getCurrentCustomerAccommodationBookings()
	{
		final List<OrderModel> allOrders = getCustomerOrders();
		if (CollectionUtils.isEmpty(allOrders))
		{
			return Collections.emptyList();
		}
		final List<AccommodationReservationData> myBookings = allOrders.stream()
				.map(orderModel -> getAccommodationBookingListPipelineManager().executePipeline(orderModel))
				.collect(Collectors.toList());

		return sortAccommodationBookings(myBookings);
	}

	@Override
	public List<AccommodationReservationData> getVisibleCurrentCustomerAccommodationBookings()
	{
		final List<OrderModel> allOrders = getCustomerOrders();
		if (CollectionUtils.isEmpty(allOrders))
		{
			return Collections.emptyList();
		}
		final List<AccommodationReservationData> myBookings = new ArrayList<AccommodationReservationData>();
		allOrders.forEach(orderModel -> {
			if (!getUserService().getCurrentUser().equals(orderModel.getUser())
					|| Boolean.TRUE.equals(orderModel.getVisibleToOwner()))
			{
				myBookings.add(getAccommodationBookingListPipelineManager().executePipeline(orderModel));
			}
		});

		return sortAccommodationBookings(myBookings);
	}

	@Override
	public List<GlobalTravelReservationData> getCurrentCustomerTravelBookings()
	{
		final List<OrderModel> allOrders = getCustomerOrders();
		if (CollectionUtils.isEmpty(allOrders))
		{
			return Collections.emptyList();
		}
		final List<GlobalTravelReservationData> myBookings = new ArrayList<>(allOrders.size());
		allOrders.forEach(orderModel -> {
			if (CollectionUtils.isNotEmpty(orderModel.getEntries()))
			{
				final GlobalTravelReservationData reservationData = getTravelBookingListPipelineManager().executePipeline(orderModel);
				myBookings.add(reservationData);
			}
		});

		return sortTravelBookings(myBookings);
	}

	@Override
	public List<GlobalTravelReservationData> getVisibleCurrentCustomerTravelBookings()
	{
		final List<OrderModel> allOrders = getCustomerOrders();
		if (CollectionUtils.isEmpty(allOrders))
		{
			return Collections.emptyList();
		}
		final List<GlobalTravelReservationData> myBookings = new ArrayList<>(allOrders.size());

		allOrders.forEach(orderModel -> {
			if (CollectionUtils.isNotEmpty(orderModel.getEntries())
					&& (!getUserService().getCurrentUser().equals(orderModel.getUser())
							|| orderModel.getVisibleToOwner().equals(Boolean.TRUE)))
			{
				final GlobalTravelReservationData reservationData = getTravelBookingListPipelineManager().executePipeline(orderModel);
				myBookings.add(reservationData);
			}
		});

		return sortTravelBookings(myBookings);
	}

	protected List<OrderModel> getCustomerOrders()
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		if (currentCustomer == null || getUserService().isAnonymousUser(currentCustomer))
		{
			return Collections.emptyList();
		}
		final List<OrderModel> allOrders = new ArrayList<>();
		final List<OrderModel> orders = getCustomerAccountService().getOrderList(currentCustomer,
				getBaseStoreService().getCurrentBaseStore(), null);
		final List<OrderModel> mappedOrders = getCustomerAccountService().getOrdersFromOrderUserMapping(currentCustomer);
		allOrders.addAll(orders);
		allOrders.addAll(mappedOrders);
		return allOrders;
	}

	/**
	 * Sorts users booking by departure time ascending and puts active bookings first
	 *
	 * @param myBookings
	 * @return sorted bookings list
	 */
	protected List<ReservationData> sortBookings(final List<ReservationData> myBookings)
	{
		final List<ReservationData> sortedBookings = myBookings.stream()
				.filter(b -> OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());

		final Comparator<ReservationData> departureTimeComparator = (b1, b2) -> getReservationDepartureTime(b1)
				.compareTo(getReservationDepartureTime(b2));

		final Comparator<ReservationData> orderCodeComparator = (b1, b2) -> b1.getCode().compareTo(b2.getCode());

		Collections.sort(sortedBookings, departureTimeComparator.thenComparing(orderCodeComparator));

		final Comparator<ReservationData> orderStatusComparator = (b1, b2) -> getOrderStatusValue(b1.getBookingStatusCode())
				.compareTo(getOrderStatusValue(b2.getBookingStatusCode()));

		final List<ReservationData> nonActiveBookings = myBookings.stream()
				.filter(b -> !OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						&& !OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						&& !OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());

		Collections.sort(nonActiveBookings,
				orderStatusComparator.thenComparing(departureTimeComparator).thenComparing(orderCodeComparator));

		sortedBookings.addAll(nonActiveBookings);

		return sortedBookings;
	}

	/**
	 * Returns the zonedDateTime corresponding to the departureTime of the first transportOffering of the given
	 * reservationData.
	 *
	 * @param reservationData
	 *           as the reservation data
	 *
	 * @return the zoned date time
	 */
	protected ZonedDateTime getReservationDepartureTime(final ReservationData reservationData)
	{

		final Optional<OriginDestinationOptionData> firstOriginDestinationOption = reservationData.getReservationItems().get(0)
				.getReservationItinerary().getOriginDestinationOptions().stream()
				.sorted(Comparator.comparing(OriginDestinationOptionData::getOriginDestinationRefNumber)).findFirst();
		if (!firstOriginDestinationOption.isPresent())
		{
			return null;
		}

		final TransportOfferingData firstTransportOffering = firstOriginDestinationOption.get().getTransportOfferings().stream()
				.sorted(Comparator
						.comparing(to -> TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
				.findFirst().get();

		return TravelDateUtils.getUtcZonedDateTime(firstTransportOffering.getDepartureTime(),
				firstTransportOffering.getDepartureTimeZoneId());
	}

	/**
	 * Sorts users booking by departure time ascending
	 *
	 * @param myBookings
	 * @return sorted bookings list
	 */
	protected List<AccommodationReservationData> sortAccommodationBookings(final List<AccommodationReservationData> myBookings)
	{
		final List<AccommodationReservationData> sortedBookings = myBookings.stream()
				.filter(b -> OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());
		final Comparator<AccommodationReservationData> departureTimeComparator = (b1, b2) -> b1.getRoomStays().get(0)
				.getCheckInDate().compareTo(b2.getRoomStays().get(0).getCheckInDate());

		final Comparator<AccommodationReservationData> orderCodeComparator = (b1, b2) -> b1.getCode().compareTo(b2.getCode());

		Collections.sort(sortedBookings, departureTimeComparator.thenComparing(orderCodeComparator));

		final Comparator<AccommodationReservationData> orderStatusComparator = ((b1,
				b2) -> getOrderStatusValue(b1.getBookingStatusCode()).compareTo(getOrderStatusValue(b2.getBookingStatusCode())));

		final List<AccommodationReservationData> nonActiveBookings = myBookings.stream()
				.filter(b -> !OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						&& !OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						&& !OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());
		Collections.sort(nonActiveBookings,
				orderStatusComparator.thenComparing(departureTimeComparator).thenComparing(orderCodeComparator));

		sortedBookings.addAll(nonActiveBookings);

		return sortedBookings;
	}

	/**
	 * Sorts users booking by departure time ascending
	 *
	 * @param myBookings
	 * @return sorted bookings list
	 */
	protected List<GlobalTravelReservationData> sortTravelBookings(final List<GlobalTravelReservationData> myBookings)
	{
		final List<GlobalTravelReservationData> activeBookings = myBookings.stream()
				.filter(b -> OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());

		final Comparator<GlobalTravelReservationData> orderStatusComparator = (b1,
				b2) -> getOrderStatusValue(b1.getBookingStatusCode()).compareTo(getOrderStatusValue(b2.getBookingStatusCode()));

		final Comparator<GlobalTravelReservationData> departureTimeComparator = (b1, b2) -> getDateForComparison(b1)
				.compareTo(getDateForComparison(b2));

		final Comparator<GlobalTravelReservationData> orderCodeComparator = (b1, b2) -> getOrderNumberForComparison(b1)
				.compareTo(getOrderNumberForComparison(b2));

		Collections.sort(activeBookings,
				orderStatusComparator.thenComparing(departureTimeComparator).thenComparing(orderCodeComparator));

		final List<GlobalTravelReservationData> nonActiveBookings = myBookings.stream()
				.filter(b -> !OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						&& !OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						&& !OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());

		Collections.sort(nonActiveBookings,
				orderStatusComparator.thenComparing(departureTimeComparator).thenComparing(orderCodeComparator));

		final List<GlobalTravelReservationData> sortedBookings = new ArrayList<GlobalTravelReservationData>();
		sortedBookings.addAll(activeBookings);
		sortedBookings.addAll(nonActiveBookings);

		return sortedBookings;

	}

	/**
	 * Provides date which is to be used for Sorting bookings for Travel.
	 *
	 * @param globalTravelReservationData
	 * @return date
	 */
	protected Date getDateForComparison(final GlobalTravelReservationData globalTravelReservationData)
	{
		if (globalTravelReservationData.getReservationData() == null
				&& globalTravelReservationData.getAccommodationReservationData() == null)
		{
			return null;
		}

		final List<Date> datesToCheck = new ArrayList<Date>();

		if (globalTravelReservationData.getAccommodationReservationData() != null)
		{
			datesToCheck.add(globalTravelReservationData.getAccommodationReservationData().getRoomStays().get(0).getCheckInDate());
		}

		if (globalTravelReservationData.getReservationData() != null
				&& CollectionUtils.isNotEmpty(globalTravelReservationData.getReservationData().getReservationItems()))
		{
			final Optional<OriginDestinationOptionData> firstOriginDestinationOption = globalTravelReservationData
					.getReservationData().getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().stream()
					.sorted(Comparator.comparing(OriginDestinationOptionData::getOriginDestinationRefNumber)).findFirst();

			if (firstOriginDestinationOption.isPresent())
			{
				final Optional<TransportOfferingData> firstTransportOffering = firstOriginDestinationOption.get()
						.getTransportOfferings().stream()
						.sorted(Comparator
								.comparing(to -> TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
						.findFirst();

				firstTransportOffering.ifPresent(transportOfferingData -> datesToCheck.add(transportOfferingData.getDepartureTime()));
			}
		}

		return datesToCheck.stream().sorted(Comparator.naturalOrder()).findFirst().orElse(null);
	}

	protected String getOrderNumberForComparison(final GlobalTravelReservationData globalTravelReservationData)
	{
		return globalTravelReservationData.getReservationData() != null ? globalTravelReservationData.getReservationData().getCode()
				: globalTravelReservationData.getAccommodationReservationData().getCode();
	}

	protected Integer getOrderStatusValue(final String orderStatusCode)
	{
		if (getOrderStatusValueMap().containsKey(orderStatusCode))
		{
			return getOrderStatusValueMap().get(orderStatusCode);
		}
		return getOrderStatusValueMap().get("DEFAULT");
	}


	/**
	 * @return the orderStatusValueMap
	 */
	protected Map<String, Integer> getOrderStatusValueMap()
	{
		return orderStatusValueMap;
	}


	/**
	 * @param orderStatusValueMap
	 *           the orderStatusValueMap to set
	 */
	@Required
	public void setOrderStatusValueMap(final Map<String, Integer> orderStatusValueMap)
	{
		this.orderStatusValueMap = orderStatusValueMap;
	}


	/**
	 * @return the transportBookingListPipelineManager
	 */
	protected ReservationPipelineManager getTransportBookingListPipelineManager()
	{
		return transportBookingListPipelineManager;
	}


	/**
	 * @param transportBookingListPipelineManager
	 *           the transportBookingListPipelineManager to set
	 */
	@Required
	public void setTransportBookingListPipelineManager(final ReservationPipelineManager transportBookingListPipelineManager)
	{
		this.transportBookingListPipelineManager = transportBookingListPipelineManager;
	}


	/**
	 * @return the accommodationBookingListPipelineManager
	 */
	protected AccommodationReservationPipelineManager getAccommodationBookingListPipelineManager()
	{
		return accommodationBookingListPipelineManager;
	}


	/**
	 * @param accommodationBookingListPipelineManager
	 *           the accommodationBookingListPipelineManager to set
	 */
	@Required
	public void setAccommodationBookingListPipelineManager(
			final AccommodationReservationPipelineManager accommodationBookingListPipelineManager)
	{
		this.accommodationBookingListPipelineManager = accommodationBookingListPipelineManager;
	}


	/**
	 * @return the travelBookingListPipelineManager
	 */
	protected GlobalTravelReservationPipelineManager getTravelBookingListPipelineManager()
	{
		return travelBookingListPipelineManager;
	}


	/**
	 * @param travelBookingListPipelineManager
	 *           the travelBookingListPipelineManager to set
	 */
	@Required
	public void setTravelBookingListPipelineManager(final GlobalTravelReservationPipelineManager travelBookingListPipelineManager)
	{
		this.travelBookingListPipelineManager = travelBookingListPipelineManager;
	}


	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}


	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}


	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}


	/**
	 * @return the customerAccountService
	 */
	protected TravelCustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}


	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	@Required
	public void setCustomerAccountService(final TravelCustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}
}
