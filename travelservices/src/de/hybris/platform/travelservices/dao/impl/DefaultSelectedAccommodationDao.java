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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.SelectedAccommodationDao;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class DefaultSelectedAccommodationDao extends DefaultGenericDao<SelectedAccommodationModel>
		implements SelectedAccommodationDao
{
	public DefaultSelectedAccommodationDao(final String typecode)
	{
		super(typecode);
	}

	private static final String FIND_BY_TRANSPORT_OFFERING_STATUSES_CANCELLED_STATUSES = "SELECT {sa:"
			+ SelectedAccommodationModel.PK + "} FROM {" + SelectedAccommodationModel._TYPECODE
			+ " AS sa JOIN AccommodationStatus AS acc ON {sa:STATUS}={acc:PK} JOIN Order AS o ON {sa:ORDER}={o:PK} JOIN OrderStatus AS os ON {o:STATUS} = {os:PK}} WHERE {sa:"
			+ SelectedAccommodationModel.TRANSPORTOFFERING + "}=?" + SelectedAccommodationModel.TRANSPORTOFFERING
			+ " AND {os:pk} NOT IN ( ?cancelledOrderStatuses) AND {o:" + OrderModel.VERSIONID + "} IS NULL";

	private static final String SELECTED_ACCOMMODATION_ATTRIBUTE = " AND {acc:pk} IN ( ?selectedAccomStatuses )";

	@Override
	public List<SelectedAccommodationModel> findSelectedAccommodations(final TransportOfferingModel transportOffering,
			final List<AccommodationStatus> selectedAccomStatuses, final List<OrderStatus> cancelledOrderStatuses)
	{
		validateParameterNotNull(transportOffering, "Transport Offering must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(SelectedAccommodationModel.TRANSPORTOFFERING, transportOffering);
		params.put("cancelledOrderStatuses", cancelledOrderStatuses);

		String query = FIND_BY_TRANSPORT_OFFERING_STATUSES_CANCELLED_STATUSES;
		if (CollectionUtils.isNotEmpty(selectedAccomStatuses))
		{
			query += SELECTED_ACCOMMODATION_ATTRIBUTE;
			params.put("selectedAccomStatuses", selectedAccomStatuses);
		}

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query, params);
		final SearchResult<SelectedAccommodationModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
		if (searchResult != null)
		{
			return searchResult.getResult();
		}
		return Collections.emptyList();
	}

	@Override
	public SelectedAccommodationModel getSelectedAccommodationForTraveller(final TransportOfferingModel transportOffering,
			final OrderModel order, final TravellerModel traveller)
	{
		validateParameterNotNull(transportOffering, "Transport Offering must not be null!");
		validateParameterNotNull(order, "order must not be null!");
		validateParameterNotNull(traveller, "order must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(SelectedAccommodationModel.TRANSPORTOFFERING, transportOffering);
		params.put(SelectedAccommodationModel.ORDER, order);
		params.put(SelectedAccommodationModel.TRAVELLER, traveller);

		final List<SelectedAccommodationModel> selectedAccommodations = find(params);

		return CollectionUtils.isNotEmpty(selectedAccommodations) ? selectedAccommodations.get(0) : null;
	}

}
