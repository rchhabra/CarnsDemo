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

package de.hybris.platform.accommodationaddon.validators;

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AddRequestForm;
import de.hybris.platform.util.Config;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("accommodationRequestValidator")
public class AccommodationRequestValidator implements Validator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AddRequestForm.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AddRequestForm form = (AddRequestForm) object;
		final String message = form.getRequestMessage();

		if (StringUtils.isBlank(message))
		{
			errors.rejectValue(AccommodationaddonWebConstants.REQUEST_MESSAGE,
					AccommodationaddonWebConstants.REQUEST_MESSAGE_ERROR_CODE);
		}

		if (Objects.nonNull(message) && !new IntRange(Config.getInt("accommodation.request.min.size", 1),
				Config.getInt("accommodation.request.max.size", 255)).containsInteger((message.length())))
		{
			errors.rejectValue(AccommodationaddonWebConstants.REQUEST_MESSAGE,
					AccommodationaddonWebConstants.REQUEST_MESSAGE_ERROR_CODE);
		}

	}

}
