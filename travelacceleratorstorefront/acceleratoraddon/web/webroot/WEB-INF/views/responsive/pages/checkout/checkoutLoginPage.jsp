<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<template:page pageTitle="${pageTitle}">
	<div class="container login-page">
		<div class="checkout-login">
			<div class="row login-form-wrap">
				<div class="col-md-4">
					<cms:pageSlot position="LeftContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
				<div class="col-md-4">
					<cms:pageSlot position="CenterContentSlot" var="feature" class="checkoutLoginPageCenter">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
				<div class="col-md-4">
					<cms:pageSlot position="RightContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
			</div>
		</div>
	</div>
</template:page>