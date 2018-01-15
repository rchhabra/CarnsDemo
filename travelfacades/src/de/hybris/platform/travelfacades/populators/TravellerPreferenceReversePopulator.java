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

import de.hybris.platform.commercefacades.travel.TravellerPreferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.enums.TravellerPreferenceType;
import de.hybris.platform.travelservices.model.user.TravellerPreferenceModel;


/**
 * Populator to populate the TravellerPreferenceModel from the TravellerPreferenceData
 */
public class TravellerPreferenceReversePopulator implements Populator<TravellerPreferenceData, TravellerPreferenceModel>
{

	@Override
	public void populate(final TravellerPreferenceData source, final TravellerPreferenceModel target) throws ConversionException
	{
		if (source != null)
		{
			target.setType(TravellerPreferenceType.valueOf(source.getType()));
			target.setValue(source.getValue());
		}
	}

}
