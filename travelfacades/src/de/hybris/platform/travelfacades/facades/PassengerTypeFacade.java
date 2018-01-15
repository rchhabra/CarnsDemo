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
package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.travel.PassengerTypeData;

import java.util.List;


/**
 * Facade that exposes Passenger Type specific services
 */
public interface PassengerTypeFacade
{

    /**
     * Facade which returns a list of PassengerType data types
     *
     * @return List<PassengerType> passenger types
     */
    List<PassengerTypeData> getPassengerTypes();
}
