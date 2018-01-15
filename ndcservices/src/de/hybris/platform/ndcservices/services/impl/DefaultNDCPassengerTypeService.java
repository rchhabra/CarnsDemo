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
package de.hybris.platform.ndcservices.services.impl;

import de.hybris.platform.ndcservices.dao.NDCPassengerTypeDAO;
import de.hybris.platform.ndcservices.services.NDCPassengerTypeService;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCPassengerTypeService}
 */
public class DefaultNDCPassengerTypeService implements NDCPassengerTypeService
{

	private NDCPassengerTypeDAO ndcPassengerTypeDAO;

	@Override
	public PassengerTypeModel getPassengerType(final String ndcCode)
	{
		return getNdcPassengerTypeDAO().getPassengerType(ndcCode);
	}

	/**
	 * Gets ndc passenger type dao.
	 *
	 * @return the ndc passenger type dao
	 */
	protected NDCPassengerTypeDAO getNdcPassengerTypeDAO()
	{
		return ndcPassengerTypeDAO;
	}

	/**
	 * Sets ndc passenger type dao.
	 *
	 * @param ndcPassengerTypeDAO
	 * 		the ndc passenger type dao
	 */
	@Required
	public void setNdcPassengerTypeDAO(final NDCPassengerTypeDAO ndcPassengerTypeDAO)
	{
		this.ndcPassengerTypeDAO = ndcPassengerTypeDAO;
	}

}

