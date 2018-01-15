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

package de.hybris.platform.travelbackoffice.widgets.inventory.handler;

import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;



/**
 * This handler is used to handle the next step logic for Step2, in Create Inventory for Booking Class
 */
public abstract class AbstractStockInventoryConfigurationHandler implements FlowActionHandler
{
	private static final Logger LOG = Logger.getLogger(AbstractStockInventoryConfigurationHandler.class);

	private static final String ERROR_MESSAGE = "create.inventory.bookingclass.assigninventory.wizard.error";
	private static final String ERROR_UNKNOWN_MESSAGE = "create.inventory.bookingclass.assigninventory.wizard.popup.error.unknown.message";
	public static final String ERROR_POPUP_TITLE = "error.title.message";

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		if (Objects.nonNull(adapter.getWidgetInstanceManager().getWidgetslot()))
		{
			final ManageStockLevelInfo manageStockLevel = adapter.getWidgetInstanceManager().getModel().getValue(
					TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID, ManageStockLevelInfo.class);
			final Object grid = adapter.getWidgetInstanceManager().getWidgetslot()
					.getFellow(TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_PROPERTIES_GRID_ID);
			if (Objects.nonNull(grid))
			{
				final List<Component> children = ((Grid) grid).getRows().getChildren();
				final int stockLevelAttributesSize = children.size() - 1;
				manageStockLevel.setStockLevelAttributes(new ArrayList<>(stockLevelAttributesSize));
				try
				{
					children.forEach(child -> {
						final int id = Integer.parseInt(child.getId());
						if (id > 0)
						{
							final StockLevelAttributes stockAttribute = createStockLevelAttribute((Row) child);
							if (Objects.nonNull(stockAttribute))
							{
								manageStockLevel.getStockLevelAttributes().add(stockAttribute);
							}
						}
					});
					if (CollectionUtils.size(manageStockLevel.getStockLevelAttributes()) < stockLevelAttributesSize)
					{
						displayErrorMessage(Labels.getLabel(ERROR_MESSAGE), Labels.getLabel(ERROR_POPUP_TITLE));
						manageStockLevel.setStockLevelAttributes(Collections.emptyList());
					}
					else
					{
						adapter.next();
					}
				}
				catch (final NumberFormatException ex)
				{
					LOG.error("Required information not found.");
					displayErrorMessage(Labels.getLabel(ERROR_UNKNOWN_MESSAGE), Labels.getLabel(ERROR_POPUP_TITLE));
				}
			}
			else
			{
				LOG.error("Required information not found.");
				displayErrorMessage(Labels.getLabel(ERROR_UNKNOWN_MESSAGE), Labels.getLabel(ERROR_POPUP_TITLE));
			}
		}
	}

	/**
	 * Method displays the error message on {@link Messagebox}
	 * 
	 * @param errorMsg
	 * @param title
	 */
	protected void displayErrorMessage(final String errorMsg, final String title)
	{
		Messagebox.show(errorMsg, title, Messagebox.OK, Messagebox.ERROR, null);
	}

	protected abstract StockLevelAttributes createStockLevelAttribute(final Row row);

}
