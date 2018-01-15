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

package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ.Query;
import de.hybris.platform.ndcfacades.ndc.OrderItemAssociationType;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType;
import de.hybris.platform.ndcfacades.ndc.SeatMapRowNbrType;
import de.hybris.platform.ndcfacades.order.NDCPaymentTransactionFacade;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCAccommodationService;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCommerceCheckoutService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

//TODO This (or part of this class) has been commented out due to 17.1 upgrade and needs to be revisited
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddAccommodationToOrderStrategyTest
{
	@InjectMocks
	AddAccommodationToOrderStrategy addAccommodationToOrderStrategy;

	@Mock
	private ProductService productService;

	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private TimeService timeService;

	@Mock
	private ModelService modelService;

	@Mock
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

	@Mock
	private KeyGenerator orderCodeGenerator;

	@Mock
	private UserService userService;

	@Mock
	private CalculationService calculationService;

	@Mock
	private StoreSessionFacade storeSessionFacade;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private ReservationFacade reservationFacade;

	@Mock
	private TravellerService travellerService;

	@Mock
	private List<AmendOrderValidationStrategy> amendOrderValidationStrategyList;

	@Mock
	private TravelCommerceCheckoutService travelCommerceCheckoutService;

	@Mock
	private BookingService bookingService;

	@Mock
	private NDCPaymentTransactionFacade ndcPaymentTransactionFacade;

	@Mock
	private NDCOrderService ndcOrderService;

	@Mock
	private NDCTransportOfferingService ndcTransportOfferingService;

	@Mock
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;

	@Mock
	private NDCAccommodationService ndcAccommodationService;

	private final TestSetup testSetup = new TestSetup();

	@Before
	public void setUp() throws NDCOrderException, CalculationException
	{
		Mockito.when(orderCodeGenerator.generate()).thenReturn("00001");
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(testSetup.createCurrencyModel("GBP"));
		Mockito.when(userService.getCurrentUser()).thenReturn(testSetup.createUserModel(testSetup.createCurrencyModel("EUR")));
		Mockito.doNothing().when(storeSessionFacade).setCurrentCurrency(Matchers.anyString());
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).detachAll();

		Mockito.when(timeService.getCurrentTime()).thenReturn(new Date());
		Mockito.when(timeService.getCurrentTime()).thenReturn(new Date());
		Mockito.when(modelService.clone(Matchers.any(PaymentInfoModel.class)))
				.thenReturn(testSetup.createPaymentInfoModel(testSetup.createAddressModel()));
		final Date currentDate = new Date();
		final TransportOfferingModel transportOfferingModelInbound = testSetup.createTransportOffering("Inbound",
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));
		Mockito.when(ndcTransportOfferingService.getTransportOffering(Matchers.anyString()))
				.thenReturn(transportOfferingModelInbound);

		Mockito
				.when(ndcAccommodationService.getConfiguredAccommodation(Matchers.any(NDCOfferItemId.class),
						Matchers.any(TransportOfferingModel.class), Matchers.anyString()))
				.thenReturn(testSetup.createConfiguredAccommodationModel());

		Mockito.when(ndcAccommodationService.checkIfAccommodationCanBeAdded(Matchers.any(ProductModel.class), Matchers.anyString(),
				Matchers.any(NDCOfferItemId.class), Matchers.any(TransportOfferingModel.class))).thenReturn(Boolean.TRUE);

		Mockito.when(ndcAccommodationService.checkIfSeatValidForFareProd(Matchers.any(ProductModel.class),
				Matchers.any(NDCOfferItemId.class))).thenReturn(Boolean.TRUE);

		Mockito.when(ndcTransportOfferingService.getTransportOfferings(Matchers.anyList()))
				.thenReturn(Collections.singletonList(transportOfferingModelInbound));

		Mockito.when(ndcOfferItemIdResolver.ndcOfferItemIdToString(Matchers.any(NDCOfferItemId.class)))
				.thenReturn("TEST_NDC_OFFER_ITEM");

		final NDCOfferItemId nDCOfferItemId = new NDCOfferItemId();
		nDCOfferItemId.setRouteCode("TEST_ROUTE_CODE");
		Mockito.when(ndcOfferItemIdResolver.getNDCOfferItemIdFromString(Matchers.anyString())).thenReturn(nDCOfferItemId);
		Mockito.when(reservationFacade.getReservationData(Matchers.any(OrderModel.class)))
				.thenReturn(testSetup.createReservationData());
		Mockito.doNothing().when(ndcPaymentTransactionFacade).createPaymentTransaction(Matchers.any(BigDecimal.class),
				Matchers.any(OrderModel.class), Matchers.anyList());


		Mockito.doNothing().when(ndcAccommodationService).createOrUpdateSelectedAccommodation(
				Matchers.any(TransportOfferingModel.class), Matchers.anyList(), Matchers.any(OrderModel.class),
				Matchers.any(ConfiguredAccommodationModel.class));

		Mockito.doNothing().when(calculationService).calculate(Matchers.any(OrderModel.class));

	}

	@Test
	public void testAmendOrder() throws NDCOrderException
	{
		final OrderModel order = testSetup.createOrderModel(testSetup.createCurrencyModel("EUR"));
		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.anyObject(), Matchers.anyObject(), Matchers.any(OrderModel.class),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(order);
		Assert.assertNotNull(addAccommodationToOrderStrategy.amendOrder(order, testSetup.createOrderChangeRQ()));
	}

	private class TestSetup
	{
		public ReservationData createReservationData()
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setTotalToPay(createPriceData(100d));

			return reservationData;
		}

		private PriceData createPriceData(final double price)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(price));
			priceData.setCurrencyIso("GBP");
			priceData.setFormattedValue("GBP " + price);
			return priceData;
		}

		public ConfiguredAccommodationModel createConfiguredAccommodationModel()
		{
			final ConfiguredAccommodationModel accommodationModel = new ConfiguredAccommodationModel();
			accommodationModel.setProduct(createProductModel("product2", ProductType.ACCOMMODATION));

			return accommodationModel;
		}

		public CurrencyModel createCurrencyModel(final String isoCode)
		{
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode(isoCode);
			return currency;
		}

		public OrderChangeRQ createOrderChangeRQ()
		{
			final OrderChangeRQ orderChangeRQ = new OrderChangeRQ();
			orderChangeRQ.setQuery(createQuery());

			return orderChangeRQ;
		}

		public Query createQuery()
		{
			final Query query = new Query();
			query.setOrder(createOrder());
			return query;
		}

		private OrderChangeRQ.Query.Order createOrder()
		{
			final OrderChangeRQ.Query.Order order = new OrderChangeRQ.Query.Order();
			order.setOrderItems(createOrderItemRepriceType());

			return order;
		}

		private OrderItemRepriceType createOrderItemRepriceType()
		{
			final OrderItemRepriceType orderItemRepriceType = new OrderItemRepriceType();
			orderItemRepriceType.getOrderItem().add(createOrderItem(createItemIDType("TEST_ITEM_ID_TYPE")));
			return orderItemRepriceType;
		}

		private OrderItemRepriceType.OrderItem createOrderItem(final ItemIDType itemIdType)
		{
			final OrderItemRepriceType.OrderItem orderItem = new OrderItemRepriceType.OrderItem();
			orderItem.setOrderItemID(itemIdType);
			orderItem.setSeatItem(createSeatItem());
			final OrderItemAssociationType assosicationType = new OrderItemAssociationType();
			assosicationType.setPassengers(createPassengers());
			orderItem.setAssociations(assosicationType);
			return orderItem;
		}

		private OrderItemAssociationType.Passengers createPassengers()
		{
			final OrderItemAssociationType.Passengers passengers = new OrderItemAssociationType.Passengers();
			//passengers.getPassengerReferences().add(createPassenger());
			return passengers;
		}

		/*private Passenger createPassenger()
		{
			final Passenger passenger = new Passenger();

			final TravelerSummaryType.ProfileID profile = new TravelerSummaryType.ProfileID();
			profile.setValue("TEST_UID");
			passenger.setProfileID(profile);
			return passenger;
		}*/

		private SeatItem createSeatItem()
		{
			final SeatItem seatItem = new SeatItem();
			seatItem.getSeatReference().add(createJAXBElement());
			seatItem.getRefs().add(createSeatRefs());
			return seatItem;
		}

		private JAXBElement<Object> createJAXBElement()
		{
			final QName qname = new QName("TEST", "TEST");

			final ListOfSeatType seatReference = new ListOfSeatType();
			seatReference.setLocation(createSeatLocationType());
			seatReference.setListKey("11");
			return new JAXBElement(qname, ListOfSeatType.class, seatReference);
		}

		private ListOfFlightSegmentType createSeatRefs()
		{
			final ListOfFlightSegmentType listOfFlightSegmentType = new ListOfFlightSegmentType();
			listOfFlightSegmentType.setSegmentKey("TEST_ITEM_SEGMENT_KEY");
			return listOfFlightSegmentType;
		}

		private ItemIDType createItemIDType(final String value)
		{
			final ItemIDType itemIDType = new ItemIDType();
			itemIDType.setValue(value);
			return itemIDType;
		}

		private SeatLocationType createSeatLocationType()
		{
			final SeatLocationType seatLocationType = new SeatLocationType();
			final SeatLocationType.Row row = new SeatLocationType.Row();
			final SeatMapRowNbrType number = new SeatMapRowNbrType();
			number.setValue("1");
			row.setNumber(number);
			seatLocationType.setRow(row);
			seatLocationType.setColumn("1");
			return seatLocationType;
		}



		public OrderModel createOrderModel(final CurrencyModel currencyModel)
		{
			final OrderModel order = new OrderModel();
			order.setCurrency(currencyModel);
			order.setCode("00001");
			order.setBookingJourneyType(BookingJourneyType.BOOKING_TRANSPORT_ONLY);
			order.setTotalPrice(310d);
			order.setCurrency(currencyModel);
			order.setUser(createUserModel(currencyModel));
			order.setGuid("TEST_UUID");
			order.setDate(new Date());
			order.setNet(Boolean.TRUE);
			order.setPaymentInfo(createPaymentInfoModel(order));
			order.setSelectedAccommodations(Collections.emptyList());
			final ProductModel product1 = createProductModel("product1", ProductType.FEE);
			final RoomRateProductModel product2 = createRoomRateProductModel("product2", ProductType.ACCOMMODATION);
			final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);

			final Date currentDate = new Date();

			final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering("Outbound", currentDate,
					TravelDateUtils.addDays(currentDate, 2));
			final TransportOfferingModel transportOfferingModelInbound = createTransportOffering("Inbound",
					TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));
			final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
			final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

			final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
			final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


			final TravelRouteModel travelRoute = createTravelRoute(origin, destination);
			final AccommodationModel accommodationModel1 = createAccommodationModel("TEST_ACCOMMODATION_CODE");
			final AccommodationOfferingModel accommodationOfferingModel1 = createAccommodationOfferingModel(
					"TEST_ACCOMMODATION_OFFERING_CODE");

			final RatePlanModel ratePlan = createRatePlanModel("TEST_RATE_PLAN_CODE", accommodationModel1);

			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(
					createOrderEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), true,
					AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
					createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute), null,
					null, product2, 1, 100d, 100d, 1, order, "TEST_ITEM_ID_TYPE");
			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(
					createOrderEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), true,
					AmendStatus.NEW, OrderEntryType.TRANSPORT,
					createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), null,
					null, product1, 1, 10d, 10d, 2, order, "TEST_ITEM_ID_TYPE");
			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(
					createOrderEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), true,
					AmendStatus.NEW, OrderEntryType.TRANSPORT,
					createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute), null,
					null, product3, 1, 100d, 100d, 3, order, "TEST_ITEM_ID_TYPE");
			order.setEntries(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()));
			order.setPaymentInfo(createPaymentInfoModel(createAddressModel()));

			Mockito.when(ndcOrderService.populateOrderEntry(Matchers.any(OrderModel.class), Matchers.any(ProductModel.class),
					Matchers.anyObject(), Matchers.anyInt(), Matchers.anyString(), Matchers.anyList(), Matchers.anyList(),
					Matchers.anyString(), Matchers.anyInt(), Matchers.anyInt())).thenReturn((OrderEntryModel) entry1);
			return order;
		}

		private OrderEntryModel createOrderEntryModel(final List<TaxValue> taxValues)
		{
			return new OrderEntryModel()
			{
				@Override
				public Collection<TaxValue> getTaxValues()
				{
					return taxValues;
				}

			};
		}


		private AbstractOrderEntryModel createAbstractOrderEntryModel(final AbstractOrderEntryModel abstractOrderEntryModel,
				final boolean isActive, final AmendStatus amendStatus, final OrderEntryType orderEntryType,
				final TravelOrderEntryInfoModel travelOrderEntryInfoModel,
				final AccommodationOrderEntryInfoModel accommodationOrderEntryInfo,
				final AbstractOrderEntryGroupModel orderEntryGroup, final ProductModel product, final int quantity,
				final double basePrice, final double totalPrice, final int entryNumber, final AbstractOrderModel order,
				final String ndcOfferItemID)
		{
			final UnitModel unitModel = new UnitModel();
			unitModel.setUnitType("awsome" + entryNumber);
			unitModel.setCode("goblins" + entryNumber);
			abstractOrderEntryModel.setUnit(unitModel);
			abstractOrderEntryModel.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
			abstractOrderEntryModel.setActive(isActive);
			abstractOrderEntryModel.setProduct(product);
			abstractOrderEntryModel.setType(orderEntryType);
			abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
			abstractOrderEntryModel.setBasePrice(basePrice);
			abstractOrderEntryModel.setTotalPrice(totalPrice);
			abstractOrderEntryModel.setAmendStatus(amendStatus);
			abstractOrderEntryModel.setAccommodationOrderEntryInfo(accommodationOrderEntryInfo);
			abstractOrderEntryModel.setEntryGroup(orderEntryGroup);
			abstractOrderEntryModel.setEntryNumber(entryNumber);
			abstractOrderEntryModel.setOrder(order);
			abstractOrderEntryModel.setNdcOfferItemID(ndcOfferItemID);
			return abstractOrderEntryModel;
		}

		private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(
				final AccommodationModel accommodationModel, final AccommodationOfferingModel accommodationOfferingModel,
				final RatePlanModel ratePlan, final List<AbstractOrderEntryModel> entries, final int roomStayRefNum)
		{
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
			accommodationOrderEntryGroupModel.setAccommodation(accommodationModel);
			accommodationOrderEntryGroupModel.setAccommodationOffering(accommodationOfferingModel);
			accommodationOrderEntryGroupModel.setRatePlan(ratePlan);
			accommodationOrderEntryGroupModel.setRoomStayRefNumber(roomStayRefNum);
			accommodationOrderEntryGroupModel.setGuestCounts(Collections.singletonList(createGuestCountModel()));
			accommodationOrderEntryGroupModel.setEntries(entries);
			return accommodationOrderEntryGroupModel;


		}

		private GuestCountModel createGuestCountModel()
		{
			final GuestCountModel guestCountModel = new GuestCountModel();
			guestCountModel.setQuantity(1);
			guestCountModel.setPassengerType(createPassengerTypeModel());
			return guestCountModel;
		}

		private PassengerTypeModel createPassengerTypeModel()
		{
			final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
			passengerTypeModel.setCode("adult");
			passengerTypeModel.setName("Adult");
			passengerTypeModel.setMinAge(16);
			return passengerTypeModel;
		}

		private AccommodationOrderEntryInfoModel createAccommodationOrderEntryInfoModel()
		{
			final AccommodationOrderEntryInfoModel accommodationOrderEntryInfoModel = new AccommodationOrderEntryInfoModel();
			return accommodationOrderEntryInfoModel;
		}

		private AccommodationModel createAccommodationModel(final String code)
		{
			final AccommodationModel accommodationModel = new AccommodationModel();
			accommodationModel.setCode(code);
			return accommodationModel;
		}

		private AccommodationOfferingModel createAccommodationOfferingModel(final String code)
		{
			final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
			accommodationOfferingModel.setCode(code);
			final VendorModel vendor = new VendorModel();
			vendor.setCode("AccommodatoinVendorCode");
			accommodationOfferingModel.setVendor(vendor);
			return accommodationOfferingModel;
		}

		private RatePlanModel createRatePlanModel(final String code, final AccommodationModel accommodation)
		{
			final RatePlanModel ratePlan = new RatePlanModel();
			ratePlan.setCode(code);
			ratePlan.setAccommodation(Collections.singletonList(accommodation));
			return ratePlan;
		}

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = new ProductModel();
			product.setCode(code);
			product.setProductType(productType);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			return product;
		}

		private RoomRateProductModel createRoomRateProductModel(final String code, final ProductType productType)
		{
			final RoomRateProductModel product = new RoomRateProductModel();
			product.setCode(code);
			product.setProductType(productType);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			return product;
		}


		private TravelOrderEntryInfoModel createTravelOrderEntryInfoModel(final int originDestinationRefNum,
				final List<TransportOfferingModel> transportOfferings, final TravelRouteModel travelRoute)
		{
			final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
			travelOrderEntryInfoModel.setOriginDestinationRefNumber(originDestinationRefNum);
			travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);
			travelOrderEntryInfoModel.setTravelRoute(travelRoute);
			travelOrderEntryInfoModel
					.setTravellers(Collections.singletonList(createTravellerModel(createTravellerInfoModel(originDestinationRefNum))));
			Mockito.when(modelService.clone(travelOrderEntryInfoModel, TravelOrderEntryInfoModel.class))
					.thenReturn(travelOrderEntryInfoModel);
			return travelOrderEntryInfoModel;
		}

		private TravelRouteModel createTravelRoute(final TransportFacilityModel origin, final TransportFacilityModel destination)
		{
			final TravelRouteModel travelRoute = new TravelRouteModel();
			travelRoute.setCode(origin + "_" + destination);
			travelRoute.setOrigin(origin);
			travelRoute.setDestination(destination);
			return travelRoute;
		}

		private TransportFacilityModel createTransportFacilityModel(final String code, final LocationModel locationModel)
		{
			final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
			transportFacilityModel.setCode(code);
			transportFacilityModel.setLocation(locationModel);
			return transportFacilityModel;
		}

		private LocationModel createLocationModel(final String code)
		{
			final LocationModel location = new LocationModel();
			location.setCode(code);
			return location;
		}

		private TransportOfferingModel createTransportOffering(final String code, final Date departureTime, final Date arrivalTime)
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel();
			transportOffering.setDepartureTime(departureTime);
			transportOffering.setArrivalTime(arrivalTime);
			transportOffering.setCode(code);
			final VendorModel vendor = new VendorModel();
			vendor.setCode("vendorCode");
			transportOffering.setVendor(vendor);
			return transportOffering;
		}

		private TravellerModel createTravellerModel(final TravellerInfoModel travellerInfoModel)
		{
			final TravellerModel traveller = new TravellerModel();
			traveller.setInfo(travellerInfoModel);
			traveller.setUid("TEST_UID");
			traveller.setSavedTravellerUid("TEST_SAVED_UID");
			traveller.setLabel("Adult");
			Mockito.when(modelService.clone(traveller, TravellerModel.class)).thenReturn(traveller);
			Mockito.when(travellerService.getExistingTraveller("TEST_UID", "00001")).thenReturn(traveller);
			return traveller;
		}

		private PassengerInformationModel createTravellerInfoModel(final int originDestinationRefNum)
		{
			final PassengerInformationModel travellerInfo = new PassengerInformationModel();
			travellerInfo.setFirstName("Integration");
			final TitleModel title = new TitleModel();
			title.setCode("Test Mr." + originDestinationRefNum);
			travellerInfo.setTitle(title);
			travellerInfo.setGender("Male");
			travellerInfo.setSurname("Test");
			return travellerInfo;
		}

		private PaymentInfoModel createPaymentInfoModel(final AbstractOrderModel order)
		{
			final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
			paymentInfo.setOwner(order);
			paymentInfo.setBank("MeineBank");
			paymentInfo.setUser(order.getUser());
			paymentInfo.setAccountNumber("34434");
			paymentInfo.setBankIDNumber("1111112");
			paymentInfo.setBaOwner("Ich");
			paymentInfo.setCode("testPaymentInfo1");
			return paymentInfo;
		}

		private TaxValue createTaxValue(final String code, final double value, final boolean absolute, final double appliedValue,
				final String currencyCode)
		{
			final TaxValue tax = new TaxValue(code, value, absolute, appliedValue, currencyCode);
			return tax;
		}


		public UserModel createUserModel(final CurrencyModel currencyModel)
		{
			final UserModel userModel = new UserModel();
			userModel.setSessionCurrency(currencyModel);
			return userModel;
		}

		private PaymentInfoModel createPaymentInfoModel(final AddressModel address)
		{
			final PaymentInfoModel payInfo = new PaymentInfoModel();
			payInfo.setBillingAddress(address);
			return payInfo;
		}

		private AddressModel createAddressModel()
		{
			final AddressModel add = new AddressModel();
			return add;
		}
	}
}