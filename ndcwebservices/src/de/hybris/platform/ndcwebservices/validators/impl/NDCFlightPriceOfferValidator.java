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
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ.Query.Offers.Offer;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.Travelers.Traveler;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.NDCRequestValidator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;


/**
 * Concrete class to validate offers for {@link FlightPriceRQ}
 */
public class NDCFlightPriceOfferValidator extends NDCAbstractOriginDestinationValidator<FlightPriceRQ>
		implements NDCRequestValidator<FlightPriceRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCFlightPriceOfferValidator.class);

	@Override
	public void validate(final FlightPriceRQ flightPriceRQ, final ErrorsType errorsType)
	{
		final List<Offer> offers = flightPriceRQ.getQuery().getOffers().getOffer();

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

			if (!validateOfferIdPerPTC(flightPriceRQ, errorsType))
			{
				return;
			}

			validateFlightPriceSameAirports(offers, errorsType);
		}
		catch (final NDCOrderException e)
		{
			LOG.debug(e);
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
		}
	}


	/**
	 * Checks if the number of the offer is 1 or 2
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

		return true;
	}

	/**
	 * Checks, in case of a return flight, if the departure of the outbound is in the same city of the arrival inbound
	 * and if the arrival of the outbound is in the same city of the departure of the inbound
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
	protected boolean validateFlightPriceSameAirports(final List<Offer> offers, final ErrorsType errorsType)
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
	 * Checks if the offer items selected belongs to the same offer
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
			for (int i = 0; i < offer.getOfferItemIDs().getOfferItemID().size(); i++)
			{
				if (!getNdcOfferItemIdResolver().isSameOffer(offerID, offer.getOfferItemIDs().getOfferItemID().get(i).getValue()))
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
	 * Checks if the selected offerItem can be applied to the referenced passenger
	 *
	 * @param flightPriceRQ
	 * 		the flight price rq
	 * @param errorsType
	 * 		the errors type
	 *
	 * @return boolean
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected boolean validateOfferIdPerPTC(final FlightPriceRQ flightPriceRQ, final ErrorsType errorsType)
			throws NDCOrderException
	{
		final Map<String, Integer> offersPerPTC = getOffersPerPTC(flightPriceRQ);

		for (final Offer offer : flightPriceRQ.getQuery().getOffers().getOffer())
		{
			for (final ItemIDType offerItem : offer.getOfferItemIDs().getOfferItemID())
			{
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(offerItem.getValue());

				if (!offersPerPTC.containsKey(ndcOfferItemId.getPtc()))
				{
					addError(errorsType,
							getConfigurationService().getConfiguration()
									.getString(NdcfacadesConstants.MISSING_TRAVELER_OFFER_ITEM_ID_ASSOCIATION));
					return false;
				}
				else
				{
					offersPerPTC.put(ndcOfferItemId.getPtc(), offersPerPTC.get(ndcOfferItemId.getPtc()) + 1);
				}
			}

			if (!areValidOffers(offersPerPTC))
			{
				addError(errorsType, getConfigurationService().getConfiguration()
						.getString(NdcwebservicesConstants.MISMATCH_BETWEEN_OFFERS_TRAVELER));
				return false;
			}
			resetOfferIdPerPTC(offersPerPTC);
		}
		return true;
	}

	/**
	 * Resets the number of offers per ptc to 0
	 *
	 * @param offersPerPTC
	 * 		the offers per ptc
	 */
	protected void resetOfferIdPerPTC(final Map<String, Integer> offersPerPTC)
	{
		offersPerPTC.entrySet().forEach(entry -> entry.setValue(0));
	}

	/**
	 * Checks if every ptc has an offer associated for the current leg
	 *
	 * @param offersPerPTC
	 * 		the offers per ptc
	 *
	 * @return boolean
	 */
	protected boolean areValidOffers(final Map<String, Integer> offersPerPTC)
	{
		return offersPerPTC.entrySet().stream().allMatch(entry -> entry.getValue() == 1);
	}

	/**
	 * Creates a map using a key the PTC provided in the Anonymous Travelers and 0 as a value. This map will be used to
	 * check if there is one offer per leg per PTC in the FlightPriceRQ
	 *
	 * @param flightPriceRQ
	 * 		the flight price rq
	 *
	 * @return offers per ptc
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected Map<String, Integer> getOffersPerPTC(final FlightPriceRQ flightPriceRQ) throws NDCOrderException
	{

		final Map<String, Integer> offersPerPTC = new HashMap<>();

		final List<Traveler> travelers = flightPriceRQ.getTravelers().getTraveler().stream()
				.filter(traveler -> BigInteger.ZERO.compareTo(traveler.getAnonymousTraveler().get(0).getPTC().getQuantity()) < 0)
				.collect(Collectors.toList());

		for (final Traveler traveler : travelers)
		{
			if (offersPerPTC.containsKey(traveler.getAnonymousTraveler().get(0).getPTC().getValue()))
			{
				throw new NDCOrderException("Duplicate PTC value in the Anonymous Traveler elements");
			}

			offersPerPTC.put(traveler.getAnonymousTraveler().get(0).getPTC().getValue(), 0);
		}

		return offersPerPTC;
	}
}
