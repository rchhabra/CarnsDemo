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

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;

import java.util.Collection;

import org.zkoss.zhtml.Text;
import org.zkoss.zul.Listcell;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


/**
 *
 */
public class BundleTemplateCollectionRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object>
{

	/**
	 * Renders the comma separated bundle Template names for collection of BundleTemplates
	 */
	@Override
	public void render(final Listcell listcell, final ListColumn lictColumn, final Object data, final DataType dataType,
			final WidgetInstanceManager widgetInstanceManager)
	{
		if (data instanceof FareProductModel)
		{
			final FareProductModel fareProduct = (FareProductModel) data;
			final Collection<BundleTemplateModel> bundleTemplates = fareProduct.getBundleTemplates();
			final StringBuilder bundleTemplateIds = new StringBuilder();
			int count = 1;
			for (final BundleTemplateModel bundleTemplate : bundleTemplates)
			{
				bundleTemplateIds.append(bundleTemplate.getId());
				if (count < bundleTemplates.size())
				{
					bundleTemplateIds.append(" , ");
					count++;
				}
			}
			final Text bundleTemplateIdsText = new Text();
			bundleTemplateIdsText.setValue(bundleTemplateIds.toString());
			listcell.appendChild(bundleTemplateIdsText);
		}

	}

}
