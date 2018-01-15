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

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;

/**
 * 
 * Integration Test for the CabinClassFacade implementation using ServicelayerTransactionalTest
 *
 */

@IntegrationTest
public class DefaultCabinClassFacadeIntegrationTest extends ServicelayerTransactionalTest
{

    @Resource
    private CabinClassFacade cabinClassFacade;

    @Resource
    private ModelService modelService;

    private CabinClassModel vipCabinClass;

    @Before
    public void setup()
    {
        vipCabinClass = new CabinClassModel();

        vipCabinClass.setCode("vip");
        vipCabinClass.setName("VIP Cabin Class", Locale.ENGLISH);
    }

    @Test
    public void getAllCabinClassesTest()
    {

        // get number of items in the database before we add anything

        final int numOfItems = cabinClassFacade.getCabinClasses().size();

		Assert.assertEquals(0, numOfItems);

        // save the new sample data model

        modelService.save(vipCabinClass);

        // get an updated list of cabin classes from the database

        final List<CabinClassData> cabinClasses = cabinClassFacade.getCabinClasses();

        // evaluate results

        Assert.assertNotNull(cabinClasses);
        Assert.assertTrue(!cabinClasses.isEmpty());
        Assert.assertEquals(1, (cabinClasses.size() - numOfItems));
        Assert.assertTrue("CabinClassModel with code '" + vipCabinClass.getCode() + "' not found!", checkResults(cabinClasses));

    }

    private boolean checkResults(final List<CabinClassData> cabinClasses)
    {

        for (final CabinClassData ccData : cabinClasses)
        {
            if (ccData.getCode().equals(vipCabinClass.getCode()) && ccData.getName().equals(vipCabinClass.getName()))
            {
                return true;
            }
        }

        return false;
    }

}
