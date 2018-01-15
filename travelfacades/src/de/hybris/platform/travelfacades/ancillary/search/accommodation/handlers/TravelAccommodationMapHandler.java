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

package de.hybris.platform.travelfacades.ancillary.search.accommodation.handlers;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TransportVehicleData;
import de.hybris.platform.commercefacades.travel.TransportVehicleInfoData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.CabinData;
import de.hybris.platform.commercefacades.travel.seatmap.data.RowInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatAvailabilityData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatFeatureData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapDetailData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.DeckType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.seating.AircraftCabinModel;
import de.hybris.platform.travelservices.model.seating.AircraftDeckModel;
import de.hybris.platform.travelservices.model.travel.AccommodationCharacteristicsModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.ProximityItemModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler to populate seat map options for a transport vehicle retrieved from DB based on mapping between the vehicle
 * config and configured accommodations
 */
public class TravelAccommodationMapHandler implements AncillarySearchHandler
{


	private static final Logger LOG = Logger.getLogger(TravelAccommodationMapHandler.class);

	private AccommodationMapService accommodationMapService;
	private TransportOfferingService transportOfferingService;
	private TravelCommerceStockService commerceStockService;
	private TravelCommercePriceService travelCommercePriceService;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator;
	private Comparator<SeatInfoData> accommodationInfoDataComparator;
	private Converter<TravellerModel, TravellerData> travellerDataConverter;
	private ProductReferenceService productReferenceService;
	private ProductService productService;
	private BiPredicate<SeatAvailabilityData, SeatInfoData> seatPredicate;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;


	/**
	 * Populates seat map based on following rules :
	 *
	 * 1) Get all itineraries from request
	 *
	 * 2) Get origindestinationoption for each itinerary and then for each transport offering in origindestinationoption
	 * get the transport vehicle to retrieve seat map for that vehicle
	 *
	 * 3) Using vehicle info code and transport offering, route and sector, accommodation map is retrieved
	 *
	 * 4) Finally all configured accommodation(decks, cabins, rows, columns and seats) are retrieved and populated in the
	 * AccommodationMapResponseData in respective DTOs.
	 */
	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		offerResponseData.setSeatMap(createAccommodationMapResonseData());

		final List<ItineraryData> itineraries = offerRequestData.getItineraries();
		if (CollectionUtils.isEmpty(itineraries))
		{
			return;
		}
		for (final ItineraryData itineraryData : itineraries)
		{
			final List<OriginDestinationOptionData> originDestinationOptions = itineraryData.getOriginDestinationOptions();
			for (final OriginDestinationOptionData originDestinationOptionData : originDestinationOptions)
			{
				populateAccomodationMapForODOptions(offerRequestData, offerResponseData, itineraryData, originDestinationOptionData);
			}
		}
	}

	protected void populateAccomodationMapForODOptions(final OfferRequestData offerRequestData,
			final OfferResponseData offerResponseData, final ItineraryData itineraryData,
			final OriginDestinationOptionData originDestinationOptionData)
	{
		final List<TransportOfferingData> transportOfferings = originDestinationOptionData.getTransportOfferings();
		for (final TransportOfferingData transportOfferingData : transportOfferings)
		{
			final TransportVehicleData transportVehicleData = getTransportVehicle(offerRequestData, transportOfferingData);
			final TransportVehicleInfoData vehicleInfoData = getTransportVehicleInfo(offerRequestData, transportVehicleData);

			final String vehicleInfoCode = vehicleInfoData.getCode();
			if (StringUtils.isEmpty(vehicleInfoCode))
			{
				continue;
			}
			final String transportOfferingCode = transportOfferingData.getCode();
			final TransportOfferingModel transportOffering = getTransportOfferingService()
					.getTransportOffering(transportOfferingCode);
			final AccommodationMapModel accommodationMap = getAccommodationMap(offerRequestData, itineraryData, transportOffering,
					transportOfferingData.getSector(), vehicleInfoCode);

			final List<ConfiguredAccommodationModel> accommodationMapConfiguration = getAccommodationMapConfiguration(
					offerRequestData, accommodationMap);
			final List<SelectedAccommodationModel> selectedAccommodations = getSelectedAccommodations(offerRequestData,
					transportOffering);
			populateAccommodationMap(offerRequestData, offerResponseData.getSeatMap(), accommodationMapConfiguration,
					transportOfferingData, selectedAccommodations, transportOffering, itineraryData, accommodationMap.getCode());
		}
	}

	/**
	 * Create the status parameters to find selected accommodations that belong to an order, for making accommodations
	 * already selected and already added to order(not in cancelled state), unavailable
	 *
	 * @param offerRequestData
	 * @param transportOffering
	 * @return
	 */
	protected List<SelectedAccommodationModel> getSelectedAccommodations(final OfferRequestData offerRequestData,
			final TransportOfferingModel transportOffering)
	{
		final List<AccommodationStatus> selectedAccomStatuses = new ArrayList<>();
		selectedAccomStatuses.add(AccommodationStatus.OCCUPIED);
		selectedAccomStatuses.add(AccommodationStatus.UNAVAILABLE);
		final List<OrderStatus> cancelledOrderStatuses = new ArrayList<>();
		cancelledOrderStatuses.add(OrderStatus.CANCELLED);
		cancelledOrderStatuses.add(OrderStatus.CANCELLING);
		return getAccommodationMapService().getSelectedAccommodations(transportOffering, selectedAccomStatuses,
				cancelledOrderStatuses);

	}

	/**
	 * Creates instance of SeatMapResponseData
	 *
	 * @return
	 */
	protected SeatMapResponseData createAccommodationMapResonseData()
	{
		final SeatMapResponseData seatMapResonseData = new SeatMapResponseData();
		final List<SeatMapData> seatMapData = new ArrayList<>();
		seatMapResonseData.setSeatMap(seatMapData);
		return seatMapResonseData;
	}

	/**
	 * This method does following :
	 *
	 * 1)Checks if seatMapConfigurations i.e. Deck(s) exist, if not then it returns.
	 *
	 * 2)Else :
	 *
	 * a)it instantiates a seatmapdata for current transport offering(within the loop)
	 *
	 * b)instantiates and associate a seatmapdetail(containing a list of cabins) to seatmapdata
	 *
	 * c)adds seatmapdata to the list of seatmaps belonging to SeatMapResponseData.
	 *
	 * 3)Creates a modifiable list of current list of configured accommodations(i.e. deck) so as to sort them based on
	 * deck no. using configuredAccomNumberComparator, this can be used to position more than one decks.
	 *
	 * 4)Start looping through current list of configured accommodations i.e., and for first Deck, calls
	 * "populateAccommodationMapDetailData" to populate child configured accommodation(cabin, row, columns etc.)
	 * belonging to each deck.
	 *
	 * @param offerRequestData
	 * @param seatMapResonseData
	 * @param seatMapConfigurations
	 * @param transportOfferingData
	 * @param selectedAccommodations
	 * @param transportOffering
	 * @param itineraryData
	 * @param accommodationMapCode
	 */
	protected void populateAccommodationMap(final OfferRequestData offerRequestData, final SeatMapResponseData seatMapResonseData,
			final List<ConfiguredAccommodationModel> seatMapConfigurations, final TransportOfferingData transportOfferingData,
			final List<SelectedAccommodationModel> selectedAccommodations, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData, final String accommodationMapCode)
	{
		if (CollectionUtils.isEmpty(seatMapConfigurations))
		{
			return;
		}

		final List<SeatMapData> seatMaps = seatMapResonseData.getSeatMap();
		final SeatMapData seatMapData = new SeatMapData();
		seatMapData.setTransportOffering(transportOfferingData);
		seatMapData.setAccommodationMapCode(accommodationMapCode);

		final SeatMapDetailData seatMapDetail = new SeatMapDetailData();
		seatMapData.setSeatMapDetail(seatMapDetail);

		final List<CabinData> cabins = new ArrayList<>();
		seatMapDetail.setCabin(cabins);
		seatMapDetail.setDecks(new ArrayList<>());
		seatMaps.add(seatMapData);

		final List<ConfiguredAccommodationModel> modifiableSeatMapConfigurations = new ArrayList<>();
		modifiableSeatMapConfigurations.addAll(seatMapConfigurations);
		Collections.sort(modifiableSeatMapConfigurations, getConfiguredAccomNumberComparator());

		final Map<ProductModel, Long> productStockMap = new HashMap<ProductModel, Long>();
		for (final ConfiguredAccommodationModel seatMapConfiguration : modifiableSeatMapConfigurations)
		{
			final ConfiguredAccommodationType type = seatMapConfiguration.getType();
			if (!ConfiguredAccommodationType.DECK.equals(type))
			{
				LOG.error("Seat Map configured accomodation mismatch, expected DECK , received : " + type);
				continue;
			}
			populateAccommodationMapDetailData(offerRequestData, seatMapDetail, seatMapConfiguration, selectedAccommodations,
					transportOffering, itineraryData, productStockMap);
		}
	}

	/**
	 * This method does following :
	 *
	 * 1) Checks if current configured accommodation(deck) contains cabin, if Not it returns
	 *
	 * 2) Else :
	 *
	 * a)Create a modifiable list of cabins, to sort them based on number using configuredAccomNumberComparator
	 *
	 * b)For each configured accommodation(cabin) get all the rows and create a modifiable list of rows to sort them
	 * based on number using configuredAccomNumberComparator.
	 *
	 * 3)Gets the row number of first row from sorted rows and sets that as starting row num of current cabin, gets last
	 * row num of the sorted rows and sets that as ending row num of the cabin(these are used to draw total rows of a
	 * cabin on FE)
	 *
	 * 4)Check and set upper deck indicator, associates cabin class data to determine cabin class on FE and set column
	 * headers for all accommodation columns belonging to current cabin.
	 *
	 * 5)Finally it calls "populateRowInfo" to populate rows, columns and accommodation info of all accommodations within
	 * current cabin.
	 *
	 *
	 * @param offerRequestData
	 * @param seatMapDetail
	 * @param deck
	 * @param selectedAccommodations
	 * @param transportOffering
	 * @param itineraryData
	 * @param productStockMap
	 */
	protected void populateAccommodationMapDetailData(final OfferRequestData offerRequestData,
			final SeatMapDetailData seatMapDetail, final ConfiguredAccommodationModel deck,
			final List<SelectedAccommodationModel> selectedAccommodations, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData, final Map<ProductModel, Long> productStockMap)
	{
		final List<CabinData> cabinDatas = seatMapDetail.getCabin();
		seatMapDetail.getDecks().add(((AircraftDeckModel) deck).getDeckType());
		final List<ConfiguredAccommodationModel> cabins = deck.getConfiguredAccommodation();
		if (CollectionUtils.isEmpty(cabins))
		{
			return;
		}

		final List<ConfiguredAccommodationModel> modifiableCabins = new ArrayList<>();
		modifiableCabins.addAll(cabins);
		Collections.sort(modifiableCabins, getConfiguredAccomNumberComparator());

		for (final ConfiguredAccommodationModel cabin : modifiableCabins)
		{
			if (!ConfiguredAccommodationType.CABIN.equals(cabin.getType()))
			{
				LOG.error("Accommodation Map configured accomodation mismatch, expected CABIN , received : " + cabin.getType());
				throw new AccommodationMapDataSetUpException(
						"Accommodation Map configured accomodation mismatch, expected CABIN , received : " + cabin.getType());
			}

			final AircraftCabinModel aircraftCabin = (AircraftCabinModel) cabin;
			final CabinData cabinData = new CabinData();
			cabinDatas.add(cabinData);

			final List<ConfiguredAccommodationModel> rows = aircraftCabin.getConfiguredAccommodation();
			final List<ConfiguredAccommodationModel> modifiableRows = new ArrayList<ConfiguredAccommodationModel>();
			modifiableRows.addAll(rows);
			Collections.sort(modifiableRows, getConfiguredAccomNumberComparator());

			cabinData.setStartingRow(modifiableRows.get(0).getNumber());
			cabinData.setEndingRow(modifiableRows.get(modifiableRows.size() - 1).getNumber());
			final boolean isUpperDeck = DeckType.UPPER.equals(((AircraftDeckModel) deck).getDeckType());
			cabinData.setUpperDeckIndicator(isUpperDeck);
			cabinData.setCabinName(aircraftCabin.getName());
			final CabinClassModel cabinClass = aircraftCabin.getCabinClass();
			if (cabinClass != null)
			{
				final CabinClassData cabinClassData = new CabinClassData();
				cabinClassData.setCode(cabinClass.getCode());
				cabinClassData.setName(cabinClass.getName());
				cabinData.setCabinClass(cabinClassData);
			}
			cabinData.setColumnHeaders(aircraftCabin.getColumnHeaders());

			final List<SeatAvailabilityData> seatAvailabilities = new ArrayList<>();
			cabinData.setSeatAvailability(seatAvailabilities);
			populateRowInfo(offerRequestData, cabinData, modifiableRows, transportOffering, itineraryData, selectedAccommodations,
					productStockMap);
			populateFromPrice(offerRequestData, cabinData);
		}

	}

	/**
	 * This method gets all accommodations belonging to all row/column of a cabin, and sort them in ascending order by
	 * price, to get the lowest accommodation fare of the cabin, this is done to display "From LowestPrice" for a cabin
	 * on FE.
	 *
	 * @param offerRequestData
	 * @param cabinData
	 */
	protected void populateFromPrice(final OfferRequestData offerRequestData, final CabinData cabinData)
	{
		final List<RowInfoData> rowInfo = cabinData.getRowInfo();
		if (CollectionUtils.isEmpty(rowInfo))
		{
			return;
		}
		final List<SeatInfoData> allSeatInfo = new ArrayList<>();
		for (final RowInfoData rowInfoData : rowInfo)
		{
			final List<SeatInfoData> seatInfo = rowInfoData.getSeatInfo();
			if (CollectionUtils.isNotEmpty(seatInfo))
			{
				allSeatInfo.addAll(seatInfo);
			}
		}

		final List<SeatAvailabilityData> disableSeats = cabinData.getSeatAvailability().stream()
				.filter(seatAvailabilityData -> seatAvailabilityData.getAvailabilityIndicator()
						.equals(AccommodationStatus.UNAVAILABLE.getCode()))
				.collect(Collectors.toList());

		final List<SeatInfoData> enabledSeatInfo = allSeatInfo.stream()
				.filter(seatInfo -> !disableSeats.stream()
						.filter(seatAvailabilityData -> getSeatPredicate().test(seatAvailabilityData, seatInfo)).findAny().isPresent())
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(enabledSeatInfo))
		{
			Collections.sort(enabledSeatInfo, getAccommodationInfoDataComparator());
			cabinData.setPriceData(enabledSeatInfo.get(0).getTotalFare().getTotalPrice());
		}
	}

	/**
	 * This method does following :
	 *
	 * 1)Checks if the rows(belonging to a cabin) and passed to it as parameter is empty, it returns if the theres no
	 * rows.
	 *
	 * 2)Else :
	 *
	 * a)Loop through all the rows and for each row create a RowInfoData and calls "populateSeatInfo" to associate
	 * accommodation info of all accommodations belonging to current row.
	 *
	 * b)adds this rowinfodata to list of rowinfos belonging to a cabin
	 *
	 * @param offerRequestData
	 * @param cabinData
	 * @param rows
	 * @param transportOffering
	 * @param itineraryData
	 * @param selectedAccommodations
	 * @param productStockMap
	 */
	protected void populateRowInfo(final OfferRequestData offerRequestData, final CabinData cabinData,
			final List<ConfiguredAccommodationModel> rows, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData, final List<SelectedAccommodationModel> selectedAccommodations,
			final Map<ProductModel, Long> productStockMap)
	{
		final List<RowInfoData> rowInfos = new ArrayList<>();
		cabinData.setRowInfo(rowInfos);

		if (CollectionUtils.isEmpty(rows))
		{
			return;
		}

		for (final ConfiguredAccommodationModel row : rows)
		{
			if (!ConfiguredAccommodationType.ROW.equals(row.getType()))
			{
				LOG.error("Seat Map configured accomodation mismatch, expected ROW , received : " + row.getType());
				continue;
			}
			final RowInfoData rowInfoData = new RowInfoData();
			populateSeatInfo(offerRequestData, row, rowInfoData, cabinData, transportOffering, itineraryData, selectedAccommodations,
					productStockMap);
			rowInfos.add(rowInfoData);
		}
	}

	/**
	 * This method does following :
	 *
	 * 1)For the row passed to it as parameter it gets all columns and sort them using configuredAccomNumberComparator to
	 * have column sorted based on number.
	 *
	 * 2)It checks if modifiable list of column is empty, returns if it is empty, Else :
	 *
	 * a)Loop through columns of the row, and for each column, gets all accommodations belonging to the column.
	 *
	 * b)Calls "createSeat" method to create the accommodation info.
	 *
	 * @param offerRequestData
	 * @param row
	 * @param rowInfoData
	 * @param cabinData
	 * @param transportOffering
	 * @param itineraryData
	 * @param selectedAccommodations
	 * @param productStockMap
	 */
	protected void populateSeatInfo(final OfferRequestData offerRequestData, final ConfiguredAccommodationModel row,
			final RowInfoData rowInfoData, final CabinData cabinData, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData, final List<SelectedAccommodationModel> selectedAccommodations,
			final Map<ProductModel, Long> productStockMap)
	{
		rowInfoData.setRowNum(row.getNumber());
		final List<SeatInfoData> seatInfo = new ArrayList<>();
		rowInfoData.setSeatInfo(seatInfo);

		final List<ConfiguredAccommodationModel> columns = row.getConfiguredAccommodation();
		final List<ConfiguredAccommodationModel> modifiableColumns = new ArrayList<>();
		modifiableColumns.addAll(columns);
		Collections.sort(modifiableColumns, getConfiguredAccomNumberComparator());

		if (CollectionUtils.isEmpty(modifiableColumns))
		{
			return;
		}

		int colNum = 0;
		for (final ConfiguredAccommodationModel column : modifiableColumns)
		{
			if (!ConfiguredAccommodationType.COLUMN.equals(column.getType()))
			{
				LOG.error("Seat Map configured accomodation mismatch, expected COLUMN , received : " + column.getType());
				continue;
			}
			++colNum;
			final List<ConfiguredAccommodationModel> seats = column.getConfiguredAccommodation();

			if (CollectionUtils.isNotEmpty(seats))
			{
				createSeats(offerRequestData, cabinData, transportOffering, itineraryData, selectedAccommodations, productStockMap,
						seatInfo, colNum, seats);
			}
		}
	}

	/**
	 * Creates seats for given column
	 *
	 * @param offerRequestData
	 * @param cabinData
	 * @param transportOffering
	 * @param itineraryData
	 * @param selectedAccommodations
	 * @param productStockMap
	 * @param seatInfo
	 * @param colNum
	 * @param seats
	 */
	protected void createSeats(final OfferRequestData offerRequestData, final CabinData cabinData,
			final TransportOfferingModel transportOffering, final ItineraryData itineraryData,
			final List<SelectedAccommodationModel> selectedAccommodations, final Map<ProductModel, Long> productStockMap,
			final List<SeatInfoData> seatInfo, final int colNum, final List<ConfiguredAccommodationModel> seats)
	{
		for (final ConfiguredAccommodationModel seat : seats)
		{
			if (!ConfiguredAccommodationType.SEAT.equals(seat.getType()))
			{
				LOG.error("Seat Map configured accomodation mismatch, expected SEAT , received : " + seat.getType());
			}
			else
			{
				createSeat(offerRequestData, seatInfo, colNum, seat, cabinData, transportOffering, itineraryData,
						selectedAccommodations, productStockMap);
			}
		}
	}

	/**
	 * Create SeatFeatureData for each proximity item associated with a seat
	 *
	 * @param offerRequestData
	 * @param proximityItemModel
	 * @param seatFeatureData
	 */
	protected void populateSeatFeatureData(final OfferRequestData offerRequestData, final ProximityItemModel proximityItemModel,
			final SeatFeatureData seatFeatureData)
	{
		seatFeatureData
				.setSeatFeatureType(Objects.nonNull(proximityItemModel.getType()) ? proximityItemModel.getType().getCode() : null);
		seatFeatureData.setSeatFeaturePosition(Objects.nonNull(proximityItemModel.getRelativePosition())
				? proximityItemModel.getRelativePosition().getCode() : null);
		seatFeatureData.setSeatFeatureDescription(proximityItemModel.getDescription());
	}

	/**
	 * Gets and return list of configured accommodations belonging to an accommodation map
	 *
	 * @param offerRequestData
	 * @param accommodationMap
	 * @return
	 */
	protected List<ConfiguredAccommodationModel> getAccommodationMapConfiguration(final OfferRequestData offerRequestData,
			final AccommodationMapModel accommodationMap)
	{
		final List<ConfiguredAccommodationModel> seatMapConfiguration = getAccommodationMapService()
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
	 * Gets and return accommodation map for transport offering, route, sector and vehicle info code
	 *
	 * @param offerRequestData
	 * @param itineraryData
	 * @param transportOffering
	 * @param travelSector
	 * @param vehicleInfoCode
	 * @return
	 */
	protected AccommodationMapModel getAccommodationMap(final OfferRequestData offerRequestData, final ItineraryData itineraryData,
			final TransportOfferingModel transportOffering, final TravelSectorData travelSector, final String vehicleInfoCode)
	{
		final AccommodationMapModel accommodationMap = getAccommodationMapService().getAccommodationMap(vehicleInfoCode,
				transportOffering, itineraryData.getRoute().getCode(), travelSector);
		if (accommodationMap == null)
		{
			LOG.error("No accommodation map found for configuration code : " + vehicleInfoCode + " , transport offering :"
					+ transportOffering.getCode() + " , route : " + itineraryData.getRoute().getCode() + " , sector : "
					+ travelSector.getOrigin() + "_" + travelSector.getDestination() + " while creating seat map");
			throw new AccommodationMapDataSetUpException("No accommodation map found for configuration code : " + vehicleInfoCode
					+ " , transport offering :" + transportOffering.getCode() + " , route : " + itineraryData.getRoute().getCode()
					+ " , sector : " + travelSector.getOrigin() + "_" + travelSector.getDestination() + " while creating seat map");
		}
		return accommodationMap;
	}

	/**
	 * Gets and returns Transport vehicle info data from the transport vehicle data passed as param
	 *
	 * @param offerRequestData
	 * @param transportVehicle
	 * @return
	 */
	protected TransportVehicleInfoData getTransportVehicleInfo(final OfferRequestData offerRequestData,
			final TransportVehicleData transportVehicle)
	{
		final TransportVehicleInfoData vehicleInfo = transportVehicle.getVehicleInfo();
		if (vehicleInfo == null)
		{
			LOG.error("No vehicle info associated with transportVehicle while creating seat map");
			throw new AccommodationMapDataSetUpException("No vehicle info associated with transportVehicle while creating seat map");
		}
		return vehicleInfo;
	}

	/**
	 * Gets and returns Transport vehicle associated with a transport offering
	 *
	 * @param offerRequestData
	 * @param transportOfferingData
	 * @return
	 */
	protected TransportVehicleData getTransportVehicle(final OfferRequestData offerRequestData,
			final TransportOfferingData transportOfferingData)
	{
		final TransportVehicleData transportVehicle = transportOfferingData.getTransportVehicle();
		if (transportVehicle == null)
		{
			LOG.error("No transportVehicle found for transport offering :" + transportOfferingData.getCode()
					+ " while creating seat map");
			throw new AccommodationMapDataSetUpException("No transportVehicle found for transport offering :"
					+ transportOfferingData.getCode() + " while creating seat map");
		}
		return transportVehicle;
	}

	/**
	 * This method does following :
	 *
	 * 1)Create a seatinfodata object for an accommodation, and adds it to list of seatinfos passed to it as parameter
	 *
	 * 2)Sets the column no. to which this accommodation belong.
	 *
	 * 3)Check if seat have proximity items(WC, GALLEY etc.) associated with it, if yes then creates proximity item data
	 * and associate it with seat.
	 *
	 * 4)Calls "populateSeatFare" to set fare for each seat
	 *
	 * 5)Calls "populateSeatAvailability" to set the availabilty of seat.
	 *
	 * @param offerRequestData
	 * @param seatInfo
	 * @param colNum
	 * @param seat
	 * @param cabinData
	 * @param transportOffering
	 * @param itineraryData
	 * @param selectedAccommodations
	 * @param productStockMap
	 */
	protected void createSeat(final OfferRequestData offerRequestData, final List<SeatInfoData> seatInfo, final int colNum,
			final ConfiguredAccommodationModel seat, final CabinData cabinData, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData, final List<SelectedAccommodationModel> selectedAccommodations,
			final Map<ProductModel, Long> productStockMap)
	{
		final SeatInfoData seatInfoData = new SeatInfoData();
		seatInfo.add(seatInfoData);
		seatInfoData.setColumnNumber(colNum);
		seatInfoData.setColumnSpan(1);
		seatInfoData.setSeatNumber(seat.getIdentifier());
		seatInfoData.setSeatUid(seat.getUid());

		boolean seatAvailability = false;
		if (seat.isBookable())
		{
			if (seat.getProduct() == null)
			{
				seatAvailability = true;
			}
			else if (offerRequestData.getSeatMapRequest() != null
					&& CollectionUtils.isNotEmpty(offerRequestData.getSeatMapRequest().getSegmentInfoDatas()))
			{
				final List<SegmentInfoData> segmentInfoDatas = offerRequestData.getSeatMapRequest().getSegmentInfoDatas();
				for (final SegmentInfoData segmentInfoData : segmentInfoDatas)
				{
					if (transportOffering.getCode().equals(segmentInfoData.getTransportOfferingCode()))
					{
						final ProductModel fareProduct = getProductService().getProductForCode(segmentInfoData.getFareBasisCode());

						final Collection<ProductReferenceModel> productReferences = getProductReferenceService()
								.getProductReferencesForSourceAndTarget(fareProduct, seat.getProduct(), true);
						if (CollectionUtils.isNotEmpty(productReferences))
						{
							seatAvailability = true;
						}
					}
				}
			}
		}
		if (seatAvailability)
		{
			final List<SeatFeatureData> seatFeatures = new ArrayList<>();

			final Collection<ProximityItemModel> proximityItem = seat.getProximityItem();
			if (CollectionUtils.isNotEmpty(proximityItem))
			{
				for (final ProximityItemModel proximityItemModel : proximityItem)
				{
					final SeatFeatureData seatFeatureData = new SeatFeatureData();
					seatFeatures.add(seatFeatureData);
					populateSeatFeatureData(offerRequestData, proximityItemModel, seatFeatureData);
				}
			}

			final Collection<AccommodationCharacteristicsModel> characteristics = seat.getCharacteristics();
			if (CollectionUtils.isNotEmpty(characteristics))
			{
				for (final AccommodationCharacteristicsModel characteristic : characteristics)
				{
					seatFeatures.add(getSeatFeatureData(characteristic));
				}
			}
			seatInfoData.setSeatFeature(seatFeatures);

			populateSeatFare(offerRequestData, seatInfoData, seat, transportOffering, itineraryData);
			populateSeatAvailability(offerRequestData, cabinData, seat, selectedAccommodations, transportOffering, productStockMap);
		}
		else
		{
			populateDisableSeatAvailability(offerRequestData, cabinData, seat);
		}
	}

	/**
	 * Gets the seat feature data.
	 *
	 * @param characteristic
	 *           the characteristic
	 * @return the seat feature data
	 */
	protected SeatFeatureData getSeatFeatureData(final AccommodationCharacteristicsModel characteristic)
	{
		final SeatFeatureData seatFeatureData = new SeatFeatureData();
		seatFeatureData.setSeatFeatureType(Objects.nonNull(characteristic.getType()) ? characteristic.getType().getCode() : null);
		seatFeatureData.setSeatFeatureDescription(characteristic.getDescription());
		return seatFeatureData;
	}

	/**
	 * This method does following :
	 *
	 * 1)For the seat passed to it as parameter, it gets product associated with the seat.
	 *
	 * 2)If the product is not null, it checks if productStockMap passed as param has entry of the product as key, if
	 * found, it gets stock of that product as value against the key.
	 *
	 * 3)If product is not found, it checks stock of product in DB, and sets it as key/value pair in productStockMap(to
	 * avoid hitting DB to check stock of same product, in case theres same product associated with another seat)
	 *
	 * 4)If stock is 0, it calls "createSeatAvailabilityData" to create SeatAvailabilityData for that seat with
	 * availability indicator = N
	 *
	 * 5)If stock is not 0, it checks for selected accommodation for this seat in the selectedAccommodations(list of all
	 * accommodations belonging to this transport offering already added to order) passed to it as param.
	 *
	 * 6)If selectedAccommodation is found for this seat, it calls "createSeatAvailabilityData" to create
	 * SeatAvailabilityData for that seat with availability indicator = N
	 *
	 * @param offerRequestData
	 * @param cabinData
	 * @param seat
	 * @param selectedAccommodations
	 * @param transportOffering
	 * @param productStockMap
	 */
	protected void populateSeatAvailability(final OfferRequestData offerRequestData, final CabinData cabinData,
			final ConfiguredAccommodationModel seat, final List<SelectedAccommodationModel> selectedAccommodations,
			final TransportOfferingModel transportOffering, final Map<ProductModel, Long> productStockMap)
	{
		boolean isSeatAvailable = true;
		final ProductModel product = seat.getProduct();
		if (product != null)
		{
			Long stock;
			final boolean containsKey = productStockMap.containsKey(product);
			if (containsKey)
			{
				stock = productStockMap.get(product);
			}
			else
			{
				final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
				transportOfferings.add(transportOffering);
				stock = getCommerceStockService().getStockLevel(product, transportOfferings);
				productStockMap.put(product, stock);
			}

			if (stock == null || stock == 0L)
			{
				createSeatAvailabilityData(offerRequestData, cabinData, seat, null);
				isSeatAvailable = false;
			}
		}
		if (!isSeatAvailable || CollectionUtils.isEmpty(selectedAccommodations))
		{
			return;
		}
		for (final SelectedAccommodationModel selectedAccommodationModel : selectedAccommodations)
		{
			final ConfiguredAccommodationModel configuredAccommodation = selectedAccommodationModel.getConfiguredAccommodation();
			if (configuredAccommodation != null && configuredAccommodation.getIdentifier().equals(seat.getIdentifier()))
			{
				createSeatAvailabilityData(offerRequestData, cabinData, seat, selectedAccommodationModel);
			}
		}
	}


	/**
	 * create a seat availabilty data for a seat and set its availability indicator to D (disable) to represent
	 * non-availability of that seat.
	 *
	 * @param offerRequestData
	 * @param cabinData
	 * @param seat
	 */
	protected void populateDisableSeatAvailability(final OfferRequestData offerRequestData, final CabinData cabinData,
			final ConfiguredAccommodationModel seat)
	{
		final SeatAvailabilityData seatAvailabilityData = new SeatAvailabilityData();
		seatAvailabilityData.setAvailabilityIndicator(AccommodationStatus.UNAVAILABLE.getCode());
		seatAvailabilityData.setSeatNumber(seat.getIdentifier());
		cabinData.getSeatAvailability().add(seatAvailabilityData);
	}

	/**
	 * create a seat availabilty data for a seat and set its availability indicator to N to represent non-availability of
	 * that seat.
	 *
	 * @param offerRequestData
	 * @param cabinData
	 * @param seat
	 * @param selectedAccommodationModel
	 */
	protected void createSeatAvailabilityData(final OfferRequestData offerRequestData, final CabinData cabinData,
			final ConfiguredAccommodationModel seat, final SelectedAccommodationModel selectedAccommodationModel)
	{
		final SeatAvailabilityData seatAvailabilityData = new SeatAvailabilityData();

		if (selectedAccommodationModel != null)
		{
			seatAvailabilityData.setTransportOfferingCode(selectedAccommodationModel.getTransportOffering().getCode());
			seatAvailabilityData.setTraveller(getTravellerDataConverter().convert(selectedAccommodationModel.getTraveller()));
		}
		seatAvailabilityData.setAvailabilityIndicator(AccommodationStatus.OCCUPIED.getCode());
		seatAvailabilityData.setSeatNumber(seat.getIdentifier());
		cabinData.getSeatAvailability().add(seatAvailabilityData);
	}

	/**
	 * This method does following :
	 *
	 * For seat passed to it as parameter :
	 *
	 * 1)Gets associated product.
	 *
	 * 2)Gets price information for associated product and transport offering
	 *
	 * 3)If not found, look for price information for associated product and travel sector
	 *
	 * 4)If not found, look for price information for associated product and travel route
	 *
	 * 5)If not found, look for price information for associated product
	 *
	 * 6)If still not found then do not associate price with the seat else, create TotalFareData based on price
	 * information and associate it with seatInfoData(representing current seat) passed as param
	 *
	 * @param offerRequestData
	 * @param seatInfoData
	 * @param seat
	 * @param transportOffering
	 * @param itineraryData
	 */
	protected void populateSeatFare(final OfferRequestData offerRequestData, final SeatInfoData seatInfoData,
			final ConfiguredAccommodationModel seat, final TransportOfferingModel transportOffering,
			final ItineraryData itineraryData)
	{
		final ProductModel associatedProduct = seat.getProduct();
		if (associatedProduct == null)
		{
			setSeatInfoPrice(seatInfoData, getTravelCommercePriceFacade().createPriceData(0d));
			return;
		}

		PriceInformation priceInfo = getTravelCommercePriceService().getPriceInformation(associatedProduct,
				PriceRowModel.TRANSPORTOFFERINGCODE, transportOffering.getCode());

		if (priceInfo == null)
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(associatedProduct, PriceRowModel.TRAVELSECTORCODE,
					transportOffering.getTravelSector().getCode());
		}
		if (priceInfo == null)
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(associatedProduct, PriceRowModel.TRAVELROUTECODE,
					itineraryData.getRoute().getCode());
		}
		if (priceInfo == null)
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(associatedProduct, null, null);
		}

		if (priceInfo == null)
		{
			LOG.debug("Price not set for the product:" + associatedProduct.getCode() + " TransportOfferingCode:"
					+ transportOffering.getCode());
		}
		else
		{
			final PriceData priceData = createPriceData(offerRequestData, priceInfo);
			setSeatInfoPrice(seatInfoData, priceData);
		}
	}

	protected void setSeatInfoPrice(final SeatInfoData seatInfoData, final PriceData priceData)
	{
		final TotalFareData totalFare = new TotalFareData();
		totalFare.setBasePrice(priceData);
		totalFare.setTotalPrice(priceData);
		seatInfoData.setTotalFare(totalFare);
	}

	/**
	 * Method takes a PriceInformation object and performs a null check. If the object a is null then null will be
	 * returned otherwise the PriceDataFactory is invoked and a new PriceData object is created and returned using the
	 * PriceValue on the PriceInformation object
	 *
	 * @param offerRequestData
	 * @param priceInfo
	 * @return
	 */
	protected PriceData createPriceData(final OfferRequestData offerRequestData, final PriceInformation priceInfo)
	{
		if (priceInfo == null)
		{
			return null;
		}
		return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY,
				BigDecimal.valueOf(priceInfo.getPriceValue().getValue()), priceInfo.getPriceValue().getCurrencyIso());
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
	@Required
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
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
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the travelCommercePriceService
	 */
	protected TravelCommercePriceService getTravelCommercePriceService()
	{
		return travelCommercePriceService;
	}

	/**
	 * @param travelCommercePriceService
	 *           the travelCommercePriceService to set
	 */
	@Required
	public void setTravelCommercePriceService(final TravelCommercePriceService travelCommercePriceService)
	{
		this.travelCommercePriceService = travelCommercePriceService;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
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
	@Required
	public void setConfiguredAccomNumberComparator(final Comparator<ConfiguredAccommodationModel> configuredAccomNumberComparator)
	{
		this.configuredAccomNumberComparator = configuredAccomNumberComparator;
	}

	/**
	 * @return the accommodationInfoDataComparator
	 */
	protected Comparator<SeatInfoData> getAccommodationInfoDataComparator()
	{
		return accommodationInfoDataComparator;
	}

	/**
	 * @param accommodationInfoDataComparator
	 *           the accommodationInfoDataComparator to set
	 */
	@Required
	public void setAccommodationInfoDataComparator(final Comparator<SeatInfoData> accommodationInfoDataComparator)
	{
		this.accommodationInfoDataComparator = accommodationInfoDataComparator;
	}

	protected Converter<TravellerModel, TravellerData> getTravellerDataConverter()
	{
		return travellerDataConverter;
	}

	@Required
	public void setTravellerDataConverter(final Converter<TravellerModel, TravellerData> travellerDataConverter)
	{
		this.travellerDataConverter = travellerDataConverter;
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

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the seatPredicate
	 */
	protected BiPredicate<SeatAvailabilityData, SeatInfoData> getSeatPredicate()
	{
		return seatPredicate;
	}

	/**
	 * @param seatPredicate
	 *           the seatPredicate to set
	 */
	@Required
	public void setSeatPredicate(final BiPredicate<SeatAvailabilityData, SeatInfoData> seatPredicate)
	{
		this.seatPredicate = seatPredicate;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
