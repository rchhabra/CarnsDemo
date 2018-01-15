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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Reverse populator to save customer review after submission
 */
public class AccommodationCustomerReviewReversePopulator implements Populator<ReviewData, CustomerReviewModel>
{
	private ProductService productService;
	private AccommodationOfferingService accommodationOfferingService;
	private EnumerationService enumerationService;
	private ConfigurationService configurationService;
	private UserService userService;

	@Override
	public void populate(final ReviewData source, final CustomerReviewModel target) throws ConversionException
	{
		target.setHeadline(source.getHeadline());
		target.setComment(source.getComment());
		target.setRating(source.getRating());
		target.setProduct(getProductService().getProductForCode(source.getProductCode()));
		target.setAccommodationOffering(
				getAccommodationOfferingService().getAccommodationOffering(source.getAccommodationOfferingCode()));
		target.setUser(getUserService().getCurrentUser());
		target.setBookingReference(source.getBookingReference());
		target.setRoomStayRefNumber(source.getRoomStayRefNumber());
		final String approvalStatusToSet = getConfigurationService().getConfiguration()
				.getString(TravelfacadesConstants.CUSTOMER_REVIEW_DEFAULT_STATUS);
		if (StringUtils.isNotEmpty(approvalStatusToSet))
		{
			target.setApprovalStatus(
					getEnumerationService().getEnumerationValue(CustomerReviewApprovalType.class, approvalStatusToSet));
		}

	}

	/**
	 *
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 *
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 *
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 *
	 * @param accommodationOfferingService
	 *           the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

	/**
	 *
	 * @return enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 *
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 *
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 *
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 *
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 *
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}



}
