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

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.commons.services.impl.DefaultOndemandTaxCalculationService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;


/*
 *This class extends the functionality of @link={DefaultOndemandTaxCalculationService}.
 *It is created to handle the tax calculation scenario for only the entries with quantity = 0.
 */
public class DefaultTravelOndemandTaxCalculationService extends DefaultOndemandTaxCalculationService
{
	private static int ROUNDING_SCALE = 2;
	private static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
	private static RoundingMode FINAL_ROUNDING_MODE = RoundingMode.DOWN;

	/*
	 * This method is overridden to prevent adding the taxes at the order level to total tax calculated from the
	 * individual Entries.
	 */
	@Override
	public BigDecimal calculateTotalTax(final AbstractOrderModel abstractOrder)
	{
		if ((abstractOrder == null) || (abstractOrder.getEntries() == null))
		{
			return BigDecimal.ZERO;
		}

		final BigDecimal totalTax = Boolean.TRUE.equals(abstractOrder.getNet()) ? getOrderEntriesTotalTax(abstractOrder)
				: getOrderEntriesTotalTax(abstractOrder).add(calculateShippingTax(abstractOrder));

		return totalTax.setScale(ROUNDING_SCALE, FINAL_ROUNDING_MODE);
	}

	/*
	 * This method is overridden to prevent throwing exception in case of entries with quantity = 0 or tax values = null.
	 */
	@Override
	public BigDecimal calculatePreciseUnitTax(final Collection<TaxValue> taxValues, final double quantity, final boolean isNet)
	{
		if ((quantity < 1.0D) || (taxValues == null))
		{
			return BigDecimal.valueOf(0);
		}

		final BigDecimal taxTotals = BigDecimal
				.valueOf(isNet ? TaxValue.sumAbsoluteTaxValues(taxValues) : TaxValue.sumAppliedTaxValues(taxValues))
				.setScale(ROUNDING_SCALE, ROUNDING_MODE);

		return taxTotals.divide(BigDecimal.valueOf(quantity), ROUNDING_SCALE, FINAL_ROUNDING_MODE);
	}
}
