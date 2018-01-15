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

package de.hybris.platform.travelacceleratorstorefront.strategies.asm;

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelLocationFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationSuggestionFacade;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.enums.TripType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Redirect strategy for transport + accommodation journey responsible for building the required url parameters for transportation
 * and redirect to the fare selection page and for building the required url parameters for accommodation and set it in the
 * session.
 */
public class ASRedirectForTransportAccommodationStrategy extends AbstractASRedirectStrategy
		implements AssistedServiceRedirectByJourneyTypeStrategy
{
	private static final String FARE_SELECTION_ROOT_URL = "/fare-selection";
	private static final String DEFAULT_CART_REDIRECT = "/";
	private static final String DEFAULT_NUMBER_OF_ROOM = "1";
	private static final String FIRST_ROOM_NUMBER = "0";

	private CabinClassFacade cabinClassFacade;
	private TravellerFacade travellerFacade;
	private TransportFacilityFacade transportFacilityFacade;
	private TravelLocationFacade travelLocationFacade;
	private AccommodationSuggestionFacade accommodationSuggestionFacade;

	@Override
	public String getRedirectPath(final CartModel cartModel)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION);

		final Map<String, String> urlParameters = getUrlParametersForTransportation(cartModel);
		if(MapUtils.isEmpty(urlParameters))
		{
			return DEFAULT_CART_REDIRECT;
		}

		final String accommodationQueryString = getUrlParametersForAccommodation(cartModel, urlParameters);
		getSessionService()
				.setAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_QUERY_STRING, accommodationQueryString);

		final String urlParametersString = "?" + urlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "");
		return StringUtils.isBlank(urlParametersString) ? DEFAULT_CART_REDIRECT : FARE_SELECTION_ROOT_URL + urlParametersString;
	}

	/**
	 * Returns the url parameters map with the attributes for the transport part.
	 *
	 * @param cartModel
	 * 		as the cart model
	 */
	protected Map<String, String> getUrlParametersForTransportation(final CartModel cartModel)
	{
		final Map<String, String> urlParameters = new HashMap<>();

		final List<AbstractOrderEntryModel> transportationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& (entry.getProduct() instanceof FareProductModel
								|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())))
				.collect(Collectors.toList());

		final List<AbstractOrderEntryModel> accommodationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(transportationEntries) || Objects.isNull(cartModel.getTripType()) ||
				(Objects.nonNull(cartModel.getTripType()) && TripType.SINGLE.toString().equals(cartModel.getTripType().getCode())
						&& CollectionUtils.isEmpty(accommodationEntries)))
		{
			return null;
		}

		final long legCount = transportationEntries.stream()
				.map(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()).distinct().count();
		if (legCount < 2 && TripType.RETURN.toString().equals(cartModel.getTripType().getCode()))
		{
			return null;
		}

		// ORIGIN
		final List<TransportFacilityModel> originList = transportationEntries.stream()
				.map(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0 ?
						entry.getTravelOrderEntryInfo().getTravelRoute().getOrigin() :
						entry.getTravelOrderEntryInfo().getTravelRoute().getDestination()).distinct().collect(Collectors.toList());
		if (CollectionUtils.size(originList) > 1)
		{
			final LocationModel originLocation = originList.get(0).getLocation();
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION, originLocation.getCode());
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION_NAME,
					getLocationName(originLocation.getCode(), LocationType.CITY.getCode()));
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION_SUGGESTION_TYPE, LocationType.CITY.getCode());
		}
		else
		{
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION, originList.get(0).getCode());
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION_NAME,
					getLocationName(originList.get(0).getCode(), LocationType.AIRPORTGROUP.getCode()));
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION_SUGGESTION_TYPE,
					LocationType.AIRPORTGROUP.getCode());
		}

		// DESTINATION
		final List<TransportFacilityModel> destinationList = transportationEntries.stream()
				.map(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0 ?
						entry.getTravelOrderEntryInfo().getTravelRoute().getDestination() :
						entry.getTravelOrderEntryInfo().getTravelRoute().getOrigin()).distinct().collect(Collectors.toList());

		if (CollectionUtils.size(destinationList) > 1)
		{
			final LocationModel destinationLocation = destinationList.get(0).getLocation();
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION, destinationLocation.getCode());
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_NAME,
					getLocationName(destinationLocation.getCode(), LocationType.CITY.getCode()));
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_SUGGESTION_TYPE, LocationType.CITY.getCode());
		}
		else
		{
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION, destinationList.get(0).getCode());
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_NAME,
					getLocationName(destinationList.get(0).getCode(), LocationType.AIRPORTGROUP.getCode()));
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_SUGGESTION_TYPE,
					LocationType.AIRPORTGROUP.getCode());
		}

		final Optional<AbstractOrderEntryModel> firstLegEntry = transportationEntries.stream()
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0).findFirst();

		final TransportOfferingModel firstTransportOffering = firstLegEntry.get().getTravelOrderEntryInfo().getTransportOfferings()
				.stream().sorted(Comparator.comparing(this::getUTCDepartureTime)).findFirst().get();
		urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTING_DATE_TIME, TravelDateUtils
				.convertDateToStringDate(firstTransportOffering.getDepartureTime(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));

		if (legCount > 1)
		{
			urlParameters.put(TravelacceleratorstorefrontWebConstants.TRIP_TYPE, TripType.RETURN.toString());

			// Return Date Time
			final TravelOrderEntryInfoModel returnOrderEntryInfo = transportationEntries.stream()
					.map(AbstractOrderEntryModel::getTravelOrderEntryInfo)
					.filter(entryInfo -> entryInfo.getOriginDestinationRefNumber() == 1).findFirst().get();

			final TransportOfferingModel firstReturnTransportOffering = returnOrderEntryInfo.getTransportOfferings().stream()
					.sorted(Comparator.comparing(this::getUTCDepartureTime)).findFirst().get();
			urlParameters.put(TravelacceleratorstorefrontWebConstants.RETURN_DATE_TIME, TravelDateUtils
					.convertDateToStringDate(firstReturnTransportOffering.getDepartureTime(),
							TravelacceleratorstorefrontWebConstants.DATE_FORMAT));
		}
		else
		{
			urlParameters.put(TravelacceleratorstorefrontWebConstants.TRIP_TYPE, TripType.SINGLE.toString());
		}

		final Optional<CabinClassData> cabinClassOptional = transportationEntries.stream()
				.map(entry -> entry.getBundleTemplate().getParentTemplate().getId()).distinct()
				.map(bundleTemplateId -> getCabinClassFacade().findCabinClassFromBundleTemplate(bundleTemplateId))
				.sorted(Comparator.comparing(CabinClassData::getIndex)).findFirst();

		cabinClassOptional.ifPresent(
				cabinClassData -> urlParameters.put(TravelacceleratorstorefrontWebConstants.CABIN_CLASS, cabinClassData.getCode()));

		// Passenger Type Quantities
		final List<TravellerData> travellers = getTravellerFacade().getTravellersForCartEntries();
		final Map<String, Long> travellersMap = travellers.stream()
				.filter(traveller -> TravellerType.PASSENGER.getCode().equals(traveller.getTravellerType())).collect(Collectors
						.groupingBy(traveller -> ((PassengerInformationData) traveller.getTravellerInfo()).getPassengerType()
								.getCode(), Collectors.counting()));
		travellersMap.forEach((key, value) -> urlParameters.put(key, String.valueOf(value)));

		return urlParameters;
	}

	/**
	 * Returns the url parameters map with the attributes for the accommodation part.
	 *
	 * @param cartModel
	 * 		as the cart model
	 * @param transportUrlParameters
	 * 		as the map with the url parameters for the transport part
	 */
	protected String getUrlParametersForAccommodation(final CartModel cartModel, final Map<String, String> transportUrlParameters)
	{
		final Map<String, String> accommodationUrlParameters = new HashMap<>();

		// destinationLocation && destinationLocationName && suggestionType
		LocationData location = null;
		final String arrivalLocationSuggestionType = transportUrlParameters
				.get(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_SUGGESTION_TYPE);
		final String arrivalLocation = transportUrlParameters.get(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION);
		if (StringUtils.isNotBlank(arrivalLocationSuggestionType))
		{
			if (StringUtils.equalsIgnoreCase(SuggestionType.AIRPORTGROUP.toString(), arrivalLocationSuggestionType))
			{
				location = getTransportFacilityFacade().getLocation(arrivalLocation);
			}
			else if (StringUtils.equalsIgnoreCase(SuggestionType.CITY.toString(), arrivalLocationSuggestionType))
			{
				location = getTravelLocationFacade().getLocation(arrivalLocation);
			}
		}

		final List<GlobalSuggestionData> suggestionResults = Objects.nonNull(location) ?
				getAccommodationSuggestionFacade().getLocationSuggestions(location.getName()) :
				new ArrayList<>();
		if (CollectionUtils.isNotEmpty(suggestionResults))
		{
			final GlobalSuggestionData firstValidResult = suggestionResults.stream().findFirst().get();
			accommodationUrlParameters
					.put(TravelacceleratorstorefrontWebConstants.SUGGESTION_TYPE, SuggestionType.LOCATION.toString());
			final String encodedDestinationLocation = firstValidResult.getCode().replaceAll("\\|", "%7C");
			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.DESTINATION_LOCATION, encodedDestinationLocation);
			final String encodedDestinationLocationName = firstValidResult.getName().replaceAll("\\|", "%7C")
					.replaceAll(", ", "%2C%20").replaceAll(" ", "%20");
			accommodationUrlParameters
					.put(TravelacceleratorstorefrontWebConstants.DESTINATION_LOCATION_NAME, encodedDestinationLocationName);
		}

		final List<AbstractOrderEntryModel> accommodationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());

		final String guestOccupancyString;
		if (CollectionUtils.isNotEmpty(accommodationEntries))
		{
			// Populate urlParameters from cart
			final List<AccommodationOrderEntryGroupModel> accommodationEntryGroups = accommodationEntries.stream()
					.map(AbstractOrderEntryModel::getEntryGroup).filter(AccommodationOrderEntryGroupModel.class::isInstance)
					.map(AccommodationOrderEntryGroupModel.class::cast).distinct().collect(Collectors.toList());

			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKIN_DATE, TravelDateUtils
					.convertDateToStringDate(accommodationEntryGroups.get(0).getStartingDate(),
							TravelacceleratorstorefrontWebConstants.DATE_FORMAT));
			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKOUT_DATE, TravelDateUtils
					.convertDateToStringDate(accommodationEntryGroups.get(0).getEndingDate(),
							TravelacceleratorstorefrontWebConstants.DATE_FORMAT));

			final long numberOfRooms = accommodationEntryGroups.stream().map
					(AccommodationOrderEntryGroupModel::getRoomStayRefNumber)
					.distinct().count();
			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.NUMBER_OF_ROOMS, String.valueOf(numberOfRooms));
			guestOccupancyString = getGuestOccupancy(cartModel);
		}
		else
		{
			// Populate urlParameters from transport info
			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKIN_DATE,
					transportUrlParameters.get(TravelacceleratorstorefrontWebConstants.DEPARTING_DATE_TIME));
			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKOUT_DATE,
					transportUrlParameters.get(TravelacceleratorstorefrontWebConstants.RETURN_DATE_TIME));
			accommodationUrlParameters.put(TravelacceleratorstorefrontWebConstants.NUMBER_OF_ROOMS, DEFAULT_NUMBER_OF_ROOM);

			guestOccupancyString = getGuestOccupancyFromTransport();
		}

		return "?" + accommodationUrlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "") +
				guestOccupancyString;
	}

	/**
	 * Returns the string with the guest occupancies build from the cart.
	 *
	 * @param cartModel
	 * 		as the cart model
	 *
	 * @return the string with the guest occupancies
	 */
	protected String getGuestOccupancy(final CartModel cartModel)
	{
		final List<AbstractOrderEntryModel> accommodationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());
		final List<AccommodationOrderEntryGroupModel> accommodationEntryGroups = accommodationEntries.stream()
				.map(AbstractOrderEntryModel::getEntryGroup).filter(AccommodationOrderEntryGroupModel.class::isInstance)
				.map(AccommodationOrderEntryGroupModel.class::cast).distinct().collect(Collectors.toList());

		final List<TravellerData> travellers = getTravellerFacade().getTravellersForCartEntries();
		final Map<String, Long> travellersMap = travellers.stream()
				.filter(traveller -> TravellerType.PASSENGER.getCode().equals(traveller.getTravellerType())).collect(Collectors
						.groupingBy(traveller -> ((PassengerInformationData) traveller.getTravellerInfo()).getPassengerType()
								.getCode(), Collectors.counting()));

		final Map<AccommodationOrderEntryGroupModel, Map<String, Integer>> roomPassengerTypeQuantityMap =
				accommodationEntryGroups
						.stream().collect(Collectors.toMap(Function.identity(), entryGroup -> getPassengerTypeQuantityList()));

		roomPassengerTypeQuantityMap.forEach((accommodationOrderEntryGroup, passengerMap) ->
				accommodationOrderEntryGroup.getAccommodation().getGuestOccupancies().forEach(guestOccupancy ->
				{
					final long quantityInMap = travellersMap.get(guestOccupancy.getPassengerType().getCode()) != null ?
							travellersMap.get(guestOccupancy.getPassengerType().getCode()) :
							TravelfacadesConstants.DEFAULT_GUEST_QUANTITY;
					final long quantityMax = guestOccupancy.getQuantityMax();

					final long quantity = Math.min(quantityInMap, quantityMax);
					passengerMap.put(guestOccupancy.getPassengerType().getCode(), (int) quantity);
					travellersMap.put(guestOccupancy.getPassengerType().getCode(), quantityInMap - quantity);
				}));

		final Map<String, Long> remainingTravellersMap = travellersMap.entrySet().stream()
				.filter(entry -> entry.getValue() > TravelfacadesConstants.DEFAULT_GUEST_QUANTITY)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (MapUtils.isNotEmpty(remainingTravellersMap))
		{
			remainingTravellersMap.forEach((passengerType, value) ->
			{
				final Map<String, Integer> firstRoomMap = roomPassengerTypeQuantityMap.entrySet().stream().findFirst().get()
						.getValue();
				final long newQuantity = firstRoomMap.get(passengerType) + value;
				firstRoomMap.put(passengerType, (int) newQuantity);
			});
		}

		final StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

		roomPassengerTypeQuantityMap.forEach((accommodationEntryGroup, passengerTypeQuantityMap) ->
		{
			queryStringBuilder.append(TravelacceleratorstorefrontWebConstants.ROOM_QUERY_STRING_INDICATOR)
					.append(accommodationEntryGroup.getRoomStayRefNumber());
			queryStringBuilder.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			passengerTypeQuantityMap.forEach((passengerType, quantity) ->
			{
				queryStringBuilder.append(quantity);
				queryStringBuilder.append(TravelacceleratorstorefrontWebConstants.HYPHEN);
				queryStringBuilder.append(passengerType);
				queryStringBuilder.append(TravelacceleratorstorefrontWebConstants.COMMA);
			});
			// remove the last comma
			queryStringBuilder.deleteCharAt(queryStringBuilder.length() - 1);
			queryStringBuilder.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
		});

		return queryStringBuilder.substring(0, queryStringBuilder.length() - 1);
	}

	/**
	 * Returns the string with the guest occupancies build from the transport travellers.
	 *
	 * @return the string with the guest occupancies
	 */
	protected String getGuestOccupancyFromTransport()
	{
		final List<TravellerData> travellers = getTravellerFacade().getTravellersForCartEntries();
		final Map<String, Long> travellersMap = travellers.stream()
				.filter(traveller -> TravellerType.PASSENGER.getCode().equals(traveller.getTravellerType())).collect(Collectors
						.groupingBy(traveller -> ((PassengerInformationData) traveller.getTravellerInfo()).getPassengerType()
								.getCode(), Collectors.counting()));

		final String guestOccupancyString = travellersMap.entrySet().stream()
				.map(entry -> entry.getValue() + TravelacceleratorstorefrontWebConstants.HYPHEN + entry.getKey())
				.collect(Collectors.joining(","));

		return TravelacceleratorstorefrontWebConstants.AMPERSAND
				+ TravelacceleratorstorefrontWebConstants.ROOM_QUERY_STRING_INDICATOR + FIRST_ROOM_NUMBER
				+ TravelacceleratorstorefrontWebConstants.EQUALS + guestOccupancyString;
	}

	/**
	 * @return the cabinClassFacade
	 */
	protected CabinClassFacade getCabinClassFacade()
	{
		return cabinClassFacade;
	}

	/**
	 * @param cabinClassFacade
	 * 		the cabinClassFacade to set
	 */
	@Required
	public void setCabinClassFacade(final CabinClassFacade cabinClassFacade)
	{
		this.cabinClassFacade = cabinClassFacade;
	}

	/**
	 * @return the travellerFacade
	 */
	protected TravellerFacade getTravellerFacade()
	{
		return travellerFacade;
	}

	/**
	 * @param travellerFacade
	 * 		the travellerFacade to set
	 */
	@Required
	public void setTravellerFacade(final TravellerFacade travellerFacade)
	{
		this.travellerFacade = travellerFacade;
	}

	/**
	 * @return the transportFacilityFacade
	 */
	protected TransportFacilityFacade getTransportFacilityFacade()
	{
		return transportFacilityFacade;
	}

	/**
	 * @param transportFacilityFacade
	 * 		the transportFacilityFacade to set
	 */
	@Required
	public void setTransportFacilityFacade(final TransportFacilityFacade transportFacilityFacade)
	{
		this.transportFacilityFacade = transportFacilityFacade;
	}

	/**
	 * @return the travelLocationFacade
	 */
	protected TravelLocationFacade getTravelLocationFacade()
	{
		return travelLocationFacade;
	}

	/**
	 * @param travelLocationFacade
	 * 		the travelLocationFacade to set
	 */
	@Required
	public void setTravelLocationFacade(final TravelLocationFacade travelLocationFacade)
	{
		this.travelLocationFacade = travelLocationFacade;
	}

	/**
	 * @return the accommodationSuggestionFacade
	 */
	protected AccommodationSuggestionFacade getAccommodationSuggestionFacade()
	{
		return accommodationSuggestionFacade;
	}

	/**
	 * @param accommodationSuggestionFacade
	 * 		the accommodationSuggestionFacade to set
	 */
	@Required
	public void setAccommodationSuggestionFacade(
			final AccommodationSuggestionFacade accommodationSuggestionFacade)
	{
		this.accommodationSuggestionFacade = accommodationSuggestionFacade;
	}
}
