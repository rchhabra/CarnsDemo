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

import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.reservation.handlers.GlobalTravelReservationHandler;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Global travel reservation basic handler.
 */
public class GlobalTravelReservationBasicHandler implements GlobalTravelReservationHandler
{
	private EnumerationService enumerationService;
	private I18NService i18NService;
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private Converter<UserModel, CustomerData> customerConverter;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final GlobalTravelReservationData globalTravelReservationData)
	{
		boolean isActiveOrder = true;
		if (abstractOrderModel instanceof OrderModel)
		{
			final String versionId = ((OrderModel) abstractOrderModel).getVersionID();
			isActiveOrder = StringUtils.isEmpty(versionId);
		}
		if (isActiveOrder)
		{
			Optional.ofNullable(abstractOrderModel.getStatus())
					.ifPresent(status -> globalTravelReservationData.setBookingStatusCode(status.getCode()));
			Optional.ofNullable(abstractOrderModel.getStatus()).ifPresent(status -> globalTravelReservationData
					.setBookingStatusName(getEnumerationService().getEnumerationName(status, getI18NService().getCurrentLocale())));
			Optional.ofNullable(abstractOrderModel.getCode()).ifPresent(code -> globalTravelReservationData.setCode(code));
		}
		else
		{
			final OrderModel latestOrderModel = getCustomerAccountService().getOrderForCode(abstractOrderModel.getCode(),
					getBaseStoreService().getCurrentBaseStore());
			final OrderStatus status = latestOrderModel.getStatus();
			globalTravelReservationData.setBookingStatusCode(status.getCode());
			globalTravelReservationData
					.setBookingStatusName(getEnumerationService().getEnumerationName(status, getI18NService().getCurrentLocale()));
			Optional.ofNullable(latestOrderModel.getCode()).ifPresent(code -> globalTravelReservationData.setCode(code));
		}
		globalTravelReservationData.setCustomerData(getCustomerConverter().convert(abstractOrderModel.getUser()));
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

	protected Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	@Required
	public void setCustomerConverter(
			final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}
}
