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
package de.hybris.platform.ndcfacades.strategies.impl;


import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Strategy to add to the specified order a PER_PAX ancillary.
 */
public class AddToOrderPerPaxStrategy extends AbstractAddAncillariesToOrderRestrictionStrategy
{
	@Override
	public void addAncillary(final OrderModel order, final List<TravellerModel> travellers, final ProductModel ancillaryProduct,
			final List<TransportOfferingModel> transportOfferings, final String offerItemID, final String routeCode,
			final int originDestinationRefNumber) throws NDCOrderException
	{
		addAncillary(order, travellers, ancillaryProduct, transportOfferings, offerItemID, routeCode, originDestinationRefNumber,
				new LinkedList<>());
	}

	@Override
	public void addAncillary(final OrderModel order, final List<TravellerModel> travellers, final ProductModel ancillaryProduct,
			final List<TransportOfferingModel> transportOfferings, final String offerItemID, final String routeCode,
			final int originDestinationRefNumber, final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		checkTravellers(travellers, ancillaryProduct);

		for (final TravellerModel traveller : travellers)
		{
			getNdcProductFacade().checkIfProductCanBeAddedToTraveller(order, traveller, ancillaryProduct,
					NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY, Collections.emptyList(), null);

			add(order, ancillaryProduct, traveller, Collections.emptyList(), offerItemID, null,
					originDestinationRefNumber, orderEntries);
		}
	}
}
