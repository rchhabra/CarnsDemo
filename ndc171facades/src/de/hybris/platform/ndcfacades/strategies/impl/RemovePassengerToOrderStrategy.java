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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy that implements the {@link AmendOrderStrategy}.
 * The strategy is used to create a new order without the passenger specified in the {@link OrderChangeRQ}.
 */
public class RemovePassengerToOrderStrategy extends AbstractAmendOrderStrategy implements AmendOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(RemovePassengerToOrderStrategy.class);

	private BookingFacade bookingFacade;

	@Override
	public OrderModel amendOrder(final OrderModel originalOrder, final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		final OrderItem orderItem = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem().stream().findFirst().get();
		final PassengerType passenger = orderItem.getAssociations().getPassengers().getPassengerReferences().stream()
				.filter(PassengerType.class::isInstance).map(PassengerType.class::cast).findFirst().get();
		final TravellerModel traveller = getTravellerService().getExistingTraveller(passenger.getProfileID());

		if (Objects.isNull(traveller) || Objects.isNull(traveller.getInfo()) || Objects
				.isNull(((PassengerInformationModel) traveller.getInfo()).getSurname()))
		{
			throw new NDCOrderException(NdcfacadesConstants.INVALID_PASSENGER);
		}

		if (!getBookingFacade().atleastOneAdultTravellerRemaining(originalOrder.getCode(), traveller.getLabel()))
		{
			throw new NDCOrderException("At least one adult should be present in the booking");
		}

		if (!isActiveTraveller(originalOrder, traveller))
		{
			throw new NDCOrderException(NdcfacadesConstants.INVALID_PASSENGER);
		}

		return createOrderWithoutTraveller(originalOrder, traveller);
	}

	/**
	 * Creates the new {@link OrderModel}, change the all {@link AbstractOrderEntryModel} related to the specified
	 * Traveller to not active and with 0 quantity and recalculates the order total.
	 *
	 * @param originalOrder
	 *           the original order
	 * @param traveller
	 *           the traveller
	 * @return the order model
	 * @throws NDCOrderException
	 *            the NDC order exception
	 */
	protected OrderModel createOrderWithoutTraveller(final OrderModel originalOrder, final TravellerModel traveller)
			throws NDCOrderException
	{
		final OrderModel amendedOrder = cloneOrder(originalOrder);
		try
		{
			final List<AbstractOrderEntryModel> travellerEntries = amendedOrder.getEntries().stream().filter(
					entry -> Objects.nonNull(entry.getTravelOrderEntryInfo())
							&& CollectionUtils.size(entry.getTravelOrderEntryInfo().getTravellers()) == 1 && StringUtils
							.equalsIgnoreCase(entry.getTravelOrderEntryInfo().getTravellers().iterator().next().getLabel(),
											traveller.getLabel()))
					.collect(Collectors.toList());

			travellerEntries.forEach(entry ->
			{
				entry.setQuantity(0L);
				entry.setActive(Boolean.FALSE);
				entry.setAmendStatus(AmendStatus.CHANGED);
				getModelService().save(entry);
			});

			getModelService().refresh(amendedOrder);
			removeBookedSeatForTraveller(amendedOrder, traveller);
			getModelService().refresh(amendedOrder);

			calculateOrderTotal(amendedOrder);

			final PriceData totalToPay = getTotalToPay(amendedOrder);

			if (totalToPay.getValue().doubleValue() > 0d)
			{
				LOG.error("Unable to cancel a passenger where there is an additional payment required.");
				throw new NDCOrderException("Error during the passenger cancellation");
			}

			if (BigDecimal.ZERO.compareTo(totalToPay.getValue()) != 0 && !createRefundPaymentTransaction(amendedOrder, travellerEntries))
			{
				throw new NDCOrderException("Error during the passenger cancellation");
			}
		}
		catch (final Exception e)
		{
			LOG.warn("Error occurred, removing order and throwing again the exception");
			removeOrder(amendedOrder);
			throw e;
		}

		return amendedOrder;
	}

	/**
	 * Removes the seat associated to the specified traveller from the order.
	 *
	 * @param amendmentOrder
	 *           the amendment order
	 * @param cancelledTraveller
	 *           the cancelled traveller
	 */
	protected void removeBookedSeatForTraveller(final OrderModel amendmentOrder, final TravellerModel cancelledTraveller)
	{
		final List<SelectedAccommodationModel> selectedAccommodations = amendmentOrder.getSelectedAccommodations();
		if (CollectionUtils.isNotEmpty(selectedAccommodations))
		{
			final List<SelectedAccommodationModel> toBeRemoved = new ArrayList<>();
			final List<SelectedAccommodationModel> remainingSelectedAccoms = new ArrayList<>();
			for (final SelectedAccommodationModel selectedAccommodationModel : selectedAccommodations)
			{
				final TravellerModel traveller = selectedAccommodationModel.getTraveller();
				if (traveller.getUid().equals(cancelledTraveller.getUid()))
				{
					toBeRemoved.add(selectedAccommodationModel);
				}
				else
				{
					remainingSelectedAccoms.add(selectedAccommodationModel);
				}
			}
			if (CollectionUtils.isNotEmpty(toBeRemoved))
			{
				for (final SelectedAccommodationModel selectedAccommodationModel : toBeRemoved)
				{
					getModelService().remove(selectedAccommodationModel);
				}

			}
			amendmentOrder.setSelectedAccommodations(remainingSelectedAccoms);
			getModelService().save(amendmentOrder);
		}
	}

	/**
	 * Gets the booking facade.
	 *
	 * @return the booking facade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * Sets the booking facade.
	 *
	 * @param bookingFacade
	 *           the new booking facade
	 */
	@Required
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}
}
