<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}">
	<div class="container login-page">
		<div class="row">
			<div class="col-xs-12 col-sm-offset-1 col-sm-10 login-form-wrap">
				<div class="col-xs-12">
					<h2 class="h2"><spring:theme code="login.register.title" text="Login / Register" /></h2>
				</div>
				<div class="col-sm-5 clearfix divide">
					<cms:pageSlot position="LeftContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
				<div class="col-sm-offset-2 col-sm-5 clearfix">
					<cms:pageSlot position="RightContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
			</div>
		</div>
	</div>
</template:page>
