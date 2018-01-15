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
 */

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelrulesengine.rao.FareSearchRequestRAO;
import de.hybris.platform.travelrulesengine.rao.LegInfoRAO;
import de.hybris.platform.travelrulesengine.utils.TravelRuleUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates FareSearchRequestRAO leg info and trip type attributes from FareSearchRequestData
 */
public class LegInfoRaoPopulator implements Populator<FareSearchRequestData, FareSearchRequestRAO>
{
	private TimeService timeService;

	@Override
	public void populate(final FareSearchRequestData source, final FareSearchRequestRAO target)
			throws ConversionException
	{
		target.setTripType(source.getTripType());

		final List<LegInfoRAO> legInfos = new ArrayList<>();
		source.getOriginDestinationInfo().forEach(odInfo -> legInfos.add(createLegInfo(odInfo)));
		target.setLegInfos(legInfos);

		final Date now = getTimeService().getCurrentTime();
		target.setAdvanceDays((int) TravelRuleUtils.getDaysBetweenDates(now, legInfos.get(0).getDepartureTime()));
		target.setDurationOfStay(
				TripType.RETURN.equals(target.getTripType()) && CollectionUtils.size(legInfos) > 1 ?
						(int) TravelRuleUtils
								.getDaysBetweenDates(legInfos.get(0).getDepartureTime(), legInfos.get(1).getDepartureTime()) : 0);
	}

	/**
	 * Create leg info leg info rao.
	 *
	 * @param odInfo
	 * 		the od info
	 * @return the leg info rao
	 */
	protected LegInfoRAO createLegInfo(final OriginDestinationInfoData odInfo)
	{
		final LegInfoRAO legInfo = new LegInfoRAO();
		legInfo.setReferenceNumber(odInfo.getReferenceNumber());
		legInfo.setDepartureTime(odInfo.getDepartureTime());
		return legInfo;
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
