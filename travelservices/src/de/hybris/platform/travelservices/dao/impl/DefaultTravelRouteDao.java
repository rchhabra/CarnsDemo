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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.TravelRouteDao;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link TravelRouteDao}
 */
public class DefaultTravelRouteDao extends DefaultGenericDao<TravelRouteModel> implements TravelRouteDao
{
	/**
	 * @param typecode
	 */
	public DefaultTravelRouteDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<TravelRouteModel> findTravelRoutes(final String origin, final String destination)
	{
		validateParameterNotNull(origin, "Parameter origin cannot be null");
		validateParameterNotNull(destination, "Parameter destination cannot be null");

		final StringBuilder sb = new StringBuilder();

		sb.append("SELECT {tr.pk} FROM { ").append(TravelRouteModel._TYPECODE).append(" AS tr JOIN ")
				.append(TransportFacilityModel._TYPECODE).append(" AS tfo ON {tr.").append(TravelRouteModel.ORIGIN).append("}={tfo.")
				.append(TransportFacilityModel.PK).append("} join ").append(TransportFacilityModel._TYPECODE).append(" AS tfd ON ")
				.append("{tr.").append(TravelRouteModel.DESTINATION).append("}={tfd.").append(TransportFacilityModel.PK)
				.append("}} WHERE {tfo.").append(TransportFacilityModel.CODE).append("} = ?originCode").append(" AND {tfd.")
				.append(TransportFacilityModel.CODE).append("} = ?destinationCode");

		final Map<String, Object> params = new HashMap<>();
		params.put("originCode", origin);
		params.put("destinationCode", destination);

		final SearchResult<TravelRouteModel> searchResult = getFlexibleSearchService().search(sb.toString(), params);

		return searchResult.getResult();
	}

	@Override
	public TravelRouteModel findTravelRoute(final String routeCode)
	{
		validateParameterNotNull(routeCode, "Travel Route Code must not be null!");

		final List<TravelRouteModel> travelRoutes = find(Collections.singletonMap(TravelRouteModel.CODE, (Object) routeCode));

		if (CollectionUtils.isEmpty(travelRoutes))
		{
			throw new ModelNotFoundException("No result for the given query");
		}
		else if (travelRoutes.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found " + travelRoutes.size() + " results for the given query");
		}
		return travelRoutes.get(0);
	}
}
