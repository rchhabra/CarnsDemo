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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.travelservices.model.pages.AncillaryPageModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import org.junit.Assert;

import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultAncillaryOfferGroupDisplayStrategyTest
{
	private DefaultAncillaryOfferGroupDisplayStrategy ancillaryOfferGroupDisplayStrategy;

	@Mock
	private AncillaryPageModel ancillaryPage;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		ancillaryOfferGroupDisplayStrategy = new DefaultAncillaryOfferGroupDisplayStrategy();
	}

	@Test
	public void testFilterOfferResponseData()
	{

		final OfferResponseData offerResponseData = new OfferResponseData();

		final List<OfferGroupData> offerGroupDataList = new ArrayList<>();
		final OfferGroupData offerGroup1 = new OfferGroupData();
		offerGroup1.setCode("MEALS");
		offerGroupDataList.add(offerGroup1);
		final OfferGroupData offerGroup2 = new OfferGroupData();
		offerGroup2.setCode("HOLD_ALLOWANCE");
		offerGroupDataList.add(offerGroup2);
		final OfferGroupData offerGroup3 = new OfferGroupData();
		offerGroup3.setCode("PRIORITY_CHECKIN");
		offerGroupDataList.add(offerGroup3);

		offerResponseData.setOfferGroups(offerGroupDataList);

		final List<String> offerGroupsList = new ArrayList<>();
		offerGroupsList.add("MEALS");
		offerGroupsList.add("HOLD_ALLOWANCE");
		//		offerGroupsList.add("SEAT");

		when(ancillaryPage.getOfferGroups()).thenReturn(offerGroupsList);

		ancillaryOfferGroupDisplayStrategy.filterOfferResponseData(ancillaryPage, offerResponseData);

		Assert.assertEquals(offerResponseData.getOfferGroups().size(), offerGroupsList.size());
		Assert.assertNotNull(offerResponseData.getSeatMap());


	}

	@Test
	public void testFilterOfferResponseDataWithSeat()
	{

		final OfferResponseData offerResponseData = new OfferResponseData();

		final List<OfferGroupData> offerGroupDataList = new ArrayList<>();
		final OfferGroupData offerGroup1 = new OfferGroupData();
		offerGroup1.setCode("MEALS");
		offerGroupDataList.add(offerGroup1);
		final OfferGroupData offerGroup2 = new OfferGroupData();
		offerGroup2.setCode("HOLD_ALLOWANCE");
		offerGroupDataList.add(offerGroup2);
		final OfferGroupData offerGroup3 = new OfferGroupData();
		offerGroup3.setCode("PRIORITY_CHECKIN");
		offerGroupDataList.add(offerGroup3);

		offerResponseData.setOfferGroups(offerGroupDataList);

		final List<String> offerGroupsList = new ArrayList<>();
		offerGroupsList.add("MEALS");
		offerGroupsList.add("HOLD_ALLOWANCE");
		offerGroupsList.add("SEAT");

		when(ancillaryPage.getOfferGroups()).thenReturn(offerGroupsList);

		ancillaryOfferGroupDisplayStrategy.filterOfferResponseData(ancillaryPage, offerResponseData);

		Assert.assertNotNull(offerResponseData);

	}

	@Test
	public void testFilterOfferResponseDataWhenContentPageIsNull()
	{

		final OfferResponseData offerResponseData = new OfferResponseData();

		final List<OfferGroupData> offerGroupDataList = new ArrayList<>();
		final OfferGroupData offerGroup1 = new OfferGroupData();
		offerGroup1.setCode("MEALS");
		offerGroupDataList.add(offerGroup1);
		final OfferGroupData offerGroup2 = new OfferGroupData();
		offerGroup2.setCode("HOLD_ALLOWANCE");
		offerGroupDataList.add(offerGroup2);
		final OfferGroupData offerGroup3 = new OfferGroupData();
		offerGroup3.setCode("PRIORITY_CHECKIN");
		offerGroupDataList.add(offerGroup3);

		offerResponseData.setOfferGroups(offerGroupDataList);

		final List<String> offerGroupsList = new ArrayList<>();
		offerGroupsList.add("MEALS");
		offerGroupsList.add("HOLD_ALLOWANCE");
		offerGroupsList.add("SEAT");

		when(ancillaryPage.getOfferGroups()).thenReturn(offerGroupsList);

		ancillaryOfferGroupDisplayStrategy.filterOfferResponseData(null, offerResponseData);

		Assert.assertNotNull(offerResponseData);

	}

}
