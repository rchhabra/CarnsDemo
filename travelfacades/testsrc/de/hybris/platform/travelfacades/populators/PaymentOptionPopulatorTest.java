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

package de.hybris.platform.travelfacades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.order.PaymentOptionData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link PaymentOptionPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentOptionPopulatorTest
{
	@InjectMocks
	PaymentOptionPopulator paymentOptionPopulator;

	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private CurrencyModel currency;

	private final String TEST_PRCE = "TEST_PRICE";

	@Before
	public void setUp()
	{
		final PriceData priceData = new PriceData();
		priceData.setFormattedValue(TEST_PRCE);
		given(commonI18NService.getCurrentCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn(StringUtils.EMPTY);
		given(priceDataFactory.create(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class), Matchers.anyString()))
				.willReturn(priceData);
	}

	@Test
	public void populateTest()
	{
		final PaymentOptionInfo source = Mockito.mock(PaymentOptionInfo.class);

		final AbstractOrderEntryModel aoe1 = Mockito.mock(AbstractOrderEntryModel.class);
		final AbstractOrderEntryModel aoe2 = Mockito.mock(AbstractOrderEntryModel.class);

		given(aoe1.getEntryNumber()).willReturn(1);
		given(aoe1.getType()).willReturn(OrderEntryType.ACCOMMODATION);
		given(aoe2.getEntryNumber()).willReturn(2);

		final List<AbstractOrderEntryModel> aoeList = new ArrayList<>();
		aoeList.add(null);
		aoeList.add(aoe1);
		aoeList.add(aoe2);

		final EntryTypePaymentInfo etpi1 = Mockito.mock(EntryTypePaymentInfo.class);
		given(etpi1.getEntries()).willReturn(aoeList);
		given(etpi1.getBookingTimeAmount()).willReturn(100d);

		final List<EntryTypePaymentInfo> paymentInfos = new ArrayList<>(2);
		paymentInfos.add(etpi1);
		given(source.getEntryTypeInfos()).willReturn(paymentInfos);
		final PaymentOptionData target = new PaymentOptionData();
		final PriceData priceData = new PriceData();
		given(travelCommercePriceFacade.createPriceData(Matchers.anyDouble(), Matchers.anyInt())).willReturn(priceData);
		paymentOptionPopulator.populate(source, target);

		Assert.assertNotNull(target.getBookingTimeAmount());

	}
}
