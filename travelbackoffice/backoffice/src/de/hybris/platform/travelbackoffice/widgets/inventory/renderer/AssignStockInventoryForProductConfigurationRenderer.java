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
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.Objects;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;

import com.hybris.cockpitng.engine.WidgetInstanceManager;


/**
 * This is responsible of rendering the layout of the Step2 of Create Inventory for Ancillary.
 */
public class AssignStockInventoryForProductConfigurationRenderer extends AbstractStockInventoryConfigurationRenderer
{

	protected static final String REFERENCE_TYPE = "Reference";

	@Override
	protected Row createStockAttributesColumnLayout(final WidgetInstanceManager widgetInstanceManager, final Integer index,
			final EventListener<Event> addButtonlistener, final EventListener<Event> removeButtonlistener,
			final StockLevelAttributes stockLevelAttributes)
	{
		final Row row = new Row();
		row.setId(index.toString());
		final Intbox availableQuantityTextBox = new Intbox();
		final Intbox oversellingTextBox = new Intbox();
		InStockStatus defaultInStockStatus = InStockStatus.NOTSPECIFIED;
		if (Objects.nonNull(stockLevelAttributes))
		{
			availableQuantityTextBox.setText(String.valueOf(stockLevelAttributes.getAvailableQuantity()));
			oversellingTextBox.setText(String.valueOf(stockLevelAttributes.getOversellingQuantity()));
			defaultInStockStatus = stockLevelAttributes.getInStockStatus();
		}
		row.appendChild(
				createEditor(widgetInstanceManager, REFERENCE_TYPE + "(" + AncillaryProductModel._TYPECODE + ")", null, false,
						AncillaryProductModel._TYPECODE + index.toString()));
		row.appendChild(availableQuantityTextBox);
		row.appendChild(oversellingTextBox);
		row.appendChild(
				createEditor(widgetInstanceManager, "java.lang.Enum(" + InStockStatus._TYPECODE + ")", defaultInStockStatus, false,
						InStockStatus._TYPECODE + index.toString()));
		final Button buttonAdd = new Button();
		buttonAdd.setId(index.toString());
		buttonAdd.setClass("yw-advancedsearch-add-btn z-button");
		buttonAdd.setStyle("right: 60px");
		buttonAdd.addEventListener(Events.ON_CLICK, addButtonlistener);
		row.appendChild(buttonAdd);
		final Button buttonRemove = new Button();
		buttonRemove.setId(index.toString());
		buttonRemove.setClass("yw-advancedsearch-delete-btn ye-delete-btn z-button");
		buttonRemove.addEventListener(Events.ON_CLICK, removeButtonlistener);
		row.appendChild(buttonRemove);
		return row;
	}

}
