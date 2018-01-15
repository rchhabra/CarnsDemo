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
package de.hybris.platform.ndcfacades.airshopping.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.ndcfacades.airshopping.AirShoppingFacade;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AirShoppingFacade}
 */
public class DefaultAirShoppingFacade implements AirShoppingFacade
{
	private FareSearchFacade fareSearchFacade;
	private Converter<AirShoppingRQ,FareSearchRequestData> ndcFareSearchRequestConverter;
	private Converter<FareSelectionData,AirShoppingRS> ndcFareSelectionDataConverter;

	@Override
	public AirShoppingRS doSearch(final AirShoppingRQ airShoppingRQ)
	{
		final AirShoppingRS airShoppingRS = new AirShoppingRS();
		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();

		getNdcFareSearchRequestConverter().convert(airShoppingRQ, fareSearchRequestData);

		fareSearchRequestData.setSalesApplication(SalesApplication.NDC);

		final FareSelectionData fareSelectionData = getFareSearchFacade().doSearch(fareSearchRequestData);

		getNdcFareSelectionDataConverter().convert(fareSelectionData, airShoppingRS);

		return airShoppingRS;
	}

	/**
	 * Gets fare search facade.
	 *
	 * @return the fare search facade
	 */
	protected FareSearchFacade getFareSearchFacade()
	{
		return fareSearchFacade;
	}

	/**
	 * Sets fare search facade.
	 *
	 * @param fareSearchFacade
	 * 		the fare search facade
	 */
	@Required
	public void setFareSearchFacade(final FareSearchFacade fareSearchFacade)
	{
		this.fareSearchFacade = fareSearchFacade;
	}

	/**
	 * Gets ndc fare search request converter.
	 *
	 * @return the ndc fare search request converter
	 */
	protected Converter<AirShoppingRQ, FareSearchRequestData> getNdcFareSearchRequestConverter()
	{
		return ndcFareSearchRequestConverter;
	}

	/**
	 * Sets ndc fare search request converter.
	 *
	 * @param ndcFareSearchRequestConverter
	 * 		the ndc fare search request converter
	 */
	@Required
	public void setNdcFareSearchRequestConverter(
			final Converter<AirShoppingRQ, FareSearchRequestData> ndcFareSearchRequestConverter)
	{
		this.ndcFareSearchRequestConverter = ndcFareSearchRequestConverter;
	}

	/**
	 * Gets ndc fare selection data converter.
	 *
	 * @return the ndc fare selection data converter
	 */
	protected Converter<FareSelectionData, AirShoppingRS> getNdcFareSelectionDataConverter()
	{
		return ndcFareSelectionDataConverter;
	}

	/**
	 * Sets ndc fare selection data converter.
	 *
	 * @param ndcFareSelectionDataConverter
	 * 		the ndc fare selection data converter
	 */
	@Required
	public void setNdcFareSelectionDataConverter(
			final Converter<FareSelectionData, AirShoppingRS> ndcFareSelectionDataConverter)
	{
		this.ndcFareSelectionDataConverter = ndcFareSelectionDataConverter;
	}
}
