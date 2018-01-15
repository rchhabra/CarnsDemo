<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="guestdetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/guestdetails"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationReservationData"%>
<%@ attribute name="passengerTypeMaxQuantityMapPerRoom" required="true" type="java.util.Map"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-primary panel-list" ${amend?"style='display: none;'":""}>
	<div class="panel-heading">
		<h3 class="title">
			<spring:message code="text.page.personaldetails.accommodation.leaddetails.specifydetails.title" text="Please Specify" />
		</h3>
	</div>
	<div class="panel-body">
		<fieldset class="fieldset">
			<legend class="sr-only">
				<spring:message code="text.page.personaldetails.accommodation.leaddetails.specifydetails.usetraveller.checkbox.sr" text="Specify Details" />
			</legend>
			<div class="row col-xs-12">
				<label for="checkbox-y" data-toggle="collapse" data-target="#roomCollapse" aria-controls="roomCollapse">
					<spring:message code="text.page.personaldetails.accommodation.leaddetails.specifydetails.usetraveller.checkbox" text="Would you like to use different details for Hotel Reservation?" />
					<form:checkbox path="useDiffLeadDetails" class="y_guestDetails" name="booking-details" id="checkbox-y" />
				</label>
			</div>
		</fieldset>
	</div>
</div>
<%-- hotel guest details --%>
<div class="panel panel-primary panel-list room-details collapse" id="roomCollapse" role="form" aria-expanded="false">
	<div class="panel-heading">
		<h3 class="title">
			<spring:message code="ttext.page.personaldetails.accommodation.leaddetails.specifydetails.usetraveller.checkbox" text="Hotel Guest Details" />
		</h3>
	</div>
	<c:set var="numberOfRooms" value="${fn:length(reservationData.roomStays)}" />
	<input type="hidden" id="y_numberOfRooms" value="${fn:escapeXml(numberOfRooms)}" />
	<%-- Room Details --%>
	<div class="panel panel-primary panel-list room-details">
		<div class="panel-heading">
			<h3 class="title">
				<spring:theme code="text.guest.details.room.details.title" arguments="${reservationData.accommodationReference.accommodationOfferingName}" />
			</h3>
		</div>
		<div class="panel panel-default my-account-secondary-panel clearfix">
			<c:forEach var="roomStay" items="${reservationData.roomStays}" varStatus="idx">
				<div class="room clearfix y_roomStayDetails" ${roomStay.nonModifiable?"style='display: none;'":""}>
					<div class="panel-body">
						<div class="col-xs-12">
							<guestdetails:roomtype roomStay="${roomStay}" />
						</div>
						<div class="col-xs-12">
							<guestdetails:leadDetails leadDetailsForm="leadForms[${idx.index}]" formValues="${personalDetailsForms.leadForms[idx.index]}" roomStayRefNumber="${roomStay.roomStayRefNumber }" roomNum="${idx.index+1}" index="${idx.index}" checkInDate="${roomStay.checkInDate}"
								passengerTypeMaxQuantityMap="${passengerTypeMaxQuantityMapPerRoom[roomStay.roomStayRefNumber]}" />
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>
