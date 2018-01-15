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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.List;


/**
 * Interface for NDC PaymentTransaction Facade
 */
public interface NDCPaymentTransactionFacade
{
	/**
	 * Method that creates the {@link PaymentTransactionModel} based on the information contained in the {@link OrderCreateRQ}
	 *
	 * @param orderCreateRQ
	 * 		orderCreateRQ receive
	 * @param orderModel
	 * 		OrderModel that needs to be populated with PaymentTransaction generated from the orderCreateRQ
	 */
	void createPaymentTransaction(OrderCreateRQ orderCreateRQ, OrderModel orderModel);

	/**
	 * Method that creates the {@link PaymentTransactionModel} based on the information contained in the
	 * {@link OrderChangeRQ}
	 *
	 * @param totalToPay
	 *           total to pay calculated on the list of {@link AbstractOrderEntryModel} added to the order
	 * @param orderModel
	 *           OrderModel that needs to be populated with PaymentTransaction generated from the orderCreateRQ
	 * @param orderEntries
	 *           the order entries
	 */
	void createPaymentTransaction(BigDecimal totalToPay, OrderModel orderModel, List<AbstractOrderEntryModel> orderEntries);

	/**
	 * Method that creates the pay later {@link PaymentTransactionModel}  for the specified {@link OrderModel}
	 *
	 * @param orderCreateRQ
	 * 		orderCreateRQ receive
	 * @param order
	 * 		OrderModel that needs to be populated with the pay later transaction
	 */
	void createPayLaterTransaction(OrderCreateRQ orderCreateRQ, OrderModel order);
}
