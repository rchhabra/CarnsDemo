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
import de.hybris.platform.commercefacades.travel.FeeData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
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
 * This handler is responsible for retrieving totals from abstract order and attaching them to reservation
 */
public class ReservationTotalFareHandler implements ReservationHandler
{
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

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private BaseStoreService baseStoreService;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		double totalPrice = 0d;

		final List<AbstractOrderEntryModel> transportEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())).collect(Collectors.toList());

		for (final AbstractOrderEntryModel entry : transportEntries)
		{
			totalPrice += entry.getTotalPrice();
		}

		final String currencyIsocode = abstractOrderModel.getCurrency().getIsocode();
		final TotalFareData totalFare = new TotalFareData();
		totalFare.setBasePrice(getTravelCommercePriceFacade().createPriceData(totalPrice, currencyIsocode));

		final List<TaxData> taxes = createTaxData(
				abstractOrderModel.getEntries().stream().filter(entry -> Objects.equals(OrderEntryType.TRANSPORT, entry.getType()))
						.flatMap(entry -> entry.getTaxValues().stream()).collect(Collectors.toList()));
		totalFare.setTaxes(taxes);

		populateDiscounts(abstractOrderModel, totalFare);
		populateFees(abstractOrderModel, totalFare);
		populateGlobalExtras(abstractOrderModel, totalFare);

		if (abstractOrderModel.getNet())
		{
			totalPrice += taxes.stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		}
		if (totalFare.getDiscountPrice() != null)
		{
			totalPrice -= totalFare.getDiscountPrice().getValue().doubleValue();
		}

		totalFare.setTotalPrice(getTravelCommercePriceFacade().createPriceData(totalPrice, currencyIsocode));

		reservationData.setTotalFare(totalFare);

		if (abstractOrderModel.getOriginalOrder() != null)
		{
			reservationData.setTotalToPay(calculateTotalToPay(abstractOrderModel));
		}
		else
		{
			reservationData.setTotalToPay(totalFare.getTotalPrice());
		}

	}

	/**
	 * Method to populate the discount objects and total discount value.
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @param totalFare
	 *           the total fare
	 */
	protected void populateDiscounts(final AbstractOrderModel abstractOrderModel, final TotalFareData totalFare)
	{
		final String currencyIsocode = abstractOrderModel.getCurrency().getIsocode();
		final List<DiscountData> totalDiscounts = new ArrayList<>();
		for (final DiscountValue discount : abstractOrderModel.getGlobalDiscountValues())
		{
			final DiscountData discountData = new DiscountData();
			discountData.setPrice(getTravelCommercePriceFacade().createPriceData(discount.getAppliedValue(), currencyIsocode));
			totalDiscounts.add(discountData);
		}

		// Get the discounts only for fare products and non-included ancillaries. Not for fees and included ancillaries
		abstractOrderModel.getEntries().stream().filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()))
				.filter(entryModel -> entryModel.getProduct() instanceof FareProductModel
						|| ProductType.FARE_PRODUCT.equals(entryModel.getProduct().getProductType())
						|| entryModel.getBundleNo() == 0)
				.forEach(entryModel -> {
					final List<DiscountData> discounts = createDiscountData(entryModel.getDiscountValues());
					totalDiscounts.addAll(discounts);
				});

		totalFare.setDiscounts(totalDiscounts);
		totalFare.setDiscountPrice(
				getTravelCommercePriceFacade().createPriceData(abstractOrderModel.getTotalDiscounts(), currencyIsocode));
	}

	/**
	 * Method to populate the globalExtras objects and total value.
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @param totalFare
	 *           the total fare
	 */
	protected void populateGlobalExtras(final AbstractOrderModel abstractOrderModel, final TotalFareData totalFare)
	{
		final List<AbstractOrderEntryModel> entries = abstractOrderModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()))
				.filter(entry -> !(ProductType.FEE.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FeeProductModel)
						&& !(ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
								|| entry.getProduct() instanceof FareProductModel)
						&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == null
						&& entry.getTravelOrderEntryInfo().getTravelRoute() == null)
				.collect(Collectors.toList());

		BigDecimal totalExtrasPrice = BigDecimal.valueOf(0);

		for (final AbstractOrderEntryModel entry : entries)
		{
			if (entry.getBundleNo() == 0)
			{
				double extrasPriceValue;
				if (entry.getQuantity() == 0)
				{
					extrasPriceValue = (CollectionUtils.isEmpty(entry.getDiscountValues())) ? entry.getTotalPrice()
							: entry.getBasePrice();
				}
				else
				{
					extrasPriceValue = entry.getBasePrice() * entry.getQuantity();
				}
				totalExtrasPrice = totalExtrasPrice.add(BigDecimal.valueOf(extrasPriceValue));
			}
		}
		final PriceData extrasPriceData = getTravelCommercePriceFacade()
				.createPriceData(totalExtrasPrice.doubleValue(), abstractOrderModel.getCurrency().getIsocode());
		totalFare.setExtrasPrice(extrasPriceData);
	}

	/**
	 * Method to populate the fees objects and total fees.
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @param totalFare
	 *           the total fare
	 */
	protected void populateFees(final AbstractOrderModel abstractOrderModel, final TotalFareData totalFare)
	{
		final List<FeeData> totalFeeList = new ArrayList<>();
		double totalFees = 0;

		final List<AbstractOrderEntryModel> feeEntries = abstractOrderModel.getEntries().stream()
				.filter(e -> Objects.equals(ProductType.FEE, e.getProduct().getProductType()) || e.getProduct() instanceof FeeProductModel)
				.collect(Collectors.toList());
		for (final AbstractOrderEntryModel entryModel : feeEntries)
		{
			totalFees = totalFees + entryModel.getTotalPrice();
			totalFeeList.add(createFeeData(entryModel));
		}
		totalFare.setTotalFees(
				getTravelCommercePriceFacade().createPriceData(totalFees, abstractOrderModel.getCurrency().getIsocode()));
		totalFare.setFees(totalFeeList);
	}

	/**
	 * Method to create Fee Data objects.
	 *
	 * @param entryModel
	 *           the entry model
	 * @return FeeData object.
	 */
	protected FeeData createFeeData(final AbstractOrderEntryModel entryModel)
	{
		final double feeValue = entryModel.getTotalPrice();
		final FeeData feeData = new FeeData();
		feeData.setName(entryModel.getProduct().getName());
		feeData.setPrice(getTravelCommercePriceFacade().createPriceData(feeValue, entryModel.getOrder().getCurrency().getIsocode()));
		return feeData;
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
	 * Calculates difference between old total and amended total to know how much user should pay (or how much they
	 * should be refunded)
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return price to pay (or to be refunded)
	 */
	protected PriceData calculateTotalToPay(final AbstractOrderModel abstractOrderModel)
	{
		final OrderModel originalOrder = abstractOrderModel.getOriginalOrder();

		final BigDecimal oldTotalWithTaxes = BigDecimal.valueOf(originalOrder.getTotalPrice())
				.add(BigDecimal.valueOf(originalOrder.getTotalTax()));
		final BigDecimal newTotalWithTaxes = BigDecimal.valueOf(abstractOrderModel.getTotalPrice())
				.add(BigDecimal.valueOf(abstractOrderModel.getTotalTax()));

		final BigDecimal totalToPay = newTotalWithTaxes.subtract(oldTotalWithTaxes);

		return getTravelCommercePriceFacade()
				.createPriceData(totalToPay.doubleValue(), abstractOrderModel.getCurrency().getIsocode());
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
	 * Gets base store service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return the baseStoreService
	 */
	@Deprecated
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * Sets base store service.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Deprecated
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
