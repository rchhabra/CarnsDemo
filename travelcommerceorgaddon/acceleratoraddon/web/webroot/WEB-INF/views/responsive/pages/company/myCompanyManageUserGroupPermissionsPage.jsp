<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
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
											<c:forEach items="${searchPageData.results}" var="result">
												<c:choose>
													<c:when test="${result.selected}">
														<div class="form-group card col-xs-12 col-sm-6 col-md-4 selected">
													</c:when>
													<c:otherwise>
														<div class="form-group card col-xs-12 col-sm-6 col-md-4">
													</c:otherwise>
												</c:choose>
												<company:permissionCardDetails permission="${result}" action="${action}" listCSSClass="pull-left col-xs-10" />
												<spring:url value="${baseUrl}/${action}/select/" var="selectUrl" htmlEscape="false">
													<spring:param name="usergroup" value="${param.usergroup}" />
													<spring:param name="permission" value="${result.code}" />
												</spring:url>
												<spring:url value="${baseUrl}/${action}/deselect/" var="deselectUrl" htmlEscape="false">
													<spring:param name="usergroup" value="${param.usergroup}" />
													<spring:param name="permission" value="${result.code}" />
												</spring:url>
												<div class="form-group account-cards-actions col-xs-12">
													<c:choose>
														<c:when test="${result.selected}">
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
</template:page>
