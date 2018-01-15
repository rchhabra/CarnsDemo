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
package de.hybris.platform.travelservices.jalo;

import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.jalo.TaxRow;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.price.strategies.TravelPricingQueryStrategy;
import de.hybris.platform.util.DateRange;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Travel price factory for querying price rows and taxes for travel specific search criteria.
 *
 * The Europe1PriceFactory doesn't support changing the query patters or filtering and is designed to work only for
 * hybris Products. The Travel accelerator extends the price factory class to provide additional methods required for
 * travel sector.
 */
public class TravelEurope1PriceFactory extends Europe1PriceFactory implements TravelPriceFactory
{
	private TimeService timeService;
	private transient TravelPricingQueryStrategy pricingQueryStrategy;

	/**
	 * @return timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 *
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected TravelPricingQueryStrategy getPricingQueryStrategy()
	{
		return pricingQueryStrategy;
	}

	/**
	 * @param pricingQueryStrategy
	 */
	public void setPricingQueryStrategy(final TravelPricingQueryStrategy pricingQueryStrategy)
	{
		this.pricingQueryStrategy = pricingQueryStrategy;
	}

	@Override
	public List<PriceInformation> getProductPriceInformations(final Product product, final Map<String, String> searchCriteria)
			throws JaloPriceFactoryException
	{
		final SessionContext ctx = getSession().getSessionContext();
		ctx.setAttribute(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP, searchCriteria);
		final User user = ctx.getUser();
		final Currency currency = ctx.getCurrency();
		final EnumerationValue ppg = getPPG(ctx, product);
		final EnumerationValue upg = getUPG(ctx, user);

		return getPriceInformations(ctx, product, ppg, ctx.getUser(), upg, currency, true, getTimeService().getCurrentTime(), null);
	}

	/**
	 * Runs the query to retrieve prices rows for product and travel search criteria. If there are no price rows
	 * returned, the default query to fetch the price rows is being invoked. Please note that this method may return more
	 * rows than allowed so make sure they're filtered afterwards!
	 *
	 * @param ctx
	 * @param product
	 *           the product to get prices for
	 * @param productGroup
	 *           the product group to get prices for
	 * @param user
	 *           the user to get prices for
	 * @param userGroup
	 *           the user price group to get prices for
	 * @throws JaloPriceFactoryException
	 */
	@Override
	protected Collection<PriceRow> queryPriceRows4Price(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{
		return getPricingQueryStrategy().getPriceRows(ctx, product, productGroup, user, userGroup);
	}

	/**
	 * Method overridden to set order currency in session context. This currency will be used when filtering tax rows
	 * based on currency. Please note that hybris OOTB code for taxes doesn't handle filtering tax rows based on order
	 * currency (price rows implementation handles this though).
	 *
	 * @param entry
	 * @return collection of TaxValues
	 */
	@Override
	public Collection<TaxValue> getTaxValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException
	{
		final SessionContext ctx = this.getSession().getSessionContext();
		ctx.setCurrency(entry.getOrder().getCurrency());
		return super.getTaxValues(entry);
	}

	/**
	 * Runs the query to retrieve tax rows for travel search criteria.
	 *
	 * @param ctx
	 * @param product
	 *           the product to get prices for
	 * @param productGroup
	 *           the product group to get prices for
	 * @param user
	 *           the user to get prices for
	 * @param userGroup
	 *           the user price group to get prices for
	 */
	@Override
	protected Collection<TaxRow> superQueryTax4Price(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{
		final Map<String, List<String>> searchParams = ctx.getAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP);
		if (searchParams == null)
		{
			return super.superQueryTax4Price(ctx, product, productGroup, user, userGroup);
		}
		return getPricingQueryStrategy().getTaxRows(ctx, product, productGroup, user, userGroup);
	}

	/**
	 * Filters tax rows which do not match the given parameters.
	 *
	 * This method has been Overridden to provide additional logic to filter on Currency as well as DateRange as
	 * filtering on Currency is not provided in the out of the box functionality. We require this additional filter as we
	 * are getting TaxRows for all Currencies and we are only interested in the TaxRows for the selected Currency.
	 *
	 * @param rows
	 *           the query result rows
	 * @param date
	 *           the requested date
	 */
	@Override
	protected List<TaxRow> filterTaxRows4Price(final Collection<TaxRow> rows, final Date date)
	{
		if (rows.isEmpty())
		{
			return Collections.emptyList();
		}
		else
		{
			final SessionContext ctx = getSession().getSessionContext();
			final List<TaxRow> ret = new ArrayList<TaxRow>(rows);

			for (final ListIterator<TaxRow> it = ret.listIterator(); it.hasNext();)
			{
				final TaxRow taxRow = it.next();
				if (taxRow.isAbsolute(ctx) && !StringUtils.equals(taxRow.getCurrency().getIsoCode(), ctx.getCurrency().getIsoCode()))
				{
					it.remove();
					// skip if currency does not match
					continue;
				}
				final DateRange dateRange = taxRow.getDateRange();
				if (dateRange != null && !dateRange.encloses(date))
				{
					it.remove();
					// skip if date range does not match
				}
			}
			return ret;
		}
	}

}
