<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-units/create" var="createUnitUrl" htmlEscape="false">
	<spring:param name="unit" value="" />
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
						<h2 class="h2">
							<spring:theme code="text.company.manage.units" text="Units" />
						</h2>
					</div>
					<div class="col-xs-5 col-sm-4 button-inline">
						<a href="${createUnitUrl}" class="btn btn-primary btn-block">
							<spring:theme code="text.company.manage.units.newUnitButton" text="Add New" />
						</a>
					</div>
				</div>
				<div class="row my-company-wrap">
					<div class="col-xs-12 panel-group" id="accordion" role="tablist" aria-multiselectable="true">
						<div class="panel panel-default panel-accordion non-accordion-links">
							<company:unitTree node="${rootNode}" loopIndex="1" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
