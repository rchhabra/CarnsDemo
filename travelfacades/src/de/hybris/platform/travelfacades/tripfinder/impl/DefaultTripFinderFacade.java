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
package de.hybris.platform.travelfacades.tripfinder.impl;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.fare.search.strategies.LocationCodesResolvingStrategy;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelfacades.strategies.SuggestionsDisplayStrategy;
import de.hybris.platform.travelfacades.tripfinder.TripFinderFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The Facade is used to delegate call to the TransportOfferingSearchFacade with the required object populated.
 */
public class DefaultTripFinderFacade implements TripFinderFacade
{
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;

	private SuggestionsDisplayStrategy suggestionsDisplayStrategy;

	private Map<LocationType, LocationCodesResolvingStrategy> locationCodesResolvingStrategyMap;
	private EnumerationService enumerationService;
	private ConfigurationService configurationService;

	@Override
	public List<TransportOfferingData> getDestinationLocations(final String activity, final String originLocation)
	{
		final SearchData searchData = populateSearchData(activity, originLocation);

		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(searchData);
		if (CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{
			return searchPageData.getResults();
		}
		return Collections.emptyList();
	}

	/**
	 * Method to prepare search parameters to be used during activity search
	 *
	 * @param activity
	 *           the activity
	 * @param originLocation
	 *           the origin location
	 * @return search data
	 */
	protected SearchData populateSearchData(final String activity, final String originLocation)
	{
		final SearchData searchData = new SearchData();
		searchData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_ACTIVITY);

		final Map<String, String> filterTerms = new HashMap<String, String>();
		filterTerms.put(TravelservicesConstants.SOLR_FIELD_ACTIVITY, activity);
		if (StringUtils.isNotEmpty(originLocation))
		{
			filterTerms.put(TravelservicesConstants.SEARCH_KEY_ORIGIN_TRANSPORTFACILITY_CODE, originLocation);
		}
		searchData.setFilterTerms(filterTerms);
		return searchData;
	}


	@Override
	public Map.Entry<String, Map<String, String>> getOriginLocationSuggestion(final String originLocationCode,
			final List<TransportOfferingData> transportOfferings)
	{
		final Map<String, Map<String, String>> originLocationsMap = getSuggestionsDisplayStrategy()
				.createStructuredView(SuggestionsDisplayStrategy.SuggestionType.ORIGIN, originLocationCode, transportOfferings);

		if (MapUtils.isNotEmpty(originLocationsMap) && originLocationsMap.entrySet().iterator().hasNext())
		{
			return originLocationsMap.entrySet().iterator().next();
		}
		return null;
	}

	@Override
	public Map<String, Map<String, String>> getDestinationLocationsSuggestion(final String originLocationCode,
			final List<TransportOfferingData> transportOfferings)
	{
		return getSuggestionsDisplayStrategy().createStructuredView(SuggestionsDisplayStrategy.SuggestionType.DESTINATION,
				originLocationCode, transportOfferings);
	}

	@Override
	public List<TransportOfferingData> getDestinationTransportOfferings(final String locationCode, final String locationType,
			final String activity)
	{
		final LocationType type = getEnumerationService().getEnumerationValue(LocationType.class, locationType);

		final LocationCodesResolvingStrategy locationCodesResolvingStrategy = Objects
				.nonNull(getLocationCodesResolvingStrategyMap().get(type)) ? getLocationCodesResolvingStrategyMap().get(type)
						: getLocationCodesResolvingStrategyMap().get(getEnumerationService().getEnumerationValue(LocationType.class,
								getConfigurationService().getConfiguration().getString(TravelfacadesConstants.DEFAULT_LOCATION_TYPE)));
		final List<String> transportOfferingCodes = locationCodesResolvingStrategy.getLocationCodes(locationCode);
		if (CollectionUtils.isEmpty(transportOfferingCodes))
		{
			return Collections.emptyList();
		}
		final List<TransportOfferingData> destinationTransportOfferings = new ArrayList<>();
		for (final String code : transportOfferingCodes)
		{
			destinationTransportOfferings.addAll(getDestinationLocations(activity, code));
		}
		return createDistinctDestinationsList(destinationTransportOfferings);

	}

	protected List<TransportOfferingData> createDistinctDestinationsList(final List<TransportOfferingData> transportOfferings)
	{
		final List<TransportOfferingData> distinctDestinationTransportOfferings = new ArrayList<>();
		transportOfferings.forEach(tOff -> {
			if (!distinctDestinationTransportOfferings.stream().map(TransportOfferingData::getDestinationLocationCity)
					.collect(Collectors.toList()).contains(tOff.getDestinationLocationCity()))
			{
				distinctDestinationTransportOfferings.add(tOff);
			}
		});
		return distinctDestinationTransportOfferings;
	}

	/**
	 * Gets transport offering search facade.
	 *
	 * @return the transportOfferingSearchFacade
	 */
	protected TransportOfferingSearchFacade<TransportOfferingData> getTransportOfferingSearchFacade()
	{
		return transportOfferingSearchFacade;
	}

	/**
	 * Sets transport offering search facade.
	 *
	 * @param transportOfferingSearchFacade
	 *           the transportOfferingSearchFacade to set
	 */
	@Required
	public void setTransportOfferingSearchFacade(
			final TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade)
	{
		this.transportOfferingSearchFacade = transportOfferingSearchFacade;
	}

	/**
	 * Gets suggestions display strategy.
	 *
	 * @return the suggestions display strategy
	 */
	protected SuggestionsDisplayStrategy getSuggestionsDisplayStrategy()
	{
		return suggestionsDisplayStrategy;
	}

	/**
	 * Sets suggestions display strategy.
	 *
	 * @param suggestionsDisplayStrategy
	 *           the suggestions display strategy
	 */
	@Required
	public void setSuggestionsDisplayStrategy(final SuggestionsDisplayStrategy suggestionsDisplayStrategy)
	{
		this.suggestionsDisplayStrategy = suggestionsDisplayStrategy;
	}

	/**
	 *
	 * @return the locationCodesResolvingStrategyMap
	 */
	protected Map<LocationType, LocationCodesResolvingStrategy> getLocationCodesResolvingStrategyMap()
	{
		return locationCodesResolvingStrategyMap;
	}

	/**
	 *
	 * @param locationCodesResolvingStrategyMap
	 *           the locationCodesResolvingStrategyMap to set
	 */
	@Required
	public void setLocationCodesResolvingStrategyMap(
			final Map<LocationType, LocationCodesResolvingStrategy> locationCodesResolvingStrategyMap)
	{
		this.locationCodesResolvingStrategyMap = locationCodesResolvingStrategyMap;
	}

	/**
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 *
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
