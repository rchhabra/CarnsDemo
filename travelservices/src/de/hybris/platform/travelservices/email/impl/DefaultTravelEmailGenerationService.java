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
package de.hybris.platform.travelservices.email.impl;


import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.email.TravelEmailGenerationService;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;


/**
 * Service to render travel related email messages
 */
public class DefaultTravelEmailGenerationService extends DefaultEmailGenerationService implements TravelEmailGenerationService
{

	private static final Logger LOG = Logger.getLogger(DefaultTravelEmailGenerationService.class);

	private static final String ADDITIONAL_NOTIFICATION_EMAILS = "additionalNotificationEmails";

	@Override
	public List<EmailMessageModel> generateEmails(final BusinessProcessModel businessProcessModel,
			final EmailPageModel emailPageModel)
	{
		ServicesUtil.validateParameterNotNull(emailPageModel, "EmailPageModel cannot be null");
		Assert.isInstanceOf(EmailPageTemplateModel.class, emailPageModel.getMasterTemplate(),
				"MasterTemplate associated with EmailPageModel should be EmailPageTemplate");

		final EmailPageTemplateModel emailPageTemplateModel = (EmailPageTemplateModel) emailPageModel.getMasterTemplate();
		final RendererTemplateModel bodyRenderTemplate = emailPageTemplateModel.getHtmlTemplate();
		Assert.notNull(bodyRenderTemplate, "HtmlTemplate associated with MasterTemplate of EmailPageModel cannot be null");
		final RendererTemplateModel subjectRenderTemplate = emailPageTemplateModel.getSubject();
		Assert.notNull(subjectRenderTemplate, "Subject associated with MasterTemplate of EmailPageModel cannot be null");

		final List<EmailMessageModel> emailMessageModels = new ArrayList<>();
		//This call creates the context to be used for rendering of subject and body templates.
		final AbstractEmailContext<BusinessProcessModel> emailContext = getEmailContextFactory().create(businessProcessModel,
				emailPageModel, bodyRenderTemplate);

		if (emailContext == null)
		{
			LOG.error("Failed to create email context for businessProcess [" + businessProcessModel + "]");
			throw new IllegalStateException("Failed to create email context for businessProcess [" + businessProcessModel + "]");
		}
		else
		{
			if (!validate(emailContext))
			{
				LOG.error("Email context for businessProcess [" + businessProcessModel + "] is not valid: "
						+ ReflectionToStringBuilder.toString(emailContext));
				throw new IllegalStateException("Email context for businessProcess [" + businessProcessModel + "] is not valid: "
						+ ReflectionToStringBuilder.toString(emailContext));
			}

			final Set<String> additionalNotificationEmails = (Set<String>) emailContext.get(ADDITIONAL_NOTIFICATION_EMAILS);
			if(!(businessProcessModel instanceof OrderProcessModel))
			{
				LOG.error("Failed to retrieve order model for businessProcess [" + businessProcessModel + "]");
				throw new IllegalStateException("Failed to retrieve order model for businessProcess [" + businessProcessModel + "]");
			}
			final OrderModel orderModel = ((OrderProcessModel) businessProcessModel).getOrder();
			if (differentiateEmailForAdditionSecurity(orderModel, additionalNotificationEmails))
			{
				createPersonalizedEmailByRecipient(subjectRenderTemplate, bodyRenderTemplate, emailContext, emailMessageModels,
						additionalNotificationEmails);
			}
			else
			{
				final StringWriter subject = new StringWriter();
				getRendererService().render(subjectRenderTemplate, emailContext, subject);

				final StringWriter body = new StringWriter();
				getRendererService().render(bodyRenderTemplate, emailContext, body);

				emailMessageModels.addAll(createEmailMessages(subject.toString(), body.toString(), emailContext));
			}
		}

		return emailMessageModels;
	}

	/**
	 * Creates personalized email for the traveller that placed the booking and for all the email addresses provided
	 *
	 * @param subjectRenderTemplate
	 * @param bodyRenderTemplate
	 * @param emailContext
	 * @param emailMessageModels
	 * @param additionalNotificationEmails
	 */
	protected void createPersonalizedEmailByRecipient(final RendererTemplateModel subjectRenderTemplate,
			final RendererTemplateModel bodyRenderTemplate, final AbstractEmailContext<BusinessProcessModel> emailContext,
			final List<EmailMessageModel> emailMessageModels, final Set<String> additionalNotificationEmails)
	{
		final StringWriter subject = new StringWriter();
		getRendererService().render(subjectRenderTemplate, emailContext, subject);

		final StringWriter body = new StringWriter();
		getRendererService().render(bodyRenderTemplate, emailContext, body);

		emailMessageModels.add(createEmailMessage(subject.toString(), body.toString(), emailContext));

		emailContext.put(TravelservicesConstants.FILTER_TRAVELLERS_BY_RECIPIENT, Boolean.TRUE);

		for (final String additionalEmail : additionalNotificationEmails)
		{
			emailContext.put(TravelservicesConstants.EMAIL, additionalEmail);

			final StringWriter additionalSubject = new StringWriter();
			getRendererService().render(subjectRenderTemplate, emailContext, additionalSubject);

			final StringWriter additionalBody = new StringWriter();
			getRendererService().render(bodyRenderTemplate, emailContext, additionalBody);

			emailMessageModels.add(createEmailMessage(additionalSubject.toString(), additionalBody.toString(), emailContext));
		}
	}

	/**
	 * Returns true if the emails need to be personalized based on the recipient because the additional security is enabled
	 *
	 * @param orderModel
	 * @param additionalNotificationEmails
	 * @return
	 */
	protected boolean differentiateEmailForAdditionSecurity(final OrderModel orderModel,
			final Set<String> additionalNotificationEmails)
	{
		return orderModel.getAdditionalSecurity() && !isOrderPlacedWithGuestUser(orderModel) &&
				CollectionUtils.isNotEmpty(additionalNotificationEmails);
	}

	/**
	 * Returns true if the provided order (or the one related to {@link AbstractOrderModel} cart) was placed through a
	 * guest checkout. The control on instanceof is done to prevent misbehaviour during the amendment journey. This because, during
	 * the amendment, a new user is created for a non registered traveller that is performing an amendment.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return boolean
	 */
	protected boolean isOrderPlacedWithGuestUser(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.nonNull(abstractOrderModel.getOriginalOrder()))
		{
			return CustomerType.GUEST.equals(((CustomerModel) abstractOrderModel.getOriginalOrder().getUser()).getType());
		}
		else
		{
			return CustomerType.GUEST.equals(((CustomerModel) abstractOrderModel.getUser()).getType());
		}
	}

	/**
	 * Create email messages list.
	 *
	 * @param emailSubject
	 * 		the email subject
	 * @param emailBody
	 * 		the email body
	 * @param emailContext
	 * 		the email context
	 * @return the list
	 */
	protected List<EmailMessageModel> createEmailMessages(final String emailSubject, final String emailBody,
			final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		final List<EmailMessageModel> emailMessageModels = new ArrayList<>();
		final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(),
				emailContext.getFromDisplayName());

		final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
				emailContext.getToDisplayName());
		emailMessageModels.add(getEmailService().createEmailMessage(Collections.singletonList(toAddress), new ArrayList<>(),
				new ArrayList<>(), fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null));

		createEmailMessagesForAdditionalNotifications(emailSubject, emailBody, emailContext, emailMessageModels, fromAddress);

		return emailMessageModels;
	}

	/**
	 * Create email messages for additional notification emails
	 *
	 * @param emailSubject
	 * 		the email subject
	 * @param emailBody
	 * 		the email body
	 * @param emailContext
	 * 		the email context
	 * @param emailMessageModels
	 * 		the email message models
	 * @param fromAddress
	 * 		the from address
	 */
	protected void createEmailMessagesForAdditionalNotifications(final String emailSubject, final String emailBody,
			final AbstractEmailContext<BusinessProcessModel> emailContext, final List<EmailMessageModel> emailMessageModels,
			final EmailAddressModel fromAddress)
	{
		final Set<String> additionalNotificationEmails = (Set<String>) emailContext.get(ADDITIONAL_NOTIFICATION_EMAILS);

		if (CollectionUtils.isEmpty(additionalNotificationEmails))
		{
			return;
		}

		for (final String additionalEmail : additionalNotificationEmails)
		{
			final EmailAddressModel additionalAddress = getEmailService().getOrCreateEmailAddressForEmail(additionalEmail,
					StringUtils.EMPTY);
			emailMessageModels.add(getEmailService().createEmailMessage(Collections.singletonList(additionalAddress),
					new ArrayList<>(), new ArrayList<>(), fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null));
		}
	}
}
