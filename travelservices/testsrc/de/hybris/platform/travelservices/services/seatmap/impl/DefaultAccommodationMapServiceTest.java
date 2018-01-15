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

package de.hybris.platform.travelservices.services.seatmap.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.dao.ConfiguredAccommodationDao;
import de.hybris.platform.travelservices.dao.SelectedAccommodationDao;
import de.hybris.platform.travelservices.dao.TransportFacilityDao;
import de.hybris.platform.travelservices.dao.TransportVehicleConfigurationMappingDao;
import de.hybris.platform.travelservices.dao.TransportVehicleInfoDao;
import de.hybris.platform.travelservices.dao.TravelRouteDao;
import de.hybris.platform.travelservices.dao.TravelSectorDao;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.accommodationmap.impl.DefaultAccommodationMapService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationMapServiceTest
{

	@Mock
	private TransportVehicleConfigurationMappingDao transportVehicleConfigurationMappingDao;

	@Mock
	private TransportVehicleInfoDao transportVehicleInfoDao;

	@Mock
	private TransportFacilityDao transportFacilityDao;

	@Mock
	private TravelSectorDao travelSectorDao;

	@Mock
	private TravelRouteDao travelRouteDao;

	@Mock
	private ConfiguredAccommodationDao configuredAccommodationDao;

	@Mock
	private TransportVehicleInfoModel transportVehicleInfoModel;

	@Mock
	private TransportOfferingModel transportOfferingModel;

	@Mock
	private AccommodationMapModel accommodationMapModel;

	@Mock
	private TravelSectorModel travelSectorModel;

	@Mock
	private TransportFacilityModel originModel;

	@Mock
	private TransportFacilityModel destinationModel;

	@Mock
	private TravelRouteModel travelRouteModel;

	@Mock
	private ConfiguredAccommodationModel configuredAccommodationModel;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private SelectedAccommodationDao selectedAccommodationDao;

	@InjectMocks
	private final DefaultAccommodationMapService defaultAccommodationMapService = new DefaultAccommodationMapService();

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void getVehicleConfigurationWhenVehicleInfoIsNullTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(null);
		defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode, null, null, null);
	}

	@Test
	public void getAccommodationMapForNullTransportOfferingTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		transportVehicleInfoModel.setCode(vehicleInfoCode);

		final TravelSectorData travelSectorData = new TravelSectorData();
		final TransportFacilityData origin = new TransportFacilityData();
		origin.setCode("testOriginCode");
		final TransportFacilityData destination = new TransportFacilityData();
		destination.setCode("testDestinationCode");
		travelSectorData.setOrigin(origin);
		travelSectorData.setDestination(destination);

		Mockito.when(transportFacilityDao.findTransportFacility(travelSectorData.getOrigin().getCode())).thenReturn(originModel);
		Mockito.when(transportFacilityDao.findTransportFacility(travelSectorData.getDestination().getCode()))
				.thenReturn(destinationModel);

		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(transportVehicleInfoModel);
		accommodationMapModel.setCode("testVehicleConfig");
		final String routeCode = "testRouteCode";
		Mockito.when(travelRouteDao.findTravelRoute(routeCode)).thenReturn(travelRouteModel);
		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelRouteModel, null))
				.thenReturn(accommodationMapModel);
		Mockito.when(travelSectorDao.findTravelSector(originModel, destinationModel)).thenReturn(travelSectorModel);
		final AccommodationMapModel receivedAccommodationMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				null, routeCode, travelSectorData);
		Assert.assertEquals(accommodationMapModel.getCode(), receivedAccommodationMap.getCode());
	}

	@Test
	public void getAccommodationMapMatchingTransportOfferingAndTransportVehicleInfoTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		transportVehicleInfoModel.setCode(vehicleInfoCode);
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(transportVehicleInfoModel);
		accommodationMapModel.setCode("testVehicleConfig");
		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, transportOfferingModel,
						catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online")))
				.thenReturn(accommodationMapModel);
		final AccommodationMapModel receivedAccommodationMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				transportOfferingModel, null, null);
		Assert.assertEquals(accommodationMapModel.getCode(), receivedAccommodationMap.getCode());
	}

	@Test
	public void getAccommodationMapMatchingTravelSectorInOfferingAndTransportVehicleInfoTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		transportVehicleInfoModel.setCode(vehicleInfoCode);
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(transportVehicleInfoModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, transportOfferingModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		Mockito.when(transportOfferingModel.getTravelSector()).thenReturn(travelSectorModel);
		accommodationMapModel.setCode("testVehicleConfig");
		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelSectorModel,
						catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online")))
				.thenReturn(accommodationMapModel);
		final AccommodationMapModel receivedAccommodationMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				transportOfferingModel, null, null);
		Assert.assertEquals(accommodationMapModel.getCode(), receivedAccommodationMap.getCode());
	}

	@Test
	public void getAccommodationMapMatchingTravelSectorNotInOfferingAndTransportVehicleInfoTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		transportVehicleInfoModel.setCode(vehicleInfoCode);
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(transportVehicleInfoModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, transportOfferingModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		Mockito.when(transportOfferingModel.getTravelSector()).thenReturn(null);

		final TravelSectorData travelSectorData = new TravelSectorData();
		final TransportFacilityData origin = new TransportFacilityData();
		origin.setCode("testOriginCode");
		final TransportFacilityData destination = new TransportFacilityData();
		destination.setCode("testDestinationCode");
		travelSectorData.setOrigin(origin);
		travelSectorData.setDestination(destination);

		Mockito.when(transportFacilityDao.findTransportFacility(travelSectorData.getOrigin().getCode())).thenReturn(originModel);
		Mockito.when(transportFacilityDao.findTransportFacility(travelSectorData.getDestination().getCode()))
				.thenReturn(destinationModel);
		Mockito.when(travelSectorDao.findTravelSector(originModel, destinationModel)).thenReturn(travelSectorModel);

		accommodationMapModel.setCode("testVehicleConfig");
		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelSectorModel,
						catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online")))
				.thenReturn(accommodationMapModel);


		final AccommodationMapModel receivedAccommodationMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				transportOfferingModel, null, travelSectorData);

		Assert.assertEquals(accommodationMapModel.getCode(), receivedAccommodationMap.getCode());
	}

	@Test
	public void getAccommodationMapMatchingTravelRouteAndTransportVehicleInfoTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		transportVehicleInfoModel.setCode(vehicleInfoCode);
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(transportVehicleInfoModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, transportOfferingModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		Mockito.when(transportOfferingModel.getTravelSector()).thenReturn(travelSectorModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelSectorModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		final String routeCode = "testRouteCode";
		Mockito.when(travelRouteDao.findTravelRoute(routeCode)).thenReturn(travelRouteModel);
		accommodationMapModel.setCode("testVehicleConfig");
		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelRouteModel,
						catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online")))
				.thenReturn(accommodationMapModel);
		final AccommodationMapModel receivedAccommodationMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				transportOfferingModel, routeCode, null);
		Assert.assertEquals(accommodationMapModel.getCode(), receivedAccommodationMap.getCode());
	}

	@Test
	public void getAccommodationMapMatchingTransportVehicleInfoTest()
	{
		final String vehicleInfoCode = "testVehicleInfo";
		transportVehicleInfoModel.setCode(vehicleInfoCode);
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(transportVehicleInfoModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, transportOfferingModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		Mockito.when(transportOfferingModel.getTravelSector()).thenReturn(travelSectorModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelSectorModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		final String routeCode = "testRouteCode";
		Mockito.when(travelRouteDao.findTravelRoute(routeCode)).thenReturn(travelRouteModel);
		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel, travelRouteModel,
				catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online"))).thenReturn(null);
		accommodationMapModel.setCode("testVehicleConfig");
		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(transportVehicleInfoModel,
						catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online")))
				.thenReturn(accommodationMapModel);
		final AccommodationMapModel receivedAccommodationMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				transportOfferingModel, routeCode, null);
		Assert.assertEquals(accommodationMapModel.getCode(), receivedAccommodationMap.getCode());
	}

	@Test
	public void getSeatMapConfigurationTest()
	{
		final List<ConfiguredAccommodationModel> configuredAccommodationModels = new ArrayList<>();
		configuredAccommodationModels.add(configuredAccommodationModel);
		accommodationMapModel.setCode("testVehicleConfig");
		Mockito
				.when(configuredAccommodationDao.findAccommodationMapConfiguration(accommodationMapModel,
						catalogVersionService.getCatalogVersion("testAirlineProductCatalog", "Online")))
				.thenReturn(configuredAccommodationModels);
		final List<ConfiguredAccommodationModel> seatMapConfiguration = defaultAccommodationMapService
				.getAccommodationMapConfiguration(accommodationMapModel);
		Assert.assertEquals(1, seatMapConfiguration.size());
	}

	@Test
	public void getAccommodationMapBasedOnTransportOffering()
	{
		final String vehicleInfoCode = "0001";
		final String route = "RT01";

		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(new TransportVehicleInfoModel());

		final AccommodationMapModel accommodationMap = new AccommodationMapModel();
		accommodationMap.setCode("1234");

		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(Mockito.any(TransportVehicleInfoModel.class),
						Mockito.any(TransportOfferingModel.class), Mockito.any(CatalogVersionModel.class)))
				.thenReturn(accommodationMap);


		final AccommodationMapModel aMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				new TransportOfferingModel(), route);

		Assert.assertEquals("1234", aMap.getCode());
	}

	@Test
	public void getAccommodationMapBasedOnTravelSector()
	{
		final String vehicleInfoCode = "0001";
		final String route = "RT01";

		final AccommodationMapModel accommodationMap = new AccommodationMapModel();
		accommodationMap.setCode("1234");

		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(new TransportVehicleInfoModel());

		Mockito
				.when(transportVehicleConfigurationMappingDao.findAccommodationMap(Mockito.any(TransportVehicleInfoModel.class),
						Mockito.any(TransportOfferingModel.class), Mockito.any(CatalogVersionModel.class)))
				.thenReturn(null, accommodationMap);

		final AccommodationMapModel aMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				new TransportOfferingModel(), route);

		Assert.assertEquals("1234", aMap.getCode());
	}

	@Test
	public void getAccommodationMapBasedOnTravelRoute()
	{
		final String vehicleInfoCode = "0001";
		final String route = "RT01";

		final AccommodationMapModel accommodationMap = new AccommodationMapModel();
		accommodationMap.setCode("1234");

		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(new TransportVehicleInfoModel());

		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(Mockito.any(TransportVehicleInfoModel.class),
				Mockito.any(TravelRouteModel.class), Mockito.any(CatalogVersionModel.class))).thenReturn(accommodationMap);

		Mockito.when(travelRouteDao.findTravelRoute(Mockito.anyString())).thenReturn(new TravelRouteModel());

		final AccommodationMapModel aMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				new TransportOfferingModel(), route);

		Assert.assertEquals("1234", aMap.getCode());
	}

	@Test
	public void getAccommodationMapBasedOnVehicleInfo()
	{
		final String vehicleInfoCode = "0001";
		final String route = "RT01";

		final AccommodationMapModel accommodationMap = new AccommodationMapModel();
		accommodationMap.setCode("1234");

		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(new TransportVehicleInfoModel());

		Mockito.when(transportVehicleConfigurationMappingDao.findAccommodationMap(Mockito.any(TransportVehicleInfoModel.class),
				Mockito.any(CatalogVersionModel.class))).thenReturn(accommodationMap);

		Mockito.when(travelRouteDao.findTravelRoute(Mockito.anyString())).thenReturn(new TravelRouteModel());

		final AccommodationMapModel aMap = defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode,
				new TransportOfferingModel(), route);

		Assert.assertEquals("1234", aMap.getCode());
	}

	@Test(expected = AccommodationMapDataSetUpException.class)
	public void getAccommodationMapBasedOnNullTransportVehicleInfo()
	{
		final String vehicleInfoCode = "0001";
		Mockito.when(transportVehicleInfoDao.findTranportVehicleInfo(vehicleInfoCode)).thenReturn(null);
		defaultAccommodationMapService.getAccommodationMap(vehicleInfoCode, null, null);
	}

	@Test
	public void testGetSelectedAccommodations()
	{
		Mockito.when(selectedAccommodationDao.findSelectedAccommodations(Matchers.any(TransportOfferingModel.class),
				Matchers.anyList(), Matchers.anyList())).thenReturn(Collections.emptyList());
		Assert.assertNotNull(defaultAccommodationMapService.getSelectedAccommodations(new TransportOfferingModel(),
				Collections.emptyList(), Collections.emptyList()));
	}

	@Test
	public void testGetAccommodation() throws AccommodationMapDataSetUpException
	{
		Mockito.when(configuredAccommodationDao.findAccommodation(Matchers.anyString(), Matchers.any(CatalogVersionModel.class)))
				.thenReturn(new ConfiguredAccommodationModel());
		Assert.assertNotNull(defaultAccommodationMapService.getAccommodation("TEST_UUID"));
	}

	@Test
	public void testGetAccommodationForException() throws AccommodationMapDataSetUpException
	{
		Mockito.when(configuredAccommodationDao.findAccommodation(Matchers.anyString(), Matchers.any(CatalogVersionModel.class)))
				.thenThrow(new AccommodationMapDataSetUpException("Exception"));
		Assert.assertNull(defaultAccommodationMapService.getAccommodation("TEST_UUID"));
	}
}
