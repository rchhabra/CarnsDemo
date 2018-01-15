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

package de.hybris.platform.ndcfacades.baggagecharges.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBaggageChargesFacadeTest
{
	@InjectMocks
	DefaultBaggageChargesFacade defaultBaggageChargesFacade;

	@Mock
	Converter<BaggageChargesRQ, OfferRequestData> baggageChargesRequestConverter;
	@Mock
	Converter<OfferResponseData, BaggageChargesRS> baggageChargesResponseConverter;
	@Mock
	AncillarySearchPipelineManager ndcAncillarySearchPipelineManager;

	@Test
	public void testGetBaggageCharges()
	{
		final BaggageChargesRQ baggageChargesRQ = new BaggageChargesRQ();

		final OfferRequestData offerRequestData = new OfferRequestData();
		Mockito.when(baggageChargesRequestConverter.convert(baggageChargesRQ)).thenReturn(offerRequestData);
		final OfferResponseData offerResponseData = new OfferResponseData();
		Mockito.when(ndcAncillarySearchPipelineManager.executePipeline(offerRequestData)).thenReturn(offerResponseData);

		final BaggageChargesRS baggageChargesRS = defaultBaggageChargesFacade.getBaggageCharges(baggageChargesRQ);
		Assert.assertNotNull(baggageChargesRS);
	}

}
