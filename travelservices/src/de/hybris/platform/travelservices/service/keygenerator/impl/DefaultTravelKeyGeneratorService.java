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
package de.hybris.platform.travelservices.service.keygenerator.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.service.keygenerator.TravelKeyGeneratorService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.StringJoiner;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of the TravelKeyGeneratorService interface. The DefaultTravelKeyGeneratorService implementation also
 * extends the PersistentKeyGenerator.
 */
public class DefaultTravelKeyGeneratorService extends PersistentKeyGenerator implements TravelKeyGeneratorService
{
	private ConfigurationService configurationService;

	private static final String REQUEST_ID_RANDOM_DIGITS = "create.request.id.random.digits.number";

	@Override
	public String generateTravellerUid(final String prefix, final String passengerNumber)
	{
		final StringBuilder uid = new StringBuilder();

		if (StringUtils.isNotEmpty(prefix))
		{
			uid.append(prefix);
			uid.append("_");
		}

		if (StringUtils.isNotEmpty(passengerNumber))
		{
			uid.append(passengerNumber);
			uid.append("_");
		}

		int count = getConfigurationService().getConfiguration()
				.getInt(TravelservicesConstants.TRAVEL_KEY_GENERATOR_RANDOM_ALPHANUMERIC_COUNT);

		if (count == 0)
		{
			count = 4;
		}

		uid.append(RandomStringUtils.randomAlphanumeric(count).toUpperCase());

		return uid.toString();
	}

	@Override
	public String generateAccommodationRequestCode(final int roomStayRefNumber, final String bookingReference)
	{
		final StringJoiner requestID = new StringJoiner("_");
		requestID.add(bookingReference).add(String.valueOf(roomStayRefNumber))
				.add(RandomStringUtils
						.randomAlphanumeric(getConfigurationService().getConfiguration().getInt(REQUEST_ID_RANDOM_DIGITS, 6)));
		return requestID.toString();
	}

	@Override
	public String generateTransportOfferingCode(final TransportOfferingModel transportOffering)
	{
		return new StringBuilder().append(transportOffering.getTravelProvider().getCode())
				.append(transportOffering.getNumber())
				.append(TravelDateUtils.convertDateToStringDate(transportOffering.getDepartureTime(), "ddMMyyyyHHmm")).toString();
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
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
