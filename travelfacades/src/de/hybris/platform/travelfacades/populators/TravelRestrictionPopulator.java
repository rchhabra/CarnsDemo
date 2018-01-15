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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;

import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link TravelRestrictionModel} as source and {@link TravelRestrictionData} as target type.
 */
public class TravelRestrictionPopulator implements Populator<TravelRestrictionModel, TravelRestrictionData>
{

   @Override
   public void populate(final TravelRestrictionModel source, final TravelRestrictionData target) throws ConversionException
   {
      Assert.notNull(source, "Parameter source cannot be null.");
      Assert.notNull(target, "Parameter target cannot be null.");

      target.setEffectiveDate(source.getEffectiveDate());
      target.setExpireDate(source.getExpireDate());

		target.setTravellerMinOfferQty(source.getTravellerMinOfferQty() != null ? source.getTravellerMinOfferQty()
				: TravelfacadesConstants.DEFAULT_MIN_QUANTITY_RESTRICTION);
		target.setTravellerMaxOfferQty(source.getTravellerMaxOfferQty() != null ? source.getTravellerMaxOfferQty()
				: TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION);
		target.setTripMinOfferQty(source.getTripMinOfferQty() != null ? source.getTripMinOfferQty()
				: TravelfacadesConstants.DEFAULT_MIN_QUANTITY_RESTRICTION);
		target.setTripMaxOfferQty(source.getTripMaxOfferQty() != null ? source.getTripMaxOfferQty()
				: TravelfacadesConstants.DEFAULT_MAX_QUANTITY_RESTRICTION);

		if (CollectionUtils.isNotEmpty(source.getPassengerTypes()))
		{
			target.setPassengerTypes(source.getPassengerTypes().stream().map(String::toLowerCase).collect(Collectors.toList()));
		}
   }

}
