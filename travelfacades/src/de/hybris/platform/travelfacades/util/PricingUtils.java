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

package de.hybris.platform.travelfacades.util;

import de.hybris.platform.commercefacades.travel.DiscountData;
import de.hybris.platform.commercefacades.travel.FeeData;
import de.hybris.platform.commercefacades.travel.TaxData;

import java.math.BigDecimal;
import java.util.List;


/**
 * Utility class for pricing and taxing methods
 */
public class PricingUtils
{
	private PricingUtils()
	{
		//empty to avoid instantiating utils class
	}

	/**
	 * Method to get the total tax value from collection
	 *
	 * @param taxes
	 * 		the taxes
	 * @return total tax value
	 */
	public static BigDecimal getTotalTaxValue(final List<TaxData> taxes)
	{
		BigDecimal taxValue = BigDecimal.ZERO;
		for (final TaxData tax : taxes)
		{
			taxValue = taxValue.add(tax.getPrice().getValue());
		}
		return taxValue;
	}

	/**
	 * Method to get the total fee value from collection
	 *
	 * @param fees
	 * 		the fees
	 * @return total fee value
	 */
	public static BigDecimal getTotalFeesValue(final List<FeeData> fees)
	{
		BigDecimal feeValue = BigDecimal.ZERO;

		for (final FeeData fee : fees)
		{
			feeValue = feeValue.add(fee.getPrice().getValue());
		}
		return feeValue;
	}


	/**
	 * Method to get the total discount value from collection
	 *
	 * @param discounts
	 * 		the discounts
	 * @return total discount value
	 */
	public static BigDecimal getTotalDiscountValue(final List<DiscountData> discounts)
	{
		BigDecimal discountValue = BigDecimal.ZERO;
		for (final DiscountData discount : discounts)
		{
			discountValue = discountValue.add(discount.getPrice().getValue());
		}
		return discountValue;
	}

}
