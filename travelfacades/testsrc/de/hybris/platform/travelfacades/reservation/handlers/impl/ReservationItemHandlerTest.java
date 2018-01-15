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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for ReservationItemHandler
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationItemHandlerTest
{
	@Mock
	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@Mock
	private Converter<TravelRouteModel, TravelRouteData> travelRouteConverter;

	@Mock
	private Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter;

	@Mock
	private TravellerService travellerService;

	@Mock
	private TravellerSortStrategy travellerSortStrategy;

	@Mock
	private TransportOfferingFacade transportOfferingFacade;

	@InjectMocks
	ReservationItemHandler handler;

	/**
	 * given: null AbstractOrderModel
	 *
	 * when: ReservationItems are populated
	 *
	 * then: reservationData has no reservation items.
	 */
	@Test
	public void testNullAbstractOrderModel()
	{
		final ReservationData reservationData = new ReservationData();
		handler.handle(null, reservationData);
		assertTrue(reservationData.getReservationItems().isEmpty());
	}

	/**
	 * given: empty AbstractOrderModel
	 *
	 * when: ReservationItems are populated
	 *
	 * then: reservationData has no reservation items.
	 */
	@Test
	public void testNullEmptyOrderEntries()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final ReservationData reservationData = new ReservationData();
		handler.handle(testDataSetUp.createCartModel(Collections.EMPTY_LIST), reservationData);
		assertTrue(reservationData.getReservationItems().isEmpty());
	}

	/**
	 * given: AbstractOrderModel with fareProducts and Itinerary is a single sector.
	 *
	 * when: ReservationItems are populated
	 *
	 * then: reservationData has 1 reservation items.
	 */
	@Test
	public void testFareProductEntriesSingleSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				null);
		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("EZY1234090320160735");
		entry1.getTravelOrderEntryInfo().setTransportOfferings(Stream.of(transportOffering1).collect(Collectors.toList()));
		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				null);
		entry2.getTravelOrderEntryInfo().setTransportOfferings(
				Stream.of(testDataSetUp.createTransportOfferingModel("EZY1234090320160735")).collect(Collectors.toList()));
		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null, null);

		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()));

		final Map<Integer, List<TravellerModel>> travellersMap = new HashMap<>();
		travellersMap.put(1, Stream.of(travellerAdult1, travellerChild1).collect(Collectors.toList()));
		given(travellerService.getTravellersPerLeg(abstractorderModel)).willReturn(travellersMap);
		final ReservationData reservationData = new ReservationData();
		handler.handle(abstractorderModel, reservationData);
		assertFalse(reservationData.getReservationItems().isEmpty());
		assertEquals(1, reservationData.getReservationItems().size());
	}

	/**
	 * given: AbstractOrderModel with fareProducts and Itinerary is a oneway multi sector(2 sectors).
	 *
	 * when: ReservationItems are populated
	 *
	 * then: reservationData has 1 reservation items.
	 */
	@Test
	public void testFareProductEntriesMultiSector()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				null);
		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("EZY1234090320160735");
		final TransportOfferingModel transportOffering2 = testDataSetUp.createTransportOfferingModel("EZY5678090320160735");
		entry1.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()));
		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				null);
		entry2.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()));
		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 1, 1, 10d, 10d, null, null);

		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()));

		final Map<Integer, List<TravellerModel>> travellersMap = new HashMap<>();
		travellersMap.put(1, Stream.of(travellerAdult1, travellerChild1).collect(Collectors.toList()));
		given(travellerService.getTravellersPerLeg(abstractorderModel)).willReturn(travellersMap);
		final ReservationData reservationData = new ReservationData();
		handler.handle(abstractorderModel, reservationData);
		assertFalse(reservationData.getReservationItems().isEmpty());
		assertEquals(1, reservationData.getReservationItems().size());
	}

	/**
	 * given: AbstractOrderModel with fareProducts and Itinerary is a return multi sector(2 sectors).
	 *
	 * when: ReservationItems are populated
	 *
	 * then: reservationData has 2 reservation items.
	 */
	@Test
	public void testFareProductEntriesMultiSectorReturn()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		//Prepare CartModel
		final FareProductModel fareProduct1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdult1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(fareProduct1, 1, 1, 20d, 20d, travellerAdult1,
				null);
		final TransportOfferingModel transportOffering1 = testDataSetUp.createTransportOfferingModel("EZY1234090320160735");
		final TransportOfferingModel transportOffering2 = testDataSetUp.createTransportOfferingModel("EZY5678090320160735");
		entry1.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()));
		final FareProductModel fareProduct2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChild1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(fareProduct2, 1, 1, 20d, 20d, travellerChild1,
				null);
		entry2.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOffering1, transportOffering2).collect(Collectors.toList()));

		//Return leg

		final FareProductModel fareProductR1 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerAdultR1 = testDataSetUp.createTravellerModel("1234", "ADULT");
		final AbstractOrderEntryModel entryR1 = testDataSetUp.createCartEntryModel(fareProductR1, 2, 1, 20d, 20d, travellerAdultR1,
				null);
		final TransportOfferingModel transportOfferingR1 = testDataSetUp.createTransportOfferingModel("EZY2222090320160735");
		final TransportOfferingModel transportOfferingR2 = testDataSetUp.createTransportOfferingModel("EZY4444090320160735");
		entryR1.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOfferingR1, transportOfferingR2).collect(Collectors.toList()));
		final FareProductModel fareProductR2 = testDataSetUp.createFareProductModel();
		final TravellerModel travellerChildR1 = testDataSetUp.createTravellerModel("5678", "CHILD");
		final AbstractOrderEntryModel entryR2 = testDataSetUp.createCartEntryModel(fareProductR2, 2, 1, 20d, 20d, travellerChildR1,
				null);
		entryR2.getTravelOrderEntryInfo()
				.setTransportOfferings(Stream.of(transportOfferingR1, transportOfferingR2).collect(Collectors.toList()));

		final ProductModel feeProduct = testDataSetUp.createFeeProduct();
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(feeProduct, 0, 1, 10d, 10d, null, null);

		final AbstractOrderModel abstractorderModel = testDataSetUp
				.createCartModel(Stream.of(entry1, entry2, entryR1, entryR2, entry3).collect(Collectors.toList()));

		final Map<Integer, List<TravellerModel>> travellersMap = new HashMap<>();
		travellersMap.put(1, Stream.of(travellerAdult1, travellerChild1).collect(Collectors.toList()));
		given(travellerService.getTravellersPerLeg(abstractorderModel)).willReturn(travellersMap);
		final ReservationData reservationData = new ReservationData();
		handler.handle(abstractorderModel, reservationData);
		assertFalse(reservationData.getReservationItems().isEmpty());
		assertEquals(2, reservationData.getReservationItems().size());
	}

	private class TestDataSetUp
	{
		private CartModel createCartModel(final List<AbstractOrderEntryModel> cartEntries)
		{
			final CartModel cart = new CartModel();
			cart.setEntries(cartEntries);
			return cart;
		}

		private AbstractOrderEntryModel createCartEntryModel(final ProductModel product, final int odRefNumber,
				final int bundleNumber, final double basePrice, final double totalPrice, final TravellerModel traveller,
				final Long qty)
		{
			final AbstractOrderEntryModel cartEntry = new CartEntryModel();
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			cartEntry.setProduct(product);
			orderEntryInfo.setOriginDestinationRefNumber(odRefNumber);
			cartEntry.setBundleNo(bundleNumber);
			cartEntry.setActive(true);
			cartEntry.setBasePrice(basePrice);
			cartEntry.setTotalPrice(totalPrice);
			orderEntryInfo.setTravellers(Stream.of(traveller).collect(Collectors.toList()));
			cartEntry.setBundleTemplate(createBundleTemplateModel());
			cartEntry.setQuantity(qty);
			cartEntry.setTravelOrderEntryInfo(orderEntryInfo);
			return cartEntry;
		}

		private TravellerModel createTravellerModel(final String uid, final String type)
		{
			final TravellerModel travellerModel = new TravellerModel();
			travellerModel.setUid(uid);
			travellerModel.setType(TravellerType.PASSENGER);
			travellerModel.setInfo(createPassengerInformationModel(type));
			travellerModel.setLabel(type);
			return travellerModel;
		}

		private PassengerInformationModel createPassengerInformationModel(final String type)
		{
			final PassengerInformationModel paxInfo = new PassengerInformationModel();
			final PassengerTypeModel paxType = new PassengerTypeModel();
			paxType.setCode(type);
			paxInfo.setPassengerType(paxType);
			return paxInfo;
		}

		private FareProductModel createFareProductModel()
		{
			final FareProductModel product = new FareProductModel();
			product.setProductType(ProductType.FARE_PRODUCT);
			product.setBookingClass("O");
			product.setFareBasisCode("OHMX21");
			product.setBundleTemplates(Stream.of(createBundleTemplateModel()).collect(Collectors.toList()));
			return product;
		}

		private ProductModel createFeeProduct()
		{
			final ProductModel product = new ProductModel();
			product.setProductType(ProductType.FEE);
			return product;
		}

		private BundleTemplateModel createBundleTemplateModel()
		{
			final BundleTemplateModel bundleModel = new BundleTemplateModel();
			bundleModel.setType(BundleType.ECONOMY);
			return bundleModel;
		}

		private TransportOfferingModel createTransportOfferingModel(final String code)
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel();
			transportOffering.setCode(code);
			return transportOffering;
		}

	}

}