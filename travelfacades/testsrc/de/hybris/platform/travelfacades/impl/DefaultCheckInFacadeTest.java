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

package de.hybris.platform.travelfacades.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.BookingInfoData;
import de.hybris.platform.commercefacades.travel.CheckInRequestData;
import de.hybris.platform.commercefacades.travel.CheckInResponseData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerTransportOfferingInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultCheckInFacade;
import de.hybris.platform.travelfacades.strategies.CheckInEvaluatorStrategy;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link CheckInFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCheckInFacadeTest
{
	@Mock
	private TransportOfferingService transportOfferingService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private TravellerService travellerService;
	@Mock
	private ModelService modelService;
	@Mock
	private CheckInEvaluatorStrategy checkInEvaluatorStrategy;
	@Mock
	private BookingService bookingService;
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private CheckInProcessModel checkInProcessModel;

	DefaultCheckInFacade defaultCheckInFacade;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultCheckInFacade = new DefaultCheckInFacade();
		defaultCheckInFacade.setBaseStoreService(baseStoreService);
		defaultCheckInFacade.setTravellerService(travellerService);
		defaultCheckInFacade.setModelService(modelService);
		defaultCheckInFacade.setCheckInEvaluatorStrategy(checkInEvaluatorStrategy);
		defaultCheckInFacade.setBookingService(bookingService);
		defaultCheckInFacade.setBusinessProcessService(businessProcessService);
	}

	@Test
	public void testDoCheckinForEmptyTravellerTransportOffering()
	{
		final CheckInRequestData checkInRequest = new CheckInRequestData();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		given(transportOfferingService.getTransportOffering(Matchers.anyString())).willReturn(transportOfferingModel);

		final CheckInResponseData result = defaultCheckInFacade.doCheckin(checkInRequest);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrors().containsKey("checkin.no.passenger.error.message"));
	}

	@Test
	public void testDoCheckin()
	{
		final String transportOfferingCode = "EZY0004";

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setDateOfBirth(DateUtils.addHours(new Date(), -21));
		travellerData.setTravellerInfo(passengerInformationData);


		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setMaxAge(20);

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		passengerInformationModel.setFirstName("testFirst");
		passengerInformationModel.setPassengerType(passengerTypeModel);

		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid("adult");
		travellerModel.setLabel("adult");
		travellerModel.setType(TravellerType.PASSENGER);
		travellerModel.setInfo(passengerInformationModel);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode(transportOfferingCode);
		transportOfferingData.setArrivalTime(DateUtils.addHours(new Date(), 4));

		final BookingInfoData bookingInfoData = new BookingInfoData();
		bookingInfoData.setBookingReference("EZY01");
		bookingInfoData.setOriginDestinationRefNumberList(Arrays.asList(Integer.valueOf(0)));

		final TravellerTransportOfferingInfoData travellerTransportOfferingInfoData = new TravellerTransportOfferingInfoData();
		travellerTransportOfferingInfoData.setTraveller(travellerData);
		travellerTransportOfferingInfoData.setTransportOffering(transportOfferingData);
		travellerTransportOfferingInfoData.setBookingInfo(bookingInfoData);

		final CheckInRequestData checkInRequest = new CheckInRequestData();
		checkInRequest
				.setTravellerTransportOfferingInfos(Stream.of(travellerTransportOfferingInfoData).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		given(transportOfferingService.getTransportOffering(transportOfferingCode)).willReturn(transportOfferingModel);
		given(travellerService.getExistingTraveller("adult")).willReturn(travellerModel);

		final CheckInResponseData result = defaultCheckInFacade.doCheckin(checkInRequest);
		Assert.assertNotNull(result);
	}

	@Test
	public void testDoCheckinForInvalidTraveller()
	{
		final String transportOfferingCode = "EZY0004";

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode(transportOfferingCode);
		transportOfferingData.setArrivalTime(DateUtils.addHours(new Date(), 4));

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");

		final TravellerTransportOfferingInfoData travellerTransportOfferingInfoData = new TravellerTransportOfferingInfoData();
		travellerTransportOfferingInfoData.setTransportOffering(transportOfferingData);
		travellerTransportOfferingInfoData.setTraveller(travellerData);

		final CheckInRequestData checkInRequest = new CheckInRequestData();
		checkInRequest
				.setTravellerTransportOfferingInfos(Stream.of(travellerTransportOfferingInfoData).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();

		given(transportOfferingService.getTransportOffering(transportOfferingCode)).willReturn(transportOfferingModel);

		final CheckInResponseData result = defaultCheckInFacade.doCheckin(checkInRequest);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrors().containsKey("checkin.no.traveller.found.error.message"));
	}

	@Test
	public void testDoCheckinForInvalidTravellerAge()
	{
		final String transportOfferingCode = "EZY0004";

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setDateOfBirth(DateUtils.addYears(new Date(), -25));
		travellerData.setTravellerInfo(passengerInformationData);

		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setMaxAge(20);

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		passengerInformationModel.setFirstName("testFirst");
		passengerInformationModel.setPassengerType(passengerTypeModel);

		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid("adult");
		travellerModel.setLabel("adult");
		travellerModel.setType(TravellerType.PASSENGER);
		travellerModel.setInfo(passengerInformationModel);

		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode(transportOfferingCode);
		transportOfferingData.setArrivalTime(DateUtils.addHours(new Date(), 4));

		final TravellerTransportOfferingInfoData travellerTransportOfferingInfoData = new TravellerTransportOfferingInfoData();
		travellerTransportOfferingInfoData.setTraveller(travellerData);
		travellerTransportOfferingInfoData.setTransportOffering(transportOfferingData);

		final CheckInRequestData checkInRequest = new CheckInRequestData();
		checkInRequest
				.setTravellerTransportOfferingInfos(Stream.of(travellerTransportOfferingInfoData).collect(Collectors.toList()));

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		given(transportOfferingService.getTransportOffering(transportOfferingCode)).willReturn(transportOfferingModel);
		given(travellerService.getExistingTraveller("adult")).willReturn(travellerModel);

		final CheckInResponseData result = defaultCheckInFacade.doCheckin(checkInRequest);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrors().containsKey("checkin.traveller.age.exceded.error.message"));
	}

	@Test
	public void testCheckTravellerEligibility()
	{
		final WarehouseModel warehouseModel = new WarehouseModel();
		warehouseModel.setCode("EZY0004");

		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setLabel("adult");

		final ConsignmentModel consignmentModel = new ConsignmentModel();
		consignmentModel.setStatus(ConsignmentStatus.READY);
		consignmentModel.setWarehouse(warehouseModel);
		consignmentModel.setTraveller(travellerModel);

		final OrderModel orderModel = new OrderModel();
		orderModel.setConsignments(Stream.of(consignmentModel).collect(Collectors.toSet()));

		given(bookingService.getOrderModelFromStore(Matchers.anyString())).willReturn(orderModel);

		final boolean result = defaultCheckInFacade.checkTravellerEligibility("adult",
				Stream.of("EZY0004").collect(Collectors.toList()), Matchers.anyString());
		Assert.assertTrue(result);
	}

	@Test
	public void testGetCheckinFlowGroupForCheckout()
	{
		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setCheckinFlowGroup("checkoutFlow");
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		final String result = defaultCheckInFacade.getCheckinFlowGroupForCheckout();
		Assert.assertSame("checkoutFlow", result);
	}

	@Test
	public void testGetCheckinFlowGroupForCheckoutForNoBaseStore()
	{
		final String result = defaultCheckInFacade.getCheckinFlowGroupForCheckout();
		Assert.assertNull(result);
	}

	@Test
	public void testIsCheckInPossible()
	{
		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setCheckinFlowGroup("checkoutFlow");
		given(checkInEvaluatorStrategy.isCheckInPossible(Matchers.any(), Matchers.anyInt())).willReturn(Boolean.TRUE);
		final boolean result = defaultCheckInFacade.isCheckInPossible(new ReservationData(), 0);
		Assert.assertTrue(result);
	}

	@Test
	public void testStartCheckInProcess()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("00001");
		given(bookingService.getOrder(Matchers.anyString())).willReturn(orderModel);

		given(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString())).willReturn(checkInProcessModel);
		willDoNothing().given(modelService).save(checkInProcessModel);

		defaultCheckInFacade.startCheckInProcess("0001", 1, Arrays.asList("adult"));

		verify(modelService).save(checkInProcessModel);
		verify(businessProcessService).startProcess(checkInProcessModel);
	}

}
