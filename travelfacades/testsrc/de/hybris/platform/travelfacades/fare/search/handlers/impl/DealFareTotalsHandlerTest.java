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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DealFareTotalsHandler}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealFareTotalsHandlerTest
{
	@InjectMocks
	DealFareTotalsHandler handler;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Before
	public void prepare()
	{
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("GBP");
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
	}

	@Test
	public void testHandle()
	{
		final TestDataSetup testData = new TestDataSetup();
		final PTCFareBreakdownData adultPTC = new PTCFareBreakdownData();
		final PassengerFareData adultPassengerFare = testData.createPassengerFareData(100d, 200d);
		adultPTC.setPassengerFare(adultPassengerFare);

		final PTCFareBreakdownData childPTC = new PTCFareBreakdownData();
		final PassengerFareData childPassengerFare = testData.createPassengerFareData(50d, 100d);
		childPTC.setPassengerFare(childPassengerFare);

		final ItineraryPricingInfoData ipid = new ItineraryPricingInfoData();
		ipid.setPtcFareBreakdownDatas(Stream.of(adultPTC, childPTC).collect(Collectors.toList()));

		final TravelBundleTemplateData bundleTemplate = new TravelBundleTemplateData();
		bundleTemplate.setIncludedAncillaries(Collections.emptyList());
		ipid.setBundleTemplates(Stream.of(bundleTemplate).collect(Collectors.toList()));

		final TotalFareData totalFareData = new TotalFareData();
		ipid.setTotalFare(totalFareData);

		final PriceData totalRate = testData.createPrice(150d);
		final PriceData totalWasRate = testData.createPrice(300d);

		Mockito.when(travelCommercePriceFacade.createPriceData(150d)).thenReturn(totalRate);
		Mockito.when(travelCommercePriceFacade.createPriceData(300d)).thenReturn(totalWasRate);

		handler.populateTotals(ipid);
		assertTrue(Objects.equals(300d, ipid.getTotalFare().getWasRate().getValue().doubleValue()));
		assertTrue(Objects.equals(150d, ipid.getTotalFare().getTotalPrice().getValue().doubleValue()));

	}

	class TestDataSetup
	{
		public PassengerFareData createPassengerFareData(final double totalValue, final double wasValue)
		{
			final PassengerFareData passengerFareData = new PassengerFareData();
			passengerFareData.setTotalFare(createPrice(totalValue));
			passengerFareData.setWasRate(createPrice(wasValue));
			return passengerFareData;
		}

		public PriceData createPrice(final double value)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(value));
			return priceData;
		}
	}
}
