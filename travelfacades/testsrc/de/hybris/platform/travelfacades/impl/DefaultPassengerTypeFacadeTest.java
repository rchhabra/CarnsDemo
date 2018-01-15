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

package de.hybris.platform.travelfacades.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.facades.impl.DefaultPassengerTypeFacade;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.services.impl.DefaultPassengerTypeService;

/**
 * 
 * Unit Test for the PassengerTypeFacade implementation
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPassengerTypeFacadeTest
{

    @InjectMocks
    private final DefaultPassengerTypeFacade passengerTypeFacade = new DefaultPassengerTypeFacade();

    @Mock
    private DefaultPassengerTypeService passengerTypeService;

    @Mock
    private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

    private final List<PassengerTypeModel> ptModels = new ArrayList<PassengerTypeModel>();

    private final List<PassengerTypeData> ptData = new ArrayList<PassengerTypeData>();

    @Before
    public void setup()
    {
        setupPassengerTypeModelSampleData(ptModels);
        setupPassengerTypeDataSampleData(ptData);
    }

    @Test
    public void getAllCabinClassesTest()
    {

        Mockito.when(passengerTypeService.getPassengerTypes()).thenReturn(ptModels);
        Mockito.when(Converters.convertAll(ptModels, passengerTypeConverter)).thenReturn(ptData);

        final List<PassengerTypeData> result = passengerTypeFacade.getPassengerTypes();

        Assert.assertNotNull(result);
        Assert.assertEquals(ptModels.size(), result.size());
    }

    /**
     * Sample data containing a list of PassengerTypeModel
     * 
     * @param ptModels
     */
    private void setupPassengerTypeModelSampleData(final List<PassengerTypeModel> ptModels)
    {

        final PassengerTypeModel adult = new PassengerTypeModel();
        adult.setCode("adult");
        adult.setName("Adult", Locale.ENGLISH);

        final PassengerTypeModel children = new PassengerTypeModel();
        adult.setCode("child");
        adult.setName("Child", Locale.ENGLISH);

        final PassengerTypeModel infant = new PassengerTypeModel();
        adult.setCode("infant");
        adult.setName("Infant", Locale.ENGLISH);

        ptModels.add(adult);
        ptModels.add(children);
        ptModels.add(infant);
    }

    /**
     * Sample data containing a list of PassengerTypeData
     * 
     * @param ptData
     */
    private void setupPassengerTypeDataSampleData(final List<PassengerTypeData> ptData)
    {

        final PassengerTypeData adult = new PassengerTypeData();
        adult.setCode("adult");
        adult.setName("Adult");

        final PassengerTypeData children = new PassengerTypeData();
        adult.setCode("child");
        adult.setName("Child");

        final PassengerTypeData infant = new PassengerTypeData();
        adult.setCode("infant");
        adult.setName("Infant");

        ptData.add(adult);
        ptData.add(children);
        ptData.add(infant);
    }

}
