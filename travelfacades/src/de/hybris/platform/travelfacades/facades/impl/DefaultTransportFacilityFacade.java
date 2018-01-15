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
package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.travelservices.storefinder.TravelStoreFinderService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of TransportFacilityFacade
 */
public class DefaultTransportFacilityFacade implements TransportFacilityFacade
{

	private Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter;
	private TravelStoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> travelStoreFinderService;
	private BaseStoreService baseStoreService;
	private TransportFacilityService transportFacilityService;
	private Converter<LocationModel, LocationData> locationConverter;
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;

	@Override
	public TransportFacilityData findNearestTransportFacility(final GeoPoint geoPoint, final PageableData pageableData)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getTravelStoreFinderService()
				.positionSearch(currentBaseStore, geoPoint, pageableData);
		for (final PointOfServiceDistanceData pos : searchPageData.getResults())
		{
			final PointOfServiceModel pointOfService = pos.getPointOfService();
			if (pointOfService != null)
			{
				return getTransportFacilityConverter().convert(pointOfService.getTransportFacility());
			}
		}
		return null;
	}

	@Override
	public TransportFacilityData findNearestTransportFacility(final GeoPoint geoPoint, final String activity,
			final PageableData pageableData)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final List<TransportFacilityData> transportFacilityForActivity = getOriginTransportFacility(activity);

		final Collection<PointOfServiceModel> posModels = getTravelStoreFinderService().getPointOfService(currentBaseStore,
				transportFacilityForActivity);

		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = getTravelStoreFinderService()
				.positionSearch(currentBaseStore, geoPoint, pageableData, posModels);
		for (final PointOfServiceDistanceData pos : searchPageData.getResults())
		{
			final PointOfServiceModel pointOfService = pos.getPointOfService();
			if (pointOfService != null)
			{
				return getTransportFacilityConverter().convert(pointOfService.getTransportFacility());
			}
		}
		return null;
	}

	@Override
	public List<TransportFacilityData> getOriginTransportFacility(final String activity)
	{
		final SearchData searchData = createSearchData(activity);

		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(searchData);
		if (CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{
			final List<TransportOfferingData> transportOfferingDataForActivity = searchPageData.getResults();
			final List<TransportFacilityData> originTransportFacilityForActivity = new ArrayList<TransportFacilityData>(
					transportOfferingDataForActivity.size());
			transportOfferingDataForActivity.forEach(
					transportOfferingData -> originTransportFacilityForActivity.add(transportOfferingData.getSector().getOrigin()));
			return originTransportFacilityForActivity;
		}
		return Collections.emptyList();
	}

	/**
	 * Method to prepare search parameters to be used during activity search
	 *
	 * @param activity
	 * @return search data
	 */
	protected SearchData createSearchData(final String activity)
	{
		final SearchData searchData = new SearchData();
		searchData.setSearchType(TravelservicesConstants.SEARCH_KEY_ORIGIN_TRANSPORTFACILITY_CODE);

		final Map<String, String> filterTerms = new HashMap<String, String>();
		filterTerms.put(TravelservicesConstants.SOLR_FIELD_ACTIVITY, activity);
		searchData.setFilterTerms(filterTerms);
		return searchData;
	}

	@Override
	public LocationData getCountry(final String transportFacilityCode)
	{
		final TransportFacilityModel transportFacilityModel = getTransportFacilityService()
				.getTransportFacility(transportFacilityCode);
		final LocationModel locationModel = getTransportFacilityService().getCountry(transportFacilityModel);
		return (locationModel == null) ? null : getLocationConverter().convert(locationModel);
	}

	@Override
	public LocationData getLocation(final String transportFacilityCode)
	{
		final TransportFacilityModel transportFacilityModel = getTransportFacilityService()
				.getTransportFacility(transportFacilityCode);
		return (Objects.isNull(transportFacilityModel) || Objects.isNull(transportFacilityModel.getLocation())) ? null
				: getLocationConverter().convert(transportFacilityModel.getLocation());
	}

	@Override
	public TransportFacilityData getTransportFacility(final String transportFacilityCode)
	{
		final TransportFacilityModel transportFacilityModel = getTransportFacilityService()
				.getTransportFacility(transportFacilityCode);
		return transportFacilityModel == null ? null : getTransportFacilityConverter().convert(transportFacilityModel);
	}

	/**
	 * @return the transportFacilityConverter
	 */
	protected Converter<TransportFacilityModel, TransportFacilityData> getTransportFacilityConverter()
	{
		return transportFacilityConverter;
	}

	/**
	 * @param transportFacilityConverter
	 *           the transportFacilityConverter to set
	 */
	@Required
	public void setTransportFacilityConverter(
			final Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter)
	{
		this.transportFacilityConverter = transportFacilityConverter;
	}

	/**
	 * @return the travelStoreFinderService
	 */
	protected TravelStoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> getTravelStoreFinderService()
	{
		return travelStoreFinderService;
	}

	/**
	 * @param travelStoreFinderService
	 *           the travelStoreFinderService to set
	 */
	@Required
	public void setTravelStoreFinderService(
			final TravelStoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> travelStoreFinderService)
	{
		this.travelStoreFinderService = travelStoreFinderService;
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the transportFacilityService
	 */
	protected TransportFacilityService getTransportFacilityService()
	{
		return transportFacilityService;
	}

	/**
	 * @param transportFacilityService
	 *           the transportFacilityService to set
	 */
	@Required
	public void setTransportFacilityService(final TransportFacilityService transportFacilityService)
	{
		this.transportFacilityService = transportFacilityService;
	}

	/**
	 * @return the locationConverter
	 */
	protected Converter<LocationModel, LocationData> getLocationConverter()
	{
		return locationConverter;
	}

	/**
	 * @param locationConverter
	 *           the locationConverter to set
	 */
	@Required
	public void setLocationConverter(final Converter<LocationModel, LocationData> locationConverter)
	{
		this.locationConverter = locationConverter;
	}

	/**
	 * @return the transportOfferingSearchFacade
	 */
	protected TransportOfferingSearchFacade<TransportOfferingData> getTransportOfferingSearchFacade()
	{
		return transportOfferingSearchFacade;
	}

	/**
	 * @param transportOfferingSearchFacade
	 *           the transportOfferingSearchFacade to set
	 */
	@Required
	public void setTransportOfferingSearchFacade(
			final TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade)
	{
		this.transportOfferingSearchFacade = transportOfferingSearchFacade;
	}

}
