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

package de.hybris.platform.solrfacetsearch.indexer.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.TravelSolrDocumentFactory;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Exporter;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test cases for {@link TravelIndexer}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelIndexerTest
{
	TravelIndexer travelIndexer = new TravelIndexer()
	{
		@Override
		protected Exporter getExporter(final SolrServerMode serverMode)
		{
			return exporter;
		}

		@Override
		protected void handleError(final IndexConfig indexConfig, final IndexedType indexedType, final String message,
				final Exception error) throws IndexerException
		{
			return;
		}

	};

	@Mock
	Exporter exporter;
	@Mock
	private TimeService timeService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration config;

	@Mock
	private TravelSolrDocumentFactory travelSolrDocumentFactory;

	@Before
	public void setUp()
	{
		travelIndexer.setConfigurationService(configurationService);
		travelIndexer.setCatalogVersionService(catalogVersionService);
		travelIndexer.setTimeService(timeService);
		travelIndexer.setTravelSolrDocumentFactory(travelSolrDocumentFactory);
	}

	@Test
	public void testIndexItemsForNullItems() throws IndexerException, InterruptedException
	{
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);
		Assert.assertTrue(CollectionUtils.isEmpty(travelIndexer.indexItems(null, new FacetSearchConfig(), indexedType)));
	}

	@Test
	public void testIndexItemsForFieldValueProviderException()
			throws IndexerException, InterruptedException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
				.thenThrow(new FieldValueProviderException("Exception"));
		Assert.assertTrue(CollectionUtils.isEmpty(
				travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType)));
	}

	@Test
	public void testIndexItemsForRuntimeException() throws IndexerException, InterruptedException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
				.thenThrow(new RuntimeException("Exception"));
		Assert.assertTrue(CollectionUtils.isEmpty(
				travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType)));
	}

	@Test
	public void testIndexItems() throws IndexerException, InterruptedException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
				.thenReturn(new SolrInputDocument());
		Assert.assertTrue(CollectionUtils.isNotEmpty(
				travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType)));
	}


	@Test
	public void testIndexItemsForIndexPropertiesForNullItems() throws IndexerException, InterruptedException
	{
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);
		Assert.assertTrue(CollectionUtils.isEmpty(travelIndexer.indexItems(null, new FacetSearchConfig(), indexedType)));
	}

	@Test
	public void testIndexItemsForIndexPropertiesForFieldValueProviderException()
			throws IndexerException, InterruptedException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any())).thenThrow(new FieldValueProviderException("Exception"));
		Assert.assertTrue(CollectionUtils.isEmpty(
				travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType,
						Collections.emptyList())));
	}

	@Test
	public void testIndexItemsForIndexPropertiesForRuntimeException()
			throws IndexerException, InterruptedException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any())).thenThrow(new RuntimeException("Exception"));
		Assert.assertTrue(CollectionUtils.isEmpty(
				travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType,
						Collections.emptyList())));
	}

	@Test
	public void testIndexItemsForIndexProperties() throws IndexerException, InterruptedException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any())).thenReturn(new SolrInputDocument());
		Assert.assertTrue(CollectionUtils.isNotEmpty(
				travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType,
						Collections.emptyList())));
	}


	@Test
	public void testIndexItemsWithInterruptedException() throws IndexerException, FieldValueProviderException
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getInt(Matchers.anyString())).thenReturn(1);
		when(timeService.getCurrentTime()).thenReturn(TravelDateUtils.addDays(date, 1));
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(MarketingRatePlanInfoModel._TYPECODE);

		final RoomRateProductModel roomRateProductModel = new RoomRateProductModel();
		final DateRangeModel dateRange = new DateRangeModel();
		dateRange.setStartingDate(date);
		dateRange.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductModel.setDateRanges(Collections.singletonList(dateRange));
		roomRateProductModel.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
		final List<ProductModel> products = new ArrayList<>();
		products.add(roomRateProductModel);
		final RatePlanModel ratePlan = new RatePlanModel();
		ratePlan.setProducts(products);


		final RatePlanConfigModel ratePlanConfigModel = new RatePlanConfigModel();
		ratePlanConfigModel.setRatePlan(ratePlan);

		final PK pkDemo = PK.fromLong(00001l);
		final MarketingRatePlanInfoModel marketingRatePlanInfoModel = new MarketingRatePlanInfoModel()
		{
			@Override
			public PK getPk()
			{
				return pkDemo;
			}
		};
		marketingRatePlanInfoModel.setCatalogVersion(new CatalogVersionModel());
		marketingRatePlanInfoModel.setRatePlanConfig(Collections.singletonList(ratePlanConfigModel));


		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.STANDALONE);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSolrConfig(solrConfig);
		when(travelSolrDocumentFactory.createInputDocument(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
				.thenReturn(new SolrInputDocument());


		final Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					travelIndexer.indexItems(Collections.singletonList(marketingRatePlanInfoModel), facetSearchConfig, indexedType);
				}
				catch (IndexerException | InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});

		t.start();

		t.interrupt();
	}

}
