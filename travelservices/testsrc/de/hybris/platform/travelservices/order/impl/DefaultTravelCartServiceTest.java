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

package de.hybris.platform.travelservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TripType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.daos.TravelCartDao;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.payment.PaymentOptionCreationStrategy;
import de.hybris.platform.travelservices.strategies.payment.impl.AccommodationPayNowPaymentOptionStrategy;
import de.hybris.platform.travelservices.strategies.payment.impl.PartiallyDelayedPaymentOptionStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCartServiceTest
{
	@InjectMocks
	DefaultTravelCartService defaultTravelCartService = new DefaultTravelCartService()
	{
		@Override
		protected CartModel internalGetSessionCart()
		{
			final CartModel cart = new CartModel();
			final OrderModel order = new OrderModel();
			order.setCode("0001");
			cart.setOriginalOrder(order);
			return cart;
		}
	};

	@InjectMocks
	DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
	{
		@Override
		protected CartModel internalGetSessionCart()
		{
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			orderEntryInfo.setOriginDestinationRefNumber(1);
			final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
			abstractOrderEntryModel.setActive(true);
			abstractOrderEntryModel.setType(OrderEntryType.TRANSPORT);
			abstractOrderEntryModel.setTravelOrderEntryInfo(orderEntryInfo);
			final CartModel cart = new CartModel();
			cart.setEntries(Collections.singletonList(abstractOrderEntryModel));

			return cart;
		}
	};

	@Mock
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private KeyGenerator orderCodeGenerator;

	@Mock
	private UserService userService;

	@Mock
	private TravelCartDao travelCartDao;

	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CommonI18NService commonI18NService;

	@Mock
	private BookingService bookingService;

	@Mock
	private TravelCommerceStockService travelCommerceStockService;

	@Mock
	private StoreSessionService storeSessionService;

	@Mock
	private ModelService modelService;

	@Mock
	private SessionService sessionService;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private TimeService timeService;

	@Mock
	private AccommodationPayNowPaymentOptionStrategy accommodationPayNowPaymentOptionStrategy;

	@Mock
	private PartiallyDelayedPaymentOptionStrategy partiallyDelayedPaymentOptionStrategy;

	@Before
	public void setUp()
	{
		defaultTravelCartService.setTimeService(timeService);
	}

	@Test
	public void testCreateCartFromOrderCurrentCurrEqOrderCurr()
	{
		final String orderCode = null;
		final String guid = null;
		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModel = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final CartModel cartModel = new CartModel();
		final TravelOrderEntryInfoModel travelOrderEntryInfo1=createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModel), travelRoute);

		final AbstractOrderEntryModel cartEntry1=createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				travelOrderEntryInfo1,
				new ProductModel(), 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel cartEntry2=createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION, null, new ProductModel(), 1, 100d,
				100d, Collections.emptyList());
		cartModel.setEntries(Arrays.asList(cartEntry1, cartEntry2));

		final AccommodationModel accommodationModel1 = createAccommodationModel("TEST_ACCOMMODATION_CODE");
		final AccommodationOfferingModel accommodationOfferingModel1 = createAccommodationOfferingModel(
				"TEST_ACCOMMODATION_OFFERING_CODE");
		final RatePlanModel ratePlan = createRatePlanModel("TEST_RATE_PLAN_CODE");
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel1 = createAccommodationOrderEntryGroupModel(
				accommodationModel1, accommodationOfferingModel1, ratePlan,
				Stream.of(createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION, null, new ProductModel(),
						1, 100d, 100d, Collections.emptyList())).collect(Collectors.toList()));
		Mockito.when(bookingService.getAccommodationOrderEntryGroups(orderModel))
				.thenReturn(Collections.singletonList(accommodationOrderEntryGroupModel1));
		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);

		Mockito.when(modelService.clone(Mockito.any(TravelOrderEntryInfoModel.class), Mockito.eq(TravelOrderEntryInfoModel.class)))
				.thenReturn(travelOrderEntryInfo1);

		final SelectedAccommodationModel selectedAccommodation = new SelectedAccommodationModel();
		final List<SelectedAccommodationModel> selectedAccommodations = Collections.singletonList(selectedAccommodation);
		cartModel.setSelectedAccommodations(selectedAccommodations);

		Mockito.when(modelService.clone(selectedAccommodation, SelectedAccommodationModel.class)).thenReturn(selectedAccommodation);

		final CartModel cartModelRtr = defaultTravelCartService.createCartFromOrder(orderCode, guid);
		Assert.assertNotNull(cartModelRtr);
		Assert.assertEquals(cartModel, cartModelRtr);
	}

	@Test
	public void testCreateCartFromOrderCurrentCurrNotEqOrderCurr()
	{
		final String orderCode = null;
		final String guid = null;
		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("INR");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));

		final CartModel cartModel = new CartModel();

		final SelectedAccommodationModel selectedAccommodation = new SelectedAccommodationModel();
		final List<SelectedAccommodationModel> selectedAccommodations = Collections.singletonList(selectedAccommodation);
		cartModel.setSelectedAccommodations(selectedAccommodations);

		cartModel.setEntries(Arrays.asList(new AbstractOrderEntryModel()));

		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());
		Mockito.when(modelService.clone(selectedAccommodation, SelectedAccommodationModel.class)).thenReturn(selectedAccommodation);

		final CartModel cartModelRtr = defaultTravelCartService.createCartFromOrder(orderCode, guid);
		Assert.assertNotNull(cartModelRtr);
		Assert.assertEquals(cartModel, cartModelRtr);
	}

	@Test
	public void testCancelTravellerWithSingleTravellerEntries()
	{
		final String orderCode = null;
		final String guid = null;
		final String cancelledTravellerCode = "LTN_CDG";
		final String cancelledTravellerUid = null;

		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));

		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravellerModel traveller = new TravellerModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		traveller.setLabel("LTN_CDG");
		traveller.setUid("travellerUID");
		orderEntryInfo.setTravellers(Arrays.asList(traveller));
		cartModel.setEntries(Arrays.asList(entry));
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);

		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());
		Mockito.when(modelService.clone(entry.getTravelOrderEntryInfo(), TravelOrderEntryInfoModel.class))
				.thenReturn(orderEntryInfo);
		Mockito.when(modelService.clone(traveller, TravellerModel.class)).thenReturn(traveller);

		final SelectedAccommodationModel selectedAccommodation = new SelectedAccommodationModel();
		selectedAccommodation.setTraveller(traveller);
		final List<SelectedAccommodationModel> selectedAccommodations = Collections.singletonList(selectedAccommodation);
		cartModel.setSelectedAccommodations(selectedAccommodations);
		Mockito.when(modelService.clone(selectedAccommodation, SelectedAccommodationModel.class)).thenReturn(selectedAccommodation);

		final CartModel cartModelRtr = defaultTravelCartService.cancelTraveller(orderCode, cancelledTravellerCode,
				cancelledTravellerUid, guid);
		Assert.assertNotNull(cartModelRtr);
		Assert.assertEquals(cartModel, cartModelRtr);
	}

	@Test
	public void testCancelTravellerWithMultiTravellerEntries()
	{
		final String orderCode = null;
		final String guid = null;
		final String cancelledTravellerCode = "LTN_CDG";
		final String cancelledTravellerUid = null;

		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));

		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final TravellerModel travellerModel1 = new TravellerModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		travellerModel1.setLabel("LTN_CDG");
		travellerModel1.setUid("travellerUId1");
		final TravellerModel travellerModel2 = new TravellerModel();
		travellerModel2.setLabel("CDG_LTN");
		travellerModel2.setUid("travellerUId2");

		orderEntryInfo.setTravellers(new ArrayList<>(Arrays.asList(travellerModel1, travellerModel2)));
		cartModel.setEntries(Arrays.asList(entry));
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		Mockito.when(modelService.clone(travellerModel1, TravellerModel.class)).thenReturn(travellerModel1);
		Mockito.when(modelService.clone(travellerModel2, TravellerModel.class)).thenReturn(travellerModel2);
		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);
		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());
		Mockito.when(modelService.clone(entry.getTravelOrderEntryInfo(), TravelOrderEntryInfoModel.class))
				.thenReturn(orderEntryInfo);
		final SelectedAccommodationModel selectedAccommodation = new SelectedAccommodationModel();
		selectedAccommodation.setTraveller(travellerModel1);
		final List<SelectedAccommodationModel> selectedAccommodations = Collections.singletonList(selectedAccommodation);
		cartModel.setSelectedAccommodations(selectedAccommodations);

		Mockito.when(modelService.clone(selectedAccommodation, SelectedAccommodationModel.class)).thenReturn(selectedAccommodation);

		final CartModel cartModelRtr = defaultTravelCartService.cancelTraveller(orderCode, cancelledTravellerCode,
				cancelledTravellerUid, guid);
		Assert.assertNotNull(cartModelRtr);
		Assert.assertEquals(cartModel, cartModelRtr);
	}

	@Test
	public void testCancelTravellerWithSelectedAccomodations()
	{
		final String orderCode = null;
		final String guid = null;
		final String cancelledTravellerCode = "LTN_CDG";
		final String cancelledTravellerUid = "TRAVELLER_UID";

		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));

		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setLabel("LTN_CDG");
		final TravellerModel traveller2 = new TravellerModel();
		traveller2.setLabel("CDG_LTN");
		orderEntryInfo.setTravellers(new ArrayList(Arrays.asList(traveller1, traveller2)));
		cartModel.setEntries(Arrays.asList(entry));
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		final SelectedAccommodationModel selectedAccommodationModel = new SelectedAccommodationModel();
		traveller1.setUid("TRAVELLER_UID");
		selectedAccommodationModel.setTraveller(traveller1);
		cartModel.setSelectedAccommodations(Arrays.asList(selectedAccommodationModel));

		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);

		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());
		Mockito.when(modelService.clone(entry.getTravelOrderEntryInfo(), TravelOrderEntryInfoModel.class))
				.thenReturn(orderEntryInfo);
		Mockito.when(modelService.clone(traveller1, TravellerModel.class)).thenReturn(traveller1);
		Mockito.when(modelService.clone(traveller2, TravellerModel.class)).thenReturn(traveller2);

		Mockito.when(modelService.clone(selectedAccommodationModel, SelectedAccommodationModel.class))
				.thenReturn(selectedAccommodationModel);

		final CartModel cartModelRtr = defaultTravelCartService.cancelTraveller(orderCode, cancelledTravellerCode,
				cancelledTravellerUid, guid);
		Assert.assertNotNull(cartModelRtr);
		Assert.assertEquals(cartModel, cartModelRtr);
	}

	@Test
	public void testCancelTravellerWithSelectedAccomodationsAndCancelledTravellerUidNotMatch()
	{
		final String orderCode = null;
		final String guid = null;
		final String cancelledTravellerCode = "LTN_CDG";
		final String cancelledTravellerUid = "CANCEL_TRAVELLER_UID";

		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));

		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		final TravellerModel travellerModel1 = new TravellerModel();
		travellerModel1.setLabel("LTN_CDG");
		final TravellerModel travellerModel2 = new TravellerModel();
		travellerModel2.setLabel("CDG_LTN");
		orderEntryInfo.setTravellers(Stream.of(travellerModel1, travellerModel2).collect(Collectors.toList()));
		entry.setTravelOrderEntryInfo(orderEntryInfo);
		cartModel.setEntries(Arrays.asList(entry));

		final SelectedAccommodationModel selectedAccommodationModel = new SelectedAccommodationModel();
		travellerModel1.setUid("TRAVELLER_UID");
		selectedAccommodationModel.setTraveller(travellerModel1);
		cartModel.setSelectedAccommodations(Arrays.asList(selectedAccommodationModel));

		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);
		Mockito.when(modelService.clone(travellerModel1, TravellerModel.class)).thenReturn(travellerModel1);
		Mockito.when(modelService.clone(travellerModel2, TravellerModel.class)).thenReturn(travellerModel2);
		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());
		Mockito.when(modelService.clone(entry.getTravelOrderEntryInfo(), TravelOrderEntryInfoModel.class))
				.thenReturn(orderEntryInfo);
		Mockito.when(modelService.clone(selectedAccommodationModel, SelectedAccommodationModel.class))
				.thenReturn(selectedAccommodationModel);

		final CartModel cartModelRtr = defaultTravelCartService.cancelTraveller(orderCode, cancelledTravellerCode,
				cancelledTravellerUid, guid);
		Assert.assertNotNull(cartModelRtr);
		Assert.assertEquals(cartModel, cartModelRtr);
	}

	@Test
	public void testRemoveDeliveryAddress()
	{
		Mockito.doNothing().when(modelService).save(Matchers.any(CartModel.class));
		defaultTravelCartService.removeDeliveryAddress();

		Mockito.verify(modelService).save(Matchers.any(CartModel.class));
	}

	@Test
	public void testGetAvailableStock()
	{
		final ProductModel productModel = new ProductModel();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		final List<TransportOfferingModel> transportOfferings = Stream.of(transportOfferingModel).collect(Collectors.toList());
		Mockito.when(travelCommerceStockService.getStockLevel(productModel, transportOfferings)).thenReturn(5l);
		defaultTravelCartService.getAvailableStock(productModel, transportOfferingModel);

		Mockito.verify(travelCommerceStockService).getStockLevel(productModel, transportOfferings);
	}

	@Test
	public void testGetAvailableStockWithNullOriginalOrderCode()
	{
		final ProductModel productModel = new ProductModel();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();

		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				final CartModel cart = new CartModel();
				return cart;
			}

		};

		final List<TransportOfferingModel> transportOfferings = Stream.of(transportOfferingModel).collect(Collectors.toList());
		Mockito.when(travelCommerceStockService.getStockLevel(productModel, transportOfferings)).thenReturn(5l);

		defaultTravelCartServicetemp.setTravelCommerceStockService(travelCommerceStockService);

		defaultTravelCartServicetemp.getAvailableStock(productModel, transportOfferingModel);

		Mockito.verify(travelCommerceStockService).getStockLevel(productModel, transportOfferings);
	}

	@Test
	public void testGetFareProductEntries()
	{
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();

		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setActive(true);
		abstractOrderEntryModel.setType(OrderEntryType.TRANSPORT);
		final ProductModel product = new FareProductModel();
		product.setProductType(ProductType.FARE_PRODUCT);
		abstractOrderEntryModel.setProduct(product);

		abstractOrderModel.setEntries(Collections.singletonList(abstractOrderEntryModel));

		final List<AbstractOrderEntryModel> abstractOrderEntryModels = defaultTravelCartService
				.getFareProductEntries(abstractOrderModel);
		Assert.assertTrue(abstractOrderEntryModels.size() == 1);
		Assert.assertEquals(abstractOrderEntryModel, abstractOrderEntryModels.get(0));
	}

	@Test
	public void testGetCurrentDestinationForNonTransportEntries()
	{
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setOriginDestinationRefNumber(1);
		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setActive(true);
		abstractOrderEntryModel.setType(OrderEntryType.ACCOMMODATION);
		abstractOrderEntryModel.setTravelOrderEntryInfo(orderEntryInfo);
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.singletonList(abstractOrderEntryModel));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{

				return cart;
			}

		};
		defaultTravelCartServicetemp.setSessionService(sessionService);
		Assert.assertNull(defaultTravelCartServicetemp.getCurrentDestination());

	}

	@Test
	public void testGetCurrentDestinationForTransportEntriesWithOriginDestinationNum1()
	{
		Assert.assertNull(defaultTravelCartServicetemp.getCurrentDestination());
	}

	@Test
	public void testGetCurrentDestinationForTransportEntries()
	{
		final TransportFacilityModel destination = new TransportFacilityModel();
		destination.setCode("LTN");
		final TravelRouteModel travelRoute = new TravelRouteModel();
		travelRoute.setDestination(destination);
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setOriginDestinationRefNumber(0);
		orderEntryInfo.setTravelRoute(travelRoute);
		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setActive(true);
		abstractOrderEntryModel.setType(OrderEntryType.TRANSPORT);
		abstractOrderEntryModel.setTravelOrderEntryInfo(orderEntryInfo);

		final ProductModel product = new ProductModel();
		product.setProductType(ProductType.FARE_PRODUCT);
		abstractOrderEntryModel.setProduct(product);
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.singletonList(abstractOrderEntryModel));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{

				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ONLY.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(new CartModel());
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito.when(
				enumerationService.getEnumerationValue(BookingJourneyType.class, BookingJourneyType.BOOKING_TRANSPORT_ONLY.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ONLY);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		Assert.assertNotNull(defaultTravelCartServicetemp.getCurrentDestination());

	}

	@Test
	public void testGetPaymentOptionsForNoSessionCart()
	{
		Mockito.when(sessionService.getAttribute(Matchers.anyString())).thenReturn(null);
		Assert.assertTrue(CollectionUtils.isEmpty(defaultTravelCartService.getPaymentOptions()));
	}

	@Test
	public void testGetPaymentOptions()
	{
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(new CartModel());
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);
		Mockito.when(partiallyDelayedPaymentOptionStrategy.create(Matchers.any(AbstractOrderModel.class)))
				.thenReturn(new PaymentOptionInfo());
		defaultTravelCartService
				.setPaymentOptionCreationStrategies(Collections.singletonList(partiallyDelayedPaymentOptionStrategy));
		Assert.assertTrue(CollectionUtils.isNotEmpty(defaultTravelCartService.getPaymentOptions()));
	}

	@Test
	public void testGetPaymentOptionsForOrderEntryTypeForNoSessionCart()
	{
		Mockito.when(sessionService.getAttribute(Matchers.anyString())).thenReturn(null);
		Assert.assertTrue(CollectionUtils.isEmpty(defaultTravelCartService.getPaymentOptions(OrderEntryType.ACCOMMODATION)));
	}

	@Test
	public void testGetPaymentOptionsForOrderEntry()
	{

		final Map<OrderEntryType, List<PaymentOptionCreationStrategy>> paymentOptionsCreationStrategyMap = new HashMap<>();
		paymentOptionsCreationStrategyMap.put(OrderEntryType.ACCOMMODATION,
				Collections.singletonList(accommodationPayNowPaymentOptionStrategy));
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(new CartModel());
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);
		Mockito.when(accommodationPayNowPaymentOptionStrategy.create(Matchers.any(AbstractOrderModel.class)))
				.thenReturn(new PaymentOptionInfo());
		defaultTravelCartService.setPaymentOptionsCreationStrategyMap(paymentOptionsCreationStrategyMap);
		Assert.assertTrue(CollectionUtils.isNotEmpty(defaultTravelCartService.getPaymentOptions(OrderEntryType.ACCOMMODATION)));
	}

	@Test
	public void testSetAdditionalSecurity()
	{
		final CartModel cartModel = new CartModel();
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cartModel);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);

		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{

				return cartModel;
			}

		};
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);
		defaultTravelCartServicetemp.setAdditionalSecurity(Boolean.TRUE);
		Mockito.verify(modelService).save(cartModel);
	}

	@Test
	public void testDeleteCurrentCartForNoSession()
	{
		Mockito.when(sessionService.getAttribute(Matchers.anyString())).thenReturn(null);
		defaultTravelCartService.deleteCurrentCart();
	}

	@Test
	public void testDeleteCurrentCart()
	{
		final CartModel cartModel = new CartModel();
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cartModel);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);

		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{

				return cartModel;
			}

		};
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);
		defaultTravelCartServicetemp.deleteCurrentCart();
	}

	@Test
	public void testCancelPartialOrder()
	{
		final String orderCode = null;
		final String guid = null;
		final OrderModel orderModel = Mockito.mock(OrderModel.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(customerAccountService.getOrderForCode(orderCode, baseStoreService.getCurrentBaseStore()))
				.thenReturn(orderModel);
		Mockito.when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("GBP");
		Mockito.when(orderModel.getCurrency().getIsocode()).thenReturn("INR");
		Mockito.when(travelCartDao.findCartsForOriginalOrder(orderModel)).thenReturn(Arrays.asList(new CartModel()));
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setType(OrderEntryType.ACCOMMODATION);
		final CartModel cartModel = new CartModel();
		cartModel.setEntries(Arrays.asList(entry));

		final SelectedAccommodationModel selectedAccommodation = new SelectedAccommodationModel();
		final List<SelectedAccommodationModel> selectedAccommodations = Collections.singletonList(selectedAccommodation);
		cartModel.setSelectedAccommodations(selectedAccommodations);

		Mockito.when(modelService.clone(selectedAccommodation, SelectedAccommodationModel.class)).thenReturn(selectedAccommodation);

		Mockito.when(cloneAbstractOrderStrategy.clone(Matchers.isNull(null), Matchers.isNull(null), Matchers.anyObject(),
				Matchers.anyString(), Matchers.any(Class.class), Matchers.any(Class.class))).thenReturn(cartModel);

		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());
		final CartModel cartModelRtr = defaultTravelCartService.cancelPartialOrder(orderCode, OrderEntryType.ACCOMMODATION, guid);
		Assert.assertNotNull(cartModelRtr);
	}

	@Test
	public void testValidateCartForNoCartInSession()
	{
		final Date currentDate = new Date();

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");



		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return null;
			}

		};
		Mockito.when(sessionService.getAttribute(Matchers.anyString())).thenReturn(null);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);

		final String departureDate = TravelDateUtils.convertDateToStringDate(currentDate, TravelservicesConstants.DATE_PATTERN);
		final String arrivalDate = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 2),
				TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate);
		Mockito.verify(sessionService, Mockito.times(0)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForRoundTrip()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);

		final String departureDate = TravelDateUtils.convertDateToStringDate(currentDate, TravelservicesConstants.DATE_PATTERN);
		final String arrivalDate = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 2),
				TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate);
		Mockito.verify(sessionService, Mockito.times(0)).removeAttribute(Matchers.anyString());
		final String arrivalDate2 = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 3),
				TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate2);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}



	@Test
	public void testValidateCartForRoundTripWithMisMatchDepartureDateArgument()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);

		final String departureDate = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 1),
				TravelservicesConstants.DATE_PATTERN);
		final String arrivalDate = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 2),
				TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForAccommodationOnly()
	{
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);

		final Date currentDate = new Date();
		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");


		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_ACCOMMODATION_ONLY.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.SINGLE.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_ACCOMMODATION_ONLY.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_ACCOMMODATION_ONLY);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.SINGLE.getCode())).thenReturn(TripType.SINGLE);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);

		final String departureDate = TravelDateUtils.convertDateToStringDate(currentDate, TravelservicesConstants.DATE_PATTERN);
		final String arrivalDate = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 2),
				TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate);
		Mockito.verify(sessionService, Mockito.times(0)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForOneWayTripWithoutReturnDateArgument()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.SINGLE.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.SINGLE.getCode())).thenReturn(TripType.SINGLE);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);

		final String departureDate = TravelDateUtils.convertDateToStringDate(currentDate, TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate, null);
		Mockito.verify(sessionService, Mockito.times(0)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForOneWayTripWithReturnDateArgument()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.SINGLE.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.SINGLE.getCode())).thenReturn(TripType.SINGLE);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);

		final String departureDate = TravelDateUtils.convertDateToStringDate(currentDate, TravelservicesConstants.DATE_PATTERN);
		final String arrivalDate2 = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 3),
				TravelservicesConstants.DATE_PATTERN);
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate2);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForOneWayTripWithDifferentDepartureDates()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.SINGLE.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.SINGLE.getCode())).thenReturn(TripType.SINGLE);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);
		final String arrivalDate2 = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 3),
				TravelservicesConstants.DATE_PATTERN);
		final String departureDate = arrivalDate2;
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate,
				arrivalDate2);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForOneWayTripWithDifferentArrivalLocation()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.SINGLE.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.SINGLE.getCode())).thenReturn(TripType.SINGLE);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);
		final String arrivalDate2 = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 3),
				TravelservicesConstants.DATE_PATTERN);
		final String departureDate = arrivalDate2;
		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), "Test_Destination_Location_Code_2", departureDate,
				arrivalDate2);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}

	@Test
	public void testValidateCartForOneWayTripWithDifferentOriginLocation()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};

		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.SINGLE.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.SINGLE.getCode())).thenReturn(TripType.SINGLE);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);
		final String arrivalDate2 = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 3),
				TravelservicesConstants.DATE_PATTERN);
		final String departureDate = arrivalDate2;
		defaultTravelCartServicetemp.validateCart("Test_Origin_Location_Code_2", destinationlocation.getCode(), departureDate,
				arrivalDate2);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}


	@Test
	public void testValidateCartForNullReturnDate()
	{
		final ProductModel product1 = createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = createProductModel("product3", ProductType.FARE_PRODUCT);
		final ProductModel product4 = createProductModel("product3", ProductType.ANCILLARY);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = createTransportOffering(
				TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.ACCOMMODATION,
				null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product4,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product3,
				1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));
		final DefaultTravelCartService defaultTravelCartServicetemp = new DefaultTravelCartService()
		{
			@Override
			protected CartModel internalGetSessionCart()
			{
				return cart;
			}

		};
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode());
		Mockito.when(sessionService.getAttribute("cart")).thenReturn(cart);
		Mockito.when(sessionService.getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE)).thenReturn(TripType.RETURN.getCode());
		Mockito
				.when(enumerationService.getEnumerationValue(BookingJourneyType.class,
						BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION.getCode()))
				.thenReturn(BookingJourneyType.BOOKING_TRANSPORT_ACCOMMODATION);
		Mockito.when(enumerationService.getEnumerationValue(TripType.class, TripType.RETURN.getCode())).thenReturn(TripType.RETURN);
		defaultTravelCartServicetemp.setEnumerationService(enumerationService);
		defaultTravelCartServicetemp.setSessionService(sessionService);
		defaultTravelCartServicetemp.setModelService(modelService);


		final String departureDate = TravelDateUtils.convertDateToStringDate(currentDate, TravelservicesConstants.DATE_PATTERN);
		final String arrivalDate = TravelDateUtils.convertDateToStringDate(TravelDateUtils.addDays(currentDate, 2),
				TravelservicesConstants.DATE_PATTERN);

		defaultTravelCartServicetemp.validateCart(originlocation.getCode(), destinationlocation.getCode(), departureDate, null);
		Mockito.verify(sessionService, Mockito.times(1)).removeAttribute(Matchers.anyString());
	}

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final boolean isActive, final AmendStatus amendStatus,
			final OrderEntryType orderEntryType, final TravelOrderEntryInfoModel travelOrderEntryInfoModel,
			final ProductModel product, final int quantity, final double basePrice, final double totalPrice,
			final List<TaxValue> taxValues)
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel()
		{
			@Override
			public Collection<TaxValue> getTaxValues()
			{
				return taxValues;
			}

		};
		abstractOrderEntryModel.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
		abstractOrderEntryModel.setActive(isActive);
		abstractOrderEntryModel.setProduct(product);
		abstractOrderEntryModel.setType(orderEntryType);
		abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
		abstractOrderEntryModel.setBasePrice(basePrice);
		abstractOrderEntryModel.setTotalPrice(totalPrice);
		abstractOrderEntryModel.setAmendStatus(amendStatus);
		return abstractOrderEntryModel;
	}

	private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(final AccommodationModel accommodationModel,
			final AccommodationOfferingModel accommodationOfferingModel, final RatePlanModel ratePlan,
			final List<AbstractOrderEntryModel> entries)
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
		accommodationOrderEntryGroupModel.setAccommodation(accommodationModel);
		accommodationOrderEntryGroupModel.setAccommodationOffering(accommodationOfferingModel);
		accommodationOrderEntryGroupModel.setRatePlan(ratePlan);
		accommodationOrderEntryGroupModel.setEntries(entries);
		return accommodationOrderEntryGroupModel;

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
		return accommodationOfferingModel;
	}

	private RatePlanModel createRatePlanModel(final String code)
	{
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setCode(code);
		return ratePlan;
	}

	private ProductModel createProductModel(final String code, final ProductType productType)
	{
		final ProductModel product = new ProductModel();
		product.setCode(code);
		product.setProductType(productType);
		return product;
	}

	private TravelOrderEntryInfoModel createTravelOrderEntryInfoModel(final int originDestinationRefNum,
			final List<TransportOfferingModel> transportOfferings, final TravelRouteModel travelRoute)
	{
		final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
		travelOrderEntryInfoModel.setOriginDestinationRefNumber(originDestinationRefNum);
		travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);
		travelOrderEntryInfoModel.setTravelRoute(travelRoute);
		return travelOrderEntryInfoModel;
	}

	private TravelRouteModel createTravelRoute(final TransportFacilityModel origin, final TransportFacilityModel destination)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();
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

	private TransportOfferingModel createTransportOffering(final Date departureTime, final Date arrivalTime)
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setDepartureTime(departureTime);
		transportOffering.setArrivalTime(arrivalTime);
		return transportOffering;
	}

}