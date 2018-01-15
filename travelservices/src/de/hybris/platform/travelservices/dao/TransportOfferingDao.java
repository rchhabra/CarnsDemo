/*
 *  
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
 */
package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * TransportOffering Dao interface which provides functionality to manage Transport Offering.
 */
public interface TransportOfferingDao extends Dao
{
	/**
	 * Get a list of TransportOfferingModel by number and departureDate.
	 *
	 * @param number
	 * 		the number of requested transport offering
	 * @param departureDate
	 * 		the departure date of requested transport offering
	 * @return List<TransportOfferingModel> list
	 */
	List<TransportOfferingModel> findTransportOfferings(String number, Date departureDate);

	/**
	 * Return list of transport offerings configured in the system.
	 *
	 * @return List<TransportOfferingModel> list
	 */
	List<TransportOfferingModel> findTransportOfferings();

	/**
	 * Returns a TransportOfferingModel for the given code.
	 *
	 * @param code
	 * 		of transportOffering
	 * @return TransportOfferingModel transport offering model
	 */
	TransportOfferingModel findTransportOffering(String code);

	/**
	 * Returns a list of TransportOfferingModels identified by the given list of codes.
	 *
	 * @param codes
	 * 		list of of transportOffering codes
	 * @return the list of Transport Offering models
	 */
	List<TransportOfferingModel> getTransportOfferings(Collection<String> codes);


}
