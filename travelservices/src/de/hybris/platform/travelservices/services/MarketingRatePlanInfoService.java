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

import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;


/**
 * MarketingRatePlanInfo Service interface which provides functionality to manage Marketing Rate Plan Info.
 */
public interface MarketingRatePlanInfoService
{
	/**
	 * Returns the MarketingRatePlanInfoModel for a given code
	 *
	 * @param marketingRatePlanInfoCode
	 * 		the code of the marketingRatePlanInfo to get
	 * @return the MarketingRatePlanInfoModel corresponding to the given code
	 */
	MarketingRatePlanInfoModel getMarketingRatePlanInfoForCode(String marketingRatePlanInfoCode);
}
