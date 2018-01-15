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
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This cronJob populates the origin locations and the destination locations of a transport offering
 */
public class UpdateLocationsToTransportOfferingJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = Logger.getLogger(UpdateLocationsToTransportOfferingJob.class);

	private BackofficeTransportOfferingService backofficeTransportOfferingService;

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		LOG.info("Performing Cronjob -> UpdateLocationsToTransportOfferingJob....");

		final List<TransportOfferingModel> transportOfferings = getBackofficeTransportOfferingService().getTransportOfferings();

		getBackofficeTransportOfferingService().updateTransportOfferingsWithLocations(transportOfferings);

		LOG.info("Cronjob -> UpdateLocationsToTransportOfferingJob Completed");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
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
	public void setBackofficeTransportOfferingService(final BackofficeTransportOfferingService backofficeTransportOfferingService)
	{
		this.backofficeTransportOfferingService = backofficeTransportOfferingService;
	}
}
