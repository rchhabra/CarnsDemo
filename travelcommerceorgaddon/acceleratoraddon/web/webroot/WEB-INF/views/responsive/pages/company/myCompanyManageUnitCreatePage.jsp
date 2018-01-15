<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${empty saveUrl}">
	<spring:url value="/my-company/organization-management/manage-units/create" var="saveUrl" htmlEscape="false"/>
</c:if>

<c:if test="${empty cancelUrl}">
	<spring:url value="/my-company/organization-management/manage-units/" var="cancelUrl" htmlEscape="false"/>
</c:if>
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
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.unit.details.back" labelKey="text.company.manage.units.createNewUnit" />
				<company:b2bUnitForm formUrl="${saveUrl}" b2BUnitForm="${b2BUnitForm}" cancelUrl="${cancelUrl}" addEditLabel="text.company.manage.units.unit.add.label"/>
			</div>
		</div>
	</div>
</template:page>

