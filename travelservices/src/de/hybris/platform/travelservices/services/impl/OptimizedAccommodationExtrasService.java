package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationExtrasService;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Optimized version of {@link AccommodationExtrasService}. It uses the list of extra codes on
 * {@link AccommodationOfferingModel} to retrieve extras instead of finding them by the StockLevel connection.
 */
public class OptimizedAccommodationExtrasService implements AccommodationExtrasService
{
	private AccommodationOfferingService accommodationOfferingService;
	private ProductService productService;

	@Override
	public List<ProductModel> getExtrasForAccommodationOffering(final String accommodationOfferingCode)
	{
		final AccommodationOfferingModel accommodationOffering = getAccommodationOfferingService()
				.getAccommodationOffering(accommodationOfferingCode);

		if (Objects.isNull(accommodationOffering) || CollectionUtils.isEmpty(accommodationOffering.getExtras()))
		{
			return Collections.emptyList();
		}

		final List<String> extraServiceCodes = accommodationOffering.getExtras();
		final List<ProductModel> extraServices = new ArrayList<>(CollectionUtils.size(extraServiceCodes));

		extraServiceCodes.forEach(accommodationCode ->
		{
			final ProductModel product = getProductService().getProductForCode(accommodationCode);
			if (product != null)
			{
				extraServices.add(product);
			}
		});

		return extraServices;
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
