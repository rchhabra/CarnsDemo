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

package de.hybris.platform.travelfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.impl.DefaultTravelBundleTemplateFacade;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link TravelBundleTemplateFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelBundleTemplateFacadeTest
{

	@InjectMocks
	private DefaultTravelBundleTemplateFacade defaultTravelBundleTemplateFacade;

	@Mock
	private TypeService typeService;

	@Mock
	private SessionService sessionService;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private ProductService productService;

	@Mock
	private TravelCommerceStockService commerceStockService;

	@Mock
	private EnumerationValueModel enumerationValueModel;

	@Mock
	private EnumerationValueModel enumerationValueModel1;

	@Mock
	private EnumerationValueModel enumerationValueModel2;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void testCreateUpgradableOptionsData()
	{
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setBundleType("ECONOMY_PLUS");
		final TotalFareData totalFareData = new TotalFareData();
		final PriceData totalPrice = new PriceData();
		totalPrice.setValue(BigDecimal.valueOf(200d));
		totalFareData.setTotalPrice(totalPrice);
		itineraryPricingInfoData.setTotalFare(totalFareData);

		final PriceData diffPrice = new PriceData();
		diffPrice.setValue(BigDecimal.valueOf(100d));
		given(travelCommercePriceFacade.createPriceData(100d, 2)).willReturn(diffPrice);
		defaultTravelBundleTemplateFacade
				.createUpgradeItineraryPricingInfoTotalPriceData(BigDecimal.valueOf(100d), itineraryPricingInfoData);

		assertEquals(100d, itineraryPricingInfoData.getTotalFare().getTotalPrice().getValue().doubleValue(), 2);

	}

	@Test
	public void testGetSequenceNumber()
	{
		given(typeService.getEnumerationValue(BundleType.valueOf("BUSINESS"))).willReturn(enumerationValueModel2);
		given(enumerationValueModel2.getSequenceNumber()).willReturn(2);
		final int seqNumber = defaultTravelBundleTemplateFacade.getSequenceNumber("BUSINESS");
		assertEquals(2, seqNumber);
	}

	@Test
	public void testGetSelectedItineraryPricingInfoData()
	{
		final PricedItineraryData pricedItineraryData = new PricedItineraryData();
		final ItineraryPricingInfoData itineraryPricingInfoData0 = new ItineraryPricingInfoData();
		itineraryPricingInfoData0.setBundleType("ECONOMY");
		itineraryPricingInfoData0.setSelected(false);
		final ItineraryPricingInfoData itineraryPricingInfoData1 = new ItineraryPricingInfoData();
		itineraryPricingInfoData1.setBundleType("ECONOMY_PLUS");
		itineraryPricingInfoData1.setSelected(true);
		final ItineraryPricingInfoData itineraryPricingInfoData2 = new ItineraryPricingInfoData();
		itineraryPricingInfoData2.setBundleType("BUSINESS");
		itineraryPricingInfoData2.setSelected(false);
		final List<ItineraryPricingInfoData> pricingInfoList = new ArrayList<>();
		pricingInfoList.add(itineraryPricingInfoData0);
		pricingInfoList.add(itineraryPricingInfoData1);
		pricingInfoList.add(itineraryPricingInfoData2);
		pricedItineraryData.setItineraryPricingInfos(pricingInfoList);
		final ItineraryPricingInfoData itineraryPricingInfoData = defaultTravelBundleTemplateFacade
				.getSelectedItineraryPricingInfoData(pricedItineraryData);
		assertEquals(true, itineraryPricingInfoData.isSelected());
	}

}
