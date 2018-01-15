<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${empty cancelUrl}">
	<spring:url value="/my-company/organization-management/manage-units/" var="cancelUrl" htmlEscape="false" />
</c:if>
<c:set var="searchUrl" value="${baseUrl}/${action}?unit=${ycommerce:encodeUrl(param.unit)}&role=${ycommerce:encodeUrl(param.role)}&sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
<jsp:useBean id="additionalParams" class="java.util.HashMap" />
<c:set target="${additionalParams}" property="unit" value="${param.unit}" />
<c:set target="${additionalParams}" property="role" value="${param.role}" />
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
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.done.button" labelKey="text.company.manage.units.${action}.mainTitle" />
				<nav:pagination top="true" showTopTotals="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.${action}.page" additionalParams="${additionalParams}"
					numberPagesShown="${numberPagesShown}" />
				<div class="col-xs-12">
					<div class="row">
						<div class="panel panel-primary panel-list account-list">
							<div class="panel-heading">
								<h3 class="title">
									<spring:theme code="text.company.manage.units.${action}"></spring:theme>
								</h3>
							</div>
							<div class="panel-body collapse in ${empty searchPageData.results ? 'content-empty' : ''}">
								<div class="form account-cards y_company-selectors">
									<fieldset class="fieldset">
										<div class="row">
											<c:forEach items="${searchPageData.results}" var="user">
												<spring:url value="/my-company/organization-management/manage-users/details" var="viewUrl" htmlEscape="false">
													<spring:param name="user" value="${user.uid}" />
												</spring:url>
												<spring:url value="${baseUrl}/members/select/" var="selectUrl" htmlEscape="false">
													<spring:param name="user" value="${user.uid}" />
													<spring:param name="role" value="${param.role}" />
												</spring:url>
												<spring:url value="${baseUrl}/members/deselect/" var="deselectUrl" htmlEscape="false">
													<spring:param name="user" value="${user.uid}" />
													<spring:param name="role" value="${param.role}" />
												</spring:url>
												<c:choose>
													<c:when test="${user.active && user.selected}">
														<div class="form-group card col-xs-12 col-sm-6 col-md-4 selected">
													</c:when>
													<c:otherwise>
														<div class="form-group card col-xs-12 col-sm-6 col-md-4">
													</c:otherwise>
												</c:choose>
												<ul class="pull-left">
													<li>
														<ycommerce:testId code="name_${action}_link">
															<a href="${viewUrl}">${fn:escapeXml(user.name)}</a>
														</ycommerce:testId>
													</li>
													<li>
														<ycommerce:testId code="email_${action}_label">
		                                       ${fn:escapeXml(user.uid)}
		                                   </ycommerce:testId>
													</li>
													<li>
														<ycommerce:testId code="b2bunit_${action}_label">
		                                       ${fn:escapeXml(user.unit.name)}
		                                   </ycommerce:testId>
													</li>
													<li class="roles-wrapper clearfix">
														<b><spring:theme code="text.company.roles.all.label" /></b>
													</li>
													<c:forEach items="${user.roles}" var="role">
														<li>
															<spring:theme code="b2busergroup.${role}.name" />
														</li>
													</c:forEach>
													<li>
														<ycommerce:testId code="status_${action}_label">
															<spring:theme code="text.company.status.active.${user.active}" />
														</ycommerce:testId>
													</li>
												</ul>
												<div class="form-group account-cards-actions col-xs-12">
													<c:choose>
														<c:when test="${user.active}">
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
														</c:when>
														<c:otherwise>
															<button class="btn btn-primary btn-block" disabled>Select</button>
														</c:otherwise>
													</c:choose>
												</div>
										</div>
										</c:forEach>
									</div>
								</fieldset>
							</div>
						</div>
					</div>
				</div>
			</div>
			<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.${action}.page" additionalParams="${additionalParams}"
				numberPagesShown="${numberPagesShown}" />
		</div>
	</div>
</template:page>