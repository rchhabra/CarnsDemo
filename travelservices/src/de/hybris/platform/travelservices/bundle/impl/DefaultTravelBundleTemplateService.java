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
package de.hybris.platform.travelservices.bundle.impl;

import de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelservices.bundle.TravelBundleTemplateService;
import de.hybris.platform.travelservices.dao.TravelBundleTemplateDao;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of TravelBundleTemplateService(also provides the features of {@link DefaultBundleTemplateService}) - Provides
 * business logic for retrieving BundleTemplates for a give TravelRoute or TravelSector or TransportOffering.
 */
public class DefaultTravelBundleTemplateService extends DefaultBundleTemplateService implements TravelBundleTemplateService
{
	private static final String CABIN_CLASS_MODEL_CANNOT_BE_NULL = "CabinClassModel cannot be Null";

	private TravelBundleTemplateDao travelBundleTemplateDao;
	private BookingService bookingService;

	@Override
	public List<BundleTemplateModel> getBundleTemplates(final TravelRouteModel travelRouteModel,
			final CabinClassModel cabinClassModel)
	{
		ServicesUtil.validateParameterNotNull(travelRouteModel, "TravelRouteModel cannot be Null");
		ServicesUtil.validateParameterNotNull(cabinClassModel, CABIN_CLASS_MODEL_CANNOT_BE_NULL);
		return getTravelBundleTemplateDao().findBundleTemplates(travelRouteModel, cabinClassModel).stream().filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public List<BundleTemplateModel> getBundleTemplates(final TravelSectorModel travelSectorModel,
			final CabinClassModel cabinClassModel)
	{
		ServicesUtil.validateParameterNotNull(travelSectorModel, "TravelSectorModel cannot be Null");
		ServicesUtil.validateParameterNotNull(cabinClassModel, CABIN_CLASS_MODEL_CANNOT_BE_NULL);
		return getTravelBundleTemplateDao().findBundleTemplates(travelSectorModel, cabinClassModel).stream().filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public List<BundleTemplateModel> getBundleTemplates(final TransportOfferingModel transportOfferingModel,
			final CabinClassModel cabinClassModel)
	{
		ServicesUtil.validateParameterNotNull(transportOfferingModel, "TransportOfferingModel cannot be Null");
		ServicesUtil.validateParameterNotNull(cabinClassModel, CABIN_CLASS_MODEL_CANNOT_BE_NULL);
		return getTravelBundleTemplateDao().findBundleTemplates(transportOfferingModel, cabinClassModel).stream().filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public List<BundleTemplateModel> getDefaultBundleTemplates(final CabinClassModel cabinClassModel)
	{
		ServicesUtil.validateParameterNotNull(cabinClassModel, CABIN_CLASS_MODEL_CANNOT_BE_NULL);
		return getTravelBundleTemplateDao().findDefaultBundleTemplates(cabinClassModel).stream().filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public String getBundleTemplateIdFromOrder(final AbstractOrderModel abstractOrder, final int originDestinationRefNumber)
	{
		if (Objects.isNull(abstractOrder))
		{
			return StringUtils.EMPTY;
		}
		final boolean isTransportOrder = getBookingService().checkIfAnyOrderEntryByType(abstractOrder, OrderEntryType.TRANSPORT);
		if (!isTransportOrder)
		{
			return StringUtils.EMPTY;
		}
		for (final AbstractOrderEntryModel entry : abstractOrder.getEntries())
		{
			if ((Objects.equals(ProductType.FARE_PRODUCT, entry.getProduct().getProductType())
					|| entry.getProduct() instanceof FareProductModel)
					&& Objects.nonNull(entry.getTravelOrderEntryInfo())
					&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == originDestinationRefNumber)
			{
				return entry.getBundleTemplate().getType().toString();
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Gets travel bundle template dao.
	 *
	 * @return the travel bundle template dao
	 */
	protected TravelBundleTemplateDao getTravelBundleTemplateDao()
	{
		return travelBundleTemplateDao;
	}

	/**
	 * Sets travel bundle template dao.
	 *
	 * @param travelBundleTemplateDao
	 * 		the travel bundle template dao
	 */
	@Required
	public void setTravelBundleTemplateDao(final TravelBundleTemplateDao travelBundleTemplateDao)
	{
		this.travelBundleTemplateDao = travelBundleTemplateDao;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 * 		the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

}
