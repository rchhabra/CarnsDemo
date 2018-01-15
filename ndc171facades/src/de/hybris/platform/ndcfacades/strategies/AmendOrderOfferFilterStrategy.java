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
package de.hybris.platform.ndcfacades.strategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Strategy to validate if an amendment to an ancillaries or a seat is valid
 */
public interface AmendOrderOfferFilterStrategy
{

	/**
	 * Returns false if the amendment cannot be performed on the specified {@link OrderModel} for the provided  list of
	 * {@link TransportOfferingModel}
	 *
	 * @param orderModel
	 * 		the order model
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param travellerUIDList
	 * 		the traveller uid list
	 *
	 * @return the boolean
	 */
	boolean filterOffer(OrderModel orderModel, List<TransportOfferingModel> transportOfferings, List<String> travellerUIDList);
}
