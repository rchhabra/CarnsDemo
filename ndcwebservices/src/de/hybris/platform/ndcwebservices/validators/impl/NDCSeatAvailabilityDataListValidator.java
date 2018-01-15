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

package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.DataListType.OriginDestinationList;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.FlightCOSCoreType;
import de.hybris.platform.ndcfacades.ndc.FlightInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.FlightReferences;
import de.hybris.platform.ndcfacades.ndc.FlightSegmentReference;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The Concrete class that validates {@link DataListType} element for {@link SeatAvailabilityRQ}
 */
public class NDCSeatAvailabilityDataListValidator implements NDCRequestValidator<SeatAvailabilityRQ>
{
	private ConfigurationService configurationService;

	@Override
	public void validate(final SeatAvailabilityRQ seatAvailabilityRQ, final ErrorsType errorsType)
	{
		if (Objects.nonNull(seatAvailabilityRQ.getDataLists()))
		{
			if (!validateOriginDestinationFlightRef(seatAvailabilityRQ.getDataLists().getOriginDestinationList(), errorsType))
			{
				return;
			}

			if (!validateAllFlightRefs(seatAvailabilityRQ.getDataLists(), seatAvailabilityRQ.getQuery().getOriginDestination(),
					errorsType))
			{
				return;
			}

			if (!validateODKey(seatAvailabilityRQ.getDataLists().getOriginDestinationList().getOriginDestination(), errorsType))
			{
				return;
			}

			if (!validateSegmentReferences(seatAvailabilityRQ.getDataLists().getFlightSegmentList(),
					seatAvailabilityRQ.getDataLists().getFlightList(), errorsType))
			{
				return;
			}

			if (!validateClassOfServiceElement(seatAvailabilityRQ.getDataLists().getFlightSegmentList(), errorsType))
			{
				return;
			}

			validateClassOfServiceElementValue(seatAvailabilityRQ.getDataLists().getFlightSegmentList(), errorsType);
		}
		else
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.DATALISTS_UNAVAILABLE));
		}
	}

	/**
	 * This method validates reference values of 3 elements( {@link FlightSegmentReference}, {@link Flight} and {@link
	 * FlightReferences} ), if they all are same than returns true otherwise false;
	 *
	 * @param dataLists
	 * 		the data lists
	 * @param originDestinations
	 * 		the origin destinations
	 * @param errorsType
	 * 		the errors type
	 * @return the boolean
	 */
	protected boolean validateAllFlightRefs(final DataListType dataLists, final List<FlightInfoAssocType> originDestinations,
			final ErrorsType errorsType)
	{
		final Set<String> flightSegmentRefs = new HashSet<>();

		for (final FlightInfoAssocType fligtInfo : originDestinations)
		{
			for (final Object originDestinationRef : fligtInfo.getOriginDestinationReferences())
			{
				final OriginDestination originDestination = (OriginDestination) originDestinationRef;
				if (Objects.nonNull(originDestination.getFlightReferences()) && CollectionUtils
						.isNotEmpty(originDestination.getFlightReferences().getValue()))
				{
					final Flight flight = (Flight) originDestination.getFlightReferences().getValue().get(0);
					flightSegmentRefs.add(flight.getFlightKey());
				}
				else
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_FLIGHT_REFERENCES));
					return false;
				}
			}
		}

		final Set<String> flightKeys = dataLists.getFlightList().getFlight().stream().map(Flight::getFlightKey)
				.collect(Collectors.toSet());

		final Set<String> flightRefs = new HashSet<>();
		dataLists.getOriginDestinationList().getOriginDestination().forEach(originDestination -> originDestination
				.getFlightReferences().getValue().forEach(object -> flightRefs.add(((Flight) object).getFlightKey())));

		if (!(CollectionUtils.size(flightSegmentRefs) == CollectionUtils.size(flightKeys)
				&& CollectionUtils.size(flightKeys) == CollectionUtils.size(flightRefs)))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISMATCH_OF_FLIGHT_REF));
			return false;
		}
		if (!flightSegmentRefs.containsAll(flightKeys) || !flightKeys.containsAll(flightRefs)
				|| !flightRefs.containsAll(flightSegmentRefs))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISMATCH_OF_FLIGHT_REF));
			return false;
		}
		return true;
	}



	/**
	 * This method validates OriginDestinationKey for every {@link OriginDestination}, add error if it is not present.
	 *
	 * @param originDestinations
	 * @param errorsType
	 */
	private boolean validateODKey(final List<OriginDestination> originDestinations, final ErrorsType errorsType)
	{
		if (originDestinations.stream().anyMatch(originDestination -> Objects.isNull(originDestination.getOriginDestinationKey())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_ORIGIN_DESTINATION_KEY));
			return false;
		}
		return true;
	}

	/**
	 * This method validates the allowed values for {@link FlightCOSCoreType} element with configured values, returns false if
	 * invalid.
	 *
	 * @param flightSegmentList
	 * 		the flight segment list
	 * @param errorsType
	 * 		the errors type
	 * @return the boolean
	 */
	protected boolean validateClassOfServiceElementValue(final FlightSegmentList flightSegmentList, final ErrorsType errorsType)
	{
		final String configuredCodes = getConfigurationService().getConfiguration().getString(
				NdcfacadesConstants.CLASS_OF_SERVICE_CODE_VALUES, NdcfacadesConstants.CLASS_OF_SERVICE_CODE_DEFAULT_VALUES);

		if (flightSegmentList.getFlightSegment().stream()
				.anyMatch(flightSegment -> !Arrays.asList(configuredCodes.split(NdcfacadesConstants.COMMA_SEPARATOR))
						.contains(flightSegment.getClassOfService().getCode().getValue())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_CLASS_OF_SERVICE_CODE));
			return false;
		}
		return true;
	}

	/**
	 * This method returns {@link FlightCOSCoreType} element, returns false if it is missing otherwise true.
	 *
	 * @param flightSegmentList
	 * 		the flight segment list
	 * @param errorsType
	 * 		the errors type
	 * @return boolean
	 */
	protected boolean validateClassOfServiceElement(final FlightSegmentList flightSegmentList, final ErrorsType errorsType)
	{
		if (flightSegmentList.getFlightSegment().stream()
				.anyMatch(flightSegment -> Objects.isNull(flightSegment.getClassOfService())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.CLASS_OF_SERVICE_MISSING));
			return false;
		}

		if (flightSegmentList.getFlightSegment().stream()
				.anyMatch(flightSegment -> Objects.isNull(flightSegment.getClassOfService().getCode())))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.CLASS_OF_SERVICE_CODE_MISSING));
			return false;
		}
		return true;
	}

	/**
	 * This method returns false if any mismatch found in segment keys in {@link FlightList} and segment keys in {@link
	 * FlightSegmentList}
	 *
	 * @param flightSegmentList
	 * 		the flight segment list
	 * @param flightList
	 * 		the flight list
	 * @param errorsType
	 * 		the errors type
	 * @return the boolean
	 */
	protected boolean validateSegmentReferences(final FlightSegmentList flightSegmentList, final FlightList flightList,
			final ErrorsType errorsType)
	{
		final Set<String> segmentKeys = new HashSet<>();
		flightList.getFlight().forEach(flight ->
		{
			segmentKeys.addAll(flight.getSegmentReferences().getValue().stream()
					.map(obj -> ((ListOfFlightSegmentType) obj).getSegmentKey()).collect(Collectors.toSet()));
		});

		final Set<String> segments = flightSegmentList.getFlightSegment().stream()
				.map(ListOfFlightSegmentType::getSegmentKey).collect(Collectors.toSet());

		if (!segmentKeys.containsAll(segments))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISMATCH_FLIGHT_AND_SEGMENT));
			return false;
		}
		return true;
	}

	/**
	 * This method add error and returns false if any {@link OriginDestination} exists without {@link FlightReferences} in
	 * request.
	 *
	 * @param originDestinationList
	 * 		the origin destination list
	 * @param errorsType
	 * 		the errors type
	 * @return the boolean
	 */
	protected boolean validateOriginDestinationFlightRef(final OriginDestinationList originDestinationList,
			final ErrorsType errorsType)
	{
		if (Objects.nonNull(originDestinationList))
		{
			if (originDestinationList.getOriginDestination().stream().anyMatch(Objects::isNull))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.FLIGHT_REF_UNAVAILABLE));
				return false;
			}
		}
		else
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.ORIGIN_DESTINATION_LIST_UNAVAILABLE));
			return false;
		}
		return true;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
