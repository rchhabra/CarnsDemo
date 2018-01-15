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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.impl.ReservationSelectedAccommodationHandler;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReservationSelectedAccommodationHandlerTest
{

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@Mock
	private SelectedAccommodationModel selectedAccommodationModel;

	@Mock
	private TransportOfferingModel transportOfferingModel;

	@Mock
	private TravellerModel travellerModel;

	@Mock
	private ConfiguredAccommodationModel configuredAccommodation;

	@InjectMocks
	ReservationSelectedAccommodationHandler reservationSelectedAccommodationHandler = new ReservationSelectedAccommodationHandler();

	@Test
	public void noSelectedSeatsInReservationDataIfNoReservationItemTest()
	{
		final ReservationData reservationData = new ReservationData();
		reservationSelectedAccommodationHandler.handle(abstractOrderModel, reservationData);
		Assert.assertNull(reservationData.getReservationItems());
	}

	@Test
	public void noSelectedSeatsInReservationDataIfNoSelectedAccomsInOrder()
	{
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
		reservationItem.setReservationPricingInfo(reservationPricingInfo);
		final List<ReservationItemData> reservationItems = new ArrayList<>();
		reservationItems.add(reservationItem);
		reservationData.setReservationItems(reservationItems);
		Mockito.when(abstractOrderModel.getSelectedAccommodations()).thenReturn(null);
		reservationSelectedAccommodationHandler.handle(abstractOrderModel, reservationData);
		Assert.assertNull(reservationData.getReservationItems().get(0).getReservationPricingInfo().getSelectedSeats());
	}

	@Test
	public void reservationDataContainsSelectedSeatsIfOrderConatinsSelectedAccomsTest()
	{
		final ReservationData reservationData = new ReservationData();
		final ReservationItemData reservationItem = new ReservationItemData();
		final ItineraryData reservationItinerary = new ItineraryData();
		final List<OriginDestinationOptionData> originDestinationOptions = new ArrayList<>();
		final OriginDestinationOptionData originDestinationOptionData = new OriginDestinationOptionData();
		originDestinationOptions.add(originDestinationOptionData);
		final List<TransportOfferingData> transportOfferings = new ArrayList<>();
		final TransportOfferingData transportOfferingData = new TransportOfferingData();
		transportOfferingData.setCode("testTransportOfferingCode");
		transportOfferings.add(transportOfferingData);
		originDestinationOptionData.setTransportOfferings(transportOfferings);
		reservationItinerary.setOriginDestinationOptions(originDestinationOptions);
		reservationItem.setReservationItinerary(reservationItinerary);
		final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
		reservationItem.setReservationPricingInfo(reservationPricingInfo);
		final List<ReservationItemData> reservationItems = new ArrayList<>();
		reservationItems.add(reservationItem);
		reservationData.setReservationItems(reservationItems);
		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		selectedAccommodations.add(selectedAccommodationModel);
		Mockito.when(selectedAccommodationModel.getTransportOffering()).thenReturn(transportOfferingModel);
		Mockito.when(transportOfferingModel.getCode()).thenReturn("testTransportOfferingCode");
		Mockito.when(configuredAccommodation.getIdentifier()).thenReturn("8A");
		Mockito.when(selectedAccommodationModel.getConfiguredAccommodation()).thenReturn(configuredAccommodation);
		Mockito.when(abstractOrderModel.getSelectedAccommodations()).thenReturn(selectedAccommodations);
		Mockito.when(selectedAccommodationModel.getTraveller()).thenReturn(travellerModel);
		final TravellerData travellerData = new TravellerData();
		Mockito.when(travellerDataConverter.convert(travellerModel)).thenReturn(travellerData);
		reservationSelectedAccommodationHandler.handle(abstractOrderModel, reservationData);
		Assert.assertNotNull(reservationPricingInfo.getSelectedSeats());
		Assert.assertEquals("8A", reservationPricingInfo.getSelectedSeats().get(0).getSeatNumber());
	}

}
