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

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.dao.TravellerDao;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.exceptions.TravelKeyGeneratorException;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.service.keygenerator.TravelKeyGeneratorService;
import de.hybris.platform.travelservices.services.PassengerTypeService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.services.accommodationmap.impl.DefaultAccommodationMapService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Class is responsible for providing concrete implementation of the TravellerService interface. The class uses the
 * travellerDao class to query the database and return TravellerModel type.
 */
public class DefaultTravellerService implements TravellerService
{
	private static final Logger LOG = Logger.getLogger(DefaultAccommodationMapService.class);

	private ModelService modelService;
	private EnumerationService enumerationService;
	private PassengerTypeService passengerTypeService;
	private TravellerDao travellerDao;
	private TravelKeyGeneratorService travelKeyGeneratorService;
	private CartService cartService;
	private ConfigurationService configurationService;

	@Override
	public TravellerModel createTraveller(final String travellerType, final String passengerType, final String travellerCode,
			final int passengerNumber, final String travellerUidPrefix)
	{
		return createTraveller(travellerType, passengerType, travellerCode, passengerNumber, travellerUidPrefix, null);
	}

	@Override
	public TravellerModel createTraveller(final String travellerType, final String passengerType, final String travellerCode,
			final int passengerNumber, final String travellerUidPrefix, final String orderOrCartCode)
	{
		final TravellerModel traveller = getModelService().create(TravellerModel._TYPECODE);
		traveller.setLabel(travellerCode);
		traveller.setType(getEnumerationService().getEnumerationValue(TravellerType._TYPECODE, travellerType));
		traveller.setVersionID(orderOrCartCode);

		if (TravelservicesConstants.TRAVELLER_TYPE_PASSENGER.equalsIgnoreCase(travellerType))
		{
			final PassengerInformationModel passengerInfo = getModelService().create(PassengerInformationModel._TYPECODE);
			passengerInfo.setPassengerType(getPassengerTypeService().getPassengerType(passengerType));
			traveller.setInfo(passengerInfo);
		}

		saveTraveller(traveller, passengerNumber, travellerUidPrefix, 0);
		return traveller;
	}

	protected void saveTraveller(final TravellerModel traveller, final int passengerNumber, final String travellerUidPrefix,
			final int attemptNo)
	{
		int attempt = attemptNo;
		try
		{
			traveller
					.setUid(getTravelKeyGeneratorService().generateTravellerUid(travellerUidPrefix, String.valueOf(passengerNumber)));
			getModelService().save(traveller);
		}
		catch (final ModelSavingException mse)
		{
			attempt++;
			final int attemptLimit = configurationService.getConfiguration()
					.getInt(TravelservicesConstants.TRAVEL_KEY_GENERATOR_ATTEMPT_LIMIT);

			if (attemptLimit != 0 && attempt == attemptLimit)
			{
				throw new TravelKeyGeneratorException(
						"Max number of attempts " + attemptLimit + " reached. Unable to generate any more UID's.");
			}

			LOG.error("Attempt: " + attempt + " - Error while saving Traveller. UID " + traveller.getUid()
					+ " already exists. Attempting to generating a new UID.", mse);
			saveTraveller(traveller, passengerNumber, travellerUidPrefix, attempt);
		}
	}

	@Override
	public TravellerModel getExistingTraveller(final String uid) throws ModelNotFoundException
	{
		return getTravellerDao().findTraveller(uid);
	}

	@Override
	public TravellerModel getExistingTraveller(final String uid, final String versionID) throws ModelNotFoundException
	{
		if(StringUtils.isEmpty(versionID))
		{
			return getTravellerDao().findTraveller(uid);
		}
		else
		{
			return getTravellerDao().findTravellerByUIDAndVersionID(uid, versionID);
		}
	}

	@Override
	public Map<Integer, List<TravellerModel>> getTravellersPerLeg(final AbstractOrderModel abstractOrderModel)
	{
		final Map<Integer, List<TravellerModel>> travellersMap = new HashMap<>();
		final List<AbstractOrderEntryModel> activeEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()) && entry.getActive())
				.filter(entry -> !(Objects.equals(ProductType.FEE, entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FeeProductModel))
				.collect(Collectors.toList());
		activeEntries.forEach(entry ->
		{
			if (MapUtils.isEmpty(travellersMap) || !travellersMap
					.containsKey(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
			{
				final List<TravellerModel> travellers = new ArrayList<>();
				travellers.addAll(entry.getTravelOrderEntryInfo().getTravellers());
				travellersMap.put(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber(), travellers);
			}
			else
			{
				if (!travellersMap.get(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber())
						.containsAll(entry.getTravelOrderEntryInfo().getTravellers()))
				{
					final List<TravellerModel> travellers = getTravellerListForEntry(travellersMap, entry);
					travellersMap.put(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber(), travellers);
				}
			}
		});
		return travellersMap;
	}

	protected List<TravellerModel> getTravellerListForEntry(final Map<Integer, List<TravellerModel>> travellersMap,
			final AbstractOrderEntryModel entry)
	{
		final List<TravellerModel> travellers = new ArrayList<>();
		travellers.addAll(travellersMap.get(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()));
		entry.getTravelOrderEntryInfo().getTravellers().forEach(traveller ->
		{
			if (!travellers.contains(traveller))
			{
				travellers.add(traveller);
			}
		});
		return travellers;
	}

	@Override
	public TravellerModel getTravellerFromCurrentCart(final String travellerCode)
	{
		final CartModel sessionCart = getCartService().getSessionCart();

		final Optional<TravellerModel> optionalTraveller = sessionCart.getEntries().stream()
				.filter(aoe -> aoe.getTravelOrderEntryInfo() != null)
				.flatMap(aoe -> aoe.getTravelOrderEntryInfo().getTravellers().stream()).distinct()
				.filter(t -> StringUtils.equalsIgnoreCase(t.getLabel(), travellerCode)).findFirst();

		if (!optionalTraveller.isPresent())
		{
			return null;
		}

		return optionalTraveller.get();
	}

	@Override
	public TravellerModel getTravellerFromCurrentCartByUID(final String travellerUID,
			final CartModel sessionCart)
	{
		final Optional<TravellerModel> optionalTraveller = sessionCart.getEntries().stream()
				.filter(aoe -> aoe.getTravelOrderEntryInfo() != null)
				.flatMap(aoe -> aoe.getTravelOrderEntryInfo().getTravellers().stream()).distinct()
				.filter(t -> StringUtils.equalsIgnoreCase(t.getUid(), travellerUID)).findFirst();

		return optionalTraveller.orElse(null);
	}

	@Override
	public List<TravellerModel> getTravellers(final List<AbstractOrderEntryModel> abstractOrderEntryModels)
	{
		final List<TravellerModel> travellers = new ArrayList<>();

		abstractOrderEntryModels.stream().filter(aoem -> aoem.getTravelOrderEntryInfo() != null).forEach(aoem ->
		{
			if (travellers.isEmpty())
			{
				travellers.addAll(aoem.getTravelOrderEntryInfo().getTravellers());
			}
			else if (!travellers.containsAll(aoem.getTravelOrderEntryInfo().getTravellers()))
			{
				aoem.getTravelOrderEntryInfo().getTravellers().forEach(traveller ->
				{
					if (!travellers.contains(traveller))
					{
						travellers.add(traveller);
					}
				});
			}
		});
		return travellers;
	}

	@Override
	public List<TravellerModel> findSavedTravellersUsingFirstNameText(final String nameText,
			final String passengerType, final CustomerModel customer)
	{
		return getTravellerDao().findSavedTravellersUsingFirstNameText(nameText, passengerType, customer);
	}

	@Override
	public List<TravellerModel> findSavedTravellersUsingLastNameText(final String nameText,
			final String passengerType, final CustomerModel customer)
	{
		return getTravellerDao().findSavedTravellersUsingSurnameText(nameText, passengerType, customer);
	}



	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService the enumerationService to set
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * @return the passengerTypeService
	 */
	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * @param passengerTypeService the passengerTypeService to set
	 */
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

	/**
	 * @return the travellerDao
	 */
	protected TravellerDao getTravellerDao()
	{
		return travellerDao;
	}

	/**
	 * @param travellerDao the travellerDao to set
	 */
	public void setTravellerDao(final TravellerDao travellerDao)
	{
		this.travellerDao = travellerDao;
	}

	/**
	 * @return the travelKeyGeneratorService
	 */
	protected TravelKeyGeneratorService getTravelKeyGeneratorService()
	{
		return travelKeyGeneratorService;
	}

	/**
	 * @param travelKeyGeneratorService the travelKeyGeneratorService to set
	 */
	public void setTravelKeyGeneratorService(final TravelKeyGeneratorService travelKeyGeneratorService)
	{
		this.travelKeyGeneratorService = travelKeyGeneratorService;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
