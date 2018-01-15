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

package de.hybris.platform.travelservices.storelocator;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.TimeZoneResponseData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.impl.DistanceUnawareLocation;
import de.hybris.platform.storelocator.model.GeocodeAddressesCronJobModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.dao.TravelPointOfServiceDao;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link TravelGeocodingJob}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TravelGeocodingJobTest
{
	TravelGeocodingJob travelGeocodingJob = new TravelGeocodingJob()
	{
		protected final boolean clearAbortRequestedIfNeeded(final T myCronJob)
		{
			return Boolean.FALSE;
		}
	};

	@Mock
	private TravelPointOfServiceDao pointOfServiceDao;

	@Mock
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private TravelGeoWebServiceWrapper travelGeoWebServiceWrapper;

	@Mock
	GPS gps;

	@Mock
	ModelService modelService;

	@Before
	public void setUp()
	{
		travelGeocodingJob.setModelService(modelService);
		Mockito.doNothing().when(modelService).save(Matchers.any(PointOfServiceModel.class));
	}

	@Test()
	public void testForCronJobModel()
	{
		final CronJobModel cronJob = new CronJobModel();

		Assert.assertEquals(CronJobResult.FAILURE, travelGeocodingJob.perform(cronJob).getResult());
		Assert.assertEquals(CronJobStatus.ABORTED, travelGeocodingJob.perform(cronJob).getStatus());
	}

	@Test()
	public void testForGeocodeAddressesCronJobModel()
	{
		final GeocodeAddressesCronJobModel cronJob = new GeocodeAddressesCronJobModel();
		cronJob.setInternalDelay(1);
		cronJob.setBatchSize(10);

		final PointOfServiceModel pointOfService1 = new PointOfServiceModel();
		pointOfService1.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService2 = new PointOfServiceModel();
		pointOfService2.setLatitude(80d);
		pointOfService2.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService3 = new PointOfServiceModel();
		pointOfService3.setLatitude(80d);
		pointOfService3.setLongitude(80d);
		pointOfService3.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService4 = new PointOfServiceModel();
		pointOfService4.setLatitude(80d);
		pointOfService4.setLongitude(80d);
		pointOfService4.setGeocodeTimestamp(new Date());
		pointOfService4.setAddress(new AddressModel());

		final PointOfServiceModel pointOfService5 = new PointOfServiceModel();
		pointOfService5.setLatitude(80d);
		pointOfService5.setLongitude(80d);
		pointOfService5.setTimeZoneId(ZoneId.systemDefault().toString());
		final AddressModel address = new AddressModel();
		address.setModifiedtime(new Date());
		pointOfService5.setAddress(address);

		final List<PointOfServiceModel> posBatch = new ArrayList<>();
		posBatch.add(pointOfService1);
		posBatch.add(pointOfService2);
		posBatch.add(pointOfService3);
		posBatch.add(pointOfService4);
		posBatch.add(pointOfService5);
		when(pointOfServiceDao.getGeocodedPOS(Matchers.anyInt()))
				.thenReturn(posBatch);
		travelGeocodingJob.setGeoServiceWrapper(travelGeoWebServiceWrapper);
		travelGeocodingJob.setPointOfServiceDao(pointOfServiceDao);
		when(travelGeoWebServiceWrapper.geocodeAddress(Matchers.any(Location.class))).thenReturn(gps);

		final TimeZoneResponseData timeZoneResponseData = new TimeZoneResponseData();
		timeZoneResponseData.setTimeZoneId(ZoneId.systemDefault().toString());
		when(travelGeoWebServiceWrapper.timeZoneOffset(Matchers.any(DistanceUnawareLocation.class)))
				.thenReturn(timeZoneResponseData);
		Assert.assertEquals(CronJobResult.SUCCESS, travelGeocodingJob.perform(cronJob).getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, travelGeocodingJob.perform(cronJob).getStatus());
	}

	@Test()
	public void testForGeocodeAddressesCronJobModelForGeoServiceWrapperException()
	{
		final GeocodeAddressesCronJobModel cronJob = new GeocodeAddressesCronJobModel();
		cronJob.setInternalDelay(1);
		cronJob.setBatchSize(10);

		final PointOfServiceModel pointOfService1 = new PointOfServiceModel();
		pointOfService1.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService2 = new PointOfServiceModel();
		pointOfService2.setLatitude(80d);
		pointOfService2.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService3 = new PointOfServiceModel();
		pointOfService3.setLatitude(80d);
		pointOfService3.setLongitude(80d);
		pointOfService3.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService4 = new PointOfServiceModel();
		pointOfService4.setLatitude(80d);
		pointOfService4.setLongitude(80d);
		pointOfService4.setGeocodeTimestamp(new Date());
		pointOfService4.setAddress(new AddressModel());

		final PointOfServiceModel pointOfService5 = new PointOfServiceModel();
		pointOfService5.setLatitude(80d);
		pointOfService5.setLongitude(80d);
		pointOfService5.setGeocodeTimestamp(new Date());
		pointOfService5.setTimeZoneId("TEST_TIME_ZONE_ID");
		final AddressModel address = new AddressModel();
		address.setModifiedtime(new Date());
		pointOfService5.setAddress(address);

		when(pointOfServiceDao.getGeocodedPOS(Matchers.anyInt()))
				.thenReturn(Stream.of(pointOfService1, pointOfService2, pointOfService3, pointOfService4, pointOfService5)
						.collect(Collectors.toList()));
		travelGeocodingJob.setGeoServiceWrapper(travelGeoWebServiceWrapper);
		travelGeocodingJob.setPointOfServiceDao(pointOfServiceDao);
		when(travelGeoWebServiceWrapper.geocodeAddress(Matchers.any(Location.class))).thenReturn(gps);

		final TimeZoneResponseData timeZoneResponseData = new TimeZoneResponseData();
		timeZoneResponseData.setTimeZoneId(ZoneId.systemDefault().toString());
		when(travelGeoWebServiceWrapper.timeZoneOffset(Matchers.any(DistanceUnawareLocation.class)))
				.thenReturn(timeZoneResponseData);
		Mockito.doNothing().when(modelService).save(Matchers.any(Object.class));

		final TravelGeocodingJob travelGeocodingJob = new TravelGeocodingJob()
		{
			protected final boolean clearAbortRequestedIfNeeded(final T myCronJob)
			{
				return Boolean.TRUE;
			}

			@Override
			protected String buildErrorMessage(final PointOfServiceModel pos, final DistanceUnawareLocation location,
					final GeoServiceWrapperException geoServiceWrapperException)
			{
				return "EXCEPTION";
			}
		};
		Assert.assertEquals(CronJobResult.ERROR, travelGeocodingJob.perform(cronJob).getResult());
		Assert.assertEquals(CronJobStatus.ABORTED, travelGeocodingJob.perform(cronJob).getStatus());
	}

	@Test()
	public void testForGeocodeAddressesCronJobModelForUnknownException()
	{
		final GeocodeAddressesCronJobModel cronJob = new GeocodeAddressesCronJobModel();
		cronJob.setInternalDelay(1);
		cronJob.setBatchSize(10);

		final PointOfServiceModel pointOfService1 = new PointOfServiceModel();
		pointOfService1.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService2 = new PointOfServiceModel();
		pointOfService2.setLatitude(80d);
		pointOfService2.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService3 = new PointOfServiceModel();
		pointOfService3.setLatitude(80d);
		pointOfService3.setLongitude(80d);
		pointOfService3.setAddress(new AddressModel());
		final PointOfServiceModel pointOfService4 = new PointOfServiceModel();
		pointOfService4.setLatitude(80d);
		pointOfService4.setLongitude(80d);
		pointOfService4.setGeocodeTimestamp(new Date());
		pointOfService4.setAddress(new AddressModel());

		final PointOfServiceModel pointOfService5 = new PointOfServiceModel();
		pointOfService5.setLatitude(80d);
		pointOfService5.setLongitude(80d);
		pointOfService5.setGeocodeTimestamp(new Date());
		pointOfService5.setTimeZoneId("TEST_TIME_ZONE_ID");
		final AddressModel address = new AddressModel();
		address.setModifiedtime(new Date());
		pointOfService5.setAddress(address);

		when(pointOfServiceDao.getGeocodedPOS(Matchers.anyInt()))
				.thenReturn(Stream.of(pointOfService1, pointOfService2, pointOfService3).collect(Collectors.toList()));
		travelGeocodingJob.setGeoServiceWrapper(geoServiceWrapper);
		travelGeocodingJob.setPointOfServiceDao(pointOfServiceDao);
		when(travelGeoWebServiceWrapper.geocodeAddress(Matchers.any(Location.class))).thenReturn(gps);

		final TimeZoneResponseData timeZoneResponseData = new TimeZoneResponseData();
		timeZoneResponseData.setTimeZoneId(ZoneId.systemDefault().toString());
		when(travelGeoWebServiceWrapper.timeZoneOffset(Matchers.any(DistanceUnawareLocation.class)))
				.thenReturn(timeZoneResponseData);
		Mockito.doNothing().when(modelService).save(Matchers.any(Object.class));

		final TravelGeocodingJob travelGeocodingJob = new TravelGeocodingJob()
		{
			protected final boolean clearAbortRequestedIfNeeded(final T myCronJob)
			{
				return Boolean.TRUE;
			}

			@Override
			protected String buildErrorMessage(final PointOfServiceModel pos, final DistanceUnawareLocation location,
					final GeoServiceWrapperException geoServiceWrapperException)
			{
				return "EXCEPTION";
			}
		};
		Assert.assertEquals(CronJobResult.ERROR, travelGeocodingJob.perform(cronJob).getResult());
		Assert.assertEquals(CronJobStatus.ABORTED, travelGeocodingJob.perform(cronJob).getStatus());
	}

}
