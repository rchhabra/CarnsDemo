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
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link OriginalOrderCodePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OriginalOrderCodePopulatorTest
{
	@InjectMocks
	OriginalOrderCodePopulator originalOrderCodePopulator;

	private final String TEST_ORDER_CODE = "TEST_ORDER_CODE";

	@Test
	public void populateTestForNull()
	{
		final OrderModel source = Mockito.mock(OrderModel.class);
		given(source.getOriginalOrder()).willReturn(null);


		final OrderModel originalOrderModel = Mockito.mock(OrderModel.class);
		given(originalOrderModel.getCode()).willReturn(TEST_ORDER_CODE);

		final OrderHistoryEntryModel ohem1 = Mockito.mock(OrderHistoryEntryModel.class);
		final OrderHistoryEntryModel ohem2 = Mockito.mock(OrderHistoryEntryModel.class);
		given(ohem1.getCreationtime())
				.willReturn(TravelDateUtils.convertStringDateToDate("15/12/2016", TravelservicesConstants.DATE_PATTERN));
		given(ohem1.getPreviousOrderVersion()).willReturn(originalOrderModel);
		given(ohem2.getCreationtime())
				.willReturn(TravelDateUtils.convertStringDateToDate("17/12/2016", TravelservicesConstants.DATE_PATTERN));


		final List<OrderHistoryEntryModel> historyEntryModels = new ArrayList<>();
		given(source.getHistoryEntries()).willReturn(historyEntryModels);
		historyEntryModels.add(ohem1);
		historyEntryModels.add(ohem2);
		final OrderData target = new OrderData();
		originalOrderCodePopulator.populate(source, target);

		Assert.assertEquals(TEST_ORDER_CODE, target.getOriginalOrderCode());
	}


	@Test
	public void populateTest()
	{
		final OrderModel source = Mockito.mock(OrderModel.class);
		final OrderModel originalOrderModel = Mockito.mock(OrderModel.class);
		given(source.getOriginalOrder()).willReturn(originalOrderModel);
		given(originalOrderModel.getCode()).willReturn(TEST_ORDER_CODE);
		final OrderData target = new OrderData();
		originalOrderCodePopulator.populate(source, target);
		Assert.assertEquals(TEST_ORDER_CODE, target.getOriginalOrderCode());
	}

	@Test
	public void populateTestForHistoryNotPresent()
	{
		final OrderModel source = Mockito.mock(OrderModel.class);
		given(source.getOriginalOrder()).willReturn(null);
		given(source.getHistoryEntries()).willReturn(Collections.emptyList());
		final OrderData target = new OrderData();
		originalOrderCodePopulator.populate(source, target);

		Assert.assertEquals(StringUtils.EMPTY, target.getOriginalOrderCode());
	}
}
