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

package de.hybris.platform.travelfacades.accommodation.reservation.utils;

import de.hybris.platform.commercefacades.accommodation.AccommodationFacilityData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AwardData;
import de.hybris.platform.commercefacades.accommodation.DayRateData;
import de.hybris.platform.commercefacades.accommodation.GuestData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.ProfileData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.commercefacades.accommodation.RoomTypeData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.commercefacades.accommodation.ServiceDetailData;
import de.hybris.platform.commercefacades.accommodation.ServiceRateData;
import de.hybris.platform.commercefacades.accommodation.TimeSpanData;
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RateRangeData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.enums.AwardType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



/**
 * Creates a mock AccommodationReservationData object
 */
public class MockAccommodationReservationDataUtils
{

	/**
	 * Returns an AccommodationReservationData with mocked values
	 *
	 * @return an AccommodationReservationData with mocked values
	 */
	public static AccommodationReservationData createAccommodationReservationData()
	{
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		reservationData.setCode("000001");
		reservationData.setAccommodationReference(createAccommodationReference());
		reservationData.setRoomStays(createRoomStays());

		return reservationData;
	}

	protected static PropertyData createAccommodationReference()
	{
		final PropertyData propertyData = new PropertyData();

		propertyData.setAccommodationOfferingCode("PARK_PLAZA_HOTELS__AMP__RESORTS_PARK_PLAZA_SHERLOCK_HOLMES_LONDON");
		propertyData.setAccommodationOfferingName("Park Plaza Sherlock Holmes London");
		propertyData.setAddress(createAddressData());
		propertyData.setAmenities(createAmenitiesList());
		propertyData.setAwards(createAwardData());

		final PositionData position = createPositionData();
		propertyData.setRelativePosition(position);
		propertyData.setPosition(position);

		propertyData.setRateRange(createRateRangeData());
		propertyData.setChainCode("PARK_PLAZA_HOTELS__AMP__RESORTS");
		propertyData.setChainName("Park Plaza Hotels &amp; Resorts");

		propertyData.setLocation(createLocation());
		propertyData.setPromoted(Boolean.FALSE);

		return propertyData;
	}

	protected static AddressData createAddressData()
	{
		final AddressData addressData = new AddressData();
		addressData.setFormattedAddress("Baker Street, 108, London, W1U 6LJ, United Kingdom");
		return addressData;
	}

	protected static List<FacilityData> createAmenitiesList()
	{
		final FacilityData facilityData1 = new FacilityData();
		facilityData1.setDescription("Bathroom");
		facilityData1.setFacilityType("BATHROOM");

		final FacilityData facilityData2 = new FacilityData();
		facilityData2.setDescription("Free toiletries");
		facilityData2.setFacilityType("BATHROOM");

		final FacilityData facilityData3 = new FacilityData();
		facilityData3.setDescription("Laundry");
		facilityData3.setFacilityType("Services");

		final FacilityData facilityData4 = new FacilityData();
		facilityData4.setDescription("Minibar");
		facilityData4.setFacilityType("Food drink");

		return Arrays.asList(facilityData1, facilityData2, facilityData3, facilityData4);
	}

	protected static List<AwardData> createAwardData()
	{
		final AwardData awardDataSR = new AwardData();
		awardDataSR.setType(AwardType.STAR_RATING);
		awardDataSR.setRating(Double.valueOf(4));

		final AwardData awardDataUR = new AwardData();
		awardDataUR.setType(AwardType.USER_RATING);
		awardDataUR.setRating(Double.valueOf(8.0));

		return Arrays.asList(awardDataSR, awardDataUR);
	}

	protected static PositionData createPositionData()
	{
		final PositionData position = new PositionData();
		position.setLatitude(Double.valueOf(51.5208982));
		position.setLongitude(Double.valueOf(-0.15681468));
		return position;
	}

	protected static RateRangeData createRateRangeData()
	{
		final RateRangeData rateRange = new RateRangeData();
		rateRange.setActualRate(createPriceData(300));
		rateRange.setWasRate(createPriceData(450));
		rateRange.setTotalDiscount(createPriceData(150));
		rateRange.setCurrencyCode("GBP");
		rateRange.setDayRates(createDayRatesList());
		return rateRange;
	}

	protected static List<DayRateData> createDayRatesList()
	{
		final DayRateData dayRate1 = new DayRateData();
		dayRate1.setDateOfStay(TravelDateUtils.convertStringDateToDate("01/12/2016", TravelservicesConstants.DATE_PATTERN));
		dayRate1.setDailyActualRate(createPriceData(100));
		dayRate1.setDailyWasRate(createPriceData(150));
		dayRate1.setDailyDiscount(createPriceData(50));

		final DayRateData dayRate2 = new DayRateData();
		dayRate2.setDateOfStay(TravelDateUtils.convertStringDateToDate("02/12/2016", TravelservicesConstants.DATE_PATTERN));
		dayRate2.setDailyActualRate(createPriceData(100));
		dayRate2.setDailyWasRate(createPriceData(150));
		dayRate2.setDailyDiscount(createPriceData(50));

		final DayRateData dayRate3 = new DayRateData();
		dayRate3.setDateOfStay(TravelDateUtils.convertStringDateToDate("03/12/2016", TravelservicesConstants.DATE_PATTERN));
		dayRate3.setDailyActualRate(createPriceData(100));
		dayRate3.setDailyWasRate(createPriceData(150));
		dayRate3.setDailyDiscount(createPriceData(50));

		return Arrays.asList(dayRate1, dayRate2, dayRate3);
	}

	protected static LocationData createLocation()
	{
		final LocationData locationData = new LocationData();
		locationData.setCode("PARK_PLAZA_HOTELS__AMP__RESORTS_PARK_PLAZA_SHERLOCK_HOLMES_LONDON");
		return locationData;
	}

	protected static List<ReservedRoomStayData> createRoomStays()
	{
		final ReservedRoomStayData reservedRoomStays = new ReservedRoomStayData();

		reservedRoomStays.setRoomStayRefNumber(Integer.valueOf(0));
		reservedRoomStays.setFromPrice(createPriceData(100));
		reservedRoomStays.setRoomTypes(createRoomTypesList());
		reservedRoomStays.setRatePlans(createRatePlansList());
		reservedRoomStays.setReservedGuests(createReservedGuestsList());
		reservedRoomStays.setServices(createServicesList());
		reservedRoomStays.setGuestCounts(createGuestCountsList());
		reservedRoomStays
				.setCheckInDate(TravelDateUtils.convertStringDateToDate("01/10/2016", TravelservicesConstants.DATE_PATTERN));
		reservedRoomStays
				.setCheckOutDate(TravelDateUtils.convertStringDateToDate("03/10/2016", TravelservicesConstants.DATE_PATTERN));

		return Arrays.asList(reservedRoomStays);
	}

	protected static List<RoomTypeData> createRoomTypesList()
	{
		final RoomTypeData roomTypeData = new RoomTypeData();

		roomTypeData.setCode("PARK_PLAZA_HOTELS__AMP__RESORTS_PARK_PLAZA_SHERLOCK_HOLMES_LONDONSUPERIOR_DOUBLE_ROOM");
		roomTypeData.setName("Superior Double Room");
		roomTypeData.setSizeMeasurement("35 m2 (376 sq. ft.)");
		roomTypeData.setOccupancies(createOccupanciesList());
		roomTypeData.setFacilities(createFacilitiesList());

		return Arrays.asList(roomTypeData);
	}

	protected static List<GuestOccupancyData> createOccupanciesList()
	{

		final GuestOccupancyData guestOccupancyAD = new GuestOccupancyData();
		guestOccupancyAD.setCode("ADULT_2");
		guestOccupancyAD.setQuantityMax(Integer.valueOf(2));
		guestOccupancyAD.setQuantityMin(Integer.valueOf(1));
		guestOccupancyAD.setPassengerType(createPassengerTypeData("adult"));

		final GuestOccupancyData guestOccupancyCH = new GuestOccupancyData();
		guestOccupancyCH.setCode("CHILD_1");
		guestOccupancyCH.setQuantityMax(Integer.valueOf(1));
		guestOccupancyCH.setQuantityMin(Integer.valueOf(0));
		guestOccupancyCH.setPassengerType(createPassengerTypeData("child"));

		return Arrays.asList(guestOccupancyAD, guestOccupancyCH);
	}

	protected static List<AccommodationFacilityData> createFacilitiesList()
	{
		final AccommodationFacilityData accommodationFacility1 = new AccommodationFacilityData();
		accommodationFacility1.setCode("AIR_CONDITIONING");
		accommodationFacility1.setShortDescription("Air Conditioning");

		final AccommodationFacilityData accommodationFacility2 = new AccommodationFacilityData();
		accommodationFacility2.setCode("BATHROOM");
		accommodationFacility2.setShortDescription("Bathroom");

		final AccommodationFacilityData accommodationFacility3 = new AccommodationFacilityData();
		accommodationFacility3.setCode("MINIBAR");
		accommodationFacility3.setShortDescription("Minibar");

		return Arrays.asList(accommodationFacility1, accommodationFacility2, accommodationFacility3);
	}

	protected static List<RatePlanData> createRatePlansList()
	{
		final RatePlanData ratePlanNR = new RatePlanData();
		ratePlanNR.setCode("4S_2P_NR_Plan");

		final RatePlanData ratePlanFC = new RatePlanData();
		ratePlanFC.setCode("4S_2P_FC_Plan");

		createRoomRateList(ratePlanNR);

		return Arrays.asList(ratePlanNR, ratePlanFC);
	}

	private static void createRoomRateList(final RatePlanData ratePlanNR)
	{
		final Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(2017, 3, 14);
		final Date startDate = startCalendar.getTime();

		final Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(2017, 3, 19);
		final Date endDate = endCalendar.getTime();

		Date currentDate = startDate;
		final List<RoomRateData> roomRates = new ArrayList<RoomRateData>();

		while (currentDate.compareTo(endDate) <= 0)
		{
			final RoomRateData roomRate = new RoomRateData();
			final RateData rate = new RateData();
			final PriceData actualRate = createPriceData(50.0);
			rate.setActualRate(actualRate);

			final List<TaxData> taxes = new ArrayList<TaxData>();
			rate.setTaxes(taxes);

			final PriceData totalDiscount = createPriceData(10.0);
			rate.setTotalDiscount(totalDiscount);

			final PriceData wasRate = createPriceData(60.0);
			rate.setWasRate(wasRate);

			final StayDateRangeData stayDateRange = new StayDateRangeData();
			stayDateRange.setStartTime(currentDate);
			roomRate.setStayDateRange(stayDateRange);

			roomRate.setRate(rate);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentDate);
			calendar.add(Calendar.DATE, 1);
			currentDate = calendar.getTime();

			roomRates.add(roomRate);
		}
		ratePlanNR.setRoomRates(roomRates);
	}

	protected static List<GuestData> createReservedGuestsList()
	{
		final GuestData guestData = new GuestData();
		guestData.setPrimaryIndicator(Boolean.TRUE);
		guestData.setProfile(createProfile());
		return Arrays.asList(guestData);
	}

	protected static List<ServiceData> createServicesList()
	{
		//Wi-Fi £10
		final ServiceData service1 = new ServiceData();
		service1.setCode("WIFI");
		service1.setRatePlanCode("4S_2P_NR_Plan");
		service1.setQuantity(Integer.valueOf(2));
		service1.setInclusive(Boolean.FALSE);
		service1.setServiceDetails(createServiceDetails("WIFI", "Wi-Fi"));
		service1.setPrice(createServiceRate(10));

		//Gym Access £15
		final ServiceData service2 = new ServiceData();
		service2.setCode("GYM_ACCESS");
		service2.setRatePlanCode("4S_2P_NR_Plan");
		service2.setQuantity(Integer.valueOf(1));
		service2.setInclusive(Boolean.FALSE);
		service2.setServiceDetails(createServiceDetails("GYM_ACCESS", "Gym Access"));
		service2.setPrice(createServiceRate(15));

		//English Breakfast £12
		final ServiceData service3 = new ServiceData();
		service3.setCode("ENGLISH_BREAKFAST");
		service3.setRatePlanCode("4S_2P_NR_Plan");
		service3.setQuantity(Integer.valueOf(1));
		service3.setInclusive(Boolean.FALSE);
		service3.setServiceDetails(createServiceDetails("ENGLISH_BREAKFAST", "English Breakfast"));
		service3.setPrice(createServiceRate(12));

		return Arrays.asList(service1, service2, service3);
	}

	protected static ServiceRateData createServiceRate(final int value)
	{
		final ServiceRateData serviceRate = new ServiceRateData();
		serviceRate.setRate(createRateData(value));
		serviceRate.setTimeSpan(createTimeSpan());
		serviceRate.setTotal(createPriceData(value));
		return serviceRate;
	}

	protected static RateData createRateData(final int value)
	{
		final RateData rate = new RateData();
		rate.setActualRate(createPriceData(value));
		return rate;
	}

	protected static TimeSpanData createTimeSpan()
	{
		final TimeSpanData timeSpan = new TimeSpanData();
		timeSpan.setStartDate(TravelDateUtils.convertStringDateToDate("01/12/2016", TravelservicesConstants.DATE_PATTERN));
		timeSpan.setEndDate(TravelDateUtils.convertStringDateToDate("03/12/2016", TravelservicesConstants.DATE_PATTERN));
		return timeSpan;
	}

	protected static ServiceDetailData createServiceDetails(final String code, final String name)
	{
		final ProductData product = new ProductData();
		product.setCode(code);
		product.setName(name);

		final ServiceDetailData serviceDetail = new ServiceDetailData();
		serviceDetail.setProduct(product);
		serviceDetail.setGuestCounts(createGuestCountsList());

		return serviceDetail;
	}

	protected static List<PassengerTypeQuantityData> createGuestCountsList()
	{
		final PassengerTypeQuantityData passengerTypeQuantity = new PassengerTypeQuantityData();
		passengerTypeQuantity.setPassengerType(createPassengerTypeData("adult"));
		passengerTypeQuantity.setQuantity(2);
		return Arrays.asList(passengerTypeQuantity);
	}

	protected static ProfileData createProfile()
	{
		final ProfileData profileData = new ProfileData();

		final TitleData titleData = new TitleData();
		titleData.setCode("mr");
		titleData.setName("Mr.");

		profileData.setFirstName("FirstName");
		profileData.setLastName("LastName");
		profileData.setContactNumber("0123456789");

		return profileData;
	}

	protected static PassengerTypeData createPassengerTypeData(final String passengerTypeCode)
	{
		final PassengerTypeData passengerType = new PassengerTypeData();

		if (passengerTypeCode.equals("adult"))
		{
			passengerType.setCode("adult");
			passengerType.setName("Adult");
			passengerType.setMinAge(Integer.valueOf(16));
		}
		if (passengerTypeCode.equals("child"))
		{
			passengerType.setCode("child");
			passengerType.setName("Child");
			passengerType.setMaxAge(Integer.valueOf(16));
			passengerType.setMinAge(Integer.valueOf(0));
		}

		return passengerType;
	}

	protected static PriceData createPriceData(final double value)
	{
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso("GBP");
		priceData.setValue(BigDecimal.valueOf(value));
		priceData.setFormattedValue(value + "£");
		priceData.setPriceType(PriceDataType.BUY);
		return priceData;
	}

}
