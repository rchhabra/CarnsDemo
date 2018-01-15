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

package de.hybris.platform.ndcfacades.offers.handlers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.seatmap.data.CabinData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatFeatureData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers.TravelAccommodationMapHandler;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.ProximityItemModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler to populate seat map options for NDC {@link SeatAvailabilityRQ} for a transport vehicle retrieved from DB
 * based on mapping between the vehicle config and configured accommodations
 */
public class NDCTravelAccommodationMapHandler extends TravelAccommodationMapHandler
{

	@Override
	protected void createSeat(final OfferRequestData offerRequestData, final List<SeatInfoData> seatInfo, final int colNum,
			final ConfiguredAccommodationModel seat, final CabinData cabinData, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData, final List<SelectedAccommodationModel> selectedAccommodations,
			final Map<ProductModel, Long> productStockMap)
	{

		final SeatInfoData seatInfoData = new SeatInfoData();
		seatInfo.add(seatInfoData);
		seatInfoData.setColumnNumber(colNum);
		seatInfoData.setColumnSpan(1);
		seatInfoData.setSeatNumber(seat.getIdentifier());

		boolean seatAvailability = false;
		if (seat.getProduct() == null)
		{
			seatAvailability = true;
		}
		else if (offerRequestData.getSeatMapRequest() != null
				&& CollectionUtils.isNotEmpty(offerRequestData.getSeatMapRequest().getSegmentInfoDatas()))
		{
			final List<SegmentInfoData> segmentInfoDatas = offerRequestData.getSeatMapRequest().getSegmentInfoDatas();
			for (final SegmentInfoData segmentInfoData : segmentInfoDatas)
			{
				if (transportOffering.getCode().equals(segmentInfoData.getTransportOfferingCode())
						&& StringUtils.equalsIgnoreCase(cabinData.getCabinClass().getCode(), segmentInfoData.getCabinClass()))
				{
					seatAvailability = true;
				}
			}
		}

		if (seatAvailability)
		{
			final Collection<ProximityItemModel> proximityItem = seat.getProximityItem();
			if (CollectionUtils.isNotEmpty(proximityItem))
			{
				final List<SeatFeatureData> seatFeatures = new ArrayList<>();
				seatInfoData.setSeatFeature(seatFeatures);
				for (final ProximityItemModel proximityItemModel : proximityItem)
				{
					final SeatFeatureData seatFeatureData = new SeatFeatureData();
					seatFeatures.add(seatFeatureData);
					populateSeatFeatureData(offerRequestData, proximityItemModel, seatFeatureData);
				}
			}
			populateSeatFare(offerRequestData, seatInfoData, seat, transportOffering, itineraryData);
			populateSeatAvailability(offerRequestData, cabinData, seat, selectedAccommodations, transportOffering, productStockMap);
		}
		else
		{
			populateDisableSeatAvailability(offerRequestData, cabinData, seat);
		}

	}

}
