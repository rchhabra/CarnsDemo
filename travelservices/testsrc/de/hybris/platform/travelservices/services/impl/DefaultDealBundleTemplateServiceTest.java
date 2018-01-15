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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultDealBundleTemplateService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDealBundleTemplateServiceTest
{
	@InjectMocks
	DefaultDealBundleTemplateService defaultDealBundleTemplateService;

	@Mock
	private BundleTemplateService bundleTemplateService;

	@Mock
	private BookingService bookingService;

	@Test
	public void testAbstractOrderIsDeal()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = testSetup.createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final BundleTemplateModel bundleTemplate1 = new BundleTemplateModel();
		final BundleTemplateModel bundleTemplate2 = new AccommodationBundleTemplateModel();
		final AbstractOrderEntryModel entry1 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION, null, product2, 1, 100d, 100d, Collections.emptyList(), bundleTemplate2, 1);
		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList(), bundleTemplate1, 2);
		final AbstractOrderEntryModel entry3 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList(), bundleTemplate1, 3);
		final AbstractOrderEntryModel entry4 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList(), bundleTemplate1, 4);

		final DealBundleTemplateModel dealBundleTemplateModel = new DealBundleTemplateModel();
		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate1)).thenReturn(dealBundleTemplateModel);
		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate2)).thenReturn(dealBundleTemplateModel);
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setEntries(Stream.of(entry1, entry2, entry3, entry4).collect(Collectors.toList()));

		Assert.assertTrue(defaultDealBundleTemplateService.abstractOrderIsDeal(order));

		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate1)).thenReturn(dealBundleTemplateModel);
		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate2)).thenReturn(new DealBundleTemplateModel());

		Assert.assertFalse(defaultDealBundleTemplateService.abstractOrderIsDeal(order));

		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate1)).thenReturn(new BundleTemplateModel());
		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate2)).thenReturn(dealBundleTemplateModel);

		Assert.assertFalse(defaultDealBundleTemplateService.abstractOrderIsDeal(order));
		Assert.assertFalse(defaultDealBundleTemplateService.abstractOrderIsDeal(null));
	}

	@Test
	public void testAbstractOrderIsDealForAccommodationOnlyJourney()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product2 = testSetup.createProductModel("product2", ProductType.ACCOMMODATION);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final BundleTemplateModel bundleTemplate2 = new AccommodationBundleTemplateModel();
		final AbstractOrderEntryModel entry1 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION, null, product2, 1, 100d, 100d, Collections.emptyList(), bundleTemplate2, 1);

		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.ACCOMMODATION,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList(), bundleTemplate2, 2);

		final DealBundleTemplateModel dealBundleTemplateModel = new DealBundleTemplateModel();
		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate2)).thenReturn(dealBundleTemplateModel);
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setEntries(Stream.of(entry1, entry2).collect(Collectors.toList()));


		Assert.assertFalse(defaultDealBundleTemplateService.abstractOrderIsDeal(order));


	}

	@Test
	public void testAbstractOrderIsDealForTransportOnlyJourney()
	{
		final TestSetup testSetup = new TestSetup();
		final ProductModel product1 = testSetup.createProductModel("product1", ProductType.FEE);
		final ProductModel product3 = testSetup.createProductModel("product3", ProductType.FARE_PRODUCT);

		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = testSetup.createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final TransportOfferingModel transportOfferingModelInbound = testSetup
				.createTransportOffering(TravelDateUtils.addDays(currentDate, 2), TravelDateUtils.addDays(currentDate, 4));

		final LocationModel originlocation = testSetup.createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = testSetup.createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = testSetup.createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = testSetup.createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = testSetup.createTravelRoute(origin, destination);

		final BundleTemplateModel bundleTemplate1 = new BundleTemplateModel();
		final AbstractOrderEntryModel entry2 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product1, 1, 100d, 100d, Collections.emptyList(), bundleTemplate1, 2);
		final AbstractOrderEntryModel entry3 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(1, Collections.singletonList(transportOfferingModelInbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList(), bundleTemplate1, 3);
		final AbstractOrderEntryModel entry4 = testSetup.createAbstractOrderEntryModel(true, AmendStatus.NEW,
				OrderEntryType.TRANSPORT,
				testSetup.createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute),
				product3, 1, 100d, 100d, Collections.emptyList(), bundleTemplate1, 4);

		final DealBundleTemplateModel dealBundleTemplateModel = new DealBundleTemplateModel();
		Mockito.when(bundleTemplateService.getRootBundleTemplate(bundleTemplate1)).thenReturn(dealBundleTemplateModel);
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setEntries(Stream.of(entry2, entry3, entry4).collect(Collectors.toList()));

		Assert.assertFalse(defaultDealBundleTemplateService.abstractOrderIsDeal(order));


	}

	private class TestSetup
	{
		private AbstractOrderEntryModel createAbstractOrderEntryModel(final boolean isActive, final AmendStatus amendStatus,
				final OrderEntryType orderEntryType, final TravelOrderEntryInfoModel travelOrderEntryInfoModel,
				final ProductModel product, final int quantity, final double basePrice, final double totalPrice,
				final List<TaxValue> taxValues, final BundleTemplateModel bundleTemplate, final int bundleNo)
		{
			final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel()
			{
				@Override
				public Collection<TaxValue> getTaxValues()
				{
					return taxValues;
				}

			};
			abstractOrderEntryModel.setTravelOrderEntryInfo(travelOrderEntryInfoModel);
			abstractOrderEntryModel.setActive(isActive);
			abstractOrderEntryModel.setProduct(product);
			abstractOrderEntryModel.setType(orderEntryType);
			abstractOrderEntryModel.setQuantity(Long.valueOf(quantity));
			abstractOrderEntryModel.setBasePrice(basePrice);
			abstractOrderEntryModel.setTotalPrice(totalPrice);
			abstractOrderEntryModel.setAmendStatus(amendStatus);
			abstractOrderEntryModel.setBundleTemplate(bundleTemplate);
			abstractOrderEntryModel.setBundleNo(bundleNo);
			return abstractOrderEntryModel;
		}

		private AccommodationOrderEntryGroupModel createAccommodationOrderEntryGroupModel(
				final AccommodationModel accommodationModel, final AccommodationOfferingModel accommodationOfferingModel,
				final RatePlanModel ratePlan, final List<AbstractOrderEntryModel> entries)
		{
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = new AccommodationOrderEntryGroupModel();
			accommodationOrderEntryGroupModel.setAccommodation(accommodationModel);
			accommodationOrderEntryGroupModel.setAccommodationOffering(accommodationOfferingModel);
			accommodationOrderEntryGroupModel.setRatePlan(ratePlan);
			accommodationOrderEntryGroupModel.setEntries(entries);
			return accommodationOrderEntryGroupModel;

		}

		private AccommodationModel createAccommodationModel(final String code)
		{
			final AccommodationModel accommodationModel = new AccommodationModel();
			accommodationModel.setCode(code);
			return accommodationModel;
		}

		private AccommodationOfferingModel createAccommodationOfferingModel(final String code)
		{
			final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
			accommodationOfferingModel.setCode(code);
			return accommodationOfferingModel;
		}

		private RatePlanModel createRatePlanModel(final String code)
		{
			final RatePlanModel ratePlan = new RatePlanModel();
			ratePlan.setCode(code);
			return ratePlan;
		}

		private ProductModel createProductModel(final String code, final ProductType productType)
		{
			final ProductModel product = new ProductModel();
			product.setCode(code);
			product.setProductType(productType);
			return product;
		}

		private TravelOrderEntryInfoModel createTravelOrderEntryInfoModel(final int originDestinationRefNum,
				final List<TransportOfferingModel> transportOfferings, final TravelRouteModel travelRoute)
		{
			final TravelOrderEntryInfoModel travelOrderEntryInfoModel = new TravelOrderEntryInfoModel();
			travelOrderEntryInfoModel.setOriginDestinationRefNumber(originDestinationRefNum);
			travelOrderEntryInfoModel.setTransportOfferings(transportOfferings);
			travelOrderEntryInfoModel.setTravelRoute(travelRoute);
			return travelOrderEntryInfoModel;
		}

		private TravelRouteModel createTravelRoute(final TransportFacilityModel origin, final TransportFacilityModel destination)
		{
			final TravelRouteModel travelRoute = new TravelRouteModel();
			travelRoute.setOrigin(origin);
			travelRoute.setDestination(destination);
			return travelRoute;
		}

		private TransportFacilityModel createTransportFacilityModel(final String code, final LocationModel locationModel)
		{
			final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
			transportFacilityModel.setCode(code);
			transportFacilityModel.setLocation(locationModel);
			return transportFacilityModel;
		}

		private LocationModel createLocationModel(final String code)
		{
			final LocationModel location = new LocationModel();
			location.setCode(code);
			return location;
		}

		private TransportOfferingModel createTransportOffering(final Date departureTime, final Date arrivalTime)
		{
			final TransportOfferingModel transportOffering = new TransportOfferingModel();
			transportOffering.setDepartureTime(departureTime);
			transportOffering.setArrivalTime(arrivalTime);
			return transportOffering;
		}

		private TravellerModel createTravellerModel(final TravellerInfoModel travellerInfoModel)
		{
			final TravellerModel traveller = new TravellerModel();
			traveller.setInfo(travellerInfoModel);
			return traveller;
		}

		private TravellerInfoModel createTravellerInfoModel()
		{
			final TravellerInfoModel travellerInfo = new TravellerInfoModel();
			return travellerInfo;
		}


	}

}