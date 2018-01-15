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

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.cronjob.UpdateStockLevelsToAccommodationOfferingCronJobModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



public class UpdateStockLevelsToAccommodationOfferingJob
		extends AbstractJobPerformable<UpdateStockLevelsToAccommodationOfferingCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(UpdateStockLevelsToAccommodationOfferingJob.class);
	private static final String CATALOG_NAME = "travelProductCatalog";
	private static final String SEPARATOR = ";";
	private static final String LIST_SEPARATOR = ",";
	private static final int DEF_AVAILABILITY = 50;
	private Long scheduledDays;
	private Boolean setForceInStock;

	private static boolean SIMULATION_MODE;

	private AccommodationOfferingService accommodationOfferingService;
	private ProductService productService;
	private CatalogVersionService catalogVersionService;
	private String baseExtDir;

	@Override
	public PerformResult perform(
			final UpdateStockLevelsToAccommodationOfferingCronJobModel updateStockLevelsToAccommodationOfferingCronJobModel)
	{
		LOG.info("Start performing UpdateStockLevelsToAccommodationOfferingJob...");

		final List<AccommodationOfferingModel> accommodationOfferings = getAccommodationOfferingService()
				.getAccommodationOfferings();
		final List<StockLevelModel> stockLevelsToSave = new ArrayList<>();

		stockLevelsToSave
				.addAll(getAccommodationStockLevels(updateStockLevelsToAccommodationOfferingCronJobModel, accommodationOfferings));

		stockLevelsToSave
				.addAll(getProductStockLevels(updateStockLevelsToAccommodationOfferingCronJobModel, accommodationOfferings));

		final int stockLevelSize = CollectionUtils.size(stockLevelsToSave);
		LOG.info("Start saving... Number of stock levels to be saved: " + stockLevelSize);
		final int numberOfBatches = (int) Math.ceil(stockLevelSize / 500d);

		for (int i = 0; i < numberOfBatches; i++)
		{
			if (i != numberOfBatches - 1)
			{
				getModelService().saveAll(stockLevelsToSave.subList(i * 500, (i + 1) * 500));
			}
			else
			{
				getModelService().saveAll(stockLevelsToSave.subList(i * 500, stockLevelSize));
			}

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Saved batch number: " + i);
			}
		}

		LOG.info("Saving finished");

		LOG.info("UpdateStockLevelsToAccommodationOfferingJob completed.");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected List<StockLevelModel> getAccommodationStockLevels(
			final UpdateStockLevelsToAccommodationOfferingCronJobModel updateStockLevelsToAccommodationOfferingCronJobModel,
			final List<AccommodationOfferingModel> accommodationOfferings)
	{
		final Map<String, List<String>> accommodationOfferingToAccommodationMaps = parseMappingFile(
				updateStockLevelsToAccommodationOfferingCronJobModel.getMappingDataFilePath());

		final Map<String, Integer> availabilityMap = StringUtils
				.isNotEmpty(updateStockLevelsToAccommodationOfferingCronJobModel.getAvailabilityDataFilePath())
						? parseAvailabilityFile(updateStockLevelsToAccommodationOfferingCronJobModel.getAvailabilityDataFilePath())
						: null;
		SIMULATION_MODE = Objects.isNull(availabilityMap);

		final Integer maxAvailability = Objects.nonNull(updateStockLevelsToAccommodationOfferingCronJobModel.getMaxAvailability())
				? updateStockLevelsToAccommodationOfferingCronJobModel.getMaxAvailability() : Integer.valueOf(DEF_AVAILABILITY);

		final LocalDateTime startDate = LocalDateTime.now();
		final LocalDateTime endDate = startDate.plusDays(Objects.nonNull(getScheduledDays()) ? getScheduledDays() : 365);

		final List<StockLevelModel> stockLevels = new ArrayList<StockLevelModel>();

		accommodationOfferingToAccommodationMaps.entrySet().forEach(entry -> {
			final Optional<AccommodationOfferingModel> accommodationOfferingModelOptional = accommodationOfferings.stream().filter(
					accommodationOfferingModel -> StringUtils.equalsIgnoreCase(accommodationOfferingModel.getCode(), entry.getKey()))
					.findFirst();
			if (accommodationOfferingModelOptional.isPresent())
			{
				final AccommodationOfferingModel accommodationOffering = accommodationOfferingModelOptional.get();
				final List<AccommodationModel> accommodations = entry.getValue().stream()
						.map(code -> (AccommodationModel) getProductService()
								.getProductForCode(getCatalogVersionService().getCatalogVersion(CATALOG_NAME, "Staged"), code))
						.collect(Collectors.toList());
				Stream.iterate(startDate, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
						.forEach(date -> setStockLevelForAccommodation(accommodationOffering, accommodations, date, maxAvailability,
								availabilityMap, stockLevels));
				setStockLevelForRoomRates(accommodationOffering, accommodations, stockLevels);
			}
		});
		return stockLevels;
	}

	protected List<StockLevelModel> getProductStockLevels(
			final UpdateStockLevelsToAccommodationOfferingCronJobModel updateStockLevelsToAccommodationOfferingCronJobModel,
			final List<AccommodationOfferingModel> accommodationOfferings)
	{

		final List<StockLevelModel> stockLevels = new ArrayList<StockLevelModel>();
		final Map<String, Integer> stockMap = updateStockLevelsToAccommodationOfferingCronJobModel.getServiceProductsStockLevels();

		final LocalDateTime startDate = LocalDateTime.now();
		final LocalDateTime endDate = startDate.plusDays(Objects.nonNull(getScheduledDays()) ? getScheduledDays() : 365);

		for (final AccommodationOfferingModel accommodationOffering : accommodationOfferings)
		{
			stockMap.entrySet().forEach(entry -> Stream.iterate(startDate, date -> date.plusDays(1))
					.limit(ChronoUnit.DAYS.between(startDate, endDate) + 1).forEach(date -> {
						final Date dateToSet = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
						if (CollectionUtils.isNotEmpty(accommodationOffering.getStockLevels()))
						{
							final Optional<StockLevelModel> stockLevelOptional = accommodationOffering.getStockLevels().stream()
									.filter(stockLevel -> StringUtils.equalsIgnoreCase(stockLevel.getProductCode(), entry.getKey())
											&& TravelDateUtils.isSameDate(stockLevel.getDate(), dateToSet))
									.findFirst();
							if (stockLevelOptional.isPresent())
							{
								stockLevelOptional.get().setAvailable(entry.getValue());
								stockLevels.add(stockLevelOptional.get());
								return;
							}
						}
						stockLevels.add(createStockLevel(accommodationOffering, entry, dateToSet));
					}));
		}
		return stockLevels;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, List<String>> parseMappingFile(final String mappingFilePath)
	{
		final String completePath = baseExtDir + mappingFilePath;
		LOG.info("Start parsing file " + completePath);
		try (BufferedReader reader = getReader(completePath))
		{
			return reader.lines().filter(StringUtils::isNotBlank)
					.collect(Collectors.toMap(this::getAccommodationOffering, this::getAccommodations));
		}
		catch (final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	protected BufferedReader getReader(final String completePath) throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(completePath));
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Integer> parseAvailabilityFile(final String availabilityDataFilePath)
	{
		final String completePath = baseExtDir + availabilityDataFilePath;
		LOG.info("Start parsing availability file " + completePath);
		try (BufferedReader reader = getReader(completePath))
		{
			return reader.lines().filter(StringUtils::isNotBlank)
					.collect(Collectors.toMap(this::getAccommodation, this::getAvailability));
		}
		catch (final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * If a mapping file is provided stock levels are populated according to its content otherwise their availability is
	 * simulated by a populating algorithm
	 *
	 * @param accommodationOffering
	 *           AccommodationOfferingModel to which the stockLevel has to be updated.
	 * @param accommodations
	 *           list of accommodations for the selected offering
	 * @param date
	 *           the date stock level will refer to
	 * @param maxAvailability
	 *           the max availability that can be set in simulation mode
	 * @param availabilityMap
	 *           a map linking accommodations codes and their actual availability (when provided)
	 * @param stockLevelsToSave
	 */
	protected void setStockLevelForAccommodation(final AccommodationOfferingModel accommodationOffering,
			final List<AccommodationModel> accommodations, final LocalDateTime date, final Integer maxAvailability,
			final Map<String, Integer> availabilityMap, final List<StockLevelModel> stockLevelsToSave)
	{
		final Set<StockLevelModel> stockLevels = accommodationOffering.getStockLevels();
		final Date dateToSet = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

		for (final AccommodationModel accommodation : accommodations)
		{
			final boolean isStockCodePresent = CollectionUtils.isNotEmpty(stockLevels)
					&& stockLevels.stream().anyMatch(stockLevel -> {
						if (stockLevel.getProductCode().equals(accommodation.getCode())
								&& TravelDateUtils.isSameDate(stockLevel.getDate(), dateToSet))
						{
							stockLevel.setAvailable(
									SIMULATION_MODE ? maxAvailability : getAvailabilityFromMap(availabilityMap, accommodation.getCode()));
							if (SIMULATION_MODE)
							{
								stockLevel.setReserved(setupSimulatedAvailability(maxAvailability));
							}
							stockLevelsToSave.add(stockLevel);
							return true;
						}
						return false;
					});
			if (!isStockCodePresent)
			{
				final StockLevelModel stockLevelModel = getModelService().create(StockLevelModel.class);
				stockLevelModel.setWarehouse(accommodationOffering);
				stockLevelModel.setProductCode(accommodation.getCode());
				stockLevelModel.setAvailable(
						SIMULATION_MODE ? maxAvailability : getAvailabilityFromMap(availabilityMap, accommodation.getCode()));
				if (SIMULATION_MODE)
				{
					stockLevelModel.setReserved(setupSimulatedAvailability(maxAvailability));
				}
				stockLevelModel.setDate(dateToSet);
				stockLevelsToSave.add(stockLevelModel);
			}
		}
	}

	/**
	 * Method that returns the int corresponding to the simulated availability, calculated using a Gaussian distribution with
	 * mean equals to maxAvailability / 2 and standard deviance equals to maxAvailability / 4. If the random number is falling
	 * outside the range [0, maxAvailability] the value 0 is return, in order to minimize the number of unavailable
	 * accommodations.
	 *
	 * @param maxAvailability
	 * 		the max availability
	 *
	 * @return simulated availability
	 */
	protected int setupSimulatedAvailability(final Integer maxAvailability)
	{
		final Random random = new Random();
		final double randomDouble = random.nextGaussian() * (maxAvailability / 4) + (maxAvailability / 2);
		return randomDouble < 0 || randomDouble > maxAvailability ? 0 : (int) Math.round(randomDouble);
	}

	/**
	 * Method that returns the int corresponding to the simulated availability, calculated using a Gaussian distribution with
	 * mean and standard deviance equals to maxAvailability / 2 and then normalized using a date correction.
	 *
	 * @param date
	 * 		the date
	 * @param maxAvailability
	 * 		the max availability
	 *
	 * @return simulated availability
	 */
	protected int setupSimulatedAvailability(final LocalDateTime date, final Integer maxAvailability)
	{
		final Random random = new Random();
		final int correctedMean = maxAvailability / 2;
		final int baseAvailability = (int) (random.nextGaussian() * correctedMean) + correctedMean;
		final int dateCorrection = (int) (ChronoUnit.DAYS.between(date, LocalDateTime.now())
				* (correctedMean / (Objects.nonNull(getScheduledDays()) ? getScheduledDays() : 365)));
		return normalizeValue(baseAvailability + dateCorrection, maxAvailability);
	}


	protected int normalizeValue(final int value, final Integer maxAvailability)
	{
		if (!new IntRange(0, maxAvailability.intValue()).containsInteger(value))
		{
			if (value < 0)
			{
				return 0;
			}
			else
			{
				return maxAvailability;
			}
		}
		return value;
	}

	protected int getAvailabilityFromMap(final Map<String, Integer> availabilityMap, final String accommodation)
	{
		return Objects.nonNull(availabilityMap.get(accommodation)) ? availabilityMap.get(accommodation) : 0;
	}


	protected List<String> getAccommodations(final String line)
	{
		return Arrays.asList(line.split(SEPARATOR)[1].split(LIST_SEPARATOR));
	}

	protected String getAccommodationOffering(final String line)
	{
		return line.split(SEPARATOR)[0];
	}


	protected Integer getAvailability(final String line)
	{
		return Integer.parseInt(line.split(SEPARATOR)[1]);
	}

	protected String getAccommodation(final String line)
	{
		return line.split(SEPARATOR)[0];
	}

	/**
	 * This method creates a stock level entry for each room rate product belonging to a rate plan associated with any
	 * accommodations that belongs to the accommodation offering, only if SET_FORCE_IN_STOCK is set to true. In that case
	 * the stock level status will be set accordingly.
	 *
	 * @param accommodationOffering
	 *           the accommodation offering where stock will be created
	 * @param accommodations
	 *           the list of products a stock level will be created for
	 * @param stockLevelsToSave
	 */
	protected void setStockLevelForRoomRates(final AccommodationOfferingModel accommodationOffering,
			final List<AccommodationModel> accommodations, final List<StockLevelModel> stockLevelsToSave)
	{
		if (Objects.nonNull(isSetForceInStock()) ? isSetForceInStock() : Boolean.TRUE)
		{
			final List<String> roomRateCodes = accommodations.stream().flatMap(accommodation -> accommodation.getRatePlan().stream())
					.flatMap(plan -> plan.getProducts().stream()).map(ProductModel::getCode).collect(Collectors.toList());

			roomRateCodes.stream().distinct().forEach(code -> {
				final StockLevelModel stockLevelModel = getModelService().create(StockLevelModel.class);
				stockLevelModel.setWarehouse(accommodationOffering);
				stockLevelModel.setProductCode(code);
				stockLevelModel.setAvailable(0);
				stockLevelModel.setInStockStatus(InStockStatus.FORCEINSTOCK);
				stockLevelsToSave.add(stockLevelModel);
			});
		}

	}

	/**
	 * @param accommodationOffering
	 *
	 * @param entry
	 *
	 * @param dateToSet
	 *
	 * @return the new stockLevelModel
	 */

	private StockLevelModel createStockLevel(final AccommodationOfferingModel accommodationOffering,
			final Entry<String, Integer> entry, final Date dateToSet)
	{
		final StockLevelModel stockLevelModel = getModelService().create(StockLevelModel.class);
		stockLevelModel.setWarehouse(accommodationOffering);
		stockLevelModel.setProductCode(entry.getKey());
		stockLevelModel.setAvailable(entry.getValue());
		stockLevelModel.setDate(dateToSet);
		return stockLevelModel;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
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
	 * @return the baseExtDir
	 */
	protected String getBaseExtDir()
	{
		return baseExtDir;
	}

	/**
	 * @param baseExtDir
	 *           the baseExtDir to set
	 */
	@Resource
	public void setBaseExtDir(final String baseExtDir)
	{
		this.baseExtDir = baseExtDir;
	}

	/**
	 *
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 *
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 *
	 * @return the catalogVersionService
	 */
	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 *
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * @return the scheduledDays
	 */
	protected Long getScheduledDays()
	{
		return scheduledDays;
	}

	/**
	 * @param scheduledDays
	 *           the scheduledDays to set
	 */
	@Required
	public void setScheduledDays(final Long scheduledDays)
	{
		this.scheduledDays = scheduledDays;
	}

	/**
	 * @return the setForceInStock
	 */
	protected Boolean isSetForceInStock()
	{
		return setForceInStock;
	}

	/**
	 * @param setForceInStock
	 *           the setForceInStock to set
	 */
	@Required
	public void setSetForceInStock(final Boolean setForceInStock)
	{
		this.setForceInStock = setForceInStock;
	}

}
