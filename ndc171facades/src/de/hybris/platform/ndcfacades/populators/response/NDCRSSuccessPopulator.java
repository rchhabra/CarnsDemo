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

package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.ndcfacades.ndc.SuccessType;


/**
 * An abstract class for NDC response success data, it will be extended by subclasses to populate
 * AirShopping/BaggageList/ServiceList response.
 */
public abstract class NDCRSSuccessPopulator
{
	/**
	 * Gets ndc success type.
	 *
	 * @return ndc success type
	 */
	protected SuccessType getNDCSuccessType()
	{
		return new SuccessType();
	}
}
