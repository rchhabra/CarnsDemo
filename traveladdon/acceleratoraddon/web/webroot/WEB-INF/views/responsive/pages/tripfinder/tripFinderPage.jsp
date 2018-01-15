<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="transportfacility" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/transportfacility"%>
<template:page pageTitle="${pageTitle}">
	<div class="container">
		<div class="trip-finder-wrap clearfix">
			<div class="col-xs-12">
				<cms:pageSlot position="TopContent" var="feature">
					<cms:component component="${feature}" />
				</cms:pageSlot>
			</div>
			<div class="col-xs-12 col-sm-9">
				<div class="row">
					<div class="col-xs-12 col-sm-4">
						<div class="y_tripFinderTransportFacilitySearch row trip-finder-origin-hide">
							<transportfacility:transportFacilitySearch />
						</div>
					</div>
				</div>
			</div>
			<div id="y_destinationLocationList" class="col-xs-12 col-sm-9 trip-finder-table">
				<%-- RESULTS HERE --%>
			</div>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar">
					<div class="promotions hidden-xs">
						<cms:pageSlot position="RightContent" var="feature">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
		</div>
	</div>
	<div class="modal fade transparent" id="y_fareFinderModal" tabindex="-1" role="dialog" aria-labelledby="trip-finder-modal-title">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<div class="container">
						<div class="form-wrapper col-xs-12 col-xs-offset-0 col-sm-6 col-sm-offset-1">
							<cms:pageSlot position="FareFinderContent" var="feature" element="div">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
