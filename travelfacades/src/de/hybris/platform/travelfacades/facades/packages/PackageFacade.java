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

package de.hybris.platform.travelfacades.facades.packages;

import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;

import java.util.List;


/**
 * The interface for Package Facade.
 */
public interface PackageFacade
{
	/**
	 * Evaluates package response based on the package request provided.
	 *
	 * @param packageRequestData package request data
	 * @return the package response data
	 */
	PackageResponseData getPackageResponse(PackageRequestData packageRequestData);

	/**
	 * Evaluates package response based on the package request provided and the transportation from cart.
	 *
	 * @param packageRequestData
	 *           package request data
	 * @return the package response data
	 */
	PackageResponseData getAmendPackageResponse(PackageRequestData packageRequestData);

	/**
	 * Checks if the cart is there and it has both transportation and accommodation parts
	 *
	 * @return status of the package cart
	 */
	Boolean isPackageInCart();

	/**
	 * Checks if the order is there and it has both transportation and accommodation parts
	 * 
	 * @param bookingReference
	 * @return status of the package in order
	 */
	Boolean isPackageInOrder(String bookingReference);

	/**
	 * Verifies if whatever is currently in the cart, matches the details of package specified in the request. If it
	 * doesn't, the cart will be cleared.
	 *
	 * @param departureLocation         departure location
	 * @param arrivalLocation           arrival location
	 * @param departureDate             departure date
	 * @param returnDate                returning date (can be empty)
	 * @param accommodationOfferingCode accommodation offering code
	 * @param checkInDate               check in date
	 * @param checkOutDate              check out date
	 * @param roomStayCandidates
	 */
	void cleanUpCartBeforeAddition(String departureLocation, String arrivalLocation, String departureDate, String returnDate,
			String accommodationOfferingCode, String checkInDate, String checkOutDate,
			List<RoomStayCandidateData> roomStayCandidates);
}
