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

package de.hybris.platform.travelfacades.fare.search.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.AutoPickPerLegBundleSelectionCriteriaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelBundleTemplatePopulatorTest
{
	@InjectMocks
	private final TravelBundleTemplatePopulator<BundleTemplateModel, BundleTemplateData> populator = new TravelBundleTemplatePopulator<BundleTemplateModel, BundleTemplateData>();

	@Mock
	private Converter<FareProductModel, FareProductData> fareProductConverter;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	private BundleTemplateModel source;

	@Before
	public void setup()
	{
		source = TestDataSetup.createSourceSampleData();

		// fare product converter mock
		given(fareProductConverter.convert(Mockito.any(FareProductModel.class))).willReturn(TestDataSetup.getFareProducts().get(0));

		// product converter mock
		given(productConverter.convert(Mockito.any(ProductModel.class))).willReturn(TestDataSetup.getProducts().get(1));

	}

	@Test
	public void populateTargetTest()
	{
		final TravelBundleTemplateData travelBundleTemplate = new TravelBundleTemplateData();
		final BundleTemplateData bundleTemplate = new BundleTemplateData();

		final List<BundleTemplateData> bundleTemplates = new ArrayList<>();
		bundleTemplates.add(travelBundleTemplate);
		bundleTemplates.add(bundleTemplate);

		bundleTemplates.forEach(target -> {
			populator.populate(source, target);
		});

		// check results
		Assert.assertFalse(travelBundleTemplate.getFareProducts().isEmpty());
		Assert.assertFalse(travelBundleTemplate.getNonFareProducts().isEmpty());

		Assert.assertEquals(2, travelBundleTemplate.getFareProducts().size());
		Assert.assertEquals(1, travelBundleTemplate.getNonFareProducts().size());

		// check fare products
		Assert.assertEquals("ORTC6", travelBundleTemplate.getFareProducts().get(0).getCode());
		Assert.assertEquals("O", travelBundleTemplate.getFareProducts().get(0).getBookingClass());
		Assert.assertEquals("ORTC6", travelBundleTemplate.getFareProducts().get(0).getFareBasisCode());

		// check non fare products
		Assert.assertTrue(travelBundleTemplate.getNonFareProducts().containsKey("ECO003"));
		Assert.assertEquals("ACPECOSEAT1", travelBundleTemplate.getNonFareProducts().get("ECO003").get(0).getCode());
		Assert.assertEquals("Economy Plus Seat", travelBundleTemplate.getNonFareProducts().get("ECO003").get(0).getName());
	}

	/**
	 * Inner class to set-up test data
	 */
	private static class TestDataSetup
	{
		private static final List<FareProductData> fareProducts = new ArrayList<>();
		private static final List<ProductData> products = new ArrayList<>();

		public static BundleTemplateModel createSourceSampleData()
		{
			fareProducts.clear();
			products.clear();

			final BundleTemplateModel source = createBundleTemplateModel("ECO001", "Economy Bundle Template", "1.0",
					BundleType.ECONOMY);
			source.setPromotional(false);
			source.setIgnoreRules(false);
			source.setChildTemplates(createChildTemplates());
			return source;
		}

		private static List<BundleTemplateModel> createChildTemplates()
		{

			final BundleTemplateModel childTemplate1 = createBundleTemplateModel("ECO002", "Child Economy Bundle Template 1", "1.0",
					BundleType.ECONOMY);
			childTemplate1.setProducts(new ArrayList<ProductModel>());
			childTemplate1.getProducts().add(createFareProductModel(ProductType.FARE_PRODUCT, "ORTC6", "O", "ORTC6", null));
			childTemplate1.getProducts()
					.add(createFareProductModel(ProductType.ACCOMMODATION, "ACPECOSEAT1", null, null, "Economy Plus Seat"));
			final PickExactlyNBundleSelectionCriteriaModel criteria1 = new PickExactlyNBundleSelectionCriteriaModel();
			childTemplate1.setBundleSelectionCriteria(criteria1);

			final BundleTemplateModel childTemplate2 = createBundleTemplateModel("ECO003", "Child Economy Bundle Template 2", "1.0",
					BundleType.ECONOMY);
			childTemplate2.setProducts(new ArrayList<ProductModel>());
			childTemplate2.getProducts()
					.add(createFareProductModel(ProductType.ACCOMMODATION, "ACPECOSEAT1", null, null, "Economy Plus Seat"));
			final AutoPickBundleSelectionCriteriaModel criteria2 = new AutoPickBundleSelectionCriteriaModel();
			childTemplate2.setBundleSelectionCriteria(criteria2);

			final BundleTemplateModel childTemplate3 = createBundleTemplateModel("ECO004", "Child Economy Bundle Template 3", "1.0",
					BundleType.ECONOMY);
			childTemplate3.setProducts(new ArrayList<ProductModel>());
			childTemplate3.getProducts()
					.add(createFareProductModel(ProductType.ACCOMMODATION, "ACPECOSEAT1", null, null, "Economy Plus Seat"));
			final AutoPickPerLegBundleSelectionCriteriaModel criteria3 = new AutoPickPerLegBundleSelectionCriteriaModel();
			childTemplate3.setBundleSelectionCriteria(criteria3);

			final BundleTemplateModel childTemplate4 = createBundleTemplateModel("ECO004", "Child Economy Bundle Template 4", "1.0",
					BundleType.ECONOMY);
			childTemplate4.setProducts(new ArrayList<ProductModel>());
			childTemplate4.getProducts()
					.add(createFareProductModel(ProductType.ACCOMMODATION, "ACPECOSEAT1", null, null, "Economy Plus Seat"));

			final List<BundleTemplateModel> childTemplates = new ArrayList<>();
			childTemplates.add(childTemplate1);
			childTemplates.add(childTemplate2);
			childTemplates.add(childTemplate3);
			childTemplates.add(childTemplate4);
			return childTemplates;
		}

		private static ProductModel createFareProductModel(final ProductType productType, final String code,
				final String bookingClass, final String fareBasisCode, final String name)
		{
			final FareProductModel product = new FareProductModel()
			{
				@Override
				public String getName()
				{
					return name;
				}
			};
			product.setProductType(productType);
			product.setCode(code);
			product.setBookingClass(bookingClass);
			product.setFareBasisCode(fareBasisCode);
			product.setName(name, Locale.ENGLISH);

			if (StringUtils.equals(productType.getCode(), ProductType.FARE_PRODUCT.getCode()))
			{
				populateFareProductData(product);
			}
			else
			{
				populateProductData(product);
			}

			return product;
		}

		// simulate the fareProductConverter output
		private static void populateFareProductData(final FareProductModel source)
		{
			final FareProductData target = new FareProductData();
			target.setCode(source.getCode());
			target.setBookingClass(source.getBookingClass());
			target.setFareBasisCode(source.getFareBasisCode());
			fareProducts.add(target);
		}

		// simulate the productConverter output
		private static void populateProductData(final ProductModel source)
		{
			final FareProductData target = new FareProductData();
			target.setCode(source.getCode());
			target.setName(source.getName());
			products.add(target);
		}

		private static BundleTemplateModel createBundleTemplateModel(final String templateId, final String templateName,
				final String templateVersion, final BundleType templateType)
		{
			final BundleTemplateModel template = new BundleTemplateModel()
			{
				@Override
				public String getName(final Locale loc)
				{
					return templateName;
				}
			};

			template.setId(templateId);
			template.setName(templateName, Locale.ENGLISH);
			template.setVersion(templateVersion);
			template.setType(templateType);

			return template;
		}

		public static List<FareProductData> getFareProducts()
		{
			return fareProducts;
		}

		public static List<ProductData> getProducts()
		{
			return products;
		}


	}
}
