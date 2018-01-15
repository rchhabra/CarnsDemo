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
package de.hybris.platform.travelfacades.strategies.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelfacades.strategies.OfferSortStrategy;
import de.hybris.platform.travelservices.enums.OfferSort;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;


/**
 * Test for the Strategy {@link OfferSortStrategy}, for implementation class {@link DefaultOfferSortStrategy}
 */
@UnitTest
public class DefaultOfferSortStrategyTest
{

	private static final String HOLDITEM = "HOLDITEM";
	public static final String MEAL = "MEAL";
	public static final String PRIORITYCHECKIN = "PRIORITYCHECKIN";
	public static final String PRIORITYBOARDING = "PRIORITYBOARDING";

	@Mock
	private EnumerationService enumerationService;

	private DefaultOfferSortStrategy defaultOfferSortStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultOfferSortStrategy = new DefaultOfferSortStrategy();
		defaultOfferSortStrategy.setEnumerationService(enumerationService);
	}

	@Test
	public void whenNoOfferSortEnumValue_thenNoSortingIsApplied()
	{
		when(enumerationService.getEnumerationValues(OfferSort.class)).thenReturn(new ArrayList<>());

		final OfferGroupData offer1 = new OfferGroupData();
		offer1.setCode(HOLDITEM);

		final OfferGroupData offer2 = new OfferGroupData();
		offer2.setCode(MEAL);

		final OfferGroupData offer3 = new OfferGroupData();
		offer3.setCode(PRIORITYCHECKIN);

		final List<OfferGroupData> offerGroups = new ArrayList<>();
		offerGroups.add(offer2);
		offerGroups.add(offer3);
		offerGroups.add(offer1);

		final List<OfferGroupData> sortedOfferGroups = defaultOfferSortStrategy.applyStrategy(offerGroups);

		Assert.notNull(sortedOfferGroups);
		Assert.notEmpty(sortedOfferGroups);
		assertEquals(sortedOfferGroups.get(0), offer2);
		assertEquals(sortedOfferGroups.get(1), offer3);
		assertEquals(sortedOfferGroups.get(2), offer1);
	}

	@Test
	public void whenOfferSortEnumValue_thenSortingIsApplied()
	{

		final List<OfferSort> offerSortEnumValues = Arrays.asList(OfferSort.HOLDITEM, OfferSort.PRIORITYCHECKIN, OfferSort.MEAL);

		when(enumerationService.getEnumerationValues(OfferSort.class)).thenReturn(offerSortEnumValues);

		final OfferGroupData offer1 = new OfferGroupData();
		offer1.setCode(HOLDITEM);

		final OfferGroupData offer2 = new OfferGroupData();
		offer2.setCode(MEAL);

		final OfferGroupData offer3 = new OfferGroupData();
		offer3.setCode(PRIORITYCHECKIN);

		final List<OfferGroupData> offerGroups = new ArrayList<>();
		offerGroups.add(offer2);
		offerGroups.add(offer3);
		offerGroups.add(offer1);

		final List<OfferGroupData> sortedOfferGroups = defaultOfferSortStrategy.applyStrategy(offerGroups);

		Assert.notNull(sortedOfferGroups);
		Assert.notEmpty(sortedOfferGroups);
		assertEquals(sortedOfferGroups.get(0), offer1);
		assertEquals(sortedOfferGroups.get(1), offer3);
		assertEquals(sortedOfferGroups.get(2), offer2);
	}

	@Test
	public void whenNoOfferSortDefinedForCode_thenAddItToTheEnd()
	{
		final List<OfferSort> offerSortEnumValues = Arrays.asList(OfferSort.HOLDITEM, OfferSort.PRIORITYCHECKIN, OfferSort.MEAL);

		when(enumerationService.getEnumerationValues(OfferSort.class)).thenReturn(offerSortEnumValues);

		final OfferGroupData offer1 = new OfferGroupData();
		offer1.setCode(HOLDITEM);

		final OfferGroupData offer2 = new OfferGroupData();
		offer2.setCode(MEAL);

		final OfferGroupData offer3 = new OfferGroupData();
		offer3.setCode(PRIORITYCHECKIN);

		final OfferGroupData offer4 = new OfferGroupData();
		offer4.setCode(PRIORITYBOARDING);


		final List<OfferGroupData> offerGroups = new ArrayList<>();
		offerGroups.add(offer4);
		offerGroups.add(offer2);
		offerGroups.add(offer3);
		offerGroups.add(offer1);

		final List<OfferGroupData> unsortedOffers = defaultOfferSortStrategy.getUndefinedOfferGroups(offerGroups,
			offerSortEnumValues);

		Assert.notNull(unsortedOffers);
		Assert.notEmpty(unsortedOffers);
		assertTrue("Expected size 1, size is: " + unsortedOffers.size(),unsortedOffers.size() == 1);
		assertTrue(unsortedOffers.contains(offer4));
	}


}
