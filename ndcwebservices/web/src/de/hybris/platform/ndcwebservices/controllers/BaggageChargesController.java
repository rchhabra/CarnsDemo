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

import de.hybris.platform.ndcfacades.baggagecharges.BaggageChargesFacade;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.BaggageChargesRQValidator;
import de.hybris.platform.ndcwebservices.validators.BaggageChargesRSValidator;
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
 * Controller for BaggageChargesRQ
 */
@Controller
@Scope("request")
@RequestMapping(value = "/baggagecharges")
public class BaggageChargesController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(BaggageChargesController.class);

	@Resource(name = "baggageChargesFacade")
	private BaggageChargesFacade baggageChargesFacade;

	@Resource(name = "baggageChargesRQValidator")
	private BaggageChargesRQValidator baggageChargesRQValidator;

	@Resource(name = "baggageChargesRSValidator")
	private BaggageChargesRSValidator baggageChargesRSValidator;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public BaggageChargesRS handleRequest(@RequestBody final BaggageChargesRQ baggageChargesRQ)
	{
		BaggageChargesRS baggageChargesRS = new BaggageChargesRS();
		final ErrorsType errorsType = new ErrorsType();
		if (!baggageChargesRQValidator.validateBaggageChargesRQ(baggageChargesRQ, baggageChargesRS))
		{
			baggageChargesRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
			return baggageChargesRS;
		}
		try
		{
			baggageChargesRS = baggageChargesFacade.getBaggageCharges(baggageChargesRQ);
			baggageChargesRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!baggageChargesRSValidator.validateBaggageChargesRS(baggageChargesRS))
			{
				final BaggageChargesRS BaggageChargesRSError = new BaggageChargesRS();
				BaggageChargesRSError.setErrors(baggageChargesRS.getErrors());
				return BaggageChargesRSError;
			}
		}
		catch (final ConversionException conversionException)
		{
			final String baggageUnavailableMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.BAGGAGE_OUT_OF_STOCK);
			if (StringUtils.equals(conversionException.getMessage(), baggageUnavailableMsg))
			{
				addError(errorsType, baggageUnavailableMsg);
			}
			else
			{
				addError(errorsType, conversionException.getMessage());
			}
			LOG.warn(conversionException.getMessage());
			LOG.debug(conversionException);
			baggageChargesRS.setErrors(errorsType);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			final String invalidSegmentKeyMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.SERVICE_UNAVAILABLE);
			final String invalidODKeyMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.INVALID_ORIGIN_DESTINATION_KEY);
			if (StringUtils.equals(e.getMessage(), invalidSegmentKeyMsg))
			{
				addError(errorsType, configurationService.getConfiguration().getString(NdcfacadesConstants.BAGGAGE_UNAVAILABLE));
			}
			else if (StringUtils.equals(e.getMessage(), invalidODKeyMsg))
			{
				addError(errorsType, invalidODKeyMsg);
			}
			baggageChargesRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			baggageChargesRS.setErrors(errorsType);
		}
		return baggageChargesRS;
	}
}
