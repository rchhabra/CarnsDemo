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
*/

package de.hybris.platform.travelrulesengine.calculation;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.travelrulesengine.rao.BookingRAO;
import de.hybris.platform.travelrulesengine.rao.FeeRAO;
import de.hybris.platform.travelrulesengine.rao.RefundActionRAO;


/**
 * Interface to travel rules calculation service methods
 */
public interface TravelRuleEngineCalculationService
{
	/**
	 * Method to add fee RAO
	 *
	 * @param cartRao
	 * 		the cart rao
	 * @return fee rao
	 */
	FeeRAO addFee(CartRAO cartRao);

	/**
	 * Method to add refund fee RAO
	 *
	 * @param bookingRao
	 * 		the booking rao
	 * @return refund action rao
	 */
	RefundActionRAO addRefundFeeAction(BookingRAO bookingRao);
}
