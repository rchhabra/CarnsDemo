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

import de.hybris.platform.configurablebundleservices.daos.BundleTemplateDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Dao to retrieve BundleTemplates for TravelRouteModel, CabinClassModel combination or TravelSectorModel,
 * CabinClassModel combination or TransportOfferingModel, CabinClassModel combination
 */
public interface TravelBundleTemplateDao extends BundleTemplateDao
{
	/**
	 * Returns List of BundleTemplateModels for TravelRouteModel, CabinClassModel combination.
	 *
	 * @param travelRouteModel
	 * 		TravelRouteModel object.
	 * @param cabinClassModel
	 * 		CabinClassModel object
	 * @return List of BundleTemplateModels
	 */
	List<BundleTemplateModel> findBundleTemplates(TravelRouteModel travelRouteModel, CabinClassModel cabinClassModel);

	/**
	 * Returns List of BundleTemplateModels for TravelSectorModel, CabinClassModel combination.
	 *
	 * @param travelSectorModel
	 * 		TravelSectorModel object.
	 * @param cabinClassModel
	 * 		CabinClassModel object
	 * @return List of BundleTemplateModels
	 */
	List<BundleTemplateModel> findBundleTemplates(TravelSectorModel travelSectorModel, CabinClassModel cabinClassModel);

	/**
	 * Returns List of BundleTemplateModels for TransportOfferingModel, CabinClassModel combination.
	 *
	 * @param transportOfferingModel
	 * 		TransportOfferingModel object.
	 * @param cabinClassModel
	 * 		CabinClassModel object
	 * @return List of BundleTemplateModels
	 */
	List<BundleTemplateModel> findBundleTemplates(TransportOfferingModel transportOfferingModel, CabinClassModel cabinClassModel);

	/**
	 * Returns list of default BundleTemplateModels for CabinClassModel.
	 *
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @return list of default BundleTemplateModels
	 */
	List<BundleTemplateModel> findDefaultBundleTemplates(CabinClassModel cabinClassModel);
}
