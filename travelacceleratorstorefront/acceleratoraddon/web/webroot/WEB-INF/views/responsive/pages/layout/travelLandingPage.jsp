<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/common"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<template:page pageTitle="${pageTitle}">
	<div class="landing-form-background background-${fn:escapeXml(siteUid)}">
		<div class="container landing-form travelforms">
			<div class="form-wrapper col-xs-12 col-xs-offset-0 col-sm-11 col-sm-offset-1">
				<div class="row">
					<%-- FARE FINDER --%>
					<div class="col-sm-6">
						<div class="form-shadow">
							<cms:pageSlot position="Section1" var="feature">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
					</div>

					<div class="col-xs-12 col-sm-4">
						<div class="form-shadow">
							<div class="panel panel-primary panel-list clearfix">
								<%-- MANAGE MY BOOKING --%>
								<cms:pageSlot position="Section2A" var="feature">
									<cms:component component="${feature}" />
								</cms:pageSlot>
							</div>
							<div class="panel panel-primary panel-list clearfix">
								<%-- FLIGHT STATUS --%>
								<cms:pageSlot position="Section2B" var="feature">
									<cms:component component="${feature}" />
								</cms:pageSlot>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="cta-section">
				<cms:pageSlot position="Section4A" var="feature">
					<cms:component component="${feature}" element="div" class="col-xs-12 col-sm-6 col-md-3 relative" />
				</cms:pageSlot>
			</div>
		</div>
	</div>
</template:page>
