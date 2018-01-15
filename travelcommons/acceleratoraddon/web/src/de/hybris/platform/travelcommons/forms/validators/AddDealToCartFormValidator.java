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

package de.hybris.platform.travelcommons.forms.validators;

import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelcommons.forms.cms.AddDealToCartForm;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component("addDealToCartFormValidator")
public class AddDealToCartFormValidator extends AbstractTravelValidator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AddDealToCartFormValidator.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AddDealToCartForm form = (AddDealToCartForm) object;
		validateBlankField("bundleTemplateID", form.getBundleTemplateID(), errors);
		validateDates(form.getStartingDate(), TravelacceleratorstorefrontValidationConstants.STARTING_DATE, form.getEndingDate(),
				TravelacceleratorstorefrontValidationConstants.ENDING_DATE, errors);

		validateEmptyField("itineraryPricingInfos", form.getItineraryPricingInfos(), errors);
		validateEmptyField("passengerTypes", form.getPassengerTypes(), errors);
	}

}
