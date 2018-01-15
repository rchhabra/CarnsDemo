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
package de.hybris.platform.travelfacades.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Integration Test for the PassengerTypeFacade implementation using ServicelayerTransactionalTest
 *
 */

@IntegrationTest
public class DefaultPassengerTypeFacadeIntegrationTest extends ServicelayerTransactionalTest
{

    @Resource
    private PassengerTypeFacade passengerTypeFacade;

    @Resource
    private ModelService modelService;

    private PassengerTypeModel vipPassengerType;

    @Before
    public void setup()
    {
        vipPassengerType = new PassengerTypeModel();

        vipPassengerType.setCode("vip");
        vipPassengerType.setName("VIP Passenger", Locale.ENGLISH);
    }

    @Test
    public void getAllPassengerTypesTest()
    {

        // get number of items in the database before we add anything

        final int numOfItems = passengerTypeFacade.getPassengerTypes().size();

		Assert.assertEquals(0, numOfItems);

        // save the new sample data model

        modelService.save(vipPassengerType);

        // get an updated list of passenger types from the database

        final List<PassengerTypeData> passengerTypes = passengerTypeFacade.getPassengerTypes();

        // evaluate results

        Assert.assertNotNull(passengerTypes);
        Assert.assertTrue(!passengerTypes.isEmpty());
        Assert.assertEquals(1, (passengerTypes.size() - numOfItems));
        Assert.assertTrue("PassengerTypeModel with code '" + vipPassengerType.getCode() + "' not found!", checkResults(passengerTypes));

    }

    private boolean checkResults(final List<PassengerTypeData> passengerTypes)
    {

        for (final PassengerTypeData ptData : passengerTypes)
        {
            if (ptData.getCode().equals(vipPassengerType.getCode()) && ptData.getName().equals(vipPassengerType.getName()))
            {
                return true;
            }
        }

        return false;
    }

}
