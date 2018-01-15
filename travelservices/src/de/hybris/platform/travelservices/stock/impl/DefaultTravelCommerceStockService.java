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

package de.hybris.platform.travelservices.stock.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.stock.exception.StockLevelNotFoundException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TravelRestrictionService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.stock.TravelStockService;
import de.hybris.platform.travelservices.strategies.stock.StockReservationCreationStrategy;
import de.hybris.platform.travelservices.strategies.stock.StockReservationReleaseByEntryTypeStrategy;
import de.hybris.platform.travelservices.strategies.stock.TravelManageStockByEntryTypeStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.tx.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Service implementation for retrieving stock levels related to transport offering (warehouse)
 */
public class DefaultTravelCommerceStockService extends DefaultCommerceStockService implements TravelCommerceStockService
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelCommerceStockService.class);
	private static final String ENABLE_STOCK_RESERVATION_PROPERTY = "enable.stock.reservation";
	private static final String DEFAULT_WAREHOUSE = "default";

	private ConfigurationService configurationService;
	private WarehouseService warehouseService;
	private TravelRestrictionService travelRestrictionService;
	private ModelService modelService;
	private Map<OrderEntryType, TravelManageStockByEntryTypeStrategy> manageStockByEntryTypeStrategyMap;
	private Map<AddToCartCriteriaType, StockReservationCreationStrategy> stockReservationCreationStrategyMap;
	private Map<OrderEntryType, StockReservationReleaseByEntryTypeStrategy> stockReservationReleaseByEntryTypeStrategyMap;
	private TravelStockService travelStockService;


	/**
	 * @deprecated Deprecated since version 2.0. Use {@link #getStockLevelQuantity(ProductModel, Collection)} instead
	 */
	@Override
	@Deprecated
	public Long getStockLevel(final ProductModel product, final Collection<TransportOfferingModel> transportOfferings)
	{
		validateParameterNotNull(product, "product cannot be null");
		validateParameterNotNull(transportOfferings, "transport offerings cannot be null");

		final List<WarehouseModel> warehouses = new ArrayList<>();
		for (final TransportOfferingModel transportOffering : transportOfferings)
		{
			warehouses.add(transportOffering);
		}

		Collection<StockLevelModel> stockLevels = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(warehouses))
		{
			stockLevels = getStockService().getStockLevels(product, warehouses);
		}

		if (CollectionUtils.isEmpty(warehouses) || CollectionUtils.isEmpty(stockLevels))
		{
			warehouses.add(getWarehouseService().getWarehouseForCode(DEFAULT_WAREHOUSE));
		}

		return getCommerceStockLevelCalculationStrategy()
				.calculateAvailability(getStockService().getStockLevels(product, warehouses));
	}

	@Override
	public void reserve(final AbstractOrderModel abstractOrder) throws InsufficientStockLevelException
	{
		if (!enableStockReservation())
		{
			return;
		}
		validateParameterNotNull(abstractOrder, "abstractOrder cannot be null");

		final Transaction tx = Transaction.current();
		tx.begin();

		LOG.info("Reserving " + abstractOrder.getEntries().size() + " Order Entries in Order " + abstractOrder.getCode());

		for (final AbstractOrderEntryModel abstractOrderEntry : abstractOrder.getEntries())
		{
			try
			{
				getStrategyForEntryType(abstractOrderEntry).reserve(abstractOrderEntry);
			}
			catch (final InsufficientStockLevelException | StockLevelNotFoundException e)
			{
				LOG.error("Unable to reseve stock for Product code " + abstractOrderEntry.getProduct().getCode(), e);
				tx.rollback();
				throw e;
			}

		}

		if (tx.isRunning())
		{
			tx.commit();
		}
	}

	@Override
	public void release(final AbstractOrderModel abstractOrder)
	{
		if (!enableStockReservation())
		{
			return;
		}
		validateParameterNotNull(abstractOrder, "abstractOrder cannot be null");

		/*
		 * We only release on cancellation, hence, we check that the abstractOrder is an order. However, left
		 * abstractOrder in the signature as an extension point (in case stock has to be released from a cart
		 */

		if (!(abstractOrder instanceof OrderModel))
		{
			return;
		}

		final OrderModel lastOrder = (OrderModel) abstractOrder;
		final OrderHistoryEntryModel lastEntry = lastOrder.getHistoryEntries().stream().reduce((entry1, entry2) -> entry2)
				.orElse(null);

		if (lastEntry == null)
		{
			return;
		}
		final OrderModel orderToRelease = lastEntry.getPreviousOrderVersion();

		if (orderToRelease == null)
		{
			return;
		}
		LOG.info("Cancelling " + orderToRelease.getEntries().size() + " Order Entries in Order " + orderToRelease.getCode());

		for (final AbstractOrderEntryModel abstractOrderEntry : orderToRelease.getEntries())
		{
			getStrategyForEntryType(abstractOrderEntry).release(abstractOrderEntry);
		}
	}

	/**
	 * Gets strategy for entry type.
	 *
	 * @param entry
	 * 		the entry
	 * @return the strategy associated to the type the given entry belongs to
	 */
	protected TravelManageStockByEntryTypeStrategy getStrategyForEntryType(final AbstractOrderEntryModel entry)
	{
		final OrderEntryType entryType = entry.getType();
		return Objects.nonNull(getManageStockByEntryTypeStrategyMap().get(entryType))
				? getManageStockByEntryTypeStrategyMap().get(entryType)
				: getManageStockByEntryTypeStrategyMap().get(OrderEntryType.DEFAULT);
	}

	@Override
	public void adjustStockReservationForAmmendment(final AbstractOrderModel newOrder, final AbstractOrderModel originalOrder)
			throws InsufficientStockLevelException
	{
		if (enableStockReservation())
		{
			validateParameterNotNull(originalOrder, "originalOrder cannot be null");
			validateParameterNotNull(newOrder, "newOrder cannot be null");

			final List<StockReservationData> newOrderProducts = getProductsPerWarehouse(newOrder);
			final List<StockReservationData> oldOrderProducts = getProductsPerWarehouse(originalOrder);

			final List<StockReservationData> stockToReserve = new ArrayList<>();
			final List<StockReservationData> stockToRelease = new ArrayList<>();

			final Iterator<StockReservationData> newStockReservationData = newOrderProducts.iterator();

			while (newStockReservationData.hasNext())
			{
				final Iterator<StockReservationData> oldStockReservationData = oldOrderProducts.iterator();

				if (!oldStockReservationData.hasNext())
				{
					break;
				}

				findMatchingProduct(stockToReserve, stockToRelease, newStockReservationData, newStockReservationData.next(),
						oldStockReservationData);
			}

			stockToReserve.addAll(newOrderProducts);
			stockToRelease.addAll(oldOrderProducts);

			LOG.info("Preparing to ammend stock reservation for Order " + newOrder.getCode());

			adjustStockReservation(stockToReserve, stockToRelease);
		}
	}

	/**
	 * Method to build stock data objects according with entry type, using warehouses for extensibility
	 *
	 * @param abstractOrder
	 * 		the abstract order
	 * @return the list of reservation data object to release/reserve stocks
	 */
	protected List<StockReservationData> getProductsPerWarehouse(final AbstractOrderModel abstractOrder)
	{
		final List<StockReservationData> stockReservationDataList = new ArrayList<>();

		abstractOrder.getEntries().forEach(entry ->
		{
			final StockReservationReleaseByEntryTypeStrategy strategy = Objects
					.nonNull(getStockReservationReleaseByEntryTypeStrategyMap().get(entry.getType()))
					? getStockReservationReleaseByEntryTypeStrategyMap().get(entry.getType())
					: getStockReservationReleaseByEntryTypeStrategyMap().get(OrderEntryType.DEFAULT);
			stockReservationDataList.addAll(strategy.getStockInformationForOrderEntry(entry));
		});
		return stockReservationDataList;
	}

	/**
	 * Method responsible for adjusting stock reservation. Method is wrapped in a transaction and will only commit if the
	 * transaction completes without errors.
	 *
	 * @param stockToReserve
	 * 		the stock to reserve
	 * @param stockToRelease
	 * 		the stock to release
	 * @throws InsufficientStockLevelException
	 * 		the insufficient stock level exception
	 */
	protected void adjustStockReservation(final List<StockReservationData> stockToReserve,
			final List<StockReservationData> stockToRelease) throws InsufficientStockLevelException
	{
		final Transaction tx = Transaction.current();
		tx.begin();

		LOG.info("Reserving " + stockToReserve.size() + " Order Entries in Order");

		for (final StockReservationData stockReservation : stockToReserve)
		{
			try
			{
				if (Objects.isNull(stockReservation.getDate()))
				{
					getStockService().reserve(stockReservation.getProduct(), stockReservation.getWarehouse(),
							stockReservation.getQuantity(), null);
				}
				else
				{
					reservePerDateProduct(stockReservation.getProduct(), stockReservation.getDate(), stockReservation.getQuantity(),
							Collections.singletonList(stockReservation.getWarehouse()));
				}
			}
			catch (final InsufficientStockLevelException e)
			{
				LOG.error(
						"Unable to reseve " + stockReservation.getQuantity() + " stock for Product code "
								+ stockReservation.getProduct().getCode() + " in Warehouse " + stockReservation.getWarehouse().getCode(),
						e);
				tx.rollback();
				throw e;
			}
		}

		if (tx.isRunning())
		{
			tx.commit();
		}

		LOG.info("Releasing " + stockToRelease.size() + " Order Entries in Order");

		for (final StockReservationData stockReservation : stockToRelease)
		{
			if (Objects.isNull(stockReservation.getDate()))
			{
				getStockService().release(stockReservation.getProduct(), stockReservation.getWarehouse(),
						stockReservation.getQuantity(), null);
			}
			else
			{
				releasePerDateProduct(stockReservation.getProduct(), stockReservation.getDate(), stockReservation.getQuantity(),
						Collections.singletonList(stockReservation.getWarehouse()));
			}
		}
	}

	/**
	 * Find matching product.
	 *
	 * @param stockToReserve
	 * 		the stock to reserve
	 * @param stockToRelease
	 * 		the stock to release
	 * @param newStockReservationData
	 * 		the new stock reservation data
	 * @param item1
	 * 		the item 1
	 * @param oldStockReservationData
	 * 		the old stock reservation data
	 */
	protected void findMatchingProduct(final List<StockReservationData> stockToReserve,
			final List<StockReservationData> stockToRelease, final Iterator<StockReservationData> newStockReservationData,
			final StockReservationData item1, final Iterator<StockReservationData> oldStockReservationData)
	{
		while (oldStockReservationData.hasNext())
		{
			final StockReservationData item2 = oldStockReservationData.next();

			if (item1.getProduct().getCode().equalsIgnoreCase(item2.getProduct().getCode())
					&& item1.getWarehouse().getCode().equalsIgnoreCase(item2.getWarehouse().getCode())
					&& isSameDate(item1.getDate(), item2.getDate()))
			{
				if (item1.getQuantity() > item2.getQuantity())
				{
					item1.setQuantity(item1.getQuantity() - item2.getQuantity());
					stockToReserve.add(item1);
				}
				else if (item1.getQuantity() < item2.getQuantity())
				{
					item1.setQuantity(item2.getQuantity() - item1.getQuantity());
					stockToRelease.add(item1);
				}
				// remove as item has been evaluated
				newStockReservationData.remove();
				oldStockReservationData.remove();
				break;
			}
		}
	}

	/**
	 * Is same date boolean.
	 *
	 * @param date1
	 * 		the date 1
	 * @param date2
	 * 		the date 2
	 * @return true if dates are equal or both null
	 */
	protected boolean isSameDate(final Date date1, final Date date2)
	{
		if (Objects.isNull(date1) ^ Objects.isNull(date2))
		{
			return false;
		}
		return (Objects.isNull(date1) || TravelDateUtils.isSameDate(date1, date2));
	}

	/**
	 * Gets products per transport offering.
	 *
	 * @param abstractOrder
	 * 		the abstract order
	 * @return products per transport offering
	 * @deprecated Deprecated since version 2.0. Use {@link #getProductsPerWarehouse(AbstractOrderModel)} instead.
	 */
	@Deprecated
	protected List<StockReservationData> getProductsPerTransportOffering(final AbstractOrderModel abstractOrder)
	{
		final List<StockReservationData> stockReservationDataList = new ArrayList<>();

		abstractOrder.getEntries().forEach(entry ->
		{
			final AddToCartCriteriaType addToCartCriteria = getTravelRestrictionService().getAddToCartCriteria(entry.getProduct());
			final StockReservationCreationStrategy strategy = getStockReservationCreationStrategyMap().get(addToCartCriteria);
			stockReservationDataList.addAll(strategy.create(entry));
		});

		return stockReservationDataList;
	}

	/**
	 * Enable stock reservation boolean.
	 *
	 * @return the boolean
	 */
	protected boolean enableStockReservation()
	{
		return getConfigurationService().getConfiguration().getBoolean(ENABLE_STOCK_RESERVATION_PROPERTY);
	}

	@Override
	public Integer getStockForDate(final ProductModel product, final Date date, final Collection<WarehouseModel> warehouses)
	{
		final StockLevelModel stock = getStockLevelModelForDate(product, date, warehouses);
		return Objects.isNull(stock) ? 0 : stock.getAvailable() - stock.getReserved();
	}

	@Override
	public Long getStockLevelQuantity(final ProductModel product, final Collection<WarehouseModel> warehouses)
	{
		Collection<StockLevelModel> stockLevels = Collections.emptyList();

		if (CollectionUtils.isNotEmpty(warehouses))
		{
			stockLevels = getStockService().getStockLevels(product, warehouses);
		}

		if (CollectionUtils.isEmpty(warehouses) || CollectionUtils.isEmpty(stockLevels))
		{
			return getCommerceStockLevelCalculationStrategy().calculateAvailability(getStockService().getStockLevels(product,
					Arrays.asList(getWarehouseService().getWarehouseForCode(DEFAULT_WAREHOUSE))));
		}

		return getCommerceStockLevelCalculationStrategy().calculateAvailability(stockLevels);
	}

	@Override
	public boolean isStockSystemEnabled(final BaseStoreModel baseStore)
	{
		if (baseStore == null || baseStore.getWarehouses() == null || baseStore.getWarehouses().isEmpty())
		{
			return false;
		}
		if (baseStore.getWarehouses().size() == 1 && DEFAULT_WAREHOUSE.equals(baseStore.getWarehouses().get(0).getCode()))
		{
			return false;
		}
		return true;
	}

	@Override
	public void reservePerDateProduct(final ProductModel product, final Date date, final int quantity,
			final Collection<WarehouseModel> warehouses) throws InsufficientStockLevelException
	{
		final StockLevelModel stock = getStockLevelModelForDate(product, date, warehouses);
		if (Objects.isNull(stock))
		{
			throw new InsufficientStockLevelException(
					String.format("Impossible to reserve the desired quantity for the product %s on %s", product.getCode(),
							TravelDateUtils.convertDateToStringDate(date, TravelservicesConstants.DATE_PATTERN)));
		}
		if (quantity > stock.getAvailable() - stock.getReserved())
		{
			throw new InsufficientStockLevelException(
					String.format("Impossible to reserve the desired quantity for the product %s on %s", product.getCode(),
							TravelDateUtils.convertDateToStringDate(date, TravelservicesConstants.DATE_PATTERN)));
		}
		stock.setReserved(stock.getReserved() + quantity);
		getModelService().save(stock);

	}

	@Override
	public void releasePerDateProduct(final ProductModel product, final Date date, final int quantity,
			final Collection<WarehouseModel> warehouses)
	{
		final StockLevelModel stock = getStockLevelModelForDate(product, date, warehouses);
		if (Objects.isNull(stock))
		{
			return;
		}
		stock.setReserved(stock.getReserved() - quantity);
		getModelService().save(stock);
	}
	
	/**
	 * Gets stock level model for date.
	 *
	 * @param product
	 * 		the product
	 * @param date
	 * 		the date
	 * @param warehouses
	 * 		the warehouses
	 * @return the stock level model for date
	 */
	protected StockLevelModel getStockLevelModelForDate(final ProductModel product, final Date date,
			final Collection<WarehouseModel> warehouses)
	{
		return getTravelStockService().getStockLevelForDate(product, warehouses, date);
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets warehouse service.
	 *
	 * @return the warehouseService
	 */
	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	/**
	 * Sets warehouse service.
	 *
	 * @param warehouseService
	 * 		the warehouseService to set
	 */
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

	/**
	 * Gets stock reservation creation strategy map.
	 *
	 * @return the stockReservationCreationStrategyMap
	 */
	protected Map<AddToCartCriteriaType, StockReservationCreationStrategy> getStockReservationCreationStrategyMap()
	{
		return stockReservationCreationStrategyMap;
	}

	/**
	 * Sets stock reservation creation strategy map.
	 *
	 * @param stockReservationCreationStrategyMap
	 * 		the stockReservationCreationStrategyMap to set
	 */
	public void setStockReservationCreationStrategyMap(
			final Map<AddToCartCriteriaType, StockReservationCreationStrategy> stockReservationCreationStrategyMap)
	{
		this.stockReservationCreationStrategyMap = stockReservationCreationStrategyMap;
	}

	/**
	 * Gets manage stock by entry type strategy map.
	 *
	 * @return manageStockByEntryTypeStreategyMap manage stock by entry type strategy map
	 */
	protected Map<OrderEntryType, TravelManageStockByEntryTypeStrategy> getManageStockByEntryTypeStrategyMap()
	{
		return manageStockByEntryTypeStrategyMap;
	}

	/**
	 * Sets manage stock by entry type strategy map.
	 *
	 * @param manageStockByEntryTypeStrategyMap
	 * 		the manage stock by entry type strategy map
	 */
	public void setManageStockByEntryTypeStrategyMap(
			final Map<OrderEntryType, TravelManageStockByEntryTypeStrategy> manageStockByEntryTypeStrategyMap)
	{
		this.manageStockByEntryTypeStrategyMap = manageStockByEntryTypeStrategyMap;
	}

	/**
	 * Gets travel restriction service.
	 *
	 * @return the travel restriction service
	 */
	protected TravelRestrictionService getTravelRestrictionService()
	{
		return travelRestrictionService;
	}

	/**
	 * Sets travel restriction service.
	 *
	 * @param travelRestrictionService
	 * 		the travel restriction service
	 */
	public void setTravelRestrictionService(final TravelRestrictionService travelRestrictionService)
	{
		this.travelRestrictionService = travelRestrictionService;
	}

	/**
	 * Gets stock reservation release by entry type strategy map.
	 *
	 * @return the stock reservation release by entry type strategy map
	 */
	protected Map<OrderEntryType, StockReservationReleaseByEntryTypeStrategy> getStockReservationReleaseByEntryTypeStrategyMap()
	{
		return stockReservationReleaseByEntryTypeStrategyMap;
	}

	/**
	 * Sets stock reservation release by entry type strategy map.
	 *
	 * @param stockReservationReleaseByEntryTypeStrategyMap
	 * 		the stock reservation release by entry type strategy map
	 */
	public void setStockReservationReleaseByEntryTypeStrategyMap(
			final Map<OrderEntryType, StockReservationReleaseByEntryTypeStrategy> stockReservationReleaseByEntryTypeStrategyMap)
	{
		this.stockReservationReleaseByEntryTypeStrategyMap = stockReservationReleaseByEntryTypeStrategyMap;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
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
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected TravelStockService getTravelStockService()
	{
		return travelStockService;
	}

	@Required
	public void setTravelStockService(final TravelStockService travelStockService)
	{
		this.travelStockService = travelStockService;
	}

}
