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
package de.hybris.platform.travelservices.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;

import java.util.List;


/**
 * Interface that exposes Cabin Class specific DAO services
 */
public interface CabinClassDao extends Dao
{

	/**
	 * DAO service which returns a list of CabinClassModel types
	 *
	 * @return SearchResult<CabinClassModel> list
	 */
	List<CabinClassModel> findCabinClasses();

	/**
	 * Dao method which returns CabinClassModel for the given cabin code.
	 *
	 * @param cabinCode
	 * 		string representing cabin code.
	 * @return CabinClassModel object.
	 */
	CabinClassModel findCabinClass(String cabinCode);

	/**
	 * Dao method which returns CabinClassModel for the given cabinclass index.
	 *
	 * @param cabinClassIndex
	 * 		string representing cabinclass index.
	 * @return CabinClassModel object.
	 */
	CabinClassModel findCabinClass(Integer cabinClassIndex);

	/**
	 * Dao method which returns CabinClassModel for the given bundleTemplate.
	 *
	 * @param bundleTemplate
	 * 		string representing bundleTemplate.
	 * @return CabinClassModel object.
	 */
	CabinClassModel findCabinClassFromBundleTemplate(String bundleTemplate);
}
