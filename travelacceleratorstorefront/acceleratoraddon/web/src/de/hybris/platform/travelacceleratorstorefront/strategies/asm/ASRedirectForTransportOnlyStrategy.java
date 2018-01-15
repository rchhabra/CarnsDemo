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
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Redirect strategy for transport only journey responsible for building the required url parameters and redirect to the fare
 * selection page.
 */
public class ASRedirectForTransportOnlyStrategy extends AbstractASRedirectStrategy
		implements AssistedServiceRedirectByJourneyTypeStrategy
{
	private static final String FARE_SELECTION_ROOT_URL = "/fare-selection";
	private static final String DEFAULT_CART_REDIRECT = "/";

	private CabinClassFacade cabinClassFacade;
	private TravellerFacade travellerFacade;

	@Override
	public String getRedirectPath(final CartModel cartModel)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);

		final String urlParameters = buildUrlParameters(cartModel);
		return StringUtils.isBlank(urlParameters) ? DEFAULT_CART_REDIRECT : FARE_SELECTION_ROOT_URL + urlParameters;
	}

	/**
	 * Returns the string with the url parameters required for the redirect to the fare-selection page.
	 *
	 * @param cartModel
	 * 		as the cart model
	 *
	 * @return the string of the url parameters
	 */
	protected String buildUrlParameters(final CartModel cartModel)
	{
		final Map<String, String> urlParameters = new HashMap<>();

		final List<AbstractOrderEntryModel> transportationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& (entry.getProduct() instanceof FareProductModel
								|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(transportationEntries) || Objects.isNull(cartModel.getTripType()))
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
										.getCode(),
								Collectors.counting()));
		travellersMap.forEach((key, value) -> urlParameters.put(key, String.valueOf(value)));

		return "?" + urlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "");
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
