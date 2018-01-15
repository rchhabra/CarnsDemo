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
*/

package de.hybris.platform.travelfacades.ancillary.search.converters.populator;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;


/**
 * Populator class to populate TravelRestriction for Category
 */
public class CategoryTravelRestrictionPopulator implements Populator<CategoryModel, OfferGroupData>
{

	private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;

	@Override
	public void populate(final CategoryModel source, final OfferGroupData target) throws ConversionException
	{

		final OfferGroupRestrictionModel offerGroupRestrictionModel = source.getTravelRestriction();
		TravelRestrictionData travelRestrictionData = new TravelRestrictionData();

		if (offerGroupRestrictionModel == null)
		{
			travelRestrictionData = setDefaultTravelRestrictionValues(travelRestrictionData);
		}
		else
		{
			travelRestrictionData = getTravelRestrictionConverter().convert(offerGroupRestrictionModel);
			travelRestrictionData.setAddToCartCriteria(getAddToCartCriteria(offerGroupRestrictionModel));
		}

		target.setTravelRestriction(travelRestrictionData);
	}

	/**
	 * Returns the addToCartCriteria code of the given offerGroupRestrictionModel. If the offerGroupRestrictionModel or
	 * the addToCartCriteria attribute are null, the default addToCartCriteria code is returned.
	 *
	 * @param offerGroupRestrictionModel
	 * @return the addToCartCriteria code
	 */
	protected String getAddToCartCriteria(final OfferGroupRestrictionModel offerGroupRestrictionModel)
	{
		return offerGroupRestrictionModel.getAddToCartCriteria() != null
				? offerGroupRestrictionModel.getAddToCartCriteria().getCode()
				: TravelfacadesConstants.DEFAULT_ADD_TO_CART_CRITERIA;
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
	 * @return travelRestrictionConverter
	 */
	protected Converter<TravelRestrictionModel, TravelRestrictionData> getTravelRestrictionConverter()
	{
		return travelRestrictionConverter;
	}

	/**
	 * @param travelRestrictionConverter
	 *           as the travelRestrictionConverter to set
	 */
	public void setTravelRestrictionConverter(
			final Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter)
	{
		this.travelRestrictionConverter = travelRestrictionConverter;
	}

}
