package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.stock.impl.DefaultStockLevelDao;
import de.hybris.platform.travelservices.dao.TravelStockLevelDao;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default Implementation of {@link TravelStockLevelDao}
 */
public class DefaultTravelStockLevelDao extends DefaultStockLevelDao implements TravelStockLevelDao
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelStockLevelDao.class);

	private static final String STOCK_LEVEL_FOR_DATE_QUERY = "SELECT {" + StockLevelModel.PK + "} from {"
			+ StockLevelModel._TYPECODE + "} WHERE {" + StockLevelModel.PRODUCTCODE + "} = ?productCode " + "AND {"
			+ StockLevelModel.DATE + "} BETWEEN ?startDate AND ?endDate AND {" + StockLevelModel.WAREHOUSE
			+ "} IN (?WAREHOUSES_PARAM)";

	private static final String STOCK_LEVEL_FOR_MULTIPLE_TRANSPORTOFFERING_QUERY = "SELECT {" + StockLevelModel.PK + "} from {"
			+ StockLevelModel._TYPECODE + "} WHERE {" + StockLevelModel.WAREHOUSE + "} IN (?WAREHOUSES_PARAM)";

	@Override
	public StockLevelModel findStockLevel(final String productCode, final Collection<WarehouseModel> warehouseModels,
			final Date date)
	{
		validateParameterNotNull(productCode, "Product code must not be null!");

		final List warehouses = filterWarehouses(warehouseModels);
		if (warehouses.isEmpty())
		{
			return null;
		}
		else
		{
			final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(STOCK_LEVEL_FOR_DATE_QUERY);
			fQuery.addQueryParameter("productCode", productCode);

			final Calendar startDate = new GregorianCalendar();
			startDate.setTime(date);
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			fQuery.addQueryParameter("startDate", startDate.getTime());

			final Calendar endDate = new GregorianCalendar();
			endDate.setTime(date);
			endDate.set(Calendar.HOUR_OF_DAY, 23);
			endDate.set(Calendar.MINUTE, 59);
			endDate.set(Calendar.SECOND, 59);
			fQuery.addQueryParameter("endDate", endDate.getTime());

			fQuery.addQueryParameter("WAREHOUSES_PARAM", warehouses);
			final SearchResult result = getFlexibleSearchService().search(fQuery);
			final List<StockLevelModel> stockLevels = result.getResult();
			if (CollectionUtils.isEmpty(stockLevels))
			{
				LOG.warn("No Stock Levels found for product: " + productCode + " and date: " + date);
				return null;
			}
			else if (CollectionUtils.size(stockLevels) > 1)
			{
				LOG.warn(
						"Multiple Stock Levels found for product: " + productCode + "and date: " + date + ". Returning the first one");
			}
			return stockLevels.get(0);
		}
	}

	@Override
	public List<StockLevelModel> findStockLevelsForWarehouses(final List<WarehouseModel> warehouses)
	{
		validateParameterNotNull(warehouses, "warehouses must not be null!");

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(STOCK_LEVEL_FOR_MULTIPLE_TRANSPORTOFFERING_QUERY);
		fQuery.addQueryParameter("WAREHOUSES_PARAM", warehouses);
		final SearchResult result = getFlexibleSearchService().search(fQuery);
		final List<StockLevelModel> stockLevels = result.getResult();
		if (CollectionUtils.isEmpty(stockLevels))
		{
			LOG.warn("No Stock Levels found for warehouses: " + stockLevels);
			return Collections.emptyList();
		}
		return stockLevels;
	}

	/**
	 * Filters warehouse list to remove null's and duplicate elements.
	 */
	protected List<WarehouseModel> filterWarehouses(final Collection<WarehouseModel> warehouses)
	{
		if (warehouses == null)
		{
			throw new IllegalArgumentException("warehouses cannot be null.");
		}
		final Set<WarehouseModel> result = new HashSet<>();
		for (final WarehouseModel house : warehouses)
		{
			if (house != null)
			{
				result.add(house);
			}
		}
		return new ArrayList<>(result);
	}
}
