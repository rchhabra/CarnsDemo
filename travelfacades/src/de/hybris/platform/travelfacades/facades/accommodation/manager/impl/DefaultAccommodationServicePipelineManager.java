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

package de.hybris.platform.travelfacades.facades.accommodation.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationServiceHandler;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationServicePipelineManager;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


/**
 * Implementation class for the {@link AccommodationServicePipelineManager}. This pipeline manager will instantiate a
 * new {@link ServiceData} and will call a list of handlers to populate the serviceData with different information.
 */
public class DefaultAccommodationServicePipelineManager implements AccommodationServicePipelineManager
{
	private static final Logger LOG = Logger.getLogger(DefaultAccommodationServicePipelineManager.class);
	private List<AccommodationServiceHandler> handlers;

	@Override
	public ServiceData executePipeline(final ProductModel productModel, final ReservedRoomStayData reservedRoomStayData,
									   final AccommodationReservationData accommodationReservationData)
	{

		final ServiceData serviceData = new ServiceData();

		try
		{
			for (final AccommodationServiceHandler handler : getHandlers())
			{
				handler.handle(productModel, reservedRoomStayData, serviceData, accommodationReservationData);
			}
		}
		catch (final AccommodationPipelineException e)
		{
			LOG.error("Unable to complete Accommodation Service Pipeline Manager", e);
			return null;
		}

		return serviceData;
	}

	/**
	 * @return the handlers
	 */
	protected List<AccommodationServiceHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * @param handlers
	 *           the handlers to set
	 */
	@Required
	public void setHandlers(final List<AccommodationServiceHandler> handlers)
	{
		this.handlers = handlers;
	}

}
