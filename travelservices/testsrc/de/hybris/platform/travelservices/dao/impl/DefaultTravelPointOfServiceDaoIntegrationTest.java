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

package de.hybris.platform.travelservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.dao.TravelPointOfServiceDao;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration Test for the TravelPointOfServiceDao implementation using ServicelayerTransactionalTest
 */
@IntegrationTest
public class DefaultTravelPointOfServiceDaoIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private TravelPointOfServiceDao pointOfServiceDao;

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
		final AddressModel address = modelService.create(AddressModel._TYPECODE);


		final UserModel testUser = modelService.create(UserModel.class);
		testUser.setUid("testUser");
		address.setOwner(testUser);

		pointOfService = new PointOfServiceModel();
		pointOfService.setName("DelhiPOS");
		pointOfService.setTimeZoneId("Indian/Maldives");
		pointOfService.setLatitude(77.1000);
		pointOfService.setLongitude(28.5562);
		pointOfService.setBaseStore(baseStoreModel);
		pointOfService.setType(PointOfServiceTypeEnum.STORE);
		pointOfService.setTransportFacility(transportFacilityModel);
		pointOfService.setAddress(address);

		modelService.saveAll(pointOfService);
	}

	@Test
	public void testGetPointOfService()
	{
		final Map<String, Object> filterParams = new HashMap<String, Object>();
		filterParams.put("baseStore", baseStoreModel);

		final TransportFacilityData transportFacilityData=new TransportFacilityData();
		transportFacilityData.setCode("DEL");
		filterParams.put("transportFacility", Collections.singletonList(transportFacilityData));

		filterParams.put("type", PointOfServiceTypeEnum.STORE);

		final Collection<PointOfServiceModel> posModels = pointOfServiceDao.getPointOfService(filterParams);
		Assert.assertEquals(pointOfService.getName(), posModels.iterator().next().getName());
	}

	@Test
	public void testGetGeocodedPOS()
	{
		final Collection<PointOfServiceModel> geoCodedPOS = pointOfServiceDao.getGeocodedPOS();

		Assert.assertTrue(CollectionUtils.isNotEmpty(geoCodedPOS));
	}

	@Test
	public void testGetGeocodedPOSWithSize()
	{
		final Collection<PointOfServiceModel> geoCodedPOS = pointOfServiceDao.getGeocodedPOS(1);

		Assert.assertEquals(1, geoCodedPOS.size());
	}
}
