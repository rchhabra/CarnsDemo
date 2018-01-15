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
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;


/**
 * Interface for the NDC Order Facade
 */
public interface NDCOrderFacade
{
	/**
	 * @param orderCreateRQ
	 *
	 * @return the order that has been payed
	 */
	OrderViewRS payOrder(OrderCreateRQ orderCreateRQ) throws NDCOrderException;

	/**
	 * Creates an order based on the OrderCreateRQ
	 *
	 * @param orderCreateRQ
	 * 		the order Create request
	 *
	 * @return OrderViewRS order created
	 */
	OrderViewRS orderCreate(OrderCreateRQ orderCreateRQ) throws NDCOrderException;

	/**
	 * Retrieves an order based on the orderRetrieveRQ
	 *
	 * @param orderRetrieveRQ
	 * 		the order retrieve request
	 *
	 * @return corresponding OrderViewRS
	 */
	OrderViewRS retrieveOrder(OrderRetrieveRQ orderRetrieveRQ) throws NDCOrderException;

	/**
	 * Checks if the order model is being amended
	 *
	 * @param orderModel
	 * 		the order retrieve request
	 *
	 * @return if the order model is being amended
	 */
	Boolean isAmendmentOrder(OrderModel orderModel);

	/**
	 * Change an order based on the OrderChangeRQ
	 *
	 * @param orderChangeRQ
	 * 		the order change request
	 *
	 * @return corresponding OrderViewRS
	 */
	OrderViewRS changeOrder(OrderChangeRQ orderChangeRQ) throws NDCOrderException;

	/**
	 * Retrieves the code of the original order
	 *
	 * @param orderModel
	 *
	 * @return if present, return the code of the original order
	 */
	String getOriginalOrderCode(OrderModel orderModel);
}
