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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * Unit Test for the DefaultTravelRestrictionService implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelRestrictionServiceTest
{
	@InjectMocks
	DefaultTravelRestrictionService travelRestrictionService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private ProductModel productModel;
	@Mock
	private CategoryModel categoryModel;

	@Test
	public void testGetAddToCartCriteria()
	{
		Mockito.when(productModel.getSupercategories()).thenReturn(Stream.of(categoryModel).collect(Collectors.toList()));
		final OfferGroupRestrictionModel offerGroupRestrictionModel = new OfferGroupRestrictionModel();
		offerGroupRestrictionModel.setAddToCartCriteria(AddToCartCriteriaType.PER_BOOKING);
		Mockito.when(categoryModel.getTravelRestriction()).thenReturn(offerGroupRestrictionModel);
		Assert.assertNotNull(travelRestrictionService.getAddToCartCriteria(productModel));
	}

	@Test
	public void testGetAddToCartCriteriaWithEmptyCategories()
	{
		Mockito.when(productModel.getSupercategories()).thenReturn(Collections.emptyList());
		Mockito.when(enumerationService.getEnumerationValue(AddToCartCriteriaType.class,
				TravelservicesConstants.DEFAULT_ADD_TO_CART_CRITERIA)).thenReturn(AddToCartCriteriaType.PER_LEG_PER_PAX);
		Assert.assertNotNull(travelRestrictionService.getAddToCartCriteria(productModel));
	}
}
