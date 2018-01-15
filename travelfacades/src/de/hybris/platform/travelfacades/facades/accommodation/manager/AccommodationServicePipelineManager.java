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

package de.hybris.platform.travelfacades.facades.accommodation.manager;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * Interface for Accommodation Service Pipeline Manager.
 */
public interface AccommodationServicePipelineManager
{

	/**
	 * This method will execute all the handler that will populate the serviceData
	 *
	 * @param productModel
	 * 		the product model
	 * @param reservedRoomStayData
	 * 		the reserved room stay data
	 * @param reservationData
	 * 		the reservation data
	 * @return the serviceData
	 */
	ServiceData executePipeline(ProductModel productModel, ReservedRoomStayData reservedRoomStayData,
								AccommodationReservationData reservationData);

}
