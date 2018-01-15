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

package de.hybris.platform.travelfacades.ancillary.search.utils;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TransportFacilityData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.commercefacades.travel.TravelSectorData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * MOCK ANCILLARY OFFER RESPONSE OBJECT SETUP
 */
public class MockAncillaryOfferDataUtils
{
	/**
	 * This method provides with the mock offer response which is used for the display of ancillaries in the ancillary
	 * page.
	 *
	 * @return OfferResponseData
	 */
	public OfferResponseData prepareMockAncillaryOffersResponseData()
	{

		final List<OfferGroupData> offerGroupDataList = prepareMockOfferGroupData();

		return createOfferResponseData(offerGroupDataList);
	}

	private OfferResponseData createOfferResponseData(final List<OfferGroupData> offerGroupDataList)
	{
		final OfferResponseData offerResponseData = new OfferResponseData();

		//Create Travelers
		final List<TravellerData> travellers = new ArrayList<>();
		final PassengerTypeData adultPassengerType = createAdultPassengerType();
		final TravellerData firstAdultPassenger = createPassenger(adultPassengerType, "adult1", "John", "Doe");
		final TravellerData secondAdultPassenger = createPassenger(adultPassengerType, "adult2", "Mary", "Doe");
		final PassengerTypeData childrenPassengerType = createChildrenPassengerType();
		final TravellerData childPassenger = createPassenger(childrenPassengerType, "child1", "Jack", "Doe");

		//Populating traveler list
		travellers.add(firstAdultPassenger);
		travellers.add(secondAdultPassenger);
		travellers.add(childPassenger);

		//Populating Itinerary Data
		final List<ItineraryData> itineraries = new ArrayList<>();
		//EDI_LGW
		final ItineraryData outboundItenerary1 = createOutboundIteneraryWithRoute();
		//LGW_CDG
		final ItineraryData outboundItenerary2 = createOutboundIteneraryWithRoute();
		//CDG_LGW
		final ItineraryData inboundItenerary1 = createInboundIteneraryWithRoute();
		//LGW_EDI
		final ItineraryData inboundItenerary2 = createInboundIteneraryWithRoute();

		outboundItenerary1.setTravellers(travellers);
		inboundItenerary1.setTravellers(travellers);
		outboundItenerary2.setTravellers(travellers);
		inboundItenerary2.setTravellers(travellers);
		itineraries.add(outboundItenerary1);
		itineraries.add(inboundItenerary1);
		itineraries.add(outboundItenerary2);
		itineraries.add(inboundItenerary2);
		offerResponseData.setItineraries(itineraries);

		offerResponseData.setOfferGroups(offerGroupDataList);
		return offerResponseData;
	}

	private List<OfferGroupData> prepareMockOfferGroupData()
	{

		final List<OfferGroupData> offerGroupDataList = new ArrayList<OfferGroupData>();

		//Create Travelers
		final PassengerTypeData adultPassengerType = createAdultPassengerType();
		final TravellerData firstAdultPassenger = createPassenger(adultPassengerType, "adult1", "John", "Doe");
		final TravellerData secondAdultPassenger = createPassenger(adultPassengerType, "adult2", "Mary", "Doe");
		final PassengerTypeData childrenPassengerType = createChildrenPassengerType();
		final TravellerData childPassenger = createPassenger(childrenPassengerType, "child1", "Jack", "Doe");

		//Populating Transport Offering
		final List<TransportOfferingData> outbound1TransportOfferingDataList = new ArrayList<TransportOfferingData>();
		outbound1TransportOfferingDataList.add(createOutbound1TransportOfferingData());
		final List<TransportOfferingData> outbound2TransportOfferingDataList = new ArrayList<TransportOfferingData>();
		outbound2TransportOfferingDataList.add(createOutbound2TransportOfferingData());
		final List<TransportOfferingData> inbound1TransportOfferingDataList = new ArrayList<TransportOfferingData>();
		inbound1TransportOfferingDataList.add(createInbound1TransportOfferingData());
		final List<TransportOfferingData> inbound2TransportOfferingDataList = new ArrayList<TransportOfferingData>();
		inbound2TransportOfferingDataList.add(createInbound2TransportOfferingData());

		// populating Hold Allowance Offer group data
		final OfferGroupData holdAllowanceOfferGroupData = new OfferGroupData();
		holdAllowanceOfferGroupData.setCode("HOLD_ITEM");
		holdAllowanceOfferGroupData.setName("Hold Allowance");
		holdAllowanceOfferGroupData.setDescription("Hold Allowance");
		final List<TransportOfferingData> multiSectorOutboundTransportOfferingDataList = createMultiSectorOutboundTransportOfferingDataList();
		final List<TransportOfferingData> multiSectorInboundTransportOfferingDataList = createMultiSectorInboundTransportOfferingDataList();
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfoHoldAllowance = new ArrayList<OriginDestinationOfferInfoData>();
		final OriginDestinationOfferInfoData originDestinationOfferInfoOutboundHoldAllowance = createOriginDestinationOfferInfo(
				multiSectorOutboundTransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOfferInfoInboundHoldAllowance = createOriginDestinationOfferInfo(
				multiSectorInboundTransportOfferingDataList);
		originDestinationOfferInfoHoldAllowance.add(originDestinationOfferInfoOutboundHoldAllowance);
		originDestinationOfferInfoHoldAllowance.add(originDestinationOfferInfoInboundHoldAllowance);
		createOfferPricingInfoData(originDestinationOfferInfoHoldAllowance, firstAdultPassenger, secondAdultPassenger,
				childPassenger, "EXTRABAG20");
		createOfferPricingInfoData(originDestinationOfferInfoHoldAllowance, firstAdultPassenger, secondAdultPassenger,
				childPassenger, "EXTRABAG32");
		holdAllowanceOfferGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfoHoldAllowance);
		offerGroupDataList.add(holdAllowanceOfferGroupData);


		// populating meals offer group data
		final OfferGroupData mealOfferGroupData = new OfferGroupData();
		mealOfferGroupData.setCode("MEAL");
		mealOfferGroupData.setName("Pre-Order Meals");
		mealOfferGroupData.setDescription("Meals on Offer");
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfosMeals = new ArrayList<OriginDestinationOfferInfoData>();
		final OriginDestinationOfferInfoData originDestinationOptionDataInboundMeals = createOriginDestinationOfferInfo(
				inbound1TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataInbound2Meals = createOriginDestinationOfferInfo(
				inbound2TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataOutboundMeals = createOriginDestinationOfferInfo(
				outbound1TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataOutbound2Meals = createOriginDestinationOfferInfo(
				outbound2TransportOfferingDataList);
		originDestinationOfferInfosMeals.add(originDestinationOptionDataOutboundMeals);
		originDestinationOfferInfosMeals.add(originDestinationOptionDataOutbound2Meals);
		originDestinationOfferInfosMeals.add(originDestinationOptionDataInboundMeals);
		originDestinationOfferInfosMeals.add(originDestinationOptionDataInbound2Meals);


		createOfferPricingInfoData(originDestinationOfferInfosMeals, firstAdultPassenger, secondAdultPassenger, null,
				"ADULTMEAL");
		createOfferPricingInfoData(originDestinationOfferInfosMeals, firstAdultPassenger, secondAdultPassenger, childPassenger,
				"SMALLMEAL");
		mealOfferGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfosMeals);
		offerGroupDataList.add(mealOfferGroupData);

		// populating Priority Boarding Offer group data
		final OfferGroupData priorityOfferGroupData = new OfferGroupData();
		priorityOfferGroupData.setCode("PRIORITY_BOARDING");
		priorityOfferGroupData.setName("Priority Boarding");
		priorityOfferGroupData.setDescription("Priority Boarding");
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfosPriority = new ArrayList<OriginDestinationOfferInfoData>();
		final OriginDestinationOfferInfoData originDestinationOptionDataOutbound1Priority = createOriginDestinationOfferInfo(
				outbound1TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataOutbound2Priority = createOriginDestinationOfferInfo(
				outbound2TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataInbound1Priority = createOriginDestinationOfferInfo(
				inbound1TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataInbound2Priority = createOriginDestinationOfferInfo(
				inbound2TransportOfferingDataList);
		originDestinationOfferInfosPriority.add(originDestinationOptionDataOutbound1Priority);
		originDestinationOfferInfosPriority.add(originDestinationOptionDataOutbound2Priority);
		originDestinationOfferInfosPriority.add(originDestinationOptionDataInbound1Priority);
		originDestinationOfferInfosPriority.add(originDestinationOptionDataInbound2Priority);

		createOfferPricingInfo(originDestinationOfferInfosPriority, firstAdultPassenger, secondAdultPassenger, childPassenger,
				"PRIORITYBOARDING");
		priorityOfferGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfosPriority);
		offerGroupDataList.add(priorityOfferGroupData);

		// populating Priority Checking Offer group data
		final OfferGroupData priorityCheckingOfferGroupData = new OfferGroupData();
		priorityCheckingOfferGroupData.setCode("PRIORITY_CHECKIN");
		priorityCheckingOfferGroupData.setName("Priority Checkin");
		priorityCheckingOfferGroupData.setDescription("Priority Checkin");
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfosPriorityCheckin = new ArrayList<OriginDestinationOfferInfoData>();
		final OriginDestinationOfferInfoData originDestinationOptionDataOutboundPriorityCheckin = createOriginDestinationOfferInfo(
				multiSectorOutboundTransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataInboundPriorityCheckin = createOriginDestinationOfferInfo(
				multiSectorInboundTransportOfferingDataList);

		originDestinationOfferInfosPriorityCheckin.add(originDestinationOptionDataOutboundPriorityCheckin);
		originDestinationOfferInfosPriorityCheckin.add(originDestinationOptionDataInboundPriorityCheckin);

		createOfferPricingInfo(originDestinationOfferInfosPriorityCheckin, firstAdultPassenger, secondAdultPassenger,
				childPassenger, "PRIORITYCHECKIN");
		priorityCheckingOfferGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfosPriorityCheckin);
		offerGroupDataList.add(priorityCheckingOfferGroupData);

		// populating Lounge Offer group data
		final OfferGroupData loungeOfferGroupData = new OfferGroupData();
		loungeOfferGroupData.setCode("LOUNGE");
		loungeOfferGroupData.setName("Lounge Access");
		loungeOfferGroupData.setDescription("LOUNGE ACCESS");
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfosLounge = new ArrayList<OriginDestinationOfferInfoData>();
		final OriginDestinationOfferInfoData originDestinationOptionDataOutbound1Lounge = createOriginDestinationOfferInfo(
				outbound1TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataOutbound2Lounge = createOriginDestinationOfferInfo(
				outbound2TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataInbound1Lounge = createOriginDestinationOfferInfo(
				inbound1TransportOfferingDataList);
		final OriginDestinationOfferInfoData originDestinationOptionDataInbound2Lounge = createOriginDestinationOfferInfo(
				inbound2TransportOfferingDataList);
		originDestinationOfferInfosLounge.add(originDestinationOptionDataOutbound1Lounge);
		originDestinationOfferInfosLounge.add(originDestinationOptionDataOutbound2Lounge);
		originDestinationOfferInfosLounge.add(originDestinationOptionDataInbound1Lounge);
		originDestinationOfferInfosLounge.add(originDestinationOptionDataInbound2Lounge);
		createOfferPricingInfo(originDestinationOfferInfosLounge, firstAdultPassenger, secondAdultPassenger, childPassenger,
				"LOUNGEACCESS");
		loungeOfferGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfosLounge);
		offerGroupDataList.add(loungeOfferGroupData);


		return offerGroupDataList;
	}

	private OriginDestinationOfferInfoData createOriginDestinationOfferInfo(
			final List<TransportOfferingData> transportOfferingDataList)
	{
		final OriginDestinationOfferInfoData originDestinationOfferInfo = new OriginDestinationOfferInfoData();
		originDestinationOfferInfo.setTransportOfferings(transportOfferingDataList);
		return originDestinationOfferInfo;
	}


	private void createOfferPricingInfoData(final List<OriginDestinationOfferInfoData> originDestinationOfferInfos,
			final TravellerData firstAdultPassenger, final TravellerData secondAdultPassenger, final TravellerData childPassenger,
			final String ancillaryProductCode)
	{
		if (CollectionUtils.isNotEmpty(originDestinationOfferInfos))
		{
			for (final OriginDestinationOfferInfoData originDestinationOfferInfo : originDestinationOfferInfos)
			{
				final List<OfferPricingInfoData> pricingInfo = new ArrayList<OfferPricingInfoData>();
				final OfferPricingInfoData pricinginfoData = new OfferPricingInfoData();
				final ProductData ancillaryProduct = new ProductData();
				ancillaryProduct.setCode(ancillaryProductCode);
				pricinginfoData.setProduct(ancillaryProduct);
				pricinginfoData.setBundleIndicator(1);
				final TravelRestrictionData restrictionData = createOfferRestrictionData();
				pricinginfoData.setTravelRestriction(restrictionData);
				final List<TravellerBreakdownData> travellerBreakDowns = new ArrayList<>();

				final TravellerBreakdownData firstAdultTravellerBreakdown = new TravellerBreakdownData();
				firstAdultTravellerBreakdown.setTraveller(firstAdultPassenger);
				final PassengerFareData firstPassengerFare = new PassengerFareData();
				populatePassengerFare(firstPassengerFare);
				firstAdultTravellerBreakdown.setPassengerFare(firstPassengerFare);
				firstAdultTravellerBreakdown.setQuantity(1);
				if (null != secondAdultPassenger)
				{
					final TravellerBreakdownData secondAdultTravellerBreakdown = new TravellerBreakdownData();
					secondAdultTravellerBreakdown.setTraveller(secondAdultPassenger);
					final PassengerFareData secondPassengerFare = new PassengerFareData();
					populatePassengerFare(secondPassengerFare);
					secondAdultTravellerBreakdown.setPassengerFare(secondPassengerFare);
					secondAdultTravellerBreakdown.setQuantity(1);
					travellerBreakDowns.add(secondAdultTravellerBreakdown);
				}
				if (null != childPassenger)
				{
					final TravellerBreakdownData childTravellerBreakdown = new TravellerBreakdownData();
					childTravellerBreakdown.setTraveller(childPassenger);
					final PassengerFareData childPassengerFare = new PassengerFareData();
					populatePassengerFare(childPassengerFare);
					childTravellerBreakdown.setPassengerFare(childPassengerFare);
					childTravellerBreakdown.setQuantity(0);
					travellerBreakDowns.add(childTravellerBreakdown);
				}

				travellerBreakDowns.add(firstAdultTravellerBreakdown);
				pricinginfoData.setTravellerBreakdowns(travellerBreakDowns);
				pricingInfo.add(pricinginfoData);
				if (null != originDestinationOfferInfo.getOfferPricingInfos())
				{
					originDestinationOfferInfo.getOfferPricingInfos().add(pricinginfoData);
				}
				else
				{
					originDestinationOfferInfo.setOfferPricingInfos(pricingInfo);
				}
			}
		}

	}

	private void createOfferPricingInfo(final List<OriginDestinationOfferInfoData> offerPricingInfos,
			final TravellerData firstAdultPassenger, final TravellerData secondAdultPassenger, final TravellerData childPassenger,
			final String ancillaryProductCode)
	{
		if (CollectionUtils.isNotEmpty(offerPricingInfos))
		{
			for (final OriginDestinationOfferInfoData originDestinationOfferInfo : offerPricingInfos)
			{
				final List<OfferPricingInfoData> pricingInfo = new ArrayList<OfferPricingInfoData>();
				final OfferPricingInfoData pricinginfoData = new OfferPricingInfoData();
				final ProductData ancillaryProduct = new ProductData();
				ancillaryProduct.setCode(ancillaryProductCode);
				pricinginfoData.setProduct(ancillaryProduct);
				pricinginfoData.setBundleIndicator(1);
				final TravelRestrictionData restrictionData = createRestrictionData();
				pricinginfoData.setTravelRestriction(restrictionData);
				final List<TravellerBreakdownData> travellerBreakDowns = new ArrayList<>();

				final TravellerBreakdownData firstAdultTravellerBreakdown = new TravellerBreakdownData();
				firstAdultTravellerBreakdown.setTraveller(firstAdultPassenger);
				final PassengerFareData firstPassengerFare = new PassengerFareData();
				populatePassengerFare(firstPassengerFare);
				firstAdultTravellerBreakdown.setPassengerFare(firstPassengerFare);
				firstAdultTravellerBreakdown.setQuantity(1);
				final TravellerBreakdownData secondAdultTravellerBreakdown = new TravellerBreakdownData();
				secondAdultTravellerBreakdown.setTraveller(secondAdultPassenger);
				final PassengerFareData secondPassengerFare = new PassengerFareData();
				populatePassengerFare(secondPassengerFare);
				secondAdultTravellerBreakdown.setPassengerFare(secondPassengerFare);
				secondAdultTravellerBreakdown.setQuantity(1);
				travellerBreakDowns.add(secondAdultTravellerBreakdown);
				final TravellerBreakdownData childTravellerBreakdown = new TravellerBreakdownData();
				childTravellerBreakdown.setTraveller(childPassenger);
				final PassengerFareData childPassengerFare = new PassengerFareData();
				populatePassengerFare(childPassengerFare);
				childTravellerBreakdown.setPassengerFare(childPassengerFare);
				childTravellerBreakdown.setQuantity(0);
				travellerBreakDowns.add(childTravellerBreakdown);

				travellerBreakDowns.add(firstAdultTravellerBreakdown);
				pricinginfoData.setTravellerBreakdowns(travellerBreakDowns);
				pricingInfo.add(pricinginfoData);
				originDestinationOfferInfo.setOfferPricingInfos(pricingInfo);
			}
		}

	}


	private void populatePassengerFare(final PassengerFareData passengerFare)
	{
		final PriceData baseFare = new PriceData();
		baseFare.setFormattedValue("15.00");
		passengerFare.setBaseFare(baseFare);
		passengerFare.setDiscounts(null);
		passengerFare.setFees(null);
		passengerFare.setTaxes(null);
		final PriceData totalFare = new PriceData();
		totalFare.setFormattedValue("15.00");
		passengerFare.setTotalFare(totalFare);
	}

	private TravelRestrictionData createOfferRestrictionData()
	{
		final TravelRestrictionData restrictionData = new TravelRestrictionData();
		restrictionData.setTravellerMinOfferQty(1);
		restrictionData.setTravellerMaxOfferQty(9);
		return restrictionData;
	}

	private TravelRestrictionData createRestrictionData()
	{
		final TravelRestrictionData restrictionData = new TravelRestrictionData();
		restrictionData.setTravellerMinOfferQty(1);
		restrictionData.setTravellerMaxOfferQty(1);
		return restrictionData;
	}

	private List<TransportOfferingData> createMultiSectorOutboundTransportOfferingDataList()
	{
		final TransportOfferingData transportOfferingDataOutbound1 = createOutbound1TransportOfferingData();

		final TransportOfferingData transportOfferingDataOutbound2 = createOutbound2TransportOfferingData();

		final List<TransportOfferingData> transportOfferingsOutbound = new ArrayList<>();
		transportOfferingsOutbound.add(transportOfferingDataOutbound1);
		transportOfferingsOutbound.add(transportOfferingDataOutbound2);
		return transportOfferingsOutbound;

	}

	private TransportOfferingData createOutbound1TransportOfferingData()
	{
		final TransportOfferingData transportOfferingDataInbound1 = new TransportOfferingData();
		transportOfferingDataInbound1.setCode("EZY8322010120160730");
		final Calendar departTimeCal1 = Calendar.getInstance();
		departTimeCal1.set(2016, 1, 1, 7, 30, 0);
		transportOfferingDataInbound1.setDepartureTime(departTimeCal1.getTime());
		transportOfferingDataInbound1.setNumber("EZY8322");
		final TravelSectorData sectorOutbound1 = new TravelSectorData();
		sectorOutbound1.setOrigin(createTransportFacility("EDI"));
		sectorOutbound1.setDestination(createTransportFacility("LGW"));
		transportOfferingDataInbound1.setSector(sectorOutbound1);
		return transportOfferingDataInbound1;
	}

	private TransportOfferingData createOutbound2TransportOfferingData()
	{
		final TransportOfferingData transportOfferingDatOutbound1 = new TransportOfferingData();
		transportOfferingDatOutbound1.setCode("EZY0805010120161530");
		final Calendar departTimeCal2 = Calendar.getInstance();
		departTimeCal2.set(2016, 1, 1, 15, 30, 0);
		transportOfferingDatOutbound1.setDepartureTime(departTimeCal2.getTime());
		transportOfferingDatOutbound1.setNumber("EZY0805");
		final TravelSectorData sectorOutbound2 = new TravelSectorData();
		sectorOutbound2.setOrigin(createTransportFacility("LGW"));
		sectorOutbound2.setDestination(createTransportFacility("CDG"));
		transportOfferingDatOutbound1.setSector(sectorOutbound2);
		return transportOfferingDatOutbound1;
	}

	private List<TransportOfferingData> createMultiSectorInboundTransportOfferingDataList()
	{
		final TransportOfferingData transportOfferingDataInbound1 = createInbound1TransportOfferingData();

		final TransportOfferingData transportOfferingDataInbound2 = createInbound2TransportOfferingData();

		final List<TransportOfferingData> transportOfferings = new ArrayList<>();
		transportOfferings.add(transportOfferingDataInbound1);
		transportOfferings.add(transportOfferingDataInbound2);
		return transportOfferings;
	}

	private TransportOfferingData createInbound1TransportOfferingData()
	{
		final TransportOfferingData transportOfferingDataInbound1 = new TransportOfferingData();
		transportOfferingDataInbound1.setCode("EZY8322010120160830");
		final Calendar departTimeCal3 = Calendar.getInstance();
		departTimeCal3.set(2016, 1, 6, 7, 30, 0);
		transportOfferingDataInbound1.setDepartureTime(departTimeCal3.getTime());
		transportOfferingDataInbound1.setNumber("EZY9322");
		final TravelSectorData sectorInbound1 = new TravelSectorData();
		sectorInbound1.setOrigin(createTransportFacility("CDG"));
		sectorInbound1.setDestination(createTransportFacility("LGW"));
		transportOfferingDataInbound1.setSector(sectorInbound1);
		return transportOfferingDataInbound1;
	}

	private TransportOfferingData createInbound2TransportOfferingData()
	{
		final TransportOfferingData transportOfferingDataInbound2 = new TransportOfferingData();
		transportOfferingDataInbound2.setCode("EZY0805010120161930");
		final Calendar departTimeCal4 = Calendar.getInstance();
		departTimeCal4.set(2016, 1, 6, 15, 30, 0);
		transportOfferingDataInbound2.setDepartureTime(departTimeCal4.getTime());
		transportOfferingDataInbound2.setNumber("EZY0905");
		final TravelSectorData sectorInbound2 = new TravelSectorData();
		sectorInbound2.setOrigin(createTransportFacility("LGW"));
		sectorInbound2.setDestination(createTransportFacility("EDI"));
		transportOfferingDataInbound2.setSector(sectorInbound2);
		return transportOfferingDataInbound2;
	}

	private TransportFacilityData createTransportFacility(final String code)
	{
		final TransportFacilityData transportFacility = new TransportFacilityData();
		transportFacility.setCode(code);
		return transportFacility;
	}

	private PassengerTypeData createChildrenPassengerType()
	{
		final PassengerTypeData childrenPassengerType = new PassengerTypeData();
		childrenPassengerType.setCode("child");
		childrenPassengerType.setName("Child");
		return childrenPassengerType;
	}

	private PassengerTypeData createAdultPassengerType()
	{
		final PassengerTypeData adultPassengerType = new PassengerTypeData();
		adultPassengerType.setCode("adult");
		adultPassengerType.setName("Adult");
		return adultPassengerType;
	}

	private TravellerData createPassenger(final PassengerTypeData passengerType, final String travellerCode,
			final String firstName, final String surName)
	{
		final TravellerData firstAdultPassengerData = new TravellerData();
		final PassengerInformationData firstAdultPassenger = new PassengerInformationData();
		firstAdultPassengerData.setTravellerInfo(firstAdultPassenger);
		firstAdultPassengerData.setTravellerType("Passenger");
		firstAdultPassengerData.setLabel(travellerCode);
		firstAdultPassenger.setFirstName(firstName);
		firstAdultPassenger.setSurname(surName);
		firstAdultPassenger.setPassengerType(passengerType);
		return firstAdultPassengerData;
	}

	private ItineraryData createOutboundIteneraryWithRoute()
	{
		final ItineraryData outboundItenerary = new ItineraryData();
		final TravelRouteData travelRoute = new TravelRouteData();
		travelRoute.setCode("EDI_LGW_CDG");
		final TransportFacilityData origin = createTransportFacility("EDI");
		final TransportFacilityData destination = createTransportFacility("CDG");
		travelRoute.setDestination(destination);
		travelRoute.setOrigin(origin);
		outboundItenerary.setRoute(travelRoute);
		return outboundItenerary;
	}

	private ItineraryData createInboundIteneraryWithRoute()
	{
		final ItineraryData inboundItinerary = new ItineraryData();
		final TravelRouteData travelRoute = new TravelRouteData();
		travelRoute.setCode("CDG_LGW_EDI");
		final TransportFacilityData inboundDestination = createTransportFacility("EDI");
		final TransportFacilityData inboundOrigin = createTransportFacility("CDG");
		travelRoute.setDestination(inboundDestination);
		travelRoute.setOrigin(inboundOrigin);
		inboundItinerary.setRoute(travelRoute);
		return inboundItinerary;
	}
}
