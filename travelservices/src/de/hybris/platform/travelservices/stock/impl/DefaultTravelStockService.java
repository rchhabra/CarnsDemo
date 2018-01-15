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
package de.hybris.platform.travelservices.stock.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.ModelCreationException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.travelservices.dao.TravelStockLevelDao;
import de.hybris.platform.travelservices.stock.TravelStockService;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Default implementation of {@link TravelStockService}
 */
public class DefaultTravelStockService extends DefaultStockService implements TravelStockService
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelStockService.class);

	private TravelStockLevelDao travelStockLevelDao;

	@Override
	public StockLevelModel getStockLevelForDate(final ProductModel product, final Collection<WarehouseModel> warehouses,
			final Date date)
	{
		return getTravelStockLevelDao().findStockLevel(product != null ? product.getCode() : null, warehouses, date);
	}

	@Override
	public StockLevelModel createStockLevel(final WarehouseModel warehouse, final StockLevelAttributes stockLevelAttribute)
	{
		if (StringUtils.isBlank(stockLevelAttribute.getCode()))
		{
			LOG.warn("Product code empty. Stock not created for warehouse [" + warehouse.getCode() + "].");
			return null;
		}
		if (stockLevelAttribute.getAvailableQuantity() < 0)
		{
			LOG.warn("Stocks available amount cannot be negative.");
		}
		if (stockLevelAttribute.getOversellingQuantity() < 0)
		{
			LOG.warn("Stocks overSelling amount cannot be negative.");
		}

		StockLevelModel stockLevel = null;
		try
		{
			stockLevel = getTravelStockLevelDao().findStockLevel(stockLevelAttribute.getCode(), warehouse);
		}
		catch (final ModelNotFoundException ex)
		{
			LOG.debug("No stock found for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode()
					+ "]. Creating a new one.");
		}

		if (Objects.nonNull(stockLevel))
		{
			LOG.warn("Stock already exist for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode()
					+ "].");
			return null;
		}

		LOG.debug("Creating Stock for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode() + "].");
		try
		{

			stockLevel = getModelService().create(StockLevelModel.class);
		}
		catch (final ModelCreationException ex)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error(ex);
			}
			LOG.warn("Stock not created for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode()
					+ "].");
			return null;
		}
		stockLevel.setProductCode(stockLevelAttribute.getCode());
		stockLevel.setWarehouse(warehouse);
		stockLevel.setAvailable(stockLevelAttribute.getAvailableQuantity());
		stockLevel.setOverSelling(stockLevelAttribute.getOversellingQuantity());
		stockLevel.setInStockStatus(stockLevelAttribute.getInStockStatus());
		try
		{
			getModelService().save(stockLevel);
		}
		catch (final ModelSavingException ex)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error(ex);
			}

			LOG.warn("Stock not created for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode()
					+ "].");
			return null;
		}
		LOG.debug("Successfully created Stock for product [" + stockLevelAttribute.getCode() + "] in warehouse ["
				+ warehouse.getCode() + "].");
		return stockLevel;
	}

	@Override
	public StockLevelModel updateStockLevel(final WarehouseModel warehouse, final StockLevelAttributes stockLevelAttribute)
	{
		if (stockLevelAttribute.getAvailableQuantity() < 0)
		{
			LOG.warn("Stocks available amount cannot be negative.");
		}
		if (stockLevelAttribute.getOversellingQuantity() < 0)
		{
			LOG.warn("Stocks overSelling amount cannot be negative.");
		}

		StockLevelModel stockLevel;
		try
		{
			stockLevel = getTravelStockLevelDao().findStockLevel(stockLevelAttribute.getCode(), warehouse);
		}
		catch (final ModelNotFoundException ex)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error(ex);
			}
			LOG.warn(
					"Stock not found for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode() + "].");
			return null;
		}
		stockLevel.setAvailable(stockLevelAttribute.getAvailableQuantity());
		stockLevel.setOverSelling(stockLevelAttribute.getOversellingQuantity());
		stockLevel.setInStockStatus(stockLevelAttribute.getInStockStatus());
		try
		{
			getModelService().save(stockLevel);
		}
		catch (final ModelSavingException ex)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error(ex);
			}

			LOG.debug("Stock not updated for product [" + stockLevelAttribute.getCode() + "] in warehouse [" + warehouse.getCode()
					+ "].");
			return null;
		}
		return stockLevel;
	}

	@Override
	public List<StockLevelModel> findStockLevelsForWarehouses(final List<WarehouseModel> warehouses)
	{
		return getTravelStockLevelDao().findStockLevelsForWarehouses(warehouses);
	}

	@Override
	public void updateStockLevelsForTransportOffering(final List<StockLevelModel> updatedStockLevels,
			final List<WarehouseModel> warehouses)
	{
		if (CollectionUtils.size(warehouses) == 1)
		{
			getModelService().saveAll(updatedStockLevels);
			getModelService().save(warehouses.get(0));
			return;
		}

		final Set<String> updatedStockCodes = updatedStockLevels.stream().map(StockLevelModel::getProductCode)
				.collect(Collectors.toSet());
		warehouses.stream().forEach(transportOffering -> {
			transportOffering.getStockLevels().forEach(stockLevel -> {
				if (updatedStockCodes.contains(stockLevel.getProductCode()))
				{
					final StockLevelModel matchingStockLevel = updatedStockLevels.stream()
							.filter(updatedStockLevel -> StringUtils.equalsIgnoreCase(updatedStockLevel.getProductCode(),
									stockLevel.getProductCode()))
							.findFirst().orElse(null);
					if (Objects.nonNull(matchingStockLevel))
					{
						stockLevel.setAvailable(matchingStockLevel.getAvailable());
						stockLevel.setOverSelling(matchingStockLevel.getOverSelling());
						stockLevel.setInStockStatus(matchingStockLevel.getInStockStatus());
						getModelService().save(stockLevel);
					}
				}
			});
			getModelService().save(transportOffering);
		});

	}

	/**
	 * Gets travel stock level dao.
	 *
	 * @return the travel stock level dao
	 */
	protected TravelStockLevelDao getTravelStockLevelDao()
	{
		return travelStockLevelDao;
	}

	/**
	 * Sets travel stock level dao.
	 *
	 * @param travelStockLevelDao
	 * 		the travel stock level dao
	 */
	@Required
	public void setTravelStockLevelDao(final TravelStockLevelDao travelStockLevelDao)
	{
		this.travelStockLevelDao = travelStockLevelDao;
	}
}
