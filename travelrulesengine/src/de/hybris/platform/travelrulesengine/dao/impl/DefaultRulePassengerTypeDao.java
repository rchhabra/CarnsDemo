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
package de.hybris.platform.travelrulesengine.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.travelrulesengine.dao.RulePassengerTypeDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * The type Default rule passenger type dao.
 */
public class DefaultRulePassengerTypeDao extends DefaultGenericDao<PassengerTypeModel> implements RulePassengerTypeDao
{
	/**
	 * Instantiates a new Default rule passenger type dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultRulePassengerTypeDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public PassengerTypeModel findPassengerTypeByCode(final String code)
	{
		validateParameterNotNull(code, "code must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(PassengerTypeModel.CODE, code);
		final List<PassengerTypeModel> passengerTypeModels = find(params);

		return CollectionUtils.isNotEmpty(passengerTypeModels) ? passengerTypeModels.get(0) : null;
	}
}
