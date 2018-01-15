/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the conf/*
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
package de.hybris.platform.travelbackofficeservices.cronjob;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelbackofficeservices.model.cronjob.ManageTransportOfferingForScheduleConfigurationCronJobModel;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.vendor.TravelVendorService;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test class for {@link ManageTransportOfferingForScheduleConfigurationJob}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ManageTransportOfferingForScheduleConfigurationJobTest
{
	@InjectMocks
	private final ManageTransportOfferingForScheduleConfigurationJob manageTransportOfferingJob = new ManageTransportOfferingForScheduleConfigurationJob();

	@Mock
	private ModelService modelService;

	@Mock
	private TravelVendorService travelVendorService;

	@Mock
	private VendorModel vendorModel;

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private ManageTransportOfferingForScheduleConfigurationCronJobModel scheduleConfigurationCronJobModel;

	@Mock
	private BackofficeTransportOfferingService backofficeTransportOfferingService;

	@Before
	public void setUp()
	{
		Mockito.when(modelService.create(TransportOfferingModel.class)).thenReturn(new TransportOfferingModel());
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.when(travelVendorService.getVendorByCode("")).thenReturn(vendorModel);
	}

	@Test
	public void testPerformWhenEndDateGreaterThanStartDate() throws ParseException
	{
		final Calendar cal = Calendar.getInstance();
		final Date startDate = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, 42);
		final ScheduleConfigurationModel scheduleConfigurationModel = createScheduleConfigurationModel(startDate, cal.getTime());
		Mockito.when(scheduleConfigurationCronJobModel.getScheduleConfiguration()).thenReturn(scheduleConfigurationModel);
		manageTransportOfferingJob.perform(scheduleConfigurationCronJobModel);
		Assert.assertTrue(scheduleConfigurationModel.getTransportOfferings().size() > 1);
	}

	@Test
	public void testPerformWhenStartDateEqualsEndDate() throws ParseException
	{
		final Calendar cal = Calendar.getInstance();
		final ScheduleConfigurationModel scheduleConfigurationModel = createScheduleConfigurationModel(cal.getTime(),
				cal.getTime());
		scheduleConfigurationCronJobModel.setScheduleConfiguration(scheduleConfigurationModel);
		Mockito.when(scheduleConfigurationCronJobModel.getScheduleConfiguration()).thenReturn(scheduleConfigurationModel);
		manageTransportOfferingJob.perform(scheduleConfigurationCronJobModel);
		Assert.assertTrue(scheduleConfigurationModel.getTransportOfferings().size() == 1);
	}

	@Test
	public void testPerformWhenEndDateLessThanStartDate() throws ParseException
	{
		final Calendar cal = Calendar.getInstance();
		final Date startDate = cal.getTime();
		cal.add(Calendar.DAY_OF_WEEK, -1);
		final ScheduleConfigurationModel scheduleConfigurationModel = createScheduleConfigurationModel(startDate, cal.getTime());
		scheduleConfigurationCronJobModel.setScheduleConfiguration(scheduleConfigurationModel);
		Mockito.when(scheduleConfigurationCronJobModel.getScheduleConfiguration()).thenReturn(scheduleConfigurationModel);
		manageTransportOfferingJob.perform(scheduleConfigurationCronJobModel);
		Assert.assertNull(scheduleConfigurationModel.getTransportOfferings());
	}

	private ScheduleConfigurationModel createScheduleConfigurationModel(final Date startDate, final Date endDate)
			throws ParseException
	{
		final ScheduleConfigurationModel scheduleConfigurationModel = new ScheduleConfigurationModel();
		final List<ScheduleConfigurationDayModel> scheduleConfigurationDayModels = new ArrayList<>();
		scheduleConfigurationModel.setScheduleConfigurationDays(scheduleConfigurationDayModels);
		final TravelProviderModel travelProvider = new TravelProviderModel();
		travelProvider.setCode("HY");
		scheduleConfigurationModel.setNumber("9518");
		scheduleConfigurationModel.setTravelProvider(travelProvider);
		scheduleConfigurationModel.setStartDate(startDate);
		scheduleConfigurationModel.setEndDate(endDate);
		final TravelSectorModel travelSector = new TravelSectorModel();
		final TransportFacilityModel origin = new TransportFacilityModel();
		final List<PointOfServiceModel> pointOfServices = new ArrayList<>();
		final PointOfServiceModel pointOfService = new PointOfServiceModel();
		pointOfService.setTimeZoneId("Europe/London");
		pointOfServices.add(pointOfService);
		origin.setPointOfService(pointOfServices);

		final TransportFacilityModel destination = new TransportFacilityModel();
		destination.setPointOfService(pointOfServices);

		travelSector.setOrigin(origin);
		travelSector.setDestination(destination);
		scheduleConfigurationModel.setTravelSector(travelSector);


		final LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (localEndDate.isAfter(localStartDate))
		{
			//Create scheduleConfigurationDayModel for every Monday between start and end date.
			scheduleConfigurationDayModels.addAll(Stream.iterate(localStartDate, date -> date.plusDays(7))
					.limit(ChronoUnit.DAYS.between(localStartDate, localEndDate) / 7)
					.map(date -> createScheduleConfigurationDayModels(date, 3, 15, 6, 15, DayOfWeek.MONDAY))
					.collect(Collectors.toList()));

			//Create scheduleConfigurationDayModel for every Wednesday between start and end date.
			scheduleConfigurationDayModels.addAll(Stream.iterate(localStartDate, date -> date.plusDays(7))
					.limit(ChronoUnit.DAYS.between(localStartDate, localEndDate) / 7)
					.map(date -> createScheduleConfigurationDayModels(date, 9, 15, 12, 15, DayOfWeek.WEDNESDAY))
					.collect(Collectors.toList()));

			//Create scheduleConfigurationDayModel for every Friday between start and end date.
			scheduleConfigurationDayModels.addAll(Stream.iterate(localStartDate, date -> date.plusDays(7))
					.limit(ChronoUnit.DAYS.between(localStartDate, localEndDate) / 7)
					.map(date -> createScheduleConfigurationDayModels(date, 15, 15, 18, 15, DayOfWeek.FRIDAY))
					.collect(Collectors.toList()));
		}
		else if (localEndDate.isEqual(localStartDate))
		{
			scheduleConfigurationDayModels.add(createScheduleConfigurationDayModels(localStartDate, 3, 15, 6, 15,
					DayOfWeek.values()[(localStartDate.getDayOfWeek().getValue() % 7)]));
		}
		return scheduleConfigurationModel;
	}

	protected ScheduleConfigurationDayModel createScheduleConfigurationDayModels(final LocalDate date, final int depHours,
			final int depMins, final int durationHrs, final int durationMins, final DayOfWeek dayOfWeek)
	{
		final ScheduleConfigurationDayModel scheduleConfigurationDayModel = new ScheduleConfigurationDayModel();
		scheduleConfigurationDayModel.setDayOfWeek(dayOfWeek);
		final Date depDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(depDate);
		calendar.set(Calendar.HOUR, depHours);
		calendar.set(Calendar.MINUTE, depMins);
		scheduleConfigurationDayModel.setDepartureTime(calendar.getTime());
		return scheduleConfigurationDayModel;
	}

}
