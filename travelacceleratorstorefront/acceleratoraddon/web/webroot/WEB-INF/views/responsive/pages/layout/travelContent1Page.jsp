<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<template:page pageTitle="${pageTitle}">
	<div class="container">
		<cms:pageSlot position="MiddleContent" var="feature">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div>
</template:page>