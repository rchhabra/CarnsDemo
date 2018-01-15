<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${empty saveUrl}">
	<spring:url value="/my-company/organization-management/manage-costcenters/{/actionUrl}" var="saveUrl" htmlEscape="false">
		<spring:param name="actionUrl" value="${actionUrl}"/>
	</spring:url>
</c:if>

<c:if test="${empty cancelUrl}">
	<c:choose>
		<c:when test="${edit eq true }">
			<spring:url value="/my-company/organization-management/manage-costcenters/view"	var="cancelUrl" htmlEscape="false">
				<spring:param name="costCenterCode" value="${b2BCostCenterForm.originalCode}"/>
			</spring:url>		
		</c:when>
		<c:otherwise>
			<c:url value="/my-company/organization-management/manage-costcenters" var="cancelUrl"/>	
		</c:otherwise>
	</c:choose>
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
		<c:choose>
			<c:when test="${edit eq true }">
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.costCenter.back" labelKey="text.company.costCenter.edit.label"  />
				<company:b2bCostCenterForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}" b2BCostCenterForm="${b2BCostCenterForm}" addEditLabel="text.company.costCenter.edit.label"/>
			</c:when>
			<c:otherwise>
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.costCenter.back" labelKey="text.company.costCenter.addButton.displayName"  />
				<company:b2bCostCenterForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}" b2BCostCenterForm="${b2BCostCenterForm}" addEditLabel="text.company.costCenter.add.new.label"/>
			</c:otherwise>
		</c:choose>

        
    </div></div></div>
</template:page>