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
package de.hybris.platform.ndcfacades.strategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;


/**
 * Strategy to validate the amend an order depending on the provided action type in the {@link OrderChangeRQ}
 */
public interface AmendOrderStrategy
{

	/**
	 * Creates and return the amended {@link OrderModel} that correspond to the clone of the originalOrder plus the modified
	 * order entries
	 *
	 * @param originalOrder
	 * 		the original order
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @return order model
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	OrderModel amendOrder(OrderModel originalOrder, OrderChangeRQ orderChangeRQ) throws NDCOrderException;
}
