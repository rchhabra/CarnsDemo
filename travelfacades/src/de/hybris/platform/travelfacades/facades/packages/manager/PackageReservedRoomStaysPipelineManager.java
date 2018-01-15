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

package de.hybris.platform.travelfacades.facades.packages.manager;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;


/**
 * Pipeline manager responsible for populating reserved room stays on accommodation availability response based on the package
 * request provided.
 */
public interface PackageReservedRoomStaysPipelineManager
{
	/**
	 * Executes a list of handlers which will evaluate reserved room stays based on either business configuration or price of
	 * available rate plans.
	 *
	 * @param packageRequestData
	 * @param accommodationAvailabilityResponseData
	 */
	void executePipeline(PackageRequestData packageRequestData,
			AccommodationAvailabilityResponseData accommodationAvailabilityResponseData);
}
