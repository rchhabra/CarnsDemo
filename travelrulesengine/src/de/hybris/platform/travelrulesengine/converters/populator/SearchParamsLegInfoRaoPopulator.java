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
package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.LegInfoRAO;
import de.hybris.platform.travelrulesengine.rao.SearchParamsRAO;

import java.util.ArrayList;
import java.util.List;


/**
 * Populates list of {@link LegInfoRAO} attribute from {@link FareSearchRequestData}
 */
public class SearchParamsLegInfoRaoPopulator implements Populator<FareSearchRequestData, SearchParamsRAO>
{
	@Override
	public void populate(final FareSearchRequestData source, final SearchParamsRAO target)
			throws ConversionException
	{
		final List<LegInfoRAO> legInfos = new ArrayList<>();
		source.getOriginDestinationInfo().forEach(odInfo -> legInfos.add(createLegInfo(odInfo)));
		target.setLegInfos(legInfos);
	}

	/**
	 * Create leg info leg info rao.
	 *
	 * @param odInfo
	 * 		the od info
	 * @return the leg info rao
	 */
	protected LegInfoRAO createLegInfo(final OriginDestinationInfoData odInfo)
	{
		final LegInfoRAO legInfo = new LegInfoRAO();
		legInfo.setReferenceNumber(odInfo.getReferenceNumber());
		legInfo.setDepartureTime(odInfo.getDepartureTime());
		return legInfo;
	}
}
