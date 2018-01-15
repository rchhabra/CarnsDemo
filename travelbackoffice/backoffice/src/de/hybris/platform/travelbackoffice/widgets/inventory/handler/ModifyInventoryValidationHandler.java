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

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackoffice.utils.TravelbackofficeUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Custom validation handler for "modify-inventory"wizard's 2nd step. It validates all mandatory field to be not null.
 */
public class ModifyInventoryValidationHandler implements FlowActionHandler
{

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		final Set<String> stockLevels = adapter.getWidgetInstanceManager().getModel().getValue("stockLevels", HashSet.class);

		boolean isSelectedStockFound = false;
		for (String stockLevel : stockLevels)
		{
			stockLevel = TravelbackofficeUtils.validateStockLevelCode(stockLevel);
			final StockLevelModel stockLevelModel = adapter.getWidgetInstanceManager().getModel().getValue(stockLevel, StockLevelModel.class);
			if(Objects.nonNull(stockLevelModel) && BooleanUtils.isTrue(stockLevelModel.getSelected()))
			{
				isSelectedStockFound = true;
				if (stockLevelModel.getAvailable() == 0 && stockLevelModel.getOverSelling() == 0
						&& (Objects.isNull(stockLevelModel.getInStockStatus())
								|| stockLevelModel.getInStockStatus().equals(InStockStatus.NOTSPECIFIED)))
				{
					Messagebox.show(Labels.getLabel(TravelbackofficeConstants.MODIFY_INVENTORY_AVAILABLE_OR_OVERSELLING_QTY_EMPTY),
							Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Button[]
							{ Button.OK }, null, null);
					return;
				}
			}
		}

		if (!isSelectedStockFound)
		{
			Messagebox.show(Labels.getLabel(TravelbackofficeConstants.MODIFY_INVENTORY_NO_STOCK_SELECTED),
					Labels.getLabel(TravelbackofficeConstants.MESSAGE_BOX_WARNING_TITLE), new Button[]
					{ Button.OK }, null, null);
		}
		else
		{
			adapter.next();
		}
	}
}
