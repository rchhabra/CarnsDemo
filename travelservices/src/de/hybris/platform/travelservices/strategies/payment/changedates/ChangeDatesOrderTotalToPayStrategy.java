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

package de.hybris.platform.travelservices.strategies.payment.changedates;

import java.math.BigDecimal;

/**
 * Strategy calculating the total Order amount to be paid after amending order. This strategy calculates the total to
 * pay considering the payment type used during Original Order Process. For e.g. if partial payment option was used
 * during original order, then calculations will be according to partial payment criteria.
 */
public interface ChangeDatesOrderTotalToPayStrategy
{
	/*
	 * Method to calculate order total to pay for change Dates.
	 */
	BigDecimal calculate();
}
