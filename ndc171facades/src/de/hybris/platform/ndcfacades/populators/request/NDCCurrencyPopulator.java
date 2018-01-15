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
import de.hybris.platform.ndcfacades.ndc.MessageParamsBaseType.CurrCodes.FiledInCurrency;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

/**
 * The NDC Currency populator for {@link AirShoppingRQ}
 */
public class NDCCurrencyPopulator implements Populator<AirShoppingRQ, FareSearchRequestData>
{

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData)
	{
		if(Objects.nonNull(airShoppingRQ.getParameters()) && Objects.nonNull(airShoppingRQ.getParameters().getCurrCodes()) && CollectionUtils.isNotEmpty(airShoppingRQ.getParameters().getCurrCodes().getFiledInCurrency()))
		{
			final FiledInCurrency filledInCurrency=airShoppingRQ.getParameters().getCurrCodes().getFiledInCurrency().stream().findFirst().get();
			if(Objects.nonNull(filledInCurrency.getCurrCode()))
			{
				final POSData posData = new POSData();
				final SourceData source = new SourceData();
				posData.setSource(source);
				source.setIsoCurrencyCode(filledInCurrency.getCurrCode().getValue());
				posData.setSource(source);
				fareSearchRequestData.setPos(posData);
			}
		}
	}

}
