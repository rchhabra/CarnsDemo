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
package de.hybris.platform.ndcfacades.flight;

import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;


/**
 * Interface for the Flight Price Facade
 */
public interface FlightPriceFacade {

	/**
	 * @param flightPriceRQ
	 * 		the flightPriceRQ data
	 * @return FlightPriceRS object with flight prices
	 */
	FlightPriceRS retrieveFlightPrice(FlightPriceRQ flightPriceRQ) throws NDCOrderException;
}
