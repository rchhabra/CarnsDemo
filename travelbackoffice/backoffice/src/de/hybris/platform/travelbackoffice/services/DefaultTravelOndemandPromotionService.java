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

package de.hybris.platform.travelbackoffice.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.impl.DefaultOndemandPromotionService;

import java.math.BigDecimal;
import java.math.RoundingMode;


/*
 *This class extends the functionality of @link={DefaultOndemandPromotionService}.
 *It is created to handle the tax calculation scenario for only the entries with quantity=0.
 */
public class DefaultTravelOndemandPromotionService extends DefaultOndemandPromotionService
{
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
	private static final RoundingMode FINAL_ROUNDING_MODE = RoundingMode.DOWN;


	/*
	 * This method is overridden to prevent throwing exception in case of entries with quantity = 0.
	 */
	@Override
	protected OndemandDiscountedOrderEntry createOndemandDiscountedOrderEntry(final AbstractOrderEntryModel abstractOrderEntry,
			final BigDecimal calculatedOrderLineDiscount)
	{
		final BigDecimal orderLineDiscount = calculatedOrderLineDiscount == null ? BigDecimal.ZERO : calculatedOrderLineDiscount;
		BigDecimal unitTotal = BigDecimal.valueOf(abstractOrderEntry.getTotalPrice().doubleValue()).setScale(2, ROUNDING_MODE)
				.subtract(orderLineDiscount).divide(
						BigDecimal.valueOf(abstractOrderEntry.getQuantity() > 0 ? abstractOrderEntry.getQuantity().doubleValue() : 1d),
						2, FINAL_ROUNDING_MODE);

		if (Boolean.FALSE.equals(abstractOrderEntry.getOrder().getNet()))
		{
			unitTotal = unitTotal.subtract(getTaxCalculationService().calculatePreciseUnitTax(abstractOrderEntry.getTaxValues(),
					abstractOrderEntry.getQuantity().doubleValue(), abstractOrderEntry.getOrder().getNet().booleanValue()));
		}
		return new OndemandDiscountedOrderEntry(abstractOrderEntry,
				unitTotal.multiply(BigDecimal.valueOf(abstractOrderEntry.getQuantity().doubleValue())).setScale(2, ROUNDING_MODE),
				unitTotal, orderLineDiscount);
	}

}
