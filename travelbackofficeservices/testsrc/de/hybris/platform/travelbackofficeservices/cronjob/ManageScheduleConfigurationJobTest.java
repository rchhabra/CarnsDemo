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
 */

package de.hybris.platform.travelbackofficeservices.cronjob;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackofficeservices.services.BackofficeScheduleConfigurationService;
import de.hybris.platform.travelbackofficeservices.services.BackofficeTransportOfferingService;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TerminalModel;
import de.hybris.platform.travelservices.model.travel.TransportVehicleModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Unit test for {@link ManageScheduleConfigurationsJob}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ManageScheduleConfigurationJobTest
{
	@InjectMocks
	private final ManageScheduleConfigurationsJob manageScheduleConfigurationsJob = new ManageScheduleConfigurationsJob();

	@Mock
	private CronJobModel cronJobModel;

	@Mock
	private BackofficeTransportOfferingService backofficeTransportOfferingService;

	@Mock
	private BackofficeScheduleConfigurationService backofficeScheduleConfigurationService;

	@Mock
	private ModelService modelService;

	private TransportOfferingModel transportOffering;

	private ScheduleConfigurationModel scheduleConfiguration;

	private ScheduleConfigurationDayModel scheduleConfigurationDay;


	@Before
	public void setUp()
	{
		Mockito.when(modelService.create(ScheduleConfigurationModel.class)).thenReturn(new ScheduleConfigurationModel());
		Mockito.when(modelService.create(ScheduleConfigurationDayModel.class)).thenReturn(new ScheduleConfigurationDayModel());
		Mockito.doNothing().when(modelService).save(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());
	}

	@Test
	public void testWithNoSchedule()
	{
		Mockito.when(backofficeScheduleConfigurationService.getAllScheduleConfigurations()).thenReturn(Collections.emptyList());
		transportOffering = createTransportOffering();
		Mockito.when(backofficeTransportOfferingService.findTransportOfferingsWithoutSchedule()).thenReturn(Collections.singletonList(transportOffering));
		manageScheduleConfigurationsJob.perform(cronJobModel);
		// entering code to create a new Schedule
		verify(transportOffering, times(1)).getNumber();
	}

	@Test
	public void testWithMatchingSchedule()
	{
		scheduleConfiguration = createSchedule(true);
		scheduleConfigurationDay = createScheduleConfigurationDay(true);
		scheduleConfiguration.setScheduleConfigurationDays(Arrays.asList(scheduleConfigurationDay));
		Mockito.when(backofficeScheduleConfigurationService.getAllScheduleConfigurations()).thenReturn(Arrays.asList(scheduleConfiguration));
		transportOffering = createTransportOffering();
		Mockito.when(backofficeTransportOfferingService.findTransportOfferingsWithoutSchedule()).thenReturn(Collections.singletonList(transportOffering));

		final TravelProviderModel travelProvider = createTravelProvider();
		Mockito.when(scheduleConfiguration.getTravelProvider()).thenReturn(travelProvider);
		Mockito.when(transportOffering.getTravelProvider()).thenReturn(travelProvider);

		final TravelSectorModel travelSector = createTravelSector();
		Mockito.when(scheduleConfiguration.getTravelSector()).thenReturn(travelSector);
		Mockito.when(transportOffering.getTravelSector()).thenReturn(travelSector);

		manageScheduleConfigurationsJob.perform(cronJobModel);
		verify(scheduleConfiguration,times(1)).setTransportOfferings(Matchers.any());
	}

	@Test
	public void testWithNoMatchingSchedule()
	{
		scheduleConfiguration = createSchedule(false);
		scheduleConfigurationDay = createScheduleConfigurationDay(true);
		scheduleConfiguration.setScheduleConfigurationDays(Arrays.asList(scheduleConfigurationDay));
		Mockito.when(backofficeScheduleConfigurationService.getAllScheduleConfigurations()).thenReturn(Arrays.asList(scheduleConfiguration));
		transportOffering = createTransportOffering();
		Mockito.when(backofficeTransportOfferingService.findTransportOfferingsWithoutSchedule()).thenReturn(Collections.singletonList(transportOffering));

		final TravelProviderModel travelProvider = createTravelProvider();
		Mockito.when(scheduleConfiguration.getTravelProvider()).thenReturn(travelProvider);
		Mockito.when(transportOffering.getTravelProvider()).thenReturn(travelProvider);

		final TravelSectorModel travelSector = createTravelSector();
		Mockito.when(scheduleConfiguration.getTravelSector()).thenReturn(travelSector);
		Mockito.when(transportOffering.getTravelSector()).thenReturn(travelSector);

		manageScheduleConfigurationsJob.perform(cronJobModel);
		// entering code to create a new Schedule, after checking the number
		verify(transportOffering, times(2)).getNumber();
		verify(scheduleConfiguration,times(1)).setTransportOfferings(Matchers.any());
	}

	@Test
	public void testWithMatchingScheduleNoMatchingDay()
	{
		scheduleConfiguration = createSchedule(true);
		scheduleConfigurationDay = createScheduleConfigurationDay(false);
		scheduleConfiguration.setScheduleConfigurationDays(Arrays.asList(scheduleConfigurationDay));
		Mockito.when(backofficeScheduleConfigurationService.getAllScheduleConfigurations()).thenReturn(Arrays.asList(scheduleConfiguration));
		transportOffering = createTransportOffering();
		Mockito.when(backofficeTransportOfferingService.findTransportOfferingsWithoutSchedule()).thenReturn(Collections.singletonList(transportOffering));

		final TravelProviderModel travelProvider = createTravelProvider();
		Mockito.when(scheduleConfiguration.getTravelProvider()).thenReturn(travelProvider);
		Mockito.when(transportOffering.getTravelProvider()).thenReturn(travelProvider);

		final TravelSectorModel travelSector = createTravelSector();
		Mockito.when(scheduleConfiguration.getTravelSector()).thenReturn(travelSector);
		Mockito.when(transportOffering.getTravelSector()).thenReturn(travelSector);

		manageScheduleConfigurationsJob.perform(cronJobModel);
		//association plus creating day
		verify(scheduleConfiguration,times(1)).setTransportOfferings(Matchers.any());
		verify(scheduleConfiguration,times(2)).setScheduleConfigurationDays(Matchers.any());

	}

	protected TransportOfferingModel createTransportOffering()
	{
		final TransportOfferingModel transportOfferingModelMock = mock(TransportOfferingModel.class);

		given(transportOfferingModelMock.getTravelSector()).willReturn(createTravelSector());

		given(transportOfferingModelMock.getDuration()).willReturn(3600000L);

		given(transportOfferingModelMock.getTransportVehicle()).willReturn(createTransportVehicle());

		given(transportOfferingModelMock.getDepartureTime()).willReturn(new Date());

		given(transportOfferingModelMock.getNumber()).willReturn("9999");

		given(transportOfferingModelMock.getOriginTerminal()).willReturn(createTerminal());

		given(transportOfferingModelMock.getDestinationTerminal()).willReturn(createTerminal());

		given(transportOfferingModelMock.getTravelProvider()).willReturn(createTravelProvider());

		return transportOfferingModelMock;

	}

	protected ScheduleConfigurationModel createSchedule(final boolean validSchedule)
	{
		final ScheduleConfigurationModel scheduleConfiguration = mock(ScheduleConfigurationModel.class);
		final Date startDate = TravelDateUtils.addDays(new Date(), -3);
		given(scheduleConfiguration.getStartDate()).willReturn(startDate);
		given(scheduleConfiguration.getEndDate()).willReturn(TravelDateUtils.addDays(new Date(), 3));
		given(scheduleConfiguration.getTravelProvider()).willReturn(createTravelProvider());
		given(scheduleConfiguration.getTravelSector()).willReturn(createTravelSector());
		given(scheduleConfiguration.getNumber()).willReturn(validSchedule? "9999" : "6666");
		return scheduleConfiguration;
	}

	protected ScheduleConfigurationDayModel createScheduleConfigurationDay(final boolean validDay)
	{
		final ScheduleConfigurationDayModel scheduleConfigurationDay = mock(ScheduleConfigurationDayModel.class);
		String dayOfWeek;
		if(validDay)
		{
			dayOfWeek = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().name();
		}
		else
		{
			dayOfWeek = TravelDateUtils.addDays(new Date(),1).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().name();
		}
		given(scheduleConfigurationDay.getDayOfWeek()).willReturn(DayOfWeek.valueOf(dayOfWeek));
		given(scheduleConfigurationDay.getDepartureTime()).willReturn(new Date());
		given(scheduleConfigurationDay.getOriginTerminal()).willReturn(createTerminal());
		given(scheduleConfigurationDay.getDestinationTerminal()).willReturn(createTerminal());
		given(scheduleConfigurationDay.getTransportVehicle()).willReturn(createTransportVehicle());

		return scheduleConfigurationDay;
	}

	protected TerminalModel createTerminal()
	{
		final TerminalModel travelTerminalMock = mock(TerminalModel.class);
		return travelTerminalMock;
	}

	protected TravelProviderModel createTravelProvider()
	{
		final TravelProviderModel travelProviderMock = mock(TravelProviderModel.class);
		return travelProviderMock;
	}

	protected TravelSectorModel createTravelSector()
	{
		final TravelSectorModel travelSectorMock = mock(TravelSectorModel.class);
		return travelSectorMock;
	}

	protected TransportVehicleModel createTransportVehicle()
	{
		final TransportVehicleModel transportVehicleMock = mock(TransportVehicleModel.class);
		return transportVehicleMock;
	}

}
