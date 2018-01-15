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

package de.hybris.platform.travelfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.TravellerPreferenceData;
import de.hybris.platform.travelservices.enums.TravellerPreferenceType;
import de.hybris.platform.travelservices.model.user.TravellerPreferenceModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravellerPreferencePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravellerPreferencePopulatorTest
{
	@InjectMocks
	TravellerPreferencePopulator travellerPreferencePopulator;

	@Test
	public void testPopulateTravellerPreferenceData()
	{
		final TravellerPreferenceModel tpModel = new TravellerPreferenceModel();
		tpModel.setType(TravellerPreferenceType.LANGUAGE);
		tpModel.setValue(TravellerPreferenceType.LANGUAGE.toString());
		final TravellerPreferenceData tpData = new TravellerPreferenceData();
		travellerPreferencePopulator.populate(tpModel, tpData);
		Assert.assertEquals(TravellerPreferenceType.LANGUAGE.name(), tpData.getType());
		Assert.assertEquals(TravellerPreferenceType.LANGUAGE.toString(), tpData.getValue());
	}
}
