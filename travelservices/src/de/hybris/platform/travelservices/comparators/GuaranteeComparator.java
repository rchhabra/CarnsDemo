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

package de.hybris.platform.travelservices.comparators;

import de.hybris.platform.travelservices.enums.GuaranteeType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;

import java.util.Comparator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Guarantee comparator.
 */
public class GuaranteeComparator implements Comparator<GuaranteeModel>
{
	private Map<GuaranteeType, Integer> guaranteesPriorityMap;

	@Override
	public int compare(final GuaranteeModel guarantee1, final GuaranteeModel guarantee2)
	{
		return comparePriority(guarantee1.getType(), guarantee2.getType());
	}

	/**
	 * Compare priority int.
	 *
	 * @param guaranteeType1
	 * 		the guarantee type 1
	 * @param guaranteeType2
	 * 		the guarantee type 2
	 *
	 * @return the int
	 */
	protected int comparePriority(final GuaranteeType guaranteeType1, final GuaranteeType guaranteeType2)
	{
		return getGuaranteesPriorityMap().get(guaranteeType1).intValue() < getGuaranteesPriorityMap().get(guaranteeType2).intValue()
				? 1 : -1;
	}

	/**
	 * Gets guarantees priority map.
	 *
	 * @return the guaranteesPriorityMap
	 */
	protected Map<GuaranteeType, Integer> getGuaranteesPriorityMap()
	{
		return guaranteesPriorityMap;
	}

	/**
	 * Sets guarantees priority map.
	 *
	 * @param guaranteesPriorityMap
	 * 		the guaranteesPriorityMap to set
	 */
	@Required
	public void setGuaranteesPriorityMap(final Map<GuaranteeType, Integer> guaranteesPriorityMap)
	{
		this.guaranteesPriorityMap = guaranteesPriorityMap;
	}
}
