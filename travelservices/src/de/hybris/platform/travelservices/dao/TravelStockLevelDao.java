package de.hybris.platform.travelservices.dao;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * Extension of StockLevelDao to provide a travel specific functionality like searching for stock level for particular date.
 */
public interface TravelStockLevelDao extends StockLevelDao
{
	/**
	 * Returns a stock level for product for a particular date.
	 *
	 * @param productCode
	 * @param warehouseModels
	 * @param date
	 * @return
	 */
	StockLevelModel findStockLevel(String productCode, Collection<WarehouseModel> warehouseModels, Date date);

	/**
	 * Returns list of {@link StockLevelModel} for given list of {@link WarehouseModel}.
	 *
	 * @param warehouses
	 */
	List<StockLevelModel> findStockLevelsForWarehouses(List<WarehouseModel> warehouses);
}
