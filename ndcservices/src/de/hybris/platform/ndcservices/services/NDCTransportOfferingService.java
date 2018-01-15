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
package de.hybris.platform.ndcservices.services;

import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


/**
 * Interface that exposes {@link TransportOfferingModel} through their code
 */
public interface NDCTransportOfferingService
{
	/**
	 * Returns a list of {@link TransportOfferingModel} based on the codes provided. Throws {@link NDCOrderException} if
	 * at least one {@link TransportOfferingModel} departure in the past or in the future but within the
	 * MIN_BOOKING_ADVANCE_TIME
	 *
	 * @param transportOfferingCodes
	 * @return
	 */
	List<TransportOfferingModel> getTransportOfferings(List<String> transportOfferingCodes) throws NDCOrderException;

	/**
	 * Returns a {@link TransportOfferingModel} based on the code provided.
	 *
	 * @param transportOfferingCode
	 * @return
	 */
	TransportOfferingModel getTransportOffering(String transportOfferingCode);

	/**
	 * Checks, in case of a return flight, if the departure of the inbound is subsequent of the arrival of the outbound
	 * plus the MIN_BOOKING_ADVANCE_TIME
	 *
	 * @param transportOfferings
	 * @return
	 */
	boolean isValidReturnDate(Map<String, List<TransportOfferingModel>> transportOfferings);

	/**
	 * Method to return the departure ZonedDateTime of the provided TransportOffering
	 *
	 * @param transportOffering
	 * @return
	 */
	ZonedDateTime getDepartureZonedDateTimeFromTransportOffering(TransportOfferingModel transportOffering);

	/**
	 * Method to return the arrival ZonedDateTime of the provided TransportOffering
	 *
	 * @param transportOffering
	 * @return
	 */
	ZonedDateTime getArrivalZonedDateTimeFromTransportOffering(TransportOfferingModel transportOffering);

	/**
	 * Check if the provided transport offering model is in the future taking in account the current time, timezone and
	 * the minimum booking advance time
	 *
	 * @param transportOffering
	 * @return
	 */
	boolean isValidDate(final TransportOfferingModel transportOffering);
}
