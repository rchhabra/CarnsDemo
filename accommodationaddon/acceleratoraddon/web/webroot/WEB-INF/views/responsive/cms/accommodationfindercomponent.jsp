<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="accommodation" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationfinder"%>
<%@ taglib prefix="passenger" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/passenger"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${hideFinderTitle eq true}">
<div role="tabpanel" class="tab-pane active" id="hotel">
</c:if>
	<c:url var="accommodationFinderUrl" value="/view/AccommodationFinderComponentController/search" />
	<form:form commandName="accommodationFinderForm" action="${accommodationFinderUrl}" method="POST" class="fe-validate form-background form-booking-trip" id="y_accommodationFinderForm">
		<fieldset>
			<c:if test="${hideFinderTitle ne true}">
				<legend class="with-icon heading-accommodation primary-legend">
					<c:if test="${!showComponent}">
						<a role="button" data-toggle="collapse" aria-expanded="true" data-target=".panel-modify" class="panel-heading panel-header-link collapsable collapsed">
					</c:if>
					<c:choose>
						<c:when test="${!showComponent}">
							<spring:theme code="text.cms.accommodationfinder.modify.title" text="Modify Search" />
						</c:when>
						<c:otherwise>
							<spring:theme code="text.cms.accommodationfinder.title" text="Hotel" />
						</c:otherwise>
					</c:choose>
					<c:if test="${!showComponent}">
						</a>
					</c:if>
				</legend>
			</c:if>
			<button type="button" class="modal-only close" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			<div class="${ showComponent ? '' : 'panel-modify collapse'} ">
				<div id="accommodation-finder-panel" class="fieldset-inner-wrapper">
					<c:set var="formPrefix" value="" />
					<accommodation:accommodationfinder formPrefix="${formPrefix}" idPrefix="accommodation" />
					<div class="row input-row less-margin">
					<%-- Begin No of Accommodations input field section --%>
						<div class="col-xs-12 col-sm-8">
							<label class="sr-only" for="hotelAccommodationQuantity">
								<spring:theme var="roomQuantityPlaceholderText" code="text.cms.accommodationfinder.accommodationquantity.placeholder" text="Accommodation Quantity" />
								${fn:escapeXml(roomQuantityPlaceholderText)}
							</label>
							<form:select class="form-control y_accommodationRoomQuantity" id="hotelAccommodationQuantity" path="numberOfRooms">
								<form:option value="-1" disabled="true">
									<spring:theme code="text.cms.accommodationfinder.accommodation.default.option" text="Rooms"></spring:theme>
								</form:option>
								<c:forEach var="entry" items="${accommodationsQuantity}">
									<form:option value="${fn:trim(entry)}" htmlEscape="true">
										<c:choose>
											<c:when test="${fn:replace(entry,' ', '') == '1' }">${fn:escapeXml(entry)}
		                                        <spring:theme code="text.cms.accommodationfinder.accommodation.single.room" text="Room"></spring:theme>
											</c:when>
											<c:otherwise>${fn:escapeXml(entry)}
		                                        <spring:theme code="text.cms.accommodationfinder.accommodation.multiple.rooms" text="Rooms"></spring:theme>
											</c:otherwise>
										</c:choose>
									</form:option>
								</c:forEach>
							</form:select>
						</div>
						<%-- End No of Accommodations input field section --%>
						<%-- Begin Guests input field section --%>
						<c:forEach var="roomCandidatesEntry" items="${accommodationFinderForm.roomStayCandidates}" varStatus='idx'>
							<div id="room[${fn:escapeXml(idx.index+1)}]" class="col-xs-12 guest-types ${ idx.index >= accommodationFinderForm.numberOfRooms ? 'hidden' : ''}">
	                            <p><spring:theme code="text.cms.accommodationfinder.room" arguments="${idx.index+1}"/></p>
								<div class="row y_passengerGroup">
									<c:set var="formPrefix" value="roomStayCandidates[${idx.index}]." />
									<form:input path="${fn:escapeXml(formPrefix)}roomStayCandidateRefNumber" type="hidden" />
									<passenger:passengertypequantity formPrefix="${formPrefix}" passengerTypeQuantityList="${roomCandidatesEntry.passengerTypeQuantityList}" />
								</div>
							</div>
						</c:forEach>
						<%-- End Guests input field section --%>

						<div class="col-xs-6 age-info">
	                        <c:set var="infoText">
	                            <spring:theme code="text.cms.accommodationfinder.age.info" text="Age refers to the age at the time of arrival date of the last leg of the journey: Adult[16+], Child[2-15], Infant[0-1]" />
	                        </c:set>
	                        <a tabindex="0" class="link-trigger" role="button" data-toggle="popover"  data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(infoText)}">
	                            <spring:theme code="text.cms.accommodationfinder.age" text="Age Information" />
	                            <i class="glyphicon glyphicon-info-sign"></i>
	                        </a>
	                    </div>
					</div>
				</div>
				<%--  Begin CTA (Search Accommodations) section --%>
				<div class="row margin-cancel">
					<div class="col-xs-12 btn-wrapper full-width">
						<form:button class="btn btn-primary col-xs-12 col-sm-6 col-sm-offset-6">
							<spring:theme code="text.cms.accommodationfinder.button.submit" text="Search" />
						</form:button>
					</div>
				</div>
				<%--  End CTA (Search Accommodations) section --%>
			</div>
		<%-- / .fieldset-inner-wrapper --%>
		</fieldset>
	</form:form>
<c:if test="${hideFinderTitle eq true}">
</div>
</c:if>
