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

package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.CabinData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatAvailabilityData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.DataListType.SeatList;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS.DataLists;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType.Row;
import de.hybris.platform.ndcfacades.ndc.SeatMapRowNbrType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Data list populator for NDC {@link SeatAvailabilityRS}
 */
public class NDCSeatAvailabilityRSDataListPopulator extends NDCAbstractOffersRSDataListPopulator
		implements Populator<OfferResponseData, SeatAvailabilityRS>
{
	private static final String OCCUPIED_SEAT_INDICATOR = "N";
	private ConfigurationService configurationService;

	@Override
	public void populate(final OfferResponseData source, final SeatAvailabilityRS target) throws ConversionException
	{
		final DataLists dataListType = new DataLists();
		populateFlightSegments(source, dataListType);
		populateSeatList(source, dataListType);
		target.setDataLists(dataListType);
	}

	/**
	 * This method creates and populate instance of {@link SeatList} using {@link OfferResponseData}.
	 *
	 * @param source
	 * 		the source
	 * @param dataListType
	 * 		the data list type
	 */
	protected void populateSeatList(final OfferResponseData source, final DataListType dataListType)
	{
		final SeatList seatList = new SeatList();
		final List<String> seatListKeys = new ArrayList<>();
		source.getSeatMap().getSeatMap().forEach(seatMapData -> {
			final ListOfFlightSegmentType flightSegmentType = getRequiredFlightSegment(seatMapData.getTransportOffering().getCode(),
					dataListType.getFlightSegmentList().getFlightSegment());

			seatMapData.getSeatMapDetail().getCabin().stream().filter(cabin -> StringUtils
					.equalsIgnoreCase(cabin.getCabinClass().getCode(), flightSegmentType.getClassOfService().getCode().getValue()))
					.forEach(cabinData -> {
						final List<String> unavailableSeatNums = getAllUnavailableSeatsForCabin(cabinData);
						populateSeat(cabinData, seatListKeys, seatList, seatMapData, unavailableSeatNums);
					});
		});
		if (CollectionUtils.isNotEmpty(seatList.getSeats()))
		{
			dataListType.setSeatList(seatList);
		}
		else
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NO_SEATS_AVAILABLE));
		}
	}

	/**
	 * This method returns all occupied seat numbers for given {@link CabinData}
	 *
	 * @param cabinData
	 * 		the cabin data
	 * @return the all unavailable seats for cabin
	 */
	protected List<String> getAllUnavailableSeatsForCabin(final CabinData cabinData)
	{
		return cabinData.getSeatAvailability().stream()
				.filter(seatAvailabilityData -> StringUtils.isNotEmpty(seatAvailabilityData.getAvailabilityIndicator()))
				.filter(seatAvailabilityData -> StringUtils.equalsIgnoreCase(seatAvailabilityData.getAvailabilityIndicator(),
						OCCUPIED_SEAT_INDICATOR))
				.map(SeatAvailabilityData::getSeatNumber).collect(Collectors.toList());
	}

	/**
	 * This method returns filters given list of {@link ListOfFlightSegmentType} on the basis of given trasnport offering code
	 *
	 * @param code
	 * 		the code
	 * @param flightSegments
	 * 		the flight segments
	 * @return the required flight segment
	 */
	protected ListOfFlightSegmentType getRequiredFlightSegment(final String code,
			final List<ListOfFlightSegmentType> flightSegments)
	{
		final Optional<ListOfFlightSegmentType> optional = flightSegments.stream()
				.filter(flightSegmentType -> StringUtils.equalsIgnoreCase(flightSegmentType.getSegmentKey(), code)).findFirst();
		return optional.orElse(null);
	}

	/**
	 * This method creates and populates instances of {@link ListOfSeatType} for every row and column.
	 *
	 * @param cabin
	 * 		the cabin
	 * @param seatListKeys
	 * 		the seat list keys
	 * @param seatList
	 * 		the seat list
	 * @param seatMapData
	 * 		the seat map data
	 * @param unavailableSeatNums
	 * 		the unavailable seat nums
	 */
	protected void populateSeat(final CabinData cabin, final List<String> seatListKeys, final SeatList seatList,
			final SeatMapData seatMapData, final List<String> unavailableSeatNums)
	{
		cabin.getRowInfo().forEach(row -> row.getSeatInfo().forEach(seatInfo -> {
			final String seatNum = seatInfo.getSeatNumber();
			if (!unavailableSeatNums.contains(seatNum))
			{
				final String colNum = seatNum.substring(seatNum.length() - 1, seatNum.length());
				final String rowNum = seatNum.substring(0, seatNum.indexOf(colNum));
				final String seatListKey = getListKey(seatMapData.getTransportOffering().getCode(), colNum, rowNum);
				if (!seatListKeys.contains(seatListKey))
				{
					final ListOfSeatType seatType = new ListOfSeatType();
					seatType.setListKey(seatListKey);
					seatListKeys.add(seatListKey);
					seatType.setLocation(getSeatLocation(rowNum, colNum));
					if (CollectionUtils.isNotEmpty(seatInfo.getSeatFeature()))
					{
						seatType.setDetails(seatInfo.getSeatFeature().get(0).getSeatFeaturePosition());
					}
					seatList.getSeats().add(seatType);
				}
			}
		}));
	}

	/**
	 * This method creates an instance of {@link SeatLocationType} with row and column number.
	 *
	 * @param rowNum
	 * 		the row num
	 * @param colNum
	 * 		the col num
	 * @return the seat location
	 */
	protected SeatLocationType getSeatLocation(final String rowNum, final String colNum)
	{
		final SeatLocationType seatLocationType = new SeatLocationType();
		seatLocationType.setColumn(colNum);
		final Row row = new Row();
		final SeatMapRowNbrType seatMapRowNbrType = new SeatMapRowNbrType();
		seatMapRowNbrType.setValue(rowNum);
		row.setNumber(seatMapRowNbrType);
		seatLocationType.setRow(row);
		return seatLocationType;
	}

	/**
	 * This method creates unique seatList key by appending code, row and column number.
	 *
	 * @param code
	 * 		the code
	 * @param col
	 * 		the col
	 * @param row
	 * 		the row
	 * @return the list key
	 */
	protected String getListKey(final String code, final String col, final String row)
	{
		return new StringBuilder().append(col).append(row).append(code).toString();
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
