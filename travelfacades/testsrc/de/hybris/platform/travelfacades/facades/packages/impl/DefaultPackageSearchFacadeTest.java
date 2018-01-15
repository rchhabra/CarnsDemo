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

package de.hybris.platform.travelfacades.facades.packages.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.manager.AccommodationOfferingSearchPipelineManager;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.AccommodationOfferingSearchResponseSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.UpdateSearchCriterionStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl.PriceAscendingSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl.PriceDescendingSortStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl.PricedPropertiesFirstSortStrategy;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageSearchResponsePipelineManager;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * unit test for {@link DefaultPackageSearchFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPackageSearchFacadeTest
{
	@InjectMocks
	DefaultPackageSearchFacade defaultPackageSearchFacade;


	@Mock
	private PackageSearchResponsePipelineManager packageSearchResponsePipelineManager;

	@Mock
	private PackageSearchResponsePipelineManager packageSearchResponsePriceRangeFilterPipelineManager;

	@Mock
	private Comparator<PropertyData> totalPackagePriceAscComparator;

	@Mock
	private AccommodationOfferingFacade accommodationOfferingFacade;

	@Mock
	private AccommodationOfferingSearchPipelineManager accommodationOfferingSearchPipelineManager;

	@Mock
	private UpdateSearchCriterionStrategy strategy;

	@Mock
	AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> accommodationSearchPageData1;

	@Mock
	AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> accommodationSearchPageData2;

	@Mock
	PriceAscendingSortStrategy priceAscendingSortStrategy;

	@Mock
	PriceDescendingSortStrategy priceDescendingSortStrategy;

	@Mock
	PricedPropertiesFirstSortStrategy pricedPropertiesFirstSortStrategy;

	private Map<String, AccommodationOfferingSearchResponseSortStrategy> sortStrategyMap;

	@Before
	public void setUp()
	{
		defaultPackageSearchFacade.setUpdateSearchCriterionStrategies(Collections.singletonList(strategy));
		Mockito.doNothing().when(strategy).applyStrategy(Matchers.any(CriterionData.class),
				Matchers.any(AccommodationOfferingSearchPageData.class));
		sortStrategyMap = new HashMap<>();
		sortStrategyMap.put("price-asc", priceAscendingSortStrategy);
		sortStrategyMap.put("price-desc", priceDescendingSortStrategy);
		sortStrategyMap.put("DEFAULT", pricedPropertiesFirstSortStrategy);
		defaultPackageSearchFacade.setSortStrategyMap(sortStrategyMap);
		Mockito.doNothing().when(pricedPropertiesFirstSortStrategy).sort(Matchers.any());
		Mockito.when(totalPackagePriceAscComparator.compare(Matchers.any(PropertyData.class), Matchers.any(PropertyData.class)))
				.thenReturn(0);

	}

	@Test
	public void testDoSearchForNullCriterian()
	{
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();

		Assert.assertNull(defaultPackageSearchFacade.doSearch(packageSearchRequestData));
	}

	@Test
	public void testDoSearchForNullPackageSearchResponseData()
	{
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();

		final TestDataSetUp testDataSetup = new TestDataSetUp();
		final RoomStayCandidateData roomStayCandidateData1 = testDataSetup.createRoomStayCandidateData(0);
		final RoomStayCandidateData roomStayCandidateData2 = testDataSetup.createRoomStayCandidateData(1);
		final RoomStayCandidateData roomStayCandidateData3 = testDataSetup.createRoomStayCandidateData(2);

		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRateDatas = new ArrayList<>();
		accommodationOfferingDayRateDatas.add(testDataSetup.createAccommodationOfferingDayRateData(true));
		accommodationOfferingDayRateDatas.add(testDataSetup.createAccommodationOfferingDayRateData(false));

		given(accommodationSearchPageData2.getResults()).willReturn(accommodationOfferingDayRateDatas);

		given(accommodationSearchPageData1.getResults()).willReturn(Collections.emptyList());

		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		roomStayCandidates.add(roomStayCandidateData1);
		roomStayCandidates.add(roomStayCandidateData2);
		roomStayCandidates.add(roomStayCandidateData3);

		Mockito.when(
				accommodationOfferingFacade.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData1))
				.thenReturn(null);
		Mockito.when(
				accommodationOfferingFacade.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData2))
				.thenReturn(accommodationSearchPageData1);
		Mockito.when(
				accommodationOfferingFacade.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData3))
				.thenReturn(accommodationSearchPageData2);

		Mockito.when(packageSearchResponsePipelineManager.executePipeline(Matchers.anyList(),
				Matchers.any(PackageSearchRequestData.class))).thenReturn(null);
		final CriterionData criterion = new CriterionData();
		criterion.setRoomStayCandidates(roomStayCandidates);
		packageSearchRequestData.setCriterion(criterion);
		Assert.assertNull(defaultPackageSearchFacade.doSearch(packageSearchRequestData));

	}

	@Test
	public void testDoSearch()
	{
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();

		final TestDataSetUp testDataSetup = new TestDataSetUp();
		final RoomStayCandidateData roomStayCandidateData1 = testDataSetup.createRoomStayCandidateData(0);
		final RoomStayCandidateData roomStayCandidateData2 = testDataSetup.createRoomStayCandidateData(1);
		final RoomStayCandidateData roomStayCandidateData3 = testDataSetup.createRoomStayCandidateData(2);

		final List<AccommodationOfferingDayRateData> accommodationOfferingDayRateDatas = new ArrayList<>();
		accommodationOfferingDayRateDatas.add(testDataSetup.createAccommodationOfferingDayRateData(true));
		accommodationOfferingDayRateDatas.add(testDataSetup.createAccommodationOfferingDayRateData(false));

		given(accommodationSearchPageData2.getResults()).willReturn(accommodationOfferingDayRateDatas);

		given(accommodationSearchPageData1.getResults()).willReturn(Collections.emptyList());

		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		roomStayCandidates.add(roomStayCandidateData1);
		roomStayCandidates.add(roomStayCandidateData2);
		roomStayCandidates.add(roomStayCandidateData3);

		Mockito.when(
				accommodationOfferingFacade.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData1))
				.thenReturn(null);
		Mockito.when(
				accommodationOfferingFacade.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData2))
				.thenReturn(accommodationSearchPageData1);
		Mockito.when(
				accommodationOfferingFacade.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData3))
				.thenReturn(accommodationSearchPageData2);

		final CriterionData criterion = new CriterionData();
		criterion.setRoomStayCandidates(roomStayCandidates);

		final PackageSearchResponseData packageSearchResponseData = new PackageSearchResponseData();
		packageSearchResponseData.setCriterion(criterion);

		Mockito.when(packageSearchResponsePipelineManager.executePipeline(Matchers.anyList(),
				Matchers.any(PackageSearchRequestData.class))).thenReturn(packageSearchResponseData);
		packageSearchRequestData.setCriterion(criterion);
		Assert.assertNotNull(defaultPackageSearchFacade.doSearch(packageSearchRequestData));

	}

	@Test
	public void testGetMinPricedPackage()
	{
		Assert.assertNull(defaultPackageSearchFacade.getMinPricedPackage(Collections.emptyList()));
		Assert.assertNull(defaultPackageSearchFacade.getMinPricedPackage(Collections.singletonList(new PropertyData())));
		Assert.assertNotNull(defaultPackageSearchFacade.getMinPricedPackage(Collections.singletonList(new PackageData())));
	}

	@Test
	public void testGetMaxPricedPackage()
	{
		Assert.assertNull(defaultPackageSearchFacade.getMaxPricedPackage(Collections.emptyList()));
		Assert.assertNull(defaultPackageSearchFacade.getMaxPricedPackage(Collections.singletonList(new PropertyData())));
		Assert.assertNotNull(defaultPackageSearchFacade.getMaxPricedPackage(Collections.singletonList(new PackageData())));
	}

	@Test
	public void testGetFilteredPackageResponseFilteredByPriceRange()
	{
		final PackageSearchRequestData packageSearchRequestData = new PackageSearchRequestData();
		final PackageSearchResponseData packageSearchResponseData=new PackageSearchResponseData();
		Mockito.when(packageSearchResponsePriceRangeFilterPipelineManager.executePipeline(Matchers.anyList(),
				Matchers.any(PackageSearchRequestData.class))).thenReturn(packageSearchResponseData);
		Assert.assertEquals(defaultPackageSearchFacade.getFilteredPackageResponseFilteredByPriceRange(packageSearchRequestData),
				packageSearchResponseData);
	}


	private class TestDataSetUp
	{

		private List<RoomStayCandidateData> createRoomStayCandidateDatas()
		{
			final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
			roomStayCandidates.add(createRoomStayCandidateData(0));
			return roomStayCandidates;
		}

		private RoomStayCandidateData createRoomStayCandidateData(final int roomStayCandidateRefNumber)
		{
			final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
			roomStayCandidateData.setRoomStayCandidateRefNumber(roomStayCandidateRefNumber);
			return roomStayCandidateData;
		}

		private AccommodationOfferingDayRateData createAccommodationOfferingDayRateData(final boolean hasRatePrice)
		{
			final AccommodationOfferingDayRateData accommodationOfferingDayRateData = new AccommodationOfferingDayRateData();
			if (hasRatePrice)
			{
				accommodationOfferingDayRateData.setPrice(createPriceData(100d));
			}
			return accommodationOfferingDayRateData;
		}

		private PriceData createPriceData(final double value)
		{
			final PriceData priceData = new PriceData();
			priceData.setValue(BigDecimal.valueOf(value));
			priceData.setFormattedValue("GBP" + value);
			priceData.setCurrencyIso("GBP");
			return priceData;
		}

	}
}
