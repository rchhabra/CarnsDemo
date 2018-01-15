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

import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.travel.TerminalData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.model.travel.TerminalModel;


/**
 * Converter implementation for {@link de.hybris.platform.travelservices.model.travel.TerminalModel} as source and {@link
 * de.hybris.platform.commercefacades.travel.TerminalData} as target type.
 */
public class TerminalPopulator implements Populator<TerminalModel, TerminalData>
{

    @Override
    public void populate(final TerminalModel source, final TerminalData target) throws ConversionException
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCode(source.getCode());
        target.setName(source.getName());
    }

}
