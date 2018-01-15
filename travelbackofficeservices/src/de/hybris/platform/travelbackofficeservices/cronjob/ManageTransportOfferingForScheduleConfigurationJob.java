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

package de.hybris.platform.travelbackofficeservices.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackofficeservices.model.cronjob.ManageTransportOfferingForScheduleConfigurationCronJobModel;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelbackofficeservices.utils.TravelBackofficeNotificationUtils;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This job is responsible for creating/modifying instances of {@link TransportOfferingModel} based on
 * {@link ScheduleConfigurationDayModel} created in backoffice.
 */
public class ManageTransportOfferingForScheduleConfigurationJob
		extends AbstractJobPerformable<ManageTransportOfferingForScheduleConfigurationCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(ManageTransportOfferingForScheduleConfigurationJob.class);

	private BackofficeTransportOfferingService backofficeTransportOfferingService;
	private TravelBackofficeNotificationUtils travelBackofficeNotificationUtils;

	@Override
	public PerformResult perform(final ManageTransportOfferingForScheduleConfigurationCronJobModel scheduleConfigurationCronJob)
	{
		LOG.info("Start performing CreateTransportOfferingForScheduleConfigurationJob...");
		final ScheduleConfigurationModel scheduleConfiguration = scheduleConfigurationCronJob.getScheduleConfiguration();
		try
		{
			if (Objects.nonNull(scheduleConfiguration)
					&& CollectionUtils.isNotEmpty(scheduleConfiguration.getScheduleConfigurationDays()))
			{
				final boolean isScheduleConfigurationModified = !CollectionUtils
						.sizeIsEmpty(scheduleConfiguration.getTransportOfferings());
				if(isScheduleConfigurationModified)
				{
					getModelService().removeAll(scheduleConfiguration.getTransportOfferings());
				}
				final List<TransportOfferingModel> transportOfferingModels = getBackofficeTransportOfferingService().createTransportOfferingForScheduleConfiguration(scheduleConfiguration);

				getBackofficeTransportOfferingService().updateTransportOfferingsWithLocations(transportOfferingModels);
				if (CollectionUtils.isNotEmpty(transportOfferingModels))
				{
					getModelService().saveAll(transportOfferingModels);
					scheduleConfiguration.setTransportOfferings(transportOfferingModels);
					getModelService().save(scheduleConfiguration);
				}

				LOG.info("CreateTransportOfferingForScheduleConfigurationJob completed.");

				getTravelBackofficeNotificationUtils().createNotificationAsWorkFlowAction(scheduleConfiguration,
						WorkflowActionStatus.COMPLETED, scheduleConfiguration.getItemtype() + " item successfully created");
			}
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Exception occured while executing CreateTransportOfferingForScheduleConfigurationJob...", e);
			if (Objects.nonNull(scheduleConfiguration))
			{
				getModelService().removeAll(scheduleConfiguration.getScheduleConfigurationDays());
				getModelService().remove(scheduleConfiguration);

				getTravelBackofficeNotificationUtils().createNotificationAsWorkFlowAction(scheduleConfiguration,
						WorkflowActionStatus.TERMINATED, scheduleConfiguration.getNumber());
			}
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
	}


	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * Gets backoffice transport offering service.
	 *
	 * @return the backoffice transport offering service
	 */
	protected BackofficeTransportOfferingService getBackofficeTransportOfferingService()
	{
		return backofficeTransportOfferingService;
	}

	/**
	 * Sets backoffice transport offering service.
	 *
	 * @param backofficeTransportOfferingService
	 * 		the backoffice transport offering service
	 */
	@Required
	public void setBackofficeTransportOfferingService(
			final BackofficeTransportOfferingService backofficeTransportOfferingService)
	{
		this.backofficeTransportOfferingService = backofficeTransportOfferingService;
	}

	/**
	 * @return the travelBackofficeNotificationUtils
	 */
	protected TravelBackofficeNotificationUtils getTravelBackofficeNotificationUtils()
	{
		return travelBackofficeNotificationUtils;
	}

	/**
	 * @param travelBackofficeNotificationUtils
	 *           the travelBackofficeNotificationUtils to set
	 */
	@Required
	public void setTravelBackofficeNotificationUtils(final TravelBackofficeNotificationUtils travelBackofficeNotificationUtils)
	{
		this.travelBackofficeNotificationUtils = travelBackofficeNotificationUtils;
	}


}
