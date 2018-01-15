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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class will populate the {@link FacilityData} from the {@link PropertyFacilityModel}
 */
public class PropertyFacilityPopulator<SOURCE extends PropertyFacilityModel, TARGET extends FacilityData>
		implements Populator<SOURCE, TARGET>
{
	private EnumerationService enumerationService;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setFacilityType(getEnumerationService().getEnumerationName(source.getType()));
		target.setDescription(source.getShortDescription());
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
