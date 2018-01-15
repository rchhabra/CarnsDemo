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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AccommodationRestrictionData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationServiceHandler;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationRestrictionExtrasStrategy;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;


/**
 * Handler class to populate the restriction attribute of the serviceDetailsData for the extra services.
 */
public class ExtraServiceRestrictionHandler implements AccommodationServiceHandler
{

	private static final Logger LOG = Logger.getLogger(ExtraServiceRestrictionHandler.class);
	private static final String DEFAULT_STRATEGY = "default";

	private Map<String, AccommodationRestrictionExtrasStrategy> extrasAvailabilityStrategyMap;

	@Override
	public void handle(final ProductModel productModel, final ReservedRoomStayData reservedRoomStayData,
					   final ServiceData serviceData, final AccommodationReservationData accommodationReservationData)
	{
		final TravelRestrictionModel travelRestriction = productModel.getTravelRestriction();

		AccommodationRestrictionExtrasStrategy strategy = null;
		if(travelRestriction != null)
		{
			strategy = getExtrasAvailabilityStrategyMap().get(travelRestriction.getClass().getSimpleName());
		}

		if(strategy == null)
		{
			strategy = getExtrasAvailabilityStrategyMap().get(DEFAULT_STRATEGY);
		}

		final AccommodationRestrictionData restrictionData;
		try
		{
			restrictionData = strategy.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData);
		}
		catch (final AccommodationPipelineException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Product with code " + productModel.getCode() + " is not available for accommodationOffering with code "
						+ reservedRoomStayData.getRoomTypes().get(0).getCode());
			}
			throw e;
		}

		serviceData.getServiceDetails().setRestriction(restrictionData);
	}

	/**
	 * @return the extrasAvailabilityStrategyMap
	 */
	protected Map<String, AccommodationRestrictionExtrasStrategy> getExtrasAvailabilityStrategyMap()
	{
		return extrasAvailabilityStrategyMap;
	}

	/**
	 * @param extrasAvailabilityStrategyMap
	 *           the extrasAvailabilityStrategyMap to set
	 */
	@Required
	public void setExtrasAvailabilityStrategyMap(final Map<String, AccommodationRestrictionExtrasStrategy> extrasAvailabilityStrategyMap)
	{
		this.extrasAvailabilityStrategyMap = extrasAvailabilityStrategyMap;
	}

}
