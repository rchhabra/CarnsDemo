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

package de.hybris.platform.travelservices.ordersplitting.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.dao.TravelConsignmentDao;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultTravelConsignmentService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelConsignmentServiceTest
{
	@InjectMocks
	private final DefaultTravelConsignmentService travelConsignmentService = new DefaultTravelConsignmentService();

	@Mock
	private TravelConsignmentDao travelConsignmentDao;

	@Mock
	private ModelService modelService;

	@Mock
	private ConsignmentModel consignmentModel;

	@Mock
	private OrderModel order;

	@Mock
	private AddressModel deliveryAddress;

	@Mock
	private TravellerModel traveller;

	/**
	 * Junit Method to test creation of a consignment
	 *
	 * @throws ConsignmentCreationException
	 */
	@Test
	public void testCreateTravelConsignment() throws ConsignmentCreationException
	{
		when(modelService.create(ConsignmentModel.class)).thenReturn(new ConsignmentModel());
		when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
		when(modelService.create(ConsignmentEntryModel.class)).thenReturn(new ConsignmentEntryModel());

		final ProductModel product = new ProductModel();
		product.setCode("ORTC1");

		final List<TravellerModel> travellers = new ArrayList<>();
		travellers.add(traveller);

		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntry.setProduct(product);
		orderEntry.setQuantity(1L);
		orderEntryInfo.setTravellers(travellers);
		orderEntry.setTravelOrderEntryInfo(orderEntryInfo);

		orderEntries.add(orderEntry);

		final ConsignmentModel consignment = travelConsignmentService.createConsignment(order, "TestOrder123", orderEntries);

		Assert.assertEquals("TestOrder123", consignment.getCode());
	}

	@Test
	public void testGetConsignment()
	{
		final WarehouseModel warehouse = new WarehouseModel();
		final OrderModel order = new OrderModel();
		final TravellerModel traveller = new TravellerModel();

		when(travelConsignmentDao.getConsignment(warehouse, order, traveller)).thenReturn(new ConsignmentModel());

		final ConsignmentModel consignment = travelConsignmentService.getConsignment(warehouse, order, traveller);

		Assert.assertNotNull(consignment);
	}

}
