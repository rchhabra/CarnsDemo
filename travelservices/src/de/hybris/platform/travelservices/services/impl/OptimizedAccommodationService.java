package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.AccommodationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Optimized version of {@link AccommodationService}. It uses the list of accommodation codes on
 * {@link AccommodationOfferingModel} to retrieve accommodations instead of finding them by the StockLevel connection.
 */
public class OptimizedAccommodationService implements AccommodationService
{
	private AccommodationOfferingService accommodationOfferingService;
	private ProductService productService;

	@Override
	public List<AccommodationModel> getAccommodationForAccommodationOffering(final String accommodationOfferingCode)
	{
		final AccommodationOfferingModel accommodationOfferingModel = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationOfferingCode);
		if (Objects.isNull(accommodationOfferingModel) || CollectionUtils.isEmpty(accommodationOfferingModel.getAccommodations()))
		{
			return Collections.emptyList();
		}
		final List<String> accommodationCodes = accommodationOfferingModel.getAccommodations();
		final List<AccommodationModel> accommodations = new ArrayList<>(CollectionUtils.size(accommodationCodes));
		accommodationCodes.forEach(accommodationCode ->
		{
			final ProductModel product = getProductService().getProductForCode(accommodationCode);
			if (product != null && product instanceof AccommodationModel)
			{
				accommodations.add((AccommodationModel) product);
			}
		});

		return accommodations;
	}

	@Override
	public AccommodationModel getAccommodationForAccommodationOffering(final String accommodationOfferingCode,
			final String accommodationCode)
	{
		final AccommodationOfferingModel accommodationOfferingModel = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationOfferingCode);

		final Optional<String> accommodationOptional = accommodationOfferingModel.getAccommodations().stream()
				.filter(acc -> StringUtils.equalsIgnoreCase(acc, accommodationCode)).findAny();
		if (accommodationOptional.isPresent())
		{
			final ProductModel product = getProductService().getProductForCode(accommodationOptional.get());
			if (product != null && product instanceof AccommodationModel)
			{
				return (AccommodationModel) product;
			}
		}
		return null;
	}

	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}
}
