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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Strategy to add ancillaries to the order
 */
public interface AddAncillariesToOrderRestrictionStrategy
{
	/**
	 * Adds ancillaries to the order based on its travel restriction and the lists of {@link TravellerModel} and
	 * {@link TransportOfferingModel} provided
	 *
	 * @param order
	 * 		the order
	 * @param travellers
	 * 		the travellers
	 * @param ancillaryProduct
	 * 		the ancillary product
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param offerItemID
	 * 		the offer item id
	 * @param routeCode
	 * 		the route code
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	void addAncillary(OrderModel order, List<TravellerModel> travellers, ProductModel ancillaryProduct,
			List<TransportOfferingModel> transportOfferings, String offerItemID, String routeCode, int originDestinationRefNumber)
			throws NDCOrderException;

	/**
	 * Adds ancillaries to the order based on its travel restriction and the lists of {@link TravellerModel} and
	 * {@link TransportOfferingModel} provided and populates a list with the modified or created {@link AbstractOrderEntryModel}
	 *
	 * @param order
	 * 		the order
	 * @param travellers
	 * 		the travellers
	 * @param ancillaryProduct
	 * 		the ancillary product
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param offerItemID
	 * 		the offer item id
	 * @param routeCode
	 * 		the route code
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param orderEntries
	 * 		the order entries
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	void addAncillary(OrderModel order, List<TravellerModel> travellers, ProductModel ancillaryProduct,
			List<TransportOfferingModel> transportOfferings, String offerItemID, String routeCode,
			int originDestinationRefNumber, List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException;
}
