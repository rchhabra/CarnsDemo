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

package de.hybris.platform.travelfulfilmentprocess.actions.checkin;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelservices.strategies.AutoAccommodationAllocationStrategy;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Performs the Auto Seat Allocation
 */
public class PerformAutoSeatAllocationAction extends AbstractProceduralAction<CheckInProcessModel>
{

	private BaseSiteService baseSiteService;
	private CatalogVersionService catalogVersionService;
	private AutoAccommodationAllocationStrategy autoAccommodationAllocationStrategy;

	@Override
	public void executeAction(final CheckInProcessModel process) throws RetryLaterException, Exception
	{
		final Integer originDestinationRefNumber = process.getOriginDestinationRefNumber();
		final List<String> travellers = process.getTravellers();
		final OrderModel orderModel = process.getOrder();

		getCatalogVersionService().setSessionCatalogVersions(
				getBaseSiteService().getProductCatalogs(orderModel.getSite()).stream()
						.map(catalog -> catalog.getActiveCatalogVersion()).collect(Collectors.toList()));
		getAutoAccommodationAllocationStrategy().autoAllocateSeat(orderModel, originDestinationRefNumber, travellers);
	}

	/**
	 * Gets auto accommodation allocation strategy.
	 *
	 * @return the autoAccommodationAllocationStrategy
	 */
	protected AutoAccommodationAllocationStrategy getAutoAccommodationAllocationStrategy()
	{
		return autoAccommodationAllocationStrategy;
	}

	/**
	 * Sets auto accommodation allocation strategy.
	 *
	 * @param autoAccommodationAllocationStrategy
	 * 		the autoAccommodationAllocationStrategy to set
	 */
	public void setAutoAccommodationAllocationStrategy(
			final AutoAccommodationAllocationStrategy autoAccommodationAllocationStrategy)
	{
		this.autoAccommodationAllocationStrategy = autoAccommodationAllocationStrategy;
	}

	/**
	 * Gets catalog version service.
	 *
	 * @return the catalogVersionService
	 */
	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * Sets catalog version service.
	 *
	 * @param catalogVersionService
	 * 		the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * Gets base site service.
	 *
	 * @return the baseSiteService
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * Sets base site service.
	 *
	 * @param baseSiteService
	 * 		the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}
