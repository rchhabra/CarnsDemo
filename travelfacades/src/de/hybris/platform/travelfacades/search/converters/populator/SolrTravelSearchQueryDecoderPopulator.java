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

import de.hybris.platform.commercefacades.travel.search.data.SearchData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;



/**
 * The class is responsible for populating the the solr search query data from the search data which is passed from the
 * facade layer.
 */
public class SolrTravelSearchQueryDecoderPopulator implements Populator<SearchData, SolrSearchQueryData>
{

	private static final String QUERY_SEPARATOR_REGEX = ":";
	private ConfigurationService configurationService;
	private static final String PROPERTY_DELIMITER = ",";

	@Override
	public void populate(final SearchData source, final SolrSearchQueryData target) throws ConversionException
	{
		final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();
		final Map<String, String> filterTerms = source.getFilterTerms();

		if (MapUtils.isNotEmpty(filterTerms))
		{
			filterTerms.entrySet().forEach(entry -> {

				final String[] filterValues = entry.getValue().split(TravelfacadesConstants.MULTI_VALUE_FILTER_TERM_SEPARATOR);
				for (final String filterValue : filterValues)
				{
					final SolrSearchQueryTermData termData = new SolrSearchQueryTermData();
					termData.setKey(entry.getKey());
					termData.setValue(filterValue);
					terms.add(termData);
				}
			});
		}

		target.setSearchType(source.getSearchType());
		target.setFilterTerms(terms);
		target.setFreeTextSearch(source.getFreeTextSearch());
		target.setSort(source.getSort());
		populateQuery(source.getQuery(), target);
	}

	/**
	 * Populate query.
	 *
	 * @param query
	 * 		the query
	 * @param target
	 * 		the target
	 */
/*
	 * Populates SolrSearchQueryData with details of facet search from query.
	 *
	 * @param query query consisting of information about facet search field and value.
	 *
	 * @param target object of SolrSearchQueryData
	 */
	protected void populateQuery(final String query, final SolrSearchQueryData target)
	{
		if (StringUtils.isEmpty(query))
		{
			return;
		}
		final List<SolrSearchQueryTermData> filterTerms = new ArrayList<>(1);
		final String[] facets = query.split(QUERY_SEPARATOR_REGEX);

		if (facets.length > 1 && StringUtils.isEmpty(target.getSort()))
		{
			target.setSort(facets[1]);
		}

		for (int idx = 2; idx < facets.length; idx+=2)
		{
			final String value = facets[idx];
			if (getRequiredFacetCodes().contains(value) || StringUtils.isEmpty(value))
			{
				continue;
			}

			final SolrSearchQueryTermData filterTerm = new SolrSearchQueryTermData();
			filterTerm.setKey(value);
			if ((idx+1) >= facets.length)
			{
				filterTerm.setValue(StringUtils.EMPTY);
			}
			else
			{
				filterTerm.setValue(facets[idx+1]);
			}

			filterTerms.add(filterTerm);
		}

		if (CollectionUtils.isNotEmpty(target.getFilterTerms()))
		{
			filterTerms.addAll(target.getFilterTerms());
		}

		target.setFilterTerms(filterTerms);
	}

	/**
	 * This method will return a list of Strings taken from the
	 * accommodation listing facet codes property, where the delimiter between the codes
	 * is a comma ","
	 *
	 * @return list of String
	 */
	protected List<String> getRequiredFacetCodes()
	{
		final String requiredFacetCodes = getConfigurationService().getConfiguration()
				.getString(TravelservicesConstants.ACCOMMODATION_LISTING_FACET_CODES);

		if (StringUtils.isEmpty(requiredFacetCodes) && !StringUtils.contains(requiredFacetCodes, PROPERTY_DELIMITER))
		{
			return Collections.emptyList();
		}

		return Arrays.asList(requiredFacetCodes.split(PROPERTY_DELIMITER));
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
