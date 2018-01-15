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
 */

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateCartData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.booking.action.strategies.CalculatePaymentTypeForChangeDatesStrategy;
import de.hybris.platform.travelfacades.facades.AccommodationBookingFacade;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.BookingListFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.exceptions.RequestKeyGeneratorException;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.DisruptionModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.GuestCountService;
import de.hybris.platform.travelservices.services.RoomPreferenceService;
import de.hybris.platform.travelservices.strategies.TravelCheckoutCustomerStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for managing a booking
 */
public class DefaultBookingFacade implements BookingFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultBookingFacade.class);

	private static final String MANAGE_MY_BOOKING_GUEST_UID = "manage_my_booking_guest_uid";
	private static final String MANAGE_MY_BOOKING_BOOKING_REFERENCE = "manage_my_booking_booking_reference";
	private static final String MANAGE_MY_BOOKING_AUTHENTICATION = "manage_my_booking_authentication";

	private TravelCustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private ReservationFacade reservationFacade;
	private AccommodationCartFacade accommodationCartFacade;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private UserService userService;
	private AccommodationReservationPipelineManager fullAccommodationReservationPipelineManager;
	private AccommodationReservationPipelineManager basicAccommodationReservationPipelineManager;

	private GlobalTravelReservationPipelineManager travelOfferingStatusSearchPipelineManager;
	private TravelCartService travelCartService;
	private BookingService bookingService;
	private EnumerationService enumerationService;
	private RoomPreferenceService roomPreferenceService;
	private CheckoutFacade checkoutFacade;
	private SessionService sessionService;
	private TravelCheckoutCustomerStrategy travelCheckoutCustomerStrategy;
	private Map<String, Integer> orderStatusValueMap;
	private ModelService modelService;
	private Converter<PassengerTypeQuantityData, GuestCountModel> guestCountReverseConverter;
	private CommonI18NService commonI18NService;
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;
	private TimeService timeService;
	private CalculatePaymentTypeForChangeDatesStrategy calculatePaymentTypeForChangeDatesStrategy;
	private List<OrderStatus> notAllowedStatuses;
	private AccommodationReservationPipelineManager guestDetailsAccommodationReservationPipelineManager;
	private GuestCountService guestCountService;
	private AccommodationBookingFacade accommodationBookingFacade;


	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private ReservationPipelineManager reservationItemPipelineManager;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private GlobalTravelReservationPipelineManager basicGlobalTravelReservationPipelineManager;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private BookingListFacade bookingListFacade;

	@Override
	public ReservationData getBookingByBookingReference(final String bookingReference)
	{
		//Retrieve order with bookingReferenceNumber
		final OrderModel orderModel = bookingService.getOrderModelFromStore(bookingReference);
		//Convert orderModel to ReservationData
		return convertOrderModelToReservationData(orderModel);
	}


	@Override
	public AccommodationReservationData getFullAccommodationBooking(final String bookingReference)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (orderModel == null)
		{
			return null;
		}
		return getFullAccommodationReservationPipelineManager().executePipeline(orderModel);
	}

	@Override
	public GlobalTravelReservationData getGlobalTravelReservationData(final String bookingReference)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		return getReservationFacade().getGlobalTravelReservationData(orderModel);
	}

	@Override
	public AccommodationReservationData getBasicAccommodationBookingFromCart()
	{
		return getBasicAccommodationBookingFromCart(getBasicAccommodationReservationPipelineManager());
	}

	@Override
	public AccommodationReservationData getAccommodationReservationDataForGuestDetailsFromCart()
	{
		return getBasicAccommodationBookingFromCart(getGuestDetailsAccommodationReservationPipelineManager());
	}

	protected AccommodationReservationData getBasicAccommodationBookingFromCart(
			final AccommodationReservationPipelineManager accommodationReservationPipelineManager)
	{
		if (getTravelCartService().hasSessionCart())
		{
			final CartModel cartModel = getTravelCartService().getSessionCart();
			return accommodationReservationPipelineManager.executePipeline(cartModel);
		}
		return null;
	}

	@Override
	public List<GuestOccupancyData> getGuestOccupanciesFromCart(final int roomStayRefNum)
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = getBookingService()
				.getAccommodationOrderEntryGroup(roomStayRefNum, getTravelCartService().getSessionCart());

		if (Objects.isNull(accommodationOrderEntryGroup))
		{
			return Collections.emptyList();
		}

		final AccommodationModel accommodation = accommodationOrderEntryGroup.getAccommodation();
		final RatePlanModel ratePlan = accommodationOrderEntryGroup.getRatePlan();

		final List<GuestOccupancyModel> guestOccupancyModels = CollectionUtils.isEmpty(ratePlan.getGuestOccupancies())
				? CollectionUtils.isNotEmpty(accommodation.getGuestOccupancies()) ? accommodation.getGuestOccupancies()
				: Collections.emptyList()
				: ratePlan.getGuestOccupancies();

		return Converters.convertAll(guestOccupancyModels, getGuestOccupancyConverter());
	}

	/**
	 * @param orderCode
	 * 		the order code
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Override
	@Deprecated
	public boolean amendAddRoom(final String orderCode)
	{
		final CartModel cart = getTravelCartService().createCartFromOrder(orderCode, getCurrentUserUid());
		if (cart != null)
		{
			getTravelCartService().setSessionCart(cart);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public String buildAccommodationDetailsQueryFromCart()
	{
		final Map<String, String> params = getAccommodationDetailsParametersFromCart();

		if (MapUtils.isEmpty(params))
		{
			return StringUtils.EMPTY;
		}

		// number of rooms is set to 1 by default and there is just 1 AD in this room to offer user all possible options
		return params.get(TravelservicesConstants.ACCOMMODATION_OFFERING_CODE) + "?" + TravelservicesConstants.CHECK_IN_DATE_TIME
				+ "=" + params.get(TravelservicesConstants.CHECK_IN_DATE_TIME) + "&" + TravelservicesConstants.CHECK_OUT_DATE_TIME
				+ "=" + params.get(TravelservicesConstants.CHECK_OUT_DATE_TIME) + "&numberOfRooms=1" + "&r1=1-adult,0-child,"
				+ "0-infant";
	}

	@Override
	public Map<String, String> getAccommodationDetailsParametersFromCart()
	{
		return getBookingService().getAccommodationDetailsParameters(getTravelCartService().getSessionCart());
	}

	@Override
	public List<ReservedRoomStayData> getNewReservedRoomStays()
	{
		final AccommodationReservationData reservation = getBasicAccommodationBookingFromCart();
		if (reservation == null || CollectionUtils.isEmpty(reservation.getRoomStays()))
		{
			return Collections.emptyList();
		}

		final List<Integer> newAccommodationOrderEntryGroupRefs = getNewAccommodationOrderEntryGroupRefs();

		if (CollectionUtils.isEmpty(newAccommodationOrderEntryGroupRefs))
		{
			return Collections.emptyList();
		}

		final List<ReservedRoomStayData> newReservedRoomStays = new ArrayList<>();
		reservation.getRoomStays().forEach(roomStay ->
		{
			if (newAccommodationOrderEntryGroupRefs.contains(roomStay.getRoomStayRefNumber()))
			{
				newReservedRoomStays.add(roomStay);
				roomStay.setNonModifiable(Boolean.FALSE);
			}
		});
		return newReservedRoomStays;
	}

	@Override
	public List<ReservedRoomStayData> getOldReservedRoomStays()
	{
		final AccommodationReservationData reservation = getBasicAccommodationBookingFromCart();
		if (reservation == null || CollectionUtils.isEmpty(reservation.getRoomStays()))
		{
			return Collections.emptyList();
		}

		final List<Integer> oldAccommodationOrderEntryGroupRefs = getOldAccommodationOrderEntryGroupRefs();

		if (CollectionUtils.isEmpty(oldAccommodationOrderEntryGroupRefs))
		{
			return Collections.emptyList();
		}

		final List<ReservedRoomStayData> oldReservedRoomStays = new ArrayList<>();
		reservation.getRoomStays().forEach(roomStay ->
		{
			if (oldAccommodationOrderEntryGroupRefs.contains(roomStay.getRoomStayRefNumber()))
			{
				roomStay.setNonModifiable(Boolean.TRUE);
				oldReservedRoomStays.add(roomStay);
			}
		});
		return oldReservedRoomStays;
	}

	@Override
	public List<Integer> getAccommodationOrderEntryGroupRefs()
	{
		return getBookingService().getAccommodationOrderEntryGroupRefs(getTravelCartService().getSessionCart());
	}

	@Override
	public List<Integer> getNewAccommodationOrderEntryGroupRefs()
	{
		return getBookingService().getNewAccommodationOrderEntryGroupRefs(getTravelCartService().getSessionCart());
	}

	@Override
	public List<Integer> getOldAccommodationOrderEntryGroupRefs()
	{
		return getBookingService().getOldAccommodationOrderEntryGroupRefs(getTravelCartService().getSessionCart());
	}

	@Override
	public Boolean updateAccommodationOrderEntryGroup(final int roomStayRefNum, final GuestData guestData,
			final List<PassengerTypeQuantityData> passengerTypeQuantityData, final List<String> roomPreferenceCodes,
			final String checkInTime)
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = getBookingService()
				.getAccommodationOrderEntryGroup(roomStayRefNum, getTravelCartService().getSessionCart());

		if (accommodationOrderEntryGroupModel == null)
		{
			return Boolean.FALSE;
		}

		accommodationOrderEntryGroupModel.setFirstName(guestData.getProfile().getFirstName());
		accommodationOrderEntryGroupModel.setLastName(guestData.getProfile().getLastName());
		accommodationOrderEntryGroupModel.setContactEmail(guestData.getProfile().getEmail());
		accommodationOrderEntryGroupModel.setContactNumber(guestData.getProfile().getContactNumber());
		final List<GuestCountModel> guestCounts = new ArrayList<>();

		passengerTypeQuantityData.forEach(pt ->
		{
			GuestCountModel guestCountModel = getGuestCountService().getGuestCount(pt.getPassengerType().getCode(),
					pt.getQuantity());
			if (Objects.isNull(guestCountModel))
			{
				LOG.debug("GuestCountModel not found for the given parameters, a new one will be created.");
				guestCountModel = getGuestCountReverseConverter().convert(pt);
			}
			guestCounts.add(guestCountModel);
		});
		accommodationOrderEntryGroupModel.setGuestCounts(guestCounts);

		accommodationOrderEntryGroupModel
				.setCheckInTime(TravelDateUtils.getDate(checkInTime, TravelservicesConstants.DATE_TIME_PATTERN));

		if (CollectionUtils.isNotEmpty(roomPreferenceCodes))
		{
			final List<RoomPreferenceModel> roomPreferences = getRoomPreferenceService().getRoomPreferences(roomPreferenceCodes);
			accommodationOrderEntryGroupModel.setRoomPreferences(roomPreferences);
		}
		try
		{
			getModelService().save(accommodationOrderEntryGroupModel);
			return Boolean.TRUE;
		}
		catch (final ModelSavingException e)
		{
			LOG.debug("Model " + AccommodationOrderEntryGroupModel._TYPECODE + " not saved", e);
			return Boolean.FALSE;
		}
	}

	@Override
	public boolean validateUserForBooking(final String bookingReference)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);

		if (orderModel == null)
		{
			return Boolean.FALSE;
		}

		if (isUserOrderOwner(bookingReference) || isCurrentUserAndOrderLinked(currentCustomer, orderModel.getCode())
				|| validateB2BUser(currentCustomer, orderModel))
		{
			return Boolean.TRUE;
		}

		final Boolean mmbAuthentication = getSessionService().getAttribute(MANAGE_MY_BOOKING_AUTHENTICATION);

		return mmbAuthentication != null && mmbAuthentication && StringUtils
				.equalsIgnoreCase(getSessionService().getAttribute(MANAGE_MY_BOOKING_BOOKING_REFERENCE), bookingReference);

	}


	@Override
	public boolean validateB2BUser(final CustomerModel currentCustomer, final OrderModel orderModel)
	{
		if (!(currentCustomer instanceof B2BCustomerModel))
		{
			return Boolean.FALSE;
		}

		if (orderModel.getUnit() == null)
		{
			return Boolean.FALSE;
		}
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) currentCustomer;
		final Set<PrincipalGroupModel> groups = b2bCustomer.getGroups();

		final List<String> groupsList = new ArrayList<String>();
		for (final PrincipalGroupModel group : groups)
		{
			groupsList.add(group.getUid());
		}

		for (final PrincipalGroupModel group : groups)
		{
			if (group instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) group;
				if (b2bUnit.getPk().equals(orderModel.getUnit().getPk()) && groupsList.contains("b2badmingroup"))
				{
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public Boolean isUserOrderOwner(final String bookingReference)
	{
		if (!getUserService().isAnonymousUser(getUserService().getCurrentUser()))
		{
			final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
			final OrderModel orderModel;
			try
			{
				orderModel = getCustomerAccountService().getOrderForCode(currentCustomer, bookingReference,
						getBaseStoreService().getCurrentBaseStore());
			}
			catch (final ModelNotFoundException e)
			{
				LOG.debug("Order " + bookingReference + " not found for user" + currentCustomer.getUid(), e);
				return Boolean.FALSE;
			}
			return orderModel != null;
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean validateUserForCheckout(final String bookingReference)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);

		if (isUserOrderOwner(bookingReference) || isCurrentUserAndOrderLinked(currentCustomer, orderModel.getCode()))
		{
			return Boolean.TRUE;
		}

		if (orderModel.getAdditionalSecurity() && containsValidPassengerReference(orderModel))
		{
			return Boolean.TRUE;
		}

		if (getTravelCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			return getTravelCheckoutCustomerStrategy().isValidBookingForCurrentGuestUser(bookingReference);
		}
		return false;
	}

	/**
	 * Checks if the PASSENGER_REFERENCE is set in session and if its valid for the provided order
	 *
	 * @param orderModel
	 * 		the order model
	 * @return
	 */
	protected boolean containsValidPassengerReference(final OrderModel orderModel)
	{
		final String passengerReference = getSessionService().getAttribute(TravelservicesConstants.PASSENGER_REFERENCE);
		return StringUtils.isNotEmpty(passengerReference) && getBookingService()
				.isValidPassengerReference(orderModel, passengerReference);
	}

	@Override
	public ReservationData getBookingByBookingReferenceAndAmendingOrder(final String bookingReference)
	{
		//Retrieve order with bookingReferenceNumber
		OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (OrderStatus.AMENDMENTINPROGRESS.equals(orderModel.getStatus()))
		{
			orderModel = getBookingService().getOrderModelByOriginalOrderCode(bookingReference);
		}
		//Convert orderModel to ReservationData
		final ReservationData reservationData = convertOrderModelToReservationData(orderModel);
		if (reservationData != null)
		{
			reservationData.setCode(bookingReference);
		}
		return reservationData;
	}

	@Override
	public AccommodationReservationData getFullAccommodationBookingForAmendOrder(final String bookingReference)
	{
		OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (orderModel == null)
		{
			return null;
		}
		if (OrderStatus.AMENDMENTINPROGRESS.equals(orderModel.getStatus()))
		{
			orderModel = getBookingService().getOrderModelByOriginalOrderCode(bookingReference);
		}
		final AccommodationReservationData accommodationReservationData = getFullAccommodationReservationPipelineManager()
				.executePipeline(orderModel);
		if (accommodationReservationData != null)
		{
			accommodationReservationData.setCode(bookingReference);
		}
		return accommodationReservationData;
	}

	protected ReservationData convertOrderModelToReservationData(final OrderModel orderModel)
	{
		return getReservationFacade().getReservationData(orderModel);
	}

	/**
	 * @deprecated Deprecated since version 4.0. use {@link DefaultReservationFacade#retrieveGlobalReservationData(String)} and
	 * {@link #getBookerEmailID(GlobalTravelReservationData, String, String)}
	 */
	@Override
	@Deprecated
	public String validateAndReturnBookerEmailId(final String bookingReference, final String lastName)
	{
		//Retrieve order with bookingReferenceNumber
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (orderModel == null)
		{
			return null;
		}

		final GlobalTravelReservationData globalReservationData = getReservationFacade().getGlobalTravelReservationData(orderModel);

		if (globalReservationData.getReservationData() != null)
		{
			final List<TravellerData> travellerDataList = globalReservationData.getReservationData().getReservationItems().stream()
					.flatMap(reservationItem -> reservationItem.getReservationItinerary().getTravellers().stream())
					.collect(Collectors.toList());

			for (final TravellerData travellerData : travellerDataList)
			{
				final String uid = checkTravellerAndReturnUid(lastName, orderModel, travellerData);
				if (StringUtils.isNotEmpty(uid))
				{
					return uid;
				}
			}
		}

		if (globalReservationData.getAccommodationReservationData() != null)
		{
			final List<GuestData> guestDataList = globalReservationData.getAccommodationReservationData().getRoomStays().stream()
					.flatMap(roomStay -> roomStay.getReservedGuests().stream()).collect(Collectors.toList());

			for (final GuestData guestData : guestDataList)
			{
				final String uid = checkGuestAndReturnUid(lastName, orderModel, guestData);
				if (StringUtils.isNotEmpty(uid))
				{
					return uid;
				}
			}
		}

		return null;
	}

	@Override
	public String getBookerEmailID(final GlobalTravelReservationData globalReservationData, final String lastName,
			final String passengerReference)
	{
		String bookerEmailID = null;
		if (Objects.nonNull(globalReservationData.getReservationData()))
		{
			bookerEmailID = getReservationFacade()
					.getBookerEmailIDFromReservationData(globalReservationData, lastName, passengerReference);
			if (Objects.nonNull(bookerEmailID))
			{
				return bookerEmailID;
			}
		}

		if (Objects.nonNull(globalReservationData.getAccommodationReservationData()))
		{
			bookerEmailID = getAccommodationBookingFacade()
					.getBookerEmailIDFromAccommodationReservationData(globalReservationData, lastName);
		}

		return bookerEmailID;
	}

	/**
	 * Check traveller and return uid string.
	 *
	 * @param lastName
	 * 		the last name
	 * @param orderModel
	 * 		the order model
	 * @param travellerData
	 * 		the traveller data
	 * @return the string
	 * @deprecated Deprecated since version 4.0. use {@link DefaultReservationFacade#checkTraveller(String, TravellerData)}
	 */
	@Deprecated
	protected String checkTravellerAndReturnUid(final String lastName, final OrderModel orderModel,
			final TravellerData travellerData)
	{
		if (!TravellerType.PASSENGER.toString().equals(travellerData.getTravellerType()))
		{
			return null;
		}
		final PassengerInformationData passengerInfoData = (PassengerInformationData) travellerData.getTravellerInfo();
		if (lastName.equalsIgnoreCase(passengerInfoData.getSurname()))
		{
			return getCustomerUid(orderModel);
		}
		return null;
	}

	/**
	 * Check guest and return uid string.
	 *
	 * @param lastName
	 * 		the last name
	 * @param orderModel
	 * 		the order model
	 * @param guestData
	 * 		the guest data
	 * @return the string
	 * @deprecated Deprecated since version 4.0. use {@link DefaultAccommodationBookingFacade#checkGuest(String, GuestData)}
	 */
	@Deprecated
	protected String checkGuestAndReturnUid(final String lastName, final OrderModel orderModel, final GuestData guestData)
	{
		if (StringUtils.containsIgnoreCase(guestData.getProfile().getLastName(), lastName))
		{
			return getCustomerUid(orderModel);
		}
		return null;
	}

	/**
	 * Gets customer uid.
	 *
	 * @param orderModel
	 * 		the order model
	 * @return the customer uid
	 * @deprecated Deprecated since version 4.0. use {@link DefaultAccommodationBookingFacade#getCustomerUid(CustomerData)}
	 */
	@Deprecated
	protected String getCustomerUid(final OrderModel orderModel)
	{
		final CustomerModel customerModel = (CustomerModel) orderModel.getUser();
		final String uid = customerModel.getUid();
		if (CustomerType.GUEST.equals(customerModel.getType()))
		{
			return StringUtils.substringAfter(uid, "|");
		}
		return uid;
	}

	/**
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Override
	@Deprecated
	public List<ReservationData> getCurrentCustomerBookings()
	{
		return getBookingListFacade().getCurrentCustomerBookings();
	}

	/**
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Override
	@Deprecated
	public List<AccommodationReservationData> getCurrentCustomerAccommodationBookings()
	{
		return getBookingListFacade().getCurrentCustomerAccommodationBookings();
	}

	/**
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Override
	@Deprecated
	public List<AccommodationReservationData> getVisibleCurrentCustomerAccommodationBookings()
	{
		return getBookingListFacade().getVisibleCurrentCustomerAccommodationBookings();
	}

	@Override
	public PriceData getBookingTotal(final String bookingReference)
	{
		OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (OrderStatus.AMENDMENTINPROGRESS.equals(orderModel.getStatus()))
		{
			orderModel = getBookingService().getOrderModelByOriginalOrderCode(bookingReference);
		}

		BigDecimal totalPrice = BigDecimal.valueOf(orderModel.getTotalPrice());
		if (orderModel.getNet())
		{
			totalPrice = totalPrice.add(BigDecimal.valueOf(orderModel.getTotalTax()));
		}

		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalPrice, orderModel.getCurrency().getIsocode());
	}

	/**
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Override
	@Deprecated
	public List<GlobalTravelReservationData> getCurrentCustomerTravelBookings()
	{
		return getBookingListFacade().getCurrentCustomerTravelBookings();
	}

	/**
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Override
	@Deprecated
	public List<GlobalTravelReservationData> getVisibleCurrentCustomerTravelBookings()
	{
		return getBookingListFacade().getVisibleCurrentCustomerTravelBookings();
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


	@Override
	public TransportOfferingData getNextScheduledTransportOfferingData()
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		if (currentCustomer == null || getUserService().isAnonymousUser(currentCustomer))
		{
			return null;
		}
		final List<OrderModel> myOrders = getCustomerAccountService().getOrderList(currentCustomer,
				baseStoreService.getCurrentBaseStore(), null);
		if (CollectionUtils.isEmpty(myOrders))
		{
			return null;
		}

		final List<OrderModel> activeOrders = myOrders.stream()
				.filter(b -> (OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getStatus().getCode())
						|| OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getStatus().getCode())
						|| OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getStatus().getCode())))
				.collect(Collectors.toList());

		final List<GlobalTravelReservationData> activeBookings = activeOrders.stream()
				.map(orderModel -> getTravelOfferingStatusSearchPipelineManager().executePipeline(orderModel))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(activeBookings))
		{
			return null;
		}

		final List<TransportOfferingData> allScheduledTransportOfferings = new ArrayList<TransportOfferingData>();
		final ZonedDateTime currentUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
				ZoneId.systemDefault());
		for (final GlobalTravelReservationData globalTravelReservationData : activeBookings)
		{
			if (globalTravelReservationData.getReservationData() == null)
			{
				continue;
			}

			for (final ReservationItemData reservationItemData : globalTravelReservationData.getReservationData()
					.getReservationItems())
			{
				final ItineraryData itineraryData = reservationItemData.getReservationItinerary();

				for (final OriginDestinationOptionData originDestinationOptionData : itineraryData.getOriginDestinationOptions())
				{
					allScheduledTransportOfferings.addAll(originDestinationOptionData.getTransportOfferings().stream()
							.filter(to -> TransportOfferingStatus.SCHEDULED.getCode().equalsIgnoreCase(to.getStatus()) && currentUtcTime
									.isBefore(TravelDateUtils.getUtcZonedDateTime(to.getDepartureTime(), to.getDepartureTimeZoneId())))
							.collect(Collectors.toList()));
				}
			}
		}

		final Comparator<TransportOfferingData> departureTimeComparator = (b1, b2) -> TravelDateUtils
				.getUtcZonedDateTime(b1.getDepartureTime(), b1.getDepartureTimeZoneId())
				.compareTo(TravelDateUtils.getUtcZonedDateTime(b2.getDepartureTime(), b2.getDepartureTimeZoneId()));
		final Comparator<TransportOfferingData> orderCodeComparator = (b1, b2) -> b1.getCode().compareTo(b2.getCode());
		Collections.sort(allScheduledTransportOfferings, departureTimeComparator.thenComparing(orderCodeComparator));
		return CollectionUtils.isNotEmpty(allScheduledTransportOfferings) ? allScheduledTransportOfferings.get(0) : null;
	}

	/**
	 * Sorts users booking by departure time ascending and puts active bookings first
	 * In future release this method has been moved to {@link= DefaultBookingListFacade}
	 *
	 * @param myBookings
	 * @return sorted bookings list
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected List<ReservationData> sortBookings(final List<ReservationData> myBookings)
	{
		final List<ReservationData> sortedBookings = myBookings.stream()
				.filter(b -> OrderStatus.ACTIVE.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED.getCode().equalsIgnoreCase(b.getBookingStatusCode())
						|| OrderStatus.ACTIVE_DISRUPTED_PENDING.getCode().equalsIgnoreCase(b.getBookingStatusCode()))
				.collect(Collectors.toList());

		final Comparator<ReservationData> departureTimeComparator = (b1, b2) -> b1.getReservationItems().get(0)
				.getReservationItinerary().getOriginDestinationOptions().get(0).getTransportOfferings().get(0).getDepartureTime()
				.compareTo(b2.getReservationItems().get(0).getReservationItinerary().getOriginDestinationOptions().get(0)
						.getTransportOfferings().get(0).getDepartureTime());

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
	 * Sorts users booking by departure time ascending
	 * In future release this method has been moved to {@link= DefaultBookingListFacade}
	 *
	 * @param myBookings
	 * @return sorted bookings list
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
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
	 * In future release this method has been moved to {@link= DefaultBookingListFacade}
	 *
	 * @param myBookings
	 * @return sorted bookings list
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
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
	 * In future release this method has been moved to {@link= DefaultBookingListFacade}
	 *
	 * @param globalTravelReservationData
	 * @return date
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
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

		if (globalTravelReservationData.getReservationData() != null)
		{
			final List<ReservationItemData> reservationItems = globalTravelReservationData.getReservationData()
					.getReservationItems();
			if (!CollectionUtils.isEmpty(reservationItems))
			{
				final ItineraryData reservationItinerary = reservationItems.get(0).getReservationItinerary();

				if (reservationItinerary != null && CollectionUtils.isNotEmpty(reservationItinerary.getOriginDestinationOptions()))
				{
					//Get first transport offering in the journey
					final OriginDestinationOptionData firstOriginDestinationOption = reservationItinerary.getOriginDestinationOptions()
							.get(0);
					final TransportOfferingData firstTransportOffering = firstOriginDestinationOption.getTransportOfferings().get(0);
					datesToCheck.add(firstTransportOffering.getDepartureTime());
				}
			}
		}

		Date dateForComparison = datesToCheck.get(0);
		for (final Date date : datesToCheck)
		{
			if (date.before(dateForComparison))
			{
				dateForComparison = date;
			}
		}

		return dateForComparison;
	}

	/**
	 * @param globalTravelReservationData
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected String getOrderNumberForComparison(final GlobalTravelReservationData globalTravelReservationData)
	{
		return globalTravelReservationData.getReservationData() != null ? globalTravelReservationData.getReservationData().getCode()
				: globalTravelReservationData.getAccommodationReservationData().getCode();
	}

	@Override
	public Boolean amendOrder(final String orderCode, final String guid)
	{
		final CartModel cartModel = getTravelCartService().createCartFromOrder(orderCode, guid);
		if (cartModel != null)
		{
			getTravelCartService().setSessionCart(cartModel);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean changeDatesForAccommodationBooking(final String checkInDate, final String checkOutDate,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse,
			final AccommodationReservationData accommodationReservationData)
	{
		final List<AccommodationAddToCartForm> addToCartForms = createAccommodationAddToCartForm(accommodationAvailabilityResponse);
		if (CollectionUtils.isEmpty(addToCartForms))
		{
			return Boolean.FALSE;
		}

		final boolean isChangeDateSuccess = deleteRoomRateEntries();
		if (!isChangeDateSuccess)
		{
			return Boolean.FALSE;
		}
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = changeDatesForAccommodationOrderEntryGroup(
				checkInDate, checkOutDate);
		if (CollectionUtils.isEmpty(accommodationOrderEntryGroupModels))
		{
			return Boolean.FALSE;
		}

		final Map<String, String> changeDatePaymentResults = getChangeDatePaymentResultsMap(accommodationReservationData,
				accommodationAvailabilityResponse);

		if (MapUtils.isEmpty(changeDatePaymentResults))
		{
			return Boolean.FALSE;
		}

		for (final AccommodationAddToCartForm form : addToCartForms)
		{
			final List<RoomRateCartData> rates = getAccommodationCartFacade().collectRoomRates(form);
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = collectAccommodationOrderEntryGroup(form,
					accommodationOrderEntryGroupModels);
			try
			{
				if (accommodationOrderEntryGroupModel == null)
				{
					return Boolean.FALSE;
				}
				final boolean isSuccess = getAccommodationCartFacade().addAccommodationsToCart(accommodationOrderEntryGroupModel,
						form.getAccommodationOfferingCode(), form.getAccommodationCode(), rates, form.getNumberOfRooms(),
						form.getRatePlanCode(), changeDatePaymentResults.get(TravelservicesConstants.BOOKING_PAYABLE_STATUS));
				if (!isSuccess)
				{
					getAccommodationCartFacade().emptyCart();
					return Boolean.FALSE;
				}
			}
			catch (final CommerceCartModificationException e)
			{
				getAccommodationCartFacade().emptyCart();
				LOG.info("Error when adding accommodation to cart", e);
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	protected List<AccommodationAddToCartForm> createAccommodationAddToCartForm(
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		if (accommodationAvailabilityResponse == null || CollectionUtils.isEmpty(accommodationAvailabilityResponse.getRoomStays()))
		{
			return Collections.emptyList();
		}

		final List<AccommodationAddToCartForm> accommodationAddToCartForms = new ArrayList<>(
				CollectionUtils.size(accommodationAvailabilityResponse.getRoomStays()));

		for (final RoomStayData roomStay : accommodationAvailabilityResponse.getRoomStays())
		{
			final AccommodationAddToCartForm accommodationAddToCartForm = new AccommodationAddToCartForm();
			accommodationAddToCartForm.setAccommodationOfferingCode(
					accommodationAvailabilityResponse.getAccommodationReference().getAccommodationOfferingCode());
			accommodationAddToCartForm.setRoomStayRefNumber(roomStay.getRoomStayRefNumber());
			accommodationAddToCartForm.setCheckInDateTime(
					TravelDateUtils.convertDateToStringDate(roomStay.getCheckInDate(), TravelservicesConstants.DATE_PATTERN));
			accommodationAddToCartForm.setCheckOutDateTime(
					TravelDateUtils.convertDateToStringDate(roomStay.getCheckOutDate(), TravelservicesConstants.DATE_PATTERN));
			accommodationAddToCartForm.setAccommodationCode(roomStay.getRoomTypes().get(0).getCode());
			accommodationAddToCartForm.setNumberOfRooms(1);
			final RatePlanData ratePlan = roomStay.getRatePlans().get(0);
			accommodationAddToCartForm.setRatePlanCode(ratePlan.getCode());
			if (CollectionUtils.isNotEmpty(ratePlan.getRoomRates()))
			{
				final int size = CollectionUtils.size(ratePlan.getRoomRates());
				final List<String> roomRateCodes = new ArrayList<>(size);
				final List<String> roomRateDates = new ArrayList<>(size);
				ratePlan.getRoomRates().forEach(roomRate ->
				{
					roomRateCodes.add(roomRate.getCode());
					roomRateDates.add(TravelDateUtils.convertDateToStringDate(roomRate.getStayDateRange().getStartTime(),
							TravelservicesConstants.DATE_PATTERN));
				});
				accommodationAddToCartForm.setRoomRateCodes(roomRateCodes);
				accommodationAddToCartForm.setRoomRateDates(roomRateDates);
			}
			accommodationAddToCartForms.add(accommodationAddToCartForm);
		}

		return accommodationAddToCartForms;
	}

	@Override
	public boolean deleteRoomRateEntries()
	{
		final CartModel cartModel = getTravelCartService().getSessionCart();
		if (cartModel == null || CollectionUtils.isEmpty(cartModel.getEntries()))
		{
			return Boolean.FALSE;
		}

		cartModel.getEntries().stream().filter(entry -> entry.getProduct() instanceof RoomRateProductModel && entry.getActive())
				.forEach(entry ->
				{
					entry.setActive(Boolean.FALSE);
					entry.setAmendStatus(AmendStatus.CHANGED);
					entry.setQuantity(0L);
				});
		getModelService().save(cartModel);
		getModelService().refresh(cartModel);
		return Boolean.TRUE;
	}


	@Override
	public List<AccommodationOrderEntryGroupModel> changeDatesForAccommodationOrderEntryGroup(final String checkInDate,
			final String checkOutDate)
	{
		final CartModel cartModel = getTravelCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels = getBookingService()
				.getAccommodationOrderEntryGroups(cartModel);
		if (CollectionUtils.isEmpty(accommodationOrderEntryGroupModels))
		{
			return Collections.emptyList();
		}

		final Date bookingCheckInDate = TravelDateUtils.convertStringDateToDate(checkInDate, TravelservicesConstants.DATE_PATTERN);
		final Date bookingCheckOutDate = TravelDateUtils.convertStringDateToDate(checkOutDate,
				TravelservicesConstants.DATE_PATTERN);

		accommodationOrderEntryGroupModels.forEach(orderEntryGroup ->
		{
			orderEntryGroup.setStartingDate(bookingCheckInDate);
			orderEntryGroup.setEndingDate(bookingCheckOutDate);

			final Date orderGroupCheckInDateTime = orderEntryGroup.getCheckInTime();
			final String orderGroupCheckInTime = TravelDateUtils.getTimeForDate(orderGroupCheckInDateTime,
					TravelservicesConstants.TIME_PATTERN);
			final Date updatedOrdergroupCheckInDateTime = TravelDateUtils.getDate(checkInDate + " " + orderGroupCheckInTime,
					TravelservicesConstants.DATE_TIME_PATTERN);
			orderEntryGroup.setCheckInTime(updatedOrdergroupCheckInDateTime);
			getModelService().save(orderEntryGroup);
			getModelService().refresh(orderEntryGroup);
		});
		return accommodationOrderEntryGroupModels;
	}

	@Override
	public Boolean createRefundPaymentTransaction(final PriceData totalToPay)
	{
		final CartModel currentCart = getTravelCartService().getSessionCart();
		return getBookingService().createRefundPaymentTransaction(currentCart, totalToPay.getValue().abs(),
				currentCart.getEntries());
	}

	@Override
	public boolean isCancelPossible(final String orderCode)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final OrderModel order;
		try
		{
			order = getCustomerAccountService().getOrderForCode(orderCode, currentBaseStore);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("Order with orderGUID " + orderCode + " not found for current user in current BaseStore", e);
			return false;
		}
		return getBookingService().isCancelPossible(order);
	}

	@Override
	public PriceData getRefundTotal(final String orderCode)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final OrderModel order = getCustomerAccountService().getOrderForCode(orderCode, currentBaseStore);

		final BigDecimal refundTotal = getBookingService().getTotalToRefund(order);
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, refundTotal, order.getCurrency().getIsocode());
	}

	@Override
	public PriceData getRefundForCancelledTraveller()
	{
		final CartModel currentCart = getTravelCartService().getSessionCart();
		final BigDecimal refundTotal = getBookingService().calculateTotalRefundForCancelledTraveller(currentCart);
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, refundTotal,
				currentCart.getCurrency().getIsocode());
	}

	@Override
	public PriceData getRefundTotal(final String orderCode, final OrderEntryType orderEntryType)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final OrderModel order = getCustomerAccountService().getOrderForCode(orderCode, currentBaseStore);

		final BigDecimal refundTotal = getBookingService().getTotalToRefund(order, orderEntryType);
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, refundTotal, order.getCurrency().getIsocode());
	}

	@Override
	public boolean cancelOrder(final String orderCode)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final OrderModel order;
		try
		{
			order = getCustomerAccountService().getOrderForCode(orderCode, currentBaseStore);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("Order with orderGUID " + orderCode + " not found for current user in current BaseStore", e);
			return false;
		}
		return getBookingService().cancelOrder(order);
	}

	@Override
	public boolean beginTravellerCancellation(final String orderCode, final String cancelledTravellerCode,
			final String cancelledTravellerUid, final String guid)
	{
		final CartModel cartWithoutCancelledTraveller = getTravelCartService().cancelTraveller(orderCode, cancelledTravellerCode,
				cancelledTravellerUid, guid);
		if (cartWithoutCancelledTraveller == null)
		{
			return false;
		}
		getTravelCartService().setSessionCart(cartWithoutCancelledTraveller);
		return true;
	}

	@Override
	public PriceData getTotalToPay()
	{
		final ReservationData reservation = getReservationFacade().getCurrentReservationData();
		return reservation.getTotalToPay();
	}

	@Override
	public PriceData getOrderTotalPaid(final String bookingReference)
	{
		//Retrieve order with bookingReferenceNumber
		OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (OrderStatus.AMENDMENTINPROGRESS.equals(orderModel.getStatus()))
		{
			orderModel = getBookingService().getOrderModelByOriginalOrderCode(bookingReference);
		}

		return Objects.nonNull(orderModel) ? getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				getBookingService().getOrderTotalPaid(orderModel), orderModel.getCurrency().getIsocode()) : null;
	}


	@Override
	public PriceData getBookingTotalByOrderEntryType(final String bookingReference, final OrderEntryType orderEntryType)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		final BigDecimal bookingTotal = BigDecimal
				.valueOf(getBookingService().getBookingTotalByOrderEntryType(orderModel, orderEntryType));
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, bookingTotal,
				orderModel.getCurrency().getIsocode());
	}

	@Override
	public boolean checkBookingJourneyType(final String orderCode, final BookingJourneyType bookingJourneyType)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(orderCode);
		return StringUtils.equalsIgnoreCase(bookingJourneyType.getCode(), orderModel.getBookingJourneyType().getCode());
	}

	/**
	 * @deprecated Deprecated since version 2.0. Use {@link #cancelTraveller(PriceData, TravellerData)} instead.
	 */
	@Deprecated
	@Override
	public boolean cancelTraveller(final PriceData totalToPay)
	{
		if (totalToPay.getValue().doubleValue() > 0d)
		{
			LOG.error("Unable to cancel a passenger where there is an additional payment required.");
			return false;
		}

		final boolean isTravellerCancelled = getBookingService().cancelTraveller(totalToPay.getValue().abs());
		if (isTravellerCancelled)
		{
			try
			{
				getCheckoutFacade().placeOrder();
			}
			catch (final InvalidCartException e)
			{
				LOG.error("Error when cancelling traveller.", e);
				return false;
			}
		}
		return true;
	}

	/**
	 * @deprecated Deprecated since version 4.0. Use {@link #cancelTraveller(PriceData, PriceData, TravellerData)}
	 *             instead.
	 */
	@Deprecated
	@Override
	public boolean cancelTraveller(final PriceData totalToPay, final TravellerData travellerData)
	{
		if (totalToPay.getValue().doubleValue() > 0d || Objects.isNull(travellerData))
		{
			LOG.error("Unable to cancel a passenger where there is an additional payment required.");
			return Boolean.FALSE;
		}

		final boolean isTravellerCancelled = getBookingService().cancelTraveller(totalToPay.getValue().abs(), travellerData);
		if (isTravellerCancelled)
		{
			try
			{
				getCheckoutFacade().placeOrder();
			}
			catch (final InvalidCartException e)
			{
				LOG.error("Error when cancelling traveller.", e);
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	@Override
	public boolean cancelTraveller(final PriceData totalToPay, final PriceData totalToRefund, final TravellerData travellerData)
	{
		if (totalToPay.getValue().doubleValue() > 0d || Objects.isNull(travellerData))
		{
			LOG.error("Unable to cancel a passenger where there is an additional payment required.");
			return Boolean.FALSE;
		}

		final boolean isTravellerCancelled = getBookingService().cancelTraveller(totalToRefund.getValue().abs(), travellerData);
		if (isTravellerCancelled)
		{
			try
			{
				getCheckoutFacade().placeOrder();
			}
			catch (final InvalidCartException e)
			{
				LOG.error("Error when cancelling traveller.", e);
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	@Override
	public String getCurrentUserUid()
	{
		final String uid;

		if (!getUserService().isAnonymousUser(getUserService().getCurrentUser()))
		{
			uid = getUserService().getCurrentUser().getUid();
		}
		else
		{
			uid = getSessionService().getAttribute(MANAGE_MY_BOOKING_GUEST_UID);
		}
		return uid;
	}

	@Override
	public boolean atleastOneAdultTravellerRemaining(final String orderCode, final String cancelledTravellerCode)
	{
		return getBookingService().atleastOneAdultTravellerRemaining(orderCode, cancelledTravellerCode);
	}

	@Override
	public Boolean isAmendment(final String bookingReference)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (OrderStatus.AMENDMENTINPROGRESS.equals(orderModel.getStatus()))
		{
			return Boolean.TRUE;
		}

		if (CollectionUtils.isEmpty(orderModel.getHistoryEntries()))
		{
			return Boolean.FALSE;
		}

		final List<OrderModel> previousOrderList = orderModel.getHistoryEntries().stream()
				.filter(entry -> entry.getPreviousOrderVersion() != null).map(OrderHistoryEntryModel::getPreviousOrderVersion)
				.collect(Collectors.toList());
		return CollectionUtils.isNotEmpty(previousOrderList)
				&& previousOrderList.stream().noneMatch(previousOrder -> notAllowedStatuses.contains(previousOrder.getStatus()));
	}

	@Override
	public ReservationData getDisruptedReservation(final String bookingReference)
	{
		//Retrieve order with bookingReferenceNumber
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);

		if (orderModel == null || orderModel.getHistoryEntries() == null)
		{
			return null;
		}

		final OrderHistoryEntryModel lastEntry = orderModel.getHistoryEntries().stream().reduce((entry1, entry2) -> entry2)
				.orElse(null);
		final OrderModel previousOrderModel = lastEntry.getPreviousOrderVersion();
		if (previousOrderModel == null)
		{
			return null;
		}

		final ReservationData disruptedReservation = convertOrderModelToReservationData(previousOrderModel);
		final List<DisruptionModel> disruptions = orderModel.getDisruptions();

		if (CollectionUtils.isEmpty(disruptions))
		{
			return null;
		}

		final List<ReservationItemData> disruptedReservationItems = new ArrayList<>();

		disruptions.forEach(disruptionModel ->
		{
			final Optional<ReservationItemData> disruptedReservationItem = disruptedReservation.getReservationItems().stream()
					.filter(item -> item.getOriginDestinationRefNumber() == disruptionModel.getOriginDestinationRefNumber())
					.findFirst();
			if (disruptedReservationItem.isPresent())
			{
				disruptedReservationItems.add(disruptedReservationItem.get());
			}
		});

		disruptedReservation.setReservationItems(disruptedReservationItems);

		return disruptedReservation;
	}

	@Override
	public boolean acceptOrder(final String orderCode)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(orderCode);
		if (OrderStatus.ACTIVE_DISRUPTED_PENDING.equals(orderModel.getTransportationOrderStatus()))
		{
			getBookingService().updateOrderStatus(orderModel, OrderStatus.ACTIVE_DISRUPTED);
			return true;
		}
		return false;
	}



	@Override
	public boolean unlinkBooking(final String orderCode)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(orderCode);
		return getBookingService().unlinkBooking(getUserService().getCurrentUser(), orderModel);
	}

	/**
	 * @param orderStatusCode
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected Integer getOrderStatusValue(final String orderStatusCode)
	{
		if (getOrderStatusValueMap().containsKey(orderStatusCode))
		{
			return getOrderStatusValueMap().get(orderStatusCode);
		}
		return getOrderStatusValueMap().get("DEFAULT");
	}

	@Override
	public void mapOrderToUserAccount(final String bookingReferenceNumber)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReferenceNumber);
		if (!isCurrentUserTheOwner(currentCustomer, orderModel)
				&& !isCurrentUserAndOrderLinked(currentCustomer, orderModel.getCode()) && !orderModel.getAdditionalSecurity())
		{
			final OrderUserAccountMappingModel orderUserAccountMapping = new OrderUserAccountMappingModel();
			orderUserAccountMapping.setOrderCode(orderModel.getCode());
			orderUserAccountMapping.setUser(currentCustomer);
			getModelService().save(orderUserAccountMapping);
		}

	}

	/**
	 * @param currentCustomer
	 * @param orderModel
	 * @return returns true if the current user is the owner
	 */
	public boolean isCurrentUserTheOwner(final CustomerModel currentCustomer, final OrderModel orderModel)
	{
		final CustomerModel owner = (CustomerModel) orderModel.getUser();
		return owner.equals(currentCustomer);
	}

	private boolean isCurrentUserAndOrderLinked(final CustomerModel currentCustomer, final String orderCode)
	{
		final OrderUserAccountMappingModel customerOrderLink = getCustomerAccountService().getOrderUserMapping(orderCode,
				currentCustomer);
		if (customerOrderLink != null)
		{
			return true;
		}
		return false;
	}


	@Override
	public boolean addRequestToRoomStayBooking(final String request, final int roomStayRefNumber, final String bookingReference)
	{
		try
		{
			getBookingService().addRequestToRoomStayBooking(request, roomStayRefNumber, bookingReference);
		}
		catch (final ModelSavingException | RequestKeyGeneratorException e)
		{
			LOG.error(String.format("Failed to create request for order %s", bookingReference));
			LOG.debug("Logging exception: ", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean removeRequestFromRoomStayBooking(final String requestCode, final int roomStayRefNumber,
			final String bookingReference)
	{
		try
		{
			getBookingService().removeRequestFromRoomStayBooking(requestCode, roomStayRefNumber, bookingReference);
		}
		catch (final ModelRemovalException | ModelNotFoundException e)
		{
			LOG.error(String.format("Failed to remove request for order %s", bookingReference));
			LOG.debug("Logging exception: ", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean beginPartialOrderCancellation(final String bookingReference, final OrderEntryType orderEntryType,
			final String guid)
	{
		final CartModel updatedCart = getTravelCartService().cancelPartialOrder(bookingReference, orderEntryType, guid);
		if (updatedCart == null)
		{
			return false;
		}
		getTravelCartService().setSessionCart(updatedCart);
		return true;
	}

	@Override
	public boolean cancelPartialOrder(final PriceData totalToRefund, final OrderEntryType orderEntryType)
	{

		if (totalToRefund.getValue().doubleValue() < 0d)
		{
			LOG.error("Unable to cancel a partial order where there is an additional payment required.");
			return false;
		}

		final boolean isPartialOrderCancelled = getBookingService().cancelPartialOrder(totalToRefund.getValue().abs(),
				orderEntryType);

		if (isPartialOrderCancelled)
		{
			try
			{
				getCheckoutFacade().placeOrder();
			}
			catch (final InvalidCartException e)
			{
				LOG.error("Error when cancelling partial order.", e);
				return false;
			}
		}
		return true;

	}

	@Override
	public boolean isCurrentCartOfType(final String bookingType)
	{
		if (!getTravelCartService().hasSessionCart())
		{
			return false;
		}

		return getBookingService().isAbstractOrderOfType(getTravelCartService().getSessionCart(), bookingType);
	}

	@Override
	public boolean isOrderOfType(final String bookingReference, final String bookingType)
	{
		return getBookingService().isAbstractOrderOfType(getBookingService().getOrderModelFromStore(bookingReference), bookingType);
	}

	/**
	 * Creates a list of RoomRateCartData respective to AccommodationAddToCartForm
	 *
	 * @param form
	 * 		object of AccommodationAddToCartForm
	 * @param accommodationOrderEntryGroupModels
	 * 		list of AccommodationOrderEntryGroupModel
	 * @return accommodationOrderEntryGroupModel object of AccommodationOrderEntryGroupModel
	 */
	protected AccommodationOrderEntryGroupModel collectAccommodationOrderEntryGroup(final AccommodationAddToCartForm form,
			final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModels)
	{
		final Optional<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroupModel = accommodationOrderEntryGroupModels
				.stream().filter(orderEntryGroupModel -> Objects
						.equals(orderEntryGroupModel.getRoomStayRefNumber(), form.getRoomStayRefNumber()))
				.findFirst();
		return accommodationOrderEntryGroupModel.orElse(null);
	}

	@Override
	public BigDecimal getOrderTotalPaidForOrderEntryType(final String bookingReference, final OrderEntryType orderEntryType)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		return getBookingService().getOrderTotalPaidForOrderEntryType(orderModel, orderEntryType);
	}

	@Override
	public boolean placeOrder()
	{
		try
		{
			getCheckoutFacade().placeOrder();
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Error Placing Order.", e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public BigDecimal getOrderTotalToPayForOrderEntryType(final String bookingReference, final OrderEntryType orderEntryType)
	{
		final BigDecimal totalPaid = getOrderTotalPaidForOrderEntryType(bookingReference, orderEntryType);

		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		final Double totalPrice = getBookingService().getOrderTotalPriceByType(orderModel, orderEntryType);

		return BigDecimal.valueOf(totalPrice).subtract(totalPaid);
	}

	@Override
	public BigDecimal getOrderTotalToPayForChangeDates()
	{
		return getBookingService().getOrderTotalToPayForChangeDates();
	}

	@Override
	public Map<String, String> getChangeDatePaymentResultsMap(final AccommodationReservationData accommodationReservationData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		return getCalculatePaymentTypeForChangeDatesStrategy().calculate(accommodationReservationData,
				accommodationAvailabilityResponse);
	}


	@Override
	public PriceData getNotRefundableAmount(final String bookingReference)
	{
		OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		if (OrderStatus.AMENDMENTINPROGRESS.equals(orderModel.getStatus()))
		{
			orderModel = getBookingService().getOrderModelByOriginalOrderCode(bookingReference);
		}


		BigDecimal totalPrice = BigDecimal.valueOf(orderModel.getTotalPrice());
		if (orderModel.getNet())
		{
			totalPrice = totalPrice.add(BigDecimal.valueOf(orderModel.getTotalTax()));
		}

		final BigDecimal totalPaid = getBookingService().getOrderTotalPaid(orderModel);
		final BigDecimal retainedAmount = totalPaid.subtract(totalPrice);

		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				retainedAmount.compareTo(BigDecimal.ZERO) > 0 ? retainedAmount : BigDecimal.ZERO, orderModel.getCurrency());
	}

	@Override
	public boolean isAdditionalSecurityActive(final String bookingReference)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);
		return orderModel.getAdditionalSecurity();
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
	 * 		the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the reservationFacade
	 */
	protected ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	/**
	 * @param reservationFacade
	 * 		the reservationFacade to set
	 */
	@Required
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
	}

	/**
	 * @return UserService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 * 		the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 * 		the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 * 		the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the checkoutFacade
	 */
	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	/**
	 * @param checkoutFacade
	 * 		the checkoutFacade to set
	 */
	@Required
	public void setCheckoutFacade(final CheckoutFacade checkoutFacade)
	{
		this.checkoutFacade = checkoutFacade;
	}

	/**
	 * @return the accommodationCartFacade
	 */
	protected AccommodationCartFacade getAccommodationCartFacade()
	{
		return accommodationCartFacade;
	}

	/**
	 * @param accommodationCartFacade
	 * 		the accommodationCartFacade to set
	 */
	@Required
	public void setAccommodationCartFacade(final AccommodationCartFacade accommodationCartFacade)
	{
		this.accommodationCartFacade = accommodationCartFacade;
	}


	/**
	 * @return the priceDataFactory
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @param priceDataFactory
	 * 		the priceDataFactory to set
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets travel checkout customer strategy.
	 *
	 * @return the travel checkout customer strategy
	 */
	protected TravelCheckoutCustomerStrategy getTravelCheckoutCustomerStrategy()
	{
		return travelCheckoutCustomerStrategy;
	}

	/**
	 * Sets travel checkout customer strategy.
	 *
	 * @param travelCheckoutCustomerStrategy
	 * 		the travel checkout customer strategy
	 */
	@Required
	public void setTravelCheckoutCustomerStrategy(final TravelCheckoutCustomerStrategy travelCheckoutCustomerStrategy)
	{
		this.travelCheckoutCustomerStrategy = travelCheckoutCustomerStrategy;
	}

	/**
	 * Gets order status value map.
	 *
	 * @return the order status value map
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected Map<String, Integer> getOrderStatusValueMap()
	{
		return orderStatusValueMap;
	}

	/**
	 * Sets order status value map.
	 *
	 * @param orderStatusValueMap
	 * 		the order status value map
	 */
	@Required
	public void setOrderStatusValueMap(final Map<String, Integer> orderStatusValueMap)
	{
		this.orderStatusValueMap = orderStatusValueMap;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets customer account service.
	 *
	 * @return the customer account service
	 */
	protected TravelCustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * Sets customer account service.
	 *
	 * @param customerAccountService
	 * 		the customer account service
	 */
	@Required
	public void setCustomerAccountService(final TravelCustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * Gets basic accommodation reservation pipeline manager.
	 *
	 * @return the basic accommodation reservation pipeline manager
	 */
	protected AccommodationReservationPipelineManager getBasicAccommodationReservationPipelineManager()
	{
		return basicAccommodationReservationPipelineManager;
	}

	/**
	 * Sets basic accommodation reservation pipeline manager.
	 *
	 * @param basicAccommodationReservationPipelineManager
	 * 		the basic accommodation reservation pipeline manager
	 */
	@Required
	public void setBasicAccommodationReservationPipelineManager(
			final AccommodationReservationPipelineManager basicAccommodationReservationPipelineManager)
	{
		this.basicAccommodationReservationPipelineManager = basicAccommodationReservationPipelineManager;
	}

	/**
	 * Gets full accommodation reservation pipeline manager.
	 *
	 * @return the full accommodation reservation pipeline manager
	 */
	protected AccommodationReservationPipelineManager getFullAccommodationReservationPipelineManager()
	{
		return fullAccommodationReservationPipelineManager;
	}

	/**
	 * Sets full accommodation reservation pipeline manager.
	 *
	 * @param fullAccommodationReservationPipelineManager
	 * 		the full accommodation reservation pipeline manager
	 */
	@Required
	public void setFullAccommodationReservationPipelineManager(
			final AccommodationReservationPipelineManager fullAccommodationReservationPipelineManager)
	{
		this.fullAccommodationReservationPipelineManager = fullAccommodationReservationPipelineManager;
	}

	/**
	 * @return the guestCountReverseConverter
	 */
	protected Converter<PassengerTypeQuantityData, GuestCountModel> getGuestCountReverseConverter()
	{
		return guestCountReverseConverter;
	}


	/**
	 * @param guestCountReverseConverter
	 * 		the guestCountReverseConverter to set
	 */
	@Required
	public void setGuestCountReverseConverter(
			final Converter<PassengerTypeQuantityData, GuestCountModel> guestCountReverseConverter)
	{
		this.guestCountReverseConverter = guestCountReverseConverter;
	}


	/**
	 * @return the guestOccupancyConverter
	 */
	protected Converter<GuestOccupancyModel, GuestOccupancyData> getGuestOccupancyConverter()
	{
		return guestOccupancyConverter;
	}


	/**
	 * @param guestOccupancyConverter
	 * 		the guestOccupancyConverter to set
	 */
	@Required
	public void setGuestOccupancyConverter(final Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter)
	{
		this.guestOccupancyConverter = guestOccupancyConverter;
	}

	/**
	 * @return the roomPreferenceService
	 */
	protected RoomPreferenceService getRoomPreferenceService()
	{
		return roomPreferenceService;
	}


	/**
	 * @param roomPreferenceService
	 * 		the roomPreferenceService to set
	 */
	@Required
	public void setRoomPreferenceService(final RoomPreferenceService roomPreferenceService)
	{
		this.roomPreferenceService = roomPreferenceService;
	}


	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	/**
	 * @param commonI18NService
	 * 		the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets the enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets the enumeration service.
	 *
	 * @param enumerationService
	 * 		the new enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the calculatePaymentTypeForChangeDatesStrategy
	 */
	protected CalculatePaymentTypeForChangeDatesStrategy getCalculatePaymentTypeForChangeDatesStrategy()
	{
		return calculatePaymentTypeForChangeDatesStrategy;
	}

	/**
	 * @param calculatePaymentTypeForChangeDatesStrategy
	 * 		the calculatePaymentTypeForChangeDatesStrategy to set
	 */
	@Required
	public void setCalculatePaymentTypeForChangeDatesStrategy(
			final CalculatePaymentTypeForChangeDatesStrategy calculatePaymentTypeForChangeDatesStrategy)
	{
		this.calculatePaymentTypeForChangeDatesStrategy = calculatePaymentTypeForChangeDatesStrategy;
	}

	/**
	 * @return notAllowedStatuses
	 */
	protected List<OrderStatus> getNotAllowedStatuses()
	{
		return notAllowedStatuses;
	}

	/**
	 * @param notAllowedStatuses
	 * 		the notAllowedStatuses to set
	 */
	@Required
	public void setNotAllowedStatuses(final List<OrderStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}


	/**
	 * @return the travelOfferingStatusSearchPipelineManager
	 */
	protected GlobalTravelReservationPipelineManager getTravelOfferingStatusSearchPipelineManager()
	{
		return travelOfferingStatusSearchPipelineManager;
	}


	/**
	 * @param travelOfferingStatusSearchPipelineManager
	 * 		the travelOfferingStatusSearchPipelineManager to set
	 */
	@Required
	public void setTravelOfferingStatusSearchPipelineManager(
			final GlobalTravelReservationPipelineManager travelOfferingStatusSearchPipelineManager)
	{
		this.travelOfferingStatusSearchPipelineManager = travelOfferingStatusSearchPipelineManager;
	}


	/**
	 * @return the guestDetailsAccommodationReservationPipelineManager
	 */
	protected AccommodationReservationPipelineManager getGuestDetailsAccommodationReservationPipelineManager()
	{
		return guestDetailsAccommodationReservationPipelineManager;
	}


	/**
	 * @param guestDetailsAccommodationReservationPipelineManager
	 * 		the guestDetailsAccommodationReservationPipelineManager to set
	 */
	@Required
	public void setGuestDetailsAccommodationReservationPipelineManager(
			final AccommodationReservationPipelineManager guestDetailsAccommodationReservationPipelineManager)
	{
		this.guestDetailsAccommodationReservationPipelineManager = guestDetailsAccommodationReservationPipelineManager;
	}

	/**
	 * @return the basicGlobalTravelReservationPipelineManager
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected GlobalTravelReservationPipelineManager getBasicGlobalTravelReservationPipelineManager()
	{
		return basicGlobalTravelReservationPipelineManager;
	}

	/**
	 * @param basicGlobalTravelReservationPipelineManager
	 * 		the basicGlobalTravelReservationPipelineManager to set
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setBasicGlobalTravelReservationPipelineManager(
			final GlobalTravelReservationPipelineManager basicGlobalTravelReservationPipelineManager)
	{
		this.basicGlobalTravelReservationPipelineManager = basicGlobalTravelReservationPipelineManager;
	}

	/**
	 * @return Converter<AbstractOrderModel, ReservationData>
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected ReservationPipelineManager getReservationItemPipelineManager()
	{
		return reservationItemPipelineManager;
	}

	/**
	 * @param reservationItemPipelineManager
	 * 		the reservationItemPipelineManager to set
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	public void setReservationItemPipelineManager(final ReservationPipelineManager reservationItemPipelineManager)
	{
		this.reservationItemPipelineManager = reservationItemPipelineManager;
	}

	/**
	 * @return the bookingListFacade
	 */
	protected BookingListFacade getBookingListFacade()
	{
		return bookingListFacade;
	}


	/**
	 * @param bookingListFacade
	 * 		the bookingListFacade to set
	 */
	@Required
	public void setBookingListFacade(final BookingListFacade bookingListFacade)
	{
		this.bookingListFacade = bookingListFacade;
	}

	/**
	 * @return the guestCountService
	 */
	protected GuestCountService getGuestCountService()
	{
		return guestCountService;
	}

	/**
	 * @param guestCountService
	 * 		the guestCountService to set
	 */
	@Required
	public void setGuestCountService(final GuestCountService guestCountService)
	{
		this.guestCountService = guestCountService;
	}


	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}


	/**
	 * @param travelCommercePriceFacade
	 * 		the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets accommodation booking facade.
	 *
	 * @return the accommodation booking facade
	 */
	protected AccommodationBookingFacade getAccommodationBookingFacade()
	{
		return accommodationBookingFacade;
	}

	/**
	 * Sets accommodation booking facade.
	 *
	 * @param accommodationBookingFacade
	 * 		the accommodation booking facade
	 */
	@Required
	public void setAccommodationBookingFacade(final AccommodationBookingFacade accommodationBookingFacade)
	{
		this.accommodationBookingFacade = accommodationBookingFacade;
	}
}
