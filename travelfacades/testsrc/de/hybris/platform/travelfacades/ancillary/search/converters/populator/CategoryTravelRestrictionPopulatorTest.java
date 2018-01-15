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

package de.hybris.platform.travelfacades.ancillary.search.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;




@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryTravelRestrictionPopulatorTest
{
	@Mock
	private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;

	@InjectMocks
	private final CategoryTravelRestrictionPopulator populator = new CategoryTravelRestrictionPopulator();

	@Before
	public void setUp()
	{
		given(travelRestrictionConverter.convert(Matchers.any(TravelRestrictionModel.class)))
				.willReturn(createTravelRestrictionData());
	}

	@Test
	public void testDefaultRestrictions()
	{
		final CategoryModel category = new CategoryModel();
		final OfferGroupData offerGroupData = new OfferGroupData();
		populator.populate(category, offerGroupData);
		assertNotNull(offerGroupData.getTravelRestriction());
		assertEquals(TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION,
				offerGroupData.getTravelRestriction().getTravellerMaxOfferQty().intValue());
	}

	@Test
	public void testTravelRestrictions()
	{
		final CategoryModel category = new CategoryModel();
		category.setTravelRestriction(new OfferGroupRestrictionModel());
		final OfferGroupData offerGroupData = new OfferGroupData();
		populator.populate(category, offerGroupData);
		populator.getTravelRestrictionConverter();
		assertNotNull(offerGroupData.getTravelRestriction());
		assertEquals(5, offerGroupData.getTravelRestriction().getTravellerMaxOfferQty().intValue());
	}

	private TravelRestrictionData createTravelRestrictionData()
	{
		final TravelRestrictionData travelRestrictionData = new TravelRestrictionData();
		travelRestrictionData.setTravellerMaxOfferQty(5);
		travelRestrictionData.setTravellerMinOfferQty(1);
		travelRestrictionData.setTripMaxOfferQty(6);
		travelRestrictionData.setTripMinOfferQty(1);
		travelRestrictionData.setAddToCartCriteria("PER_LEG_PER_PAX");

		return travelRestrictionData;
	}

}
