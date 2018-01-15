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
package de.hybris.platform.travelservices.bundle;

import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Override the {@link BundleTemplateService} to get the BundleTemplates for Travel Itineraries.
 */
public interface TravelBundleTemplateService extends BundleTemplateService
{
	/**
	 * Retrieves List of BundleTemplates for a given TravelRoute and requested cabin class.
	 *
	 * @param travelRouteModel
	 * 		the travel route model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @return List of BundleTemplates
	 */
	List<BundleTemplateModel> getBundleTemplates(TravelRouteModel travelRouteModel, CabinClassModel cabinClassModel);

	/**
	 * Retrieves List of BundleTemplates for a given TravelSector and requested cabin class.
	 *
	 * @param travelSectorModel
	 * 		the travel sector model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @return List of BundleTemplates
	 */
	List<BundleTemplateModel> getBundleTemplates(TravelSectorModel travelSectorModel, CabinClassModel cabinClassModel);

	/**
	 * Retrieves List of BundleTemplates for a given TravelSector and requested cabin class.
	 *
	 * @param transportOfferingModel
	 * 		the transport offering model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @return List of BundleTemplates
	 */
	List<BundleTemplateModel> getBundleTemplates(TransportOfferingModel transportOfferingModel, CabinClassModel cabinClassModel);

	/**
	 * Retrieves default BundleTemplates for requested cabin class.
	 *
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @return List of default BundleTemplates
	 */
	List<BundleTemplateModel> getDefaultBundleTemplates(CabinClassModel cabinClassModel);

	/**
	 * Returns the bundle template id for in the order against the originDestinationRefNumber
	 *
	 * @param abstractOrder
	 *           abstractOrder
	 * @param originDestinationRefNumber
	 *           originDestinationRefNumber
	 * @return bundle template id.
	 */
	String getBundleTemplateIdFromOrder(AbstractOrderModel abstractOrder, int originDestinationRefNumber);
}
