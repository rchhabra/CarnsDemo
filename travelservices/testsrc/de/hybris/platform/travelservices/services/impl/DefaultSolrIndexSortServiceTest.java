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
import de.hybris.platform.travelservices.dao.SolrIndexSortDao;
import de.hybris.platform.travelservices.model.search.SolrIndexedTypeDefaultSortOrderMappingModel;

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
 * Unit Test for the DefaultSolrIndexSortService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSolrIndexSortServiceTest
{
	@Mock
	private SolrIndexSortDao solrIndexSortDao;

	@InjectMocks
	private DefaultSolrIndexSortService solrIndexSortService;

	@Test
	public void getDefaultSortOrderMappingTest()
	{
		final SolrIndexedTypeDefaultSortOrderMappingModel solrIndexedTypeDefaultSortOrderMappingModel = new SolrIndexedTypeDefaultSortOrderMappingModel();
		Mockito.when(solrIndexSortDao.findIndexOrderSortMapping(Matchers.anyString()))
				.thenReturn(solrIndexedTypeDefaultSortOrderMappingModel);

		final SolrIndexedTypeDefaultSortOrderMappingModel result = solrIndexSortService.getDefaultSortOrderMapping("to");
		Assert.assertNotNull(result);
	}
}
