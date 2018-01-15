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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.impl.DefaultPriceDataFactory;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PackageFareBundlesPriceDifferenceHandlerTest
{
	@InjectMocks
	PackageFareBundlesPriceDifferenceHandler packageFareBundlesPriceDifferenceHandler;

	@Mock
	private final PriceDataFactory priceDataFactory = new DefaultPriceDataFactory();

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		packageFareBundlesPriceDifferenceHandler.handle(packageRequestData, packageResponseData);

		packageResponseData.setAvailable(true);
		final TransportPackageResponseData transportPackageResponse = new TransportPackageResponseData();
		packageResponseData.setTransportPackageResponse(transportPackageResponse);

		final FareSelectionData fareSearchResponse = new FareSelectionData();
		transportPackageResponse.setFareSearchResponse(fareSearchResponse);

		final List<PricedItineraryData> pricedItineraries = new ArrayList<PricedItineraryData>();
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		pricedItineraries.add(pricedItineraryData);
		fareSearchResponse.setPricedItineraries(pricedItineraries);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(true);
		itineraryPricingInfoData.setSelected(true);
		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);

		final List<TaxData> taxes = new ArrayList<>();

		final TotalFareData totalFare = new TotalFareData();
		final PriceData basePrice = new PriceData();
		basePrice.setValue(BigDecimal.valueOf(15));
		basePrice.setCurrencyIso("GBP");
		totalFare.setBasePrice(basePrice);
		final PriceData totalPrice = new PriceData();
		totalPrice.setValue(BigDecimal.valueOf(25));
		totalPrice.setCurrencyIso("GBP");
		totalFare.setTotalPrice(totalPrice);
		final PriceData wasRate = new PriceData();
		wasRate.setValue(BigDecimal.valueOf(35));
		totalFare.setWasRate(wasRate);
		itineraryPricingInfoData.setTotalFare(totalFare);
		totalFare.setTaxes(taxes);


		final PriceData priceDifference = new PriceData();
		priceDifference.setValue(BigDecimal.valueOf(0));

		Mockito.when(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.thenReturn(priceDifference);

		packageFareBundlesPriceDifferenceHandler.handle(packageRequestData, packageResponseData);

	}
}
