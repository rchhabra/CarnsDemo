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
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.FlightInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;


/**
 * NDC OriginDestination populator for {@link SeatAvailabilityRQ}
 */
public class NDCSeatAvailabilityOriginDestinationPopulator extends NDCAbstractOffersOriginDestinationPopulator
		implements Populator<SeatAvailabilityRQ, OfferRequestData>
{

	@Override
	public void populate(final SeatAvailabilityRQ source, final OfferRequestData target) throws ConversionException
	{
		if (Objects.nonNull(source.getQuery().getOriginDestination()))
		{
			final List<FlightInfoAssocType> flightInfoAssocTypeList = source.getQuery().getOriginDestination();
			final List<ItineraryData> itineraries = new ArrayList<>();
			flightInfoAssocTypeList.forEach(originDestination -> {
				final ItineraryData itinerary = createItinerary(
						(OriginDestination) originDestination.getOriginDestinationReferences().get(0));
				itinerary.setTripType(flightInfoAssocTypeList.size() == 1 ? TripType.SINGLE : TripType.RETURN);
				itineraries.add(itinerary);
			});
			target.setItineraries(itineraries);
			target.setSelectedOffers(getSelectedOffer());
		}
	}

	/**
	 * This method creates and returns an instance of {@link ItineraryData}
	 *
	 * @param originDestination
	 * 		the origin destination
	 *
	 * @return the itinerary data
	 */
	protected ItineraryData createItinerary(final OriginDestination originDestination)
	{
		final ItineraryData itinerary = new ItineraryData();
		itinerary.setDuration(getDurationMap(originDestination.getTotalJourney()));
		try
		{
			final TravelRouteData travelRouteData = getTravelRouteFacade()
					.getTravelRoute(originDestination.getOriginDestinationKey());
			itinerary.setOriginDestinationOptions(getOriginDestinationOptions(originDestination));
			itinerary.setRoute(travelRouteData);
		}
		catch (final ModelNotFoundException exception)
		{
			if (!StringUtils.equals(exception.getMessage(),
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.SEATS_UNAVAILABLE)))
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
	 * This method creates an instance of {@link ArrayList} of {@link OriginDestinationOptionData}
	 *
	 * @param originDestination
	 * 		the origin destination
	 *
	 * @return origin destination options
	 */
	protected List<OriginDestinationOptionData> getOriginDestinationOptions(final OriginDestination originDestination)
	{
		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>(1);
		final OriginDestinationOptionData originDestinationOption = new OriginDestinationOptionData();
		originDestinationOption.setTransportOfferings(getTransportOfferingDatas(originDestination));
		originDestinationOption.setActive(true);
		originDestinationOptions.add(originDestinationOption);
		return originDestinationOptions;
	}

	/**
	 * This method creates and returns {@link ArrayList} of {@link TransportOfferingData} from {@link OriginDestination}
	 *
	 * @param originDestination
	 * 		the origin destination
	 *
	 * @return the list of transport offering data
	 */
	protected List<TransportOfferingData> getTransportOfferingDatas(final OriginDestination originDestination)
	{
		final List<TransportOfferingData> transportOfferingDatas = new ArrayList<>();
		if (Objects.nonNull(originDestination.getFlightReferences()))
		{
			final List<Object> objects = originDestination.getFlightReferences().getValue();
			objects.forEach(obj -> ((Flight) (obj)).getSegmentReferences().getValue().forEach(value -> {
				try
				{
					final ListOfFlightSegmentType flightSegment = (ListOfFlightSegmentType) value;
					final TransportOfferingData transportOfferingData = getTransportOfferingFacade()
							.getTransportOffering(String.valueOf(flightSegment.getSegmentKey()));
					transportOfferingDatas.add(transportOfferingData);
				}
				catch (final ModelNotFoundException exception)
				{
					throw new ModelNotFoundException(
							getConfigurationService().getConfiguration().getString(NdcfacadesConstants.SEATS_UNAVAILABLE), exception);
				}
			}));
		}
		return transportOfferingDatas;
	}
}
