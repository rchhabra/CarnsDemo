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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is responsible for instantiating a list of Offer Pricing Infos for given leg which will contain summary
 * of ancillaries selected by user. It populates the OfferPricingInfo of the OriginDestinationOfferInfoData.
 */
public class ReservationOriginDestinationOfferPricingInfoHandler extends AbstractReservationOfferPricingInfoHandler
		implements ReservationHandler
{

	private Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		if (CollectionUtils.isEmpty(reservationData.getReservationItems()))
		{
			return;
		}
		reservationData.getReservationItems().forEach(reservationItem ->
		{
			final ReservationPricingInfoData reservationPricingInfo = reservationItem.getReservationPricingInfo();
			if (reservationPricingInfo != null)
			{
				final List<AbstractOrderEntryModel> ancillaryEntries = abstractOrderModel.getEntries().stream()
						.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())).filter(
								entry -> isAncillaryEntry(reservationItem, entry) && (
										isPerLegEntry(entry, reservationItem.getOriginDestinationRefNumber()) || isPerLegPerPaxEntry(entry,
												reservationItem.getOriginDestinationRefNumber()))).collect(Collectors.toList());
				reservationPricingInfo.setOriginDestinationOfferInfos(createOriginDestinationOfferInfos(ancillaryEntries));

				if (CollectionUtils.isNotEmpty(reservationPricingInfo.getOriginDestinationOfferInfos()))
				{
					reservationPricingInfo.getOriginDestinationOfferInfos().forEach(odInfo -> odInfo.setOfferPricingInfos(
							createOfferPricingInfos(ancillaryEntries, reservationItem, odInfo.getTransportOfferings())));
				}
			}
		});
	}

	/**
	 * Identifies different option in which ancillaries are assigned to order - whether it is per leg or per transport
	 * offering
	 *
	 * @param ancillaryEntries
	 * 		- set of ancillaries in Abstract Order for current leg
	 * @return list of options in which ancillaries are selected for current Abstract Order
	 */
	protected List<OriginDestinationOfferInfoData> createOriginDestinationOfferInfos(
			final List<AbstractOrderEntryModel> ancillaryEntries)
	{
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<OriginDestinationOfferInfoData>();
		ancillaryEntries.forEach(entry ->
		{
			final List<TransportOfferingModel> transportOfferings = new ArrayList<>();
			transportOfferings.addAll(entry.getTravelOrderEntryInfo().getTransportOfferings());
			if (isNewOfferInfo(originDestinationOfferInfos, transportOfferings))
			{
				final OriginDestinationOfferInfoData offerInfo = createOriginDestinationOfferInfo(transportOfferings);
				originDestinationOfferInfos.add(offerInfo);
			}
		});
		return originDestinationOfferInfos;
	}

	/**
	 * Checks whether there has been already created an originDestinationOfferInfo for given transport offering
	 * combination. The assumption is taken that if there is only 1 Transport Offering for entry ancillary is applied on
	 * sector/offering level, if there are more, it is applied on leg level
	 *
	 * @param originDestinationOfferInfos
	 * 		- current list of offer infos
	 * @param transportOfferings
	 * 		- combination of transport offerings
	 * @return true in case it is a new combination
	 */
	protected boolean isNewOfferInfo(final List<OriginDestinationOfferInfoData> originDestinationOfferInfos,
			final List<TransportOfferingModel> transportOfferings)
	{
		if (CollectionUtils.isNotEmpty(originDestinationOfferInfos))
		{
			for (final OriginDestinationOfferInfoData odInfo : originDestinationOfferInfos)
			{
				if (isSameTOCombination(odInfo.getTransportOfferings(), transportOfferings))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Compares to transportOffering lists to see whether they have same transport offering combinations
	 *
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param transportOfferingModels
	 * 		the transport offering models
	 * @return true if the lists have the same combination
	 */
	protected boolean isSameTOCombination(final List<TransportOfferingData> transportOfferings,
			final List<TransportOfferingModel> transportOfferingModels)
	{
		if (transportOfferingModels.size() != transportOfferings.size())
		{
			return false;
		}

		if (transportOfferings.size() == 1)
		{
			if (transportOfferings.get(0).getCode().equals(transportOfferingModels.get(0).getCode()))
			{
				return true;
			}
		}
		else
		{
			final List<String> dataCodes = new ArrayList<String>(transportOfferings.size());
			transportOfferings.forEach(to -> dataCodes.add(to.getCode()));

			final List<String> modelCodes = new ArrayList<String>(transportOfferingModels.size());
			transportOfferingModels.forEach(to -> modelCodes.add(to.getCode()));

			if (dataCodes.containsAll(modelCodes))
			{
				return true;
			}
		}
		return false;

	}

	/**
	 * Creates a new instance of Origin Destination Offer Info
	 *
	 * @param transportOfferings
	 * 		- offerings attached to given entry
	 * @return the origin destination offer info data
	 */
	protected OriginDestinationOfferInfoData createOriginDestinationOfferInfo(
			final List<TransportOfferingModel> transportOfferings)
	{
		final OriginDestinationOfferInfoData offerInfo = new OriginDestinationOfferInfoData();
		offerInfo.setTransportOfferings(Converters.convertAll(transportOfferings, getTransportOfferingConverter()));
		return offerInfo;
	}

	/**
	 * Creates a list of offer pricing infos for given reservation item.
	 *
	 * @param ancillaryEntries
	 * 		- ancillary entries for given Abstract Order and leg
	 * @param reservationItem
	 * 		- Reservation Item of current leg
	 * @param transportOfferings
	 * 		- combination of transportOfferings for given origin destination offer info
	 * @return list of offer pricing info for leg
	 */
	protected List<OfferPricingInfoData> createOfferPricingInfos(final List<AbstractOrderEntryModel> ancillaryEntries,
			final ReservationItemData reservationItem, final List<TransportOfferingData> transportOfferings)
	{
		final List<OfferPricingInfoData> offerPricingInfos = new ArrayList<OfferPricingInfoData>();

		final List<AbstractOrderEntryModel> filteredEntries = filterAncillaryEntriesForTransportOfferings(ancillaryEntries,
				transportOfferings);

		for (final AbstractOrderEntryModel entry : filteredEntries)
		{
			if (isPerLegPerPaxEntry(entry, reservationItem.getOriginDestinationRefNumber()))
			{
				createOfferPricingInfosForTravellerBreakdown(offerPricingInfos, entry, reservationItem);
			}
			if (isPerLegEntry(entry, reservationItem.getOriginDestinationRefNumber()))
			{
				createOfferPricingInfosForBookingBreakdown(offerPricingInfos, entry, reservationItem);
			}
		}
		return offerPricingInfos;
	}

	/**
	 * Filters out the ancillary entries to have only ones with matching transport offering combination
	 *
	 * @param ancillaryEntries
	 * 		- given ancillary entries for current leg of Abstract Order
	 * @param transportOfferings
	 * 		- combination of transport offerings to be matched
	 * @return list
	 */
	protected List<AbstractOrderEntryModel> filterAncillaryEntriesForTransportOfferings(
			final List<AbstractOrderEntryModel> ancillaryEntries, final List<TransportOfferingData> transportOfferings)
	{
		final List<AbstractOrderEntryModel> filteredEntries = ancillaryEntries.stream()
				.filter(entry -> entry.getTravelOrderEntryInfo().getTransportOfferings().size() == transportOfferings.size())
				.collect(Collectors.toList());
		if (transportOfferings.size() > 1)
		{
			return filteredEntries;
		}

		return filteredEntries.stream().filter(
				entry -> entry.getTravelOrderEntryInfo().getTransportOfferings().iterator().next().getCode()
						.equals(transportOfferings.get(0).getCode())).collect(Collectors.toList());
	}

	/**
	 * Creates a list of offerPricingInfos for the travellerBreakdown for the given entry and reservationItem
	 *
	 * @param offerPricingInfos
	 * 		the list of offerPricingInfo to be populated
	 * @param entry
	 * 		the abstractOrderEntry
	 * @param reservationItem
	 * 		the reservationItem
	 */
	protected void createOfferPricingInfosForTravellerBreakdown(final List<OfferPricingInfoData> offerPricingInfos,
			final AbstractOrderEntryModel entry, final ReservationItemData reservationItem)
	{
		final OfferPricingInfoData opi = getOfferPricingInfoFromList(offerPricingInfos, entry);
		if (opi != null)
		{
			final TravellerBreakdownData trBreakdown = getTravellerBreakdownForTraveller(opi, entry);
			if (trBreakdown != null)
			{
				final int updatedQuantity = trBreakdown.getQuantity() + entry.getQuantity().intValue();
				final BigDecimal updatedBasePriceValue = trBreakdown.getPassengerFare().getBaseFare().getValue()
						.add(BigDecimal.valueOf(entry.getBasePrice()));
				final BigDecimal updatedTotalPriceValue = trBreakdown.getPassengerFare().getTotalFare().getValue()
						.add(BigDecimal.valueOf(entry.getTotalPrice()));
				updateTravellerBreakdown(trBreakdown, updatedQuantity, updatedBasePriceValue, updatedTotalPriceValue,
						entry.getOrder().getCurrency().getIsocode());
			}
			else
			{
				final TravellerBreakdownData travellerBreakdown = createNewTravellerBreakdown(reservationItem, entry);
				final List<TravellerBreakdownData> updatedTravellerBreakdowns = new ArrayList<TravellerBreakdownData>();
				if (CollectionUtils.isNotEmpty(opi.getTravellerBreakdowns()))
				{
					updatedTravellerBreakdowns.addAll(opi.getTravellerBreakdowns());
				}
				updatedTravellerBreakdowns.add(travellerBreakdown);
				opi.setTravellerBreakdowns(updatedTravellerBreakdowns);
			}
		}
		else
		{
			final OfferPricingInfoData offerPricingInfo = createNewOfferPricingInfoForTravellerBreakdown(reservationItem, entry);
			offerPricingInfos.add(offerPricingInfo);
		}
	}

	/**
	 * Creates a list of offerPricingInfos for the bookingBreakdown for the given entry and reservationItem
	 *
	 * @param offerPricingInfos
	 * 		the list of offerPricingInfo to be populated
	 * @param entry
	 * 		the abstractOrderEntry
	 * @param reservationItem
	 * 		the reservationItem
	 */
	protected void createOfferPricingInfosForBookingBreakdown(final List<OfferPricingInfoData> offerPricingInfos,
			final AbstractOrderEntryModel entry, final ReservationItemData reservationItem)
	{
		final OfferPricingInfoData opi = getOfferPricingInfoFromList(offerPricingInfos, entry);
		if (opi != null && opi.getBookingBreakdown() != null)
		{
			final BookingBreakdownData bookingBreakdown = opi.getBookingBreakdown();
			final int updatedQuantity = bookingBreakdown.getQuantity() + entry.getQuantity().intValue();
			final BigDecimal updatedBasePriceValue = bookingBreakdown.getPassengerFare().getBaseFare().getValue()
					.add(BigDecimal.valueOf(entry.getBasePrice()));
			final BigDecimal updatedTotalPriceValue = bookingBreakdown.getPassengerFare().getTotalFare().getValue()
					.add(BigDecimal.valueOf(entry.getTotalPrice()));

			updateBookingBreakdown(bookingBreakdown, updatedQuantity, updatedBasePriceValue, updatedTotalPriceValue,
					entry.getOrder().getCurrency().getIsocode());
		}
		else
		{
			final OfferPricingInfoData offerPricingInfo = createNewOfferPricingInfoForBookingBreakdown(entry);
			offerPricingInfos.add(offerPricingInfo);
		}
	}

	/**
	 * Creates a new instance of OfferPricingInfo for a new ancillary product
	 *
	 * @param reservationItem
	 * 		- current reservation item for the leg
	 * @param entry
	 * 		- new ancillary entry which needs to be translated into offer pricing info
	 * @return new offer pricing info containing information about current entry product
	 */
	protected OfferPricingInfoData createNewOfferPricingInfoForTravellerBreakdown(final ReservationItemData reservationItem,
			final AbstractOrderEntryModel entry)
	{
		final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
		offerPricingInfo.setBundleIndicator(entry.getBundleNo() > 0 ? 1 : 0);
		offerPricingInfo.setProduct(getProductConverter().convert(entry.getProduct()));
		final TravellerBreakdownData travellerBreakdown = createNewTravellerBreakdown(reservationItem, entry);
		final List<TravellerBreakdownData> travellerBreakdowns = new ArrayList<TravellerBreakdownData>();
		travellerBreakdowns.add(travellerBreakdown);
		offerPricingInfo.setTravellerBreakdowns(travellerBreakdowns);
		return offerPricingInfo;
	}

	/**
	 * Creates a new TravellerBreakdown instance
	 *
	 * @param reservationItem
	 * 		- current reservation item for the leg
	 * @param entry
	 * 		- entry which needs to be included in traveller breakdown
	 * @return new traveller breakdown for traveller in the entry
	 */
	protected TravellerBreakdownData createNewTravellerBreakdown(final ReservationItemData reservationItem,
			final AbstractOrderEntryModel entry)
	{
		final TravellerBreakdownData travellerBreakdown = new TravellerBreakdownData();
		travellerBreakdown.setTraveller(getTravellerFromList(reservationItem, entry));
		final PassengerFareData passengerFare = new PassengerFareData();
		travellerBreakdown.setPassengerFare(passengerFare);
		updateTravellerBreakdown(travellerBreakdown, entry.getQuantity().intValue(), BigDecimal.valueOf(entry.getBasePrice()),
				BigDecimal.valueOf(entry.getTotalPrice()), entry.getOrder().getCurrency().getIsocode());
		return travellerBreakdown;

	}

	/**
	 * Finds requested traveller in traveller list of reservation item
	 *
	 * @param reservationItem
	 * 		- current reservation item of leg
	 * @param entry
	 * 		- entry containing requested traveller
	 * @return traveller data matching traveller from the entry
	 */
	protected TravellerData getTravellerFromList(final ReservationItemData reservationItem, final AbstractOrderEntryModel entry)
	{
		final TravellerModel travellerModel = entry.getTravelOrderEntryInfo().getTravellers().iterator().next();
		final List<TravellerData> travellers = reservationItem.getReservationItinerary().getTravellers().stream()
				.filter(tr -> tr.getLabel().equals(travellerModel.getLabel())).collect(Collectors.toList());
		return CollectionUtils.isNotEmpty(travellers) ? travellers.get(0) : null;
	}

	/**
	 * Gets transport offering converter.
	 *
	 * @return the transportOfferingConverter
	 */
	protected Converter<TransportOfferingModel, TransportOfferingData> getTransportOfferingConverter()
	{
		return transportOfferingConverter;
	}

	/**
	 * Sets transport offering converter.
	 *
	 * @param transportOfferingConverter
	 * 		the transportOfferingConverter to set
	 */
	@Required
	public void setTransportOfferingConverter(
			final Converter<TransportOfferingModel, TransportOfferingData> transportOfferingConverter)
	{
		this.transportOfferingConverter = transportOfferingConverter;
	}
}
