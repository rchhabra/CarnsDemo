/*
 *
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
package de.hybris.platform.travelservices.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.cronjob.UpdateStockLevelsToTransportOfferingCronJobModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A Cron job to set-up StockLevels to TransportOfferings.
 */
public class UpdateStockLevelsToTransportOfferingJob
		extends AbstractJobPerformable<UpdateStockLevelsToTransportOfferingCronJobModel>
{

	private static final Logger LOG = Logger.getLogger(UpdateStockLevelsToTransportOfferingJob.class);

	private TransportOfferingService transportOfferingService;

	/**
	 * This method calls a DAO to get all configured TransportOfferings. For each TransportOffering check if the
	 * travelSector is domestic, if yes, set economy stockLevel, else(i.e international sector) set economy, economyplus
	 * & business stockLevels.
	 *
	 * @param updateStockLevelsToTransportOfferingCronJobModel
	 *           CronJob model with stockLevel data.
	 */
	@Override
	public PerformResult perform(
			final UpdateStockLevelsToTransportOfferingCronJobModel updateStockLevelsToTransportOfferingCronJobModel)
	{
		LOG.info("Performing Cronjob -> UpdateStockLevelsToTransportOfferingCronJob....");

		for (final TransportOfferingModel transportOffering : getTransportOfferingService().getTransportOfferings())
		{
			if (transportOffering.getActive())
			{
				final List<String> transportFacilities = new ArrayList<>();
				final Optional<TravelSectorModel> travelSectorModel = Optional.ofNullable(transportOffering.getTravelSector());
				final Optional<String> origin = travelSectorModel.map(TravelSectorModel::getOrigin)
						.map(TransportFacilityModel::getCode);
				final Optional<String> destination = travelSectorModel.map(TravelSectorModel::getDestination)
						.map(TransportFacilityModel::getCode);

				origin.ifPresent(originCode -> destination.ifPresent(destinationCode -> {
					transportFacilities.add(originCode);
					transportFacilities.add(destinationCode);
					transportFacilities.add(origin.get());
					setupEconomyAndAncillaryStockLevels(updateStockLevelsToTransportOfferingCronJobModel, transportOffering);

					if (!updateStockLevelsToTransportOfferingCronJobModel.getDomesticAirports().containsAll(transportFacilities))
					{
						// Its an Internation sector, so set-up economyPlus and BusinessStock as well.
						setupEcoPlusAndBusinessStockLevels(updateStockLevelsToTransportOfferingCronJobModel, transportOffering);
					}
					getModelService().save(transportOffering);
				}));
			}
		}

		LOG.info("Cronjob -> UpdateStockLevelsToTransportOfferingCronJob Completed");

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected void setupEcoPlusAndBusinessStockLevels(
			final UpdateStockLevelsToTransportOfferingCronJobModel updateStockLevelsToTransportOfferingCronJobModel,
			final TransportOfferingModel transportOffering)
	{
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getEconomyPlusStockLevels());
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getEconomyPlusAncillaryStockLevels());
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getBusinessStockLevel());
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getBusinessAncillaryStockLevel());
	}

	protected void setupEconomyAndAncillaryStockLevels(
			final UpdateStockLevelsToTransportOfferingCronJobModel updateStockLevelsToTransportOfferingCronJobModel,
			final TransportOfferingModel transportOffering)
	{
		// For all sectors, set-up economy stock
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getEconomyStockLevels());
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getEconomyAncillaryStockLevels());
		// setup ancillary stock
		setupStockLevel(transportOffering, updateStockLevelsToTransportOfferingCronJobModel.getAncillaryStockLevel());
	}

	/**
	 * For each Stock entry check if the product code exists, if yes, update(or reset) availability, else create a new
	 * stockLevel and assign availability
	 *
	 * @param transportOffering
	 *           TransportOfferingModel to which the stockLevel has to be updated.
	 * @param stockMap
	 *           Fare Booking Class / Product Code and stockLevel.
	 */
	protected void setupStockLevel(final TransportOfferingModel transportOffering, final Map<String, Integer> stockMap)
	{
		stockMap.entrySet().forEach(entry -> updateTransportofferingWithStockLevel(transportOffering, entry));
	}

	private void updateTransportofferingWithStockLevel(final TransportOfferingModel transportOffering,
			final Entry<String, Integer> entry)
	{
		final boolean isStockCodePresent = transportOffering.getStockLevels() != null
				&& !transportOffering.getStockLevels().isEmpty()
				&& transportOffering.getStockLevels().stream().anyMatch(stockLevel -> {
					if (stockLevel.getProductCode().equals(entry.getKey()))
					{
						stockLevel.setAvailable(entry.getValue());
						getModelService().save(stockLevel);
						return true;
					}
					return false;
				});

		if (!isStockCodePresent)
		{
			final StockLevelModel stockLevelModel = getModelService().create(StockLevelModel.class);
			stockLevelModel.setWarehouse(transportOffering);
			stockLevelModel.setProductCode(entry.getKey());
			stockLevelModel.setAvailable(entry.getValue());
			getModelService().save(stockLevelModel);
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}
}
