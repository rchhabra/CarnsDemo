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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.storefinder.TravelStoreFinderService;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test to test the functionality of the TravelStoreFinder Service
 */
@IntegrationTest
public class DefaultTravelStoreFinderServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private TravelStoreFinderService travelStoreFinderService;

	@Resource
	private ModelService modelService;

	private PointOfServiceModel pointOfService;
	private BaseStoreModel baseStoreModel;
	private TransportFacilityModel transportFacilityModel;

	@Before
	public void setUp()
	{
		baseStoreModel = modelService.create(BaseStoreModel._TYPECODE);
		baseStoreModel.setUid("12345678");
		transportFacilityModel = modelService.create(TransportFacilityModel._TYPECODE);
		transportFacilityModel.setCode("DEL");

		pointOfService = new PointOfServiceModel();
		pointOfService.setName("DelhiPOS");
		pointOfService.setTimeZoneId("Indian/Maldives");
		pointOfService.setLatitude(77.1000);
		pointOfService.setLongitude(28.5562);
		pointOfService.setBaseStore(baseStoreModel);
		pointOfService.setType(PointOfServiceTypeEnum.STORE);
		pointOfService.setTransportFacility(transportFacilityModel);

		modelService.saveAll(pointOfService);
	}

	@Test
	public void testGetPointOfService()
	{
		final TransportFacilityData transportFacilityData = new TransportFacilityData();
		transportFacilityData.setCode("DEL");
		final Collection<PointOfServiceModel> posModels = travelStoreFinderService.getPointOfService(baseStoreModel,
				Collections.singletonList(transportFacilityData));
		Assert.assertEquals(pointOfService.getName(), posModels.iterator().next().getName());
	}

	@Test
	public void testGetNoPointOfService()
	{
		final TransportFacilityData transportFacilityData = new TransportFacilityData();
		transportFacilityData.setCode("IXC");
		final Collection<PointOfServiceModel> posModels = travelStoreFinderService.getPointOfService(baseStoreModel,
				Collections.singletonList(transportFacilityData));

		Assert.assertTrue(CollectionUtils.isEmpty(posModels));
	}

	@Test
	public void testPositionSearch()
	{
		final GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(77.1000);
		geoPoint.setLongitude(28.5562);

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(1);

		final StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData = travelStoreFinderService
				.positionSearch(baseStoreModel, geoPoint, pageableData, Collections.singletonList(pointOfService));
		final PointOfServiceDistanceData pos = searchPageData.getResults().get(0);
		Assert.assertEquals(pointOfService.getName(), pos.getPointOfService().getName());
	}
}
