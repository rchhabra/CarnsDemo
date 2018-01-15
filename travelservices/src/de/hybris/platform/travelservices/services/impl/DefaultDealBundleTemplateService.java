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

import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link DealBundleTemplateService}
 */
public class DefaultDealBundleTemplateService implements DealBundleTemplateService
{
	private BundleTemplateService bundleTemplateService;
	private BookingService bookingService;

	@Override
	public DealBundleTemplateModel getDealBundleTemplateById(final String dealBundleTemplateId)
	{
		final BundleTemplateModel bundleTemplateModel = getBundleTemplateService().getBundleTemplateForCode(dealBundleTemplateId);

		if (!(bundleTemplateModel instanceof DealBundleTemplateModel))
		{
			return null;
		}

		return (DealBundleTemplateModel) bundleTemplateModel;
	}

	@Override
	public boolean isDealBundleOrder(final String orderCode)
	{
		final OrderModel order = getBookingService().getOrder(orderCode);
		return abstractOrderIsDeal(order);
	}

	@Override
	public boolean abstractOrderIsDeal(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.isNull(abstractOrderModel))
		{
			return Boolean.FALSE;
		}
		final List<AbstractOrderEntryModel> bundledEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> entry.getBundleNo() > 0).collect(Collectors.toList());

		final boolean hasAccommodationBundle = bundledEntries.stream()
				.anyMatch(entry -> entry.getBundleTemplate() instanceof AccommodationBundleTemplateModel);
		final boolean hasTransportBundle = bundledEntries.stream()
				.anyMatch(entry -> entry.getBundleTemplate().getClass().equals(BundleTemplateModel.class));

		if (hasAccommodationBundle && hasTransportBundle)
		{
			final List<BundleTemplateModel> rootBundleTemplates = bundledEntries.stream()
					.map(entry -> getBundleTemplateService().getRootBundleTemplate(entry.getBundleTemplate())).distinct().limit(2)
					.collect(Collectors.toList());

			return rootBundleTemplates.size() <= 1
					&& rootBundleTemplates.stream().allMatch(DealBundleTemplateModel.class::isInstance);
		}
		return false;
	}

	/**
	 * @return the bundleTemplateService
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * @param bundleTemplateService
	 *           the bundleTemplateService to set
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
