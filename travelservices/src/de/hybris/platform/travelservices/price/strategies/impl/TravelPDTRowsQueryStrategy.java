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
package de.hybris.platform.travelservices.price.strategies.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder.QueryWithParams;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.jalo.TaxRow;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.price.TravelPDTRowsQueryBuilder;
import de.hybris.platform.travelservices.price.impl.DefaultTravelPDTRowsQueryBuilder;
import de.hybris.platform.travelservices.price.strategies.TravelPricingQueryStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Travel specific price row strategy to fetch the prices.
 *
 */
public class TravelPDTRowsQueryStrategy implements TravelPricingQueryStrategy
{
	private static final String CURRENCY = "Currency";
	private ConfigurationService configurationService;

	@Override
	public Collection<PriceRow> getPriceRows(final SessionContext ctx, final Product product, final EnumerationValue productGroup,
			final User user, final EnumerationValue userGroup)
	{
		final Map<String, String> searchParams = ctx.getAttribute(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP);
		final TravelPDTRowsQueryBuilder builder = createQueryBuilder(ctx, product, productGroup, user, userGroup);
		final boolean addCurrencyFilter = getConfigurationService().getConfiguration()
				.getBoolean(TravelservicesConstants.PRODUCT_PRICE_ROW_ADD_CURRENCY_FILTER);

		if (searchParams != null && addCurrencyFilter)
		{
			searchParams.put(CURRENCY, String.valueOf(ctx.getCurrency().getPK().getLongValue()));
		}
		final QueryWithParams queryAndParams = builder.buildPriceQueryAndParams(searchParams);
		final Collection<PriceRow> priceRowsList = FlexibleSearch.getInstance()
				.search(ctx, queryAndParams.getQuery(), queryAndParams.getParams(), PriceRow.class).getResult();
		ctx.removeAttribute(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP);
		return (priceRowsList == null) ? Collections.emptyList() : new ArrayList<PriceRow>(priceRowsList);
	}

	protected TravelPDTRowsQueryBuilder createQueryBuilder(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{
		final TravelPDTRowsQueryBuilder builder = getTravelPDTRowsQueryBuilderFor(Europe1Constants.TC.PRICEROW);
		final PK productPk = product == null ? null : product.getPK();
		final PK productGroupPk = productGroup == null ? null : productGroup.getPK();
		final PK userPk = user == null ? null : user.getPK();
		final PK userGroupPk = userGroup == null ? null : userGroup.getPK();
		final String productId = this.extractProductId(ctx, product);
		builder.withAnyProduct().withAnyUser().withProduct(productPk).withProductId(productId).withProductGroup(productGroupPk)
				.withUser(userPk).withUserGroup(userGroupPk);
		return builder;
	}

	protected String extractProductId(final SessionContext ctx, final Product product)
	{
		final String idFromContext = (String) (ctx != null ? ctx.getAttribute("productId") : null);
		if (idFromContext != null)
		{
			return idFromContext;
		}
		return this.extractProductId(product);
	}

	protected String extractProductId(final Product product)
	{
		return product == null ? null : product.getCode();
	}

	protected TravelPDTRowsQueryBuilder getTravelPDTRowsQueryBuilderFor(final String type)
	{
		return new DefaultTravelPDTRowsQueryBuilder(type);
	}

	@Override
	public Collection<TaxRow> getTaxRows(final SessionContext ctx, final Product product, final EnumerationValue productGroup,
			final User user, final EnumerationValue userGroup)
	{
		final Map<String, List<String>> searchParams = ctx.getAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP);
		if (searchParams == null)
		{
			return Collections.emptyList();
		}

		final String taxRowTypeCode = TypeManager.getInstance().getComposedType(TaxRow.class).getCode();

		final PK productPk = product == null ? null : product.getPK();
		final PK productGroupPk = productGroup == null ? null : productGroup.getPK();
		final PK userPk = user == null ? null : user.getPK();
		final PK userGroupPk = userGroup == null ? null : userGroup.getPK();
		final String productId = extractProductId(ctx, product);

		final TravelPDTRowsQueryBuilder builder = getTravelPDTRowsQueryBuilderFor(taxRowTypeCode);
		builder.withAnyUser().withProduct(productPk).withProductGroup(productGroupPk).withProductId(productId).withUser(userPk)
				.withUserGroup(userGroupPk);

		final QueryWithParams queryAndParams = builder.buildTaxQueryAndParams(searchParams);
		final Collection<TaxRow> taxRows = FlexibleSearch.getInstance()
				.search(ctx, queryAndParams.getQuery(), queryAndParams.getParams(), TaxRow.class).getResult();
		ctx.removeAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP);
		return taxRows;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
