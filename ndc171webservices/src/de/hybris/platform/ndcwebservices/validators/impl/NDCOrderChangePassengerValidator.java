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
package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * NDC {@link OrderChangeRQ} {@link Passenger} Validator
 */
public class NDCOrderChangePassengerValidator extends NDCAbstractPassengerTypeValidator<OrderChangeRQ>
{
	@Override
	public void validate(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
		final List<OrderItem> orderItems = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem();
		final Set<PassengerType> passengers = orderItems.stream()
				.flatMap(orderItem -> orderItem.getAssociations().getPassengers().getPassengerReferences().stream())
				.filter(PassengerType.class::isInstance).map(PassengerType.class::cast).collect(Collectors.toSet());

		if (!validatePTCValues(orderChangeRQ.getDataLists().getPassengerList().getPassenger(), errorsType))
		{
			return;
		}

		final NDCActionType ndcActionType = NDCActionType.valueOf(actionType);
		if ((NDCActionType.REMOVE_PASSENGER.equals(ndcActionType) || NDCActionType.ADD_ACCOMMODATION.equals(ndcActionType)
				|| NDCActionType.REMOVE_ACCOMMODATION.equals(ndcActionType))
				&& CollectionUtils.size(passengers) > NdcwebservicesConstants.MAX_REMOVE_PASSENGER_QUANTITY)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_TRAVELERS_EXCEEDED));
			return;
		}

		final Optional<PassengerType> optionalPassengerWithoutProfileId = passengers.stream()
				.filter(passenger -> StringUtils.isEmpty(passenger.getProfileID())).findAny();
		if (optionalPassengerWithoutProfileId.isPresent())
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MISSING_ORDER_CHANGE_PASSENGER_INFORMATION));
		}
	}
}
