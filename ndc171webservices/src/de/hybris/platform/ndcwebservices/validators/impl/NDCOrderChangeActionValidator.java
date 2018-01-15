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
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCoreChangeType;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
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

		if (!isValidActionType(orderChangeRQ, errorsType))
		{
			return;
		}

		final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
		final NDCActionType ndcActionType = NDCActionType.valueOf(actionType);

		if (Objects.equals(NDCActionType.ADD_ACCOMMODATION, ndcActionType)
				|| Objects.equals(NDCActionType.REMOVE_ACCOMMODATION, ndcActionType))
		{
			if (!validateSeatItem(orderChangeRQ, errorsType))
			{
				return;
			}
			if (Objects.equals(NDCActionType.ADD_ACCOMMODATION, ndcActionType))
			{
				validateSeatReferences(orderChangeRQ, errorsType);
			}
		}
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
		if (orderItems.stream().anyMatch(orderItem -> Objects.isNull(orderItem.getSeatItem())))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_SEAT_ITEM));
			return false;
		}

		return true;
	}


	/**
	 * Validate seat references.
	 *
	 * @param orderChangeRQ
	 *           the order change RQ
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateSeatReferences(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		final List<OrderItem> orderItems = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem();
		for (final OrderItem orderItem : orderItems)
		{
			final SeatItem seatItem = orderItem.getSeatItem();
			if (CollectionUtils.isEmpty(seatItem.getSeatReference()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_REF_ELEMENT));
				return false;
			}

			final ListOfSeatType seat = seatItem.getSeatReference().stream()
					.filter(seatReference -> seatReference.getValue() instanceof ListOfSeatType)
					.map(seatReference -> (ListOfSeatType) seatReference.getValue()).findFirst().orElse(null);
			if (Objects.isNull(seat))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_SEAT_REFERENCE));
				return false;
			}

			if (CollectionUtils.isEmpty(seatItem.getRefs()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_REF_ATTR));
				return false;
			}

			if (CollectionUtils.size(seatItem.getRefs()) > 1)
			{
				addError(errorsType, getConfigurationService().getConfiguration()
						.getString(NdcwebservicesConstants.MAX_SEGMENT_REFERENCE_PER_SEAT_EXCEEDED));
				return false;
			}

			if (!validateSeatLocation(seat.getLocation(), errorsType))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * This method validates {@link Location} element of each and every {@link ListOfSeatType}.
	 *
	 * @param seatLocationType
	 *           the seat
	 * @param errorsType
	 *           the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateSeatLocation(final SeatLocationType seatLocationType, final ErrorsType errorsType)
	{
		if (Objects.isNull(seatLocationType.getColumn()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_COLUMN));
			return false;
		}

		if (Objects.isNull(seatLocationType.getRow()))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_ROW));
			return false;
		}

		if (Objects.isNull(seatLocationType.getRow().getNumber())
				|| StringUtils.isEmpty(seatLocationType.getRow().getNumber().getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_ROW_NUMBER));
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
	protected boolean isValidActionType(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
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
