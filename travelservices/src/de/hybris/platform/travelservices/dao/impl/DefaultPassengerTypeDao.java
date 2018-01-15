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
import de.hybris.platform.travelservices.dao.PassengerTypeDao;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;



/**
 *
 * Class is responsible for providing concrete implementation of the PassengerTypeDao interface. The class uses the
 * FlexibleSearchService to query the database and return as list of List<PassengerTypeModel> types.
 *
 */

public class DefaultPassengerTypeDao extends DefaultGenericDao<PassengerTypeModel> implements PassengerTypeDao
{

	/**
	 * @param typecode
	 */
	public DefaultPassengerTypeDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<PassengerTypeModel> findPassengerTypes()
	{
		return find();
	}

	@Override
	public PassengerTypeModel findPassengerType(final String passengerTypeCode)
	{
		validateParameterNotNull(passengerTypeCode, "PassengerType Code code must not be null!");
		final List<PassengerTypeModel> passengerTypeModels = find(
				Collections.singletonMap(PassengerTypeModel.CODE, (Object) passengerTypeCode));
		final Optional<PassengerTypeModel> passengerTypeModel = CollectionUtils.isNotEmpty(passengerTypeModels)
				? passengerTypeModels.stream().findFirst() : null;
		return Objects.nonNull(passengerTypeModel) && passengerTypeModel.isPresent() ? passengerTypeModel.get() : null;
	}

}
