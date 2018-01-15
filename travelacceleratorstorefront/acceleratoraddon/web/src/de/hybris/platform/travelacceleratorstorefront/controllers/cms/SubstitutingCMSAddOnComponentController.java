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
package de.hybris.platform.travelacceleratorstorefront.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelservices.substitute.ExtensionSubstitutionService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller which substitutes the path to view file from original to custom one.
 *
 * @param <T>
 */
public abstract class SubstitutingCMSAddOnComponentController<T extends AbstractCMSComponentModel> extends AbstractCMSAddOnComponentController<T> {

	@Resource(name = "extensionSubstitutionService")
	private ExtensionSubstitutionService extensionSubstitutionService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	private static final String MAX_ALLOWED_CHECKIN_CHECKOUT_DATE_DIFFERENCE = "maxAllowedCheckInCheckOutDateDifference";

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController#getAddonUiExtensionName(de .hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
     */
    @Override
    protected String getAddonUiExtensionName(final T component) {
        final String addonUiExtensionName = super.getAddonUiExtensionName(component);
        return extensionSubstitutionService.getSubstitutedExtension(addonUiExtensionName);
    }

	@ModelAttribute(MAX_ALLOWED_CHECKIN_CHECKOUT_DATE_DIFFERENCE)
	public String setMaxAllowedCheckInCheckOutDateDifference()
	{
		return StringUtils.EMPTY + configurationService.getConfiguration()
				.getInt(TravelacceleratorstorefrontWebConstants.MAX_ALLOWED_CHECKIN_CHECKOUT_DATE_DIFFERENCE);
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

}
