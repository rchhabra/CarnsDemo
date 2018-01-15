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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Interface that exposes Selected Accommodation specific DAO services
 */
public interface SelectedAccommodationDao extends Dao
{
	/**
	 * this API takes a transport offering as parameter and returns list of selected accommodation(seats which are
	 * occupied or unavailable) associated with a transport offering, and associated with orders that have status other
	 * than passed in cancelledOrderStatuses
	 *
	 * @param transportOffering
	 *           the transport offering
	 * @param selectedAccomStatuses
	 *           the selected accom statuses
	 * @param cancelledOrderStatuses
	 *           the cancelled order statuses
	 * @return list
	 */
	List<SelectedAccommodationModel> findSelectedAccommodations(TransportOfferingModel transportOffering,
			List<AccommodationStatus> selectedAccomStatuses, List<OrderStatus> cancelledOrderStatuses);

	/**
	 * This API check and returns an instance of {@link SelectedAccommodationModel} if available for given
	 * {@link TransportOfferingModel}, {@link OrderModel} and {@link TravellerModel}
	 *
	 * @param transportOffering
	 * @param order
	 * @param traveller
	 */
	SelectedAccommodationModel getSelectedAccommodationForTraveller(TransportOfferingModel transportOffering, OrderModel order,
			TravellerModel traveller);

}
