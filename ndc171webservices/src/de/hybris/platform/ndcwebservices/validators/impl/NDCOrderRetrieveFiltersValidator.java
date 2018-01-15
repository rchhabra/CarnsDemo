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

import de.hybris.platform.ndcfacades.ndc.BookingReferenceType;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType.OtherID;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ.Query.Filters;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.List;
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
	private ConfigurationService configurationService;

	@Override
	public void validate(final OrderRetrieveRQ orderRetrieveRQ, final ErrorsType errorsType)
	{
		final Filters filters = orderRetrieveRQ.getQuery().getFilters();
		validateBookingReference(filters, errorsType);
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

		final List<BookingReferenceType> bookingReferences = filters.getBookingReferences().getBookingReference();

		if (CollectionUtils.size(bookingReferences) > MAX_BOOKING_REFERENCES)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_BOOKING_REFERENCES_EXCEEDED));
			return false;
		}

		final BookingReferenceType bookingReference = bookingReferences.stream().findFirst().get();
		final String orderCode = bookingReference.getID();
		final OtherID otherID = bookingReference.getOtherID();

		if (StringUtils.isEmpty(orderCode))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_BOOKING_REFERENCE));
			return false;
		}

		if (Objects.isNull(otherID) || StringUtils.isEmpty(otherID.getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SURNAME));
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
