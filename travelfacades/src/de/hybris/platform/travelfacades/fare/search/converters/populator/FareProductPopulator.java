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

package de.hybris.platform.travelfacades.fare.search.converters.populator;

import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.product.FareProductModel;


/**
 * Populator class to populate FareProductData from FareProductModel
 *
 * @param <SOURCE>
 * 		the type parameter
 * @param <TARGET>
 * 		the type parameter
 */
public class FareProductPopulator<SOURCE extends FareProductModel, TARGET extends FareProductData>
		implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setBookingClass(source.getBookingClass());
		target.setFareBasisCode(source.getFareBasisCode());
	}

}
