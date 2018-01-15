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

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} with the list of {@link RoomRateData}.
 */
public class RoomRatesHandler extends AbstractRoomRatesHandler
{
	private CategoryService categoryService;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		Instant instant = Instant.ofEpochMilli(availabilityRequestData.getCriterion().getStayDateRange().getStartTime().getTime());
		final LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		instant = Instant.ofEpochMilli(availabilityRequestData.getCriterion().getStayDateRange().getEndTime().getTime());
		final LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay -> roomStay.getRatePlans().forEach(plan -> {
			final List<RoomRateData> roomRates = new ArrayList<>();
			Stream.iterate(startDate, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate))
					.forEach(date -> populateRoomRatesForRatePlan(plan, date, roomRates));
					plan.setRoomRates(roomRates);
		}));
	}

	/**
	 * Populate room rates for rate plan.
	 *
	 * @param ratePlan
	 * 		the rate plan
	 * @param date
	 * 		the date
	 * @param roomRates
	 * 		the room rates
	 */
	protected void populateRoomRatesForRatePlan(final RatePlanData ratePlan, final LocalDateTime date,
			final List<RoomRateData> roomRates)
	{
		final Date convertedDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
		final RatePlanModel ratePlanModel = (RatePlanModel) getCategoryService().getCategoryForCode(ratePlan.getCode());
		final Optional<ProductModel> optionalRoomRate = ratePlanModel.getProducts().stream()
				.filter(roomRate -> validateRoomRateAgainstDate(convertedDate, roomRate)).findAny();
		if (optionalRoomRate.isPresent())
		{
			createRoomRateData(roomRates, (RoomRateProductModel) optionalRoomRate.get(), convertedDate);
		}
	}

	/**
	 * Gets category service.
	 *
	 * @return the category service
	 */
	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	/**
	 * Sets category service.
	 *
	 * @param categoryService
	 * 		the category service
	 */
	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
