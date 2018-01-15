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

package de.hybris.platform.travelservices.email.actions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelservices.email.TravelEmailGenerationService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link GenerateEmailAction}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GenerateEmailActionTest
{
	@InjectMocks
	GenerateEmailAction generateEmailAction;

	@Mock
	private CMSEmailPageService cmsEmailPageService;

	@Mock
	private ProcessContextResolutionStrategy contextResolutionStrategy;

	@Mock
	private TravelEmailGenerationService emailGenerationService;

	@Mock
	ModelService modelService;

	private final String frontendTemplateName = "TEST_FRONT_END_TEMPLATE";

	private final BusinessProcessModel businessProcessModel = new BusinessProcessModel();

	@Test
	public void testExecuteActionForNullCatalogVersion() throws RetryLaterException, Exception
	{
		Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(null);
		Assert.assertEquals(Transition.NOK.toString(), generateEmailAction.execute(businessProcessModel));
	}

	@Test
	public void testExecuteActionForNullEmailPageModel() throws RetryLaterException, Exception
	{
		final CatalogVersionModel contentCatalogVersion = new CatalogVersionModel();
		contentCatalogVersion.setVersion("TEST_VERSION");
		final CatalogModel catalog = new CatalogModel()
		{
			@Override
			public String getName()
			{
				return "TEST_CATALOG";
			}
		};
		contentCatalogVersion.setCatalog(catalog);
		generateEmailAction.setFrontendTemplateName(frontendTemplateName);
		Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);
		Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(frontendTemplateName, contentCatalogVersion))
				.thenReturn(null);
		Assert.assertEquals(Transition.NOK.toString(), generateEmailAction.execute(businessProcessModel));
	}

	@Test
	public void testExecuteActionForNullEmailMessageModel() throws RetryLaterException, Exception
	{
		final CatalogVersionModel contentCatalogVersion = new CatalogVersionModel();
		generateEmailAction.setFrontendTemplateName(frontendTemplateName);
		Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);
		Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(frontendTemplateName, contentCatalogVersion))
				.thenReturn(new EmailPageModel());
		Assert.assertEquals(Transition.NOK.toString(), generateEmailAction.execute(businessProcessModel));
	}

	@Test
	public void testExecuteAction() throws RetryLaterException, Exception
	{
		 final List<EmailMessageModel> emailMessageModels=new ArrayList<>();
		 emailMessageModels.add(new EmailMessageModel());
		businessProcessModel.setEmails(emailMessageModels);
		final CatalogVersionModel contentCatalogVersion = new CatalogVersionModel();
		final EmailPageModel emailPageModel = new EmailPageModel();
		generateEmailAction.setFrontendTemplateName(frontendTemplateName);
		Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);
		Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(frontendTemplateName, contentCatalogVersion))
				.thenReturn(emailPageModel);
		Mockito.when(emailGenerationService.generateEmails(businessProcessModel, emailPageModel)).thenReturn(emailMessageModels);
		Assert.assertEquals(Transition.OK.toString(), generateEmailAction.execute(businessProcessModel));
	}

}
