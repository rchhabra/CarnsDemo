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

package de.hybris.platform.travelservices.storelocator;

import de.hybris.platform.commercefacades.storelocator.data.TimeZoneResponseData;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.GeocodingJob;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.location.impl.DistanceUnawareLocation;
import de.hybris.platform.storelocator.model.GeocodeAddressesCronJobModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.dao.TravelPointOfServiceDao;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.apache.log4j.Logger;


/**
 * The type Travel geocoding job.
 */
public class TravelGeocodingJob extends GeocodingJob
{

	private static final Logger LOG = Logger.getLogger(TravelGeocodingJob.class.getName());

	private PointOfServiceDao pointOfServiceDao;
	private GeoWebServiceWrapper geoServiceWrapper;

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		cronJob.setLogToDatabase(Boolean.TRUE);
		cronJob.setLogToFile(Boolean.TRUE);

		GPS gps;
		DistanceUnawareLocation location;
		TimeZoneResponseData timeZoneOffset;

		LOG.info("Performing Cronjob -> TravelGeocodingJob....");
		try
		{
			if (!(cronJob instanceof GeocodeAddressesCronJobModel))
			{
				LOG.error("Unexpected cronjob type: " + cronJob);
				return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
			}

			final GeocodeAddressesCronJobModel cronJobModel = (GeocodeAddressesCronJobModel) cronJob;

			if (cronJobModel.getInternalDelay() == 0)
			{
				LOG.warn("Internal delay should be greater than 0");
			}

			final TravelGeoWebServiceWrapper travelGeoWebServiceWrapper = (TravelGeoWebServiceWrapper) getGeoServiceWrapper();

			final Collection<PointOfServiceModel> posBatch = ((TravelPointOfServiceDao) getPointOfServiceDao())
					.getGeocodedPOS(cronJobModel.getBatchSize());
			for (final PointOfServiceModel pos : posBatch)
			{
				if (canPosBeSkipped(pos))
				{
					pos.setGeocodeTimestamp(getCurrentDateForPos(pos));
					modelService.save(pos);
				}
				else if (isPosToBeUpdated(pos))
				{
					location = new DistanceUnawareLocation(pos);
					try
					{
						gps = travelGeoWebServiceWrapper.geocodeAddress(location);
						pos.setLatitude(Double.valueOf(gps.getDecimalLatitude()));
						pos.setLongitude(Double.valueOf(gps.getDecimalLongitude()));
						timeZoneOffset = travelGeoWebServiceWrapper.timeZoneOffset(new DistanceUnawareLocation(pos));
						pos.setTimeZoneId(Objects.nonNull(timeZoneOffset.getTimeZoneId()) ? timeZoneOffset.getTimeZoneId()
								: ZoneOffset.UTC.toString());
						pos.setGeocodeTimestamp(getCurrentDateForPos(pos));
						modelService.save(pos);
					}
					catch (final GeoServiceWrapperException e)
					{
						buildErrorMessage(pos, location, e);
					}
				}
			}

			if (clearAbortRequestedIfNeeded(cronJob))
			{
				return new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.ABORTED);
			}

			try
			{
				Thread.sleep(((long) cronJobModel.getInternalDelay()) * 1000);
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt();
				LOG.error("CronJob: " + cronJob.getCode() + " interrupted: " + e.getMessage());
				return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
			}

		}
		catch (final Exception ex)
		{
			LOG.error("Unexpected error", ex);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		LOG.info("Cronjob -> TravelGeocodingJob Completed");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	private boolean canPosBeSkipped(final PointOfServiceModel pos)
	{
		return Objects.nonNull(pos.getLatitude()) && Objects.nonNull(pos.getLongitude())
				&& Objects.isNull(pos.getGeocodeTimestamp()) && Objects.nonNull(pos.getTimeZoneId());
	}

	private boolean isPosToBeUpdated(final PointOfServiceModel pos)
	{
		if (Objects.isNull(pos.getGeocodeTimestamp()) || Objects.isNull(pos.getAddress().getModifiedtime()))
		{
			return true;
		}
		final ZoneId posZoneId = Objects.nonNull(pos.getTimeZoneId()) ? ZoneId.of(pos.getTimeZoneId())
				: ZoneId.of(ZoneOffset.UTC.toString());
		return TravelDateUtils.isBefore(pos.getGeocodeTimestamp(), posZoneId, pos.getAddress().getModifiedtime(), posZoneId);
	}

	private Date getCurrentDateForPos(final PointOfServiceModel pos)
	{
		return Date.from(LocalDateTime.now().atZone(ZoneId.of(pos.getTimeZoneId())).toInstant());
	}

	/**
	 * Gets point of service dao.
	 *
	 * @return the point of service dao
	 */
	protected PointOfServiceDao getPointOfServiceDao()
	{
		return pointOfServiceDao;
	}

	@Override
	public void setPointOfServiceDao(final PointOfServiceDao pointOfServiceDao)
	{
		super.setPointOfServiceDao(pointOfServiceDao);
		this.pointOfServiceDao = pointOfServiceDao;
	}

	/**
	 * Gets geo service wrapper.
	 *
	 * @return the geo service wrapper
	 */
	protected GeoWebServiceWrapper getGeoServiceWrapper()
	{
		return geoServiceWrapper;
	}

	@Override
	public void setGeoServiceWrapper(final GeoWebServiceWrapper geoServiceWrapper)
	{
		super.setGeoServiceWrapper(geoServiceWrapper);
		this.geoServiceWrapper = geoServiceWrapper;
	}


}
