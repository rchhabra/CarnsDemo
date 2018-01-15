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

package de.hybris.platform.ndcfacades.serviceprice;

import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS;


/**
 * An interface for {@link ServicePriceRQ}
 */
public interface ServicePriceFacade
{

	/**
	 * This method returns an instance of {@link ServicePriceRS} having all the services for given {@link ServicePriceRQ}
	 *
	 * @param servicePriceRQ
	 * 		the service price rq
	 *
	 * @return the service price
	 */
	ServicePriceRS getServicePrice(ServicePriceRQ servicePriceRQ);

}
