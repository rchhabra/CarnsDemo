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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Interface to handle operations related to rate plans
 */
public interface RatePlanService
{
	/**
	 * Return the correct guarantee to apply given the order entry group and the date when we want to apply it
	 *
	 * @param group
	 *           the group
	 * @param date
	 *           the date
	 * @return guarantee to apply
	 */
	GuaranteeModel getGuaranteeToApply(AccommodationOrderEntryGroupModel group, Date date);

	/**
	 * Return the correct guarantee to apply given the order entry group and the date when we want to apply it
	 *
	 * @param group
	 *           the group
	 * @param startingDate
	 *           the starting date
	 * @param currentDate
	 *           the current date
	 * @param date
	 *           the date
	 * @return guarantee to apply
	 */
	GuaranteeModel getGuaranteeToApply(AccommodationOrderEntryGroupModel group, Date startingDate, Date currentDate);


	/**
	 * Return amount to be paid as guarantee, applying the guarantee model on the price.
	 *
	 * @param guaranteeToApply
	 *           object of GuaranteeModel
	 * @param roomRatePrice
	 *           the price on which guarantee is to be applied
	 * @return guarantee amount
	 */
	Double getAppliedGuaranteeAmount(GuaranteeModel guaranteeToApply, BigDecimal roomRatePrice);

	/**
	 * Returns the RatePlanModel for a given code
	 *
	 * @param ratePlanCode
	 * 		the code of the ratePlan to get
	 * @return the RatePlanModel corresponding to the given code
	 */
	RatePlanModel getRatePlanForCode(String ratePlanCode);

}
