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

import de.hybris.platform.ndcservices.services.NDCTransportVehicleInfoService;
import de.hybris.platform.travelservices.dao.impl.DefaultTransportVehicleInfoDao;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCTransportVehicleInfoService}
 */
public class DefaultNDCTransportVehicleInfoService implements NDCTransportVehicleInfoService
{
	private DefaultTransportVehicleInfoDao transportVehicleInfoDao;

	@Override
	public TransportVehicleInfoModel getTransportVehicle(final String transportVehicleCode)
	{
		return getTransportVehicleInfoDao().findTranportVehicleInfo(transportVehicleCode);
	}

	/**
	 * Gets transport vehicle info dao.
	 *
	 * @return the transport vehicle info dao
	 */
	protected DefaultTransportVehicleInfoDao getTransportVehicleInfoDao()
	{
		return transportVehicleInfoDao;
	}

	/**
	 * Sets transport vehicle info dao.
	 *
	 * @param transportVehicleInfoDao
	 * 		the transport vehicle info dao
	 */
	@Required
	public void setTransportVehicleInfoDao(final DefaultTransportVehicleInfoDao transportVehicleInfoDao)
	{
		this.transportVehicleInfoDao = transportVehicleInfoDao;
	}
}
