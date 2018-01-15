/*


 *
 *
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
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.ArrayList;
import java.util.List;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} with the list of {@link RoomStayData} that
 * are linked to the {@link AccommodationOfferingModel}
 */
public class RoomStaysHandler extends AbstractRoomStaysHandler
{
	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String accommodationOfferingCode = availabilityRequestData.getCriterion().getAccommodationReference()
				.getAccommodationOfferingCode();
		final StayDateRangeData stayDateRange = availabilityRequestData.getCriterion().getStayDateRange();
		final List<AccommodationModel> accommodations = getAccommodationService()
				.getAccommodationForAccommodationOffering(accommodationOfferingCode);
		final List<RoomStayData> roomStays = new ArrayList<>(accommodations.size());
		accommodations.forEach(accommodation -> roomStays
				.add(createRoomStayData(accommodation, accommodations.indexOf(accommodation), stayDateRange)));
		accommodationAvailabilityResponseData.setRoomStays(roomStays);
	}

}
