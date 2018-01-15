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
 *
 */

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;


public class AbstractPriceValueResolver
		extends AbstractDateBasedValueResolver<MarketingRatePlanInfoModel, Object, Map<String, Double>>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	private PriceService priceService;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel model,
			final ValueResolverContext<Object, Map<String, Double>> resolverContext, final Date documentDate)
			throws FieldValueProviderException
	{
		final Map<String, Double> priceInformationMap = resolverContext.getQualifierData();
		if (MapUtils.isNotEmpty(priceInformationMap))
		{
			Double priceValue = 0d;
			for (final Entry<String, Double> ratePlanEntry : priceInformationMap.entrySet())
			{
				priceValue += ratePlanEntry.getValue();
			}

			document.addField(indexedProperty, priceValue, resolverContext.getFieldQualifier());
			return;
		}


		final boolean isOptional = ValueProviderParameterUtils.getBoolean(indexedProperty, OPTIONAL_PARAM,
				OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}

	}

	/**
	 * Load price informations list.
	 *
	 * @param indexedProperties
	 *           the indexed properties
	 * @param roomRateProduct
	 *           the room rate product
	 * @return the list
	 */
	protected List<PriceInformation> loadPriceInformations(final Collection<IndexedProperty> indexedProperties,
			final RoomRateProductModel roomRateProduct)
	{
		return getPriceService().getPriceInformationsForProduct(roomRateProduct);
	}

	/**
	 * Gets price value.
	 *
	 * @param priceInformations
	 *           the price informations
	 * @return the price value
	 */
	protected Double getPriceValue(final List<PriceInformation> priceInformations)
	{
		Double value = null;

		if (CollectionUtils.isNotEmpty(priceInformations))
		{
			value = priceInformations.get(0).getPriceValue().getValue();
		}

		return value;
	}

	/**
	 * Gets taxes.
	 *
	 * @param taxes
	 * 		the taxes
	 * @param roomRateBasePrice
	 * 		the room rate base price
	 *
	 * @return the taxes
	 */
	protected Double getTaxes(final Collection<TaxRowModel> taxes, final Double roomRateBasePrice)
	{
		final Double totalTaxValue = 0.0d;

		if (org.apache.commons.collections4.CollectionUtils.isEmpty(taxes))
		{
			return totalTaxValue;
		}
		return Double.sum(totalTaxValue,
				taxes.stream().map(tax -> (roomRateBasePrice / 100 * tax.getValue())).reduce(0.0d, Double::sum));
	}

	/**
	 * Gets price service.
	 *
	 * @return the price service
	 */
	protected PriceService getPriceService()
	{
		return priceService;
	}

	/**
	 * Sets price service.
	 *
	 * @param priceService
	 *           the price service
	 */
	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

}
