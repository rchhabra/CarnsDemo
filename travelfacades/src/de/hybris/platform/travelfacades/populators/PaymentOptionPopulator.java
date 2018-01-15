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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.order.PaymentOptionData;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.order.data.EntryTypePaymentInfo;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Payment option populator.
 */
public class PaymentOptionPopulator implements Populator<PaymentOptionInfo, PaymentOptionData>
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

	@Override
	public void populate(final PaymentOptionInfo source, final PaymentOptionData target) throws ConversionException
	{
		final List<EntryTypePaymentInfo> paymentInfos = source.getEntryTypeInfos();
		target.setAssociatedTransactions(createPaymentTransactionData(paymentInfos));
		target.setBookingTimeAmount(getTravelCommercePriceFacade()
				.createPriceData(paymentInfos.stream().mapToDouble(EntryTypePaymentInfo::getBookingTimeAmount).sum(), 2));
		target.setPrepaymentAmount(getTravelCommercePriceFacade()
				.createPriceData(paymentInfos.stream().mapToDouble(EntryTypePaymentInfo::getPrePaymentRequested).sum(), 2));
		target.setPrepaymentDeadlineInDays(
				convertMillisInDays(paymentInfos.stream().filter(info -> Objects.nonNull(info.getPrePaymentDeadline()))
						.mapToLong(EntryTypePaymentInfo::getPrePaymentDeadline).findFirst()));
		target.setCheckInPayOff(getTravelCommercePriceFacade()
				.createPriceData(paymentInfos.stream().mapToDouble(EntryTypePaymentInfo::getCheckInPayOff).sum(), 2));
	}

	/**
	 * Create payment transaction data list.
	 *
	 * @param paymentInfos
	 *           the payment infos
	 * @return the list
	 */
	protected List<PaymentTransactionData> createPaymentTransactionData(final List<EntryTypePaymentInfo> paymentInfos)
	{
		final List<PaymentTransactionData> paymentTransactionDatas = new ArrayList<>();
		paymentInfos.forEach(info -> {
			final PaymentTransactionData transaction = new PaymentTransactionData();
			final List<Integer> entryNumbers = info.getEntries().stream().filter(Objects::nonNull)
					.map(entry -> entry.getEntryNumber()).collect(Collectors.toList());
			transaction.setEntryNumbers(entryNumbers);
			transaction.setTransactionAmount(info.getBookingTimeAmount());
			transaction.setBookingType(info.getEntries().stream().filter(Objects::nonNull).findFirst().get().getType().getCode());
			paymentTransactionDatas.add(transaction);
		});
		return paymentTransactionDatas;
	}

	/**
	 * Convert millis in days integer.
	 *
	 * @param deadline
	 *           the deadline
	 * @return the integer
	 */
	protected Integer convertMillisInDays(final OptionalLong deadline)
	{
		return deadline.isPresent() ? (int) (deadline.getAsLong() / (1000 * 60 * 60 * 24)) : null;
	}

	/**
	 * Create price data price data.
	 *
	 * @deprecated Deprecated since version 3.0. Use {@link # getTravelCommercePriceFacade().createPriceData(value)} instead.
	 *
	 * @param value
	 *           the value
	 * @return the price data
	 */
	@Deprecated
	protected PriceData createPriceData(final double value)
	{
		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(value),
				getCommonI18NService().getCurrentCurrency().getIsocode());
	}

	/**
	 * Gets price data factory.
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return priceDataFactory price data factory
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
	 *           commonI18NService to set
	 */
	@Deprecated
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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
}
