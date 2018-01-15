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
import de.hybris.platform.travelservices.model.travel.TerminalModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.configurableflow.renderer.DefaultCustomViewRenderer;


/**
 * An abstract class to have the common functionality for ScheduleConfiguration wizard.
 */
public abstract class AbstractScheduleConfigurationDayRenderer extends DefaultCustomViewRenderer implements EventListener<Event>
{
	protected static final String CHECKBOX_EDITOR = "com.hybris.cockpitng.editor.boolean.checkbox";
	protected static final String REFERENCE_EDITOR = "com.hybris.cockpitng.editor.defaultreferenceeditor";
	protected static final String TIME_EDITOR = "com.hybris.cockpitng.editor.defaulttime";
	protected static final String DURATION_SPINNER_EDITOR = "de.hybris.platform.travelbackoffice.editor.transportofferingdurationspinner";
	protected static final String TERMINAL_DROPDOWN_EDITOR = "de.hybris.platform.travelbackoffice.editor.terminaldropdown";
	protected static final String REFERENCE_TYPE = "Reference";
	protected static final String BUTTON_DISABLE_CLASS = "disable";

	protected static ScheduleConfigurationDayModel copiedConfiguration;

	private LabelService labelService;

	/**
	 * Creates and return an instance of {@link Row}, it represents the configuration of a calendar day on
	 * ScheduleConfiguration wizard.
	 *
	 * @param isReadOnly
	 * @param scheduleConfigurationModel
	 * @param widgetInstanceManager
	 * @param dayOfWeek
	 * @param isModifiedConfiguration
	 * @param scheduleConfigurationDay
	 */
	protected Row createConfigDayRow(final boolean isReadOnly, final ScheduleConfigurationModel scheduleConfigurationModel,
			final WidgetInstanceManager widgetInstanceManager, final DayOfWeek dayOfWeek, final boolean isModifiedConfiguration,
			final ScheduleConfigurationDayModel scheduleConfigurationDay)
	{
		final Row row = new Row();
		row.setSclass(dayOfWeek.getCode());
		final Button button = new Button(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_COPY_BUTTON_LABEL));
		button.addEventListener(Events.ON_CLICK, event -> handleCopyButtonEvent(event, widgetInstanceManager));

		row.appendChild(button);
		if (isReadOnly)
		{
			button.setDisabled(true);
			button.setSclass(BUTTON_DISABLE_CLASS);
		}
		final Editor checkBoxEditor = createEditor(widgetInstanceManager, Boolean.class.getCanonicalName(), CHECKBOX_EDITOR,
				dayOfWeek.getCode() + "." + ScheduleConfigurationDayModel.SELECTED, Events.ON_CHECK, isReadOnly, null);
		checkBoxEditor.setId(dayOfWeek.getCode() + "." + ScheduleConfigurationDayModel.SELECTED);
		checkBoxEditor.initialize();
		row.appendChild(checkBoxEditor);

		final Label dayLabel = new Label(StringUtils.capitalize(dayOfWeek.getCode()));
		row.appendChild(dayLabel);

		final Editor depTimeEditor = createEditor(widgetInstanceManager, Date.class.getCanonicalName(), TIME_EDITOR,
				dayOfWeek.getCode() + TravelbackofficeConstants.DOT + ScheduleConfigurationDayModel.DEPARTURETIME, Events.ON_CLICK,
				isReadOnly, null);
		depTimeEditor.initialize();
		row.appendChild(depTimeEditor);

		final Editor durationHrEditor = createEditor(widgetInstanceManager, Integer.class.getCanonicalName(),
				DURATION_SPINNER_EDITOR,
				dayOfWeek.getCode() + TravelbackofficeConstants.DOT + ScheduleConfigurationDayModel.DURATIONHRS, Events.ON_CHANGE,
				isReadOnly, null);

		final Map<String, Object> durationHrParams = new HashMap<>();
		durationHrParams.put(TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE_PARAM_NAME,
				TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE);
		durationHrParams.put("constraint", TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_HR_SPINNER_CONSTRAINT);
		durationHrParams.put("isDisabled", isReadOnly);
		durationHrEditor.setParameters(durationHrParams);
		durationHrEditor.initialize();
		row.appendChild(durationHrEditor);

		final Label hrLable = new Label(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_DEPARTURETIME_HR));
		row.appendChild(hrLable);

		final Editor durationMinEditor = createEditor(widgetInstanceManager, Integer.class.getCanonicalName(),
				DURATION_SPINNER_EDITOR,
				dayOfWeek.getCode() + TravelbackofficeConstants.DOT + ScheduleConfigurationDayModel.DURATIONMINS, Events.ON_CHANGE,
				isReadOnly, null);

		final Map<String, Object> durationMinParams = new HashMap<>();
		durationMinParams.put(TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE_PARAM_NAME,
				TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE);
		durationMinParams.put("constraint", TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_MIN_SPINNER_CONSTRAINT);
		durationMinParams.put("isDisabled", isReadOnly);
		durationMinEditor.setParameters(durationMinParams);
		durationMinEditor.initialize();
		row.appendChild(durationMinEditor);

		final Label minLable = new Label(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_DEPARTURETIME_MIN));
		row.appendChild(minLable);

		final Editor originTerminalEditor = createEditor(widgetInstanceManager,
				REFERENCE_TYPE + "(" + TerminalModel._TYPECODE + ")", TERMINAL_DROPDOWN_EDITOR,
				dayOfWeek.getCode() + TravelbackofficeConstants.DOT + ScheduleConfigurationDayModel.ORIGINTERMINAL, Events.ON_OPEN,
				isReadOnly, null);
		final Map<String, Object> originTerminalParam = new HashMap<>();
		originTerminalParam.put("terminals", scheduleConfigurationModel.getTravelSector().getOrigin().getTerminals());
		originTerminalParam.put("isDisabled", isReadOnly);
		originTerminalEditor.setParameters(originTerminalParam);
		originTerminalEditor.initialize();
		row.appendChild(originTerminalEditor);

		final Editor destinationTerminalEditor = createEditor(widgetInstanceManager,
				REFERENCE_TYPE + "(" + TerminalModel._TYPECODE + ")", TERMINAL_DROPDOWN_EDITOR,
				dayOfWeek.getCode() + TravelbackofficeConstants.DOT + ScheduleConfigurationDayModel.DESTINATIONTERMINAL,
				Events.ON_OPEN, isReadOnly, null);

		final Map<String, Object> destinationTerminalParam = new HashMap<>();
		destinationTerminalParam.put("terminals", scheduleConfigurationModel.getTravelSector().getDestination().getTerminals());
		destinationTerminalParam.put("isDisabled", isReadOnly);
		destinationTerminalEditor.setParameters(destinationTerminalParam);
		destinationTerminalEditor.initialize();
		row.appendChild(destinationTerminalEditor);

		final Editor transportVehicleEditor = createEditor(widgetInstanceManager,
				REFERENCE_TYPE + "(" + TransportVehicleModel._TYPECODE + ")", REFERENCE_EDITOR,
				dayOfWeek.getCode() + TravelbackofficeConstants.DOT + ScheduleConfigurationDayModel.TRANSPORTVEHICLE,
				Editor.ON_VALUE_CHANGED, isReadOnly,
				null);
		transportVehicleEditor.initialize();
		row.appendChild(transportVehicleEditor);

		if (isModifiedConfiguration)
		{
			if (Objects.isNull(scheduleConfigurationDay))
			{
				final ScheduleConfigurationDayModel scheduleConfigurationDayModel = new ScheduleConfigurationDayModel();
				scheduleConfigurationDayModel.setDayOfWeek(dayOfWeek);
				widgetInstanceManager.getModel().put(dayOfWeek.getCode(), scheduleConfigurationDayModel);
			}
			else
			{
				final Boolean isChangeSectorConfirmed = widgetInstanceManager.getModel()
						.getValue(TravelbackofficeConstants.IS_CHANGE_SECTOR_CONFIRMED, Boolean.class);
				final Boolean fromPreviewStep = widgetInstanceManager.getModel()
						.getValue(TravelbackofficeConstants.FROM_PREVIOUS_STEP, Boolean.class);

				if (Objects.isNull(fromPreviewStep) && BooleanUtils.isTrue(isChangeSectorConfirmed))
				{
					scheduleConfigurationDay.setOriginTerminal(null);
					scheduleConfigurationDay.setDestinationTerminal(null);
				}
				checkBoxEditor.setInitialValue(scheduleConfigurationDay.getSelected());
				transportVehicleEditor.setInitialValue(scheduleConfigurationDay.getTransportVehicle());
				originTerminalEditor.setInitialValue(scheduleConfigurationDay.getOriginTerminal());
				durationMinEditor.setInitialValue(scheduleConfigurationDay.getDurationMins());
				durationHrEditor.setInitialValue(scheduleConfigurationDay.getDurationHrs());
				depTimeEditor.setInitialValue(scheduleConfigurationDay.getDepartureTime());
				destinationTerminalEditor.setInitialValue(scheduleConfigurationDay.getDestinationTerminal());
				widgetInstanceManager.getModel().put(dayOfWeek.getCode(), scheduleConfigurationDay);
			}
		}
		return row;
	}

	/**
	 * Creates grid layout
	 *
	 * @param container
	 */
	protected Grid createGridLayout(final Div container)
	{
		final Grid scheduleConfigDayGrid = new Grid();

		final Column copyButtonCol = new Column();
		final Column checkboxCol = new Column();
		final Column day = new Column(getLabelService()
				.getObjectLabel(ScheduleConfigurationDayModel._TYPECODE + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationDayModel.DAYOFWEEK));
		final Column depTime = new Column(getLabelService()
				.getObjectLabel(ScheduleConfigurationDayModel._TYPECODE + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationDayModel.DEPARTURETIME));
		final Column duration = new Column(
				Labels.getLabel(TravelbackofficeConstants.SCHEDULE_GRID_DURATION_LABEL));
		final Column durationHrLabel = new Column();
		final Column durationMin = new Column();
		final Column durationMinLabel = new Column();
		final Column orgTerminal = new Column(getLabelService()
				.getObjectLabel(ScheduleConfigurationDayModel._TYPECODE + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationDayModel.ORIGINTERMINAL));
		final Column destTerminal = new Column(getLabelService()
				.getObjectLabel(ScheduleConfigurationDayModel._TYPECODE + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationDayModel.DESTINATIONTERMINAL));
		final Column aircraft = new Column(getLabelService()
				.getObjectLabel(ScheduleConfigurationDayModel._TYPECODE + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationDayModel.TRANSPORTVEHICLE));

		day.setAlign("center");
		depTime.setAlign("center");
		duration.setAlign("right");
		orgTerminal.setAlign("center");
		destTerminal.setAlign("center");
		aircraft.setAlign("center");
		durationHrLabel.setAlign("left");
		durationMinLabel.setAlign("left");

		depTime.setWidth("150");
		duration.setWidth("80px");
		durationMin.setWidth("80px");
		orgTerminal.setWidth("150px");
		destTerminal.setWidth("180px");
		aircraft.setWidth("250px");

		final Columns columns = new Columns();
		columns.setStyle("background-color: #F0F0F0;");
		columns.setParent(scheduleConfigDayGrid);

		final Rows rows = new Rows();
		rows.setParent(scheduleConfigDayGrid);

		scheduleConfigDayGrid.setSizedByContent(true);
		scheduleConfigDayGrid.getColumns().appendChild(copyButtonCol);
		scheduleConfigDayGrid.getColumns().appendChild(checkboxCol);
		scheduleConfigDayGrid.getColumns().appendChild(day);
		scheduleConfigDayGrid.getColumns().appendChild(depTime);
		scheduleConfigDayGrid.getColumns().appendChild(duration);
		scheduleConfigDayGrid.getColumns().appendChild(durationHrLabel);
		scheduleConfigDayGrid.getColumns().appendChild(durationMin);
		scheduleConfigDayGrid.getColumns().appendChild(durationMinLabel);
		scheduleConfigDayGrid.getColumns().appendChild(orgTerminal);
		scheduleConfigDayGrid.getColumns().appendChild(destTerminal);
		scheduleConfigDayGrid.getColumns().appendChild(aircraft);

		return scheduleConfigDayGrid;
	}

	/**
	 * Method that handles event associated to {@link ScheduleConfigurationDayModel}'s fields and make checkbox
	 * autoselected once user start typing/selecting corresponding field.
	 */
	@Override
	public void onEvent(final Event event) throws Exception
	{
		final String editorProperty = ((Editor) event.getTarget()).getProperty();
		if (StringUtils.isNotBlank(editorProperty) && editorProperty.contains(TravelbackofficeConstants.DOT))
		{
			final String field = editorProperty.split("\\.")[0];
			if (Events.ON_CLICK.equals(event.getName()) || Editor.ON_VALUE_CHANGED.equals(event.getName()))
			{
				final Optional<Component> optional = ((Editor) event.getTarget()).getParent().getChildren().stream()
						.filter(component -> component instanceof Editor)
						.filter(component -> StringUtils.contains(((Editor) component).getId(), ScheduleConfigurationDayModel.SELECTED))
						.findFirst();
				if (optional.isPresent())
				{
					((Checkbox) ((Editor) optional.get()).getFirstChild()).setChecked(true);
				}
				((Editor) event.getTarget()).getWidgetInstanceManager().getModel()
						.getValue(field, ScheduleConfigurationDayModel.class).setSelected(Boolean.TRUE);

				final Row row = (Row) ((Editor) event.getTarget()).getParent();
				if (row.getFirstChild() instanceof Button)
				{
					((Button) row.getFirstChild())
							.setLabel(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_COPY_BUTTON_LABEL));
				}
			}
		}
	}

	/**
	 * Method populates copied {@link ScheduleConfigurationDayModel} values to target row.
	 *
	 * @param components
	 * @param targetScheduleConfiguration
	 */
	protected void populateValueToEditor(final List<Component> components,
			final ScheduleConfigurationDayModel targetScheduleConfiguration)
	{
		components.forEach(component -> {
			if (component instanceof Editor)
			{
				final Editor editor = (Editor) component;
				switch (editor.getProperty().split("\\" + TravelbackofficeConstants.DOT)[1])
				{
					case ScheduleConfigurationDayModel.SELECTED:
						targetScheduleConfiguration.setSelected(Boolean.TRUE);
						editor.setValue(true);
						break;

					case ScheduleConfigurationDayModel.DURATIONHRS:
						targetScheduleConfiguration.setDurationHrs(copiedConfiguration.getDurationHrs());
						editor.setValue(copiedConfiguration.getDurationHrs());
						break;

					case ScheduleConfigurationDayModel.DURATIONMINS:
						targetScheduleConfiguration.setDurationMins(copiedConfiguration.getDurationMins());
						editor.setValue(copiedConfiguration.getDurationMins());
						break;

					case ScheduleConfigurationDayModel.DEPARTURETIME:
						targetScheduleConfiguration.setDepartureTime(copiedConfiguration.getDepartureTime());
						editor.setValue(copiedConfiguration.getDepartureTime());
						break;

					case ScheduleConfigurationDayModel.ORIGINTERMINAL:
						targetScheduleConfiguration.setOriginTerminal(copiedConfiguration.getOriginTerminal());
						editor.setValue(copiedConfiguration.getOriginTerminal());
						break;

					case ScheduleConfigurationDayModel.DESTINATIONTERMINAL:
						targetScheduleConfiguration.setDestinationTerminal(copiedConfiguration.getDestinationTerminal());
						editor.setValue(copiedConfiguration.getDestinationTerminal());
						break;

					case ScheduleConfigurationDayModel.TRANSPORTVEHICLE:
						targetScheduleConfiguration.setTransportVehicle(copiedConfiguration.getTransportVehicle());
						editor.setValue(copiedConfiguration.getTransportVehicle());
						break;

					default:
						break;
				}
			}
		});
	}

	/**
	 * Method that create editor according to given parameter
	 *
	 * @param widgetInstanceManager
	 * @param type
	 * @param defaultEditor
	 * @param property
	 * @param event
	 */
	protected Editor createEditor(final WidgetInstanceManager widgetInstanceManager, final String type, final String defaultEditor,
			final String property, final String event, final boolean isReadOnly,
			final ScheduleConfigurationDayModel scheduleConfigurationDay)
	{
		final Editor editor = new Editor();
		editor.setReadOnly(isReadOnly);
		if (!isReadOnly && StringUtils.isNotEmpty(event))
		{
			editor.addEventListener(event, this);
		}
		if (isReadOnly && Objects.nonNull(scheduleConfigurationDay))
		{
			scheduleConfigurationDay.setSelected(Boolean.FALSE);
			scheduleConfigurationDay.setDepartureTime(null);
			scheduleConfigurationDay.setDurationHrs(null);
			scheduleConfigurationDay.setDurationMins(null);
			scheduleConfigurationDay.setTransportVehicle(null);
			scheduleConfigurationDay.setOriginTerminal(null);
			scheduleConfigurationDay.setDestinationTerminal(null);
		}
		editor.setNestedObjectCreationDisabled(false);
		editor.setWidgetInstanceManager(widgetInstanceManager);
		editor.setType(type);

		editor.setDefaultEditor(defaultEditor);
		editor.setProperty(property);
		return editor;
	}

	/**
	 * Method handles the copy/paste event on backoffice's ScheduleConfiguration grid.
	 *
	 * @param event
	 * @param widgetInstanceManager
	 */
	protected void handleCopyButtonEvent(final Event event, final WidgetInstanceManager widgetInstanceManager)
	{
		final Button targetButton = (Button) event.getTarget();
		final Row targetRow = (Row) targetButton.getParent();
		if (StringUtils.equalsIgnoreCase(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_COPY_BUTTON_LABEL),
				targetButton.getLabel()))
		{
			copiedConfiguration = widgetInstanceManager.getModel().getValue(targetRow.getSclass(),
					ScheduleConfigurationDayModel.class);
			final List<Component> children = ((Rows) targetRow.getParent()).getChildren();
			children.stream()
					.filter(component -> !StringUtils.equalsIgnoreCase(((Row) component).getSclass(), targetRow.getSclass()))
					.filter(component -> !StringUtils.equalsIgnoreCase(((Button) ((Row) component).getFirstChild()).getSclass(),
							BUTTON_DISABLE_CLASS))
					.forEach(component -> {
						((Button) ((Row) component).getFirstChild())
								.setLabel(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_PASTE_BUTTON_LABEL));
					});
		}
		else if (StringUtils.equalsIgnoreCase(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_PASTE_BUTTON_LABEL),
				targetButton.getLabel()))
		{
			final ScheduleConfigurationDayModel targetScheduleConfiguration = widgetInstanceManager.getModel()
					.getValue(targetRow.getSclass(), ScheduleConfigurationDayModel.class);
			targetButton.setLabel(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_COPY_BUTTON_LABEL));
			populateValueToEditor(targetRow.getChildren(), targetScheduleConfiguration);
		}
	}

	/**
	 * Returns set of weekdays between the range of start and end date.
	 *
	 * @param scheduleConfiguration
	 */
	protected Set<String> getAllValidDays(final ScheduleConfigurationModel scheduleConfiguration)
	{
		final LocalDate startDate = scheduleConfiguration.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate endDate = scheduleConfiguration.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return Stream.iterate(startDate, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
				.map(date -> date.getDayOfWeek().name()).collect(Collectors.toSet());
	}

	protected LabelService getLabelService()
	{
		return labelService;
	}

	@Required
	public void setLabelService(final LabelService labelService)
	{
		this.labelService = labelService;
	}
}
