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

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingGalleryService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Resolves the value of default image url for given AccommodationOffering of the MarketingRateInfoPlan
 */
public class OfferingImageUrlValueResolver extends AbstractValueResolver<MarketingRatePlanInfoModel, Object, Object>
{
	private static final String OPTIONAL_PARAM = "optional";
	private static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	private AccommodationOfferingGalleryService accommodationOfferingGalleryService;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final MarketingRatePlanInfoModel marketingRatePlanInfoModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		final String galleryCode = marketingRatePlanInfoModel.getAccommodationOffering().getGalleryCode();
		if (StringUtils.isNotEmpty(galleryCode))
		{
			final AccommodationOfferingGalleryModel gallery = getAccommodationOfferingGalleryService()
					.getAccommodationOfferingGallery(galleryCode, marketingRatePlanInfoModel.getCatalogVersion());
			if (gallery != null && gallery.getListImage() != null)
			{
				document.addField(indexedProperty, gallery.getListImage().getURL(), resolverContext.getFieldQualifier());
				return;
			}
		}

		final boolean isOptional = ValueProviderParameterUtils
				.getBoolean(indexedProperty, OPTIONAL_PARAM, OPTIONAL_PARAM_DEFAULT_VALUE);
		if (!isOptional)
		{
			throw new FieldValueProviderException("No value resolved for indexed property " + indexedProperty.getName());
		}

	}

	/**
	 * Gets accommodation offering gallery service.
	 *
	 * @return the accommodation offering gallery service
	 */
	protected AccommodationOfferingGalleryService getAccommodationOfferingGalleryService()
	{
		return accommodationOfferingGalleryService;
	}

	/**
	 * Sets accommodation offering gallery service.
	 *
	 * @param accommodationOfferingGalleryService
	 * 		the accommodation offering gallery service
	 */
	@Required
	public void setAccommodationOfferingGalleryService(
			final AccommodationOfferingGalleryService accommodationOfferingGalleryService)
	{
		this.accommodationOfferingGalleryService = accommodationOfferingGalleryService;
	}
}
