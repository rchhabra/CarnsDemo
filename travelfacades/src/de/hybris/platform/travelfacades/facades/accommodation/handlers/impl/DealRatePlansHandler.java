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
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.RatePlanService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} with the
 * {@link de.hybris.platform.commercefacades.accommodation.RatePlanData} specified in the criterion
 */
public class DealRatePlansHandler extends AbstractRatePlansHandler
{

	private RatePlanService ratePlanService;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay ->
		{
			final RoomStayCandidateData roomStayCandidateData = availabilityRequestData.getCriterion().getRoomStayCandidates()
					.stream().filter(roomStayCandidate -> Objects
							.equals(roomStay.getRoomStayRefNumber(), roomStayCandidate.getRoomStayCandidateRefNumber())).findFirst()
					.get();
			if (StringUtils.isNotEmpty(roomStayCandidateData.getRatePlanCode()))
			{
				RatePlanModel ratePlanModel = getRatePlanService().getRatePlanForCode(roomStayCandidateData.getRatePlanCode());
				roomStay.setRatePlans(getRatePlanConverter().convertAll(Collections.singletonList(ratePlanModel)));
				updateCancelPenaltiesDescription(Arrays.asList(ratePlanModel), roomStay);
			}

			final AccommodationModel accommodation = getAccommodationService().getAccommodationForAccommodationOffering(
					availabilityRequestData.getCriterion().getAccommodationReference().getAccommodationOfferingCode(),
					roomStay.getRoomTypes().get(0).getCode());
			updateGuestOccupancy(roomStay, accommodation);
		});
	}

	/**
	 * @return the ratePlanService
	 */
	protected RatePlanService getRatePlanService()
	{
		return ratePlanService;
	}

	/**
	 * @param ratePlanService
	 * 		the ratePlanService to set
	 */
	@Required
	public void setRatePlanService(final RatePlanService ratePlanService)
	{
		this.ratePlanService = ratePlanService;
	}
}
