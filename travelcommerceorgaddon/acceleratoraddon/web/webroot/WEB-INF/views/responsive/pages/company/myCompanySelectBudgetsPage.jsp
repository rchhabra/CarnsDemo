<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${empty cancelUrl}">
	<c:url value="/my-company/organization-management/manage-costcenters" var="cancelUrl" />
</c:if>
<jsp:useBean id="additionalParams" class="java.util.HashMap" />
<c:set target="${additionalParams}" property="costCenterCode" value="${param.costCenterCode}" />
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
				<org-common:headline url="${cancelUrl}" labelKey="text.company.select.budgets" backLabelKey="text.company.done.button" />
				<div class="account-section-content">
					<nav:pagination top="true" showTopTotals="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" additionalParams="${additionalParams}" msgKey="text.company.budget.page"
						numberPagesShown="${numberPagesShown}" hideRefineButton="true" />
				</div>
				<c:set var="searchUrl" value="/my-company/organization-management/manage-costcenters/selectBudget?costCenterCode=${ycommerce:encodeUrl(param.costCenterCode)}&sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
				<c:choose>
					<c:when test="${not empty searchPageData.results}">
						<div class="panel panel-primary panel-list account-list">
							<div class="panel-heading">
								<h3 class="title">
									<spring:theme code="text.company.costCenter.budget.title" />
								</h3>
							</div>
							<div class="panel-body collapse in">
								<div class="form account-cards y_company-selectors">
									<fieldset class="fieldset">
										<div class="row">
											<c:forEach items="${searchPageData.results}" var="b2bBudget">
												<div id="card-${ycommerce:normalizedCode(b2bBudget.code)}" class="form-group card col-xs-12 col-sm-6 col-md-4 <c:if test='${b2bBudget.selected}'>selected</c:if>">
													<ul>
														<spring:url value="/my-company/organization-management/manage-budgets/view" var="viewBudgetUrl" htmlEscape="false">
															<spring:param name="budgetCode" value="${b2bBudget.code}" />
														</spring:url>
														<li>
															<a href="${viewBudgetUrl}">${fn:escapeXml(b2bBudget.code)}</a>
														</li>
														<li>${fn:escapeXml(b2bBudget.name)}</li>
														<li>${fn:escapeXml(b2bBudget.unit.name)}</li>
													</ul>
													<span id="span-${ycommerce:normalizedCode(b2bBudget.code)}" class="account-cards-actions">
														<spring:url value="${baseUrl}/budgets/deselect/" var="deselectUrl" htmlEscape="false">
															<spring:param name="costCenterCode" value="${param.costCenterCode}" />
															<spring:param name="budgetCode" value="${b2bBudget.code}" />
														</spring:url>
														<spring:url value="${baseUrl}/budgets/select/" var="selectUrl" htmlEscape="false">
															<spring:param name="costCenterCode" value="${param.costCenterCode}" />
															<spring:param name="budgetCode" value="${b2bBudget.code}" />
														</spring:url>
														<c:choose>
															<c:when test="${b2bBudget.selected}">
																<div class="form-group account-cards-actions col-xs-12">
																	<a href="#" url="${deselectUrl}" class="action-links y_deselectLink">
																		<button class="btn btn-primary btn-block">
																			<spring:theme code="text.company.deselect.action.label" />
																		</button>
																	</a>
																</div>
															</c:when>
															<c:otherwise>
																<div class="form-group account-cards-actions col-xs-12">
																	<a href="#" url="${selectUrl}" class="action-links y_selectLink">
																		<button class="btn btn-primary btn-block">
																			<spring:theme code="text.company.select.action.label" />
																		</button>
																	</a>
																</div>
															</c:otherwise>
														</c:choose>
													</span>
												</div>
											</c:forEach>
										</div>
									</fieldset>
								</div>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<p>
							<spring:theme code="text.company.noentries" />
						</p>
					</c:otherwise>
				</c:choose>
				<div class="clearfix">
					<div class="accountActions-bottom pull-right col-xs-12 hidden-sm hidden-md hidden-lg">
						<org-common:done url="${cancelUrl}" labelKey="text.company.done.button" />
					</div>
				</div>
				<div class="account-section-content">
					<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" additionalParams="${additionalParams}" msgKey="text.company.budget.page" numberPagesShown="${numberPagesShown}"
						hideRefineButton="true" />
				</div>
			</div>
		</div>
		<c:url value="${baseUrl}/budgets" var="budgetsActionLink" />
		<script id="enableDisableLinksTemplate" type="text/x-jquery-tmpl">
		{{if selected}}
		<div class="form-group account-cards-actions col-xs-12">
			<a href="#" url="${budgetsActionLink}/deselect/?budgetCode={{= id}}&costCenterCode=${ycommerce:encodeUrl(param.costCenterCode)}" class="action-links y_deselectLink"><button class="btn btn-primary btn-block"><spring:theme code="text.company.deselect.action.label" /></button></a>
		</div>
		{{else}}
		<div class="form-group account-cards-actions col-xs-12">
			<a href="#" url="${budgetsActionLink}/select/?budgetCode={{= id}}&costCenterCode=${ycommerce:encodeUrl(param.costCenterCode)}" class="btn btn-primary btn-block"><spring:theme code="text.company.select.action.label" /></button></a>
		</div>
		{{/if}}
		{{else}}
    </script>
</template:page>
