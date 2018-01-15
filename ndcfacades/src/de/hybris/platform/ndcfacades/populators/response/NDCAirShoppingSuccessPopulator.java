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
package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * NDC Success Populator for {@link AirShoppingRS}
 */
public class NDCAirShoppingSuccessPopulator extends NDCRSSuccessPopulator implements Populator<FareSelectionData, AirShoppingRS>
{
	@Override
	public void populate(final FareSelectionData fareSelectionData, final AirShoppingRS airShoppingRS) throws ConversionException
	{
		airShoppingRS.setSuccess(getNDCSuccessType());
	}
}
