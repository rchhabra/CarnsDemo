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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.cronjob.UpdateStockLevelsToAccommodationOfferingCronJobModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Test for UpdateStockLevelsToAccommodationOfferingJob.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateStockLevelsToAccommodationOfferingJobTest
{

	final String mappingDataFilePath = "/travelstore/resources/travelstore/import/coredata/datasets/hotels/minimalset/csv/Hotel2RoomsMapping.csv";

	@Mock
	BufferedReader reader;

	private final String line = "testAccommodationOffering;testAccommodation";

	@InjectMocks
	private final UpdateStockLevelsToAccommodationOfferingJob updateStockLevelsToAccommodationOfferingJob = new UpdateStockLevelsToAccommodationOfferingJob()
	{
		@Override
		public Long getScheduledDays()
		{
			return 30L;
		}

		@Override
		protected Boolean isSetForceInStock()
		{
			return true;
		}

		@Override
		protected BufferedReader getReader(final String completePath) throws java.io.FileNotFoundException
		{
			return reader;
		}

	};

	@Mock
	private UpdateStockLevelsToAccommodationOfferingCronJobModel updateStockLevelsToAccommodationOfferingCronJobModel;
	@Mock
	private AccommodationOfferingService accommodationOfferingService;
	@Mock
	private AccommodationOfferingModel accommodationOfferingModel;
	@Mock
	private AccommodationModel accommodation;
	@Mock
	private ProductService productService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration config;
	@Mock
	private StockLevelModel stockLevel;
	@Mock
	private ModelService modelService;

	@Test
	public void testPerform()
	{
		Mockito.when(accommodationOfferingService.getAccommodationOfferings())
				.thenReturn(Stream.of(accommodationOfferingModel).collect(Collectors.toList()));
		Mockito.when(updateStockLevelsToAccommodationOfferingCronJobModel.getMappingDataFilePath()).thenReturn(mappingDataFilePath);
		Mockito.when(updateStockLevelsToAccommodationOfferingCronJobModel.getAvailabilityDataFilePath())
				.thenReturn(StringUtils.EMPTY);
		Mockito.when(updateStockLevelsToAccommodationOfferingCronJobModel.getMaxAvailability()).thenReturn(50);
		Mockito.when(accommodationOfferingModel.getCode()).thenReturn("testAccommodationOffering");
		Mockito.when(productService.getProductForCode(Matchers.any(), Matchers.anyString())).thenReturn(accommodation);
		Mockito.when(catalogVersionService.getCatalogVersion(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(catalogVersion);
		Mockito.when(reader.lines()).thenReturn(Stream.of(line));
		given(stockLevel.getDate()).willReturn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		given(stockLevel.getProductCode()).willReturn("testAccommodation");
		final Set<StockLevelModel> stockSet = new HashSet<>();
		stockSet.add(stockLevel);
		Mockito.when(accommodationOfferingModel.getStockLevels()).thenReturn(stockSet);
		Mockito.when(accommodation.getCode()).thenReturn("testAccommodation");
		Mockito.when(modelService.create(StockLevelModel.class)).thenReturn(stockLevel);
		updateStockLevelsToAccommodationOfferingJob.perform(updateStockLevelsToAccommodationOfferingCronJobModel);
	}

}
