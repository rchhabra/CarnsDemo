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
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelfulfilmentprocess.strategy.impl.SplitByTransportOffering;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * JUnit test suite for {@link SplitByTransportOffering}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SplitByTransportOfferingTest
{
	@InjectMocks
	private SplitByTransportOffering splitByTransportOffering;

	@Mock
	private ConsignmentModel consignmentModel;

	@Mock
	private TransportOfferingService transportOfferingService;

	private static final String TRAVELLER = "TRAVELLER";

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSpiltByTransportOffering()
	{
		final TransportOfferingModel to1 = new TransportOfferingModel();
		to1.setCode("EZY800120");
		final TransportOfferingModel to2 = new TransportOfferingModel();
		to2.setCode("EZY900120");
		final List<TransportOfferingModel> transportofferings = new ArrayList<>();
		transportofferings.add(to1);
		transportofferings.add(to2);
		final OrderEntryGroup orderEntryGroup = new OrderEntryGroup();
		final FareProductModel product1 = new FareProductModel();
		product1.setCode("FP1");
		final FareProductModel product2 = new FareProductModel();
		product2.setCode("FP2");
		final TravellerModel travellerModel1 = new TravellerModel();
		travellerModel1.setLabel("adult1");
		final TravellerModel travellerModel2 = new TravellerModel();
		travellerModel2.setLabel("adult2");
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final AbstractOrderEntryModel orderEntry1 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo1 = new TravelOrderEntryInfoModel();
		orderEntry1.setProduct(product1);
		orderEntry1.setQuantity(1L);
		orderEntryInfo1.setTransportOfferings(transportofferings);
		orderEntry1.setTravelOrderEntryInfo(orderEntryInfo1);
		final AbstractOrderEntryModel orderEntry2 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo2 = new TravelOrderEntryInfoModel();
		orderEntry2.setProduct(product2);
		orderEntry2.setQuantity(1L);
		orderEntryInfo2.setTransportOfferings(transportofferings);
		orderEntry2.setTravelOrderEntryInfo(orderEntryInfo2);
		orderEntries.add(orderEntry1);
		orderEntries.add(orderEntry2);
		orderEntryGroup.addAll(orderEntries);
		orderEntryGroup.setParameter(TRAVELLER, travellerModel1);

		List<OrderEntryGroup> splitedList = new ArrayList<>();
		splitedList.add(orderEntryGroup);
		final TransportOfferingModel toE1 = new TransportOfferingModel();
		toE1.setCode("EZY800120");
		final TransportOfferingModel toE2 = new TransportOfferingModel();
		toE2.setCode("EZY900120");
		final List<TransportOfferingModel> expectedTransportofferings = new ArrayList<>();
		expectedTransportofferings.add(toE1);
		expectedTransportofferings.add(toE2);
		expectedTransportofferings.add(toE1);
		expectedTransportofferings.add(toE2);

		Mockito.when(transportOfferingService.getTransportOfferingsFromOrderEntries(orderEntryGroup)).thenReturn(expectedTransportofferings);

		splitedList = splitByTransportOffering.perform(splitedList);

		Assert.assertEquals(2, orderEntries.size());

		Assert.assertEquals(4, splitedList.size());

	final TransportOfferingModel transportoffering = (TransportOfferingModel) splitedList.stream().findFirst().get()
				.getParameter("TRANSPORT_OFFERING");
		Assert.assertEquals("EZY800120", transportoffering.getCode());

		splitByTransportOffering.afterSplitting(splitedList.stream().findFirst().get(), consignmentModel);

		verify(consignmentModel, times(1)).setWarehouse(Matchers.any(WarehouseModel.class));

	}

}
