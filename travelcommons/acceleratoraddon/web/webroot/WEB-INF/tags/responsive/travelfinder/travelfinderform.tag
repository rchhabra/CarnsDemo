<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="farefinder" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/farefinder"%>
<%@ taglib prefix="accommodation" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationfinder"%>
<%@ taglib prefix="passenger" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/passenger"%>
<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="bookingJourney" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:url var="finderUrl" value="${url}" />
<form:form commandName="travelFinderForm" action="${finderUrl}" method="POST" class="fe-validate form-background form-booking-trip" id="y_travelFinderForm">
	<input type='hidden' value="${bookingJourney}" class="y_bookingJourney">
	<fieldset>
		<c:if test="${hideFinderTitle ne true}">
			<legend class="with-icon heading-booking-trip primary-legend" id="trip-finder-modal-title">
				<c:if test="${!showComponent}">
					<a role="button" data-toggle="collapse" aria-expanded="true" data-target=".panel-modify" class="panel-heading panel-header-link collapsable collapsed">
				</c:if>
				<c:choose>
					<c:when test="${!showComponent}">
						<spring:theme code="text.cms.farefinder.modify.title" text="Modify Booking" />
					</c:when>
					<c:otherwise>
						<spring:theme code="text.cms.farefinder.title" text="Booking Trip" />
					</c:otherwise>
				</c:choose>
				<c:if test="${!showComponent}">
					</a>
				</c:if>
			</legend>
			<button type="button" class="modal-only close" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
		</c:if>
		<div class="${ showComponent ? '' : 'panel-modify collapse'} ">
			<div class="fieldset-inner-wrapper">
				<c:set var="formPrefix" value="fareFinderForm." />
				<farefinder:transportInfo tripType="${travelFinderForm.fareFinderForm.tripType}" formPrefix="${formPrefix}" idPrefix="travel" />
				<%-- Begin  number of rooms  --%>
				<div class="row input-row less-margin">
					<div class="col-xs-12 col-sm-6">
						<label class="sr-only" for="hotelAccommodationQuantity">
							<spring:theme var="roomQuantityPlaceholderText" code="text.cms.accommodationfinder.accommodationquantity.placeholder" text="Accommodation Quantity" />
							${fn:escapeXml(roomQuantityPlaceholderText)}
						</label>
						<form:select class="form-control y_accommodationRoomQuantity" id="hotelAccommodationQuantity" path="accommodationFinderForm.numberOfRooms">
							<form:option value="-1" disabled="true">
								<spring:theme code="text.cms.accommodationfinder.accommodation.default.option" text="Rooms" />
							</form:option>
							<c:forEach var="entry" items="${accommodationsQuantity}">
								<form:option value="${fn:trim(entry)}">
									<c:choose>
										<c:when test="${fn:replace(entry,' ', '') == '1' }">${fn:escapeXml(entry)}
	                                        <spring:theme code="text.cms.accommodationfinder.accommodation.single.room" text="Room" />
										</c:when>
										<c:otherwise>${fn:escapeXml(entry)}
	                                        <spring:theme code="text.cms.accommodationfinder.accommodation.multiple.rooms" text="Rooms" />
										</c:otherwise>
									</c:choose>
								</form:option>
							</c:forEach>
						</form:select>
					</div>
					<div class="col-xs-12">
						<label for="y_hotel-part-stay">
							<spring:theme code="text.cms.travelfinder.accommodation.partstay" text="I need a hotel for different dates" />
							&nbsp;<input type='checkbox' ${not empty showCheckInCheckOut and showCheckInCheckOut?' checked   ':'  '} ${travelFinderForm.fareFinderForm.tripType=='SINGLE'?'disabled=true':''} id="y_hotel-part-stay">
							<form:input type="hidden" id="y_hotel-part-stay-value" path="accommodationFinderForm.partHotelStay" />
						</label>
					</div>
				</div>
			</div>
			<%-- End number of rooms  --%>
			<accommodation:accommodationfinder formPrefix="accommodationFinderForm." idPrefix="travel" />
			<div class="row input-row less-margin">
				<c:forEach var="roomCandidatesEntry" items="${travelFinderForm.accommodationFinderForm.roomStayCandidates}" varStatus='idx'>
					<div id="room[${idx.index+1}]" class="col-xs-12 guest-types ${ idx.index >= travelFinderForm.accommodationFinderForm.numberOfRooms ? 'hidden' : ''}">
						<p>
							<spring:theme code="text.cms.travelfinder.room" arguments="${idx.index+1}" />
						</p>
						<div class="row y_passengerGroup">
							<c:set var="formPrefix" value="accommodationFinderForm.roomStayCandidates[${idx.index}]." />
							<form:input path="${formPrefix}roomStayCandidateRefNumber" type="hidden" />
							<passenger:passengertypequantity formPrefix="${formPrefix}" passengerTypeQuantityList="${roomCandidatesEntry.passengerTypeQuantityList}" />
						</div>
					</div>
				</c:forEach>
				<div class="col-xs-6 age-info">
					<c:set var="infoText">
						<spring:theme code="text.cms.accommodationfinder.age.info" text="Age refers to the age at the time of arrival date of the last leg of the journey: Adult[16+], Child[2-15], Infant[0-1]" />
					</c:set>
					<a tabindex="0" class="link-trigger" role="button" data-toggle="popover" data-html="true" data-trigger="focus" data-placement="bottom" data-content="${fn:escapeXml(infoText)}">
						<spring:theme code="text.cms.accommodationfinder.age" text="Age Information" />
						<i class="glyphicon glyphicon-info-sign"></i>
					</a>
				</div>
			</div>
			<div class="row margin-cancel">
				<div class="col-xs-12 btn-wrapper full-width">
					<button type="submit" class="btn btn-primary col-xs-12 col-sm-6 col-sm-offset-6">Search</button>
				</div>
			</div>
		</div>
	</fieldset>
</form:form>
