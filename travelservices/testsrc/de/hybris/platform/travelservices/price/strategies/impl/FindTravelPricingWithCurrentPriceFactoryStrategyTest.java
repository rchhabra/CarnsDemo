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

package de.hybris.platform.travelservices.price.strategies.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link FindTravelPricingWithCurrentPriceFactoryStrategy}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FindTravelPricingWithCurrentPriceFactoryStrategyTest
{
	@InjectMocks
	FindTravelPricingWithCurrentPriceFactoryStrategy findTravelPricingWithCurrentPriceFactoryStrategy = new FindTravelPricingWithCurrentPriceFactoryStrategy()
			{
				@Override
				protected Collection<TaxValue> getTaxValues(final AbstractOrderEntryModel entry) throws CalculationException{
					return null;
				}

				@Override
				protected PriceValue getBasePrice(final AbstractOrderEntryModel entry) throws CalculationException{
					return null;
				}
			};
	@Mock
	private TransportFacilityService transportFacilityService;
	@Mock
	private SessionService sessionService;

	@Mock
	private ModelService modelService;

	@Mock
	private PriceFactory priceFactory;
	@Mock
	private AbstractOrderEntry entryItem;

	@Test
	public void testFindTaxValues() throws CalculationException, JaloPriceFactoryException
	{

		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		transportFacilityModel.setCode("facilityCode");

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		travelSectorModel.setOrigin(transportFacilityModel);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0004");
		transportOfferingModel.setTravelSector(travelSectorModel);

		final LocationModel countryModel = new LocationModel();
		countryModel.setCode("countryCode");
		countryModel.setLocationType(LocationType.COUNTRY);

		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setPriceLevel(TravelservicesConstants.PRICING_LEVEL_ROUTE);
		orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));

		Mockito.when(
				findTravelPricingWithCurrentPriceFactoryStrategy.getTransportFacilityService().getCountry(transportFacilityModel))
				.thenReturn(countryModel);

		final PassengerInformationModel passengerInfoModel = new PassengerInformationModel();
		final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
		passengerTypeModel.setCode("PASSENGER");
		passengerInfoModel.setPassengerType(passengerTypeModel);

		final TravellerModel travellerModel = Mockito.mock(TravellerModel.class);
		Mockito.when(travellerModel.getUid()).thenReturn("adult");

		orderEntryInfo.setTravellers(Stream.of(travellerModel).collect(Collectors.toList()));
		entry.setTravelOrderEntryInfo(orderEntryInfo);
		Mockito.when(travellerModel.getInfo()).thenReturn(passengerInfoModel);
		Mockito.when(modelService.getSource(entry)).thenReturn(entryItem);
		Mockito.when(priceFactory.getTaxValues(entryItem)).thenReturn(Collections.emptyList());
		Mockito.doNothing().when(sessionService).setAttribute(Matchers.eq(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());
		findTravelPricingWithCurrentPriceFactoryStrategy.findTaxValues(entry);
		verify(sessionService, times(1)).setAttribute(Matchers.eq(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());

	}

	@Test
	public void testfindBasePrice() throws CalculationException
	{
		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("tracelRoteCode");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setTravelRoute(travelRouteModel);
		orderEntryInfo.setPriceLevel(TravelservicesConstants.PRICING_LEVEL_ROUTE);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0004");
		orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		Mockito.doNothing().when(sessionService).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());

		findTravelPricingWithCurrentPriceFactoryStrategy.findBasePrice(entry);
		verify(sessionService, times(1)).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());

	}

	@Test
	public void testfindBasePriceWithSectorPriceLevel() throws CalculationException
	{
		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("tracelRoteCode");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setTravelRoute(travelRouteModel);
		orderEntryInfo.setPriceLevel(TravelservicesConstants.PRICING_LEVEL_SECTOR);

		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		travelSectorModel.setCode("travelSectorCode");

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0004");
		transportOfferingModel.setTravelSector(travelSectorModel);
		orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		Mockito.doNothing().when(sessionService).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());
		findTravelPricingWithCurrentPriceFactoryStrategy.findBasePrice(entry);
		verify(sessionService, times(1)).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());

	}

	@Test
	public void testfindBasePriceWithTOPriceLevel() throws CalculationException
	{
		final TravelRouteModel travelRouteModel = new TravelRouteModel();
		travelRouteModel.setCode("tracelRoteCode");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setTravelRoute(travelRouteModel);
		orderEntryInfo.setPriceLevel(TravelservicesConstants.PRICING_LEVEL_TRANSPORT_OFFERING);

		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		transportOfferingModel.setCode("EZY0004");
		orderEntryInfo.setTransportOfferings(Stream.of(transportOfferingModel).collect(Collectors.toList()));
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		Mockito.doNothing().when(sessionService).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());
		findTravelPricingWithCurrentPriceFactoryStrategy.findBasePrice(entry);
		verify(sessionService, times(1)).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.anyMap());

	}

	@Test
	public void testfindBasePriceWithEmptyPriceLevel() throws CalculationException
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
		orderEntryInfo.setPriceLevel(null);
		entry.setTravelOrderEntryInfo(orderEntryInfo);

		Mockito.doNothing().when(sessionService).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.any());
		findTravelPricingWithCurrentPriceFactoryStrategy.findBasePrice(entry);
		verify(sessionService, times(1)).setAttribute(Matchers.eq(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP),
				Matchers.any());

	}
}
