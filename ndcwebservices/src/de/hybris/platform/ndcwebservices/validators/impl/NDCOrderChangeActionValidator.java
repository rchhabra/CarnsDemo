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

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.Location;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCoreChangeType;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC Order Change Action Validator
 */
public class NDCOrderChangeActionValidator implements NDCRequestValidator<OrderChangeRQ>
{
	private ConfigurationService configurationService;
	private EnumerationService enumerationService;

	@Override
	public void validate(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		if (!isActionTypeSpecified(orderChangeRQ, errorsType))
		{
			return;
		}

		if (!validateAction(orderChangeRQ, errorsType))
		{
			return;
		}

		if (isAccommodationAction(orderChangeRQ.getQuery().getOrder().getActionType().getValue()))
		{
			if (!validateSeatItem(orderChangeRQ, errorsType))
			{
				return;
			}

			validateSeatLocations(orderChangeRQ, errorsType);
		}
	}

	/**
	 * Returns true if the specified {@link OrderCoreChangeType.ActionType} is {@link NDCActionType#ADD_ACCOMMODATION} or
	 * {@link NDCActionType#REMOVE_ACCOMMODATION}
	 *
	 * @param actionType
	 * 		the action type
	 *
	 * @return boolean
	 */
	protected boolean isAccommodationAction(final String actionType)
	{
		return StringUtils.equalsIgnoreCase(actionType, NDCActionType.ADD_ACCOMMODATION.getCode())
				|| StringUtils.equalsIgnoreCase(actionType, NDCActionType.REMOVE_ACCOMMODATION.getCode());
	}

	/**
	 * This method validates {@link Location} element of each and every {@link ListOfSeatType} of each and every
	 * {@link OrderItem}.
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateSeatLocations(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		for (final OrderItem orderItem : orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem())
		{
			final ListOfSeatType seat = ((ListOfSeatType) orderItem.getSeatItem().getSeatReference().get(0).getValue());
			if (!validateSeatLocation(seat, errorsType))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * This method validates {@link Location} element of each and every {@link ListOfSeatType}.
	 *
	 * @param seat
	 * 		the seat
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateSeatLocation(final ListOfSeatType seat, final ErrorsType errorsType)
	{
		if (Objects.isNull(seat.getLocation().getColumn()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_COLUMN));
			return false;
		}

		if (Objects.isNull(seat.getLocation().getRow()))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_ROW));
			return false;
		}

		if (Objects.isNull(seat.getLocation().getRow().getNumber())
				|| StringUtils.isEmpty(seat.getLocation().getRow().getNumber().getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_ROW_NUMBER));
			return false;
		}
		return true;
	}

	/**
	 * This method validates existence of {@link SeatItem} if ADD_ACCOMMODATION/REMOVE_ACCOMMODATION action is provided
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateSeatItem(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		final List<OrderItem> orderItems = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem();
		if (!orderItems.stream()
				.filter(orderItem -> Objects.isNull(orderItem.getSeatItem()) || orderItem.getSeatItem().getSeatReference().isEmpty())
				.collect(Collectors.toList()).isEmpty())
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_SEAT_ITEM));
			return false;
		}
		return true;
	}

	/**
	 * Checks if the {@link OrderCoreChangeType.ActionType} element is present
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean isActionTypeSpecified(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		if (Objects.isNull(orderChangeRQ.getQuery().getOrder().getActionType()) || StringUtils
				.isEmpty(orderChangeRQ.getQuery().getOrder().getActionType().getValue()))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_ACTION_TYPE));
			return false;
		}
		return true;
	}

	/**
	 * Validates the Action in the OrderChange based on the allowed once
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateAction(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		final NDCActionType enumerationValue = getEnumerationService().getEnumerationValue(NDCActionType.class,
				orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase());
		if (Objects.isNull(enumerationValue))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_ACTION));
			return false;
		}
		return true;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
