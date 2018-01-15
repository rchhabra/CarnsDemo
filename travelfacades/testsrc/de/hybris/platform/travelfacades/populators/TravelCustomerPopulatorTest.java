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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.converters.populator.CustomerPopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Junit Test Suite for {@link TravelCustomerPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelCustomerPopulatorTest
{
	@Mock
	private AbstractConverter<UserModel, CustomerData> customerConverter;

	@Mock
	private CustomerNameStrategy customerNameStrategy;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final TravelCustomerPopulator customerPopulator = new TravelCustomerPopulator();
		customerPopulator.setCustomerNameStrategy(customerNameStrategy);
		customerConverter = new ConverterFactory<UserModel, CustomerData, CustomerPopulator>().create(CustomerData.class,
				customerPopulator);
	}

	@Test
	public void testSetType()
	{
		final CustomerModel userModel = mock(CustomerModel.class);
		given(userModel.getName()).willReturn("Will Smith");
		given(userModel.getType()).willReturn(CustomerType.GUEST);
		final CustomerData customerData = customerConverter.convert(userModel);
		Assert.assertEquals(CustomerType.GUEST, customerData.getType());
	}
}
