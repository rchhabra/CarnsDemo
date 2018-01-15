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
package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelservices.dao.TransportOfferingDao;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.enums.TransportOfferingType;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.service.keygenerator.TravelKeyGeneratorService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.travelservices.vendor.TravelVendorService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link TransportOfferingService}
 */
public class DefaultTransportOfferingService implements TransportOfferingService
{
	private static final Logger LOG = Logger.getLogger(DefaultTransportOfferingService.class);
	private static final String TRAVEL_VENDOR_CODE = "travel.vendor.code";

	private TransportOfferingDao transportOfferingDao;
	private ModelService modelService;
	private TravelVendorService travelVendorService;
	private ConfigurationService configurationService;
	private TravelKeyGeneratorService travelKeyGeneratorService;

	@Override
	public List<TransportOfferingModel> getTransportOfferings(final String number, final Date departureDate)
	{
		ServicesUtil.validateParameterNotNull(number, "Transport Offering Number can not be null");
		ServicesUtil.validateParameterNotNull(departureDate, "Departure date can not be null");

		List<TransportOfferingModel> transportOfferingModelList = null;
		try
		{
			transportOfferingModelList = transportOfferingDao.findTransportOfferings(number, departureDate);
		}
		catch (final ModelNotFoundException ex)
		{
			LOG.info("No TransportOffering found for number: " + number + " and departure date: " + departureDate, ex);
		}

		return transportOfferingModelList;
	}

	@Override
	public TransportOfferingModel getTransportOffering(final String code)
	{
		try
		{
			return transportOfferingDao.findTransportOffering(code);
		}
		catch (ModelNotFoundException | AmbiguousIdentifierException ex)
		{
			LOG.warn(ex);
		}
		return null;
	}

	@Override
	public List<TransportOfferingModel> getTransportOfferings()
	{
		return transportOfferingDao.findTransportOfferings();
	}

	@Override
	public List<TransportOfferingModel> getTransportOfferings(final Collection<String> transportOfferingCodes)
	{
		ServicesUtil.validateParameterNotNull(transportOfferingCodes, "Transport Offering codes must not be null");
		return transportOfferingDao.getTransportOfferings(transportOfferingCodes);
	}

	@Override
	public Map<String, TransportOfferingModel> getTransportOfferingsMap(final Collection<String> transportOfferingCodes)
	{
		ServicesUtil.validateParameterNotNull(transportOfferingCodes, "Transport Offering codes must not be null");
		final List<TransportOfferingModel> transportOfferingModels = getTransportOfferings(transportOfferingCodes);
		final Map<String, TransportOfferingModel> results = new HashMap<>();

		if (CollectionUtils.isNotEmpty(transportOfferingModels))
		{
			transportOfferingModels
					.forEach(transportOfferingModel -> results.put(transportOfferingModel.getCode(), transportOfferingModel));
		}

		return results;
	}

	@Override
	public List<TransportOfferingModel> getTransportOfferingsFromOrderEntries(final List<AbstractOrderEntryModel> orderEntryList)
	{
		return orderEntryList.stream().flatMap(entry-> entry.getTravelOrderEntryInfo().getTransportOfferings().stream()).distinct().collect(
				Collectors.toList());
	}

	@Override
	public List<TransportOfferingModel> createTransportOfferingForScheduleConfiguration(final
	ScheduleConfigurationModel scheduleConfiguration)
	{
		final LocalDate startDate = scheduleConfiguration.getStartDate().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		final LocalDate endDate = scheduleConfiguration.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return Stream.iterate(startDate, date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
				.map(date -> doIterateScheduleConfiguration(scheduleConfiguration, date))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

	}

	/**
	 * Method iterates {@link ScheduleConfigurationDayModel} and create {@link TransportOfferingModel} if configured
	 * scheduleConfigurationDay's DayOfWeek matches with date.
	 *
	 * @param scheduleConfiguration
	 * 		the schedule configuration
	 * @param date
	 * 		the date
	 * @return the transport offering model
	 */
	protected TransportOfferingModel doIterateScheduleConfiguration(final ScheduleConfigurationModel scheduleConfiguration,
			final LocalDate date)
	{
		for (final ScheduleConfigurationDayModel scheduleConfigurationDay : scheduleConfiguration.getScheduleConfigurationDays())
		{
			if (scheduleConfigurationDay.getDayOfWeek().ordinal() == date.getDayOfWeek().getValue() % 7)
			{
				final TransportOfferingModel transportOffering = getModelService().create(TransportOfferingModel.class);
				populateTransportOfferingData(scheduleConfigurationDay, transportOffering, scheduleConfiguration, date);
				return transportOffering;
			}
		}
		return null;
	}

	/**
	 * Method populates data from {@link ScheduleConfigurationDayModel} to {@link TransportOfferingModel}
	 *
	 * @param scheduleConfigurationDay
	 * 		the schedule configuration day
	 * @param transportOffering
	 * 		the transport offering
	 * @param scheduleConfiguration
	 * 		the schedule configuration
	 * @param date
	 * 		the date
	 */
	protected void populateTransportOfferingData(final ScheduleConfigurationDayModel scheduleConfigurationDay,
			final TransportOfferingModel transportOffering, final ScheduleConfigurationModel scheduleConfiguration,
			final LocalDate date)
	{
		transportOffering.setTravelProvider(scheduleConfiguration.getTravelProvider());
		transportOffering.setNumber(scheduleConfiguration.getNumber());
		transportOffering
				.setDepartureTime(getDepartureTime(date, scheduleConfigurationDay.getDepartureTime(), scheduleConfiguration));
		transportOffering.setArrivalTime(getArrivalTime(scheduleConfiguration, scheduleConfigurationDay, transportOffering));
		transportOffering.setOriginTerminal(scheduleConfigurationDay.getOriginTerminal());
		transportOffering.setDestinationTerminal(scheduleConfigurationDay.getDestinationTerminal());
		transportOffering.setTravelSector(scheduleConfiguration.getTravelSector());
		transportOffering.setCode(getTravelKeyGeneratorService().generateTransportOfferingCode(transportOffering));
		transportOffering.setTransportVehicle(scheduleConfigurationDay.getTransportVehicle());
		transportOffering.setActive(Boolean.TRUE);
		transportOffering.setVendor(
				getTravelVendorService().getVendorByCode(getConfigurationService().getConfiguration().getString(TRAVEL_VENDOR_CODE)));
		transportOffering.setType(TransportOfferingType.FLIGHT);
		transportOffering.setStatus(TransportOfferingStatus.SCHEDULED);
	}

	/**
	 * Method returns the flight arrival time(considering time zone) by adding flight duration to departure time.
	 *
	 * @param scheduleConfiguration
	 * 		the schedule configuration
	 * @param scheduleConfigurationDay
	 * 		the schedule configuration day
	 * @param transportOffering
	 * 		the transport offering
	 * @return the arrival time
	 */
	protected Date getArrivalTime(final ScheduleConfigurationModel scheduleConfiguration,
			final ScheduleConfigurationDayModel scheduleConfigurationDay, final TransportOfferingModel transportOffering)
	{
		final ZoneId originZoneId = ZoneId
				.of(scheduleConfiguration.getTravelSector().getOrigin().getPointOfService().get(0).getTimeZoneId());
		final ZoneId destinationZoneId = ZoneId
				.of(scheduleConfiguration.getTravelSector().getDestination().getPointOfService().get(0).getTimeZoneId());

		final ZonedDateTime originUtcDateTime = TravelDateUtils.getUtcZonedDateTime(transportOffering.getDepartureTime(),
				originZoneId);

		final ZonedDateTime destinationUtcDateTime = originUtcDateTime.withZoneSameInstant(destinationZoneId)
				.plusHours(Objects.nonNull(scheduleConfigurationDay.getDurationHrs()) ? scheduleConfigurationDay.getDurationHrs() : 0)
				.plusMinutes(
						Objects.nonNull(scheduleConfigurationDay.getDurationMins()) ? scheduleConfigurationDay.getDurationMins() : 0);

		return Date.from(destinationUtcDateTime.toInstant());
	}

	/**
	 * Method returns the flight departure time by combining start date and time.
	 *
	 * @param date
	 * 		the date
	 * @param departureTime
	 * 		the departure time
	 * @param scheduleConfiguration
	 * 		the schedule configuration
	 * @return the departure time
	 */
	protected Date getDepartureTime(final LocalDate date, final Date departureTime,
			final ScheduleConfigurationModel scheduleConfiguration)
	{
		final ZoneId zoneId = ZoneId
				.of(scheduleConfiguration.getTravelSector().getOrigin().getPointOfService().get(0).getTimeZoneId());
		final LocalDateTime localDateTime = LocalDateTime.of(date,
				LocalDateTime.ofInstant(departureTime.toInstant(), zoneId).toLocalTime());
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}


	/**
	 * @return the transportOfferingDao
	 */
	protected TransportOfferingDao getTransportOfferingDao()
	{
		return transportOfferingDao;
	}

	/**
	 * @param transportOfferingDao
	 * 		the transportOfferingDao to set
	 */
	@Required
	public void setTransportOfferingDao(final TransportOfferingDao transportOfferingDao)
	{
		this.transportOfferingDao = transportOfferingDao;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 * 		the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets travel vendor service.
	 *
	 * @return the travel vendor service
	 */
	protected TravelVendorService getTravelVendorService()
	{
		return travelVendorService;
	}

	/**
	 * Sets travel vendor service.
	 *
	 * @param travelVendorService
	 * 		the travel vendor service
	 */
	@Required
	public void setTravelVendorService(final TravelVendorService travelVendorService)
	{
		this.travelVendorService = travelVendorService;
	}

	/**
	 * @return configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 * 		the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 *
	 * @return travelKeyGeneratorService
	 */
	protected TravelKeyGeneratorService getTravelKeyGeneratorService()
	{
		return travelKeyGeneratorService;
	}

	/**
	 *
	 * @param travelKeyGeneratorService the travelKeyGeneratorService to set
	 */
	@Required
	public void setTravelKeyGeneratorService(final
			TravelKeyGeneratorService travelKeyGeneratorService)
	{
		this.travelKeyGeneratorService = travelKeyGeneratorService;
	}
}
