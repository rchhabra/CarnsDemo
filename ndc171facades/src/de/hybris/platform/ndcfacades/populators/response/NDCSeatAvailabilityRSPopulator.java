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
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.FlightSegmentReferences;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS.Flights;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS.Flights.Cabin;
import de.hybris.platform.ndcfacades.ndc.SeatDisplay;
import de.hybris.platform.ndcfacades.ndc.SeatDisplay.Columns;
import de.hybris.platform.ndcfacades.ndc.SeatDisplay.Rows;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * The NDC offer populator for {@link SeatAvailabilityRS}
 */
public class NDCSeatAvailabilityRSPopulator extends NDCAbstractOfferRSPopulator
		implements Populator<OfferResponseData, SeatAvailabilityRS>
{

	private static final String COL_HEADER_SEPARATOR = "-";

	@Override
	public void populate(final OfferResponseData source, final SeatAvailabilityRS target) throws ConversionException
	{
		source.getSeatMap().getSeatMap().forEach(seatMapData -> {
			final Flights flights = new Flights();
			final ListOfFlightSegmentType flightSegmentType = getFlightSegmentRef(target.getDataLists().getFlightSegmentList(),
					seatMapData.getTransportOffering().getCode());
			if (Objects.nonNull(flightSegmentType))
			{
				final FlightSegmentReferences flightSegmentReferences = new FlightSegmentReferences();
				flightSegmentReferences.getValue().add(flightSegmentType);
				flights.setFlightSegmentReferences(flightSegmentReferences);
			}
			final String cabinClass = getRequiredCabinClass(seatMapData.getTransportOffering().getCode(),
					source.getSeatMap().getSegmentInfoDatas());
			populateCabinData(cabinClass, seatMapData.getSeatMapDetail().getCabin(), flights);
			target.getFlights().add(flights);
		});
	}

	/**
	 * This method filters an instance of {@link SegmentInfoData} for given transportOfferingCode and returns cabin class
	 * from filtered {@link SegmentInfoData}
	 *
	 * @param transportOfferingCode
	 * 		the transport offering code
	 * @param segmentInfoDatas
	 * 		the segment info datas
	 *
	 * @return the required cabin class
	 */
	protected String getRequiredCabinClass(final String transportOfferingCode, final List<SegmentInfoData> segmentInfoDatas)
	{
		final Optional<SegmentInfoData> opt = segmentInfoDatas.stream().filter(
				segmentInfoData -> StringUtils.equalsIgnoreCase(segmentInfoData.getTransportOfferingCode(), transportOfferingCode))
				.findFirst();
		return opt.isPresent() ? opt.get().getCabinClass() : StringUtils.EMPTY;
	}

	/**
	 * This method populates {@link Cabin}, from list of {@link CabinData}, for given cabin class.
	 *
	 * @param cabinClass
	 * 		the cabin class
	 * @param cabins
	 * 		the cabins
	 * @param flights
	 * 		the flights
	 */
	protected void populateCabinData(final String cabinClass, final List<CabinData> cabins, final Flights flights)
	{
		cabins.stream().filter(cabinData -> StringUtils.equalsIgnoreCase(cabinData.getCabinClass().getCode(), cabinClass))
				.collect(Collectors.toList()).forEach(cabinData -> {
					final Cabin cabin = new Cabin();
					cabin.setCode(cabinData.getCabinClass().getCode());
					cabin.setDefinition(cabinData.getCabinName());
					cabin.setSeatDisplay(getSeatDisplay(cabinData));
					flights.getCabin().add(cabin);
				});
	}

	/**
	 * This method creates instance of {@link SeatDisplay} from {@link CabinData} having row and column information.
	 *
	 * @param cabinData
	 * 		the cabin data
	 *
	 * @return the seat display
	 */
	protected SeatDisplay getSeatDisplay(final CabinData cabinData)
	{
		final SeatDisplay seatDisplay = new SeatDisplay();
		seatDisplay.getColumns().addAll(getColumnsData(cabinData));
		seatDisplay.setRows(getRows(cabinData));
		return seatDisplay;
	}

	/**
	 * This method creates instance of {@link Rows} having information of starting and ending row.
	 *
	 * @param cabinData
	 * 		the cabin data
	 *
	 * @return the rows
	 */
	protected Rows getRows(final CabinData cabinData)
	{
		final Rows rows = new Rows();
		rows.setFirst(BigInteger.valueOf(cabinData.getStartingRow()));
		rows.setLast(BigInteger.valueOf(cabinData.getEndingRow()));
		return rows;
	}

	/**
	 * This method creates list of {@link Columns} from {@link CabinData}
	 *
	 * @param cabinData
	 * 		the cabin data
	 *
	 * @return the columns data
	 */
	protected List<Columns> getColumnsData(final CabinData cabinData)
	{
		final List<Columns> columnsList = new ArrayList<>();
		cabinData.getColumnHeaders().stream()
				.filter(columnHeader -> !StringUtils.equalsIgnoreCase(columnHeader, COL_HEADER_SEPARATOR)).forEach(columnHeader -> {
					final Columns columns = new Columns();
					columns.setValue(columnHeader);
					columnsList.add(columns);
				});
		return columnsList;
	}

	/**
	 * This method returns {@link ListOfFlightSegmentType} from {@link FlightSegmentList} having same segment key with
	 * given code.
	 *
	 * @param flightSegmentList
	 * 		the flight segment list
	 * @param code
	 * 		the code
	 *
	 * @return the flight segment ref
	 */
	protected ListOfFlightSegmentType getFlightSegmentRef(final FlightSegmentList flightSegmentList, final String code)
	{
		final Optional<ListOfFlightSegmentType> opt = flightSegmentList.getFlightSegment().stream()
				.filter(flightSegment -> StringUtils.equalsIgnoreCase(flightSegment.getSegmentKey(), code)).findFirst();
		return opt.orElse(null);
	}
}
