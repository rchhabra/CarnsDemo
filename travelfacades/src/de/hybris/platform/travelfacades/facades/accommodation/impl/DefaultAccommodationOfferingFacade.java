/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.accommodation.search.RadiusData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.search.AccommodationOfferingSearchFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationOfferingFacade}
 */
public class DefaultAccommodationOfferingFacade implements AccommodationOfferingFacade
{

	private AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> accommodationOfferingSearchFacade;
	private AccommodationOfferingService accommodationOfferingService;
	private AccommodationDetailsPipelineManager accommodationDetailsPipelineManager;
	private AccommodationDetailsPipelineManager selectedAccommodationDetailsPipelineManager;
	private AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter;

	@Override
	public AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> searchAccommodationOfferingDayRates(
			final AccommodationSearchRequestData accommodationRequestData, final RoomStayCandidateData roomStayCandidateData)
	{
		final SearchData searchData = new SearchData();

		final Map<String, String> filterTerms = new HashMap<>();

		final PositionData position = accommodationRequestData.getCriterion().getPosition();
		final RadiusData radius = accommodationRequestData.getCriterion().getRadius();

		if (position != null && radius != null)
		{
			searchData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_SPATIAL);

			filterTerms.put(TravelservicesConstants.SOLR_FIELD_POSITION,
					position.getLatitude() + TravelservicesConstants.LOCATION_TYPE_COORDINATES_SEPARATOR + position.getLongitude());
			filterTerms.put(TravelservicesConstants.SOLR_FIELD_RADIUS, Double.toString(radius.getValue()));
		}
		else if (accommodationRequestData.getCriterion().getAccommodationReference() != null && StringUtils
				.isNotEmpty(accommodationRequestData.getCriterion().getAccommodationReference().getAccommodationOfferingCode()))
		{
			searchData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_ACCOMMODATION);
			filterTerms.put(TravelservicesConstants.SOLR_FIELD_PROPERTY_CODE,
					accommodationRequestData.getCriterion().getAccommodationReference().getAccommodationOfferingCode());
			//else if it consist of final accoomodaiton offering code final in criterian  then final set filter terms with code.
		}
		else
		{
			searchData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_ACCOMMODATION);
			filterTerms.put(TravelfacadesConstants.SOLR_FIELD_LOCATION_CODES,
					accommodationRequestData.getCriterion().getAddress().getLine1());
		}

		final String propertyFilterText = accommodationRequestData.getCriterion().getPropertyFilterText();
		if (StringUtils.isNotBlank(propertyFilterText))
		{
			searchData.setFreeTextSearch(propertyFilterText);
		}

		filterTerms.put(TravelfacadesConstants.SOLR_FIELD_DATE_OF_STAY,
				getDateOfStayFormatted(accommodationRequestData.getCriterion().getStayDateRange()));
		filterTerms.put(TravelfacadesConstants.SOLR_FIELD_NUMBER_OF_ADULTS,
				getNumberOfAdults(roomStayCandidateData.getPassengerTypeQuantityList()));

		searchData.setFilterTerms(filterTerms);
		final String query = StringUtils.isNotEmpty(accommodationRequestData.getCriterion().getQuery())
				? accommodationRequestData.getCriterion().getQuery() : StringUtils.EMPTY;
		searchData.setQuery(query);

		final AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> accommodationOfferingSearchPageData;
		if (StringUtils.isNotBlank(accommodationRequestData.getCriterion().getSort()))
		{
			final PageableData pageableData = new PageableData();
			pageableData.setSort(accommodationRequestData.getCriterion().getSort());
			searchData.setSort(accommodationRequestData.getCriterion().getSort());
			accommodationOfferingSearchPageData = getAccommodationOfferingSearchFacade().accommodationOfferingSearch(searchData,
					pageableData);
		}
		else
		{
			accommodationOfferingSearchPageData = getAccommodationOfferingSearchFacade().accommodationOfferingSearch(searchData);
		}

		return accommodationOfferingSearchPageData;
	}

	@Override
	public PropertyData getPropertyData(final String accommodationOfferingCode)
	{
		final AccommodationOfferingModel accommodationOfferingModel = accommodationOfferingService
				.getAccommodationOffering(accommodationOfferingCode);

		return getAccommodationOfferingConverter().convert(accommodationOfferingModel);
	}

	@Override
	public boolean checkAvailability(final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		if (CollectionUtils.isEmpty(accommodationAvailabilityResponse.getRoomStays()))
		{
			return false;
		}

		for (final RoomStayData roomStay : accommodationAvailabilityResponse.getRoomStays())
		{
			final Optional<RatePlanData> optionalAvailableRatePlan = roomStay.getRatePlans().stream()
					.filter(ratePlan -> ratePlan.getAvailableQuantity() != null && ratePlan.getAvailableQuantity() > 0).findAny();
			if (optionalAvailableRatePlan.isPresent())
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAccommodationAvailableForQuickSelection(
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponse)
	{
		if (CollectionUtils.isEmpty(accommodationAvailabilityResponse.getRoomStays()))
		{
			return Boolean.FALSE;
		}

		for (final RoomStayData roomStay : accommodationAvailabilityResponse.getRoomStays())
		{
			final Optional<RatePlanData> optionalAvailableRatePlan = roomStay.getRatePlans().stream()
					.filter(ratePlan -> ratePlan.getAvailableQuantity() == null
							|| (ratePlan.getAvailableQuantity() != null && ratePlan.getAvailableQuantity() < 1))
					.findAny();
			if (optionalAvailableRatePlan.isPresent())
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	protected String getNumberOfAdults(final List<PassengerTypeQuantityData> guestCounts)
	{
		final Optional<PassengerTypeQuantityData> adultCount = guestCounts.stream().filter(guestCount -> StringUtils
				.equalsIgnoreCase(guestCount.getPassengerType().getCode(), TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT))
				.findFirst();

		return adultCount.isPresent() ? Integer.toString(adultCount.get().getQuantity()) : "0";
	}

	protected String getDateOfStayFormatted(final StayDateRangeData stayDateRange)
	{
		final Date startDate = stayDateRange.getStartTime();
		final Date endDate = stayDateRange.getEndTime();

		final long numberOfDays = TravelDateUtils.getDaysBetweenDates(startDate, endDate);

		final StringBuilder dateOfStayFormatted = new StringBuilder();
		final SimpleDateFormat dateFormat = new SimpleDateFormat(TravelservicesConstants.DATE_PATTERN);
		int index = 0;
		// we take into consideration only the number of nights between start and end time
		for (Date date = stayDateRange.getStartTime(); date
				.before(stayDateRange.getEndTime()); date = TravelDateUtils.addDays(date, 1))
		{
			dateOfStayFormatted.append(dateFormat.format(date));
			if (index < numberOfDays)
			{
				dateOfStayFormatted.append(TravelfacadesConstants.MULTI_VALUE_FILTER_TERM_SEPARATOR);
			}
			index++;
		}

		return dateOfStayFormatted.toString();
	}

	@Override
	public AccommodationAvailabilityResponseData getAccommodationOfferingDetails(
			final AccommodationAvailabilityRequestData availabilityRequestData) throws ModelNotFoundException
	{
		return getAccommodationDetailsPipelineManager().executePipeline(availabilityRequestData);
	}

	@Override
	public AccommodationAvailabilityResponseData getSelectedAccommodationOfferingDetails(
			final AccommodationAvailabilityRequestData availabilityRequestData) throws ModelNotFoundException
	{
		return getSelectedAccommodationDetailsPipelineManager().executePipeline(availabilityRequestData);
	}

	/**
	 * @return accommodationOfferingSearchFacade
	 */
	protected AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> getAccommodationOfferingSearchFacade()
	{
		return accommodationOfferingSearchFacade;
	}

	/**
	 * @param accommodationOfferingSearchFacade
	 */
	@Required
	public void setAccommodationOfferingSearchFacade(
			final AccommodationOfferingSearchFacade<AccommodationOfferingDayRateData> accommodationOfferingSearchFacade)
	{
		this.accommodationOfferingSearchFacade = accommodationOfferingSearchFacade;
	}

	/**
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * @param accommodationOfferingService
	 *           the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

	/**
	 * @return the accommodationDetailsPipelineManager
	 */
	protected AccommodationDetailsPipelineManager getAccommodationDetailsPipelineManager()
	{
		return accommodationDetailsPipelineManager;
	}

	/**
	 * @param accommodationDetailsPipelineManager
	 *           the accommodationDetailsPipelineManager to set
	 */
	@Required
	public void setAccommodationDetailsPipelineManager(
			final AccommodationDetailsPipelineManager accommodationDetailsPipelineManager)
	{
		this.accommodationDetailsPipelineManager = accommodationDetailsPipelineManager;
	}

	/**
	 * @return the selectedAccommodationDetailsPipelineManager
	 */
	protected AccommodationDetailsPipelineManager getSelectedAccommodationDetailsPipelineManager()
	{
		return selectedAccommodationDetailsPipelineManager;
	}

	/**
	 * @param selectedAccommodationDetailsPipelineManager
	 *           the selectedAccommodationDetailsPipelineManager to set
	 */
	@Required
	public void setSelectedAccommodationDetailsPipelineManager(
			final AccommodationDetailsPipelineManager selectedAccommodationDetailsPipelineManager)
	{
		this.selectedAccommodationDetailsPipelineManager = selectedAccommodationDetailsPipelineManager;
	}

	/**
	 * @return the accommodationOfferingConverter
	 */
	protected AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> getAccommodationOfferingConverter()
	{
		return accommodationOfferingConverter;
	}

	/**
	 * @param accommodationOfferingConverter
	 *           the accommodationOfferingConverter to set
	 */
	@Required
	public void setAccommodationOfferingConverter(
			final AbstractPopulatingConverter<AccommodationOfferingModel, PropertyData> accommodationOfferingConverter)
	{
		this.accommodationOfferingConverter = accommodationOfferingConverter;
	}

}
