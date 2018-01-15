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

package de.hybris.platform.solrfacetsearch.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultTravelFlexibleSearchQueryPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelFlexibleSearchQueryPopulatorTest
{
	@InjectMocks
	DefaultTravelFlexibleSearchQueryPopulator travelFlexibleSearchQueryPopulator;

	@Test
	public void testPopulate()
	{
		final SolrIndexerQueryModel source = new SolrIndexerQueryModel();
		source.setActive(Boolean.FALSE);
		final IndexedTypeFlexibleSearchQuery target = new IndexedTypeFlexibleSearchQuery();
		travelFlexibleSearchQueryPopulator.populate(source, target);
	}
}
