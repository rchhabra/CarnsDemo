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

package de.hybris.platform.travelservices.utils;

import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;


/**
 * Custom enum class to represent the hierarchy of ConfiguredAccommodationTypes
 */

public enum AccommodationEnum
{
	DECK(1), CABIN(2), ROW(3), COLUMN(4), SEAT(5);

	private final int level;

	private AccommodationEnum(final int level)
	{
		this.level = level;
	}

	/**
	 * This method returns the position of the AccommodationEnum in hierarchy.
	 *
	 * @return integer.
	 */
	protected int getLevel()
	{
		return level;
	}

	/**
	 * This method maps ConfiguredAccommodationType to AccommodationEnum
	 *
	 * @param enumValue
	 *           ConfiguredAccommodationType
	 * @return AccommodationEnum
	 */
	public static AccommodationEnum mapConfiguredAccommodationTypeToAccommodationEnum(final ConfiguredAccommodationType enumValue)
	{
		switch (enumValue)
		{
			case DECK:
				return DECK;
			case CABIN:
				return CABIN;
			case ROW:
				return ROW;
			case COLUMN:
				return COLUMN;
			case SEAT:
				return SEAT;
			default:
				return null;
		}
	}

	/**
	 * This method compares two AccommodationEnum and returns true if they are in correct hierarchy.
	 *
	 * @param previousAccomodation
	 * @param currentAccomodation
	 * @return
	 */
	public static boolean areAccommodationsInCorrectHierarchy(final AccommodationEnum previousAccomodation,
			final AccommodationEnum currentAccomodation)
	{
		return previousAccomodation.getLevel() <= currentAccomodation.getLevel() ? true : false;
	}

}
