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

package de.hybris.platform.ndcfacades.servicelist;

import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;


/**
 * An interface for {@link ServiceListRQ}
 */
public interface ServiceListFacade
{

	/**
	 * This method returns an instance of {@link ServiceListRS} having all the ancillaries for given
	 * {@link ServiceListRQ}
	 *
	 * @param serviceListRQ
	 * 		the service list rq
	 *
	 * @return the service list
	 */
	ServiceListRS getServiceList(ServiceListRQ serviceListRQ);

}
