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

package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AddressContactType;
import de.hybris.platform.ndcfacades.ndc.Contacts;
import de.hybris.platform.ndcfacades.ndc.Contacts.Contact;
import de.hybris.platform.ndcfacades.ndc.CountryCode;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Payments;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType.Payer;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType.Payer.Name;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType.Payer.Name.Given;
import de.hybris.platform.ndcfacades.ndc.OrderPaymentFormType.Payer.Name.Surname;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCPaymentAddressFacadeTest
{
	@InjectMocks
	DefaultNDCPaymentAddressFacade defaultNDCPaymentAddressFacade;

	@Mock
	private UserService userService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;

	OrderCreateRQ orderCreateRQ;
	OrderModel order;

	@Before
	public void setUp()
	{
		orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();
		final Payments payments = new Payments();
		final OrderPaymentFormType orderPayment = new OrderPaymentFormType();
		payments.getPayment().add(orderPayment);
		query.setPayments(payments);
		orderCreateRQ.setQuery(query);

		final Payer payer = new Payer();
		final Name name = new Name();
		name.setTitle("Mr");
		final Given givenName = new Given();
		givenName.setValue("given Value");
		name.getGiven().add(givenName);
		final Surname surName = new Surname();
		surName.setValue("Surname");
		name.setSurname(surName);
		payer.setName(name);

		final TitleModel title = new TitleModel();
		title.setCode("Mr");
		Mockito.when(userService.getTitleForCode(payer.getName().getTitle())).thenReturn(title);

		final Contacts contacts = new Contacts();
		final Contact contact = new Contact();
		final AddressContactType addressContactType = new AddressContactType();
		addressContactType.getStreet().add("Street");
		addressContactType.setCityName("City");
		addressContactType.setPostalCode("POSTAL");
		final CountryCode countryCode = new CountryCode();
		countryCode.setValue("UK");
		addressContactType.setCountryCode(countryCode);
		contact.setAddressContact(addressContactType);

		contacts.getContact().add(contact);
		payer.setContacts(contacts);

		orderPayment.setPayer(payer);
		order = new OrderModel();
	}

	@Test(expected = NDCOrderException.class)
	public void testCreatePaymentAddressWithException() throws NDCOrderException
	{
		Mockito.when(commonI18NService.getCountry(Mockito.anyString())).thenThrow(new UnknownIdentifierException(""));
		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.INVALID_COUNTRY_CODE))
				.thenReturn("NDCOrderException");

		defaultNDCPaymentAddressFacade.createPaymentAddress(orderCreateRQ, order);
	}

	@Test
	public void testCreatePaymentAddress() throws NDCOrderException
	{
		final CountryModel country = new CountryModel();
		Mockito.when(commonI18NService.getCountry(Mockito.anyString())).thenReturn(country);
		defaultNDCPaymentAddressFacade.createPaymentAddress(orderCreateRQ, order);
		Assert.assertEquals(country, order.getDeliveryAddress().getCountry());
	}
}
