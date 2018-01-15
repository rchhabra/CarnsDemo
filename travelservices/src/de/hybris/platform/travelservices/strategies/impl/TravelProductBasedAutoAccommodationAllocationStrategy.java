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

package de.hybris.platform.travelservices.strategies.impl;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleInfoModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.AutoAccommodationAllocationStrategy;
import de.hybris.platform.travelservices.utils.AccommodationEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Strategy class to auto allocate seats. This strategy follows one rule that is to allocate the first seat that is
 * available.
 *
 * Accommodation types are represented in the following hierarchy
 *
 * Decks -> Cabin -> Rows -> Columns -> Seats.
 *
 * So, finding the first seat available, resembles the traditional DFS(Depth first search) algorithm.
 */

public class TravelProductBasedAutoAccommodationAllocationStrategy implements AutoAccommodationAllocationStrategy
{
	private static final Logger LOG = Logger.getLogger(TravelProductBasedAutoAccommodationAllocationStrategy.class);

	private static final String NO_SEATS_AVAILABLE = "No Seats Available";

	private TravellerService travellerService;
	private AccommodationMapService accommodationMapService;
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;
	private TravelCommerceStockService commerceStockService;
	private ModelService modelService;
	private TravelCartService travelCartService;
	private ProductReferenceService productReferenceService;

	@Override
	public void autoAllocateSeat(final AbstractOrderModel abstractOrderModel, final Integer legNumber,
			final List<String> travellerReferences)
	{
		final List<TravellerModel> travellers = getTravellerService().getTravellersPerLeg(abstractOrderModel).get(legNumber);
		if (CollectionUtils.isNotEmpty(travellerReferences) && travellerReferences.size() != travellers.size())
		{
			travellers.removeIf(traveller -> !travellerReferences.contains(traveller.getUid()));
		}
		final List<TransportOfferingModel> transportOfferings = getTransportOfferings(abstractOrderModel, legNumber);

		final String travelRoute = getTravelRoute(abstractOrderModel, legNumber);

		final List<AbstractOrderEntryModel> fareProductEntries = travelCartService.getFareProductEntries(abstractOrderModel);

		final Map<TransportOfferingModel, Map<TravellerModel, ConfiguredAccommodationModel>> transportOfferingTravellerAccomodationMap = new HashMap<>();
		try
		{
			for (final TransportOfferingModel transportOffering : transportOfferings)
			{
				final Optional<TransportVehicleInfoModel> vehicleInfo = Optional.ofNullable(transportOffering.getTransportVehicle())
						.map(TransportVehicleModel::getTransportVehicleInfo);
				if (!vehicleInfo.isPresent())
				{
					LOG.warn("No vehicleInfo Available for TransportOffering : " + transportOffering.getCode());
					throw new AccommodationMapDataSetUpException(
							"No vehicleInfo Available for TransportOffering : " + transportOffering.getCode());
				}
				final long numberOfSeatsToAllocate = travellers.stream().filter(
						traveller -> !isSeatAllocatedForTravellerInTransportOffering(abstractOrderModel.getSelectedAccommodations(),
								transportOffering, traveller))
						.count();
				if (numberOfSeatsToAllocate <= travellers.size())
				{
					final Optional<AbstractOrderEntryModel> fareProductEntry = fareProductEntries.stream()
							.filter(fpe -> fpe.getTravelOrderEntryInfo().getTransportOfferings().stream()
									.anyMatch(transportOfferingFPE -> transportOfferingFPE.getCode().equals(transportOffering.getCode())))
							.findAny();
					if (fareProductEntry.isPresent())
					{
						getTransportOfferingTravelAccommodationMap(abstractOrderModel, travellers, travelRoute,
								transportOfferingTravellerAccomodationMap, transportOffering, vehicleInfo, numberOfSeatsToAllocate,
								fareProductEntry.get().getProduct());
					}
				}
			}

		}
		catch (final AccommodationMapDataSetUpException accomExec)
		{
			LOG.error("Exception in Auto allocating seat", accomExec);
		}
		saveAllocatedAccomodations(transportOfferingTravellerAccomodationMap, abstractOrderModel);
	}

	protected void getTransportOfferingTravelAccommodationMap(final AbstractOrderModel abstractOrderModel,
			final List<TravellerModel> travellers, final String travelRoute,
			final Map<TransportOfferingModel, Map<TravellerModel, ConfiguredAccommodationModel>> transportOfferingTravellerAccomodationMap,
			final TransportOfferingModel transportOffering, final Optional<TransportVehicleInfoModel> vehicleInfo,
			final long numberOfSeatsToAllocate, final ProductModel fareProduct)
	{
		if (!vehicleInfo.isPresent())
		{
			LOG.warn("No vehicleInfo Available for TransportOffering : " + transportOffering.getCode());
			throw new AccommodationMapDataSetUpException(
					"No vehicleInfo Available for TransportOffering : " + transportOffering.getCode());
		}
		final AccommodationMapModel accommodationMap = getAccommodationMapService().getAccommodationMap(vehicleInfo.get().getCode(),
				transportOffering, travelRoute);

		final List<ConfiguredAccommodationModel> seatMapConfigurations = getAccommodationMapConfiguration(accommodationMap);
		final List<SelectedAccommodationModel> selectedAccommodations = getSelectedAccommodations(transportOffering);
		final Map<TravellerModel, ConfiguredAccommodationModel> travellerAccommodationMap = new HashMap<>();
		travellers.forEach(traveller -> {
			if (!isSeatAllocatedForTravellerInTransportOffering(abstractOrderModel.getSelectedAccommodations(), transportOffering,
					traveller))
			{
				getFirstAvailableSeat(seatMapConfigurations, true, null, selectedAccommodations, transportOffering,
						travellerAccommodationMap, traveller, fareProduct);
				if (CollectionUtils.isEmpty(travellerAccommodationMap.values()))
				{
					LOG.warn(NO_SEATS_AVAILABLE);
					throw new AccommodationMapDataSetUpException(NO_SEATS_AVAILABLE);
				}
			}
		});
		if (travellerAccommodationMap.size() < numberOfSeatsToAllocate)
		{
			LOG.warn(
					"No enough Seats Available, For transport offering " + transportOffering.getCode() + ", No of seats to allocate : "
							+ numberOfSeatsToAllocate + ", number of seat available " + travellerAccommodationMap.size());
			throw new AccommodationMapDataSetUpException(NO_SEATS_AVAILABLE);
		}
		transportOfferingTravellerAccomodationMap.put(transportOffering, travellerAccommodationMap);
	}

	/**
	 * Once all seats are found, creates SelectedAccommodationModel for each traveller for which we need to auto allocate
	 * and save them.
	 *
	 * @param transportOfferingTravellerAccomodationMap
	 *           a Map of TransportOfferings as key and Map as value.
	 * @param abstractOrderModel
	 *           AbstractOrderModel object.
	 */
	protected void saveAllocatedAccomodations(
			final Map<TransportOfferingModel, Map<TravellerModel, ConfiguredAccommodationModel>> transportOfferingTravellerAccomodationMap,
			final AbstractOrderModel abstractOrderModel)
	{
		final List<SelectedAccommodationModel> selectedAccomodations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(abstractOrderModel.getSelectedAccommodations()))
		{
			selectedAccomodations.addAll(abstractOrderModel.getSelectedAccommodations());
		}
		transportOfferingTravellerAccomodationMap.entrySet()
				.forEach(transportOfferingEntry -> transportOfferingEntry.getValue().entrySet().forEach(travellerEntry -> {
					final SelectedAccommodationModel allocatedAccomodationModel = getModelService()
							.create(SelectedAccommodationModel.class);
					allocatedAccomodationModel.setConfiguredAccommodation(travellerEntry.getValue());
					allocatedAccomodationModel.setTraveller(travellerEntry.getKey());
					allocatedAccomodationModel.setTransportOffering(transportOfferingEntry.getKey());
					allocatedAccomodationModel.setOrder(abstractOrderModel);
					allocatedAccomodationModel.setStatus(AccommodationStatus.OCCUPIED);
					selectedAccomodations.add(allocatedAccomodationModel);
					getModelService().save(allocatedAccomodationModel);
				}));
		abstractOrderModel.setSelectedAccommodations(selectedAccomodations);
		getModelService().save(abstractOrderModel);
	}

	/**
	 * This method gets called recursively until we reach the bottom of accommodation ladder(Seats) and an unoccupied
	 * seat is found.
	 *
	 * Each time a search is performed on an accommodation type(ie, Decks or cabins or Rows or Columns) they are sorted
	 * as defined in configuredAccomNumberComparator.
	 *
	 * Also, the process is constantly checked if the accommodations data are in correct hierarchy. Throws
	 * AccommodationMapDataSetUpException if the data is not in correct hierarchy.
	 *
	 * Once the seat is found store it in travellerAccommodationMap against the traveller.
	 *
	 * @param accomodationConfigurations
	 * @param isFirstAttempt
	 * @param previousAccommodationType
	 * @param selectedAccommodations
	 * @param transportOffering
	 * @param travellerAccommodationMap
	 * @param traveller
	 * @param fareProduct
	 * @return
	 */
	protected ConfiguredAccommodationModel getFirstAvailableSeat(
			final List<ConfiguredAccommodationModel> accomodationConfigurations, final boolean isFirstAttempt,
			final ConfiguredAccommodationType previousAccommodationType,
			final List<SelectedAccommodationModel> selectedAccommodations, final TransportOfferingModel transportOffering,
			final Map<TravellerModel, ConfiguredAccommodationModel> travellerAccommodationMap, final TravellerModel traveller,
			final ProductModel fareProduct)
	{
		if (travellerAccommodationMap.get(traveller) != null)
		{
			return travellerAccommodationMap.get(traveller);
		}
		final List<ConfiguredAccommodationModel> modifiableSeatMapConfigurations = new ArrayList<>(accomodationConfigurations);
		if (!accomodationConfigurations.stream()
				.anyMatch(accommodationModel -> accommodationModel.getType().equals(ConfiguredAccommodationType.SEAT)))
		{
			Collections.sort(modifiableSeatMapConfigurations, getConfiguredAccomNumberComparator());
		}
		for (final ConfiguredAccommodationModel configuredAccomModel : modifiableSeatMapConfigurations)
		{
			final ConfiguredAccommodationModel seat = findAvailableSeat(isFirstAttempt, previousAccommodationType,
					selectedAccommodations, transportOffering, travellerAccommodationMap, traveller, configuredAccomModel,
					fareProduct);
			if (seat != null)
			{
				return seat;
			}
		}
		return null;
	}

	protected ConfiguredAccommodationModel findAvailableSeat(final boolean isFirstAttempt,
			final ConfiguredAccommodationType previousAccommodationType,
			final List<SelectedAccommodationModel> selectedAccommodations, final TransportOfferingModel transportOffering,
			final Map<TravellerModel, ConfiguredAccommodationModel> travellerAccommodationMap, final TravellerModel traveller,
			final ConfiguredAccommodationModel configuredAccomModel, final ProductModel fareProduct)
	{
		if (AccommodationEnum.areAccommodationsInCorrectHierarchy(
				isFirstAttempt ? AccommodationEnum.mapConfiguredAccommodationTypeToAccommodationEnum(configuredAccomModel.getType())
						: AccommodationEnum.mapConfiguredAccommodationTypeToAccommodationEnum(previousAccommodationType),
				AccommodationEnum.mapConfiguredAccommodationTypeToAccommodationEnum(configuredAccomModel.getType())))
		{
			if (CollectionUtils.isNotEmpty(configuredAccomModel.getConfiguredAccommodation()))
			{
				final ConfiguredAccommodationModel seat = getFirstAvailableSeat(configuredAccomModel.getConfiguredAccommodation(),
						false, configuredAccomModel.getType(), selectedAccommodations, transportOffering, travellerAccommodationMap,
						traveller, fareProduct);
				if (seat != null)
				{
					return seat;
				}
			}
			else
			{
				if (configuredAccomModel.getType().equals(ConfiguredAccommodationType.SEAT)
						&& isSeatEnabledForProduct(fareProduct, configuredAccomModel)
						&& isSeatAvailable(configuredAccomModel, selectedAccommodations, transportOffering, travellerAccommodationMap))
				{
					travellerAccommodationMap.put(traveller, configuredAccomModel);
					LOG.info("Auto Allocation Seat found " + configuredAccomModel.getIdentifier());
					return configuredAccomModel;
				}
			}
		}
		else
		{
			LOG.warn("Accomodations are not in Corrected hierachy. Found " + configuredAccomModel.getType() + " after "
					+ previousAccommodationType);
			throw new AccommodationMapDataSetUpException("Accomodations are not in Corrected hierachy.");
		}
		return null;
	}

	/**
	 * This method returns true either AccommodationProduct doesn’t exists for the seat or seat is configured in
	 * productReference for the specified fareProduct
	 *
	 * @param fareProduct
	 * @param seat
	 * @return
	 */
	protected boolean isSeatEnabledForProduct(final ProductModel fareProduct, final ConfiguredAccommodationModel seat)
	{
		if (seat.getProduct() == null)
		{
			return true;
		}

		final Collection<ProductReferenceModel> productReferences = productReferenceService
				.getProductReferencesForSourceAndTarget(fareProduct, seat.getProduct(), true);
		if (CollectionUtils.isNotEmpty(productReferences))
		{
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if the seat is available, as in
	 *
	 * The stock for the seat product is greater than O.
	 *
	 * The seat is not an already selected seat in the same transport offering.
	 *
	 * And it is not allocated to the one of the travellers in the current allocation process.
	 *
	 * @param seat
	 * @param selectedAccommodations
	 * @param transportOffering
	 * @param travellerAccommodationMap
	 * @return
	 */
	protected boolean isSeatAvailable(final ConfiguredAccommodationModel seat,
			final List<SelectedAccommodationModel> selectedAccommodations, final TransportOfferingModel transportOffering,
			final Map<TravellerModel, ConfiguredAccommodationModel> travellerAccommodationMap)
	{
		final Long stock = getCommerceStockService().getStockLevel(seat.getProduct(),
				Stream.of(transportOffering).collect(Collectors.<TransportOfferingModel> toList()));
		if (stock > 0L && !selectedAccommodations.stream().anyMatch(selectedAccomodation -> selectedAccomodation
				.getConfiguredAccommodation().getIdentifier().equals(seat.getIdentifier()))
				&& !travellerAccommodationMap.values().contains(seat))
		{
			return true;
		}
		return false;
	}

	/**
	 * This method returns the travel route for the given leg.
	 *
	 * @param abstractOrderModel
	 * @param legNumber
	 * @return
	 */
	protected String getTravelRoute(final AbstractOrderModel abstractOrderModel, final Integer legNumber)
	{
		final Optional<AbstractOrderEntryModel> orderEntry = abstractOrderModel.getEntries().stream()
				.filter(entry -> ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber().equals(legNumber)).findFirst();
		return orderEntry.isPresent() && Objects.nonNull(orderEntry.get().getTravelOrderEntryInfo())
				&& Objects.nonNull(orderEntry.get().getTravelOrderEntryInfo().getTravelRoute())
						? orderEntry.get().getTravelOrderEntryInfo().getTravelRoute().getCode() : StringUtils.EMPTY;
	}

	/**
	 * This method returns a list of transportOfferings for the given leg.
	 *
	 * @param abstractOrderModel
	 * @param legNumber
	 * @return
	 */
	protected List<TransportOfferingModel> getTransportOfferings(final AbstractOrderModel abstractOrderModel,
			final Integer legNumber)
	{
		final List<TransportOfferingModel> transportOfferings = new ArrayList<>();

		abstractOrderModel.getEntries().stream()
				.filter(entry -> ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber().equals(legNumber))
				.forEach(entry -> transportOfferings.addAll(entry.getTravelOrderEntryInfo().getTransportOfferings()));

		return transportOfferings.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * This method returns true if the traveller has already been allocated (or selected) a seat.
	 *
	 * @param selectedAccommodations
	 * @param transportOffering
	 * @param traveller
	 * @return
	 */
	protected boolean isSeatAllocatedForTravellerInTransportOffering(final List<SelectedAccommodationModel> selectedAccommodations,
			final TransportOfferingModel transportOffering, final TravellerModel traveller)
	{
		if (CollectionUtils.isNotEmpty(selectedAccommodations))
		{
			return selectedAccommodations.stream().anyMatch(
					selectedAccommodation -> selectedAccommodation.getTransportOffering().getCode().equals(transportOffering.getCode())
							&& selectedAccommodation.getTraveller().getUid().equals(traveller.getUid()));
		}
		return false;
	}

	/**
	 * Gets and return list of configured accommodations belonging to a accommodation map
	 *
	 * @param accommodationMap
	 * @return
	 */
	protected List<ConfiguredAccommodationModel> getAccommodationMapConfiguration(final AccommodationMapModel accommodationMap)
			throws AccommodationMapDataSetUpException
	{
		final List<ConfiguredAccommodationModel> seatMapConfiguration = getAccommodationMapService()
				.getAccommodationMapConfiguration(accommodationMap);
		if (CollectionUtils.isEmpty(seatMapConfiguration))
		{
			LOG.error("No Seat map configuration found for vehicle configuration : " + accommodationMap);
			throw new AccommodationMapDataSetUpException(
					"No Seat map configuration found for vehicle configuration : " + accommodationMap);
		}
		return seatMapConfiguration;
	}

	/**
	 * Create the status parameters to find selected accommodations that belong to an order, for making accommodations
	 * already selected and already added to order(not in cancelled state), unavailable
	 *
	 * @param transportOffering
	 * @return
	 */
	protected List<SelectedAccommodationModel> getSelectedAccommodations(final TransportOfferingModel transportOffering)
	{
		final List<AccommodationStatus> selectedAccomStatuses = Stream
				.of(AccommodationStatus.OCCUPIED, AccommodationStatus.UNAVAILABLE).collect(Collectors.<AccommodationStatus> toList());

		final List<OrderStatus> cancelledOrderStatuses = Stream.of(OrderStatus.CANCELLED, OrderStatus.CANCELLING)
				.collect(Collectors.<OrderStatus> toList());

		return getAccommodationMapService().getSelectedAccommodations(transportOffering, selectedAccomStatuses,
				cancelledOrderStatuses);

	}

	/**
	 * @return the travellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * @param travellerService
	 *           the travellerService to set
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * @return the accommodationMapService
	 */
	protected AccommodationMapService getAccommodationMapService()
	{
		return accommodationMapService;
	}

	/**
	 * @param accommodationMapService
	 *           the accommodationMapService to set
	 */
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
	}

	/**
	 * @return the configuredAccomNumberComparator
	 */
	protected Comparator<ConfiguredAccommodationModel> getConfiguredAccomNumberComparator()
	{
		return configuredAccomNumberComparator;
	}

	/**
	 * @param configuredAccomNumberComparator
	 *           the configuredAccomNumberComparator to set
	 */
	public void setConfiguredAccomNumberComparator(final Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator)
	{
		this.configuredAccomNumberComparator = configuredAccomNumberComparator;
	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the productReferenceService
	 */
	protected ProductReferenceService getProductReferenceService()
	{
		return productReferenceService;
	}

	/**
	 * @param productReferenceService
	 *           the productReferenceService to set
	 */
	public void setProductReferenceService(final ProductReferenceService productReferenceService)
	{
		this.productReferenceService = productReferenceService;
	}
}
