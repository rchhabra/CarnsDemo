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

package de.hybris.platform.travelbackofficeservices.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackofficeservices.services.BackofficeScheduleConfigurationService;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Job creating creating schedule configurations from a transport offerings pool and associating orphan transport offerings to the correct schedule configuration
 */
public class ManageScheduleConfigurationsJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = Logger.getLogger(ManageScheduleConfigurationsJob.class);

	private BackofficeTransportOfferingService backofficeTransportOfferingService;
	private BackofficeScheduleConfigurationService backofficeScheduleConfigurationService;
	private EnumerationService enumerationService;

	@Override
	public PerformResult perform(final CronJobModel cronJobModel)
	{
		LOG.info("Start performing ManageScheduleConfigurationsJob");
		final List<TransportOfferingModel> transportOfferingsWithoutSchedule = getBackofficeTransportOfferingService()
				.findTransportOfferingsWithoutSchedule();
		final List<ScheduleConfigurationModel> scheduleConfigurations = new ArrayList<>();

		if (CollectionUtils.isEmpty(transportOfferingsWithoutSchedule))
		{
			LOG.info(
					"No transport offering without associated schedule found. ManageScheduleConfigurationsJob finishing successfully.");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		scheduleConfigurations.addAll(getBackofficeScheduleConfigurationService()
				.getAllScheduleConfigurations());

		final ZoneId currentZone = ZoneId.systemDefault();

		for (final TransportOfferingModel transportOffering : transportOfferingsWithoutSchedule)
		{

			List<ScheduleConfigurationModel> eligibleScheduleConfigurations = scheduleConfigurations.stream()
					.filter(schedule -> isMatchingSchedule(schedule, transportOffering))
					.sorted((ScheduleConfigurationModel s1, ScheduleConfigurationModel s2) -> TravelDateUtils
							.isAfter(s1.getEndDate(), currentZone, s2.getEndDate(), currentZone) ?
							NumberUtils.INTEGER_ONE :
							NumberUtils.INTEGER_MINUS_ONE)
					.collect(Collectors.toList());

			if (CollectionUtils.isEmpty(eligibleScheduleConfigurations))
			{
				final ScheduleConfigurationModel scheduleToCreate = createScheduleConfiguration(transportOffering);
				getModelService().save(scheduleToCreate);
				scheduleConfigurations.add(scheduleToCreate);
				continue;
			}


			if (CollectionUtils.size(eligibleScheduleConfigurations) == NumberUtils.INTEGER_ONE)
			{
				final ScheduleConfigurationModel eligibleSchedule = eligibleScheduleConfigurations.stream().findFirst().get();
				if (!updateScheduleConfiguration(eligibleSchedule, transportOffering))
				{
					final ScheduleConfigurationModel scheduleToCreate = createScheduleConfiguration(transportOffering);
					getModelService().save(scheduleToCreate);
					scheduleConfigurations.add(scheduleToCreate);
				}
				continue;
			}

			eligibleScheduleConfigurations = eligibleScheduleConfigurations.stream()
					.filter(eligibleSchedule -> areMatchingScheduleDays(eligibleSchedule, transportOffering)).collect(
							Collectors.toList());
			if (CollectionUtils.isNotEmpty(eligibleScheduleConfigurations))
			{
				final ScheduleConfigurationModel eligibleSchedule = eligibleScheduleConfigurations.stream()
						.filter(schedule -> TravelDateUtils.isBetweenDates(transportOffering.getDepartureTime(), schedule.getStartDate(),
								TravelDateUtils.addDays(schedule.getEndDate(), 1))).findFirst()
						.orElse(eligibleScheduleConfigurations.stream().findFirst().get());

				associateTransportOfferingToScheduleConfiguration(eligibleSchedule, transportOffering);
				continue;

			}

			final ScheduleConfigurationModel scheduleToCreate = createScheduleConfiguration(transportOffering);
			getModelService().save(scheduleToCreate);
			scheduleConfigurations.add(scheduleToCreate);

		}

		LOG.info("ManageScheduleConfigurationsJob completed.");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}

	protected boolean updateScheduleConfiguration(final ScheduleConfigurationModel scheduleConfigurationModel,
			final TransportOfferingModel transportOffering)
	{
		if (!(TravelDateUtils.isBetweenDates(transportOffering.getDepartureTime(), scheduleConfigurationModel.getStartDate(),
				TravelDateUtils.addDays(scheduleConfigurationModel.getEndDate(), 1)) || isWithinAWeekFromLast(
				scheduleConfigurationModel.getTransportOfferings(), transportOffering)))
		{
			return false;
		}

		final Optional<ScheduleConfigurationDayModel> optionalScheduleDay = scheduleConfigurationModel
				.getScheduleConfigurationDays().stream()
				.filter(day -> transportOffering.getDepartureTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
						.getDayOfWeek().name().equalsIgnoreCase(day.getDayOfWeek().getCode()))
				.findAny();
		if (!optionalScheduleDay.isPresent())
		{
			final List<ScheduleConfigurationDayModel> scheduleDays = new ArrayList<>(
					scheduleConfigurationModel.getScheduleConfigurationDays());
			scheduleDays.add(createScheduleConfigurationDay(transportOffering));
			scheduleConfigurationModel.setScheduleConfigurationDays(scheduleDays);
			associateTransportOfferingToScheduleConfiguration(scheduleConfigurationModel, transportOffering);
			return true;
		}
		if (!hasSameDayAttributes(optionalScheduleDay.get(), transportOffering))
		{
			return false;
		}
		associateTransportOfferingToScheduleConfiguration(scheduleConfigurationModel, transportOffering);
		return true;
	}

	protected boolean areMatchingScheduleDays(final ScheduleConfigurationModel eligibleSchedule,
			final TransportOfferingModel transportOffering)
	{
		final ScheduleConfigurationDayModel optionalScheduleDay = eligibleSchedule.getScheduleConfigurationDays().stream()
				.filter(day -> transportOffering.getDepartureTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
						.getDayOfWeek().name().equalsIgnoreCase(day.getDayOfWeek().getCode()))
				.findAny().orElse(null);
		if (Objects.isNull(optionalScheduleDay))
		{
			return false;
		}

		return hasSameDayAttributes(optionalScheduleDay, transportOffering);
	}

	protected boolean hasSameDayAttributes(final ScheduleConfigurationDayModel scheduleConfigurationDayModel, final TransportOfferingModel transportOffering)
	{
		return TravelDateUtils.isSameTime(scheduleConfigurationDayModel.getDepartureTime(), transportOffering.getDepartureTime())
				&& scheduleConfigurationDayModel.getOriginTerminal().equals(transportOffering.getOriginTerminal())
				&& scheduleConfigurationDayModel.getDestinationTerminal().equals(transportOffering.getDestinationTerminal());
	}

	protected boolean isWithinAWeekFromLast(final List<TransportOfferingModel> transportOfferings,
			final TransportOfferingModel transportOffering)
	{
		return transportOfferings.stream()
				.map(TransportOfferingModel::getDepartureTime)
				.anyMatch(date -> (TravelDateUtils.getDaysBetweenDates(date, transportOffering.getDepartureTime())) <= 7);
	}



	protected boolean isMatchingSchedule(final ScheduleConfigurationModel schedule, final TransportOfferingModel transportOffering)
	{
		return schedule.getTravelProvider().equals(transportOffering.getTravelProvider())
				&& schedule.getNumber().equals(transportOffering.getNumber())
				&& schedule.getTravelSector().equals(transportOffering.getTravelSector());
	}

	protected ScheduleConfigurationModel createScheduleConfiguration(final TransportOfferingModel transportOffering)
	{
		final ScheduleConfigurationModel newSchedule = getModelService().create(ScheduleConfigurationModel.class);
		newSchedule.setTransportOfferings(Collections.singletonList(transportOffering));
		newSchedule.setStartDate(transportOffering.getDepartureTime());
		newSchedule.setEndDate(TravelDateUtils.addDays(newSchedule.getStartDate(), 6));
		newSchedule.setNumber(transportOffering.getNumber());
		newSchedule.setTravelProvider(transportOffering.getTravelProvider());
		newSchedule.setTravelSector(transportOffering.getTravelSector());

		newSchedule.setScheduleConfigurationDays(Collections.singletonList(createScheduleConfigurationDay(transportOffering)));

		return newSchedule;
	}

	protected ScheduleConfigurationDayModel createScheduleConfigurationDay(final TransportOfferingModel transportOffering)
	{
		final ScheduleConfigurationDayModel newScheduleDay = getModelService().create(ScheduleConfigurationDayModel.class);
		newScheduleDay.setDayOfWeek(getEnumerationService().getEnumerationValue(DayOfWeek.class,
				transportOffering.getDepartureTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().name()));
		newScheduleDay.setDepartureTime(transportOffering.getDepartureTime());
		newScheduleDay.setDestinationTerminal(transportOffering.getDestinationTerminal());
		newScheduleDay.setOriginTerminal(transportOffering.getOriginTerminal());
		newScheduleDay.setTransportVehicle(transportOffering.getTransportVehicle());
		newScheduleDay.setDurationHrs((int) TimeUnit.MILLISECONDS.toHours(transportOffering.getDuration()));
		newScheduleDay.setDurationMins((int) TimeUnit.MILLISECONDS
				.toMinutes(transportOffering.getDuration() - TimeUnit.HOURS.toMillis(newScheduleDay.getDurationHrs())));
		newScheduleDay.setSelected(Boolean.TRUE);

		return newScheduleDay;
	}

	protected void associateTransportOfferingToScheduleConfiguration(
			final ScheduleConfigurationModel scheduleConfiguration, final TransportOfferingModel transportOffering)
	{
		final List<TransportOfferingModel> associatedTransportOfferings = new ArrayList<>(scheduleConfiguration.getTransportOfferings());
		associatedTransportOfferings.add(transportOffering);
		scheduleConfiguration.setTransportOfferings(associatedTransportOfferings);
		scheduleConfiguration.setEndDate(transportOffering.getDepartureTime());
		getModelService().save(scheduleConfiguration);
		getModelService().refresh(scheduleConfiguration);
	}


	/**
	 * @return backofficeTransportOfferingService
	 */
	protected BackofficeTransportOfferingService getBackofficeTransportOfferingService()
	{
		return backofficeTransportOfferingService;
	}

	/**
	 * @param backofficeTransportOfferingService
	 * 		the backofficeTransportOfferingService to set
	 */
	@Required
	public void setBackofficeTransportOfferingService(
			final BackofficeTransportOfferingService backofficeTransportOfferingService)
	{
		this.backofficeTransportOfferingService = backofficeTransportOfferingService;
	}

	/**
	 * @return backofficeScheduleConfigurationService
	 */
	protected BackofficeScheduleConfigurationService getBackofficeScheduleConfigurationService()
	{
		return backofficeScheduleConfigurationService;
	}

	/**
	 * @param backofficeScheduleConfigurationService
	 * 		the backofficeScheduleConfigurationService to set
	 */
	@Required
	public void setBackofficeScheduleConfigurationService(
			final BackofficeScheduleConfigurationService backofficeScheduleConfigurationService)
	{
		this.backofficeScheduleConfigurationService = backofficeScheduleConfigurationService;
	}

	/**
	 * @return modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @return enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
