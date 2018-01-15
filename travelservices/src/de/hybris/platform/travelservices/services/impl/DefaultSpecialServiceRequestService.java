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
package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.travelservices.dao.SpecialServiceRequestDao;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;
import de.hybris.platform.travelservices.services.SpecialServiceRequestService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of the Service on Special Service Request model objects. Default implementation of the
 * {@link de.hybris.platform.travelservices.services.SpecialServiceRequestService} interface.
 */
public class DefaultSpecialServiceRequestService implements SpecialServiceRequestService
{

	private SpecialServiceRequestDao specialServiceRequestDao;

	@Override
	public SpecialServiceRequestModel getSpecialServiceRequest(final String code)
	{
		if (StringUtils.isNotBlank(code))
		{
			return specialServiceRequestDao.findSpecialServiceRequest(code);
		}

		return null;
	}

	/**
	 * Gets special service request dao.
	 *
	 * @return the special service request dao
	 */
	protected SpecialServiceRequestDao getSpecialServiceRequestDao()
	{
		return specialServiceRequestDao;
	}

	/**
	 * Sets special service request dao.
	 *
	 * @param specialServiceRequestDao
	 * 		the special service request dao
	 */
	@Required
	public void setSpecialServiceRequestDao(final SpecialServiceRequestDao specialServiceRequestDao)
	{
		this.specialServiceRequestDao = specialServiceRequestDao;
	}

}
