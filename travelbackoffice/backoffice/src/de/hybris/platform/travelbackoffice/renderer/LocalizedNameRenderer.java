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

package de.hybris.platform.travelbackoffice.renderer;

import de.hybris.platform.travelservices.model.travel.ActivityModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zhtml.Text;
import org.zkoss.zul.Listcell;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


/**
 *
 */
public class LocalizedNameRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object>
{

	/**
	 * Renders the localized name Value
	 */
	@Override
	public void render(final Listcell listcell, final ListColumn lictColumn, final Object data, final DataType dataType,
			final WidgetInstanceManager widgetInstanceManager)
	{
		String localizedName = StringUtils.EMPTY;
		if (data instanceof TransportFacilityModel)
		{
			final TransportFacilityModel transportFacility = (TransportFacilityModel) data;
			localizedName = transportFacility.getName();
		}
		else if (data instanceof LocationModel)
		{
			final LocationModel location = (LocationModel) data;
			localizedName = location.getName();
		}
		else if (data instanceof TravelProviderModel)
		{
			final TravelProviderModel travelProvider = (TravelProviderModel) data;
			localizedName = travelProvider.getName();
		}
		else if (data instanceof ActivityModel)
		{
			final ActivityModel activity = (ActivityModel) data;
			localizedName = activity.getName();
		}
		final Text nameText = new Text();
		nameText.setValue(localizedName);
		listcell.appendChild(nameText);


	}

}
