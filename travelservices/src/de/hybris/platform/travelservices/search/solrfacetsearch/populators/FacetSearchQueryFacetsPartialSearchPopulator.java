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

import de.hybris.platform.solrfacetsearch.config.WildcardType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FreeTextWildcardQueryField;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQueryFacetsPopulator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;


/**
 * The Facets populator class to support partial search in facets query.
 * 
 */
public class FacetSearchQueryFacetsPartialSearchPopulator extends FacetSearchQueryFacetsPopulator
{

	/**
	 * Method to add wild card option to the search query.
	 * 
	 * This method is very much similar to the super method with an additional call to addFacetTextWildCardQuery to add
	 * wild card operator.
	 */
	@Override
	protected String convertQueryField(final SearchQuery searchQuery, final QueryField queryField)
	{
		final String convertedField = this.getFieldNameTranslator().translate(searchQuery, queryField.getField(),
				FieldNameProvider.FieldType.INDEX);
		final StringBuilder query = new StringBuilder();
		query.append(convertedField);
		query.append(':');
		if (queryField.getValues().size() == 1)
		{
			final String value = queryField.getValues().iterator().next();
			final String escapedValue = ClientUtils.escapeQueryChars(value);
			String convertedValue = this.getFacetSearchQueryOperatorTranslator().translate(escapedValue,
					queryField.getQueryOperator());
			convertedValue = addFacetTextWildCardQuery(searchQuery, queryField.getField(), convertedValue);
			query.append(convertedValue);
		}
		else
		{
			final List<String> convertedValues = new ArrayList<String>(queryField.getValues().size());
			for (final String value : queryField.getValues())
			{
				final String escapedValue = ClientUtils.escapeQueryChars(value);
				String convertedValue = this.getFacetSearchQueryOperatorTranslator().translate(escapedValue,
						queryField.getQueryOperator());
				convertedValue = addFacetTextWildCardQuery(searchQuery, queryField.getField(), convertedValue);
				convertedValues.add(convertedValue);
			}
			final SearchQuery.Operator operator = this.resolveQueryFieldOperator(searchQuery, queryField);
			final String separator = " " + (operator) + " ";
			final String convertedValue = StringUtils.join(convertedValues, separator);
			query.append('(');
			query.append(convertedValue);
			query.append(')');
		}
		return query.toString();
	}

	protected String addFacetTextWildCardQuery(final SearchQuery searchQuery, final String fieldName, final String searchString)
	{
		final List<FreeTextWildcardQueryField> fields = searchQuery.getFreeTextWildcardQueries();
		for (final FreeTextWildcardQueryField field : fields)
		{
			if (fieldName.equalsIgnoreCase(field.getField()))
			{
				return this.applyWildcardType(searchString, field.getWildcardType());
			}
		}
		return searchString;
	}

	protected String applyWildcardType(final String text, final WildcardType wildcardType)
	{
		final String defaultValue = "*" + text + "*";
		if (wildcardType != null)
		{
			switch (wildcardType)
			{
				case PREFIX:
					return "*" + text;
				case POSTFIX:
					return String.valueOf(text) + "*";
				case PREFIX_AND_POSTFIX:
					return "*" + text + "*";
				default:
					// do nothing
					break;
			}
		}
		return defaultValue;
	}
}
