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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.comparator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationInfoDataComparatorTest
{

	@InjectMocks
	private final AccommodationInfoDataComparator accommodationInfoDataComparator = new AccommodationInfoDataComparator();

	@Test
	public void accommodationsSortedInAscendingOrderByPriceTest()
	{
		final List<SeatInfoData> accommodations = new ArrayList<>();

		final SeatInfoData accommodationInfoData1 = new SeatInfoData();
		final TotalFareData accommodation1Fare = new TotalFareData();
		final PriceData accommodation1Price = new PriceData();
		accommodation1Price.setValue(new BigDecimal(2.2));
		accommodation1Fare.setTotalPrice(accommodation1Price);
		accommodationInfoData1.setTotalFare(accommodation1Fare);
		accommodations.add(accommodationInfoData1);


		final SeatInfoData accommodationInfoData2 = new SeatInfoData();
		final TotalFareData accommodation2Fare = new TotalFareData();
		final PriceData accommodation2Price = new PriceData();
		accommodation2Price.setValue(new BigDecimal(2.4));
		accommodation2Fare.setTotalPrice(accommodation2Price);
		accommodationInfoData2.setTotalFare(accommodation2Fare);
		accommodations.add(accommodationInfoData2);

		final SeatInfoData accommodationInfoData3 = new SeatInfoData();
		final TotalFareData accommodation3Fare = new TotalFareData();
		final PriceData accommodation3Price = new PriceData();
		accommodation3Price.setValue(new BigDecimal(2.0));
		accommodation3Fare.setTotalPrice(accommodation3Price);
		accommodationInfoData3.setTotalFare(accommodation3Fare);
		accommodations.add(accommodationInfoData3);

		final SeatInfoData accommodationInfoData4 = new SeatInfoData();
		final TotalFareData accommodation4Fare = new TotalFareData();
		final PriceData accommodation4Price = new PriceData();
		accommodation4Price.setValue(new BigDecimal(2.3));
		accommodation4Fare.setTotalPrice(accommodation4Price);
		accommodationInfoData4.setTotalFare(accommodation4Fare);
		accommodations.add(accommodationInfoData4);

		final SeatInfoData accommodationInfoData5 = new SeatInfoData();
		final TotalFareData accommodation5Fare = new TotalFareData();
		final PriceData accommodation5Price = new PriceData();
		accommodation5Price.setValue(new BigDecimal(2.4));
		accommodation5Fare.setTotalPrice(accommodation5Price);
		accommodationInfoData5.setTotalFare(accommodation5Fare);
		accommodations.add(accommodationInfoData5);

		//before sorting list
		Assert.assertEquals(2.2d, accommodations.get(0).getTotalFare().getTotalPrice().getValue().doubleValue(), 0);

		Collections.sort(accommodations, accommodationInfoDataComparator);

		//after sorting list using AccommodationInfoDataComparator
		Assert.assertEquals(2.0d, accommodations.get(0).getTotalFare().getTotalPrice().getValue().doubleValue(), 0);
	}

}
