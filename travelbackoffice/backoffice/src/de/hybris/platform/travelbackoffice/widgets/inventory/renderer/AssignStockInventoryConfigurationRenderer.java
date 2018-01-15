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
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.Objects;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.engine.WidgetInstanceManager;


/**
 * This is responsible of rendering the layout of the Step2 of Create Inventory for Booking class.
 */
public class AssignStockInventoryConfigurationRenderer extends AbstractStockInventoryConfigurationRenderer
{

	@Override
	protected Row createStockAttributesColumnLayout(final WidgetInstanceManager widgetInstanceManager, final Integer index,
			final EventListener<Event> addButtonlistener, final EventListener<Event> removeButtonlistener,
			final StockLevelAttributes stockLevelAttributes)
	{
		final Row row = new Row();
		row.setId(index.toString());
		final Textbox productTextBox = new Textbox();
		final Intbox availableQuantityTextBox = new Intbox();
		final Intbox oversellingTextBox = new Intbox();
		InStockStatus defaultInStockStatus = InStockStatus.NOTSPECIFIED;
		if (Objects.nonNull(stockLevelAttributes))
		{
			productTextBox.setText(stockLevelAttributes.getCode());
			availableQuantityTextBox.setText(String.valueOf(stockLevelAttributes.getAvailableQuantity()));
			oversellingTextBox.setText(String.valueOf(stockLevelAttributes.getOversellingQuantity()));
			defaultInStockStatus = stockLevelAttributes.getInStockStatus();
		}
		row.appendChild(productTextBox);
		row.appendChild(availableQuantityTextBox);
		row.appendChild(oversellingTextBox);
		row.appendChild(
				createEditor(widgetInstanceManager, "java.lang.Enum(" + InStockStatus._TYPECODE + ")", defaultInStockStatus, false,
						null));
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
