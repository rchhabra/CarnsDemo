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
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.BooleanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Custom validation handler for "create-schedule" and "manage-schedule" wizard's 2nd step. It validates all mandatory
 * field to be not null.
 */
public class ManageScheduleValidationHandler implements FlowActionHandler
{
	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		boolean isFieldEmpty = false;
		boolean isAnyCheckboxSelected = false;

		for (final DayOfWeek dayOfWeek : DayOfWeek.values())
		{
			final ScheduleConfigurationDayModel selectedDay = adapter.getWidgetInstanceManager().getModel()
					.getValue(dayOfWeek.getCode(), ScheduleConfigurationDayModel.class);

			if (BooleanUtils.isTrue(selectedDay.getSelected()))
			{
				isAnyCheckboxSelected = true;
				break;
			}
		}

		if (!isAnyCheckboxSelected)
		{
			Messagebox.show(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_NO_CHECKBOX_SELECTED_VALIDATION_MESSAGE),
					Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Button[]
					{ Button.OK }, null, null);
		}

		for (final DayOfWeek dayOfWeek : DayOfWeek.values())
		{
			final ScheduleConfigurationDayModel selectedDay = adapter.getWidgetInstanceManager().getModel()
					.getValue(dayOfWeek.getCode(), ScheduleConfigurationDayModel.class);

			if (BooleanUtils.isTrue(selectedDay.getSelected())
					&& ((Objects.isNull(selectedDay.getDurationHrs()) && Objects.isNull(selectedDay.getDurationMins()))
							|| (Objects.isNull(selectedDay.getDepartureTime()) || Objects.isNull(selectedDay.getOriginTerminal())
									|| Objects.isNull(selectedDay.getDestinationTerminal())
									|| Objects.isNull(selectedDay.getTransportVehicle()))))
			{
				Messagebox.show(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_VALIDATION_MESSAGE),
						Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Button[]
						{ Button.OK }, null, null);
				isFieldEmpty = true;
				break;
			}
		}

		if (isAnyCheckboxSelected && !isFieldEmpty)
		{
			adapter.next();
		}
	}
}
