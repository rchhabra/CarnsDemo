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

package de.hybris.platform.travelrulesengine.rao.providers.impl;

import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.TransportOfferingRAO;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * RAO Provider which creates TransportOfferingRAO facts to be used in rules evaluation
 */
public class DefaultTransportOfferingRAOProvider implements RAOProvider
{

	private Converter<TransportOfferingData, TransportOfferingRAO> transportOfferingRaoConverter;

	@Override
	public Set<Object> expandFactModel(final Object modelFact)
	{
		return modelFact instanceof TransportOfferingData ? createRAO((TransportOfferingData) modelFact)
				: Collections.emptySet();
	}

	/**
	 * converts TransportOfferingData to TransportOfferingRAO and return set of facts
	 *
	 * @param source
	 * @return
	 */
	protected Set<Object> createRAO(final TransportOfferingData source)
	{
		final Set<Object> facts = new LinkedHashSet<Object>();
		facts.add(getTransportOfferingRaoConverter().convert(source));
		return facts;
	}

	/**
	 * Gets transport offering rao converter.
	 *
	 * @return the transport offering rao converter
	 */
	protected Converter<TransportOfferingData, TransportOfferingRAO> getTransportOfferingRaoConverter()
	{
		return transportOfferingRaoConverter;
	}

	/**
	 * Sets transport offering rao converter.
	 *
	 * @param transportOfferingRaoConverter
	 * 		the transport offering rao converter
	 */
	@Required
	public void setTransportOfferingRaoConverter(
			final Converter<TransportOfferingData, TransportOfferingRAO> transportOfferingRaoConverter)
	{
		this.transportOfferingRaoConverter = transportOfferingRaoConverter;
	}

}
