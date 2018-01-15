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

package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.FlightType.Flight;
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcfacades.ndc.TotalJourneyType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.TravelRouteFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class to populate OriginDestination data for {@link BaggageListRQ} and {@link ServiceListRQ}
 */
public abstract class NDCAbstractOffersOriginDestinationPopulator
{

	private static final String TRANSPORT_OFFERING_STATUS_RESULT_MINUTES = "transport.offering.status.result.minutes";
	private static final String TRANSPORT_OFFERING_STATUS_RESULT_HOURS = "transport.offering.status.result.hours";

	private TravelRouteFacade travelRouteFacade;
	private TransportOfferingFacade transportOfferingFacade;
	private ConfigurationService configurationService;

	/**
	 * This method creates an instance of {@link HashMap} from {@link TotalJourneyType} having hrs and mins as key and
	 * value respectively.
	 *
	 * @param totalJourneyType
	 */
	protected Map<String, Integer> getDurationMap(final TotalJourneyType totalJourneyType)
	{
		final Map<String, Integer> durationMap = new HashMap<>();
		if (Objects.nonNull(totalJourneyType) && Objects.nonNull(totalJourneyType.getTime()))
		{
			durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_HOURS, totalJourneyType.getTime().getHours());
			durationMap.put(TRANSPORT_OFFERING_STATUS_RESULT_MINUTES, totalJourneyType.getTime().getMinutes());
		}
		return durationMap;
	}

	/**
	 * This method creates and returns {@link ArrayList} of {@link TransportOfferingData} from {@link ArrayList} of
	 * {@link Flight}
	 *
	 * @param flights
	 */
	protected List<TransportOfferingData> getTransportOfferingDatas(final List<Flight> flights)
	{
		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<>();
		flights.forEach(flight -> {
			try
			{
				final TransportOfferingData transportOfferingData = getTransportOfferingFacade()
						.getTransportOffering(flight.getSegmentKey());
				transportOfferingDatas.add(transportOfferingData);
			}
			catch (final ModelNotFoundException exception)
			{
				throw new ModelNotFoundException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.SERVICE_UNAVAILABLE), exception);
			}
		});
		return transportOfferingDatas;
	}

	/**
	 * This method creates an instance of {@link ArrayList} of {@link OriginDestinationOptionData} using {@link Flight}
	 *
	 * @param flights
	 * @param travelRouteData
	 */
	protected List<OriginDestinationOptionData> getOriginDestinationOptions(final List<Flight> flights,
			final TravelRouteData travelRouteData)
	{
		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>(1);
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();

		originDestinationOption.setTransportOfferings(getTransportOfferingDatas(flights));
		originDestinationOption.setTravelRouteCode(travelRouteData.getCode());
		originDestinationOption.setActive(true);
		originDestinationOptions.add(originDestinationOption);
		return originDestinationOptions;
	}

	/**
	 * This method creates and returns an instance of {@link ItineraryData}
	 *
	 * @param totalJourney
	 * @param originDestinationKey
	 * @param flights
	 */
	protected ItineraryData createItinerary(final TotalJourneyType totalJourney, final String originDestinationKey,
			final List<Flight> flights)
	{
		final ItineraryData itinerary = new ItineraryData();
		itinerary.setDuration(getDurationMap(totalJourney));
		try
		{
			final TravelRouteData travelRouteData = getTravelRouteFacade().getTravelRoute(originDestinationKey);
			itinerary.setRoute(travelRouteData);
			itinerary.setOriginDestinationOptions(getOriginDestinationOptions(flights, travelRouteData));
		}
		catch (final ModelNotFoundException exception)
		{
			if (!StringUtils.equals(exception.getMessage(),
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.SERVICE_UNAVAILABLE)))
			{
				throw new ModelNotFoundException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_ORIGIN_DESTINATION_KEY),
						exception);
			}
			throw exception;
		}
		return itinerary;
	}

	/**
	 * This method creates and returns an instance of {@link SelectedOffersData}
	 */
	protected SelectedOffersData getSelectedOffer()
	{
		final SelectedOffersData selectedOffersData = new SelectedOffersData();
		selectedOffersData.setOfferGroups(Collections.emptyList());
		return selectedOffersData;
	}

	protected TravelRouteFacade getTravelRouteFacade()
	{
		return travelRouteFacade;
	}

	@Required
	public void setTravelRouteFacade(final TravelRouteFacade travelRouteFacade)
	{
		this.travelRouteFacade = travelRouteFacade;
	}

	protected TransportOfferingFacade getTransportOfferingFacade()
	{
		return transportOfferingFacade;
	}

	@Required
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
