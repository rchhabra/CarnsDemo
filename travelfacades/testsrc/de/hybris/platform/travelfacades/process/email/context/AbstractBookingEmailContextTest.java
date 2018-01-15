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

package de.hybris.platform.travelfacades.process.email.context;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractBookingEmailContextTest extends AbstractBookingEmailContext
{
	@InjectMocks
	private final AbstractBookingEmailContext thiz = this;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Mock
	private OrderModel orderModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private LanguageModel languageModel;

	@Mock
	private CustomerModel customerModel;


	@Test
	public void testGetSite()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getSite()).thenReturn(baseSiteModel);
		getSite(orderProcessModel);
		verify(orderProcessModel.getOrder(), times(1)).getSite();

	}

	@Test
	public void testGetCustomer()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getUser()).thenReturn(customerModel);
		getCustomer(orderProcessModel);
		verify(orderProcessModel.getOrder(), times(1)).getUser();

	}

	@Test
	public void testGetEmailLanguage()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getLanguage()).thenReturn(languageModel);
		getEmailLanguage(orderProcessModel);
		verify(orderProcessModel.getOrder(), times(1)).getLanguage();

	}

	@Override
	protected Set<String> getAdditionalEmails(final OrderProcessModel orderProcessModel)
	{
		return null;
	}

}
