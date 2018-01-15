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
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link SpecialRequestDetailsPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SpecialRequestDetailsPopulatorTest
{

	@InjectMocks
	private SpecialRequestDetailsPopulator specialRequestDetailsPopulator;

	@Mock
	private Converter<SpecialServiceRequestModel, SpecialServiceRequestData> specialServiceRequestConverter;

	private final List<SpecialServiceRequestModel> ssRequestModelList = new ArrayList<>();

	private final List<SpecialServiceRequestData> ssRequestDataList = new ArrayList<>();

	@Before
	public void setup()
	{
		specialRequestDetailsPopulator.setSpecialServiceRequestConverter(specialServiceRequestConverter);
		setupSpecialServiceRequestModelList(ssRequestModelList);
		setupSpecialServiceRequestDataList(ssRequestDataList);
	}

	private void setupSpecialServiceRequestDataList(final List<SpecialServiceRequestData> ssRequestDataList)
	{
		final SpecialServiceRequestData ssRequestData_1 = new SpecialServiceRequestData()
		{
			@Override
			public String getName()
			{
				return "TestName";
			}

			@Override
			public String getCode()
			{
				return "TestCode";
			}
		};
		final SpecialServiceRequestData ssRequestData_2 = new SpecialServiceRequestData()
		{
			@Override
			public String getName()
			{
				return "TestName2";
			}

			@Override
			public String getCode()
			{
				return "TestCode2";
			}
		};
		ssRequestDataList.add(ssRequestData_1);
		ssRequestDataList.add(ssRequestData_2);
	}

	private void setupSpecialServiceRequestModelList(final List<SpecialServiceRequestModel> ssRequestModelList)
	{
		final SpecialServiceRequestModel ssRequestModel_1 = new SpecialServiceRequestModel()
		{
			@Override
			public String getName()
			{
				return "TestName";
			}

			@Override
			public String getCode()
			{
				return "TestCode";
			}
		};
		final SpecialServiceRequestModel ssRequestModel_2 = new SpecialServiceRequestModel()
		{
			@Override
			public String getName()
			{
				return "TestName2";
			}

			@Override
			public String getCode()
			{
				return "TestCode2";
			}
		};
		ssRequestModelList.add(ssRequestModel_1);
		ssRequestModelList.add(ssRequestModel_2);
	}

	@Test
	public void testPopulateSpecialRequestDetails()
	{
		final SpecialRequestDetailModel specialRequestDetailModel = Mockito.mock(SpecialRequestDetailModel.class);
		Mockito.when(specialRequestDetailModel.getSpecialServiceRequest()).thenReturn(ssRequestModelList);
		Mockito.when(Converters.convertAll(ssRequestModelList, specialServiceRequestConverter)).thenReturn(ssRequestDataList);
		final SpecialRequestDetailData specialRequestDetailData = new SpecialRequestDetailData();

		specialRequestDetailsPopulator.populate(specialRequestDetailModel, specialRequestDetailData);

		Assert.assertNotNull(specialRequestDetailData.getSpecialServiceRequests());
		Assert.assertEquals(ssRequestDataList.size(), specialRequestDetailData.getSpecialServiceRequests().size());

	}
}
