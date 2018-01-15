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
 */

package de.hybris.platform.travelacceleratorstorefront.strategies.asm;

import de.hybris.platform.core.model.order.CartModel;


/**
 * Interface for the ASM redirect strategies based on the journey type.
 */
public interface AssistedServiceRedirectByJourneyTypeStrategy
{
	/**
	 * Returns the redirect path.
	 *
	 * @param cartModel
	 * 		as the cart model
	 *
	 * @return a string corresponding to the redirect path
	 */
	String getRedirectPath(final CartModel cartModel);
}
