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
package de.hybris.platform.travelservices.price.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.price.TravelPDTRowsQueryBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;


/**
 * Implementation class similar to DefaultPDTRowsQueryBuilder. This has all the methods and variables from
 * DefaultPDTRowsQueryBuilder and also includes travel specific methods.
 */
public class DefaultTravelPDTRowsQueryBuilder implements TravelPDTRowsQueryBuilder
{
	private String type = null;
	private boolean anyProduct;
	private PK productPk;
	private PK productGroupPk;
	private String productId;
	private boolean anyUser;
	private PK userPk;
	private PK userGroupPk;

	/**
	 * @param type
	 */
	public DefaultTravelPDTRowsQueryBuilder(final String type)
	{
		this.type = Objects.requireNonNull(type);
	}

	@Override
	public TravelPDTRowsQueryBuilder withAnyProduct()
	{
		this.anyProduct = true;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder withProduct(final PK productPk)
	{
		this.productPk = productPk;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder withProductGroup(final PK productGroupPk)
	{
		this.productGroupPk = productGroupPk;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder withProductId(final String productId)
	{
		this.productId = productId;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder withAnyUser()
	{
		this.anyUser = true;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder withUser(final PK userPk)
	{
		this.userPk = userPk;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder withUserGroup(final PK userGroupPk)
	{
		this.userGroupPk = userGroupPk;
		return this;
	}

	@Override
	public TravelPDTRowsQueryBuilder.QueryWithParams build()
	{
		boolean matchByUser;
		final StringBuilder query = new StringBuilder();
		final ImmutableMap.Builder params = ImmutableMap.builder();
		final Map<String, Object> productParams = this.getProductRelatedParameters();
		final Map<String, Object> userParams = this.getUserRelatedParameters();
		final boolean addPricesByProductId = this.productId != null;
		boolean isUnion = false;
		final boolean matchByProduct = !productParams.isEmpty();
		matchByUser = !userParams.isEmpty();
		if (!(matchByProduct || matchByUser || addPricesByProductId))
		{
			return new TravelPDTRowsQueryBuilder.QueryWithParams("select {PK} from {" + this.type + "}", Collections.emptyMap());
		}
		if (matchByProduct || matchByUser)
		{
			query.append("select {PK} from {").append(this.type).append("} where ");
			if (matchByProduct)
			{
				query.append("{").append("productMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(productParams.keySet())).append(")");
				params.putAll(productParams);
				if (matchByUser)
				{
					query.append(" and ");
				}
			}
			if (matchByUser)
			{
				query.append("{").append("userMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
				params.putAll(userParams);
			}
		}
		if (addPricesByProductId)
		{
			if (matchByProduct || matchByUser)
			{
				query.append("}} UNION {{");
				isUnion = true;
			}
			query.append("select {PK} from {").append(this.type).append("} where {");
			query.append("productMatchQualifier").append("}=?matchByProductId and {");
			query.append("productId").append("}=?").append("productId");
			params.put("matchByProductId", Europe1PriceFactory.MATCH_BY_PRODUCT_ID);
			params.put("productId", this.productId);
			if (matchByUser)
			{
				query.append(" and {").append("userMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
			}
		}
		final StringBuilder resultQuery = isUnion ? new StringBuilder("select x.PK from ({{").append(query).append("}}) x") : query;
		return new TravelPDTRowsQueryBuilder.QueryWithParams(resultQuery.toString(), params.build());
	}

	private Map<String, Object> getProductRelatedParameters()
	{
		final ImmutableMap.Builder params = ImmutableMap.builder();
		if (this.anyProduct)
		{
			params.put("anyProduct", Europe1PriceFactory.MATCH_ANY);
		}
		if (this.productPk != null)
		{
			params.put("product", this.productPk.getLong());
		}
		if (this.productGroupPk != null)
		{
			params.put("productGroup", this.productGroupPk.getLong());
		}
		return params.build();
	}

	private Map<String, Object> getUserRelatedParameters()
	{
		final ImmutableMap.Builder params = ImmutableMap.builder();
		if (this.anyUser)
		{
			params.put("anyUser", Europe1PriceFactory.MATCH_ANY);
		}
		if (this.userPk != null)
		{
			params.put("user", this.userPk.getLong());
		}
		if (this.userGroupPk != null)
		{
			params.put("userGroup", this.userGroupPk.getLong());
		}
		return params.build();
	}

	@Override
	public QueryWithParams buildPriceQueryAndParams(final Map<String, String> searchParams)
	{
		boolean matchByUser;
		final StringBuilder query = new StringBuilder();
		final ImmutableMap.Builder params = ImmutableMap.builder();
		final Map<String, Object> productParams = this.getProductRelatedParameters();
		final Map<String, Object> userParams = this.getUserRelatedParameters();
		final boolean addPricesByProductId = this.productId != null;
		boolean isUnion = false;
		final boolean matchByProduct = !productParams.isEmpty();
		matchByUser = !userParams.isEmpty();
		if (!(matchByProduct || matchByUser || addPricesByProductId))
		{
			return new TravelPDTRowsQueryBuilder.QueryWithParams("select {PK} from {" + this.type + "}", Collections.emptyMap());
		}
		if (matchByProduct || matchByUser)
		{
			buildQueryMatchByProductOrUser(searchParams, matchByUser, query, params, productParams, userParams, matchByProduct);
		}
		if (addPricesByProductId)
		{
			if (matchByProduct || matchByUser)
			{
				query.append("}} UNION {{");
				isUnion = true;
			}
			buildQueryAddPricesByProduct(searchParams, matchByUser, query, params, userParams);
		}
		if (MapUtils.isNotEmpty(searchParams))
		{
			for (final Map.Entry<String, String> searchParamEntry : searchParams.entrySet())
			{
				params.put(searchParamEntry.getKey(), (searchParamEntry.getValue() == null) ? "" : searchParamEntry.getValue());
			}
		}
		final StringBuilder resultQuery = isUnion ? new StringBuilder("select x.PK from ({{").append(query).append("}}) x") : query;
		return new TravelPDTRowsQueryBuilder.QueryWithParams(resultQuery.toString(), params.build());
	}

	protected void buildQueryAddPricesByProduct(final Map<String, String> searchParams, final boolean matchByUser,
			final StringBuilder query, final ImmutableMap.Builder params, final Map<String, Object> userParams)
	{
		query.append("select {PK} from {").append(this.type).append("} where {");
		query.append("productMatchQualifier").append("}=?matchByProductId and {");
		query.append("productId").append("}=?").append("productId");
		params.put("matchByProductId", Europe1PriceFactory.MATCH_BY_PRODUCT_ID);
		params.put("productId", this.productId);
		if (matchByUser)
		{
			query.append(" and {").append("userMatchQualifier").append("} in (?");
			query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
		}
		appendQueryForPriceRow(query, searchParams);
	}

	protected void buildQueryMatchByProductOrUser(final Map<String, String> searchParams, final boolean matchByUser,
			final StringBuilder query, final ImmutableMap.Builder params, final Map<String, Object> productParams,
			final Map<String, Object> userParams, final boolean matchByProduct)
	{
		query.append("select {PK} from {").append(this.type).append("} where ");
		if (matchByProduct)
		{
			query.append("{").append("productMatchQualifier").append("} in (?");
			query.append(Joiner.on(", ?").join(productParams.keySet())).append(")");
			params.putAll(productParams);
			if (matchByUser)
			{
				query.append(" and ");
			}
		}
		if (matchByUser)
		{
			query.append("{").append("userMatchQualifier").append("} in (?");
			query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
			params.putAll(userParams);
		}
		appendQueryForPriceRow(query, searchParams);
	}

	@Override
	public QueryWithParams buildTaxQueryAndParams(final Map<String, List<String>> searchParams)
	{
		boolean matchByUser;
		final StringBuilder query = new StringBuilder();
		final ImmutableMap.Builder params = ImmutableMap.builder();
		final Map<String, Object> productParams = this.getProductRelatedParameters();
		final Map<String, Object> userParams = this.getUserRelatedParameters();
		final boolean addPricesByProductId = this.productId != null;
		boolean isUnion = false;
		final boolean matchByProduct = !productParams.isEmpty();
		matchByUser = !userParams.isEmpty();
		if (!(matchByProduct || matchByUser || addPricesByProductId))
		{
			return new TravelPDTRowsQueryBuilder.QueryWithParams("select {PK} from {" + this.type + "}", Collections.emptyMap());
		}
		final Map<String, Object> travelParams = new HashMap<String, Object>();
		if (matchByProduct || matchByUser)
		{
			query.append("select {PK} from {").append(this.type).append("} where ");
			if (matchByProduct)
			{
				query.append("{").append("productMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(productParams.keySet())).append(")");
				params.putAll(productParams);
				if (matchByUser)
				{
					query.append(" and ");
				}
			}
			if (matchByUser)
			{
				query.append("{").append("userMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
				params.putAll(userParams);
			}
			appendQueryForTaxRow(query, searchParams, travelParams);
		}
		if (addPricesByProductId)
		{
			if (matchByProduct || matchByUser)
			{
				query.append("}} UNION {{");
				isUnion = true;
			}
			query.append("select {PK} from {").append(this.type).append("} where {");
			query.append("productMatchQualifier").append("}=?matchByProductId and {");
			query.append("productId").append("}=?").append("productId");
			params.put("matchByProductId", Europe1PriceFactory.MATCH_BY_PRODUCT_ID);
			params.put("productId", this.productId);
			if (matchByUser)
			{
				query.append(" and {").append("userMatchQualifier").append("} in (?");
				query.append(Joiner.on(", ?").join(userParams.keySet())).append(")");
			}
			appendQueryForTaxRow(query, searchParams, travelParams);
		}
		params.putAll(travelParams);
		final StringBuilder resultQuery = isUnion ? new StringBuilder("select x.PK from ({{").append(query).append("}}) x") : query;
		return new TravelPDTRowsQueryBuilder.QueryWithParams(resultQuery.toString(), params.build());
	}

	protected void appendQueryForPriceRow(final StringBuilder query, final Map<String, String> params)
	{
		if (MapUtils.isEmpty(params))
		{
			query.append(" and {travelRouteCode} is null and {travelSectorCode} is null and {transportOfferingCode} is null");
		}
		else
		{
			for (final Map.Entry<String, String> paramEntry : params.entrySet())
			{
				query.append(" and {").append(paramEntry.getKey()).append("} = ?" + paramEntry.getKey());
			}
		}
	}

	protected void appendQueryForTaxRow(final StringBuilder query, final Map<String, List<String>> params,
			final Map<String, Object> queryParams)
	{
		final List<String> transportFacilityCodes = params.get(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY);
		final List<String> originCountryCodes = params.get(TravelservicesConstants.SEARCH_ORIGINCOUNTRY);

		if (transportFacilityCodes.isEmpty() && originCountryCodes.isEmpty())
		{
			query.append(" and {" + TaxRowModel.ORIGINCOUNTRY + "} is null and {" + TaxRowModel.ORIGINTRANSPORTFACILITY
					+ "} is null and {" + TaxRowModel.PASSENGERTYPE + "} is null");
			return;
		}
		query.append(" and (");
		appendQueryForCountryAndPassengerType(query, params, queryParams);
		if (!transportFacilityCodes.isEmpty())
		{
			if (!originCountryCodes.isEmpty())
			{
				query.append(" OR ");
			}
			appendQueryForTransportFacilityAndPassengerType(query, params, queryParams);
		}
		query.append(")");
	}

	protected void appendQueryForCountryAndPassengerType(final StringBuilder query, final Map<String, List<String>> params,
			final Map<String, Object> queryParams)
	{
		final List<String> originCountryCodes = params.get(TravelservicesConstants.SEARCH_ORIGINCOUNTRY);
		if (originCountryCodes.isEmpty())
		{
			return;
		}
		query.append("(");
		final List<String> passengerTypes = params.get(TravelservicesConstants.SEARCH_PASSENGERTYPE);
		if (passengerTypes.isEmpty())
		{
			query.append("(");
			query.append(getQueryForCountry(originCountryCodes, queryParams));
			query.append(getQueryForPassengerType("", queryParams));
			query.append(")");
		}
		else
		{
			for (int i = 0; i < passengerTypes.size(); i++)
			{
				final String passengerType = passengerTypes.get(i);
				if (i > 0)
				{
					query.append(" OR ");
				}
				query.append("(");
				query.append(getQueryForCountry(originCountryCodes, queryParams));
				query.append(getQueryForPassengerType(passengerType, queryParams));
				query.append(")");
			}
		}
		query.append(")");
	}

	protected void appendQueryForTransportFacilityAndPassengerType(final StringBuilder query,
			final Map<String, List<String>> params, final Map<String, Object> queryParams)
	{
		final List<String> transportFacilityCodes = params.get(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY);
		if (transportFacilityCodes.isEmpty())
		{
			return;
		}
		query.append("(");
		final List<String> passengerTypes = params.get(TravelservicesConstants.SEARCH_PASSENGERTYPE);
		if (passengerTypes.isEmpty())
		{
			query.append("(");
			query.append(getQueryForTransportFacility(transportFacilityCodes, queryParams));
			query.append(getQueryForPassengerType("", queryParams));
			query.append(")");
		}
		else
		{
			for (int i = 0; i < passengerTypes.size(); i++)
			{
				final String passengerType = passengerTypes.get(i);
				if (i > 0)
				{
					query.append(" OR ");
				}
				query.append("(");
				query.append(getQueryForTransportFacility(transportFacilityCodes, queryParams));
				query.append(getQueryForPassengerType(passengerType, queryParams));
				query.append(")");
			}
		}
		query.append(")");
	}

	protected String getQueryForTransportFacility(final List<String> transportFacilityCodes, final Map<String, Object> queryParams)
	{
		if (transportFacilityCodes.size() > 1)
		{
			final StringBuilder queryKey = new StringBuilder();
			for (int i = 0; i < transportFacilityCodes.size(); i++)
			{
				final String key = "originTransportFacility" + i;
				queryParams.put(key, transportFacilityCodes.get(i));
				if (i > 0)
				{
					queryKey.append(",");
				}
				queryKey.append("?" + key);
			}
			return "{" + TaxRowModel.ORIGINTRANSPORTFACILITY + "} IN (" + queryKey.toString() + ")";
		}
		else
		{
			queryParams.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, transportFacilityCodes.get(0));
			return "{" + TaxRowModel.ORIGINTRANSPORTFACILITY + "} = ?" + TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY;
		}
	}

	protected String getQueryForCountry(final List<String> countryCodes, final Map<String, Object> queryParams)
	{
		if (countryCodes.size() > 1)
		{
			final StringBuilder queryKey = new StringBuilder();
			for (int i = 0; i < countryCodes.size(); i++)
			{
				final String key = "originCountry" + i;

				if (!queryParams.containsKey(key))
				{
					queryParams.put(key, countryCodes.get(i));
				}

				if (i > 0)
				{
					queryKey.append(",");
				}
				queryKey.append("?" + key);
			}
			return "{" + TaxRowModel.ORIGINCOUNTRY + "} IN (" + queryKey.toString() + ")";
		}
		else
		{
			if (!queryParams.containsKey(TravelservicesConstants.SEARCH_ORIGINCOUNTRY))
			{
				queryParams.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, countryCodes.get(0));
			}
			return "{" + TaxRowModel.ORIGINCOUNTRY + "} = ?" + TravelservicesConstants.SEARCH_ORIGINCOUNTRY;
		}
	}


	protected String getQueryForPassengerType(final String passengerType, final Map<String, Object> queryParams)
	{
		if (passengerType.isEmpty())
		{
			return " and {" + TravelservicesConstants.SEARCH_PASSENGERTYPE + "} is null";
		}
		else
		{
			if (!queryParams.containsKey(TravelservicesConstants.SEARCH_PASSENGERTYPE))
			{
				queryParams.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, passengerType);
			}
			return " and {" + TravelservicesConstants.SEARCH_PASSENGERTYPE + "} = ?" + TravelservicesConstants.SEARCH_PASSENGERTYPE;
		}
	}
}
