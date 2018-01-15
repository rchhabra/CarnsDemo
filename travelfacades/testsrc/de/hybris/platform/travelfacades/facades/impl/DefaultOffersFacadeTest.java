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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchRequestPipelineManager;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.OfferResponseFilterStrategy;
import de.hybris.platform.travelfacades.strategies.impl.TransportOfferingPastDepartureDateStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOffersFacadeTest
{
	@InjectMocks
	private DefaultOffersFacade offersFacade;
	@Mock
	private AncillarySearchPipelineManager ancillarySearchPipelineManager;
	@Mock
	private AncillarySearchRequestPipelineManager ancillarySearchRequestPipelineManager;
	@Mock
	private Converter<ReservationData, OfferRequestData> ancillarySearchRequestConverter;
	@Mock
	private AncillarySearchPipelineManager accommodationSearchPipelineManager;
	@Mock
	private TransportOfferingPastDepartureDateStrategy transportOfferingPastDepartureDateStrategy;
	@Mock
	private ReservationFacade reservationFacade;
	@Mock
	private TravelCartFacade travelCartFacade;
	@Mock
	private CartService cartService;
	@Mock
	private ReservationData reservationData;
	@Mock
	private OfferRequestData offerRequestData;
	@Mock
	private OfferResponseData offerResponseData;
	@Mock
	private List<OfferPricingInfoData> offerPricingInfos;
	@Mock
	OfferResponseFilterStrategy strategy;


	@Test
	public void testGetOffers()
	{
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
		final OriginDestinationOfferInfoData originDestinationOfferInfoData1 = new OriginDestinationOfferInfoData();
		originDestinationOfferInfoData1.setOfferPricingInfos(offerPricingInfos);
		originDestinationOfferInfos.add(originDestinationOfferInfoData1);
		final OriginDestinationOfferInfoData originDestinationOfferInfoData2 = new OriginDestinationOfferInfoData();
		originDestinationOfferInfoData2.setOfferPricingInfos(Collections.emptyList());
		originDestinationOfferInfos.add(originDestinationOfferInfoData2);

		final List<OfferGroupData> offerGroupDataList = new ArrayList<>();
		final OfferGroupData offerGroup1 = new OfferGroupData();
		offerGroup1.setCode("MEALS");
		offerGroup1.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		offerGroupDataList.add(offerGroup1);
		final OfferGroupData offerGroup2 = new OfferGroupData();
		offerGroup2.setCode("HOLD_ALLOWANCE");
		offerGroup2.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		offerGroupDataList.add(offerGroup2);
		final OfferGroupData offerGroup3 = new OfferGroupData();
		offerGroup3.setCode("PRIORITY_CHECKIN");
		offerGroup3.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		offerGroupDataList.add(offerGroup3);

		final OfferResponseData offerResponseData = new OfferResponseData();
		offerResponseData.setOfferGroups(offerGroupDataList);
		when(ancillarySearchPipelineManager.executePipeline(
				Matchers.any(OfferRequestData.class))).thenReturn(offerResponseData);

		when(travelCartFacade.isAmendmentCart()).thenReturn(false);
		final OfferResponseData responseData = offersFacade.getOffers(offerRequestData);
		Assert.assertNotNull(responseData);
	}

	@Test
	public void testGetOffersWhenAmendment()
	{
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<>();
		final OriginDestinationOfferInfoData originDestinationOfferInfoData1 = new OriginDestinationOfferInfoData();
		originDestinationOfferInfoData1.setOfferPricingInfos(offerPricingInfos);
		originDestinationOfferInfos.add(originDestinationOfferInfoData1);
		final OriginDestinationOfferInfoData originDestinationOfferInfoData2 = new OriginDestinationOfferInfoData();
		originDestinationOfferInfoData2.setOfferPricingInfos(Collections.emptyList());
		originDestinationOfferInfos.add(originDestinationOfferInfoData2);

		final List<OfferGroupData> offerGroupDataList = new ArrayList<>();
		final OfferGroupData offerGroup1 = new OfferGroupData();
		offerGroup1.setCode("MEALS");
		offerGroup1.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		offerGroupDataList.add(offerGroup1);
		final OfferGroupData offerGroup2 = new OfferGroupData();
		offerGroup2.setCode("HOLD_ALLOWANCE");
		offerGroup2.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		offerGroupDataList.add(offerGroup2);
		final OfferGroupData offerGroup3 = new OfferGroupData();
		offerGroup3.setCode("PRIORITY_CHECKIN");
		offerGroup3.setOriginDestinationOfferInfos(originDestinationOfferInfos);
		offerGroupDataList.add(offerGroup3);

		final OfferResponseData offerResponseData = new OfferResponseData();
		offerResponseData.setOfferGroups(offerGroupDataList);
		when(ancillarySearchPipelineManager.executePipeline(
				Matchers.any(OfferRequestData.class))).thenReturn(offerResponseData);

		when(travelCartFacade.isAmendmentCart()).thenReturn(true);
		doNothing().when(strategy).filterOfferResponseData(Matchers.any(OfferResponseData.class));
		offersFacade.setOfferResponseFilterStrategyList(Stream.of(strategy).collect(Collectors.toList()));
		final OfferResponseData responseData = offersFacade.getOffers(offerRequestData);
		Assert.assertNotNull(responseData);
	}

	@Test
	public void testGetOffersRequest()
	{
		when(offersFacade.getReservationFacade().getCurrentReservationData()).thenReturn(reservationData);
		when(offersFacade.getAncillarySearchRequestPipelineManager().executePipeline(Matchers.any(ReservationData.class)))
				.thenReturn(offerRequestData);
		final OfferRequestData offerRequestData = offersFacade.getOffersRequest();
		Assert.assertNotNull(offerRequestData);
	}

	@Test
	public void testGetAccommodations()
	{
		when(offersFacade.getAccommodationSearchPipelineManager().executePipeline(Matchers.any(OfferRequestData.class)))
				.thenReturn(offerResponseData);
		final OfferResponseData offerResponseData = offersFacade.getAccommodations(offerRequestData);
		Assert.assertNotNull(offerResponseData);
	}

	@Test
	public void testGetAccommodationsWithAmendedCart()
	{
		when(offersFacade.getAccommodationSearchPipelineManager().executePipeline(Matchers.any(OfferRequestData.class)))
				.thenReturn(offerResponseData);
		when(travelCartFacade.isAmendmentCart()).thenReturn(true);
		doNothing().when(strategy).filterOfferResponseData(Matchers.any(OfferResponseData.class));
		offersFacade.setOfferResponseFilterStrategyList(Stream.of(strategy).collect(Collectors.toList()));
		final OfferResponseData offerResponseData = offersFacade.getAccommodations(offerRequestData);
		Assert.assertNotNull(offerResponseData);
	}
}
