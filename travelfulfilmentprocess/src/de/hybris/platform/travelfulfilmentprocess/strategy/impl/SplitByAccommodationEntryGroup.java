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

package de.hybris.platform.travelfulfilmentprocess.strategy.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.SplittingStrategy;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelfulfilmentprocess.constants.TravelfulfilmentprocessConstants;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy performing splitting based on accommodation order entry group for accommodation related order entries
 */
public class SplitByAccommodationEntryGroup implements SplittingStrategy
{
	private BookingService bookingService;

	@Override
	public List<OrderEntryGroup> perform(final List<OrderEntryGroup> orderEntryGroup)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();
		for (final OrderEntryGroup orderEntry : orderEntryGroup)
		{
			result.addAll(splitByOrderEntryGroup(orderEntry));
		}
		return result;
	}

	/**
	 * This method retrieves all the accommodation order entry groups and splits order entries according with the group
	 * they belong to in order to create a consignment for each group.
	 *
	 * @param entryGroup
	 *           the entry group
	 * @return the new list of split entry groups
	 */
	protected List<OrderEntryGroup> splitByOrderEntryGroup(final OrderEntryGroup entryGroup)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();
		final Optional<AbstractOrderEntryModel> entryModel = entryGroup.stream().findFirst();
		if (!entryModel.isPresent())
		{
			return result;
		}
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(entryModel.get().getOrder());
		accommodationOrderEntryGroups.forEach(group -> {
			final OrderEntryGroup tmpOrderEntryList = new OrderEntryGroup();
			tmpOrderEntryList.setParameter(TravelfulfilmentprocessConstants.ACCOMMODATION_OFFERING,
					group.getAccommodationOffering());
			tmpOrderEntryList.setParameter(TravelfulfilmentprocessConstants.ACCOMMODATION, group.getAccommodation());
			tmpOrderEntryList.setParameter(TravelfulfilmentprocessConstants.REF_NUMBER, group.getRoomStayRefNumber());
			tmpOrderEntryList.addAll(group.getEntries());
			result.add(tmpOrderEntryList);
		});
		return result;
	}

	@Override
	public void afterSplitting(final OrderEntryGroup arg0, final ConsignmentModel arg1)
	{
		// do nothing
	}

	/**
	 * Gets booking service.
	 *
	 * @return bookingService booking service
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}


}
