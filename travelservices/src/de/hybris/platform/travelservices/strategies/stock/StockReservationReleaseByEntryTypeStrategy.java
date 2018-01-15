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

package de.hybris.platform.travelservices.strategies.stock;

import de.hybris.platform.commerceservices.stock.reservation.StockReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.List;


/**
 * Interface to build {@link StockReservationData} object to apply reservation/release policies
 */
public interface StockReservationReleaseByEntryTypeStrategy
{
	/**
	 * Gets stock information for order entry.
	 *
	 * @param entry
	 * 		the entry to scan
	 * @return a list of related reservation data objects built according to product type and warehouse
	 */
	List<StockReservationData> getStockInformationForOrderEntry(AbstractOrderEntryModel entry);
}
