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
package de.hybris.platform.ndcfacades.context.impl;

import de.hybris.platform.ndcfacades.context.NDCJAXBContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;


/**
 * The type Default ndcjaxb context.
 */
public class DefaultNDCJAXBContext implements NDCJAXBContext
{
	private static final Logger LOG = Logger.getLogger(DefaultNDCJAXBContext.class);

	private JAXBContext jaxbContext;

	/**
	 * Instantiates a new NDC JAXB context.
	 *
	 * @param path
	 * 		the path
	 */
	protected DefaultNDCJAXBContext(final String path)
	{
		try
		{
			jaxbContext = JAXBContext.newInstance(path);
			LOG.info("JAXB context instantiated for " + path);
		}
		catch (final JAXBException e)
		{
			LOG.error("Error instantiating JAXB context for " + path, e);
		}
	}

	@Override
	public JAXBContext getNDCJaxbContext() throws JAXBException
	{
		return jaxbContext;
	}
}
