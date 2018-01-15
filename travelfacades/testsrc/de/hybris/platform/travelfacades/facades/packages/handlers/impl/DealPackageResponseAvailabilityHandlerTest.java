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
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.StandardPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DealPackageResponseAvailabilityHandlerTest
{
	@InjectMocks
	DealPackageResponseAvailabilityHandler dealPackageResponseAvailabilityHandler;

	@Mock
	AccommodationOfferingFacade accommodationOfferingFacade;

	@Test
	public void testHandle()
	{
		final PackageRequestData packageRequestData = new PackageRequestData();
		final PackageResponseData packageResponseData = new PackageResponseData();

		//------------------packageResponseData---------//
		final TransportPackageResponseData transportPackageResponse = new TransportPackageResponseData();
		packageResponseData.setTransportPackageResponse(transportPackageResponse);

		final FareSelectionData fareSearchResponse = new FareSelectionData();
		transportPackageResponse.setFareSearchResponse(fareSearchResponse);

		final List<PricedItineraryData> pricedItineraries = new ArrayList<PricedItineraryData>();
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		pricedItineraries.add(pricedItineraryData);
		fareSearchResponse.setPricedItineraries(pricedItineraries);

		final List<ItineraryPricingInfoData> itineraryPricingInfos = new ArrayList<>();
		final ItineraryPricingInfoData itineraryPricingInfoData=new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(true);
		itineraryPricingInfos.add(itineraryPricingInfoData);
		pricedItineraryData.setItineraryPricingInfos(itineraryPricingInfos);

		final AccommodationPackageResponseData accommodationPackageResponse = new AccommodationPackageResponseData();
		final AccommodationAvailabilityResponseData accommodationAvailabilityResponse = new AccommodationAvailabilityResponseData();
		accommodationPackageResponse.setAccommodationAvailabilityResponse(accommodationAvailabilityResponse);
		packageResponseData.setAccommodationPackageResponse(accommodationPackageResponse);

		final List<StandardPackageResponseData> standardPackageResponses = new ArrayList<>();
		final StandardPackageResponseData standardPackageResponse = new StandardPackageResponseData();
		final List<PackageProductData> packageProducts = new ArrayList<>();
		final PackageProductData packageProduct = new PackageProductData();
		final ProductData product = new ProductData();
		final StockData stock = new StockData();
		stock.setStockLevel(10l);
		product.setStock(stock);
		packageProduct.setProduct(product);
		packageProducts.add(packageProduct);
		standardPackageResponse.setPackageProducts(packageProducts);
		standardPackageResponses.add(standardPackageResponse);
		packageResponseData.setStandardPackageResponses(standardPackageResponses);
		//------------------packageRequestData---------//

		final TransportPackageRequestData transportPackageRequest = new TransportPackageRequestData();
		final FareSearchRequestData fareSearchRequest = new FareSearchRequestData();
		final List<OriginDestinationInfoData> originDestinationInfos = new ArrayList<>();
		final OriginDestinationInfoData originDestinationInfo = new OriginDestinationInfoData();
		originDestinationInfos.add(originDestinationInfo);
		fareSearchRequest.setOriginDestinationInfo(originDestinationInfos);
		transportPackageRequest.setFareSearchRequest(fareSearchRequest);
		packageRequestData.setTransportPackageRequest(transportPackageRequest);

		Mockito.when(accommodationOfferingFacade.isAccommodationAvailableForQuickSelection(accommodationAvailabilityResponse))
				.thenReturn(true);

		dealPackageResponseAvailabilityHandler.handle(packageRequestData, packageResponseData);
		Assert.assertTrue(packageResponseData.isAvailable());
	}

}
