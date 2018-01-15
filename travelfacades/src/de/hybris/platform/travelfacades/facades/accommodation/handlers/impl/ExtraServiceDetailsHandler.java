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
import de.hybris.platform.commercefacades.accommodation.ServiceDetailData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationServiceHandler;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler class to populate serviceDetails attribute of the serviceData for a given productModel and
 * reservedRoomStayData
 */
public class ExtraServiceDetailsHandler implements AccommodationServiceHandler
{
	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void handle(final ProductModel productModel, final ReservedRoomStayData reservedRoomStayData,
			final ServiceData serviceData, final AccommodationReservationData accommodationReservationData)
	{
		final ServiceDetailData serviceDetails = new ServiceDetailData();
		serviceDetails.setProduct(getProductConverter().convert(productModel));
		serviceDetails.setGuestCounts(reservedRoomStayData.getGuestCounts());

		serviceData.setServiceDetails(serviceDetails);
	}

	/**
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 *           the productConverter to set
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

}
