<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="bookingdetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/bookingdetails"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%-- Add Room overlay --%>
<div class="modal-dialog" role="document">
	<div class="modal-content">
		<div class="modal-body">
			<div class="container travelforms add-room">
				<div class="form-wrapper col-xs-12 col-xs-offset-0 col-sm-6 col-sm-offset-1">
					<c:url var="accommodationFinderUrl" value="/manage-booking/add-room-package/${orderCode}" />
					<form:form id="y_addRoomForm" commandName="accommodationAvailabilityForm" action="${accommodationFinderUrl}" method="POST" class="fe-validate form-background form-booking-trip">
						<fieldset>
							<legend class="with-icon heading-accommodation primary-legend">
								<spring:message code="text.page.bookingdetails.add.room.title" text="Add Room" />
							</legend>
							<button type="button" class="modal-only close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<div class="fieldset-inner-wrapper">
								<div class="row input-row less-margin">
									<div class="col-xs-12 col-sm-6">
										<label class="sr-only" for="travelAccommodationRoomQuantity">
											<spring:message code="text.page.bookingdetails.add.room.quantity.title" text="Room Quantity" />
										</label>
										<form:select class="form-control y_accommodationRoomQuantity" id="hotelAccommodationQuantity" path="numberOfRooms">
											<form:option value="-1" disabled="true" htmlEscape="true">
												<spring:message code="text.cms.accommodationfinder.accommodation.default.option" text="Rooms" />
											</form:option>
											<c:forEach var="entry" items="${accommodationsQuantity}">
												<form:option value="${fn:trim(entry)}">
													<c:choose>
														<c:when test="${fn:replace(entry,' ', '') == '1' }">${fn:escapeXml(entry)}
		                                       					 <spring:message code="text.cms.accommodationfinder.accommodation.single.room" text="Room" />
														</c:when>
														<c:otherwise>${fn:escapeXml(entry)}
		                                       						 <spring:message code="text.cms.accommodationfinder.accommodation.multiple.rooms" text="Rooms" />
														</c:otherwise>
													</c:choose>
												</form:option>
											</c:forEach>
										</form:select>
									</div>
								</div>
								<div class="row input-row less-margin">
									<c:forEach var="roomCandidatesEntry" items="${accommodationAvailabilityForm.roomStayCandidates}" varStatus='idx'>
										<div id="room[${idx.index+1}]" class="col-xs-12 guest-types ${ idx.index >= accommodationAvailabilityForm.numberOfRooms ? 'hidden' : ''}">
											<p>
												<spring:message code="text.cms.accommodationfinder.room" arguments="${idx.index+1}" />
											</p>
											<div class="row">
												<c:set var="formPrefix" value="roomStayCandidates[${idx.index}]." />
												<form:input path="${formPrefix}roomStayCandidateRefNumber" type="hidden" />
												<bookingdetails:passengerQuantity formPrefix="${formPrefix}" passengerTypeQuantityList="${roomCandidatesEntry.passengerTypeQuantityList}" />
											</div>
										</div>
									</c:forEach>
									<div id="y_roomStayCandidatesError" class="fe-error col-xs-12" style="display: none;"></div>
									<div class="col-xs-12 age-info">
										<c:set var="infoText">
											<spring:message code="text.cms.accommodationfinder.age.info" text="Age refers to the age at the time of arrival date of the last leg of the journey: Adult[16+], Child[2-15], Infant[0-1]" />
										</c:set>
										<a tabindex="0" class="link-trigger" role="button" data-toggle="popover" data-html="true" data-trigger="focus" data-placement="bottom" data-content="${fn:escapeXml(infoText)}">
											<spring:message code="text.cms.accommodationfinder.age" text="Age Information" />
											<i class="glyphicon glyphicon-info-sign"></i>
										</a>
									</div>
								</div>
								<div class="row margin-cancel">
									<div class="col-xs-12 btn-wrapper full-width">
										<a id="y_addRoomContinue" class="btn btn-primary col-xs-12 col-sm-6 col-sm-offset-6"> 
											<spring:message code="text.page.bookingdetails.add.room.continue" text="Continue" />
										</a>
									</div>
								</div>
							</div>
						</fieldset>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</div>
