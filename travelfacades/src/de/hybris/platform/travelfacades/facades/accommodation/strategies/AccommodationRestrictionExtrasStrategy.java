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

package de.hybris.platform.travelfacades.facades.accommodation.strategies;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AccommodationRestrictionData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;


/**
 * Abstract strategy to create check the availability of the extra services and create an accommodationRestrictionData
 * for each product
 */
public interface AccommodationRestrictionExtrasStrategy
{

	/**
	 * Applies the strategy to check the availability of the product against the reservedRoomStayData for each date. If
	 * the product is available it returns the corresponding restrictionData, otherwise it throws a
	 * {@link AccommodationPipelineException}
	 *
	 * @param productModel
	 * 		the product model
	 * @param reservedRoomStayData
	 * 		the reserved room stay data
	 * @param accommodationReservationData
	 * 		the accommodation reservation data
	 * @return the accommodationRestrictionData if the product is available
	 * @throws AccommodationPipelineException
	 * 		if the product is not available for the given accommodationOffering in the selected dates.
	 */
	AccommodationRestrictionData applyStrategy(ProductModel productModel, ReservedRoomStayData reservedRoomStayData,
											   AccommodationReservationData accommodationReservationData) throws AccommodationPipelineException;

}
