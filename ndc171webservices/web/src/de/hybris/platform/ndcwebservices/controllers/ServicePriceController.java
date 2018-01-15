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
import de.hybris.platform.ndcfacades.ndc.ServicePriceRQ;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS;
import de.hybris.platform.ndcfacades.serviceprice.ServicePriceFacade;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.ServicePriceRQValidator;
import de.hybris.platform.ndcwebservices.validators.ServicePriceRSValidator;
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
 * Controller for {@link ServicePriceRQ}
 */
@Controller
@Scope("request")
@RequestMapping(value = "/v171/serviceprice")
public class ServicePriceController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(ServicePriceController.class);

	@Resource(name = "servicePriceRQValidator")
	private ServicePriceRQValidator servicePriceRQValidator;

	@Resource(name = "servicePriceRSValidator")
	private ServicePriceRSValidator servicePriceRSValidator;

	@Resource(name = "servicePriceFacade")
	private ServicePriceFacade servicePriceFacade;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public ServicePriceRS handleRequest(@RequestBody final ServicePriceRQ servicePriceRQ)
	{
		ServicePriceRS servicePriceRS = new ServicePriceRS();
		final ErrorsType errorsType = new ErrorsType();
		if (!servicePriceRQValidator.validateServicePriceRQ(servicePriceRQ, servicePriceRS))
		{
			servicePriceRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
			return servicePriceRS;
		}

		try
		{
			servicePriceRS = servicePriceFacade.getServicePrice(servicePriceRQ);
			servicePriceRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!servicePriceRSValidator.validateServicePriceRS(servicePriceRS))
			{
				final ServicePriceRS servicePriceRSError = new ServicePriceRS();
				servicePriceRSError.setErrors(servicePriceRS.getErrors());
				return servicePriceRSError;
			}
		}
		catch (final ConversionException conversionException)
		{
			final String offerUnavailableMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.SERVICE_OUT_OF_STOCK);
			if (StringUtils.equals(conversionException.getMessage(), offerUnavailableMsg))
			{
				addError(errorsType, offerUnavailableMsg);
			}
			else
			{
				addError(errorsType, conversionException.getMessage());
			}
			LOG.warn(conversionException.getMessage());
			LOG.debug(conversionException);
			servicePriceRS.setErrors(errorsType);
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
			servicePriceRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			servicePriceRS.setErrors(errorsType);
		}
		return servicePriceRS;
	}
}
