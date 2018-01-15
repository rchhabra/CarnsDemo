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

package de.hybris.platform.travelfacades.facades.accommodation.search.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.manager.AccommodationOfferingSearchPipelineManager;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.AccommodationOfferingSearchResponseSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.UpdateSearchCriterionStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl.PriceAscendingSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl.PriceDescendingSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl.PricedPropertiesFirstSortStrategy;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for the implementation of {@link DefaultAccommodationSearchFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccommodationSearchFacadeTest
{
	@InjectMocks
	DefaultAccommodationSearchFacade defaultAccommodationSearchFacade;

	@Mock
	private AccommodationOfferingFacade accommodationOfferingFacade;
	@Mock
	private AccommodationOfferingSearchPipelineManager accommodationOfferingSearchPipelineManager;
	private Map<String, AccommodationOfferingSearchResponseSortStrategy> sortStrategyMap;

	@Mock
	AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> accommodationSearchPageData1;
	@Mock
	AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> accommodationSearchPageData2;
	@Mock
	UpdateSearchCriterionStrategy strategy;
	private final String TEST_CURRENCY_ISO_CODE = "TEST_CURRENCY_ISO_CODE";

	@Mock
	PriceAscendingSortStrategy priceAscendingSortStrategy;
	@Mock
	PriceDescendingSortStrategy priceDescendingSortStrategy;
	@Mock
	PricedPropertiesFirstSortStrategy pricedPropertiesFirstSortStrategy;

	@Before
	public void setUp()
	{
		defaultAccommodationSearchFacade.setUpdateSearchCriterionStrategies(Arrays.asList(strategy));
		sortStrategyMap = new HashMap<>();
		sortStrategyMap.put("price-asc", priceAscendingSortStrategy);
		sortStrategyMap.put("price-desc", priceDescendingSortStrategy);
		sortStrategyMap.put("DEFAULT", pricedPropertiesFirstSortStrategy);
		defaultAccommodationSearchFacade.setSortStrategyMap(sortStrategyMap);
		Mockito.doNothing().when(pricedPropertiesFirstSortStrategy).sort(Matchers.any());
	}

	@Test
	public void testDoSearchForNullCriterion()
	{
		Assert.assertNull(defaultAccommodationSearchFacade.doSearch(new AccommodationSearchRequestData()));
	}

	@Test
	public void testDoSearch()
	{
		final RoomStayCandidateData roomStayCandidateData1 = createRoomStayCandidateData();
		final RoomStayCandidateData roomStayCandidateData2 = createRoomStayCandidateData();
		final RoomStayCandidateData roomStayCandidateData3 = createRoomStayCandidateData();

		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		roomStayCandidates.add(roomStayCandidateData1);
		roomStayCandidates.add(roomStayCandidateData2);
		roomStayCandidates.add(roomStayCandidateData3);
		final CriterionData criterion = new CriterionData();
		criterion.setRoomStayCandidates(roomStayCandidates);

		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRateDatas = new ArrayList<>();
		accommodationOfferingDayRateDatas.add(createAccommodationOfferingDayRateData(true));
		accommodationOfferingDayRateDatas.add(createAccommodationOfferingDayRateData(false));

		final AccommodationSearchRequestData accommodationSearchRequestData = new AccommodationSearchRequestData();
		accommodationSearchRequestData.setCriterion(criterion);

		given(accommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationSearchRequestData,
				roomStayCandidateData1)).willReturn(null);
		given(accommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationSearchRequestData,
				roomStayCandidateData2)).willReturn(accommodationSearchPageData1);
		given(accommodationOfferingFacade.searchAccommodationOfferingDayRates(accommodationSearchRequestData,
				roomStayCandidateData3)).willReturn(accommodationSearchPageData2);
		given(accommodationSearchPageData2.getResults()).willReturn(accommodationOfferingDayRateDatas);

		given(accommodationSearchPageData1.getResults()).willReturn(Collections.emptyList());

		Mockito.doNothing().when(strategy).applyStrategy(Matchers.any(CriterionData.class),
				Matchers.any(AccommodationOfferingSearchPageData.class));
		final AccommodationSearchResponseData accommodationSearchResponseData = new AccommodationSearchResponseData();
		accommodationSearchResponseData.setCriterion(criterion);
		given(accommodationOfferingSearchPipelineManager.executePipeline(Matchers.any(), Matchers.any()))
				.willReturn(accommodationSearchResponseData);
		Assert.assertNotNull(defaultAccommodationSearchFacade.doSearch(accommodationSearchRequestData));

	}

	public AccommodationOfferingDayRateData createAccommodationOfferingDayRateData(final boolean hasRatePrice)
	{
		final AccommodationOfferingDayRateData accommodationOfferingDayRateData = new AccommodationOfferingDayRateData();
		if (hasRatePrice)
		{
			accommodationOfferingDayRateData.setPrice(createPriceData(100d));
		}
		return accommodationOfferingDayRateData;
	}

	public RoomStayCandidateData createRoomStayCandidateData()
	{
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		return roomStayCandidateData;
	}

	private PriceData createPriceData(final double value)
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(value));
		priceData.setFormattedValue("" + value);
		priceData.setCurrencyIso(TEST_CURRENCY_ISO_CODE);
		return priceData;
	}
}
