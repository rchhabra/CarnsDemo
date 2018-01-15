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

import de.hybris.platform.ndcfacades.baggagelist.BaggageListFacade;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageListRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.BaggageListRQValidator;
import de.hybris.platform.ndcwebservices.validators.BaggageListRSValidator;
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
 * Controller for BaggageListRQ
 */
@Controller
@Scope("request")
@RequestMapping(value = "/v171/baggagelist")
public class BaggageListController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(BaggageListController.class);

	@Resource(name = "baggageListFacade")
	private BaggageListFacade baggageListFacade;

	@Resource(name = "baggageListRQValidator")
	private BaggageListRQValidator baggageListRQValidator;

	@Resource(name = "baggageListRSValidator")
	private BaggageListRSValidator baggageListRSValidator;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public BaggageListRS handleRequest(@RequestBody final BaggageListRQ baggageListRQ)
	{
		BaggageListRS baggageListRS = new BaggageListRS();
		final ErrorsType errorsType = new ErrorsType();
		if (!baggageListRQValidator.validateBaggageListRQ(baggageListRQ, baggageListRS))
		{
			baggageListRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
			return baggageListRS;
		}
		try
		{
			baggageListRS = baggageListFacade.getBaggageList(baggageListRQ);
			baggageListRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!baggageListRSValidator.validateBaggageListRS(baggageListRS))
			{
				final BaggageListRS baggageListRSError = new BaggageListRS();
				baggageListRSError.setErrors(baggageListRS.getErrors());
				return baggageListRSError;
			}
		}
		catch (final ConversionException conversionException)
		{
			if (StringUtils.isEmpty(conversionException.getMessage()))
			{
				final String offerUnavailableMsg = configurationService.getConfiguration()
						.getString(NdcfacadesConstants.SERVICE_UNAVAILABLE);
				addError(errorsType, offerUnavailableMsg);
			}
			else
			{
				addError(errorsType, conversionException.getMessage());
			}
			LOG.warn(conversionException.getMessage());
			LOG.debug(conversionException);
			baggageListRS.setErrors(errorsType);
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
				addError(errorsType, invalidSegmentKeyMsg);
			}
			else if (StringUtils.equals(e.getMessage(), invalidODKeyMsg))
			{
				addError(errorsType, invalidODKeyMsg);
			}
			baggageListRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			baggageListRS.setErrors(errorsType);
		}
		return baggageListRS;
	}
}
