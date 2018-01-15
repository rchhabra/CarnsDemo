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

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackoffice.utils.TravelbackofficeUtils;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stock.TravelStockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;




/**
 * Class is responsible to search for the instance of {@link StockLevelModel} for "modify-inventory" wizard.
 */
public class ModifyInventoryFinderHandler implements FlowActionHandler
{
	private TravelStockService travelStockService;
	private ModelService modelService;

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		final ManageStockLevelInfo manageStockLevel = adapter.getWidgetInstanceManager().getModel().getValue(
				TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID, ManageStockLevelInfo.class);

		manageStockLevel.setModified(Boolean.TRUE);
		manageStockLevel.setStockItemType(TransportOfferingModel._TYPECODE);
		adapter.getWidgetInstanceManager().getModel().remove("fromPreviewStep");
		final List<TransportOfferingModel> transportOfferings = manageStockLevel.getTransportOfferings().stream().distinct().collect(
				Collectors.toList());
		if (CollectionUtils.isEmpty(transportOfferings))
		{
			showErrorPopup(TravelbackofficeConstants.MODIFY_INVENTORY_NO_TRANSPORTOFFERING_SELECTED);
			return;
		}
		final List<WarehouseModel> warehouses = new ArrayList<>(transportOfferings);

		final List<StockLevelModel> stockLevels = getTravelStockService()
				.findStockLevelsForWarehouses(warehouses);

		if (CollectionUtils.isEmpty(stockLevels))
		{
			showErrorPopup(TravelbackofficeConstants.MODIFY_INVENTORY_TRANSPORTOFFERING_NO_STOCK);
		}
		else
		{
			final Map<String, Long> groupedStockLevels = stockLevels.stream()
					.collect(Collectors.groupingBy(StockLevelModel::getProductCode, Collectors.counting()));

			final Set<String> commonStockLevels = getCommonStocks(groupedStockLevels,
					CollectionUtils.size(warehouses));

			if (CollectionUtils.isEmpty(commonStockLevels))
			{
				showErrorPopup(TravelbackofficeConstants.MODIFY_INVENTORY_TRANSPORTOFFERING_NO_COMMON_STOCK);
			}
			else
			{
				adapter.getWidgetInstanceManager().getModel().put("stockLevels", commonStockLevels);
				if (CollectionUtils.size(warehouses) == 1)
				{
					stockLevels.forEach(stockLevel -> {
						stockLevel.setSelected(Boolean.TRUE);
						String stockLevelCode = stockLevel.getProductCode();
						stockLevelCode = TravelbackofficeUtils.validateStockLevelCode(stockLevelCode);
						adapter.getWidgetInstanceManager().getModel().put(stockLevelCode, stockLevel);
					});
				}
				else
				{
					commonStockLevels.forEach(stockLevelCode -> {
						final StockLevelModel stockLevelModel = getModelService().create(StockLevelModel.class);
						stockLevelModel.setProductCode(stockLevelCode);
						stockLevelCode = TravelbackofficeUtils.validateStockLevelCode(stockLevelCode);
						adapter.getWidgetInstanceManager().getModel().put(stockLevelCode, stockLevelModel);
					});
				}
				adapter.next();
			}
		}
	}

	/**
	 * Method returns the common stock level code from the given map of stocks by checking the transport offering size with the
	 * value of each map entry.
	 *
	 * @param groupedStockLevels
	 * 		the grouped stock levels
	 * @param size
	 * 		the size
	 *
	 * @return the common stocks
	 */
	protected Set<String> getCommonStocks(final Map<String, Long> groupedStockLevels, final int size)
	{
		if (MapUtils.isNotEmpty(groupedStockLevels))
		{
			return groupedStockLevels.entrySet().stream().filter(entry -> (entry.getValue() == size)).collect(Collectors.toSet())
					.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	/**
	 * Method responsible to show error/warning popup on step-1 of "modify-inventory" wizard.
	 *
	 * @param label
	 * 		the label
	 */
	protected void showErrorPopup(final String label)
	{
		Messagebox.show(Labels.getLabel(label), Labels.getLabel(TravelbackofficeConstants.ERROR_TITLE), new Button[]
				{ Button.OK }, null, null);
	}

	/**
	 * Gets travel stock service.
	 *
	 * @return travelStockService travel stock service
	 */
	protected TravelStockService getTravelStockService()
	{
		return travelStockService;
	}

	/**
	 * Sets travel stock service.
	 *
	 * @param travelStockService
	 * 		the travelStockService to set
	 */
	@Required
	public void setTravelStockService(final TravelStockService travelStockService)
	{
		this.travelStockService = travelStockService;
	}

	/**
	 * Gets model service.
	 *
	 * @return modelService model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
