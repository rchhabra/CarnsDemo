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

package de.hybris.platform.travelcommerceorgaddon.impex;

import de.hybris.platform.cms2.jalo.contents.contentslot.ContentSlot;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/*
 * This strategy is used to place MyComponent Component in first place in the headerLinks as by default, MyCompany component is appended to last position in headerLinks.
 */
public class MyCompanyComponentPositionStrategy
{
	private ModelService modelService;

	public void sortComponents(final ContentSlot contentSlot)
	{
		final ContentSlotModel contentSlotModel = getModelService().get(contentSlot.getPK());
		final List<AbstractCMSComponentModel> components = contentSlotModel.getCmsComponents();
		if (CollectionUtils.size(components) > 1)
		{
			final List<AbstractCMSComponentModel> newComponentModifiableList = new ArrayList<>(components.size());
			final AbstractCMSComponentModel lastComponent = components.get(CollectionUtils.size(components) - 1);
			newComponentModifiableList.add(lastComponent);
			newComponentModifiableList.addAll(components.subList(0, CollectionUtils.size(components) - 1));
			contentSlotModel.setCmsComponents(newComponentModifiableList);
			getModelService().save(contentSlotModel);
		}
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
