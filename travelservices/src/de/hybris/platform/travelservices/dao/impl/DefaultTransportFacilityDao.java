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

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.TransportFacilityDao;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link TransportFacilityDao}
 */
public class DefaultTransportFacilityDao extends DefaultGenericDao<TransportFacilityModel> implements TransportFacilityDao
{

	/**
	 * Instantiates a new Default transport facility dao.
	 *
	 * @param typecode
	 *           the typecode
	 */
	public DefaultTransportFacilityDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public TransportFacilityModel findTransportFacility(final String code)
	{
		validateParameterNotNull(code, "code must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportFacilityModel.CODE, code);
		final List<TransportFacilityModel> transportFacilityModel = find(params);

		return CollectionUtils.isNotEmpty(transportFacilityModel) ? transportFacilityModel.get(0) : null;
	}
}
