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
 */
package de.hybris.platform.traveladdon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.tripfinder.TripFinderFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.enums.LocationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for Trip Finder page
 */
@Controller
@RequestMapping("/trip-finder")
public class TripFinderPageController extends AbstractSearchPageController
{
	private static final Logger LOG = Logger.getLogger(TripFinderPageController.class);
	private static final String TRIP_FINDER_CMS_PAGE = "tripFinderPage";

	@Resource(name = "transportFacilityFacade")
	private TransportFacilityFacade transportFacilityFacade;

	@Resource
	private TripFinderFacade tripFinderFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	/**
	 * @param model
	 * @param activity
	 * @param city
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getTripFinderPage(final Model model, @RequestParam(value = "activity") final String activity,
			@RequestParam(value = "city", required = false) final String city) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(TRIP_FINDER_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRIP_FINDER_CMS_PAGE));

		//We need to remove below attributes from session to show farefinder component on travel/hotel site once user click on "trip finder" link again after searching for trips.
		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_SELECTION_DATA);
		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES);
		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES);

		if (!validateFieldPattern(activity, TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_AND_UNDERSCORES))
		{
			return getViewForPage(model);
		}

		if (StringUtils.isNotBlank(city) && !validateFieldPattern(city,
				TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES))
		{
			return getViewForPage(model);
		}

		model.addAttribute("activity", activity);
		model.addAttribute("city", city);
		return getViewForPage(model);
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @return String
	 * @throws GeoLocatorException
	 * @throws MapServiceException
	 */
	@ResponseBody
	@RequestMapping(value = "/get-nearest-airport", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String searchByCurrentPosition(@RequestParam("activity") final String activity,
			@RequestParam("latitude") final double latitude, @RequestParam("longitude") final double longitude)
			throws GeoLocatorException, MapServiceException
	{
		TransportFacilityData transportFacilityData = null;
		try
		{
			final GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(latitude);
			geoPoint.setLongitude(longitude);

			transportFacilityData = transportFacilityFacade.findNearestTransportFacility(geoPoint, activity,
					createPageableData(0, 1, StringUtils.EMPTY, ShowMode.Page));
		}
		catch (final GeoLocatorException | MapServiceException ex)
		{
			LOG.warn("Error while finding nearest transport facility for latitude : " + latitude + " and longitude : " + longitude
					+ " , reason : " + ex.getMessage());
		}
		if (transportFacilityData == null)
		{
			return StringUtils.EMPTY;
		}
		return transportFacilityData.getCode();
	}

	/**
	 * @param model
	 * @param activity
	 * @param originLocationCode
	 * @param city
	 * @return List<TransportOfferingData>
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/get-destination-locations.json", method =
	{ RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getDestinationLocations(final Model model, @RequestParam(value = "activity") final String activity,
			@RequestParam(value = "origin", required = false) final String originLocationCode,
			@RequestParam(value = "originType", required = false) final String originType,
			@RequestParam(value = "city", required = false) final String city) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(TRIP_FINDER_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(TRIP_FINDER_CMS_PAGE));

		if (!validateFieldPattern(activity, TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_AND_UNDERSCORES))
		{
			return TraveladdonControllerConstants.Views.Pages.TripFinder.DestinationLocationJSONResponse;
		}

		if (StringUtils.isNotBlank(originLocationCode) && !validateFieldPattern(originLocationCode,
				TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES))
		{
			return TraveladdonControllerConstants.Views.Pages.TripFinder.DestinationLocationJSONResponse;
		}

		if (StringUtils.isNotBlank(city) && !validateFieldPattern(city,
				TravelacceleratorstorefrontValidationConstants.REGEX_LETTERS_DASHES_PARENTHESES_SPACES))
		{
			return TraveladdonControllerConstants.Views.Pages.TripFinder.DestinationLocationJSONResponse;
		}

		final List<TransportOfferingData> destinationTransportOfferings = tripFinderFacade
				.getDestinationTransportOfferings(originLocationCode, originType, activity);

		if (originLocationCode != null)
		{
			final Map.Entry<String, Map<String, String>> entry = tripFinderFacade.getOriginLocationSuggestion(originLocationCode,
					destinationTransportOfferings);

			if (StringUtils.equalsIgnoreCase(LocationType.CITY.getCode(), originType))
			{
				if (entry != null && StringUtils.isNotEmpty(entry.getKey()))
				{
					model.addAttribute("originLocationName", entry.getKey());
					model.addAttribute("originLocationCode", originLocationCode);
					model.addAttribute("originLocationSuggestionType", originType);

				}
			}
			else
			{
				if (entry != null && MapUtils.isNotEmpty(entry.getValue()) && entry.getValue().entrySet().iterator().hasNext())
				{
					final Map.Entry<String, String> mapEntry = entry.getValue().entrySet().iterator().next();
					model.addAttribute("originLocationName", mapEntry.getKey());
					model.addAttribute("originLocationCode", mapEntry.getValue());
					model.addAttribute("originLocationSuggestionType", originType);
				}
			}
		}
		model.addAttribute("destinationTransportOfferings", destinationTransportOfferings);
		final Map<String, String> locationMap = new HashMap<String, String>();
		for (final TransportOfferingData data : destinationTransportOfferings)
		{
			locationMap.put(data.getSector().getDestination().getCode(), data.getDestinationLocationCity());
		}
		model.addAttribute("locationMap", locationMap);
		model.addAttribute("city", city);
		return TraveladdonControllerConstants.Views.Pages.TripFinder.DestinationLocationJSONResponse;
	}

	protected Boolean validateFieldPattern(final String attribute, final String pattern)
	{
		if (!attribute.matches(pattern))
		{
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
