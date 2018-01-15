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

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelfacades.strategies.OfferSortStrategy;
import de.hybris.platform.travelservices.enums.OfferSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.strategies.OfferSortStrategy} interface.
 */
public class DefaultOfferSortStrategy implements OfferSortStrategy
{
	private EnumerationService enumerationService;

	@Override
	public List<OfferGroupData> applyStrategy(final List<OfferGroupData> offerGroups)
	{
		final List<OfferSort> offerSortEnums = getEnumerationService().getEnumerationValues(OfferSort.class);

		final List<OfferGroupData> sortedOfferGroups = new ArrayList<>(offerGroups.size());
		for (final OfferSort offerSort : offerSortEnums)
		{
			final Optional<OfferGroupData> offerGroup = offerGroups.stream()
					.filter(group -> group.getCode().equals(offerSort.getCode())).findFirst();
			if (offerGroup.isPresent())
			{
				sortedOfferGroups.add(offerGroup.get());
			}
		}
		final List<OfferGroupData> undefinedOfferGroups = getUndefinedOfferGroups(offerGroups, offerSortEnums);
		if (CollectionUtils.isNotEmpty(undefinedOfferGroups))
		{
			sortedOfferGroups.addAll(undefinedOfferGroups);
		}
		return sortedOfferGroups;
	}

	/**
	 * Checks whether there are any OfferGroups which are not defined by OfferSort enum
	 *
	 * @param offerGroups
	 * 		all offer groups
	 * @param offerSortEnums
	 * 		list of all defined offer group categories
	 * @return undefined offer groups
	 */
	protected List<OfferGroupData> getUndefinedOfferGroups(final List<OfferGroupData> offerGroups,
			final List<OfferSort> offerSortEnums)
	{
		final List<String> offerSortCodes = new ArrayList<>(offerSortEnums.size());
		offerSortEnums.forEach(offerEnum -> offerSortCodes.add(offerEnum.getCode()));

		final List<OfferGroupData> undefinedOfferGroups = new ArrayList<>();
		for (final OfferGroupData offerGroup : offerGroups)
		{
			if (!offerSortCodes.contains(offerGroup.getCode()))
			{
				undefinedOfferGroups.add(offerGroup);
			}
		}
		return undefinedOfferGroups;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

}
