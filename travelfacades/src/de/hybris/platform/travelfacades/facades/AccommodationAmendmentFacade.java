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

package de.hybris.platform.travelfacades.facades;

/**
 * Interface to be used during accommodation amendment
 */
public interface AccommodationAmendmentFacade
{

	/**
	 * Creates a cart from order and attaches it to the session to start add accommodation amendment process
	 *
	 * @param orderCode
	 *           the order code
	 * @return boolean
	 */
	Boolean startAmendment(String orderCode);

}
