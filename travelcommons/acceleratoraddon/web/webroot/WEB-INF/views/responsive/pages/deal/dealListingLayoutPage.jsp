<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="deallisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/deallisting"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<div class="container">
		<div class="col-xs-12">
			<cms:pageSlot position="TopInfoHeader" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		<div class="margin-reset clearfix y_dealSelectionSection">
			<div>
				<section class="col-xs-12 primary-custom-content">
					<cms:pageSlot position="TopInfoContent" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</section>
				<%-- Deal Results --%>
				<div class="col-xs-12 deal-results-wrapper">
					<div class="results-list">
						<h3 class="h4">
							<spring:theme code="text.page.deallisting.dealresults.title" text="Please choose your deal" />
						</h3>
						<div class="row">
							<ul class="clearfix deal-items grid" aria-label="deals list">
								<cms:pageSlot position="BodyContent" var="feature">
									<cms:component component="${feature}" />
								</cms:pageSlot>
							</ul>
						</div>
					</div>
				</div>
				<%-- / Deal Results --%>
				<div class="col-xs-12">
					<div class="custom-content clearfix">
						<cms:pageSlot position="BottomContent" var="feature">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
				</div>
			</div>
		</div>
	</div>
	<deallisting:dealfinderform finderFormTitleCode = "text.page.deallisting.deal.finder.title.message" finderFormTitleText="Search More Packages"/>
</template:page>
