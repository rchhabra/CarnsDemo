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

package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AnonymousTravelerList;
import de.hybris.platform.ndcfacades.ndc.AnonymousTravelerType;
import de.hybris.platform.ndcfacades.ndc.ArrivalCode;
import de.hybris.platform.ndcfacades.ndc.BaggageListRS;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.DataListType.OriginDestinationList;
import de.hybris.platform.ndcfacades.ndc.DepartureCode;
import de.hybris.platform.ndcfacades.ndc.FlightCOSCoreType;
import de.hybris.platform.ndcfacades.ndc.FlightCOSCoreType.Code;
import de.hybris.platform.ndcfacades.ndc.FlightReferences;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.SegmentReferences;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;
import de.hybris.platform.ndcfacades.ndc.TravelerCoreType.PTC;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to populate response for {@link BaggageListRS} and {@link ServiceListRS}
 */
public abstract class NDCAbstractOffersRSDataListPopulator
{

	private Converter<TransportOfferingData, ListOfFlightSegmentType> ndcFlightSegmentConverter;

	/**
	 * This method create an instance of {@link AnonymousTravelerList} for given map.
	 *
	 * @param map
	 * 		the hash map
	 *
	 * @return the anonymous traveler list
	 */
	protected AnonymousTravelerList createTravellers(final Map<String, Integer> map)
	{
		final AnonymousTravelerList travelerList = new AnonymousTravelerList();

		map.entrySet().forEach(entry -> {
			final AnonymousTravelerType anonymousTravelerType = new AnonymousTravelerType();
			anonymousTravelerType.setObjectKey(entry.getKey());
			final PTC ptc = new PTC();
			ptc.setQuantity(BigInteger.valueOf(entry.getValue()));
			ptc.setValue(entry.getKey());
			anonymousTravelerType.setPTC(ptc);
			travelerList.getAnonymousTraveler().add(anonymousTravelerType);
		});
		return travelerList;
	}

	/**
	 * This method creates {@link HashMap} from {@link OfferResponseData} having {@link PassengerTypeData} and count as
	 * key and value respectively.
	 *
	 * @param source
	 * 		the source
	 *
	 * @return the map
	 */
	protected Map<String, Integer> createTravellerTypeCountMap(final OfferResponseData source)
	{
		final Map<String, Integer> travellerTypeCountMap = new HashMap<>();
		source.getItineraries().get(0).getTravellers().forEach(travellerData -> {

			final PassengerInformationData pid = (PassengerInformationData) travellerData.getTravellerInfo();
			if (travellerTypeCountMap.containsKey(pid.getPassengerType().getCode()))
			{
				int count = travellerTypeCountMap.get(pid.getPassengerType().getCode());
				travellerTypeCountMap.put(pid.getPassengerType().getCode(), ++count);
			}
			else
			{
				travellerTypeCountMap.put(pid.getPassengerType().getCode(), 1);
			}
		});
		return travellerTypeCountMap;
	}

	/**
	 * This method populates the {@link DataListType} from {@link OfferResponseData}
	 *
	 * @param source
	 * 		the source
	 * @param dataListType
	 * 		the data list type
	 */
	protected void populateFlightSegments(final OfferResponseData source, final DataListType dataListType)
	{
		final FlightSegmentList flightSegmentList = new FlightSegmentList();
		final FlightList flightList = new FlightList();
		final OriginDestinationList odList = new OriginDestinationList();
		int flightKeyCounter = 0;
		final Map<String, Flight> flightMap = new HashMap<>();
		for (final ItineraryData itineraryData : source.getItineraries())
		{
			final OriginDestination originDestination = new OriginDestination();
			originDestination.setOriginDestinationKey(itineraryData.getRoute().getCode());
			populateOriginDestinationData(originDestination, itineraryData);
			odList.getOriginDestination().add(originDestination);
			final String flightKey = NdcfacadesConstants.FLIGHT + flightKeyCounter;
			if (!flightMap.containsKey(flightKey))
			{
				final Flight flight = new Flight();
				final SegmentReferences segmentReferences = new SegmentReferences();
				final FlightReferences flightReferences = new FlightReferences();
				flightReferences.getValue().add(flight);
				flight.setFlightKey(flightKey);
				flightMap.put(flightKey, flight);
				itineraryData.getOriginDestinationOptions()
						.forEach(odOptionData -> odOptionData.getTransportOfferings().forEach(transportOfferingData -> {
							final ListOfFlightSegmentType flightSegmentType = new ListOfFlightSegmentType();
							flightSegmentType.setSegmentKey(transportOfferingData.getCode());
							getNdcFlightSegmentConverter().convert(transportOfferingData, flightSegmentType);
							if (Objects.nonNull(source.getSeatMap()))
							{
								populateClassOfService(source.getSeatMap().getSegmentInfoDatas(), transportOfferingData.getCode(),
										flightSegmentType);
							}
							flightSegmentList.getFlightSegment().add(flightSegmentType);
							segmentReferences.getValue().add(flightSegmentType);
							flight.setSegmentReferences(segmentReferences);
						}));
				flightList.getFlight().add(flight);
				originDestination.setFlightReferences(flightReferences);
				flightKeyCounter++;
			}
		}
		dataListType.setOriginDestinationList(odList);
		dataListType.setFlightList(flightList);
		dataListType.setFlightSegmentList(flightSegmentList);
	}

	/**
	 * This method populates {@link FlightCOSCoreType} for given {@link ListOfFlightSegmentType}
	 *
	 * @param segmentInfoDatas
	 * 		the segment info datas
	 * @param transportOfferingCode
	 * 		the transport offering code
	 * @param flightSegmentType
	 * 		the flight segment type
	 */
	protected void populateClassOfService(final List<SegmentInfoData> segmentInfoDatas, final String transportOfferingCode,
			final ListOfFlightSegmentType flightSegmentType)
	{
		final FlightCOSCoreType flightCosCoreType = new FlightCOSCoreType();
		final Code code = new Code();
		code.setValue(getRequiredCabinClass(segmentInfoDatas, transportOfferingCode));
		flightCosCoreType.setCode(code);
		flightSegmentType.setClassOfService(flightCosCoreType);
	}

	/**
	 * This method returns cabin class for filtered {@link SegmentInfoData} for given transport offering code
	 *
	 * @param segmentInfoDatas
	 * 		the segment info datas
	 * @param code
	 * 		the code
	 *
	 * @return the required cabin class
	 */
	protected String getRequiredCabinClass(final List<SegmentInfoData> segmentInfoDatas, final String code)
	{
		final Optional<SegmentInfoData> opt = segmentInfoDatas.stream()
				.filter(segmentInfoData -> StringUtils.equalsIgnoreCase(segmentInfoData.getTransportOfferingCode(), code))
				.findFirst();
		return opt.isPresent() ? opt.get().getCabinClass() : null;
	}

	/**
	 * This method populates {@link OriginDestination} from {@link ItineraryData}
	 *
	 * @param originDestination
	 * 		the origin destination
	 * @param itineraryData
	 * 		the itinerary data
	 */
	protected void populateOriginDestinationData(final OriginDestination originDestination, final ItineraryData itineraryData)
	{
		final ArrivalCode arrivalCode = new ArrivalCode();
		arrivalCode.setValue(itineraryData.getRoute().getDestination().getCode());
		originDestination.setArrivalCode(arrivalCode);

		final DepartureCode departureCode = new DepartureCode();
		departureCode.setValue(itineraryData.getRoute().getOrigin().getCode());
		originDestination.setDepartureCode(departureCode);
	}

	/**
	 * Gets ndc flight segment converter.
	 *
	 * @return the ndc flight segment converter
	 */
	protected Converter<TransportOfferingData, ListOfFlightSegmentType> getNdcFlightSegmentConverter()
	{
		return ndcFlightSegmentConverter;
	}

	/**
	 * Sets ndc flight segment converter.
	 *
	 * @param ndcFlightSegmentConverter
	 * 		the ndc flight segment converter
	 */
	@Required
	public void setNdcFlightSegmentConverter(
			final Converter<TransportOfferingData, ListOfFlightSegmentType> ndcFlightSegmentConverter)
	{
		this.ndcFlightSegmentConverter = ndcFlightSegmentConverter;
	}
}
