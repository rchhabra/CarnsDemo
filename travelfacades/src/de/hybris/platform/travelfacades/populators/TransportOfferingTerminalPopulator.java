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
package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.TerminalModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel} as
 * source and {@link de.hybris.platform.commercefacades.travel.TransportOfferingData} as target type.
 */
public class TransportOfferingTerminalPopulator implements Populator<TransportOfferingModel, TransportOfferingData>
{

	private Converter<TerminalModel, TerminalData> terminalConverter;

	@Override
	public void populate(final TransportOfferingModel source, final TransportOfferingData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getOriginTerminal() != null)
		{
			target.setOriginTerminal(getTerminalConverter().convert(source.getOriginTerminal()));
		}

		if (source.getDestinationTerminal() != null)
		{
			target.setDestinationTerminal(getTerminalConverter().convert(source.getDestinationTerminal()));
		}

	}

	/**
	 * Gets terminal converter.
	 *
	 * @return the terminal converter
	 */
	protected Converter<TerminalModel, TerminalData> getTerminalConverter()
	{
		return terminalConverter;
	}

	/**
	 * Sets terminal converter.
	 *
	 * @param terminalConverter
	 * 		the terminal converter
	 */
	@Required
	public void setTerminalConverter(final Converter<TerminalModel, TerminalData> terminalConverter)
	{
		this.terminalConverter = terminalConverter;
	}
}
