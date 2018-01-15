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
package de.hybris.platform.ndcfacades.order;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.order.exceptions.CalculationException;


/**
 * Interface for the NDC Order Entry Facade
 */
public interface NDCOrderEntryFacade
{
	/**
	 * Method that creates the Order entry based on the information contained in the OrderCreateRQ
	 *
	 * @param orderCreateRQ
	 *           orderCreateRQ receive
	 * @param order
	 *           OrderModel that needs to be populated with the entries generated from the orderCreateRQ
	 */
	void createOrderEntries(OrderCreateRQ orderCreateRQ, OrderModel order) throws CalculationException, NDCOrderException;
}
