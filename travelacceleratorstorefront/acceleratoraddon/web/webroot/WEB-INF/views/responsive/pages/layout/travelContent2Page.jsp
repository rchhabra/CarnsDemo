<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<template:page pageTitle="${pageTitle}">
	<%-- LEFT WIDE --%>
	<div class="container">
		<div class="row">
			<div class="col-xs-12 col-sm-9">
				<cms:pageSlot position="LeftContent" var="feature" element="div" class="col-md-9" rowspan="2">
					<cms:component component="${feature}" />
				</cms:pageSlot>
			</div>
			<div class="col-xs-12 col-sm-3 side-to-top">
				<cms:pageSlot position="RightContent" var="feature" element="div" class="col-md-3">
					<cms:component component="${feature}" />
				</cms:pageSlot>
				<div class="promotions hidden-xs">
					<cms:pageSlot position="SideContent" var="feature" element="section">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
			</div>
		</div>
	</div>
</template:page>