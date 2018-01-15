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

package de.hybris.platform.ndcfacades.offers.handlers.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.enums.CategoryOption;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelservices.services.TravelCategoryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Handler class to populate OfferGroup for the OfferResponseData.
 */
public class NDCOfferGroupHandler implements AncillarySearchHandler
{
	private TravelCategoryService travelCategoryService;
	private Converter<CategoryModel, OfferGroupData> categoryConverter;
	private ConfigurablePopulator<CategoryModel, OfferGroupData, CategoryOption> offerGroupDataConfiguredPopulator;
	private Collection<CategoryOption> categoryOptionList;

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		final List<String> transportOfferingCodes = getTransportOfferingCodes(offerRequestData);

		final List<CategoryModel> accommodationCategories = getTravelCategoryService()
				.getAvailableAccommodationCategories(transportOfferingCodes);

		final List<CategoryModel> ancillaryCategories = getTravelCategoryService().getAncillaryCategories(transportOfferingCodes);

		final List<CategoryModel> allCategories = new ArrayList<>();
		allCategories.addAll(ancillaryCategories);

		if (Objects.nonNull(accommodationCategories))
		{
			allCategories.addAll(accommodationCategories);
		}
		final List<OfferGroupData> offerGroups = new ArrayList<>();

		for (final CategoryModel aCategory : allCategories)
		{
			final OfferGroupData anOfferGroup = new OfferGroupData();
			getCategoryConverter().convert(aCategory, anOfferGroup);
			anOfferGroup.setDescription(aCategory.getDescription());
			if (!getCategoryOptionList().isEmpty())
			{
				getOfferGroupDataConfiguredPopulator().populate(aCategory, anOfferGroup, getCategoryOptionList());
			}
			offerGroups.add(anOfferGroup);
		}
		offerResponseData.setOfferGroups(offerGroups);
	}

	/**
	 * Returns a list of Transport Offering codes from the current OfferRequestData
	 *
	 * @param offerRequestData
	 * 		the current Offer Request Data
	 *
	 * @return List<String>  the Transport Offering codes
	 */
	protected List<String> getTransportOfferingCodes(final OfferRequestData offerRequestData)
	{
		final List<String> codes = new ArrayList<>();

		for (final ItineraryData anItineraryData : offerRequestData.getItineraries())
		{

			for (final OriginDestinationOptionData originDestinationOption : anItineraryData.getOriginDestinationOptions())
			{

				codes.addAll(originDestinationOption.getTransportOfferings().stream().map(TransportOfferingData::getCode)
						.collect(Collectors.toList()));
			}
		}
		return codes;
	}

	/**
	 * Gets travel category service.
	 *
	 * @return the travelCategoryService
	 */
	protected TravelCategoryService getTravelCategoryService()
	{
		return travelCategoryService;
	}

	/**
	 * Sets travel category service.
	 *
	 * @param travelCategoryService
	 * 		the travelCategoryService to set
	 */
	public void setTravelCategoryService(final TravelCategoryService travelCategoryService)
	{
		this.travelCategoryService = travelCategoryService;
	}

	/**
	 * Gets category converter.
	 *
	 * @return the categoryConverter
	 */
	protected Converter<CategoryModel, OfferGroupData> getCategoryConverter()
	{
		return categoryConverter;
	}

	/**
	 * Sets category converter.
	 *
	 * @param categoryConverter
	 * 		the categoryConverter to set
	 */
	public void setCategoryConverter(final Converter<CategoryModel, OfferGroupData> categoryConverter)
	{
		this.categoryConverter = categoryConverter;
	}

	/**
	 * Gets offer group data configured populator.
	 *
	 * @return the offerGroupDataConfiguredPopulator
	 */
	protected ConfigurablePopulator<CategoryModel, OfferGroupData, CategoryOption> getOfferGroupDataConfiguredPopulator()
	{
		return offerGroupDataConfiguredPopulator;
	}

	/**
	 * Sets offer group data configured populator.
	 *
	 * @param offerGroupDataConfiguredPopulator
	 * 		as the offerGroupDataConfiguredPopulator
	 */
	public void setOfferGroupDataConfiguredPopulator(
			final ConfigurablePopulator<CategoryModel, OfferGroupData, CategoryOption> offerGroupDataConfiguredPopulator)
	{
		this.offerGroupDataConfiguredPopulator = offerGroupDataConfiguredPopulator;
	}

	/**
	 * Getter method for categoryOptionList
	 *
	 * @return the Collection<CategoryOption> injected in spring
	 */
	protected Collection<CategoryOption> getCategoryOptionList()
	{
		return categoryOptionList;
	}

	/**
	 * Setter method for categoryOptionList
	 *
	 * @param categoryOptionList
	 * 		to be injected
	 */
	public void setCategoryOptionList(final Collection<CategoryOption> categoryOptionList)
	{
		this.categoryOptionList = categoryOptionList;
	}
}
