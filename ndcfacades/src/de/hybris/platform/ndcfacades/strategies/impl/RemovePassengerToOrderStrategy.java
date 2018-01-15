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
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
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
		final String uid = orderChangeRQ.getQuery().getPassengers().getPassenger().get(0).getProfileID().getValue();
		final TravellerModel traveller = getTravellerService().getExistingTraveller(uid);

		if (Objects.isNull(traveller) || Objects.isNull(traveller.getInfo()) || Objects
				.isNull(((PassengerInformationModel) traveller.getInfo()).getSurname()))
		{
			throw new NDCOrderException("Invalid passenger provided");
		}

		if (!getBookingFacade().atleastOneAdultTravellerRemaining(originalOrder.getCode(), traveller.getLabel()))
		{
			throw new NDCOrderException("At least one adult should be present in the booking");
		}

		if (!isActiveTraveller(originalOrder, traveller))
		{
			throw new NDCOrderException("Invalid passenger provided");
		}

		return createOrderWithoutTraveller(originalOrder, traveller.getLabel(), traveller.getUid());
	}

	/**
	 * Creates the new {@link OrderModel}, change the all {@link AbstractOrderEntryModel} related to the specified Traveller to not active and with 0 quantity and recalculates the order total
	 *
	 * @param originalOrder
	 * @param cancelledTravellerCode
	 * @param cancelledTravellerUid
	 * @return
	 * @throws NDCOrderException
	 */
	protected OrderModel createOrderWithoutTraveller(final OrderModel originalOrder, final String cancelledTravellerCode,
			final String cancelledTravellerUid) throws NDCOrderException
	{
		final OrderModel amendedOrder = cloneOrder(originalOrder);
		try
		{
			final List<AbstractOrderEntryModel> travellerEntries = amendedOrder.getEntries().stream().filter(
					entry -> Objects.nonNull(entry.getTravelOrderEntryInfo())
							&& CollectionUtils.size(entry.getTravelOrderEntryInfo().getTravellers()) == 1 && StringUtils
							.equalsIgnoreCase(entry.getTravelOrderEntryInfo().getTravellers().iterator().next().getLabel(),
									cancelledTravellerCode)).collect(Collectors.toList());

			travellerEntries.forEach(entry ->
			{
				entry.setQuantity(0L);
				entry.setActive(Boolean.FALSE);
				entry.setAmendStatus(AmendStatus.CHANGED);
				getModelService().save(entry);
			});

			getModelService().refresh(amendedOrder);
			removeBookedSeatForTraveller(amendedOrder, cancelledTravellerUid);
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
	 * Removes the seat associated to the specified traveller from the order
	 *
	 * @param amendmentOrder
	 * @param cancelledTravellerUid
	 */
	protected void removeBookedSeatForTraveller(final OrderModel amendmentOrder, final String cancelledTravellerUid)
	{
		final List<SelectedAccommodationModel> selectedAccommodations = amendmentOrder.getSelectedAccommodations();
		if (CollectionUtils.isNotEmpty(selectedAccommodations))
		{
			final List<SelectedAccommodationModel> toBeRemoved = new ArrayList<>();
			final List<SelectedAccommodationModel> remainingSelectedAccoms = new ArrayList<>();
			for (final SelectedAccommodationModel selectedAccommodationModel : selectedAccommodations)
			{
				final TravellerModel traveller = selectedAccommodationModel.getTraveller();
				if (traveller.getUid().equals(cancelledTravellerUid))
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

	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	@Required
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}
}
