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
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.PK;
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
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.ProximityItemType;
import de.hybris.platform.travelservices.enums.ProximityRelativePosition;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.ProximityItemModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductAvailabilityOrderValidationStrategyTest
{
	@InjectMocks
	ProductAvailabilityOrderValidationStrategy strategy;

	@Mock
	private BookingService bookingService;

	@Mock
	private ProductService productService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private TravelCommerceStockService travelCommerceStockService;

	private final TestSetup testSetup = new TestSetup();

	@Before
	public void setUp() throws NDCOrderException, CalculationException
	{
		Mockito.when(productService.getProductForCode(Matchers.anyString()))
				.thenReturn(testSetup.createProductModel("PRIORIRTYCHECKIN", ProductType.ANCILLARY));

	}

	@Test
	public void testValidateAmendOrder() throws NDCOrderException
	{
		Mockito.when(travelCommerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.anyList()))
				.thenReturn(10l);
		final OrderModel order = testSetup.createOrderModel(testSetup.createCurrencyModel("EUR"), true);
		final List<TravellerModel> travellers = new ArrayList<>();
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		order.getEntries().forEach(entry -> {
			entry.getTravelOrderEntryInfo().getTravellers().forEach(traveller -> {
				if (!travellers.contains(traveller))
				{
					travellers.add(traveller);
				}
			});
			entry.getTravelOrderEntryInfo().getTransportOfferings().forEach(transportOffering -> {
				if (!transportOfferings.contains(transportOffering))
				{
					transportOfferings.add(transportOffering);
				}
			});
		});

		Assert.assertTrue(strategy.validateAmendOrder(order, "TEST_PRODUCT", 1, "TEST_UID", Collections.singletonList("Inbound"),
				"TEST_ROUTE_CODE"));
	}

	@Test
	public void testValidateAmendOrderForOrderWithOriginalOrder() throws NDCOrderException
	{
		Mockito.when(travelCommerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.anyList()))
				.thenReturn(10l);
		Mockito.when(bookingService.getProductQuantityInOrderForTransportOffering(Matchers.anyString(),
				Matchers.any(ProductModel.class), Matchers.any(TransportOfferingModel.class))).thenReturn(2l);
		final OrderModel order = testSetup.createOrderModel(testSetup.createCurrencyModel("EUR"), true);
		order.setOriginalOrder(order);
		final List<TravellerModel> travellers = new ArrayList<>();
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		order.getEntries().forEach(entry -> {
			entry.getTravelOrderEntryInfo().getTravellers().forEach(traveller -> {
				if (!travellers.contains(traveller))
				{
					travellers.add(traveller);
				}
			});
			entry.getTravelOrderEntryInfo().getTransportOfferings().forEach(transportOffering -> {
				if (!transportOfferings.contains(transportOffering))
				{
					transportOfferings.add(transportOffering);
				}
			});
		});

		Assert.assertTrue(strategy.validateAmendOrder(order, "TEST_PRODUCT", 1, "TEST_UID", Collections.singletonList("Inbound"),
				"TEST_ROUTE_CODE"));
	}

	@Test
	public void testValidateAmendOrderForUnAvailableStock() throws NDCOrderException
	{
		Mockito.when(travelCommerceStockService.getStockLevel(Matchers.any(ProductModel.class), Matchers.anyList())).thenReturn(0l);
		final OrderModel order = testSetup.createOrderModel(testSetup.createCurrencyModel("EUR"), true);
		final List<TravellerModel> travellers = new ArrayList<>();
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		order.getEntries().forEach(entry -> {
			entry.getTravelOrderEntryInfo().getTravellers().forEach(traveller -> {
				if (!travellers.contains(traveller))
				{
					travellers.add(traveller);
				}
			});
			entry.getTravelOrderEntryInfo().getTransportOfferings().forEach(transportOffering -> {
				if (!transportOfferings.contains(transportOffering))
				{
					transportOfferings.add(transportOffering);
				}
			});
		});

		Assert.assertFalse(strategy.validateAmendOrder(order, "TEST_PRODUCT", 1, "TEST_UID", Collections.singletonList("Inbound"),
				"TEST_ROUTE_CODE"));
	}

	private class TestSetup
	{
		private SelectedAccommodationModel createSelectedAccommodationModel(final TravelOrderEntryInfoModel travelOrderEntryInfo)
		{
			final SelectedAccommodationModel selAccModel = new SelectedAccommodationModel();
			selAccModel.setConfiguredAccommodation(createConfiguredAccommodationModel(true, "1A", ConfiguredAccommodationType.SEAT));
			selAccModel.setTransportOffering(travelOrderEntryInfo.getTransportOfferings().iterator().next());
			selAccModel.setTraveller(travelOrderEntryInfo.getTravellers().iterator().next());
			return selAccModel;
		}

		private ConfiguredAccommodationModel createConfiguredAccommodationModel(final boolean isSeat, final String identifier,
				final ConfiguredAccommodationType type)
		{
			final ConfiguredAccommodationModel seatConfigModel1 = new ConfiguredAccommodationModel();
			seatConfigModel1.setType(type);
			if (isSeat)
			{
				//Seats
				seatConfigModel1.setIdentifier(identifier);
				seatConfigModel1.setProduct(new AccommodationModel());
				final ProximityItemModel proximity = new ProximityItemModel();
				proximity.setType(ProximityItemType.GALLEY);
				proximity.setRelativePosition(ProximityRelativePosition.LEFT);
				seatConfigModel1.setProximityItem(Stream.of(proximity).collect(Collectors.toList()));
			}
			return seatConfigModel1;
		}

		public ReservationData createReservationData()
		{
			final ReservationData reservationData = new ReservationData();
			reservationData.setTotalToPay(createPriceData(0d));

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

		public OrderModel createOrderModel(final CurrencyModel currencyModel, final boolean isActive)
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
			final ProductModel product1 = createProductModel("product1", ProductType.FEE);
			final RoomRateProductModel product2 = createRoomRateProductModel("product2", ProductType.ACCOMMODATION);
			final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);

			final Date currentDate = new Date();

			final TransportOfferingModel transportOfferingModelInbound = createTransportOffering("Inbound",
					TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));
			final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
			final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

			final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
			final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);
			final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

			final List<SelectedAccommodationModel> selectedAccommodationModel = new ArrayList<>();

			final TravelOrderEntryInfoModel travellerInfoModel = createTravelOrderEntryInfoModel(0,
					Collections.singletonList(transportOfferingModelInbound), travelRoute);

			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(
					createOrderEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), isActive,
					AmendStatus.NEW, OrderEntryType.ACCOMMODATION, travellerInfoModel, null, null, product2, 1, 100d, 100d, 1, order,
					"TEST_ITEM_ID_TYPE");
			selectedAccommodationModel.add(testSetup.createSelectedAccommodationModel(entry1.getTravelOrderEntryInfo()));
			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(
					createOrderEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), isActive,
					AmendStatus.NEW, OrderEntryType.TRANSPORT, travellerInfoModel, null, null, product1, 1, 10d, 10d, 2, order,
					"TEST_ITEM_ID_TYPE");
			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(
					createOrderEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), isActive,
					AmendStatus.NEW, OrderEntryType.TRANSPORT, travellerInfoModel, null, null, product3, 1, 100d, 100d, 3, order,
					"TEST_ITEM_ID_TYPE");
			order.setEntries(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()));
			order.setPaymentInfo(createPaymentInfoModel(createAddressModel()));
			order.setSelectedAccommodations(selectedAccommodationModel);
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

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = new ProductModel()
			{
				@Override
				public PK getPk()
				{
					return de.hybris.platform.core.PK.fromLong(00001l);
				}
			};
			product.setCode(code);
			product.setProductType(productType);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			if (ProductType.ANCILLARY.equals(productType))
			{
				product.setSupercategories(Collections.singletonList(createCategoryModel(code)));
			}
			return product;
		}

		private CategoryModel createCategoryModel(final String code)
		{
			final CategoryModel category = new CategoryModel();
			category.setCode(code);
			return category;
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
			Mockito.when(transportOfferingService.getTransportOffering(code)).thenReturn(transportOffering);
			return transportOffering;
		}

		private TravellerModel createTravellerModel(final TravellerInfoModel travellerInfoModel)
		{
			final TravellerModel traveller = new TravellerModel();
			traveller.setInfo(travellerInfoModel);
			traveller.setUid("TEST_UID");
			traveller.setSavedTravellerUid("TEST_SAVED_UID");
			traveller.setLabel("Adult");

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
