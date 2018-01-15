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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.AccommodationInfoData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataAccommodationInfoHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Extension of the {@link PropertyDataAccommodationInfoHandler} class. Handler is responsible for setting the
 * AccommodationInfos of rateRange
 */
public class PackageAccommodationInfoHandler extends PropertyDataAccommodationInfoHandler
{
	private static final Logger LOG = Logger.getLogger(PackageAccommodationInfoHandler.class);

	/**
	 * Populates the list of accommodation info
	 *
	 * @param dayRatesForRoomStayCandidate
	 *           map of documents per room stay candidates
	 * @param propertyData
	 *           the dto to be populated
	 *
	 */
	@Override
	protected void handlingAttributes(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final PropertyData propertyData)
	{
		final List<AccommodationInfoData> accInfos = new ArrayList<>();
		for (final Map.Entry<Integer, List<AccommodationOfferingDayRateData>> entry : dayRatesForRoomStayCandidate.entrySet())
		{
			final AccommodationOfferingDayRateData accommodationOfferingDayRateData = entry.getValue().get(0);
			if (CollectionUtils.isEmpty(accommodationOfferingDayRateData.getRatePlanConfigs()))
			{
				continue;
			}

			for (final String ratePlanConfig : accommodationOfferingDayRateData.getRatePlanConfigs())
			{
				int quantity = 0;
				try
				{
					quantity = Integer.parseInt(ratePlanConfig.split("\\|", 3)[2]);
				}
				catch (final NumberFormatException e)
				{
					LOG.error("Cannot parse number of rooms string to integer" + e.getClass().getName() + " : " + e.getMessage());
				}
				if (quantity == 1)
				{
					accommodationOfferingDayRateData.getAccommodationInfos().forEach(name -> {
						final AccommodationInfoData accommodationInfo = new AccommodationInfoData();
						accommodationInfo.setAccommodationName(name);
						accommodationInfo.setCardinality(1);
						accInfos.add(accommodationInfo);
					});
				}
			}
		}
		propertyData.getRateRange().setAccommodationInfos(accInfos);
	}
}
