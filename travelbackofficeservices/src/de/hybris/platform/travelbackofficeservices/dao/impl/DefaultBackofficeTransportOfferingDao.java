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
 */

package de.hybris.platform.travelbackofficeservices.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelbackofficeservices.dao.BackofficeTransportOfferingDao;
import de.hybris.platform.travelservices.dao.impl.DefaultTransportOfferingDao;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Concrete implementation of backoffice specific transport offering dao
 */
public class DefaultBackofficeTransportOfferingDao extends DefaultTransportOfferingDao
		implements BackofficeTransportOfferingDao
{
	final static String FIND_WITHOUT_SCHEDULE_AND_ORDER = "SELECT {to." + TransportOfferingModel.PK + "} "
			+ "FROM {" + TransportOfferingModel._TYPECODE + " AS to } "
			+ "WHERE {to." + TransportOfferingModel.SCHEDULECONFIGURATION + "} IS NULL "
			+ "ORDER BY {" + TransportOfferingModel.DEPARTURETIME + "} ASC";

	public DefaultBackofficeTransportOfferingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<TransportOfferingModel> findTransportOfferingsWithoutSchedule()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_WITHOUT_SCHEDULE_AND_ORDER);
		final SearchResult<TransportOfferingModel> results = getFlexibleSearchService().search(flexibleSearchQuery);
		return results.getResult();
	}
}
