<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%-- Form (Manage Booking) --%>
<c:url var="manageMyBookingUrl" value="/manage-booking/login" />
<form:form commandName="manageMyBookingForm" action="${manageMyBookingUrl}" class="col-xs-12 form-background" id="y_manageBookingsForm">
	<fieldset>
		<legend class="primary-legend">
			<a class="panel-heading panel-header-link collapsable collapsed" role="button" data-toggle="collapse" href="#manage-booking" aria-expanded="false" aria-controls="manage-booking"><spring:theme code="manage.booking.title" text="Manage Booking" /></a>
		</legend>
			<%-- From / To input fields --%>
		<div id="manage-booking" class="panel-body collapse">
			<div class="row input-row stacked">
				<label class="sr-only" for="bookingReference">
					<spring:theme code="text.manage.booking.booking.reference" text="Booking Reference" />
				</label>
				<form:input type="search" class="col-xs-12 input-grid form-control" id="bookingReference" path="bookingReference" placeholder="Booking Reference" />
				<label class="sr-only" for="lastName">
					<spring:theme code="text.manage.booking.last.name" text="Last Name" />
				</label>
				<form:input type="search" class="col-xs-12 input-grid form-control" id="lastName" placeholder="Last Name" path="lastName" />
				<div class="col-xs-12 btn-wrapper">
					<button type="submit" class="btn btn-primary col-xs-12 y_manageBookingSubmit" disabled>
						<spring:theme code="button.manage.booking.submit" text="Manage Booking" />
					</button>
				</div>
			</div>
		</div>

			<%-- / From / To input fields --%>

	</fieldset>
</form:form>
<%-- / Form (Manage Booking) --%>

<div class="modal fade manage-booking-modal" tabindex="-1" id="y_manageBookingModal" role="dialog" aria-labelledby="manageBookingLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
		</div>
	</div>
</div>
