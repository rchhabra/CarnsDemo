<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="actionData" required="false" type="de.hybris.platform.commercefacades.travel.AccommodationBookingActionData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel-default">
	<div class="panel-body">
		<c:url value="${actionData.actionUrl}" var="actionUrl" />
		<form id="y_updateBookingDatesForm" action="${actionUrl}" method="POST" >
			<%-- Departing / Returning dates input fields --%>
			<fieldset class="fieldset">
				<legend class="sr-only">
					<spring:theme code="accommodation.booking.details.change.dates" text="Change Dates" />
				</legend>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-4">
						<p>
							<spring:theme code="accommodation.booking.details.change.dates.description" text="Would you like to change the dates of your holiday?" />
						</p>
					</div>
					<div class="col-xs-12 col-sm-4">
						<label class="sr-only" for="datePickerDeparting">
							<spring:theme code="accommodation.booking.details.change.dates.departing" text="Departing" />
						</label>
						<input type="text" name="checkInDate" class="col-xs-12 datepicker input-grid form-control y_updateBookingDatePickerCheckIn" id="accommodationDatePickerCheckIn" name="datePickerDeparting" placeholder="<spring:theme code="accommodation.booking.details.change.dates.departing" text="Departing"/>"
							autocomplete="off">
					</div>
					<div>
						<div class="col-xs-12 col-sm-4">
						<input id="y_maxAllowedCheckInCheckOutDateDifference" type="hidden" value="${fn:escapeXml(maxAllowedCheckInCheckOutDateDifference)}" />
							<label class="sr-only" for="datePickerReturning">
								<spring:theme code="accommodation.booking.details.change.dates.returning" text="Returning" />
							</label>
							<input type="text" name="checkOutDate" class="col-xs-12 datepicker input-grid form-control y_updateBookingDatePickerCheckOut" id="accommodationDatePickerCheckOut" name="datePickerReturning" placeholder="<spring:theme code="accommodation.booking.details.change.dates.returning" text="Returning"/>"
								autocomplete="off">
						</div>
					</div>
				</div>
			</fieldset>
			<div class="form-group col-xs-12 col-sm-4 col-xs-offset-0 col-sm-offset-8">
				<a class="btn btn-primary btn-block y_updateBookingDates">
					<spring:theme code="button.booking.details.accommodation.booking.action" text="Change Dates" />
				</a>
			</div>
		</form>
		 <div class="modal fade" id="y_updatedBookingPagedHtml" tabindex="-1" role="dialog" aria-hidden="true">
		 </div>
	</div>
</div>
