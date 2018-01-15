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

package de.hybris.platform.travelfacades.accommodation.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.*;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.ExtraServiceRestrictionHandler;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationRestrictionExtrasStrategy;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.BDDMockito.given;


/**
 * Unit test class for {@link ExtraServiceRestrictionHandler}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExtraServiceRestrictionHandlerTest
{

	@InjectMocks
	private ExtraServiceRestrictionHandler handler;

	@Mock
	private ProductModel productModel;

	@Mock
	private TravelRestrictionModel travelRestriction;

	@Mock
	private Map<String, AccommodationRestrictionExtrasStrategy> extrasAvailabilityStrategyMap;

	@Mock
	private AccommodationRestrictionExtrasStrategy strategy;

	@Mock
	private AccommodationRestrictionData restrictionData;

	@Mock
	private ReservedRoomStayData reservedRoomStayData;

	@Mock
	private AccommodationReservationData accommodationReservationData;

	@Test
	public void testPopulateServiceWithTravelRestriction()
	{
		given(productModel.getTravelRestriction()).willReturn(travelRestriction);
		given(extrasAvailabilityStrategyMap.get(Matchers.any(TravelRestrictionModel.class))).willReturn(strategy);
		given(strategy.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData)).willReturn(restrictionData);
		final ServiceData service = new ServiceData();
		final ServiceDetailData serviceDetails = new ServiceDetailData();
		service.setServiceDetails(serviceDetails);
		handler.handle(productModel, reservedRoomStayData, service, accommodationReservationData);
		Assert.assertNotNull(service.getServiceDetails().getRestriction());
	}

	@Test
	public void testPopulateServiceWithoutTravelRestriction()
	{
		given(productModel.getTravelRestriction()).willReturn(null);
		given(strategy.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData)).willReturn(restrictionData);
		given(extrasAvailabilityStrategyMap.get(Matchers.anyString())).willReturn(strategy);
		final ServiceData service = new ServiceData();
		final ServiceDetailData serviceDetails = new ServiceDetailData();
		service.setServiceDetails(serviceDetails);
		handler.handle(productModel, reservedRoomStayData, service, accommodationReservationData);
		Assert.assertNotNull(service.getServiceDetails().getRestriction());
	}

	@Test(expected = AccommodationPipelineException.class)
	public void testPopulateThrowAccommodationPipelineException()
	{
		given(productModel.getTravelRestriction()).willReturn(travelRestriction);
		given(extrasAvailabilityStrategyMap.get(Matchers.any(TravelRestrictionModel.class))).willReturn(strategy);
		given(strategy.applyStrategy(productModel, reservedRoomStayData, accommodationReservationData))
				.willThrow(new AccommodationPipelineException(
						"Product not available for accommodationOffering with code "));
		final ServiceData service = new ServiceData();
		final ServiceDetailData serviceDetails = new ServiceDetailData();
		service.setServiceDetails(serviceDetails);
		handler.handle(productModel, reservedRoomStayData, service, accommodationReservationData);
	}

}