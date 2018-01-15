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
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * This handler is responsible for instantiating a list of Offer Pricing Infos for given leg which will contain summary
 * of ancillaries selected by user. It populates the OfferPricingInfo of the ReservationData.
 */
public class ReservationOfferPricingInfoHandler extends AbstractReservationOfferPricingInfoHandler implements ReservationHandler
{

	private Converter<TravellerModel, TravellerData> travellerDataConverter;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{

		final List<AbstractOrderEntryModel> ancillaryEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()))
				.filter(entry -> isAncillaryEntry(entry) && (isPerPaxEntry(entry) || isPerBookingEntry(entry)))
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(ancillaryEntries))
		{
			reservationData.setOfferPricingInfos(createOfferPricingInfo(ancillaryEntries));
		}
	}

	/**
	 * Creates a list of offer pricing infos for the given ancillary entries.
	 *
	 * @param ancillaryEntries
	 * 		- ancillary entries for given Abstract Order and leg
	 * @return list of offer pricing info for traveller
	 */
	protected List<OfferPricingInfoData> createOfferPricingInfo(final List<AbstractOrderEntryModel> ancillaryEntries)
	{
		final List<OfferPricingInfoData> offerPricingInfos = new ArrayList<OfferPricingInfoData>();

		for (final AbstractOrderEntryModel entry : ancillaryEntries)
		{
			if (isPerPaxEntry(entry))
			{
				createOfferPricingInfosForTravellerBreakdown(offerPricingInfos, entry);
			}
			if (isPerBookingEntry(entry))
			{
				createOfferPricingInfosForBookingBreakdown(offerPricingInfos, entry);
			}
		}
		return offerPricingInfos;
	}

	/**
	 * Creates a list of offerPricingInfos for the travellerBreakdown for the given entry
	 *
	 * @param offerPricingInfos
	 * 		the list of offerPricingInfo to be populated
	 * @param entry
	 * 		the abstractOrderEntry
	 */
	protected void createOfferPricingInfosForTravellerBreakdown(final List<OfferPricingInfoData> offerPricingInfos,
			final AbstractOrderEntryModel entry)
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
				final TravellerBreakdownData travellerBreakdown = createNewTravellerBreakdown(entry);
				List<TravellerBreakdownData> travellerBreakdowns = opi.getTravellerBreakdowns();
				if (CollectionUtils.isEmpty(travellerBreakdowns))
				{
					travellerBreakdowns = new ArrayList<>();
				}
				travellerBreakdowns.add(travellerBreakdown);
			}
		}
		else
		{
			final OfferPricingInfoData offerPricingInfo = createNewOfferPricingInfoForTravellerBreakdown(entry);
			offerPricingInfos.add(offerPricingInfo);
		}
	}

	/**
	 * Creates a list of offerPricingInfos for the bookingBreakdown for the given entry
	 *
	 * @param offerPricingInfos
	 * 		the list of offerPricingInfo to be populated
	 * @param entry
	 * 		the abstractOrderEntry
	 */
	protected void createOfferPricingInfosForBookingBreakdown(final List<OfferPricingInfoData> offerPricingInfos,
			final AbstractOrderEntryModel entry)
	{
		final OfferPricingInfoData opi = getOfferPricingInfoFromList(offerPricingInfos, entry);
		if (opi != null)
		{
			final BookingBreakdownData bookingBreakdown = opi.getBookingBreakdown();
			if (bookingBreakdown != null)
			{
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
				opi.setBookingBreakdown(createNewBookingBreakdown(entry));
			}
		}
		else
		{
			final OfferPricingInfoData offerPricingInfo = createNewOfferPricingInfoForBookingBreakdown(entry);
			offerPricingInfos.add(offerPricingInfo);
		}
	}

	/**
	 * Creates a new TravellerBreakdown instance
	 *
	 * @param entry
	 * 		- entry which needs to be included in traveller breakdown
	 * @return new traveller breakdown for traveller in the entry
	 */
	protected TravellerBreakdownData createNewTravellerBreakdown(final AbstractOrderEntryModel entry)
	{
		final TravellerBreakdownData travellerBreakdown = new TravellerBreakdownData();
		travellerBreakdown.setTraveller(getTraveller(entry));

		final PassengerFareData passengerFare = new PassengerFareData();
		travellerBreakdown.setPassengerFare(passengerFare);
		updateTravellerBreakdown(travellerBreakdown, entry.getQuantity().intValue(),
				BigDecimal.valueOf(entry.getBasePrice() * entry.getQuantity()), BigDecimal.valueOf(entry.getTotalPrice()),
				entry.getOrder().getCurrency().getIsocode());

		return travellerBreakdown;
	}

	/**
	 * Finds requested travellerData
	 *
	 * @param entry
	 * 		- entry containing requested traveller
	 * @return traveller data matching traveller from the entry
	 */
	protected TravellerData getTraveller(final AbstractOrderEntryModel entry)
	{
		final TravellerModel travellerModel = entry.getTravelOrderEntryInfo().getTravellers().iterator().next();
		return getTravellerDataConverter().convert(travellerModel);
	}

	/**
	 * Creates a new instance of OfferPricingInfo for a new ancillary product
	 *
	 * @param entry
	 * 		- new ancillary entry which needs to be translated into offer pricing info
	 * @return new offer pricing info containing information about current entry product
	 */
	protected OfferPricingInfoData createNewOfferPricingInfoForTravellerBreakdown(final AbstractOrderEntryModel entry)
	{
		final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
		offerPricingInfo.setBundleIndicator(entry.getBundleNo() > 0 ? 1 : 0);
		offerPricingInfo.setProduct(getProductConverter().convert(entry.getProduct()));
		final TravellerBreakdownData travellerBreakdown = createNewTravellerBreakdown(entry);
		final List<TravellerBreakdownData> travellerBreakdowns = new ArrayList<TravellerBreakdownData>();
		travellerBreakdowns.add(travellerBreakdown);
		offerPricingInfo.setTravellerBreakdowns(travellerBreakdowns);
		return offerPricingInfo;
	}

	/**
	 * Gets traveller data converter.
	 *
	 * @return the travellerDataConverter
	 */
	protected Converter<TravellerModel, TravellerData> getTravellerDataConverter()
	{
		return travellerDataConverter;
	}

	/**
	 * Sets traveller data converter.
	 *
	 * @param travellerDataConverter
	 * 		the travellerDataConverter to set
	 */
	public void setTravellerDataConverter(final Converter<TravellerModel, TravellerData> travellerDataConverter)
	{
		this.travellerDataConverter = travellerDataConverter;
	}

}
