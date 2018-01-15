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
 */

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.travelservices.dao.AbstractOrderEntryGroupDao;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Default travel model service.
 */
public class DefaultTravelModelService extends DefaultModelService
{
	private AbstractOrderEntryGroupDao abstractOrderEntryGroupDao;

	@Override
	public <T> T clone(T original) {
		T clone = super.clone(original);

		if(!(clone instanceof AbstractOrderModel))
		{
			return clone;
		}

		AbstractOrderModel clonedOrder = (AbstractOrderModel) clone;

		cloneTravelOrderEntryInfo(clonedOrder);
		cloneAbstractOrderEntryGroup((AbstractOrderModel) original, clonedOrder);

		return clone;
	}

	/**
	 * Clone abstract order entry group.
	 *
	 * @param originalOrder
	 * 		the original order
	 * @param clonedOrder
	 * 		the cloned order
	 */
	protected void cloneAbstractOrderEntryGroup(final AbstractOrderModel originalOrder, final AbstractOrderModel clonedOrder)
	{
		final List<AbstractOrderEntryGroupModel> abstractOrderEntryGroups = getAbstractOrderEntryGroupDao()
				.findAbstractOrderEntryGroups(originalOrder);
		if (CollectionUtils.isNotEmpty(abstractOrderEntryGroups))
		{
			abstractOrderEntryGroups.forEach(originalEntryGroup ->
			{
				final AbstractOrderEntryGroupModel clonedEntryGroup = super.clone(originalEntryGroup);
				super.save(clonedEntryGroup);

				clonedOrder.getEntries().stream().filter(entry -> originalEntryGroup.equals(entry.getEntryGroup()))
						.forEach(entry -> entry.setEntryGroup(clonedEntryGroup));
			});
		}
	}

	/**
	 * Clone travel order entry info.
	 *
	 * @param clonedOrder
	 * 		the cloned order
	 */
	protected void cloneTravelOrderEntryInfo(final AbstractOrderModel clonedOrder)
	{
		clonedOrder.getEntries().forEach(entry ->
		{
			if (OrderEntryType.TRANSPORT.equals(entry.getType()))
			{
				final TravelOrderEntryInfoModel clonedInfo = super
						.clone(entry.getTravelOrderEntryInfo(), TravelOrderEntryInfoModel.class);
				entry.setTravelOrderEntryInfo(clonedInfo);
			}
		});
	}

	/**
	 * Gets abstract order entry group dao.
	 *
	 * @return the abstractOrderEntryGroupDao
	 */
	protected AbstractOrderEntryGroupDao getAbstractOrderEntryGroupDao()
	{
		return abstractOrderEntryGroupDao;
	}

	/**
	 * Sets abstract order entry group dao.
	 *
	 * @param abstractOrderEntryGroupDao
	 * 		the abstractOrderEntryGroupDao to set
	 */
	@Required
	public void setAbstractOrderEntryGroupDao(final AbstractOrderEntryGroupDao abstractOrderEntryGroupDao)
	{
		this.abstractOrderEntryGroupDao = abstractOrderEntryGroupDao;
	}
}
