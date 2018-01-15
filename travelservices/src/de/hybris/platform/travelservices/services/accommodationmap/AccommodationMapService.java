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

package de.hybris.platform.travelservices.services.accommodationmap;

import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * This interface exposes APIs to find accommodation map and seat map configuration based on different parameters
 */
public interface AccommodationMapService
{

	/**
	 * This API gets accommodation map from DB based on following rules : 1)Get accommodation map based on vehicle info
	 * and transport offering 2)If not found, get accommodation map based on vehicle info and travel sector 3)If not
	 * found, get accommodation map based on vehicle info and travel route 4)If not found, get accommodation map based on
	 * vehicle info
	 *
	 * @param vehicleInfoCode
	 * 		the vehicle info code
	 * @param transportOffering
	 * 		the transport offering
	 * @param route
	 * 		the route
	 * @param travelSectorData
	 * 		the travel sector data
	 *
	 * @return accommodation map
	 */
	AccommodationMapModel getAccommodationMap(String vehicleInfoCode, TransportOfferingModel transportOffering, String route,
			TravelSectorData travelSectorData);

	/**
	 * This API gets accommodation map from DB based on following rules : 1)Get accommodation map based on vehicle info
	 * and transport offering 2)If not found, get accommodation map based on vehicle info and travel sector 3)If not
	 * found, get accommodation map based on vehicle info and travel route 4)If not found, get accommodation map based on
	 * vehicle info
	 *
	 * @param vehicleInfoCode
	 * 		the vehicle info code
	 * @param transportOffering
	 * 		the transport offering
	 * @param route
	 * 		the route
	 *
	 * @return accommodation map
	 */
	AccommodationMapModel getAccommodationMap(String vehicleInfoCode, TransportOfferingModel transportOffering, String route);

	/**
	 * Gets seat map configuration (list of configured accommodations based on an accommodation map)
	 *
	 * @param accommodationMap
	 * 		the accommodation map
	 *
	 * @return accommodation map configuration
	 */
	List<ConfiguredAccommodationModel> getAccommodationMapConfiguration(AccommodationMapModel accommodationMap);

	/**
	 * Gets all selectedAccommodations for a transport offering, where selected accommodations are in the status passed
	 * in selectedAccomStatuses and is part of Orders with status other than cancelledOrderStatuses
	 *
	 * @param transportOffering
	 * 		the transport offering
	 * @param selectedAccomStatuses
	 * 		the selected accom statuses
	 * @param cancelledOrderStatuses
	 * 		the cancelled order statuses
	 *
	 * @return selected accommodations
	 */
	List<SelectedAccommodationModel> getSelectedAccommodations(TransportOfferingModel transportOffering,
			List<AccommodationStatus> selectedAccomStatuses, List<OrderStatus> cancelledOrderStatuses);

	/**
	 * Get configured accommodation model (seat) for an uid
	 *
	 * @param uid
	 * 		the uid
	 *
	 * @return accommodation
	 */
	ConfiguredAccommodationModel getAccommodation(String uid);

	/**
	 * Verifies following cases for the accommodation: 1. If the accommodation is configured in the system. 2. if The
	 * accomodation is bookable. 3. if the accommoation is already booked by any other passenger in the transport
	 * offering.
	 * <p>
	 * NOTE: IT DOESNOT CHECK IF THE ACCOMMODATION IS ALREADY BOOKED IN THE CURRENT CART.
	 *
	 * @param accommodation
	 * @param transportOfferingCode
	 * @param travellerData
	 *
	 * @return
	 */
	boolean isAccommodationAvailableForBooking(ConfiguredAccommodationModel accommodation, String transportOfferingCode,
			TravellerData travellerData);

	/**
	 * Verifies if the accommodation is already added to the cart by the passengers of current booking.
	 *
	 * @param abstractOrder
	 * @param configuredAccommodationModel
	 *
	 * @return
	 */
	boolean isSeatInCart(AbstractOrderModel abstractOrder, ConfiguredAccommodationModel configuredAccommodationModel);

	/**
	 * Verifies if product is referenced by the fareproduct entry of the cart.
	 *
	 * @param seatProduct
	 * @param transportOfferingCode
	 * @param abstractOrder
	 *
	 * @return
	 */
	boolean isSeatProductReferencedByFareProductInOrder(ProductModel seatProduct, String transportOfferingCode,
			AbstractOrderModel abstractOrder);

	/**
	 * Fetches the accommodation, already selected by the traveller in the cart based on the label of the traveller.
	 *
	 * @param travellerCode
	 * 		the traveller code
	 * @param transportOfferingCode
	 * 		the transport offering code
	 * @param abstractOrder
	 * 		the abstract order
	 *
	 * @return the selected seat for traveller
	 */
	SelectedAccommodationModel getSelectedSeatForTraveller(String travellerCode, String transportOfferingCode,
			AbstractOrderModel abstractOrder);
}
