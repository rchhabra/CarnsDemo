/*
* [y] hybris Platform
*
* Copyright (c) 2000-2015 hybris AG
* All rights reserved.
*
* This software is the confidential and proprietary information of hybris
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with hybris.
*
*/

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;

import java.util.List;


/**
 * OrderUserAccountMappingDao interface which provides functionality to manage OrderUserAccountMapping.
 */
public interface OrderUserAccountMappingDao
{
	/**
	 *
	 * @param user
	 * @param order
	 * @return List<OrderUserAccountMappingModel> a list of mappings
	 */
	List<OrderUserAccountMappingModel> findMappings(UserModel user, AbstractOrderModel order);
}
