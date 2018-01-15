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

package de.hybris.platform.travelfacades.util;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.StringUtils;


/**
 * This class contains some reusable methods required for Traveller population
 *
 * @deprecated
 * 	This class utils has been deprecated since version 2.0, please check methods Javadoc to get details on how to replace it.
 */
@Deprecated
public class TravellerUtils
{
	private TravellerUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * This method populates a Map of Traveller Code with their display names for a given Passenger Type code
	 *
	 * @param travellerDatas
	 * @param passengerTypeFacade
	 *
	 * @return Map<String, Map<String, String>> object
	 *
	 * @deprecated
	 * 	Deprecated since version 2.0. Replaced by {@link de.hybris.platform.travelfacades.facades.TravellerFacade#populateTravellersNamesMap(List)}
	 * 	to avoid need of passing {@link PassengerTypeFacade} as a param.
	 */
	@Deprecated
	public static Map<String, Map<String, String>> populateTravellersNamesMap(final List<TravellerData> travellerDatas,
			final PassengerTypeFacade passengerTypeFacade)
	{
		final Map<String, Map<String, String>> travellerPassengerTypeMap = new HashMap<>();

		for (final PassengerTypeData passengerTypeData : passengerTypeFacade.getPassengerTypes())
		{
			final Map<String, String> travellerNameMap = new HashMap<>();

			int counter = 1;
			for (final TravellerData travellerData : travellerDatas)
			{
				if (!passengerTypeData.getCode()
						.equalsIgnoreCase(((PassengerInformationData) travellerData.getTravellerInfo()).getPassengerType().getCode()))
				{
					continue;
				}
				if (!travellerPassengerTypeMap.containsKey(passengerTypeData.getCode()))
				{
					travellerPassengerTypeMap.put(passengerTypeData.getCode(), travellerNameMap);
				}
				final PassengerInformationData passengerInfoData = ((PassengerInformationData) travellerData.getTravellerInfo());
				if (StringUtils.isEmpty(passengerInfoData.getFirstName()) && StringUtils.isEmpty(passengerInfoData.getSurname()))
				{
					travellerNameMap.put(travellerData.getLabel(), passengerInfoData.getPassengerType().getName() + " " + counter++);
				}
				else
				{
					travellerNameMap.put(travellerData.getLabel(),
							passengerInfoData.getFirstName() + " " + passengerInfoData.getSurname());
				}

			}
		}
		return travellerPassengerTypeMap;
	}
}
