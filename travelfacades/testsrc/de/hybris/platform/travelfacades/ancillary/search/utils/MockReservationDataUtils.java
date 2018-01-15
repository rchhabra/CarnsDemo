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

package de.hybris.platform.travelfacades.ancillary.search.utils;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.travelservices.enums.TravellerType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;


/**
 * Creates a mock ReservationData object needed for testing
 */
public class MockReservationDataUtils
{
	private static final Logger LOG = Logger.getLogger(MockReservationDataUtils.class);

	private static final String SMITH = "Smith";
	private static final String LHR_DXB_FLIGHT = "EZY1234010120161235";
	private static final String DXB_LHR_FLIGHT = "EZY5678100120161935";

	/**
	 * Creates a ReservationData mock object for single journey
	 *
	 * @param bookingReference
	 * @return ReservationData for single journey
	 */
	public ReservationData createResDataForSingleSectorReturnJouney(final String bookingReference)
	{
		ReservationData reservationData = null;
		try
		{
			final ReservationItemData reservationItemOutbound = createReservationItemOubound();

			final ReservationItemData reservationItemInbound = createReservationItemInbound();

			final List<ReservationItemData> reservationItems = Stream.of(reservationItemOutbound, reservationItemInbound)
					.collect(Collectors.<ReservationItemData> toList());

			reservationData = createReservationData(reservationItems, bookingReference);
		}
		catch (final Exception e)
		{
			LOG.error("Error when creating a mock reservation data object", e);
		}

		return reservationData;
	}

	private ReservationItemData createReservationItemInbound() throws ParseException
	{
		final List<OriginDestinationOfferInfoData> odOfferInfoInbound = createOriginDestinationOffersInbound();
		final ItineraryPricingInfoData itineraryPricingInfo = createItineraryPricingInfoData("Economy");
		final ReservationPricingInfoData reservationPricingInfoInbound = createReservationPricingInfoData(odOfferInfoInbound,
				itineraryPricingInfo);
		final List<TravellerData> travellers = createTravellers();
		final ItineraryData itineraryData = createItineraryData("DXB_LHR",
				Stream.of(DXB_LHR_FLIGHT).collect(Collectors.<String> toList()), false, travellers);
		return createReservationItemData(reservationPricingInfoInbound, itineraryData);
	}

	private ReservationItemData createReservationItemOubound() throws ParseException
	{
		final List<OriginDestinationOfferInfoData> odOfferInfosOutbound = createOriginDestinationOffersOutbound();
		final ItineraryPricingInfoData itineraryPricingInfo = createItineraryPricingInfoData("Economy Plus");
		final ReservationPricingInfoData reservationPricingInfoOutbound = createReservationPricingInfoData(odOfferInfosOutbound,
				itineraryPricingInfo);
		final List<TravellerData> travellers = createTravellers();
		final ItineraryData itineraryData = createItineraryData("LHR_DXB",
				Stream.of(LHR_DXB_FLIGHT).collect(Collectors.<String> toList()), true, travellers);
		return createReservationItemData(reservationPricingInfoOutbound, itineraryData);
	}

	private List<TravellerData> createTravellers() throws ParseException
	{
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		//create traveller Data John Smith
		final TravellerData travellerDataPax1 = createTravellerData("Pax1", "1111", "John", SMITH,
				simpleDateFormat.parse("01-01-1980"));
		final TravellerData travellerDataPax2 = createTravellerData("Pax2", "2222", "Jade", SMITH,
				simpleDateFormat.parse("27-07-1987"));
		final TravellerData travellerDataPax3 = createTravellerData("Pax3", "3333", "Daizy", "Brown",
				simpleDateFormat.parse("01-01-2009"));
		return Stream.of(travellerDataPax1, travellerDataPax2, travellerDataPax3).collect(Collectors.<TravellerData> toList());
	}

	private List<OriginDestinationOfferInfoData> createOriginDestinationOffersOutbound() throws ParseException
	{
		final OriginDestinationOfferInfoData odOfferInfoMeal = createOriginDestinationOfferInfoMeal(LHR_DXB_FLIGHT, true);
		final OriginDestinationOfferInfoData odOfferInfoHoldItems = createOriginDestinationOfferInfoHoldItem(LHR_DXB_FLIGHT, true);
		final OriginDestinationOfferInfoData odOfferInfoLoungeAccess = createOriginDestinationOfferInfoLoungeAccess(LHR_DXB_FLIGHT,
				true);
		return Stream.of(odOfferInfoMeal, odOfferInfoHoldItems, odOfferInfoLoungeAccess)
				.collect(Collectors.<OriginDestinationOfferInfoData> toList());
	}

	private List<OriginDestinationOfferInfoData> createOriginDestinationOffersInbound() throws ParseException
	{
		final OriginDestinationOfferInfoData odOfferInfoMeal = createOriginDestinationOfferInfoMeal(DXB_LHR_FLIGHT, false);
		final OriginDestinationOfferInfoData odOfferInfoHoldItems = createOriginDestinationOfferInfoHoldItem(DXB_LHR_FLIGHT, false);
		final OriginDestinationOfferInfoData odOfferInfoLoungeAccess = createOriginDestinationOfferInfoLoungeAccess(DXB_LHR_FLIGHT,
				false);
		return Stream.of(odOfferInfoMeal, odOfferInfoHoldItems, odOfferInfoLoungeAccess)
				.collect(Collectors.<OriginDestinationOfferInfoData> toList());
	}

	private OriginDestinationOfferInfoData createOriginDestinationOfferInfoMeal(final String toCode, final boolean isOutbound)
			throws ParseException
	{
		final List<TravellerBreakdownData> travellerBreakDownStdMeal = createTravellerBreakDownForOffer(1, 1, 0);
		final OfferPricingInfoData offerPricingInfoDataMeal1 = createOfferPricingInfoData("Meal1", "Standard Meal",
				travellerBreakDownStdMeal, 0);

		final List<TravellerBreakdownData> travellerBreakDownChildMeal = createTravellerBreakDownForOffer(0, 0, 1);
		final OfferPricingInfoData offerPricingInfoDataMeal2 = createOfferPricingInfoData("Meal2", "Child Meal",
				travellerBreakDownChildMeal, 0);

		final List<TransportOfferingData> transportOfferings = Stream.of(createTransportOfferingData(toCode, isOutbound))
				.collect(Collectors.<TransportOfferingData> toList());
		return createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoDataMeal1, offerPricingInfoDataMeal2).collect(Collectors.<OfferPricingInfoData> toList()),
				transportOfferings);
	}

	private OriginDestinationOfferInfoData createOriginDestinationOfferInfoHoldItem(final String toCode, final boolean isOutbound)
			throws ParseException
	{
		final List<TravellerBreakdownData> travellerBreakDownBag20kg = createTravellerBreakDownForOffer(1, 2, 1);
		final OfferPricingInfoData offerPricingInfoDataBag1 = createOfferPricingInfoData("HoldItem1", "Excess Baggage 20Kg",
				travellerBreakDownBag20kg, 0);

		final List<TravellerBreakdownData> travellerBreakDownBagSportGolf = createTravellerBreakDownForOffer(1, 0, 0);
		final OfferPricingInfoData offerPricingInfoDataBag2 = createOfferPricingInfoData("HoldItem2", "Sporting Equipment - Golf",
				travellerBreakDownBagSportGolf, 0);

		final List<TransportOfferingData> transportOfferings = Stream.of(createTransportOfferingData(toCode, isOutbound))
				.collect(Collectors.<TransportOfferingData> toList());

		return createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoDataBag1, offerPricingInfoDataBag2).collect(Collectors.<OfferPricingInfoData> toList()),
				transportOfferings);
	}

	private OriginDestinationOfferInfoData createOriginDestinationOfferInfoLoungeAccess(final String toCode,
			final boolean isOutbound) throws ParseException
	{
		final List<TravellerBreakdownData> travellerBreakDownLoungeAccess = createTravellerBreakDownForOffer(1, 1, 1);
		final OfferPricingInfoData offerPricingInfoDataLA1 = createOfferPricingInfoData("LAccess1", "Club Lounge",
				travellerBreakDownLoungeAccess, 0);

		final List<TransportOfferingData> transportOfferings = Stream.of(createTransportOfferingData(toCode, isOutbound))
				.collect(Collectors.<TransportOfferingData> toList());

		return createOriginDestinationOfferInfoData(
				Stream.of(offerPricingInfoDataLA1).collect(Collectors.<OfferPricingInfoData> toList()), transportOfferings);
	}

	private List<TravellerBreakdownData> createTravellerBreakDownForOffer(final int pax1Quantity, final int pax2Quantity,
			final int pax3Quantity) throws ParseException
	{
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

		final List<TravellerBreakdownData> travellerBreakDownBag20kg = new ArrayList<>();
		if (pax1Quantity > 0)
		{
			//create traveller Data John Smith
			final TravellerData travellerDataPax1 = createTravellerData("Pax1", "1111", "John", SMITH,
					simpleDateFormat.parse("01-01-1980"));
			final TravellerBreakdownData travellerBreakdownPax1 = createTravellerBreakdownData(travellerDataPax1, pax1Quantity);
			travellerBreakDownBag20kg.add(travellerBreakdownPax1);
		}

		if (pax2Quantity > 0)
		{
			//create traveller Data Jade Smith
			final TravellerData travellerDataPax2 = createTravellerData("Pax2", "2222", "Jade", SMITH,
					simpleDateFormat.parse("27-07-1987"));
			final TravellerBreakdownData travellerBreakdownPax2 = createTravellerBreakdownData(travellerDataPax2, pax2Quantity);
			travellerBreakDownBag20kg.add(travellerBreakdownPax2);
		}
		if (pax3Quantity > 0)
		{
			//create traveller data Darren Brown
			final TravellerData travellerDataPax3 = createTravellerData("Pax3", "3333", "Daizy", "Brown",
					simpleDateFormat.parse("01-01-2009"));
			final TravellerBreakdownData travellerBreakdownPax3 = createTravellerBreakdownData(travellerDataPax3, pax3Quantity);
			travellerBreakDownBag20kg.add(travellerBreakdownPax3);
		}
		return travellerBreakDownBag20kg;
	}

	private ReservationData createReservationData(final List<ReservationItemData> reservationItems, final String bookingReference)
	{
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(reservationItems);
		reservationData.setCode(bookingReference);
		return reservationData;
	}

	private ReservationItemData createReservationItemData(final ReservationPricingInfoData reservationPricingInfo,
			final ItineraryData itineraryData)
	{
		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setReservationPricingInfo(reservationPricingInfo);
		reservationItemData.setReservationItinerary(itineraryData);
		return reservationItemData;
	}

	private ReservationPricingInfoData createReservationPricingInfoData(
			final List<OriginDestinationOfferInfoData> originDestinationOfferInfos,
			final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final ReservationPricingInfoData resPricingInfoData = new ReservationPricingInfoData();
		resPricingInfoData.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		resPricingInfoData.setItineraryPricingInfo(itineraryPricingInfo);
		return resPricingInfoData;
	}

	private ItineraryPricingInfoData createItineraryPricingInfoData(final String bundleTypeName)
	{
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setBundleTypeName(bundleTypeName);
		return itineraryPricingInfo;
	}

	private OriginDestinationOfferInfoData createOriginDestinationOfferInfoData(final List<OfferPricingInfoData> offerPricingInfos,
			final List<TransportOfferingData> transportOfferings)
	{
		final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
		odOfferInfoData.setOfferPricingInfos(offerPricingInfos);
		odOfferInfoData.setTransportOfferings(transportOfferings);
		return odOfferInfoData;
	}

	private OfferPricingInfoData createOfferPricingInfoData(final String productCode, final String productName,
			final List<TravellerBreakdownData> travellerBreakDown, final Integer bundleIndicator)
	{
		final OfferPricingInfoData offerPriceInfo = new OfferPricingInfoData();
		offerPriceInfo.setTravellerBreakdowns(travellerBreakDown);
		offerPriceInfo.setBundleIndicator(bundleIndicator);
		final ProductData productData = new ProductData();
		productData.setCode(productCode);
		productData.setName(productName);
		offerPriceInfo.setProduct(productData);
		return offerPriceInfo;
	}

	private TravellerBreakdownData createTravellerBreakdownData(final TravellerData travellerData, final Integer quantity)
	{
		final TravellerBreakdownData travellerBreakdownData = new TravellerBreakdownData();
		travellerBreakdownData.setTraveller(travellerData);
		travellerBreakdownData.setQuantity(quantity);
		return travellerBreakdownData;
	}

	private TravellerData createTravellerData(final String code, final String uid, final String firstName, final String surname,
			final Date dob)
	{
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel(code);
		travellerData.setUid(uid);
		travellerData.setTravellerType(TravellerType.PASSENGER.toString());
		travellerData.setTravellerInfo(createPassengerInformationData(firstName, surname, dob));
		return travellerData;
	}

	private TravellerInfoData createPassengerInformationData(final String firstName, final String surname, final Date dob)
	{
		final PassengerInformationData paxInfoData = new PassengerInformationData();
		paxInfoData.setFirstName(firstName);
		paxInfoData.setSurname(surname);
		paxInfoData.setDateOfBirth(dob);
		return paxInfoData;
	}

	private TransportOfferingData createTransportOfferingData(final String code, final boolean isOutbound)
	{
		final TransportOfferingData toData = new TransportOfferingData();
		try
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

			toData.setCode(code);
			if (isOutbound)
			{
				toData.setNumber("EZY1234");
				toData.setDepartureTime(simpleDateFormat.parse("01-01-2016 12:35"));
				toData.setArrivalTime(simpleDateFormat.parse("01-01-2016 22:35"));
				toData.setStatus("ON TIME");
				toData.setOriginLocationCity("London");
				toData.setDestinationLocationCity("Dubai");
				toData.setOriginTerminal(createTerminalData("T1", "Terminal 1"));
				toData.setDestinationTerminal(createTerminalData("T3", "Terminal 3"));
				toData.setSector(createTravelSectorData("LHR", "DXB", "London Heathrow", "Dubai International Airport"));
			}
			else
			{
				toData.setNumber("EZY5678");
				toData.setDepartureTime(simpleDateFormat.parse("10-01-2016 19:35"));
				toData.setArrivalTime(simpleDateFormat.parse("11-01-2016 05:35"));
				toData.setStatus("ON TIME");
				toData.setOriginLocationCity("Dubai");
				toData.setDestinationLocationCity("London");
				toData.setOriginTerminal(createTerminalData("T2", "Terminal 2"));
				toData.setDestinationTerminal(createTerminalData("T4", "Terminal 4"));
				toData.setSector(createTravelSectorData("DXB", "LHR", "Dubai International Airport", "London Heathrow"));
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error when creating a mock reservation data object", e);
		}
		return toData;
	}

	private TerminalData createTerminalData(final String code, final String name)
	{
		final TerminalData terminalData = new TerminalData();
		terminalData.setCode(code);
		terminalData.setName(name);
		return terminalData;
	}

	private TravelSectorData createTravelSectorData(final String originCode, final String destinationCode, final String originName,
			final String destinationName)
	{
		final TravelSectorData travelSector = new TravelSectorData();
		travelSector.setOrigin(createTransportFacilityData(originCode, originName));
		travelSector.setDestination(createTransportFacilityData(destinationCode, destinationName));
		travelSector.setCode(originCode.concat("_").concat(destinationCode));
		return travelSector;
	}

	private TransportFacilityData createTransportFacilityData(final String code, final String name)
	{
		final TransportFacilityData transportFacilityData = new TransportFacilityData();
		transportFacilityData.setCode(code);
		transportFacilityData.setName(name);
		return transportFacilityData;
	}

	private ItineraryData createItineraryData(final String routeCode, final List<String> toCodes, final boolean isOutbound,
			final List<TravellerData> travellers)
	{
		final ItineraryData itineraryData = new ItineraryData();
		final TravelRouteData travelRouteData = new TravelRouteData();
		travelRouteData.setCode(routeCode);
		itineraryData.setRoute(travelRouteData);
		itineraryData.setOriginDestinationOptions(Stream.of(createOriginDestinationOptionData(toCodes, isOutbound))
				.collect(Collectors.<OriginDestinationOptionData> toList()));
		itineraryData.setTravellers(travellers);
		return itineraryData;
	}

	private OriginDestinationOptionData createOriginDestinationOptionData(final List<String> toCodes, final boolean isOutbound)
	{
		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		final List<TransportOfferingData> transportOfferings = toCodes.stream()
				.map(code -> createTransportOfferingData(code, isOutbound)).collect(Collectors.<TransportOfferingData> toList());
		odOptionData.setTransportOfferings(transportOfferings);
		return odOptionData;
	}

}
