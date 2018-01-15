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

import de.hybris.platform.travelservices.dao.RatePlanDao;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.services.RatePlanService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default rate plan service.
 */
public class DefaultRatePlanService implements RatePlanService
{
	private Comparator<GuaranteeModel> guaranteeComparator;
	private RatePlanDao ratePlanDao;

	@Override
	public GuaranteeModel getGuaranteeToApply(final AccommodationOrderEntryGroupModel group, final Date date)
	{
		if (Objects.isNull(group) || Objects.isNull(group.getRatePlan())
				|| CollectionUtils.isEmpty(group.getRatePlan().getGuarantee()))
		{
			return null;
		}
		final List<GuaranteeModel> guarantees = (List<GuaranteeModel>) group.getRatePlan().getGuarantee();

		return getGuranteeToApply(guarantees, group.getStartingDate(), date);
	}

	@Override
	public GuaranteeModel getGuaranteeToApply(final AccommodationOrderEntryGroupModel group, final Date startingDate,
			final Date currentDate)
	{
		if (group.getRatePlan() == null || CollectionUtils.isEmpty(group.getRatePlan().getGuarantee()) || startingDate == null
				|| currentDate == null)
		{
			return null;
		}

		final List<GuaranteeModel> guarantees = (List<GuaranteeModel>) group.getRatePlan().getGuarantee();

		return getGuranteeToApply(guarantees, startingDate, currentDate);
	}

	/**
	 * Gets gurantee to apply.
	 *
	 * @param guarantees
	 * 		the guarantees
	 * @param startingDate
	 * 		the starting date
	 * @param currentDate
	 * 		the current date
	 *
	 * @return the gurantee to apply
	 */
	protected GuaranteeModel getGuranteeToApply(final List<GuaranteeModel> guarantees, final Date startingDate,
			final Date currentDate)
	{
		if (CollectionUtils.isEmpty(guarantees) || Objects.isNull(startingDate) || Objects.isNull(currentDate))
		{
			return null;
		}

		normalizeGuaranteesDeadlines(guarantees, startingDate, currentDate);
		final Comparator<GuaranteeModel> offSetComparator = Comparator.comparing(GuaranteeModel::getRelativeDeadline);
		final Optional<GuaranteeModel> guaranteeModel = guarantees.stream()
				.sorted(offSetComparator.reversed().thenComparing(getGuaranteeComparator().reversed())).findFirst();
		return guaranteeModel.isPresent() ? guaranteeModel.get() : null;
	}

	@Override
	public Double getAppliedGuaranteeAmount(final GuaranteeModel guaranteeToApply, final BigDecimal roomRatePrice)
	{
		return Objects.nonNull(guaranteeToApply.getFixedAmount()) ? guaranteeToApply.getFixedAmount()
				: getGuaranteeAmount(guaranteeToApply.getPercentageAmount(), roomRatePrice.doubleValue());
	}


	/**
	 * Gets guarantee amount.
	 *
	 * @param amountPercent
	 * 		the amount percent
	 * @param totalPrice
	 * 		the total price
	 *
	 * @return the guarantee amount
	 */
	protected Double getGuaranteeAmount(final Double amountPercent, final Double totalPrice)
	{
		return Objects.nonNull(amountPercent) ? totalPrice * amountPercent / 100 : 0.0d;
	}

	/**
	 * This method normalizes deadlines, setting the relative deadline to the given date if it is before this date. This
	 * allows the application of a priority policy, triggered when two or more guarantees clash of the same date.
	 * Calculation has been made based on the offset in milliseconds between the date (resolved as per above explanation)
	 * and the check in date. To make the algorithm consistent if an absolute deadline is present it is converted into
	 * the relative deadline (in milliseconds) then eventually normalized.
	 *
	 * @param guarantees
	 * 		the guarantees
	 * @param startingDate
	 * 		the starting date
	 * @param currentDate
	 * 		the current date
	 */
	protected void normalizeGuaranteesDeadlines(final List<GuaranteeModel> guarantees, final Date startingDate,
			final Date currentDate)
	{
		if (CollectionUtils.isEmpty(guarantees) || Objects.isNull(startingDate) || Objects.isNull(currentDate))
		{
			return;
		}

		final Long offSetInMillis = startingDate.toInstant().toEpochMilli() - currentDate.toInstant().toEpochMilli();
		guarantees.forEach(guarantee -> {
			if (Objects.nonNull(guarantee.getAbsoluteDeadline()))
			{
				guarantee.setRelativeDeadline(guarantee.getAbsoluteDeadline().toInstant().toEpochMilli());
			}
			if (Objects.isNull(guarantee.getRelativeDeadline()) || (offSetInMillis - guarantee.getRelativeDeadline() < 0))
			{
				guarantee.setRelativeDeadline(offSetInMillis);
			}
		});
	}

	@Override
	public RatePlanModel getRatePlanForCode(final String ratePlanCode)
	{
		return getRatePlanDao().findRatePlan(ratePlanCode);
	}

	/**
	 * Gets guarantee comparator.
	 *
	 * @return guaranteeComparator guarantee comparator
	 */
	protected Comparator<GuaranteeModel> getGuaranteeComparator()
	{
		return guaranteeComparator;
	}

	/**
	 * Sets guarantee comparator.
	 *
	 * @param guaranteeComparator
	 * 		the guaranteeComparator to set
	 */
	@Required
	public void setGuaranteeComparator(final Comparator<GuaranteeModel> guaranteeComparator)
	{
		this.guaranteeComparator = guaranteeComparator;
	}

	/**
	 * Gets rate plan dao.
	 *
	 * @return ratePlanDao rate plan dao
	 */
	protected RatePlanDao getRatePlanDao()
	{
		return ratePlanDao;
	}

	/**
	 * Sets rate plan dao.
	 *
	 * @param ratePlanDao
	 * 		the ratePlanDao to set
	 */
	@Required
	public void setRatePlanDao(final RatePlanDao ratePlanDao)
	{
		this.ratePlanDao = ratePlanDao;
	}
}
