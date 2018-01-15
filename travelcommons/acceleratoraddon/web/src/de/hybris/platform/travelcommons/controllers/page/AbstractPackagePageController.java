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

package de.hybris.platform.travelcommons.controllers.page;

import de.hybris.platform.accommodationaddon.controllers.pages.AbstractAccommodationPageController;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.SearchProcessingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.enums.FareSelectionDisplayOrder;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.forms.AddBundleToCartForm;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.fare.sorting.strategies.AbstractResultSortingStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;


/**
 * Abstract controller for Package related pages
 */
public abstract class AbstractPackagePageController extends AbstractAccommodationPageController
{
	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "transportOfferingFacade")
	private TransportOfferingFacade transportOfferingFacade;

	@Resource(name = "fareSelectionSortingStrategyMap")
	private Map<FareSelectionDisplayOrder, AbstractResultSortingStrategy> fareSelectionSortingStrategyMap;

	protected void populateFareSearchResponseInModel(final FareSelectionData fareSearchResponse, final Model model)
	{
		if (fareSearchResponse != null)
		{
			final Optional<PricedItineraryData> outboundPricedItineraryOptional = fareSearchResponse.getPricedItineraries().stream()
					.filter(pricedItinerary -> TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER == pricedItinerary
							.getOriginDestinationRefNumber()).findAny();

			final Optional<PricedItineraryData> inboundPricedItineraryOptional = fareSearchResponse.getPricedItineraries().stream()
					.filter(pricedItinerary -> TravelfacadesConstants.INBOUND_REFERENCE_NUMBER == pricedItinerary
							.getOriginDestinationRefNumber()).findAny();

			final AddBundleToCartForm addBundleToCartForm = new AddBundleToCartForm();
			model.addAttribute(TraveladdonWebConstants.ADD_BUNDLE_TO_CART_FORM, addBundleToCartForm);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.ADD_BUNDLE_TO_CART_URL,
					TravelcommonsWebConstants.CHANGE_BUNDLE_URL);

			model.addAttribute(TraveladdonWebConstants.TRIP_TYPE,
					outboundPricedItineraryOptional.isPresent() && inboundPricedItineraryOptional.isPresent() ?
							TripType.RETURN.toString() :
							TripType.SINGLE.toString());

			model.addAttribute(TraveladdonWebConstants.PI_DATE_FORMAT, TraveladdonWebConstants.PRICED_ITINERARY_DATE_FORMAT);
			model.addAttribute(TraveladdonWebConstants.OUTBOUND_REF_NUMBER, TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER);
			model.addAttribute(TraveladdonWebConstants.INBOUND_REF_NUMBER, TravelfacadesConstants.INBOUND_REFERENCE_NUMBER);
			model.addAttribute(TraveladdonWebConstants.NO_OF_OUTBOUND_OPTIONS,
					countJourneyOptions(fareSearchResponse, TravelfacadesConstants.OUTBOUND_REFERENCE_NUMBER));
			model.addAttribute(TraveladdonWebConstants.NO_OF_INBOUND_OPTIONS,
					countJourneyOptions(fareSearchResponse, TravelfacadesConstants.INBOUND_REFERENCE_NUMBER));
			model.addAttribute(TraveladdonWebConstants.ECO_BUNDLE_TYPE, BundleType.ECONOMY);
			model.addAttribute(TraveladdonWebConstants.ECO_PLUS_BUNDLE_TYPE, BundleType.ECONOMY_PLUS);
			model.addAttribute(TraveladdonWebConstants.BUSINESS_BUNDLE_TYPE, BundleType.BUSINESS);

			if (outboundPricedItineraryOptional.isPresent())
			{
				final TravelRouteData route = outboundPricedItineraryOptional.get().getItinerary().getRoute();
				model.addAttribute(TraveladdonWebConstants.ORIGIN,
						route.getOrigin().getName() + " (" + route.getOrigin().getCode() + ")");
				model.addAttribute(TraveladdonWebConstants.DESTINATION,
						route.getDestination().getName() + " (" + route.getDestination().getCode() + ")");
			}
		}
	}

	protected int countJourneyOptions(final FareSelectionData fareSelectionData, final int referenceNumber)
	{
		return (int) fareSelectionData.getPricedItineraries().stream().filter(
				pricedItinerary -> pricedItinerary.getOriginDestinationRefNumber() == referenceNumber && pricedItinerary
						.isAvailable()).count();
	}

	/**
	 * Method handles the preparation of a FareSearchRequestData object using the FareFinderForm.
	 *
	 * @param fareFinderForm
	 * @return
	 */
	protected FareSearchRequestData prepareFareSearchRequestData(final FareFinderForm fareFinderForm,
			final HttpServletRequest request)
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();

		fareSearchRequestData.setTravelPreferences(createTravelPreferences(fareFinderForm));
		fareSearchRequestData.setPassengerTypes(fareFinderForm.getPassengerTypeQuantityList());
		fareSearchRequestData.setOriginDestinationInfo(createOriginDestinationInfos(fareFinderForm));
		fareSearchRequestData.setTripType(TripType.valueOf(fareFinderForm.getTripType()));
		fareSearchRequestData.setSearchProcessingInfo(createSearchProcessingInfo(request));
		fareSearchRequestData.setSalesApplication(SalesApplication.WEB);

		return fareSearchRequestData;

	}

	protected SearchProcessingInfoData createSearchProcessingInfo(final HttpServletRequest request)
	{
		final SearchProcessingInfoData searchProcessingInfoData = new SearchProcessingInfoData();
		final String displayOrder = request.getParameter(TraveladdonWebConstants.DISPLAY_ORDER);
		if (StringUtils.isNotBlank(displayOrder))
		{
			searchProcessingInfoData.setDisplayOrder(displayOrder);
		}
		else
		{
			searchProcessingInfoData.setDisplayOrder(FareSelectionDisplayOrder.DEPARTURE_TIME.toString());
		}
		return searchProcessingInfoData;
	}

	protected List<OriginDestinationInfoData> createOriginDestinationInfos(final FareFinderForm fareFinderForm)
	{
		final List<OriginDestinationInfoData> originDestinationInfoData = new ArrayList<>();

		final OriginDestinationInfoData departureInfo = new OriginDestinationInfoData();

		departureInfo.setDepartureLocation(fareFinderForm.getDepartureLocation());
		departureInfo.setDepartureLocationType(
				enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getDepartureLocationSuggestionType()));
		departureInfo.setArrivalLocation(fareFinderForm.getArrivalLocation());
		departureInfo.setArrivalLocationType(
				enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getArrivalLocationSuggestionType()));
		departureInfo.setDepartureTime(
				TravelDateUtils.convertStringDateToDate(fareFinderForm.getDepartingDateTime(), TravelservicesConstants
						.DATE_PATTERN));
		departureInfo.setReferenceNumber(0);

		originDestinationInfoData.add(departureInfo);

		if (StringUtils.equalsIgnoreCase(TripType.RETURN.name(), fareFinderForm.getTripType()))
		{
			final OriginDestinationInfoData returnInfo = new OriginDestinationInfoData();

			returnInfo.setDepartureLocation(fareFinderForm.getArrivalLocation());
			returnInfo.setDepartureLocationType(
					enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getArrivalLocationSuggestionType()));
			returnInfo.setArrivalLocation(fareFinderForm.getDepartureLocation());
			returnInfo.setArrivalLocationType(
					enumerationService.getEnumerationValue(LocationType.class, fareFinderForm.getDepartureLocationSuggestionType()));
			returnInfo.setDepartureTime(
					TravelDateUtils.convertStringDateToDate(fareFinderForm.getReturnDateTime(), TravelservicesConstants
							.DATE_PATTERN));
			returnInfo.setReferenceNumber(1);
			originDestinationInfoData.add(returnInfo);
		}
		return originDestinationInfoData;
	}

	protected TravelPreferencesData createTravelPreferences(final FareFinderForm fareFinderForm)
	{
		final TravelPreferencesData travelPreferences = new TravelPreferencesData();
		travelPreferences.setCabinPreference(fareFinderForm.getCabinClass());
		travelPreferences.setTransportOfferingPreferences(createTransportOfferingPreferences());
		return travelPreferences;
	}

	protected TransportOfferingPreferencesData createTransportOfferingPreferences()
	{
		final TransportOfferingPreferencesData transportOfferingPreferencesData = new TransportOfferingPreferencesData();
		transportOfferingPreferencesData.setTransportOfferingType(TransportOfferingType.DIRECT);
		return transportOfferingPreferencesData;
	}

	/**
	 * Creates the passenger type quantity data.
	 *
	 * @param numberOfRoomsString
	 * 		the accommodation finder form
	 * @param roomStayCandidates
	 * 		roomStayCandidates
	 * @return the list
	 */
	protected List<PassengerTypeQuantityData> createPassengerTypeQuantityData(final String numberOfRoomsString,
			final List<RoomStayCandidateData> roomStayCandidates) throws NumberFormatException
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final int numberOfRooms = Integer.parseInt(numberOfRoomsString);
		if (CollectionUtils.size(roomStayCandidates) < numberOfRooms)
		{
			return passengerTypeQuantityList;
		}

		for (int i = 0; i < numberOfRooms; i++)
		{
			final List<PassengerTypeQuantityData> guestCounts = roomStayCandidates.get(i).getPassengerTypeQuantityList();
			if (CollectionUtils.isEmpty(guestCounts))
			{
				continue;
			}

			for (int j = 0; j < guestCounts.size(); j++)
			{
				final PassengerTypeQuantityData gc = guestCounts.get(j);
				if (CollectionUtils.isEmpty(passengerTypeQuantityList))
				{
					final PassengerTypeQuantityData ptqd = clonePassengerTypeQuantityData(gc);

					passengerTypeQuantityList.add(ptqd);
				}
				else
				{
					final Optional<PassengerTypeQuantityData> passengerTypeQuantityData = passengerTypeQuantityList.stream()
							.filter(pt -> StringUtils.equals(pt.getPassengerType().getCode(), gc.getPassengerType().getCode()))
							.findFirst();
					if (passengerTypeQuantityData.isPresent())
					{
						passengerTypeQuantityData.get().setQuantity(passengerTypeQuantityData.get().getQuantity() + gc.getQuantity());
					}
					else
					{
						final PassengerTypeQuantityData ptqd = clonePassengerTypeQuantityData(gc);

						passengerTypeQuantityList.add(ptqd);
					}
				}
			}
		}
		return passengerTypeQuantityList;
	}

	/**
	 * Clone passenger type quantity data.
	 *
	 * @param gc
	 * 		the gc
	 * @return the passenger type quantity data
	 */
	protected PassengerTypeQuantityData clonePassengerTypeQuantityData(final PassengerTypeQuantityData gc)
	{
		final PassengerTypeQuantityData ptqd = new PassengerTypeQuantityData();
		ptqd.setAge(gc.getAge());
		ptqd.setQuantity(gc.getQuantity());

		final PassengerTypeData ptd = new PassengerTypeData();
		ptd.setCode(gc.getPassengerType().getCode());
		ptd.setIdentifier(gc.getPassengerType().getIdentifier());
		ptd.setName(gc.getPassengerType().getName());
		ptd.setPassengerType(gc.getPassengerType().getCode());
		ptd.setMinAge(gc.getPassengerType().getMinAge());
		ptd.setMaxAge(gc.getPassengerType().getMaxAge());

		ptqd.setPassengerType(ptd);
		return ptqd;
	}

	/**
	 * Gets the location name.
	 *
	 * @param locationCode
	 * 		the location code
	 * @param locationType
	 * 		the location type
	 * @return the location name
	 */
	protected String getLocationName(final String locationCode, final String locationType)
	{
		String locationName = StringUtils.EMPTY;
		final Map<GlobalSuggestionData, List<GlobalSuggestionData>> suggestionResults = transportOfferingFacade
				.getOriginSuggestionData(locationCode);

		GlobalSuggestionData firstSuggestion = null;
		if (MapUtils.isNotEmpty(suggestionResults))
		{
			firstSuggestion = suggestionResults.keySet().iterator().next();
		}
		if (StringUtils.isNotBlank(locationType) && Objects.nonNull(firstSuggestion))
		{
			if (StringUtils.equalsIgnoreCase(SuggestionType.AIRPORTGROUP.toString(), locationType))
			{
				locationName = suggestionResults.get(firstSuggestion).stream().findFirst().get().getName();
			}
			else if (StringUtils.equalsIgnoreCase(SuggestionType.CITY.toString(), locationType))
			{
				locationName = firstSuggestion.getName();
			}
		}
		return locationName;
	}

	/**
	 * Method to sort the FareSelectionData based on the displayOrder. If displayOrder is null, empty or not a valid
	 * FareSelectionDisplayOrder enum, the default sorting by departureDate is applied.
	 *
	 * @param fareSelectionData
	 *           as the FareSelectionData to be sorted
	 * @param displayOrder
	 *           as the String corresponding to a sortingStrategy
	 */
	protected void sortFareSelectionData(final FareSelectionData fareSelectionData, final String displayOrder)
	{
		final FareSelectionDisplayOrder displayOrderOption = Arrays.asList(FareSelectionDisplayOrder.values()).stream()
				.filter(val -> val.toString().equals(displayOrder)).findAny().orElse(FareSelectionDisplayOrder.DEPARTURE_TIME);
		final AbstractResultSortingStrategy sortingStrategy = fareSelectionSortingStrategyMap.get(displayOrderOption);
		sortingStrategy.sortFareSelectionData(fareSelectionData);
	}

}
