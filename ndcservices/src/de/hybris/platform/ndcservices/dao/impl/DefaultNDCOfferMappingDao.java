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
package de.hybris.platform.ndcservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.ndcservices.dao.NDCOfferMappingDao;
import de.hybris.platform.ndcservices.model.NDCOfferMappingModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Collections;
import java.util.Optional;


/**
 * Default implementation of {@link NDCOfferMappingDao}
 */
public class DefaultNDCOfferMappingDao extends DefaultGenericDao<NDCOfferMappingModel> implements NDCOfferMappingDao
{
	/**
	 * Instantiates a new Default ndc offer mapping dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultNDCOfferMappingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public NDCOfferMappingModel getNDCOfferMappingFromCode(final String code)
	{
		validateParameterNotNull(code, "code must not be null!");

		final Optional<NDCOfferMappingModel> optionalNDCOfferMapping = find(
				Collections.singletonMap(NDCOfferMappingModel.CODE, (Object) code))
				.stream().findFirst();

		return optionalNDCOfferMapping.isPresent() ? optionalNDCOfferMapping.get() : null;
	}

	@Override
	public NDCOfferMappingModel getNDCOfferMappingFromOfferItemID(final String ndcOfferItemID)
	{
		validateParameterNotNull(ndcOfferItemID, "code must not be null!");

		final Optional<NDCOfferMappingModel> optionalNDCOfferMapping = find(
				Collections.singletonMap(NDCOfferMappingModel.NDCOFFERITEMID, (Object) ndcOfferItemID))
				.stream().findFirst();

		return optionalNDCOfferMapping.isPresent() ? optionalNDCOfferMapping.get() : null;
	}
}
