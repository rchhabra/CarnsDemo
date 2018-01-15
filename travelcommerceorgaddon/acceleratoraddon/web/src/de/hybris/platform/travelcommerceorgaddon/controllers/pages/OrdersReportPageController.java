/*
* [y] hybris Platform
*
* Copyright (c) 2000-2015 hybris AG
* All rights reserved.
*
* This software is the confidential and proprietary information of hybris
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with hybris.
*
*/

package de.hybris.platform.travelcommerceorgaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;
import de.hybris.platform.travelb2bfacades.reservation.B2BReservationFacade;
import de.hybris.platform.travelcommerceorgaddon.controllers.TravelcommerceorgaddonControllerConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(OrdersReportPageController.MY_COMPANY_ORGANIZATION_MANAGEMENT_REPORT_ORDERS)
public class OrdersReportPageController extends MyCompanyPageController
{
	static final String MY_COMPANY_ORGANIZATION_MANAGEMENT_REPORT_ORDERS = "/my-company/organization-management/report-orders/";

	@Resource(name = "b2bReservationFacade")
	B2BReservationFacade b2bReservationFacade;

	@Resource(name = "costCenterFacade")
	private B2BCostCenterFacade costCenterFacade;

	@Resource(name = "b2bUnitFacade")
	protected B2BUnitFacade b2bUnitFacade;

	Logger LOG = Logger.getLogger(this.getClass());

	@RequestMapping(value = "", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewOrders() throws CMSItemNotFoundException
	{
		final String defaultUnit = b2bReservationFacade.getDefaultUnit();
		return REDIRECT_PREFIX + MY_COMPANY_ORGANIZATION_MANAGEMENT_REPORT_ORDERS + defaultUnit;
	}

	@RequestMapping(value = "/{unitId:.*}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String viewOrders(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@PathVariable(value = "unitId") final String unitId,
			@RequestParam(value = "sort", defaultValue = OrderModel.CODE) final String sortCode, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException
	{

		if (!b2bReservationFacade.validateUnit(unitId))
		{
			return REDIRECT_PREFIX + "/";
		}
		checkDateParameters(model, request);

		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid(unitId);
		final List<B2BUnitData> childUnits = unitData.getChildren();

		final List<String> unitCodes = new ArrayList<String>();
		unitCodes.add(unitId);
		childUnits.forEach(childUnit -> {
			unitCodes.add(childUnit.getUid());
		});

		final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
		final SearchPageData<B2BReservationData> searchPageData = findReservations(unitCodes, request, pageableData);

		model.addAttribute("totalForQuery", findTotal(unitCodes, request));
		model.addAttribute("unitId", unitId);

		populateModel(model, searchPageData, showMode);

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));

		return TravelcommerceorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyOrdersReportPage;
	}

	private void checkDateParameters(final Model model, final HttpServletRequest request) {
		if(getDateFromParam(request.getParameter("fromDate")) == null ||
				getDateFromParam(request.getParameter("toDate")) == null){
			return;
		}

		if (!b2bReservationFacade.checkDatesInterval(getDateFromParam(request.getParameter("fromDate")),
				getDateFromParam(request.getParameter("toDate"))))
		{
			GlobalMessages.addErrorMessage(model, "orders.report.invalid.date.interval");
		}
	}

	protected PriceData findTotal(final List<String> unitCodes, final HttpServletRequest request)
	{
		final String currency = request.getParameter("currency");
		if (StringUtils.isEmpty(currency))
		{
			LOG.info("Unable to calculate totals for multi currency results, skipping");
			return null;
		}
		return b2bReservationFacade.findTotal(unitCodes, request.getParameter("email"),
				getDateFromParam(request.getParameter("fromDate")), getDateFromParam(request.getParameter("toDate")),
				request.getParameter("costCenter"), currency);
	}

	protected SearchPageData<B2BReservationData> findReservations(final List<String> unitCodes, final HttpServletRequest request,
			final PageableData pageableData)
	{
		return b2bReservationFacade.findReservations(null, pageableData, unitCodes, request.getParameter("email"),
				getDateFromParam(request.getParameter("fromDate")), getDateFromParam(request.getParameter("toDate")),
				request.getParameter("costCenter"), request.getParameter("currency"));
	}

	private Date getDateFromParam(final String parameter)
	{
		if (StringUtils.isEmpty(parameter))
		{
			return null;
		}
		try
		{
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return simpleDateFormat.parse(parameter);
		}
		catch (final ParseException pe)
		{
			return null;
		}
	}

	@ModelAttribute("costCenters")
	public List<? extends B2BCostCenterData> getVisibleActiveCostCenters()
	{
		final List<? extends B2BCostCenterData> costCenterData = costCenterFacade.getActiveCostCenters();
		return costCenterData == null ? Collections.<B2BCostCenterData> emptyList() : costCenterData;
	}

	@Override
	protected void populateModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		final int numberPagesShown = 5;//getSiteConfigService().getInt(PAGINATION_NUMBER_OF_RESULTS_COUNT, 5);

		model.addAttribute("numberPagesShown", Integer.valueOf(numberPagesShown));
		model.addAttribute("searchPageData", searchPageData);
		model.addAttribute("isShowAllAllowed", calculateShowAll(searchPageData, showMode));
		model.addAttribute("isShowPageAllowed", calculateShowPaged(searchPageData, showMode));
	}

}
