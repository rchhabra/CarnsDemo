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
package de.hybris.platform.travelrulesengine.converters.populator;


import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelrulesengine.rao.LegInfoRAO;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TripType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;


/**
 * This populator populates LegInfoRao and TripType in CartRao
 */
public class LegInfoCartRaoPopulator implements Populator<AbstractOrderModel, CartRAO>
{

	@Override
	public void populate(final AbstractOrderModel source, final CartRAO target)
			throws ConversionException
	{

		if (Objects.isNull(source) || CollectionUtils.isEmpty(source.getEntries()))
		{
			return;
		}

		final List<LegInfoRAO> legInfos = new ArrayList<>();
		final List<AbstractOrderEntryModel> fareProductEntries = source.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo() != null)
				.filter(entry -> entry.getProduct() instanceof FareProductModel
						|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType()))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fareProductEntries))
		{
			for (final AbstractOrderEntryModel entry : fareProductEntries)
			{
				if (legInfos.stream()
						.anyMatch(legInfo -> legInfo.getReferenceNumber() == entry.getTravelOrderEntryInfo()
								.getOriginDestinationRefNumber()))
				{
					continue;
				}
				legInfos.add(createLegInfo(entry.getTravelOrderEntryInfo()));
			}

			target.setLegInfos(legInfos);
		}

		target.setTripType(CollectionUtils.size(legInfos) > 1 ? TripType.RETURN : TripType.SINGLE);
	}

	/**
	 * Create leg info leg info rao.
	 *
	 * @param odInfo
	 *           the od info
	 * @return the leg info rao
	 */
	protected LegInfoRAO createLegInfo(final TravelOrderEntryInfoModel travelOrderEntryInfo)
	{
		final LegInfoRAO legInfo = new LegInfoRAO();
		legInfo.setReferenceNumber(travelOrderEntryInfo.getOriginDestinationRefNumber());
		final Date departureDate = travelOrderEntryInfo.getTransportOfferings().stream()
				.sorted(Comparator.comparing(TransportOfferingModel::getDepartureTime)).collect(Collectors.toList()).get(0)
				.getDepartureTime();
		legInfo.setDepartureTime(departureDate);
		return legInfo;
	}

}
