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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelfacades.strategies.EncodeSavedSearchStrategy;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator to populate the SavedSearchModel from the SavedSearchData
 */
public class SavedSearchReversePopulator implements Populator<SavedSearchData, SavedSearchModel>
{

	private EncodeSavedSearchStrategy encodeSavedSearchStrategy;


	@Override
	public void populate(final SavedSearchData source, final SavedSearchModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setEncodedSearch(getEncodeSavedSearchStrategy().getEncodedSearch(source));

	}

	/**
	 * Gets encode saved search strategy.
	 *
	 * @return the encode saved search strategy
	 */
	protected EncodeSavedSearchStrategy getEncodeSavedSearchStrategy()
	{
		return encodeSavedSearchStrategy;
	}

	/**
	 * Sets encode saved search strategy.
	 *
	 * @param encodeSavedSearchStrategy
	 * 		the encodeSavedSearchStrategy to set
	 */
	@Required
	public void setEncodeSavedSearchStrategy(final EncodeSavedSearchStrategy encodeSavedSearchStrategy)
	{
		this.encodeSavedSearchStrategy = encodeSavedSearchStrategy;
	}

}
