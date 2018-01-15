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

package de.hybris.platform.solrfacetsearch.search.context.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchListener;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.TransportOfferingStatus;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The class is responsible for adding a Filter Query in SearchQuery to filter out TransportOfferings based on their
 * status
 */
public class TransportOfferingStatusFacetSearchListener implements FacetSearchListener
{

	private List<TransportOfferingStatus> notAllowedStatuses;

	/**
	 * Method to add filterQuery in SearchQuery to filter TransportOffering from solr search based on its status
	 *
	 * @param facetSearchContext
	 *           list of {@link FacetSearchContext}
	 *
	 */
	@Override
	public void beforeSearch(final FacetSearchContext facetSearchContext) throws FacetSearchException
	{
		if (!StringUtils.equalsIgnoreCase(TransportOfferingModel._TYPECODE, facetSearchContext.getIndexedType().getCode()))
		{
			return;
		}

		final Set<String> statuses = getNotAllowedStatuses().stream().map(TransportOfferingStatus::getCode)
				.collect(Collectors.toSet());
		final IndexedProperty indexedProperty = facetSearchContext.getIndexedType().getIndexedProperties()
				.get(TravelservicesConstants.SEARCH_KEY_STATUS);
		if (Objects.isNull(indexedProperty))
		{
			return;
		}

		facetSearchContext.getSearchQuery().addFilterQuery(new QueryField(TravelservicesConstants.SOLR_FQ_NOT_OPERATOR
				+ TravelservicesConstants.SEARCH_KEY_STATUS + "_" + indexedProperty.getType(), SearchQuery.Operator.OR, statuses));
	}

	@Override
	public void afterSearch(final FacetSearchContext facetSearchContext) throws FacetSearchException
	{
		// empty method carried from interface but not used
	}

	@Override
	public void afterSearchError(final FacetSearchContext facetSearchContext) throws FacetSearchException
	{
		// empty method carried from interface but not used
	}

	/**
	 * Gets not allowed statuses.
	 *
	 * @return the not allowed statuses
	 */
	protected List<TransportOfferingStatus> getNotAllowedStatuses()
	{
		return this.notAllowedStatuses;
	}

	/**
	 * Sets not allowed statuses.
	 *
	 * @param notAllowedStatuses
	 *           the not allowed statuses
	 */
	@Required
	public void setNotAllowedStatuses(final List<TransportOfferingStatus> notAllowedStatuses)
	{
		this.notAllowedStatuses = notAllowedStatuses;
	}

}
