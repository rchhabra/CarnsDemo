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

package de.hybris.platform.ndcwebservices.controllers;

import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRQ;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;
import de.hybris.platform.ndcfacades.seatavailability.SeatAvailabilityFacade;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.SeatAvailabilityRQValidator;
import de.hybris.platform.ndcwebservices.validators.SeatAvailabilityRSValidator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for SeatAvailabilityRQ
 */
@Controller
@Scope("request")
@RequestMapping(value = "/seatavailability")
public class SeatAvailabilityController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(SeatAvailabilityController.class);

	@Resource(name = "seatAvailabilityRQValidator")
	private SeatAvailabilityRQValidator seatAvailabilityRQValidator;

	@Resource(name = "seatAvailabilityRSValidator")
	private SeatAvailabilityRSValidator seatAvailabilityRSValidator;

	@Resource(name = "seatAvailabilityFacade")
	private SeatAvailabilityFacade seatAvailabilityFacade;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public SeatAvailabilityRS handleRequest(@RequestBody final SeatAvailabilityRQ seatAvailabilityRQ)
	{
		SeatAvailabilityRS seatAvailabilityRS = new SeatAvailabilityRS();
		final ErrorsType errorsType = new ErrorsType();
		if (!seatAvailabilityRQValidator.validateSeatAvailabilityRQ(seatAvailabilityRQ, seatAvailabilityRS))
		{
			seatAvailabilityRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
			return seatAvailabilityRS;
		}
		try
		{
			seatAvailabilityRS = seatAvailabilityFacade.getSeatMap(seatAvailabilityRQ);
			seatAvailabilityRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
			if (!seatAvailabilityRSValidator.validateSeatAvailabilityRS(seatAvailabilityRS))
			{
				final SeatAvailabilityRS seatAvailabilityRSError = new SeatAvailabilityRS();
				seatAvailabilityRSError.setErrors(seatAvailabilityRS.getErrors());
				return seatAvailabilityRSError;
			}
		}
		catch (final ConversionException conversionException)
		{
			LOG.warn(conversionException.getMessage());
			LOG.debug(conversionException);
			addError(errorsType, conversionException.getMessage());
			seatAvailabilityRS.setErrors(errorsType);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			final String invalidSegmentKeyMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.SEATS_UNAVAILABLE);
			final String invalidODKeyMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.INVALID_ORIGIN_DESTINATION_KEY);
			if (StringUtils.equals(e.getMessage(), invalidSegmentKeyMsg))
			{
				addError(errorsType, invalidSegmentKeyMsg);
			}
			else if (StringUtils.equals(e.getMessage(), invalidODKeyMsg))
			{
				addError(errorsType, invalidODKeyMsg);
			}
			seatAvailabilityRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			seatAvailabilityRS.setErrors(errorsType);
		}
		return seatAvailabilityRS;
	}
}
