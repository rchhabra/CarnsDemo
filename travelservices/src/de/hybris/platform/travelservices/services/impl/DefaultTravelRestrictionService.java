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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;
import de.hybris.platform.travelservices.services.TravelRestrictionService;

import java.util.Optional;


/**
 * Default implementation of the {@link TravelRestrictionService} interface.
 */
public class DefaultTravelRestrictionService implements TravelRestrictionService
{

	private EnumerationService enumerationService;

	@Override
	public AddToCartCriteriaType getAddToCartCriteria(final ProductModel productModel)
	{
		final Optional<CategoryModel> offerGroupOptional = productModel.getSupercategories().stream().findAny();
		if (offerGroupOptional.isPresent())
		{
			final OfferGroupRestrictionModel offerGroupRestrictionModel = offerGroupOptional.get().getTravelRestriction();
			if (offerGroupRestrictionModel != null && offerGroupRestrictionModel.getAddToCartCriteria() != null)
			{
				return offerGroupRestrictionModel.getAddToCartCriteria();
			}
		}
		return getEnumerationService().getEnumerationValue(AddToCartCriteriaType.class,
				TravelservicesConstants.DEFAULT_ADD_TO_CART_CRITERIA);
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

}
