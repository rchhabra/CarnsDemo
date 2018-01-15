<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:url value="${redirectUrl}" var="continueUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<div class="container">
		<div class="col-xs-12 col-sm-3 side-to-top">
			<aside id="sidebar">
				<div class="promotions hidden-xs">
					<cms:pageSlot position="SideContent" var="feature" element="section">
						<cms:component component="${feature}"/>
					</cms:pageSlot>
				</div>
			</aside>
		</div>
		<div class="col-xs-12 col-sm-9">
			<h2 class="h2"><spring:theme code="checkout.error.paymentethod.formentry.hop.failed.header" text="Checkout error"/></h2>
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="title"><spring:theme code="checkout.error.paymentethod.formentry.hop.failed.error.header" text="Checkout error"/></h3>
				</div>
				<div class="panel-body">
					<div class="row">
						<div class="col-xs-12">
			       			<p> <spring:theme code="checkout.multi.hostedOrderPageError.${decision}.${reasonCode}"/></p>
						</div>
						<div class="col-xs-12">
							<a class="btn btn-primary" href="${continueUrl}"><spring:theme code="checkout.multi.hostedOrderPageError.continue" text="continue"/></a>
					    </div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
