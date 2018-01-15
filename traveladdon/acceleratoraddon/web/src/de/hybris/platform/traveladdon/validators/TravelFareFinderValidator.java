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

package de.hybris.platform.traveladdon.validators;

import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


/**
 *
 * Custom validation to valid Fare Finder Form attributes which can't be validated using JSR303
 *
 */

@Component("travelFareFinderValidator")
public class TravelFareFinderValidator extends FareFinderValidator
{
	private static final String FIELD_DEPARTING_DATE_TIME = "departingDateTime";
	private static final String ERROR_TYPE_OUT_OF_RANGE = "OutOfRange";

	/**
	 * Method used to validate Departing and Arrival dates
	 *
	 * @param fareFinderForm
	 * @param errors
	 */
	@Override
	protected void validateDepartureAndArrivalDateTimeRange(final FareFinderForm fareFinderForm, final Errors errors)
	{
		if (StringUtils.equalsIgnoreCase(fareFinderForm.getTripType(), TripType.RETURN.name()))
		{
			Date departingDate = TravelDateUtils.getDate(fareFinderForm.getDepartingDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			departingDate = TravelDateUtils.addDays(departingDate, 1);
			final Date arrivalDate = TravelDateUtils.getDate(fareFinderForm.getReturnDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			if (departingDate.after(arrivalDate))
			{
				rejectValue(errors, FIELD_DEPARTING_DATE_TIME, ERROR_TYPE_OUT_OF_RANGE);
			}
		}
	}

}
