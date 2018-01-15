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
 */

package de.hybris.platform.travelfacades.facades.packages.handlers;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;


/**
 * Interface for PackageReservedRoomStay handlers
 */
public interface PackageReservedRoomStayHandler
{
	/**
	 * Evaluates reserved room stays for given package request and sets them on accommodation availability response.
	 *
	 * @param packageRequestData
	 * @param accommodationAvailabilityResponseData
	 */
	void handle(PackageRequestData packageRequestData,
			AccommodationAvailabilityResponseData accommodationAvailabilityResponseData);
}
