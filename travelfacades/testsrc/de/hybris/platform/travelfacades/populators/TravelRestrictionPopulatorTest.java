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

package de.hybris.platform.travelfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for TravelRestrictionPopulator implementation
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelRestrictionPopulatorTest
{

   @InjectMocks
   private final TravelRestrictionPopulator travelRestrictionPopulator = new TravelRestrictionPopulator();

   /**
    * Test method to verify the population of TravelRestrictionModel to TravelRestrictionData objects.
    */
   @Test
   public void populateTravelRestrictionDataFromTravelRestrictionModelTest()
   {

      final TravelRestrictionModel trModel = Mockito.mock(TravelRestrictionModel.class);

      Mockito.when(trModel.getEffectiveDate()).thenReturn(new Date());
      Mockito.when(trModel.getExpireDate()).thenReturn(new Date());
      Mockito.when(trModel.getTravellerMaxOfferQty()).thenReturn(1);
      Mockito.when(trModel.getTravellerMinOfferQty()).thenReturn(0);
      Mockito.when(trModel.getTripMaxOfferQty()).thenReturn(1);
      Mockito.when(trModel.getTripMinOfferQty()).thenReturn(0);
		Mockito.when(trModel.getAddToCartCriteria()).thenReturn(null);
		Mockito.when(trModel.getPassengerTypes()).thenReturn(Arrays.asList("child"));

      final TravelRestrictionData trData = new TravelRestrictionData();

      travelRestrictionPopulator.populate(trModel, trData);

      Assert.assertEquals(trModel.getEffectiveDate(), trData.getEffectiveDate());
      Assert.assertEquals(trModel.getExpireDate(), trData.getExpireDate());
      Assert.assertEquals(trModel.getTravellerMaxOfferQty(), trData.getTravellerMaxOfferQty());
      Assert.assertEquals(trModel.getTravellerMinOfferQty(), trData.getTravellerMinOfferQty());
      Assert.assertEquals(trModel.getTripMaxOfferQty(), trData.getTripMaxOfferQty());
      Assert.assertEquals(trModel.getTripMinOfferQty(), trData.getTripMinOfferQty());
		Assert.assertNull(trData.getAddToCartCriteria());
		Assert.assertEquals(trModel.getPassengerTypes(), trData.getPassengerTypes());

   }

   /**
    * Test method to verify the population of TravelRestrictionModel to TravelRestrictionData objects when the
    * TravelRestrictionModel has null attributes.
    */
   @Test
   public void nullSourceAttributesTest()
   {
      final TravelRestrictionModel trModel = Mockito.mock(TravelRestrictionModel.class);

      Mockito.when(trModel.getEffectiveDate()).thenReturn(null);
      Mockito.when(trModel.getExpireDate()).thenReturn(null);
      Mockito.when(trModel.getTravellerMaxOfferQty()).thenReturn(null);
      Mockito.when(trModel.getTravellerMinOfferQty()).thenReturn(null);
      Mockito.when(trModel.getTripMaxOfferQty()).thenReturn(null);
      Mockito.when(trModel.getTripMinOfferQty()).thenReturn(null);
		Mockito.when(trModel.getAddToCartCriteria()).thenReturn(null);
		Mockito.when(trModel.getPassengerTypes()).thenReturn(null);

      final TravelRestrictionData trData = new TravelRestrictionData();

      travelRestrictionPopulator.populate(trModel, trData);

      Assert.assertNull(trData.getEffectiveDate());
      Assert.assertNull(trData.getExpireDate());
		Assert.assertEquals(-1, trData.getTravellerMaxOfferQty().intValue());
		Assert.assertEquals(0, trData.getTravellerMinOfferQty().intValue());
		Assert.assertEquals(-1, trData.getTripMaxOfferQty().intValue());
		Assert.assertEquals(0, trData.getTripMinOfferQty().intValue());
		Assert.assertNull(trData.getAddToCartCriteria());
		Assert.assertNull(trData.getPassengerTypes());

   }

   /**
    * Test method to verify the population of TravelRestrictionModel to TravelRestrictionData objects when the
    * TravelRestrictionModel is null.
    */
   @Test(expected = IllegalArgumentException.class)
   public void nullSourceTest()
   {

      final TravelRestrictionModel trModel = null;
      final TravelRestrictionData trData = new TravelRestrictionData();

      travelRestrictionPopulator.populate(trModel, trData);

   }
}
