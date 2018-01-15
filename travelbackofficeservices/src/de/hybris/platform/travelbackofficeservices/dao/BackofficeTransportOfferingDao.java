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

package de.hybris.platform.travelbackofficeservices.dao;

import de.hybris.platform.travelservices.dao.TransportOfferingDao;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Interface for a backoffice specific transport offering dao
 */
public interface BackofficeTransportOfferingDao extends TransportOfferingDao
{
	/**
	 * Returns a list of {@link TransportOfferingModel} not having any {@link ScheduleConfigurationModel} associated
	 * ordered by departure date
	 * @return
	 */
	List<TransportOfferingModel> findTransportOfferingsWithoutSchedule();
}
