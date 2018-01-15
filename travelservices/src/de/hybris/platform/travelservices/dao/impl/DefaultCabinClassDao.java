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

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.CabinClassDao;
import de.hybris.platform.travelservices.model.travel.BundleTemplateTransportOfferingMappingModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Class is responsible for providing concrete implementation of the CabinClassDao interface. The class uses the
 * FlexibleSearchService to query the database and return as list of List<CabinClassModel> types.
 */

public class DefaultCabinClassDao extends DefaultGenericDao<CabinClassModel> implements CabinClassDao
{
	private static final Logger LOG = Logger.getLogger(DefaultCabinClassDao.class);

	private static final String BUNDLE_TEMPLATE_ID = "bundleTemplateId";

	private static final String FIND_CABIN_CLASS_BY_BUNDLE_TEMPLATE_QUERY = "SELECT {cc:"
			+ CabinClassModel.PK + "} FROM {"
			+ BundleTemplateTransportOfferingMappingModel._TYPECODE + " as btto JOIN "
			+ CabinClassModel._TYPECODE + " as cc ON {cc:" + CabinClassModel.PK + "} = {btto:"
			+ BundleTemplateTransportOfferingMappingModel.CABINCLASS + "} JOIN " + BundleTemplateModel._TYPECODE + " as bt ON {bt:"
			+ BundleTemplateModel.PK + "} = {btto:" + BundleTemplateTransportOfferingMappingModel.BUNDLETEMPLATE + "}}" + " WHERE {"
			+ BundleTemplateTransportOfferingMappingModel.TRANSPORTOFFERING + "} IS NULL AND {"
			+ BundleTemplateTransportOfferingMappingModel.TRAVELSECTOR + "} IS NULL AND {"
			+ BundleTemplateTransportOfferingMappingModel.TRAVELROUTE + "} IS NULL AND {bt:"
			+ BundleTemplateModel.ID + "} =?" + BUNDLE_TEMPLATE_ID;

	public DefaultCabinClassDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<CabinClassModel> findCabinClasses()
	{
		return find();
	}

	@Override
	public CabinClassModel findCabinClass(final String cabinCode)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(CabinClassModel.CODE, cabinCode);

		final List<CabinClassModel> cabinClasses = find(params);

		if (CollectionUtils.isEmpty(cabinClasses))
		{
			LOG.warn("No cabin class for code + " + cabinCode);
			return null;
		}
		if (CollectionUtils.size(cabinClasses) > 1)
		{
			LOG.warn("Ambiguous cabin classes found for code + " + cabinCode);
			return null;
		}
		return cabinClasses.get(0);
	}

	@Override
	public CabinClassModel findCabinClass(final Integer cabinClassIndex)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(CabinClassModel.CABINCLASSINDEX, cabinClassIndex);

		final List<CabinClassModel> cabinClasses = find(params);

		if (CollectionUtils.isEmpty(cabinClasses))
		{
			LOG.warn("No cabin class for index + " + cabinClassIndex);
			return null;
		}
		if (CollectionUtils.size(cabinClasses) > 1)
		{
			LOG.warn("Ambiguous cabin classes found for index + " + cabinClassIndex);
			return null;
		}
		return cabinClasses.get(0);
	}

	@Override
	public CabinClassModel findCabinClassFromBundleTemplate(final String bundleTemplateId)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(BUNDLE_TEMPLATE_ID, bundleTemplateId);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_CABIN_CLASS_BY_BUNDLE_TEMPLATE_QUERY,
				params);

		final SearchResult<CabinClassModel> searchResults = getFlexibleSearchService().search(flexibleSearchQuery);

		final List<CabinClassModel> cabinClasses = searchResults.getResult();

		if (CollectionUtils.isEmpty(cabinClasses))
		{
			LOG.warn("No cabin class for bundle template + " + bundleTemplateId);
			return null;
		}

		return cabinClasses.get(0);
	}

}
