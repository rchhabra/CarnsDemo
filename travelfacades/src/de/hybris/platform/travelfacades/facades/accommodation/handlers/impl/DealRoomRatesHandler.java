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
import de.hybris.platform.commercefacades.accommodation.RoomRateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * This handler populates the {@link AccommodationAvailabilityResponseData} with the
 * {@link de.hybris.platform.commercefacades.accommodation.RoomRateData}
 */
public class DealRoomRatesHandler extends AbstractRoomRatesHandler
{
	private BundleTemplateService bundleTemplateService;

	@Override
	public void handle(final AccommodationAvailabilityRequestData availabilityRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		Instant instant = Instant.ofEpochMilli(availabilityRequestData.getCriterion().getStayDateRange().getStartTime().getTime());
		final LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		instant = Instant.ofEpochMilli(availabilityRequestData.getCriterion().getStayDateRange().getEndTime().getTime());
		final LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		accommodationAvailabilityResponseData.getRoomStays().forEach(roomStay -> roomStay.getRatePlans().forEach(plan ->
		{
			List<RoomRateData> roomRates = Stream.iterate(startDate, date -> date.plusDays(1))
					.limit(ChronoUnit.DAYS.between(startDate, endDate))
					.flatMap(date -> getRoomRates(date, availabilityRequestData.getCriterion().getBundleTemplateId()).stream())
					.collect(Collectors.toList());
			plan.setRoomRates(roomRates);
		}));
	}

	/**
	 * Returns the list of {@link de.hybris.platform.commercefacades.accommodation.RoomRateData} created based on the list of
	 * {@link de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel} specified in the {@link
	 * de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel} corresponding to the given bundleTemplateId
	 *
	 * @param date
	 * 		the date to be used to check the validity of the RoomRate
	 * @param bundleTemplateId
	 * 		the bundleTemplateId
	 *
	 * @return a List<RoomRateData>
	 */
	protected List<RoomRateData> getRoomRates(final LocalDateTime date, final String bundleTemplateId)
	{
		final Date convertedDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
		final Optional<ProductModel> optionalRoomRate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId)
				.getProducts().stream()
				.filter(roomRate -> validateRoomRateAgainstDate(convertedDate, roomRate)).findAny();
		List<RoomRateData> roomRates = new ArrayList<>();
		if (optionalRoomRate.isPresent())
		{
			createRoomRateData(roomRates, (RoomRateProductModel) optionalRoomRate.get(), convertedDate);
		}
		return roomRates;
	}

	/**
	 * @return the bundleTemplateService
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * @param bundleTemplateService
	 * 		the bundleTemplateService to set
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
