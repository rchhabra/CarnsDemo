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
package de.hybris.platform.traveladdon.controllers.cms;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.DiscountData;
import de.hybris.platform.commercefacades.travel.FeeData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.reservation.data.OfferBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.model.components.TransportReservationComponentModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for Transport Reservation Component
 */
@Controller("TransportReservationComponentController")
@RequestMapping(value = TraveladdonControllerConstants.Actions.Cms.TransportReservationComponent)
public class TransportReservationComponentController extends SubstitutingCMSAddOnComponentController<TransportReservationComponentModel>
{
	private static final Logger LOGGER = Logger.getLogger(TransportReservationComponentController.class);

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final TransportReservationComponentModel component)
	{
		// Model is only filled when the component is called
	}

	/**
	 * This method is responsible for populating itinerary component after see full reservation button is clicked
	 *
	 * @param componentUid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/load", method = RequestMethod.GET)
	protected String getComponent(@RequestParam final String componentUid, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		request.setAttribute(COMPONENT_UID, componentUid);
		try
		{
			final String view = handleGet(request, response, model);
			populateModel(model);
			return view;
		}
		catch (final Exception e)
		{
			LOGGER.error("Exception loading the component", e);
		}
		return StringUtils.EMPTY;
	}

	protected void populateModel(final Model model)
	{
		final ReservationData reservationData = reservationFacade.getCurrentReservationData();

		model.addAttribute(TraveladdonWebConstants.RESERVATION, reservationData);
		model.addAttribute(TraveladdonWebConstants.DATE_FORMAT_LABEL, TraveladdonWebConstants.DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.TIME_FORMAT_LABEL, TraveladdonWebConstants.TIME_FORMAT);
		model.addAttribute(TraveladdonWebConstants.PTC_FARE_BREAKDOWN_SUMMARY, getPTCBreakdownSummary(reservationData));
		model.addAttribute(TraveladdonWebConstants.EXTRAS_SUMMARY, getExtrasSummary(reservationData));
		model.addAttribute(TraveladdonWebConstants.SUMMARY_TAXES_FEES, getTaxesFeesSummary(reservationData));
		model.addAttribute(TraveladdonWebConstants.SUMMARY_DISCOUNTS, getDiscountsSummary(reservationData));
	}

	/**
	 * Sums up values of all discounts to be displayed in itinerary summary section
	 *
	 * @param reservationData
	 *           - the reservation
	 * @return total value of discounts
	 */
	protected PriceData getDiscountsSummary(final ReservationData reservationData)
	{
		if (reservationData == null)
		{
			return new PriceData();
		}
		final TotalFareData totalFare = reservationData.getTotalFare();
		final List<DiscountData> discounts = totalFare.getDiscounts();
		if (CollectionUtils.isEmpty(discounts))
		{
			return null;
		}

		BigDecimal totalDiscounts = BigDecimal.ZERO;

		for (final DiscountData discount : discounts)
		{
			totalDiscounts = totalDiscounts.add(discount.getPrice().getValue());
		}

		return priceDataFactory.create(PriceDataType.BUY, totalDiscounts, commonI18NService.getCurrentCurrency().getIsocode());

	}

	/**
	 * Sums up values of all taxes and fees to be displayed in itinerary summary section
	 *
	 * @param reservationData
	 *           - the reservation
	 * @return total value of taxes and fees
	 */
	protected PriceData getTaxesFeesSummary(final ReservationData reservationData)
	{
		if (reservationData == null)
		{
			return new PriceData();
		}

		final TotalFareData totalFare = reservationData.getTotalFare();

		final List<TaxData> taxes = totalFare.getTaxes();
		final List<FeeData> fees = totalFare.getFees();

		if (CollectionUtils.isEmpty(taxes) && CollectionUtils.isEmpty(fees))
		{
			return null;
		}

		final BigDecimal totalTaxes = getTotalTaxes(taxes);
		final BigDecimal totalFees = getTotalFees(fees);

		return priceDataFactory
				.create(PriceDataType.BUY, totalTaxes.add(totalFees), commonI18NService.getCurrentCurrency().getIsocode());
	}

	/**
	 * Sums up all fees from the reservation
	 *
	 * @param fees - fees attached to reservation object
	 * @return total value of fees
	 */
	protected BigDecimal getTotalFees(final List<FeeData> fees)
	{
		if (CollectionUtils.isEmpty(fees))
		{
			return BigDecimal.ZERO;
		}

		BigDecimal totalFees = BigDecimal.ZERO;
		for (final FeeData fee : fees)
		{
			totalFees = totalFees.add(fee.getPrice().getValue());
		}
		return totalFees;
	}

	/**
	 * Sums up all taxes from the reservation
	 *
	 * @param taxes - fees attached to reservation object
	 * @return
	 */
	protected BigDecimal getTotalTaxes(final List<TaxData> taxes)
	{
		if (CollectionUtils.isEmpty(taxes))
		{
			return BigDecimal.ZERO;
		}

		BigDecimal totalTaxes = BigDecimal.ZERO;
		for (final TaxData tax : taxes)
		{
			totalTaxes = totalTaxes.add(tax.getPrice().getValue());
		}
		return totalTaxes;
	}

	/**
	 * Sums up prices of all extras selected by user which will be displayed in itinerary summary section.
	 *
	 * @param reservationData - current reservation object
	 * @return total price of all extras
	 */
	protected PriceData getExtrasSummary(final ReservationData reservationData)
	{
		if (reservationData == null)
		{
			return new PriceData();
		}
		BigDecimal totalExtras = BigDecimal.ZERO;
		final List<OfferBreakdownData> offerBreakdowns = reservationData.getReservationItems().stream()
				.flatMap(item -> item.getReservationPricingInfo().getOfferBreakdowns().stream()).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(reservationData.getOfferBreakdowns()))
		{
			offerBreakdowns.addAll(reservationData.getOfferBreakdowns());
		}

		if (CollectionUtils.isNotEmpty(offerBreakdowns))
		{
			final List<OfferBreakdownData> extras = offerBreakdowns.stream().filter(
					offer -> !offer.getIncluded() || (offer.getIncluded() && !offer.getTotalFare().getTotalPrice().getValue()
							.equals(BigDecimal.ZERO))).collect(Collectors.toList());

			for (final OfferBreakdownData offer : extras)
			{
				totalExtras = totalExtras.add(offer.getTotalFare().getTotalPrice().getValue());
			}
		}

		return priceDataFactory.create(PriceDataType.BUY, totalExtras, commonI18NService.getCurrentCurrency().getIsocode());
	}

	/**
	 * Sums up PTC breakdowns from all legs which will be displayed in itinerary summary section
	 *
	 * @param reservationData - current reservation object
	 * @return summary of PTC breakdowns for all legs
	 */
	protected List<PTCFareBreakdownData> getPTCBreakdownSummary(final ReservationData reservationData)
	{
		if (reservationData == null)
		{
			return Collections.emptyList();
		}

		final List<PTCFareBreakdownData> ptcBreakdowns = new ArrayList<PTCFareBreakdownData>();
		reservationData.getReservationItems().forEach(item ->
		{
			final List<PTCFareBreakdownData> ptcFareBreakdownDatas = item.getReservationPricingInfo().getItineraryPricingInfo()
					.getPtcFareBreakdownDatas();
			if (CollectionUtils.isEmpty(ptcBreakdowns))
			{
				createPTCBreakdownSummary(ptcBreakdowns, ptcFareBreakdownDatas);
			}
			else
			{
				updatePTCBreakdowns(ptcBreakdowns, ptcFareBreakdownDatas);
			}
		});

		return ptcBreakdowns;
	}

	/**
	 * Creates a new instances of PTC breakdowns which will store the summary of fare breakdowns from all legs
	 *
	 * @param ptcBreakdownsSummary  - list of PTC breakdowns which will be displayed in itinerary summary
	 * @param ptcFareBreakdownDatas - list of PTC breakdowns from current reservation item
	 */
	protected void createPTCBreakdownSummary(final List<PTCFareBreakdownData> ptcBreakdownsSummary,
			final List<PTCFareBreakdownData> ptcFareBreakdownDatas)
	{
		ptcFareBreakdownDatas.forEach(ptc ->
		{
			final PTCFareBreakdownData breakdown = new PTCFareBreakdownData();
			breakdown.setPassengerTypeQuantity(ptc.getPassengerTypeQuantity());
			final PassengerFareData fare = new PassengerFareData();
			fare.setBaseFare(priceDataFactory.create(PriceDataType.BUY, ptc.getPassengerFare().getBaseFare().getValue(),
					commonI18NService.getCurrentCurrency().getIsocode()));
			fare.setTotalFare(priceDataFactory.create(PriceDataType.BUY, ptc.getPassengerFare().getTotalFare().getValue(),
					commonI18NService.getCurrentCurrency().getIsocode()));
			breakdown.setPassengerFare(fare);
			ptcBreakdownsSummary.add(breakdown);
		});

	}

	/**
	 * Updates already existing PTC breakdown with values from next reservation item
	 *
	 * @param ptcBreakdowns        - list of PTC breakdowns which will be displayed in itinerary summary
	 * @param newPtcFareBreakdowns - list of PTC breakdowns from next reservation item
	 */
	protected void updatePTCBreakdowns(final List<PTCFareBreakdownData> ptcBreakdowns,
			final List<PTCFareBreakdownData> newPtcFareBreakdowns)
	{
		ptcBreakdowns.forEach(ptcBreakdown ->
		{
			final List<PTCFareBreakdownData> matchingBreakdown = newPtcFareBreakdowns.stream().filter(
					ptc -> ptc.getPassengerTypeQuantity().getPassengerType().getCode()
							.equals(ptcBreakdown.getPassengerTypeQuantity().getPassengerType().getCode())).collect(Collectors.toList());
			final PassengerFareData fare = ptcBreakdown.getPassengerFare();
			final BigDecimal updatedBasePrice = fare.getBaseFare().getValue()
					.add(matchingBreakdown.get(0).getPassengerFare().getBaseFare().getValue());
			final BigDecimal updatedTotalPrice = fare.getTotalFare().getValue()
					.add(matchingBreakdown.get(0).getPassengerFare().getTotalFare().getValue());
			fare.setBaseFare(
					priceDataFactory.create(PriceDataType.BUY, updatedBasePrice, commonI18NService.getCurrentCurrency().getIsocode
							()));
			fare.setTotalFare(priceDataFactory
					.create(PriceDataType.BUY, updatedTotalPrice, commonI18NService.getCurrentCurrency().getIsocode()));
		});
	}

}
