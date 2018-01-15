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

import de.hybris.platform.ndcfacades.flight.FlightPriceFacade;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.FlightPriceRQValidator;
import de.hybris.platform.ndcwebservices.validators.FlightPriceRSValidator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for FlightPriceController
 */
@Controller
@RequestMapping(value = "/flightprice")
public class FlightPriceController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(FlightPriceController.class);


	@Resource(name = "flightPriceRQValidator")
	private FlightPriceRQValidator flightPriceRQValidator;

	@Resource(name = "flightPriceRSValidator")
	private FlightPriceRSValidator flightPriceRSValidator;

	@Resource(name = "flightPriceFacade")
	private FlightPriceFacade flightPriceFacade;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public FlightPriceRS flightPriceRequest(@RequestBody final FlightPriceRQ flightPriceRQ)
	{
		final ErrorsType errorsType = new ErrorsType();
		FlightPriceRS flightPriceRS = new FlightPriceRS();
		flightPriceRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

		try
		{
			if (!flightPriceRQValidator.validateFlightPriceRQ(flightPriceRQ, flightPriceRS))
			{
				return flightPriceRS;
			}

			flightPriceRS = flightPriceFacade.retrieveFlightPrice(flightPriceRQ);
			flightPriceRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!flightPriceRSValidator.validateFlightPriceRS(flightPriceRS))
			{
				final FlightPriceRS flightPriceRSErrors = new FlightPriceRS();
				flightPriceRSErrors.setErrors(flightPriceRS.getErrors());
				flightPriceRSErrors.setVersion(NdcwebservicesConstants.NDC_VERSION);
				return flightPriceRSErrors;
			}
		}
		catch (final ConversionException | NDCOrderException e)
		{
			LOG.warn(e.getMessage());
			LOG.debug(e);
			addError(errorsType, e.getMessage());
			flightPriceRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			flightPriceRS.setErrors(errorsType);
		}

		return flightPriceRS;
	}
}
