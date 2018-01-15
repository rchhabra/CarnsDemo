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
import de.hybris.platform.travelfacades.facades.accommodation.search.strategies.UpdateSearchCriterionStrategy;
import de.hybris.platform.travelservices.search.facetdata.AccommodationOfferingSearchPageData;

import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

/**
 * This strategy will update the {@link CriterionData} with the facets that have been applied on a during an accommodation search.
 */
public class UpdateFilteredFacetsStrategy implements UpdateSearchCriterionStrategy
{
	@Override
	public void applyStrategy(final CriterionData criterion, final AccommodationOfferingSearchPageData searchPageData)
	{

		if (Objects.isNull(criterion.getFilteredFacets()))
		{
			criterion.setFilteredFacets(new ArrayList<>());
		}

		if (CollectionUtils.isNotEmpty(searchPageData.getFilteredFacets())
				&& CollectionUtils.isEmpty(criterion.getFilteredFacets()))
		{
			criterion.getFilteredFacets().addAll(searchPageData.getFilteredFacets());
		}
	}
}
