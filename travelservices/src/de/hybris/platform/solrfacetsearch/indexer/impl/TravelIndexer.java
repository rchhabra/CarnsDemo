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

package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.TravelSolrDocumentFactory;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Exporter;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.utils.RatePlanUtils;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Required;


/**
 * Overrides the DefaultIndexer class to include travel specific date based indexing
 */
public class TravelIndexer extends DefaultIndexer
{
	private static final Logger LOG = Logger.getLogger(TravelIndexer.class);

	private TimeService timeService;
	private CatalogVersionService catalogVersionService;
	private ConfigurationService configurationService;
	private TravelSolrDocumentFactory travelSolrDocumentFactory;

	@Override
	public Collection<SolrInputDocument> indexItems(final Collection<ItemModel> items, final FacetSearchConfig facetSearchConfig,
			final IndexedType indexedType) throws IndexerException, InterruptedException
	{
		if (!StringUtils.equalsIgnoreCase(MarketingRatePlanInfoModel._TYPECODE, indexedType.getCode()))
		{
			return super.indexItems(items, facetSearchConfig, indexedType);
		}

		if (items == null)
		{
			return Collections.emptyList();
		}

		final IndexConfig indexConfig = facetSearchConfig.getIndexConfig();
		final SolrConfig solrConfig = facetSearchConfig.getSolrConfig();

		final Collection<SolrInputDocument> documents = new ArrayList(CollectionUtils.size(items));

		for (final ItemModel itemModel : items)
		{
			if (Thread.interrupted())
			{
				throw new InterruptedException();
			}

			// Get the MarketingRatePlanInfo
			final MarketingRatePlanInfoModel marketingRatePlanInfoModel = (MarketingRatePlanInfoModel) itemModel;

			final int configuredDaysToIndex = getConfigurationService().getConfiguration()
					.getInt(TravelservicesConstants.ACCOMMODATION_DAYS_TO_INDEX);

			final Date configStartDate = getTimeService().getCurrentTime();
			final Date configEndDate = TravelDateUtils.addDays(configStartDate, configuredDaysToIndex);

			// The index workers create a new session context that do not have a catalog version set. In order to get the catalog
			// aware items, the catalog version need to be set. The catalog version is retrieved from the item model being indexed
			// in order to support both the staged and online versions of the item to be indexed.
			getCatalogVersionService()
					.setSessionCatalogVersions(Collections.singleton(marketingRatePlanInfoModel.getCatalogVersion()));

			for (Date date = configStartDate; !date.after(configEndDate); date = TravelDateUtils.addDays(date, 1))
			{
				final Collection<RatePlanConfigModel> ratePlanConfigs = marketingRatePlanInfoModel.getRatePlanConfig();
				final boolean roomRatesAvailable = isRoomRateAvailableOnRatePlans(ratePlanConfigs, date);

				if (!roomRatesAvailable)
				{
					LOG.warn("No room rates found for MarketingRatePlanInfo: " + marketingRatePlanInfoModel.getCode() + " and date: "
							+ date);
					continue;
				}

				try
				{
					logDebug(itemModel);

					final SolrInputDocument solrDocument = getTravelSolrDocumentFactory()
							.createInputDocument(itemModel, indexConfig, indexedType, date);
					documents.add(solrDocument);
				}
				catch (FieldValueProviderException | RuntimeException e)
				{
					final String message = "Failed to index item with PK " + itemModel.getPk() + ": " + e.getMessage();
					handleError(indexConfig, indexedType, message, e);
				}
			}
		}

		final SolrServerMode serverMode = solrConfig.getMode();
		final Exporter exporter = getExporter(serverMode);
		exporter.exportToUpdateIndex(documents, facetSearchConfig, indexedType);

		return documents;
	}

	@Override
	public Collection<SolrInputDocument> indexItems(final Collection<ItemModel> items, final FacetSearchConfig facetSearchConfig,
			final IndexedType indexedType, final Collection<IndexedProperty> indexedProperties)
			throws IndexerException, InterruptedException
	{
		if (!StringUtils.equalsIgnoreCase(MarketingRatePlanInfoModel._TYPECODE, indexedType.getCode()))
		{
			return super.indexItems(items, facetSearchConfig, indexedType, indexedProperties);
		}

		if (items == null)
		{
			return Collections.emptyList();
		}

		final IndexConfig indexConfig = facetSearchConfig.getIndexConfig();
		final SolrConfig solrConfig = facetSearchConfig.getSolrConfig();

		final Collection<SolrInputDocument> documents = new ArrayList(CollectionUtils.size(items));

		for (final ItemModel itemModel : items)
		{
			if (Thread.interrupted())
			{
				throw new InterruptedException();
			}

			// Get the MarketingRatePlanInfo
			final MarketingRatePlanInfoModel marketingRatePlanInfoModel = (MarketingRatePlanInfoModel) itemModel;

			final int configuredDaysToIndex = getConfigurationService().getConfiguration()
					.getInt(TravelservicesConstants.ACCOMMODATION_DAYS_TO_INDEX);

			final Date configStartDate = getTimeService().getCurrentTime();
			final Date configEndDate = TravelDateUtils.addDays(configStartDate, configuredDaysToIndex);

			getCatalogVersionService()
					.setSessionCatalogVersions(Collections.singleton(marketingRatePlanInfoModel.getCatalogVersion()));

			for (Date date = configStartDate; date.before(configEndDate); date = TravelDateUtils.addDays(date, 1))
			{
				final Collection<RatePlanConfigModel> ratePlanConfigs = marketingRatePlanInfoModel.getRatePlanConfig();
				final boolean roomRatesAvailable = isRoomRateAvailableOnRatePlans(ratePlanConfigs, date);

				if (!roomRatesAvailable)
				{
					continue;
				}

				try
				{
					logDebug(itemModel);

					final SolrInputDocument solrDocument = getTravelSolrDocumentFactory()
							.createInputDocument(itemModel, indexConfig, indexedType, indexedProperties, date);
					documents.add(solrDocument);
				}
				catch (FieldValueProviderException | RuntimeException e)
				{
					final String message = "Failed to index item with PK " + itemModel.getPk() + ": " + e.getMessage();
					handleError(indexConfig, indexedType, message, e);
				}
			}
		}

		final SolrServerMode serverMode = solrConfig.getMode();
		final Exporter exporter = getExporter(serverMode);
		exporter.exportToUpdateIndex(documents, facetSearchConfig, indexedType);

		return documents;
	}

	protected void logDebug(final ItemModel itemModel)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Indexing item with PK " + itemModel.getPk());
		}
	}

	protected boolean isRoomRateAvailableOnRatePlans(final Collection<RatePlanConfigModel> ratePlanConfigs, final Date
			currentDate)
	{
		for (final RatePlanConfigModel ratePlanConfig : ratePlanConfigs)
		{
			final RoomRateProductModel roomRateAvailableOnRatePlan = RatePlanUtils
					.getRoomRateForRatePlan(ratePlanConfig.getRatePlan(), currentDate);
			if (roomRateAvailableOnRatePlan == null)
			{
				return false;
			}
		}
		return true;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected TravelSolrDocumentFactory getTravelSolrDocumentFactory()
	{
		return travelSolrDocumentFactory;
	}

	@Required
	public void setTravelSolrDocumentFactory(final TravelSolrDocumentFactory travelSolrDocumentFactory)
	{
		this.travelSolrDocumentFactory = travelSolrDocumentFactory;
	}
}
