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
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationServiceHandler;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;


/**
 * Handler class to populate the basic information of the serviceData for a given productModel and reservedRoomStayData
 */
public class ExtraServiceBasicHandler implements AccommodationServiceHandler
{

	@Override
	public void handle(final ProductModel productModel, final ReservedRoomStayData reservedRoomStayData,
					   final ServiceData serviceData, final AccommodationReservationData accommodationReservationData)
	{
		serviceData.setCode(productModel.getCode());
		serviceData.setInclusive(Boolean.FALSE);
		serviceData.setRatePlanCode(reservedRoomStayData.getRatePlans().get(0).getCode());

		serviceData.setQuantity(getServiceQuantity(serviceData.getCode(), reservedRoomStayData));
	}

	protected Integer getServiceQuantity(final String code, final ReservedRoomStayData reservedRoomStay)
	{
		final Optional<ServiceData> optionalService = reservedRoomStay.getServices().stream()
				.filter(service -> StringUtils.equalsIgnoreCase(service.getCode(), code)).findFirst();
		return optionalService.isPresent() ? optionalService.get().getQuantity() : Integer.valueOf(0);
	}

}
