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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.RatePlanDao;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link RatePlanDao}
 */
public class DefaultRatePlanDao extends DefaultGenericDao<RatePlanModel> implements RatePlanDao
{

	/**
	 * @param typecode
	 */
	public DefaultRatePlanDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public RatePlanModel findRatePlan(final String code)
	{
		validateParameterNotNull(code, "RatePlan code must not be null!");
		final List<RatePlanModel> ratePlanModels = find(Collections.singletonMap(RatePlanModel.CODE, (Object) code));
		final Optional<RatePlanModel> ratePlanModel = CollectionUtils.isNotEmpty(ratePlanModels)
				? ratePlanModels.stream().findFirst() : null;
		return Objects.nonNull(ratePlanModel) && ratePlanModel.isPresent() ? ratePlanModel.get() : null;
	}
}
