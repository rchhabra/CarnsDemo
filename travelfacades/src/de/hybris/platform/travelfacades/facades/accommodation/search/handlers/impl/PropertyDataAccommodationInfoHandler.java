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

package de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.AccommodationInfoData;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;


/**
 * Concrete implementation of the {@link PropertyHandler} interface. Handler is responsible to the accommodation attributes on
 * the {@link PropertyData}
 */
public class PropertyDataAccommodationInfoHandler extends AbstractDefaultPropertyHandler implements PropertyHandler
{

	@Override
	public void handle(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final AccommodationSearchRequestData accommodationSearchRequest, final PropertyData propertyData)
	{
		if (BooleanUtils.isTrue(validateDayRatesAgainstRequest(dayRatesForRoomStayCandidate, accommodationSearchRequest)
				&& CollectionUtils.isNotEmpty(propertyData.getRatePlanConfigs())))
		{
			handlingAttributes(dayRatesForRoomStayCandidate, propertyData);
		}
	}

	/**
	 * Populates the list of accommodation info, e.g. accommodation names with distinct values and their cardinality
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
		dayRatesForRoomStayCandidate.entrySet().forEach(entry -> entry.getValue().get(0).getAccommodationInfos().forEach(name -> {
			final Optional<AccommodationInfoData> optionalInfo = accInfos.stream()
					.filter(info -> info.getAccommodationName().equals(name)).findFirst();
			if (optionalInfo.isPresent())
			{
				optionalInfo.get().setCardinality(optionalInfo.get().getCardinality() + 1);
			}
			else
			{
				final AccommodationInfoData accommodationInfo = new AccommodationInfoData();
				accommodationInfo.setAccommodationName(name);
				accommodationInfo.setCardinality(1);
				accInfos.add(accommodationInfo);
			}

		}));
		propertyData.getRateRange().setAccommodationInfos(accInfos);
	}
}
