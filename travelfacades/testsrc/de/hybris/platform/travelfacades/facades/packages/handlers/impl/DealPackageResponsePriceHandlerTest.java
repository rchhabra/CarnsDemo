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
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RateData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.StandardPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.impl.DefaultPriceDataFactory;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealPackageResponsePriceHandlerTest
{
	@InjectMocks
	DealPackageResponsePriceHandler dealPackageResponsePriceHandler;

	@Mock
	private final PriceDataFactory priceDataFactory = new DefaultPriceDataFactory();

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		packageResponseData.setAvailable(false);
		dealPackageResponsePriceHandler.handle(packageRequestData, packageResponseData);

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

		final List<TaxData> taxes = new ArrayList<>();

		final TotalFareData totalFare = new TotalFareData();
		final PriceData basePrice = new PriceData();
		basePrice.setValue(BigDecimal.valueOf(15));
		basePrice.setCurrencyIso("GBP");
		totalFare.setBasePrice(basePrice);
		final PriceData totalPrice = new PriceData();
		totalPrice.setValue(BigDecimal.valueOf(25));
		totalFare.setTotalPrice(totalPrice);
		final PriceData wasRate = new PriceData();
		wasRate.setValue(BigDecimal.valueOf(35));
		totalFare.setWasRate(wasRate);
		itineraryPricingInfoData.setTotalFare(totalFare);
		totalFare.setTaxes(taxes);

		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);

		final AccommodationPackageResponseData accommodationPackageResponse = new AccommodationPackageResponseData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();

		final List<RoomStayData> roomStays = new ArrayList<>();
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();

		final RateData baseRate = new RateData();
		baseRate.setBasePrice(basePrice);
		final PriceData actualRate = new PriceData();
		actualRate.setValue(BigDecimal.valueOf(30));
		baseRate.setActualRate(actualRate);
		baseRate.setWasRate(wasRate);
		baseRate.setTaxes(taxes);

		roomStay.setBaseRate(baseRate);
		roomStays.add(roomStay);
		accommodationAvailabilityResponse.setRoomStays(roomStays);

		accommodationPackageResponse.setAccommodationAvailabilityResponse(accommodationAvailabilityResponse);
		packageResponseData.setAccommodationPackageResponse(accommodationPackageResponse);

		final List<StandardPackageResponseData> standardPackageResponses = new ArrayList<>();
		final StandardPackageResponseData standardPackageResponse = new StandardPackageResponseData();
		final List<PackageProductData> packageProducts = new ArrayList<>();
		final PackageProductData packageProduct = new PackageProductData();
		packageProduct.setPrice(baseRate);
		packageProduct.setQuantity(1);
		packageProducts.add(packageProduct);
		standardPackageResponse.setPackageProducts(packageProducts);
		standardPackageResponses.add(standardPackageResponse);
		packageResponseData.setStandardPackageResponses(standardPackageResponses);

		Mockito.when(travelCommercePriceFacade.getBookingFeesAndTaxes()).thenReturn(BigDecimal.valueOf(55));

		dealPackageResponsePriceHandler.handle(packageRequestData, packageResponseData);
		Assert.assertTrue(Objects.nonNull(packageResponseData.getPrice()));
	}
}
