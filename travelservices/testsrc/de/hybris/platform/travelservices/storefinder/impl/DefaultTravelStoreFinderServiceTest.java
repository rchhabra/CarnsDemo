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

package de.hybris.platform.travelservices.storefinder.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.dao.TravelPointOfServiceDao;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * Unit Test for the DefaultTravelStoreFinderService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelStoreFinderServiceTest
{
	@InjectMocks
	private DefaultTravelStoreFinderService travelStoreFinderService;

	@Mock
	private TravelPointOfServiceDao travelPointOfServiceDao;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private PointOfServiceModel pointOfService;

	@Test
	public void positionSearchTest()
	{
		final GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(77.1000);
		geoPoint.setLongitude(28.5562);

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(1);

		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = travelStoreFinderService
				.positionSearch(baseStoreModel, geoPoint, pageableData, Collections.singletonList(pointOfService));

		Assert.assertNotNull(searchPageData);
	}


	@Test
	public void positionSearchTestForNullResults()
	{
		final GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(77.1000);
		geoPoint.setLongitude(28.5562);

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(1);

		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = travelStoreFinderService
				.positionSearch(baseStoreModel, geoPoint, pageableData, null);

		Assert.assertNotNull(searchPageData);
	}

	@Test
	public void getPointOfServiceTest()
	{
		final TransportFacilityData transportFacilityData = new TransportFacilityData();
		transportFacilityData.setCode("LTN");
		final Collection<PointOfServiceModel> posModels = travelStoreFinderService.getPointOfService(baseStoreModel,
				Collections.singletonList(transportFacilityData));
		Assert.assertNotNull(posModels);
	}




}
