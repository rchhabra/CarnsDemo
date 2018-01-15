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


/**
 * Interface for NDC PaymentAddress Facade
 */
public interface NDCPaymentAddressFacade
{

	/**
	 * Method that creates the PaymentAddress based on the information contained in the OrderCreateRQ
	 *
	 * @param orderCreateRQ
	 *           orderCreateRQ receive
	 * @param orderModel
	 *           OrderModel that needs to be populated with PaymentAddress generated from the orderCreateRQ
	 * @throws NDCOrderException
	 */
	void createPaymentAddress(OrderCreateRQ orderCreateRQ, OrderModel orderModel) throws NDCOrderException;
}
