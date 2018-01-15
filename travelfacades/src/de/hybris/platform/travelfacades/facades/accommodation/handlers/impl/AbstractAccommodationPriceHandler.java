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

package de.hybris.platform.travelfacades.facades.accommodation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.AccommodationDetailsHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler for {@link AccommodationPriceHandler} and {@link DealAccommodationPriceHandler}
 */
public abstract class AbstractAccommodationPriceHandler implements AccommodationDetailsHandler
{
	private CommonI18NService commonI18NService;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	protected String getCurrentCurrency(final AccommodationAvailabilityRequestData availabilityRequestData)
	{
		if (StringUtils.isNotBlank(availabilityRequestData.getCriterion().getCurrencyIso()))
		{
			return availabilityRequestData.getCriterion().getCurrencyIso();
		}
		else
		{
			return getCommonI18NService().getCurrentCurrency().getIsocode();
		}
	}

	protected Double getTaxesValue(final List<TaxData> taxes)
	{
		if (CollectionUtils.isEmpty(taxes))
		{
			return 0d;
		}

		Double result = 0d;
		for (final TaxData tax : taxes)
		{
			result += tax.getPrice().getValue().doubleValue();
		}
		return result;
	}

	protected List<TaxData> getTaxes(final Collection<TaxRowModel> taxes, final String currencyIso, final Double roomRateBasePrice)
	{
		final List<TaxData> result = new ArrayList<>();

		final List<TaxRowModel> taxRowModels = taxes.stream().filter(
				tax -> Objects.isNull(tax.getCurrency()) || StringUtils.equalsIgnoreCase(tax.getCurrency().getIsocode(), currencyIso))
				.collect(Collectors.toList());

		result.addAll(taxRowModels.stream()
				.map(tax -> createTaxData(
						Objects.isNull(tax.getCurrency()) ? roomRateBasePrice / 100 * tax.getValue() : tax.getValue(), currencyIso))
				.collect(Collectors.toList()));

		return result;
	}

	protected TaxData createTaxData(final Double value, final String currencyIso)
	{
		final TaxData taxData = new TaxData();
		taxData.setPrice(getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(value), currencyIso));
		return taxData;
	}

	protected Double getPriceValue(final List<PriceInformation> priceInformations)
	{
		Double value = null;

		if (CollectionUtils.isNotEmpty(priceInformations))
		{
			value = priceInformations.get(0).getPriceValue().getValue();
		}

		return value != null ? value : 0d;
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
