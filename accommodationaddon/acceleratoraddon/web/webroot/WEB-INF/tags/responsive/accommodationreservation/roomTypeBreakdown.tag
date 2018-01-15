<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="roomStay" required="true" type="de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="isChangeDates" required="false" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="row room-wrap">
	<div class="col-xs-4 col-sm-2">
		<c:forEach items="${roomStay.roomTypes[0].images}" var="accommodationImage">
			<c:if test="${accommodationImage.format == 'list'}">
				<img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}" class="img-responsive">
			</c:if>
		</c:forEach>
	</div>
	<div class="col-xs-8 col-sm-10">
		<div class="row">
			<div class="col-xs-12">
				<h3>${fn:escapeXml(roomStay.roomTypes[0].name)}</h3>
			</div>
			<div class="col-xs-12">
				<p>${fn:escapeXml(roomStay.roomTypes[0].bedType)}</p>
			</div>
		</div>
		<div class="row">
			<c:if test="${fn:length(roomStay.guestCounts)>0}">
				<div class="col-xs-12 col-sm-6 item-wrapper">
					<dl>
						<dt>
							<spring:theme code="text.cms.accommodationbreakdown.guests" text="Guests" />
						</dt>
						<c:forEach items="${roomStay.guestCounts}" var="guestCount">
							<c:if test="${guestCount.quantity > 0}">
								<dd>
									<spring:theme code="text.cms.accommodationbreakdown.guests.details" text="Guests Details" arguments="${guestCount.quantity},${guestCount.passengerType.name}" />
								</dd>
							</c:if>
						</c:forEach>
					</dl>
				</div>
			</c:if>
			<div class="col-xs-12 col-sm-6 item-wrapper">
				<dl>
					<dt>
						<spring:theme code="text.cms.accommodationbreakdown.dates" text="Dates" />
					</dt>
					<dd>
						<spring:theme code="text.cms.accommodationbreakdown.checkIn" text="Check In" />
						:&nbsp;
						<fmt:formatDate value="${roomStay.checkInDate}" pattern="dd/MM/yyyy" />
					</dd>
					<dd>
						<spring:theme code="text.cms.accommodationbreakdown.checkOut" text="Check Out" />
						:&nbsp;
						<fmt:formatDate value="${roomStay.checkOutDate}" pattern="dd/MM/yyyy" />
					</dd>
				</dl>
			</div>
		</div>
	</div>
</div>
<div id="accommodation-plan-info_${fn:escapeXml(index)}">
	<a role="button" data-toggle="collapse" href="#collapse-plan-info_${index}${isChangeDates ? '_change-dates-overlay' : ''}" aria-expanded="true" aria-controls="collapse-plan-info_${index}${isChangeDates ? '_change-dates-overlay' : ''}" class="">
		<i class="glyphicon glyphicon-info-sign"></i>
		<spring:theme code="text.cms.accommodationbreakdown.plan.info" text="Plan Information" />
	</a>
</div>
<div class="col-xs-12">
	<div id="collapse-plan-info_${index}${isChangeDates ? '_change-dates-overlay' : ''}" class="row booking-features panel-collapse collapse">
		<div class="col-xs-12">
			<div class="col-xs-12 col-sm-6">
				<ul class="small feature-list">
					<c:forEach var="guarantee" items="${roomStay.ratePlans[0].guarantees}">
						<li>${fn:escapeXml(guarantee.longDescription)}</li>
					</c:forEach>
					<c:forEach var="cancelPenalty" items="${roomStay.ratePlans[0].cancelPenalties}">
						<c:if test="${cancelPenalty.formattedDescription != null}">
							<li>${fn:escapeXml(cancelPenalty.formattedDescription)}</li>
						</c:if>
					</c:forEach>
				</ul>
			</div>
			<div class="col-xs-12 col-sm-6">
				<ul class="small feature-list">
					<c:forEach var="ratePlanInclusion" items="${roomStay.ratePlans[0].ratePlanInclusions}">
						<li>${fn:escapeXml(ratePlanInclusion.longDescription )}</li>
					</c:forEach>
					<c:forEach var="mealType" items="${roomStay.ratePlans[0].mealTypes}">
						<li>${fn:escapeXml(mealType)}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
</div>
