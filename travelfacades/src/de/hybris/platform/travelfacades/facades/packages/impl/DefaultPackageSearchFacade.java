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

package de.hybris.platform.travelfacades.facades.packages.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.PackageData;
import de.hybris.platform.commercefacades.packages.request.PackageSearchRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageSearchResponseData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.travelfacades.facades.accommodation.search.impl.DefaultAccommodationSearchFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.UpdateSearchCriterionStrategy;
import de.hybris.platform.travelfacades.facades.packages.PackageSearchFacade;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageSearchResponsePipelineManager;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PackageSearchFacade}
 */
public class DefaultPackageSearchFacade extends DefaultAccommodationSearchFacade implements PackageSearchFacade
{
	private PackageSearchResponsePipelineManager packageSearchResponsePipelineManager;
	private PackageSearchResponsePipelineManager packageSearchResponsePriceRangeFilterPipelineManager;
	private Comparator<PropertyData> totalPackagePriceAscComparator;

	@Override
	public PackageSearchResponseData doSearch(final PackageSearchRequestData packageSearchRequestData)
	{
		if (packageSearchRequestData.getCriterion() == null)
		{
			return null;
		}

		final List<AccommodationOfferingDayRateData> accommodationOfferingAggregatedDayRates = new ArrayList<AccommodationOfferingDayRateData>();
		final List<RoomStayCandidateData> roomStayCandidates = packageSearchRequestData.getCriterion().getRoomStayCandidates();
		final CriterionData searchPageCriterionData = createSearchPageCriterionData();

		for (final RoomStayCandidateData roomStayCandidateData : roomStayCandidates)
		{
			final AccommodationOfferingSearchPageData<SearchStateData, AccommodationOfferingDayRateData> accommodationSearchPageData = getAccommodationOfferingFacade()
					.searchAccommodationOfferingDayRates(packageSearchRequestData, roomStayCandidateData);

			if (accommodationSearchPageData != null)
			{
				for (final UpdateSearchCriterionStrategy strategy : getUpdateSearchCriterionStrategies())
				{
					strategy.applyStrategy(searchPageCriterionData, accommodationSearchPageData);
				}

				final List<AccommodationOfferingDayRateData> accommodationOfferingDayRates = accommodationSearchPageData.getResults();
				if (CollectionUtils.isNotEmpty(accommodationOfferingDayRates))
				{
					accommodationOfferingDayRates.removeIf(rate -> Objects.isNull(rate.getPrice()));

					accommodationOfferingDayRates.forEach(accommodationOfferingDayRateData -> accommodationOfferingDayRateData
							.setRoomStayCandidateRefNumber(roomStayCandidates.indexOf(roomStayCandidateData)));

					accommodationOfferingAggregatedDayRates.addAll(accommodationOfferingDayRates);
				}
			}
		}

		final PackageSearchResponseData packageSearchResponseData = getPackageSearchResponsePipelineManager()
				.executePipeline(accommodationOfferingAggregatedDayRates, packageSearchRequestData);

		if (packageSearchResponseData != null)
		{
			// Updating the response with search related params
			updateSearchResponse(packageSearchResponseData, searchPageCriterionData);

			// Calling the sorting strategies
			sortProperties(packageSearchRequestData, packageSearchResponseData);
		}

		return packageSearchResponseData;
	}

	@Override
	public PackageData getMinPricedPackage(final List<PropertyData> packages)
	{
		if (CollectionUtils.isEmpty(packages))
		{
			return null;
		}

		final Optional<PropertyData> optMinPricedPackage = packages.stream().min(getTotalPackagePriceAscComparator());

		if (optMinPricedPackage.isPresent())
		{
			final PropertyData minPricedProperty = optMinPricedPackage.get();
			if (minPricedProperty instanceof PackageData)
			{
				final PackageData minPricedPackage = (PackageData) minPricedProperty;
				return minPricedPackage;
			}
		}
		return null;
	}

	@Override
	public PackageData getMaxPricedPackage(final List<PropertyData> packages)
	{
		if (CollectionUtils.isEmpty(packages))
		{
			return null;
		}

		final Optional<PropertyData> optMaxPricedPackage = packages.stream().max(getTotalPackagePriceAscComparator());

		if (optMaxPricedPackage.isPresent())
		{
			final PropertyData maxPricedProperty = optMaxPricedPackage.get();
			if (maxPricedProperty instanceof PackageData)
			{
				final PackageData maxPricedPackage = (PackageData) maxPricedProperty;
				return maxPricedPackage;
			}
		}
		return null;
	}

	@Override
	public PackageSearchResponseData getFilteredPackageResponseFilteredByPriceRange(
			final PackageSearchRequestData packageSearchRequestData)
	{
		return getPackageSearchResponsePriceRangeFilterPipelineManager().executePipeline(Collections.emptyList(),
				packageSearchRequestData);
	}

	/**
	 * @return the packageSearchResponsePipelineManager
	 */
	protected PackageSearchResponsePipelineManager getPackageSearchResponsePipelineManager()
	{
		return packageSearchResponsePipelineManager;
	}

	/**
	 * @param packageSearchResponsePipelineManager
	 *           the packageSearchResponsePipelineManager to set
	 */
	@Required
	public void setPackageSearchResponsePipelineManager(
			final PackageSearchResponsePipelineManager packageSearchResponsePipelineManager)
	{
		this.packageSearchResponsePipelineManager = packageSearchResponsePipelineManager;
	}

	/**
	 * @return the packageSearchResponsePriceRangeFilterPipelineManager
	 */
	protected PackageSearchResponsePipelineManager getPackageSearchResponsePriceRangeFilterPipelineManager()
	{
		return packageSearchResponsePriceRangeFilterPipelineManager;
	}

	/**
	 * @param packageSearchResponsePriceRangeFilterPipelineManager
	 *           the packageSearchResponsePriceRangeFilterPipelineManager to set
	 */
	@Required
	public void setPackageSearchResponsePriceRangeFilterPipelineManager(
			final PackageSearchResponsePipelineManager packageSearchResponsePriceRangeFilterPipelineManager)
	{
		this.packageSearchResponsePriceRangeFilterPipelineManager = packageSearchResponsePriceRangeFilterPipelineManager;
	}

	/**
	 * @return the totalPackagePriceAscComparator
	 */
	protected Comparator<PropertyData> getTotalPackagePriceAscComparator()
	{
		return totalPackagePriceAscComparator;
	}

	/**
	 * @param totalPackagePriceAscComparator
	 *           the totalPackagePriceAscComparator to set
	 */
	@Required
	public void setTotalPackagePriceAscComparator(final Comparator<PropertyData> totalPackagePriceAscComparator)
	{
		this.totalPackagePriceAscComparator = totalPackagePriceAscComparator;
	}
}
