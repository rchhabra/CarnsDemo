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
import de.hybris.platform.commercefacades.accommodation.AwardData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.travel.enums.AwardType;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.PropertyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * Concrete implementation of the {@link PropertyHandler} interface. Handler is responsible to set common attributes on
 * the {@link PropertyData}
 */
public class PropertyDataBasicHandler extends AbstractDefaultPropertyHandler implements PropertyHandler
{

	@Override
	public void handle(final Map<Integer, List<AccommodationOfferingDayRateData>> propertyMap,
			final AccommodationSearchRequestData accommodationSearchRequest, final PropertyData propertyData)
	{
		handlingAttributes(propertyMap, propertyData);
	}

	/**
	 * Populates all the fields belonging to the same property
	 *
	 * @param dayRatesForRoomStayCandidate
	 *           map of documents per room stay candidates
	 * @param propertyData
	 *           the dto to be populated
	 */
	@Override
	protected void handlingAttributes(final Map<Integer, List<AccommodationOfferingDayRateData>> dayRatesForRoomStayCandidate,
			final PropertyData propertyData)
	{
		final Optional<Map.Entry<Integer, List<AccommodationOfferingDayRateData>>> optionalEntry = dayRatesForRoomStayCandidate
				.entrySet().stream().findAny();
		if (!optionalEntry.isPresent())
		{
			return;
		}
		final AccommodationOfferingDayRateData accommodationOfferingDayRateData = optionalEntry.get().getValue().get(0);
		propertyData.setAddress(accommodationOfferingDayRateData.getAddressData());
		if (Objects.nonNull(accommodationOfferingDayRateData.getLatitude())
				&& Objects.nonNull(accommodationOfferingDayRateData.getLongitude()))
		{
			final PositionData positionData = new PositionData();
			positionData.setLatitude(accommodationOfferingDayRateData.getLatitude());
			positionData.setLongitude(accommodationOfferingDayRateData.getLongitude());
			propertyData.setPosition(positionData);
		}
		propertyData.setAccommodationOfferingCode(accommodationOfferingDayRateData.getAccommodationOfferingCode());
		propertyData.setAccommodationOfferingName(accommodationOfferingDayRateData.getAccommodationOfferingName());
		propertyData.setChainCode(accommodationOfferingDayRateData.getChainCode());
		propertyData.setChainName(accommodationOfferingDayRateData.getChainName());

		propertyData.setPromoted(accommodationOfferingDayRateData.getBoosted());

		final List<AwardData> awards = new ArrayList<>();
		final Double userRating = accommodationOfferingDayRateData.getAverageUserRating();
		if (Objects.nonNull(userRating) && userRating.doubleValue() > 0)
		{
			awards.add(createAwardData(AwardType.USER_RATING, userRating,
					Objects.nonNull(accommodationOfferingDayRateData.getNumberOfReviews())
							? accommodationOfferingDayRateData.getNumberOfReviews().toString() : StringUtils.EMPTY));
		}
		final Integer starRating = accommodationOfferingDayRateData.getStarRating();
		if (Objects.nonNull(starRating) && starRating.intValue() > 0)
		{
			awards.add(createAwardData(AwardType.STAR_RATING, Double.valueOf(starRating.doubleValue()), StringUtils.EMPTY));
		}
		propertyData.setAwards(awards);

		if (StringUtils.isNotEmpty(accommodationOfferingDayRateData.getMainImageMediaUrl()))
		{
			final ImageData imageData = new ImageData();
			imageData.setImageType(ImageDataType.PRIMARY);
			imageData.setUrl(accommodationOfferingDayRateData.getMainImageMediaUrl());
			propertyData.setImages(Collections.singletonList(imageData));
		}

	}

	protected AwardData createAwardData(final AwardType awardType, final Double rating, final String additionalInfo)
	{
		final AwardData award = new AwardData();
		award.setType(awardType);
		award.setRating(rating);
		award.setAdditionalInfo(additionalInfo);
		return award;
	}
}
