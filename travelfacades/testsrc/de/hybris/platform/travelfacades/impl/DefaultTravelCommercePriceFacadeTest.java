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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultTravelCommercePriceFacade;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TravelCommercePriceFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelCommercePriceFacadeTest
{
	@Mock
	private ProductService productService;
	@Mock
	private TravelCommercePriceService travelCommercePriceService;
	@Mock
	private TransportOfferingFacade transportOfferingFacade;
	@Mock
	private BundleRuleService bundleRuleService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private TransportOfferingService transportOfferingService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration config;
	@Mock
	private TravelRulesService travelRulesService;
	@Mock
	private PriceDataFactory priceDataFactory;


	@InjectMocks
	DefaultTravelCommercePriceFacade defaultTravelCommercePriceFacade;


	@Test
	public void testGetPriceInformation()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("GBP", 10D, true));
		given(travelCommercePriceService.getPriceInformation(Matchers.any(), Matchers.anyString(), Matchers.anyString()))
				.willReturn(priceInformation);

		final PriceInformation result = defaultTravelCommercePriceFacade.getPriceInformation(productCode);
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetPriceInformationForTravelCriteria()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("GBP", 10D, true));
		given(travelCommercePriceService.getPriceInformation(Matchers.any(), Matchers.anyString(), Matchers.anyString()))
				.willReturn(priceInformation);

		final PriceInformation result = defaultTravelCommercePriceFacade.getPriceInformation(productCode, "sectorCode", "LTN_CDG");
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetPriceInformationByHierarchyForDefaultPrice()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		final PriceInformation priceInformation = new PriceInformation(new PriceValue("GBP", 10D, true));
		given(travelCommercePriceService.getPriceInformation(Matchers.any(), Matchers.eq(PriceRowModel.TRANSPORTOFFERINGCODE),
				Matchers.anyString())).willReturn(null);
		given(travelCommercePriceService.getPriceInformation(Matchers.any(), Matchers.eq(PriceRowModel.TRAVELSECTORCODE),
				Matchers.anyString())).willReturn(null);
		given(travelCommercePriceService.getPriceInformation(Matchers.any(), Matchers.eq(PriceRowModel.TRAVELROUTECODE),
				Matchers.anyString())).willReturn(null);
		given(travelCommercePriceService.getPriceInformation(Matchers.any(), Matchers.eq(null), Matchers.eq(null)))
				.willReturn(priceInformation);
		given(config.getString("pricing.lookup.attributes")).willReturn("route,sector,transportOffering");
		given(configurationService.getConfiguration()).willReturn(config);
		final PriceInformation result = defaultTravelCommercePriceFacade.getPriceInformationByHierarchy(productCode, "toCode",
				"sectorCode", "routeCode");
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetPriceLevelInfo()
	{
		final String productCode = "prod0004";
		given(transportOfferingFacade.isMultiSectorRoute(Matchers.anyListOf(String.class))).willReturn(Boolean.TRUE);

		final PriceLevel priceLevel = new PriceLevel();
		given(travelCommercePriceService.getPriceLevelInfo(Matchers.any(), Matchers.anyListOf(String.class), Matchers.anyString(),
				Matchers.anyBoolean())).willReturn(priceLevel);

		final PriceLevel result = defaultTravelCommercePriceFacade.getPriceLevelInfo(productCode, new ArrayList<String>(),
				"routeCode");
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetPriceLevelInfoByHierarchy()
	{
		final PriceLevel priceLevel = new PriceLevel();
		given(travelCommercePriceService.getPriceLevelInfoByHierarchy(Matchers.any(), Matchers.anyString(), Matchers.anyString()))
				.willReturn(priceLevel);

		final ProductModel productModel = new ProductModel();
		final PriceLevel result = defaultTravelCommercePriceFacade.getPriceLevelInfoByHierarchy(productModel, "toCode",
				"routeCode");
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetPriceInformationByProductPriceBundleRule()
	{
		final String productCode = "prod0004";
		final ProductModel productModel = new ProductModel();
		given(productService.getProductForCode(productCode)).willReturn(productModel);

		final ChangeProductPriceBundleRuleModel changePriceBundleRule = new ChangeProductPriceBundleRuleModel();
		changePriceBundleRule.setPrice(new BigDecimal(10));
		given(bundleRuleService.getChangePriceBundleRule(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
				.willReturn(changePriceBundleRule);

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);

		final PriceInformation result = defaultTravelCommercePriceFacade.getPriceInformationByProductPriceBundleRule(null,
				"prod0004");
		Assert.assertNotNull(result);
	}

	@Test
	public void testSetPriceAndTaxSearchCriteriaInContext()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("adult");
		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setPassengerType(passengerTypeData);
		final TravellerData travellerData = new TravellerData();
		travellerData.setTravellerInfo(passengerInformationData);

		willDoNothing().given(travelCommercePriceService).setPriceAndTaxSearchCriteriaInContext(Matchers.any(),
				Matchers.anyListOf(String.class), Matchers.eq("adult"));

		defaultTravelCommercePriceFacade.setPriceAndTaxSearchCriteriaInContext(new PriceLevel(), new ArrayList<String>(),
				travellerData);

		verify(travelCommercePriceService).setPriceAndTaxSearchCriteriaInContext(Matchers.any(), Matchers.anyListOf(String.class),
				Matchers.eq("adult"));
	}

	@Test
	public void testSetPriceSearchCriteriaInContext()
	{
		willDoNothing().given(travelCommercePriceService).setPriceSearchCriteriaInContext(Matchers.any());

		defaultTravelCommercePriceFacade.setPriceSearchCriteriaInContext(new PriceLevel());

		verify(travelCommercePriceService).setPriceSearchCriteriaInContext(Matchers.any());
	}

	@Test
	public void testSetTaxSearchCriteriaInContext()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("adult");
		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setPassengerType(passengerTypeData);
		final TravellerData travellerData = new TravellerData();
		travellerData.setTravellerInfo(passengerInformationData);

		willDoNothing().given(travelCommercePriceService).setTaxSearchCriteriaInContext(Matchers.anyListOf(String.class),
				Matchers.eq("adult"));

		defaultTravelCommercePriceFacade.setTaxSearchCriteriaInContext(new ArrayList<String>(), travellerData);

		verify(travelCommercePriceService).setTaxSearchCriteriaInContext(Matchers.anyListOf(String.class), Matchers.eq("adult"));
	}

	@Test
	public void testIsPriceInformationAvailable()
	{
		final ProductModel productModel = new ProductModel();
		given(travelCommercePriceService.isPriceInformationAvailable(Matchers.any(), Matchers.anyString(), Matchers.anyString()))
				.willReturn(Boolean.TRUE);

		final boolean result = defaultTravelCommercePriceFacade.isPriceInformationAvailable(productModel, "sectorCode", "LTN_CDG");
		Assert.assertTrue(result);
	}

	@Test
	public void testAddPropertyPriceLevelToCartEntry()
	{
		willDoNothing().given(travelCommercePriceService).addPropertyPriceLevelToCartEntry(Matchers.any(), Matchers.anyString(),
				Matchers.anyInt());

		defaultTravelCommercePriceFacade.addPropertyPriceLevelToCartEntry(new PriceLevel(), "sectorCode", 0);

		verify(travelCommercePriceService).addPropertyPriceLevelToCartEntry(Matchers.any(), Matchers.anyString(),
				Matchers.anyInt());
	}

	@Test
	public void testGetBookingFeesAndTaxes()
	{
		given(travelRulesService.getTotalFee()).willReturn(20d);
		Assert.assertEquals(BigDecimal.valueOf(20d), defaultTravelCommercePriceFacade.getBookingFeesAndTaxes());
	}

	@Test
	public void testcreatePriceData()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);

		final PriceData price = new PriceData();
		price.setValue(BigDecimal.valueOf(20));
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(20.0), "GBP")).willReturn(price);

		Assert.assertNotNull(defaultTravelCommercePriceFacade.createPriceData(20));
	}

	@Test
	public void testSetPriceAndTaxSearchCriteriaInContextWithoutTravellerData()
	{
		willDoNothing().given(travelCommercePriceService).setPriceAndTaxSearchCriteriaInContext(Matchers.any(PriceLevel.class),
				Matchers.anyList(), Matchers.any());
		defaultTravelCommercePriceFacade.setPriceAndTaxSearchCriteriaInContext(new PriceLevel(), new ArrayList<String>());
		verify(travelCommercePriceService).setPriceAndTaxSearchCriteriaInContext(Matchers.any(), Matchers.anyListOf(String.class),
				Matchers.any());
	}

	@Test
	public void testGetDueAmountWhenTotalAmountGreaterThanPaidAmount()
	{
		final AccommodationReservationData totalAmount = new AccommodationReservationData();
		final RateData totalRate = new RateData();
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(100));
		totalRate.setActualRate(priceData);
		totalAmount.setTotalRate(totalRate);

		final PriceData amountPaid = new PriceData();
		amountPaid.setValue(BigDecimal.valueOf(70));

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);

		final PriceData mockedDueAmount = new PriceData();
		mockedDueAmount.setValue(BigDecimal.valueOf(30));
		given(priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.willReturn(mockedDueAmount);

		final PriceData dueAmount = defaultTravelCommercePriceFacade.getDueAmount(totalAmount, amountPaid);
		Assert.assertEquals(dueAmount.getValue(), mockedDueAmount.getValue());
	}

	@Test
	public void testGetDueAmountWhenPaidAmountGreaterThanTotalAmount()
	{
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		final RateData totalRate = new RateData();
		final PriceData priceData = new PriceData();
		priceData.setValue(new BigDecimal(100));
		totalRate.setActualRate(priceData);
		reservationData.setTotalRate(totalRate);

		final PriceData amountPaid = new PriceData();
		amountPaid.setValue(new BigDecimal(170));

		Assert.assertNull(defaultTravelCommercePriceFacade.getDueAmount(reservationData, amountPaid));
	}
}
