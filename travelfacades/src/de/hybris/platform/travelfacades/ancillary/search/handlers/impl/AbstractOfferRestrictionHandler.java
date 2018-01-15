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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler responsible for instantiating the travelRestriction
 */
public abstract class AbstractOfferRestrictionHandler
{
	private TravelRestrictionFacade travelRestrictionFacade;
	private ProductService productService;
	private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;


	/**
	 * This method sets the travelRestrictionData to the offerPricingInfoData for the corresponding productData
	 *
	 * @param offerPricingInfoData
	 *           as the OfferPricingInfoData with the productData required to retrieve and then set the TravelRestriction
	 */
	public void setTravelRestriction(final OfferPricingInfoData offerPricingInfoData)
	{
		final String productCode = offerPricingInfoData.getProduct().getCode();
		final ProductModel product = getProductService().getProductForCode(productCode);
		final TravelRestrictionModel travelRestrictionModel = product.getTravelRestriction();

		TravelRestrictionData travelRestrictionData = new TravelRestrictionData();
		if (travelRestrictionModel == null)
		{
			travelRestrictionData = setDefaultTravelRestrictionValues(travelRestrictionData);
		}
		else
		{
			travelRestrictionData = getTravelRestrictionConverter().convert(travelRestrictionModel);
		}
		final String addToCartCriteria = getTravelRestrictionFacade().getAddToCartCriteria(productCode);
		travelRestrictionData.setAddToCartCriteria(addToCartCriteria);

		offerPricingInfoData.setTravelRestriction(travelRestrictionData);
	}

	/**
	 * Method to get the travelRestrictionData with the default values: effectiveDate = empty, expireDate = empty,
	 * travellerMinOfferQty = 0, travellerMaxOfferQty = -1, tripMinOfferQty = 0, tripMaxOfferQty = -1, addToCartCriteria
	 * = PER_LEG_PER_PAX. The constant -1 is the value to represent no restriction on number
	 *
	 * @param travelRestrictionData
	 *           the travelRestrictionData to populate with the default values
	 *
	 * @return the travelRestrictionData with the default values
	 */
	protected TravelRestrictionData setDefaultTravelRestrictionValues(final TravelRestrictionData travelRestrictionData)
	{
		travelRestrictionData.setTravellerMaxOfferQty(TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION);
		travelRestrictionData.setTravellerMinOfferQty(TravelfacadesConstants.DEFAULT_MIN_QUANTITY_RESTRICTION);
		travelRestrictionData.setTripMaxOfferQty(TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION);
		travelRestrictionData.setTripMinOfferQty(TravelfacadesConstants.DEFAULT_MIN_QUANTITY_RESTRICTION);
		travelRestrictionData.setAddToCartCriteria(TravelfacadesConstants.DEFAULT_ADD_TO_CART_CRITERIA);

		return travelRestrictionData;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the travelRestrictionConverter
	 */
	protected Converter<TravelRestrictionModel, TravelRestrictionData> getTravelRestrictionConverter()
	{
		return travelRestrictionConverter;
	}

	/**
	 * @param travelRestrictionConverter
	 *           the travelRestrictionConverter to set
	 */
	@Required
	public void setTravelRestrictionConverter(
			final Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter)
	{
		this.travelRestrictionConverter = travelRestrictionConverter;
	}

	/**
	 * @return the travelRestrictionFacade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * @param travelRestrictionFacade
	 *           the travelRestrictionFacade to set
	 */
	@Required
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}
}
