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

import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingOption;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.search.strategies.LocationCodesResolvingStrategy;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelfacades.strategies.SuggestionsDisplayStrategy;
import de.hybris.platform.travelfacades.strategies.TransportSuggestionsDisplayStrategy;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.facades.TransportOfferingFacade} interface.
 */
public class DefaultTransportOfferingFacade implements TransportOfferingFacade
{
	private TransportOfferingService transportOfferingService;
	private Converter<TravelRouteModel, TravelRouteData> travelRouteConverter;
	private Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter;
	private ConfigurablePopulator<TransportOfferingModel, TransportOfferingData, TransportOfferingOption> transportOfferingConfiguredPopulator;
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;
	private TravelRouteService travelRouteService;
	private SuggestionsDisplayStrategy suggestionsDisplayStrategy;
	private TransportSuggestionsDisplayStrategy transportSuggestionsDisplayStrategy;
	private TimeService timeService;
	private ConfigurationService configurationService;
	private TravelRulesService travelRulesService;
	private Map<LocationType, LocationCodesResolvingStrategy> locationCodesResolvingStrategyMap;
	private EnumerationService enumerationService;

	@Override
	public List<TransportOfferingData> getTransportOfferings(final String number, final Date departureDate,
			final List<TransportOfferingOption> options)
	{

		final List<TransportOfferingModel> transportOfferingModelList = getTransportOfferingService().getTransportOfferings(number,
				departureDate);
		return getTransportOfferings(options, transportOfferingModelList);
	}

	@Override
	public List<TransportOfferingData> getTransportOfferings(final List<TransportOfferingOption> options,
			final List<TransportOfferingModel> transportOfferingModelList)
	{
		final List<TransportOfferingData> transportOfferingDataList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(transportOfferingModelList))
		{
			for (final TransportOfferingModel model : transportOfferingModelList)
			{
				final TransportOfferingData data = getTransportOfferingConverter().convert(model);
				if (CollectionUtils.isNotEmpty(options))
				{
					getTransportOfferingConfiguredPopulator().populate(model, data, options);
				}
				transportOfferingDataList.add(data);
			}
		}
		return transportOfferingDataList;
	}

	@Override
	public List<ScheduledRouteData> getScheduledRoutes(final FareSearchRequestData fareSearchRequestData)
	{
		final List<ScheduledRouteData> scheduledRoutes = new LinkedList<>();

		fareSearchRequestData.getOriginDestinationInfo().forEach(originDestinationInfo -> {
			final List<TravelRouteData> travelRoutes = getTravelRoutes(originDestinationInfo);
			travelRoutes.forEach(travelRoute -> {
				final List<TravelSectorData> travelSectors = travelRoute.getSectors();
				if (travelSectors != null)
				{
					final Queue<List<TransportOfferingData>> transportOfferingConnections = buildTransportOfferingConnections(
							originDestinationInfo, travelSectors, fareSearchRequestData);
					buildScheduledRoutes(transportOfferingConnections, travelRoute, scheduledRoutes,
							originDestinationInfo.getReferenceNumber());
				}
			});
		});

		return scheduledRoutes;
	}

	protected Queue<List<TransportOfferingData>> buildTransportOfferingConnections(
			final OriginDestinationInfoData originDestinationInfo, final List<TravelSectorData> travelSectors,
			final FareSearchRequestData fareSearchRequestData)
	{
		boolean isFirstSector = true;
		final Queue<List<TransportOfferingData>> transportOfferingConnections = getNewPriorityQueue();
		for (final TravelSectorData travelSector : travelSectors)
		{
			// call to Solr to get a list of TransportOfferingData for the travel sector for the current departure date
			final List<TransportOfferingData> transportOfferings = searchTransportOfferings(travelSector,
					originDestinationInfo.getDepartureTime(), fareSearchRequestData);
			if (isFirstSector)
			{
				buildInitialTransportOfferings(transportOfferingConnections, transportOfferings);
				isFirstSector = false;
			}
			else
			{
				// check if the a TransportOffering has overnight connections
				if (checkForOverNightTransportOfferingConnections(transportOfferingConnections))
				{
					final Date nextDate = TravelDateUtils.addDays(originDestinationInfo.getDepartureTime(), 1);
					transportOfferings.addAll(searchTransportOfferings(travelSector, nextDate, fareSearchRequestData));
				}
				final Queue<List<TransportOfferingData>> updatedTransportOfferingConnections = buildTransportOfferingConnections(
						transportOfferingConnections, transportOfferings);
				transportOfferingConnections.addAll(updatedTransportOfferingConnections);
				// if no connected transport offerings are available on the subsequent sector, then the transport offerings from the previous sectors cannot be provided.
				if (transportOfferingConnections.isEmpty())
				{
					break;
				}
			}
		}
		return transportOfferingConnections;
	}

	/**
	 * Method to check if the a TransportOffering has an overnight connection. The method will check if in the current
	 * Queue of transport offering connections there at least one TransportOfferingData with arrival time + arrival date
	 * threshold falling in the day after.
	 *
	 * @param transportOfferingConnections
	 *           current queue of Transport Offering connections
	 * @return a boolean flag, true if check for flight overnight connections is true, false otherwise
	 */
	protected boolean checkForOverNightTransportOfferingConnections(
			final Queue<List<TransportOfferingData>> transportOfferingConnections)
	{

		return transportOfferingConnections.stream().anyMatch(toc -> {

			// get the last transport offering
			final TransportOfferingData transportOffering = toc.get(toc.size() - 1);

			final Date departureDate = TravelDateUtils.addHours(transportOffering.getArrivalTime(),
					TravelfacadesConstants.ARRIVAL_DATE_TO_THRESHOLD);

			if (!TravelDateUtils.isSameDate(transportOffering.getDepartureTime(), departureDate))
			{
				return true;
			}

			return false;

		});
	}

	/**
	 * Method returns List of TravelRouteData that contains the origin destination sent in the request.
	 *
	 * @param originDestinationInfo
	 * @return List of TravelRouteData
	 */
	protected List<TravelRouteData> getTravelRoutes(final OriginDestinationInfoData originDestinationInfo)
	{
		final List<TravelRouteModel> travelRouteModels = new ArrayList<>();
		final List<String> originLocationCodes = getLocationCodesResolvingStrategy(originDestinationInfo.getDepartureLocationType())
				.getLocationCodes(originDestinationInfo.getDepartureLocation());
		final List<String> destinationLocationCodes = getLocationCodesResolvingStrategy(
				originDestinationInfo.getArrivalLocationType()).getLocationCodes(originDestinationInfo.getArrivalLocation());
		for (final String originCode : originLocationCodes)
		{
			for (final String destinationCode : destinationLocationCodes)
			{
				travelRouteModels.addAll(getTravelRouteService().getTravelRoutes(originCode, destinationCode));
			}
		}
		return Converters.convertAll(travelRouteModels, getTravelRouteConverter());
	}

	/**
	 * Method returning a LocationCodesResolvingStrategy according to the LocatiionType passed as an argument
	 *
	 * @param locationType
	 * @return
	 */
	protected LocationCodesResolvingStrategy getLocationCodesResolvingStrategy(final LocationType locationType)
	{
		return Objects.nonNull(getLocationCodesResolvingStrategyMap().get(locationType))
				? getLocationCodesResolvingStrategyMap().get(locationType)
				: getLocationCodesResolvingStrategyMap().get(getEnumerationService().getEnumerationValue(LocationType.class,
						getConfigurationService().getConfiguration().getString(TravelfacadesConstants.DEFAULT_LOCATION_TYPE)));
	}

	/**
	 * Method returns a new PriorityQueue<List<TransportOfferingData>> object with a comparator
	 *
	 * @return Queue<List<TransportOfferingData>>
	 */
	protected Queue<List<TransportOfferingData>> getNewPriorityQueue()
	{
		return new PriorityQueue<>(Comparator.comparing(
				to -> TravelDateUtils.getUtcZonedDateTime(to.get(0).getDepartureTime(), to.get(0).getDepartureTimeZoneId())));
	}

	/**
	 * Method handles the preparation of routes in case of multi-sector i.e. connected transport offerings. The transport
	 * offering on the last sector is compared with the date-time of the transport offering in the next sector, to
	 * prepare feasible routes containing transport offering options. If the date-time of connected transport offerings
	 * are within the configured limits, then such transport offerings are added as available combinations.
	 *
	 * @param routeCombinations
	 *           the queue containing a list of {@link TransportOfferingData}
	 * @param transportOfferings
	 *           list of {@link TransportOfferingData}
	 *
	 * @return
	 */
	protected Queue<List<TransportOfferingData>> buildTransportOfferingConnections(
			final Queue<List<TransportOfferingData>> routeCombinations, final List<TransportOfferingData> transportOfferings)
	{

		final Queue<List<TransportOfferingData>> validTransportOfferingConnectionsQueue = getNewPriorityQueue();

		// loop through the queue routeCombinations
		while (!routeCombinations.isEmpty())
		{

			final List<TransportOfferingData> currentTransportOfferings = routeCombinations.remove();

			// The transport offering of the last sector in the route is selected to compare with the transport offering of the subsequent sector
			final TransportOfferingData lastTransportOffering = currentTransportOfferings.get(currentTransportOfferings.size() - 1);

			// loop through the TransportOffering of the current sector and add valid transport offerings from the next sector
			transportOfferings.forEach(transportOffering -> {

				if (isTransportOfferingConnectionFeasible(lastTransportOffering.getArrivalTime(),
						transportOffering.getDepartureTime()))
				{

					final List<TransportOfferingData> validTransportOfferingConnections = new LinkedList<>();
					validTransportOfferingConnections.addAll(currentTransportOfferings);
					validTransportOfferingConnections.add(transportOffering);

					validTransportOfferingConnectionsQueue.add(validTransportOfferingConnections);
				}
			});
		}

		return validTransportOfferingConnectionsQueue;
	}

	/**
	 * Method creates individual lists for each {@link TransportOfferingData} and before adding the list to the Queue.
	 *
	 * @param routeCombinations
	 *           the queue containing a list of {@link TransportOfferingData} that will contain the list of
	 *           {@link TransportOfferingData}
	 * @param transportOfferings
	 *           list of {@link TransportOfferingData} which will then be converted to individual list items containing a
	 *           single {@link TransportOfferingData} element
	 */
	protected void buildInitialTransportOfferings(final Queue<List<TransportOfferingData>> routeCombinations,
			final List<TransportOfferingData> transportOfferings)
	{

		transportOfferings.forEach(to -> {

			final List<TransportOfferingData> currentSector = new LinkedList<>();
			currentSector.add(to);

			routeCombinations.add(currentSector);

		});
	}

	/**
	 * Method takes arrivalDate and departureDate {@link Date} objects and run the following check:
	 *
	 * departureDate is AFTER (arrivalDate + x hours) && departureDate is BEFORE (arrivalDate + x hours)
	 *
	 * @param arrivalDate
	 *           the Transport Offering arrival date which will have a threshold applied
	 * @param departureDate
	 *           the Transport Offering departure date which will not have a threshold applied
	 * @return
	 */
	protected boolean isTransportOfferingConnectionFeasible(final Date arrivalDate, final Date departureDate)
	{
		final Date from = TravelDateUtils.addHours(arrivalDate, TravelfacadesConstants.ARRIVAL_DATE_FROM_THRESHOLD);
		final Date to = TravelDateUtils.addHours(arrivalDate, TravelfacadesConstants.ARRIVAL_DATE_TO_THRESHOLD);

		return (departureDate.equals(from) || departureDate.after(from)) && (departureDate.before(to) || departureDate.equals(to));
	}

	/**
	 * Method takes the queue which contains a list of {@link TransportOfferingData} and creates a
	 * {@link ScheduledRouteData} for each element before adding to the {@link ScheduledRouteData} list.
	 *
	 * @param transportOfferingConnections
	 *           the queue containing a list of {@link TransportOfferingData} elements
	 * @param travelRoute
	 *           the TravelRouteData which is to be added to the {@link ScheduledRouteData}
	 * @param scheduledRoutes
	 *           the {@link ScheduledRouteData} list where each {@link ScheduledRouteData} object will be added too.
	 * @param referenceNumber
	 *           the referenceNumber for this route which will be added to the {@link ScheduledRouteData} object
	 */
	protected void buildScheduledRoutes(final Queue<List<TransportOfferingData>> transportOfferingConnections,
			final TravelRouteData travelRoute, final List<ScheduledRouteData> scheduledRoutes, final int referenceNumber)
	{

		while (!transportOfferingConnections.isEmpty())
		{
			if (!isConnectionValid(transportOfferingConnections.iterator().next()))
			{
				transportOfferingConnections.remove();
				continue;
			}

			final ScheduledRouteData scheduledRoute = new ScheduledRouteData();
			scheduledRoute.setReferenceNumber(referenceNumber);
			scheduledRoute.setRoute(travelRoute);
			scheduledRoute.setTransportOfferings(transportOfferingConnections.remove());

			scheduledRoutes.add(scheduledRoute);
		}
	}

	/**
	 * Checks if the first flight in the connection is in future and if it is enough in future to be booked based on
	 * configurable attribute
	 *
	 * @param transportOfferings
	 * @return true if transport offerings can be booked
	 */
	protected boolean isConnectionValid(final List<TransportOfferingData> transportOfferings)
	{
		if (CollectionUtils.isEmpty(transportOfferings))
		{
			return false;
		}

		final ZonedDateTime firstOfferingDepartureUtc = TravelDateUtils.getUtcZonedDateTime(
				transportOfferings.get(0).getDepartureTime(), transportOfferings.get(0).getDepartureTimeZoneId());
		final ZonedDateTime currentUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
				ZoneId.systemDefault());
		if (currentUtcTime.isAfter(firstOfferingDepartureUtc))
		{
			return false;
		}

		final int bookingWindow = TravelfacadesConstants.MIN_BOOKING_ADVANCE_TIME;
		final ZonedDateTime minBookingUtcTime = currentUtcTime.plusHours(bookingWindow);
		if (minBookingUtcTime.isAfter(firstOfferingDepartureUtc))
		{
			return false;
		}
		return true;
	}

	/**
	 * Method which makes a call to Solr and returns a list of {@link TransportOfferingData} based on the
	 * {@link TravelSectorData} Origin and Destination codes and {@link OriginDestinationInfo} departureDate.
	 *
	 * @param travelSector
	 *           the {@link TravelSectorData} which will be used in the solr query
	 * @param departureDate
	 *           the {@link Date} which will be used in the solr query
	 *
	 * @return List<TransportOfferingData>
	 */
	protected List<TransportOfferingData> searchTransportOfferings(final TravelSectorData travelSector, final Date departureDate,
			final FareSearchRequestData fareSearchRequestData)
	{
		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(createSearchData(travelSector, departureDate));
		final List<TransportOfferingData> transportOfferings = searchPageData.getResults();
		if (CollectionUtils.isNotEmpty(transportOfferings))
		{
			// Do drools filtering
			if (isFilteringEnabledForTransportOffering())
			{
				getTravelRulesService().filterTransportOfferings(transportOfferings, fareSearchRequestData);
			}
			return transportOfferings;
		}
		return Collections.emptyList();
	}

	private boolean isFilteringEnabledForTransportOffering()
	{
		return getConfigurationService().getConfiguration().getBoolean(TravelfacadesConstants.FILTER_TRANSPORTOFFERING_ENABLED);
	}

	protected SearchData createSearchData(final TravelSectorData travelSector, final Date departureDate)
	{
		final SearchData searchData = new SearchData();
		searchData.setSearchType(TravelservicesConstants.SEARCH_TYPE_TRANSPORT_OFFERING);

		final Map<String, String> filterTerms = new HashMap<>();
		filterTerms.put(TravelservicesConstants.SEARCH_KEY_ORIGIN_TRANSPORTFACILITY_CODE, travelSector.getOrigin().getCode());
		filterTerms.put(TravelservicesConstants.SEARCH_KEY_DESTINATION_TRANSPORTFACILITY_CODE,
				travelSector.getDestination().getCode());
		filterTerms.put(TravelservicesConstants.SEARCH_KEY_DEPARTURE_DATE,
				TravelDateUtils.convertDateToStringDate(departureDate, TravelservicesConstants.DATE_PATTERN));

		searchData.setFilterTerms(filterTerms);

		return searchData;
	}

	@Override
	public TransportOfferingData getTransportOffering(final String code)
	{
		return getTransportOfferingConverter().convert(getTransportOfferingService().getTransportOffering(code));
	}

	@Override
	public boolean isMultiSectorRoute(final List<String> transportOfferings)
	{
		if (transportOfferings.size() > 1)
		{
			return Boolean.TRUE;
		}
		else
		{
			return Boolean.FALSE;
		}
	}

	@Override
	public Map<String, Map<String, String>> getOriginSuggestions(final String text)
	{
		final SearchData searchData = populateSuggestionsSearchData(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN,
				text, null);
		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(searchData);

		if (CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{
			return getSuggestionsDisplayStrategy().createStructuredView(SuggestionsDisplayStrategy.SuggestionType.ORIGIN, text,
					searchPageData.getResults());
		}
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Map<String, String>> getDestinationSuggestions(final String originLocation, final String text)
	{
		final SearchData searchData = populateSuggestionsSearchData(
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION, text, originLocation);
		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(searchData);

		if (CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{
			return getSuggestionsDisplayStrategy().createStructuredView(SuggestionsDisplayStrategy.SuggestionType.DESTINATION, text,
					searchPageData.getResults());
		}
		return Collections.emptyMap();
	}

	/**
	 * Method to populate the search criteria for solr suggestions search
	 *
	 * @param text
	 * @return
	 */
	protected SearchData populateSuggestionsSearchData(final String searchType, final String text, final String originLocation)
	{
		final SearchData searchData = new SearchData();

		final Map<String, String> filterTerms = new HashMap<String, String>();
		if (TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN.equalsIgnoreCase(searchType))
		{
			searchData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN);
			filterTerms.put(TravelservicesConstants.SOLR_FIELD_ORIGIN_LOCATION_DATA, text);
		}
		else if (TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION.equalsIgnoreCase(searchType))
		{
			searchData.setSearchType(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION);
			filterTerms.put(TravelservicesConstants.SOLR_FIELD_DESTINATION_LOCATION_DATA, text);
			if (StringUtils.isNotBlank(originLocation))
			{
				filterTerms.put(TravelservicesConstants.SOLR_FIELD_ALL_ORIGINS_TO_DESTINATION, originLocation);
			}
		}
		searchData.setFilterTerms(filterTerms);

		return searchData;
	}


	@Override
	public Map<GlobalSuggestionData, List<GlobalSuggestionData>> getOriginSuggestionData(final String text)
	{
		if (StringUtils.isEmpty(text))
		{
			return Collections.emptyMap();
		}

		final SearchData searchData = populateSuggestionsSearchData(TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_ORIGIN,
				text, null);
		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(searchData);

		if (CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{
			return getTransportSuggestionsDisplayStrategy().createSuggestionsMap(text, searchPageData.getResults(),
					TransportSuggestionsDisplayStrategy.LegSuggestionType.ORIGIN);
		}
		return Collections.emptyMap();
	}

	@Override
	public Map<GlobalSuggestionData, List<GlobalSuggestionData>> getDestinationSuggestionData(final String originLocation,
			final String text)
	{
		final SearchData searchData = populateSuggestionsSearchData(
				TravelservicesConstants.SOLR_SEARCH_TYPE_SUGGESTIONS_DESTINATION, text, originLocation);
		final TransportOfferingSearchPageData<SearchData, TransportOfferingData> searchPageData = getTransportOfferingSearchFacade()
				.transportOfferingSearch(searchData);

		if (CollectionUtils.isNotEmpty(searchPageData.getResults()))
		{
			return getTransportSuggestionsDisplayStrategy().createSuggestionsMap(text, searchPageData.getResults(),
					TransportSuggestionsDisplayStrategy.LegSuggestionType.DESTINATION);
		}
		return Collections.emptyMap();
	}

	/**
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 *           the transportOfferingService to set
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * @return the travelRouteConverter
	 */
	protected Converter<TravelRouteModel, TravelRouteData> getTravelRouteConverter()
	{
		return travelRouteConverter;
	}

	/**
	 * @param travelRouteConverter
	 *           the travelRouteConverter to set
	 */
	@Required
	public void setTravelRouteConverter(final Converter<TravelRouteModel, TravelRouteData> travelRouteConverter)
	{
		this.travelRouteConverter = travelRouteConverter;
	}

	/**
	 * @return the transportOfferingConverter
	 */
	protected Converter<TransportOfferingModel, TransportOfferingData> getTransportOfferingConverter()
	{
		return transportOfferingConverter;
	}

	/**
	 * @param transportOfferingConverter
	 *           the transportOfferingConverter to set
	 */
	@Required
	public void setTransportOfferingConverter(
			final Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter)
	{
		this.transportOfferingConverter = transportOfferingConverter;
	}

	/**
	 * @return the transportOfferingConfiguredPopulator
	 */
	protected ConfigurablePopulator<TransportOfferingModel, TransportOfferingData, TransportOfferingOption> getTransportOfferingConfiguredPopulator()
	{
		return transportOfferingConfiguredPopulator;
	}

	/**
	 * @param transportOfferingConfiguredPopulator
	 *           the transportOfferingConfiguredPopulator to set
	 */
	@Required
	public void setTransportOfferingConfiguredPopulator(
			final ConfigurablePopulator<TransportOfferingModel, TransportOfferingData, TransportOfferingOption> transportOfferingConfiguredPopulator)
	{
		this.transportOfferingConfiguredPopulator = transportOfferingConfiguredPopulator;
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

	/**
	 * @return the travelRouteService
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * @param travelRouteService
	 *           the travelRouteService to set
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}

	/**
	 * @return the suggestionsDisplayStrategy
	 */
	protected SuggestionsDisplayStrategy getSuggestionsDisplayStrategy()
	{
		return suggestionsDisplayStrategy;
	}

	/**
	 * @param suggestionsDisplayStrategy
	 *           the suggestionsDisplayStrategy to set
	 */
	@Required
	public void setSuggestionsDisplayStrategy(final SuggestionsDisplayStrategy suggestionsDisplayStrategy)
	{
		this.suggestionsDisplayStrategy = suggestionsDisplayStrategy;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
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
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets travel rules service.
	 *
	 * @return the travel rules service
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * Sets travel rules service.
	 *
	 * @param travelRulesService
	 *           the travel rules service
	 */
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

	/**
	 *
	 * @return the transportSuggestionsDisplayStrategy
	 */
	protected TransportSuggestionsDisplayStrategy getTransportSuggestionsDisplayStrategy()
	{
		return transportSuggestionsDisplayStrategy;
	}

	/**
	 *
	 * @param transportSuggestionsDisplayStrategy
	 *           the transportSuggestionsDisplayStrategy to set
	 */
	@Required
	public void setTransportSuggestionsDisplayStrategy(
			final TransportSuggestionsDisplayStrategy transportSuggestionsDisplayStrategy)
	{
		this.transportSuggestionsDisplayStrategy = transportSuggestionsDisplayStrategy;
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
	 *           the locationCodesResolvingStrategyMap
	 */
	@Required
	public void setLocationCodesResolvingStrategyMap(
			final Map<LocationType, LocationCodesResolvingStrategy> locationCodesResolvingStrategyMap)
	{
		this.locationCodesResolvingStrategyMap = locationCodesResolvingStrategyMap;
	}

	/**
	 *
	 * @return enumerationService
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




}
