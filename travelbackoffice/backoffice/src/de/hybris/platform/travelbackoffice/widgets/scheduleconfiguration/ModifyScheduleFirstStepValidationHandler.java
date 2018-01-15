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

import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Custom validation handler for "modify-schedule" wizard's 1st step. It validates all mandatory fields to be selected.
 */
public class ModifyScheduleFirstStepValidationHandler implements FlowActionHandler
{

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		final ScheduleConfigurationModel selectedScheduleConfiguration = adapter.getWidgetInstanceManager().getModel()
				.getValue(TravelbackofficeConstants.ITEM, ScheduleConfigurationModel.class);

		if (Objects.isNull(selectedScheduleConfiguration))
		{
			Messagebox.show(Labels.getLabel(TravelbackofficeConstants.MODIFY_MISSING_SELECTED_SCHEDULE),
					Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Button[]
					{ Button.OK }, null, null);
			return;
		}

		final Boolean isChangeSectorConfirmed = adapter.getWidgetInstanceManager().getModel()
				.getValue(TravelbackofficeConstants.IS_CHANGE_SECTOR_CONFIRMED, Boolean.class);
		if (BooleanUtils.isTrue(isChangeSectorConfirmed))
		{
			final ScheduleConfigurationModel searchScheduleInfo = adapter.getWidgetInstanceManager().getModel()
					.getValue(TravelbackofficeConstants.SEARCH_SCHEDULE_INFO, ScheduleConfigurationModel.class);
			final TravelSectorModel travelSector = searchScheduleInfo.getTravelSector();
			if (Objects.isNull(travelSector))
			{
				Messagebox.show(Labels.getLabel(TravelbackofficeConstants.MODIFY_SCHEDULE_MISSING_TRAVEL_SECTOR),
						Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Button[]
						{ Button.OK }, null, null);
				return;
			}
		}
		adapter.next();
	}
}
