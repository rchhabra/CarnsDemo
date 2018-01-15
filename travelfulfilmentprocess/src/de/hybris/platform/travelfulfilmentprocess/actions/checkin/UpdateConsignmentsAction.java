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

package de.hybris.platform.travelfulfilmentprocess.actions.checkin;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.ordersplitting.impl.DefaultTravelConsignmentService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Performs the update of the consignments status
 */
public class UpdateConsignmentsAction extends AbstractSimpleDecisionAction<CheckInProcessModel>
{

	private TravellerService travellerService;
	private DefaultTravelConsignmentService travelConsignmentService;

	@Override
	public Transition executeAction(final CheckInProcessModel process) throws RetryLaterException, Exception
	{

		final OrderModel orderModel = process.getOrder();

		if (orderModel == null)
		{
			return Transition.NOK;
		}

		final List<ConsignmentModel> consignments = new ArrayList<>();


		final List<TransportOfferingModel> transportOfferings = orderModel.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo() != null)
				.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() != null && entry
						.getTravelOrderEntryInfo().getOriginDestinationRefNumber().equals(process.getOriginDestinationRefNumber()))
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTransportOfferings().stream()).distinct()
				.collect(Collectors.toList());

		for (final String travellerUid : process.getTravellers())
		{

			final TravellerModel travellerModel = getTravellerService().getExistingTraveller(travellerUid);
			if (travellerModel == null)
			{
				return Transition.NOK;
			}

			for (final TransportOfferingModel transportOfferingModel : transportOfferings)
			{
				final ConsignmentModel consignment = getTravelConsignmentService().getConsignment(transportOfferingModel, orderModel,
						travellerModel);
				if (consignment == null)
				{
					return Transition.NOK;
				}

				if (checkIn(consignment))
				{
					consignment.setTravellerInfo(getModelService().clone(travellerModel.getInfo()));
					consignments.add(consignment);
				}
				else
				{
					return Transition.NOK;
				}

			}
		}

		getModelService().saveAll(consignments);

		return Transition.OK;

	}

	/**
	 * Check in boolean.
	 *
	 * @param consignment
	 *           the consignment
	 * @return the boolean
	 */
	protected boolean checkIn(final ConsignmentModel consignment)
	{
		if (!consignment.getStatus().equals(ConsignmentStatus.READY))
		{
			return false;
		}

		consignment.setStatus(ConsignmentStatus.CHECKED_IN);
		return true;
	}

	/**
	 * Gets traveller service.
	 *
	 * @return the travellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * Sets traveller service.
	 *
	 * @param travellerService
	 *           the travellerService to set
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * Gets travel consignment service.
	 *
	 * @return the travelConsignmentService
	 */
	protected DefaultTravelConsignmentService getTravelConsignmentService()
	{
		return travelConsignmentService;
	}

	/**
	 * Sets travel consignment service.
	 *
	 * @param travelConsignmentService
	 *           the travelConsignmentService to set
	 */
	public void setTravelConsignmentService(final DefaultTravelConsignmentService travelConsignmentService)
	{
		this.travelConsignmentService = travelConsignmentService;
	}

}
