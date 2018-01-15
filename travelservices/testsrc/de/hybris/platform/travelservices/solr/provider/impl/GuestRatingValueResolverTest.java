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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultSolrInputDocument;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link GuestRatingValueResolver}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GuestRatingValueResolverTest
{
	@InjectMocks
	GuestRatingValueResolver guestRatingValueResolver;

	@Mock
	private DefaultSolrInputDocument defaultSolrInputDocument;

	@Mock
	private IndexedProperty indexedProperty;

	@Test
	public void testAddFieldValues() throws FieldValueProviderException
	{
		final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel()
		{
			@Override
			public Double getAverageUserRating()
			{
				return 9d;
			}
		};
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel();
		marketingRatePlanInfoModel.setAccommodationOffering(accommodationOfferingModel);
		guestRatingValueResolver.addFieldValues(defaultSolrInputDocument, null, indexedProperty, marketingRatePlanInfoModel, null);
		Mockito.verify(defaultSolrInputDocument, Mockito.times(1)).addField(indexedProperty, 9d);
	}

	@Test
	public void testAddFieldValuesForNullModel() throws FieldValueProviderException
	{
		guestRatingValueResolver.addFieldValues(null, null, indexedProperty, null, null);
		Mockito.verify(defaultSolrInputDocument, Mockito.times(0)).addField(Matchers.any(IndexedProperty.class),
				Matchers.any(Double.class));
	}

	@Test
	public void testAddFieldValuesForNullAccommodationOffering() throws FieldValueProviderException
	{
		guestRatingValueResolver.addFieldValues(null, null, null, new MarketingRatePlanInfoModel(), null);
		Mockito.verify(defaultSolrInputDocument, Mockito.times(0)).addField(Matchers.any(IndexedProperty.class),
				Matchers.any(Double.class));
	}

	@Test
	public void testAddFieldValuesForNullAverageRatingOfAccommodationOffering() throws FieldValueProviderException
	{
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel();
		marketingRatePlanInfoModel.setAccommodationOffering(new AccommodationOfferingModel());
		guestRatingValueResolver.addFieldValues(defaultSolrInputDocument, null, indexedProperty, marketingRatePlanInfoModel, null);
		Mockito.verify(defaultSolrInputDocument, Mockito.times(1)).addField(indexedProperty, 0d);

	}


}
