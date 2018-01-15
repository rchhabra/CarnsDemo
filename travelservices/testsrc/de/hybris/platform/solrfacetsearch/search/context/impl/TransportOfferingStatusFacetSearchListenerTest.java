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

package de.hybris.platform.solrfacetsearch.search.context.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TransportOfferingStatusFacetSearchListener}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingStatusFacetSearchListenerTest
{
	@InjectMocks
	TransportOfferingStatusFacetSearchListener transportOfferingStatusFacetSearchListener;

	@Mock
	IndexedType indexedType;

	@Mock
	SearchQuery searchQuery;

	@Test
	public void testBeforeSearch() throws FacetSearchException
	{
		final List<TransportOfferingStatus> notAllowedStatuses = Arrays.asList(TransportOfferingStatus.CANCELLED,
				TransportOfferingStatus.BOARDED, TransportOfferingStatus.DEPARTED);
		transportOfferingStatusFacetSearchListener.setNotAllowedStatuses(notAllowedStatuses);


		final IndexedProperty indexedProperty = Mockito.mock(IndexedProperty.class);
		final Map<String, IndexedProperty> indexedProperties = new HashMap<>();
		indexedProperties.put(TravelservicesConstants.SEARCH_KEY_STATUS, indexedProperty);
		Mockito.when(indexedProperty.getType()).thenReturn("TEST_CODE");

		final FacetSearchContext facetSearchContext = Mockito.mock(FacetSearchContext.class);
		Mockito.when(facetSearchContext.getSearchQuery()).thenReturn(searchQuery);
		Mockito.when(facetSearchContext.getIndexedType()).thenReturn(indexedType);
		Mockito.when(indexedType.getIndexedProperties()).thenReturn(indexedProperties);
		Mockito.when(indexedType.getCode()).thenReturn(TransportOfferingModel._TYPECODE);
		transportOfferingStatusFacetSearchListener.beforeSearch(facetSearchContext);
	}

	@Test
	public void testBeforeSearchForTestTypeCode() throws FacetSearchException
	{
		final FacetSearchContext facetSearchContext = Mockito.mock(FacetSearchContext.class);
		Mockito.when(facetSearchContext.getIndexedType()).thenReturn(indexedType);
		Mockito.when(indexedType.getCode()).thenReturn("TEST_CODE");
		transportOfferingStatusFacetSearchListener.beforeSearch(facetSearchContext);
	}

	@Test
	public void testAfterSearch() throws FacetSearchException
	{
		final FacetSearchContext facetSearchContext = Mockito.mock(FacetSearchContext.class);
		transportOfferingStatusFacetSearchListener.afterSearch(facetSearchContext);
	}

	@Test
	public void testAfterSearchError() throws FacetSearchException
	{
		final FacetSearchContext facetSearchContext = Mockito.mock(FacetSearchContext.class);
		transportOfferingStatusFacetSearchListener.afterSearchError(facetSearchContext);
	}

}
