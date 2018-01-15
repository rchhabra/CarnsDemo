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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.strategies.AmendOrderOfferFilterStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy that extends the {@link AmendOrderOfferFilterStrategy}.
 * The strategy is used to filter out the OrderItem if all the consignments have status included in
 * the notAllowedStatusList for at least one transportOffering.
 */
public class NDCTravellerStatusStrategy implements AmendOrderOfferFilterStrategy
{
	private List<ConsignmentStatus> notAllowedStatusList;

	@Override
	public boolean filterOffer(final OrderModel orderModel, final List<TransportOfferingModel> transportOfferings,
			final List<String> travellerUIDList)
	{
		for (final String travellerUID : travellerUIDList)
		{
			for (final TransportOfferingModel transportOfferingModel : transportOfferings)
			{
				if (!checkConsignmentsStatusPerTravellerForTransportOffering(orderModel, transportOfferingModel, travellerUID))
				{
					return false;
				}
			}
		}
		return true;
	}

	protected boolean checkConsignmentsStatusPerTravellerForTransportOffering(final OrderModel orderModel,
			final TransportOfferingModel transportOfferingModel, final String travellerUID)
	{
		final List<ConsignmentModel> consignmentsForTransportOffering = orderModel.getConsignments().stream().filter(
				consignment -> consignment.getConsignmentEntries().stream().allMatch(
						entry -> entry.getOrderEntry().getTravelOrderEntryInfo() != null && sameTraveller(entry, travellerUID) && entry
								.getOrderEntry().getTravelOrderEntryInfo().getTransportOfferings().stream()
								.map(WarehouseModel::getCode).anyMatch(code -> code.equals(transportOfferingModel.getCode()))))
				.collect(Collectors.toList());

		final boolean notValid = consignmentsForTransportOffering.stream()
				.anyMatch(consignment -> getNotAllowedStatusList().contains(consignment.getStatus()));

		return !notValid;
	}

	/**
	 * Checks if the traveller associated to the consignment corresponds to the traveller UID provided. Since one consigment is created
	 * per leg per pax, all match should return true.
	 *
	 * @param consignmentEntryModel
	 * @param travellerUID
	 * @return
	 */
	protected boolean sameTraveller(final ConsignmentEntryModel consignmentEntryModel, final String travellerUID)
	{
		return consignmentEntryModel.getOrderEntry().getTravelOrderEntryInfo().getTravellers().stream()
				.allMatch(traveller -> StringUtils.equalsIgnoreCase(traveller.getUid(), travellerUID));
	}

	protected List<ConsignmentStatus> getNotAllowedStatusList()
	{
		return notAllowedStatusList;
	}

	@Required
	public void setNotAllowedStatusList(final List<ConsignmentStatus> notAllowedStatusList)
	{
		this.notAllowedStatusList = notAllowedStatusList;
	}
}
