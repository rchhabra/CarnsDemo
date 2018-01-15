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
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Vlayout;

import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;


/**
 * Custom view renderer class for "Create Schedule" wizard's second step, it creates view for schedule configuration day
 * grid.
 */
public class CreateScheduleConfigurationDayRenderer extends AbstractScheduleConfigurationDayRenderer
{

	@Override
	public void render(final Component parent, final ViewType customView, final Map<String, String> parameters,
			final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
	{
		final Vlayout vlayout = new Vlayout();
		vlayout.setParent(parent);
		vlayout.appendChild(createLayout(widgetInstanceManager));
	}

	/**
	 * Creates grid layout.
	 *
	 * @param widgetInstanceManager
	 *
	 */
	protected Component createLayout(final WidgetInstanceManager widgetInstanceManager)
	{

		final Div container = new Div();
		container.setVisible(true);
		final Grid scheduleConfigDayGrid = createGridLayout(container);

		final ScheduleConfigurationModel scheduleConfigurationModel = widgetInstanceManager.getModel().getValue("item",
				ScheduleConfigurationModel.class);

		final Set<String> validDays = getAllValidDays(scheduleConfigurationModel);

		Arrays.asList(DayOfWeek.values()).forEach(dayOfWeek -> {

			final DayOfWeek day = DayOfWeek.values()[(dayOfWeek.ordinal() + 1) % 7];
			final boolean isReadOnly = !validDays.contains(day.getCode());
			final ScheduleConfigurationDayModel scheduleConfigurationDayModel = widgetInstanceManager.getModel()
					.getValue(day.getCode(), ScheduleConfigurationDayModel.class);
			scheduleConfigurationDayModel.setDayOfWeek(day);
			final Row row = createConfigDayRow(isReadOnly, scheduleConfigurationModel, widgetInstanceManager, day, false,
					scheduleConfigurationDayModel);
			scheduleConfigDayGrid.getRows().appendChild(row);
		});

		container.appendChild(scheduleConfigDayGrid);
		return container;
	}

}
