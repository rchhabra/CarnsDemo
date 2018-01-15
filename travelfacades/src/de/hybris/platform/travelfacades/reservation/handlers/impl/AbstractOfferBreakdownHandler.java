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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.reservation.data.OfferBreakdownData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler responsible for instantiating the offerBreakdowns of the reservationData, that will contain summary
 * of ancillaries selected by user
 */
public class AbstractOfferBreakdownHandler
{
	private Converter<ProductModel, ProductData> productConverter;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private CommonI18NService commonI18NService;

	/**
	 * Creates unique offer breakdowns for each product based on whether it is included in the bundle or not
	 *
	 * @param entries
	 *           - abstract order entries which are included/excluded from the bundle based on indicator
	 * @param offerBreakdowns
	 *           - list of unique offer breakdowns for each product
	 * @param included
	 *           - indicates whether the extra is included in bundle or not
	 */
	protected void retreiveOfferBreakdownsFromEntries(final List<AbstractOrderEntryModel> entries,
			final List<OfferBreakdownData> offerBreakdowns, final Boolean included)
	{
		for (final AbstractOrderEntryModel entry : entries)
		{
			if (CollectionUtils.isEmpty(offerBreakdowns))
			{
				final OfferBreakdownData offerBreakdown = createOfferBreakdown(entry);
				offerBreakdowns.add(offerBreakdown);
			}
			else
			{
				final OfferBreakdownData offBreak = getOfferBreakdownFromList(offerBreakdowns, entry.getProduct().getCode(),
						included);
				if (offBreak == null)
				{
					final OfferBreakdownData offerBreakdown = createOfferBreakdown(entry);
					offerBreakdowns.add(offerBreakdown);
				}
				else
				{
					updateOfferBreakdown(offBreak, entry);
				}
			}
		}
	}

	/**
	 * Creates new instance of Offer Breakdown for given entry
	 *
	 * @param entry
	 *           the entry
	 * @return new offer breakdown
	 */
	protected OfferBreakdownData createOfferBreakdown(final AbstractOrderEntryModel entry)
	{
		final OfferBreakdownData offerBreakdown = new OfferBreakdownData();
		offerBreakdown.setProduct(getProductConverter().convert(entry.getProduct()));
		offerBreakdown.setQuantity(entry.getQuantity().intValue());
		offerBreakdown.setIncluded(entry.getBundleNo() > 0 ? Boolean.TRUE : Boolean.FALSE);
		final TotalFareData totalFare = new TotalFareData();
		final String currencyIsocode = entry.getOrder().getCurrency().getIsocode();
		totalFare.setBasePrice(getTravelCommercePriceFacade().createPriceData(entry.getBasePrice(), currencyIsocode));

		totalFare.setTotalPrice(getTravelCommercePriceFacade().createPriceData(entry.getTotalPrice(), currencyIsocode));
		offerBreakdown.setTotalFare(totalFare);
		return offerBreakdown;
	}

	/**
	 * Finds an Offer Breakdown in a list which has the same product and included indicator as entry
	 *
	 * @param offerBreakdowns
	 *           the offer breakdowns
	 * @param productCode
	 *           the product code
	 * @param included
	 *           the included
	 * @return matching Offer Breakdown
	 */
	protected OfferBreakdownData getOfferBreakdownFromList(final List<OfferBreakdownData> offerBreakdowns,
			final String productCode, final Boolean included)
	{
		for (final OfferBreakdownData offerBreakdown : offerBreakdowns)
		{
			if (offerBreakdown.getProduct().getCode().equals(productCode) && included.equals(offerBreakdown.getIncluded()))
			{
				return offerBreakdown;
			}
		}
		return null;
	}

	/**
	 * Updates quantity and price of existing offer breakdown with details from given entry
	 *
	 * @param offerBreakdown
	 *           the offer breakdown
	 * @param entry
	 *           the entry
	 */
	protected void updateOfferBreakdown(final OfferBreakdownData offerBreakdown, final AbstractOrderEntryModel entry)
	{
		final int updatedQuantity = offerBreakdown.getQuantity() + entry.getQuantity().intValue();
		offerBreakdown.setQuantity(updatedQuantity);

		final BigDecimal totalPrice = (updatedQuantity == 0) ? BigDecimal.valueOf(entry.getTotalPrice())
				: offerBreakdown.getTotalFare().getTotalPrice().getValue().add(BigDecimal.valueOf(entry.getTotalPrice()));
		final String currencyIisocode = entry.getOrder().getCurrency().getIsocode();
		offerBreakdown.getTotalFare()
				.setTotalPrice(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue(), currencyIisocode));
	}

	/**
	 * Gets product converter.
	 *
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * Sets product converter.
	 *
	 * @param productConverter
	 *           the productConverter to set
	 */
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the commonI18NService
	 */
	@Deprecated
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Deprecated
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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
