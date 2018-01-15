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
import de.hybris.platform.travelservices.model.product.AccommodationModel;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} with the list of
 * {@link de.hybris.platform.commercefacades.accommodation.RatePlanData}
 */
public class RatePlansHandler extends AbstractRatePlansHandler
{

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		final String accommodationOfferingCode = availabilityRequestData.getCriterion().getAccommodationReference()
				.getAccommodationOfferingCode();
		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay ->
		{
			final AccommodationModel accommodation = getAccommodationService()
					.getAccommodationForAccommodationOffering(accommodationOfferingCode, roomStay.getRoomTypes().get(0).getCode());
			roomStay.setRatePlans(getRatePlanConverter().convertAll(accommodation.getRatePlan()));
			updateGuestOccupancy(roomStay, accommodation);
			updateCancelPenaltiesDescription(accommodation.getRatePlan(), roomStay);
		});
	}

}
