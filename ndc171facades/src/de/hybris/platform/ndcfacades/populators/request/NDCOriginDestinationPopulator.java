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

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AirShopReqAttributeQueryType.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.services.TravelLocationService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC Origin Destination Populator
 * Creates an originDestinationInfo object from the OriginDestination list in the {@link AirShoppingRQ}
 */
public class NDCOriginDestinationPopulator implements Populator<AirShoppingRQ, FareSearchRequestData>
{

	private EnumerationService enumerationService;
	private TravelLocationService travelLocationService;
	private ConfigurationService configurationService;

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData) throws
			ConversionException
	{
		int index = 0;
		final List<OriginDestinationInfoData> originDestinationInfo = new ArrayList<>();

		for (final OriginDestination originDestination : airShoppingRQ.getCoreQuery().getOriginDestinations().getOriginDestination())
		{
			final OriginDestinationInfoData originDestinationInfoData = new OriginDestinationInfoData();

			originDestinationInfoData.setDepartureLocation(originDestination.getDeparture().getAirportCode().getValue());
			populateDepartureLocationType(originDestinationInfoData, originDestination);

			populateDepartureTime(originDestinationInfoData, originDestination);

			originDestinationInfoData.setArrivalLocation(originDestination.getArrival().getAirportCode().getValue());
			populateArrivalLocationType(originDestinationInfoData, originDestination);

			originDestinationInfoData.setReferenceNumber(index);
			originDestinationInfo.add(originDestinationInfoData);

			index++;
		}

		fareSearchRequestData.setOriginDestinationInfo(originDestinationInfo);
	}

	/**
	 * Populate departure time.
	 *
	 * @param originDestinationInfoData
	 * 		the origin destination info data
	 * @param originDestination
	 * 		the origin destination
	 */
	protected void populateDepartureTime(final OriginDestinationInfoData originDestinationInfoData,
			final OriginDestination originDestination)
	{
		final XMLGregorianCalendar airShoppingRequestDate = originDestination.getDeparture().getDate();
		originDestinationInfoData.setDepartureTime(
				Date.from((LocalDate
						.of(airShoppingRequestDate.getYear(), airShoppingRequestDate.getMonth(), airShoppingRequestDate.getDay()))
						.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

	/**
	 * Populate arrival location type.
	 *
	 * @param originDestinationInfoData
	 * 		the origin destination info data
	 * @param originDestination
	 * 		the origin destination
	 */
	protected void populateArrivalLocationType(final OriginDestinationInfoData originDestinationInfoData,
			final OriginDestination originDestination)
	{
		final LocationModel arrivalLocation = getTravelLocationService()
				.getLocation(originDestination.getArrival().getAirportCode().getValue());

		if (Objects.isNull(arrivalLocation) || Objects.isNull(arrivalLocation.getLocationType()))
		{
			originDestinationInfoData.setArrivalLocationType(getEnumerationService().getEnumerationValue(LocationType.class,
					NdcfacadesConstants.DEFAULT_LOCATION_TYPE));
		}
		else
		{
			originDestinationInfoData.setArrivalLocationType(arrivalLocation.getLocationType());
		}
	}

	/**
	 * Populate departure location type.
	 *
	 * @param originDestinationInfoData
	 * 		the origin destination info data
	 * @param originDestination
	 * 		the origin destination
	 */
	protected void populateDepartureLocationType(final OriginDestinationInfoData originDestinationInfoData,
			final OriginDestination originDestination)
	{
		final LocationModel departureLocation = getTravelLocationService()
				.getLocation(originDestination.getDeparture().getAirportCode().getValue());

		if (Objects.isNull(departureLocation) || Objects.isNull(departureLocation.getLocationType()))
		{
			originDestinationInfoData.setDepartureLocationType(getEnumerationService().getEnumerationValue(LocationType.class,
					NdcfacadesConstants.DEFAULT_LOCATION_TYPE));
		}
		else
		{
			originDestinationInfoData.setDepartureLocationType(departureLocation.getLocationType());
		}
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * Gets travel location service.
	 *
	 * @return the travel location service
	 */
	protected TravelLocationService getTravelLocationService()
	{
		return travelLocationService;
	}

	/**
	 * Sets travel location service.
	 *
	 * @param travelLocationService
	 * 		the travel location service
	 */
	@Required
	public void setTravelLocationService(final TravelLocationService travelLocationService)
	{
		this.travelLocationService = travelLocationService;
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
