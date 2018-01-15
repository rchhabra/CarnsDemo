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

import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC {@link OrderChangeRQ} {@link OrderItem} Validator
 */
public class NDCOrderChangeSegmentRefsValidator implements NDCRequestValidator<OrderChangeRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderChangeSegmentRefsValidator.class);
	private static final String SPACE = " ";

	private ConfigurationService configurationService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;

	@Override
	public void validate(final OrderChangeRQ orderChangeRQ, final ErrorsType errorsType)
	{
		final List<OrderItem> orderItems = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem();
		final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
		final NDCActionType ndcActionType = NDCActionType.valueOf(actionType);

		if (NDCActionType.ADD_ANCILLARIES.equals(ndcActionType) || NDCActionType.REMOVE_ANCILLARIES.equals(ndcActionType))
		{
			for (final OrderItem orderItem : orderItems)
			{
				final Set<ServiceIDType> serviceIDs = orderItem.getAssociations().getServices().getServiceID().stream()
						.collect(Collectors.toSet());
				final Set<String> offerItemTransportOfferings = getOfferItemTransportOfferings(orderItem, errorsType);

				if (!validateServicesForSegmentKey(serviceIDs, offerItemTransportOfferings, errorsType))
				{
					return;
				}
			}
		}
		else if (NDCActionType.ADD_ACCOMMODATION.equals(ndcActionType) || NDCActionType.REMOVE_ACCOMMODATION.equals(ndcActionType))
		{
			for (final OrderItem orderItem : orderItems)
			{
				final Set<String> offerItemTransportOfferings = getOfferItemTransportOfferings(orderItem, errorsType);
				final String invalidFlightSegment = findAndGetAnyInvalidSegmentKey(offerItemTransportOfferings,
						orderItem.getSeatItem().getRefs());

				if (StringUtils.isNotEmpty(invalidFlightSegment))
				{
					final ListOfSeatType seat = orderItem.getSeatItem().getSeatReference().stream()
							.filter(seatReference -> seatReference.getValue() instanceof ListOfSeatType)
							.map(seatReference -> (ListOfSeatType) seatReference.getValue()).findFirst().get();
					addError(errorsType, getErrorMessgage(invalidFlightSegment, seat.getListKey(),NdcwebservicesConstants.INVALID_SEATITEM_SEGMENTKEY_REFS));
					return;
				}
			}
		}
	}

	/**
	 * Gets the offer item transport offerings.
	 *
	 * @param orderItem
	 *           the order item
	 * @param errorsType
	 *           the errors type
	 * @return the offer item transport offerings
	 */
	protected Set<String> getOfferItemTransportOfferings(final OrderItem orderItem, final ErrorsType errorsType)
	{
		NDCOfferItemId ndcOfferItemId;
		try
		{
			ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(orderItem.getOrderItemID().getValue());
			return ndcOfferItemId.getBundleList().stream().flatMap(entry -> entry.getTransportOfferings().stream())
					.collect(Collectors.toSet());
		}
		catch (final NDCOrderException e)
		{
			LOG.debug(e);
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
		}
		return Collections.emptySet();
	}

	/**
	 * Validate services for segment key.
	 *
	 * @param serviceIDs
	 *           the service I ds
	 * @param offerItemTransportOfferings
	 *           the offer item transport offerings
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateServicesForSegmentKey(final Set<ServiceIDType> serviceIDs,
			final Set<String> offerItemTransportOfferings, final ErrorsType errorsType)
	{
		for (final ServiceIDType serviceID : serviceIDs)
		{
			final String invalidFlightSegment = findAndGetAnyInvalidSegmentKey(offerItemTransportOfferings, serviceID.getRefs());

			if (StringUtils.isNotEmpty(invalidFlightSegment))
			{
				addError(errorsType, getErrorMessgage(invalidFlightSegment, serviceID.getValue(),NdcwebservicesConstants.INVALID_SERVICEID_SEGMENTKEY_REFS));
				return false;
			}
		}
		return true;
	}

	/**
	 * Find and get any invalid segment key.
	 *
	 * @param offerItemTransportOfferings
	 *           the offer item transport offerings
	 * @param listOfFlightSegmentType
	 *           the list of flight segment type
	 * @return the string
	 */
	protected String findAndGetAnyInvalidSegmentKey(final Set<String> offerItemTransportOfferings,
			final List<Object> listOfFlightSegmentType)
	{
		final Set<String> flightSegments = listOfFlightSegmentType.stream()
				.filter(ListOfFlightSegmentType.class::isInstance)
				.map(entry -> ((ListOfFlightSegmentType) entry).getSegmentKey()).collect(Collectors.toSet());

		return flightSegments.stream().filter(flightSegment -> !offerItemTransportOfferings.contains(flightSegment)).findAny()
				.orElse(StringUtils.EMPTY);
	}

	/**
	 * Gets the error messgage.
	 *
	 * @param invalidFlightSegment
	 *           the invalid flight segment
	 * @param serviceOrSeat
	 *           the service or seat
	 * @return the error messgage
	 */
	protected String getErrorMessgage(final String invalidFlightSegment, final String serviceOrSeat,
			final String errorMessageConfig)
	{
		return new StringBuilder(invalidFlightSegment).append(SPACE)
				.append(getConfigurationService().getConfiguration().getString(errorMessageConfig)).append(SPACE)
				.append(serviceOrSeat).toString();
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
	 * @return the ndcOfferItemIdResolver
	 */
	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	/**
	 * @param ndcOfferItemIdResolver
	 *           the ndcOfferItemIdResolver to set
	 */

	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

}
