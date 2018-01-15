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

package de.hybris.platform.travelservices.dao.impl;

import de.hybris.platform.basecommerce.constants.BasecommerceConstants;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.storelocator.exception.PointOfServiceDaoException;
import de.hybris.platform.storelocator.impl.DefaultPointOfServiceDao;
import de.hybris.platform.storelocator.jalo.PointOfService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.dao.TravelPointOfServiceDao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;


public class DefaultTravelPointOfServiceDao extends DefaultPointOfServiceDao implements TravelPointOfServiceDao
{

	public static final String POS_TF_JOIN_QUERY = "SELECT {pos.pk} FROM {PointOfService AS pos JOIN TransportFacility As tf ON "
			+ "{pos:transportFacility} = {tf:pk}}";


	@Override
	public Collection<PointOfServiceModel> getGeocodedPOS(final int size) throws PointOfServiceDaoException
	{
		if (size < 0)
		{
			throw new PointOfServiceDaoException("Batch size must be positive number");
		}
		final String query =
				"SELECT {pos.PK} FROM {" + BasecommerceConstants.TC.POINTOFSERVICE + " as pos join Address as add on {pos."
						+ PointOfService.ADDRESS + "} = {add.pk}}";
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		final SearchResult<PointOfServiceModel> result = search(fQuery);
		final List<PointOfServiceModel> resultingPos = result.getResult();
		if (CollectionUtils.isEmpty(resultingPos))
		{
			return Collections.emptyList();
		}
		if (size == 0)
		{
			return resultingPos;
		}
		return resultingPos.size() <= size ? resultingPos : resultingPos.subList(0, size);
	}

	@Override
	public Collection<PointOfServiceModel> getGeocodedPOS() throws PointOfServiceDaoException
	{
		final Collection<PointOfServiceModel> allGeocodedPOS = getGeocodedPOS(0);
		if (CollectionUtils.isEmpty(allGeocodedPOS))
		{
			return Collections.emptyList();
		}
		return allGeocodedPOS;
	}

	@Override
	public Collection<PointOfServiceModel> getPointOfService(final Map<String, ? extends Object> filterParams)
	{
		final StringBuilder builder = new StringBuilder(POS_TF_JOIN_QUERY);
		Map<String, Object> queryParameters = null;
		if (MapUtils.isNotEmpty(filterParams))
		{
			queryParameters = appendWhereClausesToBuilder(builder, filterParams);
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder);
		if (MapUtils.isNotEmpty(queryParameters))
		{
			query.addQueryParameters(queryParameters);
		}
		final FlexibleSearchService fss = this.getFlexibleSearchService();
		final SearchResult searchResult = fss.search(query);
		return searchResult.getResult();
	}

	protected Map<String, Object> appendWhereClausesToBuilder(final StringBuilder builder,
			final Map<String, ? extends Object> filterParams)
	{
		builder.append(" WHERE ");
		boolean firstParam = true;
		final Map<String, Object> queryParameters = new HashMap<String, Object>();
		for (final Map.Entry<String, ? extends Object> entry : filterParams.entrySet())
		{
			final String filterName = entry.getKey();
			final Object filterValue = entry.getValue();
			if (firstParam)
			{
				firstParam = false;
			}
			else
			{
				builder.append(" AND ");
			}
			if (StringUtils.equals("transportFacility", filterName) && filterValue instanceof Collection)
			{
				builder.append("{tf.code} IN (");
				final List<TransportFacilityData> transportFacilityDataList = (List<TransportFacilityData>) filterValue;
				transportFacilityDataList
						.forEach(transportFacilityData -> builder.append("'").append(transportFacilityData.getCode()).append("',"));
				builder.deleteCharAt(builder.length() - 1);
				builder.append(")");
			}
			else
			{
				builder.append("{pos.").append(filterName).append("}= ?").append(filterName);
				queryParameters.put(filterName, filterValue);
			}
		}
		return queryParameters;
	}
}
