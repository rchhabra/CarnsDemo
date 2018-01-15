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
package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.Location;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderOfferItemType;
import de.hybris.platform.ndcfacades.ndc.Passenger;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * The concrete class to validates the Offer for {@link OrderCreateRQ}
 */
public class NDCOrderCreateOfferValidator extends NDCAbstractOriginDestinationValidator<OrderCreateRQ>
		implements NDCRequestValidator<OrderCreateRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderCreateOfferValidator.class);

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		final List<Offer> offers = orderCreateRQ.getQuery().getOrderItems().getShoppingResponse().getOffers().getOffer();

		if (!validateOfferNumber(offers, errorsType))
		{
			return;
		}
		try
		{
			if (!validateSameFlight(offers, errorsType))
			{
				return;
			}

			if (!validateOfferIdPerPTC(offers, errorsType))
			{
				return;
			}

			if (!validateSeatOfferIdPerPTC(orderCreateRQ.getQuery().getOrderItems().getOfferItem(), errorsType))
			{
				return;
			}

			if (!validateOrderCreateSameAirports(offers, errorsType))
			{
				return;
			}

			if (!validateOfferItemForSeatRef(offers, orderCreateRQ.getQuery().getOrderItems().getOfferItem(), errorsType))
			{
				return;
			}

			validateSeatItemReferences(orderCreateRQ.getQuery().getOrderItems().getOfferItem(), errorsType);
		}
		catch (final NDCOrderException e)
		{
			LOG.debug(e);
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
		}
	}

	/**
	 * This method validates the number of references per seat item. Max 1 passenger per seat is allowed
	 *
	 * @param orderOfferItemTypeList
	 * 		the order offer item type list
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateSeatItemReferences(final List<OrderOfferItemType> orderOfferItemTypeList,
			final ErrorsType errorsType)
	{
		final Set<String> seatIds = new HashSet<>();

		for (final OrderOfferItemType orderOfferItemType : orderOfferItemTypeList)
		{
			if (Objects.nonNull(orderOfferItemType.getOfferItemType())
					&& CollectionUtils.isNotEmpty(orderOfferItemType.getOfferItemType().getSeatItem()))
			{
				for (final SeatItem seatItem : orderOfferItemType.getOfferItemType().getSeatItem())
				{
					if (seatItem.getRefs().size() > NdcwebservicesConstants.MAX_PASSENGER_REF_PER_SEAT)
					{
						addError(errorsType, getConfigurationService().getConfiguration()
								.getString(NdcwebservicesConstants.MAX_PASSENGER_REF_PER_SEAT_EXCEEDED));
						return false;
					}
					final String seatKey = ((ListOfSeatType) seatItem.getSeatReference().get(0).getValue()).getListKey();
					if (seatIds.contains(seatKey))
					{
						addError(errorsType,
								getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_SEAT_ASSOCIATION));
						return false;
					}
					else
					{
						seatIds.add(seatKey);
					}
				}
			}
		}
		return true;
	}

	/**
	 * This method validates whether offerItem for seats belongs to the same offer
	 *
	 * @param offers
	 * 		the offers
	 * @param offerItems
	 * 		the offer items
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateOfferItemForSeatRef(final List<Offer> offers, final List<OrderOfferItemType> offerItems,
			final ErrorsType errorsType) throws NDCOrderException
	{
		final Set<String> offerItemIDs = offers.stream().flatMap(offer -> offer.getOfferItems().getOfferItem().stream())
				.map(offerItem -> offerItem.getOfferItemID().getValue()).collect(Collectors.toSet());

		for (final OrderOfferItemType orderOfferItemType : offerItems)
		{
			if (Objects.isNull(orderOfferItemType.getOfferItemType()))
			{
				continue;
			}

			final String offerItemID = orderOfferItemType.getOfferItemID().getValue();

			if (!offerItemIDs.contains(orderOfferItemType.getOfferItemID().getValue()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFERS_COMBINATIONS));
				return false;
			}

			final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(offerItemID);

			final List<String> transportOfferingCodes = ndcOfferItemId.getBundleList().stream()
					.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toList());

			if (!validateSeatItem(orderOfferItemType.getOfferItemType().getSeatItem(), errorsType, transportOfferingCodes))
			{
				return false;
			}

		}
		return true;
	}

	/**
	 * This method validates {@link SeatItem} against transportOfferingCode, checks whether each {@link SeatItem}
	 * associated to correct transportOfferingCode.
	 *
	 * @param seatItems
	 * 		the seat items
	 * @param errorsType
	 * 		the errors type
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 *
	 * @return the boolean
	 */
	protected boolean validateSeatItem(final List<SeatItem> seatItems, final ErrorsType errorsType,
			final List<String> transportOfferingCodes)
	{
		for (final SeatItem seatItem : seatItems)
		{
			if (Objects.isNull(seatItem.getRefs()) || CollectionUtils.isEmpty(seatItem.getRefs()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_REF_ATTR));
				return false;
			}

			if (Objects.isNull(seatItem.getSeatReference()) || CollectionUtils.isEmpty(seatItem.getSeatReference()))
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_REF_ELEMENT));
				return false;
			}

			final ListOfSeatType seat = ((ListOfSeatType) seatItem.getSeatReference().get(0).getValue());
			if (!validateSeatLocation(seat, errorsType))
			{
				return false;
			}
			final String transportOfferingCode = getTransportOfferingCode(seat);
			if (!transportOfferingCodes.contains(transportOfferingCode))
			{
				addError(errorsType,
						new StringBuilder(
								getConfigurationService().getConfiguration().getString(NdcfacadesConstants.SEAT_LOCATION_KEY_MISMATCH))
								.append(seat.getListKey()).toString());
				return false;
			}
		}
		return true;
	}

	/**
	 * This method validates {@link Location} element of each and every {@link ListOfSeatType}.
	 *
	 * @param seat
	 * 		the seat
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return the boolean
	 */
	protected boolean validateSeatLocation(final ListOfSeatType seat, final ErrorsType errorsType)
	{
		if (Objects.isNull(seat.getLocation().getColumn()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_COLUMN));
			return false;
		}

		if (Objects.isNull(seat.getLocation().getRow()))
		{
			addError(errorsType, getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_ROW));
			return false;
		}

		if (Objects.isNull(seat.getLocation().getRow().getNumber())
				|| StringUtils.isEmpty(seat.getLocation().getRow().getNumber().getValue()))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_SEAT_ROW_NUMBER));
			return false;
		}
		return true;
	}

	/**
	 * This method returns transport offering code associated to given {@link ListOfSeatType}.
	 *
	 * @param seat
	 * 		the seat
	 *
	 * @return the transport offering code
	 */
	protected String getTransportOfferingCode(final ListOfSeatType seat)
	{
		final String seatNum = new StringBuilder(seat.getLocation().getColumn())
				.append(seat.getLocation().getRow().getNumber().getValue()).toString();
		if (seat.getListKey().indexOf(seatNum) == 0)
		{
			return seat.getListKey().substring(seatNum.length());
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Check if the number of the offer is 1 or 2
	 *
	 * @param offers
	 * 		the offers
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 */
	protected boolean validateOfferNumber(final List<Offer> offers, final ErrorsType errorsType)
	{
		if (offers.size() > NdcservicesConstants.RETURN_FLIGHT)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_OFFERS_EXCEEDED));
			return false;
		}

		for (final Offer offer : offers)
		{
			final String offerID = offer.getOfferID().getValue();
			final boolean offerIdOfferItemIdSame = offer.getOfferItems().getOfferItem().stream()
					.anyMatch(offerItem -> StringUtils.equals(offerID, offerItem.getOfferItemID().getValue()));
			if (offerIdOfferItemIdSame)
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
				return false;
			}
		}

		return true;
	}

	/**
	 * Check if the offer items selected belongs to the same offer
	 *
	 * @param offers
	 * 		the offers
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateOrderCreateSameAirports(final List<Offer> offers, final ErrorsType errorsType)
			throws NDCOrderException
	{
		if (offers.size() == NdcservicesConstants.ONE_WAY_FLIGHT)
		{
			return true;
		}

		final List<String> offerIds = new LinkedList<>();
		offerIds.add(offers.get(NdcservicesConstants.OUTBOUND_FLIGHT_REF_NUMBER).getOfferID().getValue());
		offerIds.add(offers.get(NdcservicesConstants.INBOUND_FLIGHT_REF_NUMBER).getOfferID().getValue());

		return validateSameAirports(offerIds, errorsType);
	}

	/**
	 * Check if all the offers contained in an offer belong to the same flight
	 *
	 * @param offers
	 * 		the offers
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateSameFlight(final List<Offer> offers, final ErrorsType errorsType) throws NDCOrderException
	{
		for (final Offer offer : offers)
		{
			final String offerID = offer.getOfferID().getValue();
			for (int i = 0; i < offer.getOfferItems().getOfferItem().size(); i++)
			{
				if (!getNdcOfferItemIdResolver().isSameOffer(offerID,
						offer.getOfferItems().getOfferItem().get(i).getOfferItemID().getValue()))
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFERS_COMBINATIONS));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check if the selected offerItem can be applied to the referenced passenger
	 *
	 * @param offers
	 * 		the offers
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateOfferIdPerPTC(final List<Offer> offers, final ErrorsType errorsType) throws NDCOrderException
	{
		for (final Offer offer : offers)
		{
			for (final OfferItem offerItem : offer.getOfferItems().getOfferItem())
			{
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(offerItem.getOfferItemID().getValue());

				if (Objects.isNull(offerItem.getPassengers()))
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_REFERENCE));
					return false;
				}

				for (final Object passengerObj : offerItem.getPassengers().getPassengerReference())
				{
					final Passenger passenger = (Passenger) passengerObj;
					if (ndcOfferItemId.getPtc().compareTo(passenger.getPTC().getValue()) != 0)
					{
						addError(errorsType, getConfigurationService().getConfiguration()
								.getString(NdcwebservicesConstants.INVALID_PTC_OFFER_COMBINATION));
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Check if the selected offerItem can be applied to the referenced passenger
	 *
	 * @param offers
	 * 		the offers
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateSeatOfferIdPerPTC(final List<OrderOfferItemType> offers, final ErrorsType errorsType)
			throws NDCOrderException
	{
		for (final OrderOfferItemType offer : offers)
		{
			final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
					.getNDCOfferItemIdFromString(offer.getOfferItemID().getValue());

			if (Objects.isNull(offer.getOfferItemType().getSeatItem()))
			{
				continue;
			}

			for (final SeatItem seat : offer.getOfferItemType().getSeatItem())
			{
				if (seat.getRefs().isEmpty())
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_REFERENCE));
					return false;
				}

				for (final Object passengerObj : seat.getRefs())
				{
					final Passenger passenger = (Passenger) passengerObj;
					if (ndcOfferItemId.getPtc().compareTo(passenger.getPTC().getValue()) != 0)
					{
						addError(errorsType, getConfigurationService().getConfiguration()
								.getString(NdcwebservicesConstants.INVALID_PTC_OFFER_COMBINATION));
						return false;
					}
				}
			}

		}
		return true;
	}
}
