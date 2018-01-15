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

package de.hybris.platform.travelservices.substitute.impl;

import de.hybris.platform.travelservices.substitute.ExtensionSubstitutionService;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Service needed for addons to search files in correct extensions
 */
public class DefaultExtensionSubsitutionService implements ExtensionSubstitutionService
{

	private Map<String, String> extensionSubstitutionMap;

	@Override
	public String getSubstitutedExtension(final String extension)
	{
		final String sub = getExtensionSubstitutionMap().get(extension);
		if (StringUtils.isEmpty(sub))
		{
			return extension;
		}

		return sub;
	}

	/**
	 * @return the extensionSubstitutionMap
	 */
	protected Map<String, String> getExtensionSubstitutionMap()
	{
		return extensionSubstitutionMap;
	}

	/**
	 * @param extensionSubstitutionMap
	 *           the extensionSubstitutionMap to set
	 */
	public void setExtensionSubstitutionMap(final Map<String, String> extensionSubstitutionMap)
	{
		this.extensionSubstitutionMap = extensionSubstitutionMap;
	}

}
