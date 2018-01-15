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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite {@link DefaultTravelCommerceCartService}.
 */
@IntegrationTest
public class DefaultTravelCommerceCartServiceIntegrationTest extends ServicelayerTest
{
	private static final int BUNDLENO_REQUESTED = 1;
	private static final String TRANSPORT_OFFERING_CODE = "EZY8323201512070810_test";
	private static final Logger LOG = Logger.getLogger(DefaultTravelCommerceCartServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String PRODUCT_MODEL_CODE_FAREPRODUCT1 = "FPLGWCDGOCAT1Test";
	private static final String PRODUCT_MODEL_CODE_ANCILLARYPRODUCT = "EXTRABAG20Test";
	private static final String TRAVEL_ROUTE_CODE = "LGW_CDG_test";
	private static final int NEW_BUNDLE = -1;

	@Resource
	private DefaultTravelCommerceCartService travelCommerceCartService;

	@Resource
	private UserService userService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private UnitService unitService;

	@Resource
	private CartService cartService;

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Resource
	private TravellerService travellerService;

	@Resource
	private TravelCommercePriceService travelCommercePriceService;

	@Resource
	private Map<String, String> offerGroupToOriginDestinationMapping;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private TravelRouteService travelRouteService;

	@Resource
	private TransportOfferingService transportOfferingService;

	@Resource
	private BaseSiteService baseSiteService;

	private CartModel travelMasterCart;

	private ProductModel fareProduct1;

	private ProductModel ancillaryProduct;

	private BundleTemplateModel economyBundleTemplate;

	private UnitModel unitModel;

	/**
	 * Setup method to set data before executing the test
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultTravelBundleCommerceCartServiceIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		importCsv("/travelservices/test/testTravelBundleCommerceCartService.csv", "utf-8");
		importCsv("/travelservices/test/testBundleData.csv", "utf-8");

		LOG.info("Finished data for DefaultTravelBundleCommerceCartServiceIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add(TRANSPORT_OFFERING_CODE);
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		transportOfferingCodes.forEach(
				transportOffering -> transportOfferings.add(transportOfferingService.getTransportOffering(transportOffering)));

		final TravelRouteModel travelRoute = travelRouteService.getTravelRoute(TRAVEL_ROUTE_CODE);

		final TravellerModel travellerModel = travellerService.createTraveller("PASSENGER", "adult", "adult1", 0,
				StringUtils.EMPTY);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, transportOfferings);
		params.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, 0);
		params.put(TravelservicesConstants.CART_ENTRY_PRICELEVEL, "default");
		params.put(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE, travelRoute);
		params.put(TravelservicesConstants.CART_ENTRY_TRAVELLER, travellerModel);
		sessionService.setAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP, params);

		setupProducts();
		setupBundleTemplates();
		setupAncillaryProduct();
		final UserModel travacc = userService.getUserForUID("travacc");
		final Collection<CartModel> cartModels = travacc.getCarts();
		assertEquals(1, cartModels.size());
		travelMasterCart = cartModels.iterator().next();

		modelService.detachAll();
	}

	/**
	 * Test add bundle product to cart
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartBundle() throws CommerceCartModificationException
	{
		// Add bundle product to cart
		travelCommerceCartService.addToCart(travelMasterCart, fareProduct1, 1, unitModel, true, NEW_BUNDLE, economyBundleTemplate,
				false, "<no xml>");

		assertEquals(2, travelMasterCart.getEntries().size());
		final AbstractOrderEntryModel cartEntryModel = travelMasterCart.getEntries().iterator().next();
		assertEquals(PRODUCT_MODEL_CODE_FAREPRODUCT1, cartEntryModel.getProduct().getCode());
		assertEquals(unitModel, cartEntryModel.getUnit());
		assertEquals(1, cartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(2), cartEntryModel.getEntryGroupNumbers().stream().findFirst().get());

	}

	/**
	 * Test to add transport offering code to a cart entry for a bundle
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntryForBundle() throws CommerceCartModificationException
	{
		// Add bundle product to cart
		travelCommerceCartService.addToCart(travelMasterCart, fareProduct1, 1, unitModel, true, NEW_BUNDLE, economyBundleTemplate,
				false, "<no xml>");
		final List<TravellerModel> travellerList = new ArrayList<>();
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add(TRANSPORT_OFFERING_CODE);
		travelCommerceCartService.addPropertiesToCartEntryForBundle(travelMasterCart, BUNDLENO_REQUESTED, fareProduct1,
				populatePropertiesMap(transportOfferingCodes, TRAVEL_ROUTE_CODE, travellerList, 0));

		final AbstractOrderEntryModel cartEntryModel = travelMasterCart.getEntries().iterator().next();
		assertEquals(TRANSPORT_OFFERING_CODE,
				cartEntryModel.getTravelOrderEntryInfo().getTransportOfferings().stream().findFirst().get().getCode());


	}

	/**
	 * Test to add ancillary Product to Cart
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddAncillaryProductToCart() throws CommerceCartModificationException
	{
		// Add ancillary product to cart
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(travelMasterCart);
		parameter.setQuantity(1);
		parameter.setProduct(ancillaryProduct);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(true);

		final CommerceCartModification modification = travelCommerceCartService.addToCart(parameter);
		assertEquals(1, modification.getQuantityAdded());
	}

	/**
	 * Test to add transport offering code to a cart entry for a bundle
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddTransportOfferingCodeToCartEntry() throws CommerceCartModificationException
	{
		// Add ancillary product to cart
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(travelMasterCart);
		parameter.setQuantity(1);
		parameter.setProduct(ancillaryProduct);
		parameter.setUnit(unitModel);
		parameter.setCreateNewEntry(true);

		final CommerceCartModification modification = travelCommerceCartService.addToCart(parameter);

		final List<TravellerModel> travellerList = new ArrayList<>();
		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add(TRANSPORT_OFFERING_CODE);
		travelCommerceCartService.addPropertiesToCartEntry(travelMasterCart, modification.getEntry().getEntryNumber(),
				ancillaryProduct, populatePropertiesMap(transportOfferingCodes, TRAVEL_ROUTE_CODE, travellerList, 0));

		final AbstractOrderEntryModel cartEntryModel = travelMasterCart.getEntries().iterator().next();
		assertEquals(TRANSPORT_OFFERING_CODE,
				cartEntryModel.getTravelOrderEntryInfo().getTransportOfferings().stream().findFirst().get().getCode());


	}

	private void setupBundleTemplates()
	{

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		economyBundleTemplate = getBundleTemplate("FareProductEconomyBundleTest", catalogVersionModel);

	}

	private BundleTemplateModel getBundleTemplate(final String bundleId, final CatalogVersionModel catalogVersionModel)
	{
		final BundleTemplateModel exampleModel = new BundleTemplateModel();
		exampleModel.setId(bundleId);
		exampleModel.setCatalogVersion(catalogVersionModel);

		return flexibleSearchService.getModelByExample(exampleModel);
	}

	private void setupProducts()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		fareProduct1 = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_FAREPRODUCT1);

		unitModel = unitService.getUnitForCode("pieces");
		fareProduct1.setUnit(unitModel);
	}

	private void setupAncillaryProduct()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		ancillaryProduct = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_ANCILLARYPRODUCT);

		unitModel = unitService.getUnitForCode("pieces");
		ancillaryProduct.setUnit(unitModel);
	}

	private Map<String, Object> populatePropertiesMap(final List<String> transportOfferingCodes, final String travelRouteCode,
			final List<TravellerModel> travellerList, final int originDestinationRefNumber)
	{
		final Map<String, Object> propertiesMap = new HashMap<>();
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		transportOfferingCodes.forEach(
				transportOffering -> transportOfferings.add(transportOfferingService.getTransportOffering(transportOffering)));
		propertiesMap.put(TravelOrderEntryInfoModel.TRANSPORTOFFERINGS, transportOfferings);
		final TravelRouteModel travelRoute = travelRouteService.getTravelRoute(travelRouteCode);
		propertiesMap.put(TravelOrderEntryInfoModel.TRAVELROUTE, travelRoute);
		propertiesMap.put(TravelOrderEntryInfoModel.TRAVELLERS, travellerList);
		propertiesMap.put(TravelOrderEntryInfoModel.ORIGINDESTINATIONREFNUMBER, originDestinationRefNumber);
		return propertiesMap;
	}

}
