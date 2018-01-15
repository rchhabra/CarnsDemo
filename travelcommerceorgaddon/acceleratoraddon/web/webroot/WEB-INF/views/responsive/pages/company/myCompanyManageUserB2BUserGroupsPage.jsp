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
<spring:theme code="text.company.select.action.label" var="selectAction" />
<spring:theme code="text.company.deselect.action.label" var="deselectAction" />
<c:if test="${empty cancelUrl}">
	<c:url value="/my-company/organization-management/manage-users" var="cancelUrl" />
</c:if>
<c:set var="searchUrl" value="${baseUrl}/${action}?user=${ycommerce:encodeUrl(param.user)}&sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
<jsp:useBean id="sortQueryParams" class="java.util.HashMap" />
<c:set target="${sortQueryParams}" property="user" value="${param.user}" />
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
				<org-common:headline url="${cancelUrl}" labelKey="text.company.manageUsers.${action}.title" backLabelKey="text.company.done.button" />
				<div class="account-section-content">
					<nav:pagination top="true" showTopTotals="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" additionalParams="${sortQueryParams}" msgKey="text.company.${action}.page"
						numberPagesShown="${numberPagesShown}" hideRefineButton="true" />
					<c:choose>
						<c:when test="${not empty searchPageData.results}">
							<div class="panel panel-primary panel-list account-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="text.company.manageUsers.${action}.header.title" />
									</h3>
								</div>
								<div class="panel-body collapse in">
									<div class="form account-cards y_company-selectors">
										<fieldset class="fieldset">
											<div class="row">
												<c:forEach items="${searchPageData.results}" var="result">
													<spring:url value="/my-company/organization-management/manage-usergroups/details/" var="viewUserGroupUrl" htmlEscape="false">
														<spring:param name="usergroup" value="${result.uid}" />
													</spring:url>
													<spring:url value="/my-company/organization-management/manage-units/details/" var="unitUrl" htmlEscape="false">
														<spring:param name="unit" value="${result.unit.uid}" />
													</spring:url>
													<div id="row-${ycommerce:normalizedCode(result.uid)}" class="form-group card col-xs-12 col-sm-6 col-md-4 <c:if test='${result.selected}'>selected</c:if>">
														<ul class="pull-left">
															<li>
																<ycommerce:testId code="${action}_id_link">
																	<a href="${viewUserGroupUrl}">${fn:escapeXml(result.uid)}</a>
																</ycommerce:testId>
															</li>
															<li>
																<ycommerce:testId code="${action}_name_link">${fn:escapeXml(result.name)}</ycommerce:testId>
															</li>
															<li>
																<ycommerce:testId code="${action}_b2bunit_label">
																	<a href="${unitUrl}">${fn:escapeXml(result.unit.name)}</a>
																</ycommerce:testId>
															</li>
														</ul>
														<ycommerce:testId code="${action}_actions_label">
															<span id="selection-${ycommerce:normalizedCode(result.uid)}" class="account-cards-actions">
																<spring:url value="${baseUrl}/${action}/select/" var="selectUrl" htmlEscape="false">
																	<spring:param name="user" value="${param.user}" />
																	<spring:param name="usergroup" value="${result.uid}" />
																</spring:url>
																<spring:url value="${baseUrl}/${action}/deselect/" var="deselectUrl" htmlEscape="false">
																	<spring:param name="user" value="${param.user}" />
																	<spring:param name="usergroup" value="${result.uid}" />
																</spring:url>
																<c:choose>
																	<c:when test="${result.selected}">
																		<div class="form-group account-cards-actions col-xs-12">
																			<a href="#" url="${deselectUrl}" class="action-links y_deselectUser">
																				<button class="btn btn-primary btn-block">
																					<spring:theme code="text.company.deselect.action.label" />
																				</button>
																			</a>
																		</div>
																	</c:when>
																	<c:otherwise>
																		<div class="form-group account-cards-actions col-xs-12">
																			<a href="#" url="${selectUrl}" class="action-links y_selectUser">
																				<button class="btn btn-primary btn-block">
																					<spring:theme code="text.company.select.action.label" />
																				</button>
																			</a>
																		</div>
																	</c:otherwise>
																</c:choose>
															</span>
														</ycommerce:testId>
													</div>
												</c:forEach>
											</div>
										</fieldset>
									</div>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<spring:theme code="text.company.noentries" />
						</c:otherwise>
					</c:choose>
					<div class="clearfix">
						<div class="accountActions-bottom pull-right col-xs-12 hidden-sm hidden-md hidden-lg">
							<org-common:done url="${cancelUrl}" labelKey="text.company.done.button" />
						</div>
					</div>
					<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" additionalParams="${sortQueryParams}" msgKey="text.company.${action}.page" numberPagesShown="${numberPagesShown}"
						hideRefineButton="true" />
				</div>
			</div>
		</div>
	</div>
	<c:url value="${baseUrl}/${action}" var="actionLink" />
	<script id="enableDisableLinksTemplate" type="text/x-jquery-tmpl">
        {{if selected}}
		<div class="form-group account-cards-actions col-xs-12">
            <a href="#" url="${actionLink}/deselect/?usergroup={{= id}}&user=${param.user}" class="action-links y_deselectUser"><button class="btn btn-primary btn-block"><spring:theme code="text.company.deselect.action.label" /></button></a></div>
        {{else}}
		<div class="form-group account-cards-actions col-xs-12">
            <a href="#" url="${actionLink}/select/?usergroup={{= id}}&user=${param.user}" class="action-links y_selectUser"><button class="btn btn-primary btn-block"><spring:theme code="text.company.select.action.label" /></button></a></div>
        {{/if}}
    </script>
</template:page>
