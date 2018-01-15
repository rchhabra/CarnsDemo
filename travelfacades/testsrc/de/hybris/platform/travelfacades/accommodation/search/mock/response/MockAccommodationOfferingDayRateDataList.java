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

package de.hybris.platform.travelfacades.accommodation.search.mock.response;

import de.hybris.platform.commercefacades.accommodation.AccommodationOfferingDayRateData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.DiscountData;
import de.hybris.platform.commercefacades.user.data.AddressData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class to create mock list of accommodation offering day rates which will be later replaced by actual documents from
 * Solr
 */
public class MockAccommodationOfferingDayRateDataList
{

	private static String startDate = "01/12/2016";
	private static String endDate = "04/12/2016";

	public List<AccommodationOfferingDayRateData> buildDayRateDataListForCandidate(final int candidateRefNumber)
	{
		return buildDayRateDataList().stream().filter(rate -> rate.getRoomStayCandidateRefNumber().intValue() == candidateRefNumber)
				.collect(Collectors.toList());
	}

	public List<AccommodationOfferingDayRateData> buildDayRateDataList()
	{
		final List<AccommodationOfferingDayRateData> dayRateDataList = new ArrayList<>();
		dayRateDataList.addAll(buildFirstAccOffDocs());
		dayRateDataList.addAll(buildSecondAccOffDocs());
		dayRateDataList.addAll(buildThirdAccOffDocs());
		dayRateDataList.addAll(buildFourthAccOffDocs());
		dayRateDataList.addAll(buildNotValidPriceAccOffDocs());
		return dayRateDataList;
	}

	public Map<Integer, List<AccommodationOfferingDayRateData>> buildMapForSingleProperty()
	{
		return buildFirstAccOffDocs().stream()
				.collect(Collectors.groupingBy(AccommodationOfferingDayRateData::getRoomStayCandidateRefNumber));
	}

	private LocalDateTime getDateFromString(final String dateString)
	{
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return LocalDateTime.of(LocalDate.parse(dateString, formatter), LocalTime.NOON);
	}

	private List<AccommodationOfferingDayRateData> buildFirstAccOffDocs()
	{
		final List<AccommodationOfferingDayRateData> accOffDocs = new ArrayList<>();

		Stream.iterate(getDateFromString(startDate), date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(getDateFromString(startDate), getDateFromString(endDate))).forEach(date -> {

					final AccommodationOfferingDayRateData accommodationOfferingDayRateData0 = new AccommodationOfferingDayRateData();
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData1 = new AccommodationOfferingDayRateData();

					accommodationOfferingDayRateData0.setAccommodationOfferingCode("H_TEL_CALIFORNIA_CHAMPS_ELYS_ES");
					accommodationOfferingDayRateData0.setAccommodationOfferingName("Hôtel California Champs Elysées");
					accommodationOfferingDayRateData0.setLatitude(48.86486109);
					accommodationOfferingDayRateData0.setLongitude(2.28426844);
					final AddressData address0 = new AddressData();
					address0.setFormattedAddress("rue de Berri 16, 75008 Paris");
					accommodationOfferingDayRateData0.setAddressData(address0);

					accommodationOfferingDayRateData0.setStarRating(4);
					accommodationOfferingDayRateData0.setAverageUserRating(9.2);
					accommodationOfferingDayRateData0.setNumberOfReviews(39);
					accommodationOfferingDayRateData0.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData0.setAdultsCount(Integer.valueOf(3));
					accommodationOfferingDayRateData0.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData0.setAccommodationInfos(Arrays.asList("1_" + "Family Room"));
					accommodationOfferingDayRateData0.setRoomStayCandidateRefNumber(Integer.valueOf(0));

					accommodationOfferingDayRateData0.setPrice(buildPriceData(105.0));
					accommodationOfferingDayRateData0.setPromotionalDiscount(buildDiscountData(10.0));

					accOffDocs.add(accommodationOfferingDayRateData0);

					accommodationOfferingDayRateData1.setAccommodationOfferingCode("H_TEL_CALIFORNIA_CHAMPS_ELYS_ES");
					accommodationOfferingDayRateData1.setAccommodationOfferingName("Hôtel California Champs Elysées");
					accommodationOfferingDayRateData1.setLatitude(48.86486109);
					accommodationOfferingDayRateData1.setLongitude(2.28426844);
					final AddressData address1 = new AddressData();
					address1.setFormattedAddress("rue de Berri 16, 75008 Paris");
					accommodationOfferingDayRateData0.setAddressData(address1);

					accommodationOfferingDayRateData1.setStarRating(4);
					accommodationOfferingDayRateData1.setAverageUserRating(9.2);
					accommodationOfferingDayRateData1.setNumberOfReviews(39);
					accommodationOfferingDayRateData1.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData1.setAdultsCount(Integer.valueOf(2));
					accommodationOfferingDayRateData1.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData1.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData1.setAccommodationInfos(Arrays.asList("1_" + "Superior Double Room"));
					accommodationOfferingDayRateData1.setRoomStayCandidateRefNumber(Integer.valueOf(1));

					accommodationOfferingDayRateData1.setPrice(buildPriceData(75.0));
					accommodationOfferingDayRateData1.setPromotionalDiscount(buildDiscountData(10.0));

					accOffDocs.add(accommodationOfferingDayRateData1);

				});

		return accOffDocs;
	}

	private List<AccommodationOfferingDayRateData> buildSecondAccOffDocs()
	{
		final List<AccommodationOfferingDayRateData> accOffDocs = new ArrayList<>();

		Stream.iterate(getDateFromString(startDate), date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(getDateFromString(startDate), getDateFromString(endDate))).forEach(date -> {
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData0 = new AccommodationOfferingDayRateData();
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData1 = new AccommodationOfferingDayRateData();

					accommodationOfferingDayRateData0
							.setAccommodationOfferingCode("LE_MERIDIEN_HOTELS__AMP__RESORTS_LE_M_RIDIEN_ETOILE");
					accommodationOfferingDayRateData0.setAccommodationOfferingName("Le Méridien Etoile");
					accommodationOfferingDayRateData0.setChainCode("LE_MERIDIEN_HOTELS__AMP__RESORTS");
					accommodationOfferingDayRateData0.setChainName("Le Meridien Hotels &amp; Resorts");
					accommodationOfferingDayRateData0.setLatitude(48.83836882);
					accommodationOfferingDayRateData0.setLongitude(2.25017488);
					final AddressData address0 = new AddressData();
					address0.setFormattedAddress("Avenue Des Champs Elyses 74, 75008  Paris ");
					accommodationOfferingDayRateData0.setAddressData(address0);

					accommodationOfferingDayRateData0.setStarRating(5);
					accommodationOfferingDayRateData0.setAverageUserRating(9.5);
					accommodationOfferingDayRateData0.setNumberOfReviews(69);
					accommodationOfferingDayRateData0.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData0.setAdultsCount(Integer.valueOf(3));
					accommodationOfferingDayRateData0.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData0.setAccommodationInfos(Arrays.asList("1_" + "Urban Triple Room"));
					accommodationOfferingDayRateData0.setRoomStayCandidateRefNumber(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setPrice(buildPriceData(180.0));
					accommodationOfferingDayRateData0.setPromotionalDiscount(buildDiscountData(0.0));

					accOffDocs.add(accommodationOfferingDayRateData0);


					accommodationOfferingDayRateData1
							.setAccommodationOfferingCode("LE_MERIDIEN_HOTELS__AMP__RESORTS_LE_M_RIDIEN_ETOILE");
					accommodationOfferingDayRateData1.setAccommodationOfferingName("Le Méridien Etoile");
					accommodationOfferingDayRateData1.setChainCode("LE_MERIDIEN_HOTELS__AMP__RESORTS");
					accommodationOfferingDayRateData1.setChainName("Le Meridien Hotels &amp; Resorts");
					accommodationOfferingDayRateData1.setLatitude(48.83836882);
					accommodationOfferingDayRateData1.setLongitude(2.25017488);
					final AddressData address1 = new AddressData();
					address1.setFormattedAddress("Avenue Des Champs Elyses 74, 75008  Paris ");
					accommodationOfferingDayRateData0.setAddressData(address1);

					accommodationOfferingDayRateData1.setStarRating(5);
					accommodationOfferingDayRateData1.setAverageUserRating(9.5);
					accommodationOfferingDayRateData1.setNumberOfReviews(69);
					accommodationOfferingDayRateData1.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData1.setAdultsCount(Integer.valueOf(2));
					accommodationOfferingDayRateData1.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData1.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData1.setAccommodationInfos(Arrays.asList("1_" + "Urban Double or Twin Room"));
					accommodationOfferingDayRateData1.setRoomStayCandidateRefNumber(Integer.valueOf(1));
					accommodationOfferingDayRateData1.setPrice(buildPriceData(100.0));
					accommodationOfferingDayRateData1.setPromotionalDiscount(buildDiscountData(0.0));

					accOffDocs.add(accommodationOfferingDayRateData1);

				});

		return accOffDocs;
	}

	private List<AccommodationOfferingDayRateData> buildThirdAccOffDocs()
	{
		final List<AccommodationOfferingDayRateData> accOffDocs = new ArrayList<>();

		Stream.iterate(getDateFromString(startDate), date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(getDateFromString(startDate), getDateFromString(endDate))).forEach(date -> {
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData0 = new AccommodationOfferingDayRateData();
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData1 = new AccommodationOfferingDayRateData();

					accommodationOfferingDayRateData0.setAccommodationOfferingCode("MERCURE_PARIS_TERMINUS_NORD");
					accommodationOfferingDayRateData0.setAccommodationOfferingName("Mercure Paris Terminus Nord");
					accommodationOfferingDayRateData0.setLatitude(48.87597385);
					accommodationOfferingDayRateData0.setLongitude(2.35075101);
					final AddressData address0 = new AddressData();
					address0.setFormattedAddress("Boulevard De Denain 12, 75010 Paris");
					accommodationOfferingDayRateData0.setAddressData(address0);

					accommodationOfferingDayRateData0.setStarRating(4);
					accommodationOfferingDayRateData0.setAverageUserRating(8.9);
					accommodationOfferingDayRateData0.setNumberOfReviews(23);
					accommodationOfferingDayRateData0.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData0.setAdultsCount(Integer.valueOf(3));
					accommodationOfferingDayRateData0.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData0.setAccommodationInfos(Arrays.asList("1_" + "Privilege Double Room (3 Adults)"));
					accommodationOfferingDayRateData0.setRoomStayCandidateRefNumber(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setPrice(buildPriceData(85.0));
					accommodationOfferingDayRateData0.setPromotionalDiscount(buildDiscountData(20.0));

					accOffDocs.add(accommodationOfferingDayRateData0);

					accommodationOfferingDayRateData1.setAccommodationOfferingCode("MERCURE_PARIS_TERMINUS_NORD");
					accommodationOfferingDayRateData1.setAccommodationOfferingName("Mercure Paris Terminus Nord");
					accommodationOfferingDayRateData1.setLatitude(48.87597385);
					accommodationOfferingDayRateData1.setLongitude(2.35075101);
					final AddressData address1 = new AddressData();
					address1.setFormattedAddress("Boulevard De Denain 12, 75010 Paris");
					accommodationOfferingDayRateData0.setAddressData(address1);

					accommodationOfferingDayRateData1.setStarRating(4);
					accommodationOfferingDayRateData1.setAverageUserRating(8.9);
					accommodationOfferingDayRateData1.setNumberOfReviews(23);
					accommodationOfferingDayRateData1.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData1.setAdultsCount(Integer.valueOf(2));
					accommodationOfferingDayRateData1.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData1.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData1.setAccommodationInfos(Arrays.asList("1_" + "Superior Room with 1 Double Bed"));
					accommodationOfferingDayRateData1.setRoomStayCandidateRefNumber(Integer.valueOf(1));
					accommodationOfferingDayRateData1.setPrice(buildPriceData(60.0));
					accommodationOfferingDayRateData1.setPromotionalDiscount(buildDiscountData(0.0));

					accOffDocs.add(accommodationOfferingDayRateData1);

				});

		return accOffDocs;
	}

	private List<AccommodationOfferingDayRateData> buildFourthAccOffDocs()
	{
		final List<AccommodationOfferingDayRateData> accOffDocs = new ArrayList<>();

		Stream.iterate(getDateFromString(startDate), date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(getDateFromString(startDate), getDateFromString(endDate))).forEach(date -> {
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData0 = new AccommodationOfferingDayRateData();
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData1 = new AccommodationOfferingDayRateData();

					accommodationOfferingDayRateData0.setAccommodationOfferingCode("NOVOTEL_PARIS_SUD_PORTE_DE_CHARENTON");
					accommodationOfferingDayRateData0.setAccommodationOfferingName("Novotel Paris Sud Porte de Charenton");
					accommodationOfferingDayRateData0.setLatitude(48.78995976);
					accommodationOfferingDayRateData0.setLongitude(2.28327759);
					final AddressData address0 = new AddressData();
					address0.setFormattedAddress("place des Marseillais 35, Paris");
					accommodationOfferingDayRateData0.setAddressData(address0);

					accommodationOfferingDayRateData0.setStarRating(4);
					accommodationOfferingDayRateData0.setAverageUserRating(8.3);
					accommodationOfferingDayRateData0.setNumberOfReviews(44);
					accommodationOfferingDayRateData0.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData0.setAdultsCount(Integer.valueOf(3));
					accommodationOfferingDayRateData0.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData0.setAccommodationInfos(Arrays.asList("1_" + "Triple Room"));
					accommodationOfferingDayRateData0.setRoomStayCandidateRefNumber(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setPrice(buildPriceData(100.0));
					accommodationOfferingDayRateData0.setPromotionalDiscount(buildDiscountData(15.0));

					accOffDocs.add(accommodationOfferingDayRateData0);

					accommodationOfferingDayRateData1.setAccommodationOfferingCode("NOVOTEL_PARIS_SUD_PORTE_DE_CHARENTON");
					accommodationOfferingDayRateData1.setAccommodationOfferingName("Novotel Paris Sud Porte de Charenton");
					accommodationOfferingDayRateData1.setLatitude(48.78995976);
					accommodationOfferingDayRateData1.setLongitude(2.28327759);
					final AddressData address1 = new AddressData();
					address1.setFormattedAddress("place des Marseillais 35, Paris");
					accommodationOfferingDayRateData0.setAddressData(address1);

					accommodationOfferingDayRateData1.setStarRating(4);
					accommodationOfferingDayRateData1.setAverageUserRating(8.3);
					accommodationOfferingDayRateData1.setNumberOfReviews(44);
					accommodationOfferingDayRateData1.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData1.setAdultsCount(Integer.valueOf(2));
					accommodationOfferingDayRateData1.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData1.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData1.setAccommodationInfos(Arrays.asList("1_" + "Executive Double Room"));
					accommodationOfferingDayRateData1.setRoomStayCandidateRefNumber(Integer.valueOf(1));
					accommodationOfferingDayRateData1.setPrice(buildPriceData(80.0));
					accommodationOfferingDayRateData1.setPromotionalDiscount(buildDiscountData(15.0));

					accOffDocs.add(accommodationOfferingDayRateData1);

				});

		return accOffDocs;
	}

	private List<AccommodationOfferingDayRateData> buildNotValidPriceAccOffDocs()
	{
		final List<AccommodationOfferingDayRateData> accOffDocs = new ArrayList<>();

		Stream.iterate(getDateFromString(startDate), date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(getDateFromString(startDate), getDateFromString(endDate)) - 1).forEach(date -> {

					final AccommodationOfferingDayRateData accommodationOfferingDayRateData0 = new AccommodationOfferingDayRateData();
					final AccommodationOfferingDayRateData accommodationOfferingDayRateData1 = new AccommodationOfferingDayRateData();

					accommodationOfferingDayRateData0.setAccommodationOfferingCode("DAUNOU_OP_RA");
					accommodationOfferingDayRateData0.setAccommodationOfferingName("Daunou Opéra");
					accommodationOfferingDayRateData0.setLatitude(48.86651464);
					accommodationOfferingDayRateData0.setLongitude(2.29327759);
					final AddressData address0 = new AddressData();
					address0.setFormattedAddress("Rue des Petits Htels 7, 75010 Paris");
					accommodationOfferingDayRateData0.setAddressData(address0);

					accommodationOfferingDayRateData0.setStarRating(4);
					accommodationOfferingDayRateData0.setAverageUserRating(8.7);
					accommodationOfferingDayRateData0.setNumberOfReviews(87);
					accommodationOfferingDayRateData0.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData0.setAdultsCount(Integer.valueOf(3));
					accommodationOfferingDayRateData0.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData0.setAccommodationInfos(Arrays.asList("1_" + "Triple Room"));
					accommodationOfferingDayRateData0.setRoomStayCandidateRefNumber(Integer.valueOf(0));
					accommodationOfferingDayRateData0.setPrice(buildPriceData(125.0));
					accommodationOfferingDayRateData0.setPromotionalDiscount(buildDiscountData(0.0));

					accOffDocs.add(accommodationOfferingDayRateData0);

					accommodationOfferingDayRateData1.setAccommodationOfferingCode("DAUNOU_OP_RA");
					accommodationOfferingDayRateData1.setAccommodationOfferingName("Daunou Opéra");
					accommodationOfferingDayRateData1.setLatitude(48.86651464);
					accommodationOfferingDayRateData1.setLongitude(2.29327759);
					final AddressData address1 = new AddressData();
					address1.setFormattedAddress("Rue des Petits Htels 7, 75010 Paris");
					accommodationOfferingDayRateData0.setAddressData(address1);

					accommodationOfferingDayRateData1.setStarRating(4);
					accommodationOfferingDayRateData1.setAverageUserRating(8.7);
					accommodationOfferingDayRateData1.setNumberOfReviews(87);
					accommodationOfferingDayRateData1.setDateOfStay(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

					accommodationOfferingDayRateData1.setAdultsCount(Integer.valueOf(2));
					accommodationOfferingDayRateData1.setChildrenCountMin(Integer.valueOf(0));
					accommodationOfferingDayRateData1.setChildrenCountMax(Integer.valueOf(2));

					accommodationOfferingDayRateData1.setAccommodationInfos(Arrays.asList("1_" + "Double or Twin Room"));
					accommodationOfferingDayRateData1.setRoomStayCandidateRefNumber(Integer.valueOf(1));
					accommodationOfferingDayRateData1.setPrice(buildPriceData(80.0));
					accommodationOfferingDayRateData1.setPromotionalDiscount(buildDiscountData(0.0));

					accOffDocs.add(accommodationOfferingDayRateData1);

				});

		return accOffDocs;
	}


	private DiscountData buildDiscountData(final Double value)
	{
		final DiscountData discountData = new DiscountData();
		discountData.setPrice(buildPriceData(value));
		return discountData;
	}

	private PriceData buildPriceData(final Double value)
	{
		final PriceData priceData = new PriceData();
		priceData.setPriceType(PriceDataType.BUY);
		priceData.setCurrencyIso("GBP");
		priceData.setValue(BigDecimal.valueOf(value));
		return priceData;
	}
}
