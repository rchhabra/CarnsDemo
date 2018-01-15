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
import de.hybris.platform.travelservices.dao.MarketingRatePlanInfoDao;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link MarketingRatePlanInfoDao}
 */
public class DefaultMarketingRatePlanInfoDao extends DefaultGenericDao<MarketingRatePlanInfoModel>
		implements MarketingRatePlanInfoDao
{

	/**
	 * Instantiates a new Default marketing rate plan info dao.
	 *
	 * @param typecode
	 *           the typecode
	 */
	public DefaultMarketingRatePlanInfoDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public MarketingRatePlanInfoModel findMarketingRatePlanInfo(final String code)
	{
		validateParameterNotNull(code, "MarketingRatePlanInfo code must not be null!");

		final List<MarketingRatePlanInfoModel> marketingRatePlanInfoModels = find(
				Collections.singletonMap(MarketingRatePlanInfoModel.CODE, (Object) code));
		final Optional<MarketingRatePlanInfoModel> marketingRatePlanInfoModel = CollectionUtils
				.isNotEmpty(marketingRatePlanInfoModels) ? marketingRatePlanInfoModels.stream().findFirst() : null;
		return Objects.nonNull(marketingRatePlanInfoModel) && marketingRatePlanInfoModel.isPresent()
				? marketingRatePlanInfoModel.get() : null;
	}

}
