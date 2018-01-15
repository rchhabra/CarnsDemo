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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.TransportVehicleConfigurationMappingDao;
import de.hybris.platform.travelservices.jalo.travel.TransportVehicleConfigurationMapping;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleConfigurationMappingModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public class DefaultTransportVehicleConfigurationMappingDao extends DefaultGenericDao<TransportVehicleConfigurationMappingModel>
		implements TransportVehicleConfigurationMappingDao
{

	private static final String TRANSPORT_ROUTE_MUST_NOT_BE_NULL = "transport route must not be null!";
	private static final String TRANSPORT_SECTOR_MUST_NOT_BE_NULL = "transport sector must not be null!";
	private static final String CATALOG_VERSION_MUST_NOT_BE_NULL = "catalog version must not be null!";
	private static final String TRANSPORT_OFFERING_MUST_NOT_BE_NULL = "transport offering must not be null!";
	private static final String VEHICLE_INFO_MUST_NOT_BE_NULL = "vehicle info must not be null!";

	public DefaultTransportVehicleConfigurationMappingDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public AccommodationMapModel findAccommodationMap(final TransportVehicleInfoModel vehicle,
			final TransportOfferingModel transportOffering, final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(vehicle, VEHICLE_INFO_MUST_NOT_BE_NULL);
		validateParameterNotNull(transportOffering, TRANSPORT_OFFERING_MUST_NOT_BE_NULL);
		validateParameterNotNull(catalogVersion, CATALOG_VERSION_MUST_NOT_BE_NULL);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportVehicleConfigurationMapping.VEHICLE, vehicle);
		params.put(TransportVehicleConfigurationMapping.TRANSPORTOFFERING, transportOffering);
		params.put(TransportVehicleConfigurationMapping.CATALOGVERSION, catalogVersion);
		final List<TransportVehicleConfigurationMappingModel> transportVehicleConfigurationMapping = find(params);
		if (CollectionUtils.isNotEmpty(transportVehicleConfigurationMapping))
		{
			return transportVehicleConfigurationMapping.get(0).getAccommodationMap();
		}
		return null;
	}

	@Override
	public AccommodationMapModel findAccommodationMap(final TransportVehicleInfoModel vehicle,
			final TravelSectorModel travelSector, final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(vehicle, VEHICLE_INFO_MUST_NOT_BE_NULL);
		validateParameterNotNull(travelSector, TRANSPORT_SECTOR_MUST_NOT_BE_NULL);
		validateParameterNotNull(catalogVersion, CATALOG_VERSION_MUST_NOT_BE_NULL);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportVehicleConfigurationMapping.VEHICLE, vehicle);
		params.put(TransportVehicleConfigurationMapping.TRAVELSECTOR, travelSector);
		params.put(TransportVehicleConfigurationMapping.CATALOGVERSION, catalogVersion);
		final List<TransportVehicleConfigurationMappingModel> transportVehicleConfigurationMapping = find(params);
		if (CollectionUtils.isNotEmpty(transportVehicleConfigurationMapping))
		{
			return transportVehicleConfigurationMapping.get(0).getAccommodationMap();
		}
		return null;
	}

	@Override
	public AccommodationMapModel findAccommodationMap(final TransportVehicleInfoModel vehicle,
			final TravelRouteModel travelRoute, final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(vehicle, VEHICLE_INFO_MUST_NOT_BE_NULL);
		validateParameterNotNull(travelRoute, TRANSPORT_ROUTE_MUST_NOT_BE_NULL);
		validateParameterNotNull(catalogVersion, CATALOG_VERSION_MUST_NOT_BE_NULL);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportVehicleConfigurationMapping.VEHICLE, vehicle);
		params.put(TransportVehicleConfigurationMapping.TRAVELROUTE, travelRoute);
		params.put(TransportVehicleConfigurationMapping.CATALOGVERSION, catalogVersion);
		final List<TransportVehicleConfigurationMappingModel> transportVehicleConfigurationMapping = find(params);
		if (CollectionUtils.isNotEmpty(transportVehicleConfigurationMapping))
		{
			return transportVehicleConfigurationMapping.get(0).getAccommodationMap();
		}
		return null;
	}

	@Override
	public AccommodationMapModel findAccommodationMap(final TransportVehicleInfoModel vehicle,
			final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(vehicle, VEHICLE_INFO_MUST_NOT_BE_NULL);
		validateParameterNotNull(catalogVersion, CATALOG_VERSION_MUST_NOT_BE_NULL);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TransportVehicleConfigurationMapping.VEHICLE, vehicle);
		params.put(TransportVehicleConfigurationMapping.CATALOGVERSION, catalogVersion);
		final List<TransportVehicleConfigurationMappingModel> transportVehicleConfigurationMapping = find(params);
		if (CollectionUtils.isNotEmpty(transportVehicleConfigurationMapping))
		{
			return transportVehicleConfigurationMapping.get(0).getAccommodationMap();
		}
		return null;
	}

}
