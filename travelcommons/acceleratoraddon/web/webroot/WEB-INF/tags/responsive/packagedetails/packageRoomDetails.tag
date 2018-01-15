<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomType" required="true" type="de.hybris.platform.commercefacades.accommodation.RoomTypeData"%>
<%@ attribute name="roomStayIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="reservedRoomStayRefNumber" required="false" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${empty reservedRoomStayRefNumber}">
		<c:set var="selected" value="selected-" />
		<c:set var="index" value="${roomStayIndex}" />
	</c:when>
	<c:otherwise>
		<c:set var="index" value="${reservedRoomStayRefNumber}-${roomStayIndex}" />
	</c:otherwise>
</c:choose>

<div class="modal fade modal-extra-details" id="${selected}extra-room-details-modal-${index}" tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header clearfix">
				<div class="col-xs-10"><span>${fn:escapeXml(roomType.name)}</span></div>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="accommodation-image-gallery">
					<div class="owl-carousel main-image-carousel">
						<c:forEach items="${roomType.images}" var="accommodationImage">
							<c:if test="${accommodationImage.format == 'accommodationDetail'}">
								<div class="item">
									<img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
								</div>
							</c:if>
						</c:forEach>
					</div>
					<div class="thumbnail-carousel-wrapper">
						<div class="owl-carousel thumbnail-carousel">
							<c:forEach items="${roomType.images}" var="accommodationImage">
								<c:if test="${accommodationImage.format == 'thumbnail'}">
									<div class="item">
										<img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
									</div>
								</c:if>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
			<div class="accommodation-details clearfix">
				<div class="col-xs-12">
					<div class="row">
						<div class="col-xs-12 col-sm-9">
							<h4>${fn:escapeXml(roomType.name)}</h4>
							<p>${fn:escapeXml(roomType.description)}</p>
						</div>
						<div class="col-xs-12 col-sm-3">
							<dl>
								<dt><spring:theme code="text.accommodation.details.accommodation.occupancy" text="Occupancy"/></dt>
								<dd>
									<span class="sr-only">
										<c:set var="maxGuests" value="0" />
										<c:forEach var="occupancy" items="${roomType.occupancies}">
											<c:set var="maxGuests" value="${ maxGuests + occupancy.quantityMax }" />
										</c:forEach>
										<spring:theme code="text.accommodation.details.accommodation.max.people.screen.reader" arguments="${maxGuests}" />
									</span>
									<span aria-label="people">
										<c:forEach var="occupancy" items="${roomType.occupancies}" varStatus="guestOccupanciesIdx">
											<c:forEach begin="1" end="${occupancy.quantityMax}">
												<span class="person-icon ${occupancy.passengerType.code == 'child' || occupancy.passengerType.code == 'infant' ? 'child-icon' : ''}">&nbsp;</span>
											</c:forEach>
											<c:if test="${!guestOccupanciesIdx.last}">+ &nbsp;</c:if>
										</c:forEach>
									</span>
								</dd>
							</dl>
							<p>
								<small><spring:theme code="text.accommodation.details.accommodation.size" text="Size"/><br />${fn:escapeXml(roomType.sizeMeasurement)}</small>
							</p>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-accommodation panel-primary panel-list clearfix features">
				<h4 class="panel-title">
					<a class="panel-heading panel-header-link collapsable collapsed" role="button" data-toggle="collapse" href="#${selected}amenities-panel-${index}" aria-expanded="false" aria-controls="${selected}amenities-panel-${index}"><spring:theme code="text.accommodation.details.accommodation.features" text="Accommodation Features" /></a>
				</h4>
				<div class="panel-body collapse" id="${selected}amenities-panel-${index}">
					<div class="col-xs-12">
						<ul class="small clearfix">
							<c:forEach var="facility" items="${roomType.facilities}">
								<li><span class="glyphicon glyphicon-ok" aria-hidden="true"></span>${fn:escapeXml(facility.shortDescription)}</li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
