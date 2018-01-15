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

import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.facades.packages.DealBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.packages.DealCartFacade;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Redirect strategy for package journey responsible for building the required url parameters and redirect to the package details
 * page.
 */
public class ASRedirectForBookingPackageStrategy extends AbstractASRedirectStrategy
		implements AssistedServiceRedirectByJourneyTypeStrategy
{
	private static final String DEFAULT_CART_REDIRECT = "/";
	private static final String PACKAGE_DETAILS_ROOT_URL = "/package-details";
	private static final String DEAL_DETAILS_ROOT_URL = "/deal-details";

	private DealCartFacade dealCartFacade;
	private DealBundleTemplateFacade dealBundleTemplateFacade;
	private CabinClassFacade cabinClassFacade;
	private TravellerFacade travellerFacade;

	@Override
	public String getRedirectPath(final CartModel cartModel)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_PACKAGE);

		final String urlParameters;
		final String redirectUrl;
		if (getDealCartFacade().isDealInCart())
		{
			urlParameters = buildUrlParametersForDeal(cartModel);
			redirectUrl = DEAL_DETAILS_ROOT_URL;
		}
		else
		{
			urlParameters = buildUrlParametersForPackage(cartModel);
			redirectUrl = PACKAGE_DETAILS_ROOT_URL;
		}

		return StringUtils.isBlank(urlParameters) ? DEFAULT_CART_REDIRECT : redirectUrl + urlParameters;
	}

	/**
	 * Returns the string with the url parameters required for the redirect to the deal-details page.
	 *
	 * @param cartModel
	 * 		as the cart model
	 *
	 * @return the string of the url parameters
	 */
	protected String buildUrlParametersForDeal(final CartModel cartModel)
	{
		final Map<String, String> urlParameters = new HashMap<>();

		urlParameters.put(TravelacceleratorstorefrontWebConstants.DEAL_BUNDLE_TEMPLATE_ID,
				getDealBundleTemplateFacade().getDealBundleTemplateIdFromAbstractOrder(cartModel));

		final Optional<AbstractOrderEntryModel> firstLegEntry = cartModel.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0).findFirst();
		final TransportOfferingModel firstTransportOffering = firstLegEntry.get().getTravelOrderEntryInfo()
				.getTransportOfferings()
				.stream().sorted(Comparator.comparing(this::getUTCDepartureTime)).findFirst().get();
		urlParameters.put(TravelacceleratorstorefrontWebConstants.DEAL_SELECTED_DEPARTURE_DATE, TravelDateUtils
				.convertDateToStringDate(firstTransportOffering.getDepartureTime(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));

		return "?" + urlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "");
	}

	/**
	 * Returns the string with the url parameters required for the redirect to the package-details page.
	 *
	 * @param cartModel
	 * 		as the cart model
	 *
	 * @return the string of the url parameters
	 */
	protected String buildUrlParametersForPackage(final CartModel cartModel)
	{
		final List<AbstractOrderEntryModel> transportationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& (entry.getProduct() instanceof FareProductModel
								|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())))
				.collect(Collectors.toList());

		final List<AbstractOrderEntryModel> accommodationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(transportationEntries) || CollectionUtils.isEmpty(accommodationEntries))
		{
			return null;
		}

		final Map<String, String> urlParameters = new HashMap<>();
		populateUrlParametersForAccommodation(accommodationEntries, urlParameters);
		populateUrlParametersForTransport(transportationEntries, urlParameters);
		populatePartHotelStay(urlParameters);

		String redirectUrl = "/" + urlParameters.get(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE);
		urlParameters.remove(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE);
		redirectUrl = redirectUrl + "?" + urlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "");

		redirectUrl = redirectUrl + getGuestOccupancy(transportationEntries, accommodationEntries);
		return redirectUrl;
	}

	/**
	 * Populates the url parameters map with the attributes for the transport part.
	 *
	 * @param transportationEntries
	 * 		as the list of abstract order entry models of type transport
	 * @param urlParameters
	 * 		as the url parameters map
	 */
	protected void populateUrlParametersForTransport(final List<AbstractOrderEntryModel> transportationEntries,
			final Map<String, String> urlParameters)
	{
		// ORIGIN
		final List<TransportFacilityModel> originList = transportationEntries.stream()
				.map(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0 ?
						entry.getTravelOrderEntryInfo().getTravelRoute().getOrigin() :
						entry.getTravelOrderEntryInfo().getTravelRoute().getDestination()).distinct().collect(Collectors.toList());
		if (CollectionUtils.size(originList) > 1)
		{
			final LocationModel originLocation = originList.get(0).getLocation();
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION, originLocation.getCode());
			urlParameters
					.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION_SUGGESTION_TYPE, LocationType.CITY.getCode());
		}
		else
		{
			urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTURE_LOCATION, originList.get(0).getCode());
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
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_SUGGESTION_TYPE, LocationType.CITY.getCode
					());
		}
		else
		{
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION, destinationList.get(0).getCode());
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ARRIVAL_LOCATION_SUGGESTION_TYPE,
					LocationType.AIRPORTGROUP.getCode());
		}

		final Optional<AbstractOrderEntryModel> firstLegEntry = transportationEntries.stream()
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0).findFirst();

		final TransportOfferingModel firstTransportOffering = firstLegEntry.get().getTravelOrderEntryInfo()
				.getTransportOfferings()
				.stream().sorted(Comparator.comparing(this::getUTCDepartureTime)).findFirst().get();
		urlParameters.put(TravelacceleratorstorefrontWebConstants.DEPARTING_DATE_TIME, TravelDateUtils
				.convertDateToStringDate(firstTransportOffering.getDepartureTime(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));

		final long legCount = transportationEntries.stream()
				.map(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber())
				.distinct().count();
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

		cabinClassOptional.ifPresent(cabinClassData ->
				urlParameters.put(TravelacceleratorstorefrontWebConstants.CABIN_CLASS, cabinClassData.getCode()));
	}

	/**
	 * Populates the url parameters map with the attributes for the accommodation part.
	 *
	 * @param accommodationEntries
	 * 		as the list of abstract order entry models of type accommodation
	 * @param urlParameters
	 * 		as the url parameters map
	 */
	protected void populateUrlParametersForAccommodation(final List<AbstractOrderEntryModel> accommodationEntries,
			final Map<String, String> urlParameters)
	{
		final List<AccommodationOrderEntryGroupModel> accommodationEntryGroups = accommodationEntries.stream()
				.map(AbstractOrderEntryModel::getEntryGroup).filter(AccommodationOrderEntryGroupModel.class::isInstance)
				.map(AccommodationOrderEntryGroupModel.class::cast).distinct().collect(Collectors.toList());

		final String accommodationOfferingCode = accommodationEntryGroups.get(0).getAccommodationOffering().getCode();
		urlParameters.put(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE, accommodationOfferingCode);

		urlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKIN_DATE, TravelDateUtils
				.convertDateToStringDate(accommodationEntryGroups.get(0).getStartingDate(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));
		urlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKOUT_DATE, TravelDateUtils
				.convertDateToStringDate(accommodationEntryGroups.get(0).getEndingDate(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));
		final long numberOfRooms = accommodationEntryGroups.stream().map(AccommodationOrderEntryGroupModel::getRoomStayRefNumber)
				.distinct().count();
		urlParameters.put(TravelacceleratorstorefrontWebConstants.NUMBER_OF_ROOMS, String.valueOf(numberOfRooms));
	}

	/**
	 * Returns the string with the guest occupancies.
	 *
	 * @param transportationEntries
	 * 		as the list of abstract order entry models of type transport
	 * @param accommodationEntries
	 * 		as the list of abstract order entry models of type accommodation
	 *
	 * @return the string with the guest occupancies
	 */
	protected String getGuestOccupancy(final List<AbstractOrderEntryModel> transportationEntries,
			final List<AbstractOrderEntryModel> accommodationEntries)
	{
		final List<AccommodationOrderEntryGroupModel> accommodationEntryGroups = accommodationEntries.stream()
				.map(AbstractOrderEntryModel::getEntryGroup).filter(AccommodationOrderEntryGroupModel.class::isInstance)
				.map(AccommodationOrderEntryGroupModel.class::cast).distinct().collect(Collectors.toList());

		final List<TravellerData> travellers = getTravellerFacade().getTravellersForCartEntries();
		final Map<String, Long> travellersMap = travellers.stream()
				.filter(traveller -> TravellerType.PASSENGER.getCode().equals(traveller.getTravellerType())).collect(Collectors
						.groupingBy(traveller -> ((PassengerInformationData) traveller.getTravellerInfo()).getPassengerType()
										.getCode(),	Collectors.counting()));

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
	 * Populates the partHotelStay attribute in the urlParameters map
	 *
	 * @param urlParameters
	 * 		as the map of the url parameters.
	 */
	protected void populatePartHotelStay(final Map<String, String> urlParameters)
	{
		final Date checkInDate = TravelDateUtils
				.convertStringDateToDate(urlParameters.get(TravelacceleratorstorefrontWebConstants.CHECKIN_DATE),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT);
		final Date checkOutDate = TravelDateUtils
				.convertStringDateToDate(urlParameters.get(TravelacceleratorstorefrontWebConstants.CHECKOUT_DATE),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT);

		final Date departingDate = TravelDateUtils
				.convertStringDateToDate(urlParameters.get(TravelacceleratorstorefrontWebConstants.DEPARTING_DATE_TIME),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT);

		boolean partHotelStay = true;
		final String returnDateString = urlParameters.get(TravelacceleratorstorefrontWebConstants.RETURN_DATE_TIME);
		if (StringUtils.isNotBlank(returnDateString))
		{
			final Date returnDate = TravelDateUtils
					.convertStringDateToDate(returnDateString, TravelacceleratorstorefrontWebConstants.DATE_FORMAT);
			partHotelStay = !(TravelDateUtils.isSameDate(checkInDate, departingDate) && TravelDateUtils
					.isSameDate(checkOutDate, returnDate));
		}

		urlParameters.put(TravelacceleratorstorefrontWebConstants.PART_HOTEL_STAY, String.valueOf(partHotelStay));
	}

	/**
	 * @return the dealCartFacade
	 */
	protected DealCartFacade getDealCartFacade()
	{
		return dealCartFacade;
	}

	/**
	 * @param dealCartFacade
	 * 		the dealCartFacade to set
	 */
	@Required
	public void setDealCartFacade(final DealCartFacade dealCartFacade)
	{
		this.dealCartFacade = dealCartFacade;
	}

	/**
	 * @return the dealBundleTemplateFacade
	 */
	protected DealBundleTemplateFacade getDealBundleTemplateFacade()
	{
		return dealBundleTemplateFacade;
	}

	/**
	 * @param dealBundleTemplateFacade
	 * 		the dealBundleTemplateFacade to set
	 */
	@Required
	public void setDealBundleTemplateFacade(
			final DealBundleTemplateFacade dealBundleTemplateFacade)
	{
		this.dealBundleTemplateFacade = dealBundleTemplateFacade;
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
}
