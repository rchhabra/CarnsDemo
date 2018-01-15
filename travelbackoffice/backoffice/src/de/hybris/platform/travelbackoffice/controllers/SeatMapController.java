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
package de.hybris.platform.travelbackoffice.controllers;

import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import java.util.Objects;



/**
 * The type Seat map controller.
 */
public class SeatMapController extends DefaultWidgetController
{

	@Wire
	private Editor transportVehicleReferenceEditor;

	@Wire
	private Editor transportOfferingReferenceEditor;

	@Wire
	private Editor travelSectorReferenceEditor;

	@Wire
	private Editor travelRouteReferenceEditor;

	@ViewEvent(componentID = "searchBtn", eventName = Events.ON_CLICK)
	public void searchBtn() throws InterruptedException
	{
		if (Objects.isNull(transportVehicleReferenceEditor.getValue()))
		{
			Messagebox.show(Labels.getLabel(TravelbackofficeConstants.SEAT_MAP_FINDER_MANDATORY_FIELDS_MISSING),
					Labels.getLabel(TravelbackofficeConstants.ERROR_TITLE), new Button[]
					{ Button.OK }, null, null);
		}
		else
		{
			//invoke seat map service to load seatmap svg.
		}
	}
}
