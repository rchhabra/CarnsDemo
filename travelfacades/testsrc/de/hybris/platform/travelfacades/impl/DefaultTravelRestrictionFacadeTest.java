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

package de.hybris.platform.travelfacades.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultTravelRestrictionFacade;
import de.hybris.platform.travelfacades.strategies.impl.TravelRestrictionStrategy;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravelCategoryService;
import de.hybris.platform.travelservices.services.TravelRestrictionService;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TravelRestrictionFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelRestrictionFacadeTest
{
	@Mock
	private TravelCommerceCartService travelCommerceCartService;
	@Mock
	private CartService cartService;
	@Mock
	private ProductService productService;
	@Mock
	private TravelRestrictionStrategy travelRestrictionStrategy;
	@Mock
	private TravelCategoryService travelCategoryService;
	@Mock
	private BookingService bookingService;
	@Mock
	private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;
	@Mock
	private TravelRestrictionService travelRestrictionService;

	DefaultTravelRestrictionFacade defaultTravelRestrictionFacade;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultTravelRestrictionFacade = new DefaultTravelRestrictionFacade();
		defaultTravelRestrictionFacade.setTravelCommerceCartService(travelCommerceCartService);
		defaultTravelRestrictionFacade.setCartService(cartService);
		defaultTravelRestrictionFacade.setProductService(productService);
		defaultTravelRestrictionFacade.setTravelRestrictionStrategy(travelRestrictionStrategy);
		defaultTravelRestrictionFacade.setTravelCategoryService(travelCategoryService);
		defaultTravelRestrictionFacade.setBookingService(bookingService);
		defaultTravelRestrictionFacade.setTravelRestrictionConverter(travelRestrictionConverter);
		defaultTravelRestrictionFacade.setTravelRestrictionService(travelRestrictionService);
	}

	@Test
	public void testCheckIfProductCanBeAdded()
	{
		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);

		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setQuantity(new Long(1));
		given(bookingService.getOrderEntry(Matchers.any(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyListOf(String.class), Matchers.anyListOf(String.class), Matchers.anyBoolean()))
						.willReturn(abstractOrderEntryModel);

		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		given(travelRestrictionStrategy.checkQuantityForTravelRestriction(Matchers.any(), Matchers.anyLong()))
				.willReturn(Boolean.FALSE);

		final boolean result = defaultTravelRestrictionFacade.checkIfProductCanBeAdded(productCode, 1, "", new ArrayList<String>(),
				"");
		Assert.assertFalse(result);
	}


	@Test
	public void testGetCategoryRestrictionErrors()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		productModel.setProductType(ProductType.FARE_PRODUCT);
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);

		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid("adult");

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0004");

		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("routeCode");

		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		abstractOrderEntryModel.setQuantity(new Long(1));
		abstractOrderEntryModel.setProduct(productModel);
		orderEntryInfo.setTravellers(Stream.of(travellerModel).collect(Collectors.toList()));
		orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));
		orderEntryInfo.setTravelRoute(travelRouteModel);
		abstractOrderEntryModel.setTravelOrderEntryInfo(orderEntryInfo);

		cartModel.setEntries(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));

		final OfferGroupRestrictionModel travelRestrictionModel = new OfferGroupRestrictionModel();
		travelRestrictionModel.setTravellerMinOfferQty(4);

		final CategoryModel categoryModel = new CategoryModel()
		{
			@Override
			public String getName()
			{
				return "categoryName";
			}
		};
		categoryModel.setTravelRestriction(travelRestrictionModel);

		given(travelCategoryService.getAncillaryCategories(Matchers.anyListOf(String.class)))
				.willReturn(Stream.of(categoryModel).collect(Collectors.toList()));

		given(travelCommerceCartService.getOrderEntriesForCategory(Matchers.any(), Matchers.any(), Matchers.anyString(),
				Matchers.anyListOf(String.class), Matchers.anyString()))
						.willReturn(Stream.of(new AbstractOrderEntryModel()).collect(Collectors.toList()));

		given(travelRestrictionStrategy.checkQuantityForMandatoryTravelRestriction(Matchers.any(), Matchers.anyLong()))
				.willReturn(Boolean.FALSE);

		final Map<String, String> result = defaultTravelRestrictionFacade.getCategoryRestrictionErrors();
		Assert.assertNotNull(result);
		Assert.assertTrue(result.containsKey("categoryName"));
	}

	@Test
	public void testGetTravelRestrictionForProduct()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		productModel.setProductType(ProductType.FARE_PRODUCT);

		final TravelRestrictionModel travelRestriction = new TravelRestrictionModel();
		productModel.setTravelRestriction(travelRestriction);

		final TravelRestrictionData travelRestrictionData = new TravelRestrictionData();

		given(productService.getProductForCode(productCode)).willReturn(productModel);
		given(travelRestrictionService.getAddToCartCriteria(productModel)).willReturn(AddToCartCriteriaType.PER_LEG_PER_PAX);
		given(travelRestrictionConverter.convert(travelRestriction)).willReturn(travelRestrictionData);
		final TravelRestrictionData travelRestrictionAct = defaultTravelRestrictionFacade
				.getTravelRestrictionForProduct(productCode);
		Assert.assertEquals(travelRestrictionData, travelRestrictionAct);

		productModel.setTravelRestriction(null);
		final TravelRestrictionData travelRestrictionNull = defaultTravelRestrictionFacade
				.getTravelRestrictionForProduct(productCode);
		Assert.assertNull(travelRestrictionNull);
	}

	@Test
	public void tesGetTravelRestrictionForCategory()
	{
		final String categoryCode = "cat0004";
		final CategoryModel categoryModel = new CategoryModel();
		categoryModel.setCode(categoryCode);

		final TravelRestrictionData travelRestrictionData = new TravelRestrictionData();

		final OfferGroupRestrictionModel offerGroupRestriction = new OfferGroupRestrictionModel();
		categoryModel.setTravelRestriction(offerGroupRestriction);
		given(travelCategoryService.getCategoryForCode(categoryCode)).willReturn(categoryModel);
		given(travelRestrictionConverter.convert(offerGroupRestriction)).willReturn(travelRestrictionData);

		final TravelRestrictionData travelRestrictionAct = defaultTravelRestrictionFacade
				.getTravelRestrictionForCategory(categoryCode);
		Assert.assertEquals(travelRestrictionData, travelRestrictionAct);

		categoryModel.setTravelRestriction(null);
		final TravelRestrictionData travelRestrictionNull = defaultTravelRestrictionFacade
				.getTravelRestrictionForCategory(categoryCode);
		Assert.assertNull(travelRestrictionNull);
	}

	@Test
	public void testGetAddToCartCriteria()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		productModel.setProductType(ProductType.FARE_PRODUCT);

		final TravelRestrictionModel travelRestriction = new TravelRestrictionModel();
		productModel.setTravelRestriction(travelRestriction);

		given(productService.getProductForCode(productCode)).willReturn(productModel);
		given(travelRestrictionService.getAddToCartCriteria(productModel)).willReturn(AddToCartCriteriaType.PER_LEG_PER_PAX);

		final String addToCartCriteria = defaultTravelRestrictionFacade.getAddToCartCriteria(productCode);
		Assert.assertEquals(AddToCartCriteriaType.PER_LEG_PER_PAX.getCode(), addToCartCriteria);
	}

	@Test
	public void testCheckCategoryRestrictions()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		productModel.setProductType(ProductType.FARE_PRODUCT);
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		final CartModel cartModel = new CartModel();
		given(cartService.getSessionCart()).willReturn(cartModel);

		final TravellerModel travellerModel = new TravellerModel();
		travellerModel.setUid("adult");

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0004");

		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("routeCode");

		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		abstractOrderEntryModel.setQuantity(new Long(1));
		abstractOrderEntryModel.setProduct(productModel);
		orderEntryInfo.setTravellers(Stream.of(travellerModel).collect(Collectors.toList()));
		orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));
		orderEntryInfo.setTravelRoute(travelRouteModel);
		abstractOrderEntryModel.setTravelOrderEntryInfo(orderEntryInfo);

		cartModel.setEntries(Stream.of(abstractOrderEntryModel).collect(Collectors.toList()));

		final OfferGroupRestrictionModel travelRestrictionModel = new OfferGroupRestrictionModel();
		travelRestrictionModel.setTravellerMinOfferQty(4);

		final CategoryModel categoryModel = new CategoryModel()
		{
			@Override
			public String getName()
			{
				return "categoryName";
			}
		};
		categoryModel.setTravelRestriction(travelRestrictionModel);

		given(travelCategoryService.getAncillaryCategories(Matchers.anyListOf(String.class)))
				.willReturn(Stream.of(categoryModel).collect(Collectors.toList()));

		given(travelCommerceCartService.getOrderEntriesForCategory(Matchers.any(), Matchers.any(), Matchers.anyString(),
				Matchers.anyListOf(String.class), Matchers.anyString()))
						.willReturn(Stream.of(new AbstractOrderEntryModel()).collect(Collectors.toList()));

		given(travelRestrictionStrategy.checkQuantityForMandatoryTravelRestriction(Matchers.any(), Matchers.anyLong()))
				.willReturn(Boolean.FALSE);

		final boolean isEmpty = defaultTravelRestrictionFacade.checkCategoryRestrictions();
		Assert.assertTrue(isEmpty == false);
	}
}