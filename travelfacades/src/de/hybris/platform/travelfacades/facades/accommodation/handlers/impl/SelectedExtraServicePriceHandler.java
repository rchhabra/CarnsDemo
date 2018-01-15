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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;

import org.apache.commons.collections.CollectionUtils;



/**
 * Handler class to populate the price of the serviceData for each roomStay in
 * {@link AccommodationAvailabilityResponseData}
 */
public class SelectedExtraServicePriceHandler extends AbstractExtraServicePriceHandler implements AccommodationDetailsHandler
{
	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay ->
		{
			final ReservedRoomStayData reservedRoomStayData = (ReservedRoomStayData) roomStay;
			if (CollectionUtils.isEmpty(reservedRoomStayData.getServices()))
			{
				return;
			}
			reservedRoomStayData.getServices().forEach(service -> service.setPrice(getServiceRate(service, reservedRoomStayData)));
		});
	}
}
