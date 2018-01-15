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
package de.hybris.platform.travelb2bfacades.reservation.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;
import de.hybris.platform.travelb2bfacades.reservation.B2BReservationFacade;
import de.hybris.platform.travelb2bfacades.reservation.manager.B2BReservationPipelineManager;
import de.hybris.platform.travelb2bservices.order.TravelB2BOrderService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BReservationFacade}
 */
public class DefaultB2BReservationFacade implements B2BReservationFacade
{

	private TravelB2BOrderService travelb2bOrderService;
	private UserService userService;
	private B2BReservationPipelineManager b2bReservationPipelineManager;
	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;


	/**
	 * @deprecated Deprecated since version 3.0.
	 *
	 * @param searchState
	 *           the search state
	 * @param pageableData
	 *           the pageableData
	 * @param unitCode
	 *           the b2b unit code
	 * @param email
	 *           the email
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @param costCenterUid
	 *           the cost center
	 * @param currency
	 *           the currency
	 * @return
	 */
	@Deprecated
	@Override
	public SearchPageData<B2BReservationData> findReservations(final SearchStateData searchState, final PageableData pageableData,
			final String unitCode, final String email, final Date fromDate, final Date toDate, final String costCenterUid,
			final String currency)
	{
		return findReservations(searchState, pageableData, Collections.singletonList(unitCode), email, fromDate, toDate,
				costCenterUid, currency);
	}

	@Override
	public SearchPageData<B2BReservationData> findReservations(final SearchStateData searchState, final PageableData pageableData,
			final List<String> unitCodes, final String email, final Date fromDate, final Date toDate, final String costCenterUid,
			final String currency)
	{
		final SearchPageData<OrderModel> searchPageData = getTravelb2bOrderService().getPagedOrders(pageableData, unitCodes, email,
				fromDate, toDate, costCenterUid, currency);
		setSorts(searchPageData,
				Arrays.asList(OrderModel.CODE, OrderModel.USER, OrderModel.UNIT, OrderModel.CREATIONTIME, OrderModel.TOTALPRICE));
		return convertSearchPageData(searchPageData);
	}

	/**
	 * Check if fromDate is before toDate.
	 *
	 * @param fromDate
	 *           the date to compare
	 * @param toDate
	 *           the date to be compared with
	 * @return true if fromDate is before toDate, false otherwise.
	 */
	@Override
	public boolean checkDatesInterval(final Date fromDate, final Date toDate)
	{
		final ZoneId currentZone = ZoneId.systemDefault();
		if (fromDate == null || toDate == null || TravelDateUtils.isBefore(toDate, currentZone, fromDate, currentZone))
		{
			return false;
		}

		return true;

	}

	/**
	 * Sets the sorting parameters in the searchPageData.
	 *
	 * @param searchPageData
	 * 		the searchPage
	 * @param sortCodes
	 * 		the sort codes
	 */
	protected void setSorts(final SearchPageData<OrderModel> searchPageData, final List<String> sortCodes)
	{
		final List<SortData> searchSorts = searchPageData.getSorts();

		final SortData selectedSort = searchSorts.stream().filter(searchSort -> searchSort.isSelected()).findAny()
				.orElse(new SortData());

		final List<SortData> sorts = new ArrayList<SortData>();
		sortCodes.forEach(sortCode -> {
			final SortData sort = new SortData();

			if (sortCode.equals(selectedSort.getCode()))
			{
				sort.setSelected(true);
			}

			sort.setCode(sortCode);
			sort.setName(sortCode);
			sorts.add(sort);
		});
		searchPageData.setSorts(sorts);
	}

	/**
	 * Convert search page data search page data.
	 *
	 * @param searchPageModelData
	 * 		the search page model
	 *
	 * @return searchPageData This method converts the list of orderModels in B2BReservationData
	 */
	protected SearchPageData<B2BReservationData> convertSearchPageData(final SearchPageData<OrderModel> searchPageModelData)
	{
		final SearchPageData<B2BReservationData> searchPageData = new SearchPageData<B2BReservationData>();
		searchPageData.setPagination(searchPageModelData.getPagination());
		searchPageData.setSorts(searchPageModelData.getSorts());
		final List<B2BReservationData> reservations = new ArrayList<B2BReservationData>();

		searchPageModelData.getResults()
				.forEach(result -> reservations.add(getB2bReservationPipelineManager().executePipeline(result)));

		searchPageData.setResults(reservations);
		return searchPageData;
	}

	@Override
	public String getDefaultUnit()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
			if (b2bCustomer.getDefaultB2BUnit() != null)
			{
				return b2bCustomer.getDefaultB2BUnit().getUid();
			}

			final Set<PrincipalGroupModel> groups = b2bCustomer.getGroups();
			for (final PrincipalGroupModel group : groups)
			{
				if (group instanceof B2BUnitModel)
				{
					final B2BUnitModel b2bUnit = (B2BUnitModel) group;
					return b2bUnit.getUid();
				}
			}
		}
		return null;
	}

	@Override
	public boolean validateUnit(final String unitCode)
	{
		final UserModel user = getUserService().getCurrentUser();
		if (!(user instanceof B2BCustomerModel))
		{
			return false;
		}

		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
		final Set<PrincipalGroupModel> groups = b2bCustomer.getGroups();
		for (final PrincipalGroupModel group : groups)
		{
			if (group instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) group;
				if (b2bUnit.getUid().equals(unitCode))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 *
	 * @param unitCode
	 *           the b2b unit code
	 * @param email
	 *           the email
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @param costCenter
	 * @param currencyIso
	 *           the currency iso code
	 * @return
	 */
	@Deprecated
	@Override
	public PriceData findTotal(final String unitCode, final String email, final Date fromDate, final Date toDate,
			final String costCenter, final String currencyIso)
	{
		return findTotal(Collections.singletonList(unitCode), email, fromDate, toDate, costCenter, currencyIso);
	}

	@Override
	public PriceData findTotal(final List<String> unitCodes, final String email, final Date fromDate, final Date toDate,
			final String costCenter, final String currencyIso)
	{
		final Double totalValue = getTravelb2bOrderService().findTotal(unitCodes, email, fromDate, toDate, costCenter, currencyIso);
		if (totalValue != null)
		{
			return getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.valueOf(totalValue.doubleValue()),
					currencyIso);
		}
		return null;
	}

	/**
	 * Gets price data factory.
	 *
	 * @return price data factory
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @param priceDataFactory
	 * 		the price data factory
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets travelb 2 b order service.
	 *
	 * @return the travelb 2 b order service
	 */
	protected TravelB2BOrderService getTravelb2bOrderService()
	{
		return travelb2bOrderService;
	}

	/**
	 * Sets travelb 2 b order service.
	 *
	 * @param travelb2bOrderService
	 * 		the travelb 2 b order service
	 */
	@Required
	public void setTravelb2bOrderService(final TravelB2BOrderService travelb2bOrderService)
	{
		this.travelb2bOrderService = travelb2bOrderService;
	}

	/**
	 * Gets user service.
	 *
	 * @return the user service
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the user service
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Gets b 2 b reservation pipeline manager.
	 *
	 * @return the b 2 b reservation pipeline manager
	 */
	protected B2BReservationPipelineManager getB2bReservationPipelineManager()
	{
		return b2bReservationPipelineManager;
	}

	/**
	 * Sets b 2 b reservation pipeline manager.
	 *
	 * @param b2bReservationPipelineManager
	 * 		the b 2 b reservation pipeline manager
	 */
	@Required
	public void setB2bReservationPipelineManager(final B2BReservationPipelineManager b2bReservationPipelineManager)
	{
		this.b2bReservationPipelineManager = b2bReservationPipelineManager;
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 * 		the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
