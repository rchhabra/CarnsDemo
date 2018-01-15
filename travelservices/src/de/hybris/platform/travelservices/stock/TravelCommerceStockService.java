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

package de.hybris.platform.travelservices.stock;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collection;
import java.util.Date;


/**
 * Service that collects functionality for stock levels related with transport offering (warehouse). The service offers:
 * <ul>
 * <li><strong>getStockLevel</strong> get stock level for a particular product on a particular transport offering
 * <li><strong>reserve</strong> stock for a particular order
 * <li><strong>release</strong> stock on a particular order
 * <li><strong>releaseForAmendment</strong> stock for an existing order
 * </ul>
 */
public interface TravelCommerceStockService extends CommerceStockService
{
	/**
	 * Gets stock level.
	 *
	 * @param product
	 * 		the product for which the stock level is checked
	 * @param transportOfferings
	 * 		the collection of transport offering model for the given product.
	 * @return Long stock level
	 */
	Long getStockLevel(final ProductModel product, final Collection<TransportOfferingModel> transportOfferings);

	/**
	 * Reserves products in the specified warehouse for a given Order. This method is wrapped in a transaction and so
	 * will only commit once the transaction completes without errors.
	 *
	 * @param currentOrder
	 * 		the current order
	 * @throws InsufficientStockLevelException
	 * 		the insufficient stock level exception
	 */
	void reserve(final AbstractOrderModel currentOrder) throws InsufficientStockLevelException;

	/**
	 * Release products in the specified warehouse for a given Order.
	 *
	 * @param currentOrder
	 * 		the current order
	 * @throws IllegalArgumentException
	 * @throws SystemException
	 */
	void release(final AbstractOrderModel currentOrder);

	/**
	 * Method compares the newOrder with the originalOrder and reserves stock accordingly.
	 *
	 * @param newOrder
	 * 		the new order
	 * @param originalOrder
	 * 		the original order
	 * @throws InsufficientStockLevelException
	 * 		the insufficient stock level exception
	 */
	void adjustStockReservationForAmmendment(final AbstractOrderModel newOrder, final AbstractOrderModel originalOrder)
			throws InsufficientStockLevelException;

	/**
	 * This method returns the product availability for a given date
	 *
	 * @param product
	 * 		the product
	 * @param date
	 * 		the date
	 * @param warehouses
	 * 		the warehouses
	 * @return the availability
	 */
	Integer getStockForDate(ProductModel product, Date date, Collection<WarehouseModel> warehouses);

	/**
	 * This method calculates availability for a given product
	 *
	 * @param product
	 * 		the product
	 * @param warehouses
	 * 		the warehouses
	 * @return a long value representing the stock level
	 */
	Long getStockLevelQuantity(ProductModel product, Collection<WarehouseModel> warehouses);

	/**
	 * This method performs reservation for an accommodation for a given date
	 *
	 * @param product
	 * 		the product
	 * @param date
	 * 		the date
	 * @param quantity
	 * 		the quantity
	 * @param warehouses
	 * 		the warehouses
	 * @throws InsufficientStockLevelException
	 * 		the insufficient stock level exception
	 */
	void reservePerDateProduct(ProductModel product, Date date, int quantity, Collection<WarehouseModel> warehouses)
			throws InsufficientStockLevelException;

	/**
	 * This method performs stock releasing for an accommodation for a given date
	 *
	 * @param product
	 * 		the product
	 * @param date
	 * 		the date
	 * @param quantity
	 * 		the quantity
	 * @param warehouses
	 * 		the warehouses
	 */
	void releasePerDateProduct(ProductModel product, Date date, int quantity, Collection<WarehouseModel> warehouses);
}
