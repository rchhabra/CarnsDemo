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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.enums.CategoryOption;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.handlers.impl.OfferGroupHandler;
import de.hybris.platform.travelfacades.strategies.OfferSortStrategy;
import de.hybris.platform.travelservices.services.TravelCategoryService;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for OfferGroupHandler.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferGroupHandlerTest
{
	@Mock
	private TravelCategoryService travelCategoryService;

	@Mock
	private Converter<CategoryModel, OfferGroupData> categoryConverter;

	@Mock
	private OfferSortStrategy offerSortStrategy;

	@Mock
	private ConfigurablePopulator<CategoryModel, OfferGroupData, CategoryOption> offerGroupDataConfiguredPopulator;

	@InjectMocks
	private OfferGroupHandler handler;

	@Before
	public void setUp()
	{

	}

	/**
	 * given: TransportOfferings,Category models and no CategoryOptionList.
	 * 
	 * when: there are no CategoryOptions
	 * 
	 * Then: OfferGroups are populated in OfferResponseData without travel restrictions.
	 */
	@Test
	public void testPopulateNoCategoryOtions()
	{
		final List<String> transportOfferingCodes = Stream.of("EZY1234060320160725", "EZY5678060320160925")
				.collect(Collectors.toList());
		final List<TransportOfferingData> transportOfferings = transportOfferingCodes.stream().map(toConverter)
				.collect(Collectors.toList());
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OriginDestinationOptionData odOptionData = testDataSetUp.createOriginDestinationOptionData(transportOfferings);
		final ItineraryData itineraryData = testDataSetUp.createItineraryData(Stream.of(odOptionData).collect(Collectors.toList()));
		final OfferRequestData offerRequestData = testDataSetUp
				.createOfferRequestData(Stream.of(itineraryData).collect(Collectors.toList()));
		final CategoryModel holdItemCat = testDataSetUp.createCategory("HOLDITEM");
		final CategoryModel mealCat = testDataSetUp.createCategory("MEAL");
		given(travelCategoryService.getAncillaryCategories(Matchers.anyList()))
				.willReturn(Stream.of(holdItemCat, mealCat).collect(Collectors.toList()));
		final OfferGroupData holdItemOffer = testDataSetUp.createOfferGroupData("HOLDITEM");
		given(categoryConverter.convert(Matchers.eq(holdItemCat), Matchers.any(OfferGroupData.class))).willReturn(holdItemOffer);
		final OfferGroupData mealOffer = testDataSetUp.createOfferGroupData("MEAL");
		given(categoryConverter.convert(Matchers.eq(mealCat), Matchers.any(OfferGroupData.class))).willReturn(mealOffer);
		given(offerSortStrategy.applyStrategy(Matchers.anyList()))
				.willReturn(Stream.of(holdItemOffer, mealOffer).collect(Collectors.toList()));
		handler.setCategoryOptionList(Collections.emptyList());
		final OfferResponseData offerResponseData = new OfferResponseData();
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups());
		assertEquals(2, offerResponseData.getOfferGroups().size());
	}

	/**
	 * given: TransportOfferings,Category models and no CategoryOptionList.
	 * 
	 * when: CategoryOptions are present
	 * 
	 * Then: OfferGroups are populated in OfferResponseData with travel restrictions.
	 */
	@Test
	public void testPopulateWithCategoryOptions()
	{
		final List<String> transportOfferingCodes = Stream.of("EZY1234060320160725", "EZY5678060320160925")
				.collect(Collectors.toList());
		final List<TransportOfferingData> transportOfferings = transportOfferingCodes.stream().map(toConverter)
				.collect(Collectors.toList());
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final OriginDestinationOptionData odOptionData = testDataSetUp.createOriginDestinationOptionData(transportOfferings);
		final ItineraryData itineraryData = testDataSetUp.createItineraryData(Stream.of(odOptionData).collect(Collectors.toList()));
		final OfferRequestData offerRequestData = testDataSetUp
				.createOfferRequestData(Stream.of(itineraryData).collect(Collectors.toList()));
		final CategoryModel holdItemCat = testDataSetUp.createCategory("HOLDITEM");
		final CategoryModel mealCat = testDataSetUp.createCategory("MEAL");
		given(travelCategoryService.getAncillaryCategories(Matchers.anyList()))
				.willReturn(Stream.of(holdItemCat, mealCat).collect(Collectors.toList()));
		final OfferGroupData holdItemOffer = testDataSetUp.createOfferGroupData("HOLDITEM");
		given(categoryConverter.convert(Matchers.eq(holdItemCat), Matchers.any(OfferGroupData.class))).willReturn(holdItemOffer);
		final OfferGroupData mealOffer = testDataSetUp.createOfferGroupData("MEAL");
		given(categoryConverter.convert(Matchers.eq(mealCat), Matchers.any(OfferGroupData.class))).willReturn(mealOffer);
		given(offerSortStrategy.applyStrategy(Matchers.anyList()))
				.willReturn(Stream.of(holdItemOffer, mealOffer).collect(Collectors.toList()));
		handler.setCategoryOptionList(Stream.of(CategoryOption.TRAVEL_RESTRICTION).collect(Collectors.toList()));
		willDoNothing().given(offerGroupDataConfiguredPopulator).populate(Matchers.any(CategoryModel.class),
				Matchers.any(OfferGroupData.class), Matchers.anyList());
		final OfferResponseData offerResponseData = new OfferResponseData();
		handler.handle(offerRequestData, offerResponseData);
		assertNotNull(offerResponseData.getOfferGroups());
		assertEquals(2, offerResponseData.getOfferGroups().size());
	}

	Function<String, TransportOfferingData> toConverter = new Function<String, TransportOfferingData>()
	{
		@Override
		public TransportOfferingData apply(final String code)
		{
			final TransportOfferingData toModel = new TransportOfferingData();
			toModel.setCode(code);
			return toModel;
		}

	};

	private class TestDataSetUp
	{
		private OfferRequestData createOfferRequestData(final List<ItineraryData> itineraries)
		{
			final OfferRequestData offerRequestData = new OfferRequestData();
			offerRequestData.setItineraries(itineraries);
			return offerRequestData;
		}

		private ItineraryData createItineraryData(final List<OriginDestinationOptionData> originDestinationOptions)
		{
			final ItineraryData itineraryData = new ItineraryData();
			itineraryData.setOriginDestinationOptions(originDestinationOptions);
			return itineraryData;
		}

		private OriginDestinationOptionData createOriginDestinationOptionData(final List<TransportOfferingData> transportOfferings)
		{
			final OriginDestinationOptionData odData = new OriginDestinationOptionData();
			odData.setTransportOfferings(transportOfferings);
			return odData;
		}

		private CategoryModel createCategory(final String code)
		{
			final CategoryModel catModel = new CategoryModel()
			{
				@Override
				public String getDescription()
				{
					return "Description";
				}
			};
			catModel.setCode(code);
			return catModel;
		}

		private OfferGroupData createOfferGroupData(final String code)
		{
			final OfferGroupData offerGroupData = new OfferGroupData();
			offerGroupData.setCode(code);
			return offerGroupData;
		}
	}

}
