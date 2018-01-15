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
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.DiscountData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SegmentInfoData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelfacades.util.PricingUtils;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is responsible for instantiating ReservationPricingInfo attribute of ReservationItem and attaching
 * subtotal for each leg of the journey
 */
public class ReservationPricingInfoHandler implements ReservationHandler
{
	private TravelCartService travelCartService;
	private TravelCommercePriceFacade travelCommercePriceFacade;

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

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		if (CollectionUtils.isNotEmpty(reservationData.getReservationItems()))
		{
			final List<SegmentInfoData> segmentInfoDatas = getSegmentInfoData(abstractOrderModel);
			reservationData.getReservationItems().forEach(reservationItem -> {
				createReservationPricingInfo(abstractOrderModel, reservationItem);
				reservationItem.getReservationPricingInfo().setSegmentInfoDatas(segmentInfoDatas);
			});
		}
	}

	/**
	 * Creates a new Reservation Pricing Info for given Reservation Item
	 *
	 * @param abstractOrderModel
	 *           - given abstract order
	 * @param reservationItem
	 *           the reservation item
	 */
	protected void createReservationPricingInfo(final AbstractOrderModel abstractOrderModel,
			final ReservationItemData reservationItem)
	{
		final ReservationPricingInfoData reservationPricingInfo = new ReservationPricingInfoData();
		reservationPricingInfo
				.setTotalFare(calculateSubtotalForLeg(abstractOrderModel, reservationItem.getOriginDestinationRefNumber()));
		reservationItem.setReservationPricingInfo(reservationPricingInfo);
	}

	/**
	 * creates and returns SegmentInfoData from fareProductEntries present in abstractOrderModel
	 *
	 * @param abstractOrderModel
	 *           - given abstract order
	 * @return list of SegmentInfoData
	 */
	protected List<SegmentInfoData> getSegmentInfoData(final AbstractOrderModel abstractOrderModel)
	{
		final List<SegmentInfoData> segmentInfoDatas = new ArrayList<>();
		final List<AbstractOrderEntryModel> fareProductEntries = travelCartService.getFareProductEntries(abstractOrderModel);
		fareProductEntries.forEach(
				fareProductEntry -> fareProductEntry.getTravelOrderEntryInfo().getTransportOfferings().forEach(transportOffering -> {
					final SegmentInfoData segmentInfoData = new SegmentInfoData();
					segmentInfoData.setTransportOfferingCode(transportOffering.getCode());
					segmentInfoData.setFareBasisCode(fareProductEntry.getProduct().getCode());
					segmentInfoDatas.add(segmentInfoData);
				}));
		return segmentInfoDatas;
	}

	/**
	 * Sums up all prices of Abstract Order Entries which are associated with given leg
	 *
	 * @param abstractOrderModel
	 *           - given abstract order
	 * @param originDestinationRefNumber
	 *           - leg indicator
	 * @return total fare data
	 */
	protected TotalFareData calculateSubtotalForLeg(final AbstractOrderModel abstractOrderModel,
			final int originDestinationRefNumber)
	{
		final List<AbstractOrderEntryModel> entries = abstractOrderModel.getEntries().stream()
				.filter(e -> OrderEntryType.TRANSPORT.equals(e.getType()))
				.filter(
						e -> !(Objects.equals(ProductType.FEE, e.getProduct().getProductType()) || e.getProduct() instanceof FeeProductModel))
				.filter(e -> e.getActive() && e.getTravelOrderEntryInfo().getOriginDestinationRefNumber() != null
						&& e.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNumber)
				.collect(Collectors.toList());

		BigDecimal basePrice = BigDecimal.valueOf(0);
		BigDecimal totalTaxPrice = BigDecimal.valueOf(0);
		BigDecimal totalDiscountPrice = BigDecimal.valueOf(0);
		BigDecimal totalExtrasPrice = BigDecimal.valueOf(0);

		final String currencyIsocode = abstractOrderModel.getCurrency().getIsocode();

		final List<TaxData> totalTaxes = new ArrayList<>();
		final List<DiscountData> totalDiscounts = new ArrayList<>();
		for (final AbstractOrderEntryModel entry : entries)
		{
			// Get the base prices only for fare products. Not for fees and ancillaries
			if (entry.getProduct() instanceof FareProductModel
					|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType()))
			{
				final double basePriceForPassengers = (entry.getQuantity() == 0) ? entry.getBasePrice()
						: entry.getBasePrice() * entry.getQuantity();
				basePrice = basePrice.add(BigDecimal.valueOf(basePriceForPassengers));
			}
			// Get the prices for included ancillaries
			if ((entry.getProduct() instanceof AncillaryProductModel
					|| ProductType.ANCILLARY.equals(entry.getProduct().getProductType()))
					&& entry.getBundleNo() > 0)
			{
				final double extrasForPassengers = entry.getQuantity() == 0 ? entry.getTotalPrice()
						: entry.getTotalPrice() * entry.getQuantity();
				totalExtrasPrice = totalExtrasPrice.add(BigDecimal.valueOf(extrasForPassengers));
			}
			// Get the prices only for non-included ancillaries. Not for fees, fare products and included ancillaries
			if (entry.getBundleNo() == 0)
			{
				final double extrasForPassengers = getPriceForAncillaryEntry(entry);
				totalExtrasPrice = totalExtrasPrice.add(BigDecimal.valueOf(extrasForPassengers));
			}
			final List<TaxData> taxes = createTaxData(new ArrayList<>(entry.getTaxValues()));
			totalTaxPrice = totalTaxPrice.add(PricingUtils.getTotalTaxValue(taxes));
			totalTaxes.addAll(taxes);

			// Get the discounts only for fare products and non-included ancillaries. Not for fees and included ancillaries
			if (entry.getProduct() instanceof FareProductModel
					|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
					|| entry.getBundleNo() == 0)
			{
				final List<DiscountData> discounts = createDiscountData(entry.getDiscountValues());
				totalDiscountPrice = totalDiscountPrice.add(PricingUtils.getTotalDiscountValue(discounts));
				totalDiscounts.addAll(discounts);
			}
		}
		BigDecimal totalPrice = basePrice.add(totalExtrasPrice).subtract(totalDiscountPrice);
		if (abstractOrderModel.getNet())
		{
			totalPrice = totalPrice.add(totalTaxPrice);
		}
		final TotalFareData totalFare = new TotalFareData();
		totalFare.setTaxes(totalTaxes);
		totalFare.setTaxPrice(getTravelCommercePriceFacade().createPriceData(totalTaxPrice.doubleValue(), currencyIsocode));
		totalFare.setDiscounts(totalDiscounts);
		totalFare
				.setDiscountPrice(getTravelCommercePriceFacade().createPriceData(totalDiscountPrice.doubleValue(), currencyIsocode));
		totalFare.setBasePrice(getTravelCommercePriceFacade().createPriceData(basePrice.doubleValue(), currencyIsocode));
		totalFare.setExtrasPrice(getTravelCommercePriceFacade().createPriceData(totalExtrasPrice.doubleValue(), currencyIsocode));
		totalFare.setTotalBaseExtrasPrice(getTravelCommercePriceFacade()
				.createPriceData(basePrice.doubleValue() + totalExtrasPrice.doubleValue(), currencyIsocode));
		totalFare.setTotalPrice(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue(), currencyIsocode));
		return totalFare;
	}

	/**
	 * Method to get the price from an order entry. Cancellation scenario is handled here where the quantity is 0. For
	 * entries that has more than 1 quantity, the price should be base price * quantity as the change product price rule
	 * could affect the total price. For cancelled entries, either base price or total price is picked because the
	 * quantity is 0.
	 *
	 * @param entry
	 *           the entry
	 * @return the price for ancillary entry
	 */
	protected double getPriceForAncillaryEntry(final AbstractOrderEntryModel entry)
	{
		double priceValue;
		// Cancelled entry
		if (entry.getQuantity() == 0)
		{
			priceValue = (CollectionUtils.isEmpty(entry.getDiscountValues())) ? entry.getTotalPrice() : entry.getBasePrice();
		}
		else
		{
			priceValue = entry.getBasePrice() * entry.getQuantity();
		}
		return priceValue;
	}

	/**
	 * Method to convert the Tax Values to Tax Data objects.
	 *
	 * @param taxValues
	 *           the tax values
	 * @return list of TaxData objects.
	 */
	protected List<TaxData> createTaxData(final List<TaxValue> taxValues)
	{
		final List<TaxData> taxes = new ArrayList<>();
		for (final TaxValue taxValue : taxValues)
		{
			final TaxData taxData = new TaxData();
			taxData.setCode(taxValue.getCode());
			taxData.setPrice(
					getTravelCommercePriceFacade().createPriceData(taxValue.getAppliedValue(), taxValue.getCurrencyIsoCode()));
			taxes.add(taxData);
		}
		return taxes;
	}

	/**
	 * Method to convert the Discount Values to Discount Data objects.
	 *
	 * @param discountValues
	 *           the discount values
	 * @return list of DiscountData objects.
	 */
	protected List<DiscountData> createDiscountData(final List<DiscountValue> discountValues)
	{
		final List<DiscountData> discounts = new ArrayList<>();
		for (final DiscountValue discount : discountValues)
		{
			final DiscountData discountData = new DiscountData();
			discountData.setPrice(
					getTravelCommercePriceFacade().createPriceData(discount.getAppliedValue(), discount.getCurrencyIsoCode()));
			discounts.add(discountData);
		}
		return discounts;
	}

	/**
	 * Method to create a new PriceData Object using PriceDataFactory
	 *
	 * @param priceValue
	 *           the price value
	 * @param currencyIsoCode
	 *           the currency iso code
	 * @deprecated Deprecated since version 3.0.
	 * @return PriceData price data
	 */
	@Deprecated
	protected PriceData createPriceData(final double priceValue, final String currencyIsoCode)
	{
		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currencyIsoCode);
	}

	/**
	 * Method to create a new PriceData Object using PriceDataFactory
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param priceValue
	 *           the price value
	 * @return PriceData price data
	 */
	@Deprecated
	protected PriceData createPriceData(final double priceValue)
	{
		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue),
				commonI18NService.getCurrentCurrency().getIsocode());
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
	 * Gets common i 18 n service.
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
	 * Sets common i 18 n service.
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
	 * Gets travel cart service.
	 *
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * Sets travel cart service.
	 *
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

}
