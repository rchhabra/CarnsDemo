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
package de.hybris.platform.ndcwebservices.validators.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem.ALaCarteSelection;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem.ServiceSelection;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The concrete class to validates the Offer for {@link OrderCreateRQ}
 */
public class NDCOrderCreateOfferValidator extends NDCAbstractOriginDestinationValidator<OrderCreateRQ>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderCreateOfferValidator.class);
	private static final String SPACE = " ";

	private ProductService productService;
	private Map<String, String> offerGroupToOriginDestinationMapping;

	@Override
	public void validate(final OrderCreateRQ orderCreateRQ, final ErrorsType errorsType)
	{
		final List<Offer> offers = orderCreateRQ.getQuery().getOrder().getOffer();

		if (CollectionUtils.isEmpty(offers))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_ORDER_OFFERS));
			return;
		}

		if (!validateOfferNumber(offers, errorsType))
		{
			return;
		}
		try
		{
			if (!validateSameFlight(offers, errorsType))
			{
				return;
			}

			if (!validateOrderCreateSameAirports(offers, errorsType))
			{
				return;
			}

			if (!validateOfferIdPerPTC(offers, errorsType))
			{
				return;
			}

			if (!validateOfferItems(offers, errorsType))
			{
				return;
			}
		}
		catch (final NDCOrderException e)
		{
			LOG.debug(e);
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
		}
	}

	/**
	 * Check if the number of the offer is 1 or 2.
	 *
	 * @param offers
	 *           the offers
	 * @param errorsType
	 *           the errors type
	 * @return boolean
	 */
	protected boolean validateOfferNumber(final List<Offer> offers, final ErrorsType errorsType)
	{
		if (CollectionUtils.size(offers) > NdcservicesConstants.RETURN_FLIGHT)
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MAX_OFFERS_EXCEEDED));
			return false;
		}

		for (final Offer offer : offers)
		{
			final String offerID = offer.getOfferID();
			final boolean offerIdOfferItemIdSame = offer.getOfferItem().stream()
					.anyMatch(offerItem -> StringUtils.equals(offerID, offerItem.getOfferItemID()));
			if (offerIdOfferItemIdSame)
			{
				addError(errorsType,
						getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFER_ITEM_ID));
				return false;
			}
		}

		return true;
	}

	/**
	 * Check if all the offers contained in an offer belong to the same flight.
	 *
	 * @param offers
	 *           the offers
	 * @param errorsType
	 *           the errors type
	 * @return boolean
	 * @throws NDCOrderException
	 *            the ndc order exception
	 */
	protected boolean validateSameFlight(final List<Offer> offers, final ErrorsType errorsType) throws NDCOrderException
	{
		for (final Offer offer : offers)
		{
			final String offerID = offer.getOfferID();
			for (final OfferItem offerItem : offer.getOfferItem())
			{
				if (!getNdcOfferItemIdResolver().isSameOffer(offerID, offerItem.getOfferItemID()))
				{
					addError(errorsType,
							getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_OFFERS_COMBINATIONS));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check if the offer items selected belongs to the same offer.
	 *
	 * @param offers
	 *           the offers
	 * @param errorsType
	 *           the errors type
	 * @return boolean
	 * @throws NDCOrderException
	 *            the ndc order exception
	 */
	protected boolean validateOrderCreateSameAirports(final List<Offer> offers, final ErrorsType errorsType)
			throws NDCOrderException
	{
		if (CollectionUtils.size(offers) == NdcservicesConstants.ONE_WAY_FLIGHT)
		{
			return true;
		}

		final List<String> offerIds = offers.stream().map(Offer::getOfferID).collect(Collectors.toList());
		return validateSameAirports(offerIds, errorsType);
	}

	/**
	 * Check if the selected offerItem can be applied to the referenced passenger.
	 *
	 * @param offers
	 *           the offers
	 * @param errorsType
	 *           the errors type
	 * @return boolean
	 * @throws NDCOrderException
	 *            the ndc order exception
	 */
	protected boolean validateOfferIdPerPTC(final List<Offer> offers, final ErrorsType errorsType) throws NDCOrderException
	{
		for (final Offer offer : offers)
		{
			for (final OfferItem offerItem : offer.getOfferItem())
			{
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(offerItem.getOfferItemID());

				final List<String> passengerTypeCodes = offerItem.getPassengerRefs().stream().filter(PassengerType.class::isInstance)
						.map(passengerRef -> ((PassengerType) passengerRef).getPTC()).collect(Collectors.toList());

				if (!validatePTCWithNDCOfferItemId(ndcOfferItemId, passengerTypeCodes, errorsType))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Validate PTC with NDC offer item id.
	 *
	 * @param ndcOfferItemId
	 *           the ndc offer item id
	 * @param passengerTypeCodes
	 *           the passenger type codes
	 * @return true, if successful
	 */
	protected boolean validatePTCWithNDCOfferItemId(final NDCOfferItemId ndcOfferItemId, final List<String> passengerTypeCodes,
			final ErrorsType errorsType)
	{
		if (CollectionUtils.isEmpty(passengerTypeCodes))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.MISSING_PASSENGER_REFERENCE));
			return false;
		}

		final boolean ptcMatched = passengerTypeCodes.stream().allMatch(ptc -> StringUtils.equals(ndcOfferItemId.getPtc(), ptc));
		if (ptcMatched)
		{
			return ptcMatched;
		}
		addError(errorsType,
				getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_PTC_OFFER_COMBINATION));
		return false;
	}

	/**
	 * Validate offer items.
	 *
	 * @param offers
	 *           the offers
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 * @throws NDCOrderException
	 */
	protected boolean validateOfferItems(final List<Offer> offers, final ErrorsType errorsType) throws NDCOrderException
	{
		for (final Offer offer : offers)
		{
			final Map<String, String> seatsSelectedPerPaxPerSegmentMap = new HashMap<>();
			for (final OfferItem offerItem : offer.getOfferItem())
			{
				if (!validateALaCarteSelection(offerItem, errorsType))
				{
					return false;
				}

				final ALaCarteSelection aLaCarteSelection = offerItem.getALaCarteSelection();
				final String segmentID = Objects.nonNull(aLaCarteSelection) ? aLaCarteSelection.getSegmentID() : StringUtils.EMPTY;

				final List<ServiceSelection> serviceSelections = offerItem.getServiceSelection();
				if (!validateServiceSelections(serviceSelections, segmentID, errorsType))
				{
					return false;
				}

				if (StringUtils.isNotEmpty(segmentID) && !validateSegmentID(offerItem.getOfferItemID(), segmentID, errorsType))
				{
					return false;
				}

				final String seatSelection = offerItem.getSeatSelection();
				if (!validateSeatSelection(seatSelection, segmentID, offerItem.getPassengerRefs(), seatsSelectedPerPaxPerSegmentMap,
						errorsType))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Validate A la carte selection.
	 *
	 * @param aLaCarteSelection
	 *           the a la carte selection
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateALaCarteSelection(final OfferItem offerItem, final ErrorsType errorsType)
	{
		final ALaCarteSelection aLaCarteSelection = offerItem.getALaCarteSelection();
		final List<ServiceSelection> serviceSelections = offerItem.getServiceSelection();
		final String seatSelection = offerItem.getSeatSelection();

		if (Objects.isNull(aLaCarteSelection)
				&& (CollectionUtils.isNotEmpty(serviceSelections) || StringUtils.isNotEmpty(seatSelection)))
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MISSING_OFFERITEM_ALACARTESELECTION));
			return false;
		}

		if (Objects.nonNull(aLaCarteSelection) && aLaCarteSelection.getQuantity() <= 0)
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.OFFERITEM_ALACARTESELECTION_QUANTITY));
			return false;
		}
		return true;
	}

	/**
	 * Validate service selections.
	 *
	 * @param serviceSelections
	 *           the service selections
	 * @param segmentID
	 *           the segment ID
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateServiceSelections(final List<ServiceSelection> serviceSelections, final String segmentID,
			final ErrorsType errorsType)
	{
		for (final ServiceSelection serviceSelection : serviceSelections)
		{
			ProductModel product;
			try
			{
				product = getProductService().getProductForCode(serviceSelection.getServiceDefinitionID());
			}
			catch (final UnknownIdentifierException e)
			{
				addError(errorsType, getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_SERVICE_ID));
				return false;
			}

			String categoryCode = StringUtils.EMPTY;
			if (CollectionUtils.isNotEmpty(product.getSupercategories()))
			{
				final Optional<CategoryModel> category = product.getSupercategories().stream().findFirst();
				categoryCode = category.isPresent() ? category.get().getCode() : StringUtils.EMPTY;
			}

			final String mapping = getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode,
					getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
							TravelservicesConstants.TRAVEL_ROUTE));

			if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRANSPORT_OFFERING) && StringUtils.isEmpty(segmentID))
			{
				final String errorMessage = new StringBuilder(getConfigurationService().getConfiguration()
						.getString(NdcwebservicesConstants.MISSING_OFFERITEM_ALACARTE_SEGMENTID)).append(SPACE)
								.append(serviceSelection.getServiceDefinitionID()).toString();
				addError(errorsType, errorMessage);
				return false;
			}
		}
		return true;
	}

	/**
	 * Validate segment ID.
	 *
	 * @param offerItemID
	 *           the offer item ID
	 * @param segmentID
	 *           the segment ID
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 * @throws NDCOrderException
	 *            the NDC order exception
	 */
	protected boolean validateSegmentID(final String offerItemID, final String segmentID, final ErrorsType errorsType)
			throws NDCOrderException
	{
		final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(offerItemID);
		final Set<String> transportOfferingCodes = ndcOfferItemId.getBundleList().stream()
				.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toSet());
		if (!transportOfferingCodes.contains(segmentID))
		{
			final String errorMessage = new StringBuilder(segmentID).append(SPACE)
					.append(getConfigurationService().getConfiguration()
							.getString(NdcwebservicesConstants.INVALID_OFFERITEM_ALACARTESELECTION_SEGMENTID))
					.append(SPACE).append(offerItemID).toString();
			addError(errorsType, errorMessage);
			return false;
		}
		return true;
	}

	/**
	 * Validate seat selection.
	 *
	 * @param seatSelection
	 *           the seat selection
	 * @param segmentID
	 *           the segment ID
	 * @param passengerRefs
	 *           the passenger refs
	 * @param seatsSelectedPerPaxPerSegmentMap
	 *           the seats selected per pax per segment
	 * @param errorsType
	 *           the errors type
	 * @return true, if successful
	 */
	protected boolean validateSeatSelection(final String seatSelection, final String segmentID, final List<Object> passengerRefs,
			final Map<String, String> seatsSelectedPerPaxPerSegmentMap, final ErrorsType errorsType)
	{
		if (StringUtils.isEmpty(seatSelection))
		{
			return true;
		}

		if (StringUtils.isEmpty(segmentID))
		{
			final String errorMessage = new StringBuilder(getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MISSING_OFFERITEM_ALACARTE_SEGMENTID)).append(SPACE).append(seatSelection)
							.toString();
			addError(errorsType, errorMessage);
			return false;
		}

		if (CollectionUtils.size(passengerRefs) > NdcwebservicesConstants.MAX_PASSENGER_REF_PER_SEAT)
		{
			addError(errorsType, getConfigurationService().getConfiguration()
					.getString(NdcwebservicesConstants.MAX_PASSENGER_REF_PER_SEAT_EXCEEDED));
			return false;
		}

		final String passengerId = passengerRefs.stream().filter(PassengerType.class::isInstance)
				.map(passengerRef -> ((PassengerType) passengerRef).getPassengerID()).findFirst().get();

		final String seatSelectedPerSegmentPerPaxKey = segmentID + passengerId;
		final String seatSelectedPerSegmentPerPaxValue = segmentID + seatSelection;
		if (seatsSelectedPerPaxPerSegmentMap.containsKey(seatSelectedPerSegmentPerPaxKey)
				|| seatsSelectedPerPaxPerSegmentMap.containsValue(seatSelectedPerSegmentPerPaxValue))
		{
			addError(errorsType,
					getConfigurationService().getConfiguration().getString(NdcwebservicesConstants.INVALID_SEAT_ASSOCIATION));
			return false;
		}
		else
		{
			seatsSelectedPerPaxPerSegmentMap.put(seatSelectedPerSegmentPerPaxKey, seatSelectedPerSegmentPerPaxValue);
		}
		return true;
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
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the offerGroupToOriginDestinationMapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * @param offerGroupToOriginDestinationMapping
	 *           the offerGroupToOriginDestinationMapping to set
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}
}
