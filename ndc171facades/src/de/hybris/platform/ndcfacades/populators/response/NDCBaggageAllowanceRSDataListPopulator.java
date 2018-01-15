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
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS.DataLists;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * Data list populator for NDC {@link BaggageAllowanceRS}
 */
public class NDCBaggageAllowanceRSDataListPopulator extends NDCAbstractOffersRSDataListPopulator
		implements Populator<OfferResponseData, BaggageAllowanceRS>
{
	@Override
	public void populate(final OfferResponseData source, final BaggageAllowanceRS target) throws ConversionException
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
	 * @param passengerTypeCountMap
	 *           the passenger type count map
	 * @return the passenger list
	 */
	protected PassengerList createTravellers(final Map<String, Integer> passengerTypeCountMap)
	{
		final PassengerList passengerList = new PassengerList();
		passengerTypeCountMap.forEach((ptc, count) -> {
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
