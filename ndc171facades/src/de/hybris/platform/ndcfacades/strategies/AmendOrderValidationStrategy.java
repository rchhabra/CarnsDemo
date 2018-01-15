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

import java.util.List;


/**
 * Strategy to validate if an ancillary can be added or removed to the specified order
 */
public interface AmendOrderValidationStrategy
{

	/**
	 * Method to validate that validates if an ancillary can be added or removed to the specified order
	 *
	 * @param order
	 * 		the order to which the ancillary needs to be added
	 * @param productCode
	 * 		as the product to be added or removed from the order
	 * @param qty
	 * 		as the quantity to add/remove
	 * @param travellerCode
	 * 		as the code of the traveller to whom the product is added or removed
	 * @param transportOfferingCodes
	 * 		as the codes of the transportOfferings the product is added to or removed from
	 * @param travelRouteCode
	 * 		as the travelRouteCode the product is added to or removed from
	 * @return valid add to order if true, false otherwise.
	 */
	boolean validateAmendOrder(OrderModel order, String productCode, long qty, String travellerCode,
			List<String> transportOfferingCodes, String travelRouteCode);
}
