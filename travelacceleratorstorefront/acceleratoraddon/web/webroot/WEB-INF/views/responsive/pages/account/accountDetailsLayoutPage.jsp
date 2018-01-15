<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<template:page pageTitle="${pageTitle}">
	<div class="my-company-section">
		<div class="page-margin">
			<div class="container">
				<div class="col-xs-12 col-sm-3 side-to-top">
					<aside id="sidebar">
						<div class="promotions hidden-xs">
							<cms:pageSlot position="LeftContent" var="feature">
								<cms:component component="${feature}" element="section" />
							</cms:pageSlot>
						</div>
					</aside>
				</div>
				<div class="col-xs-12 col-sm-9">
					<cms:pageSlot position="RightContent" var="feature" element="div">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
			</div>
		</div>
	</div>
</template:page>