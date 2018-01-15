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

package de.hybris.platform.travelservices.ancillary.search.accommodation.comparator;

import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;

import java.util.Comparator;


/**
 * Compares configured accommodation(deck, cabin, rows etc.) to sort them based on lowest "number"
 */
public class ConfiguredAccomNumberComparator implements Comparator<ConfiguredAccommodationModel>
{

	@Override
	public int compare(final ConfiguredAccommodationModel rowCol1, final ConfiguredAccommodationModel rowCol2)
	{
		final Integer rowColNum1 = rowCol1.getNumber();
		final Integer rowColNum2 = rowCol2.getNumber();
		if (rowColNum1 > rowColNum2)
		{
			return 1;
		}
		else if (rowColNum1 < rowColNum2)
		{
			return -1;
		}
		return 0;
	}

}
