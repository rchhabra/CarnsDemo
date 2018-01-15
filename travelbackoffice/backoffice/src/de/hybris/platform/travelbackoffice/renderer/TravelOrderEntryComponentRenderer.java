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
*/

package de.hybris.platform.travelbackoffice.renderer;

import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.travelbackoffice.helpers.TravelOrderEntryInfo;
import de.hybris.platform.travelbackoffice.helpers.TravelOrderOnDemandPricesHelper;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;

import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomSection;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


/**
 * The Renderer class is used to render the Order Entries inside the on demand price section under the Common Tab of the
 * Order Entry
 */
public class TravelOrderEntryComponentRenderer implements WidgetComponentRenderer<Component, CustomSection, Object>
{
	private final TravelOrderOnDemandPricesHelper orderOnDemandPricesHelper;

	public TravelOrderEntryComponentRenderer(final TravelOrderOnDemandPricesHelper orderOnDemandPricesHelper)
	{
		this.orderOnDemandPricesHelper = orderOnDemandPricesHelper;
	}

	public void render(final Component parent, final CustomSection configuration, final Object object, final DataType dataType,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final WidgetModel model = widgetInstanceManager.getModel();
		final OrderEntryModel orderEntry = model.getValue("currentObject", OrderEntryModel.class);
		final TravelOrderEntryInfo orderEntryInfo = orderOnDemandPricesHelper.estimateOrderEntryInfo(orderEntry);
		final Vlayout vlayout = new Vlayout();
		vlayout.appendChild(buildOrderEntryGrid(orderEntryInfo));
		parent.appendChild(vlayout);
	}

	private Grid buildOrderEntryGrid(final TravelOrderEntryInfo orderEntryInfo)
	{
		final Grid grid = new Grid();
		grid.setWidth("1150px");
		final Columns columns = new Columns();
		columns.setParent(grid);
		Column col = new Column(Labels.getLabel("hmc.msg.product"));
		col.setWidth("100px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.transport.offerings"));
		col.setWidth("150px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.travel.route"));
		col.setWidth("150px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.origin.destination.ref.number"));
		col.setWidth("100px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.travellers"));
		col.setWidth("150px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.unitprice"));
		col.setWidth("100px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.entryprice"));
		col.setWidth("100px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.unittax"));
		col.setWidth("100px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.entrytax"));
		col.setWidth("100px");
		col.setParent(columns);
		col = new Column(Labels.getLabel("hmc.msg.entrypricetotal"));
		col.setWidth("100px");
		col.setParent(columns);
		final Rows rows = new Rows();
		rows.setParent(grid);
		final Row row = new Row();
		rows.appendChild(row);
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getEntryDesc())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getTransportOfferings())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getTravelRoute())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getOriginDestinationRefNumber())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getTravellers())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getUnitPrice())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getEntryPrice())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getUnitTax())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getEntryTax())));
		row.appendChild(new Label(String.valueOf(orderEntryInfo.getEntryTotal())));
		return grid;
	}
}
