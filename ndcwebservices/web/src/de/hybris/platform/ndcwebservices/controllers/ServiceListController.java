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
import de.hybris.platform.ndcfacades.ndc.ServiceListRQ;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;
import de.hybris.platform.ndcfacades.servicelist.ServiceListFacade;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.ServiceListRQValidator;
import de.hybris.platform.ndcwebservices.validators.ServiceListRSValidator;
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
 * Controller for ServiceListRQ
 */
@Controller
@Scope("request")
@RequestMapping(value = "/servicelist")
public class ServiceListController extends NDCAbstractController
{

	private static final Logger LOG = Logger.getLogger(ServiceListController.class);

	@Resource(name = "serviceListRQValidator")
	private ServiceListRQValidator serviceListRQValidator;

	@Resource(name = "serviceListRSValidator")
	private ServiceListRSValidator serviceListRSValidator;

	@Resource(name = "serviceListFacade")
	private ServiceListFacade serviceListFacade;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public ServiceListRS handleRequest(@RequestBody final ServiceListRQ serviceListRQ)
	{
		ServiceListRS serviceListRS = new ServiceListRS();
		final ErrorsType errorsType = new ErrorsType();
		if (!serviceListRQValidator.validateServiceListRQ(serviceListRQ, serviceListRS))
		{
			serviceListRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
			return serviceListRS;
		}
		try
		{
			serviceListRS = serviceListFacade.getServiceList(serviceListRQ);
			serviceListRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!serviceListRSValidator.validateServiceListRS(serviceListRS))
			{
				final ServiceListRS serviceListRSError = new ServiceListRS();
				serviceListRSError.setErrors(serviceListRS.getErrors());
				return serviceListRSError;
			}
		}
		catch (final ConversionException conversionException)
		{
			final String offerUnavailableMsg = configurationService.getConfiguration()
					.getString(NdcfacadesConstants.SERVICE_UNAVAILABLE);
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
			serviceListRS.setErrors(errorsType);
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
			serviceListRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			serviceListRS.setErrors(errorsType);
		}
		return serviceListRS;
	}
}
