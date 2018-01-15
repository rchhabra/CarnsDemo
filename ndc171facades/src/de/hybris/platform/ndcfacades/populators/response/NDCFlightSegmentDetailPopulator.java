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

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.FlightDetailType;
import de.hybris.platform.ndcfacades.ndc.FlightDurationType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC Flight Segment Detail Populator for {@link ListOfFlightSegmentType}
 */
public class NDCFlightSegmentDetailPopulator implements Populator<TransportOfferingData, ListOfFlightSegmentType>
{

	private static final Logger LOG = Logger.getLogger(NDCFlightSegmentDetailPopulator.class);

	private ConfigurationService configurationService;

	@Override
	public void populate(final TransportOfferingData transportOfferingData, final ListOfFlightSegmentType listOfFlightSegmentType)
			throws ConversionException
	{
		final FlightDetailType flightDetail = new FlightDetailType();
		final FlightDurationType flightDuration = new FlightDurationType();

		try
		{
			if (Objects.nonNull(transportOfferingData.getDurationValue()))
			{
				final Duration duration = DatatypeFactory.newInstance().newDuration(transportOfferingData.getDurationValue());
				flightDuration.setValue(duration);
			}
		}
		catch (final DatatypeConfigurationException e)
		{
			LOG.debug(e);
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.DURATION_CONVERSION_ERROR));
		}

		flightDetail.setFlightDuration(flightDuration);
		listOfFlightSegmentType.setFlightDetail(flightDetail);
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
