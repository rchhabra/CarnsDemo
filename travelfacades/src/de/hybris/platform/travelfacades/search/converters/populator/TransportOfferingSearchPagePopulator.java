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

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.search.facetdata.TransportOfferingSearchPageData;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Transport offering search page populator.
 *
 * @param <QUERY>
 * 		the type parameter
 * @param <STATE>
 * 		the type parameter
 * @param <RESULT>
 * 		the type parameter
 * @param <ITEM>
 * 		the type parameter
 */
public class TransportOfferingSearchPagePopulator<QUERY, STATE, RESULT, ITEM extends TransportOfferingData>
		implements Populator<TransportOfferingSearchPageData<QUERY, RESULT>, TransportOfferingSearchPageData<STATE, ITEM>>
{
	private Converter<RESULT, ITEM> searchResultTransportOfferingConverter;

	@Override
	public void populate(final TransportOfferingSearchPageData<QUERY, RESULT> source,
			final TransportOfferingSearchPageData<STATE, ITEM> target)
	{
		target.setPagination(source.getPagination());
		target.setSorts(source.getSorts());
		target.setKeywordRedirectUrl(source.getKeywordRedirectUrl());

		if (source.getResults() != null)
		{
			target.setResults(Converters.convertAll(source.getResults(), getSearchResultTransportOfferingConverter()));
		}
	}

	/**
	 * Gets search result transport offering converter.
	 *
	 * @return the search result transport offering converter
	 */
	protected Converter<RESULT, ITEM> getSearchResultTransportOfferingConverter()
	{
		return searchResultTransportOfferingConverter;
	}

	/**
	 * Sets search result transport offering converter.
	 *
	 * @param searchResultTransportOfferingConverter
	 * 		the search result transport offering converter
	 */
	@Required
	public void setSearchResultTransportOfferingConverter(final Converter<RESULT, ITEM> searchResultTransportOfferingConverter)
	{
		this.searchResultTransportOfferingConverter = searchResultTransportOfferingConverter;
	}
}
