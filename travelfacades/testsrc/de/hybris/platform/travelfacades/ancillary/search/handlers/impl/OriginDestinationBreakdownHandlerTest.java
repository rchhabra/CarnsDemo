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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link OriginDestinationBreakdownHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OriginDestinationBreakdownHandlerTest
{
	@InjectMocks
	OriginDestinationBreakdownHandler originDestinationBreakdownHandler;

	@Mock
	private ProductService productService;

	@Mock
	private TravelCommercePriceService travelCommercePriceService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private ProductModel product;

	@Mock
	private PriceInformation priceInfo;

	@Mock
	private PriceValue priceValue;

	private final String TEST_PRODUCT_CODE = "TEST_PRODUCT_CODE";
	private final String TEST_OFFER_GROUP_CODEA = "TEST_OFFER_GROUP_CODEA";
	private final String TEST_OFFER_GROUP_CODEB = "TEST_OFFER_GROUP_CODEB";

	private final String TEST_TRAVEL_ROUTE_CODE_A = "TEST_TRAVEL_ROUTE_CODE_A";
	private final String TEST_TRAVEL_ROUTE_CODE_B = "TEST_TRAVEL_ROUTE_CODE_B";

	private final String TEST_FORMATTED_VALUE = "TEST_FORMATTED_VALUE";
	private final String TEST_CURRENCY_ISO_CODE = "TEST_CURRENCY_ISO_CODE";
	private final BigDecimal TEST_PRICE_VALUE = BigDecimal.valueOf(100d);

	@Test
	public void test()
	{
		given(productService.getProductForCode(Matchers.anyString())).willReturn(product);
		given(travelCommercePriceService.getProductWebPrice(Matchers.any(ProductModel.class), Matchers.any()))
				.willReturn(priceInfo);
		given(priceInfo.getPriceValue()).willReturn(priceValue);
		given(priceValue.getCurrencyIso()).willReturn(TEST_CURRENCY_ISO_CODE);
		given(priceValue.getValue()).willReturn(TEST_PRICE_VALUE.doubleValue());
		final PriceData priceData = new PriceData();
		priceData.setFormattedValue(TEST_FORMATTED_VALUE);
		priceData.setValue(TEST_PRICE_VALUE);
		priceData.setCurrencyIso(TEST_CURRENCY_ISO_CODE);
		given(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString()))
				.willReturn(priceData);
		final OfferRequestData offerRequestData = createOfferRequestData();
		final OfferResponseData offerResponseData = createOfferResponseData();
		originDestinationBreakdownHandler.handle(offerRequestData, offerResponseData);
	}

	private OfferRequestData createOfferRequestData()
	{
		final OfferRequestData offerRequestData = new OfferRequestData();
		offerRequestData.setSelectedOffers(createSelectedOffersData());
		return offerRequestData;
	}

	private SelectedOffersData createSelectedOffersData()

	{
		final SelectedOffersData selectedOffersData = new SelectedOffersData();
		selectedOffersData.setOfferGroups(createOfferGroupDatas());
		return selectedOffersData;
	}

	private List<OfferGroupData> createOfferGroupDatas()
	{
		final List<OfferGroupData> offerGroups = new ArrayList<>();
		offerGroups.add(createOfferGroupData());
		return offerGroups;
	}

	private OfferGroupData createOfferGroupData()
	{
		final OfferGroupData offerGroupData = new OfferGroupData();
		offerGroupData.setCode(TEST_OFFER_GROUP_CODEB);
		offerGroupData.setOriginDestinationOfferInfos(createOriginDestinationOfferInfos());
		return offerGroupData;

	}

	private OfferResponseData createOfferResponseData()
	{
		final OfferResponseData offerResponseData = new OfferResponseData();
		offerResponseData.setOfferGroups(createOfferGroups());
		return offerResponseData;
	}

	private List<OfferGroupData> createOfferGroups()
	{
		final List<OfferGroupData> offerGroups = new ArrayList<>();
		offerGroups.add(createOfferGroup(true, TEST_OFFER_GROUP_CODEA));
		offerGroups.add(createOfferGroup(true, TEST_OFFER_GROUP_CODEB));
		offerGroups.add(createOfferGroup(false, TEST_OFFER_GROUP_CODEA));
		return offerGroups;
	}

	private OfferGroupData createOfferGroup(final boolean hasOriginDestinationInfo, final String code)
	{
		final OfferGroupData offerGroupData = new OfferGroupData();
		offerGroupData.setCode(code);
		if (hasOriginDestinationInfo)
		{
			offerGroupData.setOriginDestinationOfferInfos(createOriginDestinationOfferInfos());

		}
		return offerGroupData;
	}

	private List<OriginDestinationOfferInfoData> createOriginDestinationOfferInfos()
	{
		final List<OriginDestinationOfferInfoData> offerPricingInfos = new ArrayList<>();
		offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_A, 0, true, true, true));
		offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_B, 0, true, true, true));
		offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_B, 1, true, true, true));
		offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_B, 1, true, true, true));
		//offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_B, 1, false, false, false));
		//offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_B, 1, true, false, false));
		//offerPricingInfos.add(createOriginDestinationOfferInfo(TEST_TRAVEL_ROUTE_CODE_B, 1, true, true, false));

		return offerPricingInfos;
	}

	private OriginDestinationOfferInfoData createOriginDestinationOfferInfo(final String travelRouteCode,
			final int originDestinationRefNumber, final boolean hasTravellerRestriction,
			final boolean hasAddToCriteriaForTravellerRestriciton, final boolean hasPerLegAddToCriteriaForTravellerRestriction)
	{
		final OriginDestinationOfferInfoData originDestinationOfferInfoData = new OriginDestinationOfferInfoData();
		originDestinationOfferInfoData.setOfferPricingInfos(createOfferPricingInfoDatas(hasTravellerRestriction,
				hasAddToCriteriaForTravellerRestriciton, hasPerLegAddToCriteriaForTravellerRestriction));
		originDestinationOfferInfoData.setTravelRouteCode(travelRouteCode);
		originDestinationOfferInfoData.setOriginDestinationRefNumber(originDestinationRefNumber);
		originDestinationOfferInfoData.setTransportOfferings(createTransportOfferings());
		return originDestinationOfferInfoData;
	}

	private List<TransportOfferingData> createTransportOfferings()
	{
		final List<TransportOfferingData> transprortOfferings = new ArrayList<>();
		transprortOfferings.add(createTransprortOfferingData());
		return transprortOfferings;
	}

	private TransportOfferingData createTransprortOfferingData()
	{
		final TransportOfferingData transportOffering = new TransportOfferingData();
		transportOffering.setCode("TEST_TRANSPORT_OFFERING_CODE");
		return transportOffering;
	}

	private List<OfferPricingInfoData> createOfferPricingInfoDatas(final boolean hasTravellerRestriction,
			final boolean hasAddToCriteriaForTravellerRestriciton, final boolean hasPerLegAddToCriteriaForTravellerRestriction)
	{
		final List<OfferPricingInfoData> offerPricingInfoDatas = new ArrayList<>();
		offerPricingInfoDatas.add(createOfferPricingInfoData(hasTravellerRestriction, hasAddToCriteriaForTravellerRestriciton,
				hasPerLegAddToCriteriaForTravellerRestriction));
		return offerPricingInfoDatas;
	}

	private OfferPricingInfoData createOfferPricingInfoData(final boolean hasTravellerRestriction,
			final boolean hasAddToCriteriaForTravellerRestriciton, final boolean hasPerLegAddToCriteriaForTravellerRestriction)
	{
		final OfferPricingInfoData OfferPricingInfoData = new OfferPricingInfoData();
		if (hasTravellerRestriction)
		{
			OfferPricingInfoData.setTravelRestriction(createTravellerRestriction(hasAddToCriteriaForTravellerRestriciton,
					hasPerLegAddToCriteriaForTravellerRestriction));
			if (hasAddToCriteriaForTravellerRestriciton && hasPerLegAddToCriteriaForTravellerRestriction)
			{
				OfferPricingInfoData.setProduct(createProductData());
				OfferPricingInfoData.setBookingBreakdown(createBookingBreakdownData());
			}
		}
		return OfferPricingInfoData;
	}

	private TravelRestrictionData createTravellerRestriction(final boolean hasAddToCriteriaForTravellerRestriciton,
			final boolean hasPerLegAddToCriteriaForTravellerRestriction)
	{
		final TravelRestrictionData travelRestrictionData = new TravelRestrictionData();
		if (hasAddToCriteriaForTravellerRestriciton)
		{
			travelRestrictionData.setAddToCartCriteria(hasPerLegAddToCriteriaForTravellerRestriction
					? AddToCartCriteriaType.PER_LEG.getCode() : AddToCartCriteriaType.PER_BOOKING.getCode());
		}

		return travelRestrictionData;
	}

	private ProductData createProductData()
	{
		final ProductData productData = new ProductData();
		productData.setCode(TEST_PRODUCT_CODE);
		return productData;
	}

	private BookingBreakdownData createBookingBreakdownData()
	{
		final BookingBreakdownData bookingBreakdownData = new BookingBreakdownData();
		bookingBreakdownData.setQuantity(2);
		return bookingBreakdownData;
	}

}

