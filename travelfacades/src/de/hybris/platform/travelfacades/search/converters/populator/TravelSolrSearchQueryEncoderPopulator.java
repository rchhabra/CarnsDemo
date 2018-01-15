/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.travelfacades.search.converters.populator;

import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchQueryEncoderPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Extension of {@link SolrSearchQueryEncoderPopulator} to prevent double encoding in travel implementation.
 */
public class TravelSolrSearchQueryEncoderPopulator extends SolrSearchQueryEncoderPopulator
{
	@Override
	public void populate(final SolrSearchQueryData source, final SearchQueryData target)
	{
		final StringBuilder builder = new StringBuilder();

		if (source != null)
		{
			if (StringUtils.isNotBlank(source.getFreeTextSearch()))
			{
				builder.append(source.getFreeTextSearch());
			}

			builder.append(':');

			if (StringUtils.isNotBlank(source.getSort()))
			{
				builder.append(source.getSort());
			}

			final List<SolrSearchQueryTermData> terms = source.getFilterTerms();
			if (CollectionUtils.isNotEmpty(terms))
			{
				for (final SolrSearchQueryTermData term : terms)
				{
					if (StringUtils.isNotBlank(term.getKey()) && StringUtils.isNotBlank(term.getValue()))
					{
						builder.append(':').append(term.getKey()).append(':').append(term.getValue());
					}
				}
			}
		}

		final String result = builder.toString();

		// Special case for empty query
		if (":".equals(result))
		{
			target.setValue("");
		}
		else
		{
			target.setValue(result);
		}
	}
}
