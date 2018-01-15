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

package de.hybris.platform.travelfacades.facades.accommodation.strategies;

import de.hybris.platform.commercefacades.accommodation.CancelPenaltyData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;


/**
 * Abstract strategy to create and update the formatted description of the {@linkplain CancelPenaltyData} for the
 * {@linkplain RoomStayData}
 */
public interface CancelPenaltiesDescriptionCreationStrategy
{

	/**
	 * Updates the formatted description of the cancel penalty
	 * 
	 * @param ratePlan
	 * @param roomStay
	 */
	void updateCancelPenaltiesDescription(RatePlanModel ratePlan, RoomStayData roomStay);

}
