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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageReservedRoomStayHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageReservedRoomStaysPipelineManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PackageReservedRoomStaysPipelineManager}
 */
public class DefaultPackageReservedRoomStaysPipelineManager implements PackageReservedRoomStaysPipelineManager
{
	private List<PackageReservedRoomStayHandler> handlers;

	@Override
	public void executePipeline(final PackageRequestData packageRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		getHandlers().forEach(handler -> handler.handle(packageRequestData, accommodationAvailabilityResponseData));
	}

	/**
	 * Gets handlers.
	 *
	 * @return the handlers
	 */
	protected List<PackageReservedRoomStayHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * Sets handlers.
	 *
	 * @param handlers
	 * 		the handlers
	 */
	@Required
	public void setHandlers(final List<PackageReservedRoomStayHandler> handlers)
	{
		this.handlers = handlers;
	}
}
