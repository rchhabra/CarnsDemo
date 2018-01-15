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
import de.hybris.platform.travelservices.dao.RatePlanConfigDao;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link RatePlanConfigDao}
 */
public class DefaultRatePlanConfigDao extends DefaultGenericDao<RatePlanConfigModel> implements RatePlanConfigDao
{

	/**
	 * @param typecode
	 */
	public DefaultRatePlanConfigDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public RatePlanConfigModel findRatePlanConfig(final String code)
	{
		validateParameterNotNull(code, "RatePlanConfig code must not be null!");
		final List<RatePlanConfigModel> ratePlanConfigModels = find(
				Collections.singletonMap(RatePlanConfigModel.CODE, (Object) code));
		final Optional<RatePlanConfigModel> ratePlanConfigModel = CollectionUtils.isNotEmpty(ratePlanConfigModels)
				? ratePlanConfigModels.stream().findFirst() : null;
		return Objects.nonNull(ratePlanConfigModel) && ratePlanConfigModel.isPresent() ? ratePlanConfigModel.get() : null;
	}
}
