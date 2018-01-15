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
package de.hybris.platform.ndcwebservices.controllers;

import de.hybris.platform.ndcfacades.ndc.ErrorsType;
import de.hybris.platform.ndcfacades.ndc.MsgDocumentType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.ndcfacades.order.NDCOrderFacade;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcwebservices.constants.NdcwebservicesConstants;
import de.hybris.platform.ndcwebservices.validators.OrderChangeRQValidator;
import de.hybris.platform.ndcwebservices.validators.OrderCreateRQValidator;
import de.hybris.platform.ndcwebservices.validators.OrderRetrieveRQValidator;
import de.hybris.platform.ndcwebservices.validators.OrderViewRSValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for OrderController
 */
@Controller
@Scope("request")
@RequestMapping(value = "/v171/order")
public class OrderController extends NDCAbstractController
{
	private static final Logger LOG = Logger.getLogger(OrderController.class);

	@Resource(name = "orderCreateRQValidator")
	private OrderCreateRQValidator orderCreateRQValidator;

	@Resource(name = "orderRetrieveRQValidator")
	private OrderRetrieveRQValidator orderRetrieveRQValidator;

	@Resource(name = "orderChangeRQValidator")
	private OrderChangeRQValidator orderChangeRQValidator;

	@Resource(name = "orderViewRSValidator")
	private OrderViewRSValidator orderViewRSValidator;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "ndcOrderFacade")
	private NDCOrderFacade ndcOrderFacade;

	@RequestMapping(value = "/change", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public OrderViewRS orderChangeRequest(@RequestBody final OrderChangeRQ orderChangeRQ)
	{
		final ErrorsType errorsType = new ErrorsType();
		OrderViewRS orderViewRS = new OrderViewRS();
		setOrderViewRSBaseElement(orderViewRS);

		try
		{
			if (!orderChangeRQValidator.validateOrderChangeRQ(orderChangeRQ, orderViewRS))
			{
				return orderViewRS;
			}

			orderViewRS = ndcOrderFacade.changeOrder(orderChangeRQ);
			orderViewRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!orderViewRSValidator.validateOrderViewRS(orderViewRS))
			{
				return getOrderViewRSWithErrors(orderViewRS);
			}
		}
		catch (final ConversionException | NDCOrderException e)
		{
			LOG.warn(e.getMessage());
			LOG.debug(e);
			addError(errorsType, e.getMessage());
			orderViewRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			orderViewRS.setErrors(errorsType);
		}

		return orderViewRS;
	}

	@RequestMapping(value = "/retrieve", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public OrderViewRS orderRetrieveRequest(@RequestBody final OrderRetrieveRQ orderRetrieveRQ)
	{
		final ErrorsType errorsType = new ErrorsType();
		OrderViewRS orderViewRS = new OrderViewRS();
		setOrderViewRSBaseElement(orderViewRS);

		try
		{
			if (!orderRetrieveRQValidator.validateOrderRetrieveRQ(orderRetrieveRQ, orderViewRS))
			{
				return orderViewRS;
			}

			orderViewRS = ndcOrderFacade.retrieveOrder(orderRetrieveRQ);
			orderViewRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!orderViewRSValidator.validateOrderViewRS(orderViewRS))
			{
				return getOrderViewRSWithErrors(orderViewRS);
			}
		}
		catch (ConversionException | NDCOrderException e)
		{
			LOG.warn(e.getMessage());
			LOG.debug(e);
			addError(errorsType, e.getMessage());
			orderViewRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			orderViewRS.setErrors(errorsType);
		}

		return orderViewRS;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_TRAVELAGENCYGROUP" })
	public OrderViewRS orderCreateRequest(@RequestBody final OrderCreateRQ orderCreateRQ)
	{
		final ErrorsType errorsType = new ErrorsType();
		OrderViewRS orderViewRS = new OrderViewRS();
		setOrderViewRSBaseElement(orderViewRS);

		try
		{
			if (!orderCreateRQValidator.validateOrderCreateRQ(orderCreateRQ, orderViewRS))
			{
				return orderViewRS;
			}

			if (isOrderPayment(orderCreateRQ))
			{
				orderViewRS = ndcOrderFacade.payOrder(orderCreateRQ);
			}
			else
			{
				orderViewRS = ndcOrderFacade.orderCreate(orderCreateRQ);
			}

			orderViewRS.setVersion(NdcwebservicesConstants.NDC_VERSION);

			if (!orderViewRSValidator.validateOrderViewRS(orderViewRS))
			{
				return getOrderViewRSWithErrors(orderViewRS);
			}
		}
		catch (final ConversionException | NDCOrderException e)
		{
			LOG.warn(e.getMessage());
			LOG.debug(e);
			addError(errorsType, e.getMessage());
			orderViewRS.setErrors(errorsType);
		}
		catch (final Exception e)
		{
			LOG.error(configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR), e);
			addError(errorsType, configurationService.getConfiguration().getString(NdcwebservicesConstants.GENERIC_ERROR));
			orderViewRS.setErrors(errorsType);
		}
		return orderViewRS;
	}

	/**
	 * @param orderCreateRQ
	 */
	protected boolean isOrderPayment(final OrderCreateRQ orderCreateRQ)
	{
		return Objects.nonNull(orderCreateRQ.getQuery().getBookingReferences()) && CollectionUtils
				.isNotEmpty(orderCreateRQ.getQuery().getBookingReferences().getBookingReference());
	}

	/**
	 * Gets the order view RS with errors.
	 *
	 * @param orderViewRS
	 *           the order view RS
	 * @return the order view RS with errors
	 */
	protected OrderViewRS getOrderViewRSWithErrors(final OrderViewRS orderViewRS)
	{
		final OrderViewRS orderViewRSErrors = new OrderViewRS();
		orderViewRSErrors.setErrors(orderViewRS.getErrors());
		setOrderViewRSBaseElement(orderViewRSErrors);
		return orderViewRSErrors;
	}

	/**
	 * Sets the order view RS base element.
	 *
	 * @param orderViewRS
	 *           the new order view RS base element
	 */
	protected void setOrderViewRSBaseElement(final OrderViewRS orderViewRS)
	{
		final MsgDocumentType msgDocumentType = new MsgDocumentType();
		orderViewRS.setVersion(NdcwebservicesConstants.NDC_VERSION);
		orderViewRS.setDocument(msgDocumentType);
	}
}
