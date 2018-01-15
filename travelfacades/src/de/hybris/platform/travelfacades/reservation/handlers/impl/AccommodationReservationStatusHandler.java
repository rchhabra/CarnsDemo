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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.reservation.handlers.AccommodationReservationHandler;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This handler is responsible for populating the Status values of the {@link AccommodationReservationData}. If the
 * order is one of the previous version, the latest orderModel is retrieve and its status property value is set on the
 * AccommodationReservationData, otherwise is populated from the status property of the order.
 */
public class AccommodationReservationStatusHandler implements AccommodationReservationHandler
{
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private EnumerationService enumerationService;
	private I18NService i18NService;

	@Override
	public void handle(final AbstractOrderModel abstractOrder, final AccommodationReservationData accommodationReservationData)
			throws AccommodationPipelineException
	{
		boolean isActiveOrder = true;
		if (abstractOrder instanceof OrderModel)
		{
			final String versionId = ((OrderModel) abstractOrder).getVersionID();
			isActiveOrder = StringUtils.isEmpty(versionId);
		}
		if (isActiveOrder)
		{
			Optional.ofNullable(abstractOrder.getAccommodationOrderStatus()).ifPresent(status -> {
				accommodationReservationData.setBookingStatusCode(status.getCode());
				accommodationReservationData
						.setBookingStatusName(enumerationService.getEnumerationName(status, i18NService.getCurrentLocale()));
			});
		}
		else
		{
			final OrderModel latestOrderModel = getCustomerAccountService().getOrderForCode(abstractOrder.getCode(),
					getBaseStoreService().getCurrentBaseStore());
			final OrderStatus status = latestOrderModel.getAccommodationOrderStatus();
			accommodationReservationData.setBookingStatusCode(status.getCode());
			accommodationReservationData
					.setBookingStatusName(enumerationService.getEnumerationName(status, i18NService.getCurrentLocale()));
		}
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
	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
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
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * Gets i 18 n service.
	 *
	 * @return the i18NService
	 */
	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * Sets i 18 n service.
	 *
	 * @param i18nService
	 * 		the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

}
