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
package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.Contacts.Contact;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.order.NDCPaymentAddressFacade;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCPaymentAddressFacade}
 */
public class DefaultNDCPaymentAddressFacade implements NDCPaymentAddressFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultNDCPaymentAddressFacade.class);

	private UserService userService;
	private CommonI18NService commonI18NService;
	private ConfigurationService configurationService;

	/**
	 * Creates the payment address
	 *
	 * @param orderCreateRQ
	 * 		the order create rq
	 * @param orderModel
	 * 		the order model
	 *
	 * @throws NDCOrderException
	 */
	@Override
	public void createPaymentAddress(final OrderCreateRQ orderCreateRQ, final OrderModel orderModel) throws NDCOrderException
	{
		final OrderPaymentFormType orderPayment = orderCreateRQ.getQuery().getPayments().getPayment().get(0);
		final AddressModel paymentAddress = new AddressModel();
		final CreditCardPaymentInfoModel creditCardPaymentInfo = new CreditCardPaymentInfoModel();

		paymentAddress.setOwner(creditCardPaymentInfo);
		orderModel.setPaymentAddress(paymentAddress);
		populateAddressModel(paymentAddress, orderPayment);
		orderModel.setDeliveryAddress(paymentAddress);
	}

	/**
	 * Creates the {@link AddressModel} based on the information contained in the {@link OrderPaymentFormType} of the
	 * {@link OrderCreateRQ}
	 *
	 * @param paymentAddress
	 * 		the payment address
	 * @param orderPayment
	 * 		the order payment
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void populateAddressModel(final AddressModel paymentAddress, final OrderPaymentFormType orderPayment)
			throws NDCOrderException
	{
		paymentAddress.setVisibleInAddressBook(true);
		paymentAddress.setDuplicate(false);
		paymentAddress.setFirstname(orderPayment.getPayer().getName().getGiven().stream().map(
				OrderPaymentFormType.Payer.Name.Given::getValue).collect(Collectors.joining(" ")));
		paymentAddress.setLastname(orderPayment.getPayer().getName().getSurname().getValue());

		if (!Objects.isNull(orderPayment.getPayer().getName().getTitle()))
		{
			paymentAddress.setTitle(getUserService().getTitleForCode(orderPayment.getPayer().getName().getTitle()));
		}

		if (!Objects.isNull(orderPayment.getPayer().getContacts()))
		{
			for (final Contact contact : orderPayment.getPayer().getContacts().getContact())
			{
				if (!Objects.isNull(contact.getAddressContact()))
				{
					paymentAddress.setStreetname(contact.getAddressContact().getStreet().stream().collect(Collectors.joining(" ")));
					paymentAddress.setTown(contact.getAddressContact().getCityName());
					paymentAddress.setPostalcode(contact.getAddressContact().getPostalCode());
					try
					{
						paymentAddress
								.setCountry(getCommonI18NService().getCountry(contact.getAddressContact().getCountryCode().getValue()));
					}
					catch (final UnknownIdentifierException e){
						LOG.debug(e);
						throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_COUNTRY_CODE));
					}
					return;
				}
			}
		}
	}

	/**
	 * Gets user service.
	 *
	 * @return the user service
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the user service
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
