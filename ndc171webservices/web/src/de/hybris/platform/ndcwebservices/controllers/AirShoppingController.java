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
package de.hybris.platform.ndcwebservices.controllers;

import de.hybris.platform.ndcfacades.airshopping.AirShoppingFacade;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.AirShoppingRQValidator;
import de.hybris.platform.ndcwebservices.validators.AirShoppingRSValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for AirShoppingRQ
 */
@Controller
@Scope("request")
@RequestMapping(value = "/v171/airshopping")
public class AirShoppingController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(AirShoppingController.class);

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "airShoppingFacade")
	private AirShoppingFacade airShoppingFacade;

	@Resource(name = "airShoppingRQValidator")
	private AirShoppingRQValidator airShoppingRQValidator;

	@Resource(name = "airShoppingRSValidator")
	private AirShoppingRSValidator airShoppingRSValidator;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public AirShoppingRS airShoppingRequest(@RequestBody final AirShoppingRQ airShoppingRQ)
	{
		final ErrorsType errorsType = new ErrorsType();
		AirShoppingRS airShoppingRS = new AirShoppingRS();
		airShoppingRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

		try
		{
			if (!airShoppingRQValidator.validateAirShoppingRQ(airShoppingRQ, airShoppingRS))
			{
				return airShoppingRS;
			}

			airShoppingRS = airShoppingFacade.doSearch(airShoppingRQ);
			airShoppingRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!airShoppingRSValidator.validateAirShoppingRS(airShoppingRS))
			{
				final AirShoppingRS airShoppingRSError = new AirShoppingRS();
				airShoppingRSError.setVersion(NdcwebservicesConstants.NDC_VERSION);
				airShoppingRSError.setErrors(airShoppingRS.getErrors());
				return airShoppingRSError;
			}
		}
		catch (final ConversionException conversionException)
		{
			LOG.warn(conversionException.getMessage());
			LOG.debug(conversionException);
			addError(errorsType, conversionException.getMessage());
			airShoppingRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			airShoppingRS.setErrors(errorsType);
		}

		return airShoppingRS;
	}
}
