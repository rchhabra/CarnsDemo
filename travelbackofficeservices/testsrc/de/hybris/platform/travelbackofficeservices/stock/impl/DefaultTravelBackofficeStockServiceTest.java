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

package de.hybris.platform.travelbackofficeservices.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelservices.dao.TravelStockLevelDao;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * The type Default travel backoffice stock service test.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelBackofficeStockServiceTest
{

	@InjectMocks
	private DefaultTravelBackofficeStockService travelStockService;

	@Mock
	private TravelStockLevelDao travelStockLevelDao;

	@Mock
	private ModelService modelService;

	/**
	 * Test create stock levels for transport offerings.
	 */
	@Test
	public void testCreateStockLevelsForTransportOfferings()
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stock);
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class))).thenReturn(null);
		travelStockService.createStockLevels(createManageStockLevel(), Arrays.asList(transportOffering));

		Mockito.verify(modelService, Mockito.times(1)).save(stock);
	}

	/**
	 * Test create stock levels for transport offerings for null list.
	 */
	@Test
	public void testCreateStockLevelsForTransportOfferingsForNullList()
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stock);

		travelStockService.createStockLevels(null, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		final ManageStockLevelInfo manageStockData = createManageStockLevel();
		manageStockData.setStockLevelAttributes(Collections.emptyList());
		travelStockService.createStockLevels(manageStockData, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		travelStockService.createStockLevels(manageStockData, Collections.emptyList());
		Mockito.verify(modelService, Mockito.times(0)).save(stock);
	}

	/**
	 * Test create stock levels for travel sector.
	 */
	@Test
	public void testCreateStockLevelsForTravelSector()
	{
		final TravelSectorModel sector = new TravelSectorModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		sector.setTransportOffering(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stock);
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class))).thenReturn(null);
		travelStockService.createStockLevels(createManageStockLevel(), sector);

		Mockito.verify(modelService, Mockito.times(1)).save(stock);
	}

	/**
	 * Test create stock levels for travel sector for null.
	 */
	@Test
	public void testCreateStockLevelsForTravelSectorForNull()
	{
		final TravelSectorModel sector = new TravelSectorModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		sector.setTransportOffering(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stock);
		travelStockService.createStockLevels(null, sector);

		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		travelStockService.createStockLevels(null, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		final ManageStockLevelInfo manageStockData = createManageStockLevel();
		manageStockData.setStockLevelAttributes(Collections.emptyList());
		travelStockService.createStockLevels(manageStockData, sector);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		sector.setTransportOffering(Collections.emptyList());
		travelStockService.createStockLevels(manageStockData, sector);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);
	}

	/**
	 * Test create stock levels for schedule configuration model.
	 */
	@Test
	public void testCreateStockLevelsForScheduleConfigurationModel()
	{
		final ScheduleConfigurationModel schedule = new ScheduleConfigurationModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		schedule.setTransportOfferings(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stock);
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class))).thenReturn(null);
		travelStockService.createStockLevels(createManageStockLevel(), schedule);

		Mockito.verify(modelService, Mockito.times(1)).save(stock);
	}

	/**
	 * Test create stock levels for schedule configuration model for null.
	 */
	@Test
	public void testCreateStockLevelsForScheduleConfigurationModelForNull()
	{
		final ScheduleConfigurationModel schedule = new ScheduleConfigurationModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		schedule.setTransportOfferings(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stock);
		travelStockService.createStockLevels(null, schedule);

		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		travelStockService.createStockLevels(null, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		final ManageStockLevelInfo manageStockData = createManageStockLevel();
		manageStockData.setStockLevelAttributes(Collections.emptyList());
		travelStockService.createStockLevels(manageStockData, schedule);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		schedule.setTransportOfferings(Collections.emptyList());
		travelStockService.createStockLevels(manageStockData, schedule);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);
	}


	/**
	 * Test update stock levels for transport offerings.
	 */
	@Test
	public void testUpdateStockLevelsForTransportOfferings()
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class)))
				.thenReturn(stock);
		travelStockService.updateStockLevels(createManageStockLevel(), Arrays.asList(transportOffering));

		Mockito.verify(modelService, Mockito.times(1)).save(stock);
	}

	/**
	 * Test update stock levels for transport offerings for null list.
	 */
	@Test
	public void testUpdateStockLevelsForTransportOfferingsForNullList()
	{
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class)))
				.thenReturn(stock);

		travelStockService.updateStockLevels(null, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		final ManageStockLevelInfo manageStockData = createManageStockLevel();
		manageStockData.setStockLevelAttributes(Collections.emptyList());
		travelStockService.updateStockLevels(manageStockData, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		travelStockService.updateStockLevels(manageStockData, Collections.emptyList());
		Mockito.verify(modelService, Mockito.times(0)).save(stock);
	}

	/**
	 * Test update stock levels for travel sector.
	 */
	@Test
	public void testUpdateStockLevelsForTravelSector()
	{
		final TravelSectorModel sector = new TravelSectorModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		sector.setTransportOffering(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class)))
				.thenReturn(stock);
		travelStockService.updateStockLevels(createManageStockLevel(), sector);

		Mockito.verify(modelService, Mockito.times(1)).save(stock);
	}

	/**
	 * Test update stock levels for travel sector for null.
	 */
	@Test
	public void testUpdateStockLevelsForTravelSectorForNull()
	{
		final TravelSectorModel sector = new TravelSectorModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		sector.setTransportOffering(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class)))
				.thenReturn(stock);
		travelStockService.updateStockLevels(null, sector);

		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		travelStockService.updateStockLevels(null, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		final ManageStockLevelInfo manageStockData = createManageStockLevel();
		manageStockData.setStockLevelAttributes(Collections.emptyList());
		travelStockService.updateStockLevels(manageStockData, sector);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		sector.setTransportOffering(Collections.emptyList());
		travelStockService.updateStockLevels(manageStockData, sector);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);
	}

	/**
	 * Test update stock levels for schedule configuration model.
	 */
	@Test
	public void testUpdateStockLevelsForScheduleConfigurationModel()
	{
		final ScheduleConfigurationModel schedule = new ScheduleConfigurationModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		schedule.setTransportOfferings(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class)))
				.thenReturn(stock);
		travelStockService.updateStockLevels(createManageStockLevel(), schedule);

		Mockito.verify(modelService, Mockito.times(1)).save(stock);
	}

	/**
	 * Test update stock levels for schedule configuration model for null.
	 */
	@Test
	public void testUpdateStockLevelsForScheduleConfigurationModelForNull()
	{
		final ScheduleConfigurationModel schedule = new ScheduleConfigurationModel();
		final TransportOfferingModel transportOffering = new TransportOfferingModel()
		{
			@Override
			public String getName(final Locale loc)
			{
				return "HY2490";
			}
		};
		transportOffering.setCode("HY2490");

		schedule.setTransportOfferings(Arrays.asList(transportOffering));
		final StockLevelModel stock = new StockLevelModel();
		Mockito.when(travelStockLevelDao.findStockLevel(Matchers.anyString(), Matchers.any(WarehouseModel.class)))
				.thenReturn(stock);
		travelStockService.updateStockLevels(null, schedule);

		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		travelStockService.updateStockLevels(null, Arrays.asList(transportOffering));
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		final ManageStockLevelInfo manageStockData = createManageStockLevel();
		manageStockData.setStockLevelAttributes(Collections.emptyList());
		travelStockService.updateStockLevels(manageStockData, schedule);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);

		schedule.setTransportOfferings(Collections.emptyList());
		travelStockService.updateStockLevels(manageStockData, schedule);
		Mockito.verify(modelService, Mockito.times(0)).save(stock);
	}

	/**
	 * Create manage stock level manage stock level.
	 *
	 * @return the manage stock level
	 */
	public ManageStockLevelInfo createManageStockLevel()
	{
		final ManageStockLevelInfo manageStockLevelData = new ManageStockLevelInfo();
		final StockLevelAttributes stockLevelAttribute = new StockLevelAttributes();
		stockLevelAttribute.setCode("M");
		stockLevelAttribute.setAvailableQuantity(10);
		stockLevelAttribute.setOversellingQuantity(10);
		stockLevelAttribute.setInStockStatus(InStockStatus.FORCEINSTOCK);
		manageStockLevelData.setStockLevelAttributes(Arrays.asList(stockLevelAttribute));
		return manageStockLevelData;
	}

}
