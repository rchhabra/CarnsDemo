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
package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.fare.search.handlers.seatavailability.SeatAvailabilityKey;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler checks the bundle availability based on the availability of the seats.
 */
public class BundleSeatsAvailabilityHandler implements FareSearchHandler
{
	private static final Logger LOG = Logger.getLogger(BundleSeatsAvailabilityHandler.class);

	private AccommodationMapService accommodationMapService;
	private TransportOfferingService transportOfferingService;
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;
	private ProductReferenceService productReferenceService;
	private ProductService productService;
	private TravelCommerceStockService commerceStockService;
	private ConfigurationService configurationService;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		if (BooleanUtils.isFalse(getConfigurationService().getConfiguration()
				.getBoolean(TravelfacadesConstants.CONFIG_BUNDLE_SEAT_AVAILABILITY_CHECK)))
		{
			return;
		}

		final int nPax = getTotalNumberOfPassengers(fareSearchRequestData.getPassengerTypes());

		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{

			if (!pricedItinerary.isAvailable())
			{
				continue;
			}

			final Map<SeatAvailabilityKey, Integer> availabilityMap = calculateAvailableSeats(pricedItinerary);

			for (final OriginDestinationOptionData originDestinationOption : pricedItinerary.getItinerary()
					.getOriginDestinationOptions())
			{
				for (final TransportOfferingData to : originDestinationOption.getTransportOfferings())
				{
					pricedItinerary.getItineraryPricingInfos().forEach(itineraryPricingInfoData ->
					{

						final SeatAvailabilityKey key = new SeatAvailabilityKey(pricedItinerary.getId(), to.getCode(),
								itineraryPricingInfoData.getBundleTemplates().stream().map(TravelBundleTemplateData::getId)
										.collect(Collectors.toList()));

						if (availabilityMap.containsKey(key))
						{
							if (availabilityMap.get(key) < nPax)
							{
								itineraryPricingInfoData.getBundleTemplates()
										.forEach(travelBundleTemplateData -> travelBundleTemplateData.setAvailable(false));
								itineraryPricingInfoData.setAvailable(false);
							}
						}
					});
				}
			}
		}
	}

	/**
	 * Populate seats map.
	 *
	 * @param pricedItinerary the priced itinerary
	 * @return the int
	 */
	protected Map<SeatAvailabilityKey, Integer> calculateAvailableSeats(final PricedItineraryData pricedItinerary)
	{

		final Map<SeatAvailabilityKey, Integer> seatAvailabilityMap = new HashMap<>();

		for (final OriginDestinationOptionData originDestinationOption : pricedItinerary.getItinerary()
				.getOriginDestinationOptions())
		{
			for (final TransportOfferingData to : originDestinationOption.getTransportOfferings())
			{
				int toAvailableSeats;

				final TransportVehicleData transportVehicleData = getTransportVehicle(to);
				final TransportVehicleInfoData vehicleInfoData = getTransportVehicleInfo(transportVehicleData);

				final String vehicleInfoCode = vehicleInfoData.getCode();

				if (StringUtils.isEmpty(vehicleInfoCode))
				{
					continue;
				}

				final String transportOfferingCode = to.getCode();
				final TransportOfferingModel transportOffering = getTransportOfferingService()
						.getTransportOffering(transportOfferingCode);
				final AccommodationMapModel accommodationMap = getAccommodationMap(pricedItinerary.getItinerary(), transportOffering,
						to.getSector(), vehicleInfoCode);

				final List<ConfiguredAccommodationModel> accommodationMapConfiguration = getAccommodationMapConfiguration(
						accommodationMap);
				final List<SelectedAccommodationModel> selectedAccommodations = getSelectedAccommodations(transportOffering);

				// Reading the structure of the map
				final List<ConfiguredAccommodationModel> modifiableSeatMapConfigurations = new ArrayList<>();
				modifiableSeatMapConfigurations.addAll(accommodationMapConfiguration);
				modifiableSeatMapConfigurations.sort(getConfiguredAccomNumberComparator());

				for (final ConfiguredAccommodationModel seatMapConfiguration : modifiableSeatMapConfigurations)
				{
					if (!ConfiguredAccommodationType.DECK.equals(seatMapConfiguration.getType()))
					{
						LOG.error("Expected DECK found: " + seatMapConfiguration.getType());
						continue;
					}

					if (CollectionUtils.isEmpty(seatMapConfiguration.getConfiguredAccommodation()))
					{
						LOG.error("No cabins defined");
						continue;
					}

					final List<ConfiguredAccommodationModel> cabins = new ArrayList<>();
					cabins.addAll(seatMapConfiguration.getConfiguredAccommodation());
					cabins.sort(getConfiguredAccomNumberComparator());

					for (final ConfiguredAccommodationModel cabin : cabins)
					{
						if (!ConfiguredAccommodationType.CABIN.equals(cabin.getType()))
						{
							LOG.error("Expected CABIN, found: " + cabin.getType());
							continue;
						}

						final List<ConfiguredAccommodationModel> rows = new ArrayList<>();
						rows.addAll(cabin.getConfiguredAccommodation());
						rows.sort(getConfiguredAccomNumberComparator());

						if (CollectionUtils.isEmpty(rows))
						{
							LOG.error("No rows found.");
							continue;
						}

						for (final ConfiguredAccommodationModel row : rows)
						{
							if (!ConfiguredAccommodationType.ROW.equals(row.getType()))
							{
								LOG.error("Expected ROW found, " + row.getType());
								continue;
							}

							final List<ConfiguredAccommodationModel> columns = row.getConfiguredAccommodation();
							final List<ConfiguredAccommodationModel> modifiableColumns = new ArrayList<>();
							modifiableColumns.addAll(columns);
							modifiableColumns.sort(getConfiguredAccomNumberComparator());

							for (final ConfiguredAccommodationModel column : modifiableColumns)
							{
								if (!ConfiguredAccommodationType.COLUMN.equals(column.getType()))
								{
									LOG.error("Expected COLUMN, found: " + column.getType());
									continue;
								}

								final List<ConfiguredAccommodationModel> seats = column.getConfiguredAccommodation();

								if (CollectionUtils.isEmpty(seats))
								{
									LOG.error("Expected at least one SEAT... none found");
									continue;
								}

								// Calculate Free TO available
								toAvailableSeats = calculateFreeToAvailableSeats(selectedAccommodations, seats);

								// Calculate the total of available seats for bundles
								for (final ItineraryPricingInfoData ipi : pricedItinerary.getItineraryPricingInfos())
								{
									if (!ipi.isAvailable())
									{
										continue;
									}

									final OptionalInt minBTSeats = ipi.getBundleTemplates().stream().mapToInt(
											value -> calculateAvailableSeats(seats, selectedAccommodations, value, transportOffering))
											.min();

									final SeatAvailabilityKey key = new SeatAvailabilityKey(pricedItinerary.getId(), to.getCode(),
											ipi.getBundleTemplates().stream().map(BundleTemplateData::getId).collect(Collectors.toList()));

									if (seatAvailabilityMap.containsKey(key))
									{
										final int totalSeatPerIpi = seatAvailabilityMap.get(key) + toAvailableSeats + minBTSeats
												.getAsInt();
										seatAvailabilityMap.put(key, totalSeatPerIpi);
									}
									else
									{
										seatAvailabilityMap.put(key, toAvailableSeats + minBTSeats.getAsInt());
									}
								}
							}
						}
					}
				}
			}
		}

		return seatAvailabilityMap;
	}

	/**
	 * Calculate available seats int.
	 *
	 * @param seats                  the seats
	 * @param selectedAccommodations the selected accommodations
	 * @param btd                    the btd
	 * @param transportOffering      the transport offering
	 * @return the int
	 */
	protected int calculateAvailableSeats(final List<ConfiguredAccommodationModel> seats,
			final List<SelectedAccommodationModel> selectedAccommodations, final TravelBundleTemplateData btd,
			final TransportOfferingModel transportOffering)
	{
		if (CollectionUtils.isEmpty(btd.getFareProducts()))
		{
			return 0;
		}

		int btAvailableSeats = 0;

		final ProductModel fareProduct = getProductService().getProductForCode(btd.getFareProducts().get(0).getCode());

		for (final ConfiguredAccommodationModel seat : seats)
		{
			if (!ConfiguredAccommodationType.SEAT.equals(seat.getType()))
			{
				LOG.error("Expected SEAT, found: " + seat.getType());
				continue;
			}

			if (seat.getProduct() == null)
			{
				continue;
			}

			if (isSeatAvailable(seat, selectedAccommodations, fareProduct, transportOffering))
			{
				btAvailableSeats++;
			}
		}

		return btAvailableSeats;
	}

	/**
	 * Check if current seat is available. The seat is available if there's at least a product reference with current Fare
	 * Product, seat hasn't been selected previously on the Transport Offering and stocklevel > 0
	 *
	 * @param seat                   the seat
	 * @param selectedAccommodations the selected accommodations
	 * @param fareProduct            the fare product
	 * @param transportOffering      the transport offering
	 * @return the boolean
	 */
	protected boolean isSeatAvailable(final ConfiguredAccommodationModel seat,
			final List<SelectedAccommodationModel> selectedAccommodations, final ProductModel fareProduct,
			final TransportOfferingModel transportOffering)
	{
		final Collection<ProductReferenceModel> productReferences = getProductReferenceService()
				.getProductReferencesForSourceAndTarget(fareProduct, seat.getProduct(), true);

		if (CollectionUtils.isEmpty(productReferences) || selectedAccommodations.stream().anyMatch(selectedAccommodation -> Objects
				.equals(seat.getIdentifier(), selectedAccommodation.getConfiguredAccommodation().getIdentifier())))
		{
			return false;
		}

		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
		transportOfferings.add(transportOffering);
		final Long stock = getCommerceStockService().getStockLevel(seat.getProduct(), transportOfferings);

		return !(stock != null && stock == 0L);
	}

	/**
	 * Calculate free to available seats int.
	 *
	 * @param selectedAccommodations the selected accommodations
	 * @param seats                  the seats
	 * @return the int
	 */
	protected int calculateFreeToAvailableSeats(final List<SelectedAccommodationModel> selectedAccommodations,
			final List<ConfiguredAccommodationModel> seats)
	{
		int toAvailableSeats = 0;
		for (final ConfiguredAccommodationModel seat : seats)
		{
			if (!ConfiguredAccommodationType.SEAT.equals(seat.getType()))
			{
				LOG.error("Expected SEAT, found: " + seat.getType());
				continue;
			}

			if (isSeatAvailable(seat, selectedAccommodations))
			{
				toAvailableSeats++;
			}
		}
		return toAvailableSeats;
	}

	/**
	 * Check if seat is available: if seat is not linked to any product and hasn't been previously selected
	 *
	 * @param seat                   the seat
	 * @param selectedAccommodations the selected accommodations
	 * @return the boolean
	 */
	protected boolean isSeatAvailable(final ConfiguredAccommodationModel seat,
			final List<SelectedAccommodationModel> selectedAccommodations)
	{
		// No seat product linked and seat was not selected
		return seat.getProduct() == null && selectedAccommodations.stream().noneMatch(selectedAccommodation -> Objects
				.equals(seat.getIdentifier(), selectedAccommodation.getConfiguredAccommodation().getIdentifier()));
	}


	/**
	 * Gets and returns Transport vehicle info data from the transport vehicle data passed as param
	 *
	 * @param transportVehicle the transport vehicle
	 * @return transport vehicle info
	 */
	protected TransportVehicleInfoData getTransportVehicleInfo(final TransportVehicleData transportVehicle)
	{
		final TransportVehicleInfoData vehicleInfo = transportVehicle.getVehicleInfo();
		if (vehicleInfo == null)
		{
			LOG.error("No vehicle info associated with transportVehicle while creating seat map");
			throw new AccommodationMapDataSetUpException("No vehicle info associated with transportVehicle while creating seat "
					+ "map");
		}
		return vehicleInfo;
	}

	/**
	 * Gets and returns Transport vehicle associated with a transport offering
	 *
	 * @param transportOfferingData the transport offering data
	 * @return transport vehicle
	 */
	protected TransportVehicleData getTransportVehicle(final TransportOfferingData transportOfferingData)
	{
		final TransportVehicleData transportVehicle = transportOfferingData.getTransportVehicle();
		if (transportVehicle == null)
		{
			LOG.error("No transportVehicle found for transport offering :" + transportOfferingData.getCode()
					+ " while creating seat map");
			throw new AccommodationMapDataSetUpException(
					"No transportVehicle found for transport offering :" + transportOfferingData.getCode()
							+ " while creating seat map");
		}
		return transportVehicle;
	}

	/**
	 * This methods works out the total number of passenger for the given list of Passenger Type Quantity
	 *
	 * @param passengerTypes the list of Passenger Type Quantity for the current Search Request
	 * @return the total number of passengers
	 */
	protected int getTotalNumberOfPassengers(final List<PassengerTypeQuantityData> passengerTypes)
	{
		int nPax = 0;
		for (final PassengerTypeQuantityData passengerTypeQuantity : passengerTypes)
		{
			nPax += passengerTypeQuantity.getQuantity();
		}
		return nPax;
	}

	/**
	 * Gets and return accommodation map for transport offering, route, sector and vehicle info code
	 *
	 * @param itineraryData     the itinerary data
	 * @param transportOffering the transport offering
	 * @param travelSector      the travel sector
	 * @param vehicleInfoCode   the vehicle info code
	 * @return accommodation map
	 */
	protected AccommodationMapModel getAccommodationMap(final ItineraryData itineraryData,
			final TransportOfferingModel transportOffering, final TravelSectorData travelSector, final String vehicleInfoCode)
	{
		final AccommodationMapModel accommodationMap = getAccommodationMapService()
				.getAccommodationMap(vehicleInfoCode, transportOffering, itineraryData.getRoute().getCode(), travelSector);
		if (accommodationMap == null)
		{
			LOG.error("No accommodation map found for configuration code : " + vehicleInfoCode + " , transport offering :"
					+ transportOffering.getCode() + " , route : " + itineraryData.getRoute().getCode() + " , sector : " + travelSector
					.getOrigin() + "_" + travelSector.getDestination() + " while creating seat map");
			throw new AccommodationMapDataSetUpException(
					"No accommodation map found for configuration code : " + vehicleInfoCode + " , transport offering :"
							+ transportOffering.getCode() + " , route : " + itineraryData.getRoute().getCode() + " , sector : "
							+ travelSector.getOrigin() + "_" + travelSector.getDestination() + " while creating seat map");
		}
		return accommodationMap;
	}

	/**
	 * Gets and return list of configured accommodations belonging to an accommodation map
	 *
	 * @param accommodationMap the accommodation map
	 * @return accommodation map configuration
	 */
	protected List<ConfiguredAccommodationModel> getAccommodationMapConfiguration(final AccommodationMapModel accommodationMap)
	{
		final List<ConfiguredAccommodationModel> seatMapConfiguration = accommodationMapService
				.getAccommodationMapConfiguration(accommodationMap);
		if (CollectionUtils.isEmpty(seatMapConfiguration))
		{
			LOG.error("No Seat map configuration found for accommodation map : " + accommodationMap);
			throw new AccommodationMapDataSetUpException(
					"No Seat map configuration found for accommodation map : " + accommodationMap);
		}
		return seatMapConfiguration;
	}

	/**
	 * Create the status parameters to find selected accommodations that belong to an order, for making accommodations
	 * already selected and already added to order(not in cancelled state), unavailable
	 *
	 * @param transportOffering the transport offering
	 * @return selected accommodations
	 */
	protected List<SelectedAccommodationModel> getSelectedAccommodations(final TransportOfferingModel transportOffering)
	{
		final List<AccommodationStatus> selectedAccomStatuses = new ArrayList<>();
		selectedAccomStatuses.add(AccommodationStatus.OCCUPIED);
		selectedAccomStatuses.add(AccommodationStatus.UNAVAILABLE);
		final List<OrderStatus> cancelledOrderStatuses = new ArrayList<>();
		cancelledOrderStatuses.add(OrderStatus.CANCELLED);
		cancelledOrderStatuses.add(OrderStatus.CANCELLING);
		return accommodationMapService.getSelectedAccommodations(transportOffering, selectedAccomStatuses, cancelledOrderStatuses);

	}

	/**
	 * Gets accommodation map service.
	 *
	 * @return the accommodation map service
	 */
	protected AccommodationMapService getAccommodationMapService()
	{
		return accommodationMapService;
	}

	/**
	 * Sets accommodation map service.
	 *
	 * @param accommodationMapService the accommodation map service
	 */
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transport offering service
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService the transport offering service
	 */
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * Gets configured accom number comparator.
	 *
	 * @return the configured accom number comparator
	 */
	protected Comparator<ConfiguredAccommodationModel> getConfiguredAccomNumberComparator()
	{
		return configuredAccomNumberComparator;
	}

	/**
	 * Sets configured accom number comparator.
	 *
	 * @param configuredAccomNumberComparator the configured accom number comparator
	 */
	public void setConfiguredAccomNumberComparator(final Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator)
	{
		this.configuredAccomNumberComparator = configuredAccomNumberComparator;
	}

	/**
	 * Gets product reference service.
	 *
	 * @return the product reference service
	 */
	protected ProductReferenceService getProductReferenceService()
	{
		return productReferenceService;
	}

	/**
	 * Sets product reference service.
	 *
	 * @param productReferenceService the product reference service
	 */
	public void setProductReferenceService(final ProductReferenceService productReferenceService)
	{
		this.productReferenceService = productReferenceService;
	}

	/**
	 * Gets product service.
	 *
	 * @return the product service
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Sets product service.
	 *
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * Gets commerce stock service.
	 *
	 * @return the commerce stock service
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * Sets commerce stock service.
	 *
	 * @param commerceStockService the commerce stock service
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
