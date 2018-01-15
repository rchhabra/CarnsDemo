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

package de.hybris.platform.travelbackoffice.widgets.bundle;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackoffice.utils.TravelBackofficeMessageBoxManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Custom Handler for the custom navigation action defined in the Second Step of the Bundle Wizard. This handler sets
 * the catalog version of the Parent Bundle in the child bundles
 */
public class CreateBundleCustomSecondStepHandler implements FlowActionHandler
{
	protected static final String CREATE_BUNDLE_WIZARD_SECOND_STEP_ERROR_MESSAGE = "create.bundle.wizard.second.step.error"
			+ ".message";
	protected static final String CREATE_BUNDLE_WIZARD_SECOND_STEP_ERROR_TITLE = "create.bundle.wizard.second.step.error.title";
	protected static final String FARE_PRODUCT_BUNDLE_ID = "FareProduct";
	protected static final String ANCILLARY_PRODUCT_BUNDLE_ID = "AncillaryProduct";

	private TravelBackofficeMessageBoxManager travelBackofficeMessageBoxManager;

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter flowActionHandlerAdapter,
			final Map<String, String> map)
	{
		final BundleTemplateModel parentBundleTemplate = flowActionHandlerAdapter.getWidgetInstanceManager().getModel()
				.getValue(TravelbackofficeConstants.CREATE_BUNDLE_TEMPLATE_ITEM_ID, BundleTemplateModel.class);
		final BundleTemplateModel fareBundleTemplate = flowActionHandlerAdapter.getWidgetInstanceManager().getModel()
				.getValue(TravelbackofficeConstants.CREATE_FARE_BUNDLE_TEMPLATE_ITEM_ID, BundleTemplateModel.class);
		final BundleTemplateModel ancillaryBundleTemplate = flowActionHandlerAdapter.getWidgetInstanceManager().getModel()
				.getValue(TravelbackofficeConstants.CREATE_ANCILLARY_BUNDLE_TEMPLATE_ITEM_ID, BundleTemplateModel.class);
		if (Objects.nonNull(parentBundleTemplate) && Objects.nonNull(fareBundleTemplate)
				&& Objects.nonNull(ancillaryBundleTemplate))
		{
			fareBundleTemplate.setCatalogVersion(parentBundleTemplate.getCatalogVersion());
			fareBundleTemplate.setId(getFareBundleTemplateId(parentBundleTemplate));

			ancillaryBundleTemplate.setCatalogVersion(parentBundleTemplate.getCatalogVersion());
			ancillaryBundleTemplate.setId(getAnacillaryBundleTemplateId(parentBundleTemplate));

			final List<BundleTemplateModel> childTemplates = new ArrayList<>();
			childTemplates.add(fareBundleTemplate);
			parentBundleTemplate.setChildTemplates(childTemplates);
			flowActionHandlerAdapter.next();
		}
		else
		{
			getTravelBackofficeMessageBoxManager().displayErrorMessage(CREATE_BUNDLE_WIZARD_SECOND_STEP_ERROR_TITLE,
					CREATE_BUNDLE_WIZARD_SECOND_STEP_ERROR_MESSAGE);
		}
	}

	/**
	 * Returns the id of the Fare Product Bundle Template. The id is build as a concat of the {@FARE_PRODUCT_BUNDLE_ID} constant
	 * and the id of the parentBundleTemplate id.
	 *
	 * @param parentBundleTemplate
	 * 		the parent bundle template
	 *
	 * @return a string representing the id of the Fare Product Bundle Template
	 */
	protected String getFareBundleTemplateId(final BundleTemplateModel parentBundleTemplate)
	{
		return FARE_PRODUCT_BUNDLE_ID + parentBundleTemplate.getId();
	}

	/**
	 * Returns the id of the Ancillary Product Bundle Template. The id is build as a concat of the {@ANCILLARY_PRODUCT_BUNDLE_ID}
	 * constant and the id of the parentBundleTemplate id.
	 *
	 * @param parentBundleTemplate
	 * 		the parent bundle template
	 *
	 * @return a string representing the id of the Ancillary Product Bundle Template
	 */
	protected String getAnacillaryBundleTemplateId(final BundleTemplateModel parentBundleTemplate)
	{
		return ANCILLARY_PRODUCT_BUNDLE_ID + parentBundleTemplate.getId();
	}

	/**
	 * @return the travelBackofficeMessageBoxManager
	 */
	protected TravelBackofficeMessageBoxManager getTravelBackofficeMessageBoxManager()
	{
		return travelBackofficeMessageBoxManager;
	}

	/**
	 * @param travelBackofficeMessageBoxManager
	 * 		the travelBackofficeMessageBoxManager to set
	 */
	@Required
	public void setTravelBackofficeMessageBoxManager(final TravelBackofficeMessageBoxManager travelBackofficeMessageBoxManager)
	{
		this.travelBackofficeMessageBoxManager = travelBackofficeMessageBoxManager;
	}
}
