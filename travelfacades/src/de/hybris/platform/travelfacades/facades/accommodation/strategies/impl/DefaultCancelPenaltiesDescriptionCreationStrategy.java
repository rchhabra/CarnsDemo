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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.CancelPenaltyData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.CancelPenaltiesDescriptionCreationStrategy;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * Implementation of {@link CancelPenaltiesDescriptionCreationStrategy} interface, to create and update the formatted
 * description of the {@linkplain CancelPenaltyData}
 */
public class DefaultCancelPenaltiesDescriptionCreationStrategy implements CancelPenaltiesDescriptionCreationStrategy
{

	private static final String DATE_PATTERN = "dd/MM/yyyy";

	@Override
	public void updateCancelPenaltiesDescription(final RatePlanModel ratePlan, final RoomStayData roomStay)
	{
		final List<CancelPenaltyData> cancelPenalties = roomStay.getRatePlans().stream()
				.flatMap(ratePlanData -> ratePlanData.getCancelPenalties().stream()).collect(Collectors.toList());

		for (final CancelPenaltyData cancelPenaltyData : cancelPenalties)
		{
			final Optional<CancelPenaltyModel> optional = ratePlan.getCancelPenalty().stream()
					.filter(penaltyModel -> StringUtils.equals(penaltyModel.getCode(), cancelPenaltyData.getCode())).findFirst();
			if (optional.isPresent())
			{
				final String description = createCancelPenaltyDescription(optional.get(), roomStay.getCheckInDate());
				cancelPenaltyData.setFormattedDescription(description);
			}
		}
	}

	/**
	 * Creates the CancelPenalty description, calculating the deadline Date for the given checkInDate
	 * 
	 * @param cancelPenaltyModel
	 * @param checkInDate
	 * 
	 * @return the String representing the formatted description for the cancelPenalty
	 */
	protected String createCancelPenaltyDescription(final CancelPenaltyModel cancelPenaltyModel, final Date checkInDate)
	{
		Date deadline = cancelPenaltyModel.getAbsoluteDeadline();
		if (deadline == null && cancelPenaltyModel.getRelativeDeadline() != null)
		{
			deadline = new Date(checkInDate.getTime() - cancelPenaltyModel.getRelativeDeadline());
		}

		if (StringUtils.isBlank(cancelPenaltyModel.getDescription()))
		{
			return null;
		}

		final StringBuilder formattedDescription = new StringBuilder();
		formattedDescription.append(cancelPenaltyModel.getDescription());
		if (deadline != null)
		{
			formattedDescription.append(" ");
			formattedDescription.append(TravelDateUtils.convertDateToStringDate(deadline, DATE_PATTERN));
		}

		return formattedDescription.toString();
	}

}
