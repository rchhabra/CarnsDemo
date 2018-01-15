<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="isDetail" value="false" />
<c:if test="${fn:length(bookingActionResponse.bookingActions) > 0}">
	<c:set var="isDetail" value="true" />
</c:if>
<c:forEach var="travellerData" items="${reservationItem.reservationItinerary.travellers}" varStatus="travellerDataIndex">
	<c:url var="checkInUrl" value="/manage-booking/check-in/${reservationData.code}/${reservationItem.originDestinationRefNumber}?travellerReference=${travellerData.uid}" />
	<div class="row group-traveller">
		<div class="group-traveller-details ${isDetail ? 'group-traveller-options' : '' } options clearfix">
			<dl class="col-xs-12 col-sm-11">
				<div class="col-xs-6 col-sm-2">
					<dt class="${travellerDataIndex.index > 0 ? 'sr-only' :''}">
						<label>
							<spring:theme code="text.page.travellerdetails.form.heading.title" text="Title" />
						</label>
					</dt>
					<dd>${fn:escapeXml(travellerData.travellerInfo.title.name)}</dd>
				</div>
				<div class="col-xs-6 col-sm-4">
					<dt class="${travellerDataIndex.index > 0 ? 'sr-only' :''}">
						<label>
							<spring:theme code="text.page.travellerdetails.form.heading.firstname" text="Firstname" var="firstnamePlaceholder" />
							${fn:escapeXml(firstnamePlaceholder)}
						</label>
					</dt>
					<dd>${fn:escapeXml(travellerData.travellerInfo.firstName)}</dd>
				</div>
				<div class="col-xs-6 col-sm-4">
					<dt class="${travellerDataIndex.index > 0 ? 'sr-only' :''}">
						<label>
							<spring:theme var="lastnamePlaceholder" code="text.page.travellerdetails.form.heading.lastname" text="Lastname" />
							${fn:escapeXml(lastnamePlaceholder)}
						</label>
					</dt>
					<dd>${fn:escapeXml(travellerData.travellerInfo.surname)}</dd>
				</div>
				<div class="col-xs-6 col-sm-2">
					<dt class="${travellerDataIndex.index > 0 ? 'sr-only' :''}">
						<label>
							<spring:theme code="text.page.travellerdetails.form.heading.gender" text="Gender" />
						</label>
					</dt>
					<dd>
    					<c:choose>
    						<c:when test= "${fn:escapeXml(travellerData.travellerInfo.gender) == 'male'}">
    							<spring:theme code="text.page.travellerdetails.form.heading.gender.male"/>
    						</c:when>
    						<c:otherwise>
    							<spring:theme code="text.page.travellerdetails.form.heading.gender.female"/>
    						</c:otherwise>
    					</c:choose>
    				</dd>
				</div>
			</dl>
			<div class="form-group col-xs-1 additional-information">
				<div class="pull-left">
					<a href="#" data-toggle="modal" data-target="#ancillariesModal_${travellerDataIndex.index}">
						<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
					</a>
					<span class="sr-only">
						<spring:theme code="text.page.additional.information.modal.heading" text="Additional Information" />
					</span>
					<span class="glyphicon glyphicon-ok-sign hidden" aria-hidden="true">
						<span class="sr-only">
							<spring:theme code="text.page.additional.information.added" text="Additional information added" />
						</span>
					</span>
				</div>
				<c:if test="${isDetail}">
					<div class="pull-left visible-xs-block y_options options-show">
						<a>
							<span class="glyphicon glyphicon-chevron-down y_down" aria-hidden="true"></span>
							<span class="glyphicon glyphicon-chevron-up y_up hidden" aria-hidden="true"></span>
						</a>
					</div>
				</c:if>
			</div>
			<c:if test="${isDetail}">
				<div class="col-xs-12 options">
					<div class="row">
						<div class="form-group col-xs-12 col-sm-4">
							<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="CHECK_IN" travellerUid="${travellerData.uid}" leg="${reservationItem.originDestinationRefNumber}" />
						</div>
						<div class="form-group col-xs-12 col-sm-4">
							<input type="hidden" value="${fn:escapeXml(travellerData.uid)}" name="travellerUid" id="travellerUid"> <input type="hidden" value="${fn:escapeXml(reservationData.code)}" name="bookingReference" id="bookingReference">
							<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="REMOVE_TRAVELLER" travellerUid="${travellerData.uid}" leg="${reservationItem.originDestinationRefNumber}" />
						</div>
					</div>
				</div>
			</c:if>
		</div>
		<c:if test="${reservationItem.originDestinationRefNumber == 0}">
		    <booking:additionalInformationBooking reservationData="${reservationData}" currentIndex="${travellerDataIndex.index}" travellerData="${travellerData}" />
	    </c:if>
	</div>
</c:forEach>
