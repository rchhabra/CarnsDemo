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

package de.hybris.platform.travelservices.bundle.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.dao.TravelBundleTemplateDao;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultTravelBundleTemplateService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelBundleTemplateServiceTest
{
	@InjectMocks
	DefaultTravelBundleTemplateService defaultTravelBundleTemplateService;

	@Mock
	private TravelBundleTemplateDao travelBundleTemplateDao;

	@Mock
	private CabinClassModel cabinClassModel;

	@Mock
	private BookingService bookingService;

	private final List<BundleTemplateModel> result = Collections.singletonList(new BundleTemplateModel());
	@Test
	public void testGetBundleTemplatesForTravelRouteModel()
	{
		final TravelRouteModel travelRouteModel=new TravelRouteModel();
		Mockito.when(travelBundleTemplateDao.findBundleTemplates(travelRouteModel, cabinClassModel)).thenReturn(result);
		Assert.assertNotNull(defaultTravelBundleTemplateService.getBundleTemplates(travelRouteModel, cabinClassModel));
	}

	@Test
	public void testGetBundleTemplatesForTravelSectorModel()
	{
		final TravelSectorModel travelSectorModel = new TravelSectorModel();
		Mockito.when(travelBundleTemplateDao.findBundleTemplates(travelSectorModel, cabinClassModel)).thenReturn(result);
		Assert.assertNotNull(defaultTravelBundleTemplateService.getBundleTemplates(travelSectorModel, cabinClassModel));
	}

	@Test
	public void testGetBundleTemplatesForTransportOfferingModel()
	{
		final TransportOfferingModel transportOfferingModel = new TransportOfferingModel();
		Mockito.when(travelBundleTemplateDao.findBundleTemplates(transportOfferingModel, cabinClassModel)).thenReturn(result);
		Assert.assertNotNull(defaultTravelBundleTemplateService.getBundleTemplates(transportOfferingModel, cabinClassModel));
	}

	@Test
	public void testGetDefaultBundleTemplates()
	{
		Mockito.when(travelBundleTemplateDao.findDefaultBundleTemplates(cabinClassModel)).thenReturn(result);
		Assert.assertNotNull(defaultTravelBundleTemplateService.getDefaultBundleTemplates(cabinClassModel));
	}

	@Test
	public void testGetBundleTemplateIdFromCartForNullOrderArgument()
	{
		Assert.assertEquals(StringUtils.EMPTY, defaultTravelBundleTemplateService.getBundleTemplateIdFromOrder(null, 0));
	}

	@Test
	public void testGetBundleTemplateIdFromCartForAccommodationOnlyOrderArgument()
	{
		final OrderModel order = new OrderModel();
		Mockito.when(bookingService.checkIfAnyOrderEntryByType(order, OrderEntryType.TRANSPORT)).thenReturn(Boolean.FALSE);
		Mockito.when(bookingService.checkIfAnyOrderEntryByType(order, OrderEntryType.ACCOMMODATION)).thenReturn(Boolean.TRUE);
		Assert.assertEquals(StringUtils.EMPTY, defaultTravelBundleTemplateService.getBundleTemplateIdFromOrder(order, 0));
	}

	@Test
	public void testGetBundleTemplateIdFromCartForTransportOnlyOneWayOrderArgument()
	{
		final ProductModel product = createProductModel("product", ProductType.FARE_PRODUCT);
		final Date currentDate = new Date();

		final TransportOfferingModel transportOfferingModelOutbound = createTransportOffering(currentDate,
				TravelDateUtils.addDays(currentDate, 2));
		final LocationModel originlocation = createLocationModel("Test_Origin_Location_Code");
		final TransportFacilityModel origin = createTransportFacilityModel("Test_Origin_Code", originlocation);

		final LocationModel destinationlocation = createLocationModel("Test_Destination_Location_Code");
		final TransportFacilityModel destination = createTransportFacilityModel("Test_Origin_Code", destinationlocation);


		final TravelRouteModel travelRoute = createTravelRoute(origin, destination);

		final AbstractOrderEntryModel entry = createAbstractOrderEntryModel(true, AmendStatus.NEW, OrderEntryType.TRANSPORT,
				createTravelOrderEntryInfoModel(0, Collections.singletonList(transportOfferingModelOutbound), travelRoute), product,
				1, 100d, 100d, Collections.emptyList());
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setType(BundleType.ECONOMY);
		entry.setBundleTemplate(bundleTemplate);
		final OrderModel order = new OrderModel();
		order.setEntries(Collections.singletonList(entry));
		Mockito.when(bookingService.checkIfAnyOrderEntryByType(order, OrderEntryType.TRANSPORT)).thenReturn(Boolean.TRUE);
		Assert.assertEquals(StringUtils.EMPTY, defaultTravelBundleTemplateService.getBundleTemplateIdFromOrder(order, 1));
		Assert.assertEquals("ECONOMY", defaultTravelBundleTemplateService.getBundleTemplateIdFromOrder(order, 0));
	}

	private ProductModel createProductModel(final String code, final ProductType productType)
	{
		final ProductModel product = new ProductModel();
		product.setCode(code);
		product.setProductType(productType);
		return product;
	}

	private TransportOfferingModel createTransportOffering(final Date departureTime, final Date arrivalTime)
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setDepartureTime(departureTime);
		transportOffering.setArrivalTime(arrivalTime);
		return transportOffering;
	}

	private LocationModel createLocationModel(final String code)
	{
		final LocationModel location = new LocationModel();
		location.setCode(code);
		return location;
	}

	private TransportFacilityModel createTransportFacilityModel(final String code, final LocationModel locationModel)
	{
		final TransportFacilityModel transportFacilityModel = new TransportFacilityModel();
		transportFacilityModel.setCode(code);
		transportFacilityModel.setLocation(locationModel);
		return transportFacilityModel;
	}

	private TravelRouteModel createTravelRoute(final TransportFacilityModel origin, final TransportFacilityModel destination)
	{
		final TravelRouteModel travelRoute = new TravelRouteModel();
		travelRoute.setOrigin(origin);
		travelRoute.setDestination(destination);
		return travelRoute;
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

	private AbstractOrderEntryModel createAbstractOrderEntryModel(final boolean isActive, final AmendStatus amendStatus,
			final OrderEntryType orderEntryType, final TravelOrderEntryInfoModel travelOrderEntryInfoModel,
			final ProductModel product, final int quantity, final double basePrice, final double totalPrice,
			final List<TaxValue> taxValues)
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
		return abstractOrderEntryModel;
	}
}