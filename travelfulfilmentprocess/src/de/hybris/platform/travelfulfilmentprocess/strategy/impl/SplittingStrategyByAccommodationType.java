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
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.Objects;
import java.util.StringJoiner;


/**
 * Concrete implementation of {@link SplittingStrategy} to handle accommodation based entries. Actual logic will be
 * performed by the nested strategies called by perform() method inherited from {@link AbstractSplittingStrategyByType}
 */
public class SplittingStrategyByAccommodationType extends AbstractSplittingStrategyByType implements SplittingStrategy
{

	/**
	 * This method sets additional fields against the created consignment model for accommodation consignments only. An
	 * accommodation based consignment is identified by a non null AccommodationOffering parameter.
	 */
	@Override
	public void afterSplitting(final OrderEntryGroup group, final ConsignmentModel createdOne)
	{
		if (Objects.isNull(group.getParameter(TravelfulfilmentprocessConstants.ACCOMMODATION_OFFERING)))
		{
			return;
		}
		final AccommodationOfferingModel accommodationOffering = (AccommodationOfferingModel) group
				.getParameter(TravelfulfilmentprocessConstants.ACCOMMODATION_OFFERING);
		final AccommodationModel accommodation = (AccommodationModel) group
				.getParameter(TravelfulfilmentprocessConstants.ACCOMMODATION);
		final Integer refNumber = (Integer) group.getParameter(TravelfulfilmentprocessConstants.REF_NUMBER);
		createdOne.setWarehouse(accommodationOffering);
		final StringJoiner joiner = new StringJoiner("-");
		joiner.add(accommodation.getCode()).add(refNumber.toString());
		createdOne.setCode(joiner.toString());
	}

}
