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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract handler responsible for instantiating a list of Offer Pricing Infos for given leg which will contain summary
 * of ancillaries selected by user
 */
public abstract class AbstractReservationOfferPricingInfoHandler
{
	private Converter<ProductModel, ProductData> productConverter;
	private List<ProductType> notAncillaryProductTypes;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private Map<String, ProductType> productTypeInstanceMap;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private CommonI18NService commonI18NService;

	/**
	 * Checks if the given abstractOrderEntry is an ancillary entry.
	 *
	 * @param reservationItem
	 *           the reservation item
	 * @param entry
	 *           the entry
	 * @return true if the abstractOrderEntry is an ancillary entry, false otherwise
	 */
	protected boolean isAncillaryEntry(final ReservationItemData reservationItem, final AbstractOrderEntryModel entry)
	{
		return isAncillaryEntry(entry) && entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() != null
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == reservationItem.getOriginDestinationRefNumber();
	}

	/**
	 * Checks if the given abstractOrderEntry is an ancillary entry.
	 *
	 * @param entry
	 *           the entry
	 * @return true if the abstractOrderEntry is an ancillary entry, false otherwise
	 */
	protected boolean isAncillaryEntry(final AbstractOrderEntryModel entry)
	{
		if (entry.getActive())
		{
			final String className = entry.getProduct().getClass().getSimpleName();
			ProductType productType = getProductTypeInstanceMap().get(className);
			if (Objects.isNull(productType))
			{
				productType = entry.getProduct().getProductType();
			}

			return !getNotAncillaryProductTypes().contains(productType);
		}

		return false;
	}

	/**
	 * Checks if the given abstractOrderEntry is an entry for a product of type PER_LEG_PER_PAX
	 *
	 * @param entry
	 *           the entry
	 * @param originDestinationRefNumber
	 *           the origin destination ref number
	 * @return true if the abstractOrderEntry is PER_LEG_PER_PAX, false otherwise
	 */
	protected boolean isPerLegPerPaxEntry(final AbstractOrderEntryModel entry, final int originDestinationRefNumber)
	{
		return entry.getActive() && CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTransportOfferings())
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNumber
				&& CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers());
	}

	/**
	 * Checks if the given abstractOrderEntry is an entry for a product of type PER_LEG
	 *
	 * @param entry
	 *           the entry
	 * @param originDestinationRefNumber
	 *           the origin destination ref number
	 * @return true if the abstractOrderEntry is PER_LEG, false otherwise
	 */
	protected boolean isPerLegEntry(final AbstractOrderEntryModel entry, final int originDestinationRefNumber)
	{
		return CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTransportOfferings())
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNumber
				&& CollectionUtils.isEmpty(entry.getTravelOrderEntryInfo().getTravellers());
	}

	/**
	 * Checks if the given abstractOrderEntry is an entry for a product of type PER_BOOKING
	 *
	 * @param entry
	 *           the entry
	 * @return true if the abstractOrderEntry is PER_BOOKING, false otherwise
	 */
	protected boolean isPerBookingEntry(final AbstractOrderEntryModel entry)
	{
		return CollectionUtils.isEmpty(entry.getTravelOrderEntryInfo().getTransportOfferings())
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == null
				&& CollectionUtils.isEmpty(entry.getTravelOrderEntryInfo().getTravellers());
	}

	/**
	 * Checks if the given abstractOrderEntry is an entry for a product of type PER_PAX
	 *
	 * @param entry
	 *           the entry
	 * @return true if the abstractOrderEntry is PER_PAX, false otherwise
	 */
	protected boolean isPerPaxEntry(final AbstractOrderEntryModel entry)
	{
		return entry.getActive() && CollectionUtils.isEmpty(entry.getTravelOrderEntryInfo().getTransportOfferings())
				&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == null
				&& CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers());
	}

	/**
	 * Gets requested Offer Pricing info from the list
	 *
	 * @param offerPricingInfos
	 *           - ancillary entries for given leg
	 * @param entry
	 *           - current ancillary abstract order entry
	 * @return offer pricing info matching the entry
	 */
	protected OfferPricingInfoData getOfferPricingInfoFromList(final List<OfferPricingInfoData> offerPricingInfos,
			final AbstractOrderEntryModel entry)
	{
		if (CollectionUtils.isEmpty(offerPricingInfos))
		{
			return null;
		}
		for (final OfferPricingInfoData offerPricingInfo : offerPricingInfos)
		{
			if (offerPricingInfo.getProduct().getCode().equals(entry.getProduct().getCode())
					&& checkBundleNo(entry.getBundleNo(), offerPricingInfo.getBundleIndicator()))
			{
				return offerPricingInfo;
			}
		}
		return null;
	}

	/**
	 * Compares the bundleNo and the bundleIndicator values.
	 *
	 * @param bundleNo
	 *           the bundleNo of the orderEntry
	 * @param bundleIndicator
	 *           the bundleIndicator of the offerPricingInfo
	 * @return true if the bundleNo and the bundleIndicator are equals to 0 or if the bundleNo is greater than 0 and the
	 *         bundleIndicator is equals to 1, false otherwise.
	 */
	protected boolean checkBundleNo(final Integer bundleNo, final Integer bundleIndicator)
	{
		if (bundleNo == 0 && bundleIndicator == 0)
		{
			return true;
		}

		return bundleNo > 0 && bundleIndicator == 1;
	}

	/**
	 * Finds Traveller Breakdown for current traveller in the entry
	 *
	 * @param offerPricingInfo
	 *           - pricing info for product in the entry
	 * @param entry
	 *           - current abstract order entry
	 * @return traveller breakdown for traveller from current entry
	 */
	protected TravellerBreakdownData getTravellerBreakdownForTraveller(final OfferPricingInfoData offerPricingInfo,
			final AbstractOrderEntryModel entry)
	{
		if (CollectionUtils.isNotEmpty(offerPricingInfo.getTravellerBreakdowns()))
		{
			for (final TravellerBreakdownData travellerBreakdown : offerPricingInfo.getTravellerBreakdowns())
			{
				if (travellerBreakdown.getTraveller().getLabel()
						.equals(entry.getTravelOrderEntryInfo().getTravellers().iterator().next().getLabel()))
				{
					return travellerBreakdown;
				}
			}
		}
		return null;
	}

	/**
	 * Updates given traveller breakdown with new quantity, base price and total price
	 *
	 * @param travellerBreakdown
	 * 		- current traveller breakdown
	 * @param quantity
	 * 		- new quantity
	 * @param basePriceValue
	 * 		- new base price
	 * @param totalPriceValue
	 * 		- new total price
	 *
	 * @deprecated Deprecated since version 3.0. Use {@link #updateTravellerBreakdown(TravellerBreakdownData, int, BigDecimal, BigDecimal, String)}
	 */
	@Deprecated
	protected void updateTravellerBreakdown(final TravellerBreakdownData travellerBreakdown, final int quantity,
			final BigDecimal basePriceValue, final BigDecimal totalPriceValue)
	{
		travellerBreakdown.setQuantity(quantity);
		final PassengerFareData passengerFare = travellerBreakdown.getPassengerFare();
		passengerFare.setBaseFare(getTravelCommercePriceFacade().createPriceData(basePriceValue.doubleValue()));
		passengerFare.setTotalFare(getTravelCommercePriceFacade().createPriceData(totalPriceValue.doubleValue()));

	}

	/**
	 * Updates given booking breakdown with new quantity, base price and total price
	 *
	 * @param bookingBreakdown
	 *           - current booking breakdown
	 * @param quantity
	 *           - new quantity
	 * @param basePriceValue
	 *           - new base price
	 * @param totalPriceValue
	 *           - new total price
	 * @deprecated Deprecated since version 3.0. Use {@link #updateBookingBreakdown(BookingBreakdownData, int, BigDecimal, BigDecimal, String)}
	 */
	@Deprecated
	protected void updateBookingBreakdown(final BookingBreakdownData bookingBreakdown, final int quantity,
			final BigDecimal basePriceValue, final BigDecimal totalPriceValue)
	{
		bookingBreakdown.setQuantity(quantity);
		final PassengerFareData passengerFare = bookingBreakdown.getPassengerFare();
		passengerFare.setBaseFare(getTravelCommercePriceFacade().createPriceData(basePriceValue.doubleValue()));
		passengerFare.setTotalFare(getTravelCommercePriceFacade().createPriceData(totalPriceValue.doubleValue()));
	}

	/**
	 * Updates given traveller breakdown with new quantity, base price and total price
	 *
	 * @param travellerBreakdown
	 * 		- current traveller breakdown
	 * @param quantity
	 * 		- new quantity
	 * @param basePriceValue
	 * 		- new base price
	 * @param totalPriceValue
	 * 		- new total price
	 * @param currencyIsocode
	 * 		- the currency isocode
	 */
	protected void updateTravellerBreakdown(final TravellerBreakdownData travellerBreakdown, final int quantity,
			final BigDecimal basePriceValue, final BigDecimal totalPriceValue, final String currencyIsocode)
	{
		travellerBreakdown.setQuantity(quantity);
		final PassengerFareData passengerFare = travellerBreakdown.getPassengerFare();
		passengerFare.setBaseFare(getTravelCommercePriceFacade().createPriceData(basePriceValue.doubleValue(), currencyIsocode));
		passengerFare.setTotalFare(getTravelCommercePriceFacade().createPriceData(totalPriceValue.doubleValue(), currencyIsocode));
	}

	/**
	 * Updates given booking breakdown with new quantity, base price and total price
	 *
	 * @param bookingBreakdown
	 * 		- current booking breakdown
	 * @param quantity
	 * 		- new quantity
	 * @param basePriceValue
	 * 		- new base price
	 * @param totalPriceValue
	 * 		- new total price
	 * @param currencyIsocode
	 * 		- the currency isocode
	 */
	protected void updateBookingBreakdown(final BookingBreakdownData bookingBreakdown, final int quantity,
			final BigDecimal basePriceValue, final BigDecimal totalPriceValue, final String currencyIsocode)
	{
		bookingBreakdown.setQuantity(quantity);
		final PassengerFareData passengerFare = bookingBreakdown.getPassengerFare();
		passengerFare.setBaseFare(getTravelCommercePriceFacade().createPriceData(basePriceValue.doubleValue(), currencyIsocode));
		passengerFare.setTotalFare(getTravelCommercePriceFacade().createPriceData(totalPriceValue.doubleValue(),currencyIsocode));
	}

	/**
	 * Creates a new instance of OfferPricingInfo for a new ancillary product
	 *
	 * @param entry
	 *           - new ancillary entry which needs to be translated into offer pricing info
	 * @return new offer pricing info containing information about current entry product
	 */
	protected OfferPricingInfoData createNewOfferPricingInfoForBookingBreakdown(final AbstractOrderEntryModel entry)
	{
		final OfferPricingInfoData offerPricingInfo = new OfferPricingInfoData();
		offerPricingInfo.setBundleIndicator(entry.getBundleNo() > 0 ? 1 : 0);
		offerPricingInfo.setProduct(getProductConverter().convert(entry.getProduct()));
		offerPricingInfo.setBookingBreakdown(createNewBookingBreakdown(entry));
		return offerPricingInfo;
	}

	/**
	 * Creates a new BookingBreakdown instance
	 *
	 * @param entry
	 *           - entry which needs to be included in booking breakdown
	 * @return new booking breakdown for traveller in the entry
	 */
	protected BookingBreakdownData createNewBookingBreakdown(final AbstractOrderEntryModel entry)
	{
		final BookingBreakdownData bookingBreakdown = new BookingBreakdownData();
		final PassengerFareData passengerFare = new PassengerFareData();
		bookingBreakdown.setPassengerFare(passengerFare);
		updateBookingBreakdown(bookingBreakdown, entry.getQuantity().intValue(), BigDecimal.valueOf(entry.getBasePrice()),
				BigDecimal.valueOf(entry.getTotalPrice()), entry.getOrder().getCurrency().getIsocode());
		return bookingBreakdown;
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets common I18N service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the commonI18NService
	 */
	@Deprecated
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common I18N service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Deprecated
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets product converter.
	 *
	 * @return the productConverter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * Sets product converter.
	 *
	 * @param productConverter
	 *           the productConverter to set
	 */
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	/**
	 * Gets not ancillary product types.
	 *
	 * @return the not ancillary product types
	 */
	protected List<ProductType> getNotAncillaryProductTypes()
	{
		return notAncillaryProductTypes;
	}

	/**
	 * Sets not ancillary product types.
	 *
	 * @param notAncillaryProductTypes
	 *           the not ancillary product types
	 */
	public void setNotAncillaryProductTypes(final List<ProductType> notAncillaryProductTypes)
	{
		this.notAncillaryProductTypes = notAncillaryProductTypes;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets the product type instance map.
	 *
	 * @return the productTypeInstanceMap
	 */
	protected Map<String, ProductType> getProductTypeInstanceMap()
	{
		return productTypeInstanceMap;
	}

	/**
	 * Sets the product type instance map.
	 *
	 * @param productTypeInstanceMap
	 *           the productTypeInstanceMap to set
	 */
	@Required
	public void setProductTypeInstanceMap(final Map<String, ProductType> productTypeInstanceMap)
	{
		this.productTypeInstanceMap = productTypeInstanceMap;
	}

}
