package de.hybris.platform.travelservices.stock;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.Collection;
import java.util.Date;
import java.util.List;



/**
 * Extension of {@link StockService} to provide travel specific functionality such as getting stock level of product for
 * particular date.
 */
public interface TravelStockService extends StockService
{
	/**
	 * Gets stock level of a product for a particular date.
	 *
	 * @param product
	 * @param warehouses
	 * @param date
	 * @return
	 */
	StockLevelModel getStockLevelForDate(ProductModel product, Collection<WarehouseModel> warehouses, Date date);


	/**
	 * Creates a {@link StockLevelModel} within the given warehouse, with the attributes passed in{@link StockLevelAttributes}
	 *
	 * @param warehouse
	 * @param stockLevelAttribute
	 * @return
	 */
	StockLevelModel createStockLevel(WarehouseModel warehouse, StockLevelAttributes stockLevelAttribute);

	/**
	 * Updates a {@link StockLevelModel} identified through {@link StockLevelAttributes} within the given warehouse
	 *
	 * @param warehouse
	 * @param stockLevelAttribute
	 * @return
	 */
	StockLevelModel updateStockLevel(WarehouseModel warehouse, StockLevelAttributes stockLevelAttribute);

	/**
	 * Returns list of {@link StockLevelModel} for given list of {@link WarehouseModel }
	 *
	 * @param warehouses
	 * @return list
	 */
	List<StockLevelModel> findStockLevelsForWarehouses(List<WarehouseModel> warehouses);

	/**
	 * Updates the Stocklevel information for each Warehouse.
	 *
	 * @param stockLevels
	 * @param warehouses
	 */
	void updateStockLevelsForTransportOffering(List<StockLevelModel> stockLevels, List<WarehouseModel> warehouses);
}
