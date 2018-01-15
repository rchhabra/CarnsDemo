<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:url value="/my-account/update-profile" var="updateProfileUrl" />
<spring:url value="/my-account/update-password" var="updatePasswordUrl" />
<spring:url value="/my-account/update-email" var="updateEmailUrl" />
<spring:url value="/my-account/address-book" var="addressBookUrl" />
<spring:url value="/my-account/payment-details" var="paymentDetailsUrl" />
<spring:url value="/my-account/orders" var="ordersUrl" />
<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}">
	<div class="container">
		<h2 class="h2"><spring:theme code="text.account.title" /></h2>
		<div class="margin-reset clearfix">
			<div class="col-xs-12 col-sm-9">
				<cms:pageSlot position="TopContent" var="feature" element="div" class="accountPageTopContent">
					<div class="panel panel-primary panel-list">
						<div class="panel-heading">
							<cms:component component="${feature}" />
						</div>
					</div>
				</cms:pageSlot>
				<cms:pageSlot position="TopInfoContent" var="feature" element="div">
					<cms:component component="${feature}" />
				</cms:pageSlot>
				<cms:pageSlot position="BodyContent" var="feature" element="div" class="accountPageBodyContent">
					<div class="panel panel-primary panel-list panel-stack panel-details">
						<cms:component component="${feature}" />
					</div>
				</cms:pageSlot>
				<cms:pageSlot position="BottomContent" var="feature" element="div" class="accountPageBottomContent">
					<div class="panel panel-primary panel-list">
						<div class="panel-heading">
							<cms:component component="${feature}" />
						</div>
					</div>
				</cms:pageSlot>
			</div>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar">
					<div class="promotions hidden-xs">
						<cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
		</div>
	</div>
</template:page>
