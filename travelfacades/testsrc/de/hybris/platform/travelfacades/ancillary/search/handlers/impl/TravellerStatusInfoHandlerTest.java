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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.enums.TravellerStatus;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.TravellerStatusInfoHandler;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for TravellerStatusInfoHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravellerStatusInfoHandlerTest
{
	@Mock
	private TravelCartFacade travelCartFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CustomerAccountService customerAccountService;

	@InjectMocks
	private final TravellerStatusInfoHandler handler = new TravellerStatusInfoHandler();

	/**
	 * given: OfferResponseData
	 * 
	 * when: in purchase flow
	 * 
	 * then: TravellerStatusInfo is not populated.
	 */
	@Test
	public void testPopulatePurchase()
	{
		given(travelCartFacade.isAmendmentCart()).willReturn(false);
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OfferResponseData offerResponseData = testDataSetUp.createOfferResponseData(null);
		handler.handle(null, offerResponseData);
		assertNull(offerResponseData.getItineraries());
	}

	/**
	 * given: OfferResponseData
	 * 
	 * when: in amendment flow and Consignment status is valid TravellerStatus
	 * 
	 * then: TravellerStatusInfo is populated with Consignment status.
	 */
	@Test
	public void testPopulateAmendment()
	{
		final List<String> transportOfferingCodes = Stream.of("EZY1234060320160725", "EZY5678060320160925")
				.collect(Collectors.toList());
		final List<TransportOfferingData> transportOfferings = transportOfferingCodes.stream().map(toConverter)
				.collect(Collectors.toList());
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OriginDestinationOptionData odOptionData = testDataSetUp.createOriginDestinationOptionData(transportOfferings);
		final ItineraryData itineraryData = testDataSetUp.createItineraryData(Stream.of(odOptionData).collect(Collectors.toList()),
				Stream.of(testDataSetUp.createTravellerData("1234"), testDataSetUp.createTravellerData("5678"))
						.collect(Collectors.toList()));
		final OfferResponseData offerResponsetData = testDataSetUp
				.createOfferResponseData(Stream.of(itineraryData).collect(Collectors.toList()));
		given(travelCartFacade.isAmendmentCart()).willReturn(true);
		given(travelCartFacade.getOriginalOrderCode()).willReturn("00001");
		final BaseStoreModel baseStoreModel = testDataSetUp.createBaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(customerAccountService.getOrderForCode("00001", baseStoreModel))
				.willReturn(testDataSetUp.createOrderModel(ConsignmentStatus.CHECKED_IN));
		handler.handle(null, offerResponsetData);
		final TravellerData travellerData = offerResponsetData.getItineraries().stream().findFirst().get().getTravellers().stream()
				.findFirst().get();

		assertEquals(TravellerStatus.CHECKED_IN, travellerData.getTravellerStatusInfo().get("EZY1234060320160725"));
	}

	/**
	 * given: OfferResponseData
	 * 
	 * when: in amendment flow and Consignment status is not a valid TravellerStatus
	 * 
	 * then: TravellerStatusInfo is populated as READY.
	 */
	@Test
	public void testPopulateIncorrectStatus()
	{
		final List<String> transportOfferingCodes = Stream.of("EZY1234060320160725", "EZY5678060320160925")
				.collect(Collectors.toList());
		final List<TransportOfferingData> transportOfferings = transportOfferingCodes.stream().map(toConverter)
				.collect(Collectors.toList());
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OriginDestinationOptionData odOptionData = testDataSetUp.createOriginDestinationOptionData(transportOfferings);
		final ItineraryData itineraryData = testDataSetUp.createItineraryData(Stream.of(odOptionData).collect(Collectors.toList()),
				Stream.of(testDataSetUp.createTravellerData("1234"), testDataSetUp.createTravellerData("5678"))
						.collect(Collectors.toList()));
		final OfferResponseData offerResponsetData = testDataSetUp
				.createOfferResponseData(Stream.of(itineraryData).collect(Collectors.toList()));
		given(travelCartFacade.isAmendmentCart()).willReturn(true);
		given(travelCartFacade.getOriginalOrderCode()).willReturn("00001");
		final BaseStoreModel baseStoreModel = testDataSetUp.createBaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(customerAccountService.getOrderForCode("00001", baseStoreModel))
				.willReturn(testDataSetUp.createOrderModel(ConsignmentStatus.SHIPPED));
		handler.handle(null, offerResponsetData);
		final TravellerData travellerData = offerResponsetData.getItineraries().stream().findFirst().get().getTravellers().stream()
				.findFirst().get();

		assertEquals(TravellerStatus.READY, travellerData.getTravellerStatusInfo().get("EZY1234060320160725"));
	}

	Function<String, TransportOfferingData> toConverter = new Function<String, TransportOfferingData>()
	{
		@Override
		public TransportOfferingData apply(final String code)
		{
			final TransportOfferingData toModel = new TransportOfferingData();
			toModel.setCode(code);
			return toModel;
		}

	};

	private class TestDataSetUp
	{
		private OfferRequestData createOfferRequestData(final List<ItineraryData> itineraries)
		{
			final OfferRequestData offerRequestData = new OfferRequestData();
			offerRequestData.setItineraries(itineraries);
			return offerRequestData;
		}

		private OfferResponseData createOfferResponseData(final List<ItineraryData> itineraries)
		{
			final OfferResponseData offerResponseData = new OfferResponseData();
			offerResponseData.setItineraries(itineraries);
			return offerResponseData;
		}

		private ItineraryData createItineraryData(final List<OriginDestinationOptionData> originDestinationOptions,
				final List<TravellerData> travellers)
		{
			final ItineraryData itineraryData = new ItineraryData();
			itineraryData.setOriginDestinationOptions(originDestinationOptions);
			itineraryData.setTravellers(travellers);
			return itineraryData;
		}

		private OriginDestinationOptionData createOriginDestinationOptionData(final List<TransportOfferingData> transportOfferings)
		{
			final OriginDestinationOptionData odData = new OriginDestinationOptionData();
			odData.setTransportOfferings(transportOfferings);
			return odData;
		}

		private TravellerData createTravellerData(final String uid)
		{
			final TravellerData travellerData = new TravellerData();
			travellerData.setUid(uid);
			return travellerData;
		}

		private TravellerModel createTravellerModel(final String uid)
		{
			final TravellerModel travellerModel = new TravellerModel();
			travellerModel.setUid(uid);
			return travellerModel;
		}

		private BaseStoreModel createBaseStoreModel()
		{
			final BaseStoreModel baseStoreModel = new BaseStoreModel();
			return baseStoreModel;
		}

		private OrderModel createOrderModel(final ConsignmentStatus status)
		{
			final OrderModel orderModel = new OrderModel();
			orderModel.setConsignments(Stream.of(createConsignmentModel("EZY1234060320160725", "1234", status),
					createConsignmentModel("EZY1234060320160725", "5678", status),
					createConsignmentModel("EZY5678060320160925", "1234", status),
					createConsignmentModel("EZY5678060320160925", "5678", status)).collect(Collectors.toSet()));
			return orderModel;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel toModel = new TransportOfferingModel();
			toModel.setCode(code);
			return toModel;
		}

		private ConsignmentModel createConsignmentModel(final String transportOffering, final String travellerUid,
				final ConsignmentStatus status)
		{
			final ConsignmentModel consignment = new ConsignmentModel();
			consignment.setWarehouse(createTransportOfferingModel(transportOffering));
			consignment.setTraveller(createTravellerModel(travellerUid));
			consignment.setStatus(status);
			return consignment;
		}

	}

}
