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

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.ServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationServicePipelineManager;
import de.hybris.platform.travelservices.services.AccommodationExtrasService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationExtrasFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationExtrasFacadeTest
{
	@InjectMocks
	DefaultAccommodationExtrasFacade defaultAccommodationExtrasFacade;
	@Mock
	private AccommodationExtrasService accommodationExtrasService;
	@Mock
	private AccommodationServicePipelineManager accommodationServicePipelineManager;

	@Test
	public void testGetAvailableServices()
	{
		final AccommodationReservationData reservationData = new AccommodationReservationData();
		final PropertyData accommodationReference = new PropertyData();
		accommodationReference.setAccommodationOfferingCode("AccommodationOfferingCode");
		reservationData.setAccommodationReference(accommodationReference);
		final ProductModel productModel = new ProductModel();
		final List<ProductModel> extraServices = Collections.singletonList(productModel);
		final ReservedRoomStayData roomStay = new ReservedRoomStayData();
		roomStay.setRoomStayRefNumber(0);
		reservationData.setRoomStays(Collections.singletonList(roomStay));
		final ServiceData serviceData=new ServiceData();

		given(accommodationExtrasService.getExtrasForAccommodationOffering("AccommodationOfferingCode")).willReturn(extraServices);
		given(accommodationServicePipelineManager.executePipeline(productModel, roomStay, reservationData)).willReturn(serviceData);

		Assert.assertNotNull(defaultAccommodationExtrasFacade.getAvailableServices(reservationData));

	}
}
