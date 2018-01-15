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

package de.hybris.platform.travelservices.solr.provider.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Resolves the indexed value of address to have it in one line format
 */
public class AddressValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	private Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap;
	private Converter<AddressModel, StringBuilder> defaultAddressFormatConverter;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final Iterator<PointOfServiceModel> posIterator = marketingRatePlanInfoModel.getAccommodationOffering().getLocation()
				.getPointOfService().iterator();
		if (posIterator.hasNext())
		{
			final AddressModel address = posIterator.next().getAddress();
			if (address != null)
			{
				final String formattedAddress = getFormattedAddress(address);
				if (StringUtils.isNotEmpty(formattedAddress))
				{
					document.addField(indexedProperty, formattedAddress, resolverContext.getFieldQualifier());
					return;
				}
			}
		}

		final boolean isOptional = ValueProviderParameterUtils
				.getBoolean(indexedProperty, OPTIONAL_PARAM, OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}

	}

	protected String getFormattedAddress(final AddressModel address)
	{
		final String isoCode = address.getCountry().getIsocode();

		final Converter<AddressModel, StringBuilder> addressFormatConverter = addressFormatConverterMap.containsKey(isoCode) ?
				addressFormatConverterMap.get(isoCode) :
				getDefaultAddressFormatConverter();

		return addressFormatConverter.convert(address).toString();
	}

	/**
	 * Gets address format converter map.
	 *
	 * @return the address format converter map
	 */
	protected Map<String, Converter<AddressModel, StringBuilder>> getAddressFormatConverterMap()
	{
		return addressFormatConverterMap;
	}

	/**
	 * Sets address format converter map.
	 *
	 * @param addressFormatConverterMap
	 * 		the address format converter map
	 */
	@Required
	public void setAddressFormatConverterMap(final Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap)
	{
		this.addressFormatConverterMap = addressFormatConverterMap;
	}

	/**
	 * Gets default address format converter.
	 *
	 * @return the default address format converter
	 */
	protected Converter<AddressModel, StringBuilder> getDefaultAddressFormatConverter()
	{
		return defaultAddressFormatConverter;
	}

	/**
	 * Sets default address format converter.
	 *
	 * @param defaultAddressFormatConverter
	 * 		the default address format converter
	 */
	@Required
	public void setDefaultAddressFormatConverter(final Converter<AddressModel, StringBuilder> defaultAddressFormatConverter)
	{
		this.defaultAddressFormatConverter = defaultAddressFormatConverter;
	}
}
