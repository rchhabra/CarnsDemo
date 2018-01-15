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

package de.hybris.platform.travelservices.strategies.payment;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;


/**
 * Interface for creating payment transaction entries under given circumstances
 */
public interface PaymentTransactionEntryCreationStrategy
{
	/**
	 * Method to create transaction entries
	 *
	 * @param order
	 * @param amount
	 * @param entries
	 */
	void createTransactionEntries(AbstractOrderModel order, List<AbstractOrderEntryModel> entries);
}
