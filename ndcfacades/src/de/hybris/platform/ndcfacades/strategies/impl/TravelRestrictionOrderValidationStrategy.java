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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


/**
 * Strategy that extends the {@link AmendOrderValidationStrategy}.
 * The strategy is used to validate the addToOrder of a product. The addToOrder is not valid if the final quantity of the
 * product doesn't fulfil the travel restriction for that product.
 */
public class TravelRestrictionOrderValidationStrategy implements AmendOrderValidationStrategy
{
	private TravelRestrictionFacade travelRestrictionFacade;
	private ProductService productService;

	@Override
	public boolean validateAmendOrder(final OrderModel order, final String productCode, final long qty, final String travellerCode,
			final List<String> transportOfferingCodes, final String travelRouteCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		return getTravelRestrictionFacade().checkIfProductCanBeAdded(productModel, qty, travelRouteCode,
				transportOfferingCodes, travellerCode, order);
	}

	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	@Required
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}
}
