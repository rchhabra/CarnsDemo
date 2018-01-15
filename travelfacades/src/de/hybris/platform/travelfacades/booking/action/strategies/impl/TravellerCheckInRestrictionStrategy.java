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
*/

package de.hybris.platform.travelfacades.booking.action.strategies.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>. For each leg and each traveller, the value
 * is set to false if the check in has already been done, true otherwise
 */
public class TravellerCheckInRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{

	private BaseStoreService baseStoreService;
	private CustomerAccountService customerAccountService;

	private static final String ALTERNATIVE_MESSAGE = "booking.action.traveller.check.in.alternative.message";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(bookingActionData -> bookingActionData.isEnabled()).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(reservationData.getCode(), baseStoreModel);

		for (final BookingActionData bookingActionData : enabledBookingActions)
		{
			final Integer originDestinationRefNumber = Integer.valueOf(bookingActionData.getOriginDestinationRefNumber());
			final List<ConsignmentModel> consignmentsForOriginDestination = new ArrayList<>();

			for (final ConsignmentModel consignmentModel : orderModel.getConsignments())
			{
				if (Objects.nonNull(consignmentModel.getTraveller())
						&& consignmentModel.getTraveller().getUid().equals(bookingActionData.getTraveller().getUid())
						&& consignmentModel.getConsignmentEntries().stream()
								.anyMatch(entry -> Objects.equals(OrderEntryType.TRANSPORT, entry.getOrderEntry().getType())
										&& entry.getOrderEntry().getTravelOrderEntryInfo().getOriginDestinationRefNumber()
												.equals(originDestinationRefNumber)))
				{
					consignmentsForOriginDestination.add(consignmentModel);
				}
			}

			final boolean enabled = consignmentsForOriginDestination.stream()
					.anyMatch(consignment -> !consignment.getStatus().equals(ConsignmentStatus.CHECKED_IN));
			if (!enabled)
			{
				bookingActionData.setEnabled(enabled);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			}
		}
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the customerAccountService
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

}
