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
package de.hybris.platform.travelfacades.strategies.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test for the Strategy {@link TravellerStatusStrategy}
 */
@UnitTest
public class TravellerStatusStrategyTest
{

	public static final String ORDER_CODE = "000000";
	public final String TEST_TRANSPORT_OFFERING_CODE_A = "TEST_TRANSPORT_OFFERING_CODE_A";
	public final String TEST_TRANSPORT_OFFERING_CODE_B = "TEST_TRANSPORT_OFFERING_CODE_B";
	public final String TEST_TRANSPORT_OFFERING_CODE_C = "TEST_TRANSPORT_OFFERING_CODE_C";
	public final String TEST_TRANSPORT_OFFERING_CODE_D = "TEST_TRANSPORT_OFFERING_CODE_D";



	@Mock
	private TravelCartFacade travelCartFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CustomerAccountService customerAccountService;

	private List<ConsignmentStatus> notAllowedStatusList;

	private TravellerStatusStrategy travellerStatusStrategy;
	private BaseStoreModel baseStore;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		travellerStatusStrategy = new TravellerStatusStrategy();
		travellerStatusStrategy.setTravelCartFacade(this.travelCartFacade);
		travellerStatusStrategy.setBaseStoreService(this.baseStoreService);
		travellerStatusStrategy.setCustomerAccountService(this.customerAccountService);
		notAllowedStatusList = new ArrayList<ConsignmentStatus>();
		notAllowedStatusList.add(ConsignmentStatus.CHECKED_IN);
		notAllowedStatusList.add(ConsignmentStatus.CANCELLED);
		travellerStatusStrategy.setNotAllowedStatusList(this.notAllowedStatusList);

		when(this.travelCartFacade.getOriginalOrderCode()).thenReturn(ORDER_CODE);

		baseStore = mock(BaseStoreModel.class);
		when(this.baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
	}

	@Test
	public void whenNotAllPassengerAreCheckedInForOneLeg_thenDoNotRemoveOriginDestinationInfo()
	{
		final OfferGroupData offerGroupData = new OfferGroupData();

		final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
		odOfferInfoData.setOriginDestinationRefNumber(0);

		final List<OriginDestinationOfferInfoData> odOfferInfoDataList = new ArrayList<>();
		odOfferInfoDataList.add(odOfferInfoData);

		offerGroupData.setOriginDestinationOfferInfos(odOfferInfoDataList);

		final OrderModel orderModel = new OrderModel();

		when(this.customerAccountService.getOrderForCode(ORDER_CODE, baseStore)).thenReturn(orderModel);

		final ConsignmentModel consignment1 = new ConsignmentModel();

		final Set<ConsignmentModel> consigments = new HashSet<>();

		consigments.add(consignment1);

		orderModel.setConsignments(consigments);

		final ConsignmentEntryModel consignmentEntry1 = new ConsignmentEntryModel();
		final ConsignmentEntryModel consignmentEntry2 = new ConsignmentEntryModel();

		final AbstractOrderEntryModel aoe1 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo1 = new TravelOrderEntryInfoModel();
		orderEntryInfo1.setOriginDestinationRefNumber(0);
		aoe1.setTravelOrderEntryInfo(orderEntryInfo1);
		final AbstractOrderEntryModel aoe2 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo2 = new TravelOrderEntryInfoModel();
		orderEntryInfo2.setOriginDestinationRefNumber(0);
		aoe2.setTravelOrderEntryInfo(orderEntryInfo2);

		consignmentEntry1.setOrderEntry(aoe1);
		consignmentEntry2.setOrderEntry(aoe2);

		final Set<ConsignmentEntryModel> consignmentEntryModels = new HashSet<>();
		consignmentEntryModels.add(consignmentEntry1);
		consignmentEntryModels.add(consignmentEntry2);

		consignment1.setConsignmentEntries(consignmentEntryModels);

		consignment1.setStatus(ConsignmentStatus.READY);

		final OfferResponseData offerResponseData = new OfferResponseData();

		offerResponseData.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));

		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setOriginDestinationRefNumber(0);
		final ItineraryData itinData = new ItineraryData();
		itinData.setOriginDestinationOptions(Stream.of(odOptionData).collect(Collectors.toList()));
		offerResponseData.setItineraries(Stream.of(itinData).collect(Collectors.toList()));

		travellerStatusStrategy.filterOfferResponseData(offerResponseData);

		Assert.assertNotNull(offerGroupData.getOriginDestinationOfferInfos());
		Assert.assertTrue(CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()));
		assertTrue("Expected size was 1, current size is " + offerGroupData.getOriginDestinationOfferInfos().size(),
				offerGroupData.getOriginDestinationOfferInfos().size() == 1);

	}

	@Test
	public void whenAllPassengerAreCheckedInForOneLeg_thenRemoveOriginDestinationInfo()
	{
		final OfferGroupData offerGroupData = new OfferGroupData();

		final OriginDestinationOfferInfoData odOfferInfoData = new OriginDestinationOfferInfoData();
		odOfferInfoData.setOriginDestinationRefNumber(0);

		final List<OriginDestinationOfferInfoData> odOfferInfoDataList = new ArrayList<>();
		odOfferInfoDataList.add(odOfferInfoData);

		offerGroupData.setOriginDestinationOfferInfos(odOfferInfoDataList);

		final OrderModel orderModel = new OrderModel();

		when(this.customerAccountService.getOrderForCode(ORDER_CODE, baseStore)).thenReturn(orderModel);

		final ConsignmentModel consignment1 = new ConsignmentModel();

		final Set<ConsignmentModel> consigments = new HashSet<>();

		consigments.add(consignment1);

		orderModel.setConsignments(consigments);

		final ConsignmentEntryModel consignmentEntry1 = new ConsignmentEntryModel();
		final ConsignmentEntryModel consignmentEntry2 = new ConsignmentEntryModel();

		final AbstractOrderEntryModel aoe1 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo1 = new TravelOrderEntryInfoModel();
		orderEntryInfo1.setOriginDestinationRefNumber(0);
		aoe1.setTravelOrderEntryInfo(orderEntryInfo1);
		final AbstractOrderEntryModel aoe2 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo2 = new TravelOrderEntryInfoModel();
		orderEntryInfo2.setOriginDestinationRefNumber(0);
		aoe2.setTravelOrderEntryInfo(orderEntryInfo2);

		consignmentEntry1.setOrderEntry(aoe1);
		consignmentEntry2.setOrderEntry(aoe2);

		final Set<ConsignmentEntryModel> consignmentEntryModels = new HashSet<>();
		consignmentEntryModels.add(consignmentEntry1);
		consignmentEntryModels.add(consignmentEntry2);

		consignment1.setConsignmentEntries(consignmentEntryModels);

		consignment1.setStatus(ConsignmentStatus.CHECKED_IN);

		final OfferResponseData offerResponseData = new OfferResponseData();

		offerResponseData.setOfferGroups(Stream.of(offerGroupData).collect(Collectors.toList()));

		final OriginDestinationOptionData odOptionData = new OriginDestinationOptionData();
		odOptionData.setOriginDestinationRefNumber(0);
		final ItineraryData itinData = new ItineraryData();
		itinData.setOriginDestinationOptions(Stream.of(odOptionData).collect(Collectors.toList()));
		offerResponseData.setItineraries(Stream.of(itinData).collect(Collectors.toList()));

		travellerStatusStrategy.filterOfferResponseData(offerResponseData);

		Assert.assertNotNull(offerGroupData.getOriginDestinationOfferInfos());
		assertTrue(offerGroupData.getOriginDestinationOfferInfos().isEmpty());
		assertTrue("Expected size was 0, current size is " + offerGroupData.getOriginDestinationOfferInfos().size(),
				offerGroupData.getOriginDestinationOfferInfos().size() == 0);

	}

	@Test
	public void testFilterSeatMapData()
	{
		final OrderModel orderModel = new OrderModel();

		when(this.customerAccountService.getOrderForCode(ORDER_CODE, baseStore)).thenReturn(orderModel);

		final ConsignmentModel consignment1 = new ConsignmentModel();

		final Set<ConsignmentModel> consigments = new HashSet<>();

		consigments.add(consignment1);

		orderModel.setConsignments(consigments);

		final ConsignmentEntryModel consignmentEntry1 = new ConsignmentEntryModel();
		final ConsignmentEntryModel consignmentEntry2 = new ConsignmentEntryModel();

		final Collection<TransportOfferingModel> transportOfferings = new ArrayList<>();
		final TransportOfferingModel to1 = Mockito.mock(TransportOfferingModel.class);
		when(to1.getCode()).thenReturn(TEST_TRANSPORT_OFFERING_CODE_A);

		final TransportOfferingModel to2 = Mockito.mock(TransportOfferingModel.class);
		when(to2.getCode()).thenReturn(TEST_TRANSPORT_OFFERING_CODE_B);

		final TransportOfferingModel to3 = Mockito.mock(TransportOfferingModel.class);
		when(to3.getCode()).thenReturn(TEST_TRANSPORT_OFFERING_CODE_C);

		transportOfferings.add(to1);
		transportOfferings.add(to2);
		transportOfferings.add(to3);

		final TravelOrderEntryInfoModel orderEntryInfo1 = new TravelOrderEntryInfoModel();
		orderEntryInfo1.setOriginDestinationRefNumber(0);
		orderEntryInfo1.setTransportOfferings(transportOfferings);

		final AbstractOrderEntryModel aoe1 = new AbstractOrderEntryModel();
		aoe1.setTravelOrderEntryInfo(orderEntryInfo1);
		final AbstractOrderEntryModel aoe2 = new AbstractOrderEntryModel();

		final TravelOrderEntryInfoModel orderEntryInfo2 = new TravelOrderEntryInfoModel();
		orderEntryInfo2.setOriginDestinationRefNumber(0);
		orderEntryInfo2.setTransportOfferings(transportOfferings);
		aoe2.setTravelOrderEntryInfo(orderEntryInfo2);

		consignmentEntry1.setOrderEntry(aoe1);
		consignmentEntry2.setOrderEntry(aoe2);
		final Set<ConsignmentEntryModel> consignmentEntryModels = new HashSet<>();
		consignmentEntryModels.add(consignmentEntry1);
		consignmentEntryModels.add(consignmentEntry2);

		consignment1.setConsignmentEntries(consignmentEntryModels);

		consignment1.setStatus(ConsignmentStatus.PICKPACK);

		final TransportOfferingData tod1 = new TransportOfferingData();
		tod1.setCode(TEST_TRANSPORT_OFFERING_CODE_A);

		final TransportOfferingData tod2 = new TransportOfferingData();
		tod2.setCode(TEST_TRANSPORT_OFFERING_CODE_B);


		final SeatMapData testSeatMapData1 = new SeatMapData();
		testSeatMapData1.setTransportOffering(tod1);

		final SeatMapData testSeatMapData2 = new SeatMapData();
		testSeatMapData2.setTransportOffering(tod2);

		final SeatMapResponseData testSeatMapResponseData = new SeatMapResponseData();

		final List<SeatMapData> testSeatMapDatas = new ArrayList<>();
		testSeatMapDatas.add(testSeatMapData1);
		testSeatMapDatas.add(testSeatMapData2);
		testSeatMapResponseData.setSeatMap(testSeatMapDatas);
		travellerStatusStrategy.filterSeatMapData(testSeatMapResponseData);

		Assert.assertEquals(2, CollectionUtils.size(testSeatMapResponseData.getSeatMap()));
	}

	@Test
	public void testFilterSeatMapDataForInValidConsignmentStatus()
	{
		final OrderModel orderModel = new OrderModel();

		when(this.customerAccountService.getOrderForCode(ORDER_CODE, baseStore)).thenReturn(orderModel);

		final ConsignmentModel consignment1 = new ConsignmentModel();

		final Set<ConsignmentModel> consigments = new HashSet<>();

		consigments.add(consignment1);

		orderModel.setConsignments(consigments);

		final ConsignmentEntryModel consignmentEntry1 = new ConsignmentEntryModel();
		final ConsignmentEntryModel consignmentEntry2 = new ConsignmentEntryModel();
		final ConsignmentEntryModel consignmentEntry3 = new ConsignmentEntryModel();

		final Collection<TransportOfferingModel> transportOfferings = new ArrayList<>();
		final TransportOfferingModel to1 = Mockito.mock(TransportOfferingModel.class);
		when(to1.getCode()).thenReturn(TEST_TRANSPORT_OFFERING_CODE_A);

		final TransportOfferingModel to2 = Mockito.mock(TransportOfferingModel.class);
		when(to2.getCode()).thenReturn(TEST_TRANSPORT_OFFERING_CODE_B);

		final TransportOfferingModel to3 = Mockito.mock(TransportOfferingModel.class);
		when(to3.getCode()).thenReturn(TEST_TRANSPORT_OFFERING_CODE_C);

		transportOfferings.add(to1);
		transportOfferings.add(to2);
		transportOfferings.add(to3);

		final TravelOrderEntryInfoModel orderEntryInfo1 = new TravelOrderEntryInfoModel();
		orderEntryInfo1.setOriginDestinationRefNumber(0);
		orderEntryInfo1.setTransportOfferings(transportOfferings);

		final AbstractOrderEntryModel aoe1 = new AbstractOrderEntryModel();
		aoe1.setTravelOrderEntryInfo(orderEntryInfo1);
		final AbstractOrderEntryModel aoe2 = new AbstractOrderEntryModel();

		final AbstractOrderEntryModel aoe3 = new AbstractOrderEntryModel();
		aoe3.setTravelOrderEntryInfo(null);
		final TravelOrderEntryInfoModel orderEntryInfo2 = new TravelOrderEntryInfoModel();
		orderEntryInfo2.setOriginDestinationRefNumber(0);
		orderEntryInfo2.setTransportOfferings(transportOfferings);
		aoe2.setTravelOrderEntryInfo(orderEntryInfo2);

		consignmentEntry1.setOrderEntry(aoe1);
		consignmentEntry2.setOrderEntry(aoe2);
		consignmentEntry3.setOrderEntry(aoe3);
		final Set<ConsignmentEntryModel> consignmentEntryModels = new HashSet<>();
		consignmentEntryModels.add(consignmentEntry1);
		consignmentEntryModels.add(consignmentEntry2);
		consignmentEntryModels.add(consignmentEntry3);

		consignment1.setConsignmentEntries(consignmentEntryModels);

		consignment1.setStatus(ConsignmentStatus.CHECKED_IN);

		final TransportOfferingData tod1 = new TransportOfferingData();
		tod1.setCode(TEST_TRANSPORT_OFFERING_CODE_A);

		final TransportOfferingData tod2 = new TransportOfferingData();
		tod2.setCode(TEST_TRANSPORT_OFFERING_CODE_B);

		final TransportOfferingData tod3 = new TransportOfferingData();
		tod3.setCode(TEST_TRANSPORT_OFFERING_CODE_D);

		final SeatMapData testSeatMapData1 = new SeatMapData();
		testSeatMapData1.setTransportOffering(tod1);

		final SeatMapData testSeatMapData2 = new SeatMapData();
		testSeatMapData2.setTransportOffering(tod2);

		final SeatMapData testSeatMapData3 = new SeatMapData();
		testSeatMapData3.setTransportOffering(tod3);

		final SeatMapResponseData testSeatMapResponseData = new SeatMapResponseData();

		final List<SeatMapData> testSeatMapDatas = new ArrayList<>();
		testSeatMapDatas.add(testSeatMapData1);
		testSeatMapDatas.add(testSeatMapData2);
		testSeatMapDatas.add(testSeatMapData3);
		testSeatMapResponseData.setSeatMap(testSeatMapDatas);
		travellerStatusStrategy.filterSeatMapData(testSeatMapResponseData);

		Assert.assertEquals(0, CollectionUtils.size(testSeatMapResponseData.getSeatMap()));
	}
}
