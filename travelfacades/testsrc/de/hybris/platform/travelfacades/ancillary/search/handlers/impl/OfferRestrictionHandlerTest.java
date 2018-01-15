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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** Unit tests for OfferRestrictionHandler */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferRestrictionHandlerTest
{
	@Mock
	private TravelRestrictionFacade travelRestrictionFacade;

   @Mock
   private ProductService productService;

   @Mock
   private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;

   @InjectMocks
   private final OfferRestrictionHandler offerRestrictionHandler = new OfferRestrictionHandler();

   /**
    * Test method to verify the populate method of OfferRestrictionHandler.
    */
   @Test
   public void populateOfferRestrictionTest()
   {

      final TestDataSetup testDataSetup = new TestDataSetup();

      final List<ProductData> meals = new ArrayList<>();
      meals.add(testDataSetup.createProductData("Meal1"));
      meals.add(testDataSetup.createProductData("Meal2"));

      final List<ProductData> holdItems = new ArrayList<>();
      holdItems.add(testDataSetup.createProductData("HoldItem1"));
      holdItems.add(testDataSetup.createProductData("HoldItem2"));
      // holdItems.add(testDataSetup.createProductData("HoldItem3"));

      final List<OfferGroupData> offerGroups = new ArrayList<>();
      offerGroups.add(testDataSetup.createOfferGroupDataFromProducts("Meals", meals));
      offerGroups.add(testDataSetup.createOfferGroupDataFromProducts("HoldItems", holdItems));

      final OfferResponseData offerResponseData = testDataSetup.createOfferResponseData(offerGroups);

      final TravelRestrictionModel travelRestrictionModel = testDataSetup.createTravelRestriction();
      final TravelRestrictionData travelRestrictionData = testDataSetup.createTravelRestrictionData();

		final ProductModel prodMeal1 = testDataSetup.createProductModel("Meal1");
      prodMeal1.setTravelRestriction(travelRestrictionModel);
		final ProductModel prodMeal2 = testDataSetup.createProductModel("Meal2");
      prodMeal2.setTravelRestriction(travelRestrictionModel);
		final ProductModel prodHoldItem1 = testDataSetup.createProductModel("prodHoldItem1");
      prodHoldItem1.setTravelRestriction(travelRestrictionModel);
		final ProductModel prodHoldItem2 = testDataSetup.createProductModel("prodHoldItem2");
      prodHoldItem2.setTravelRestriction(travelRestrictionModel);
      given(productService.getProductForCode("Meal1")).willReturn(prodMeal1);
      given(productService.getProductForCode("Meal2")).willReturn(prodMeal2);
      given(productService.getProductForCode("HoldItem1")).willReturn(prodHoldItem1);
      given(productService.getProductForCode("HoldItem2")).willReturn(prodHoldItem2);

      given(travelRestrictionConverter.convert(travelRestrictionModel)).willReturn(travelRestrictionData);
		given(travelRestrictionFacade.getAddToCartCriteria(BDDMockito.anyString()))
				.willReturn(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());

		final List<ProductModel> productModels = Arrays.asList(prodMeal1, prodMeal2, prodHoldItem1, prodHoldItem2);

      offerRestrictionHandler.handle(null, offerResponseData);

      for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
      {
         for (final OriginDestinationOfferInfoData odOption : offerGroupData.getOriginDestinationOfferInfos())
         {
            for (final OfferPricingInfoData opInfo : odOption.getOfferPricingInfos())
            {
               Assert.assertNotNull(opInfo.getTravelRestriction());

					final Optional<ProductModel> opt = productModels.stream()
                        .filter(productModel -> productModel.getCode().equals(opInfo.getProduct().getCode())).findAny();
               if (opt.isPresent())
               {
                  final TravelRestrictionModel trModel = opt.get().getTravelRestriction();
                  final TravelRestrictionData trData = opInfo.getTravelRestriction();

                  Assert.assertEquals(trModel.getEffectiveDate(), trData.getEffectiveDate());
                  Assert.assertEquals(trModel.getExpireDate(), trData.getExpireDate());
                  Assert.assertEquals(trModel.getTravellerMinOfferQty(), trData.getTravellerMinOfferQty());
                  Assert.assertEquals(trModel.getTravellerMaxOfferQty(), trData.getTravellerMaxOfferQty());
                  Assert.assertEquals(trModel.getTripMinOfferQty(), trData.getTripMinOfferQty());
                  Assert.assertEquals(trModel.getTripMaxOfferQty(), trData.getTripMaxOfferQty());
						Assert.assertEquals(trModel.getAddToCartCriteria().getCode(), trData.getAddToCartCriteria());
               }

            }

         }

      }

   }

   /**
    * Test method to verify the populate method of OfferRestrictionHandler when there is no TravelRestrictionModel set against
    * the ProductModel.
    */
   @Test
   public void nullTravelRestrictionModelTest()
   {
      final TestDataSetup testDataSetup = new TestDataSetup();

      final List<ProductData> products = new ArrayList<>();
      products.add(testDataSetup.createProductData("product"));

      final List<OfferGroupData> offerGroups = new ArrayList<>();
      offerGroups.add(testDataSetup.createOfferGroupDataFromProducts("NoRestrictionProducts", products));

      final OfferResponseData offerResponseData = testDataSetup.createOfferResponseData(offerGroups);

      final ProductModel productModel = testDataSetup.createProductModel("product");
      given(productService.getProductForCode("product")).willReturn(productModel);
		given(travelRestrictionFacade.getAddToCartCriteria(BDDMockito.anyString()))
				.willReturn(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());

      offerRestrictionHandler.handle(null, offerResponseData);

      for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
      {
         for (final OriginDestinationOfferInfoData odOption : offerGroupData.getOriginDestinationOfferInfos())
         {
            for (final OfferPricingInfoData opInfo : odOption.getOfferPricingInfos())
            {
               Assert.assertNotNull(opInfo.getTravelRestriction());
               final TravelRestrictionData trData = opInfo.getTravelRestriction();

               Assert.assertNull(trData.getEffectiveDate());
               Assert.assertNull(trData.getExpireDate());
               final Integer defaultMin = Integer.valueOf(TravelfacadesConstants.DEFAULT_MIN_QUANTITY_RESTRICTION);
               final Integer defaultMax = Integer.valueOf(TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION);
               Assert.assertEquals(defaultMin, trData.getTravellerMinOfferQty());
               Assert.assertEquals(defaultMax, trData.getTravellerMaxOfferQty());
               Assert.assertEquals(defaultMin, trData.getTripMinOfferQty());
               Assert.assertEquals(defaultMax, trData.getTripMaxOfferQty());
					Assert.assertEquals(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode(), trData.getAddToCartCriteria());

            }
         }
      }
   }

   /**
    * Test data setup
    */
   private class TestDataSetup
   {

      private OfferResponseData createOfferResponseData(final List<OfferGroupData> offerGroups)
      {
         final OfferResponseData offerResponseData = new OfferResponseData();
         offerResponseData.setOfferGroups(offerGroups);
         return offerResponseData;

      }

      private OfferGroupData createOfferGroupDataFromProducts(final String categoryCode, final List<ProductData> meals)
      {
         final List<OfferPricingInfoData> offerPricingInfoDatas = meals.stream()
                  .map(productData -> createOfferPricingInfoData(productData)).collect(Collectors.toList());

         final List<OriginDestinationOfferInfoData> odOptions = new ArrayList<>();
         odOptions.add(createOriginDestinationOption(offerPricingInfoDatas, 0));
         odOptions.add(createOriginDestinationOption(offerPricingInfoDatas, 1));
         return createOfferGroupData("Meal", odOptions);
      }

      private OfferGroupData createOfferGroupData(final String categoryCode, final List<OriginDestinationOfferInfoData> odOptions)
      {
         final OfferGroupData offerGroupData = new OfferGroupData();
         offerGroupData.setCode(categoryCode);
         offerGroupData.setOriginDestinationOfferInfos(odOptions);
         return offerGroupData;
      }

      private OriginDestinationOfferInfoData createOriginDestinationOption(final List<OfferPricingInfoData> opInfoData,
               final int originDestinationRefNumber)
      {
         final OriginDestinationOfferInfoData odData = new OriginDestinationOfferInfoData();
         odData.setOfferPricingInfos(opInfoData);
         odData.setOriginDestinationRefNumber(originDestinationRefNumber);
         return odData;
      }

      private ProductData createProductData(final String code)
      {
         final ProductData productData = new ProductData();
         productData.setCode(code);
         return productData;
      }

      private OfferPricingInfoData createOfferPricingInfoData(final ProductData productData)
      {
         final OfferPricingInfoData offerPricingInfoData = new OfferPricingInfoData();
         offerPricingInfoData.setProduct(productData);
         return offerPricingInfoData;
      }

		private ProductModel createProductModel(final String code)
      {
			final ProductModel productModel = new ProductModel();
         productModel.setCode(code);
         return productModel;
      }

      private TravelRestrictionModel createTravelRestriction()
      {
         final TravelRestrictionModel travelRestriction = new TravelRestrictionModel();
         travelRestriction.setEffectiveDate(null);
         travelRestriction.setExpireDate(null);
         travelRestriction.setTravellerMinOfferQty(0);
         travelRestriction.setTravellerMaxOfferQty(1);
         travelRestriction.setTripMinOfferQty(0);
         travelRestriction.setTripMaxOfferQty(1);
			travelRestriction.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG_PER_PAX);
         return travelRestriction;
      }

      private TravelRestrictionData createTravelRestrictionData()
      {
         final TravelRestrictionData travelRestriction = new TravelRestrictionData();
         travelRestriction.setEffectiveDate(null);
         travelRestriction.setExpireDate(null);
         travelRestriction.setTravellerMinOfferQty(0);
         travelRestriction.setTravellerMaxOfferQty(1);
         travelRestriction.setTripMinOfferQty(0);
         travelRestriction.setTripMaxOfferQty(1);
			travelRestriction.setAddToCartCriteria(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());
         return travelRestriction;
      }

   }
}