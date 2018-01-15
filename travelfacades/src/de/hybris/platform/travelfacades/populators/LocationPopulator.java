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
*/
package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.LocationModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.travel.LocationModel} as source and
 * {@link de.hybris.platform.commercefacades.travel.LocationData} as target type.
 */
public class LocationPopulator implements Populator<LocationModel, LocationData>
{

	private Converter<MediaModel, ImageData> imageConverter;

	@Override
	public void populate(final LocationModel source, final LocationData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());

		if (source.getPicture() != null)
		{
			target.setImage(getImageConverter().convert(source.getPicture()));
		}
	}

	/**
	 * Gets image converter.
	 *
	 * @return the image converter
	 */
	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	/**
	 * Sets image converter.
	 *
	 * @param imageConverter
	 * 		the image converter
	 */
	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

}
