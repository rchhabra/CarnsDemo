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

import de.hybris.platform.ndcservices.dao.NDCPassengerTypeDAO;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.Collections;
import java.util.Optional;


/**
 * Default implementation for NDCPassengerTypeDAO
 */
public class DefaultNDCPassengerTypeDAO extends DefaultGenericDao<PassengerTypeModel> implements NDCPassengerTypeDAO
{

	/**
	 * Instantiates a new Default ndc passenger type dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultNDCPassengerTypeDAO(final String typecode)
	{
		super(typecode);
	}

	@Override
	public PassengerTypeModel getPassengerType(final String ndcCode)
	{
		validateParameterNotNull(ndcCode, "PassengerType NDCCode must not be null!");

		final Optional<PassengerTypeModel> optionalPassengerType = find(Collections.singletonMap(PassengerTypeModel.NDCCODE, (Object) ndcCode))
				.stream().findFirst();

		return optionalPassengerType.isPresent() ? optionalPassengerType.get() : null;
	}
}
