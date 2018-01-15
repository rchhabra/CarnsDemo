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
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;

import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;


/**
 * Custom view renderer class for "Modify Schedule" wizard's second step, it creates view for schedule configuration day
 * grid.
 */
public class ModifyScheduleConfigurationDayRenderer extends AbstractScheduleConfigurationDayRenderer
{
	private static final String TRANSPORT_OFFERING_EFFECTED_LABEL_CLASS = "transportoffering-effected-label";

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
	 */
	protected Component createLayout(final WidgetInstanceManager widgetInstanceManager)
	{
		final Div container = new Div();
		container.setVisible(true);
		final ScheduleConfigurationModel selectedScheduleConfiguration = widgetInstanceManager.getModel()
				.getValue(TravelbackofficeConstants.ITEM, ScheduleConfigurationModel.class);
		final Boolean isChangeSectorConfirmed = widgetInstanceManager.getModel()
				.getValue(TravelbackofficeConstants.IS_CHANGE_SECTOR_CONFIRMED, Boolean.class);
		if (BooleanUtils.isTrue(isChangeSectorConfirmed))
		{
			widgetInstanceManager.getModel().put(TravelbackofficeConstants.SAVED_TRAVEL_SECTOR,
					selectedScheduleConfiguration.getTravelSector());
			final ScheduleConfigurationModel searchScheduleInfo = widgetInstanceManager.getModel()
					.getValue(TravelbackofficeConstants.SEARCH_SCHEDULE_INFO, ScheduleConfigurationModel.class);
			final TravelSectorModel travelSector = searchScheduleInfo.getTravelSector();
			selectedScheduleConfiguration.setTravelSector(travelSector);
		}

		final int totalTransportOfferings = CollectionUtils.isNotEmpty(selectedScheduleConfiguration.getTransportOfferings())
				? selectedScheduleConfiguration.getTransportOfferings().size() : 0;
		final Label totalTransportOfferingsLabel = new Label(new StringBuilder(String.valueOf(totalTransportOfferings))
				.append(TravelbackofficeConstants.SPACE)
				.append(Labels.getLabel(TravelbackofficeConstants.MODIFY_SCHEDULE_TOTAL_TRANSPORT_OFFERINGS_UPDATED)).toString());
		totalTransportOfferingsLabel.setClass(TRANSPORT_OFFERING_EFFECTED_LABEL_CLASS);
		container.appendChild(totalTransportOfferingsLabel);

		createScheduleConfigLayout(container, selectedScheduleConfiguration);
		createScheduleConfigDayLayout(container, selectedScheduleConfiguration, widgetInstanceManager);

		return container;
	}

	/**
	 * Creates grid layout for {@link ScheduleConfigurationDayModel} data.
	 *
	 * @param container
	 * @param scheduleConfigurationModel
	 * @param widgetInstanceManager
	 */
	protected void createScheduleConfigDayLayout(final Div container, final ScheduleConfigurationModel scheduleConfigurationModel,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Grid scheduleConfigDayGrid = createGridLayout(container);

		final Set<String> validDays = getAllValidDays(scheduleConfigurationModel);
		Arrays.asList(DayOfWeek.values()).forEach(dayOfWeek -> {

			final DayOfWeek day = DayOfWeek.values()[(dayOfWeek.ordinal() + 1) % 7];
			final boolean isReadOnly = !validDays.contains(day.getCode());
			final ScheduleConfigurationDayModel scheduleConfigurationDay = getScheduleConfigDay(scheduleConfigurationModel, day,
					widgetInstanceManager);
			final Row row = createConfigDayRow(isReadOnly, scheduleConfigurationModel, widgetInstanceManager, day, true,
					scheduleConfigurationDay);
			scheduleConfigDayGrid.getRows().appendChild(row);
		});
		container.appendChild(scheduleConfigDayGrid);
	}

	/**
	 * Method checks if {@link ScheduleConfigurationDayModel} is exist in {@link ScheduleConfigurationModel} for given
	 * {@link DayOfWeek}, if no it returns the model from {@link WidgetInstanceManager}
	 *
	 * @param scheduleConfigurationModel
	 * @param dayOfWeek
	 * @param widgetInstanceManager
	 */
	protected ScheduleConfigurationDayModel getScheduleConfigDay(final ScheduleConfigurationModel scheduleConfigurationModel,
			final DayOfWeek dayOfWeek, final WidgetInstanceManager widgetInstanceManager)
	{
		ScheduleConfigurationDayModel scheduleConfigurationDayModel = null;
		final Optional<ScheduleConfigurationDayModel> optional = scheduleConfigurationModel.getScheduleConfigurationDays().stream()
				.filter(scheduleConfigDay -> StringUtils.equalsIgnoreCase(dayOfWeek.getCode(),
						scheduleConfigDay.getDayOfWeek().getCode()))
				.findFirst();
		if (optional.isPresent())
		{
			scheduleConfigurationDayModel = optional.get();
		}
		else
		{
			scheduleConfigurationDayModel = widgetInstanceManager.getModel().getValue(dayOfWeek.getCode(),
					ScheduleConfigurationDayModel.class);
		}
		return scheduleConfigurationDayModel;
	}

	/**
	 * Creates grid layout for {@link ScheduleConfigurationModel} data.
	 *
	 * @param container
	 * @param scheduleConfigurationModel
	 */
	protected void createScheduleConfigLayout(final Div container, final ScheduleConfigurationModel scheduleConfigurationModel)
	{
		final Grid scheduleConfigGrid = new Grid();
		final Column col1 = new Column();
		final Column col2 = new Column();

		final Columns columns = new Columns();
		columns.setParent(scheduleConfigGrid);

		final Rows rows = new Rows();
		rows.setParent(scheduleConfigGrid);

		scheduleConfigGrid.setSizedByContent(true);
		scheduleConfigGrid.getColumns().appendChild(col1);
		scheduleConfigGrid.getColumns().appendChild(col2);

		final Row row1 = new Row();
		row1.appendChild(new Label(getLabelService().getObjectLabel(
				ScheduleConfigurationModel._TYPECODE + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.NUMBER)));
		row1.appendChild(new Label(scheduleConfigurationModel.getNumber()));
		scheduleConfigGrid.getRows().appendChild(row1);

		final Row row2 = new Row();
		row2.appendChild(new Label(getLabelService().getObjectLabel(
				ScheduleConfigurationModel._TYPECODE + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.TRAVELPROVIDER)));
		row2.appendChild(new Label(scheduleConfigurationModel.getTravelProvider().getCode()));
		scheduleConfigGrid.getRows().appendChild(row2);

		final Row row3 = new Row();
		row3.appendChild(new Label(getLabelService().getObjectLabel(
				ScheduleConfigurationModel._TYPECODE + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.STARTDATE)));
		row3.appendChild(
				new Label(TravelDateUtils.convertDateToStringDate(scheduleConfigurationModel.getStartDate(), "dd/MM/yyyy")));
		scheduleConfigGrid.getRows().appendChild(row3);

		final Row row4 = new Row();
		row4.appendChild(new Label(getLabelService().getObjectLabel(
				ScheduleConfigurationModel._TYPECODE + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.ENDDATE)));
		row4.appendChild(new Label(TravelDateUtils.convertDateToStringDate(scheduleConfigurationModel.getEndDate(), "dd/MM/yyyy")));
		scheduleConfigGrid.getRows().appendChild(row4);

		container.appendChild(scheduleConfigGrid);
	}

}
