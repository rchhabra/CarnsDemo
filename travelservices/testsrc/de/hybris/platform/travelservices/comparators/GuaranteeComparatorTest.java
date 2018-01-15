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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.travelservices.enums.GuaranteeType;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link GuaranteeComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GuaranteeComparatorTest
{
	@InjectMocks
	GuaranteeComparator guaranteeComparator;
	private Map<GuaranteeType, Integer> guaranteesPriorityMap;
	@Test
	public void testCompare()
	{
		guaranteesPriorityMap = new HashMap<>();
		guaranteesPriorityMap.put(GuaranteeType.PREPAYMENT, 1);
		guaranteesPriorityMap.put(GuaranteeType.DEPOSIT, 2);
		guaranteesPriorityMap.put(GuaranteeType.GUARANTEE, 3);
		guaranteeComparator.setGuaranteesPriorityMap(guaranteesPriorityMap);
		final GuaranteeModel guarantee1 = new GuaranteeModel();
		guarantee1.setType(GuaranteeType.PREPAYMENT);
		final GuaranteeModel guarantee2 = new GuaranteeModel();
		guarantee2.setType(GuaranteeType.DEPOSIT);

		Assert.assertEquals(1, guaranteeComparator.compare(guarantee1, guarantee2));
		guarantee1.setType(GuaranteeType.GUARANTEE);
		Assert.assertEquals(-1, guaranteeComparator.compare(guarantee1, guarantee2));
	}
}
