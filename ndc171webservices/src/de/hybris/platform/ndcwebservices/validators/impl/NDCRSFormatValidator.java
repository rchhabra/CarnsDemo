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

import de.hybris.platform.ndcfacades.context.NDCJAXBContext;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC response format validator
 */
public class NDCRSFormatValidator<N> implements NDCRequestValidator<N>
{
	private static final Logger LOG = Logger.getLogger(NDCRSFormatValidator.class);

	private ConfigurationService configurationService;
	private NDCJAXBContext ndcJAXBContext;

	@Override
	public void validate(final N ndcResponseObj, final ErrorsType errorsType)
	{
		try
		{
			final JAXBContext jc = getNdcJAXBContext().getNDCJaxbContext();
			final JAXBSource source = new JAXBSource(jc, ndcResponseObj);

			final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			final Schema schema = sf
					.newSchema(new File(getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.NDC_XSD_PATH)
							+ ndcResponseObj.getClass().getSimpleName() + ".xsd"));

			final Validator validator = schema.newValidator();
			validator.validate(source);

		}
		catch (final Exception e)
		{
			LOG.error(getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR)
					+ ndcResponseObj.getClass().getSimpleName(), e);
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			return;
		}
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

	protected NDCJAXBContext getNdcJAXBContext()
	{
		return ndcJAXBContext;
	}

	@Required
	public void setNdcJAXBContext(final NDCJAXBContext ndcJAXBContext)
	{
		this.ndcJAXBContext = ndcJAXBContext;
	}
}
