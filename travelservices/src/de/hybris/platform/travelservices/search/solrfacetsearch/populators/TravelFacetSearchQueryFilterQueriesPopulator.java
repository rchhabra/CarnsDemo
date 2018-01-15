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

package de.hybris.platform.travelservices.search.solrfacetsearch.populators;

import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQueryFilterQueriesPopulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;


/**
 * This class is an extension of {@link FacetSearchQueryFilterQueriesPopulator} where a method has been overridden to avoid
 * unnecessary character encoding of solr operator characters ('-')
 */
public class TravelFacetSearchQueryFilterQueriesPopulator extends FacetSearchQueryFilterQueriesPopulator
{
	@Override
	protected String convertQueryField(SearchQuery searchQuery, QueryField queryField)
	{
		String convertedField = getFieldNameTranslator()
				.translate(searchQuery, queryField.getField(), FieldNameProvider.FieldType.INDEX);
		StringBuilder query = new StringBuilder();
		query.append(convertedField);
		query.append(':');
		String value;
		String separator;
		if (queryField.getValues().size() == 1)
		{
			value = queryField.getValues().iterator().next();
			value = ClientUtils.escapeQueryChars(value);
			separator = getFacetSearchQueryOperatorTranslator().translate(value, queryField.getQueryOperator());
			query.append(separator);
		}
		else
		{
			List<String> convertedValues = new ArrayList<>(queryField.getValues().size());

			Iterator queryFieldValueIterator = queryField.getValues().iterator();

			String convertedValue;
			while (queryFieldValueIterator.hasNext())
			{
				value = (String) queryFieldValueIterator.next();
				convertedValue = ClientUtils.escapeQueryChars(value);
				convertedValue = getFacetSearchQueryOperatorTranslator().translate(convertedValue, queryField.getQueryOperator());
				convertedValues.add(convertedValue);
			}

			SearchQuery.Operator operator = resolveQueryFieldOperator(searchQuery, queryField);
			separator = " " + operator + " ";
			convertedValue = StringUtils.join(convertedValues, separator);
			query.append('(');
			query.append(convertedValue);
			query.append(')');
		}

		return query.toString();
	}

}
