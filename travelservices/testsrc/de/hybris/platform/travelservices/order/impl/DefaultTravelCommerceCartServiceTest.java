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

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.order.CommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.impl.OrderEntryModifiableChecker;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.bundle.impl.BundleOrderEntryModifiableChecker;
import de.hybris.platform.configurablebundleservices.bundle.impl.BundleOrderEntryRemoveableChecker;
import de.hybris.platform.configurablebundleservices.bundle.impl.DefaultCartBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.AutoPickPerLegBundleSelectionCriteriaModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.CommerceBundleCartModificationException;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * JUnit test suite for {@link DefaultTravelCommerceCartService}
 */
@UnitTest
public class DefaultTravelCommerceCartServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String SESSION_ATTRIBUTE_CALCULATE_CART = "CALCULATE_CART";

	private DefaultTravelCommerceCartService travelCommerceCartService;

	private CartModel masterCartModel;

	private ProductModel productModel;

	private UnitModel unitModel;

	private BillingTimeModel billingTimeModel;

	private BundleTemplateModel bundleTemplateModel;

	@Mock
	private ProductService productService;

	@Mock
	private CartService cartService;

	@Mock
	private SubscriptionCommercePriceService subscriptionCommercePriceService;

	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Mock
	private ModelService modelService;

	@Mock
	private WarehouseService warehouseService;

	@Mock
	private TimeService timeService;

	@Mock
	private BillingTimeService billingTimeService;

	@Mock
	private StockService stockService;

	@Mock
	private SessionService sessionService;

	@Mock
	private BundleTemplateService bundleTemplateService;

	@Mock
	private OrderEntryDao<CartModel, CartEntryModel> cartEntryDao;

	@Mock
	private BundleRuleService bundleRuleService;

	@Mock
	private BundleOrderEntryRemoveableChecker orderEntryRemoveableChecker;

	@Mock
	private L10NService l10NService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CommerceStockService commerceStockService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private TravelRouteService travelRouteService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private TravellerService travellerService;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@Mock
	private TravelRouteModel travelRoute;

	@Mock
	private TransportOfferingModel transportOffering;

	@Mock
	private SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy;

	@Mock
	private CommerceAddToCartStrategy commerceAddToCartStrategy;

	@Mock
	private CommerceUpdateCartEntryStrategy commerceUpdateCartEntryStrategy;

	@Mock
	private TravellerModel traveller;

	@Spy
	private final ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker = new OrderEntryModifiableChecker();

	@Mock
	private final ModifiableChecker<AbstractOrderEntryModel> orderEntryModifiableChecker = new BundleOrderEntryModifiableChecker();

	@Mock
	private final AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker = new DefaultCartBundleComponentEditableChecker();

	@Mock
	private CartEntryModel cartEntryModel;

	private Map<String, String> offerGroupToOriginDestinationMapping;

	private static final String TRANSPORT_OFFERING_CODE = "TestTransportOfferingCode_123";

	private static final String TRAVEL_ROUTE_CODE = "TestTravelRoute_123";

	/**
	 * The setup method instantiates the class instance to be tested and also sets all the required attributes of the
	 * class.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		travelCommerceCartService = new DefaultTravelCommerceCartService()
		{
			@Override
			protected CommerceCartModification addTravelProductToCart(final CartModel masterCartModel,
					final ProductModel productModel, final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry,
					final int bundleNo, final BundleTemplateModel bundleTemplateModel, final boolean removeCurrentProducts,
					final String xmlProduct) throws CommerceCartModificationException
			{
				final CommerceCartModification mod = new CommerceCartModification();
				mod.setQuantityAdded(1);
				mod.setEntry(cartEntryModel);
				return mod;
			}

			@Override
			protected CommerceCartModification addProductToCart(@Nonnull final CartModel masterCartModel,
					@Nonnull final ProductModel productModel, final long quantityToAdd, final UnitModel unit,
					final boolean forceNewEntry, final int bundleNo, @Nullable final BundleTemplateModel bundleTemplateModel,
					@Nullable final String xmlProduct, final boolean ignoreEmptyBundle) throws CommerceCartModificationException
			{
				final ProductModel product = new ProductModel();
				product.setCode("TEST_PRODCUT_CODE");

				final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
				travelOrderEntryInfoModel.setTravellers(Stream.of(new TravellerModel()).collect(Collectors.toList()));
				final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
				orderEntryModel.setProduct(product);
				orderEntryModel.setTravelOrderEntryInfo(travelOrderEntryInfoModel);

				final CartEntryModel cartEntryModel = new CartEntryModel();
				orderEntryModel.setChildEntries(Stream.of(cartEntryModel).collect(Collectors.toList()));
				final CommerceCartModification commerceCartModification = new CommerceCartModification();
				commerceCartModification.setQuantityAdded(10);
				commerceCartModification.setEntry(orderEntryModel);
				return commerceCartModification;
			}

			@Override
			protected void removeCartEntry(final CartModel masterCartModel, final CartEntryModel cartEntry)
			{
				return;
			}

		};
		travelCommerceCartService.setCommerceCartCalculationStrategy(commerceCartCalculationStrategy);
		travelCommerceCartService.setCommercePriceService(subscriptionCommercePriceService);
		travelCommerceCartService.setSessionService(sessionService);
		travelCommerceCartService.setModelService(modelService);
		travelCommerceCartService.setBundleCartEntryDao(cartEntryDao);
		travelCommerceCartService.setBillingTimeService(billingTimeService);
		travelCommerceCartService.setBundleTemplateService(bundleTemplateService);
		travelCommerceCartService.setSubscriptionCommerceCartStrategy(subscriptionCommerceCartStrategy);
		travelCommerceCartService.setOrderEntryModifiableChecker(orderEntryModifiableChecker);
		travelCommerceCartService.setSubscriptionProductStockQuantity(1000);
		travelCommerceCartService.setRemovableChecker(orderEntryRemoveableChecker);
		travelCommerceCartService.setBundleComponentEditableChecker(bundleComponentEditableChecker);
		travelCommerceCartService.setBundleRuleService(bundleRuleService);
		travelCommerceCartService.setL10NService(l10NService);
		travelCommerceCartService.setBaseStoreService(baseStoreService);
		travelCommerceCartService.setBaseSiteService(baseSiteService);
		travelCommerceCartService.setCommerceStockService(commerceStockService);
		travelCommerceCartService.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		travelCommerceCartService.setCommerceUpdateCartEntryStrategy(commerceUpdateCartEntryStrategy);
		travelCommerceCartService.setTransportOfferingService(transportOfferingService);
		travelCommerceCartService.setTravellerService(travellerService);
		travelCommerceCartService.setTravelCommercePriceService(travelCommercePriceService);
		travelCommerceCartService.setCartService(cartService);
		offerGroupToOriginDestinationMapping = new HashMap<>();
		offerGroupToOriginDestinationMapping.put("PRIORITYCHECKIN", "TravelRoute");
		offerGroupToOriginDestinationMapping.put("HOLDITEM", "TravelRoute");
		offerGroupToOriginDestinationMapping.put("PRIORITYBOARDING", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("LOUNGEACCESS", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("MEAL", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("ACCOMMODATION", "TransportOffering");
		offerGroupToOriginDestinationMapping.put("DEFAULT", "TravelRoute");
		travelCommerceCartService.setOfferGroupToOriginDestinationMapping(offerGroupToOriginDestinationMapping);
		productModel = mock(ProductModel.class);
		unitModel = mock(UnitModel.class);

		billingTimeModel = mock(BillingTimeModel.class);
		masterCartModel = mock(CartModel.class);
		given(masterCartModel.getBillingTime()).willReturn(billingTimeModel);
		given(billingTimeService.getBillingTimeForCode(null)).willReturn(billingTimeModel);
		given(productModel.getSoldIndividually()).willReturn(Boolean.TRUE);

		final BaseSiteModel baseSite = mock(BaseSiteModel.class);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);

		Mockito.when(sessionService.executeInLocalView(Mockito.any(SessionExecutionBody.class))).thenAnswer(invocation -> {
			final SessionExecutionBody args = (SessionExecutionBody) invocation.getArguments()[0];
			return args.execute();
		});

		doReturn(Boolean.TRUE).when(entryOrderChecker).canModify(Mockito.any(AbstractOrderEntryModel.class));

		bundleTemplateModel = mock(BundleTemplateModel.class);
	}

	/**
	 * This method is used test adding bundle to cart.
	 *
	 * @throws CommerceCartModificationException
	 * @throws CalculationException
	 */
	@Test
	public void testAddToCartNewBundle() throws CommerceCartModificationException, CalculationException
	{
		final StockLevelModel stockLevelModel = mock(StockLevelModel.class);
		long actualAdded = 0;

		given(Integer.valueOf(stockLevelModel.getAvailable())).willReturn(Integer.valueOf(10));
		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, -1, false)).willReturn(cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getBundleNo().intValue()).willReturn(1);
		final List<AbstractOrderEntryModel> allEntries = new ArrayList<>();
		allEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allEntries);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);

		final TravellerModel travellerModel = new TravellerModel();

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		final PassengerTypeModel passengerType = new PassengerTypeModel();
		passengerType.setCode("adult");
		passengerInformationModel.setPassengerType(passengerType);
		travellerModel.setInfo(passengerInformationModel);
		final BundleTemplateModel rootBundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel childRootBundleTemplate1 = new BundleTemplateModel();
		childRootBundleTemplate1.setBundleSelectionCriteria(new PickExactlyNBundleSelectionCriteriaModel());
		final BundleTemplateModel childRootBundleTemplate2 = new BundleTemplateModel();
		childRootBundleTemplate2.setBundleSelectionCriteria(new AutoPickBundleSelectionCriteriaModel());
		final BundleTemplateModel childRootBundleTemplate3 = new BundleTemplateModel();
		childRootBundleTemplate3.setBundleSelectionCriteria(new AutoPickBundleSelectionCriteriaModel());

		final ProductModel productModel1 = new ProductModel();
		final CategoryModel offerGroup1 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel1 = new OfferGroupRestrictionModel();
		offerGroupRestrictionModel1.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG);
		offerGroupRestrictionModel1.setPassengerTypes(Stream.of("adult").collect(Collectors.toList()));
		offerGroup1.setTravelRestriction(offerGroupRestrictionModel1);
		offerGroup1.setCode("PRIORITYCHECKIN");
		productModel1.setSupercategories(Stream.of(offerGroup1).collect(Collectors.toList()));

		final ProductModel productModel2 = new ProductModel();
		final CategoryModel offerGroup2 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel2 = new OfferGroupRestrictionModel();
		offerGroupRestrictionModel2.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG);
		offerGroup2.setTravelRestriction(offerGroupRestrictionModel1);
		offerGroup2.setCode("PRIORITYBOARDING");
		productModel2.setSupercategories(Stream.of(offerGroup2).collect(Collectors.toList()));


		childRootBundleTemplate2.setProducts(Stream.of(productModel1, productModel2).collect(Collectors.toList()));
		childRootBundleTemplate3.setProducts(Stream.of(productModel1, productModel2).collect(Collectors.toList()));

		rootBundleTemplate.setChildTemplates(
				Stream.of(childRootBundleTemplate1, childRootBundleTemplate2, childRootBundleTemplate3).collect(Collectors.toList()));
		given(bundleTemplateService.isAutoPickComponent(childRootBundleTemplate1)).willReturn(Boolean.FALSE);
		given(bundleTemplateService.isAutoPickComponent(childRootBundleTemplate2)).willReturn(Boolean.TRUE);
		given(bundleTemplateService.isAutoPickComponent(childRootBundleTemplate3)).willReturn(Boolean.TRUE);

		given(bundleTemplateModel.getProducts()).willReturn(products);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(bundleTemplateModel.getParentTemplate()).willReturn(rootBundleTemplate);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateModel, -1)))
				.willReturn(Boolean.TRUE);
		given(productModel.getMaxOrderQuantity()).willReturn(null);

		final Map<String, Object> addBundleToCartParamsMap = new HashMap<>();
		final List<TransportOfferingModel> transportOfferingCodes = new ArrayList<>();
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY098012345");
		transportOfferingCodes.add(transportOfferingModel);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, transportOfferingCodes);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, 0);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVELLER, travellerModel);
		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("TEST_TRAVE_ROUTE_CODE");
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE, travelRouteModel);

		given(sessionService.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP))
				.willReturn(addBundleToCartParamsMap);

		given(transportOfferingService.getTransportOffering(Matchers.anyString())).willReturn(transportOffering);
		given(travelCommercePriceService.isPriceInformationAvailable(Matchers.any(ProductModel.class), Matchers.anyString(),
				Matchers.anyString())).willReturn(Boolean.TRUE);
		Mockito.doNothing().when(travelCommercePriceService).setPriceAndTaxSearchCriteriaInContext(Matchers.any(PriceLevel.class),
				Matchers.anyList(), Matchers.anyString());
		final List<CommerceCartModification> modifications = travelCommerceCartService.addToCart(masterCartModel, productModel, 1,
				unitModel, true, -1, bundleTemplateModel, false, "<no xml>");
		assertEquals(5, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);
		verify(modelService, atLeastOnce()).save(Mockito.any(AbstractOrderEntryModel.class));
	}

	/**
	 * @throws CommerceCartModificationException
	 * @throws CalculationException
	 */
	@Test
	public void testAddToCartRemovingExistingProducts() throws CommerceCartModificationException, CalculationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);
		final ProductModel product = mock(ProductModel.class);
		long actualAdded = 0;

		given(cartService.addNewEntry(masterCartModel, productModel, 1, unitModel, 1, false)).willReturn(cartEntryModel);
		given(cartEntryModel.getOrder()).willReturn(masterCartModel);
		given(cartEntryModel.getBundleNo()).willReturn(Integer.valueOf(1));
		given(cartEntryModel.getBundleTemplate()).willReturn(bundleTemplateModel);
		given(cartEntryModel.getProduct()).willReturn(product);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(bundleTemplateModel);
		given(sessionService.getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART)).willReturn(Boolean.FALSE).willReturn(null);
		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		given(bundleTemplateModel.getProducts()).willReturn(products);
		final List<CartEntryModel> allCartEntries = new ArrayList<>();
		allCartEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateModel))
				.willReturn(allCartEntries);

		given(cartEntryDao.findEntriesByMasterCartAndBundleNo(masterCartModel, 1)).willReturn(allCartEntries);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateModel, 1)))
				.willReturn(Boolean.TRUE);
		given(productModel.getMaxOrderQuantity()).willReturn(null);
		final CommerceCartModification mod = new CommerceCartModification();
		mod.setQuantityAdded(1);
		mod.setEntry(cartEntryModel);
		Mockito.doReturn(mod).when(commerceAddToCartStrategy).addToCart(Mockito.any(CommerceCartParameter.class));

		final Map<String, Object> addBundleToCartParamsMap = new HashMap<>();
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add("EZY098012345");
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, transportOfferingCodes);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, 0);
		given(sessionService.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP))
				.willReturn(addBundleToCartParamsMap);

		given(transportOfferingService.getTransportOffering(Matchers.anyString())).willReturn(transportOffering);
		final List<CommerceCartModification> modifications = travelCommerceCartService.addToCart(masterCartModel, productModel, 1,
				unitModel, true, 1, bundleTemplateModel, true, "<no xml>");
		assertEquals(1, modifications.size());
		actualAdded = modifications.iterator().next().getQuantityAdded();
		assertEquals(1, actualAdded);

		verify(modelService, atLeastOnce()).save(Mockito.any(AbstractOrderEntryModel.class));
	}

	/**
	 * The method test when there is no bundle template while adding it to cart
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartNoBundleTemplate() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		travelCommerceCartService.addToCart(masterCartModel, productModel, 1, unitModel, false, 1, null, false, "<no xml>");
	}


	/**
	 * The method test adding/persisting properties like transport offering code to a cart entry.
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntryForBundle() throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);
		final ProductModel product = mock(ProductModel.class);
		final List<CartEntryModel> allCartEntries = new ArrayList<>();
		allCartEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndProduct(masterCartModel, 1, product)).willReturn(allCartEntries);
		final List<TravellerModel> travellerList = new ArrayList<>();
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add(TRANSPORT_OFFERING_CODE);
		travelCommerceCartService.addPropertiesToCartEntryForBundle(masterCartModel, 1, product,
				populatePropertiesMap(transportOfferingCodes, TRAVEL_ROUTE_CODE, travellerList, 0));

		verify(modelService, times(2)).save(Mockito.any(CartEntryModel.class));


	}

	/**
	 * The method test adding/persisting properties like transport offering code to a cart entry.
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntry() throws CommerceCartModificationException
	{
		final CartEntryModel cartEntryModel = mock(CartEntryModel.class);
		final ProductModel product = mock(ProductModel.class);
		final List<AbstractOrderEntryModel> allCartEntries = new ArrayList<>();
		allCartEntries.add(cartEntryModel);
		given(masterCartModel.getEntries()).willReturn(allCartEntries);
		given(cartEntryModel.getEntryNumber()).willReturn(1);
		final List<TravellerModel> travellerList = new ArrayList<>();
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add(TRANSPORT_OFFERING_CODE);
		travelCommerceCartService.addPropertiesToCartEntry(masterCartModel, 1, product,
				populatePropertiesMap(transportOfferingCodes, TRAVEL_ROUTE_CODE, travellerList, 0));

		verify(modelService, times(2)).save(Mockito.any(CartEntryModel.class));

	}

	@Test
	public void testAddPerLegBundleProductToCart() throws CommerceBundleCartModificationException
	{
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("TEST_TRANSPORT_OFFERING_CODE");

		final List<TransportOfferingModel> cartEntryTransportOfferings = new ArrayList<>();
		cartEntryTransportOfferings.add(transportOffering);

		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("TEST_TRAVE_ROUTE_CODE");
		final Map<String, Object> addBundleToCartParamsMap = new HashMap<>();
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, cartEntryTransportOfferings);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE, travelRouteModel);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, 1);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVELLER, new TravellerModel());


		final BundleTemplateModel rootBundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel childRootBundleTemplate1 = new BundleTemplateModel();
		childRootBundleTemplate1.setBundleSelectionCriteria(new PickExactlyNBundleSelectionCriteriaModel());
		final BundleTemplateModel childRootBundleTemplate2 = new BundleTemplateModel();
		childRootBundleTemplate2.setBundleSelectionCriteria(new AutoPickPerLegBundleSelectionCriteriaModel());

		final ProductModel productModel1 = new ProductModel();
		final CategoryModel offerGroup1 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel1 = new OfferGroupRestrictionModel();
		offerGroup1.setTravelRestriction(offerGroupRestrictionModel1);
		productModel1.setSupercategories(Stream.of(offerGroup1).collect(Collectors.toList()));

		final ProductModel productModel2 = new ProductModel();
		final CategoryModel offerGroup2 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel2 = new OfferGroupRestrictionModel();
		offerGroup2.setTravelRestriction(offerGroupRestrictionModel2);
		offerGroupRestrictionModel2.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG);
		productModel2.setSupercategories(Stream.of(offerGroup2).collect(Collectors.toList()));

		final ProductModel productModel3 = new ProductModel();
		final CategoryModel offerGroup3 = new CategoryModel();
		productModel3.setSupercategories(Stream.of(offerGroup3).collect(Collectors.toList()));

		final ProductModel productModel4 = new ProductModel();
		final CategoryModel offerGroup4 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel4 = new OfferGroupRestrictionModel();
		offerGroupRestrictionModel4.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG);
		offerGroup4.setTravelRestriction(offerGroupRestrictionModel4);
		productModel4.setSupercategories(Stream.of(offerGroup4).collect(Collectors.toList()));

		final List<ProductModel> autoPickProducts = new ArrayList<>();
		autoPickProducts.add(productModel1);
		autoPickProducts.add(productModel2);
		autoPickProducts.add(productModel3);
		autoPickProducts.add(productModel4);
		childRootBundleTemplate2.setProducts(autoPickProducts);
		rootBundleTemplate
				.setChildTemplates(Stream.of(childRootBundleTemplate1, childRootBundleTemplate2).collect(Collectors.toList()));

		given(sessionService.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP))
				.willReturn(addBundleToCartParamsMap);
		given(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).willReturn(bundleTemplateModel);
		given(bundleTemplateService.getRootBundleTemplate(bundleTemplateModel)).willReturn(rootBundleTemplate);
		given(travelCommercePriceService.isPriceInformationAvailable(productModel2, PriceRowModel.TRAVELROUTECODE,
				travelRouteModel.getCode())).willReturn(Boolean.TRUE);
		given(travelCommercePriceService.isPriceInformationAvailable(productModel4, PriceRowModel.TRAVELROUTECODE,
				travelRouteModel.getCode())).willReturn(Boolean.FALSE);
		Mockito.doNothing().when(travelCommercePriceService).setPriceAndTaxSearchCriteriaInContext(Matchers.any(PriceLevel.class),
				Matchers.anyList(), Matchers.anyString());
		travelCommerceCartService.addPerLegBundleProductToCart("TEST_BUNDLE_TEMPLATE_ID", 2, Collections.emptyList());
	}

	@Test
	public void testAddToBundle() throws CommerceCartModificationException
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final OrderModel order = new OrderModel();
		entry.setOrder(order);
		final AbstractOrderEntryModel orderEntry1 = new AbstractOrderEntryModel();
		orderEntry1.setBundleNo(1);
		final AbstractOrderEntryModel orderEntry2 = new AbstractOrderEntryModel();
		order.setEntries(Stream.of(orderEntry1, orderEntry2).collect(Collectors.toList()));
		travelCommerceCartService.addToBundle(entry, new BundleTemplateModel(), 0);
		Mockito.verify(modelService, Mockito.times(1)).save(Matchers.any());
		travelCommerceCartService.addToBundle(entry, new BundleTemplateModel(), 2);
		Mockito.verify(modelService, Mockito.times(2)).save(Matchers.any());

	}

	@Test
	public void testGetOrderEntriesForCategory()
	{

		final CategoryModel categoryModel = new CategoryModel();

		final AbstractOrderEntryModel cartEntry0 = new AbstractOrderEntryModel();
		cartEntry0.setBundleNo(0);
		final ProductModel productModel0 = new ProductModel();
		productModel0.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry0.setProduct(productModel0);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo0 = new TravelOrderEntryInfoModel();
		cartEntry0.setTravelOrderEntryInfo(travellerOrderEntryInfo0);

		final AbstractOrderEntryModel cartEntry1 = new AbstractOrderEntryModel();
		cartEntry1.setBundleNo(1);
		final ProductModel productModel1 = new ProductModel();
		productModel1.setSupercategories(Collections.emptyList());
		cartEntry1.setProduct(productModel1);

		final AbstractOrderEntryModel cartEntry2 = new AbstractOrderEntryModel();
		cartEntry2.setBundleNo(0);
		final ProductModel productModel2 = new ProductModel();
		productModel2.setSupercategories(Collections.emptyList());
		cartEntry2.setProduct(productModel2);

		final AbstractOrderEntryModel cartEntry3 = new AbstractOrderEntryModel();
		cartEntry3.setBundleNo(0);
		final ProductModel productModel3 = new ProductModel();
		productModel3.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry3.setProduct(productModel3);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo3 = new TravelOrderEntryInfoModel();
		final TravelRouteModel travelRoute3 = new TravelRouteModel();
		travelRoute3.setCode("TEST_TRAVEL_ROUTE_CODE_3");
		final TravellerModel traveller3 = new TravellerModel();
		traveller3.setUid("TEST_TRAVELLER_CODE_1");
		travellerOrderEntryInfo3.setTravellers(Stream.of(traveller3).collect(Collectors.toList()));
		travellerOrderEntryInfo3.setTravelRoute(travelRoute3);
		cartEntry3.setTravelOrderEntryInfo(travellerOrderEntryInfo3);

		final AbstractOrderEntryModel cartEntry4 = new AbstractOrderEntryModel();
		cartEntry4.setBundleNo(0);
		final ProductModel productModel4 = new ProductModel();
		productModel4.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry4.setProduct(productModel4);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo4 = new TravelOrderEntryInfoModel();
		final TravellerModel traveller4 = new TravellerModel();
		traveller4.setUid("TEST_TRAVELLER_CODE");
		travellerOrderEntryInfo4.setTravellers(Stream.of(traveller4).collect(Collectors.toList()));
		cartEntry4.setTravelOrderEntryInfo(travellerOrderEntryInfo4);



		final AbstractOrderEntryModel cartEntry5 = new AbstractOrderEntryModel();
		cartEntry5.setBundleNo(0);
		final ProductModel productModel5 = new ProductModel();
		productModel5.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry5.setProduct(productModel5);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo5 = new TravelOrderEntryInfoModel();
		final TravelRouteModel travelRoute5 = new TravelRouteModel();
		travelRoute5.setCode("TEST_TRAVEL_ROUTE_CODE_1");
		final TravellerModel traveller5 = new TravellerModel();
		traveller5.setUid("TEST_TRAVELLER_CODE");
		travellerOrderEntryInfo5.setTravellers(Stream.of(traveller5).collect(Collectors.toList()));
		travellerOrderEntryInfo5.setTravelRoute(travelRoute5);
		cartEntry5.setTravelOrderEntryInfo(travellerOrderEntryInfo5);

		final AbstractOrderEntryModel cartEntry6 = new AbstractOrderEntryModel();
		cartEntry6.setBundleNo(0);
		final ProductModel productModel6 = new ProductModel();
		productModel6.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry6.setProduct(productModel5);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo6 = new TravelOrderEntryInfoModel();
		final TravelRouteModel travelRoute6 = new TravelRouteModel();
		travelRoute6.setCode("TEST_TRAVEL_ROUTE_CODE");
		final TravellerModel traveller6 = new TravellerModel();
		traveller6.setUid("TEST_TRAVELLER_CODE");
		travellerOrderEntryInfo6.setTravellers(Stream.of(traveller6).collect(Collectors.toList()));
		travellerOrderEntryInfo6.setTravelRoute(travelRoute6);
		cartEntry6.setTravelOrderEntryInfo(travellerOrderEntryInfo6);


		final AbstractOrderEntryModel cartEntry7 = new AbstractOrderEntryModel();
		cartEntry7.setBundleNo(0);
		final ProductModel productModel7 = new ProductModel();
		productModel7.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry7.setProduct(productModel7);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo7 = new TravelOrderEntryInfoModel();
		final TravelRouteModel travelRoute7 = new TravelRouteModel();
		travelRoute7.setCode("TEST_TRAVEL_ROUTE_CODE");
		final TravellerModel traveller7 = new TravellerModel();
		traveller7.setUid("TEST_TRAVELLER_CODE");
		travellerOrderEntryInfo7.setTravellers(Stream.of(traveller7).collect(Collectors.toList()));
		travellerOrderEntryInfo7.setTravelRoute(travelRoute7);
		final TransportOfferingModel transportOfferingModel7 = new TransportOfferingModel();
		transportOfferingModel7.setCode("TEST_TRANSPORT_OFFERING_CODE_1");
		travellerOrderEntryInfo7.setTransportOfferings(Stream.of(transportOfferingModel7).collect(Collectors.toList()));
		cartEntry7.setTravelOrderEntryInfo(travellerOrderEntryInfo7);

		final AbstractOrderEntryModel cartEntry8 = new AbstractOrderEntryModel();
		cartEntry8.setBundleNo(0);
		final ProductModel productModel8 = new ProductModel();
		productModel8.setSupercategories(Stream.of(categoryModel).collect(Collectors.toList()));
		cartEntry8.setProduct(productModel8);
		final TravelOrderEntryInfoModel travellerOrderEntryInfo8 = new TravelOrderEntryInfoModel();
		final TravelRouteModel travelRoute8 = new TravelRouteModel();
		travelRoute8.setCode("TEST_TRAVEL_ROUTE_CODE");
		final TravellerModel traveller8 = new TravellerModel();
		traveller8.setUid("TEST_TRAVELLER_CODE");
		travellerOrderEntryInfo8.setTravellers(Stream.of(traveller8).collect(Collectors.toList()));
		travellerOrderEntryInfo8.setTravelRoute(travelRoute8);
		final TransportOfferingModel transportOfferingModel8 = new TransportOfferingModel();
		transportOfferingModel8.setCode("TEST_TRANSPORT_OFFERING_CODE");
		travellerOrderEntryInfo8.setTransportOfferings(Stream.of(transportOfferingModel8).collect(Collectors.toList()));
		cartEntry8.setTravelOrderEntryInfo(travellerOrderEntryInfo8);

		final CartModel cartModel = new CartModel();
		cartModel.setEntries(
				Stream.of(cartEntry0, cartEntry1, cartEntry2, cartEntry3, cartEntry4, cartEntry5, cartEntry6, cartEntry7, cartEntry8)
						.collect(Collectors.toList()));

		travelCommerceCartService.getOrderEntriesForCategory(cartModel, categoryModel, "TEST_TRAVEL_ROUTE_CODE",
				Stream.of(transportOfferingModel8.getCode()).collect(Collectors.toList()), "TEST_TRAVELLER_CODE");
		travelCommerceCartService.getOrderEntriesForCategory(cartModel, categoryModel, null,
				Stream.of(transportOfferingModel8.getCode()).collect(Collectors.toList()), "TEST_TRAVELLER_CODE");
	}

	@Test
	public void testAddSelectedAccommodationToCartForExistingSelectedAccommodation()
	{
		final CartModel cartModel = new CartModel();
		final SelectedAccommodationModel selectedAccommodationModel0 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel0 = new TransportOfferingModel();
		transportOfferingModel0.setCode("TEST_TRANSPORT_OFFERING_CODE_0");
		selectedAccommodationModel0.setTransportOffering(transportOfferingModel0);

		final SelectedAccommodationModel selectedAccommodationModel1 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel1 = new TransportOfferingModel();
		transportOfferingModel1.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel1.setTransportOffering(transportOfferingModel1);
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setLabel("TEST_TRAVEL_CODE_1");
		selectedAccommodationModel1.setTraveller(traveller1);

		final SelectedAccommodationModel selectedAccommodationModel2 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel2 = new TransportOfferingModel();
		transportOfferingModel2.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel2.setTransportOffering(transportOfferingModel2);
		final TravellerModel traveller2 = new TravellerModel();
		traveller2.setLabel("TEST_TRAVEL_CODE");
		selectedAccommodationModel2.setTraveller(traveller2);

		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		selectedAccommodations.add(selectedAccommodationModel0);
		selectedAccommodations.add(selectedAccommodationModel1);
		selectedAccommodations.add(selectedAccommodationModel2);
		cartModel.setSelectedAccommodations(selectedAccommodations);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(modelService.create(SelectedAccommodationModel.class)).willReturn(new SelectedAccommodationModel());
		travelCommerceCartService.addSelectedAccommodationToCart("TEST_TRANSPORT_OFFERING_CODE", "TEST_TRAVEL_CODE",
				new ConfiguredAccommodationModel());


		Mockito.verify(modelService, Mockito.times(0)).save(cartModel);
	}

	@Test
	public void testAddSelectedAccommodationToCartForNonExistingSelectedAccommodation()
	{
		final CartModel cartModel = new CartModel();
		final SelectedAccommodationModel selectedAccommodationModel0 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel0 = new TransportOfferingModel();
		transportOfferingModel0.setCode("TEST_TRANSPORT_OFFERING_CODE_0");
		selectedAccommodationModel0.setTransportOffering(transportOfferingModel0);

		final SelectedAccommodationModel selectedAccommodationModel1 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel1 = new TransportOfferingModel();
		transportOfferingModel1.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel1.setTransportOffering(transportOfferingModel1);
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setUid("TEST_TRAVEL_CODE_1");
		selectedAccommodationModel1.setTraveller(traveller1);

		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		selectedAccommodations.add(selectedAccommodationModel0);
		selectedAccommodations.add(selectedAccommodationModel1);
		cartModel.setSelectedAccommodations(selectedAccommodations);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(modelService.create(SelectedAccommodationModel.class)).willReturn(new SelectedAccommodationModel());
		travelCommerceCartService.addSelectedAccommodationToCart("TEST_TRANSPORT_OFFERING_CODE", "TEST_TRAVEL_CODE",
				new ConfiguredAccommodationModel());
		Mockito.verify(modelService, Mockito.times(1)).save(cartModel);
	}

	@Test
	public void testRemoveSelectedAccommodationFromCart()
	{
		final SelectedAccommodationModel selectedAccommodationModel1 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel1 = new TransportOfferingModel();
		transportOfferingModel1.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel1.setTransportOffering(transportOfferingModel1);
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setUid("TEST_TRAVEL_CODE_1");
		selectedAccommodationModel1.setTraveller(traveller1);
		final ConfiguredAccommodationModel configuredAccommodationModel1 = new ConfiguredAccommodationModel();
		configuredAccommodationModel1.setIdentifier("TEST_CONFIGURED_ACCOMMODATION_CODE_1");
		selectedAccommodationModel1.setConfiguredAccommodation(configuredAccommodationModel1);
		final SelectedAccommodationModel selectedAccommodationModel2 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel2 = new TransportOfferingModel();
		transportOfferingModel2.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel2.setTransportOffering(transportOfferingModel2);
		final TravellerModel traveller2 = new TravellerModel();
		traveller2.setLabel("TEST_TRAVEL_CODE");
		selectedAccommodationModel2.setTraveller(traveller2);
		final ConfiguredAccommodationModel configuredAccommodationModel2 = new ConfiguredAccommodationModel();
		configuredAccommodationModel2.setIdentifier("TEST_CONFIGURED_ACCOMMODATION_CODE_2");
		selectedAccommodationModel2.setConfiguredAccommodation(configuredAccommodationModel2);
		final SelectedAccommodationModel selectedAccommodationModel3 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel3 = new TransportOfferingModel();
		transportOfferingModel3.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel3.setTransportOffering(transportOfferingModel3);
		final TravellerModel traveller3 = new TravellerModel();
		traveller3.setLabel("TEST_TRAVEL_CODE");
		selectedAccommodationModel3.setTraveller(traveller3);
		final ConfiguredAccommodationModel configuredAccommodationModel3 = new ConfiguredAccommodationModel();
		configuredAccommodationModel3.setIdentifier("TEST_CONFIGURED_ACCOMMODATION_CODE");
		selectedAccommodationModel3.setConfiguredAccommodation(configuredAccommodationModel3);
		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		selectedAccommodations.add(selectedAccommodationModel1);
		selectedAccommodations.add(selectedAccommodationModel2);
		selectedAccommodations.add(selectedAccommodationModel3);
		final CartModel cartModel = new CartModel();
		cartModel.setSelectedAccommodations(selectedAccommodations);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(modelService.create(SelectedAccommodationModel.class)).willReturn(new SelectedAccommodationModel());
		travelCommerceCartService.removeSelectedAccommodationFromCart("TEST_TRANSPORT_OFFERING_CODE", "TEST_TRAVEL_CODE",
				configuredAccommodationModel3.getIdentifier());
		Mockito.verify(modelService, Mockito.times(1)).remove(Matchers.any(SelectedAccommodationModel.class));
	}

	@Test
	public void testRemoveSelectedAccommodationFromCartForNoExistingAccommodation()
	{
		final SelectedAccommodationModel selectedAccommodationModel1 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel1 = new TransportOfferingModel();
		transportOfferingModel1.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel1.setTransportOffering(transportOfferingModel1);
		final TravellerModel traveller1 = new TravellerModel();
		traveller1.setUid("TEST_TRAVEL_CODE_1");
		selectedAccommodationModel1.setTraveller(traveller1);
		final ConfiguredAccommodationModel configuredAccommodationModel1 = new ConfiguredAccommodationModel();
		configuredAccommodationModel1.setIdentifier("TEST_CONFIGURED_ACCOMMODATION_CODE_1");
		selectedAccommodationModel1.setConfiguredAccommodation(configuredAccommodationModel1);
		final SelectedAccommodationModel selectedAccommodationModel2 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel2 = new TransportOfferingModel();
		transportOfferingModel2.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel2.setTransportOffering(transportOfferingModel2);
		final TravellerModel traveller2 = new TravellerModel();
		traveller2.setLabel("TEST_TRAVEL_CODE");
		selectedAccommodationModel2.setTraveller(traveller2);
		final ConfiguredAccommodationModel configuredAccommodationModel2 = new ConfiguredAccommodationModel();
		configuredAccommodationModel2.setIdentifier("TEST_CONFIGURED_ACCOMMODATION_CODE_2");
		selectedAccommodationModel2.setConfiguredAccommodation(configuredAccommodationModel2);
		final SelectedAccommodationModel selectedAccommodationModel3 = new SelectedAccommodationModel();
		final TransportOfferingModel transportOfferingModel3 = new TransportOfferingModel();
		transportOfferingModel3.setCode("TEST_TRANSPORT_OFFERING_CODE");
		selectedAccommodationModel3.setTransportOffering(transportOfferingModel3);
		final TravellerModel traveller3 = new TravellerModel();
		traveller3.setLabel("TEST_TRAVEL_CODE");
		selectedAccommodationModel3.setTraveller(traveller3);
		final ConfiguredAccommodationModel configuredAccommodationModel3 = new ConfiguredAccommodationModel();
		configuredAccommodationModel3.setIdentifier("TEST_CONFIGURED_ACCOMMODATION_CODE");
		selectedAccommodationModel3.setConfiguredAccommodation(configuredAccommodationModel3);
		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		selectedAccommodations.add(selectedAccommodationModel1);
		selectedAccommodations.add(selectedAccommodationModel2);
		selectedAccommodations.add(selectedAccommodationModel3);
		final CartModel cartModel = new CartModel();
		cartModel.setSelectedAccommodations(selectedAccommodations);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(modelService.create(SelectedAccommodationModel.class)).willReturn(new SelectedAccommodationModel());
		travelCommerceCartService.removeSelectedAccommodationFromCart("TEST_TRANSPORT_OFFERING_CODE_4", "TEST_TRAVEL_CODE",
				configuredAccommodationModel3.getIdentifier());
		Mockito.verify(modelService, Mockito.times(0)).remove(Matchers.any(SelectedAccommodationModel.class));
	}

	@Test
	public void testRemoveSelectedAccommodationFromCartForNullSelectedAccommodationsInCart()
	{
		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		final CartModel cartModel = new CartModel();
		cartModel.setSelectedAccommodations(selectedAccommodations);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(modelService.create(SelectedAccommodationModel.class)).willReturn(new SelectedAccommodationModel());
		travelCommerceCartService.removeSelectedAccommodationFromCart("TEST_TRANSPORT_OFFERING_CODE_4", "TEST_TRAVEL_CODE",
				"TEST_CONFIGURED_ACCOMMODATION_CODE");
		Mockito.verify(modelService, Mockito.times(0)).remove(Matchers.any(SelectedAccommodationModel.class));
	}

	@Test
	public void testRemoveCartEntriesForMinODRefNumberWithOutboundReferenceNumber()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = testSetup.createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION, null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));

		given(cartService.getSessionCart()).willReturn(cart);
		given(cartService.hasSessionCart()).willReturn(Boolean.TRUE);

		given(travellerService.getTravellers(Matchers.anyList()))
				.willReturn(Collections.singletonList(testSetup.createTravellerModel(testSetup.createTravellerInfoModel())));
		travelCommerceCartService.removeCartEntriesForMinODRefNumber(0);
		verify(modelService, times(5)).removeAll(Matchers.anyList());
		verify(modelService, times(1)).refresh(Matchers.any(CartModel.class));
	}

	@Test
	public void testRemoveCartEntriesForMinODRefNumberWithInboundReferenceNumber()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = testSetup.createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION, null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));

		final EntryGroup entryGroup1 = testSetup.createEntryGroups();
		final EntryGroup entryGroup2 = testSetup.createEntryGroups();

		cart.setEntryGroups(Stream.of(entryGroup1, entryGroup2).collect(Collectors.toList()));

		given(cartService.getSessionCart()).willReturn(cart);
		given(cartService.hasSessionCart()).willReturn(Boolean.TRUE);

		given(travellerService.getTravellers(Matchers.anyList()))
				.willReturn(Collections.singletonList(testSetup.createTravellerModel(testSetup.createTravellerInfoModel())));
		travelCommerceCartService.removeCartEntriesForMinODRefNumber(1);
		verify(modelService, times(2)).removeAll(Matchers.anyList());
		verify(modelService, times(1)).refresh(Matchers.any(CartModel.class));
	}

	@Test
	public void testRemoveCartEntriesForMinODRefNumberWithoutCart()
	{

		given(cartService.getSessionCart()).willReturn(null);
		given(cartService.hasSessionCart()).willReturn(Boolean.FALSE);

		verify(modelService, times(0)).removeAll(Matchers.anyList());
		verify(modelService, times(0)).refresh(Matchers.any(CartModel.class));
	}

	@Test
	public void testRemoveCartEntriesForODRefNumberWithInboundReferenceNumber()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = testSetup.createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION, null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));

		final EntryGroup entryGroup1 = testSetup.createEntryGroups();
		final EntryGroup entryGroup2 = testSetup.createEntryGroups();

		cart.setEntryGroups(Stream.of(entryGroup1, entryGroup2).collect(Collectors.toList()));

		given(cartService.getSessionCart()).willReturn(cart);
		given(cartService.hasSessionCart()).willReturn(Boolean.TRUE);

		given(travellerService.getTravellers(Matchers.anyList()))
				.willReturn(Collections.singletonList(testSetup.createTravellerModel(testSetup.createTravellerInfoModel())));
		travelCommerceCartService.removeCartEntriesForODRefNumber(1, cart);
		verify(modelService, times(2)).removeAll(Matchers.anyList());
		verify(modelService, times(1)).refresh(Matchers.any(CartModel.class));
	}

	@Test
	public void testRemoveCartEntriesForODRefNumberForSpecialArgumentScenarios()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = testSetup.createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry1 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION, null, product2, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry3 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final AbstractOrderEntryModel entry4 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList());
		final CartModel cart = new CartModel();
		cart.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));

		travelCommerceCartService.removeCartEntriesForODRefNumber(1, null);
		verify(modelService, times(0)).removeAll(Matchers.anyList());
		verify(modelService, times(0)).refresh(Matchers.any(CartModel.class));

		travelCommerceCartService.removeCartEntriesForODRefNumber(1, new CartModel());
		verify(modelService, times(0)).removeAll(Matchers.anyList());
		verify(modelService, times(0)).refresh(Matchers.any(CartModel.class));

		travelCommerceCartService.removeCartEntriesForODRefNumber(null, cart);
		verify(modelService, times(0)).removeAll(Matchers.anyList());
		verify(modelService, times(0)).refresh(Matchers.any(CartModel.class));
	}

	@Test
	public void testAddAutoPickProductsToCart() throws CommerceCartModificationException
	{
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("TEST_TRANSPORT_OFFERING_CODE");

		final List<TransportOfferingModel> cartEntryTransportOfferings = new ArrayList<>();
		cartEntryTransportOfferings.add(transportOffering);

		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("TEST_TRAVE_ROUTE_CODE");
		final Map<String, Object> addBundleToCartParamsMap = new HashMap<>();
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, cartEntryTransportOfferings);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE, travelRouteModel);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, 1);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVELLER, new TravellerModel());


		final BundleTemplateModel rootBundleTemplate = new BundleTemplateModel();
		rootBundleTemplate.setId("ROOTBUNDLE");
		final BundleTemplateModel childRootBundleTemplate1 = new BundleTemplateModel();
		childRootBundleTemplate1.setBundleSelectionCriteria(new PickExactlyNBundleSelectionCriteriaModel());
		final BundleTemplateModel childRootBundleTemplate2 = new BundleTemplateModel();
		childRootBundleTemplate2.setBundleSelectionCriteria(new AutoPickBundleSelectionCriteriaModel());

		final ProductModel productModel1 = new ProductModel();
		final CategoryModel offerGroup1 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel1 = new OfferGroupRestrictionModel();
		offerGroup1.setTravelRestriction(offerGroupRestrictionModel1);
		offerGroup1.setCode("LOUNGEACCESS");
		productModel1.setSupercategories(Stream.of(offerGroup1).collect(Collectors.toList()));

		final ProductModel productModel2 = new ProductModel();
		final CategoryModel offerGroup2 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel2 = new OfferGroupRestrictionModel();
		offerGroup2.setTravelRestriction(offerGroupRestrictionModel2);
		offerGroup2.setCode("PRIORITYBOARDING");
		productModel2.setSupercategories(Stream.of(offerGroup2).collect(Collectors.toList()));

		final List<ProductModel> autoPickProducts = new ArrayList<>();
		autoPickProducts.add(productModel1);
		autoPickProducts.add(productModel2);
		childRootBundleTemplate2.setProducts(autoPickProducts);
		rootBundleTemplate
				.setChildTemplates(Stream.of(childRootBundleTemplate1, childRootBundleTemplate2).collect(Collectors.toList()));

		given(sessionService.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP))
				.willReturn(addBundleToCartParamsMap);
		given(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).willReturn(bundleTemplateModel);
		given(bundleTemplateModel.getParentTemplate()).willReturn(rootBundleTemplate);
		given(travelCommercePriceService
				.isPriceInformationAvailable(productModel2, PriceRowModel.TRAVELROUTECODE, travelRouteModel.getCode()))
				.willReturn(Boolean.TRUE);
		Mockito.doNothing().when(travelCommercePriceService)
				.setPriceAndTaxSearchCriteriaInContext(Matchers.any(PriceLevel.class), Matchers.anyList(), Matchers.anyString());
		travelCommerceCartService.addAutoPickProductsToCart(new ProductModel(), rootBundleTemplate.getId(), 1);
	}

	@Test
	public void testAddPerLegBundleProductToCartWithSingleParameter() throws CommerceCartModificationException
	{
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("TEST_TRANSPORT_OFFERING_CODE");

		final List<TransportOfferingModel> cartEntryTransportOfferings = new ArrayList<>();
		cartEntryTransportOfferings.add(transportOffering);

		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("TEST_TRAVE_ROUTE_CODE");
		final Map<String, Object> addBundleToCartParamsMap = new HashMap<>();
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, cartEntryTransportOfferings);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE, travelRouteModel);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, 1);
		addBundleToCartParamsMap.put(TravelservicesConstants.CART_ENTRY_TRAVELLER, new TravellerModel());


		final BundleTemplateModel rootBundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel childRootBundleTemplate1 = new BundleTemplateModel();
		childRootBundleTemplate1.setBundleSelectionCriteria(new PickExactlyNBundleSelectionCriteriaModel());
		final BundleTemplateModel childRootBundleTemplate2 = new BundleTemplateModel();
		childRootBundleTemplate2.setBundleSelectionCriteria(new AutoPickPerLegBundleSelectionCriteriaModel());

		final ProductModel productModel1 = new ProductModel();
		final CategoryModel offerGroup1 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel1 = new OfferGroupRestrictionModel();
		offerGroup1.setTravelRestriction(offerGroupRestrictionModel1);
		productModel1.setSupercategories(Stream.of(offerGroup1).collect(Collectors.toList()));

		final ProductModel productModel2 = new ProductModel();
		final CategoryModel offerGroup2 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel2 = new OfferGroupRestrictionModel();
		offerGroup2.setTravelRestriction(offerGroupRestrictionModel2);
		offerGroupRestrictionModel2.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG);
		productModel2.setSupercategories(Stream.of(offerGroup2).collect(Collectors.toList()));

		final ProductModel productModel3 = new ProductModel();
		final CategoryModel offerGroup3 = new CategoryModel();
		productModel3.setSupercategories(Stream.of(offerGroup3).collect(Collectors.toList()));

		final ProductModel productModel4 = new ProductModel();
		final CategoryModel offerGroup4 = new CategoryModel();
		final OfferGroupRestrictionModel offerGroupRestrictionModel4 = new OfferGroupRestrictionModel();
		offerGroupRestrictionModel4.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG);
		offerGroup4.setTravelRestriction(offerGroupRestrictionModel4);
		productModel4.setSupercategories(Stream.of(offerGroup4).collect(Collectors.toList()));

		final List<ProductModel> autoPickProducts = new ArrayList<>();
		autoPickProducts.add(productModel1);
		autoPickProducts.add(productModel2);
		autoPickProducts.add(productModel3);
		autoPickProducts.add(productModel4);
		childRootBundleTemplate2.setProducts(autoPickProducts);
		rootBundleTemplate
				.setChildTemplates(Stream.of(childRootBundleTemplate1, childRootBundleTemplate2).collect(Collectors.toList()));
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setQuantityAdded(0);
		given(sessionService.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP))
				.willReturn(addBundleToCartParamsMap);
		given(bundleTemplateService.getBundleTemplateForCode(Matchers.anyString())).willReturn(bundleTemplateModel);
		given(bundleTemplateModel.getParentTemplate()).willReturn(rootBundleTemplate);
		given(travelCommercePriceService
				.isPriceInformationAvailable(productModel2, PriceRowModel.TRAVELROUTECODE, travelRouteModel.getCode()))
				.willReturn(Boolean.TRUE);
		given(travelCommercePriceService
				.isPriceInformationAvailable(productModel4, PriceRowModel.TRAVELROUTECODE, travelRouteModel.getCode()))
				.willReturn(Boolean.FALSE);
		Mockito.doNothing().when(travelCommercePriceService)
				.setPriceAndTaxSearchCriteriaInContext(Matchers.any(PriceLevel.class), Matchers.anyList(), Matchers.anyString());
		when(travelCommerceCartService.addToCart(Matchers.any(CommerceCartParameter.class))).thenReturn(modification);
		travelCommerceCartService.addPerLegBundleProductToCart("BUNDLE_ID");
	}

	protected Map<String, Object> populatePropertiesMap(final List<String> transportOfferingCodes, final String travelRouteCode,
			final List<TravellerModel> travellerList, final int originDestinationRefNumber)
	{
		final Map<String, Object> propertiesMap = new HashMap<>();
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		transportOfferings.add(transportOffering);
		propertiesMap.put(TravelOrderEntryInfoModel.TRANSPORTOFFERINGS, transportOfferings);
		propertiesMap.put(TravelOrderEntryInfoModel.TRAVELROUTE, travelRoute);
		propertiesMap.put(TravelOrderEntryInfoModel.TRAVELLERS, travellerList);
		propertiesMap.put(TravelOrderEntryInfoModel.ORIGINDESTINATIONREFNUMBER, originDestinationRefNumber);
		return propertiesMap;
	}

	private class TestSetup
	{
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
			Set<Integer> entryGroupNumbers = new HashSet<Integer>();
			entryGroupNumbers.add(4);
			entryGroupNumbers.add(5);

			abstractOrderEntryModel.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
			abstractOrderEntryModel.setActive(isActive);
			abstractOrderEntryModel.setProduct(product);
			abstractOrderEntryModel.setType(orderEntryType);
			abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
			abstractOrderEntryModel.setBasePrice(basePrice);
			abstractOrderEntryModel.setTotalPrice(totalPrice);
			abstractOrderEntryModel.setAmendStatus(amendStatus);
			abstractOrderEntryModel.setEntryGroupNumbers(entryGroupNumbers);
			return abstractOrderEntryModel;
		}
		
		private EntryGroup createEntryChildGroups(int groupNumber)
		{
			final EntryGroup childEntryGroup = new EntryGroup();
			childEntryGroup.setGroupNumber(groupNumber);
			return childEntryGroup;
		}
		
		private EntryGroup createEntryGroups()
		{
			final List<EntryGroup> children = new ArrayList();
			children.add(createEntryChildGroups(4));
			children.add(createEntryChildGroups(4));
			final EntryGroup entryGroup = new EntryGroup();
			entryGroup.setChildren(children);
			return entryGroup;
		}

		private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(
				final AccommodationModel accommodationModel, final AccommodationOfferingModel accommodationOfferingModel,
				final RatePlanModel ratePlan, final List<AbstractOrderEntryModel> entries)
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

		private TravellerModel createTravellerModel(final TravellerInfoModel travellerInfoModel)
		{
			final TravellerModel traveller = new TravellerModel();
			traveller.setInfo(travellerInfoModel);
			return traveller;
		}

		private TravellerInfoModel createTravellerInfoModel()
		{
			final TravellerInfoModel travellerInfo = new TravellerInfoModel();
			return travellerInfo;
		}


	}

}
