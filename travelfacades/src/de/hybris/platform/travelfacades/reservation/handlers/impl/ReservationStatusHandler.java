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

import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * This handler is responsible for populating the Status values of the ReservationData If the order is one of the
 * previous version, the status is set to CANCELLED, otherwise is populated from the status property of the order.
 */
public class ReservationStatusHandler implements ReservationHandler
{

	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private EnumerationService enumerationService;
	private I18NService i18NService;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		boolean isActiveOrder = true;
		if (abstractOrderModel instanceof OrderModel)
		{
			final String versionId = ((OrderModel) abstractOrderModel).getVersionID();
			isActiveOrder = StringUtils.isEmpty(versionId);
		}
		if (isActiveOrder)
		{
			Optional.ofNullable(abstractOrderModel.getTransportationOrderStatus()).ifPresent(status -> {
				reservationData.setBookingStatusCode(status.getCode());
				reservationData.setBookingStatusName(enumerationService.getEnumerationName(status, i18NService.getCurrentLocale()));
			});
		}
		else
		{
			final OrderModel latestOrderModel = getCustomerAccountService().getOrderForCode(abstractOrderModel.getCode(),
					getBaseStoreService().getCurrentBaseStore());
			final OrderStatus status = latestOrderModel.getTransportationOrderStatus();
			reservationData.setBookingStatusCode(status.getCode());
			reservationData.setBookingStatusName(enumerationService.getEnumerationName(status, i18NService.getCurrentLocale()));
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
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

}
