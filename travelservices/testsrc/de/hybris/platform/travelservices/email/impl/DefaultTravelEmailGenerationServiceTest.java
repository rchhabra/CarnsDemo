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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.process.email.context.EmailContextFactory;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelEmailGenerationServiceTest
{
	@InjectMocks
	private DefaultTravelEmailGenerationService travelEmailGenerationService;

	@Mock
	private EmailContextFactory<BusinessProcessModel> emailContextFactory;

	@Mock
	private RendererService rendererService;

	@Mock
	private EmailService emailService;

	@Test
	public void testGenerateEmails()
	{
		final OrderProcessModel businessProcessModel = new OrderProcessModel();
		final EmailPageModel emailPageModel = new EmailPageModel();
		final EmailPageTemplateModel emailPageTemplateModel = new EmailPageTemplateModel();
		emailPageModel.setMasterTemplate(emailPageTemplateModel);
		final RendererTemplateModel bodyRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setHtmlTemplate(bodyRenderTemplate);
		final RendererTemplateModel subjectRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setSubject(subjectRenderTemplate);

		final AbstractEmailContext<BusinessProcessModel> emailContext=Mockito.mock(AbstractEmailContext.class);
		BDDMockito.given(emailContextFactory.create(businessProcessModel,
				emailPageModel, bodyRenderTemplate)).willReturn(emailContext);
		BDDMockito.given(emailContext.getToEmail()).willReturn("testTo@test.com");
		BDDMockito.given(emailContext.getFromEmail()).willReturn("testFrom@test.com");
		BDDMockito.given(emailContext.getFromDisplayName()).willReturn("displayName");
		BDDMockito.willDoNothing().given(rendererService).render(Matchers.any(RendererTemplateModel.class),
				Matchers.any(AbstractEmailContext.class), Matchers.any(StringWriter.class));

		final EmailAddressModel fromAddress = new EmailAddressModel();
		final EmailAddressModel toAddress = new EmailAddressModel();
		final EmailMessageModel emailMessageModel = new EmailMessageModel();
		BDDMockito.given(emailService.getOrCreateEmailAddressForEmail("testFrom@test.com", "displayName")).willReturn(fromAddress);
		BDDMockito.given(emailService.getOrCreateEmailAddressForEmail("testTo@test.com", "displayName")).willReturn(toAddress);
		BDDMockito.given(
				emailService.createEmailMessage(Matchers.anyListOf(EmailAddressModel.class), Matchers.anyList(), Matchers.anyList(),
						Matchers.any(EmailAddressModel.class), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
						Matchers.any()))
				.willReturn(emailMessageModel);
		final OrderModel orderModel = new OrderModel();
		orderModel.setAdditionalSecurity(false);
		businessProcessModel.setOrder(orderModel);
		final List<EmailMessageModel> generatedEmails = travelEmailGenerationService.generateEmails(businessProcessModel,
				emailPageModel);
		Assert.assertTrue(generatedEmails.contains(emailMessageModel));
	}

	@Test
	public void testGenerateEmailsWithAdditionalNotification()
	{
		final OrderProcessModel businessProcessModel = new OrderProcessModel();
		final EmailPageModel emailPageModel = new EmailPageModel();
		final EmailPageTemplateModel emailPageTemplateModel = new EmailPageTemplateModel();
		emailPageModel.setMasterTemplate(emailPageTemplateModel);
		final RendererTemplateModel bodyRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setHtmlTemplate(bodyRenderTemplate);
		final RendererTemplateModel subjectRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setSubject(subjectRenderTemplate);

		final AbstractEmailContext<BusinessProcessModel> emailContext = Mockito.mock(AbstractEmailContext.class);
		BDDMockito.given(emailContextFactory.create(businessProcessModel, emailPageModel, bodyRenderTemplate))
				.willReturn(emailContext);
		BDDMockito.given(emailContext.getToEmail()).willReturn("testTo@test.com");
		BDDMockito.given(emailContext.getFromEmail()).willReturn("testFrom@test.com");
		BDDMockito.given(emailContext.getFromDisplayName()).willReturn("displayName");
		BDDMockito.given(emailContext.get("additionalNotificationEmails"))
				.willReturn(new HashSet<String>(Arrays.asList("additional@test.com")));
		BDDMockito.willDoNothing().given(rendererService).render(Matchers.any(RendererTemplateModel.class),
				Matchers.any(AbstractEmailContext.class), Matchers.any(StringWriter.class));

		final EmailAddressModel fromAddress = new EmailAddressModel();
		final EmailAddressModel toAddress = new EmailAddressModel();
		final EmailAddressModel additionalAddress = new EmailAddressModel();
		final EmailMessageModel emailMessageModel = new EmailMessageModel();
		BDDMockito.given(emailService.getOrCreateEmailAddressForEmail("testFrom@test.com", "displayName")).willReturn(fromAddress);
		BDDMockito.given(emailService.getOrCreateEmailAddressForEmail("testTo@test.com", "displayName")).willReturn(toAddress);
		BDDMockito.given(emailService.getOrCreateEmailAddressForEmail("additional@test.com",StringUtils.EMPTY)).willReturn(additionalAddress);
		BDDMockito.given(emailService.createEmailMessage(Matchers.anyListOf(EmailAddressModel.class), Matchers.anyList(),
				Matchers.anyList(), Matchers.any(EmailAddressModel.class), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.any())).willReturn(emailMessageModel);
		final OrderModel orderModel = new OrderModel();
		orderModel.setAdditionalSecurity(false);
		businessProcessModel.setOrder(orderModel);

		final List<EmailMessageModel> generatedEmails = travelEmailGenerationService.generateEmails(businessProcessModel,
				emailPageModel);
		Assert.assertTrue(generatedEmails.contains(emailMessageModel));
	}

	@Test(expected = IllegalStateException.class)
	public void testGenerateEmailWithException()
	{
		final BusinessProcessModel businessProcessModel = new BusinessProcessModel();
		final EmailPageModel emailPageModel = new EmailPageModel();
		final EmailPageTemplateModel emailPageTemplateModel = new EmailPageTemplateModel();
		emailPageModel.setMasterTemplate(emailPageTemplateModel);
		final RendererTemplateModel bodyRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setHtmlTemplate(bodyRenderTemplate);
		final RendererTemplateModel subjectRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setSubject(subjectRenderTemplate);
		BDDMockito.given(emailContextFactory.create(businessProcessModel, emailPageModel, bodyRenderTemplate)).willReturn(null);

		travelEmailGenerationService.generateEmails(businessProcessModel, emailPageModel);
	}

	@Test(expected = IllegalStateException.class)
	public void testGenerateEmailNotValid()
	{
		final BusinessProcessModel businessProcessModel = new BusinessProcessModel();
		final EmailPageModel emailPageModel = new EmailPageModel();
		final EmailPageTemplateModel emailPageTemplateModel = new EmailPageTemplateModel();
		emailPageModel.setMasterTemplate(emailPageTemplateModel);
		final RendererTemplateModel bodyRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setHtmlTemplate(bodyRenderTemplate);
		final RendererTemplateModel subjectRenderTemplate = new RendererTemplateModel();
		emailPageTemplateModel.setSubject(subjectRenderTemplate);

		final AbstractEmailContext<BusinessProcessModel> emailContext = Mockito.mock(AbstractEmailContext.class);
		BDDMockito.given(emailContextFactory.create(businessProcessModel, emailPageModel, bodyRenderTemplate))
				.willReturn(emailContext);

		travelEmailGenerationService.generateEmails(businessProcessModel, emailPageModel);
	}

}
