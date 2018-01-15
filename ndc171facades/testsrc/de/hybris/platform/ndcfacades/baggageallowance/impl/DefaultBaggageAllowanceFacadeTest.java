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
package de.hybris.platform.ndcfacades.baggageallowance.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBaggageAllowanceFacadeTest
{
	@InjectMocks
	DefaultBaggageAllowanceFacade defaultBaggageAllowanceFacade;

	@Mock
	Converter<BaggageAllowanceRQ, OfferRequestData> baggageAllowanceRequestConverter;

	@Mock
	Converter<OfferResponseData, BaggageAllowanceRS> baggageAllowanceResponseConverter;

	@Mock
	AncillarySearchPipelineManager ndcAncillarySearchPipelineManager;

	@Mock
	List<String> ndcOfferList;

	@Test
	public void testGetBaggageAllowance()
	{
		final BaggageAllowanceRQ baggageAllowanceRQ = new BaggageAllowanceRQ();
		final OfferRequestData offerRequestData = new OfferRequestData();

		Mockito.when(baggageAllowanceRequestConverter.convert(baggageAllowanceRQ)).thenReturn(offerRequestData);
		final OfferResponseData offerResponseData = new OfferResponseData();
		final OfferGroupData offerGroup = new OfferGroupData();
		offerGroup.setCode("offerGroupCode");
		final List<OfferGroupData> offerGroups = Collections.singletonList(offerGroup);
		offerResponseData.setOfferGroups(offerGroups);
		Mockito.when(ndcAncillarySearchPipelineManager.executePipeline(offerRequestData)).thenReturn(offerResponseData);

		Mockito.when(ndcOfferList.contains(offerGroup.getCode())).thenReturn(true);

		final BaggageAllowanceRS baggageAllowanceRS = defaultBaggageAllowanceFacade.getBaggageAllowance(baggageAllowanceRQ);
		Assert.assertNotNull(baggageAllowanceRS);
	}

}
