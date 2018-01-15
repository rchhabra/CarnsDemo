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
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Strategy to evaluate the enabled property of the List<BookingActionData>, setting it to false if all the travellers
 * have been checked in in every leg, true otherwise
 */
public class JourneyCheckInRestrictionStrategy implements BookingActionEnabledEvaluatorStrategy
{
	private BaseStoreService baseStoreService;
	private CustomerAccountService customerAccountService;
	private TimeService timeService;
	private List<ConsignmentStatus> consignmentStatusList;

	private static final String ALTERNATIVE_MESSAGE = "booking.action.journey.check.in.alternative.message";

	@Override
	public void applyStrategy(final List<BookingActionData> bookingActionDataList, final ReservationData reservationData)
	{
		final List<BookingActionData> enabledBookingActions = bookingActionDataList.stream()
				.filter(BookingActionData::isEnabled).collect(Collectors.toList());
		if (enabledBookingActions.isEmpty())
		{
			return;
		}

		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(reservationData.getCode(), baseStoreModel);

		final boolean enabled = orderModel.getConsignments().stream()
				.anyMatch(consignment -> consignment.getConsignmentEntries().stream()
						.anyMatch(entry -> Objects.equals(OrderEntryType.TRANSPORT, entry.getOrderEntry().getType()))
						&& !checkedInOrCancelledOrPastLeg(consignment));

		if (!enabled)
		{
			enabledBookingActions.forEach(bookingActionData -> {
				bookingActionData.setEnabled(enabled);
				bookingActionData.getAlternativeMessages().add(ALTERNATIVE_MESSAGE);
			});
		}

	}

	protected boolean checkedInOrCancelledOrPastLeg(final ConsignmentModel consignment)
	{
		final boolean isNotValidStatus = consignmentStatusList.contains(consignment.getStatus());

		final TransportOfferingModel transportOfferingModel = (TransportOfferingModel) consignment.getWarehouse();
		final ZoneId zoneId;

		final List<PointOfServiceModel> originPointOfServices = transportOfferingModel.getTravelSector().getOrigin().getPointOfService();
		if (CollectionUtils.isNotEmpty(originPointOfServices) && originPointOfServices.get(0).getTimeZoneId() != null)
		{
			zoneId = ZoneId.of(originPointOfServices.get(0).getTimeZoneId());
		}
		else
		{
			zoneId = ZoneId.from(ZoneOffset.UTC);
		}

		final boolean pastLeg = TravelDateUtils.isBefore(transportOfferingModel.getDepartureTime(), zoneId,
				getTimeService().getCurrentTime(), ZoneId.systemDefault());

		return isNotValidStatus || pastLeg;
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

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the consignmentStatusList
	 */
	protected List<ConsignmentStatus> getConsignmentStatusList()
	{
		return consignmentStatusList;
	}

	/**
	 * @param consignmentStatusList
	 *           the consignmentStatusList to set
	 */
	public void setConsignmentStatusList(final List<ConsignmentStatus> consignmentStatusList)
	{
		this.consignmentStatusList = consignmentStatusList;
	}

}
