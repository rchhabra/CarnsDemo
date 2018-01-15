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

package de.hybris.platform.travelfacades.accommodation.search.handlers;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.accommodation.search.mock.request.MockAccommodationSearchRequestData;
import de.hybris.platform.travelfacades.accommodation.search.mock.response.MockAccommodationOfferingDayRateDataList;
import de.hybris.platform.travelfacades.facades.accommodation.search.handlers.impl.PropertyDataPriceHandler;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PropertyDataPriceHandlerTest
{
	MockAccommodationSearchRequestData requestBuilder = new MockAccommodationSearchRequestData();
	MockAccommodationOfferingDayRateDataList dayRatesBuilder = new MockAccommodationOfferingDayRateDataList();

	PropertyData propertyData = new PropertyData();

	PropertyDataPriceHandler handler;

	@Mock
	CommonI18NService commonI18NService;

	@Before
	public void setUp()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("GBP");
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		propertyData.setRatePlanConfigs(Stream.of("").collect(Collectors.toList()));

	}

	@Test
	public void testHandler()
	{
		handler = new PropertyDataPriceHandler()
		{
			@Override
			protected PriceData createPriceData(final BigDecimal dayPrice)
			{
				final PriceData priceData = new PriceData();
				priceData.setCurrencyIso("GBP");
				priceData.setPriceType(PriceDataType.BUY);
				priceData.setValue(dayPrice);
				return priceData;
			}
		};

		handler.setCommonI18NService(commonI18NService);
		handler.handle(dayRatesBuilder.buildMapForSingleProperty(), requestBuilder.buildRequestData(), propertyData);

		Assert.assertEquals(BigDecimal.valueOf(480.0), propertyData.getRateRange().getActualRate().getValue());
	}
}
