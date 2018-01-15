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

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;


/**
 * Interface for strategy creating objects representing payment options
 */
public interface PaymentOptionCreationStrategy
{
	/**
	 * Creates a {@link PaymentOptionInfo} from a cart/order for a specific scenario
	 *
	 * @param abstractOrder
	 * 		the abstract order
	 * @return payment option info
	 */
	public PaymentOptionInfo create(AbstractOrderModel abstractOrder);
}
