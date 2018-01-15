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

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultSolrInputDocument;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.solr.provider.impl.AbstractDateBasedValueResolver.ValueResolverContext;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.PriceValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link RoomRatePriceValueResolver}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RoomRatePriceValueResolverTest
{
	@InjectMocks
	RoomRatePriceValueResolver roomRatePriceValueResolver;

	@Mock
	private PriceService priceService;

	@Mock
	private DefaultSolrInputDocument defaultSolrInputDocument;

	@Test
	public void testAddFieldValues() throws FieldValueProviderException
	{
		final ValueResolverContext<Object, Map<String, Double>> resolverContext = new ValueResolverContext<>();

		final Map<String, Double> priceInformationMap = new HashMap<>();
		priceInformationMap.put("TEST_RATE_PLAN_CONFIG_CODE", 200d);

		resolverContext.setQualifierData(priceInformationMap);
		resolverContext.setFieldQualifier("TEST_FIELD_QUALIFIER");
		roomRatePriceValueResolver.addFieldValues(defaultSolrInputDocument, new DefaultIndexerBatchContext(), new IndexedProperty(),
				new MarketingRatePlanInfoModel(), resolverContext, new Date());
	}

	@Test
	public void testAddFieldValuesForEmpty() throws FieldValueProviderException
	{
		final ValueResolverContext<Object, Map<String, Double>> resolverContext = new ValueResolverContext<>();

		final Map<String, Double> priceInformationMap = new HashMap<>();

		resolverContext.setQualifierData(priceInformationMap);
		resolverContext.setFieldQualifier("TEST_FIELD_QUALIFIER");
		roomRatePriceValueResolver.addFieldValues(defaultSolrInputDocument, new DefaultIndexerBatchContext(), new IndexedProperty(),
				new MarketingRatePlanInfoModel(), resolverContext, new Date());
	}

	@Test
	public void testLoadQualifierData()
	{
		final Date date = TravelDateUtils.convertStringDateToDate("02/01/2017", TravelservicesConstants.DATE_PATTERN);
		final Date documentDate = TravelDateUtils.addDays(date, 1);

		final RatePlanConfigModel ratePlanConfig1 = new RatePlanConfigModel();
		ratePlanConfig1.setQuantity(1);
		ratePlanConfig1.setCode("TEST_RATE_PLAN_CONFIG_1");
		final RatePlanModel ratePlan1 = new RatePlanModel();

		final RoomRateProductModel roomRateProductModel1 = new RoomRateProductModel();
		roomRateProductModel1.setDateRanges(Collections.emptyList());
		ratePlan1.setProducts(Collections.singletonList(roomRateProductModel1));
		ratePlanConfig1.setRatePlan(ratePlan1);

		final RatePlanConfigModel ratePlanConfig2 = new RatePlanConfigModel();
		ratePlanConfig2.setCode("TEST_RATE_PLAN_CONFIG_2");
		ratePlanConfig2.setQuantity(2);
		final RatePlanModel ratePlan2 = new RatePlanModel();
		final RoomRateProductModel roomRateProductModel2 = new RoomRateProductModel();

		final DateRangeModel dateRangeModel2 = new DateRangeModel();
		dateRangeModel2.setStartingDate(date);
		dateRangeModel2.setEndingDate(TravelDateUtils.addDays(date, 1));
		roomRateProductModel2.setDateRanges(Arrays.asList(dateRangeModel2));

		final List<DayOfWeek> daysOfWeek2 = new ArrayList<>();
		daysOfWeek2.add(DayOfWeek.MONDAY);
		daysOfWeek2.add(DayOfWeek.TUESDAY);
		roomRateProductModel2.setDaysOfWeek(daysOfWeek2);

		ratePlan2.setProducts(Collections.singletonList(roomRateProductModel2));
		ratePlanConfig2.setRatePlan(ratePlan2);

		final RatePlanConfigModel ratePlanConfig3 = new RatePlanConfigModel();
		ratePlanConfig3.setCode("TEST_RATE_PLAN_CONFIG_3");
		ratePlanConfig3.setQuantity(3);
		final RatePlanModel ratePlan3 = new RatePlanModel();
		final RoomRateProductModel roomRateProductMode3 = new RoomRateProductModel();

		final DateRangeModel dateRangeModel3 = new DateRangeModel();
		dateRangeModel3.setStartingDate(TravelDateUtils.addDays(date, 2));
		dateRangeModel3.setEndingDate(TravelDateUtils.addDays(date, 3));
		roomRateProductMode3.setDateRanges(Arrays.asList(dateRangeModel3));

		final List<DayOfWeek> daysOfWeek3 = new ArrayList<>();
		daysOfWeek3.add(DayOfWeek.WEDNESDAY);
		daysOfWeek3.add(DayOfWeek.THURSDAY);
		roomRateProductMode3.setDaysOfWeek(daysOfWeek3);

		ratePlan3.setProducts(Collections.singletonList(roomRateProductMode3));
		ratePlanConfig3.setRatePlan(ratePlan3);

		final RatePlanConfigModel ratePlanConfig4 = new RatePlanConfigModel();
		ratePlanConfig4.setCode("TEST_RATE_PLAN_CONFIG_4");
		ratePlanConfig4.setQuantity(4);
		final RatePlanModel ratePlan4 = new RatePlanModel();
		final RoomRateProductModel roomRateProductMode4 = new RoomRateProductModel();

		final DateRangeModel dateRangeModel4 = new DateRangeModel();
		ratePlanConfig4.setQuantity(4);
		dateRangeModel4.setStartingDate(documentDate);
		dateRangeModel4.setEndingDate(TravelDateUtils.addDays(date, 2));
		roomRateProductMode4.setDateRanges(Arrays.asList(dateRangeModel4));

		final List<DayOfWeek> daysOfWeek4 = new ArrayList<>();
		daysOfWeek4.add(DayOfWeek.TUESDAY);
		daysOfWeek4.add(DayOfWeek.WEDNESDAY);
		roomRateProductMode4.setDaysOfWeek(daysOfWeek4);

		ratePlan4.setProducts(Collections.singletonList(roomRateProductMode4));
		ratePlanConfig4.setRatePlan(ratePlan4);

		final RatePlanConfigModel ratePlanConfig5 = new RatePlanConfigModel();
		ratePlanConfig5.setQuantity(5);
		ratePlanConfig5.setCode("TEST_RATE_PLAN_CONFIG_5");

		final RatePlanModel ratePlan5 = new RatePlanModel();
		final RoomRateProductModel roomRateProductMode5 = new RoomRateProductModel();

		final DateRangeModel dateRangeModel5 = new DateRangeModel();
		dateRangeModel5.setStartingDate(date);
		dateRangeModel5.setEndingDate(documentDate);
		roomRateProductMode5.setDateRanges(Arrays.asList(dateRangeModel5));

		final List<DayOfWeek> daysOfWeek5 = new ArrayList<>();
		daysOfWeek5.add(DayOfWeek.MONDAY);
		daysOfWeek5.add(DayOfWeek.TUESDAY);
		roomRateProductMode5.setDaysOfWeek(daysOfWeek5);

		ratePlan5.setProducts(Collections.singletonList(roomRateProductMode5));
		ratePlanConfig5.setRatePlan(ratePlan4);

		final Collection<RatePlanConfigModel> ratePlanConfigs = new ArrayList<>();
		ratePlanConfigs.add(ratePlanConfig1);
		ratePlanConfigs.add(ratePlanConfig2);
		ratePlanConfigs.add(ratePlanConfig3);
		ratePlanConfigs.add(ratePlanConfig4);
		ratePlanConfigs.add(ratePlanConfig5);


		final MarketingRatePlanInfoModel marketingRatePlanInfo = new MarketingRatePlanInfoModel();
		marketingRatePlanInfo.setRatePlanConfig(ratePlanConfigs);

		final PriceValue priceValue = new PriceValue("TEST_CURRENCY_ISO", 100d, true);
		final PriceInformation priceInformation = new PriceInformation(priceValue);

		when(priceService.getPriceInformationsForProduct(Matchers.any(RoomRateProductModel.class)))
				.thenReturn(Collections.singletonList(priceInformation));

		Assert.assertEquals(500,
				roomRatePriceValueResolver.loadQualifierData(null, Collections.emptyList(), marketingRatePlanInfo, null, documentDate)
						.get("TEST_RATE_PLAN_CONFIG_5").intValue());
	}

}
