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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.model.cronjob.UpdateStockLevelsToTransportOfferingCronJobModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.impl.DefaultTransportOfferingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.ImmutableMap;


/**
 * Unit Test for UpdateStockLevelToTransportOfferingJob.
 */

@UnitTest
public class UpdateStockLevelToTransportOfferingJobTest
{

	@Mock
	private TransportOfferingService transportOfferingService;

	@Mock
	private UpdateStockLevelsToTransportOfferingCronJobModel updateStockLevelsToTransportOfferingCronJobModel;

	@InjectMocks
	private UpdateStockLevelsToTransportOfferingJob updateStockLevelToTransportOfferingJob;

	private ModelService modelServiceMock;

	private TransportOfferingModel mock_LHREDI;

	private TransportOfferingModel mock_CDGEDI;

	@Before
	public void prepare()
	{
		mock_LHREDI = prepareTransportOfferingModelMocks("LHR", "EDI");

		mock_CDGEDI = prepareTransportOfferingModelMocksWithStockLevels("CDG", "EDI");

		modelServiceMock = mock(ModelService.class);
		final StockLevelModel stoclLevelModelMockO = mock(StockLevelModel.class);
		doNothing().when(stoclLevelModelMockO).setProductCode("O");
		doNothing().when(stoclLevelModelMockO).setAvailable(9);
		doNothing().when(stoclLevelModelMockO).setWarehouse(mock_LHREDI);
		final StockLevelModel stoclLevelModelMockM = mock(StockLevelModel.class);
		doNothing().when(stoclLevelModelMockM).setProductCode("M");
		doNothing().when(stoclLevelModelMockM).setAvailable(9);
		doNothing().when(stoclLevelModelMockM).setWarehouse(mock_LHREDI);
		final StockLevelModel stoclLevelModelMockY = mock(StockLevelModel.class);
		doNothing().when(stoclLevelModelMockY).setProductCode("Y");
		doNothing().when(stoclLevelModelMockY).setAvailable(9);
		doNothing().when(stoclLevelModelMockY).setWarehouse(mock_LHREDI);

		final List<TransportOfferingModel> transportOfferings = new ArrayList<TransportOfferingModel>();

		transportOfferings.add(mock_LHREDI);
		transportOfferings.add(mock_CDGEDI);
		transportOfferingService = mock(DefaultTransportOfferingService.class);
		given(transportOfferingService.getTransportOfferings()).willReturn(transportOfferings);

		given(modelServiceMock.create(StockLevelModel.class)).willReturn(stoclLevelModelMockO, stoclLevelModelMockM,
				stoclLevelModelMockY);
		doNothing().when(modelServiceMock).save(stoclLevelModelMockO);
		doNothing().when(modelServiceMock).save(stoclLevelModelMockY);
		doNothing().when(modelServiceMock).save(stoclLevelModelMockM);

		updateStockLevelsToTransportOfferingCronJobModel = mock(UpdateStockLevelsToTransportOfferingCronJobModel.class);
		given(updateStockLevelsToTransportOfferingCronJobModel.getDomesticAirports()).willReturn(Arrays.asList(new String[]
		{ "LHR", "LGW", "EDI", "MAN" }));
		given(updateStockLevelsToTransportOfferingCronJobModel.getEconomyStockLevels())
				.willReturn(ImmutableMap.of("O", 9, "M", 9, "Y", 9));
		given(updateStockLevelsToTransportOfferingCronJobModel.getEconomyPlusStockLevels())
				.willReturn(ImmutableMap.of("T", 9, "E", 9, "W", 9));
		given(updateStockLevelsToTransportOfferingCronJobModel.getBusinessStockLevel())
				.willReturn(ImmutableMap.of("C", 9, "D", 9, "J", 9));
		given(updateStockLevelsToTransportOfferingCronJobModel.getEconomyAncillaryStockLevels())
				.willReturn(ImmutableMap.of("ACECONSEAT1", 70));
		given(updateStockLevelsToTransportOfferingCronJobModel.getEconomyPlusAncillaryStockLevels())
				.willReturn(ImmutableMap.of("ACPECOSEAT1", 20));
		given(updateStockLevelsToTransportOfferingCronJobModel.getBusinessAncillaryStockLevel())
				.willReturn(ImmutableMap.of("ACBIZZSEAT1", 10));
	}

	private TransportOfferingModel prepareTransportOfferingModelMocks(final String origin, final String destination)
	{
		final TravelSectorModel travelSectorMock = mock(TravelSectorModel.class);
		final TransportFacilityModel originFacilityMock = mock(TransportFacilityModel.class);
		given(originFacilityMock.getCode()).willReturn(origin);
		final TransportFacilityModel destinationFacilityMock = mock(TransportFacilityModel.class);
		given(destinationFacilityMock.getCode()).willReturn(destination);
		given(travelSectorMock.getOrigin()).willReturn(originFacilityMock);
		given(travelSectorMock.getDestination()).willReturn(destinationFacilityMock);
		final TransportOfferingModel transportOfferingModelMock = mock(TransportOfferingModel.class);
		given(transportOfferingModelMock.getTravelSector()).willReturn(travelSectorMock);
		given(transportOfferingModelMock.getStockLevels()).willReturn(null);
		given(transportOfferingModelMock.getActive()).willReturn(true);
		return transportOfferingModelMock;
	}

	private TransportOfferingModel prepareTransportOfferingModelMocksWithStockLevels(final String origin, final String destination)
	{
		final TravelSectorModel travelSectorMock = mock(TravelSectorModel.class);
		final TransportFacilityModel originFacilityMock = mock(TransportFacilityModel.class);
		given(originFacilityMock.getCode()).willReturn(origin);
		final TransportFacilityModel destinationFacilityMock = mock(TransportFacilityModel.class);
		given(destinationFacilityMock.getCode()).willReturn(destination);
		given(travelSectorMock.getOrigin()).willReturn(originFacilityMock);
		given(travelSectorMock.getDestination()).willReturn(destinationFacilityMock);
		final TransportOfferingModel transportOfferingModelMock = mock(TransportOfferingModel.class);
		given(transportOfferingModelMock.getTravelSector()).willReturn(travelSectorMock);

		final Set<StockLevelModel> stockLevels = createStocklevelsForTransportOffering();

		given(transportOfferingModelMock.getStockLevels()).willReturn(stockLevels);
		given(transportOfferingModelMock.getActive()).willReturn(true);
		return transportOfferingModelMock;
	}

	private Set<StockLevelModel> createStocklevelsForTransportOffering()
	{
		final Set<StockLevelModel> stockSet = new HashSet<>();
		final StockLevelModel stoclLevelModelMockO = mock(StockLevelModel.class);
		given(stoclLevelModelMockO.getProductCode()).willReturn("O");
		stockSet.add(stoclLevelModelMockO);
		final StockLevelModel stoclLevelModelMockM = mock(StockLevelModel.class);
		given(stoclLevelModelMockM.getProductCode()).willReturn("M");
		stockSet.add(stoclLevelModelMockM);
		final StockLevelModel stoclLevelModelMockY = mock(StockLevelModel.class);
		given(stoclLevelModelMockY.getProductCode()).willReturn("Y");
		stockSet.add(stoclLevelModelMockY);
		return stockSet;
	}

	@Test
	public void testPerformUpdateStockLevelsToTransportOfferingCronJobModel()
	{
		updateStockLevelToTransportOfferingJob = new UpdateStockLevelsToTransportOfferingJob()
		{

			@Override
			protected ModelService getModelService()
			{
				return modelServiceMock;
			}
		};

		updateStockLevelToTransportOfferingJob.setTransportOfferingService(transportOfferingService);
		updateStockLevelToTransportOfferingJob.perform(updateStockLevelsToTransportOfferingCronJobModel);
		verify(transportOfferingService).getTransportOfferings();
		verify(mock_LHREDI, times(4)).getStockLevels();
	}

	@Test
	public void testPerformUpdateStockLevelsToTransportOfferingCronJobModelWithDomesticAirport()
	{
		given(updateStockLevelsToTransportOfferingCronJobModel.getDomesticAirports()).willReturn(Arrays.asList(new String[]
		{ "MAN" }));

		updateStockLevelToTransportOfferingJob = new UpdateStockLevelsToTransportOfferingJob()
		{

			@Override
			protected ModelService getModelService()
			{
				return modelServiceMock;
			}
		};

		updateStockLevelToTransportOfferingJob.setTransportOfferingService(transportOfferingService);
		updateStockLevelToTransportOfferingJob.perform(updateStockLevelsToTransportOfferingCronJobModel);
		verify(transportOfferingService).getTransportOfferings();
		verify(mock_LHREDI, times(12)).getStockLevels();
	}
}
