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

package de.hybris.platform.travelfacades.search.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.impex.jalo.ErrorHandler.RESULT;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.functions.T;
import org.jgroups.protocols.pbcast.STATE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TransportOfferingSearchPagePopulatorTest
{
	@Mock
	Converter<RESULT, ITEM> searchResultTransportOfferingConverter;

	@InjectMocks
	private final TransportOfferingSearchPagePopulator populator = new TransportOfferingSearchPagePopulator();

	@Test
	public void testPopulateNullResults()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PaginationData paginationData = testDataSetUp.createPaginationData();
		final SortData sortData = testDataSetUp.createSortData();
		final TransportOfferingSearchPageData<T, RESULT> source = testDataSetUp.createTransportOfferingSearchPageData(
				paginationData, Stream.of(sortData).collect(Collectors.toList()), "someurl", null);
		final TransportOfferingSearchPageData<STATE, T> target = new TransportOfferingSearchPageData<STATE, T>();
		populator.populate(source, target);
	}

	@Test
	public void testPopulateWithResults()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final PaginationData paginationData = testDataSetUp.createPaginationData();
		final SortData sortData = testDataSetUp.createSortData();
		final List<RESULT> results = testDataSetUp.createResults();
		final TransportOfferingSearchPageData<T, RESULT> source = testDataSetUp.createTransportOfferingSearchPageData(
				paginationData, Stream.of(sortData).collect(Collectors.toList()), "someurl", results);
		final TransportOfferingSearchPageData<STATE, T> target = new TransportOfferingSearchPageData<STATE, T>();
		given(Converters.convertAll(source.getResults(), searchResultTransportOfferingConverter)).willReturn(new ArrayList<ITEM>());
		populator.populate(source, target);
	}

	private class TestDataSetUp
	{
		private TransportOfferingSearchPageData<T, RESULT> createTransportOfferingSearchPageData(
				final PaginationData paginationData, final List<SortData> sorts, final String redirectUrl, final List<RESULT> results)
		{
			final TransportOfferingSearchPageData<T, RESULT> source = new TransportOfferingSearchPageData<T, RESULT>();
			source.setPagination(paginationData);
			source.setSorts(sorts);
			source.setKeywordRedirectUrl(redirectUrl);
			source.setResults(results);
			return source;
		}

		private PaginationData createPaginationData()
		{
			final PaginationData paginationData = new PaginationData();
			paginationData.setCurrentPage(1);
			return paginationData;
		}

		private SortData createSortData()
		{
			final SortData sortData = new SortData();
			return sortData;
		}

		private List<RESULT> createResults()
		{
			return Arrays.asList(RESULT.IGNORE);
		}


	}

	private class ITEM extends TransportOfferingData
	{

	}

}
