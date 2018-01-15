/*
 *
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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.ProfileData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.accommodation.ServiceDetailData;
import de.hybris.platform.commercefacades.accommodation.ServiceRateData;
import de.hybris.platform.commercefacades.accommodation.TimeSpanData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.CancelPenaltiesDescriptionCreationStrategy;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Accommodation reservation reserved room stay handler.
 */
public class AccommodationReservationReservedRoomStayHandler implements AccommodationReservationHandler
{
	private BookingService bookingService;

	private Converter<RatePlanModel, RatePlanData> ratePlanConverter;

	private Converter<AccommodationModel, RoomTypeData> roomTypeConverter;

	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	private Converter<SpecialRequestDetailModel, SpecialRequestDetailData> specialRequestDetailsConverter;

	private Converter<ProductModel, ProductData> productConverter;

	private CancelPenaltiesDescriptionCreationStrategy cancelPenaltiesDescriptionCreationStrategy;

	private Converter<RoomPreferenceModel, RoomPreferenceData> roomPreferenceConverter;

	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * Handle.
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @param accommodationReservationData
	 *           the accommodation reservation data
	 */
	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrder);

		final List<ReservedRoomStayData> roomStays = new ArrayList<ReservedRoomStayData>();
		entryGroups.forEach(entryGroup -> {
			final ReservedRoomStayData roomStay = new ReservedRoomStayData();
			roomStay.setCheckInDate(entryGroup.getStartingDate());
			roomStay.setCheckOutDate(entryGroup.getEndingDate());
			roomStay.setRoomStayRefNumber(entryGroup.getRoomStayRefNumber());
			roomStay.setRatePlans(Arrays.asList(getRatePlanConverter().convert(entryGroup.getRatePlan())));
			roomStay.setRoomTypes(Arrays.asList(getRoomTypeConverter().convert(entryGroup.getAccommodation())));
			final boolean isModifiable = entryGroup.getEntries().stream()
					.allMatch(entry -> entry.getActive() && AmendStatus.NEW.equals(entry.getAmendStatus()));
			roomStay.setNonModifiable(!isModifiable);
			setServices(entryGroup, roomStay, abstractOrder);
			setGuestCounts(entryGroup, roomStay);
			setGuestData(entryGroup, roomStay);
			setTotalsPerRoom(entryGroup, abstractOrder, roomStay);
			setSpecialRequestDetails(entryGroup, roomStay);
			setRoomPreferences(entryGroup, roomStay);
			setArrivalTime(entryGroup, roomStay);
			getCancelPenaltiesDescriptionCreationStrategy().updateCancelPenaltiesDescription(entryGroup.getRatePlan(), roomStay);
			roomStays.add(roomStay);
		});

		accommodationReservationData.setRoomStays(roomStays);
	}

	/**
	 * Sets special request details.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param roomStay
	 *           the room stay
	 */
	protected void setSpecialRequestDetails(final AccommodationOrderEntryGroupModel entryGroup,
			final ReservedRoomStayData roomStay)
	{
		if (Objects.nonNull(entryGroup.getSpecialRequestDetail()))
		{
			roomStay.setSpecialRequestDetail(getSpecialRequestDetailsConverter().convert(entryGroup.getSpecialRequestDetail()));
		}
	}

	/**
	 * Sets room preferences.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param roomStay
	 *           the room stay
	 */
	protected void setRoomPreferences(final AccommodationOrderEntryGroupModel entryGroup, final ReservedRoomStayData roomStay)
	{
		if (CollectionUtils.isNotEmpty(entryGroup.getRoomPreferences()))
		{
			roomStay.setRoomPreferences(Converters.convertAll(entryGroup.getRoomPreferences(), getRoomPreferenceConverter()));
		}
	}

	/**
	 * Sets totals per room.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param abstractOrder
	 *           the abstract order
	 * @param roomStay
	 *           the room stay
	 */
	public void setTotalsPerRoom(final AccommodationOrderEntryGroupModel entryGroup, final AbstractOrderModel abstractOrder,
			final ReservedRoomStayData roomStay)
	{
		Double totalPerRoom = 0d;
		Double baseRate = 0d;
		Double basePriceRate = 0d;
		Double totalTaxes = 0d;
		final List<TaxData> taxes = new ArrayList<>();
		final String currencyIso = abstractOrder.getCurrency().getIsocode();
		for (final AbstractOrderEntryModel entry : getEntries(entryGroup))
		{
			if (entry.getProduct() instanceof RoomRateProductModel)
			{
				basePriceRate += entry.getBasePrice() * entry.getQuantity();
				baseRate += entry.getTotalPrice();
			}
			totalPerRoom = totalPerRoom + entry.getTotalPrice();
			if (CollectionUtils.isNotEmpty(entry.getTaxValues()))
			{
				taxes.addAll(entry.getTaxValues().stream()
						.map(taxValue -> createTaxDataFromValue(taxValue.getAppliedValue(), currencyIso)).collect(Collectors.toList()));
			}
		}
		if (CollectionUtils.isNotEmpty(taxes))
		{
			totalTaxes = taxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		}
		if (abstractOrder.getNet())
		{
			totalPerRoom = Double.sum(totalPerRoom, totalTaxes);
			baseRate = Double.sum(baseRate, totalTaxes);
		}
		final RateData totalPerRoomStay = new RateData();
		totalPerRoomStay
				.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalPerRoom), currencyIso));
		totalPerRoomStay.setTaxes(taxes);
		totalPerRoomStay.setTotalTax(createTaxDataFromValue(totalTaxes, currencyIso));
		roomStay.setTotalRate(totalPerRoomStay);

		final RateData basePerRoomStay = new RateData();
		basePerRoomStay
				.setBasePrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(basePriceRate), currencyIso));
		basePerRoomStay.setTaxes(taxes);
		final Double totalTaxValue = taxes.stream().mapToDouble(taxData -> taxData.getPrice().getValue().doubleValue()).sum();
		basePerRoomStay.setTotalTax(createTaxDataFromValue(totalTaxValue, currencyIso));
		basePerRoomStay.setActualRate(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(baseRate), currencyIso));
		basePerRoomStay.setTotalDiscount(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(basePriceRate + totalTaxValue - baseRate), currencyIso));
		roomStay.setBaseRate(basePerRoomStay);
	}

	/**
	 * Creates tax data from value
	 *
	 * @param value
	 * @return
	 */
	protected TaxData createTaxDataFromValue(final Double value, final String currencyIso)
	{
		final TaxData taxData = new TaxData();
		taxData.setPrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(value), currencyIso));
		return taxData;
	}

	/**
	 * Sets services.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param roomStay
	 *           the room stay
	 * @param abstractOrder
	 *           the abstract order
	 */
	public void setServices(final AccommodationOrderEntryGroupModel entryGroup, final ReservedRoomStayData roomStay,
			final AbstractOrderModel abstractOrder)
	{
		final List<ServiceData> services = new ArrayList<ServiceData>();
		getEntries(entryGroup).stream().filter(entry -> isAServiceProduct(entry.getProduct())).forEach(entry -> {
			final ServiceData service = new ServiceData();

			service.setCode(entry.getProduct().getCode());
			service.setQuantity(entry.getQuantity().intValue());
			service.setRatePlanCode(roomStay.getRatePlans().get(0).getCode());

			final ServiceDetailData serviceDetails = new ServiceDetailData();
			serviceDetails.setProduct(getProductConverter().convert(entry.getProduct()));
			serviceDetails.setGuestCounts(roomStay.getGuestCounts());
			service.setServiceDetails(serviceDetails);

			service.setPrice(getServiceRate(service, roomStay, entry, abstractOrder));

			services.add(service);
		});
		roomStay.setServices(services);
	}

	/**
	 * Is a service product boolean.
	 *
	 * @param product
	 *           the product
	 * @return the boolean
	 */
	protected boolean isAServiceProduct(final ProductModel product)
	{
		return !(product instanceof RoomRateProductModel);
	}

	/**
	 * Gets service rate.
	 *
	 * @param serviceData
	 *           the service data
	 * @param reservedRoomStayData
	 *           the reserved room stay data
	 * @param entry
	 *           the entry
	 * @param abstractOrder
	 *           the abstract order
	 * @return the service rate
	 */
	protected ServiceRateData getServiceRate(final ServiceData serviceData, final ReservedRoomStayData reservedRoomStayData,
			final AbstractOrderEntryModel entry, final AbstractOrderModel abstractOrder)
	{
		final ServiceRateData serviceRateData = new ServiceRateData();

		final TimeSpanData timeSpan = new TimeSpanData();
		timeSpan.setStartDate(reservedRoomStayData.getCheckInDate());
		timeSpan.setEndDate(reservedRoomStayData.getCheckOutDate());
		serviceRateData.setTimeSpan(timeSpan);
		serviceRateData.setBasePrice(getBasePriceData(entry, abstractOrder));
		serviceRateData.setTotal(getTotalPriceData(entry, abstractOrder));

		return serviceRateData;
	}


	/**
	 * Gets the total price data.
	 *
	 * @param entry
	 *           the entry
	 * @param abstractOrder
	 *           the abstract order
	 * @return the total price data
	 */
	protected PriceData getTotalPriceData(final AbstractOrderEntryModel entry, final AbstractOrderModel abstractOrder)
	{
		final BigDecimal totalPriceValue = BigDecimal.valueOf(entry.getTotalPrice());
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, totalPriceValue,
				abstractOrder.getCurrency().getIsocode());
	}

	/**
	 * Gets the base price data.
	 *
	 * @param entry
	 *           the entry
	 * @param abstractOrder
	 *           the abstract order
	 * @return the base price data
	 */
	protected PriceData getBasePriceData(final AbstractOrderEntryModel entry, final AbstractOrderModel abstractOrder)
	{
		final BigDecimal basePriceValue = BigDecimal.valueOf(entry.getBasePrice());
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, basePriceValue,
				abstractOrder.getCurrency().getIsocode());
	}

	/**
	 * Sets guest counts.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param roomStay
	 *           the room stay
	 */
	protected void setGuestCounts(final AccommodationOrderEntryGroupModel entryGroup, final ReservedRoomStayData roomStay)
	{
		final List<GuestCountModel> guestCounts = entryGroup.getGuestCounts();
		final List<PassengerTypeQuantityData> passengerTypeQuantities = new ArrayList<PassengerTypeQuantityData>();

		guestCounts.forEach(guestCount -> {
			final PassengerTypeQuantityData passengerTypeQuantity = new PassengerTypeQuantityData();
			passengerTypeQuantity.setPassengerType(getPassengerTypeConverter().convert(guestCount.getPassengerType()));
			passengerTypeQuantity.setQuantity(guestCount.getQuantity());
			passengerTypeQuantities.add(passengerTypeQuantity);
		});

		roomStay.setGuestCounts(passengerTypeQuantities);
	}

	/**
	 * Sets guest data.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @param roomStay
	 *           the room stay
	 */
	protected void setGuestData(final AccommodationOrderEntryGroupModel entryGroup, final ReservedRoomStayData roomStay)
	{
		final GuestData guestData = new GuestData();
		guestData.setPrimaryIndicator(true);
		final ProfileData profileData = new ProfileData();
		profileData.setContactNumber(entryGroup.getContactNumber());
		profileData.setFirstName(entryGroup.getFirstName());
		profileData.setLastName(entryGroup.getLastName());
		profileData.setEmail(entryGroup.getContactEmail());
		guestData.setProfile(profileData);
		roomStay.setReservedGuests(Arrays.asList(guestData));

	}

	/**
	 * Returns the list of AbstractOrderEntryModels for the given entryGroup that are active and with quantityStatus
	 * different from DEAD.
	 *
	 * @param entryGroup
	 *           as the entryGroup
	 * @return a list of AbstractOrderEntryModels
	 */
	protected List<AbstractOrderEntryModel> getEntries(final AccommodationOrderEntryGroupModel entryGroup)
	{
		return entryGroup.getEntries().stream().filter(
				entry -> BooleanUtils.isTrue(entry.getActive()) && !Objects.equals(OrderEntryStatus.DEAD, entry.getQuantityStatus()))
				.collect(Collectors.toList());
	}

	protected void setArrivalTime(final AccommodationOrderEntryGroupModel entryGroup, final ReservedRoomStayData roomStay)
	{
		if (Objects.nonNull(entryGroup.getCheckInTime()))
		{
			roomStay.setArrivalTime(TravelDateUtils.getTimeForDate(entryGroup.getCheckInTime(), "HH:mm"));
		}
	}

	/**
	 * Gets rate plan converter.
	 *
	 * @return the rate plan converter
	 */
	protected Converter<RatePlanModel, RatePlanData> getRatePlanConverter()
	{
		return ratePlanConverter;
	}

	/**
	 * Sets rate plan converter.
	 *
	 * @param ratePlanConverter
	 *           the rate plan converter
	 */
	@Required
	public void setRatePlanConverter(final Converter<RatePlanModel, RatePlanData> ratePlanConverter)
	{
		this.ratePlanConverter = ratePlanConverter;
	}

	/**
	 * Gets room type converter.
	 *
	 * @return the room type converter
	 */
	protected Converter<AccommodationModel, RoomTypeData> getRoomTypeConverter()
	{
		return roomTypeConverter;
	}

	/**
	 * Sets room type converter.
	 *
	 * @param roomTypeConverter
	 *           the room type converter
	 */
	@Required
	public void setRoomTypeConverter(final Converter<AccommodationModel, RoomTypeData> roomTypeConverter)
	{
		this.roomTypeConverter = roomTypeConverter;
	}


	/**
	 * Gets passenger type converter.
	 *
	 * @return the passenger type converter
	 */
	protected Converter<PassengerTypeModel, PassengerTypeData> getPassengerTypeConverter()
	{
		return passengerTypeConverter;
	}

	/**
	 * Sets passenger type converter.
	 *
	 * @param passengerTypeConverter
	 *           the passenger type converter
	 */
	@Required
	public void setPassengerTypeConverter(final Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the price data factory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the price data factory
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
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
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets booking service.
	 *
	 * @return the booking service
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 *           the booking service
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * Gets product converter.
	 *
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * Sets product converter.
	 *
	 * @param productConverter
	 *           the productConverter to set
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	/**
	 * Gets special request details converter.
	 *
	 * @return the specialRequestDetailsConverter
	 */
	protected Converter<SpecialRequestDetailModel, SpecialRequestDetailData> getSpecialRequestDetailsConverter()
	{
		return specialRequestDetailsConverter;
	}

	/**
	 * Sets special request details converter.
	 *
	 * @param specialRequestDetailsConverter
	 *           the specialRequestDetailsConverter to set
	 */
	@Required
	public void setSpecialRequestDetailsConverter(
			final Converter<SpecialRequestDetailModel, SpecialRequestDetailData> specialRequestDetailsConverter)
	{
		this.specialRequestDetailsConverter = specialRequestDetailsConverter;
	}

	/**
	 * Gets the cancel penalties description creation strategy.
	 *
	 * @return the cancelPenaltiesDescriptionCreationStrategy
	 */
	protected CancelPenaltiesDescriptionCreationStrategy getCancelPenaltiesDescriptionCreationStrategy()
	{
		return cancelPenaltiesDescriptionCreationStrategy;
	}

	/**
	 * Sets the cancel penalties description creation strategy.
	 *
	 * @param cancelPenaltiesDescriptionCreationStrategy
	 *           the cancelPenaltiesDescriptionCreationStrategy to set
	 */
	@Required
	public void setCancelPenaltiesDescriptionCreationStrategy(
			final CancelPenaltiesDescriptionCreationStrategy cancelPenaltiesDescriptionCreationStrategy)
	{
		this.cancelPenaltiesDescriptionCreationStrategy = cancelPenaltiesDescriptionCreationStrategy;
	}

	/**
	 * Gets the room preference converter.
	 *
	 * @return the roomPreferenceConverter
	 */
	protected Converter<RoomPreferenceModel, RoomPreferenceData> getRoomPreferenceConverter()
	{
		return roomPreferenceConverter;
	}

	/**
	 * Sets the room preference converter.
	 *
	 * @param roomPreferenceConverter
	 *           the roomPreferenceConverter to set
	 */
	@Required
	public void setRoomPreferenceConverter(final Converter<RoomPreferenceModel, RoomPreferenceData> roomPreferenceConverter)
	{
		this.roomPreferenceConverter = roomPreferenceConverter;
	}
}
