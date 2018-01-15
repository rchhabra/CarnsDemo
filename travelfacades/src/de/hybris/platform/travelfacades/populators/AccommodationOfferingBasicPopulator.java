/*
* [y] hybris Platform
*
* Copyright (c) 2000-2015 hybris AG
* All rights reserved.
*
* This software is the confidential and proprietary information of hybris
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with hybris.
*
*/

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.accommodation.AwardData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.commercefacades.accommodation.search.PositionData;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.travel.enums.AwardType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.util.CommerceCatalogUtils;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelfacades.facades.accommodation.handlers.impl.AccommodationDetailsBasicHandler;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingGalleryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * The type Accommodation offering basic populator.
 */
public class AccommodationOfferingBasicPopulator implements Populator<AccommodationOfferingModel, PropertyData>
{
	private static final Logger LOG = Logger.getLogger(AccommodationDetailsBasicHandler.class);

	private AbstractPopulatingConverter<PropertyFacilityModel, FacilityData> propertyFacilityConverter;
	private Comparator<FacilityData> configuredFacilityDataComparator;
	private AccommodationOfferingGalleryService accommodationOfferingGalleryService;
	private CatalogVersionService catalogVersionService;
	private List<String> imageFormats;
	private ImageFormatMapping imageFormatMapping;
	private MediaContainerService mediaContainerService;
	private MediaService mediaService;
	private Converter<MediaModel, ImageData> imageConverter;
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;

	@Override
	public void populate(final AccommodationOfferingModel source, final PropertyData target) throws ConversionException
	{
		target.setAccommodationOfferingCode(source.getCode());
		target.setAccommodationOfferingName(source.getName());
		target.setDescription(source.getDescription());
		target.setAddress(getAddressData(source));
		target.setAwards(getAwards(source));
		target.setPosition(getPosition(source.getLocation()));
		target.setAmenities(getPropertyFacilityConverter().convertAll(source.getActivePropertyFacility()));
		Collections.sort(target.getAmenities(), getConfiguredFacilityDataComparator());
		target.setPropertyInformation(source.getPropertyInformation());
		populateImageGallery(source, target);
	}

	private void populateImageGallery(final AccommodationOfferingModel accommodationOfferingModel, final PropertyData
			propertyData)
	{
		if (StringUtils.isEmpty(accommodationOfferingModel.getGalleryCode()))
		{
			propertyData.setImages(Collections.emptyList());
			return;
		}

		final AccommodationOfferingGalleryModel accommodationOfferingGallery = getAccommodationOfferingGalleryService()
				.getAccommodationOfferingGallery(accommodationOfferingModel.getGalleryCode(), getCatalogVersion());

		final List<MediaContainerModel> mediaContainers = accommodationOfferingGallery.getGallery();

		if (CollectionUtils.isNotEmpty(mediaContainers))
		{
			final List<ImageData> imageList = new ArrayList<>();

			int galleryIndex = 0;
			for (final MediaContainerModel mediaContainer : mediaContainers)
			{
				addImagesInFormats(mediaContainer, ImageDataType.GALLERY, galleryIndex++, imageList);
			}

			final MediaModel listImage = accommodationOfferingGallery.getListImage();
			if (Objects.nonNull(listImage))
			{
				final ImageData imageData = new ImageData();
				imageData.setImageType(ImageDataType.PRIMARY);
				imageData.setUrl(listImage.getURL());
				imageList.add(imageData);
			}

			for (final ImageData imageData : imageList)
			{
				if (imageData.getAltText() == null)
				{
					// If AltText is null, by default we use the property's name
					imageData.setAltText(propertyData.getAccommodationOfferingName());
				}
			}

			propertyData.setImages(imageList);
		}
	}

	/**
	 * Gets address data.
	 *
	 * @param accommodationOfferingModel
	 * 		the accommodation offering model
	 *
	 * @return the address data
	 */
	protected AddressData getAddressData(final AccommodationOfferingModel accommodationOfferingModel)
	{
		final Iterator<PointOfServiceModel> posIterator = accommodationOfferingModel.getLocation().getPointOfService().iterator();
		if (posIterator.hasNext())
		{
			final AddressModel address = posIterator.next().getAddress();
			return getAddressConverter().convert(address);
		}
		return new AddressData();
	}

	/**
	 * Gets awards.
	 *
	 * @param accommodationOfferingModel
	 * 		the accommodation offering model
	 *
	 * @return the awards
	 */
	protected List<AwardData> getAwards(final AccommodationOfferingModel accommodationOfferingModel)
	{
		final List<AwardData> awards = new ArrayList<>();
		final Integer starRating = accommodationOfferingModel.getStarRating();
		if (Objects.nonNull(starRating) && starRating > 0)
		{
			final AwardData awardDataSR = new AwardData();
			awardDataSR.setType(AwardType.STAR_RATING);
			awardDataSR.setRating(starRating.doubleValue());
			awards.add(awardDataSR);
		}
		final Double userRating = accommodationOfferingModel.getAverageUserRating();
		if (Objects.nonNull(userRating) && userRating > 0)
		{
			final AwardData awardDataUR = new AwardData();
			awardDataUR.setType(AwardType.USER_RATING);
			awardDataUR.setRating(userRating);
			awards.add(awardDataUR);
		}
		return awards;
	}

	/**
	 * Gets position.
	 *
	 * @param location
	 * 		the location
	 *
	 * @return the position
	 */
	protected PositionData getPosition(final LocationModel location)
	{
		final PositionData position = new PositionData();
		final Iterator<PointOfServiceModel> posIterator = location.getPointOfService().iterator();
		if (posIterator.hasNext())
		{
			final PointOfServiceModel pointOfService = posIterator.next();
			position.setLongitude(pointOfService.getLongitude());
			position.setLatitude(pointOfService.getLatitude());
		}
		return position;
	}

	/**
	 * This method will add on the images list the medias converted from the given media container. If the imageType is
	 * of type GALLERY then also the galleryIndex is set on the Image data
	 *
	 * @param mediaContainer
	 * 		the media container
	 * @param imageType
	 * 		the image type
	 * @param galleryIndex
	 * 		the gallery index
	 * @param imagesList
	 * 		the images list
	 */
	protected void addImagesInFormats(final MediaContainerModel mediaContainer, final ImageDataType imageType,
			final int galleryIndex, final List<ImageData> imagesList)
	{
		for (final String imageFormat : getImageFormats())
		{
			try
			{
				final String mediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
				if (mediaFormatQualifier == null)
				{
					continue;
				}
				final MediaFormatModel mediaFormat = getMediaService().getFormat(mediaFormatQualifier);
				if (mediaFormat == null)
				{
					continue;
				}

				final MediaModel media = getMediaContainerService().getMediaForFormat(mediaContainer, mediaFormat);
				if (media == null)
				{
					continue;
				}
				final ImageData imageData = getImageConverter().convert(media);
				imageData.setFormat(imageFormat);
				imageData.setImageType(imageType);
				if (ImageDataType.GALLERY.equals(imageType))
				{
					imageData.setGalleryIndex(galleryIndex);
				}
				imagesList.add(imageData);
			}
			catch (final ModelNotFoundException ex)

			{
				LOG.debug("Media model not found.", ex);
			}
		}
	}

	protected List<MediaContainerModel> getMediaContainerForSelectedAccommodation(final String galleryCode)
	{
		return getAccommodationOfferingGalleryService().getAccommodationOfferingGallery(galleryCode, getCatalogVersion())
				.getGallery();
	}

	/**
	 * Gets catalog version.
	 *
	 * @return the catalog version
	 */
	protected CatalogVersionModel getCatalogVersion()
	{
		final Collection<CatalogVersionModel> sessionCatalogVersions = getCatalogVersionService().getSessionCatalogVersions();
		if (CollectionUtils.isNotEmpty(sessionCatalogVersions))
		{
			return CommerceCatalogUtils.getActiveProductCatalogVersion(sessionCatalogVersions);
		}
		return null;
	}

	/**
	 * Gets accommodation offering gallery service.
	 *
	 * @return the accommodation offering gallery service
	 */
	protected AccommodationOfferingGalleryService getAccommodationOfferingGalleryService()
	{
		return accommodationOfferingGalleryService;
	}

	/**
	 * Sets accommodation offering gallery service.
	 *
	 * @param accommodationOfferingGalleryService
	 * 		the accommodation offering gallery service
	 */
	public void setAccommodationOfferingGalleryService(
			final AccommodationOfferingGalleryService accommodationOfferingGalleryService)
	{
		this.accommodationOfferingGalleryService = accommodationOfferingGalleryService;
	}

	/**
	 * Gets catalog version service.
	 *
	 * @return the catalog version service
	 */
	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * Sets catalog version service.
	 *
	 * @param catalogVersionService
	 * 		the catalog version service
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * Gets image formats.
	 *
	 * @return the image formats
	 */
	protected List<String> getImageFormats()
	{
		return imageFormats;
	}

	/**
	 * Sets image formats.
	 *
	 * @param imageFormats
	 * 		the image formats
	 */
	public void setImageFormats(final List<String> imageFormats)
	{
		this.imageFormats = imageFormats;
	}

	/**
	 * Gets image format mapping.
	 *
	 * @return the image format mapping
	 */
	protected ImageFormatMapping getImageFormatMapping()
	{
		return imageFormatMapping;
	}

	/**
	 * Sets image format mapping.
	 *
	 * @param imageFormatMapping
	 * 		the image format mapping
	 */
	public void setImageFormatMapping(final ImageFormatMapping imageFormatMapping)
	{
		this.imageFormatMapping = imageFormatMapping;
	}


	/**
	 * Gets media service.
	 *
	 * @return the media service
	 */
	protected MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * Sets media service.
	 *
	 * @param mediaService
	 * 		the media service
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * Gets image converter.
	 *
	 * @return the image converter
	 */
	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	/**
	 * Sets image converter.
	 *
	 * @param imageConverter
	 * 		the image converter
	 */
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

	/**
	 * Gets media container service.
	 *
	 * @return the media container service
	 */
	protected MediaContainerService getMediaContainerService()
	{
		return mediaContainerService;
	}

	/**
	 * Sets media container service.
	 *
	 * @param mediaContainerService
	 * 		the media container service
	 */
	public void setMediaContainerService(final MediaContainerService mediaContainerService)
	{
		this.mediaContainerService = mediaContainerService;
	}

	/**
	 * Gets property facility converter.
	 *
	 * @return the propertyFacilityConverter
	 */
	protected AbstractPopulatingConverter<PropertyFacilityModel, FacilityData> getPropertyFacilityConverter()
	{
		return propertyFacilityConverter;
	}

	/**
	 * Sets property facility converter.
	 *
	 * @param propertyFacilityConverter
	 * 		the propertyFacilityConverter to set
	 */
	public void setPropertyFacilityConverter(
			final AbstractPopulatingConverter<PropertyFacilityModel, FacilityData> propertyFacilityConverter)
	{
		this.propertyFacilityConverter = propertyFacilityConverter;
	}

	/**
	 * Gets configured facility data comparator.
	 *
	 * @return the configuredFacilityDataComparator
	 */
	protected Comparator<FacilityData> getConfiguredFacilityDataComparator()
	{
		return configuredFacilityDataComparator;
	}

	/**
	 * Sets configured facility data comparator.
	 *
	 * @param configuredFacilityDataComparator
	 * 		the configuredFacilityDataComparator to set
	 */
	public void setConfiguredFacilityDataComparator(final Comparator<FacilityData> configuredFacilityDataComparator)
	{
		this.configuredFacilityDataComparator = configuredFacilityDataComparator;
	}

	/**
	 * Gets address converter.
	 *
	 * @return the addressConverter
	 */
	protected AbstractPopulatingConverter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	/**
	 * Sets address converter.
	 *
	 * @param addressConverter
	 * 		the addressConverter to set
	 */
	public void setAddressConverter(final AbstractPopulatingConverter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}
}
