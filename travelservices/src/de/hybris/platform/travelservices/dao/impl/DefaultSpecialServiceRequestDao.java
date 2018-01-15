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

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.SpecialServiceRequestDao;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation of the DAO on Special Service Request model objects. Default implementation of the
 * {@link de.hybris.platform.travelservices.dao.SpecialServiceRequestDao} interface.
 */
public class DefaultSpecialServiceRequestDao extends DefaultGenericDao<SpecialServiceRequestModel>
		implements SpecialServiceRequestDao
{

	/**
	 * @param typecode
	 */
	public DefaultSpecialServiceRequestDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public SpecialServiceRequestModel findSpecialServiceRequest(final String code)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("code", code);

		final List<SpecialServiceRequestModel> specialServiceRequestModels = find(params);
		return CollectionUtils.isEmpty(specialServiceRequestModels) ? null : specialServiceRequestModels.get(0);
	}

}
