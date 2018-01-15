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
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract redirect strategy that exposes the common methods for the ASM redirect strategies.
 */
public abstract class AbstractASRedirectStrategy
{
	private TravellerSortStrategy travellerSortStrategy;
	private PassengerTypeFacade passengerTypeFacade;
	private TransportOfferingFacade transportOfferingFacade;
	private SessionService sessionService;

	/**
	 * Returns the map of the passenger type quantities.
	 *
	 * @return a map with key corresponding to the passenger type and value correponding to its quantity
	 */
	protected Map<String, Integer> getPassengerTypeQuantityList()
	{
		final List<PassengerTypeData> sortedPassengerTypes = getTravellerSortStrategy()
				.sortPassengerTypes(getPassengerTypeFacade().getPassengerTypes());

		return sortedPassengerTypes.stream().collect(
				Collectors.toMap(PassengerTypeData::getCode, passengerTypeData -> TravelfacadesConstants.DEFAULT_GUEST_QUANTITY));
	}

	/**
	 * Gets the location name.
	 *
	 * @param locationCode
	 * 		the location code
	 * @param locationType
	 * 		the location type
	 *
	 * @return the location name
	 */
	protected String getLocationName(final String locationCode, final String locationType)
	{
		String locationName = StringUtils.EMPTY;
		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> suggestionResults = getTransportOfferingFacade()
				.getOriginSuggestionData(locationCode);

		GlobalSuggestionData firstSuggestion = null;
		if (org.apache.commons.collections.MapUtils.isNotEmpty(suggestionResults))
		{
			firstSuggestion = suggestionResults.keySet().iterator().next();
		}
		if (StringUtils.isNotBlank(locationType) && Objects.nonNull(firstSuggestion))
		{
			if (StringUtils.equalsIgnoreCase(SuggestionType.AIRPORTGROUP.toString(), locationType))
			{
				locationName = suggestionResults.get(firstSuggestion).stream().findFirst().get().getName();
			}
			else if (StringUtils.equalsIgnoreCase(SuggestionType.CITY.toString(), locationType))
			{
				locationName = firstSuggestion.getName();
			}
		}
		return locationName.replaceAll("\\|", "%7C").replaceAll(", ", "%2C%20").replaceAll(" ", "%20");
	}

	/**
	 * Return the UTC departure time for the given transportOffering
	 *
	 * @param transportOfferingModel
	 * 		as the tranportOffering
	 *
	 * @return the zoned date time
	 */
	protected ZonedDateTime getUTCDepartureTime(final TransportOfferingModel transportOfferingModel)
	{
		final String zoneId = transportOfferingModel.getTravelSector().getOrigin().getPointOfService().get(0).getTimeZoneId();
		return TravelDateUtils.getUtcZonedDateTime(transportOfferingModel.getDepartureTime(), ZoneId.of(zoneId));
	}

	/**
	 * @return the travellerSortStrategy
	 */
	protected TravellerSortStrategy getTravellerSortStrategy()
	{
		return travellerSortStrategy;
	}

	/**
	 * @param travellerSortStrategy
	 * 		the travellerSortStrategy to set
	 */
	@Required
	public void setTravellerSortStrategy(final TravellerSortStrategy travellerSortStrategy)
	{
		this.travellerSortStrategy = travellerSortStrategy;
	}

	/**
	 * @return the passengerTypeFacade
	 */
	protected PassengerTypeFacade getPassengerTypeFacade()
	{
		return passengerTypeFacade;
	}

	/**
	 * @param passengerTypeFacade
	 * 		the passengerTypeFacade to set
	 */
	@Required
	public void setPassengerTypeFacade(final PassengerTypeFacade passengerTypeFacade)
	{
		this.passengerTypeFacade = passengerTypeFacade;
	}

	/**
	 * @return the transportOfferingFacade
	 */
	protected TransportOfferingFacade getTransportOfferingFacade()
	{
		return transportOfferingFacade;
	}

	/**
	 * @param transportOfferingFacade
	 * 		the transportOfferingFacade to set
	 */
	@Required
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}

	/**
	 * @return the sessionService
	 */
	protected  SessionService getSessionService()
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
}
