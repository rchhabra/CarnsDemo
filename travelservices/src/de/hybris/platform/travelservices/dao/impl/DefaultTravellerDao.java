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

package de.hybris.platform.travelservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.TravellerDao;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Class is responsible for providing concrete implementation of the TravellerDao interface. The class uses the to query
 * the database and return TravellerModel type.
 */
public class DefaultTravellerDao extends DefaultGenericDao<TravellerModel> implements TravellerDao
{
	private static final Logger LOG = Logger.getLogger(DefaultTravellerDao.class);

	private static final String FIND_BY_UID_AND_VERSION_ID =
			"SELECT {t." + TravellerModel.PK + "} FROM {" + TravellerModel._TYPECODE + " AS t} WHERE {t:" + TravellerModel.VERSIONID
					+ "} IS NULL AND {t:" + TravellerModel.UID + "}=?uid";

	private static String FIND_SAVED_TRAVELLERS = "select {tr." + TravellerModel.PK + "} from {" + TravellerModel._TYPECODE
			+ " as tr join "
			+ PassengerInformationModel._TYPECODE + " as pi on {tr." + TravellerModel.INFO + "}={pi." + PassengerInformationModel.PK
			+ "} join " + PassengerTypeModel._TYPECODE + " as pt on {pi."
			+ PassengerInformationModel.PASSENGERTYPE + "}={pt."
			+ PassengerTypeModel.PK + "}} where {tr:" + TravellerModel.CUSTOMER + "} = ?" + TravellerModel.CUSTOMER + " and {pt:"
			+ PassengerTypeModel.CODE + "} = ?" + PassengerTypeModel.CODE + " and {pi:" + PassengerInformationModel.FIRSTNAME
			+ "} like ?" + PassengerInformationModel.FIRSTNAME + "";

	private static String FIND_SAVED_TRAVELLERS_USING_SURNAME = "select {tr." + TravellerModel.PK + "} from {"
			+ TravellerModel._TYPECODE + " as tr join " + PassengerInformationModel._TYPECODE + " as pi on {tr."
			+ TravellerModel.INFO + "}={pi." + PassengerInformationModel.PK + "} join " + PassengerTypeModel._TYPECODE
			+ " as pt on {pi." + PassengerInformationModel.PASSENGERTYPE + "}={pt." + PassengerTypeModel.PK + "}} where {tr:"
			+ TravellerModel.CUSTOMER + "} = ?" + TravellerModel.CUSTOMER + " and {pt:" + PassengerTypeModel.CODE + "} = ?"
			+ PassengerTypeModel.CODE + " and {pi:" + PassengerInformationModel.SURNAME + "} like ?"
			+ PassengerInformationModel.SURNAME + "";

	/**
	 * Instantiates a new Default traveller dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultTravellerDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public TravellerModel findTraveller(final String uid) throws ModelNotFoundException
	{
		validateParameterNotNull(uid, "Traveller unique Id must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TravellerModel.UID, uid);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_BY_UID_AND_VERSION_ID, params);

		final SearchResult<TravellerModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
		if (Objects.isNull(searchResult))
		{
			throw new ModelNotFoundException("No traveller found for the given uid");
		}
		final List<TravellerModel> travellers = searchResult.getResult();
		if (CollectionUtils.isEmpty(travellers))
		{
			throw new ModelNotFoundException("No traveller found for the given uid");
		}
		final Optional<TravellerModel> travellerModel = travellers.stream().findFirst();
		return travellerModel.orElse(null);
	}

	@Override
	public TravellerModel findTravellerByUIDAndVersionID(final String uid, final String versionID) throws ModelNotFoundException
	{
		validateParameterNotNull(uid, "Traveller unique Id must not be null!");
		validateParameterNotNull(versionID, "Traveller unique versionID must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TravellerModel.UID, uid);
		params.put(TravellerModel.VERSIONID, versionID);

		final List<TravellerModel> travellers = find(params);
		if (CollectionUtils.isEmpty(travellers))
		{
			throw new ModelNotFoundException("No traveller found for the given uid and versionID");
		}
		return travellers.stream().findFirst().get();
	}

	@Override
	public List<TravellerModel> findSavedTravellersUsingFirstNameText(final String firstNameText, final String passengerType,
			final CustomerModel customer)
	{
		validateParameterNotNull(firstNameText, "nameText must not be null!");
		validateParameterNotNull(passengerType, "passengerType must not be null!");
		validateParameterNotNull(customer, "customer must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TravellerModel.CUSTOMER, customer);
		params.put(PassengerTypeModel.CODE, passengerType);
		params.put(PassengerInformationModel.FIRSTNAME, firstNameText + "%");

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_SAVED_TRAVELLERS, params);

		final SearchResult<TravellerModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	@Override
	public List<TravellerModel> findSavedTravellersUsingSurnameText(final String surnameText, final String passengerType,
			final CustomerModel customer)
	{
		validateParameterNotNull(surnameText, "surnameText must not be null!");
		validateParameterNotNull(passengerType, "passengerType must not be null!");
		validateParameterNotNull(customer, "customer must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(TravellerModel.CUSTOMER, customer);
		params.put(PassengerTypeModel.CODE, passengerType);
		params.put(PassengerInformationModel.SURNAME, surnameText + "%");

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_SAVED_TRAVELLERS_USING_SURNAME, params);

		final SearchResult<TravellerModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
		return searchResult.getResult();
	}

}
