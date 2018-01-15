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
package de.hybris.platform.ndcservices.services;

import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;

/**
 * Interface that exposes TransportVehicleInfoModel through his ndcCode
 */
public interface NDCTransportVehicleInfoService
{
	/**
	 * Find a TransportVehicleInfoModel through his ndcCode
	 *
	 * @param ndcCode
	 * 	of the TransportVehicleInfoModel
	 * @return
	 * 	the TransportVehicleInfoModel with that ndcCode
	 */
	TransportVehicleInfoModel getTransportVehicle(String ndcCode);
}
