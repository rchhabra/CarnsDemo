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

package de.hybris.platform.travelfacades.facades.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfacades.search.TransportOfferingSearchFacade;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.travelservices.storefinder.TravelStoreFinderService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultTransportFacilityFacadeTest
{
	private DefaultTransportFacilityFacade transportFacilityFacade;
	@Mock
	private Converter<TransportFacilityModel, TransportFacilityData> transportFacilityConverter;
	@Mock
	private TravelStoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> travelStoreFinderService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private TransportFacilityService transportFacilityService;
	@Mock
	private Converter<LocationModel, LocationData> locationConverter;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private TransportFacilityModel transportFacilityModel;
	@Mock
	private LocationModel locationModel;
	@Mock
	private TransportFacilityData transportFacilityData;
	@Mock
	private LocationData locationData;
	@Mock
	private GeoPoint geoPoint;
	@Mock
	private PageableData pageableData;
	@Mock
	private StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData;
	@Mock
	private TransportOfferingSearchFacade<TransportOfferingData> transportOfferingSearchFacade;
	@Mock
	TransportOfferingSearchPageData<SearchData, TransportOfferingData> transportOfferingSearchPageData;



	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		transportFacilityFacade = new DefaultTransportFacilityFacade();
		transportFacilityFacade.setBaseStoreService(baseStoreService);
		transportFacilityFacade.setTravelStoreFinderService(travelStoreFinderService);
		transportFacilityFacade.setLocationConverter(locationConverter);
		transportFacilityFacade.setTransportFacilityConverter(transportFacilityConverter);
		transportFacilityFacade.setTransportFacilityService(transportFacilityService);
		transportFacilityFacade.setTransportOfferingSearchFacade(transportOfferingSearchFacade);

	}

	@Test
	public void testfindNearestTransportFacility()
	{
		when(transportFacilityFacade.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);

		when(transportFacilityFacade.getTravelStoreFinderService().positionSearch(baseStoreModel, geoPoint, pageableData))
				.thenReturn(searchPageData);

		final List<PointOfServiceDistanceData> posList = new ArrayList<>();
		final PointOfServiceDistanceData pos = new PointOfServiceDistanceData();
		final PointOfServiceModel pointOfService = new PointOfServiceModel();
		pointOfService.setTransportFacility(transportFacilityModel);
		pos.setPointOfService(pointOfService);
		posList.add(pos);

		when(searchPageData.getResults()).thenReturn(posList);
		when(transportFacilityFacade.getTransportFacilityConverter().convert(transportFacilityModel))
				.thenReturn(transportFacilityData);

		final TransportFacilityData transportFacility = transportFacilityFacade.findNearestTransportFacility(geoPoint,
				pageableData);
		Assert.assertNotNull(transportFacility);

	}

	@Test
	public void testfindNearestTransportFacilityWithActivity()
	{
		final DefaultTransportFacilityFacade transportFacilityFacadeSpy = Mockito.spy(transportFacilityFacade);
		when(transportFacilityFacadeSpy.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);

		final String activity = "city_break";
		final PointOfServiceDistanceData pos = new PointOfServiceDistanceData();
		final PointOfServiceModel pointOfService = new PointOfServiceModel();
		pointOfService.setTransportFacility(transportFacilityModel);
		pos.setPointOfService(pointOfService);
		final Collection<PointOfServiceModel> posList = Collections.singletonList(pointOfService);

		final TransportFacilityData transportFacilityData = new TransportFacilityData();
		transportFacilityData.setCode("LTN");
		transportFacilityData.setName("Luton Airport");
		transportFacilityData.setLocation(locationData);
		final List<TransportFacilityData> transportFacilityForActivity = new ArrayList(
				Collections.singletonList(transportFacilityData));

		Mockito.doReturn(transportFacilityForActivity).when(transportFacilityFacadeSpy).getOriginTransportFacility(activity);

		when(transportFacilityFacadeSpy.getTravelStoreFinderService().getPointOfService(baseStoreModel,transportFacilityForActivity))
				.thenReturn(posList);

		when(transportFacilityFacadeSpy.getTravelStoreFinderService().positionSearch(baseStoreModel, geoPoint, pageableData,posList))
				.thenReturn(searchPageData);

		when(searchPageData.getResults()).thenReturn(new ArrayList(Collections.singletonList(pos)));
		when(transportFacilityFacadeSpy.getTransportFacilityConverter().convert(transportFacilityModel))
				.thenReturn(transportFacilityData);

		final TransportFacilityData transportFacility = transportFacilityFacadeSpy.findNearestTransportFacility(geoPoint, activity,
				pageableData);
		Assert.assertNotNull(transportFacility);
		Assert.assertEquals(transportFacility.getCode(), transportFacilityData.getCode());

	}

	@Test
	public void testGetOriginTransportFacility()
	{
		final String activity = "city_break";
		final SearchData searchData = Mockito.mock(SearchData.class);
		final TransportOfferingData tod = new TransportOfferingData();
		final TravelSectorData ts = new TravelSectorData();
		final TransportFacilityData transportFacilityData = new TransportFacilityData();
		transportFacilityData.setCode("LTN");
		transportFacilityData.setName("Luton Airport");
		transportFacilityData.setLocation(locationData);
		ts.setOrigin(transportFacilityData);
		tod.setSector(ts);

		final DefaultTransportFacilityFacade transportFacilityFacadeSpy = Mockito.spy(transportFacilityFacade);
		Mockito.doReturn(searchData).when(transportFacilityFacadeSpy).createSearchData(activity);
		when(transportOfferingSearchPageData.getResults()).thenReturn(Collections.singletonList(tod));
		when(transportFacilityFacadeSpy.getTransportOfferingSearchFacade().transportOfferingSearch(searchData))
				.thenReturn(transportOfferingSearchPageData);
		final List<TransportFacilityData> originTransportFacility = transportFacilityFacadeSpy
				.getOriginTransportFacility("city_break");
		Assert.assertNotNull(originTransportFacility);
		Assert.assertEquals(originTransportFacility.get(0).getCode(), transportFacilityData.getCode());

	}

	@Test
	public void testfindNearestTransportFacilityWhenResultIsEmpty()
	{
		when(transportFacilityFacade.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);

		when(transportFacilityFacade.getTravelStoreFinderService().positionSearch(baseStoreModel, geoPoint, pageableData))
				.thenReturn(searchPageData);

		when(searchPageData.getResults()).thenReturn(Collections.emptyList());

		final TransportFacilityData transportFacility = transportFacilityFacade.findNearestTransportFacility(geoPoint,
				pageableData);
		Assert.assertNull(transportFacility);

	}

	@Test
	public void testfindNearestTransportFacilityWhenPosIsEmpty()
	{
		when(transportFacilityFacade.getBaseStoreService().getCurrentBaseStore()).thenReturn(baseStoreModel);

		when(transportFacilityFacade.getTravelStoreFinderService().positionSearch(baseStoreModel, geoPoint, pageableData))
				.thenReturn(searchPageData);

		final List<PointOfServiceDistanceData> posList = new ArrayList<>();
		final PointOfServiceDistanceData pos = new PointOfServiceDistanceData();
		posList.add(pos);

		when(searchPageData.getResults()).thenReturn(posList);

		final TransportFacilityData transportFacility = transportFacilityFacade.findNearestTransportFacility(geoPoint,
				pageableData);
		Assert.assertNull(transportFacility);

	}

	@Test
	public void testGetCountry()
	{
		when(transportFacilityFacade.getTransportFacilityService().getTransportFacility(Matchers.anyString()))
				.thenReturn(transportFacilityModel);

		when(transportFacilityService.getCountry(transportFacilityModel)).thenReturn(locationModel);

		when(transportFacilityFacade.getLocationConverter().convert(locationModel)).thenReturn(locationData);

		final LocationData locationData = transportFacilityFacade.getCountry(Matchers.anyString());
		Assert.assertNotNull(locationData);

	}

	@Test
	public void testGetCountryWhenLocationIsNull()
	{
		when(transportFacilityFacade.getTransportFacilityService().getTransportFacility(Matchers.anyString()))
				.thenReturn(transportFacilityModel);

		when(transportFacilityService.getCountry(transportFacilityModel)).thenReturn(null);

		final LocationData locationData = transportFacilityFacade.getCountry(Matchers.anyString());
		Assert.assertNull(locationData);

	}

	@Test
	public void testGetLocation()
	{
		when(transportFacilityFacade.getTransportFacilityService().getTransportFacility(Matchers.anyString()))
				.thenReturn(transportFacilityModel);

		when(transportFacilityModel.getLocation()).thenReturn(locationModel);

		when(transportFacilityFacade.getLocationConverter().convert(locationModel)).thenReturn(locationData);

		final LocationData locationData = transportFacilityFacade.getLocation(Matchers.anyString());
		Assert.assertNotNull(locationData);

	}

	@Test
	public void testGetLocationWhenLocationIsNull()
	{
		when(transportFacilityFacade.getTransportFacilityService().getTransportFacility(Matchers.anyString()))
				.thenReturn(transportFacilityModel);

		when(transportFacilityModel.getLocation()).thenReturn(null);

		final LocationData locationData = transportFacilityFacade.getLocation(Matchers.anyString());
		Assert.assertNull(locationData);

	}

	@Test
	public void testGetTransportFacility()
	{
		when(transportFacilityFacade.getTransportFacilityService().getTransportFacility(Matchers.anyString()))
				.thenReturn(transportFacilityModel);

		when(transportFacilityFacade.getTransportFacilityConverter().convert(transportFacilityModel))
				.thenReturn(transportFacilityData);

		final TransportFacilityData transportFacilityData = transportFacilityFacade.getTransportFacility(Matchers.anyString());
		Assert.assertNotNull(transportFacilityData);

	}

	@Test
	public void testGetTransportFacilityWhenNull()
	{
		when(transportFacilityFacade.getTransportFacilityService().getTransportFacility(Matchers.anyString())).thenReturn(null);

		final TransportFacilityData transportFacilityData = transportFacilityFacade.getTransportFacility(Matchers.anyString());
		Assert.assertNull(transportFacilityData);

	}

	@Test
	public void testCreateSearchData()
	{
		Assert.assertNotNull(transportFacilityFacade.createSearchData("beach").getFilterTerms().get("activity"));
		Assert.assertEquals("beach", transportFacilityFacade.createSearchData("beach").getFilterTerms().get("activity"));
	}


}
