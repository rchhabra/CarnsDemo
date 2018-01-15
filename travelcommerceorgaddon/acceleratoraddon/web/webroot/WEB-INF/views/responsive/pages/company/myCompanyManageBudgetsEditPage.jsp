<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-budgets/view" var="cancelUrl" htmlEscape="false">
	<spring:param name="budgetCode" value="${b2BBudgetForm.originalCode}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/update" var="saveUrl" htmlEscape="false">
	<spring:param name="budgetCode" value="${b2BBudgetForm.originalCode}" />
</spring:url>
<template:page pageTitle="${pageTitle}">
	<div class="my-company-section">
		<div class="container">
			<div class="col-xs-12 col-sm-3 side-to-top">
				<aside id="sidebar">
					<div class="promotions hidden-xs">
						<cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
			<div class="col-xs-12 col-sm-9">
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.budget.viewPage.back.budgetDetails" labelKey="text.company.budget.edit.title.label" />
				<company:b2bBudgetForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}" b2BBudgetForm="${b2BBudgetForm}" addEditLabel="text.company.budget.edit.title.label" />
			</div>
		</div>
	</div>
</template:page>
