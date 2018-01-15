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
 */

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.travelservices.dao.AccommodationOfferingGalleryDao;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link de.hybris.platform.travelservices.dao.AccommodationOfferingGalleryDao}
 */
public class DefaultAccommodationOfferingGalleryDao extends DefaultGenericDao<AccommodationOfferingGalleryModel>
		implements AccommodationOfferingGalleryDao
{
	public DefaultAccommodationOfferingGalleryDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public AccommodationOfferingGalleryModel findAccommodationOfferingGallery(final String code)
	{
		validateParameterNotNull(code, "Accommodation Offering Gallery Code must not be null!");

		final List<AccommodationOfferingGalleryModel> accommodationOfferingGalleries = find(
				Collections.singletonMap(AccommodationOfferingGalleryModel.CODE, (Object) code));
		if (CollectionUtils.isEmpty(accommodationOfferingGalleries))
		{
			throw new ModelNotFoundException("No result for the given query");
		}
		else if (accommodationOfferingGalleries.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					"Found " + accommodationOfferingGalleries.size() + " results for the given query");
		}
		final Optional<AccommodationOfferingGalleryModel> accommodationOfferingGalleryModel = accommodationOfferingGalleries
				.stream().findFirst();
		return accommodationOfferingGalleryModel.orElse(null);
	}

	@Override
	public AccommodationOfferingGalleryModel findAccommodationOfferingGallery(final String code,
			final CatalogVersionModel catalogVersionModel)
	{
		validateParameterNotNull(code, "Accommodation Offering Gallery Code must not be null!");
		validateParameterNotNull(catalogVersionModel, "Catalog Version must not be null!");

		final Map<String, Object> params = new HashMap<>();
		params.put(AccommodationOfferingGalleryModel.CODE, code);
		params.put(AccommodationOfferingGalleryModel.CATALOGVERSION, catalogVersionModel);

		final List<AccommodationOfferingGalleryModel> accommodationOfferingGalleries = find(params);
		if (CollectionUtils.isEmpty(accommodationOfferingGalleries))
		{
			throw new ModelNotFoundException("No result for the given query");
		}
		else if (accommodationOfferingGalleries.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					"Found " + accommodationOfferingGalleries.size() + " results for the given query");
		}
		final Optional<AccommodationOfferingGalleryModel> accommodationOfferingGalleryModel = accommodationOfferingGalleries
				.stream().findFirst();
		return accommodationOfferingGalleryModel.orElse(null);
	}
}
