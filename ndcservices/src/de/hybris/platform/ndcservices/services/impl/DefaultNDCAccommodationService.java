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

package de.hybris.platform.ndcservices.services.impl;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.NDCOfferItemIdBundle;
import de.hybris.platform.ndcservices.services.NDCAccommodationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.dao.SelectedAccommodationDao;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.AccommodationMapModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCAccommodationService}
 */
public class DefaultNDCAccommodationService implements NDCAccommodationService
{
	private AccommodationMapService accommodationMapService;
	private ModelService modelService;
	private ProductService productService;
	private ProductReferenceService productReferenceService;
	private SelectedAccommodationDao selectedAccommodationDao;

	@Override
	public ConfiguredAccommodationModel getConfiguredAccommodation(final NDCOfferItemId ndcOfferItemId,
			final TransportOfferingModel transportOffering, final String seatNum)
	{
		final AccommodationMapModel accommodationMap = getAccommodationMap(ndcOfferItemId, transportOffering);
		if (Objects.nonNull(accommodationMap))
		{
			final String accommodationUid = String.join("-", accommodationMap.getCode(), seatNum);
			return getAccommodationMapService().getAccommodation(accommodationUid);
		}
		return null;
	}

	@Override
	public boolean checkIfAccommodationCanBeAdded(final ProductModel product, final String seatNum,
			final NDCOfferItemId ndcOfferItemId, final TransportOfferingModel transportOffering)
	{
		return checkIfAccommodationAvailable(seatNum, transportOffering);
	}


	@Override
	public void createOrUpdateSelectedAccommodation(final TransportOfferingModel transportOffering,
			final List<TravellerModel> travellers, final OrderModel orderModel,
			final ConfiguredAccommodationModel configuredAccommodation)
	{
		final SelectedAccommodationModel existedSelectedAccommodation = getSelectedAccommodationForTraveller(transportOffering,
				orderModel, travellers.get(0));
		if (Objects.isNull(existedSelectedAccommodation))
		{
			final SelectedAccommodationModel selectedAccommodation = getModelService().create(SelectedAccommodationModel.class);
			selectedAccommodation.setConfiguredAccommodation(configuredAccommodation);
			selectedAccommodation.setTransportOffering(transportOffering);
			selectedAccommodation.setTraveller(travellers.get(0));
			selectedAccommodation.setStatus(AccommodationStatus.OCCUPIED);
			selectedAccommodation.setOrder(orderModel);
			getModelService().save(selectedAccommodation);
		}
		else
		{
			existedSelectedAccommodation.setConfiguredAccommodation(configuredAccommodation);
			getModelService().save(existedSelectedAccommodation);
		}
	}

	@Override
	public SelectedAccommodationModel getSelectedAccommodationForTraveller(final TransportOfferingModel transportOffering,
			final OrderModel orderModel, final TravellerModel traveller)
	{
		return getSelectedAccommodationDao()
				.getSelectedAccommodationForTraveller(transportOffering, orderModel, traveller);
	}

	@Override
	public boolean checkIfSeatValidForFareProd(final ProductModel accommodationProd, final NDCOfferItemId ndcOfferItemId)
	{
		for (final NDCOfferItemIdBundle offerItemIdBundle : ndcOfferItemId.getBundleList())
		{
			final ProductModel fareProduct = getProductService().getProductForCode(offerItemIdBundle.getFareProduct());

			final Collection<ProductReferenceModel> productReferences = getProductReferenceService()
					.getProductReferencesForSourceAndTarget(fareProduct, accommodationProd, true);
			if (CollectionUtils.isNotEmpty(productReferences))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if given seatNum is available for {@link TransportOfferingModel}
	 *
	 * @param seatNum
	 * 		the seat num
	 * @param transportOffering
	 * 		the transport offering
	 *
	 * @return the boolean
	 */
	protected boolean checkIfAccommodationAvailable(final String seatNum, final TransportOfferingModel transportOffering)
	{
		final List<AccommodationStatus> selectedAccomStatuses = new ArrayList<>();
		selectedAccomStatuses.add(AccommodationStatus.OCCUPIED);
		selectedAccomStatuses.add(AccommodationStatus.UNAVAILABLE);
		final List<OrderStatus> cancelledOrderStatuses = new ArrayList<>();
		cancelledOrderStatuses.add(OrderStatus.CANCELLED);
		cancelledOrderStatuses.add(OrderStatus.CANCELLING);
		final List<SelectedAccommodationModel> selectedAccommodations = getAccommodationMapService()
				.getSelectedAccommodations(transportOffering, selectedAccomStatuses, cancelledOrderStatuses);
		return !getUnavailableSeatNums(selectedAccommodations).contains(seatNum);
	}

	/**
	 * This method returns all the seat numbers which are unavailable/occupied.
	 *
	 * @param selectedAccommodations
	 * 		the selected accommodations
	 *
	 * @return the unavailable seat nums
	 */
	protected List<String> getUnavailableSeatNums(final List<SelectedAccommodationModel> selectedAccommodations)
	{
		final List<String> unavailableSeats = new ArrayList<>();
		selectedAccommodations.forEach(selectedAccommodation ->
		{
			final ConfiguredAccommodationModel seat = selectedAccommodation.getConfiguredAccommodation();
			final String seatIdentifier = seat.getIdentifier();
			final ConfiguredAccommodationModel row = seat.getSuperConfiguredAccommodation().getSuperConfiguredAccommodation();
			final String colNum = seatIdentifier.substring(row.getNumber().toString().length());
			unavailableSeats.add(new StringBuilder(row.getNumber().toString()).append(colNum).toString());
		});
		return unavailableSeats;
	}

	@Override
	public void removeSelectedAccommodation(final OrderModel orderModel, final TransportOfferingModel transportOffering,
			final TravellerModel travellerModel)
	{
		if (CollectionUtils.isNotEmpty(orderModel.getSelectedAccommodations()))
		{
			final Optional<SelectedAccommodationModel> selectedAccommodationModelOptional = orderModel.getSelectedAccommodations()
					.stream().filter(selectedAccommodation -> selectedAccommodation.getTransportOffering().equals(transportOffering))
					.filter(selectedAccommodation -> selectedAccommodation.getTraveller().equals(travellerModel)).findFirst();

			if (selectedAccommodationModelOptional.isPresent())
			{
				final SelectedAccommodationModel selectedAccommodationModel = selectedAccommodationModelOptional.get();
				final List<SelectedAccommodationModel> oldList = orderModel.getSelectedAccommodations();
				final List<SelectedAccommodationModel> newList = new ArrayList<>(oldList);
				newList.remove(selectedAccommodationModel);
				orderModel.setSelectedAccommodations(newList);
				getModelService().remove(selectedAccommodationModel);
				getModelService().save(orderModel);
			}
		}
	}

	/**
	 * This method returns instance of {@link AccommodationMapModel} for given {@link TransportOfferingModel} and route
	 * code.
	 *
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 * @param transportOffering
	 * 		the transport offering
	 *
	 * @return the accommodation map
	 */
	protected AccommodationMapModel getAccommodationMap(final NDCOfferItemId ndcOfferItemId,
			final TransportOfferingModel transportOffering)
	{
		return getAccommodationMapService().getAccommodationMap(
				transportOffering.getTransportVehicle().getTransportVehicleInfo().getCode(), transportOffering,
				ndcOfferItemId.getRouteCode());
	}

	/**
	 * Gets accommodation map service.
	 *
	 * @return the accommodation map service
	 */
	protected AccommodationMapService getAccommodationMapService()
	{
		return accommodationMapService;
	}

	/**
	 * Sets accommodation map service.
	 *
	 * @param accommodationMapService
	 * 		the accommodation map service
	 */
	@Required
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets product service.
	 *
	 * @return the product service
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Sets product service.
	 *
	 * @param productService
	 * 		the product service
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * Gets product reference service.
	 *
	 * @return the product reference service
	 */
	protected ProductReferenceService getProductReferenceService()
	{
		return productReferenceService;
	}

	/**
	 * Sets product reference service.
	 *
	 * @param productReferenceService
	 * 		the product reference service
	 */
	@Required
	public void setProductReferenceService(final ProductReferenceService productReferenceService)
	{
		this.productReferenceService = productReferenceService;
	}

	/**
	 * Gets selected accommodation dao.
	 *
	 * @return the selected accommodation dao
	 */
	protected SelectedAccommodationDao getSelectedAccommodationDao()
	{
		return selectedAccommodationDao;
	}

	/**
	 * Sets selected accommodation dao.
	 *
	 * @param selectedAccommodationDao
	 * 		the selected accommodation dao
	 */
	@Required
	public void setSelectedAccommodationDao(final SelectedAccommodationDao selectedAccommodationDao)
	{
		this.selectedAccommodationDao = selectedAccommodationDao;
	}

}
