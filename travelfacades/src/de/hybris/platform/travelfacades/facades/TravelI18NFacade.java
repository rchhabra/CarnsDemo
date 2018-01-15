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
package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;

import java.util.List;


/**
 * Extension of Default TravelI18NFacade containing methods specific for Travel
 */
public interface TravelI18NFacade
{
	/**
	 * Retrieves all languages set up in system
	 *
	 * @return list of languages
	 */
	List<LanguageData> getAllLanguages();

	/**
	 * Retrieves all countries set up in system
	 *
	 * @return list of countries
	 */
	List<CountryData> getAllCountries();
}
