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
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackoffice.utils.TravelbackofficeUtils;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
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
 * This is responsible of rendering the layout of the Step2 of modify inventory for flight.
 */
public class ModifyInventoryForFlightRenderer extends DefaultCustomViewRenderer implements EventListener<Event>
{
	private static final String CHECKBOX_EDITOR = "com.hybris.cockpitng.editor.boolean.checkbox";
	private static final String TRAVEL_TEXT_EDITOR = "de.hybris.platform.travelbackoffice.editor.text";
	private static final String MODIFY_INVENTORY_GRID_CLASS = "modify-inventory-grid";

	private LabelService labelService;

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
	 * 		the widget instance manager
	 *
	 * @return the component
	 */
	protected Component createLayout(final WidgetInstanceManager widgetInstanceManager)
	{
		final Div container = new Div();
		container.setVisible(true);
		createGridLayout(container, widgetInstanceManager);
		putInStockStatusEnumInContext(widgetInstanceManager);
		return container;
	}

	/**
	 * Method fetches the {@link InStockStatus} label value and save into the context.
	 *
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 */
	protected void putInStockStatusEnumInContext(final WidgetInstanceManager widgetInstanceManager)
	{
		Arrays.asList(InStockStatus.values()).forEach(inStockStatus -> widgetInstanceManager.getModel()
				.put(inStockStatus.getCode(), getLabelService().getObjectLabel(inStockStatus)));
	}

	/**
	 * Creates grid layout
	 *
	 * @param container
	 * 		the container
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 *
	 * @return the grid
	 */
	protected Grid createGridLayout(final Div container, final WidgetInstanceManager widgetInstanceManager)
	{
		final Grid modifyInventoryGrid = new Grid();
		modifyInventoryGrid.setClass(MODIFY_INVENTORY_GRID_CLASS);
		final ManageStockLevelInfo manageStockLevel = widgetInstanceManager.getModel()
				.getValue(TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID,
						ManageStockLevelInfo.class);
		final boolean isSingleTransportOffering = CollectionUtils.size(manageStockLevel.getTransportOfferings()) == 1;
		createGridColumns(modifyInventoryGrid, isSingleTransportOffering, widgetInstanceManager);
		createGridRows(modifyInventoryGrid, widgetInstanceManager, isSingleTransportOffering);
		container.appendChild(modifyInventoryGrid);
		return modifyInventoryGrid;
	}

	/**
	 * Creates grid rows
	 *
	 * @param modifyInventoryGrid
	 * 		the modify inventory grid
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 * @param isSingleTransportOffering
	 * 		the is single transport offering
	 */
	protected void createGridRows(final Grid modifyInventoryGrid, final WidgetInstanceManager widgetInstanceManager,
			final boolean isSingleTransportOffering)
	{
		final Rows rows = new Rows();
		rows.setParent(modifyInventoryGrid);
		final Set<String> stockLevels = widgetInstanceManager.getModel().getValue("stockLevels", java.util.HashSet.class);
		stockLevels.forEach(stockLevelCode -> {

			stockLevelCode = TravelbackofficeUtils.validateStockLevelCode(stockLevelCode);
			final StockLevelModel stockLevelModel = widgetInstanceManager.getModel().getValue(stockLevelCode, StockLevelModel
					.class);

			final Row row = createRow(widgetInstanceManager, isSingleTransportOffering, stockLevelModel, stockLevelCode);
			modifyInventoryGrid.getRows().appendChild(row);
		});
	}

	/**
	 * Method creates {@link Row} for each stock level entry.
	 *
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 * @param isSingleTransportOffering
	 * 		the is single transport offering
	 * @param stockLevelModel
	 * 		the stock level model
	 * @param property
	 * 		the property
	 *
	 * @return the row
	 */
	protected Row createRow(final WidgetInstanceManager widgetInstanceManager, final boolean isSingleTransportOffering,
			final StockLevelModel stockLevelModel, final String property)
	{
		final Row row = new Row();

		final Editor checkBoxEditor = createEditor(widgetInstanceManager, Boolean.class.getCanonicalName(), CHECKBOX_EDITOR,
				property + TravelbackofficeConstants.DOT + StockLevelModel.SELECTED, Events.ON_CHECK);
		checkBoxEditor.setInitialValue(stockLevelModel.getSelected());
		checkBoxEditor.initialize();
		row.appendChild(checkBoxEditor);

		final Label code = new Label(String.valueOf(stockLevelModel.getProductCode()));
		row.appendChild(code);

		final Editor availableQtyEditor = createEditor(widgetInstanceManager, String.class.getCanonicalName(), TRAVEL_TEXT_EDITOR,
				property + TravelbackofficeConstants.DOT + StockLevelModel.AVAILABLE, Events.ON_CHANGING);
		availableQtyEditor.initialize();
		if (!isSingleTransportOffering
				&& availableQtyEditor.getValue() instanceof Integer && (int) availableQtyEditor.getValue() == 0)
		{
			availableQtyEditor.setInitialValue(StringUtils.EMPTY);
		}
		row.appendChild(availableQtyEditor);

		if (isSingleTransportOffering)
		{
			final Label reservedQty = new Label(String.valueOf(stockLevelModel.getReserved()));
			row.appendChild(reservedQty);
		}

		final Editor oversellingQtyEditor = createEditor(widgetInstanceManager, String.class.getCanonicalName(),
				TRAVEL_TEXT_EDITOR,
				property + TravelbackofficeConstants.DOT + StockLevelModel.OVERSELLING, Events.ON_CHANGING);
		oversellingQtyEditor.initialize();
		if (!isSingleTransportOffering
				&& oversellingQtyEditor.getValue() instanceof Integer && (int) oversellingQtyEditor.getValue() == 0)
		{
			oversellingQtyEditor.setInitialValue(StringUtils.EMPTY);
		}
		row.appendChild(oversellingQtyEditor);

		final Editor inStockStatusEditor = createEditor(widgetInstanceManager, "java.lang.Enum(" + InStockStatus._TYPECODE + ")",
				null, property + TravelbackofficeConstants.DOT + StockLevelModel.INSTOCKSTATUS, Events.ON_CHANGE);
		inStockStatusEditor.initialize();
		row.appendChild(inStockStatusEditor);

		if (isSingleTransportOffering)
		{
			availableQtyEditor.setInitialValue(stockLevelModel.getAvailable());
			oversellingQtyEditor.setInitialValue(stockLevelModel.getOverSelling());
			inStockStatusEditor.setInitialValue(stockLevelModel.getInStockStatus());
		}

		return row;
	}

	/**
	 * Creates grid columns
	 *
	 * @param modifyInventoryGrid
	 * 		the modify inventory grid
	 * @param isSingleTransportOffering
	 * 		the is single transport offering
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 */
	protected void createGridColumns(final Grid modifyInventoryGrid, final Boolean isSingleTransportOffering,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final Column checkboxCol = new Column();
		final Checkbox selectChecbox = new Checkbox(Labels.getLabel(TravelbackofficeConstants.MODIFY_INVENTORY_SELECT_CHECKBOX));
		if (isSingleTransportOffering)
		{
			selectChecbox.setChecked(Objects.nonNull(widgetInstanceManager.getModel().getValue("fromPreviewStep", Boolean.class))
					? widgetInstanceManager.getModel().getValue("selectAllCheckboxVal", Boolean.class) : Boolean.TRUE);
		}
		selectChecbox.setId("selectCheckbox");

		selectChecbox.addEventListener(Events.ON_CHECK, event -> {
			final Rows rows = (Rows) event.getTarget().getParent().getParent().getParent().getChildren().get(1);

			rows.getChildren().forEach(component -> ((Checkbox) component.getFirstChild().getFirstChild())
					.setChecked(((Checkbox) event.getTarget()).isChecked()));

			final Set<String> stockLevels = widgetInstanceManager.getModel().getValue("stockLevels", HashSet.class);
			stockLevels.forEach(stockLevelCode -> {
				stockLevelCode = TravelbackofficeUtils.validateStockLevelCode(stockLevelCode);
				final StockLevelModel stockLevelModel = widgetInstanceManager.getModel().getValue(stockLevelCode,
						StockLevelModel.class);
				if (Objects.nonNull(stockLevelModel))
				{
					stockLevelModel.setSelected(((Checkbox) event.getTarget()).isChecked());
				}
			});
			widgetInstanceManager.getModel().put("selectAllCheckboxVal", ((Checkbox) event.getTarget()).isChecked());
		});

		checkboxCol.appendChild(selectChecbox);
		final Column code = new Column(Labels.getLabel(TravelbackofficeConstants.BOOKING_CLASS_LABEL));
		final Column availableQty = new Column(Labels.getLabel(TravelbackofficeConstants.AVAILABLE_QUANTITY_LABEL));
		final Column oversellingQty = new Column(Labels.getLabel(TravelbackofficeConstants.OVERSELLING_QUANTITY_LABEL));
		final Column stockStatus = new Column(Labels.getLabel(TravelbackofficeConstants.INSTOCK_STATUS_LABEL));

		code.setAlign("center");
		availableQty.setAlign("center");
		oversellingQty.setAlign("center");
		stockStatus.setAlign("center");

		final Columns columns = new Columns();
		columns.setStyle("background-color: #F0F0F0;");
		columns.setParent(modifyInventoryGrid);

		modifyInventoryGrid.getColumns().appendChild(checkboxCol);
		modifyInventoryGrid.getColumns().appendChild(code);
		modifyInventoryGrid.getColumns().appendChild(availableQty);
		if (isSingleTransportOffering)
		{
			final Column reservedQty = new Column(Labels.getLabel(TravelbackofficeConstants.RESERVED_QUANTITY_LABEL));
			reservedQty.setAlign("center");
			modifyInventoryGrid.getColumns().appendChild(reservedQty);
		}
		modifyInventoryGrid.getColumns().appendChild(oversellingQty);
		modifyInventoryGrid.getColumns().appendChild(stockStatus);

	}

	/**
	 * Method that create editor according to given parameter
	 *
	 * @param widgetInstanceManager
	 * 		the widget instance manager
	 * @param type
	 * 		the type
	 * @param defaultEditor
	 * 		the default editor
	 * @param property
	 * 		the property
	 * @param event
	 * 		the event
	 *
	 * @return the editor
	 */
	protected Editor createEditor(final WidgetInstanceManager widgetInstanceManager, final String type, final String
			defaultEditor, final String property, final String event)
	{
		final Editor editor = new Editor();
		editor.setWidgetInstanceManager(widgetInstanceManager);
		editor.addEventListener(event, this);
		editor.setType(type);
		editor.setDefaultEditor(defaultEditor);
		editor.setProperty(property);
		return editor;
	}

	@Override
	public void onEvent(final Event event) throws Exception
	{
		if (Events.ON_CHANGING.equals(event.getName()) || Events.ON_CHANGE.equals(event.getName()))
		{
			final Optional<Component> optional = event.getTarget().getParent().getChildren().stream()
					.filter(component -> component instanceof Editor)
					.filter(component -> StringUtils.contains(component.getId(), StockLevelModel.SELECTED)).findFirst();

			optional.ifPresent(component -> ((Checkbox) component.getFirstChild()).setChecked(true));

			((Editor) event.getTarget()).getWidgetInstanceManager().getModel()
					.getValue(((Editor) event.getTarget()).getProperty() + Boolean.TRUE, StockLevelModel.class)
					.setSelected(Boolean.TRUE);

		}
	}

	/**
	 * Gets label service.
	 *
	 * @return the label service
	 */
	protected LabelService getLabelService()
	{
		return labelService;
	}

	/**
	 * Sets label service.
	 *
	 * @param labelService
	 * 		the label service
	 */
	@Required
	public void setLabelService(final LabelService labelService)
	{
		this.labelService = labelService;
	}

}
