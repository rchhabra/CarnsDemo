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

package de.hybris.platform.travelservices.order.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TripType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
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
import de.hybris.platform.travelservices.order.daos.TravelCartDao;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite {@link DefaultTravelCartService}.
 */
@IntegrationTest
public class DefaultTravelCartServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	public DefaultTravelCartService travelCartService;

	@Resource
	private CartService cartService;

	@Resource
	public CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

	@Resource
	public CustomerAccountService customerAccountService;

	@Resource
	public BaseStoreService baseStoreService;

	@Resource
	public KeyGenerator orderCodeGenerator;

	@Resource
	public UserService userService;

	@Resource
	public TravelCartDao travelCartDao;

	@Resource
	public CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Resource
	public CommonI18NService commonI18NService;

	@Resource
	public BookingService bookingService;

	@Resource
	public TravelCommerceStockService commerceStockService;

	@Resource
	public StoreSessionService storeSessionService;

	@Resource
	public SessionService sessionService;

	@Resource
	public TimeService timeService;

	@Resource
	public ModelService modelService;

	@Resource
	public BaseSiteService baseSiteService;

	@Resource
	public EnumerationService enumerationService;

	@Resource
	private GuidKeyGenerator guidKeyGenerator;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CatalogVersionService catalogVersionService;

	public static final String CURRENCY_SESSION_ATTR_KEY = "currency".intern();
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESTORE_UID = "testStore";

	private CurrencyModel currencyModel;
	private UserModel user;

	private static boolean isSet = Boolean.FALSE;

	@Before
	public void setUp() throws ImpExException
	{
		if (Objects.isNull(baseSiteService.getCurrentBaseSite()))
		{
			importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
			baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
			final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
			Assert.assertNotNull(catalogVersionModel);
			catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));

			user = createUser("user");

			userService.setCurrentUser(user);
		}
		travelCartService.setTravelCommerceStockService(commerceStockService);
		if (Objects.isNull(currencyModel) || Objects.isNull(commonI18NService.getCurrentCurrency()))
		{
			currencyModel = Objects.isNull(commonI18NService.getCurrentCurrency()) ? modelService.create(CurrencyModel.class)
					: commonI18NService.getCurrentCurrency();

			final CurrencyModel optCurrency = flexibleSearchService
					.<CurrencyModel> search("SELECT {PK} FROM {Currency} WHERE {isocode}='EUR'").getResult().get(0);
			if (optCurrency != null)
			{
				currencyModel = optCurrency;
			}
			if (!currencyModel.getIsocode().equals("EUR".intern()))
			{
				currencyModel.setIsocode("EUR".intern());
				modelService.save(currencyModel);
				commonI18NService.setCurrentCurrency(currencyModel);
				sessionService.setAttribute(CURRENCY_SESSION_ATTR_KEY, "EUR".intern());
			}

		}

	}


	private UserModel createUser(final String uid)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(uid);
		user.setName(uid);
		modelService.save(user);
		return user;
	}

	@Test
	public void testRemoveDeliveryAddress()
	{
		travelCartService.removeDeliveryAddress();

		Assert.assertNull(travelCartService.getSessionCart().getDeliveryAddress());
	}

	@Test
	public void testGetSessionCartForTravelBooking()
	{

		final TestSetup testSetup = new TestSetup();

		final CartModel cart = testSetup.createCartModel();
		Assert.assertTrue(Objects.nonNull(travelCartService.getSessionCart()));
		Assert.assertEquals(cart, travelCartService.getSessionCart());
		Assert.assertEquals(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION,
				travelCartService.getSessionCart().getBookingJourneyType());
	}

	private class TestSetup
	{
		private OrderEntryModel createOrderEntryModel(final List<TaxValue> taxValues)
		{
			return modelService.create(OrderEntryModel.class);
		}

		private CartEntryModel createCartEntryModel(final List<TaxValue> taxValues)
		{
			return modelService.create(CartEntryModel.class);
		}

		private AbstractOrderEntryModel createAbstractOrderEntryModel(final AbstractOrderEntryModel abstractOrderEntryModel,
				final boolean isActive, final AmendStatus amendStatus, final OrderEntryType orderEntryType,
				final TravelOrderEntryInfoModel travelOrderEntryInfoModel,
				final AccommodationOrderEntryInfoModel accommodationOrderEntryInfo,
				final AbstractOrderEntryGroupModel orderEntryGroup, final ProductModel product, final int quantity,
				final double basePrice, final double totalPrice, final int entryNumber, final AbstractOrderModel order)
		{
			final UnitModel unitModel = modelService.create(UnitModel.class);
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
			return abstractOrderEntryModel;
		}

		private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(
				final AccommodationModel accommodationModel, final AccommodationOfferingModel accommodationOfferingModel,
				final RatePlanModel ratePlan, final List<AbstractOrderEntryModel> entries, final int roomStayRefNum)
		{
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = modelService
					.create(AccommodationOrderEntryGroupModel.class);
			accommodationOrderEntryGroupModel.setAccommodation(accommodationModel);
			accommodationOrderEntryGroupModel.setAccommodationOffering(accommodationOfferingModel);
			accommodationOrderEntryGroupModel.setRatePlan(ratePlan);
			accommodationOrderEntryGroupModel.setRoomStayRefNumber(roomStayRefNum);
			accommodationOrderEntryGroupModel.setGuestCounts(Collections.singletonList(createGuestCountModel()));
			accommodationOrderEntryGroupModel.setEntries(entries);
			entries.forEach(entry -> modelService.save(entry));
			modelService.save(accommodationOrderEntryGroupModel);
			return accommodationOrderEntryGroupModel;


		}

		private GuestCountModel createGuestCountModel()
		{
			final GuestCountModel guestCountModel = modelService.create(GuestCountModel.class);
			guestCountModel.setQuantity(1);
			guestCountModel.setPassengerType(createPassengerTypeModel());
			return guestCountModel;
		}

		private PassengerTypeModel createPassengerTypeModel()
		{
			final PassengerTypeModel passengerTypeModel = modelService.create(PassengerTypeModel.class);
			passengerTypeModel.setCode("adult");
			passengerTypeModel.setName("Adult");
			passengerTypeModel.setMinAge(16);
			return passengerTypeModel;
		}

		private AccommodationOrderEntryInfoModel createAccommodationOrderEntryInfoModel()
		{
			final AccommodationOrderEntryInfoModel accommodationOrderEntryInfoModel = modelService
					.create(AccommodationOrderEntryInfoModel.class);
			modelService.save(accommodationOrderEntryInfoModel);
			return accommodationOrderEntryInfoModel;
		}

		private AccommodationModel createAccommodationModel(final String code)
		{
			final AccommodationModel accommodationModel = modelService.create(AccommodationModel.class);
			accommodationModel.setCode(code);
			modelService.save(accommodationModel);
			return accommodationModel;
		}

		private AccommodationOfferingModel createAccommodationOfferingModel(final String code)
		{
			final AccommodationOfferingModel accommodationOfferingModel = modelService.create(AccommodationOfferingModel.class);
			accommodationOfferingModel.setCode(code);
			final VendorModel vendor = modelService.create(VendorModel.class);
			vendor.setCode("AccommodatoinVendorCode");
			modelService.save(vendor);
			accommodationOfferingModel.setVendor(vendor);
			modelService.save(accommodationOfferingModel);
			return accommodationOfferingModel;
		}

		private RatePlanModel createRatePlanModel(final String code, final AccommodationModel accommodation)
		{
			final RatePlanModel ratePlan = modelService.create(RatePlanModel.class);
			ratePlan.setCode(code);
			ratePlan.setAccommodation(Collections.singletonList(accommodation));
			modelService.save(ratePlan);
			return ratePlan;
		}

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = modelService.create(ProductModel.class);
			product.setCode(code);
			product.setProductType(productType);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			product.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
			modelService.save(product);
			return product;
		}

		private RoomRateProductModel createRoomRateProductModel(final String code, final ProductType productType)
		{
			final RoomRateProductModel product = modelService.create(RoomRateProductModel.class);
			product.setCode(code);
			product.setProductType(productType);
			product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
			product.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
			modelService.save(product);
			return product;
		}


		private TravelOrderEntryInfoModel createTravelOrderEntryInfoModel(final int originDestinationRefNum,
				final List<TransportOfferingModel> transportOfferings, final TravelRouteModel travelRoute)
		{
			final TravelOrderEntryInfoModel travelOrderEntryInfoModel = modelService.create(TravelOrderEntryInfoModel.class);
			travelOrderEntryInfoModel.setOriginDestinationRefNumber(originDestinationRefNum);
			travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);
			travelOrderEntryInfoModel.setTravelRoute(travelRoute);
			travelOrderEntryInfoModel
					.setTravellers(Collections.singletonList(createTravellerModel(createTravellerInfoModel(originDestinationRefNum))));
			modelService.save(travelOrderEntryInfoModel);
			return travelOrderEntryInfoModel;
		}

		private TravelRouteModel createTravelRoute(final TransportFacilityModel origin, final TransportFacilityModel destination)
		{
			final TravelRouteModel travelRoute = modelService.create(TravelRouteModel.class);
			travelRoute.setCode(origin + "_" + destination);
			travelRoute.setOrigin(origin);
			travelRoute.setDestination(destination);
			modelService.save(travelRoute);
			return travelRoute;
		}

		private TransportFacilityModel createTransportFacilityModel(final String code, final LocationModel locationModel)
		{
			final TransportFacilityModel transportFacilityModel = modelService.create(TransportFacilityModel.class);
			transportFacilityModel.setCode(code);
			transportFacilityModel.setLocation(locationModel);
			modelService.save(transportFacilityModel);
			return transportFacilityModel;
		}

		private LocationModel createLocationModel(final String code)
		{
			final LocationModel location = modelService.create(LocationModel.class);
			location.setCode(code);
			modelService.save(location);
			return location;
		}

		private TransportOfferingModel createTransportOffering(final String code, final Date departureTime, final Date arrivalTime)
		{
			final TransportOfferingModel transportOffering = modelService.create(TransportOfferingModel.class);
			transportOffering.setDepartureTime(departureTime);
			transportOffering.setArrivalTime(arrivalTime);
			transportOffering.setCode(code);
			final VendorModel vendor = modelService.create(VendorModel.class);
			vendor.setCode("vendorCode");
			modelService.save(vendor);
			transportOffering.setVendor(vendor);
			modelService.save(transportOffering);
			return transportOffering;
		}

		private TravellerModel createTravellerModel(final TravellerInfoModel travellerInfoModel)
		{
			final TravellerModel traveller = modelService.create(TravellerModel.class);
			traveller.setInfo(travellerInfoModel);
			traveller.setUid(guidKeyGenerator.generate().toString());
			traveller.setSavedTravellerUid(guidKeyGenerator.generate().toString());
			traveller.setLabel("Adult");
			modelService.save(traveller);
			return traveller;
		}

		private PassengerInformationModel createTravellerInfoModel(final int originDestinationRefNum)
		{
			final PassengerInformationModel travellerInfo = modelService.create(PassengerInformationModel.class);
			travellerInfo.setFirstName("Integration");
			final TitleModel title = modelService.create(TitleModel.class);
			title.setCode("Test Mr." + originDestinationRefNum);
			title.setName("Test Mr." + originDestinationRefNum);
			travellerInfo.setTitle(title);
			travellerInfo.setGender("Male");
			travellerInfo.setSurname("Test");
			modelService.save(travellerInfo);
			return travellerInfo;
		}

		private PaymentInfoModel createPaymentInfoModel(final AbstractOrderModel order)
		{
			final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
			paymentInfo.setOwner(order);
			paymentInfo.setBank("MeineBank");
			paymentInfo.setUser(user);
			paymentInfo.setAccountNumber("34434");
			paymentInfo.setBankIDNumber("1111112");
			paymentInfo.setBaOwner("Ich");
			paymentInfo.setCode("testPaymentInfo1");
			modelService.save(paymentInfo);
			return paymentInfo;
		}

		private TaxValue createTaxValue(final String code, final double value, final boolean absolute, final double appliedValue,
				final String currencyCode)
		{
			final TaxValue tax = new TaxValue(code, value, absolute, appliedValue, currencyCode);
			return tax;
		}

		private CartModel createCartModel()
		{
			final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TEST_BASESTORE_UID);
			final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();

			baseStore.setExternalTaxEnabled(Boolean.FALSE);
			baseStore.setUid(TEST_BASESTORE_UID);
			final CartModel cart = modelService.create(CartModel.class);
			cart.setCode("00001");
			cart.setBookingJourneyType(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
			cart.setTripType(TripType.RETURN);
			cart.setTotalPrice(310d);
			cart.setCurrency(currencyModel);
			cart.setUser(user);
			cart.setSite(baseSite);
			cart.setStore(baseStore);
			cart.setGuid(guidKeyGenerator.generate().toString());
			cart.setDate(new Date());
			cart.setNet(Boolean.TRUE);
			cart.setPaymentInfo(createPaymentInfoModel(cart));
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

			AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel1 = null;
			final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(
					createCartEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), true,
					AmendStatus.NEW, OrderEntryType.ACCOMMODATION, null, createAccommodationOrderEntryInfoModel(),
					accommodationOrderEntryGroupModel1, product2, 1, 100d, 100d, 1, cart);
			accommodationOrderEntryGroupModel1 = createAccommodationOrderEntryGroupModel(accommodationModel1,
					accommodationOfferingModel1, ratePlan, Collections.singletonList(entry1), 0);
			final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(
					createCartEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), true,
					AmendStatus.NEW, OrderEntryType.TRANSPORT,
					createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), null,
					null, product1, 1, 10d, 10d, 2, cart);
			final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(
					createCartEntryModel(Collections.singletonList(createTaxValue("YQ", 10d, true, 10d, "EUR"))), true,
					AmendStatus.NEW, OrderEntryType.TRANSPORT,
					createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute), null,
					null, product3, 1, 100d, 100d, 3, cart);
			cart.setEntries(Stream.of(entry1, entry2, entry3).collect(Collectors.toList()));
			modelService.save(cart);
			travelCartService.setSessionCart(cart);
			return cart;
		}

	}
}