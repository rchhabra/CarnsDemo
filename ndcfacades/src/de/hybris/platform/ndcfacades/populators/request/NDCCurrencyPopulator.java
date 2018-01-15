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
package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.POSData;
import de.hybris.platform.commercefacades.travel.SourceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;

import java.util.Objects;


/**
 * The NDC Currency populator for {@link AirShoppingRQ}
 */
public class NDCCurrencyPopulator implements Populator<AirShoppingRQ, FareSearchRequestData>
{

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData)
	{
		if (!Objects.isNull(airShoppingRQ.getParameters()) && !Objects.isNull(airShoppingRQ.getParameters().getCurrCodes())
				&& !airShoppingRQ.getParameters().getCurrCodes().getCurrCode().isEmpty()
				&& !Objects.isNull(airShoppingRQ.getParameters().getCurrCodes().getCurrCode().get(0).getValue()))
		{
			final POSData posData = new POSData();
			final SourceData source = new SourceData();

			posData.setSource(source);

			source.setIsoCurrencyCode(airShoppingRQ.getParameters().getCurrCodes().getCurrCode().get(0).getValue());

			posData.setSource(source);
			fareSearchRequestData.setPos(posData);
		}
	}

}
