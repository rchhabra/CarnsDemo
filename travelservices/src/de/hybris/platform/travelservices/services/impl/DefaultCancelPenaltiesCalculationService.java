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

import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.services.CancelPenaltiesCalculationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link CancelPenaltiesCalculationService} implementing methods to handle cancel penalties
 * calculation
 */
public class DefaultCancelPenaltiesCalculationService implements CancelPenaltiesCalculationService
{
	private TimeService timeService;


	@Override
	public CancelPenaltyModel getActiveCancelPenalty(final Collection<CancelPenaltyModel> cancelPenalties, final Date checkInDate,
			final BigDecimal plannedAmount)
	{
		if (CollectionUtils.isEmpty(cancelPenalties) || Objects.isNull(checkInDate) || Objects.isNull(plannedAmount))
		{
			return null;
		}
		final List<CancelPenaltyModel> activeCancelPenalties = cancelPenalties.stream()
				.filter(cancelPenalty -> isCancelPenaltyDeadLineMet(cancelPenalty, checkInDate)).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(activeCancelPenalties))
		{
			return null;
		}

		final Comparator<CancelPenaltyModel> deadLinesComparator = Comparator.comparing(
				cancelPenalty -> getCancelPenaltyDeadline(cancelPenalty, checkInDate),
				Comparator.nullsFirst(Comparator.naturalOrder()));
		final Comparator<CancelPenaltyModel> amountComparator = Comparator
				.comparing(cancelPenalty -> getCancelPenaltyAmount(cancelPenalty, plannedAmount));

		return Collections.max(activeCancelPenalties, deadLinesComparator.thenComparing(amountComparator));
	}

	protected boolean isCancelPenaltyDeadLineMet(final CancelPenaltyModel cancelPenalty, final Date checkInDate)
	{
		final Date cancelPenalityDeadLineDate = getCancelPenaltyDeadline(cancelPenalty, checkInDate);
		if (Objects.isNull(cancelPenalityDeadLineDate))
		{
			return Boolean.TRUE;
		}

		return cancelPenalityDeadLineDate.before(getTimeService().getCurrentTime());
	}

	@Override
	public Date getCancelPenaltyDeadline(final CancelPenaltyModel cancelPenalty, final Date checkInDate)
	{
		if (Objects.isNull(cancelPenalty) || Objects.isNull(checkInDate)
				|| (Objects.isNull(cancelPenalty.getAbsoluteDeadline()) && Objects.isNull(cancelPenalty.getRelativeDeadline())))
		{
			return null;
		}

		Date deadlineDate = checkInDate;
		if (Objects.nonNull(cancelPenalty.getRelativeDeadline()))
		{
			deadlineDate = new Date(checkInDate.getTime() - cancelPenalty.getRelativeDeadline());
		}
		if (Objects.nonNull(cancelPenalty.getAbsoluteDeadline()) && cancelPenalty.getAbsoluteDeadline().before(deadlineDate))
		{
			deadlineDate = cancelPenalty.getAbsoluteDeadline();
		}

		return deadlineDate;
	}

	@Override
	public BigDecimal getCancelPenaltyAmount(final CancelPenaltyModel cancelPenalty, final BigDecimal plannedAmount)
	{
		if (Objects.isNull(cancelPenalty) || Objects.isNull(plannedAmount))
		{
			return BigDecimal.ZERO;
		}
		final BigDecimal fixedAmount = cancelPenalty.getFixedAmount() != null ? BigDecimal.valueOf(cancelPenalty.getFixedAmount())
				: BigDecimal.ZERO;
		final BigDecimal percentageAmount = cancelPenalty.getPercentageAmount() != null
				? plannedAmount.multiply(BigDecimal.valueOf(cancelPenalty.getPercentageAmount())).divide(BigDecimal.valueOf(100d),
						RoundingMode.HALF_UP)
				: BigDecimal.ZERO;

		return fixedAmount.max(percentageAmount);
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
