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

package de.hybris.platform.travelbackoffice.widgets.inventory.renderer;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.renderer.DefaultCustomViewRenderer;


/**
 * This is responsible of rendering the layout of the Step2 of Create Inventory for Booking class.
 */
public abstract class AbstractStockInventoryConfigurationRenderer extends DefaultCustomViewRenderer
{
	private static final Logger LOG = Logger.getLogger(AssignStockInventoryConfigurationRenderer.class);
	private static final String SCHEDULE_PARENT_TITLE = "create.inventory.bookingclass.assigninventory.wizard.schedule.parenttitle";
	private static final String SCHEDULE_CHILD_TITLE = "create.inventory.bookingclass.assigninventory.wizard.schedule.childtitle";

	private static final String TRAVEL_SECTOR_PARENT_TITLE = "create.inventory.bookingclass.assigninventory.wizard.travelsector.parenttitle";
	private static final String TRAVEL_SECTOR_CHILD_TITLE = "create.inventory.bookingclass.assigninventory.wizard.travelsector.childtitle";

	private static final String FLIGHT_PARENT_TITLE = "create.inventory.bookingclass.assigninventory.wizard.flight.parenttitle";
	private static final String FLIGHT_CHILD_TITLE = "create.inventory.bookingclass.assigninventory.wizard.flight.childtitle";

	private static final String BOOKING_CLASS_LABEL = "create.inventory.bookingclass.assigninventory.wizard.bookingclass";
	private static final String AVAILABLE_QUANTITY_LABEL = "create.inventory.bookingclass.assigninventory.wizard.availablequantity";
	private static final String OVERSELLING_QUANTITY_LABEL = "create.inventory.bookingclass.assigninventory.wizard.oversellingquantity";
	private static final String INSTOCK_STATUS_LABEL = "create.inventory.bookingclass.assigninventory.wizard.instockstatus";
	private static final String ERROR_UNKNOWN_MESSAGE = "create.inventory.bookingclass.assigninventory.wizard.error.unknown.message";

	private EventListener<Event> addButtonEventListener;
	private EventListener<Event> removeButtonEventListener;

	@Override
	public void render(final Component parent, final ViewType customView, final Map<String, String> parameters,
			final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
	{
		final Vlayout vlayout = new Vlayout();
		vlayout.setParent(parent);
		Component childComponent = null;
		Label parentLabel;
		final ManageStockLevelInfo manageStockLevel = widgetInstanceManager.getModel().getValue(
				TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID, ManageStockLevelInfo.class);
		final ScheduleConfigurationModel scheduleConfiguration = widgetInstanceManager.getModel().getValue(
				TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_SCHEDULE_CONFIGURATION_ITEM_ID,
				ScheduleConfigurationModel.class);
		if (Objects.isNull(manageStockLevel)
				|| (StringUtils.equals(ScheduleConfigurationModel._TYPECODE, manageStockLevel.getStockItemType())
						&& Objects.isNull(scheduleConfiguration)))
		{
			LOG.error("ManageStockLevelInfo/ScheduleConfigurationModel object is missing. Please close the wizard and try again.");
			parentLabel = new Label(Labels.getLabel(ERROR_UNKNOWN_MESSAGE));
		}
		else
		{
			String parentLabelText = StringUtils.EMPTY;

			switch (manageStockLevel.getStockItemType())
			{
				case TravelSectorModel._TYPECODE:
				{
					parentLabelText = Labels.getLabel(TRAVEL_SECTOR_PARENT_TITLE) + TravelbackofficeConstants.COLON;
					manageStockLevel
							.setTravelSectors(manageStockLevel.getTravelSectors().stream().distinct().collect(Collectors.toList()));
					childComponent = createLayoutForContent(TRAVEL_SECTOR_CHILD_TITLE,
							manageStockLevel.getTravelSectors().stream().map(TravelSectorModel::getName).collect(Collectors.toList()));
					break;
				}
				case TransportOfferingModel._TYPECODE:
				{
					parentLabelText = Labels.getLabel(FLIGHT_PARENT_TITLE) + TravelbackofficeConstants.COLON;
					manageStockLevel.setTransportOfferings(
							manageStockLevel.getTransportOfferings().stream().distinct().collect(Collectors.toList()));
					final List<String> flightInfos = new ArrayList<>(CollectionUtils.size(manageStockLevel.getTransportOfferings()));
					manageStockLevel.getTransportOfferings()
							.forEach(transportOffering -> flightInfos.add(getFlightInfo(transportOffering, widgetInstanceManager)));
					childComponent = createLayoutForContent(FLIGHT_CHILD_TITLE, flightInfos);
					break;
				}
				case ScheduleConfigurationModel._TYPECODE:
				{

					parentLabelText = Labels.getLabel(SCHEDULE_PARENT_TITLE) + TravelbackofficeConstants.COLON;
					final long numberOfFlights = manageStockLevel.getScheduleConfigurations().stream()
							.mapToLong(configuration -> CollectionUtils.size(configuration.getTransportOfferings())).sum();
					final Object[] args =
					{ numberOfFlights, scheduleConfiguration.getTravelProvider().getCode() + scheduleConfiguration.getNumber() };
					childComponent = new Label(Labels.getLabel(SCHEDULE_CHILD_TITLE, args));
					break;
				}
				default:
			}
			parentLabel = new Label(parentLabelText);
		}
		vlayout.appendChild(parentLabel);
		if (Objects.nonNull(childComponent))
		{
			vlayout.appendChild(childComponent);
		}
		vlayout.appendChild(createLayoutForStockLevels(widgetInstanceManager, manageStockLevel));
	}

	/**
	 * Method returns the String by concatenating the travel provider's code, transport offering number and departure
	 * date .
	 *
	 * @param transportOfferingModel
	 * 		the transport offering model
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 *
	 * @return the flight info
	 */
	protected String getFlightInfo(final TransportOfferingModel transportOfferingModel,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Editor editor = new Editor();
		editor.setNestedObjectCreationDisabled(false);
		editor.setWidgetInstanceManager(widgetInstanceManager);
		editor.setType("java.util.Date");
		editor.setInitialValue(transportOfferingModel.getDepartureTime());
		editor.initialize();
		final String departureDate = ((Datebox) editor.getFirstChild().getFirstChild()).getText();
		return transportOfferingModel.getTravelProvider().getCode() + transportOfferingModel.getNumber()
				+ TravelbackofficeConstants.SPACE + departureDate;

	}

	/**
	 * Method creates the layout container that contains the Grid structure.
	 *
	 * @param childLabelText
	 * 		the child label text
	 * @param displayValues
	 * 		the display values
	 *
	 * @return the component
	 */
	protected Component createLayoutForContent(final String childLabelText, final List<String> displayValues)
	{
		final Div container = new Div();
		container.setVisible(true);

		final Grid stockLevelGrid = new Grid();
		final Column col1 = new Column();
		final Column col2 = new Column();

		final Columns stockLevelGridColumns = new Columns();
		stockLevelGridColumns.setParent(stockLevelGrid);

		final Rows stockLevelGridRows = new Rows();
		stockLevelGridRows.setParent(stockLevelGrid);

		stockLevelGrid.setSizedByContent(true);
		stockLevelGrid.getColumns().appendChild(col1);
		stockLevelGrid.getColumns().appendChild(col2);


		displayValues.forEach(displayValue -> stockLevelGrid.getRows().appendChild(
				createContentRow(displayValues.indexOf(displayValue) == 0 ? childLabelText : StringUtils.EMPTY, displayValue)));
		container.appendChild(stockLevelGrid);
		return container;
	}

	/**
	 * Creates an returns an instance of {@link Row} for grid.
	 *
	 * @param firstColumnText
	 * 		the first column text
	 * @param secondColumnText
	 * 		the second column text
	 *
	 * @return row
	 */
	protected Row createContentRow(final String firstColumnText, final String secondColumnText)
	{
		final Row row = new Row();
		final Label firstColumn = createLabel((StringUtils.isEmpty(firstColumnText) ? StringUtils.EMPTY
				: Labels.getLabel(firstColumnText) + TravelbackofficeConstants.COLON), true);
		final Label secondColumn = createLabel(secondColumnText, false);
		row.appendChild(firstColumn);
		row.appendChild(secondColumn);
		return row;
	}

	/**
	 * Method creates layout for StockLevelAttributes in Grid
	 *
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 * @param manageStockLevel
	 * 		the manage stock level
	 *
	 * @return the component
	 */
	protected Component createLayoutForStockLevels(final WidgetInstanceManager widgetInstanceManager,
			final ManageStockLevelInfo manageStockLevel)
	{
		final Div container = new Div();
		container.setVisible(true);

		final Grid stockLevelGrid = new Grid();
		final Column col1 = new Column();
		final Column col2 = new Column();
		final Column col3 = new Column();
		final Column col4 = new Column();
		final Column col5 = new Column();
		final Column col6 = new Column();

		final Columns stockLevelGridColumns = new Columns();
		stockLevelGridColumns.setParent(stockLevelGrid);

		final Rows stockLevelGridRows = new Rows();
		stockLevelGridRows.setParent(stockLevelGrid);

		stockLevelGrid.setClass("manage-inventory-grid");
		stockLevelGrid.setSizedByContent(true);
		stockLevelGrid.getColumns().appendChild(col1);
		stockLevelGrid.getColumns().appendChild(col2);
		stockLevelGrid.getColumns().appendChild(col3);
		stockLevelGrid.getColumns().appendChild(col4);
		stockLevelGrid.getColumns().appendChild(col5);
		stockLevelGrid.getColumns().appendChild(col6);
		stockLevelGrid.setId(TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_PROPERTIES_GRID_ID);
		final Row row1 = new Row();
		row1.setId(String.valueOf(0));
		final Label productLabel = createLabel(Labels.getLabel(BOOKING_CLASS_LABEL) + TravelbackofficeConstants.COLON, true);
		final Label availableQuantityLabel = createLabel(
				Labels.getLabel(AVAILABLE_QUANTITY_LABEL) + TravelbackofficeConstants.COLON, true);
		final Label oversellingLabel = createLabel(Labels.getLabel(OVERSELLING_QUANTITY_LABEL) + TravelbackofficeConstants.COLON,
				true);
		final Label inStockStatusLabel = createLabel(Labels.getLabel(INSTOCK_STATUS_LABEL) + TravelbackofficeConstants.COLON, true);
		row1.appendChild(productLabel);
		row1.appendChild(availableQuantityLabel);
		row1.appendChild(oversellingLabel);
		row1.appendChild(inStockStatusLabel);
		row1.appendChild(new Label());
		row1.appendChild(new Label());

		stockLevelGrid.getRows().appendChild(row1);

		removeButtonEventListener = event -> {
			if (StringUtils.equals(Events.ON_CLICK, event.getName()))
			{
				if (CollectionUtils.size(stockLevelGrid.getRows().getChildren()) > 2)
				{
					final Component removeComponent = event.getTarget().getParent();
					stockLevelGrid.getRows().removeChild(removeComponent);
					final Row lastRow = (Row) stockLevelGrid.getRows().getLastChild();
					if (lastRow.getIndex() > 0)
					{
						lastRow.getChildren().get(4).setVisible(Boolean.TRUE);
					}
					container.appendChild(stockLevelGrid);
					container.getRedrawCallback();
				}
			}
		};
		addButtonEventListener = event -> {
			if (StringUtils.equals(Events.ON_CLICK, event.getName()))
			{
				stockLevelGrid.getRows().appendChild(createStockAttributesColumnLayout(widgetInstanceManager,
						Integer.parseInt(event.getTarget().getId()) + 1, addButtonEventListener, removeButtonEventListener, null));
				container.appendChild(stockLevelGrid);
				event.getTarget().setVisible(Boolean.FALSE);
				container.getRedrawCallback();
			}
		};

		if (CollectionUtils.isNotEmpty(manageStockLevel.getStockLevelAttributes()))
		{
			manageStockLevel.getStockLevelAttributes()
					.forEach(stockLevelAttributes -> stockLevelGrid.getRows().appendChild(createStockAttributesColumnLayout(
							widgetInstanceManager, 1, addButtonEventListener, removeButtonEventListener, stockLevelAttributes)));
		}
		else
		{
			stockLevelGrid.getRows().appendChild(createStockAttributesColumnLayout(widgetInstanceManager, 1,
					addButtonEventListener, removeButtonEventListener, null));
		}
		container.appendChild(stockLevelGrid);
		return container;
	}

	/**
	 * An abstract method to creates column layout for grid used to display the StockLevelAttribute
	 *
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 * @param index
	 * 		the index
	 * @param addButtonlistener
	 * 		the add buttonlistener
	 * @param removeButtonlistener
	 * 		the remove buttonlistener
	 * @param stockLevelAttributes
	 * 		the stock level attributes
	 *
	 * @return row
	 */
	protected abstract Row createStockAttributesColumnLayout(final WidgetInstanceManager widgetInstanceManager,
			final Integer index,
			final EventListener<Event> addButtonlistener, final EventListener<Event> removeButtonlistener,
			final StockLevelAttributes stockLevelAttributes);

	/**
	 * Method creates the {@link Label} for given labelname.
	 *
	 * @param labelName
	 * 		the label name
	 * @param isBold
	 * 		the is bold
	 *
	 * @return the label
	 */
	protected Label createLabel(final String labelName, final boolean isBold)
	{
		final Label label = new Label(labelName);
		if (isBold)
		{
			label.setStyle("font-weight: bold");
		}
		return label;
	}

	/**
	 * Method creates an returns an instance of {@link Editor}
	 *
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 * @param type
	 * 		the type
	 * @param inStockStatus
	 * 		the in stock status
	 * @param nestedObjectCreation
	 * 		the nested object creation
	 * @param property
	 * 		the property
	 *
	 * @return the editor
	 */
	protected Editor createEditor(final WidgetInstanceManager widgetInstanceManager, final String type,
			final InStockStatus inStockStatus, final boolean nestedObjectCreation, final String property)
	{
		final Editor editor = new Editor();
		editor.setNestedObjectCreationDisabled(nestedObjectCreation);
		editor.setWidgetInstanceManager(widgetInstanceManager);
		editor.setType(type);
		if (Objects.nonNull(inStockStatus))
		{
			editor.setInitialValue(inStockStatus);
		}
		editor.setOptional(Boolean.TRUE);
		editor.setProperty(property);
		editor.initialize();
		return editor;
	}

}
