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

import de.hybris.platform.ndcservices.dao.NDCCreditCardTypeMappingDao;
import de.hybris.platform.ndcservices.model.NDCCreditCardTypeMappingModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Collections;
import java.util.Optional;


/**
 * Default implementation of {@link NDCCreditCardTypeMappingDao}
 */
public class DefaultNDCCreditCardTypeMappingDao extends DefaultGenericDao<NDCCreditCardTypeMappingModel>
		implements NDCCreditCardTypeMappingDao
{
	/**
	 * Instantiates a new Default ndc credit card type mapping dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultNDCCreditCardTypeMappingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public NDCCreditCardTypeMappingModel getCreditCardTypeMapping(final String cardTypeCode)
	{
		validateParameterNotNull(cardTypeCode, "NDCCreditCardTypeMapping code must not be null!");

		final Optional<NDCCreditCardTypeMappingModel> optionalNDCCreditCardTypeMapping = find(
				Collections.singletonMap(NDCCreditCardTypeMappingModel.CODE, (Object) cardTypeCode))
				.stream().findFirst();

		return optionalNDCCreditCardTypeMapping.isPresent() ? optionalNDCCreditCardTypeMapping.get() : null;
	}
}
