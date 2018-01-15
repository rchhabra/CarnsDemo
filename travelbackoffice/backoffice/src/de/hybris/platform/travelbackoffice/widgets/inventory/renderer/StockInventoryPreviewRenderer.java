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
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.configurableflow.renderer.DefaultCustomViewRenderer;


/**
 * Creates the view of the Preview page for Create Inventory for Booking Class.
 */
public class StockInventoryPreviewRenderer extends DefaultCustomViewRenderer
{

	private static final String PARENT_CONTENT = "create.inventory.bookingclass.preview.wizard.content.parenttitle";
	private static final String CHILD_CONTENT_SCHEDULE = "create.inventory.bookingclass.preview.wizard.content.childtitle.schedule";
	private static final String CHILD_CONTENT_TRAVEL_SECTOR = "create.inventory.bookingclass.preview.wizard.content.childtitle.travelsector";
	private static final String CHILD_CONTENT_FLIGHT = "create.inventory.bookingclass.preview.wizard.content.childtitle.flight";
	private static final String INSTOCK_STATUS_NA_LABEL = "create.inventory.bookingclass.preview.wizard.instockstatus.na";

	private LabelService labelService;

	@Override
	public void render(final Component parent, final ViewType customView, final Map<String, String> parameters,
			final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
	{
		Component childContent = null;
		final ManageStockLevelInfo manageStockLevel = widgetInstanceManager.getModel().getValue(
				TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID, ManageStockLevelInfo.class);



		switch (manageStockLevel.getStockItemType())
		{
			case TravelSectorModel._TYPECODE:
			{
				childContent = createLayoutForContent(CHILD_CONTENT_TRAVEL_SECTOR,
						manageStockLevel.getTravelSectors().stream().map(TravelSectorModel::getName).collect(Collectors.toList()));
				break;
			}
			case TransportOfferingModel._TYPECODE:
			{
				final List<String> flightInfos = new ArrayList<>(CollectionUtils.size(manageStockLevel.getTransportOfferings()));
				manageStockLevel.getTransportOfferings()
						.forEach(transportOffering -> flightInfos.add(getFlightInfo(transportOffering, widgetInstanceManager)));
				childContent = createLayoutForContent(CHILD_CONTENT_FLIGHT, flightInfos);
				break;
			}

			case ScheduleConfigurationModel._TYPECODE:
			{
				final ScheduleConfigurationModel obj = widgetInstanceManager.getModel().getValue(
						TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_SCHEDULE_CONFIGURATION_ITEM_ID,
						ScheduleConfigurationModel.class);
				childContent = createLayoutForContent(CHILD_CONTENT_SCHEDULE,
						Stream.of(obj.getTravelProvider().getCode() + obj.getNumber()).collect(Collectors.toList()));
				break;
			}
			default:
		}
		final Vlayout vlayout = new Vlayout();
		vlayout.setParent(parent);
		final Label label = new Label(Labels.getLabel(PARENT_CONTENT));
		vlayout.appendChild(label);
		vlayout.appendChild(childContent);
		vlayout.appendChild(createLayout(widgetInstanceManager));
	}

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

	protected Component createLayoutForContent(final String childLabelText, final List<String> displayValues)
	{
		final Div container = new Div();
		container.setVisible(true);

		final Grid scheduleConfigGrid = new Grid();
		final Column col1 = new Column();
		final Column col2 = new Column();

		final Columns scheduleConfigGridColumns = new Columns();
		scheduleConfigGridColumns.setParent(scheduleConfigGrid);

		final Rows scheduleConfigGridRows = new Rows();
		scheduleConfigGridRows.setParent(scheduleConfigGrid);
		scheduleConfigGrid.setSizedByContent(true);
		scheduleConfigGrid.getColumns().appendChild(col1);
		scheduleConfigGrid.getColumns().appendChild(col2);
		displayValues.forEach(displayValue -> scheduleConfigGrid.getRows().appendChild(
				createContentRow(displayValues.indexOf(displayValue) == 0 ? childLabelText : StringUtils.EMPTY, displayValue)));
		container.appendChild(scheduleConfigGrid);
		return container;
	}

	protected Row createContentRow(final String firstColumnText, final String secondColumnText)
	{
		final Row row = new Row();
		final Label firstColumn = createLabel((StringUtils.isEmpty(firstColumnText) ? StringUtils.EMPTY
				: Labels.getLabel(firstColumnText) + TravelbackofficeConstants.COLON + " "), true);
		final Label secondColumn = createLabel(secondColumnText, false);
		row.appendChild(firstColumn);
		row.appendChild(secondColumn);
		return row;
	}

	/**
	 * Method creates layout for GridLayout for StockLevel Attributes
	 */
	private Component createLayout(final WidgetInstanceManager widgetInstanceManager)
	{
		final Div container = new Div();
		container.setVisible(true);

		final Grid scheduleConfigGrid = new Grid();
		final Column col1 = new Column();
		final Column col2 = new Column();
		final Column col3 = new Column();
		final Column col4 = new Column();

		final Columns scheduleConfigGridColumns = new Columns();
		scheduleConfigGridColumns.setParent(scheduleConfigGrid);

		final Rows scheduleConfigGridRows = new Rows();
		scheduleConfigGridRows.setParent(scheduleConfigGrid);

		scheduleConfigGrid.setSizedByContent(true);
		scheduleConfigGrid.getColumns().appendChild(col1);
		scheduleConfigGrid.getColumns().appendChild(col2);
		scheduleConfigGrid.getColumns().appendChild(col3);
		scheduleConfigGrid.getColumns().appendChild(col4);

		final Row row1 = new Row();
		row1.appendChild(
				createLabel(Labels.getLabel(TravelbackofficeConstants.BOOKING_CLASS_LABEL) + TravelbackofficeConstants.COLON, true));
		row1.appendChild(createLabel(
				Labels.getLabel(TravelbackofficeConstants.AVAILABLE_QUANTITY_LABEL) + TravelbackofficeConstants.COLON, true));
		row1.appendChild(createLabel(
				Labels.getLabel(TravelbackofficeConstants.OVERSELLING_QUANTITY_LABEL) + TravelbackofficeConstants.COLON, true));
		row1.appendChild(
				createLabel(Labels.getLabel(TravelbackofficeConstants.INSTOCK_STATUS_LABEL) + TravelbackofficeConstants.COLON, true));

		scheduleConfigGrid.getRows().appendChild(row1);
		final ManageStockLevelInfo manageStockLevel = widgetInstanceManager.getModel().getValue(
				TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID, ManageStockLevelInfo.class);
		manageStockLevel.getStockLevelAttributes().forEach(
				stockAttribute -> scheduleConfigGrid.getRows().appendChild(createStockAttributesColumnLayout(stockAttribute)));
		container.appendChild(scheduleConfigGrid);
		return container;
	}

	protected Row createStockAttributesColumnLayout(final StockLevelAttributes stockLevelAttribute)
	{
		final Row row = new Row();
		row.appendChild(createLabel(stockLevelAttribute.getCode(), false));
		row.appendChild(createLabel(String.valueOf(stockLevelAttribute.getAvailableQuantity()), false));
		row.appendChild(createLabel(String.valueOf(stockLevelAttribute.getOversellingQuantity()), false));
		row.appendChild(
				createLabel((Objects.isNull(stockLevelAttribute.getInStockStatus()) ? Labels.getLabel(INSTOCK_STATUS_NA_LABEL)
						: getLabelService().getObjectLabel(stockLevelAttribute.getInStockStatus())), false));
		return row;
	}

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
	 * @return the labelService
	 */
	protected LabelService getLabelService()
	{
		return labelService;
	}

	/**
	 * @param labelService
	 *           the labelService to set
	 */
	@Required
	public void setLabelService(final LabelService labelService)
	{
		this.labelService = labelService;
	}

}
