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

package de.hybris.platform.solrfacetsearch.config.factories.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultTravelFlexibleSearchQuerySpecFactory}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelFlexibleSearchQuerySpecFactoryTest
{
	@InjectMocks
	DefaultTravelFlexibleSearchQuerySpecFactory travelFlexibleSearchQuerySpecFactory;

	@Test
	public void testPopulateRuntimeParameters() throws SolrServiceException
	{
		final IndexedTypeFlexibleSearchQuery indexTypeFlexibleSearchQueryData = new IndexedTypeFlexibleSearchQuery();
		indexTypeFlexibleSearchQueryData.setActive(Boolean.TRUE);
		indexTypeFlexibleSearchQueryData.setParameters(new HashMap<>());
		final IndexedType indexedType = new IndexedType();
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		travelFlexibleSearchQuerySpecFactory.populateRuntimeParameters(indexTypeFlexibleSearchQueryData, indexedType,
				facetSearchConfig);
	}
}
