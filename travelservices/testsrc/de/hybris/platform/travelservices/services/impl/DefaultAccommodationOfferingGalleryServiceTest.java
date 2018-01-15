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
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.travelservices.dao.AccommodationOfferingGalleryDao;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultAccommodationOfferingGalleryService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationOfferingGalleryServiceTest
{
	@InjectMocks
	private DefaultAccommodationOfferingGalleryService accommodationOfferingGalleryService;
	@Mock
	private AccommodationOfferingGalleryDao accommodationOfferingGalleryDao;

	@Test
	public void getAccommodationOfferingGalleryTest()
	{
		final AccommodationOfferingGalleryModel accommodationOfferingGallery = new AccommodationOfferingGalleryModel();

		Mockito.when(accommodationOfferingGalleryDao.findAccommodationOfferingGallery(Matchers.any()))
				.thenReturn(accommodationOfferingGallery);

		final AccommodationOfferingGalleryModel result = accommodationOfferingGalleryService
				.getAccommodationOfferingGallery("acco1");
		Assert.assertNotNull(result);
	}

	@Test
	public void getAccommodationOfferingGalleryUsingCatalogTest()
	{
		final AccommodationOfferingGalleryModel accommodationOfferingGallery = new AccommodationOfferingGalleryModel();

		Mockito.when(accommodationOfferingGalleryDao.findAccommodationOfferingGallery(Matchers.any(), Matchers.any()))
				.thenReturn(accommodationOfferingGallery);
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		final AccommodationOfferingGalleryModel result = accommodationOfferingGalleryService
				.getAccommodationOfferingGallery("acco1", catalogVersionModel);
		Assert.assertNotNull(result);
	}
}
