<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${empty editUrl}">
	<spring:url value="/my-company/organization-management/manage-costcenters/edit" var="editUrl" htmlEscape="false">
		<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
	</spring:url>
</c:if>
<spring:url value="/my-company/organization-management/manage-costcenters/selectBudget" var="selectBudgetsForCostcenterUrl" htmlEscape="false">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-costcenters/enable" var="enableCostCenterUrl" htmlEscape="false">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-costcenters/unitDetails" var="viewCostCenterUnitUrl" htmlEscape="false">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
	<spring:param name="unit" value="${b2bCostCenter.unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-costcenters" var="backToManageCostCenterUrl" htmlEscape="false">
</spring:url>
<spring:url value="/my-company/organization-management/manage-costcenters/disable" var="disableCostCenterUrl" htmlEscape="false">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
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
						<org-common:headline url="${backToManageCostCenterUrl}" labelKey="text.company.costCenter.viewDetails.label" />
					</div>
					<div class="col-xs-5 col-sm-4 button-inline">
						<org-common:back cancelUrl="${backToManageCostCenterUrl}" displayTextMsgKey="text.company.costCenter.back" />
					</div>
				</div>
				<div class="Cost Centre-details col-xs-12">
					<div class="row">
						<div class="col-xs-12 well well-lg well-tertiary">
							<div class="col-sm-12 col-no-padding">
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.costCenter.id.title" />
										</span>
										<span class="item-value"> ${fn:escapeXml(b2bCostCenter.code)} </span>
									</div>
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.costCenter.name.title" />
										</span>
										<span class="item-value"> ${fn:escapeXml(b2bCostCenter.name)} </span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.costCenter.unit.title" />
										</span>
										<span class="item-value">
											<a href="${viewCostCenterUnitUrl}"> ${fn:escapeXml(b2bCostCenter.unit.uid)} </a>
										</span>
									</div>
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.costCenter.currency.title" />
										</span>
										<span class="item-value"> ${fn:escapeXml(b2bCostCenter.currency.isocode)} </span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.costCenter.state.title" />
										</span>
										<span class="item-value">
											<c:choose>
												<c:when test="${b2bCostCenter.active}">
													<spring:theme code="text.company.costCenter.state.active" />
												</c:when>
												<c:otherwise>
													<spring:theme code="text.company.costCenter.state.inactive" />
												</c:otherwise>
											</c:choose>
										</span>
									</div>
								</div>
							</div>
							<div class="col-xs-12 col-sm-5  item-action">
								<a href="${editUrl}" class="button edit btn btn-block btn-primary">
									<spring:theme code="text.company.costCenter.button.displayName" />
								</a>
							</div>
						</div>
					</div>
				</div>
				<div class="row heading-with-button">
					<div class="col-sm-12">
						<c:choose>
							<c:when test="${b2bCostCenter.active && empty accErrorMsgs}">
								<div class="accountActions-link  col-xs-12 col-sm-4 col-sm-offset-8">
									<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-disable">
										<spring:theme code="text.company.costCenter.disable.button" text="Disable Cost Center" />
									</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="accountActions-link  col-xs-12 col-sm-4 col-sm-offset-8">
									<a href="${enableCostCenterUrl}" class="btn btn-primary btn-block">
										<spring:theme code="text.company.costCenter.enable.button" />
									</a>
								</div>
							</c:otherwise>
						</c:choose>
						<company:actionConfirmationModal id="disable" targetUrl="${disableCostCenterUrl}" modalTitle="text.company.costCenter.disable.confirm" messageKey="text.company.costCenter.disable.confirm.message" />
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<div class="panel-heading">
						<h3 class="title">
							<spring:theme code="text.company.manage.costcenter.budget.title" />
						</h3>
					</div>
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:choose>
										<c:when test="${not empty b2bCostCenter.b2bBudgetData}">
											<c:forEach items="${b2bCostCenter.b2bBudgetData}" var="b2bBudget">
												<spring:url value="/my-company/organization-management/manage-budgets/view" var="viewBudgetUrl" htmlEscape="false">
													<spring:param name="budgetCode" value="${b2bBudget.code}" />
												</spring:url>
												<div class="form-group card col-xs-12 col-sm-6 col-md-4">
													<ul class="">
														<li>
															<a href="${viewBudgetUrl}">${fn:escapeXml(b2bBudget.code)}</a>
														</li>
														<li>${fn:escapeXml(b2bBudget.name)}</li>
														<li>
															<fmt:formatDate value="${b2bBudget.startDate}" />
														</li>
														<li>
															<fmt:formatNumber value="${b2bBudget.budget}" maxFractionDigits="2" minFractionDigits="2" />
															(${fn:escapeXml(b2bBudget.currency.isocode)})
														</li>
													</ul>
												</div>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<div class="search-empty">
												<spring:theme code="text.company.budget.noBudgetMessage" />
											</div>
										</c:otherwise>
									</c:choose>
									<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
										<a href="${selectBudgetsForCostcenterUrl}">
											<button class="btn btn-primary btn-block">
												<spring:theme code="text.company.addExisting.button" />
											</button>
										</a>
									</div>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4 col-sm-offset-8" id="backButton">
						<org-common:back cancelUrl="${backToManageCostCenterUrl}" displayTextMsgKey="text.company.costCenter.back" />
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>