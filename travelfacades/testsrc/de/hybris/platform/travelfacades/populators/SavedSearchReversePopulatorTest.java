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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.travelfacades.strategies.EncodeSavedSearchStrategy;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SavedSearchReversePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SavedSearchReversePopulatorTest
{
	@InjectMocks
	SavedSearchReversePopulator savedSearchReversePopulator;

	@Mock
	private SavedSearchModel mockTarget;

	@Mock
	private EncodeSavedSearchStrategy mockEncodeSavedSearchStrategy;

	@Test
	public void testPopulateSavedSearchModel()
	{
		given(mockEncodeSavedSearchStrategy.getEncodedSearch(Matchers.any(SavedSearchData.class))).willReturn("testEncodedSearch");

		savedSearchReversePopulator.populate(new SavedSearchData(), mockTarget);
		verify(mockTarget, times(1)).setEncodedSearch(Matchers.anyString());
	}
}
