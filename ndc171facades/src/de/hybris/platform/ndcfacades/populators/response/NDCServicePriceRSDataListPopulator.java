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
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS.DataLists;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS.DataLists.PassengerList;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * Data list populator for NDC {@link ServicePriceRS}
 */
public class NDCServicePriceRSDataListPopulator extends NDCAbstractOffersRSDataListPopulator
		implements Populator<OfferResponseData, ServicePriceRS>
{

	@Override
	public void populate(final OfferResponseData source, final ServicePriceRS target) throws ConversionException
	{
		final Map<String, Integer> map = createTravellerTypeCountMap(source);
		final DataLists dataLists = new DataLists();
		populateFlightSegments(source, dataLists);
		dataLists.setPassengerList(createTravellers(map));
		target.setDataLists(dataLists);
	}

	/**
	 * This method create an instance of {@link PassengerList} for given map.
	 *
	 * @param map
	 *           the hash map
	 *
	 * @return the anonymous traveler list
	 */
	protected PassengerList createTravellers(final Map<String, Integer> map)
	{
		final PassengerList passengerList = new PassengerList();
		map.forEach((ptc, count) -> {
			IntStream.rangeClosed(1, count).forEach(passengerTypeIndex -> {
				final PassengerType passenger = new PassengerType();
				passenger.setPassengerID(ptc + passengerTypeIndex);
				passenger.setPTC(ptc);
				passengerList.getPassenger().add(passenger);
			});
		});
		return passengerList;
	}

}
