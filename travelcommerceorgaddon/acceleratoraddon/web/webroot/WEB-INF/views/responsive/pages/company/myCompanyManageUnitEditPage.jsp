<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-units/edit" var="editUnitUrl" htmlEscape="false">
	<spring:param name="unit" value="${b2BUnitForm.originalUid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details" var="cancelUrl" htmlEscape="false">
	<spring:param name="unit" value="${b2BUnitForm.originalUid}" />
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
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.unit.edit.back" labelKey="text.company.manage.units.unit.edit.title" labelArguments="${empty b2BUnitForm.name ? b2BUnitForm.uid : b2BUnitForm.name}" />
				<company:b2bUnitForm formUrl="${editUnitUrl}" b2BUnitForm="${b2BUnitForm}" cancelUrl="${cancelUrl}" addEditLabel="text.company.manage.units.unit.edit.label"/>
			</div>
		</div>
	</div>
</template:page>
