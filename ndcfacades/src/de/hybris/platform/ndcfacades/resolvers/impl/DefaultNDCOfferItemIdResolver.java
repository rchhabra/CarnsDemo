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
package de.hybris.platform.ndcfacades.resolvers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.NDCOfferItemIdBundle;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.model.NDCOfferMappingModel;
import de.hybris.platform.ndcservices.services.NDCOfferMappingService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;
import de.hybris.platform.travelservices.services.TransportOfferingService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCOfferItemIdResolver}
 */
public class DefaultNDCOfferItemIdResolver implements NDCOfferItemIdResolver
{
	private static final Logger LOG = Logger.getLogger(DefaultNDCOfferItemIdResolver.class);

	private static final int PTC = 0;
	private static final int ORIGIN_DESTINATION_REF_NUMBER = 1;
	private static final int ROUTE_CODE = 2;
	private static final int BUNDLES = 3;

	private static final int BUNDLE_ID_OFFSET = 0;
	private static final int FARE_PRODUCT_OFFSET = 1;
	private static final int TRANSPORT_OFFERINGS_OFFSET = 2;
	private static final int MIN_OFFER_ELEMENTS = 4;

	private static final String EMPTY_PTC = "";

	private KeyGenerator keyGenerator;
	private ModelService modelService;
	private ConfigurationService configurationService;
	private PassengerTypeService passengerTypeService;
	private TransportOfferingService transportOfferingService;
	private NDCOfferMappingService ndcOfferMappingService;

	@Override
	public NDCOfferItemId getNDCOfferItemIdFromString(final String offerItemID) throws NDCOrderException
	{
		final String ndcOfferItemIdString = getNDCOfferItemId(offerItemID);

		if (StringUtils.isEmpty(offerItemID))
		{
			throw new NDCOrderException("Invalid offerItemID provided");
		}

		final String[] offerItemIdSplit = ndcOfferItemIdString.split("\\|");

		if (offerItemIdSplit.length < MIN_OFFER_ELEMENTS)
		{
			throw new NDCOrderException("Invalid offerItemID provided");
		}

		final NDCOfferItemId ndcOfferItemId = new NDCOfferItemId();

		ndcOfferItemId.setPtc(offerItemIdSplit[PTC]);
		ndcOfferItemId.setOriginDestinationRefNumber(Integer.parseInt(offerItemIdSplit[ORIGIN_DESTINATION_REF_NUMBER]));
		ndcOfferItemId.setRouteCode(offerItemIdSplit[ROUTE_CODE]);

		final String[] bundleTemplateStrings = Arrays.copyOfRange(offerItemIdSplit, BUNDLES, offerItemIdSplit.length);
		final List<NDCOfferItemIdBundle> ndcOfferItemIdBundleList = new LinkedList<>();

		for (final String bundleTemplateString : bundleTemplateStrings)
		{
			final NDCOfferItemIdBundle ndcOfferItemIdBundle = new NDCOfferItemIdBundle();
			final String[] bundleTemplateInfo = bundleTemplateString.split("#");

			final List<String> transportOfferings = Arrays
					.stream(Arrays.copyOfRange(bundleTemplateInfo, TRANSPORT_OFFERINGS_OFFSET, bundleTemplateInfo.length))
					.collect(Collectors.toList());

			ndcOfferItemIdBundle.setTransportOfferings(transportOfferings);

			ndcOfferItemIdBundle.setBundle(bundleTemplateInfo[BUNDLE_ID_OFFSET]);

			ndcOfferItemIdBundle.setFareProduct(bundleTemplateInfo[FARE_PRODUCT_OFFSET]);

			ndcOfferItemIdBundleList.add(ndcOfferItemIdBundle);
		}
		ndcOfferItemId.setBundleList(ndcOfferItemIdBundleList);

		return ndcOfferItemId;
	}

	@Override
	public String generateAirShoppingNDCOfferItemId(final PTCFareBreakdownData ptcFareBreakdownData,
			final PricedItineraryData pricedItinerary,
			final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final NDCOfferItemId ndcOfferItemId = new NDCOfferItemId();
		final List<NDCOfferItemIdBundle> ndcOfferItemIdBundleList = new LinkedList<>();

		if (Objects.nonNull(ptcFareBreakdownData))
		{
			final String ndcPTCCode = getPassengerTypeService()
					.getPassengerType(ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode()).getNdcCode();
			ndcOfferItemId.setPtc(ndcPTCCode);
		}
		else
		{
			ndcOfferItemId.setPtc(EMPTY_PTC);
		}

		ndcOfferItemId.setOriginDestinationRefNumber(pricedItinerary.getOriginDestinationRefNumber());
		ndcOfferItemId.setRouteCode(pricedItinerary.getItinerary().getRoute().getCode());

		for (final TravelBundleTemplateData bundleTemplates : itineraryPricingInfo.getBundleTemplates())
		{
			final NDCOfferItemIdBundle ndcOfferItemIdBundle = new NDCOfferItemIdBundle();

			ndcOfferItemIdBundle.setFareProduct(bundleTemplates.getFareProducts().get(0).getCode());
			ndcOfferItemIdBundle.setBundle(itineraryPricingInfo.getBundleTemplates().get(0).getId());
			ndcOfferItemIdBundle.setTransportOfferings(bundleTemplates.getTransportOfferings().stream()
					.map(TransportOfferingData::getCode)
					.collect(Collectors.toList()));
			ndcOfferItemIdBundleList.add(ndcOfferItemIdBundle);
		}

		ndcOfferItemId.setBundleList(ndcOfferItemIdBundleList);
		return ndcOfferItemIdToString(ndcOfferItemId);
	}

	@Override
	public String generateAirShoppingNDCOfferItemId(final PricedItineraryData pricedItinerary,
			final ItineraryPricingInfoData itineraryPricingInfo)
	{
		return generateAirShoppingNDCOfferItemId(null, pricedItinerary, itineraryPricingInfo);
	}

	@Override
	public boolean isSameOffer(final String firstOfferItemId, final String secondOfferItemId) throws NDCOrderException
	{
		final String firstFlightId = getOfferId(firstOfferItemId);
		final String secondFlightId = getOfferId(secondOfferItemId);

		return firstFlightId.compareTo(secondFlightId) == 0;
	}

	@Override
	public String ndcOfferItemIdToString(final NDCOfferItemId ndcOfferItemId)
	{
		final StringBuilder id = new StringBuilder();

		if (Objects.nonNull(ndcOfferItemId.getPtc()))
		{
			id.append(ndcOfferItemId.getPtc());
		}

		id.append("|").append(ndcOfferItemId.getOriginDestinationRefNumber());
		id.append("|").append(ndcOfferItemId.getRouteCode());

		for (final NDCOfferItemIdBundle ndcOfferItemIdBundle : ndcOfferItemId.getBundleList())
		{
			final String transportOfferingCodes = ndcOfferItemIdBundle.getTransportOfferings().stream()
					.collect(Collectors.joining("#"));

			id.append("|").append(ndcOfferItemIdBundle.getBundle()).append("#")
					.append(ndcOfferItemIdBundle.getFareProduct()).append("#").append(transportOfferingCodes);
		}

		if (isMappingEnabled())
		{
			NDCOfferMappingModel ndcOfferMappingModel = getNdcOfferMappingService()
					.getNDCOfferMappingFromOfferItemID(id.toString());

			if(Objects.nonNull(ndcOfferMappingModel))
			{
				return ndcOfferMappingModel.getCode();
			}

			ndcOfferMappingModel = new NDCOfferMappingModel();
			ndcOfferMappingModel.setNDCOfferItemID(id.toString());
			ndcOfferMappingModel.setCode(String.valueOf(getKeyGenerator().generate()));

			try
			{
				getModelService().save(ndcOfferMappingModel);
			}
			catch (final ModelSavingException e)
			{
				LOG.debug(e);
				LOG.info("NDCOfferItemId already mapped to a code, retrieving the code from db.");
				ndcOfferMappingModel = getNdcOfferMappingService().getNDCOfferMappingFromOfferItemID(id.toString());
			}

			return ndcOfferMappingModel.getCode();
		}
		else
		{
			return id.toString();
		}
	}

	@Override
	public String generateOrderNDCOfferItemId(final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		if (!isValidOrderEntry(orderEntries))
		{
			throw new NDCOrderException("Invalid orderEntry provided");
		}

		final NDCOfferItemId ndcOfferItemId = new NDCOfferItemId();
		final List<NDCOfferItemIdBundle> ndcOfferItemIdBundleList = new LinkedList<>();

		ndcOfferItemId.setPtc(getPTCFromOrderEntry(orderEntries.get(0)));
		ndcOfferItemId.setOriginDestinationRefNumber(orderEntries.get(0).getTravelOrderEntryInfo().getOriginDestinationRefNumber());
		ndcOfferItemId.setRouteCode(orderEntries.get(0).getTravelOrderEntryInfo().getTravelRoute().getCode());

		for(final AbstractOrderEntryModel orderEntry : orderEntries)
		{
			final NDCOfferItemIdBundle ndcOfferItemIdBundle = new NDCOfferItemIdBundle();
			ndcOfferItemIdBundle.setFareProduct(orderEntry.getProduct().getCode());
			ndcOfferItemIdBundle.setBundle(orderEntry.getBundleTemplate().getParentTemplate().getId());
			ndcOfferItemIdBundle.setTransportOfferings(
					orderEntry.getTravelOrderEntryInfo().getTransportOfferings().stream().map(WarehouseModel::getCode)
							.collect(Collectors.toList()));
			ndcOfferItemIdBundleList.add(ndcOfferItemIdBundle);
		}

		ndcOfferItemId.setBundleList(ndcOfferItemIdBundleList);

		return ndcOfferItemIdToString(ndcOfferItemId);
	}

	@Override
	public List<TransportOfferingModel> getTransportOfferingFromNDCOfferItemId(final String orderItemId)
			throws NDCOrderException
	{
		final NDCOfferItemId ndcOfferItemId = getNDCOfferItemIdFromString(orderItemId);

		return ndcOfferItemId.getBundleList().stream()
				.flatMap(ndcOfferItemIdBundle -> ndcOfferItemIdBundle.getTransportOfferings().stream()).collect(Collectors.toList())
				.stream().map(transportOfferingCode -> getTransportOfferingService().getTransportOffering(transportOfferingCode))
				.collect(Collectors.toList());
	}

	/**
	 * Return the PTC associated to the traveler in the order entry. Since each fare product order entry contains only one
	 * reference to a traveler the first is used.
	 *
	 * @param orderEntry
	 * 		the order entry
	 *
	 * @return string
	 */
	private String getPTCFromOrderEntry(final AbstractOrderEntryModel orderEntry)
	{
		final Optional<TravellerModel> traveler = orderEntry.getTravelOrderEntryInfo().getTravellers().stream().findFirst();

		if(!traveler.isPresent())
		{
			return EMPTY_PTC;
		}

		final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveler.get().getInfo();
		return passengerInfo.getPassengerType().getNdcCode();
	}

	/**
	 * Checks if the provided orderEntry can be used to generate an NDCOfferItemId
	 *
	 * @param orderEntries
	 * 		the order entries
	 *
	 * @return boolean
	 */
	private boolean isValidOrderEntry(final List<AbstractOrderEntryModel> orderEntries)
	{
		for(final AbstractOrderEntryModel orderEntry : orderEntries)
		{
			if (Objects.isNull(orderEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
			{
				return false;
			}

			if (Objects.isNull(orderEntry.getTravelOrderEntryInfo().getTravelRoute()))
			{
				return false;
			}

			if (!(ProductType.FARE_PRODUCT.equals(orderEntry.getProduct().getProductType())
					|| orderEntry.getProduct() instanceof FareProductModel))
			{
				return false;
			}

			if (Objects.isNull(orderEntry.getBundleTemplate()) || Objects.isNull(orderEntry.getBundleTemplate().getParentTemplate()))
			{
				return false;
			}

			if (CollectionUtils.isEmpty(orderEntry.getTravelOrderEntryInfo().getTransportOfferings()))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two offers removing the ptc and comparing the other part
	 *
	 * @param offerItemID
	 * 		the offer item id
	 *
	 * @return offer id
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected String getOfferId(final String offerItemID) throws NDCOrderException
	{
		final String ndcOfferItemIdString = getNDCOfferItemId(offerItemID);

		if (StringUtils.isEmpty(offerItemID))
		{
			throw new NDCOrderException("Invalid offerItemID provided");
		}

		final String[] offerItemIdSplit = ndcOfferItemIdString.split("\\|");

		if (offerItemIdSplit.length < MIN_OFFER_ELEMENTS)
		{
			throw new NDCOrderException("Invalid offerItemID provided");
		}

		final String[] getFlightIdSplit = Arrays.copyOfRange(offerItemIdSplit, ROUTE_CODE, offerItemIdSplit.length);
		return Arrays.stream(getFlightIdSplit).collect(Collectors.joining(" "));
	}

	/**
	 * Return the String value of the NDCOfferItemId corresponding resolving, if enabled, the mapping
	 *
	 * @param offerItemID
	 * 		the offer item id
	 *
	 * @return ndc offer item id
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected String getNDCOfferItemId(final String offerItemID) throws NDCOrderException
	{
		final String ndcOfferItemIdString;
		if (isMappingEnabled())
		{
			final NDCOfferMappingModel ndcOfferMappingModel = getNdcOfferMappingService()
					.getNDCOfferMappingFromCode(offerItemID);

			if (Objects.isNull(ndcOfferMappingModel))
			{
				throw new NDCOrderException("Invalid offerItemID provided");
			}
			ndcOfferItemIdString = ndcOfferMappingModel.getNDCOfferItemID();
		}
		else
		{
			ndcOfferItemIdString = offerItemID;
		}
		return ndcOfferItemIdString;
	}

	/**
	 * Checks if the mapping between NDCOfferItemId and a code is enabled
	 *
	 * @return boolean
	 */
	protected boolean isMappingEnabled()
	{
		return Boolean
				.valueOf(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NDC_OFFER_ITEM_ID_MAPPING));
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets ndc offer mapping service.
	 *
	 * @return the ndc offer mapping service
	 */
	protected NDCOfferMappingService getNdcOfferMappingService()
	{
		return ndcOfferMappingService;
	}

	/**
	 * Sets ndc offer mapping service.
	 *
	 * @param ndcOfferMappingService
	 * 		the ndc offer mapping service
	 */
	@Required
	public void setNdcOfferMappingService(final NDCOfferMappingService ndcOfferMappingService)
	{
		this.ndcOfferMappingService = ndcOfferMappingService;
	}

	/**
	 * Gets passenger type service.
	 *
	 * @return the passenger type service
	 */
	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * Sets passenger type service.
	 *
	 * @param passengerTypeService
	 * 		the passenger type service
	 */
	@Required
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

	/**
	 * Gets key generator.
	 *
	 * @return the key generator
	 */
	protected KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	/**
	 * Sets key generator.
	 *
	 * @param keyGenerator
	 * 		the key generator
	 */
	@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transport offering service
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService
	 * 		the transport offering service
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}
}
