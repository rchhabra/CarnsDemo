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
package de.hybris.platform.travelbackofficeservices.services;

import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.List;


/**
 * The interface Backoffice transport offering service.
 */
public interface BackofficeTransportOfferingService extends TransportOfferingService
{
	/**
	 * Method takes a list of transport offerings and updates them with the origin and destination facilities and
	 * locations
	 *
	 * @param transportOfferings
	 */
	void updateTransportOfferingsWithLocations(List<TransportOfferingModel> transportOfferings);

	/**
	 * Returns a list of {@link TransportOfferingModel} having no ScheduleConfiguration associated
	 * @return the list of TransportOfferingModel
	 */
	List<TransportOfferingModel> findTransportOfferingsWithoutSchedule();
}
