<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="personaldetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/personaldetails"%>
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
<spring:htmlEscape defaultHtmlEscape="true" />
<c:url var="submitUrl" value="/checkout/personal-details" />
<template:page pageTitle="${pageTitle}">
	<progress:travelBookingProgressBar stage="personaldetails" bookingJourney="${bookingJourney}" />
	<div class="container panel-details">
		<div class="margin-reset clearfix">
			<form:form id="y_personalForms" modelAttribute="personalDetailsForms" action="${submitUrl}" method="POST">
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
						<spring:theme code="text.page.personaldetails.title" text="Please enter your personal details" />
					</h2>
				</div>
				<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">
					<c:choose>
						<c:when test="${not empty personalDetailsForms.travellerForms}">
							<c:set var="travellerForm" value="${personalDetailsForms.travellerForms[0]}" />
							<div class="panel panel-primary panel-list" ${amend?"style='display: none;'":""}>
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="text.page.travellerdetails.areyoutravelling.title" text="Please specify" />
									</h3>
								</div>
								<div class="panel-body">
									<form:errors path="travellerForms[0].booker" cssClass="alert alert-danger" element="div" />
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
													<form:radiobutton path="travellerForms[0].booker" class="y_bookerIsTravelling" value="true" checked="${travellerForm.booker?'checked':'' }" />
													<spring:theme code="text.page.travellerdetails.form.option.yes" text="Yes" />
												</label>
												<label for="travellerForms0.booker2" class="radio-inline col-xs-3">
													<form:radiobutton path="travellerForms[0].booker" class="y_bookerIsTravelling" value="false" checked="${travellerForm.booker?'':'checked' }" />
													<spring:theme code="text.page.travellerdetails.form.option.no" text="No" />
												</label>
											</div>
										</div>
									</fieldset>
								</div>
							</div>
							<div class="panel panel-primary panel-list panel-stack panel-details" ${amend?"style='display: none;'":""}>
								<div class="panel-heading">
									<h3 class="title">
										<spring:theme code="text.page.travellerdetails.form.title" text="Passenger Details" />
									</h3>
								</div>
								<div class="panel-body">
									<c:set var="isCustomer" value="false" />
									<sec:authorize access="hasAnyRole('ROLE_CUSTOMERGROUP')">
										<c:set var="isCustomer" value="true" />
									</sec:authorize>
									<travellerdetails:travellers travellerForms="${personalDetailsForms.travellerForms}" adultsTitles="${adultsTitles}" childrenTitles="${childrenTitles}" reasonFortravel="${reasonForTravelOptions}" savedTravellers="${savedTravellers}" reservationData="${transportReservationData}"
										isCollapsable="true" showPhoneNumber="true" isCustomer="${isCustomer}"/>
								</div>
							</div>
							<personaldetails:accommodationleaddetails reservationData="${reservationData}" passengerTypeMaxQuantityMapPerRoom="${passengerTypeMaxQuantityMapPerRoom}" />
							<div class="row bottom-row">
								<div class="col-xs-12 col-sm-4 pull-right">
									<button type="submit" class="btn btn-secondary col-xs-12 y_personalDetailsSubmit disabled">
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
