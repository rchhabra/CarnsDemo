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

package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


/**
 * Data list populator for NDC {@link ServiceListRS}
 */
public class NDCServiceListRSDataListPopulator extends NDCAbstractOffersRSDataListPopulator
		implements Populator<OfferResponseData, ServiceListRS>
{

	@Override
	public void populate(final OfferResponseData source, final ServiceListRS target) throws ConversionException
	{
		final Map<String, Integer> map = createTravellerTypeCountMap(source);
		final DataListType dataListType = new DataListType();
		populateFlightSegments(source, dataListType);
		dataListType.setAnonymousTravelerList(createTravellers(map));
		target.setDataLists(dataListType);
	}

}
