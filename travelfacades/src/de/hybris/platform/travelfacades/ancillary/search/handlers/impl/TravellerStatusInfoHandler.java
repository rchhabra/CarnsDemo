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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.enums.TravellerStatus;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Handler to populate travellerStatusInfo for each traveller in the ItineraryData of the OfferResponseData
 */
public class TravellerStatusInfoHandler implements AncillarySearchHandler
{

	private static final Logger LOG = Logger.getLogger(TravellerStatusInfoHandler.class);

	private TravelCartFacade travelCartFacade;
	private BaseStoreService baseStoreService;
	private CustomerAccountService customerAccountService;

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		// Populate only if it's an amendment
		if (!getTravelCartFacade().isAmendmentCart())
		{
			return;
		}

		final String orderCode = getTravelCartFacade().getOriginalOrderCode();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(orderCode, baseStoreModel);

		for (final ItineraryData itinerary : offerResponseData.getItineraries())
		{
			for (final TravellerData traveller : itinerary.getTravellers())
			{
				final String travellerUid = traveller.getUid();

				final List<String> transportOfferingCodes = itinerary.getOriginDestinationOptions().stream()
						.flatMap(odOption -> odOption.getTransportOfferings().stream().map(TransportOfferingData::getCode).distinct())
						.collect(Collectors.toList());

				traveller.setTravellerStatusInfo(getTravellerStatusMap(travellerUid, transportOfferingCodes, orderModel));
			}
		}
	}

	/**
	 * The method creates and returns the travellerStatusMap for a specific traveller, where the key is the
	 * transportOfferingCode and the value is the TravellerStatus for that transportOffering.
	 *
	 * @param travellerUid
	 *           as the Uid of the traveller to use to create the travellerStatusMap
	 * @param transportOfferingCodes
	 *           as the list of transportOffering codes to be used to create the travellerStatusMap
	 * @param orderModel
	 *           as the orderModel with the consignments that have to be checked
	 * @return a Map<String, TravellerStatus>, where the key is the transportOfferingCode and the value is the
	 *         TravellerStatus for that transportOffering.
	 */
	protected Map<String, TravellerStatus> getTravellerStatusMap(final String travellerUid,
			final List<String> transportOfferingCodes, final OrderModel orderModel)
	{
		final Map<String, TravellerStatus> travellerStatusMap = new HashMap<>();

		for (final String transportOfferingCode : transportOfferingCodes)
		{
			final ConsignmentModel consignment = getConsignment(travellerUid, transportOfferingCode, orderModel);
			travellerStatusMap.put(transportOfferingCode, getTravellerStatus(consignment));
		}

		return travellerStatusMap;
	}

	/**
	 * The method returns the consignment corresponding to the traveller and the transportOffering
	 *
	 * @param travellerUid
	 *           as the Uid of the traveller
	 * @param transportOfferingCode
	 *           as the code of the transportOffering
	 * @param orderModel
	 *           as the orderModel
	 * @return the consignmentModel of the orderModel, corresponding to the specific traveller and the transportOffering
	 */
	protected ConsignmentModel getConsignment(final String travellerUid, final String transportOfferingCode,
			final OrderModel orderModel)
	{
		if (StringUtils.isEmpty(transportOfferingCode) || StringUtils.isEmpty(travellerUid))
		{
			return null;
		}
		final Optional<ConsignmentModel> consignmentModel = orderModel.getConsignments().stream()
				.filter(consignment -> consignment.getWarehouse().getCode().equals(transportOfferingCode)
						&& consignment.getTraveller().getUid().equals(travellerUid))
				.findFirst();
		return consignmentModel.orElse(null);
	}

	/**
	 * The method evaluate and returns the TravellerStatus based on a specific consignment.
	 *
	 * @param consignment
	 *           as the consignmentModel used to get the TravellerStatus
	 * @return the TravellerStatus that corresponds to the status of the consignment. If the consignment status doesn't
	 *         correspond to any TravellerStatus, the default return value is TravellerStatus.READY.
	 */
	protected TravellerStatus getTravellerStatus(final ConsignmentModel consignment)
	{
		TravellerStatus status;
		try
		{
			status = TravellerStatus.valueOf(consignment.getStatus().getCode());
		}
		catch (final IllegalArgumentException e)
		{
			LOG.error("TravellerStatus not found for value " + consignment.getStatus().getCode(), e);
			status = TravellerStatus.READY;
		}
		return status;
	}

	/**
	 * @return the travelCartFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * @param travelCartFacade
	 *           the travelCartFacade to set
	 */
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the customerAccountService
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

}
