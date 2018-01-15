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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.dao.RoomPreferenceDAO;
import de.hybris.platform.travelservices.model.accommodation.RoomPreferenceModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.RoomPreferenceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link RoomPreferenceService}
 */
public class DefaultRoomPreferenceService implements RoomPreferenceService
{
	private static final Logger LOG = Logger.getLogger(DefaultRoomPreferenceService.class);

	private RoomPreferenceDAO roomPreferenceDAO;

	private BookingService bookingService;

	private TravelCartService travelCartService;

	private ProductService productService;

	private ModelService modelService;

	private List<String> defaultRoomBedPreferenceCodes;

	@Override
	public List<RoomPreferenceModel> getRoomPreferences(final String roomPreferenceType)
	{
		return getRoomPreferenceDAO().getRoomPreferences(roomPreferenceType);
	}

	@Override
	public List<RoomPreferenceModel> getRoomPreferences(final List<String> roomPreferenceCodes)
	{
		if (CollectionUtils.isEmpty(roomPreferenceCodes))
		{
			return Collections.emptyList();
		}

		List<String> localRoomPreferenceCodes = new ArrayList<String>(roomPreferenceCodes);

		localRoomPreferenceCodes = localRoomPreferenceCodes.stream()
				.filter(roomPreferenceCode -> StringUtils.isNotEmpty(roomPreferenceCode)).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(localRoomPreferenceCodes))
		{
			return getRoomPreferenceDAO().getRoomPreferences(localRoomPreferenceCodes);
		}

		return Collections.emptyList();
	}

	@Override
	public Boolean saveRoomPreference(final int roomStayRefNum, final List<String> roomPreferenceCodes)
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = getBookingService()
				.getAccommodationOrderEntryGroup(roomStayRefNum, getTravelCartService().getSessionCart());

		if (accommodationOrderEntryGroupModel == null)
		{
			return Boolean.FALSE;
		}

		final List<RoomPreferenceModel> roomPreferences = getRoomPreferences(roomPreferenceCodes);
		if (CollectionUtils.isEmpty(roomPreferences)
				&& !CollectionUtils.containsAny(roomPreferenceCodes, getDefaultRoomBedPreferenceCodes()))
		{
			return Boolean.FALSE;
		}

		accommodationOrderEntryGroupModel.setRoomPreferences(roomPreferences);

		try
		{
			getModelService().save(accommodationOrderEntryGroupModel);
			return Boolean.TRUE;
		}
		catch (final ModelSavingException e)
		{
			LOG.debug("Model " + AccommodationOrderEntryGroupModel._TYPECODE + " not saved", e);
			return Boolean.FALSE;
		}
	}

	@Override
	public List<RoomPreferenceModel> getRoomPreferencesForTypeAndAccommodation(final String roomPreferenceType,
			final List<String> roomTypeCodes)
	{
		return roomTypeCodes.stream().map(roomTypeCode -> getProductService().getProductForCode(roomTypeCode))
				.filter(AccommodationModel.class::isInstance)
				.flatMap(accommodation -> ((AccommodationModel) accommodation).getRoomPreferences().stream())
				.filter(roomPreference -> roomPreferenceType.equals(roomPreference.getPreferenceType().getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * @return the roomPreferenceDAO
	 */
	protected RoomPreferenceDAO getRoomPreferenceDAO()
	{
		return roomPreferenceDAO;
	}

	/**
	 * @param roomPreferenceDAO
	 *           the roomPreferenceDAO to set
	 */
	@Required
	public void setRoomPreferenceDAO(final RoomPreferenceDAO roomPreferenceDAO)
	{
		this.roomPreferenceDAO = roomPreferenceDAO;
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
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 * 		the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
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
	 *           the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the defaultRoomBedPreferenceCodes
	 */
	protected List<String> getDefaultRoomBedPreferenceCodes()
	{
		return defaultRoomBedPreferenceCodes;
	}


	/**
	 * @param defaultRoomBedPreferenceCodes
	 *           the defaultRoomBedPreferenceCodes to set
	 */
	@Required
	public void setDefaultRoomBedPreferenceCodes(final List<String> defaultRoomBedPreferenceCodes)
	{
		this.defaultRoomBedPreferenceCodes = defaultRoomBedPreferenceCodes;
	}


}
