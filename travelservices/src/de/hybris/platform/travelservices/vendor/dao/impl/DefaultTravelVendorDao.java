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

package de.hybris.platform.travelservices.vendor.dao.impl;

import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.vendor.dao.TravelVendorDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link TravelVendorDao}
 */
public class DefaultTravelVendorDao extends DefaultGenericDao<VendorModel> implements TravelVendorDao
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelVendorDao.class);

	public DefaultTravelVendorDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public VendorModel getVendorByCode(final String code)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(VendorModel.CODE, code);
		final List<VendorModel> vendors = find(params);
		if (CollectionUtils.isEmpty(vendors))
		{
			LOG.warn("No vendor found for code + " + code);
			return null;
		}
		if (CollectionUtils.size(vendors) > 1)
		{
			LOG.warn("Ambiguous vendors found for code + " + code);
			return null;
		}
		return vendors.get(0);
	}

}
