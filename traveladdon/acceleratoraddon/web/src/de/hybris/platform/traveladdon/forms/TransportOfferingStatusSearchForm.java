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
 */
package de.hybris.platform.traveladdon.forms;

import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * TransportOfferingStatusSearchForm object used to bind with the form in the statusSearch performing a validation.
 *
 */

public class TransportOfferingStatusSearchForm
{

    @NotEmpty(message = "{transport.offering.number.invalid}")
    private String transportOfferingNumber;

    @NotEmpty(message = "{departure.date.not.null}")
    private String departureDate;

    public void setTransportOfferingNumber(final String transportOfferingNumber)
    {
        this.transportOfferingNumber = transportOfferingNumber;
    }

    public void setDepartureDate(final String departureDate)
    {
        this.departureDate = departureDate;
    }

	public String getTransportOfferingNumber()
    {
        return transportOfferingNumber;
    }

	public String getDepartureDate()
    {
        return departureDate;
    }

}
