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
package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.ndc.AirShopReqAttributeQueryType.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import javax.xml.datatype.DatatypeConstants;

import org.springframework.beans.factory.annotation.Required;

import javax.xml.datatype.DatatypeConstants;

import org.springframework.beans.factory.annotation.Required;


/**
 * NDC {@link AirShoppingRQ} date Validation
 */
public class NDCAirShoppingDateValidator implements NDCRequestValidator<AirShoppingRQ>
{
	private ConfigurationService configurationService;

	@Override
	public void validate(final AirShoppingRQ airShoppingRQ, final ErrorsType errorsType)
	{
		final List<OriginDestination> originDestinationList = airShoppingRQ
				.getCoreQuery().getOriginDestinations().getOriginDestination();

		if (!validatePastDate(originDestinationList, errorsType))
		{
			return;
		}

		validateSubsequentDate(originDestinationList, errorsType);
	}

	/**
	 * Checks if the departure date of any flight is equal o subsequent to the previous one
	 *
	 * @param originDestinationList
	 * 		the origin destination list
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateSubsequentDate(final List<OriginDestination> originDestinationList, final ErrorsType errorsType)
	{
		for (int i = 0; i < originDestinationList.size() - 1; i++)
		{
			final int relation = originDestinationList.get(i).getDeparture().getDate()
					.compare(originDestinationList.get(i + 1).getDeparture().getDate());
			if (relation == DatatypeConstants.GREATER || relation == DatatypeConstants.INDETERMINATE)
			{
				addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_DATES));
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the departure date is in a valid XMLGregorianCalendar format
	 *
	 * @param originDestinationList
	 * 		the origin destination list
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validatePastDate(final List<OriginDestination> originDestinationList, final ErrorsType errorsType)
	{
		for (final OriginDestination originDestination : originDestinationList)
		{
			if (LocalDate
					.of(originDestination.getDeparture().getDate().getYear(), originDestination.getDeparture().getDate().getMonth(),
							originDestination.getDeparture().getDate().getDay())
					.compareTo(ChronoLocalDate.from(LocalDate.now().atStartOfDay())) < 0)
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.PAST_DATE));
				return false;
			}
		}
		return true;
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
