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

package de.hybris.platform.travelservices.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultTravelCategoryDaoIntegrationTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(DefaultTravelCategoryDaoIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private ModelService modelService;

	@Resource
	private DefaultTravelCategoryDao travelCategoryDao;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CategoryService categoryService;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating data for DefaultTravelCategoryDaoIntegrationTest ...");

		final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId("testAirlineProductCatalog");
		modelService.save(catalog);
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion("Online");
		modelService.save(catalogVersion);

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testAirlineProductCatalog", "Online");
		//Setting up test data for 5 products, 3 are ancillary and in stock(out of which 2 belongs to same category and one belong to different category),
		// one is ancillary and not in stock and last one is non-ancillary and in stock
		//if the flexible query implementation is correct, "getAncillaryCategoriesTest" should receive 2 categories(because of DISTINCT check) in the list returned by the
		//travelCategoryDao.getAncillaryCategories(transportOfferingsCodes), and those 2 categories should be testPRIORITYCHECKIN and testPRIORITYBOARDING

		createAncillaryProductWithCategoryAndStock();
		createAncillaryProductWithSameCategoryAndStock();
		createAncillaryProductWithDifferentCategoryAndStock();
		createAncillaryProductWithCategoryAndNoStock();
		createNonAncillaryProductWithCategoryAndStock();

		LOG.info("Finished data for DefaultProductsFeaturesDaoIntegrationTest ");
	}

	@Test
	public void getAncillaryCategoriesTest()
	{
		final List<String> transportOfferingsCodes = new ArrayList<>();
		transportOfferingsCodes.add("testTransportOffering1");
		transportOfferingsCodes.add("testTransportOffering2");
		transportOfferingsCodes.add("testTransportOffering3");
		transportOfferingsCodes.add("testTransportOffering4");
		transportOfferingsCodes.add("testTransportOffering5");
		final List<CategoryModel> ancillaryCategories = travelCategoryDao.getAncillaryCategories(transportOfferingsCodes);
		assertEquals(2, ancillaryCategories.size());
		final List<String> returnedCategoryCodes = new ArrayList<>();
		for (final CategoryModel categoryModel : ancillaryCategories)
		{
			returnedCategoryCodes.add(categoryModel.getCode());
		}
		assertTrue(returnedCategoryCodes.contains("testPRIORITYCHECKIN"));
		assertTrue(returnedCategoryCodes.contains("testPRIORITYBOARDING"));
		assertFalse(returnedCategoryCodes.contains("testLOUNGEACCESS"));
		assertFalse(returnedCategoryCodes.contains("testNONANCILLARYCATEGORY"));
	}

	private void createAncillaryProductWithCategoryAndStock()
	{
		final CategoryModel category = modelService.create(CategoryModel.class);
		category.setCode("testPRIORITYCHECKIN");
		category.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		modelService.save(category);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode("testPRICHECKIN1");
		productModel.setName("Priority Check In");
		productModel.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		productModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		productModel.setProductType(ProductType.ANCILLARY);
		modelService.save(productModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		category.setProducts(products);
		modelService.save(category);
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testAirlineProductCatalog", "Online");


		final TransportOfferingModel transportOffering = modelService.create(TransportOfferingModel.class);
		transportOffering.setCode("testTransportOffering1");
		final VendorModel vendor = modelService.create(VendorModel.class);
		vendor.setCode("vendorCode");
		modelService.save(vendor);
		transportOffering.setVendor(vendor);
		modelService.save(transportOffering);

		final StockLevelModel res = modelService.create(StockLevelModel.class);
		res.setProductCode(productModel.getCode());
		res.setAvailable(30);
		res.setWarehouse(transportOffering);
		modelService.save(res);
	}

	private void createAncillaryProductWithSameCategoryAndStock()
	{
		final CategoryModel category = categoryService.getCategoryForCode("testPRIORITYCHECKIN");

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode("testPRICHECKIN2");
		productModel.setName("Priority Check In");
		productModel.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		productModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		productModel.setProductType(ProductType.ANCILLARY);
		modelService.save(productModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		products.addAll(category.getProducts());
		category.setProducts(products);
		modelService.save(category);
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testAirlineProductCatalog", "Online");


		final TransportOfferingModel transportOffering = modelService.create(TransportOfferingModel.class);
		transportOffering.setCode("testTransportOffering2");
		final VendorModel vendor = modelService.create(VendorModel.class);
		vendor.setCode("vendorCode");
		modelService.save(vendor);
		transportOffering.setVendor(vendor);
		modelService.save(transportOffering);

		final StockLevelModel res = modelService.create(StockLevelModel.class);
		res.setProductCode(productModel.getCode());
		res.setAvailable(30);
		res.setWarehouse(transportOffering);
		modelService.save(res);
	}

	private void createAncillaryProductWithDifferentCategoryAndStock()
	{
		final CategoryModel category = modelService.create(CategoryModel.class);
		category.setCode("testPRIORITYBOARDING");
		category.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		modelService.save(category);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode("testPRIBOARDING1");
		productModel.setName("Priority Boarding");
		productModel.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		productModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		productModel.setProductType(ProductType.ANCILLARY);
		modelService.save(productModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		category.setProducts(products);
		modelService.save(category);
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testAirlineProductCatalog", "Online");


		final TransportOfferingModel transportOffering = modelService.create(TransportOfferingModel.class);
		transportOffering.setCode("testTransportOffering3");
		final VendorModel vendor = modelService.create(VendorModel.class);
		vendor.setCode("vendorCode");
		modelService.save(vendor);
		transportOffering.setVendor(vendor);
		modelService.save(transportOffering);

		final StockLevelModel res = modelService.create(StockLevelModel.class);
		res.setProductCode(productModel.getCode());
		res.setAvailable(30);
		res.setWarehouse(transportOffering);
		modelService.save(res);
	}

	private void createAncillaryProductWithCategoryAndNoStock()
	{
		final CategoryModel category = modelService.create(CategoryModel.class);
		category.setCode("testLOUNGEACCESS");
		category.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		modelService.save(category);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode("testLOUNGEACC1");
		productModel.setName("Lounge Access");
		productModel.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		productModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		productModel.setProductType(ProductType.ANCILLARY);
		modelService.save(productModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		category.setProducts(products);
		modelService.save(category);
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testAirlineProductCatalog", "Online");
	}

	private void createNonAncillaryProductWithCategoryAndStock()
	{
		final CategoryModel category = modelService.create(CategoryModel.class);
		category.setCode("testNONANCILLARYCATEGORY");
		category.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		modelService.save(category);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode("testNonAncillaryProduct");
		productModel.setName("Non Ancillary Product");
		productModel.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		productModel.setCatalogVersion(catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"));
		productModel.setProductType(ProductType.FARE_PRODUCT);
		modelService.save(productModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		category.setProducts(products);
		modelService.save(category);

		final TransportOfferingModel transportOffering = modelService.create(TransportOfferingModel.class);
		transportOffering.setCode("testTransportOffering5");
		final VendorModel vendor = modelService.create(VendorModel.class);
		vendor.setCode("vendorCode");
		modelService.save(vendor);
		transportOffering.setVendor(vendor);
		modelService.save(transportOffering);

		final StockLevelModel res = modelService.create(StockLevelModel.class);
		res.setProductCode(productModel.getCode());
		res.setAvailable(30);
		res.setWarehouse(transportOffering);
		modelService.save(res);
	}
}