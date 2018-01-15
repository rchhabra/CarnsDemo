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

package de.hybris.platform.travelfacades.facades.packages.impl;

import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageFacade;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageDetailsPipelineManager;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PackageFacade}
 */
public class DefaultPackageFacade implements PackageFacade
{
	private PackageDetailsPipelineManager packageDetailsPipelineManager;
	private PackageDetailsPipelineManager amendPackageDetailsPipelineManager;
	private TravelCartFacade travelCartFacade;
	private BookingFacade bookingFacade;
	private AccommodationCartFacade accommodationCartFacade;

	@Override
	public PackageResponseData getPackageResponse(final PackageRequestData packageRequestData)
	{
		return getPackageDetailsPipelineManager().executePipeline(packageRequestData);
	}

	@Override
	public PackageResponseData getAmendPackageResponse(final PackageRequestData packageRequestData)
	{
		return getAmendPackageDetailsPipelineManager().executePipeline(packageRequestData);
	}

	@Override
	public Boolean isPackageInCart()
	{
		return getTravelCartFacade().hasSessionCart() && getBookingFacade().isCurrentCartOfType(OrderEntryType.TRANSPORT.getCode())
				&& getBookingFacade().isCurrentCartOfType(OrderEntryType.ACCOMMODATION.getCode());
	}

	@Override
	public Boolean isPackageInOrder(final String bookingReference)
	{
		return getBookingFacade().isOrderOfType(bookingReference, OrderEntryType.TRANSPORT.getCode())
				&& getBookingFacade().isOrderOfType(bookingReference, OrderEntryType.ACCOMMODATION.getCode());
	}

	@Override
	public void cleanUpCartBeforeAddition(final String departureLocation, final String arrivalLocation, final String departureDate,
			final String returnDate, final String accommodationOfferingCode, final String checkInDate, final String checkOutDate,
			final List<RoomStayCandidateData> roomStayCandidates)
	{
		getTravelCartFacade().validateCart(departureLocation, arrivalLocation, departureDate, returnDate);
		getAccommodationCartFacade().cleanUpCartBeforeAddition(accommodationOfferingCode, checkInDate, checkOutDate, roomStayCandidates);
	}

	/**
	 * Gets package details pipeline manager.
	 *
	 * @return the package details pipeline manager
	 */
	protected PackageDetailsPipelineManager getPackageDetailsPipelineManager()
	{
		return packageDetailsPipelineManager;
	}

	/**
	 * Sets package details pipeline manager.
	 *
	 * @param packageDetailsPipelineManager
	 * 		the package details pipeline manager
	 */
	@Required
	public void setPackageDetailsPipelineManager(final PackageDetailsPipelineManager packageDetailsPipelineManager)
	{
		this.packageDetailsPipelineManager = packageDetailsPipelineManager;
	}

	/**
	 * Gets amend package details pipeline manager.
	 *
	 * @return the amendPackageDetailsPipelineManager
	 */
	protected PackageDetailsPipelineManager getAmendPackageDetailsPipelineManager()
	{
		return amendPackageDetailsPipelineManager;
	}

	/**
	 * Sets amend package details pipeline manager.
	 *
	 * @param amendPackageDetailsPipelineManager
	 * 		the amendPackageDetailsPipelineManager to set
	 */
	public void setAmendPackageDetailsPipelineManager(final PackageDetailsPipelineManager amendPackageDetailsPipelineManager)
	{
		this.amendPackageDetailsPipelineManager = amendPackageDetailsPipelineManager;
	}

	/**
	 * Gets travel cart facade.
	 *
	 * @return the travel cart facade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * Sets travel cart facade.
	 *
	 * @param travelCartFacade
	 * 		the travel cart facade
	 */
	@Required
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}

	/**
	 * Gets booking facade.
	 *
	 * @return the booking facade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * Sets booking facade.
	 *
	 * @param bookingFacade
	 * 		the booking facade
	 */
	@Required
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}

	/**
	 * Gets accommodation cart facade.
	 *
	 * @return the accommodation cart facade
	 */
	protected AccommodationCartFacade getAccommodationCartFacade()
	{
		return accommodationCartFacade;
	}

	/**
	 * Sets accommodation cart facade.
	 *
	 * @param accommodationCartFacade
	 * 		the accommodation cart facade
	 */
	@Required
	public void setAccommodationCartFacade(final AccommodationCartFacade accommodationCartFacade)
	{
		this.accommodationCartFacade = accommodationCartFacade;
	}
}
