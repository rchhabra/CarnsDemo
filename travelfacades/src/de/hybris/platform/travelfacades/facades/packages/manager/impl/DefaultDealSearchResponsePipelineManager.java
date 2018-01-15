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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageProductData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.StandardPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.DealSearchResponsePipelineManager;
import de.hybris.platform.travelfacades.facades.packages.manager.StandardPackagePipelineManager;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for the {@link DealSearchResponsePipelineManager}. This pipeline manager will further invoke the {@link
 * FareSearchPipelineManager}, the {@link AccommodationDetailsPipelineManager} and the {@link StandardPackagePipelineManager} for
 * deals and then a list of {@link PackageResponseHandler} to populate the {@link PackageResponseData}.
 */
public class DefaultDealSearchResponsePipelineManager implements DealSearchResponsePipelineManager
{
	private FareSearchFacade dealFareSearchFacade;
	private AccommodationDetailsPipelineManager dealAccommodationDetailsPipelineManager;
	private StandardPackagePipelineManager standardPackagePipelineManager;
	private List<PackageResponseHandler> handlers;

	@Override
	public PackageResponseData executePipeline(final PackageRequestData packageRequestData)
	{
		final PackageResponseData packageResponseData = new PackageResponseData();

		packageResponseData
				.setTransportPackageResponse(getTransportPackageResponse(packageRequestData.getTransportPackageRequest()));
		packageResponseData.setAccommodationPackageResponse(
				getAccommodationPackageResponse(packageRequestData.getAccommodationPackageRequest()));
		packageResponseData.setStandardPackageResponses(getStandardPackageResponses(packageRequestData.getBundleTemplates()));

		for (final PackageResponseHandler handler : getHandlers())
		{
			handler.handle(packageRequestData, packageResponseData);
		}

		return packageResponseData;
	}

	/**
	 * Returns the {@link TransportPackageResponseData} calculated from the {@link TransportPackageRequestData}
	 *
	 * @param transportPackageRequestData
	 * 		as the transportPackageRequestData
	 *
	 * @return the transportPackageResponseData
	 */
	protected TransportPackageResponseData getTransportPackageResponse(
			final TransportPackageRequestData transportPackageRequestData)
	{
		final TransportPackageResponseData transportPackageResponseData = new TransportPackageResponseData();
		transportPackageResponseData
				.setFareSearchResponse(getDealFareSearchFacade().doSearch(transportPackageRequestData.getFareSearchRequest()));

		return transportPackageResponseData;
	}

	/**
	 * Returns the {@link AccommodationPackageResponseData} calculated from the {@link AccommodationPackageRequestData}
	 *
	 * @param accommodationPackageRequest
	 * 		as the accommodationPackageRequest
	 *
	 * @return the accommodationPackageResponseData
	 */
	protected AccommodationPackageResponseData getAccommodationPackageResponse(
			final AccommodationPackageRequestData accommodationPackageRequest)
	{
		final AccommodationPackageResponseData accommodationPackageResponseData = new AccommodationPackageResponseData();
		accommodationPackageResponseData.setAccommodationAvailabilityResponse(getDealAccommodationDetailsPipelineManager()
				.executePipeline(accommodationPackageRequest.getAccommodationAvailabilityRequest()));
		return accommodationPackageResponseData;
	}

	/**
	 * Returns the list of {@link StandardPackageResponseData} calculated from the list of {@link BundleTemplateData}
	 *
	 * @param bundleTemplates
	 * 		as the list of bundleTemplatedata
	 *
	 * @return the list of standardPackageResponseData
	 */
	protected List<StandardPackageResponseData> getStandardPackageResponses(final List<BundleTemplateData> bundleTemplates)
	{
		final StandardPackageResponseData standardPackageResponseData = new StandardPackageResponseData();
		final List<PackageProductData> packageProductDatas = bundleTemplates.stream()
				.flatMap(bundleTemplate -> getStandardPackagePipelineManager().executePipeline(bundleTemplate).stream())
				.collect(Collectors.toList());
		standardPackageResponseData.setPackageProducts(packageProductDatas);
		return Collections.singletonList(standardPackageResponseData);
	}

	/**
	 * @return the dealFareSearchFacade
	 */
	protected FareSearchFacade getDealFareSearchFacade()
	{
		return dealFareSearchFacade;
	}

	/**
	 * @param dealFareSearchFacade
	 *           the dealFareSearchFacade to set
	 */
	@Required
	public void setDealFareSearchFacade(final FareSearchFacade dealFareSearchFacade)
	{
		this.dealFareSearchFacade = dealFareSearchFacade;
	}

	/**
	 * @return the dealAccommodationDetailsPipelineManager
	 */
	protected AccommodationDetailsPipelineManager getDealAccommodationDetailsPipelineManager()
	{
		return dealAccommodationDetailsPipelineManager;
	}

	/**
	 * @param dealAccommodationDetailsPipelineManager
	 * 		the dealAccommodationDetailsPipelineManager to set
	 */
	@Required
	public void setDealAccommodationDetailsPipelineManager(
			final AccommodationDetailsPipelineManager dealAccommodationDetailsPipelineManager)
	{
		this.dealAccommodationDetailsPipelineManager = dealAccommodationDetailsPipelineManager;
	}

	/**
	 * @return the standardPackagePipelineManager
	 */
	protected StandardPackagePipelineManager getStandardPackagePipelineManager()
	{
		return standardPackagePipelineManager;
	}

	/**
	 * @param standardPackagePipelineManager
	 * 		the standardPackagePipelineManager to set
	 */
	@Required
	public void setStandardPackagePipelineManager(final StandardPackagePipelineManager standardPackagePipelineManager)
	{
		this.standardPackagePipelineManager = standardPackagePipelineManager;
	}

	/**
	 * @return the handlers
	 */
	protected List<PackageResponseHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * @param handlers
	 * 		the handlers to set
	 */
	@Required
	public void setHandlers(final List<PackageResponseHandler> handlers)
	{
		this.handlers = handlers;
	}
}
