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

package de.hybris.platform.travelservices.services.accommodationmap.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commerceservices.util.CommerceCatalogUtils;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.dao.ConfiguredAccommodationDao;
import de.hybris.platform.travelservices.dao.SelectedAccommodationDao;
import de.hybris.platform.travelservices.dao.TransportFacilityDao;
import de.hybris.platform.travelservices.dao.TransportVehicleConfigurationMappingDao;
import de.hybris.platform.travelservices.dao.TransportVehicleInfoDao;
import de.hybris.platform.travelservices.dao.TravelRouteDao;
import de.hybris.platform.travelservices.dao.TravelSectorDao;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A service class implementation of AccommodationMapService
 */
public class DefaultAccommodationMapService implements AccommodationMapService
{
	private static final Logger LOG = Logger.getLogger(DefaultAccommodationMapService.class);

	private static final String NO_TRANSPORT_VEHICLE_INFO_FOUND_FOR_CODE = "No transport vehicle info found for code : ";

	private TransportVehicleConfigurationMappingDao transportVehicleConfigurationMappingDao;
	private TransportVehicleInfoDao transportVehicleInfoDao;
	private TransportFacilityDao transportFacilityDao;
	private TravelSectorDao travelSectorDao;
	private TravelRouteDao travelRouteDao;
	private ConfiguredAccommodationDao configuredAccommodationDao;
	private SelectedAccommodationDao selectedAccommodationDao;

	private CatalogVersionService catalogVersionService;
	private TransportOfferingService transportOfferingService;
	private ProductReferenceService productReferenceService;

	@Override
	public AccommodationMapModel getAccommodationMap(final String vehicleInfoCode, final TransportOfferingModel transportOffering,
			final String route, final TravelSectorData travelSectorData)
	{
		AccommodationMapModel accommodationMap = null;
		final TransportVehicleInfoModel tranportVehicleInfo = getTransportVehicleInfoDao().findTranportVehicleInfo(vehicleInfoCode);
		if (tranportVehicleInfo == null)
		{
			LOG.error(NO_TRANSPORT_VEHICLE_INFO_FOUND_FOR_CODE + vehicleInfoCode);
			throw new AccommodationMapDataSetUpException(NO_TRANSPORT_VEHICLE_INFO_FOUND_FOR_CODE + vehicleInfoCode);
		}
		// if transport offering is not null, get vehicle configuration based on transport vehicle info and transport offering
		final CatalogVersionModel catalogVersion = getCatalogVersion();
		if (transportOffering != null)
		{
			accommodationMap = getAccommodationMap(transportOffering, tranportVehicleInfo, catalogVersion);
		}
		if (accommodationMap != null)
		{
			return accommodationMap;
		}
		/*
		 * if transport offering was null or no vehicle configuration found based on vehicle info and transport offering
		 * then get it based on vehicle info and travel sector
		 */
		accommodationMap = getAccommodationMap(transportOffering, travelSectorData, tranportVehicleInfo, catalogVersion);
		if (accommodationMap != null)
		{
			return accommodationMap;
		}

		/*
		 * if vehicle configuration still not found, get it based on vehicle info and travel route
		 */
		accommodationMap = getAccommodationMap(route, tranportVehicleInfo, catalogVersion);
		if (accommodationMap != null)
		{
			return accommodationMap;
		}

		/*
		 * If vehicle configuration still not found, retrieve it based on just vehicle info
		 */
		return getTransportVehicleConfigurationMappingDao().findAccommodationMap(tranportVehicleInfo, catalogVersion);

	}

	@Override
	public AccommodationMapModel getAccommodationMap(final String vehicleInfoCode, final TransportOfferingModel transportOffering,
			final String route)
	{
		AccommodationMapModel accommodationMap = null;
		final TransportVehicleInfoModel tranportVehicleInfo = getTransportVehicleInfoDao().findTranportVehicleInfo(vehicleInfoCode);
		if (tranportVehicleInfo == null)
		{
			LOG.error(NO_TRANSPORT_VEHICLE_INFO_FOUND_FOR_CODE + vehicleInfoCode);
			throw new AccommodationMapDataSetUpException(NO_TRANSPORT_VEHICLE_INFO_FOUND_FOR_CODE + vehicleInfoCode);
		}
		final CatalogVersionModel catalogVersion = getCatalogVersion();
		// if transport offering is not null, get vehicle configuration based on transport vehicle info and transport offering
		if (transportOffering != null)
		{
			accommodationMap = getAccommodationMap(transportOffering, tranportVehicleInfo, catalogVersion);
		}
		if (accommodationMap != null)
		{
			return accommodationMap;
		}
		/*
		 * if transport offering was null or no vehicle configuration found based on vehicle info and transport offering
		 * then get it based on vehicle info and travel sector
		 */
		accommodationMap = getAccommodationMap(transportOffering, tranportVehicleInfo, catalogVersion);
		if (accommodationMap != null)
		{
			return accommodationMap;
		}

		/*
		 * if vehicle configuration still not found, get it based on vehicle info and travel route
		 */
		accommodationMap = getAccommodationMap(route, tranportVehicleInfo, catalogVersion);
		if (accommodationMap != null)
		{
			return accommodationMap;
		}

		/*
		 * If vehicle configuration still not found, retrieve it based on just vehicle info
		 */
		return getTransportVehicleConfigurationMappingDao().findAccommodationMap(tranportVehicleInfo, catalogVersion);
	}

	/**
	 * Gets accommodation map.
	 *
	 * @param route
	 *           the route
	 * @param tranportVehicleInfo
	 *           the tranport vehicle info
	 * @param catalogVersion
	 *           the catalog version
	 * @return the accommodation map
	 */
	protected AccommodationMapModel getAccommodationMap(final String route, final TransportVehicleInfoModel tranportVehicleInfo,
			final CatalogVersionModel catalogVersion)
	{
		final TravelRouteModel travelRoute = getTravelRouteDao().findTravelRoute(route);
		return getTransportVehicleConfigurationMappingDao().findAccommodationMap(tranportVehicleInfo, travelRoute, catalogVersion);
	}

	/**
	 * Gets accommodation map.
	 *
	 * @param transportOffering
	 *           the transport offering
	 * @param travelSectorData
	 *           the travel sector data
	 * @param tranportVehicleInfo
	 *           the tranport vehicle info
	 * @param catalogVersion
	 *           the catalog version
	 * @return the accommodation map
	 */
	protected AccommodationMapModel getAccommodationMap(final TransportOfferingModel transportOffering,
			final TravelSectorData travelSectorData, final TransportVehicleInfoModel tranportVehicleInfo,
			final CatalogVersionModel catalogVersion)
	{
		TravelSectorModel travelSector = null;
		// if transport offering is not null, get travel sector from offering
		if (transportOffering != null)
		{
			travelSector = transportOffering.getTravelSector();
		}
		/*
		 * if transport offering is null or travel sector within transport offering is null, get travel sector based on
		 * transport facility (origin) and transport facility (destination)
		 */
		if (travelSector == null)
		{
			final TransportFacilityModel origin = getTransportFacilityDao()
					.findTransportFacility(travelSectorData.getOrigin().getCode());
			final TransportFacilityModel destination = getTransportFacilityDao()
					.findTransportFacility(travelSectorData.getDestination().getCode());
			travelSector = getTravelSectorDao().findTravelSector(origin, destination);
		}
		// if travel sector is found, get vehicle configuration
		if (travelSector != null)
		{
			return getTransportVehicleConfigurationMappingDao().findAccommodationMap(tranportVehicleInfo, travelSector,
					catalogVersion);
		}
		return null;
	}

	/**
	 * Gets accommodation map.
	 *
	 * @param transportOffering
	 *           the transport offering
	 * @param tranportVehicleInfo
	 *           the tranport vehicle info
	 * @param catalogVersion
	 *           the catalog version
	 * @return the accommodation map
	 */
	protected AccommodationMapModel getAccommodationMap(final TransportOfferingModel transportOffering,
			final TransportVehicleInfoModel tranportVehicleInfo, final CatalogVersionModel catalogVersion)
	{
		return getTransportVehicleConfigurationMappingDao().findAccommodationMap(tranportVehicleInfo, transportOffering,
				catalogVersion);

	}

	@Override
	public List<ConfiguredAccommodationModel> getAccommodationMapConfiguration(final AccommodationMapModel accommodationMap)
	{
		final CatalogVersionModel catalogVersion = getCatalogVersion();
		return getConfiguredAccommodationDao().findAccommodationMapConfiguration(accommodationMap, catalogVersion);
	}

	@Override
	public List<SelectedAccommodationModel> getSelectedAccommodations(final TransportOfferingModel transportOffering,
			final List<AccommodationStatus> selectedAccomStatuses, final List<OrderStatus> cancelledOrderStatuses)
	{
		return getSelectedAccommodationDao().findSelectedAccommodations(transportOffering, selectedAccomStatuses,
				cancelledOrderStatuses);
	}

	@Override
	public ConfiguredAccommodationModel getAccommodation(final String uid)
	{
		final CatalogVersionModel catalogVersion = getCatalogVersion();
		try
		{
			return getConfiguredAccommodationDao().findAccommodation(uid, catalogVersion);
		}
		catch (final AccommodationMapDataSetUpException ex)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error(ex);
			}
			return null;
		}

	}

	/**
	 * Create the status parameters to find selected accommodations that belong to an order, for making accommodations
	 * already selected and already added to order(not in cancelled state), unavailable
	 *
	 * @param transportOffering
	 * 		as the transport offering
	 *
	 * @return the list of selected accommodation models
	 */
	protected List<SelectedAccommodationModel> getSelectedAccommodations(final TransportOfferingModel transportOffering)
	{
		final List<AccommodationStatus> selectedAccomStatuses = new ArrayList<>();
		selectedAccomStatuses.add(AccommodationStatus.OCCUPIED);
		selectedAccomStatuses.add(AccommodationStatus.UNAVAILABLE);
		final List<OrderStatus> cancelledOrderStatuses = new ArrayList<>();
		cancelledOrderStatuses.add(OrderStatus.CANCELLED);
		cancelledOrderStatuses.add(OrderStatus.CANCELLING);
		return getSelectedAccommodations(transportOffering, selectedAccomStatuses, cancelledOrderStatuses);

	}

	@Override
	public boolean isAccommodationAvailableForBooking(final ConfiguredAccommodationModel accommodation,
			final String transportOfferingCode, final TravellerData travellerData)
	{
		if (Objects.isNull(accommodation) || StringUtils.isBlank(transportOfferingCode) || !accommodation.isBookable())
		{
			return Boolean.FALSE;
		}

		final TransportOfferingModel transportOfferingModel = getTransportOfferingService()
				.getTransportOffering(transportOfferingCode);

		if (Objects.isNull(transportOfferingModel))
		{
			return Boolean.FALSE;
		}
		final List<SelectedAccommodationModel> alreadyBookedAccommodations = getSelectedAccommodations(transportOfferingModel);
		if (CollectionUtils.isNotEmpty(alreadyBookedAccommodations) && alreadyBookedAccommodations.stream()
				.anyMatch(bookedAccommodation -> bookedAccommodation.getConfiguredAccommodation().equals(accommodation)
						&& !bookedAccommodation.getTraveller().getSimpleUID().equals(travellerData.getSimpleUID())))
		{
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public boolean isSeatProductReferencedByFareProductInOrder(final ProductModel seatProduct, final String transportOfferingCode,
			final AbstractOrderModel abstractOrder)
	{
		if (Objects.isNull(seatProduct) || StringUtils.isBlank(transportOfferingCode) || Objects.isNull(abstractOrder))
		{
			return Boolean.FALSE;
		}
		final List<AbstractOrderEntryModel> entries = abstractOrder.getEntries().stream().filter(
				entry -> (entry.getProduct() instanceof FareProductModel) && entry.getTravelOrderEntryInfo().getTransportOfferings()
						.stream().anyMatch(transportOffering -> StringUtils.equals(transportOffering.getCode(), transportOfferingCode)))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(entries))
		{
			return Boolean.FALSE;
		}

		final Collection<ProductReferenceModel> productReferences = getProductReferenceService()
				.getProductReferencesForSourceAndTarget(entries.get(0).getProduct(), seatProduct, true);
		if (CollectionUtils.isEmpty(productReferences))
		{
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@Override
	public SelectedAccommodationModel getSelectedSeatForTraveller(final String travellerCode, final String transportOfferingCode,
			final AbstractOrderModel abstractOrder)
	{
		if (StringUtils.isBlank(travellerCode) || Objects.isNull(abstractOrder))
		{
			return null;
		}
		final Optional<SelectedAccommodationModel> previousSelectedSeat = abstractOrder.getSelectedAccommodations().stream()
				.filter(selectedAccommodation -> StringUtils.equals(travellerCode, selectedAccommodation.getTraveller().getLabel())
						&& StringUtils.equals(transportOfferingCode, selectedAccommodation.getTransportOffering().getCode()))
				.findAny();

		return previousSelectedSeat.orElse(null);
	}

	@Override
	public boolean isSeatInCart(final AbstractOrderModel abstractOrder,
	final ConfiguredAccommodationModel configuredAccommodationModel)
	{
		if (Objects.isNull(abstractOrder) || Objects.isNull(configuredAccommodationModel)
				|| CollectionUtils.isEmpty(abstractOrder.getSelectedAccommodations()))
		{
			return Boolean.FALSE;
		}
		return abstractOrder.getSelectedAccommodations().stream()
				.anyMatch(selectedAccommodation -> Objects
						.equals(selectedAccommodation.getConfiguredAccommodation(), configuredAccommodationModel));
	}

	/**
	 * Gets catalog version.
	 *
	 * @return the catalog version
	 */
	protected CatalogVersionModel getCatalogVersion()
	{
		final Collection<CatalogVersionModel> sessionCatalogVersions = getCatalogVersionService().getSessionCatalogVersions();
		if (CollectionUtils.isNotEmpty(sessionCatalogVersions))
		{
			return CommerceCatalogUtils.getActiveProductCatalogVersion(sessionCatalogVersions);
		}
		return null;
	}

	/**
	 * Gets transport vehicle configuration mapping dao.
	 *
	 * @return the transport vehicle configuration mapping dao
	 */
	protected TransportVehicleConfigurationMappingDao getTransportVehicleConfigurationMappingDao()
	{
		return transportVehicleConfigurationMappingDao;
	}

	/**
	 * Sets transport vehicle configuration mapping dao.
	 *
	 * @param transportVehicleConfigurationMappingDao
	 *           the transport vehicle configuration mapping dao
	 */
	@Required
	public void setTransportVehicleConfigurationMappingDao(
			final TransportVehicleConfigurationMappingDao transportVehicleConfigurationMappingDao)
	{
		this.transportVehicleConfigurationMappingDao = transportVehicleConfigurationMappingDao;
	}

	/**
	 * Gets transport vehicle info dao.
	 *
	 * @return the transport vehicle info dao
	 */
	protected TransportVehicleInfoDao getTransportVehicleInfoDao()
	{
		return transportVehicleInfoDao;
	}

	/**
	 * Sets transport vehicle info dao.
	 *
	 * @param transportVehicleInfoDao
	 *           the transport vehicle info dao
	 */
	@Required
	public void setTransportVehicleInfoDao(final TransportVehicleInfoDao transportVehicleInfoDao)
	{
		this.transportVehicleInfoDao = transportVehicleInfoDao;
	}

	/**
	 * Gets transport facility dao.
	 *
	 * @return the transport facility dao
	 */
	protected TransportFacilityDao getTransportFacilityDao()
	{
		return transportFacilityDao;
	}

	/**
	 * Sets transport facility dao.
	 *
	 * @param transportFacilityDao
	 *           the transport facility dao
	 */
	@Required
	public void setTransportFacilityDao(final TransportFacilityDao transportFacilityDao)
	{
		this.transportFacilityDao = transportFacilityDao;
	}

	/**
	 * Gets travel sector dao.
	 *
	 * @return the travel sector dao
	 */
	protected TravelSectorDao getTravelSectorDao()
	{
		return travelSectorDao;
	}

	/**
	 * Sets travel sector dao.
	 *
	 * @param travelSectorDao
	 *           the travel sector dao
	 */
	@Required
	public void setTravelSectorDao(final TravelSectorDao travelSectorDao)
	{
		this.travelSectorDao = travelSectorDao;
	}

	/**
	 * Gets travel route dao.
	 *
	 * @return the travel route dao
	 */
	protected TravelRouteDao getTravelRouteDao()
	{
		return travelRouteDao;
	}

	/**
	 * Sets travel route dao.
	 *
	 * @param travelRouteDao
	 *           the travel route dao
	 */
	@Required
	public void setTravelRouteDao(final TravelRouteDao travelRouteDao)
	{
		this.travelRouteDao = travelRouteDao;
	}

	/**
	 * Gets configured accommodation dao.
	 *
	 * @return the configured accommodation dao
	 */
	protected ConfiguredAccommodationDao getConfiguredAccommodationDao()
	{
		return configuredAccommodationDao;
	}

	/**
	 * Sets configured accommodation dao.
	 *
	 * @param configuredAccommodationDao
	 *           the configured accommodation dao
	 */
	@Required
	public void setConfiguredAccommodationDao(final ConfiguredAccommodationDao configuredAccommodationDao)
	{
		this.configuredAccommodationDao = configuredAccommodationDao;
	}

	/**
	 * Gets selected accommodation dao.
	 *
	 * @return the selected accommodation dao
	 */
	protected SelectedAccommodationDao getSelectedAccommodationDao()
	{
		return selectedAccommodationDao;
	}

	/**
	 * Sets selected accommodation dao.
	 *
	 * @param selectedAccommodationDao
	 *           the selected accommodation dao
	 */
	@Required
	public void setSelectedAccommodationDao(final SelectedAccommodationDao selectedAccommodationDao)
	{
		this.selectedAccommodationDao = selectedAccommodationDao;
	}

	/**
	 * Gets catalog version service.
	 *
	 * @return the catalog version service
	 */
	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * Sets catalog version service.
	 *
	 * @param catalogVersionService
	 *           the catalog version service
	 */
	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 *           the transportOfferingService to set
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	protected ProductReferenceService getProductReferenceService()
	{
		return productReferenceService;
	}

	@Required
	public void setProductReferenceService(final ProductReferenceService productReferenceService)
	{
		this.productReferenceService = productReferenceService;
	}

}
