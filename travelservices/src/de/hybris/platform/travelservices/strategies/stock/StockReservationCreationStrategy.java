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
 * Strategy to create the {@link StockReservationData}
 */
public interface StockReservationCreationStrategy
{

	/**
	 * Creates a list of {@link StockReservationData} for the given AbstractOrderEntryModel
	 *
	 * @param entry
	 * 		the abstractOrderEntryModel used to create the list of stockReservationData
	 * @return the list of stockReservationData
	 */
	List<StockReservationData> create(AbstractOrderEntryModel entry);

}
