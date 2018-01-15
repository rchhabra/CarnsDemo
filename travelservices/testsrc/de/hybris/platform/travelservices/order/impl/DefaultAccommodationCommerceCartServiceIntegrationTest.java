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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.daos.OrderDao;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.dao.AbstractOrderEntryGroupDao;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForEntryGroupCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesPaymentActionStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAccommodationCommerceCartServiceIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESTORE_UID = "testStore";
	protected static final String PRODUCT_CODE = "Product1";

	@Resource
	DefaultAccommodationCommerceCartService accommodationCommerceCartService;
	@Resource
	private CartService cartService;
	@Resource
	private ModelService modelService;
	@Resource
	private OrderDao orderDao;
	@Resource
	private AbstractOrderEntryTypeService abstractOrderEntryTypeService;
	@Resource
	private TypeService typeService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private UserService userService;
	@Resource
	private BaseStoreService baseStoreService;
	@Resource
	private GuidKeyGenerator guidKeyGenerator;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private Map<String, ChangeDatesPaymentActionStrategy> changeDatesPaymentActionStrategyMap;
	@Resource
	private OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy;
	@Resource
	private BookingService bookingService;
	@Resource
	private AbstractOrderEntryGroupDao abstractOrderEntryGroupDao;


	private CartModel masterCart;
	private CurrencyModel currency;
	private CustomerModel user;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		currency = commonI18NService.getCurrency("EUR");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		assertNotNull(catalogVersionModel);
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));

		final UserModel user = createUser("user");

		setupCart(user);
		userService.setCurrentUser(user);

	}

	private UserModel createUser(final String uid)
	{
		user = modelService.create(CustomerModel.class);
		user.setUid(uid);
		user.setName(uid);
		modelService.save(user);
		return user;
	}

	private void setupCart(final UserModel user)
	{
		masterCart = modelService.create(CartModel.class);
		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TEST_BASESTORE_UID);
		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();

		baseStore.setExternalTaxEnabled(Boolean.FALSE);
		baseStore.setUid(TEST_BASESTORE_UID);
		masterCart.setStore(baseStore);
		masterCart.setCurrency(currency);
		masterCart.setDate(new Date());
		masterCart.setNet(Boolean.TRUE);
		masterCart.setUser(user);
		masterCart.setGuid(guidKeyGenerator.generate().toString());
		masterCart.setSite(baseSite);


		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(createCartEntry(masterCart, "goblins"));
		masterCart.setEntries(entryList);

		modelService.save(masterCart);
		cartService.setSessionCart(masterCart);
		userService.setCurrentUser(user);
	}

	private CartEntryModel createCartEntry(final CartModel cartModel, final String unitCode)
	{
		final ProductModel productModel = createProduct(ProductModel.class, "testProduct");

		final UnitModel unitModel = createUnit(unitCode);

		final CartEntryModel cartEntry = modelService.create(CartEntryModel.class);
		cartEntry.setQuantity(Long.valueOf(1));

		cartEntry.setUnit(unitModel);
		cartEntry.setProduct(productModel);
		cartEntry.setEntryNumber(1);
		cartEntry.setOrder(cartModel);
		return cartEntry;
	}

	private UnitModel createUnit(final String unitCode)
	{
		final UnitModel unitModel = modelService.create(UnitModel.class);
		unitModel.setUnitType("awsome");
		unitModel.setCode(unitCode);
		return unitModel;
	}

	private ProductModel createProduct(final Class type, final String productCode)
	{
		final ProductModel productModel = modelService.create(type);
		productModel.setCode(productCode);
		return productModel;
	}

	private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroup()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = modelService
				.create(AccommodationOrderEntryGroupModel.class);
		accommodationOrderEntryGroup.setRoomStayRefNumber(0);
		final AccommodationOfferingModel accommodationOfferingModel = modelService.create(AccommodationOfferingModel.class);
		accommodationOfferingModel.setCode("testAccommodationOffering");
		final VendorModel vendor = modelService.create(VendorModel.class);
		vendor.setCode("accommodationOfferingVendor");
		accommodationOfferingModel.setVendor(vendor);
		final AccommodationModel accommodation = modelService.create(AccommodationModel.class);
		accommodation.setCode("testAccommodation");
		final RatePlanModel ratePlan = modelService.create(RatePlanModel.class);
		ratePlan.setCode("testRatePlan");
		accommodationOrderEntryGroup.setAccommodationOffering(accommodationOfferingModel);
		accommodationOrderEntryGroup.setAccommodation(accommodation);
		accommodationOrderEntryGroup.setRatePlan(ratePlan);

		accommodationOrderEntryGroup.setStartingDate(new Date("11/07/2017"));
		accommodationOrderEntryGroup.setEndingDate(new Date("12/07/2017"));

		return accommodationOrderEntryGroup;
	}

	@Test
	public void testPopulateAccommodationDetailsOnRoomRateEntry()
	{
		accommodationCommerceCartService.populateAccommodationDetailsOnRoomRateEntry(1, Collections.emptyList());
		assertTrue(masterCart.getEntries().iterator().next().getActive());
	}

	@Test
	public void testAmendOrderEntryGroup()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "libiya");
		cartEntryModel.setEntryNumber(2);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		final OrderModel originalOrder = modelService.create(OrderModel.class);
		final PaymentTransactionModel paymentTransaction = modelService.create(PaymentTransactionModel.class);
		paymentTransaction.setAbstractOrderEntries(Arrays.asList(cartEntryModel));
		final PaymentTransactionEntryModel paymentTransactionEntry = modelService.create(PaymentTransactionEntryModel.class);
		paymentTransactionEntry.setType(PaymentTransactionType.CAPTURE);
		paymentTransactionEntry.setAmount(BigDecimal.valueOf(100d));

		originalOrder.setPaymentTransactions(Arrays.asList(paymentTransaction));
		originalOrder.setCurrency(currency);
		originalOrder.setDate(new Date());
		originalOrder.setNet(Boolean.TRUE);
		originalOrder.setUser(user);
		masterCart.setOriginalOrder(originalOrder);
		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);

		assertTrue(accommodationCommerceCartService.amendOrderEntryGroup(accommodationOrderEntryGroup, Arrays.asList(2), "REFUND"));

	}

	@Test
	public void testRollBackEntries()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct2");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		accommodationCommerceCartService.rollbackAccommodationEntries("testAccommodation", "testAccommodationOffering",
				"testRatePlan");
		assertTrue(CollectionUtils.isEmpty(masterCart.getEntries()));
	}

	@Test
	public void testCleanupCartBeforeAddition()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "america");
		cartEntryModel.setEntryNumber(4);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct3");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		accommodationCommerceCartService.cleanupCartBeforeAddition("testAccommodationOffering", "12/07/2017", "13/07/2017");
		assertTrue(CollectionUtils.isEmpty(masterCart.getEntries()));
	}

	@Test
	public void testEmptyCart()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "london");
		cartEntryModel.setEntryNumber(4);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct4");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		accommodationCommerceCartService.emptyCart();
		assertTrue(CollectionUtils.isEmpty(masterCart.getEntries()));
	}

	@Test
	public void testGetNumberOfEntryGroups()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "london");
		cartEntryModel.setEntryNumber(4);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct4");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);

		assertEquals(1, accommodationCommerceCartService.getNumberOfEntryGroupsInCart());
	}

	@Test
	public void testGetEntriesForProductAndAccommodation()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct5");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		assertTrue(Objects.nonNull(accommodationCommerceCartService.getEntriesForProductAndAccommodation(masterCart, product,
				(CartEntryModel) cartEntryModel)));
	}

	@Test
	public void testGetMaxRoomStayRefNumber()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct6");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		assertEquals(0, accommodationCommerceCartService.getMaxRoomStayRefNumber());
	}

	@Test
	public void testRemoveAccommodationOrderEntryGroup()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct7");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		accommodationCommerceCartService.removeAccommodationOrderEntryGroup(0);
		assertTrue(CollectionUtils.isEmpty(masterCart.getEntries()));
	}

	@Test
	public void testGetCurrentAccommodationOffering()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct10");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		assertEquals("testAccommodationOffering", accommodationCommerceCartService.getCurrentAccommodationOffering());
	}

	@Test
	public void testRemoveRoomStay()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct8");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		accommodationCommerceCartService.removeRoomStay(0);
		assertTrue(CollectionUtils.isEmpty(masterCart.getEntries()));
	}

	@Test
	public void testIsNewRoomInCart()
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = createAccommodationOrderEntryGroup();

		final AbstractOrderEntryModel cartEntryModel = createCartEntry(masterCart, "indonesia");
		cartEntryModel.setEntryNumber(3);
		cartEntryModel.setEntryGroup(accommodationOrderEntryGroup);
		cartEntryModel.setTotalPrice(100d);
		cartEntryModel.setType(OrderEntryType.ACCOMMODATION);
		cartEntryModel.setActive(true);
		cartEntryModel.setAmendStatus(AmendStatus.NEW);

		final ProductModel product = createProduct(RoomRateProductModel.class, "testRoomRateProduct9");
		cartEntryModel.setProduct(product);

		accommodationOrderEntryGroup.setEntries(Arrays.asList(cartEntryModel));

		modelService.save(accommodationOrderEntryGroup);

		masterCart.setEntries(Arrays.asList(cartEntryModel));
		modelService.save(masterCart);
		assertTrue(accommodationCommerceCartService.isNewRoomInCart());
	}

}
