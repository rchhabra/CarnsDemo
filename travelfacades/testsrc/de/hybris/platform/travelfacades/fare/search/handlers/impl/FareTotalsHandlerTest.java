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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FareTotalsHandlerTest
{
	@InjectMocks
	private final FareTotalsHandler handler = new FareTotalsHandler();

	@Mock
	private TravelRulesService travelRulesService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	private FareSearchRequestData fareSearchRequestData;
	private FareSelectionData fareSelectionData;

	private List<PricedItineraryData> pricedItineraries;
	private List<ScheduledRouteData> scheduledRoutes;


	@Before
	public void setup()
	{
		fareSearchRequestData = new FareSearchRequestData();
		fareSelectionData = new FareSelectionData();

		pricedItineraries = new ArrayList<>();
		scheduledRoutes = new ArrayList<>();

		final ItineraryPricingInfoData ipInfoData = new ItineraryPricingInfoData();
		ipInfoData.setAvailable(true);

		final TravelBundleTemplateData travelBundleTemplateData = new TravelBundleTemplateData();
		travelBundleTemplateData.setId("travelBundleTemplate");
		travelBundleTemplateData.setAvailable(true);
		travelBundleTemplateData.setFareProducts(Collections.emptyList());
		travelBundleTemplateData.setNonFareProducts(Collections.emptyMap());
		travelBundleTemplateData.setIncludedAncillaries(Collections.emptyList());

		final List<TravelBundleTemplateData> bundleTemplates = new ArrayList<>();
		bundleTemplates.add(travelBundleTemplateData);

		ipInfoData.setPtcFareBreakdownDatas(createPTCBreakdown());

		ipInfoData.setBundleTemplates(bundleTemplates);

		final PricedItineraryData piData = new PricedItineraryData();
		piData.setAvailable(true);
		piData.setItineraryPricingInfos(new ArrayList<>());
		piData.getItineraryPricingInfos().add(ipInfoData);

		pricedItineraries.add(piData);

		fareSelectionData.setPricedItineraries(pricedItineraries);

		final PriceData price = new PriceData();
		price.setValue(new BigDecimal("30.00"));

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GB");

		Mockito.when(travelCommercePriceFacade.createPriceData(Matchers.anyDouble())).thenReturn(price);
	}

	@Test
	public void availableItineraryPricingInfoPriceTotalsTest()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(Mockito.anyString())).thenReturn("adult");

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertEquals(new BigDecimal("30.00"), fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()
				.get(0).getTotalFare().getBasePrice().getValue());
		Assert.assertEquals(new BigDecimal("30.00"), fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()
				.get(0).getTotalFare().getTotalPrice().getValue());
		Assert.assertEquals(2,
				fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getTotalFare().getTaxes().size());
		Assert.assertEquals(new BigDecimal("5.00"), fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos()
				.get(0).getTotalFare().getTaxes().get(0).getPrice().getValue());
	}

	@Test
	public void availableItineraryNoConfigurationTest()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString(Mockito.anyString())).thenReturn(StringUtils.EMPTY);

		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0)
				.getPtcFareBreakdownDatas().get(0).getPassengerFare().getPerPax());
	}

	@Test
	public void unavailableItineraryPricingInfoTest()
	{
		fareSelectionData.getPricedItineraries().get(0).setAvailable(false);
		handler.handle(scheduledRoutes, fareSearchRequestData, fareSelectionData);

		Assert.assertNull(fareSelectionData.getPricedItineraries().get(0).getItineraryPricingInfos().get(0).getTotalFare());
	}

	private static List<PTCFareBreakdownData> createPTCBreakdown()
	{
		final List<PTCFareBreakdownData> ptcFareBreakdowns = new ArrayList<>();
		ptcFareBreakdowns.add(adultPTCBreakdown("adult", 1, "25.00"));
		ptcFareBreakdowns.add(adultPTCBreakdown("child", 1, "10.00"));

		return ptcFareBreakdowns;
	}

	private static PTCFareBreakdownData adultPTCBreakdown(final String passengerTypeCode, final int qty, final String farePrice)
	{
		final PassengerTypeData passengerType = new PassengerTypeData();
		passengerType.setCode(passengerTypeCode);

		final PassengerTypeQuantityData PassengerTypeQuantity = new PassengerTypeQuantityData();
		PassengerTypeQuantity.setQuantity(qty);
		PassengerTypeQuantity.setPassengerType(passengerType);

		final FareInfoData fareInfo = new FareInfoData();
		fareInfo.setFareDetails(createFareDetails("ECO001"));

		final List<FareInfoData> fareInfos = new ArrayList<>();
		fareInfos.add(fareInfo);

		final PassengerFareData passengerFare = new PassengerFareData();
		passengerFare.setBaseFare(new PriceData());
		passengerFare.getBaseFare().setValue(new BigDecimal(farePrice));
		passengerFare.setTotalFare(new PriceData());
		passengerFare.getTotalFare().setValue(new BigDecimal(farePrice));

		final TaxData taxData = new TaxData();
		taxData.setPrice(new PriceData());
		taxData.getPrice().setValue(new BigDecimal("5.00"));
		passengerFare.setTaxes(new ArrayList<>());
		passengerFare.getTaxes().add(taxData);

		final PTCFareBreakdownData ptcBreakdown = new PTCFareBreakdownData();
		ptcBreakdown.setPassengerTypeQuantity(PassengerTypeQuantity);
		ptcBreakdown.setFareInfos(fareInfos);
		ptcBreakdown.setPassengerFare(passengerFare);

		return ptcBreakdown;
	}

	private static List<FareDetailsData> createFareDetails(final String fareCode)
	{
		final PriceData farePrice = new PriceData();
		farePrice.setValue(new BigDecimal("10.00"));

		final FareProductData fareProduct = new FareProductData();
		fareProduct.setCode(fareCode);
		fareProduct.setPrice(farePrice);

		final FareDetailsData fareDetail = new FareDetailsData();
		fareDetail.setFareProduct(fareProduct);

		final List<FareDetailsData> fareDetails = new ArrayList<>();
		fareDetails.add(fareDetail);

		return fareDetails;
	}
}
