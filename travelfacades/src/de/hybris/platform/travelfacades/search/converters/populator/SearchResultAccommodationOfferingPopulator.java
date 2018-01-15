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
package de.hybris.platform.travelfacades.search.converters.populator;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;



/**
 * The class is responsible for populating AccommodationOfferingData DTO object from the solr search result data.
 */
public class SearchResultAccommodationOfferingPopulator
		implements Populator<SearchResultValueData, AccommodationOfferingDayRateData>
{
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private CommonI18NService commonI18NService;

	/**
	 * The method populates AccommodationOfferingDayRateData.
	 *
	 * @param source
	 * @param target
	 */
	@Override
	public void populate(final SearchResultValueData source, final AccommodationOfferingDayRateData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setAccommodationOfferingCode(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_PROPERTY_CODE));
		target.setAccommodationOfferingName(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_PROPERTY_NAME));
		target.setAdultsCount(this.<Integer> getValue(source, TravelfacadesConstants.SOLR_FIELD_NUMBER_OF_ADULTS));

		final String dateOfStayStr = this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_DATE_OF_STAY);
		target.setDateOfStay(TravelDateUtils.convertStringDateToDate(dateOfStayStr, TravelservicesConstants.DATE_PATTERN));

		final Double priceValue = this.<Double> getValue(source, TravelfacadesConstants.SOLR_FIELD_PRICE_VALUE);
		if (priceValue != null)
		{
			final PriceData priceData = getTravelCommercePriceFacade().createPriceData(priceValue);
			target.setPrice(priceData);
		}

		final Double taxValue = this.<Double> getValue(source, TravelfacadesConstants.SOLR_FIELD_TAX_VALUE);
		if (Objects.nonNull(taxValue))
		{
			final PriceData taxData = getTravelCommercePriceFacade().createPriceData(taxValue);
			target.setTotalTaxes(taxData);
		}
		target.setStarRating(this.<Integer> getValue(source, TravelfacadesConstants.SOLR_FIELD_STAR_RATING));
		target.setAverageUserRating(this.<Double> getValue(source, TravelfacadesConstants.SOLR_FIELD_AVERAGE_USER_RATING));
		target.setNumberOfReviews(this.<Integer> getValue(source, TravelfacadesConstants.SOLR_FIELD_NUMBER_OF_REVIEWS));
		target.setMainImageMediaUrl(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_IMAGE_URL));
		target.setChildrenCountMin(this.<Integer> getValue(source, TravelfacadesConstants.SOLR_FIELD_MIN_CHILDREN_COUNT));
		target.setChildrenCountMax(this.<Integer> getValue(source, TravelfacadesConstants.SOLR_FIELD_MAX_CHILDREN_COUNT));
		target.setLocationCodes(this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_LOCATION_CODES));
		target.setLocationNames(getValue(source, TravelfacadesConstants.SOLR_FIELD_LOCATION_NAMES));

		final List<String> positionCoordinates = this.<List<String>> getValue(source,
				TravelfacadesConstants.SOLR_FIELD_POSITION_COORDINATES);
		if (CollectionUtils.isNotEmpty(positionCoordinates) && StringUtils.contains(positionCoordinates.get(0),
				TravelfacadesConstants.SOLR_LATITUDE_LONGITUDE_POSITION_SEPARATOR))
		{
			final String[] latlonCodes = positionCoordinates.get(0)
					.split(TravelfacadesConstants.SOLR_LATITUDE_LONGITUDE_POSITION_SEPARATOR);
			target.setLatitude(Double.valueOf(latlonCodes[0]));
			target.setLongitude(Double.valueOf(latlonCodes[1]));
		}

		target.setAccommodationInfos(getValue(source, TravelfacadesConstants.SOLR_FIELD_ACCOMMODATION_INFOS));
		target.setRatePlanConfigs(getValue(source, TravelfacadesConstants.SOLR_FIELD_RATE_PLAN_CONFIGS));
		final String formattedAddress = this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_ADDRESS);
		final String contactNumber = this.<String> getValue(source, TravelfacadesConstants.SOLR_FIELD_CONTACT_NUMBER);

		final AddressData address = new AddressData();
		address.setPhone(contactNumber);
		address.setFormattedAddress(formattedAddress);

		target.setAddressData(address);

		target.setBoosted(this.<Boolean> getValue(source, TravelfacadesConstants.SOLR_FIELD_BOOSTED));
	}

	/**
	 * Gets value.
	 *
	 * @param <T>
	 *           the type parameter
	 * @param source
	 *           the source
	 * @param propertyName
	 *           the property name
	 * @return the value
	 */
	protected <T> T getValue(final SearchResultValueData source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}
		return (T) source.getValues().get(propertyName);
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the commonI18NService
	 */
	@Deprecated
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Deprecated
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
