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
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationReviewForm;
import de.hybris.platform.util.Config;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("accommodationReviewValidator")
public class AccommodationReviewValidator implements Validator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AccommodationReviewForm.class.equals(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AccommodationReviewForm form = (AccommodationReviewForm) object;

		final String headline = form.getHeadline();
		if (StringUtils.isBlank(headline))
		{
			errors.rejectValue(AccommodationaddonWebConstants.REVIEW_HEADLINE,
					AccommodationaddonWebConstants.REVIEW_BLANK_HEADLINE_ERROR_CODE);
		}

		if (Objects.nonNull(headline) && !new IntRange(Config.getInt("accommodation.review.headline.min.size", 1),
				Config.getInt("accommodation.review.headline.max.size", 255)).containsInteger((headline.length())))
		{
			errors.rejectValue(AccommodationaddonWebConstants.REVIEW_HEADLINE,
					AccommodationaddonWebConstants.REVIEW_HEADLINE_ERROR_CODE);
		}

		final String comment = form.getComment();
		if (StringUtils.isBlank(comment))
		{
			errors.rejectValue(AccommodationaddonWebConstants.REVIEW_COMMENT,
					AccommodationaddonWebConstants.REVIEW_BLANK_COMMENT_ERROR_CODE);
		}

		if (Objects.nonNull(comment) && comment.length() < (Config.getInt("accommodation.review.comment.min.size", 1)))
		{
			errors.rejectValue(AccommodationaddonWebConstants.REVIEW_COMMENT,
					AccommodationaddonWebConstants.REVIEW_COMMENT_ERROR_CODE);
		}


		try
		{
			final Double rating = form.getRating();
			if (!new DoubleRange(Config.getDouble("customerreview.minimalrating", 0),
					Config.getDouble("customerreview.maximalrating", 10)).containsDouble(rating))
			{
				errors.rejectValue(AccommodationaddonWebConstants.REVIEW_RATING,
						AccommodationaddonWebConstants.REVIEW_RATING_ERROR_CODE);
			}
		}
		catch (final NumberFormatException NFex)
		{
			errors.rejectValue(AccommodationaddonWebConstants.REVIEW_RATING,
					AccommodationaddonWebConstants.REVIEW_RATING_ERROR_CODE);
		}

	}

}
