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

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadata;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadatas;
import de.hybris.platform.ndcfacades.ndc.SeatAvailResMetadataType;
import de.hybris.platform.ndcfacades.ndc.SeatAvailResMetadataType.Other;
import de.hybris.platform.ndcfacades.ndc.SeatAvailResMetadataType.Other.OtherMetadata;
import de.hybris.platform.ndcfacades.ndc.SeatAvailabilityRS;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Required;


/**
 * NDC Metadata Populator populate currency related information for {@link SeatAvailabilityRS}
 */
public class NDCSeatAvailabilityRSMetadataPopulator implements Populator<OfferResponseData, SeatAvailabilityRS>
{
	private StoreSessionFacade storeSessionFacade;

	@Override
	public void populate(final OfferResponseData offerResponseData, final SeatAvailabilityRS seatAvailabilityRS)
			throws ConversionException
	{
		final SeatAvailResMetadataType metadata = new SeatAvailResMetadataType();
		final Other other = new Other();
		final OtherMetadata otherMetadata = new OtherMetadata();
		final CurrencyMetadatas currencyMetadatas = new CurrencyMetadatas();
		final CurrencyMetadata currencyMetadata = new CurrencyMetadata();

		currencyMetadata.setMetadataKey(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		currencyMetadata.setDecimals(BigInteger.valueOf(NdcfacadesConstants.PRECISION));
		currencyMetadatas.getCurrencyMetadata().add(currencyMetadata);
		otherMetadata.setCurrencyMetadatas(currencyMetadatas);
		other.getOtherMetadata().add(otherMetadata);
		metadata.setOther(other);
		seatAvailabilityRS.setMetadata(metadata);
	}

	/**
	 * Gets store session facade.
	 *
	 * @return the store session facade
	 */
	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	/**
	 * Sets store session facade.
	 *
	 * @param storeSessionFacade
	 * 		the store session facade
	 */
	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}
}
