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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.AbstractOfferResponseFilterStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Strategy that extends the {@link de.hybris.platform.travelfacades.strategies.AbstractOfferResponseFilterStrategy}.
 * The strategy is used to filter out the originDestinationOfferInfos if all the consignments have status included in
 * the notAllowedStatusList for at least one transportOffering.
 */
public class TravellerStatusStrategy extends AbstractOfferResponseFilterStrategy
{

	private TravelCartFacade travelCartFacade;
	private BaseStoreService baseStoreService;
	private CustomerAccountService customerAccountService;
	private List<ConsignmentStatus> notAllowedStatusList;

	@Override
	public void filterOfferResponseData(final OfferResponseData offerResponseData)
	{

		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			if (CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				offerGroupData.getOriginDestinationOfferInfos().removeIf(odOfferInfo -> {
					final boolean valid = checkAllConsignmentsStatus(odOfferInfo);
					setOriginDestinationStatus(odOfferInfo.getOriginDestinationRefNumber(), offerResponseData, !valid);
					return valid;
				});
			}
		}

	}

	/**
	 * Method to check if all consignments have status included in the notAllowedStatusList for a specific
	 * originDestinationOfferInfo
	 *
	 * @param originDestinationOfferInfo
	 * 		as the originDestinationOfferInfo to check
	 * @return true if all the consignementStatus are valid, false otherwise
	 */
	protected boolean checkAllConsignmentsStatus(final OriginDestinationOfferInfoData originDestinationOfferInfo)
	{
		final String orderCode = getTravelCartFacade().getOriginalOrderCode();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(orderCode, baseStoreModel);

		final Integer odNumber = originDestinationOfferInfo.getOriginDestinationRefNumber();

		final List<ConsignmentModel> consignmentsForOriginDestination = orderModel.getConsignments().stream()
				.filter(consignment -> consignment.getConsignmentEntries().stream()
				.allMatch(entry -> entry.getOrderEntry().getTravelOrderEntryInfo() != null && entry.getOrderEntry()
				.getTravelOrderEntryInfo().getOriginDestinationRefNumber().equals(odNumber)))
				.collect(Collectors.toList());

		// check if all consignments are valid for the leg

		return consignmentsForOriginDestination.stream()
				.allMatch(consignment -> getNotAllowedStatusList().contains(consignment.getStatus()));
	}

	@Override
	public void filterSeatMapData(final SeatMapResponseData seatMapResponseData)
	{
		seatMapResponseData.getSeatMap()
				.removeIf(seatMap -> !checkAllConsignmentsStatusForTransportOffering(seatMap.getTransportOffering()));
	}

	/**
	 * Method to check if all consignments have status included in the notAllowedStatusList for a specific
	 * transportOffering
	 *
	 * @param transportOffering
	 * 		as the transportOffering to check
	 * @return true if all the consignementStatus are valid, false otherwise
	 */
	protected boolean checkAllConsignmentsStatusForTransportOffering(final TransportOfferingData transportOffering)
	{
		final String orderCode = getTravelCartFacade().getOriginalOrderCode();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(orderCode, baseStoreModel);

		final List<ConsignmentModel> consignmentsForTransportOffering = orderModel.getConsignments().stream()
				.filter(consignment -> consignment.getConsignmentEntries().stream()
						.allMatch(entry -> entry.getOrderEntry().getTravelOrderEntryInfo() != null && entry.getOrderEntry()
								.getTravelOrderEntryInfo().getTransportOfferings().stream()
								.map(to -> to.getCode()).anyMatch(code -> code.equals(transportOffering.getCode()))))
				.collect(Collectors.toList());

		final boolean notValid = consignmentsForTransportOffering.stream()
				.allMatch(consignment -> getNotAllowedStatusList().contains(consignment.getStatus()));

		return !notValid;
	}

	/**
	 * Gets travel cart facade.
	 *
	 * @return the travelCartFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * Sets travel cart facade.
	 *
	 * @param travelCartFacade
	 * 		the travelCartFacade to set
	 */
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}

	/**
	 * Gets base store service.
	 *
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * Sets base store service.
	 *
	 * @param baseStoreService
	 * 		the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * Gets customer account service.
	 *
	 * @return the customerAccountService
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * Sets customer account service.
	 *
	 * @param customerAccountService
	 * 		the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * Gets not allowed status list.
	 *
	 * @return the notAllowedStatusList
	 */
	protected List<ConsignmentStatus> getNotAllowedStatusList()
	{
		return notAllowedStatusList;
	}

	/**
	 * Sets not allowed status list.
	 *
	 * @param notAllowedStatusList
	 * 		the notAllowedStatusList to set
	 */
	public void setNotAllowedStatusList(final List<ConsignmentStatus> notAllowedStatusList)
	{
		this.notAllowedStatusList = notAllowedStatusList;
	}

}
