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

import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.CancelPenaltiesDescriptionCreationStrategy;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler for {@link RatePlansHandler} and {@link SelectedRatePlansHandler}
 */
public abstract class AbstractRatePlansHandler implements AccommodationDetailsHandler
{
	private AccommodationService accommodationService;
	private Converter<RatePlanModel, RatePlanData> ratePlanConverter;
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;
	private CancelPenaltiesDescriptionCreationStrategy cancelPenaltiesDescriptionCreationStrategy;

	/**
	 * Sets guest occupancy in each rate plan with the guest occupancy from an accommodation if not available
	 *
	 * @param roomStayData
	 * @param accommodation
	 */
	protected void updateGuestOccupancy(final RoomStayData roomStayData, final AccommodationModel accommodation)
	{
		roomStayData.getRatePlans().forEach(rp -> {
			if (CollectionUtils.isEmpty(rp.getOccupancies()))
			{
				rp.setOccupancies(getGuestOccupancyConverter().convertAll(accommodation.getGuestOccupancies()));
			}
		});
	}

	/**
	 * Updates the formattedDescription of the cancelPenalties of the ratePlans of the given roomStay
	 *
	 * @param ratePlanModelList
	 * @param roomStay
	 */
	protected void updateCancelPenaltiesDescription(Collection<RatePlanModel> ratePlanModelList, RoomStayData roomStay)
	{
		ratePlanModelList.forEach(
				ratePlan -> getCancelPenaltiesDescriptionCreationStrategy().updateCancelPenaltiesDescription(ratePlan, roomStay));
	}

	/**
	 * @return the accommodationService
	 */
	protected AccommodationService getAccommodationService()
	{
		return accommodationService;
	}

	/**
	 * @param accommodationService
	 *           the accommodationService to set
	 */
	@Required
	public void setAccommodationService(final AccommodationService accommodationService)
	{
		this.accommodationService = accommodationService;
	}

	/**
	 * @return the ratePlanConverter
	 */
	protected Converter<RatePlanModel, RatePlanData> getRatePlanConverter()
	{
		return ratePlanConverter;
	}

	/**
	 * @param ratePlanConverter
	 *           the ratePlanConverter to set
	 */
	@Required
	public void setRatePlanConverter(final Converter<RatePlanModel, RatePlanData> ratePlanConverter)
	{
		this.ratePlanConverter = ratePlanConverter;
	}

	/**
	 * @return the guestOccupancyConverter
	 */
	protected Converter<GuestOccupancyModel, GuestOccupancyData> getGuestOccupancyConverter()
	{
		return guestOccupancyConverter;
	}

	/**
	 * @param guestOccupancyConverter
	 *           the guestOccupancyConverter to set
	 */
	@Required
	public void setGuestOccupancyConverter(final Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter)
	{
		this.guestOccupancyConverter = guestOccupancyConverter;
	}

	/**
	 * @return the cancelPenaltiesDescriptionCreationStrategy
	 */
	protected CancelPenaltiesDescriptionCreationStrategy getCancelPenaltiesDescriptionCreationStrategy()
	{
		return cancelPenaltiesDescriptionCreationStrategy;
	}

	/**
	 * @param cancelPenaltiesDescriptionCreationStrategy
	 *           the cancelPenaltiesDescriptionCreationStrategy to set
	 */
	@Required
	public void setCancelPenaltiesDescriptionCreationStrategy(
			final CancelPenaltiesDescriptionCreationStrategy cancelPenaltiesDescriptionCreationStrategy)
	{
		this.cancelPenaltiesDescriptionCreationStrategy = cancelPenaltiesDescriptionCreationStrategy;
	}

}
