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
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.facades.TravelRouteFacade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * NDC {@link OrderChangeRQ} {@link OrderItem} Validator
 */
public class NDCOrderChangeOfferValidator implements NDCRequestValidator<OrderChangeRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderChangeOfferValidator.class);

	private ConfigurationService configurationService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private TravelRouteFacade travelRouteFacade;
	private EnumerationService enumerationService;

	@Override
	public void validate(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		final List<OrderItem> orderItems = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem();

		try
		{
			final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
			if (!validateOrderItemAssociations(actionType, orderItems, errorsType))
			{
				return;
			}

			validateOrderItemsIdPerPTC(orderItems, errorsType);
		}
		catch (final NDCOrderException e)
		{
			LOG.debug(e);
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
		}
	}

	/**
	 * Validates, depending on the {@link ActionType} the required associations
	 *
	 * @param actionType
	 * 		the action type
	 * @param orderItems
	 * 		the order items
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateOrderItemAssociations(final String actionType, final List<OrderItem> orderItems,
			final ErrorsType errorsType)
	{
		final NDCActionType ndcActionType = getEnumerationService().getEnumerationValue(NDCActionType.class, actionType);

		for (final OrderItem orderItem : orderItems)
		{
			if (Objects.isNull(orderItem.getAssociations()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_ASSOCIATIONS));
				return false;
			}


			if (Objects.isNull(orderItem.getAssociations().getPassengers()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_REFERENCE));
				return false;
			}

			if (NDCActionType.ADD_ANCILLARIES.equals(ndcActionType) || NDCActionType.REMOVE_ANCILLARIES.equals(ndcActionType))
			{
				if(Objects.isNull(orderItem.getAssociations().getServices()))
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SERVICE_ID_REFERENCE));
					return false;
				}

				final Optional<ServiceIDType> optionalServiceId = orderItem.getAssociations().getServices().getServiceID().stream()
						.filter(serviceID -> CollectionUtils.isEmpty(serviceID.getRefs())).findAny();
				if (optionalServiceId.isPresent())
				{
					addError(errorsType, getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.MISSING_SERVICE_SEGMENT_REFERENCE));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks if the selected {@link OrderItem} can be applied to the referenced {@link Passenger}
	 *
	 * @param orderItems
	 * 		the order items
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateOrderItemsIdPerPTC(final List<OrderItem> orderItems, final ErrorsType errorsType)
			throws NDCOrderException
	{
		for (final OrderItem orderItem : orderItems)
		{
			final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
					.getNDCOfferItemIdFromString(orderItem.getOrderItemID().getValue());
			final List<PassengerType> passengers = orderItem.getAssociations().getPassengers().getPassengerReferences().stream()
					.filter(PassengerType.class::isInstance).map(PassengerType.class::cast).collect(Collectors.toList());
			final boolean isNdcOfferForPassenger = passengers.stream()
					.allMatch(passenger -> StringUtils.equals(passenger.getPTC(), ndcOfferItemId.getPtc()));

			if (!isNdcOfferForPassenger)
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_PTC_OFFER_COMBINATION));
				return false;
			}
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
	 * Gets ndc offer item id resolver.
	 *
	 * @return the ndc offer item id resolver
	 */
	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	/**
	 * Sets ndc offer item id resolver.
	 *
	 * @param ndcOfferItemIdResolver
	 * 		the ndc offer item id resolver
	 */
	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

	/**
	 * Gets travel route facade.
	 *
	 * @return the travel route facade
	 */
	protected TravelRouteFacade getTravelRouteFacade()
	{
		return travelRouteFacade;
	}

	/**
	 * Sets travel route facade.
	 *
	 * @param travelRouteFacade
	 * 		the travel route facade
	 */
	@Required
	public void setTravelRouteFacade(final TravelRouteFacade travelRouteFacade)
	{
		this.travelRouteFacade = travelRouteFacade;
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
