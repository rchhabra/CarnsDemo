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
package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.services.CabinClassService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facade that provides Cabin Class specific services. The facade uses the CabinClassService to get CabinClassModels and
 * uses converter/populators to transfer CabinClassData data types.
 */

public class DefaultCabinClassFacade implements CabinClassFacade
{

	private CabinClassService cabinClassService;
	private Converter<CabinClassModel, CabinClassData> cabinClassConverter;

	@Override
	public List<CabinClassData> getCabinClasses()
	{

		final List<CabinClassModel> ccModels = getCabinClassService().getCabinClasses();

		return Converters.convertAll(ccModels, getCabinClassConverter());
	}

	@Override
	public CabinClassData findCabinClassFromBundleTemplate(final String bundleTemplateId)
	{
		return getCabinClassConverter().convert(getCabinClassService().findCabinClassFromBundleTemplate(bundleTemplateId));
	}

	protected Converter<CabinClassModel, CabinClassData> getCabinClassConverter()
	{
		return cabinClassConverter;
	}

	@Required
	public void setCabinClassConverter(final Converter<CabinClassModel, CabinClassData> cabinClassConverter)
	{
		this.cabinClassConverter = cabinClassConverter;
	}

	protected CabinClassService getCabinClassService()
	{
		return cabinClassService;
	}

	@Required
	public void setCabinClassService(final CabinClassService cabinClassService)
	{
		this.cabinClassService = cabinClassService;
	}

}
