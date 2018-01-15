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

package de.hybris.platform.travelservices.ordersplitting.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.impl.DefaultConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.dao.TravelConsignmentDao;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.ordersplitting.TravelConsignmentService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Service Implementation to create Consignment
 */
public class DefaultTravelConsignmentService extends DefaultConsignmentService implements TravelConsignmentService
{
	private ModelService modelService;
	private TravelConsignmentDao travelConsignmentDao;

	@Override
	public ConsignmentModel createConsignment(final AbstractOrderModel order, final String code,
			final List<AbstractOrderEntryModel> orderEntries) throws ConsignmentCreationException
	{
		final ConsignmentModel cons = (ConsignmentModel) getModelService().create(ConsignmentModel.class);

		cons.setStatus(ConsignmentStatus.READY);
		cons.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
		cons.setCode(code);

		if (order != null)
		{
			cons.setShippingAddress(order.getDeliveryAddress());
		}

		for (final AbstractOrderEntryModel orderEntry : orderEntries)
		{

			final ConsignmentEntryModel entry = (ConsignmentEntryModel) getModelService().create(ConsignmentEntryModel.class);

			entry.setOrderEntry(orderEntry);
			entry.setQuantity(orderEntry.getQuantity());
			if (Objects.nonNull(orderEntry.getTravelOrderEntryInfo())
					&& CollectionUtils.isNotEmpty(orderEntry.getTravelOrderEntryInfo().getTravellers()))
			{
				final Optional<TravellerModel> travellerModel = orderEntry.getTravelOrderEntryInfo().getTravellers().stream()
						.findFirst();
				if (travellerModel.isPresent())
				{
					cons.setTraveller(travellerModel.get());
				}
			}
			entry.setConsignment(cons);
			cons.getConsignmentEntries().add(entry);

		}
		cons.setOrder(order);

		return cons;
	}

	@Override
	public ConsignmentModel getConsignment(final WarehouseModel warehouse, final OrderModel order, final TravellerModel traveller)
	{
		return getTravelConsignmentDao().getConsignment(warehouse, order, traveller);
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the travelConsignmentDao
	 */
	protected TravelConsignmentDao getTravelConsignmentDao()
	{
		return travelConsignmentDao;
	}

	/**
	 * @param travelConsignmentDao
	 *           the travelConsignmentDao to set
	 */
	public void setTravelConsignmentDao(final TravelConsignmentDao travelConsignmentDao)
	{
		this.travelConsignmentDao = travelConsignmentDao;
	}

}
