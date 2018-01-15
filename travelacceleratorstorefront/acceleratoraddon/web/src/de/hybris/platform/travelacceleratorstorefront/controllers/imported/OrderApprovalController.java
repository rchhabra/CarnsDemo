/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */

package de.hybris.platform.travelacceleratorstorefront.controllers.imported;


/**
 * Controller for order approval dashboard.
 */

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.forms.OrderApprovalDecisionForm;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.workflow.enums.WorkflowActionType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@Scope("tenant")
@RequestMapping("/my-account")
public class OrderApprovalController extends AbstractSearchPageController
{
	private static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";
	private static final String ORDER_APPROVAL_DASHBOARD_CMS_PAGE = "order-approval-dashboard";
	private static final String WORKFLOW_ACTION_CODE_PATH_VARIABLE_PATTERN = "{workflowActionCode:.*}";
	private static final String ORDER_APPROVAL_DETAIL_CMS_PAGE = "order-approval-details";
	private static final String TRAVEL_RESERVATION_DATA = "travelReservationData";
	private static final String BOOKING_TOTAL_FORMATTED_VALUE = "bookingTotalFormattedValue";

	private static final Logger LOG = Logger.getLogger(OrderApprovalController.class);

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "b2bOrderFacade")
	private B2BOrderFacade orderFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@RequestMapping(value = "/order-approval-dashboard", method = RequestMethod.GET)
	@RequireHardLogIn
	public String orderApprovalDashboard(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
                                         @RequestParam(value = "sort", required = false) final String sortCode, final Model model)
            throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
        final SearchPageData<? extends B2BOrderApprovalData> searchPageData = orderFacade.getPagedOrdersForApproval(
                new WorkflowActionType[]
		{ WorkflowActionType.START }, pageableData);
		populateModel(model, searchPageData, showMode);

		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderApprovalDashboard"));
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_APPROVAL_DASHBOARD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_APPROVAL_DASHBOARD_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}


	@RequestMapping(value = "/orderApprovalDetails/" + WORKFLOW_ACTION_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String orderApprovalDetails(@PathVariable("workflowActionCode") final String workflowActionCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			final B2BOrderApprovalData orderApprovalDetails = orderFacade.getOrderApprovalDetailsForCode(workflowActionCode);
			final OrderData b2bOrderData = orderApprovalDetails.getB2bOrderData();
			model.addAttribute("orderApprovalData", orderApprovalDetails);
			model.addAttribute("orderData", b2bOrderData);

			if (!model.containsAttribute("orderApprovalDecisionForm"))
			{
				model.addAttribute("orderApprovalDecisionForm", new OrderApprovalDecisionForm());
			}

			if (b2bOrderData != null)
			{
				final String bookingReference = b2bOrderData.getCode();
				final GlobalTravelReservationData travelReservationData = bookingFacade
						.getGlobalTravelReservationData(bookingReference);

				model.addAttribute(TRAVEL_RESERVATION_DATA, travelReservationData);
				if (travelReservationData.getAccommodationReservationData() != null)
				{
					model.addAttribute(TravelacceleratorstorefrontWebConstants.MY_ACCOUNT_BOOKING_ACCOMMODATION_ROOM_MAPPING,
							getAccommodationRoomNameMapping(
						Collections.singletonList(travelReservationData.getAccommodationReservationData())));
				}
				model.addAttribute(TravelacceleratorstorefrontWebConstants.DATE_PATTERN, TravelservicesConstants.DATE_PATTERN);

				final PriceData totalPrice = bookingFacade.getBookingTotal(bookingReference);
				model.addAttribute(BOOKING_TOTAL_FORMATTED_VALUE, totalPrice.getFormattedValue());
			}

			final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
			breadcrumbs.add(new Breadcrumb("/my-account/order-approval-dashboard",
					getMessageSource().getMessage(
                    "text.account.orderApprovalDashboard", null, getI18nService().getCurrentLocale()), null));
			breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[]
			{ b2bOrderData.getCode() }, "Order {0}", getI18nService().getCurrentLocale()), null));

			model.addAttribute("breadcrumbs", breadcrumbs);

		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a order that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_APPROVAL_DETAIL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_APPROVAL_DETAIL_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	protected Map<String, Map<String, Integer>> getAccommodationRoomNameMapping(
			final List<AccommodationReservationData> accommodationReservationDatas)
	{
		if (CollectionUtils.isEmpty(accommodationReservationDatas))
		{
			return Collections.emptyMap();
		}
		final Map<String, Map<String, Integer>> accommodationRoomStayMapping = new HashMap<>();
		accommodationReservationDatas.forEach(accommodationReservationData -> {
			final Map<String, Integer> roomStayMapping = new HashMap<>();
			accommodationReservationData.getRoomStays().forEach(roomStay -> {
				final String roomTypeName = roomStay.getRoomTypes().get(0).getName();
				roomStayMapping.put(roomTypeName,
						roomStayMapping.get(roomTypeName) != null ? roomStayMapping.get(roomTypeName) + 1 : 1);

			});
			accommodationRoomStayMapping.put(accommodationReservationData.getCode(), roomStayMapping);
		});
		return accommodationRoomStayMapping;
	}


	@RequestMapping(value = "/order/approvalDecision", method = RequestMethod.POST)
	@RequireHardLogIn
	public String orderApprovalDecision(
            @ModelAttribute("orderApprovalDecisionForm") final OrderApprovalDecisionForm orderApprovalDecisionForm, final Model model)
            throws CMSItemNotFoundException
	{
		try
		{
			if ("REJECT".contains(orderApprovalDecisionForm.getApproverSelectedDecision())
					&& StringUtils.isEmpty(orderApprovalDecisionForm.getComments()))
			{
				GlobalMessages.addErrorMessage(model, "text.account.orderApproval.addApproverComments");
				model.addAttribute("orderApprovalDecisionForm", orderApprovalDecisionForm);
				return orderApprovalDetails(orderApprovalDecisionForm.getWorkFlowActionCode(), model);
			}

			final B2BOrderApprovalData b2bOrderApprovalData = new B2BOrderApprovalData();
			b2bOrderApprovalData.setSelectedDecision(orderApprovalDecisionForm.getApproverSelectedDecision());
			b2bOrderApprovalData.setApprovalComments(orderApprovalDecisionForm.getComments());
			b2bOrderApprovalData.setWorkflowActionModelCode(orderApprovalDecisionForm.getWorkFlowActionCode());

			orderFacade.setOrderApprovalDecision(b2bOrderApprovalData);

		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a order that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}

		return REDIRECT_MY_ACCOUNT + "/orderApprovalDetails/" + orderApprovalDecisionForm.getWorkFlowActionCode();
	}

}
