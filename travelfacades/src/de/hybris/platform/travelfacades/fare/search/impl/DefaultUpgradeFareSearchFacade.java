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

package de.hybris.platform.travelfacades.fare.search.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.SearchProcessingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportOfferingPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.enums.FareSelectionDisplayOrder;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.fare.search.UpgradeFareSearchFacade;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of UpgradeFareSearchFacade
 */
public class DefaultUpgradeFareSearchFacade implements UpgradeFareSearchFacade
{
	private ReservationFacade reservationFacade;
	private FareSearchPipelineManager upgradeBundleFareSearchPipelineManager;

	@Override
	public FareSelectionData doUpgradeSearch()
	{
		final ReservationData reservationData = getReservationFacade().getCurrentReservationData();
		final FareSearchRequestData fareSearchRequestData = createFareSearchRequestData(reservationData);
		return getUpgradeBundleFareSearchPipelineManager().executePipeline(Collections.emptyList(), fareSearchRequestData);
	}

	protected FareSearchRequestData createFareSearchRequestData(final ReservationData reservationData)
	{
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();

		final List<OriginDestinationInfoData> originDestinationInfoData = new ArrayList<>(2);


		// create Transport Offering PreferencesData

		final TransportOfferingPreferencesData transportOfferingPreferencesData = new TransportOfferingPreferencesData();
		transportOfferingPreferencesData.setTransportOfferingType(TransportOfferingType.DIRECT);

		// create Travel Preferences

		final TravelPreferencesData travelPreferences = new TravelPreferencesData();
		travelPreferences.setCabinPreference("M");
		travelPreferences.setTransportOfferingPreferences(transportOfferingPreferencesData);
		fareSearchRequestData
				.setTripType(CollectionUtils.size(reservationData.getReservationItems()) > 1 ? TripType.RETURN : TripType.SINGLE);
		fareSearchRequestData.setSalesApplication(SalesApplication.WEB);
		// populate passenger types

		fareSearchRequestData.setPassengerTypes(reservationData.getReservationItems().get(0).getReservationPricingInfo()
				.getItineraryPricingInfo().getPtcFareBreakdownDatas().stream().map(PTCFareBreakdownData::getPassengerTypeQuantity)
				.collect(Collectors.toList()));

		reservationData.getReservationItems().forEach(reservationItem -> {
			final OriginDestinationInfoData departureInfo = new OriginDestinationInfoData();
			departureInfo.setReferenceNumber(reservationItem.getOriginDestinationRefNumber());
			final List<TransportOfferingData> transportOfferingDatas = reservationItem.getReservationItinerary()
					.getOriginDestinationOptions().get(0).getTransportOfferings();
			departureInfo.setDepartureLocation(transportOfferingDatas.get(0).getSector().getOrigin().getCode());
			departureInfo.setArrivalLocation(
					transportOfferingDatas.get(transportOfferingDatas.size() - 1).getSector().getDestination().getCode());
			departureInfo.setDepartureTime(transportOfferingDatas.get(0).getDepartureTime());
			departureInfo.setArrivalTime(transportOfferingDatas.get(transportOfferingDatas.size() - 1).getArrivalTime());
			departureInfo.setItinerary(reservationItem.getReservationItinerary());

			originDestinationInfoData.add(departureInfo);

		});
		fareSearchRequestData.setTravelPreferences(travelPreferences);
		fareSearchRequestData.setOriginDestinationInfo(originDestinationInfoData);
		// set searchProcessingInfoData and displayOption
		final SearchProcessingInfoData searchProcessingInfoData = new SearchProcessingInfoData();
		searchProcessingInfoData.setDisplayOrder(FareSelectionDisplayOrder.DEPARTURE_TIME.toString());
		fareSearchRequestData.setSearchProcessingInfo(searchProcessingInfoData);
		return fareSearchRequestData;
	}

	/**
	 * @return the reservationFacade
	 */
	protected ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	/**
	 * @param reservationFacade
	 *           the reservationFacade to set
	 */
	@Required
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
	}

	/**
	 * @return the upgradeBundleFareSearchPipelineManager
	 */
	protected FareSearchPipelineManager getUpgradeBundleFareSearchPipelineManager()
	{
		return upgradeBundleFareSearchPipelineManager;
	}

	/**
	 * @param upgradeBundleFareSearchPipelineManager
	 *           the upgradeBundleFareSearchPipelineManager to set
	 */
	@Required
	public void setUpgradeBundleFareSearchPipelineManager(final FareSearchPipelineManager upgradeBundleFareSearchPipelineManager)
	{
		this.upgradeBundleFareSearchPipelineManager = upgradeBundleFareSearchPipelineManager;
	}
}
