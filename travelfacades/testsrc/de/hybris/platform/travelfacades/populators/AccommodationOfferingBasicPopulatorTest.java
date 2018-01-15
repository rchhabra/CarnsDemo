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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.property.FacilityData;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.travelservices.model.accommodation.AccommodationOfferingGalleryModel;
import de.hybris.platform.travelservices.model.facility.PropertyFacilityModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingGalleryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for @{link = AccommodationOfferingBasicPopulator} implementation
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AccommodationOfferingBasicPopulatorTest
{
	private final String TEST_ACCOMMODATION_OFFERING_CODE = "ACCOMMODATION_OFFERING_CODE_TEST";
	private final String TEST_ACCOMMODATION_OFFERING_GALLERY_CODE = "TEST_ACCOMMODATION_OFFERING_GALLERY_CODE";
	@InjectMocks
	private AccommodationOfferingBasicPopulator accommodationOfferingBasicPopulator;

	@Mock
	private AbstractPopulatingConverter<PropertyFacilityModel, FacilityData> propertyFacilityConverter;

	@Mock
	private Comparator<FacilityData> configuredFacilityDataComparator;

	@Mock
	private AccommodationOfferingGalleryService accommodationOfferingGalleryService;

	@Mock
	private CatalogVersionService catalogVersionService;

	private final String TEST_IMAGE_FORMAT_A = "TEST_IMAGE_FORMAT_A";
	private final String TEST_IMAGE_FORMAT_B = "TEST_IMAGE_FORMAT_B";
	private final String TEST_IMAGE_FORMAT_C = "TEST_IMAGE_FORMAT_C";
	private final String TEST_IMAGE_FORMAT_D = "TEST_IMAGE_FORMAT_D";
	private final String TEST_IMAGE_FORMAT_E = "TEST_IMAGE_FORMAT_E";
	private final String TEST_MEDIA_FORMAT_QUALIFIER_B = "TEST_MEDIA_FORMAT_QUALIFIER_B";
	private final String TEST_MEDIA_FORMAT_QUALIFIER_C = "TEST_MEDIA_FORMAT_QUALIFIER_C";
	private final String TEST_MEDIA_FORMAT_QUALIFIER_D = "TEST_MEDIA_FORMAT_QUALIFIER_D";
	private final String TEST_MEDIA_FORMAT_QUALIFIER_E = "TEST_MEDIA_FORMAT_QUALIFIER_E";
	private final List<String> imageFormats = Arrays.asList(TEST_IMAGE_FORMAT_A, TEST_IMAGE_FORMAT_B, TEST_IMAGE_FORMAT_C,
			TEST_IMAGE_FORMAT_D, TEST_IMAGE_FORMAT_E);

	@Mock
	private ImageFormatMapping imageFormatMapping;

	@Mock
	private MediaContainerService mediaContainerService;

	@Mock
	private MediaService mediaService;

	@Mock
	private Converter<MediaModel, ImageData> imageConverter;

	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;

	private List<PropertyFacilityModel> activePropertyList;

	@Before
	public void setUp()
	{
		accommodationOfferingBasicPopulator.setImageFormats(imageFormats);
		given(addressConverter.convert(Matchers.any(AddressModel.class))).willReturn(null);
		given(propertyFacilityConverter.convertAll(activePropertyList)).willReturn(Collections.emptyList());
		given(configuredFacilityDataComparator.compare(Matchers.any(FacilityData.class), Matchers.any(FacilityData.class)))
				.willReturn(0);
	}

	@Test
	public void populateBasicPropertyDataTest()
	{
		final CatalogModel catalog = createCatalog("test");
		final CatalogVersionModel catalogVersionModel = createCatalogVersion(catalog, "online", Boolean.TRUE);

		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<>();
		catalogVersions.add(catalogVersionModel);
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		final MediaContainerModel testMediaContainerModel_B = new MediaContainerModel();
		final MediaContainerModel testMediaContainerModel_D = new MediaContainerModel();
		final MediaContainerModel testMediaContainerModel_E = new MediaContainerModel();
		final List<MediaContainerModel> gallery = new ArrayList<>();
		gallery.add(testMediaContainerModel_B);
		gallery.add(testMediaContainerModel_D);
		gallery.add(testMediaContainerModel_E);

		final AccommodationOfferingGalleryModel accommodationOfferingGalleryModel = new AccommodationOfferingGalleryModel();
		accommodationOfferingGalleryModel.setGallery(gallery);


		given(accommodationOfferingGalleryService.getAccommodationOfferingGallery(TEST_ACCOMMODATION_OFFERING_GALLERY_CODE,
				catalogVersionModel)).willReturn(accommodationOfferingGalleryModel);

		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(TEST_IMAGE_FORMAT_A)).willReturn(null);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(TEST_IMAGE_FORMAT_B))
				.willReturn(TEST_MEDIA_FORMAT_QUALIFIER_B);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(TEST_IMAGE_FORMAT_C))
				.willReturn(TEST_MEDIA_FORMAT_QUALIFIER_C);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(TEST_IMAGE_FORMAT_D))
				.willReturn(TEST_MEDIA_FORMAT_QUALIFIER_D);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(TEST_IMAGE_FORMAT_E))
				.willReturn(TEST_MEDIA_FORMAT_QUALIFIER_E);


		final MediaFormatModel mediaFormat_B = new MediaFormatModel();
		mediaFormat_B.setExternalID("B");
		final MediaFormatModel mediaFormat_D = new MediaFormatModel();
		mediaFormat_D.setExternalID("D");
		final MediaFormatModel mediaFormat_E = new MediaFormatModel();
		mediaFormat_E.setExternalID("E");
		given(mediaService.getFormat(TEST_MEDIA_FORMAT_QUALIFIER_B)).willReturn(mediaFormat_B);
		given(mediaService.getFormat(TEST_MEDIA_FORMAT_QUALIFIER_C)).willReturn(null);
		given(mediaService.getFormat(TEST_MEDIA_FORMAT_QUALIFIER_D)).willReturn(mediaFormat_D);
		given(mediaService.getFormat(TEST_MEDIA_FORMAT_QUALIFIER_E)).willReturn(mediaFormat_E);
		final MediaModel mediaModel = new MediaModel();

		given(mediaContainerService.getMediaForFormat(testMediaContainerModel_B, mediaFormat_B)).willReturn(mediaModel);
		given(mediaContainerService.getMediaForFormat(testMediaContainerModel_D, mediaFormat_D)).willReturn(null);
		given(mediaContainerService.getMediaForFormat(testMediaContainerModel_E, mediaFormat_E))
				.willThrow(new ModelNotFoundException("Model Not Found Exception"));
		final ImageData imageData = new ImageData();
		given(imageConverter.convert(mediaModel)).willReturn(imageData);
		final AccommodationOfferingModel source = createAccommodationOfferingModel();
		given(source.getGalleryCode()).willReturn(TEST_ACCOMMODATION_OFFERING_GALLERY_CODE);
		given(source.getAverageUserRating()).willReturn(new Double(5));
		given(source.getStarRating()).willReturn(new Integer(5));
		final PropertyData target = new PropertyData();
		accommodationOfferingBasicPopulator.populate(source, target);
		Assert.assertEquals(TEST_ACCOMMODATION_OFFERING_CODE, target.getAccommodationOfferingCode());
	}

	@Test
	public void populateBasicPropertyDataTestForNullScenarios()
	{
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<>();
		catalogVersions.add(catalogVersionModel);
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(null);
		given(accommodationOfferingGalleryService.getAccommodationOfferingGallery(TEST_ACCOMMODATION_OFFERING_GALLERY_CODE, null))
				.willReturn(null);
		final AccommodationOfferingModel source = createAccommodationOfferingModel();
		given(source.getGalleryCode()).willReturn(null);
		given(source.getAverageUserRating()).willReturn(null);
		given(source.getStarRating()).willReturn(null);
		final PropertyData target = new PropertyData();
		accommodationOfferingBasicPopulator.populate(source, target);

		Assert.assertEquals(TEST_ACCOMMODATION_OFFERING_CODE, target.getAccommodationOfferingCode());
	}

	private AccommodationOfferingModel createAccommodationOfferingModel()
	{
		final AccommodationOfferingModel accommodationOfferingModel = Mockito.mock(AccommodationOfferingModel.class);

		given(accommodationOfferingModel.getCode()).willReturn(TEST_ACCOMMODATION_OFFERING_CODE);
		given(accommodationOfferingModel.getDescription()).willReturn(null);
		given(accommodationOfferingModel.getPropertyInformation()).willReturn(null);
		final PointOfServiceModel pointService = new PointOfServiceModel();
		pointService.setAddress(new AddressModel());
		pointService.setLongitude(new Double(90));
		pointService.setLatitude(new Double(90));
		final LocationModel location = new LocationModel();
		location.setPointOfService(Arrays.asList(pointService));
		given(accommodationOfferingModel.getLocation()).willReturn(location);
		given(accommodationOfferingModel.getGalleryCode()).willReturn(TEST_ACCOMMODATION_OFFERING_GALLERY_CODE);

		return accommodationOfferingModel;
	}

	private CatalogModel createCatalog(final String id)
	{
		final CatalogModel catalog = new CatalogModel();
		catalog.setId(id);
		return catalog;
	}

	private CatalogVersionModel createCatalogVersion(final CatalogModel catalog, final String version, final Boolean active)
	{
		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion(version);
		catalogVersion.setActive(active);
		return catalogVersion;
	}
}
