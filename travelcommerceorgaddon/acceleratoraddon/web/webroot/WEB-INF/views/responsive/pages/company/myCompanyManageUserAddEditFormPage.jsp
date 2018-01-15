<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common" %>
<%@ taglib prefix="customFormElement" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/customFormElement" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${empty saveUrl}">
	<c:choose>
		<c:when test="${not empty b2BCustomerForm.uid}">
			<spring:url value="/my-company/organization-management/manage-users/edit" var="saveUrl" htmlEscape="false">
				<spring:param name="user" value="${b2BCustomerForm.uid}" />
			</spring:url>
		</c:when>
		<c:otherwise>
			<spring:url value="/my-company/organization-management/manage-users/create" var="saveUrl" htmlEscape="false" />
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test="${empty cancelUrl}">
	<c:choose>
		<c:when test="${not empty b2BCustomerForm.uid}">
			<spring:url value="/my-company/organization-management/manage-users/details" var="cancelUrl" htmlEscape="false">
				<spring:param name="user" value="${b2BCustomerForm.uid}" />
			</spring:url>
		</c:when>
		<c:otherwise>
			<spring:url value="/my-company/organization-management/manage-users" var="cancelUrl" htmlEscape="false" />
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
				<c:when test="${not empty b2BCustomerForm.uid}">
					<org-common:headline url="${cancelUrl}" labelKey="text.company.${action}.edit.title" backLabelKey="text.company.manageUsers.back.button" labelArguments="${fn:escapeXml(b2BCustomerForm.parentB2BUnit)}" />
				<company:b2bCustomerForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}" b2BCustomerForm="${b2BCustomerForm}" addEditLabel="text.company.manageUser.addedituser"/>
				</c:when>
				<c:otherwise>
					<org-common:headline url="${cancelUrl}" labelKey="text.company.${action}.users.new.title" backLabelKey="text.company.manageUsers.back.button" labelArguments="${fn:escapeXml(param.unit)}" />
				<company:b2bCustomerForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}" b2BCustomerForm="${b2BCustomerForm}" addEditLabel="text.company.manageUser.adduser"/>
				</c:otherwise>
			</c:choose>

			</div>
		</div>
	</div>
</template:page>
