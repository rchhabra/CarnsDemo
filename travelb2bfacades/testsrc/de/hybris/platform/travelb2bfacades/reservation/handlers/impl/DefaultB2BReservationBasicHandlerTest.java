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

package de.hybris.platform.travelb2bfacades.reservation.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultB2BReservationBasicHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BReservationBasicHandlerTest
{
	@InjectMocks
	DefaultB2BReservationBasicHandler defaultB2BReservationBasicHandler;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Test
	public void test()
	{
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();

		final UserModel userModel = new UserModel();
		userModel.setUid("TEST_USER_UID");
		userModel.setUid("TEST_USER_NAME");
		abstractOrderModel.setUser(userModel);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("TEST_CURRENCY_ISO_CODE");
		abstractOrderModel.setCurrency(currency);

		final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		final B2BCostCenterModel costCenterModel = new B2BCostCenterModel()
		{
			@Override
			public String getName()
			{
				return "TEST_COST_CENTER_NAME";
			}
		};
		abstractOrderEntryModel.setCostCenter(costCenterModel);

		final B2BUnitModel unit = new B2BUnitModel()
		{
			@Override
			public String getName()
			{
				return "TEST_UNIT_NAME";
			}
		};

		abstractOrderModel.setUnit(unit);

		abstractOrderModel.setEntries(Arrays.asList(abstractOrderEntryModel));

		abstractOrderModel.setTotalPrice(100d);
		abstractOrderModel.setTotalTax(10d);
		abstractOrderModel.setNet(Boolean.TRUE);

		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(110d));
		Mockito.when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).thenReturn(priceData);

		final B2BReservationData b2bReservationData = new B2BReservationData();

		defaultB2BReservationBasicHandler.handle(abstractOrderModel, b2bReservationData);

		Assert.assertEquals(costCenterModel.getName(), b2bReservationData.getCostCenter());


		final PriceData priceData2 = new PriceData();
		priceData2.setValue(BigDecimal.valueOf(100d));
		Mockito.when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString())).thenReturn(priceData2);
		abstractOrderModel.setNet(Boolean.FALSE);
		defaultB2BReservationBasicHandler.handle(abstractOrderModel, b2bReservationData);

		Assert.assertEquals(costCenterModel.getName(), b2bReservationData.getCostCenter());
	}

}
