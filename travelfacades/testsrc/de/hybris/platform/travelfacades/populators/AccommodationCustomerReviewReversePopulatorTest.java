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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link AccommodationCustomerReviewReversePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationCustomerReviewReversePopulatorTest
{
	@InjectMocks
	AccommodationCustomerReviewReversePopulator accommodationCustomerReviewReversePopulator;

	@Mock
	private ProductService productService;

	@Mock
	private AccommodationOfferingService accommodationOfferingService;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private UserService userService;

	private final String TEST_ACCOMMODATION_OFFERING_CODE = "TEST_ACCOMMODATION_OFFERING_CODE";
	private final String TEST_APPROVAL_STATUS = "TEST_APPROVAL_STATUS";

	@Before
	public void setUp()
	{

		given(productService.getProductForCode(Matchers.anyString())).willReturn(null);
		given(userService.getCurrentUser()).willReturn(null);
		given(accommodationOfferingService.getAccommodationOffering(Matchers.anyString())).willReturn(null);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(Matchers.anyString())).willReturn(TEST_APPROVAL_STATUS);
		given(enumerationService.getEnumerationValue(CustomerReviewApprovalType.class, TEST_APPROVAL_STATUS))
				.willReturn(CustomerReviewApprovalType.APPROVED);

	}

	@Test
	public void populateTest()
	{
		final ReviewData source = new ReviewData();
		source.setAccommodationOfferingCode(TEST_ACCOMMODATION_OFFERING_CODE);
		final CustomerReviewModel target = new CustomerReviewModel();
		accommodationCustomerReviewReversePopulator.populate(source, target);

		Assert.assertEquals(CustomerReviewApprovalType.APPROVED, target.getApprovalStatus());
	}
}
