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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.AbstractOrderEntryGroupDao;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link AbstractOrderEntryGroupDao}
 */
public class DefaultAbstractOrderEntryGroupDao extends DefaultGenericDao<AbstractOrderEntryGroupModel> implements AbstractOrderEntryGroupDao
{
	private static String FIND_BY_ORDER = "SELECT DISTINCT {aoeg." + AbstractOrderEntryGroupModel.PK + "} FROM {"
			+ AbstractOrderEntryGroupModel._TYPECODE + " AS aoeg JOIN " + AbstractOrderEntryModel._TYPECODE + " AS aoe ON {aoe."
			+ AbstractOrderEntryModel.ENTRYGROUP + "} = {aoeg." + AbstractOrderEntryGroupModel.PK + "} JOIN "
			+ AbstractOrderModel._TYPECODE + " AS ao ON {aoe." + AbstractOrderEntryModel.ORDER + "}={ao."
			+ AbstractOrderModel.PK + "}} WHERE {ao." + AbstractOrderModel.PK + "} = ?abstractOrder";

	public DefaultAbstractOrderEntryGroupDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<AbstractOrderEntryGroupModel> findAbstractOrderEntryGroups(final AbstractOrderModel abstractOrderModel)
	{

		validateParameterNotNull(abstractOrderModel, "AbstractOrderModel must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("abstractOrder", abstractOrderModel);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_ORDER, params);
		final SearchResult<AbstractOrderEntryGroupModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		if (searchResult != null)
		{
			return searchResult.getResult();
		}

		return Collections.emptyList();
	}

}
