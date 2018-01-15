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
package de.hybris.platform.travelservices.substitute;

/**
 * When dealing with the add-on framework, there are times when an extension name is required (usually when building
 * paths to resources). However, this is often assumed to be the extension where an item type is defined, not where the
 * resources to handle it are located. So in these circumstances, we use the substituion service to return the correct
 * extension name.
 */
public interface ExtensionSubstitutionService
{

	/**
	 * Takes in an extension name, and if this is registered with the service, returns the alternate extension instead.
	 * If the name is not registered with the service, it merely returns the same extension name it has been called with.
	 *
	 * @param extension
	 * 		the extension name to check for substitution
	 * @return either the same name or a substituted naem
	 */
	String getSubstitutedExtension(String extension);
}
