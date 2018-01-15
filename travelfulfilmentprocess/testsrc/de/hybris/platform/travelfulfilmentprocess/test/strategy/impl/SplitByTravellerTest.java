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

package de.hybris.platform.travelfulfilmentprocess.test.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplitByTraveller;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * JUnit test suite for {@link SplitByTraveller}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SplitByTravellerTest
{
	@InjectMocks
	SplitByTraveller splitByTraveller;
	@Mock
	private TravellerService travellerService;

	private static final String TRAVELLER = "TRAVELLER";

	@Test
	public void testSplitByTraveller()
	{
		final OrderEntryGroup orderEntryGroup = new OrderEntryGroup();
		final TravellerModel travellerModel1 = new TravellerModel();
		travellerModel1.setLabel("adult1");
		final TravellerModel travellerModel2 = new TravellerModel();
		travellerModel2.setLabel("adult2");
		final FareProductModel product1 = new FareProductModel();
		product1.setCode("FP1");
		final FareProductModel product2 = new FareProductModel();
		product2.setCode("FP2");
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final AbstractOrderEntryModel orderEntry1 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo1 = new TravelOrderEntryInfoModel();
		orderEntry1.setProduct(product1);
		orderEntry1.setQuantity(1L);
		orderEntryInfo1.setTravellers(Arrays.asList(new TravellerModel[]
		{ travellerModel1 }));
		orderEntry1.setTravelOrderEntryInfo(orderEntryInfo1);
		final AbstractOrderEntryModel orderEntry2 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo2 = new TravelOrderEntryInfoModel();
		orderEntry2.setProduct(product2);
		orderEntry2.setQuantity(1L);
		orderEntryInfo2.setTravellers(Arrays.asList(new TravellerModel[]
		{ travellerModel2 }));
		orderEntry2.setTravelOrderEntryInfo(orderEntryInfo2);
		orderEntries.add(orderEntry1);
		orderEntries.add(orderEntry2);
		orderEntryGroup.addAll(orderEntries);

		List<OrderEntryGroup> splitedList = new ArrayList<>();
		splitedList.add(orderEntryGroup);

		final List<TravellerModel> expectedTravellersList = new ArrayList<>();
		final TravellerModel expectedTravellerModel1 = new TravellerModel();
		expectedTravellerModel1.setLabel("adult1");
		final TravellerModel expectedTravellerModel2 = new TravellerModel();
		expectedTravellerModel2.setLabel("adult2");
		expectedTravellersList.add(expectedTravellerModel1);
		expectedTravellersList.add(expectedTravellerModel2);

		Mockito.when(travellerService.getTravellers(orderEntryGroup)).thenReturn(expectedTravellersList);

		splitedList = splitByTraveller.perform(splitedList);
		Assert.assertEquals(2, splitedList.size());

		final TravellerModel travellerModel = (TravellerModel) splitedList.stream().findFirst().get().getParameter("TRAVELLER");
		Assert.assertEquals("adult1", travellerModel.getLabel());
	}
}
