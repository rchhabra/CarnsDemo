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
package de.hybris.platform.travelservices.services;

import de.hybris.platform.travelservices.model.travel.CabinClassModel;

import java.util.List;


/**
 * Interface that exposes Cabin Class specific services
 */
public interface CabinClassService
{

	/**
	 * Service which returns a list of CabinClassModel types
	 *
	 * @return List<CabinClassModel> cabin classes
	 */
	List<CabinClassModel> getCabinClasses();

	/**
	 * service which returns CabinClassModel for the given cabin code.
	 *
	 * @param cabinCode
	 * 		string representing cabin code.
	 * @return CabinClassModel object.
	 */
	CabinClassModel getCabinClass(String cabinCode);

	/**
	 * service which returns CabinClassModel for the given cabinclass index.
	 *
	 * @param cabinClassIndex
	 * 		string representing cabinclass index.
	 * @return CabinClassModel object.
	 */
	CabinClassModel getCabinClass(Integer cabinClassIndex);

	/**
	 * Service method which returns CabinClassModel for the given bundleTemplate.
	 *
	 * @param bundleTemplate
	 * 		string representing bundleTemplate.
	 * @return CabinClassModel object.
	 */
	CabinClassModel findCabinClassFromBundleTemplate(String bundleTemplate);

}
