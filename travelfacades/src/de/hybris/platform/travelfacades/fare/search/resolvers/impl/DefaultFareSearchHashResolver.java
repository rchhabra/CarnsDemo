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
package de.hybris.platform.travelfacades.fare.search.resolvers.impl;

import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link FareSearchHashResolver}
 */
public class DefaultFareSearchHashResolver implements FareSearchHashResolver
{
	protected static final Logger LOG = Logger.getLogger(DefaultFareSearchHashResolver.class);

	private static final Random RANDOM = new SecureRandom();

	private SessionService sessionService;
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	@Override
	public String generateSeed()
	{
		return new BigInteger(130, RANDOM).toString(32);
	}

	@Override
	public String generateIdentifier(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final String searchSeed = getSessionSearchSeed("");
		return generateDigest(itineraryPricingInfo, searchSeed);
	}

	@Override
	public String generateIdentifier(final ItineraryPricingInfoData itineraryPricingInfo, final String masterBundleTemplateId)
	{
		final String searchSeed = getSessionSearchSeed(masterBundleTemplateId);
		return generateDigest(itineraryPricingInfo, searchSeed);
	}

	protected String generateDigest(final ItineraryPricingInfoData itineraryPricingInfo, final String seed)
	{
		final List<String> stringList = new LinkedList<>();

		for (final TravelBundleTemplateData bundleTemplates : itineraryPricingInfo.getBundleTemplates())
		{
			if(CollectionUtils.isEmpty(bundleTemplates.getFareProducts()))
			{
				return "";
			}

			final StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append(getTravelBundleTemplateFacade().getMasterBundleTemplateId(bundleTemplates.getId()));
			stringBuilder.append("#").append(bundleTemplates.getFareProducts().get(0).getCode());

			for (final TransportOfferingData transportOffering : bundleTemplates.getTransportOfferings())
			{
				stringBuilder.append("#").append(transportOffering.getCode());
			}
			stringList.add(stringBuilder.toString());
		}

		final String stringToMarshal = seed + "-" + stringList.stream().collect(Collectors.joining("|"));

		return createDigest(stringToMarshal.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public boolean validItineraryIdentifier(final AddBundleToCartRequestData addBundleToCartRequestData,
			final String itineraryIdentifier, final String bundleType)
	{
		final String searchSeed = getSessionService().getAttribute(TravelservicesConstants.SEARCH_SEED);

		if (Objects.isNull(searchSeed))
		{
			return false;
		}

		final List<String> stringList = new LinkedList<>();

		for (final AddBundleToCartData addBundleToCartData : addBundleToCartRequestData.getAddBundleToCartData())
		{
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder
					.append(getTravelBundleTemplateFacade().getMasterBundleTemplateId(addBundleToCartData.getBundleTemplateId()));

			stringBuilder.append("#").append(addBundleToCartData.getProductCode());

			for (final String transportOfferingCode : addBundleToCartData.getTransportOfferings())
			{
				stringBuilder.append("#").append(transportOfferingCode);
			}
			stringList.add(stringBuilder.toString());
		}

		final String stringToMarshal = searchSeed + "-" + stringList.stream().collect(Collectors.joining("|"));

		return StringUtils.equalsIgnoreCase(createDigest(stringToMarshal.getBytes(StandardCharsets.UTF_8)), itineraryIdentifier);
	}

	@Override
	public boolean validItineraryIdentifier(final List<ItineraryPricingInfoData> itineraryPricingInfos,
			final String masterBundleTemplateId)
	{
		final String searchSeed = getSessionService()
				.getAttribute(TravelservicesConstants.SEARCH_SEED + "-" + masterBundleTemplateId);

		cleanSessionSeeds();

		if (Objects.isNull(searchSeed))
		{
			return false;
		}

		for (final ItineraryPricingInfoData itineraryPricingInfoData : itineraryPricingInfos)
		{
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(searchSeed).append("-");
			stringBuilder.append(masterBundleTemplateId);

			stringBuilder.append("#")
					.append(itineraryPricingInfoData.getBundleTemplates().get(0).getFareProducts().get(0).getCode());

			for (final TransportOfferingData transportOffering : itineraryPricingInfoData.getBundleTemplates().get(0)
					.getTransportOfferings())
			{
				stringBuilder.append("#").append(transportOffering.getCode());
			}

			if (!StringUtils.equalsIgnoreCase(createDigest(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)),
					itineraryPricingInfoData.getItineraryIdentifier()))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validPackageItineraryIdentifier(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final String bundleTemplateId = getTravelBundleTemplateFacade()
				.getMasterBundleTemplateId(itineraryPricingInfo.getBundleTemplates().get(0).getFareProductBundleTemplateId());

		final String searchSeed = getSessionService().getAttribute(TravelservicesConstants.SEARCH_SEED);

		if (Objects.isNull(searchSeed))
		{
			return false;
		}

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(searchSeed).append("-");
		stringBuilder.append(bundleTemplateId);

		stringBuilder.append("#")
				.append(itineraryPricingInfo.getBundleTemplates().get(0).getFareProducts().get(0).getCode());

		for (final TransportOfferingData transportOffering : itineraryPricingInfo.getBundleTemplates().get(0)
				.getTransportOfferings())
		{
			stringBuilder.append("#").append(transportOffering.getCode());
		}

		if (!StringUtils.equalsIgnoreCase(createDigest(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)),
				itineraryPricingInfo.getItineraryIdentifier()))
		{
			return false;
		}

		return true;
	}

	/**
	 * Removes all the session seeds associated to the different
	 */
	protected void cleanSessionSeeds()
	{
		final List<String> seedAttributes = getSessionService().getAllAttributes().entrySet().stream()
				.filter(entry -> StringUtils.contains(entry.getKey(),
						TravelservicesConstants.SEARCH_SEED)).map(Entry::getKey).collect(Collectors.toList());
		for (final String seed : seedAttributes)
		{
			getSessionService().removeAttribute(seed);
		}
	}

	/**
	 * Returns the search seed stored in session. If not set, it generates a new one, sets it in session and returns it.
	 *
	 * @param masterBundleTemplateId
	 * 		the master bundle template id
	 *
	 * @return session search seed
	 */
	protected String getSessionSearchSeed(final String masterBundleTemplateId)
	{
		return StringUtils.isEmpty(masterBundleTemplateId) ?
				getSessionService().getAttribute(TravelservicesConstants.SEARCH_SEED) :
				getSessionService().getAttribute(TravelservicesConstants.SEARCH_SEED + "-" + masterBundleTemplateId);
	}

	/**
	 * Creates the String from the byte message passed as a parameter using an MD5 hash
	 *
	 * @param message
	 * 		the message
	 *
	 * @return string
	 */
	protected String createDigest(final byte[] message)
	{
		final MessageDigest messageDigest;
		final byte[] digest;
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
			digest = messageDigest.digest(message);
		}
		catch (final NoSuchAlgorithmException e)
		{
			LOG.error("Invalid algorithm provided for the digest generation, plain string generated.");
			LOG.debug(e);
			return new BigInteger(1, message).toString(16);
		}

		return new BigInteger(1, digest).toString(16);
	}

	/**
	 * Gets session service.
	 *
	 * @return the session service
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 * 		the session service
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets travel bundle template facade.
	 *
	 * @return the travel bundle template facade
	 */
	protected TravelBundleTemplateFacade getTravelBundleTemplateFacade()
	{
		return travelBundleTemplateFacade;
	}

	/**
	 * Sets travel bundle template facade.
	 *
	 * @param travelBundleTemplateFacade
	 * 		the travel bundle template facade
	 */
	@Required
	public void setTravelBundleTemplateFacade(final TravelBundleTemplateFacade travelBundleTemplateFacade)
	{
		this.travelBundleTemplateFacade = travelBundleTemplateFacade;
	}
}
