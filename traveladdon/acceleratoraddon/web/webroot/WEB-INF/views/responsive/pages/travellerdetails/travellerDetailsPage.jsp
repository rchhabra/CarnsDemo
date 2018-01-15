<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="travellerdetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/travellerdetails"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:url var="submitUrl" value="/checkout/traveller-details" />
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	<progress:bookingProgressBar stage="personalDetails" bookingJourney="${bookingJourney}" />
	<div class="container panel-details">
		<div class="margin-reset clearfix">
			<form:form id="y_travellerForms" modelAttribute="travellerDetailsForms" action="${submitUrl}" method="POST">
				<div class="global-alerts">
					<c:set var="formErrors">
						<form:errors path="*" />
					</c:set>
					<c:if test="${not empty formErrors}">
						<div class="alert alert-danger">
							<span>
								<spring:theme code="text.page.travellerdetails.form.error.globalmessage" text="Please make sure all fields are filled." />
							</span>
						</div>
					</c:if>
				</div>
				<div class="col-xs-12">
					<h2 class="h2">
						<spring:theme code="text.page.travellerdetails.title" text="Please enter you passenger details" />
					</h2>
				</div>
				<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
					<c:choose>
						<c:when test="${not empty travellerDetailsForms.travellerForms}">
							<c:set var="travellerForm" value="${travellerDetailsForms.travellerForms[0]}" />
							<div class="panel panel-primary panel-list">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="text.page.travellerdetails.areyoutravelling.title" text="Please specify" />
									</h3>
								</div>
								<div class="panel-body">
									<form:errors path="travellerForms[${0}].booker" cssClass="alert alert-danger" element="div" />
									<fieldset class="fieldset">
										<legend class="sr-only">
											<spring:theme code="sr.travellerdetails.areyoutravelling" />
										</legend>
										<p class="form-label">
											<spring:theme code="text.page.travellerdetails.form.heading.areyoutravelling" text="Are you travelling?" />
										</p>
										<div class="row">
											<div class="col-xs-10 col-md-5 form-group">
												<label for="travellerForms0.booker1" class="radio-inline col-xs-3">
													<form:radiobutton path="travellerForms[${0}].booker" class="y_bookerIsTravelling" value="true" checked="${travellerForm.booker?'checked':'' }" />
													<spring:theme code="text.page.travellerdetails.form.option.yes" text="Yes" />
												</label>
												<label for="travellerForms0.booker2" class="radio-inline col-xs-3">
													<form:radiobutton path="travellerForms[${0}].booker" class="y_bookerIsTravelling" value="false" checked="${travellerForm.booker?'':'checked' }" />
													<spring:theme code="text.page.travellerdetails.form.option.no" text="No" />
												</label>
											</div>
										</div>
											<%-- / .row --%>
									</fieldset>
								</div>
							</div>
							<%-- Additional security section --%>
							<c:if test="${showAdditionalSecurity}">
								<div class="panel panel-primary panel-list">
									<div class="panel-heading">
										<h3 class="title">
											<spring:theme code="text.page.travellerdetails.additionalsecurity.title" text="Additional security" />
										</h3>
									</div>
									<div class="panel-body">
										<fieldset class="fieldset">
											<legend class="sr-only">
												<spring:theme code="text.page.travellerdetails.additionalsecurity" text="Additional security" />
											</legend>
											<c:set var="additionalSecurityTooltip">
												<spring:theme code="text.page.travellerdetails.additionalsecurity.tooltip"
															  text="Please mark this checkbox if you want to add additional security to your booking. In case the additional security is active, every traveller will have to provide a passenger reference together with the last name and Booking reference in order to access the reservation. Moreover they will be able to amend only their part of the booking. These restriction do not apply to the traveller that is placing the booking in case he is logged in." />
											</c:set>
											<span class="glyphicon glyphicon-lock"></span>
											<a tabindex="0" class="form-label" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(additionalSecurityTooltip)}">
												<spring:theme code="text.page.travellerdetails.additionalsecurity.form" text="Do you want to apply additional security to your booking?" />
												&nbsp;<i class="glyphicon glyphicon-info-sign"></i>
											</a>
											<label for="additionalSecurityActive" class="radio-inline">
												<form:checkbox path="additionalSecurityActive" value="true" checked="${additionalSecurityActive?'checked':'' }" />
											</label>
										</fieldset>
									</div>
								</div>
							</c:if>
							<%-- End of Additional security section --%>
							<div class="panel panel-primary panel-list panel-stack">
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="text.page.travellerdetails.form.title" text="Passenger Details" />
									</h3>
								</div>
								<c:set var="isCustomer" value="false" />
								<sec:authorize access="hasAnyRole('ROLE_CUSTOMERGROUP')">
									<c:set var="isCustomer" value="true" />
								</sec:authorize>
								<c:choose>
									<c:when test="${fn:length(travellerDetailsForms.travellerForms) gt 9}">
										<div class="panel-group" id="travellerGroups" role="tablist" aria-multiselectable="true">
											<div class="panel panel-default">
												<div class="panel-body">
													<c:set var="sizeOfPreviousList" value="0" />
													<c:forEach var="travellersPerType" items="${travellersPerTypeMap}" varStatus="travellerTypeIndex">
														<c:if test="${fn:length(travellersPerType.value.travellerForms) gt 0}">
															<div class="panel-heading" role="tab" id="${travellersPerType.key}Group">
																<h4 class="panel-title">
																	<c:set var="labelKey" value="text.page.travellerdetails.form.${travellersPerType.key}" />
																	<c:if test="${fn:length(travellersPerType.value.travellerForms) gt 1}">
																		<c:set var="labelKey" value="text.page.travellerdetails.form.${travellersPerType.key}.multiple" />
																	</c:if>
																	<a role="button" data-toggle="collapse" data-parent="#travellerGroups" href="#${travellersPerType.key}Collapse" aria-expanded="true" aria-controls="${travellersPerType.key}Collapse">
																		<spring:theme code="${labelKey}" arguments="${fn:length(travellersPerType.value.travellerForms)}" />
																	</a>
																</h4>
															</div>
															<div id="${travellersPerType.key}Collapse" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="${travellersPerType.key}Group">
																<travellerdetails:groupTravellers travellerForms="${travellersPerType.value.travellerForms}" adultsTitles="${adultsTitles}" childrenTitles="${childrenTitles}" reasonFortravel="${reasonForTravelOptions}" reservationData="${reservationData}" isCollapsable="false"
																								  showPhoneNumber="false" relativeIndex="${sizeOfPreviousList}" isCustomer="${isCustomer}" currentIndex="${travellerTypeIndex.index}"/>
															</div>
															<c:set var="sizeOfPreviousList" value="${fn:length(travellersPerType.value.travellerForms) + sizeOfPreviousList}" />
														</c:if>
													</c:forEach>
												</div>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="panel-body">
											<travellerdetails:travellers travellerForms="${travellerDetailsForms.travellerForms}" adultsTitles="${adultsTitles}" childrenTitles="${childrenTitles}" reasonFortravel="${reasonForTravelOptions}" savedTravellers="${savedTravellers}" reservationData="${reservationData}"
																		 isCollapsable="false" showPhoneNumber="false" isCustomer="${isCustomer}"/>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="row bottom-row">
								<div class="col-xs-12 col-sm-4 pull-right">
									<button type="submit" class="btn btn-secondary col-xs-12">
										<spring:theme code="text.page.travellerdetails.button.continue" text="Continue" />
									</button>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<span>
								<spring:theme code="text.page.travellerdetails.notravellersfound" text="No travellers found" />
							</span>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="col-xs-12 col-sm-3">
					<aside id="sidebar" class="reservation y_reservationSideBar">
						<div class="main-wrap">
							<cms:pageSlot position="Reservation" var="feature" element="div">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
						<div class="promotions hidden-xs">
							<cms:pageSlot position="SideContent" var="feature" element="section">
								<cms:component component="${feature}" />
							</cms:pageSlot>
						</div>
					</aside>
				</div>
			</form:form>
		</div>
	</div>
	<reservation:fullReservationOverlay />
</template:page>
