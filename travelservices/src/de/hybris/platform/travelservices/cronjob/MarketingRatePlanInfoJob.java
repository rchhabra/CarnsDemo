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

package de.hybris.platform.travelservices.cronjob;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.MarketingRatePlanInfoModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanConfigModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.cronjob.MarketingRatePlanInfoCronjobModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.MarketingRatePlanInfoService;
import de.hybris.platform.travelservices.services.RatePlanConfigService;
import de.hybris.platform.travelservices.services.impl.DefaultAccommodationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Cronjob to create and populate the MarketingRatePlanInfo and the RatePlanConfig. This cronjob will correctly
 * populated the items only if the GuestOccupancy are set just for the Accommodation. In case that different ratePlans
 * are used for different GuestOccupancies, the logic should be extended and customised.
 */
public class MarketingRatePlanInfoJob extends AbstractJobPerformable<MarketingRatePlanInfoCronjobModel>
{
	private static final String ADULT = "adult";
	private static final String CHILD = "child";
	private static final int DEFAULT_RATE_PLAN_CONFIG_QUANTITY = 1;

	private static final Logger LOG = Logger.getLogger(MarketingRatePlanInfoJob.class);

	private AccommodationOfferingService accommodationOfferingService;
	private DefaultAccommodationService defaultAccommodationService;
	private ModelService modelService;
	private CatalogVersionService catalogVersionService;
	private RatePlanConfigService ratePlanConfigService;
	private MarketingRatePlanInfoService marketingRatePlanInfoService;

	@Override
	public PerformResult perform(final MarketingRatePlanInfoCronjobModel marketingRatePlanInfoCronjobModel)
	{
		LOG.info("Start performing MarketingRatePlanInfoJob...");

		final int batchsize = marketingRatePlanInfoCronjobModel.getBatchSize();
		int offset = 0;

		List<AccommodationOfferingModel> accommodationOfferings = marketingRatePlanInfoCronjobModel.getAccommodationOfferings();
		if (CollectionUtils.isNotEmpty(accommodationOfferings))
		{
			createMarketingRatePlanInfoList(accommodationOfferings, marketingRatePlanInfoCronjobModel.getRatePlanCode());
		}
		else
		{
			SearchResult<AccommodationOfferingModel> searchResults;
			do
			{
				searchResults = getAccommodationOfferingService().getAccommodationOfferings(batchsize, offset);
				accommodationOfferings = searchResults.getResult();
				createMarketingRatePlanInfoList(accommodationOfferings, marketingRatePlanInfoCronjobModel.getRatePlanCode());
				offset += batchsize;
			}
			while (offset <= searchResults.getTotalCount());
		}

		LOG.info("MarketingRatePlanInfoJob completed.");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected void createMarketingRatePlanInfoList(final List<AccommodationOfferingModel> accommodationOfferings,
			final String ratePlanCode)
	{
		final List<MarketingRatePlanInfoModel> marketingRatePlanInfos = new ArrayList<>();

		for (final AccommodationOfferingModel accommodationOffering : accommodationOfferings)
		{
			// Using the default implementation to avoid exceptions while running this cronjob with optimised version of
			// accommodation service enabled.
			final List<AccommodationModel> accommodations = getDefaultAccommodationService()
					.getAccommodationForAccommodationOffering(accommodationOffering.getCode());

			final List<GuestOccupancyModel> guestOccupancies = accommodations.stream()
					.flatMap(accommodation -> accommodation.getGuestOccupancies().stream()).distinct().collect(Collectors.toList());
			final List<GuestOccupancyModel> adultGuestOccupancies = guestOccupancies.stream()
					.filter(guestOccupancy -> StringUtils.equalsIgnoreCase(guestOccupancy.getPassengerType().getCode(), ADULT))
					.collect(Collectors.toList());

			for (final GuestOccupancyModel guestOccupancyModel : adultGuestOccupancies)
			{

				final MarketingRatePlanInfoModel marketingRatePlanInfo = createNewMarketingRatePlanInfo(accommodationOffering,
						accommodations, guestOccupancyModel, ratePlanCode);

				if (marketingRatePlanInfo != null)
				{
					marketingRatePlanInfos.add(marketingRatePlanInfo);
				}
			}
		}
		getModelService().saveAll(marketingRatePlanInfos);
	}

	protected MarketingRatePlanInfoModel createNewMarketingRatePlanInfo(final AccommodationOfferingModel accommodationOffering,
			final List<AccommodationModel> accommodations, final GuestOccupancyModel guestOccupancyModel, final String ratePlanCode)
	{
		final Optional<AccommodationModel> accommodationOptional = accommodations.stream()
				.filter(accommodation -> accommodation.getGuestOccupancies().contains(guestOccupancyModel)).findAny();

		if (!accommodationOptional.isPresent())
		{
			return null;
		}

		final String code = guestOccupancyModel.getCode() + "_" + accommodationOffering.getCode();

		MarketingRatePlanInfoModel marketingRatePlanInfo = null;
		try
		{
			marketingRatePlanInfo = getMarketingRatePlanInfoService().getMarketingRatePlanInfoForCode(code);
		}
		catch (final NoSuchElementException e)
		{
			LOG.debug("No marketingRatePlanInfo found for given code", e);
		}

		if (Objects.isNull(marketingRatePlanInfo))
		{
			marketingRatePlanInfo = getModelService().create(MarketingRatePlanInfoModel.class);
		}

		marketingRatePlanInfo.setCode(code);
		marketingRatePlanInfo.setAccommodationOffering(accommodationOffering);
		marketingRatePlanInfo.setNumberOfAdults(guestOccupancyModel.getQuantityMax());

		final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion("travelProductCatalog", "Staged");
		marketingRatePlanInfo.setCatalogVersion(catalogVersion);

		final RatePlanConfigModel ratePlanConfig = createRatePlanConfig(accommodationOptional.get(), ratePlanCode, catalogVersion);
		marketingRatePlanInfo.setRatePlanConfig(Arrays.asList(ratePlanConfig));
		final Optional<GuestOccupancyModel> childGuestOccupancyOptional = accommodationOptional.get().getGuestOccupancies().stream()
				.filter(occupancy -> StringUtils.equalsIgnoreCase(occupancy.getPassengerType().getCode(), CHILD)).findFirst();
		if (childGuestOccupancyOptional.isPresent())
		{
			marketingRatePlanInfo.setExtraGuests(Arrays.asList(childGuestOccupancyOptional.get()));
		}

		return marketingRatePlanInfo;
	}

	protected RatePlanConfigModel createRatePlanConfig(final AccommodationModel accommodationModel, final String ratePlanCode,
			final CatalogVersionModel catalogVersion)
	{
		final Collection<RatePlanModel> ratePlans = accommodationModel.getRatePlan();

		if (CollectionUtils.isEmpty(ratePlans))
		{
			return null;
		}

		final Optional<RatePlanModel> ratePlanOptional = ratePlans.stream()
				.filter(ratePlan -> StringUtils.containsIgnoreCase(ratePlan.getCode(), ratePlanCode)).findAny();

		RatePlanModel ratePlanModel = null;
		if (ratePlanOptional.isPresent())
		{
			ratePlanModel = ratePlanOptional.get();
		}
		else
		{
			final Optional<RatePlanModel> ratePlan = ratePlans.stream().findFirst();
			if (ratePlan.isPresent())
			{
				ratePlanModel = ratePlan.get();
			}
		}
		if (Objects.isNull(ratePlanModel))
		{
			LOG.debug("No ratePlan found for given model");
			return null;
		}

		final String code = accommodationModel.getCode() + "_" + ratePlanModel.getCode();

		RatePlanConfigModel ratePlanConfig = null;
		try
		{
			ratePlanConfig = getRatePlanConfigService().getRatePlanConfigForCode(code);
		}
		catch (final NoSuchElementException e)
		{
			LOG.debug("No ratePlanConfig found for given code", e);
		}

		if (Objects.isNull(ratePlanConfig))
		{
			ratePlanConfig = getModelService().create(RatePlanConfigModel.class);
		}

		ratePlanConfig.setCode(code);
		ratePlanConfig.setAccommodation(accommodationModel);
		ratePlanConfig.setRatePlan(ratePlanModel);
		ratePlanConfig.setQuantity(DEFAULT_RATE_PLAN_CONFIG_QUANTITY);
		ratePlanConfig.setCatalogVersion(catalogVersion);

		return ratePlanConfig;
	}

	/**
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * @param accommodationOfferingService
	 *           the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
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
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the catalogVersionService
	 */
	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * @return the ratePlanConfigService
	 */
	protected RatePlanConfigService getRatePlanConfigService()
	{
		return ratePlanConfigService;
	}

	/**
	 * @param ratePlanConfigService
	 *           the ratePlanConfigService to set
	 */
	@Required
	public void setRatePlanConfigService(final RatePlanConfigService ratePlanConfigService)
	{
		this.ratePlanConfigService = ratePlanConfigService;
	}

	/**
	 * @return the marketingRatePlanInfoService
	 */
	protected MarketingRatePlanInfoService getMarketingRatePlanInfoService()
	{
		return marketingRatePlanInfoService;
	}

	/**
	 * @param marketingRatePlanInfoService
	 *           the marketingRatePlanInfoService to set
	 */
	@Required
	public void setMarketingRatePlanInfoService(final MarketingRatePlanInfoService marketingRatePlanInfoService)
	{
		this.marketingRatePlanInfoService = marketingRatePlanInfoService;
	}

	protected DefaultAccommodationService getDefaultAccommodationService()
	{
		return defaultAccommodationService;
	}

	@Required
	public void setDefaultAccommodationService(final DefaultAccommodationService defaultAccommodationService)
	{
		this.defaultAccommodationService = defaultAccommodationService;
	}
}
