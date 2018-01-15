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
 */

package de.hybris.platform.travelservices.search.solrfacetsearch.impl;

import de.hybris.platform.solrfacetsearch.search.FreeTextWildcardQueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.DisMaxFreeTextQueryBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Extension of {@link DisMaxFreeTextQueryBuilder} to customise logic as needed for Accommodation search purposes.
 * For Accommodation search it is necessary to perform a wildcard query using a string that was passed but no matter what
 * tokenizer is used, prepareTerms() method will always split the text string into terms. Therefore it was decided to use
 * phraseQuery for wildcard query instead of terms.
 */
public class AccommodationDisMaxFreeTextQueryBuilder extends DisMaxFreeTextQueryBuilder
{
	private static final Logger LOG = Logger.getLogger(AccommodationDisMaxFreeTextQueryBuilder.class);

	@Override
	public String buildQuery(final SearchQuery searchQuery)
	{
		if (StringUtils.isBlank(searchQuery.getUserQuery()))
		{
			return "";
		}

		final Map<String, List<FieldParameter>> queryFields = new LinkedHashMap<>();

		final String tieParam = searchQuery.getFreeTextQueryBuilderParameters().get("tie");
		final float tie = StringUtils.isNotEmpty(tieParam) ? Float.valueOf(tieParam) : 0.0F;

		final String groupedByQueryTypeParam = searchQuery.getFreeTextQueryBuilderParameters().get("groupByQueryType");
		final boolean groupByQueryType = !StringUtils.isNotEmpty(groupedByQueryTypeParam) || Boolean.valueOf(groupedByQueryTypeParam);

		final List<QueryValue> terms = prepareTerms(searchQuery);
		final List<QueryValue> phraseQueries = preparePhraseQueries(searchQuery);

		addFreeTextQuery(searchQuery, terms, groupByQueryType, queryFields);
		addFreeTextFuzzyQuery(searchQuery, terms, groupByQueryType, queryFields);
		addFreeTextWildCardQuery(searchQuery, phraseQueries, groupByQueryType, queryFields);
		addFreeTextPhraseQuery(searchQuery, phraseQueries, groupByQueryType, queryFields);

		final String query = buildQuery(queryFields, tie, searchQuery);

		LOG.debug(query);

		return query;
	}

	@Override
	protected void addFreeTextWildCardQuery(SearchQuery searchQuery, List<QueryValue> terms, boolean groupByQueryType, Map<String, List<DisMaxFreeTextQueryBuilder.FieldParameter>> queryFields) {
		final List<FreeTextWildcardQueryField> fields = searchQuery.getFreeTextWildcardQueries();
		fields.forEach(field -> {
			String boostString = "";
			if (field.getBoost() != null) {
				boostString = "^" + field.getBoost();
			}

			for (QueryValue term : terms)
			{
				if (shouldIncludeTerm(term, field.getMinTermLength()))
				{
					String value = applyWildcardType(term.getValue(), field.getWildcardType());
					addQueryField(term.getValue(), FieldType.WILD_CARD_QUERY, field.getField(), value + boostString,
							groupByQueryType, queryFields);
				}
			}
		});
	}
}
