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

package de.hybris.platform.ndcfacades.baggagelist.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.ndc.BaggageListRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageListRS;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;

import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBaggageListFacadeTest
{
	@InjectMocks
	DefaultBaggageListFacade defaultBaggageListFacade;

	@Mock
	private Converter<BaggageListRQ, OfferRequestData> baggageListRequestConverter;
	@Mock
	private Converter<OfferResponseData, BaggageListRS> baggageListResponseConverter;

	@Mock
	private AncillarySearchPipelineManager ndcAncillarySearchPipelineManager;

	@Test
	public void testConvertNull()
	{
		Mockito.when(ndcAncillarySearchPipelineManager.executePipeline(null)).thenReturn(null);
		assertTrue(Objects.nonNull(defaultBaggageListFacade.getBaggageList(null)));

	}

}
