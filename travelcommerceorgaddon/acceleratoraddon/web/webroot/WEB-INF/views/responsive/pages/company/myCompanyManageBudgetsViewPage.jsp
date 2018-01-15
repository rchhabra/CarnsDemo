<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-budgets/edit" var="editBudgetDetailsUrl" htmlEscape="false">
	<spring:param name="budgetCode" value="${budget.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/enable" var="enableUrl" htmlEscape="false">
	<spring:param name="budgetCode" value="${budget.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/disable" var="disableUrl" htmlEscape="false">
	<spring:param name="budgetCode" value="${budget.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/unitDetails" var="viewBudgetUnitUrl" htmlEscape="false">
	<spring:param name="budgetCode" value="${budget.code}" />
	<spring:param name="unit" value="${budget.unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets" var="backUrl" htmlEscape="false">
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
				<div class="row heading-with-button">
					<div class="col-xs-7 col-sm-8">
						<h2 class="h2">
							<spring:theme code="text.company.budget.viewPage.label" text="Budget Details" />
						</h2>
					</div>
					<div class="col-xs-5 col-sm-4 button-inline">
						<a href="${backUrl}" class="btn btn-primary btn-block">
							<spring:theme code="text.company.budget.viewPage.back" text="Back to Budgets" />
						</a>
					</div>
				</div>
				<div class="budget-details col-xs-12">
					<div class="row">
						<div class="col-xs-12 well well-lg well-tertiary">
							<div class="col-sm-12 col-no-padding">
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.budgetId" text="Budget ID" />
										</span>
										<span class="item-value"> ${fn:escapeXml(budget.code)} </span>
									</div>
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.name" text="Budget Name" />
										</span>
										<span class="item-value"> ${fn:escapeXml(budget.name)} </span>
									</div>
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.unit" text="Parent business unit" />
										</span>
										<span class="item-value">
											<a href="${viewBudgetUnitUrl}">${fn:escapeXml(budget.unit.name)}</a>
										</span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.startDate" text="Start Date" />
										</span>
										<span class="item-value"> ${fn:escapeXml(budget.startDate)} </span>
									</div>
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.endDate" text="End Date" />
										</span>
										<span class="item-value"> ${fn:escapeXml(budget.endDate)} </span>
									</div>
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.amount" text="Budget Amount" />
										</span>
										<span class="item-value">
											<fmt:formatNumber value="${budget.budget}" maxFractionDigits="2" minFractionDigits="2" />
											(${fn:escapeXml(budget.currency.isocode)})
										</span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.budget.enabledDisabled" text="Status" />
										</span>
										<span class="item-value">
											<c:choose>
												<c:when test="${budget.active}">
													<spring:theme code="text.company.on" text="ON" />
												</c:when>
												<c:otherwise>
													<spring:theme code="text.company.off" text="OFF" />
												</c:otherwise>
											</c:choose>
										</span>
									</div>
								</div>
							</div>
							<div class="col-xs-12 col-sm-5  item-action">
								<a href="${editBudgetDetailsUrl}" id="EditBudget_button" class="button edit btn btn-block btn-primary">
									<spring:theme code="text.company.costCenter.button.displayName" text="Edit" />
								</a>
							</div>
						</div>
					</div>
				</div>
				<div class="row heading-with-button">
					<c:choose>
						<c:when test="${budget.active}">
							<div class="accountActions-link  col-xs-12 col-sm-4 col-sm-offset-8">
								<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-disable">
									<spring:theme code="text.company.budget.disableButton.displayName" text="Disable Budget" />
								</a>
							</div>
						</c:when>
						<c:otherwise>
							<div class="accountActions-link  col-xs-12 col-sm-4 col-sm-offset-8">
								<a href="${enableUrl}" class="btn btn-primary btn-block">
									<spring:theme code="text.company.budget.enableButton.displayName" text="Enable" />
								</a>
							</div>
						</c:otherwise>
					</c:choose>
					<company:actionConfirmationModal id="disable" targetUrl="${disableUrl}" modalTitle="text.company.budget.disable.confirm" messageKey="text.company.budget.disable.confirm.message" />
				</div>
				<div class="panel panel-primary panel-list account-list">
					<div class="panel-heading ">
						<h3 class="title">
							<spring:theme code="text.company.budget.viewPage.costCenters" text="Cost Centers" />
						</h3>
					</div>
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<c:choose>
								<c:when test="${not empty budget.costCenters}">
									<c:forEach items="${budget.costCenters}" var="costCenter">
										<spring:url value="/my-company/organization-management/manage-costcenters/view" var="viewCostCenterUrl" htmlEscape="false">
											<spring:param name="costCenterCode" value="${costCenter.code}" />
										</spring:url>
										<fieldset class="fieldset">
											<div class="row">
												<div class="form-group card col-xs-12 col-sm-6 col-md-4">
													<ul id="id-0">
														<li>
															<a href="${viewCostCenterUrl}">${fn:escapeXml(costCenter.code)}</a>
														</li>
														<li>
															<c:out value="${costCenter.name}" />
														</li>
													</ul>
												</div>
											</div>
										</fieldset>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<div class="search-empty">
										<spring:theme code="text.company.budget.noCostCentersFound.message" />
									</div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4 col-sm-offset-8" id="backButton">
						<a href="${backUrl}" class="btn btn-primary btn-block">
							<spring:theme code="text.company.budget.viewPage.back" text="Back to Budgets" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
