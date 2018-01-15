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

import de.hybris.platform.ndcfacades.ndc.BookingReferenceType.OtherID;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query.Filters;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC Order Retrieve Filters Validator Check if only one booking reference is specified in the request Check if a
 * passenger surname is specified
 */
public class NDCOrderRetrieveFiltersValidator implements NDCRequestValidator<OrderRetrieveRQ>
{
	private static final int MAX_BOOKING_REFERENCES = 1;
	private static final int REQUESTED_BOOKING_REFERENCES = 0;
	private ConfigurationService configurationService;

	@Override
	public void validate(final OrderRetrieveRQ orderRetrieveRQ, final ErrorsType errorsType)
	{
		if (!validatePassengerSurname(orderRetrieveRQ.getQuery().getFilters(), errorsType))
		{
			return;
		}

		validateBookingReference(orderRetrieveRQ.getQuery().getFilters(), errorsType);
	}

	/**
	 * Validate booking reference boolean.
	 *
	 * @param filters
	 * 		the filters
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateBookingReference(final Filters filters, final ErrorsType errorsType)
	{
		if (Objects.isNull(filters.getBookingReferences())
				|| CollectionUtils.isEmpty(filters.getBookingReferences().getBookingReference()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_BOOKING_REFERENCE));
			return false;
		}

		if (filters.getBookingReferences().getBookingReference().size() > MAX_BOOKING_REFERENCES)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_BOOKING_REFERENCES_EXCEEDED));
			return false;
		}

		final OtherID otherID = filters.getBookingReferences().getBookingReference().get(REQUESTED_BOOKING_REFERENCES).getOtherID();

		if (Objects.isNull(otherID) || StringUtils.isEmpty(otherID.getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_BOOKING_REFERENCE));
			return false;
		}

		return true;
	}

	/**
	 * Validate passenger surname boolean.
	 *
	 * @param filters
	 * 		the filters
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validatePassengerSurname(final Filters filters, final ErrorsType errorsType)
	{
		if (Objects.isNull(filters.getPassengers()) || Objects.isNull(filters.getPassengers().getName())
				|| StringUtils.isEmpty(filters.getPassengers().getName().getSurname().getValue()))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SURNAME));
			return false;
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
