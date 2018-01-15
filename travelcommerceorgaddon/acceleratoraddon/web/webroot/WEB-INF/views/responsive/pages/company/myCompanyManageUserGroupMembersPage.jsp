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
	<c:url value="/my-company/organization-management/manage-usergroups" var="cancelUrl" />
</c:if>
<c:set var="searchUrl" value="${baseUrl}/${action}?usergroup=${ycommerce:encodeUrl(param.usergroup)}&sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
<jsp:useBean id="additionalParams" class="java.util.HashMap" />
<c:set target="${additionalParams}" property="user" value="${param.user}" />
<c:set target="${additionalParams}" property="usergroup" value="${param.usergroup}" />
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
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.done.button" labelKey="text.company.usergroups.${action}.title" />
				<nav:pagination top="true" showTopTotals="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.${action}.page" additionalParams="${additionalParams}"
					numberPagesShown="${numberPagesShown}" showCurrentPageInfo="true" />
				<div class="panel panel-primary panel-list account-list">
					<div class="panel-heading">
						<h3 class="title">
							<spring:theme code="text.company.usergroups.${action}.breadcrumb"></spring:theme>
						</h3>
					</div>
					<c:choose>
						<c:when test="${not empty searchPageData.results}">
							<div class="panel-body collapse in ${empty searchPageData.results ? 'content-empty' : ''}">
								<div class="form account-cards y_company-selectors">
									<fieldset class="fieldset">
										<div class="row">
											<c:forEach items="${searchPageData.results}" var="user">
												<c:choose>
													<c:when test="${user.selected}">
														<div class="form-group card col-xs-12 col-sm-6 col-md-4 selected">
													</c:when>
													<c:otherwise>
														<div class="form-group card col-xs-12 col-sm-6 col-md-4">
													</c:otherwise>
												</c:choose>
												<spring:url value="/my-company/organization-management/manage-users/details" var="viewUrl" htmlEscape="false">
													<spring:param name="user" value="${user.uid}" />
												</spring:url>
												<spring:url value="${baseUrl}/${action}/select/" var="selectUrl" htmlEscape="false">
													<spring:param name="user" value="${user.uid}" />
													<spring:param name="usergroup" value="${param.usergroup}" />
												</spring:url>
												<spring:url value="${baseUrl}/${action}/deselect/" var="deselectUrl" htmlEscape="false">
													<spring:param name="user" value="${user.uid}" />
													<spring:param name="usergroup" value="${param.usergroup}" />
												</spring:url>
												<spring:url value="/my-company/organization-management/manage-units/details/" var="unitDetailUrl" htmlEscape="false">
													<spring:param name="unit" value="${user.unit.uid}" />
												</spring:url>
												<ul class="pull-left">
													<li>
														<ycommerce:testId code="${action}_name_link">
															<a href="${viewUrl}">${fn:escapeXml(user.name)}</a>
														</ycommerce:testId>
													</li>
													<li>
														<ycommerce:testId code="${action}_b2bunit_label">
															<a href="${unitDetailUrl}">${fn:escapeXml(user.unit.name)}</a>
														</ycommerce:testId>
													</li>
													<li class="roles-wrapper clearfix">
														<b><spring:theme code="text.company.roles.all.label" /></b>
														<ycommerce:testId code="${action}_roles_label">
															<ul id="roles-${user.normalizedUid}">
																<c:forEach items="${user.roles}" var="role">
																	<li>
																		<spring:theme code="b2busergroup.${role}.name" />
																	</li>
																</c:forEach>
															</ul>
														</ycommerce:testId>
													</li>
												</ul>
												<div class="form-group account-cards-actions col-xs-12">
													<c:choose>
														<c:when test="${user.selected}">
															<a href="#" url="${deselectUrl}" class="y_deselectUser">
																<button class="btn btn-primary btn-block">Deselect</button>
															</a>
														</c:when>
														<c:otherwise>
															<a href="#" url="${selectUrl}" class="y_selectUser">
																<button class="btn btn-primary btn-block">Select</button>
															</a>
														</c:otherwise>
													</c:choose>
												</div>
										</div>
										</c:forEach>
								</div>
								</fieldset>
							</div>
				</div>
				</c:when>
				<c:otherwise>
					<p>
						<spring:theme code="text.company.noentries" />
					</p>
				</c:otherwise>
				</c:choose>
			</div>
			<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.${action}.page" additionalParams="${additionalParams}"
				numberPagesShown="${numberPagesShown}" />
		</div>
	</div>
	
	<script id="userRolesTemplate" type="text/x-jquery-tmpl">
	{{each displayRoles}}
		<li>{{= $value}}</li>
	{{/each}}
	</script>
</template:page>