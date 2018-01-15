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

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackofficeservices.services.BackofficeScheduleConfigurationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.BooleanUtils;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vlayout;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.editor.defaultdate.DefaultDateEditor;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.renderer.DefaultCustomViewRenderer;


/**
 * Custom view renderer class for "Modify Schedule" wizard's first step, it creates view for schedule configuration
 * search.
 */
public class ModifyScheduleConfigurationSearchRenderer extends DefaultCustomViewRenderer
{
	private static final String REFERENCE_TYPE = "Reference";
	private static final String REFERENCE_EDITOR = "com.hybris.cockpitng.editor.defaultreferenceeditor";
	private static final String TEXT_EDITOR = "com.hybris.cockpitng.editor.defaulttext";
	private static final String DATE_EDITOR = "com.hybris.cockpitng.editor.defaultdate";
	private static final String TRAVEL_PROVIDER_LABEL = "type.ScheduleConfiguration.travelProvider.name";
	private static final String NUMBER = "type.ScheduleConfiguration.number.name";
	private static final String START_DATE = "type.ScheduleConfiguration.startDate.name";
	private static final String END_DATE = "type.ScheduleConfiguration.endDate.name";
	private static final String SEARCH = "type.ScheduleConfiguration.searchButton.name";
	private static final String TRAVEL_SECTOR_LABEL = "type.ScheduleConfiguration.travelSector.name";
	private static final String CHANGE_SECTOR_CONFIRM_LABEL = "type.ScheduleConfiguration.changeSectorConfirm.label";
	private static final String NUMBER_OF_SCHEDULE_CONFIGS_LABEL = "type.ScheduleConfiguration.numberOfScheduleConfigsFound.label";
	private static final String MODIFYSCHEDULE_WIZARD_SEARCHRESULTS_PAGESIZE = "travelbackoffice.modifyschedule.wizard.searchresults.pagesize";
	private static final String GRID_WITH_NOBORDER = "gridWithNoBorder";
	private static final String SEARCH_FIELD_ALIGN_RIGHT = "searchFieldAlignRight";
	private static final String GMT = "GMT";


	private BackofficeScheduleConfigurationService backofficeScheduleConfigurationService;
	private ConfigurationService configurationService;

	@Override
	public void render(final Component parent, final ViewType customView, final Map<String, String> parameters,
			final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
	{
		final Vlayout searchFieldsLayout = new Vlayout();
		searchFieldsLayout.setParent(parent);
		final Separator separator = new Separator();
		separator.setSpacing("20px");
		separator.setParent(parent);
		final Vlayout searchResultsVLayout = new Vlayout();
		searchResultsVLayout.setParent(parent);

		final List<ScheduleConfigurationModel> scheduleConfigurations = widgetInstanceManager.getModel()
				.getValue(TravelbackofficeConstants.SCHEDULE_CONFIGURATIONS, List.class);
		widgetInstanceManager.getModel().put(TravelbackofficeConstants.FROM_PREVIOUS_STEP, null);
		if (CollectionUtils.isNotEmpty(scheduleConfigurations))
		{
			final Component searchResultsLayout = createSearchResultsLayout(scheduleConfigurations, widgetInstanceManager);
			searchResultsVLayout.appendChild(searchResultsLayout);
		}

		searchFieldsLayout.appendChild(createLayout(widgetInstanceManager, searchResultsVLayout));
	}

	/**
	 * Creates layout.
	 *
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 * @param searchResultsVLayout
	 *           the search results v layout
	 *
	 * @return the component
	 */
	protected Component createLayout(final WidgetInstanceManager widgetInstanceManager, final Vlayout searchResultsVLayout)
	{
		final Div container = new Div();
		appendSearchFields(container, widgetInstanceManager, searchResultsVLayout);
		return container;
	}

	/**
	 * Creates the search fields.
	 *
	 * @param container
	 *           the container
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 * @param searchResultsVLayout
	 *           the search results v layout
	 */
	protected void appendSearchFields(final Div container, final WidgetInstanceManager widgetInstanceManager,
			final Vlayout searchResultsVLayout)
	{
		final Grid grid = new Grid();
		grid.setSclass(GRID_WITH_NOBORDER);

		final Columns columns = new Columns();
		final Column column1 = new Column();
		column1.setWidth("20%");
		columns.appendChild(column1);
		final Column column2 = new Column();
		column2.setWidth("80%");
		columns.appendChild(column2);
		columns.setParent(grid);

		final Rows rows = new Rows();
		final Row travelProviderRow = new Row();
		final Label travelProviderLabel = new Label(Labels.getLabel(TRAVEL_PROVIDER_LABEL) + TravelbackofficeConstants.COLON);
		final Editor travelProviderEditor = createEditor(widgetInstanceManager,
				REFERENCE_TYPE + "(" + TravelProviderModel._TYPECODE + ")", REFERENCE_EDITOR,
				TravelbackofficeConstants.SEARCH_SCHEDULE_INFO + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationModel.TRAVELPROVIDER,
				Boolean.TRUE);
		travelProviderRow.appendChild(travelProviderLabel);
		travelProviderRow.appendChild(travelProviderEditor);
		rows.appendChild(travelProviderRow);

		final Row numberRow = new Row();
		final Label numberLabel = new Label(Labels.getLabel(NUMBER) + TravelbackofficeConstants.COLON);
		final Editor numberEditor = createEditor(widgetInstanceManager, "", TEXT_EDITOR,
				TravelbackofficeConstants.SEARCH_SCHEDULE_INFO + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.NUMBER,
				Boolean.TRUE);
		numberRow.appendChild(numberLabel);
		numberRow.appendChild(numberEditor);
		rows.appendChild(numberRow);

		final String defaultTimeZoneInGMTFormat = GMT + ZonedDateTime.now().getOffset();
		final Row startDateRow = new Row();
		final Label startDateLabel = new Label(Labels.getLabel(START_DATE) + TravelbackofficeConstants.COLON);
		startDateRow.appendChild(startDateLabel);
		final Editor startDateEditor = createEditor(widgetInstanceManager, Date.class.getCanonicalName(), DATE_EDITOR,
				TravelbackofficeConstants.SEARCH_SCHEDULE_INFO + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.STARTDATE,
				Boolean.FALSE);
		startDateRow.appendChild(startDateEditor);
		startDateEditor.addParameter(DefaultDateEditor.SELECTED_TIME_ZONE, defaultTimeZoneInGMTFormat);
		startDateEditor.initialize();
		rows.appendChild(startDateRow);

		final Row endDateRow = new Row();
		final Label endDateLabel = new Label(Labels.getLabel(END_DATE) + TravelbackofficeConstants.COLON);
		endDateRow.appendChild(endDateLabel);
		final Editor endDateEditor = createEditor(widgetInstanceManager, Date.class.getCanonicalName(), DATE_EDITOR,
				TravelbackofficeConstants.SEARCH_SCHEDULE_INFO + TravelbackofficeConstants.DOT + ScheduleConfigurationModel.ENDDATE,
				Boolean.FALSE);
		endDateEditor.addParameter(DefaultDateEditor.SELECTED_TIME_ZONE, defaultTimeZoneInGMTFormat);
		endDateEditor.initialize();
		endDateRow.appendChild(endDateEditor);
		rows.appendChild(endDateRow);

		rows.setParent(grid);
		container.appendChild(grid);

		final Button searchButton = new Button();
		searchButton.setSclass(SEARCH_FIELD_ALIGN_RIGHT);
		searchButton.setLabel(Labels.getLabel(SEARCH));
		final EventListener<Event> searchButtonListener = getSearchButtonListener(travelProviderEditor, numberEditor,
				startDateEditor, endDateEditor, widgetInstanceManager, searchResultsVLayout);
		searchButton.addEventListener(Events.ON_CLICK, searchButtonListener);

		container.appendChild(searchButton);
	}

	/**
	 * Gets the search button listener.
	 *
	 * @param travelProviderEditor
	 *           the travel provider editor
	 * @param numberEditor
	 *           the number editor
	 * @param startDateEditor
	 *           the start date editor
	 * @param endDateEditor
	 *           the end date editor
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 * @param searchResultsVLayout
	 *           the search results v layout
	 *
	 * @return the search button listener
	 */
	protected EventListener<Event> getSearchButtonListener(final Editor travelProviderEditor, final Editor numberEditor,
			final Editor startDateEditor, final Editor endDateEditor, final WidgetInstanceManager widgetInstanceManager,
			final Vlayout searchResultsVLayout)
	{
		return event -> {
			if (Objects.nonNull(travelProviderEditor.getValue()) && Objects.nonNull(numberEditor.getValue()))
			{
				final TravelProviderModel travelProvider = (TravelProviderModel) travelProviderEditor.getValue();
				final String number = (String) numberEditor.getValue();
				Date startDate = null;
				Date endDate = null;
				if (Objects.nonNull(startDateEditor.getValue()))
				{
					startDate = DateUtils.truncate(startDateEditor.getValue(), Calendar.DATE);
				}
				if (Objects.nonNull(endDateEditor.getValue()))
				{
					endDate = DateUtils.truncate(endDateEditor.getValue(), Calendar.DATE);
				}

				final List<ScheduleConfigurationModel> scheduleConfigurations = getBackofficeScheduleConfigurationService()
						.getScheduleConfigurationModel(number, travelProvider, startDate, endDate);

				if (CollectionUtils.isEmpty(scheduleConfigurations))
				{
					Messagebox.show(Labels.getLabel(TravelbackofficeConstants.MODIFY_SCHEDULE_NOT_AVAILABLE),
							Labels.getLabel(TravelbackofficeConstants.ERROR_TITLE), new Messagebox.Button[]
					{ Messagebox.Button.OK }, null, null);
					return;
				}

				widgetInstanceManager.getModel().put(TravelbackofficeConstants.SCHEDULE_CONFIGURATIONS, scheduleConfigurations);
				widgetInstanceManager.getModel().put(TravelbackofficeConstants.ITEM, null);
				widgetInstanceManager.getModel().put(TravelbackofficeConstants.IS_CHANGE_SECTOR_CONFIRMED, null);

				final Component searchResultsLayout = createSearchResultsLayout(scheduleConfigurations, widgetInstanceManager);
				searchResultsVLayout.getChildren().stream().findFirst().ifPresent(searchResultsVLayout::removeChild);
				searchResultsVLayout.appendChild(searchResultsLayout);
			}
			else
			{
				Messagebox.show(Labels.getLabel(TravelbackofficeConstants.MODIFY_SCHEDULE_MISSING_MANDATORY_FIELDS),
						Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Messagebox.Button[]
				{ Messagebox.Button.OK }, null, null);
			}
		};
	}

	/**
	 * Creates the editor.
	 *
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 * @param type
	 *           the type
	 * @param defaultEditor
	 *           the default editor
	 * @param property
	 *           the property
	 *
	 * @param doInit
	 * @return the editor
	 */
	protected Editor createEditor(final WidgetInstanceManager widgetInstanceManager, final String type, final String defaultEditor,
			final String property, final boolean doInit)
	{
		final Editor editor = new Editor();
		editor.setNestedObjectCreationDisabled(false);
		editor.setWidgetInstanceManager(widgetInstanceManager);
		editor.setType(type);
		editor.setProperty(property);
		editor.setDefaultEditor(defaultEditor);
		if (doInit)
		{
			editor.initialize();
		}
		return editor;
	}

	/**
	 * Creates the search results layout.
	 *
	 * @param scheduleConfigurations
	 *           the schedule configurations
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 *
	 * @return the component
	 */
	protected Component createSearchResultsLayout(final List<ScheduleConfigurationModel> scheduleConfigurations,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Div searchResultsLayout = new Div();
		final Object[] args =
		{ CollectionUtils.size(scheduleConfigurations) };
		final Label numberOfScheduleConfigsFoundLabel = new Label(
				Labels.getLabel(NUMBER_OF_SCHEDULE_CONFIGS_LABEL, args) + TravelbackofficeConstants.COLON);
		searchResultsLayout.appendChild(numberOfScheduleConfigsFoundLabel);
		searchResultsLayout.appendChild(createRadiogroup(scheduleConfigurations, widgetInstanceManager));
		final Separator separator = new Separator();
		separator.setSpacing("20px");
		searchResultsLayout.appendChild(separator);
		searchResultsLayout.appendChild(createChangeSectorConfirmCheck(widgetInstanceManager));
		return searchResultsLayout;
	}

	/**
	 * Creates the radiogroup.
	 *
	 * @param scheduleConfigurations
	 *           the schedule configurations
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 *
	 * @return the radiogroup
	 */
	protected Radiogroup createRadiogroup(final List<ScheduleConfigurationModel> scheduleConfigurations,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Radiogroup radioGroup = new Radiogroup();
		radioGroup.addEventListener(Events.ON_CHECK, event -> {
			final ScheduleConfigurationModel scheduleConfiguration = ((Radio) event.getTarget()).getValue();
			widgetInstanceManager.getModel().put(TravelbackofficeConstants.ITEM, scheduleConfiguration);
		});
		radioGroup.appendChild(createSearchResultsGrid(scheduleConfigurations, widgetInstanceManager));
		return radioGroup;
	}

	/**
	 * Creates the search results grid.
	 *
	 * @param scheduleConfigurations
	 *           the schedule configurations
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 *
	 * @return the grid
	 */
	protected Grid createSearchResultsGrid(final List<ScheduleConfigurationModel> scheduleConfigurations,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Grid grid = new Grid();
		grid.setMold("paging");
		grid.setPageSize(getConfigurationService().getConfiguration().getInt(MODIFYSCHEDULE_WIZARD_SEARCHRESULTS_PAGESIZE));
		createSearchResults(grid, scheduleConfigurations, widgetInstanceManager);
		return grid;
	}

	/**
	 * Creates the search results.
	 *
	 * @param grid
	 *           the grid
	 * @param scheduleConfigurations
	 *           the schedule configurations
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 */
	protected void createSearchResults(final Grid grid, final List<ScheduleConfigurationModel> scheduleConfigurations,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Columns columns = new Columns();

		final Column travelProviderCol = new Column(Labels.getLabel(TRAVEL_PROVIDER_LABEL));
		final Column numberCol = new Column(Labels.getLabel(NUMBER));
		final Column startDateCol = new Column(Labels.getLabel(START_DATE));
		final Column endDateCol = new Column(Labels.getLabel(END_DATE));
		final Column sectorLabel = new Column(Labels.getLabel(TRAVEL_SECTOR_LABEL));

		columns.appendChild(new Column());
		columns.appendChild(travelProviderCol);
		columns.appendChild(numberCol);
		columns.appendChild(startDateCol);
		columns.appendChild(endDateCol);
		columns.appendChild(sectorLabel);
		columns.setParent(grid);

		final Rows rows = new Rows();

		final ScheduleConfigurationModel selectedScheduleConfiguration = widgetInstanceManager.getModel()
				.getValue(TravelbackofficeConstants.ITEM, ScheduleConfigurationModel.class);

		for (final ScheduleConfigurationModel scheduleConfiguration : scheduleConfigurations)
		{
			final Row row = new Row();

			final Radio radio = new Radio();
			radio.setValue(scheduleConfiguration);
			if (Objects.equals(selectedScheduleConfiguration, scheduleConfiguration))
			{
				radio.setChecked(true);
				final TravelSectorModel savedTravelSector = widgetInstanceManager.getModel()
						.getValue(TravelbackofficeConstants.SAVED_TRAVEL_SECTOR, TravelSectorModel.class);
				if (Objects.nonNull(savedTravelSector))
				{
					scheduleConfiguration.setTravelSector(savedTravelSector);
				}
			}
			row.appendChild(radio);

			final Label travelProviderVal = new Label(scheduleConfiguration.getTravelProvider().getCode());
			row.appendChild(travelProviderVal);

			final Label numberVal = new Label(scheduleConfiguration.getNumber());
			row.appendChild(numberVal);

			final Label startDateVal = new Label(TravelDateUtils.convertDateToStringDate(scheduleConfiguration.getStartDate(),
					TravelservicesConstants.DATE_PATTERN));
			row.appendChild(startDateVal);

			final Label endDateVal = new Label(
					TravelDateUtils.convertDateToStringDate(scheduleConfiguration.getEndDate(), TravelservicesConstants.DATE_PATTERN));
			row.appendChild(endDateVal);

			final Label travelSectorVal = new Label(scheduleConfiguration.getTravelSector().getCode());
			row.appendChild(travelSectorVal);

			rows.appendChild(row);
		}
		rows.setParent(grid);
	}

	/**
	 * Creates the change sector confirm check.
	 *
	 * @param widgetInstanceManager
	 *           the widget instance manager
	 *
	 * @return the component
	 */
	protected Component createChangeSectorConfirmCheck(final WidgetInstanceManager widgetInstanceManager)
	{
		final Div changeSectorDiv = new Div();

		final Checkbox changeSectorConfirmCheck = new Checkbox(Labels.getLabel(CHANGE_SECTOR_CONFIRM_LABEL));
		final Boolean isChangeSectorConfirmed = widgetInstanceManager.getModel()
				.getValue(TravelbackofficeConstants.IS_CHANGE_SECTOR_CONFIRMED, Boolean.class);
		changeSectorConfirmCheck.setChecked(BooleanUtils.isTrue(isChangeSectorConfirmed));
		changeSectorDiv.appendChild(changeSectorConfirmCheck);

		final Grid travelSectorGrid = new Grid();
		travelSectorGrid.setSclass("gridWithNoBorder");
		final Columns columns = new Columns();
		final Column column1 = new Column();
		column1.setWidth("20%");
		columns.appendChild(column1);
		final Column column2 = new Column();
		column2.setWidth("80%");
		columns.appendChild(column2);
		columns.setParent(travelSectorGrid);

		final Rows rows = new Rows();
		final Row travelSectorRow = new Row();
		final Label travelSectorLabel = new Label(Labels.getLabel(TRAVEL_SECTOR_LABEL) + TravelbackofficeConstants.COLON);
		final Editor travelSectorEditor = createEditor(widgetInstanceManager,
				REFERENCE_TYPE + "(" + TravelSectorModel._TYPECODE + ")", REFERENCE_EDITOR,
				TravelbackofficeConstants.SEARCH_SCHEDULE_INFO + TravelbackofficeConstants.DOT
						+ ScheduleConfigurationModel.TRAVELSECTOR,
				Boolean.TRUE);

		travelSectorRow.appendChild(travelSectorLabel);
		travelSectorRow.appendChild(travelSectorEditor);
		travelSectorRow.setVisible(changeSectorConfirmCheck.isChecked());
		rows.appendChild(travelSectorRow);
		rows.setParent(travelSectorGrid);
		changeSectorDiv.appendChild(travelSectorGrid);

		changeSectorConfirmCheck.addEventListener(Events.ON_CHECK, event -> {
			travelSectorRow.setVisible(changeSectorConfirmCheck.isChecked());
			widgetInstanceManager.getModel().put(TravelbackofficeConstants.IS_CHANGE_SECTOR_CONFIRMED,
					changeSectorConfirmCheck.isChecked());
		});
		return changeSectorDiv;
	}

	/**
	 * Gets backoffice schedule configuration service.
	 *
	 * @return the backofficeScheduleConfigurationService
	 */
	protected BackofficeScheduleConfigurationService getBackofficeScheduleConfigurationService()
	{
		return backofficeScheduleConfigurationService;
	}

	/**
	 * Sets backoffice schedule configuration service.
	 *
	 * @param backofficeScheduleConfigurationService
	 *           the backofficeScheduleConfigurationService to set
	 */
	@Required
	public void setBackofficeScheduleConfigurationService(
			final BackofficeScheduleConfigurationService backofficeScheduleConfigurationService)
	{
		this.backofficeScheduleConfigurationService = backofficeScheduleConfigurationService;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
