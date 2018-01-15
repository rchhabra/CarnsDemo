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
package de.hybris.platform.ndcwebservices.marshallers;

import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.Result;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


/**
 * Ndc implementation of the {@link Jaxb2Marshaller}
 */
public class NDCJaxb2Marshaller extends Jaxb2Marshaller
{
	private static final String SCHEMA_PREFIX = "http://www.iata.org/IATA/EDIST/2017.1 ../";

	private List<String> ndcSupportedMessages;

	@Override
	public void marshal(final Object graph, final Result result) throws XmlMappingException
	{
		if (getNdcSupportedMessages().contains(graph.getClass().getSimpleName()))
		{
			ndcMarshal(graph, result);
		}
		else
		{
			super.marshal(graph, result);
		}
	}

	public void ndcMarshal(final Object graph, final Result result) throws XmlMappingException
	{
		try
		{
			final Marshaller marshaller = this.createMarshaller();
			setAdditionalProperties(graph, marshaller);
			marshaller.marshal(graph, result);
		}
		catch (final JAXBException e)
		{
			throw this.convertJaxbException(e);
		}
	}

	protected void setAdditionalProperties(final Object graph, final Marshaller marshaller) throws PropertyException
	{
		final String schemaLocation = SCHEMA_PREFIX + graph.getClass().getSimpleName() + ".xsd";
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
	}

	protected List<String> getNdcSupportedMessages()
	{
		return ndcSupportedMessages;
	}

	@Required
	public void setNdcSupportedMessages(final List<String> ndcSupportedMessages)
	{
		this.ndcSupportedMessages = ndcSupportedMessages;
	}
}
