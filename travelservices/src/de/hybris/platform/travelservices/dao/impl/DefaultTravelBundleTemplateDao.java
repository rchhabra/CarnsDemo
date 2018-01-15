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

import de.hybris.platform.configurablebundleservices.daos.impl.DefaultBundleTemplateDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.TravelBundleTemplateDao;
import de.hybris.platform.travelservices.model.travel.BundleTemplateTransportOfferingMappingModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of ExtBundleTemplateDao to retrieve BundleTemplates for TravelRouteModel, CabinClassModel combination
 * or TravelSectorModel, CabinClassModel combination or TransportOfferingModel, CabinClassModel combination
 */
public class DefaultTravelBundleTemplateDao extends DefaultBundleTemplateDao implements TravelBundleTemplateDao
{
	private static final String FIND_BUNDLE_TEMPLATES_QUERY = "SELECT {"
			+ BundleTemplateTransportOfferingMappingModel.BUNDLETEMPLATE + "} FROM {"
			+ BundleTemplateTransportOfferingMappingModel._TYPECODE + "}";

	private static final String FIND_BUNDLE_TEMPLATES_BY_ROUTE_CABIN_CLASS_QUERY = FIND_BUNDLE_TEMPLATES_QUERY + " WHERE {"
			+ BundleTemplateTransportOfferingMappingModel.TRAVELROUTE + "}=?" + TravelRouteModel._TYPECODE + " AND {"
			+ BundleTemplateTransportOfferingMappingModel.CABINCLASS + "}=?" + CabinClassModel._TYPECODE;

	private static final String FIND_BUNDLE_TEMPLATES_BY_SECTOR_CABIN_CLASS_QUERY = FIND_BUNDLE_TEMPLATES_QUERY + " WHERE {"
			+ BundleTemplateTransportOfferingMappingModel.TRAVELSECTOR + "}=?" + TravelSectorModel._TYPECODE + " AND {"
			+ BundleTemplateTransportOfferingMappingModel.CABINCLASS + "}=?" + CabinClassModel._TYPECODE;

	private static final String FIND_BUNDLE_TEMPLATES_BY_TRANSPORT_OFFERING_CABIN_CLASS_QUERY = FIND_BUNDLE_TEMPLATES_QUERY
			+ " WHERE {" + BundleTemplateTransportOfferingMappingModel.TRANSPORTOFFERING + "}=?" + TransportOfferingModel._TYPECODE
			+ " AND {" + BundleTemplateTransportOfferingMappingModel.CABINCLASS + "}=?" + CabinClassModel._TYPECODE;

	private static final String FIND_DEFAULT_BUNDLE_TEMPLATES_QUERY = FIND_BUNDLE_TEMPLATES_QUERY + " WHERE {"
			+ BundleTemplateTransportOfferingMappingModel.TRANSPORTOFFERING + "} IS NULL AND {"
			+ BundleTemplateTransportOfferingMappingModel.TRAVELSECTOR + "} IS NULL AND {"
			+ BundleTemplateTransportOfferingMappingModel.TRAVELROUTE + "} IS NULL AND {"
			+ BundleTemplateTransportOfferingMappingModel.CABINCLASS + "}=?" + CabinClassModel._TYPECODE;

	@Override
	public List<BundleTemplateModel> findBundleTemplates(final TravelRouteModel travelRouteModel,
			final CabinClassModel cabinClassModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(TravelRouteModel._TYPECODE, travelRouteModel);
		params.put(CabinClassModel._TYPECODE, cabinClassModel);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BUNDLE_TEMPLATES_BY_ROUTE_CABIN_CLASS_QUERY,
				params);

		final SearchResult<BundleTemplateModel> searchResults = getFlexibleSearchService().search(flexibleSearchQuery);

		return searchResults.getResult();
	}

	@Override
	public List<BundleTemplateModel> findBundleTemplates(final TravelSectorModel travelSectorModel,
			final CabinClassModel cabinClassModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(TravelSectorModel._TYPECODE, travelSectorModel);
		params.put(CabinClassModel._TYPECODE, cabinClassModel);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BUNDLE_TEMPLATES_BY_SECTOR_CABIN_CLASS_QUERY,
				params);

		final SearchResult<BundleTemplateModel> searchResults = getFlexibleSearchService().search(flexibleSearchQuery);

		return searchResults.getResult();
	}

	@Override
	public List<BundleTemplateModel> findBundleTemplates(final TransportOfferingModel transportOfferingModel,
			final CabinClassModel cabinClassModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(TransportOfferingModel._TYPECODE, transportOfferingModel);
		params.put(CabinClassModel._TYPECODE, cabinClassModel);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_BUNDLE_TEMPLATES_BY_TRANSPORT_OFFERING_CABIN_CLASS_QUERY, params);

		final SearchResult<BundleTemplateModel> searchResults = getFlexibleSearchService().search(flexibleSearchQuery);

		return searchResults.getResult();
	}

	@Override
	public List<BundleTemplateModel> findDefaultBundleTemplates(final CabinClassModel cabinClassModel)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(CabinClassModel._TYPECODE, cabinClassModel);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_DEFAULT_BUNDLE_TEMPLATES_QUERY, params);

		final SearchResult<BundleTemplateModel> searchResults = getFlexibleSearchService().search(flexibleSearchQuery);

		return searchResults.getResult();
	}


}
