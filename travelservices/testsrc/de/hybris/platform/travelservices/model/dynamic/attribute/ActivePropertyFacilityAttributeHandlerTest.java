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

package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ActivePropertyFacilityAttributeHandlerTest
{
	@InjectMocks
	private ActivePropertyFacilityAttributeHandler activePropertyFacilityAttributeHandler;

	@Test
	public void testGet()
	{
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
		Assert.assertTrue(CollectionUtils.isEmpty(activePropertyFacilityAttributeHandler.get(accommodationOfferingModel)));

		final PropertyFacilityModel propertyFacilityModel=new PropertyFacilityModel();
		propertyFacilityModel.setActive(true);
		final Collection<PropertyFacilityModel> propertyFacilitiesExp=Collections.singletonList(propertyFacilityModel);
		accommodationOfferingModel.setPropertyFacility(propertyFacilitiesExp);
		final List<PropertyFacilityModel> propertyFacilities=activePropertyFacilityAttributeHandler.get(accommodationOfferingModel);

		Assert.assertTrue(activePropertyFacilityAttributeHandler.get(accommodationOfferingModel).contains(propertyFacilityModel));
	}

}
