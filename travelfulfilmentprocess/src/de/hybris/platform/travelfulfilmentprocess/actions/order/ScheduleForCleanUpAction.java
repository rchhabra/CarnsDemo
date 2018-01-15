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
package de.hybris.platform.travelfulfilmentprocess.actions.order;

import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Schedule for clean up action.
 */
public class ScheduleForCleanUpAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	protected Integer minPeriodWaitingForCleanUpInSeconds = null;
	protected TimeService timeService;

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * Sets min period waiting for clean up in seconds.
	 *
	 * @param minPeriodWaitingForCleanUpInSeconds
	 * 		the min period waiting for clean up in seconds
	 */
	public void setMinPeriodWaitingForCleanUpInSeconds(final Integer minPeriodWaitingForCleanUpInSeconds)
	{
		this.minPeriodWaitingForCleanUpInSeconds = minPeriodWaitingForCleanUpInSeconds;
	}

	/**
	 * Gets min period waiting for clean up in seconds.
	 *
	 * @return the min period waiting for clean up in seconds
	 */
	protected Integer getMinPeriodWaitingForCleanUpInSeconds()
	{
		if (minPeriodWaitingForCleanUpInSeconds == null)
		{
			try
			{
				minPeriodWaitingForCleanUpInSeconds = Integer.valueOf(
						Integer.parseInt(Config.getParameter("travelfulfilmentprocess.fraud.minPeriodWaitingForCleanUpInSeconds")));
			}
			catch (final NumberFormatException e)
			{
				minPeriodWaitingForCleanUpInSeconds = Integer.valueOf(60 * 60 * 24 * 7);
			}
		}
		return minPeriodWaitingForCleanUpInSeconds;
	}

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		ServicesUtil.validateParameterNotNull(process, "process cannot be null");
		final OrderModel order = process.getOrder();
		ServicesUtil.validateParameterNotNull(order, "order cannot be null");
		if (Boolean.FALSE.equals(order.getFraudulent()))
		{
			return Transition.NOK;
		}
		final FraudReportModel lastReport = getLastFraudReportModelWithFraudStatus(order.getFraudReports());
		if (lastReport == null)
		{
			return Transition.NOK;
		}
		final Date lastModification = lastReport.getTimestamp();
		final Date currentDate = getTimeService().getCurrentTime();
		final Calendar threshold = Calendar.getInstance();
		threshold.setTime(currentDate);
		threshold.add(Calendar.SECOND, -getMinPeriodWaitingForCleanUpInSeconds().intValue());
		if (lastModification.before(threshold.getTime()))
		{
			return Transition.OK;
		}
		else
		{
			return Transition.NOK;
		}
	}

	/**
	 * Gets last fraud report model with fraud status.
	 *
	 * @param reports
	 * 		the reports
	 *
	 * @return the last fraud report model with fraud status
	 */
	protected FraudReportModel getLastFraudReportModelWithFraudStatus(final Set<FraudReportModel> reports)
	{
		if (reports == null)
		{
			return null;
		}
		FraudReportModel lastReport = null;
		for (final FraudReportModel report : reports)
		{
			if ((report.getStatus().equals(FraudStatus.FRAUD) || report.getStatus().equals(FraudStatus.CHECK))
					&& (Objects.isNull(lastReport) || report.getTimestamp().before(lastReport.getTimestamp())))
			{
				lastReport = report;
			}
		}

		return lastReport;
	}
}
