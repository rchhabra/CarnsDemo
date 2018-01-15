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

package de.hybris.platform.travelbackoffice.widgets.scheduleconfiguration;

import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackofficeservices.model.cronjob.ManageTransportOfferingForScheduleConfigurationCronJobModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEventTypes;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.util.MessageboxUtils;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;



/**
 * Default confirmation popup handler for "create-schedule" and "modify-schedule" wizard.
 */
public class DefaultManageScheduleConfirmationHandler implements FlowActionHandler
{
	private CronJobService cronjobService;
	private ModelService modelService;

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		Messagebox.show(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_CONFIRMATION_POPUP),
				Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_PREVIEW_CONFIRMATION_POPUP_TITLE),
				MessageboxUtils.NO_YES_OPTION, null, clickEvent -> {
					if (Button.YES.equals(clickEvent.getButton()))
					{
						final WidgetModel widgetModel = adapter.getWidgetInstanceManager().getModel();
						final List<ScheduleConfigurationDayModel> scheduleConfigurationDays = getSelectedScheduleConfigDays(
								widgetModel);
						if (CollectionUtils.isNotEmpty(scheduleConfigurationDays))
						{
							final ScheduleConfigurationModel scheduleConfigurationModel = adapter.getWidgetInstanceManager().getModel()
									.getValue(TravelbackofficeConstants.ITEM, ScheduleConfigurationModel.class);
							scheduleConfigurationModel
									.setStartDate(DateUtils.truncate(scheduleConfigurationModel.getStartDate(), Calendar.DATE));
							scheduleConfigurationModel
									.setEndDate(DateUtils.truncate(scheduleConfigurationModel.getEndDate(), Calendar.DATE));
							final ManageTransportOfferingForScheduleConfigurationCronJobModel scheduleConfigurationCronJobModel = (ManageTransportOfferingForScheduleConfigurationCronJobModel) getCronjobService()
									.getCronJob("manageTransportOfferingForScheduleConfigurationCronJob");
							scheduleConfigurationModel.setScheduleConfigurationDays(scheduleConfigurationDays);
							scheduleConfigurationCronJobModel.setScheduleConfiguration(scheduleConfigurationModel);
							try
							{
								getModelService().saveAll(scheduleConfigurationDays);
								getModelService().save(scheduleConfigurationModel);
								getModelService().save(scheduleConfigurationCronJobModel);
								getCronjobService().performCronJob(scheduleConfigurationCronJobModel, false);
								NotificationUtils.notifyUser(
										NotificationUtils.getWidgetNotificationSource(adapter.getWidgetInstanceManager()),
										NotificationEventTypes.EVENT_TYPE_OBJECT_CREATION, NotificationEvent.Level.SUCCESS,
										scheduleConfigurationModel);

								adapter.done();
							}
							catch (final ModelSavingException mse)
							{
								getModelService().refresh(scheduleConfigurationModel);
								getModelService().removeAll(scheduleConfigurationDays);
								getModelService().remove(scheduleConfigurationModel);
								NotificationUtils.notifyUser(
										NotificationUtils.getWidgetNotificationSource(adapter.getWidgetInstanceManager()),
										NotificationEventTypes.EVENT_TYPE_OBJECT_CREATION, NotificationEvent.Level.FAILURE, mse);
							}
						}
					}
				});
	}

	/**
	 * Method returns list of {@link ScheduleConfigurationDayModel} which are selected.
	 *
	 * @param widgetModel
	 * 		as the widget model
	 */
	protected List<ScheduleConfigurationDayModel> getSelectedScheduleConfigDays(final WidgetModel widgetModel)
	{
		final List<ScheduleConfigurationDayModel> scheduleConfigurationDays = new ArrayList<>();
		Arrays.asList(DayOfWeek.values()).forEach(dayOfWeek -> {
			final ScheduleConfigurationDayModel scheduleConfigurationDay = widgetModel.getValue(dayOfWeek.getCode(),
					ScheduleConfigurationDayModel.class);
			if (BooleanUtils.isTrue(scheduleConfigurationDay.getSelected()))
			{
				scheduleConfigurationDays.add(scheduleConfigurationDay);
			}
		});
		return scheduleConfigurationDays;
	}

	/**
	 * @return the cronjobService
	 */
	protected CronJobService getCronjobService()
	{
		return cronjobService;
	}

	/**
	 * @param cronjobService
	 *           the cronjobService to set
	 */
	@Required
	public void setCronjobService(final CronJobService cronjobService)
	{
		this.cronjobService = cronjobService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
