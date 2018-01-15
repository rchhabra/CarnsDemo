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

package de.hybris.platform.travelfacades.facades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Implementation class for TravelCustomerFacade interface.
 */

public class DefaultTravelCustomerFacade extends DefaultCustomerFacade implements TravelCustomerFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelCustomerFacade.class);

	private TimeService timeService;

	private ConfigurationService configurationService;

	private Converter<SavedSearchModel, SavedSearchData> savedSearchConverter;

	private Converter<SavedSearchData, SavedSearchModel> savedSearchReverseConverter;

	private TravelCustomerAccountService customerAccountService;


	@Override
	public String createGuestCustomer(final String email, final String lastName) throws DuplicateUidException
	{

		validateParameterNotNullStandardMessage("email", email);
		final CustomerModel guestCustomer = getModelService().create(CustomerModel.class);
		final String guid = generateGUID();

		//takes care of localizing the name based on the site language
		guestCustomer.setUid(guid + "|" + email);
		guestCustomer.setName(lastName);
		guestCustomer.setType(CustomerType.valueOf(CustomerType.GUEST.getCode()));
		guestCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		guestCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());

		getCustomerAccountService().registerGuestForAnonymousCheckout(guestCustomer, guid);

		return guid + "|" + email;

	}

	@Override
	public boolean saveCustomerSearch(final SavedSearchData savedSearchData)
	{
		final CustomerModel customerModel = (CustomerModel) getUserService().getCurrentUser();
		final List<SavedSearchModel> saveSearches = new ArrayList<>();
		final Collection<SavedSearchModel> customerSavedSearches = customerModel.getSavedSearch();
		final SavedSearchModel currentSearch = getSavedSearchReverseConverter().convert(savedSearchData);

		if (customerSavedSearches != null)
		{
			final int maxSavedSearches = configurationService.getConfiguration()
					.getInt(TravelfacadesConstants.CUSTOMER_SAVED_SEARCH_MAX_NUMBER);

			for (final SavedSearchModel savedSearch : customerSavedSearches)
			{
				if (savedSearch.getEncodedSearch().equalsIgnoreCase(currentSearch.getEncodedSearch()))
				{
					return false;
				}
			}

			if (maxSavedSearches <= customerSavedSearches.size())
			{

				removeOldSavedSearches(customerSavedSearches, customerModel, (customerSavedSearches.size() - maxSavedSearches) + 1);
			}
			saveSearches.addAll(customerModel.getSavedSearch());
		}

		saveSearches.add(currentSearch);
		customerModel.setSavedSearch(saveSearches);
		getModelService().save(customerModel);

		return true;
	}

	@Override
	public List<SavedSearchData> getCustomerSearches()
	{
		final int maxSavedSearches = configurationService.getConfiguration()
				.getInt(TravelfacadesConstants.CUSTOMER_SAVED_SEARCH_MAX_NUMBER);
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final Collection<SavedSearchModel> persistentSavedSearches = removeElapsedSearches(currentUser.getSavedSearch(),
				currentUser);
		final AtomicInteger index = new AtomicInteger(0);
		return Converters.convertAll(
				persistentSavedSearches.stream().sorted((o1, o2) -> o2.getCreationtime().compareTo(o1.getCreationtime()))
						.filter(savedsearch -> index.getAndIncrement() < maxSavedSearches).collect(Collectors.toList()),
				getSavedSearchConverter());
	}

	@Override
	public boolean removeSavedSearch(final String savedSearchID)
	{
		try
		{
			final SavedSearchModel savedSearchModel = getCustomerAccountService().findSavedSearch(savedSearchID);
			getModelService().remove(savedSearchModel);
		}
		catch (final ModelNotFoundException mnfEx)
		{
			LOG.error("Unable to find Search with uid " + savedSearchID, mnfEx);
			return false;
		}
		return true;
	}

	@Override
	public void removeOldSavedSearches(final Collection<SavedSearchModel> savedSearches, final CustomerModel customerModel,
			final int number)
	{
		final AtomicInteger index = new AtomicInteger(0);
		final List<SavedSearchModel> savedSearchModel = savedSearches.stream()
				.sorted((o1, o2) -> o1.getCreationtime().compareTo(o2.getCreationtime()))
				.filter(savedsearch -> index.getAndIncrement() < number).collect(Collectors.toList());

		getModelService().removeAll(savedSearchModel);
		getModelService().refresh(customerModel);
	}

	/*
	 * Removes the saved searches whose departure time has elapsed compared to current Date.
	 *
	 * @param savedSearches list of saved searches by customer.
	 *
	 * @param customerModel
	 *
	 * @return savedSearches list of saved searches without elapsed departure Date.
	 */
	protected Collection<SavedSearchModel> removeElapsedSearches(final Collection<SavedSearchModel> savedSearches,
			final CustomerModel customerModel)
	{
		if (CollectionUtils.isEmpty(savedSearches))
		{
			return savedSearches;
		}

		final Date currentDate = getTimeService().getCurrentTime();
		final List<SavedSearchModel> elapsedSavedSearches = savedSearches.stream()
				.filter(savedSearch -> TravelDateUtils.getDaysBetweenDates(TravelDateUtils
						.convertStringDateToDate(getSavedSearchConverter().convert(savedSearch).getDepartingDateTime(),
								TravelservicesConstants.DATE_PATTERN)
						, currentDate) > 0)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(elapsedSavedSearches))
		{
			return savedSearches;
		}

		getModelService().removeAll(elapsedSavedSearches);
		getModelService().refresh(customerModel);

		return customerModel.getSavedSearch();
	}

	@Override
	public boolean isCurrentUserB2bCustomer()
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		if (currentUser instanceof B2BCustomerModel)
		{
			return true;
		}

		return false;
	}

	@Override
	protected TravelCustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	@Required
	public void setCustomerAccountService(final TravelCustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Converter<SavedSearchModel, SavedSearchData> getSavedSearchConverter()
	{
		return savedSearchConverter;
	}

	/**
	 * @param savedSearchConverter
	 *           the savedSearchConverter to set
	 */
	@Required
	public void setSavedSearchConverter(final Converter<SavedSearchModel, SavedSearchData> savedSearchConverter)
	{
		this.savedSearchConverter = savedSearchConverter;
	}

	protected Converter<SavedSearchData, SavedSearchModel> getSavedSearchReverseConverter()
	{
		return savedSearchReverseConverter;
	}

	/**
	 * @param savedSearchReverseConverter
	 *           the savedSearchReverseConverter to set
	 */
	@Required
	public void setSavedSearchReverseConverter(final Converter<SavedSearchData, SavedSearchModel> savedSearchReverseConverter)
	{
		this.savedSearchReverseConverter = savedSearchReverseConverter;
	}
}
