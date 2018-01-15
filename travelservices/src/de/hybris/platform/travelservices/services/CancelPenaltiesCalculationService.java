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

import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;


/**
 * Interface for operations about cancel penalties calculation
 */
public interface CancelPenaltiesCalculationService
{
	/**
	 * Retrieves the cancel penalty currently active
	 *
	 * @param cancelPenalties
	 * @param checkInDate
	 * @param plannedAmount
	 * @return
	 */
	CancelPenaltyModel getActiveCancelPenalty(Collection<CancelPenaltyModel> cancelPenalties, Date checkInDate,
			BigDecimal plannedAmount);

	/**
	 * This method returns the deadline Date for the given CancelPenalty. If both RelativeDeadline and AbsoluteDeadline
	 * are defined, the earliest one will be returned.
	 *
	 * @param cancelPenalty
	 * @param checkInDate
	 *
	 * @return the Date corresponding to the deadline
	 */
	Date getCancelPenaltyDeadline(CancelPenaltyModel cancelPenalty, Date checkInDate);


	/**
	 * This method returns the amount to retain from the refund. If both fixedAmount and percentageAmount are defined in
	 * the cancelPenalty, the highest one will be returned.
	 *
	 * @param cancelPenalty
	 * @param plannedAmount
	 *
	 * @return the BigDecimal corresponding to the amount of the cancelPenalty
	 */
	BigDecimal getCancelPenaltyAmount(CancelPenaltyModel cancelPenalty, BigDecimal plannedAmount);

}
