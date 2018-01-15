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

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AvailableServiceData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationExtrasFacade;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationServicePipelineManager;
import de.hybris.platform.travelservices.services.AccommodationExtrasService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Default implementation of the {@link AccommodationExtrasFacade}
 */
public class DefaultAccommodationExtrasFacade implements AccommodationExtrasFacade
{
	private AccommodationExtrasService accommodationExtrasService;
	private AccommodationServicePipelineManager accommodationServicePipelineManager;

	@Override
	public List<AvailableServiceData> getAvailableServices(final AccommodationReservationData reservationData)
	{
		final String accommodationOfferingCode = reservationData.getAccommodationReference().getAccommodationOfferingCode();

		final List<ProductModel> extraServices = getAccommodationExtrasService()
				.getExtrasForAccommodationOffering(accommodationOfferingCode);

		return reservationData.getRoomStays().stream()
				.map(roomStay -> createAvailableServiceData(reservationData, extraServices, roomStay)).collect(Collectors.toList());
	}

	protected AvailableServiceData createAvailableServiceData(final AccommodationReservationData reservationData,
															  final List<ProductModel> extraServices, final ReservedRoomStayData roomStay)
	{
		final AvailableServiceData availableService = new AvailableServiceData();
		availableService.setRoomStayRefNumber(roomStay.getRoomStayRefNumber());

		final List<ServiceData> services = new ArrayList<>();
		extraServices.forEach(extraService -> {
			final ServiceData serviceData = getAccommodationServicePipelineManager().executePipeline(extraService, roomStay,
					reservationData);
			if (serviceData != null)
			{
				services.add(serviceData);
			}
		});
		availableService.setServices(services);
		return availableService;
	}

	/**
	 * @return the accommodationExtrasService
	 */
	protected AccommodationExtrasService getAccommodationExtrasService()
	{
		return accommodationExtrasService;
	}

	/**
	 * @param accommodationExtrasService
	 *           the accommodationExtrasService to set
	 */
	@Required
	public void setAccommodationExtrasService(final AccommodationExtrasService accommodationExtrasService)
	{
		this.accommodationExtrasService = accommodationExtrasService;
	}

	/**
	 * @return the accommodationServicePipelineManager
	 */
	protected AccommodationServicePipelineManager getAccommodationServicePipelineManager()
	{
		return accommodationServicePipelineManager;
	}

	/**
	 * @param accommodationServicePipelineManager
	 *           the accommodationServicePipelineManager to set
	 */
	@Required
	public void setAccommodationServicePipelineManager(
			final AccommodationServicePipelineManager accommodationServicePipelineManager)
	{
		this.accommodationServicePipelineManager = accommodationServicePipelineManager;
	}

}

