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

package de.hybris.platform.travelfulfilmentprocess.strategy.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.SplittingStrategy;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Objects;


/**
 * Concrete implementation of {@link SplittingStrategy} to handle transport based entries. Actual logic will be
 * performed by the nested strategies called by perform() method inherited from {@link AbstractSplittingStrategyByType}
 */
public class SplittingStrategyByTransportType extends AbstractSplittingStrategyByType implements SplittingStrategy
{

	/**
	 * This method sets additional fields against the created consignment model for transport consignments only. A
	 * transport based consignment is identified by a non null TransportOffering parameter.
	 */
	@Override
	public void afterSplitting(final OrderEntryGroup group, final ConsignmentModel createdOne)
	{
		if (Objects.isNull(group.getParameter(TravelfulfilmentprocessConstants.TRANSPORT_OFFERING)))
		{
			return;
		}
		final TransportOfferingModel transportOffering = (TransportOfferingModel) group
				.getParameter(TravelfulfilmentprocessConstants.TRANSPORT_OFFERING);
		final TravellerModel traveller = (TravellerModel) group.getParameter(TravelfulfilmentprocessConstants.TRAVELLER);
		createdOne.setWarehouse(transportOffering);
		createdOne.setCode(transportOffering.getCode() + "-" + traveller.getLabel());
	}

}
