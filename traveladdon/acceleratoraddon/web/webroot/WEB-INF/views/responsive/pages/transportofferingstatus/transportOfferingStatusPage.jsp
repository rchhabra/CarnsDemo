<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="transportofferingstatus" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/transportofferingstatus"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<style>
.errorblock {
	color: red;
	background-color: #ffEEEE;
	border: 1px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>

<template:page pageTitle="${pageTitle}">
	<div class="container">
		<div class="fare-selection-wrap clearfix">
			<div class="col-xs-12 col-sm-9 fare-selection-body">
				<div>
					<h2 class="h2"><spring:theme code="status.search.title" text="Flight Status" /></h2>
					<c:url var="actionURL" value="/transport-offering-status" />
					<transportofferingstatus:statusForm formId="y_transportOfferingStatusForm" action="${actionURL}" method="POST" orientation='o' />
				</div>
				<transportofferingstatus:statusResults></transportofferingstatus:statusResults>
			</div>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar" class="">
					<div class="promotions hidden-xs side-to-top">
						<cms:pageSlot position="RightContent" var="feature">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
		</div>
	</div>
</template:page>
