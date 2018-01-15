/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelfacades.facades.accommodation.search.strategies.impl;


import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.UpdateSearchCriterionStrategy;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.fest.util.Collections;


/**
 * This strategy will update the {@link CriterionData} with the facets during an accommodation search.
 */
public class UpdateFacetsStrategy implements UpdateSearchCriterionStrategy
{
	@Override
	public void applyStrategy(final CriterionData criterion, final AccommodationOfferingSearchPageData searchPageData)
	{
		if (Objects.isNull(criterion.getFacets()))
		{
			criterion.setFacets(new ArrayList<>());
		}

		populateFacets(criterion.getFacets(), searchPageData.getFacets());
	}

	/**
	 * Populates totalFacets with accommodationFacets. If facet already exist, it updates the facet values with the
	 * missing ones, otherwise it will add the facet.
	 *
	 * @param totalFacets
	 * 		the accommodationOfferingFacade to set
	 * @param accommodationFacets
	 * 		the accommodationOfferingFacade to set
	 */
	protected void populateFacets(final List<FacetData<SearchStateData>> totalFacets,
			final List<FacetData<SearchStateData>> accommodationFacets)
	{
		if (Collections.isEmpty(accommodationFacets))
		{
			return;
		}

		if (CollectionUtils.isEmpty(totalFacets))
		{
			totalFacets.addAll(accommodationFacets);
			return;
		}
		accommodationFacets.forEach(accommodationFacet ->
		{
			boolean isFacetFound = Boolean.FALSE;
			for (final FacetData<SearchStateData> totalFacet : totalFacets)
			{
				if (StringUtils.equals(totalFacet.getCode(), accommodationFacet.getCode()))
				{
					final List<FacetValueData<SearchStateData>> totalFacetValues = totalFacet.getValues();
					populateFacetValues(totalFacetValues, accommodationFacet.getValues());
					totalFacet.setValues(totalFacetValues);
					isFacetFound = Boolean.TRUE;
					break;
				}
			}
			if (!isFacetFound)
			{
				totalFacets.add(accommodationFacet);
			}
		});
	}

	/**
	 * Populates totalFacetValues with missing accommodationFacetValues.
	 *
	 * @param totalFacetValues
	 * 		the totalFacetValues to set
	 * @param accommodationFacetValues
	 * 		the accommodationFacetValues to set
	 */
	protected void populateFacetValues(final List<FacetValueData<SearchStateData>> totalFacetValues,
			final List<FacetValueData<SearchStateData>> accommodationFacetValues)
	{
		if (CollectionUtils.isEmpty(accommodationFacetValues))
		{
			return;
		}

		if (CollectionUtils.isEmpty(totalFacetValues))
		{
			totalFacetValues.addAll(accommodationFacetValues);
			return;
		}

		accommodationFacetValues.forEach(accommodationfacetValue ->
		{
			boolean isFacetValueFound = Boolean.FALSE;
			for (final FacetValueData<SearchStateData> totalFacetValue : totalFacetValues)
			{
				if (StringUtils.equals(totalFacetValue.getCode(), accommodationfacetValue.getCode()))
				{
					isFacetValueFound = Boolean.TRUE;
					break;
				}
			}
			if (!isFacetValueFound)
			{
				totalFacetValues.add(accommodationfacetValue);
			}
		});

	}
}
