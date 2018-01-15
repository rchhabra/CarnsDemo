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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.strategies.AmendOrderOfferFilterStrategy;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the {@link AmendOrderOfferFilterStrategy}.
 * The strategy is used to filter out the OrderItem if at least one of its transportOfferings has a status among the not allowed ones.
 */
public class NDCTransportOfferingStatusStrategy implements AmendOrderOfferFilterStrategy
{
	private List<TransportOfferingStatus> notAllowedStatuses;

	@Override
	public boolean filterOffer(final OrderModel orderModel, final List<TransportOfferingModel> transportOfferings,
			final List<String> travellerUIDList)
	{
		final Set<TransportOfferingStatus> transportOfferingStatus = transportOfferings.stream()
				.map(TransportOfferingModel::getStatus).collect(Collectors.toSet());

		return getNotAllowedStatuses().stream().noneMatch(transportOfferingStatus::contains);
	}

	protected List<TransportOfferingStatus> getNotAllowedStatuses()
	{
		return notAllowedStatuses;
	}

	@Required
	public void setNotAllowedStatuses(final List<TransportOfferingStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}
}
