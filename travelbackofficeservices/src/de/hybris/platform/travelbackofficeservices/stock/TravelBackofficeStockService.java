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

package de.hybris.platform.travelbackofficeservices.stock;

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.stock.TravelStockService;

import java.util.Collection;


/**
 * The interface Travel backoffice stock service.
 */
public interface TravelBackofficeStockService extends TravelStockService
{
	/**
	 * Creates a new Stocklevel information for each Warehouse using information provided in manageStockLevelData.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param transportOffering
	 * 		the transport offering
	 */
	void createStockLevels(ManageStockLevelInfo manageStockLevel, Collection<? extends WarehouseModel> transportOffering);

	/**
	 * Creates a new Stocklevel information for each transport offering in the ScheduleConfigurationModel using
	 * information provided in manageStockLevel.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param schedule
	 * 		the schedule
	 */
	void createStockLevels(ManageStockLevelInfo manageStockLevel, ScheduleConfigurationModel schedule);

	/**
	 * Creates a new Stocklevel information for each transport offering in the TravelSectorModel using information
	 * provided in manageStockLevel.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param sector
	 * 		the sector
	 */
	void createStockLevels(ManageStockLevelInfo manageStockLevel, TravelSectorModel sector);

	/**
	 * Updates the Stocklevel information for each Warehouse using information provided in manageStockLevel.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param warehouses
	 * 		the warehouses
	 */
	void updateStockLevels(ManageStockLevelInfo manageStockLevel, Collection<? extends WarehouseModel> warehouses);

	/**
	 * Updates the Stocklevel information for each transport offering in the ScheduleConfigurationModel using information
	 * provided in manageStockLevel.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param schedule
	 * 		the schedule
	 */
	void updateStockLevels(ManageStockLevelInfo manageStockLevel, ScheduleConfigurationModel schedule);

	/**
	 * Updates the Stocklevel information for each transport offering in the TravelSectorModel using information provided
	 * in manageStockLevel.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param sector
	 * 		the sector
	 */
	void updateStockLevels(ManageStockLevelInfo manageStockLevel, TravelSectorModel sector);

}
