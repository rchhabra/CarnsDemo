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

package de.hybris.platform.travelfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ancillary.accommodation.data.ConfiguredAccommodationData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfiguredAccommodationPopulatorTest
{
	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private ConfiguredAccommodationModel configuredAccommodationModel;

	@Mock
	private ProductModel productModel;

	@InjectMocks
	private final ConfiguredAccommodationPopulator configuredAccommodationPopulator = new ConfiguredAccommodationPopulator();

	@Test
	public void configuredAccommodationDataPopulatedTest()
	{
		Mockito.when(configuredAccommodationModel.isBookable()).thenReturn(Boolean.FALSE);
		Mockito.when(configuredAccommodationModel.getProduct()).thenReturn(productModel);
		final ProductData productData = new ProductData();
		productData.setCode("testCode");
		Mockito.when(productConverter.convert(productModel)).thenReturn(productData);
		final ConfiguredAccommodationData configuredAccommodationData = new ConfiguredAccommodationData();
		configuredAccommodationPopulator.populate(configuredAccommodationModel, configuredAccommodationData);
		Assert.assertFalse(configuredAccommodationData.getBookable());
		Assert.assertEquals("testCode", configuredAccommodationData.getProduct().getCode());
	}

	@Test
	public void setProductConverterTest()
	{
		configuredAccommodationPopulator.setProductConverter(productConverter);
		Assert.assertEquals(productConverter, configuredAccommodationPopulator.getProductConverter());
	}

}
