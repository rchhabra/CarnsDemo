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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;

import java.util.Comparator;


/**
 * Compares seatInfoData for sorting them starting from lowest price
 */
public class AccommodationInfoDataComparator implements Comparator<SeatInfoData>
{

	@Override
	public int compare(final SeatInfoData seatInfo1, final SeatInfoData seatInfo2)
	{
		double seatPrice1 = 0.0d;
		double seatPrice2 = 0.0d;
		if (seatInfo1 != null)
		{
			final TotalFareData seatFareData = seatInfo1.getTotalFare();
			if (seatFareData != null)
			{
				final PriceData totalPrice = seatFareData.getTotalPrice();
				if (totalPrice != null && totalPrice.getValue() != null)
				{
					seatPrice1 = totalPrice.getValue().doubleValue();
				}
			}
		}
		if (seatInfo2 != null)
		{
			final TotalFareData seatFareData = seatInfo2.getTotalFare();
			if (seatFareData != null)
			{
				final PriceData totalPrice = seatFareData.getTotalPrice();
				if (totalPrice != null && totalPrice.getValue() != null)
				{
					seatPrice2 = totalPrice.getValue().doubleValue();
				}
			}
		}
		if (seatPrice1 > seatPrice2)
		{
			return 1;
		}
		else if (seatPrice1 < seatPrice2)
		{
			return -1;
		}
		return 0;
	}

}
